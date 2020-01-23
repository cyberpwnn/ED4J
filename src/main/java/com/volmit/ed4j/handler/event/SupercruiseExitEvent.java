package com.volmit.ed4j.handler.event;

import com.volmit.dumpster.JSONObject;
import com.volmit.ed4j.util.EliteEvent;

public class SupercruiseExitEvent extends EliteEvent
{
	protected final String bodyType;
	protected final String body;

	public SupercruiseExitEvent(JSONObject data)
	{
		super(data);
		bodyType = getString("BodyType");
		body = getString("Body");
	}

	public String getBodyType()
	{
		return bodyType;
	}

	public String getBody()
	{
		return body;
	}
}
