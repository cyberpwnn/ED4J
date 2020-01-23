package com.volmit.ed4j.handler.event;

import com.volmit.dumpster.JSONObject;
import com.volmit.ed4j.util.EliteEvent;

public class MissionAcceptedEvent extends EliteEvent
{
	public MissionAcceptedEvent(JSONObject data)
	{
		super(data);
	}
}
