package com.volmit.ed4j;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import com.darkprograms.speech.synthesiser.Synthesiser;
import com.sun.javafx.tk.Toolkit;
import com.volmit.dumpster.F;
import com.volmit.dumpster.GList;
import com.volmit.dumpster.JSONArray;
import com.volmit.dumpster.JSONException;
import com.volmit.dumpster.JSONObject;
import com.volmit.ed4j.handler.IEventHandler;
import com.volmit.ed4j.handler.SoundEventHandler;
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
import com.volmit.ed4j.state.EliteState;
import com.volmit.ed4j.util.MarketSector;
import com.volmit.ed4j.util.UIFocus;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javazoom.jl.player.Player;

public class ED4J
{
	public static final String EDDB_VERSION = "v6";

	public static File target;
	public static File status;
	public static File folder;
	public static GList<JSONObject> events;
	public static ExecutorService svc;
	public static BufferedReader bu;
	public static long start;
	public static int pipSystems;
	public static int pipEngines;
	public static int pipWeapons;
	public static int uiFocus;
	public static int fireGroup;
	public static int cpip;
	public static int cui;
	public static int cfire;
	public static JSONArray stationData;
	public static JSONArray commodityData;
	public static JSONArray moduleData;
	public static IEventHandler h = new SoundEventHandler();
	public static EliteState state = new EliteState();

	private static void downloadMarketData() throws IOException
	{
		System.out.print("Populating Station Data");
		stationData = download(new URL("https://eddb.io/archive/" + EDDB_VERSION + "/stations.json"));
		System.out.print("Populating Commodity Data");
		commodityData = download(new URL("https://eddb.io/archive/" + EDDB_VERSION + "/commodities.json"));
		System.out.print("Populating Module Data");
		moduleData = download(new URL("https://eddb.io/archive/" + EDDB_VERSION + "/modules.json"));
	}

	public static void doIt(File csvm) throws FileNotFoundException, IOException
	{
		try(ICsvBeanReader beanReader = new CsvBeanReader(new FileReader(csvm), CsvPreference.STANDARD_PREFERENCE))
		{
			final String[] headers = new String[] {"id", "station_id", "commodity_id", "supply", "supply_bracket", "buy_price", "sell_price", "demand", "demand_bracket", "collected_at"};
			final CellProcessor[] processors = getProcessors();
			MarketSector sec;

			while((sec = beanReader.read(MarketSector.class, headers, processors)) != null)
			{
				System.out.println(sec);
			}
		}
	}

	private static CellProcessor[] getProcessors()
	{
		final CellProcessor[] processors = new CellProcessor[] {new NotNull(new ParseInt()), new NotNull(new ParseInt()), new NotNull(new ParseInt()), new NotNull(new ParseInt()), new NotNull(new ParseInt()), new NotNull(new ParseInt()), new NotNull(new ParseInt()), new NotNull(new ParseInt()), new NotNull(new ParseInt()), new NotNull(new ParseInt()),};

		return processors;
	}

	private static JSONArray download(URL u) throws IOException
	{
		URLConnection conn = u.openConnection();
		long estimatedSize = conn.getContentLengthLong();
		System.out.print(" -> Downloading: " + F.fileSize(estimatedSize));
		char[] cbuf = new char[8192];
		int read = 0;
		BufferedInputStream bu = new BufferedInputStream(conn.getInputStream(), 8192);
		BufferedReader br = new BufferedReader(new InputStreamReader(bu));
		StringBuilder sb = new StringBuilder();

		while((read = br.read(cbuf)) != -1)
		{
			sb.append(cbuf, 0, read);
		}

		br.close();
		JSONArray j = new JSONArray(sb.toString());
		System.out.println(" -> Done");

		return j;
	}

