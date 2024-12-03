package com.volmit.ed4j.handler;

import com.volmit.dumpster.F;
import com.volmit.ed4j.ED4J;
import com.volmit.ed4j.handler.event.CompleteJumpEvent;
import com.volmit.ed4j.handler.event.DockedEvent;
import com.volmit.ed4j.handler.event.DockingRequestDeniedEvent;
import com.volmit.ed4j.handler.event.DockingRequestGrantedEvent;
import com.volmit.ed4j.handler.event.FuelScoopedEvent;
import com.volmit.ed4j.handler.event.MissionAcceptedEvent;
import com.volmit.ed4j.handler.event.MissionCompletedEvent;
import com.volmit.ed4j.handler.event.OverheatingEvent;
import com.volmit.ed4j.handler.event.ReceiveTextEvent;
import com.volmit.ed4j.handler.event.RefueledEvent;
import com.volmit.ed4j.handler.event.ScannedEvent;
import com.volmit.ed4j.handler.event.ShipSwapEvent;
import com.volmit.ed4j.handler.event.StartJumpEvent;
import com.volmit.ed4j.handler.event.SupercruiseExitEvent;
import com.volmit.ed4j.handler.event.UndockedEvent;
import com.volmit.ed4j.util.LandingPads;
import com.volmit.ed4j.util.UIFocus;

public class TTSEventHandler implements IEventHandler
{
	private static void talk(String s)
	{
		ED4J.talk(s);
	}

	@Override
	public void onUndocked(UndockedEvent e)
	{
		talk("Departing from " + e.getStationName());
	}

	@Override
	public void onScanned(ScannedEvent e)
	{
		talk(e.getScanType() + " Scan Detected");
	}

	@Override
	public void onStartJump(StartJumpEvent e)
	{
		if(e.getStarSystem() != null)
		{
			talk("Jumping to " + e.getStarSystem() + ". Star class, " + e.getStarClass() + ".");
		}

		else
		{
			talk("Engaging Supercruise");
		}
	}

	@Override
	public void onCompleteJump(CompleteJumpEvent e)
	{
		talk("Welcome to " + e.getStarSystem() + ", " + e.getSecurity());
	}

	@Override
	public void onReceiveText(ReceiveTextEvent e)
	{
		if(e.getUnlocalizedMessage().toLowerCase().contains("hostile") || e.getUnlocalizedMessage().toLowerCase().contains("pirate"))
		{
			talk("Hostile Vessel Detected.");
		}
	}

	@Override
	public void onSupercruiseExit(SupercruiseExitEvent e)
	{

		if(e.getBodyType().equals("Station"))
		{
			talk("Welcome to " + e.getBody() + ".");
		}
	}

	@Override
	public void onDockingRequestDenied(DockingRequestDeniedEvent e)
	{
		talk("Request Denied. " + e.getReason());
	}

	@Override
	public void onDockingRequestGranted(DockingRequestGrantedEvent e)
	{
		String m = LandingPads.getPadDistance(e.getLandingPad());
		if(!m.isEmpty())
		{
			talk("Landing pad " + e.getLandingPad() + " is in the " + m + ".");
		}
	}

	@Override
	public void onDocked(DockedEvent e)
	{
		talk("Docked at " + e.getStationName());
	}

	@Override
	public void onOverheating(OverheatingEvent e)
	{
		talk("Overheating!");
	}

	@Override
	public void onMissionAccepted(MissionAcceptedEvent e)
	{
		talk("Mission Accepted");
	}

	@Override
	public void onShipSwapped(ShipSwapEvent e)
	{
		talk("Swapping your " + e.getOldShip() + " to your " + e.getNewShip());
	}

	@Override
	public void onMissionCompleted(MissionCompletedEvent e)
	{
		talk("Mission Complete. Plus Respect");
	}

	@Override
	public void onFuelScooped(FuelScoopedEvent e)
	{
		talk("Scooped " + F.f(e.getTonsScooped(), 1) + " tons of fuel");
	}

	@Override
	public void onRefueled(RefueledEvent e)
	{
		talk("Refuled " + F.f(e.getTonsRefueled(), 1) + " tons of fuel");
	}

	@Override
	public void onFireGroup(int cfire)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUiFocus(UIFocus uiFocus)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPipsChanged(int pipSystems, int pipEngines, int pipWeapons)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReceiveText(String channel, String messageLocalized, String from, String message)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoadGame(String commander, String ship, long credits)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLocation()
	{
		// TODO Auto-generated method stub
		
	}
}
