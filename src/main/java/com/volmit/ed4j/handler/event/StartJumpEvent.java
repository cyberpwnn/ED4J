package com.volmit.ed4j.handler.event;

import com.volmit.dumpster.JSONObject;
import com.volmit.ed4j.util.EliteEvent;
import com.volmit.ed4j.util.JumpType;

public class StartJumpEvent extends EliteEvent
{
	protected final JumpType jumpType;
	protected final String starSystem;
	protected final String starClass;

	public StartJumpEvent(JSONObject data)
	{
		super(data);
		jumpType = JumpType.valueOf(getString("JumpType").toUpperCase());
		starSystem = getString("StarSystem");
		starClass = getString("StarClass");
	}

	public JumpType getJumpType()
	{
		return jumpType;
	}

	public String getStarSystem()
	{
		return starSystem;
	}

	public String getStarClass()
	{
		return starClass;
	}
}
