package com.volmit.ed4j.handler.event;

import com.volmit.dumpster.JSONObject;
import com.volmit.ed4j.util.EliteEvent;
import com.volmit.ed4j.util.StationType;

public class DockedEvent extends EliteEvent
{
	protected final String stationName;
	protected final StationType stationType;
	
	public DockedEvent(JSONObject data)
	{
		super(data);
		stationName = getString("StationName");
		stationType = StationType.valueOf(getString("StationType").toUpperCase() );
	}

	public String getStationName()
	{
		return stationName;
	}

	public StationType getStationType()
	{
		return stationType;
	}
}
