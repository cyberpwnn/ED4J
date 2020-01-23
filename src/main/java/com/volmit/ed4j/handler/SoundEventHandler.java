package com.volmit.ed4j.handler;

import java.io.File;
import java.io.FileInputStream;

import com.volmit.dumpster.F;
import com.volmit.dumpster.GList;
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

import javazoom.jl.player.Player;

public class SoundEventHandler implements IEventHandler
{
	private Player current = null;
	private File folder;

	public SoundEventHandler()
	{
		folder = new File("sounds");
		folder.mkdirs();
	}

	public void play(String f)
	{
		File fl = new File(folder, f);

		if(!fl.exists())
		{
			fl.mkdirs();
		}

		GList<File> mp3s = new GList<>();

		for(File i : fl.listFiles())
		{
			if(i.getName().endsWith(".mp3"))
			{
				mp3s.add(i);
			}
		}

		if(mp3s.isEmpty())
		{
			System.out.println("Missing MP3s in " + fl.getAbsolutePath());
		}

		else
		{
			File ff = mp3s.pickRandom();
			System.out.print("Playing " + fl.getName() + "/" + ff.getName() + " -> ");
			try
			{
				if(current != null)
				{
					current.close();
					current = null;
					System.out.println("Interrupted");
				}

				new Thread(() ->
				{
					try
					{
						Player p = new Player(new FileInputStream(ff));
						p.play();
						System.out.println("Done");
					}

					catch(Throwable x)
					{
						x.printStackTrace();
					}
				}).start();
			}

			catch(Throwable e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onFireGroup(int cfire)
	{
		play("Fire Group " + (cfire + 1));
	}

	@Override
	public void onUiFocus(UIFocus uiFocus)
	{
		play("UI " + F.capitalize(uiFocus.toString().toLowerCase()));
	}

	@Override
	public void onPipsChanged(int pipSystems, int pipEngines, int pipWeapons)
	{
		play("Pips " + pipSystems + "S " + pipEngines + "E " + pipWeapons + "W");
	}

	@Override
	public void onUndocked(UndockedEvent e)
	{
		play("Undocked");
	}

	@Override
	public void onScanned(ScannedEvent e)
	{
		play(F.capitalizeWords(e.getScanType().toString().toLowerCase()) + " Scanned");
	}

	@Override
	public void onStartJump(StartJumpEvent e)
	{
		if(e.getStarSystem() != null)
		{
			play("Start Jump");
		}

		else
		{
			play("Start Supercruise");
		}
	}

	@Override
	public void onCompleteJump(CompleteJumpEvent e)
	{
		play("Complete Jump");
	}

	@Override
	public void onReceiveText(ReceiveTextEvent e)
	{
		if(e.getUnlocalizedMessage().toLowerCase().contains("hostile") || e.getUnlocalizedMessage().toLowerCase().contains("pirate"))
		{
			play("Receive Hostile Text");
		}

		else
		{
			play("Receive Text");
		}
	}

	@Override
	public void onSupercruiseExit(SupercruiseExitEvent e)
	{
		play("Supercruise Exit");
	}

	@Override
	public void onDockingRequestDenied(DockingRequestDeniedEvent e)
	{
		play("Docking Denied " + e.getReason());
	}

	@Override
	public void onDockingRequestGranted(DockingRequestGrantedEvent e)
	{
		play("Docking Granted");
	}

	@Override
	public void onDocked(DockedEvent e)
	{
		play("Docked");
	}

	@Override
	public void onOverheating(OverheatingEvent e)
	{
		play("Overheating");
	}

	@Override
	public void onMissionAccepted(MissionAcceptedEvent e)
	{
		play("Mission Accepted");
	}

	@Override
	public void onShipSwapped(ShipSwapEvent e)
	{
		play("Ship Swapped");
	}

	@Override
	public void onMissionCompleted(MissionCompletedEvent e)
	{
		play("Mission Completed");
	}

	@Override
	public void onFuelScooped(FuelScoopedEvent e)
	{
		play("Fuel Scooped");
	}

	@Override
	public void onRefueled(RefueledEvent e)
	{
		play("Refueled");
	}
}
