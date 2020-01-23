package com.volmit.ed4j.util;

import com.volmit.dumpster.JSONObject;

public class EliteEvent
{
	protected final JSONObject data;

	public EliteEvent(JSONObject data)
	{
		this.data = data;
	}

	protected int getInt(String key)
	{
		return data.getInt(key);
	}
	
	protected double getDouble(String key)
	{
		return data.getDouble(key);
	}
	
	protected String getString(String key)
	{
		return data.getString(key);
	}

	public JSONObject getData()
	{
		return data;
	}
}
