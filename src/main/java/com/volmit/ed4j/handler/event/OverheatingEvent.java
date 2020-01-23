package com.volmit.ed4j.handler.event;

import com.volmit.dumpster.JSONObject;
import com.volmit.ed4j.util.EliteEvent;

public class OverheatingEvent extends EliteEvent
{
	public OverheatingEvent(JSONObject data)
	{
		super(data);
	}
}
