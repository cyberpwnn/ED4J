package com.volmit.ed4j.handler.event;

import com.volmit.dumpster.JSONObject;
import com.volmit.ed4j.util.EliteEvent;

public class DockingRequestDeniedEvent extends EliteEvent
{
	protected final String reason;

	public DockingRequestDeniedEvent(JSONObject data)
	{
		super(data);
		reason = getString("Reason");
	}

	public String getReason()
	{
		return reason;
	}
}
