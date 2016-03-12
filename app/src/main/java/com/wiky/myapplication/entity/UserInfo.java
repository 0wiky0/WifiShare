package com.wiky.myapplication.entity;
import java.util.HashMap;
import java.util.Map;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;

/**
 * 存储当前的用户信息
 * @author wiky
 * 2014.11.9
 */
public class UserInfo
{
	//单例模式
	private static UserInfo userInfo = null;
	private UserInfo(){}

	public static UserInfo getInstance()
	{
		if(userInfo==null)
		{
			synchronized(UserInfo.class)
			{
				if(userInfo==null){
					userInfo = new UserInfo();
				}
			}
		}
		return userInfo;
	}

	//相关属性(测试：给定初始值默认登录成功)
	private String userID = "none";     //当前用户ID
	private String passWord = "201314";   //当前用户密码
	private boolean isLogged = false;   //标记当前的登录状态 
	private int shareWifiNum = 0;       //当前分享的wifi信号数量
	private int integral = -1;        //记录用户当前的积分
	private int wifiPermission = -1;  //标记当前用户是否拥有获取分享的wifi信息的权限（0：不可获取；  1：可获取）        
	private boolean isUseingShareWifi = false;   //标记当前是否正在使用分享的wifi（通过一键连接形式连接的wifi信号）
	private boolean shareWifi = false;      //标记是否分享当前登录的wifi信息
	private int netId_of_onekeyConnWifi = -1;        //记录用户通过一键连接的wifi信号的netId
	private Map<String, Object> shareWifiInfo = new HashMap<String, Object>();  //存储分享的wifi信息
	private Bitmap avatar = null;
	private String sharerID = null;   //当前使用的wifi的分享者的ID

	/**
	 * 退出登录
	 */
	public void exit()
	{
		userID = "none";     //当前用户ID
		passWord = "201314";   //当前用户密码
		isLogged = false;   //标记当前的登录状态 
		shareWifiNum = 0;       //当前分享的wifi信号数量
		integral = -1;        //记录用户当前的积分
		wifiPermission = -1;  //标记当前用户是否拥有获取分享的wifi信息的权限（0：不可获取；  1：可获取）        
		isUseingShareWifi = false;   //标记当前是否正在使用分享的wifi（通过一键连接形式连接的wifi信号）
		shareWifi = false;      //标记是否分享当前登录的wifi信息
	}

	/**
	 * 读取本地存储的用户信息
	 * @return 本地未存在用户信息则返回false
	 */
	public boolean readInfo(Activity activity)
	{
		boolean exist = false;
		SharedPreferences preferences=activity.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
		exist = preferences.getBoolean("exist", false);
		if(exist != false)
		{//表明存在用户信息
			userID = preferences.getString("UserName", "@");    //返回key为“name”的值，若不存在，则返回设置的缺省值："defaultname"
			passWord = preferences.getString("PassWord", "@");
		}
		return exist;
	}

	/**
	 * "记住密码"将用户信息存储至本地并赋值对应的成员变量
	 * @param activity
	 * @param userID
	 * @param passWord
	 */
	public void saveInfo(Activity activity,String userID, String passWord)
	{
		this.userID = userID;
		this.passWord = passWord;
		SharedPreferences preferences=activity.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
		Editor editor=preferences.edit();   //获得编辑器
		editor.putBoolean("exist", true); 	   		   //表明配置文件存在配置信息
		editor.putString("UserName", userID);        //向编辑器中存入用户账号信息
		editor.putString("PassWord", passWord); 	   //向编辑器中存入用户密码信息
		editor.commit();                               //提交
	}

	/**
	 * 设置是否分享wifi
	 * @param share
	 */
	public void setShareWifi(boolean share)
	{
		shareWifi = share;
	}

	public boolean getShareWifi()
	{
		return shareWifi;
	}

	/**
	 * 存储分享的wifi信息
	 */
	public void setShareWifiInfo(Map<String, Object> shareWifiInfo)
	{
		this.shareWifiInfo = shareWifiInfo;
	}

	/**
	 * 获取分享的wifi信息
	 */
	public Map<String, Object> getShareWifiInfo()
	{
		return shareWifiInfo;
	}

	public boolean isLogged()
	{
		return isLogged;
	}

	public void setLogged(boolean isLogged)
	{
		this.isLogged = isLogged;
	}

	public int getIntegral()
	{
		return integral;
	}

	public void setIntegral(int integral)
	{
		this.integral = integral;
	}

	public String getUserID()
	{
		return userID;
	}

	public void setUserID(String userID)
	{
		this.userID = userID;
	}

	public String getPassWord()
	{
		return passWord;
	}

	public void setPassWord(String passWord)
	{
		this.passWord = passWord;
	}

	public boolean isUseingShareWifi()
	{
		return isUseingShareWifi;
	}

	public void setUseingShareWifi(boolean isUseingShareWifi)
	{
		this.isUseingShareWifi = isUseingShareWifi;
	}

	public int getWifiPermission()
	{
		return wifiPermission;
	}

	public void setWifiPermission(final int wifiPermission)
	{
		this.wifiPermission = wifiPermission;
		new Thread()
		{
			@Override
			public void run(){
				//发送请求
//				MyClient.getInstance().resetWifiPermission(userID, wifiPermission);
			}
		}.start();
	}

	public int getNetId_of_onekeyConnWifi()
	{
		return netId_of_onekeyConnWifi;
	}

	public void setNetId_of_onekeyConnWifi(int netId_of_onekeyConnWifi)
	{
		this.netId_of_onekeyConnWifi = netId_of_onekeyConnWifi;
	}

	public Bitmap getAvatar()
	{
		return avatar;
	}

	public void setAvatar(Bitmap avatar)
	{
		this.avatar = avatar;
	}

	public String getSharerID()
	{
		return sharerID;
	}

	public void setSharerID(String sharerID)
	{
		this.sharerID = sharerID;
	}
}
