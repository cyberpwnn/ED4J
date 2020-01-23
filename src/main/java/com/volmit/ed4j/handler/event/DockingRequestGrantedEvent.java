package com.volmit.ed4j.handler.event;

import com.volmit.dumpster.JSONObject;
import com.volmit.ed4j.util.EliteEvent;

public class DockingRequestGrantedEvent extends EliteEvent
{
	protected final int landingPad;

	public DockingRequestGrantedEvent(JSONObject data)
	{
		super(data);
		landingPad = getInt("LandingPad");
	}

	public int getLandingPad()
	{
		return landingPad;
	}
}
