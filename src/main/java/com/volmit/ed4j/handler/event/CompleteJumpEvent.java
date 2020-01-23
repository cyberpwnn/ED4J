package com.volmit.ed4j.handler.event;

import com.volmit.dumpster.JSONObject;
import com.volmit.ed4j.util.EliteEvent;

public class CompleteJumpEvent extends EliteEvent
{
	protected final String starSystem;
	protected final String security;

	public CompleteJumpEvent(JSONObject data)
	{
		super(data);
		starSystem = getString("StarSystem");
		security = getString("SystemSecurity_Localised");
	}

	public String getStarSystem()
	{
		return starSystem;
	}

	public String getSecurity()
	{
		return security;
	}
}
