package com.wiky.myapplication.ui;

import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.wiky.myapplication.R;
import com.wiky.myapplication.util.MyToast;
import com.wiky.myapplication.util.WifiAdmin;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class WifiDetial extends AppCompatActivity implements OnClickListener {
    private String currentBSSID = null; // 当前查看的wifi的ID(唯一区别WIFI网络的名字)
    List<ScanResult> list;
    private Timer timer;
    private WifiAdmin wifiAdmin;
    private int pwType; // 标识wifi信号的加密方式（capabilities）
    private TextView tv_ssid; // 显示当前查看的wifi名称
    private TextView tv_level; // 显示当前查看的wifi信号强度
    private TextView tv_state; // 显示当前查看的wifi连接状态
    private Button btn_left; // 功能：密码登录或断开连接
    private Button btn_right; // 功能：一键连接或信号测速
    private ScanResult currentInfo; // 当前查看的wifi信号信息
    private ImageView imgv_pointer; // 指针图片
    private int pointer_begin = 0; // 指针开始时刻与初始方向（竖直）的夹角
    public static WifiDetial app;
    private String leftBtnText = "直接连接"; // 左侧按钮的文本内容
    private String rightBtnText = "一键连接"; // 右侧按钮的文本内容
    private static final int GETWIFIINFO = 1; // 获取wifi信息
    private MyHandler myHandler;


    // wifi消息处理广播对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifidetial);
        app = this;
        myHandler = new MyHandler(this);
        init();

        getWifiInfo();

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = GETWIFIINFO;
                myHandler.sendMessage(msg);
            }
        }, 1000, 1000);// 1000毫秒

    }

    private void init() {
        wifiAdmin = new WifiAdmin(this);
        // 获取intent携带的数据
        Intent intent = getIntent();
        currentBSSID = intent.getStringExtra("BSSID");

        // 获取wifi信号信息并进行相关判断
        wifiAdmin.startScan();
        ScanResult scanWifi = wifiAdmin.getScanResult(currentBSSID); // 指定的BSSID的wifi信号信息
        if (scanWifi == null) {
            finish();
        } else {
            if (scanWifi.capabilities == null) {
                pwType = 0;
            } else if (scanWifi.capabilities.toLowerCase().contains("wep"))
                pwType = 2;// 密码类型为wep...
            else if (scanWifi.capabilities.toLowerCase().contains("wpa")) {
                pwType = 3;// 密码类型为wpa...
            } else {
                pwType = 1;// scanWifi.capabilities==""无密码
            }

            // 控件初始化
            imgv_pointer = (ImageView) findViewById(R.id.img_wifidetailac_pointer);
            tv_ssid = (TextView) findViewById(R.id.tv_wifidetail_ssid); // 显示wifi信号的ssid
            tv_level = (TextView) findViewById(R.id.tv_wifidetail_level); // 显示wifi信号的强度
            tv_state = (TextView) findViewById(R.id.tv_wifidetail_state); // 显示wifi信号状态
            btn_left = (Button) findViewById(R.id.btn_wifiDetail_left);
            btn_right = (Button) findViewById(R.id.btn_wifiDetail_right);
            btn_left.setOnClickListener(this);
            btn_right.setOnClickListener(this);

            if (pwType != 0 && pwType != 1) {// 需要密码，则显示密码输入框
                leftBtnText = "密码登录";
            }
        }

    }

    private void getWifiInfo() {
        wifiAdmin.startScan();
        list = wifiAdmin.getWifiList();

        if (list != null)
            for (ScanResult result : list) {
                if (currentBSSID != null && currentBSSID.equals(result.BSSID)) {
                    // 判断当前查看的wifi信号是否为当前连接的wifi
                    WifiInfo connectedInfo = wifiAdmin.getConnectionWifiInfo();
//                    Log.d("WifiDetial","connectedInfo.getSSID() = "+connectedInfo.getSSID()+", result.SSID = "+result.SSID);
                    if (connectedInfo != null && !connectedInfo.getSSID().equalsIgnoreCase(result.SSID)
                            || !connectedInfo.getSSID().equalsIgnoreCase("\""+result.SSID+"\"")) {
                        // 当前未存在连接对象，部分机型会在SSID上加引号""
                        tv_state.setText("未连接");
                        btn_left.setText(leftBtnText);
                        btn_right.setText(rightBtnText);
                    } else {
                        tv_state.setText("已连接");
                        btn_left.setText("断开连接");
                        btn_right.setText("信号测速");
                    }
                    currentInfo = result;
                    tv_ssid.setText(result.SSID);
                    int level = (result.level + 200) / 2;
                    tv_level.setText(level + "%");
                    rotateImg((level - 70) * 3); // 旋转指针
                    break;
                }
            }
    }

    /**
     * 控制指针旋转
     *
     * @param degrees 当前指针与竖直方向的夹角度数
     */
    public void rotateImg(int degrees) {
        AnimationSet animationSet = new AnimationSet(true);
        RotateAnimation rotateAnimation = new RotateAnimation(pointer_begin, degrees, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.97f);
        rotateAnimation.setDuration(800);
        animationSet.addAnimation(rotateAnimation);
        animationSet.setFillAfter(true);// 动画执行完后停留在执行完的状态
        imgv_pointer.startAnimation(animationSet);
        pointer_begin = degrees;
    }

    @Override
    public void onClick(View v) {
        Button button = (Button) v;
        switch (button.getId()) {
            case R.id.btn_wifiDetail_left:
                if ("断开连接".equals(button.getText().toString())) {// 断开连接
                    wifiAdmin.disconnectWifi(wifiAdmin.getNetworkId());
                    wifiAdmin.removeNetwork(wifiAdmin.getNetworkId());// 删除该配置信息
                    tv_state.setText("未连接");
                    button.setText(leftBtnText);
                    btn_right.setText("一键连接");
                } else if ("密码登录".equals(button.getText().toString())) {
                    showLoginDialog();
                } else if ("直接连接".equals(button.getText().toString())) {
                    if (currentInfo != null) {
                        WifiConfiguration c = wifiAdmin.CreateWifiInfo(currentInfo.SSID, "", 1);
                        if (c != null && wifiAdmin.addNetwork(c)) {
                            tv_state.setText("已连接");
                            btn_left.setText("断开连接");
                            btn_right.setText("信号测速");
                        }
                    }
                }
                break;
            case R.id.btn_wifiDetail_right:
                if ("信号测速".equals(button.getText().toString())) {
                    // 信号测速
                } else if ("一键连接".equals(button.getText().toString())) {
                    if (currentInfo != null) {
                        if (pwType == 0 || pwType == 1) {// 无加密信号
                            new MyToast(this).showToast("抱歉,该类信号暂不支持一键连接 ╯﹏╰");
                        } else {
                            // 从服务端获取该wifi信号的密码，并进行连接
                        }
                    }
                }
                break;
            default:
                break;
        }

    }

    /**
     * 显示密码登录的对话框
     */
    private void showLoginDialog() {
        new MaterialDialog.Builder(this)
                .title("密码登录")
                .inputRangeRes(8, 30, R.color.colorAccent)
                .input(null, null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        WifiConfiguration c = wifiAdmin.CreateWifiInfo(currentInfo.SSID, input.toString(), pwType);
                        if (c != null) {
                            // System.out.println("---->配置信息不为空");
                            if (wifiAdmin.addNetwork(c)) {// 连接成功
                                // System.out.println("---->连接成功");
                                btn_left.setText("断开连接");
                                btn_right.setText("信号测速");
                                tv_state.setText("已连接");
                                //如果用户允许分享则可以在这里记录Wifi信息，并发送给服务端（demo中省略这一步）
                            } else {
                                new MyToast(WifiDetial.this).showToast("连接失败，请检查密码！");
                            }
                        }
                    }
                }).show();
    }

    @Override
    protected void onDestroy() {
        if (timer != null)
            timer.cancel();
        super.onDestroy();
    }

    private static class MyHandler extends Handler {

        private final WeakReference<WifiDetial> mActivity;

        public MyHandler(WifiDetial activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            WifiDetial activity = mActivity.get();
            switch (msg.what) {
                case GETWIFIINFO:
                    // 获取当前查看的wifi信号的详细信息
                    activity.getWifiInfo();
                    break;
            }
        }
    }
}
