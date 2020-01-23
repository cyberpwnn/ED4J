package com.volmit.ed4j.state;

import com.volmit.ed4j.util.UIFocus;

public class EliteState
{
	private UIFocus focus;
	private String station;
	private int fireGroup;
	private int pipSystems;
	private int pipEngines;
	private int pipWeapons;

	public void setPips(int s, int e, int w)
	{
		pipSystems = s;
		pipWeapons = w;
		pipEngines = e;
	}

	public void setUIFocus(UIFocus uiFocus)
	{
		focus = uiFocus;
	}

	public void setFireGroup(int cfire)
	{
		fireGroup = cfire;
	}

	public UIFocus getFocus()
	{
		return focus;
	}

	public void setFocus(UIFocus focus)
	{
		this.focus = focus;
	}

	public int getPipSystems()
	{
		return pipSystems;
	}

	public void setPipSystems(int pipSystems)
	{
		this.pipSystems = pipSystems;
	}

	public int getPipEngines()
	{
		return pipEngines;
	}

	public void setPipEngines(int pipEngines)
	{
		this.pipEngines = pipEngines;
	}

	public int getPipWeapons()
	{
		return pipWeapons;
	}

	public void setPipWeapons(int pipWeapons)
	{
		this.pipWeapons = pipWeapons;
	}

	public int getFireGroup()
	{
		return fireGroup;
	}

	public void setStation(String string)
	{
		station = string;
	}

	public String getStation()
	{
		return station;
	}
}
