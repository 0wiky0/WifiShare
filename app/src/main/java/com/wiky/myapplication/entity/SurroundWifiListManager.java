package com.wiky.myapplication.entity;

import java.util.ArrayList;




/**
 * 周边分享wifi列表
 * @author wiky
 * 2015.3.12
 */
public class SurroundWifiListManager
{
	//单例模式
	private static SurroundWifiListManager userInfo = null;
	private SurroundWifiListManager()
	{}

	public static SurroundWifiListManager getInstance()
	{
		if(userInfo==null)
		{
			synchronized(SurroundWifiListManager.class)
			{
				if(userInfo==null){
					userInfo = new SurroundWifiListManager();
				}
			}
		}
		return userInfo;
	}

	private static ArrayList<String> surroundWifiList = new ArrayList<String>();

	public void add(String str)
	{
		surroundWifiList.add(str);
	}

	public void clearAll()
	{
		surroundWifiList.clear();
	}

	public ArrayList<String> getSurroundWifiList()
	{
		return surroundWifiList;
	}


}
