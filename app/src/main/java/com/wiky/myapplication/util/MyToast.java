package com.wiky.myapplication.util;

import android.app.Activity;
import android.widget.Toast;

/**
 * 自定义Toast类
 * @author wiky
 * @version 2013-12-19 v1.0
 */
public class MyToast
{
    private Toast mToast;
    private Activity myactivity;
    public MyToast(Activity activity)
    {
        this.myactivity = activity;
    }

    /**
     * 显示Toast
     * @param text  显示内容
     */
    public void showToast(String text)
    {
        //防止Toast重复弹出相同的信息
        if(mToast == null)
        {
            mToast = Toast.makeText(myactivity.getApplicationContext(), text, Toast.LENGTH_SHORT);
        }
        else
        {
            mToast.setText(text);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    /**
     * 消除Toast
     */
    public void cancelToast()
    {
        if (mToast != null)
        {
            mToast.cancel();
        }
    }
}
