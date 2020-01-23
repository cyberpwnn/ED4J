package com.volmit.ed4j.handler.event;

import com.volmit.dumpster.JSONObject;
import com.volmit.ed4j.util.EliteEvent;

public class RefueledEvent extends EliteEvent
{
	protected final double tonsRefueled;
	
	public RefueledEvent(JSONObject data)
	{
		super(data);
		tonsRefueled = getDouble("Amount");
	}

	public double getTonsRefueled()
	{
		return tonsRefueled;
	}
}
