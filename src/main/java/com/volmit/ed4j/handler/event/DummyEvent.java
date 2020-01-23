package com.volmit.ed4j.handler.event;

import com.volmit.dumpster.JSONObject;
import com.volmit.ed4j.util.EliteEvent;

public class DummyEvent extends EliteEvent
{
	public DummyEvent(JSONObject data)
	{
		super(data);
	}
}
