package com.volmit.ed4j.handler.event;

import com.volmit.dumpster.JSONObject;
import com.volmit.ed4j.util.EliteEvent;

public class FuelScoopedEvent extends EliteEvent
{
	protected final double tonsScooped;
	
	public FuelScoopedEvent(JSONObject data)
	{
		super(data);
		tonsScooped = getDouble("Scooped");
	}

	public double getTonsScooped()
	{
		return tonsScooped;
	}
}