	private static boolean handle(JSONObject e)
	{
		String event = e.getString("event");
		if(e.has("StationName"))
		{
			state.setStation(e.getString("StationName"));
		}

		if(event.equals("Continued"))
		{
			try
			{
				retarget();
			}

			catch(IOException ex)
			{
				ex.printStackTrace();
			}

			return true;
		}

		if(event.equals("Undocked"))
		{
			h.onUndocked(new UndockedEvent(e));
		}

		if(event.equals("Scanned"))
		{
			h.onScanned(new ScannedEvent(e));
		}

		if(event.equals("StartJump"))
		{
			h.onStartJump(new StartJumpEvent(e));
		}

		if(event.equals("FSDJump"))
		{
			h.onCompleteJump(new CompleteJumpEvent(e));
		}

		if(event.equals("ReceiveText"))
		{
			h.onReceiveText(new ReceiveTextEvent(e));
		}

		if(event.equals("SupercruiseExit"))
		{
			h.onSupercruiseExit(new SupercruiseExitEvent(e));
		}

		if(event.equals("DockingDenied"))
		{
			h.onDockingRequestDenied(new DockingRequestDeniedEvent(e));
		}

		if(event.equals("DockingGranted"))
		{
			h.onDockingRequestGranted(new DockingRequestGrantedEvent(e));
		}

		if(event.equals("Docked"))
		{
			h.onDocked(new DockedEvent(e));
		}

		if(event.equals("HeatWarning"))
		{
			h.onOverheating(new OverheatingEvent(e));
		}

		if(event.equals("MissionAccepted"))
		{
			h.onMissionAccepted(new MissionAcceptedEvent(e));
		}

		if(event.equals("ShipyardSwap"))
		{
			h.onShipSwapped(new ShipSwapEvent(e));
		}

		if(event.equals("RefuelAll"))
		{
			h.onRefueled(new RefueledEvent(e));
		}

		if(event.equals("FuelScoop"))
		{
			h.onFuelScooped(new FuelScoopedEvent(e));
		}

		if(event.equals("MissionCompleted"))
		{
			h.onMissionCompleted(new MissionCompletedEvent(e));
		}

		if(event.equals("StoredShips"))
		{
			int has = e.has("ShipsHere") ? e.getJSONArray("ShipsHere").length() : 0;

			if(has > 0)
			{
				if(has == 1)
				{
					talk("You have 1 ship here.");
				}

				else
				{
					talk("You have " + has + " ships here.");
				}
			}
		}

		if(event.equals("CargoDepot"))
		{
			if(e.has("UpdateType") && e.getString("UpdateType").equals("Deliver"))
			{
				int delivered = e.getInt("Count");
				int total = e.getInt("TotalItemsToDeliver");
				int soFar = e.getInt("ItemsDelivered");
				String carg = e.getString("CargoType");
				String s = "Delivered " + delivered + carg + ". ";

				if(total == soFar)
				{
					s += "Delivery Complete.";
				}

				else
				{
					s += (total - soFar) + carg + " remaining.";
				}

				talk(s);
			}
		}

		if(event.equals("CommitCrime"))
		{
			if(e.has("Fine"))
			{
				if(e.getString("CrimeType").equals("collidedAtSpeedInNoFireZone"))
				{
					talk("Fined " + e.getInt("Fine") + " credits for flying like an asshole.");
				}
			}
		}

		return false;
	}

