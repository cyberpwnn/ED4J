package com.volmit.ed4j.handler.event;

import com.volmit.dumpster.JSONObject;
import com.volmit.ed4j.util.EliteEvent;

public class ShipSwapEvent extends EliteEvent
{
	protected final String oldShip;
	protected final String newShip;

	public ShipSwapEvent(JSONObject data)
	{
		super(data);
		oldShip = getString("StoreOldShip").replaceAll("-", " ");
		newShip = getString("ShipType_Localised").replaceAll("-", " ");
	}

	public String getOldShip()
	{
		return oldShip;
	}

	public String getNewShip()
	{
		return newShip;
	}
}
