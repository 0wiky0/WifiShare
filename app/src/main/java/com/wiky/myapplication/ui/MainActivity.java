package com.wiky.myapplication.ui;

import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.wiky.myapplication.R;
import com.wiky.myapplication.adapter.MyAdapter;
import com.wiky.myapplication.entity.MyWifiScanResult;
import com.wiky.myapplication.util.WifiAdmin;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
        , SwipeRefreshLayout.OnRefreshListener, MyAdapter.OnRecyclerViewItemClickListener {
    private static final String TAG = "MainActivity";

    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeLayout;
    private WifiAdmin wifiAdmin;
    private ArrayList<MyWifiScanResult> wifiDatas = new ArrayList<>();
    private Timer timer;
    private final MyHandler mHandler = new MyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wifiAdmin = new WifiAdmin(this);
        // wifi未开启则开启wifi
        if (!wifiAdmin.isWifiEnabled()) {
            //System.out.println("onResume：+开启wifi");
            wifiAdmin.openWifi(); // 开启wifi
        }
        initView();

        reFreshView();
    }

    @Override
    protected void onResume() {
        if (timer == null) {
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    Message msg = new Message();
                    msg.what = 1;
                    mHandler.sendMessage(msg);
                }
            }, 1000, 2000);// 2000毫秒
        }
        super.onResume();
    }

    /**
     * 刷新界面数据
     */
    private void reFreshView(){
        loadWifiData();
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(getCurrentWifiName());
    }

    private void loadWifiData() {
        wifiDatas.clear();
        if (wifiAdmin.isWifiEnabled()) {
            wifiAdmin.startScan();
            List<ScanResult> lists = wifiAdmin.getWifiList();
            for (ScanResult scanResult : lists) {
                if (wifiAdmin.isNeedPassWord(scanResult)) {
                    wifiDatas.add(new MyWifiScanResult(scanResult, false, true));
                } else {
                    //不需要密码的放在列表前面
                    wifiDatas.add(0, new MyWifiScanResult(scanResult, false, false));
                }
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // 获取已连接的wifi信息

        //侧边菜单栏控件
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //侧边菜单栏开关控件
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //下拉刷新控件
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.id_swiperefreshlayout);
        mSwipeLayout.setOnRefreshListener(this);
        // 设置下拉圆圈上的颜色，蓝色、绿色、橙色、红色
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);

        //RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        // 当确定List内容（数据）的变化不会影响RecycleView布局的大小时，以下设置可以提高性能
        mRecyclerView.setHasFixedSize(true);
        // 使用linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        // 设置设配器 (MyAdapter见步骤4)
        mAdapter = new MyAdapter(wifiDatas);
        mAdapter.setOnRecyclerViewItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onBackPressed() {
        //如果侧边导航菜单为打开（显示）状态则关闭（收缩）菜单
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 下拉刷新
     */
    @Override
    public void onRefresh() {
        //防止正在刷新时又触发刷新
        mSwipeLayout.setEnabled(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadWifiData();
                // 停止刷新
                mSwipeLayout.setEnabled(true);
                mSwipeLayout.setRefreshing(false);
            }
        }, 500); // 0.5秒后发送消息，停止刷新
    }

    /**
     * 侧边导航菜单项选中回调方法
     *
     * @param item
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRecycleViewItemClick(View view, int position) {
        Log.d(TAG, "position = " + position);
        MyWifiScanResult result = wifiDatas.get(position);// 获取当前点击的wifi信号信息
        if (result != null) {// 如果信号存在，则打开查看信号详情的界面
            Intent intent = new Intent();
            intent.setClass(this, WifiDetial.class);
            intent.putExtra("BSSID", result.getScanResult().BSSID);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                TextView bSsid = (TextView) view.findViewById(R.id.txt_bssid);
//                ActivityOptions options = ActivityOptions
//                        .makeSceneTransitionAnimation(this, bSsid, "robot");
//                // start the new activity
//                startActivity(intent, options.toBundle());
//            } else {
//                startActivity(intent);
//            }
            startActivity(intent);
        } else {// 如果信号不存在，则进行刷新
            loadWifiData();
        }
    }

    /**
     * 获取当前连接的wifi的名称
     * @return  wifi的名称
     */
    private String getCurrentWifiName(){
        if(wifiAdmin!=null){
            WifiInfo connectedInfo = wifiAdmin.getConnectionWifiInfo();
            if (connectedInfo == null||connectedInfo.getSSID().equalsIgnoreCase("<unknown ssid>")||connectedInfo.getSSID().equalsIgnoreCase("0x")) {// 当前未存在连接对象
                return "未连接";
            } else {
                return connectedInfo.getSSID();
            }
        }
        return "未连接";
    }

    @Override
    protected void onPause() {
        // 销毁timer线程
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (timer != null)
            timer.cancel();
        super.onDestroy();
    }

    private static class MyHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        public MyHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    // UI线程扫描填充wifi列表信息
                    MainActivity activity = mActivity.get();
                    if (activity != null) {
                        activity.reFreshView();
                    }
                    break;
            }
        }
    }
}