	public static void talk(String text)
	{
		svc.execute(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					Synthesiser synth = new Synthesiser("en-gb");
					InputStream is = synth.getMP3Data(text);
					InputStream bufferedIn = new BufferedInputStream(is);
					Player p = new Player(bufferedIn);
					p.play();
				}

				catch(Exception e)
				{
					e.printStackTrace();
					return;
				}
			}
		});
	}

	private static void readStatus() throws JSONException, IOException
	{
		try
		{
			BufferedReader bb = new BufferedReader(new FileReader(status));
			JSONObject o = new JSONObject(bb.readLine());
			bb.close();
			uiFocus = o.getInt("GuiFocus");
			fireGroup = o.getInt("FireGroup");
			JSONArray ja = o.getJSONArray("Pips");
			pipSystems = ja.getInt(0);
			pipEngines = ja.getInt(1);
			pipWeapons = ja.getInt(2);
			int cv = pipSystems << 2 + pipEngines << 3 + pipWeapons << 4;

			if(cv != cpip)
			{
				cpip = cv;
				printPips();
				state.setPips(pipSystems, pipEngines, pipWeapons);
				h.onPipsChanged(pipSystems, pipEngines, pipWeapons);
			}

			if(cui != uiFocus)
			{
				cui = uiFocus;
				state.setUIFocus(UIFocus.values()[cui]);
				System.out.println("UI Focus: " + F.capitalize(UIFocus.values()[cui].name().toLowerCase()));
				h.onUiFocus(UIFocus.values()[cui]);
			}

			if(cfire != fireGroup)
			{
				cfire = fireGroup;
				state.setFireGroup(cfire);
				System.out.println("Fire Group: #" + cfire);
				h.onFireGroup(cfire);
			}
		}

		catch(Exception e)
		{

		}
	}

	private static void printPips()
	{
		System.out.println("Pips: " + F.pc((double) pipSystems / 12.0) + " " + F.pc((double) pipEngines / 12.0) + " " + F.pc((double) pipWeapons / 12.0) + " ");
	}

	private static void tick() throws InterruptedException, IOException
	{
		if(getLatestJournal() != target)
		{
			try
			{
				retarget();
			}

			catch(FileNotFoundException e)
			{
				e.printStackTrace();
			}
		}

		try
		{
			blocking: while(true)
			{
				String line;

				while((line = bu.readLine()) != null)
				{
					events.add(new JSONObject(line));
					System.out.println(new JSONObject(line).toString(4));
				}

				readStatus();

				while(!events.isEmpty())
				{
					if(handle(events.pop()))
					{
						break blocking;
					}
				}

				Thread.sleep(250);
			}
		}

		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String[] a)
	{
		new JFXPanel(); // this will prepare JavaFX toolkit and environment
		Platform.runLater(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					downloadMarketData();
					events = new GList<JSONObject>();
					folder = new File(new File(System.getProperty("user.home")), "Saved Games/Frontier Developments/Elite Dangerous");
					status = new File(folder, "Status.json");

					if(!folder.exists())
					{
						System.out.println("Unable to locate journal files\n" + folder.getAbsolutePath());
						System.exit(404);
					}

					System.out.println("Journal Directory: " + folder.getAbsolutePath());
					System.out.println("Status File: " + status.getAbsolutePath());
					retarget();
					svc = Executors.newWorkStealingPool(16);

					while(true)
					{
						try
						{
							tick();
							Thread.sleep(50);
						}

						catch(InterruptedException e)
						{
							e.printStackTrace();
						}
					}
				}

				catch(Exception e1)
				{
					e1.printStackTrace();
				}
			}
		});

	}

	public static void retarget() throws IOException
	{
		if(bu != null)
		{
			try
			{
				bu.close();
			}

			catch(IOException e)
			{
				e.printStackTrace();
			}
		}

		target = getLatestJournal();
		start = System.currentTimeMillis();
		bu = new BufferedReader(new FileReader(target));
		dump();
		System.out.println("ReTarget Log: " + target.getName());
	}

	private static void dump() throws IOException
	{
		while(bu.readLine() != null)
		{

		}
	}

	public static File getLatestJournal()
	{
		long biggest = Long.MIN_VALUE;
		File f = null;

		for(File i : folder.listFiles())
		{
			if(i.getName().startsWith("Journal.") && i.getName().endsWith(".log") && i.isFile())
			{
				long time = Long.valueOf(i.getName().split("\\.")[1]);

				if(time > biggest)
				{
					biggest = time;
					f = i;
				}
			}
		}

		return f;
	}
}
