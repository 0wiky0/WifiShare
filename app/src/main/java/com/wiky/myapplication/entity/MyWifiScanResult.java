package com.wiky.myapplication.entity;

import android.net.wifi.ScanResult;

/**
 * Created by wiky on 2016/3/4.
 */
public class MyWifiScanResult {
    private ScanResult scanResult;
    private boolean isShare = false;
    private boolean isNeedPassword = false;

    public MyWifiScanResult(ScanResult scanResult, boolean isShare,boolean isNeedPassword) {
        this.scanResult = scanResult;
        this.isShare = isShare;
        this.isNeedPassword = isNeedPassword;
    }

    public boolean isShare() {
        return isShare;
    }

    public void setIsShare(boolean isShare) {
        this.isShare = isShare;
    }

    public ScanResult getScanResult() {
        return scanResult;
    }

    public void setScanResult(ScanResult scanResult) {
        this.scanResult = scanResult;
    }

    public boolean isNeedPassword() {
        return isNeedPassword;
    }

    public void setIsNeedPassword(boolean isNeedPassword) {
        this.isNeedPassword = isNeedPassword;
    }
}

