package com.volmit.ed4j.handler.event;

import com.volmit.dumpster.JSONObject;
import com.volmit.ed4j.util.EliteEvent;
import com.volmit.ed4j.util.ScanType;

public class ScannedEvent extends EliteEvent
{
	protected final ScanType scanType;

	public ScannedEvent(JSONObject data)
	{
		super(data);
		scanType = ScanType.valueOf(getString("ScanType").toUpperCase());
	}

	public ScanType getScanType()
	{
		return scanType;
	}
}
