package com.volmit.ed4j.handler.event;

import com.volmit.dumpster.JSONObject;
import com.volmit.ed4j.util.EliteEvent;

public class ReceiveTextEvent extends EliteEvent
{
	protected final String unlocalizedMessage;
	protected final String localizedMessage;

	public ReceiveTextEvent(JSONObject data)
	{
		super(data);
		unlocalizedMessage = getString("Message");
		localizedMessage = getString("Message_Localised");
	}

	public String getUnlocalizedMessage()
	{
		return unlocalizedMessage;
	}

	public String getLocalizedMessage()
	{
		return localizedMessage;
	}
}
