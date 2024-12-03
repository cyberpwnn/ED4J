package com.volmit.ed4j.handler;

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
import com.volmit.ed4j.util.UIFocus;

public interface IEventHandler
{
	public void onUndocked(UndockedEvent e);

	public void onScanned(ScannedEvent e);

	public void onStartJump(StartJumpEvent e);

	public void onCompleteJump(CompleteJumpEvent e);

	public void onReceiveText(ReceiveTextEvent e);

	public void onSupercruiseExit(SupercruiseExitEvent e);

	public void onDockingRequestDenied(DockingRequestDeniedEvent e);

	public void onDockingRequestGranted(DockingRequestGrantedEvent e);

	public void onDocked(DockedEvent e);

	public void onOverheating(OverheatingEvent e);

	public void onMissionAccepted(MissionAcceptedEvent e);

	public void onShipSwapped(ShipSwapEvent e);

	public void onMissionCompleted(MissionCompletedEvent e);

	public void onFuelScooped(FuelScoopedEvent e);
	
	public void onReceiveText(String channel, String messageLocalized, String from, String  message);
	
	public void onLoadGame(String commander, String ship, long credits);

	public void onLocation();
	
	public void onRefueled(RefueledEvent e);

	public void onFireGroup(int cfire);

	public void onUiFocus(UIFocus uiFocus);

	public void onPipsChanged(int pipSystems, int pipEngines, int pipWeapons);
}
