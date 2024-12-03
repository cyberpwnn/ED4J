package com.volmit.ed4j.handler;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import com.volmit.dumpster.F;
import com.volmit.dumpster.GList;
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

import javazoom.jl.player.Player;

public class SoundEventHandler implements IEventHandler
{
	private Player current = null;
	private File root;
	private String currentVoice;
	private List<Runnable> q;

	public SoundEventHandler(File root)
	{
		this.root = root;
		q = new ArrayList<>();
		updateVoice();
		play("Start");

		new Thread(() ->
		{
			while(true)
			{
				try
				{
					Thread.sleep(250);
				}

				catch(InterruptedException e)
				{
					e.printStackTrace();
				}
				Runnable r = null;

				synchronized(q)
				{
					if(q.size() > 0)
					{
						updateVoice();
						r = q.get(0);
						q.remove(0);
					}
				}

				if(r != null)
				{
					try
					{
						r.run();
					}

					catch(Throwable e)
					{
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	private void updateVoice()
	{
		try
		{
			currentVoice = "EDEN";

			for(File i : new File("voiceconfig").listFiles())
			{
				currentVoice = i.getName();
				break;
			}
		}

		catch(Throwable e)
		{

		}
	}

	public File getFolder()
	{
		return new File(root, currentVoice + "/events");
	}

	public void play(String f)
	{
		synchronized(q)
		{
			q.add(() ->
			{
				File fl = new File(getFolder(), f);

				if(!fl.exists())
				{
					fl.mkdirs();
				}

				GList<File> mp3s = new GList<>();
				
				if(fl.isFile())
				{
					return;
				}
				
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
					}

					catch(Throwable e)
					{
						e.printStackTrace();
					}
				}
			});
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
		
		String m = LandingPads.getPadDistance(e.getLandingPad());
		if(!m.isEmpty())
		{
			play("Docking Granted " + F.capitalize(m));
		}
		
		else
		{
			play("Docking Granted");
		}
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
		robot(KeyEvent.VK_H);
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

	private void robot(int code)
	{
		try
		{
			Robot robot = new Robot(ED4J.device);
			robot.keyPress(code);

			try
			{
				Thread.sleep(200);
			}

			catch(InterruptedException e)
			{
				e.printStackTrace();
			}

			robot.keyRelease(code);
			System.out.println("Pressed " + KeyEvent.getKeyText(code) + " on Monitor " + ED4J.monitor);
		}

		catch(AWTException e)
		{
			e.printStackTrace();
		}
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

	@Override
	public void onReceiveText(String channel, String messageLocalized, String from, String message)
	{
		
	}

	@Override
	public void onLoadGame(String commander, String ship, long credits)
	{
		play("Welcome Back");
	}

	@Override
	public void onLocation()
	{
		
	}
}
