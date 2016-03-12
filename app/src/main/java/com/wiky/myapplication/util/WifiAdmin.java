package com.wiky.myapplication.util;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.util.Log;

import com.wiky.myapplication.entity.SurroundWifiListManager;
import com.wiky.myapplication.entity.UserInfo;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * wifi管理工具类
 * @author wiky （改至网络）
 * 2014.10.24
 */
public class WifiAdmin {

    private static final String TAG = "[WifiAdmin]";
    private WifiManager mWifiManager;
    private WifiInfo mWifiInfo;   //当前连接的wifi信号信息
    private List<ScanResult> mWifiList = null;
    private List<WifiConfiguration> mWifiConfiguration;
    private WifiLock mWifiLock;
    private DhcpInfo dhcpInfo;

    public WifiAdmin(Context context) {
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mWifiInfo = mWifiManager.getConnectionInfo();
    }

    /**
     * 打开wifi
     * @return  开启成功返回true
     */
    public boolean openWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            Log.i(TAG, "setWifiEnabled.....");
            mWifiManager.setWifiEnabled(true);
            Log.i(TAG, "setWifiEnabled.....end");
        }
        return mWifiManager.isWifiEnabled();
    }

    /**
     * 关闭wifi
     */
    public void closeWifi() {
        if (mWifiManager.isWifiEnabled())
        {
            mWifiManager.setWifiEnabled(false);
        }
    }

    /**
     * 获取当前的wifi状态
     * @return   WIFI_STATE_DISABLED, WIFI_STATE_DISABLING, 
     * 			 WIFI_STATE_ENABLED, WIFI_STATE_ENABLING, WIFI_STATE_UNKNOWN
     */
    public int checkState() {
        return mWifiManager.getWifiState();
    }

    /**
     * 判断当前wifi是否可用
     * @return  可用返回TRUE
     */
    public boolean isWifiEnabled()
    {
        return mWifiManager.isWifiEnabled();
    }

    /**
     * 判断当前热点是否开启
     * @return  开启返回TRUE
     */
    public boolean isWifiApStart()
    {
        //热点状态
//    	int WIFI_AP_STATE_DISABLING = 10;  
//    	int WIFI_AP_STATE_DISABLED = 11;  
        int WIFI_AP_STATE_ENABLING = 3;  //12 ??????待确认
        int WIFI_AP_STATE_ENABLED = 13;
//    	int WIFI_AP_STATE_FAILED = 14; 

        try {
            Method method = mWifiManager.getClass().getMethod("getWifiApState");
            int i = (Integer) method.invoke(mWifiManager);
            System.out.println("热点状态="+i);
            if(i==WIFI_AP_STATE_ENABLING || i==WIFI_AP_STATE_ENABLED)
                return true;
        } catch (Exception e) {
            Log.e(TAG,"Cannot get WiFi AP state" + e);
            return false;
        }
        return false;
    }

    /**
     * 锁定wifiLock
     */
    public void acquireWifiLock() {
        mWifiLock.acquire();
    }

    /**
     * 解锁wifiLock
     */
    public void releaseWifiLock() {
        if (mWifiLock.isHeld()) {
            mWifiLock.acquire();
        }
    }

    public void creatWifiLock() {
        mWifiLock = mWifiManager.createWifiLock("Test");
    }

    /**
     * 获取配置过的wifi列表信息（注意在获取之前进行startScan（））
     * @return
     */
    public List<WifiConfiguration> getConfiguration() {
        return mWifiConfiguration;
    }

    /**
     * 指定配置好的网络进行连接
     * @param index
     */
    public void connectConfiguration(int index) {
        if (index > mWifiConfiguration.size()) {
            return;
        }
        mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId, true);
    }

    /**
     * wifi扫描
     */
    public void startScan() {
        mWifiManager.startScan();
        // Log.i(TAG, "startScan result:" + scan);
        List<ScanResult> wifiList = mWifiManager.getScanResults();
        mWifiList = new ArrayList<>();
        boolean isAdd = true;

        if (wifiList != null) {
            for (int i = 0; i < wifiList.size(); i++) {
                isAdd = true;
                for (int j = 0; j < mWifiList.size(); j++) {
                    if (!wifiList.get(i).SSID.trim().equals("")&&mWifiList.get(j).SSID.equals(wifiList.get(i).SSID)) {
                        isAdd = false;
                        if (mWifiList.get(j).level < wifiList.get(i).level) {
                            // ssid相同且新的信号更强
                            mWifiList.remove(j);
                            mWifiList.add(wifiList.get(i));
                            break;
                        }
                    }
                }
                if (!wifiList.get(i).SSID.trim().equals("")&&isAdd)
                    mWifiList.add(wifiList.get(i));
            }
        }

        //mWifiList = mWifiManager.getScanResults();
        mWifiConfiguration = mWifiManager.getConfiguredNetworks();

//        if (mWifiList != null) {
//            Log.i(TAG, "startScan result:" + mWifiList.size());
//            for (int i = 0; i < mWifiList.size(); i++) {
//                ScanResult result = mWifiList.get(i);
//                Log.i(TAG, "startScan result[" + i + "]" + result.SSID + "," + result.BSSID);
//            }
//            Log.i(TAG, "startScan result end.");
//        } else {
//            Log.i(TAG, "startScan result is null.");
//        }
    }

    /**
     * 获取扫描出的wifi列表信息（注意在获取之前调用startScan（））
     * @return
     */
    public List<ScanResult> getWifiList() {
        return mWifiList;
    }

    /**
     * 获得扫描出的wifi列表信息中指定的BSSID信号信息
     * @param BSSID
     * @return
     */
    public ScanResult getScanResult(String BSSID)
    {
        if(mWifiList!=null)
            for (ScanResult result : mWifiList) {
                if (BSSID.equals(result.BSSID)) {
                    return result;
                }
            }
        return null;
    }

    /**
     * 获取扫描出需要密码的wifi信号列表信息（注意在获取之前调用startScan（））
     * @return
     */
    public List<ScanResult> getWithPWWifiList() {
        ArrayList<ScanResult> listWithPW = new ArrayList<ScanResult>();
        if(mWifiList!=null)
        {
            for (ScanResult scanResult : mWifiList)
            {
                if(scanResult.capabilities.toLowerCase().indexOf("wep")!=-1
                        ||scanResult.capabilities.toLowerCase().indexOf("wpa")!=-1)
                {
                    boolean flag = false;  //标记当前添加的wifi信号是否已被分享
                    ArrayList<String> surroundWifiList = SurroundWifiListManager.getInstance().getSurroundWifiList();
//    				System.out.println("getWithPWWifiList====>>surroundWifiList="+surroundWifiList);
                    for (String string : surroundWifiList)
                    {
                        if(string.equals(scanResult.SSID))
                        {
//    						System.out.println("getWithPWWifiList====>>same="+string);
                            listWithPW.add(0, scanResult);
                            flag = true;
                            break;
                        }
                    }
                    if(flag == false)
                        listWithPW.add(scanResult);
                }
            }
        }
        return listWithPW;
    }

    /**
     * 判断是否需要密码
     * @param scanResult wifi信息
     * @return true ： 需要
     */
    public boolean isNeedPassWord(ScanResult scanResult)   {
        if(scanResult.capabilities.toLowerCase().indexOf("wep")!=-1
                ||scanResult.capabilities.toLowerCase().indexOf("wpa")!=-1)
        {
            return true;
        }
        return false;
    }

    /**
     * 获取扫描出不需要密码的wifi信号列表信息（注意在获取之前调用startScan（））
     * @return
     */
    public List<ScanResult> getWoutPWWifiList() {
        List<ScanResult> listWouthPW = new ArrayList<ScanResult>();
        if(mWifiList!=null)
            for (ScanResult scanResult : mWifiList)
            {
                if(scanResult.capabilities.toLowerCase().indexOf("wep")==-1
                        &&scanResult.capabilities.toLowerCase().indexOf("wpa")==-1)
                {
                    listWouthPW.add(scanResult);
                }
            }
        return listWouthPW;
    }

    /**
     * 查看扫描结果
     * @return
     */
    public StringBuilder lookUpScan() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < mWifiList.size(); i++) {
            stringBuilder.append("Index_" + (i + 1) + ":");
            stringBuilder.append((mWifiList.get(i)).toString());
            stringBuilder.append("/n");
        }
        return stringBuilder;
    }

    public String getMacAddress() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
    }

    public String getBSSID() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
    }

    public DhcpInfo getDhcpInfo() {
        dhcpInfo = mWifiManager.getDhcpInfo();
        return dhcpInfo;
    }

    public int getIPAddress() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
    }

    /**
     * 获取当前连接的wifi信号id
     * @return
     */
    public int getNetworkId() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
    }

    /**
     * 获取当前连接的wifi信号信息
     * @return
     */
    public WifiInfo getConnectionWifiInfo() {
        mWifiInfo = mWifiManager.getConnectionInfo();
        return mWifiInfo;
    }

    /**
     * 添加一个网络配置并连接 
     * @param wcg
     * return 连接成功返回true
     */
    public boolean addNetwork(WifiConfiguration wcg) {
        int wcgID = mWifiManager.addNetwork(wcg);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        boolean b = mWifiManager.enableNetwork(wcgID, true);
        return b;
    }

    /**
     * 从配置列表中移除wifi信息
     * @param netId
     */
    public void removeNetwork(int netId)
    {
        System.out.println("删除配置信息");
        UserInfo.getInstance().setNetId_of_onekeyConnWifi(-1);
        mWifiManager.removeNetwork(netId);
    }

    /**
     * 断开指定的wifi连接
     * @param netId
     */
    public void disconnectWifi(int netId) {
        mWifiManager.disableNetwork(netId);
        mWifiManager.disconnect();
    }

    /**
     * 创建配置信息
     * @param SSID
     * @param Password  wifi密码
     * @param Type
     * 		Type=1 :WIFICIPHER_NOPASS(无密码)
     * 		Type=2 :WIFICIPHER_WEP(加密方式)
     *  	Type=3 :WIFICIPHER_WPA(加密方式)
     * @return    返回配置完的wifi对象
     */
    public WifiConfiguration CreateWifiInfo(String SSID, String Password, int Type) {
        Log.i(TAG, "SSID:" + SSID + ",password:" + Password);
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";

        WifiConfiguration tempConfig = this.IsExsits(SSID);

        if (tempConfig != null) {
            mWifiManager.removeNetwork(tempConfig.networkId);
        } else {
            Log.i(TAG, "IsExsits is null.");
        }

        if (Type == 1) // WIFICIPHER_NOPASS
        {
            Log.i(TAG, "Type =1.");
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }
        if (Type == 2) // WIFICIPHER_WEP
        {
            Log.i(TAG, "Type =2.");
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + Password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == 3) // WIFICIPHER_WPA
        {
            Log.i(TAG, "Type =3.");
            config.preSharedKey = "\"" + Password + "\"";

            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    /**
     * 查看指定的SSID是否已经配置过  
     * @param SSID
     * @return
     */
    private WifiConfiguration IsExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }
}