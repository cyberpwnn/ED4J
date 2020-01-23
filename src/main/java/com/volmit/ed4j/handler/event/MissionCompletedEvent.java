package com.volmit.ed4j.handler.event;

import com.volmit.dumpster.JSONObject;
import com.volmit.ed4j.util.EliteEvent;

public class MissionCompletedEvent extends EliteEvent
{
	public MissionCompletedEvent(JSONObject data)
	{
		super(data);
	}
}
