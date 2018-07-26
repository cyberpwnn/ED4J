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
import com.volmit.dumpster.F;
import com.volmit.dumpster.GList;
import com.volmit.dumpster.JSONArray;
import com.volmit.dumpster.JSONException;
import com.volmit.dumpster.JSONObject;

import javazoom.jl.player.Player;

public class ED4J
{
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

	private static void downloadMarketData() throws IOException
	{
		System.out.print("Populating Station Data");
		stationData = download(new URL("https://eddb.io/archive/v5/stations.json"));
		System.out.print("Populating Commodity Data");
		commodityData = download(new URL("https://eddb.io/archive/v5/commodities.json"));
		System.out.print("Populating Module Data");
		moduleData = download(new URL("https://eddb.io/archive/v5/modules.json"));
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

	private static String getPadDistance(int pad)
	{
		int c = 0;
		int m = 1;
		int f = 2;
		int k = 3;

		//@fuckboy:on
		if(pad == 1) {k= m;}
		if(pad == 2) {k= c;}
		if(pad == 3) {k= m;}
		if(pad == 4) {k= f;}
		if(pad == 5) {k= c;}
		if(pad == 6) {k= c;}
		if(pad == 7) {k= m;}
		if(pad == 8) {k= f;}
		if(pad == 9) {k= c;}
		if(pad == 10) {k= f;}
		if(pad == 11) {k= c;}
		if(pad == 12) {k= c;}
		if(pad == 13) {k= c;}
		if(pad == 14) {k= f;}
		if(pad == 15) {k= f;}
		if(pad == 16) {k= c;}
		if(pad == 17) {k= c;}
		if(pad == 18) {k= m;}
		if(pad == 19) {k= f;}
		if(pad == 20) {k= c;}
		if(pad == 21) {k= c;}
		if(pad == 22) {k= m;}
		if(pad == 23) {k= f;}
		if(pad == 24) {k= c;}
		if(pad == 25) {k= f;}
		if(pad == 26) {k= c;}
		if(pad == 27) {k= c;}
		if(pad == 28) {k= m;}
		if(pad == 29) {k= m;}
		if(pad == 30) {k= f;}
		if(pad == 31) {k= f;}
		if(pad == 32) {k= m;}
		if(pad == 33) {k= m;}
		if(pad == 34) {k= c;}
		if(pad == 35) {k= c;}
		if(pad == 36) {k= m;}
		if(pad == 37) {k= m;}
		if(pad == 38) {k= f;}
		if(pad == 39) {k= c;}
		if(pad == 40) {k= f;}
		if(pad == 41) {k= c;}
		if(pad == 42) {k= c;}
		if(pad == 43) {k= m;}
		if(pad == 44) {k= m;}
		if(pad == 45) {k= f;}
		if(k == 0) {return "front.";}
		if(k == 1) {return "middle.";}
		if(k == 2) {return "back.";}
		//@fuckboy:off

		return "";
	}

	private static boolean handle(JSONObject e)
	{
		String event = e.getString("event");
		String station = e.has("StationName") ? e.getString("StationName") : "";

		if(event.equals("Continued"))
		{
			return true;
		}

		if(event.equals("Undocked"))
		{
			talk("Departing from " + station);
		}

		if(event.equals("Scanned"))
		{
			talk(e.getString("ScanType") + " Scan Detected");
		}

		if(event.equals("StartJump"))
		{
			if(e.has("StarSystem"))
			{
				talk("Jumping to " + e.getString("StarSystem") + ". Star class, " + e.getString("StarClass") + ".");
			}
		}

		if(event.equals("FSDJump"))
		{
			talk("Welcome to " + e.getString("StarSystem") + ", " + e.getString("SystemSecurity_Localised"));
		}

		if(event.equals("ReceiveText"))
		{
			if(e.getString("Message").toLowerCase().contains("hostile") || e.getString("Message").toLowerCase().contains("pirate"))
			{
				talk("Hostile Vessel Detected.");
			}
		}

		if(event.equals("SupercruiseExit"))
		{
			if(e.getString("BodyType").equals("Station"))
			{
				talk("Welcome to " + e.getString("Body") + ".");
			}
		}

		if(event.equals("DockingDenied"))
		{
			talk("Request Denied. " + e.getString("Reason"));
		}

		if(event.equals("DockingGranted"))
		{
			String m = getPadDistance(e.getInt("LandingPad"));
			if(!m.isEmpty())
			{
				talk("Your landing pad is located near the " + m);
			}
		}

		if(event.equals("Docked"))
		{
			talk(station + " is experiencing a " + e.getString("FactionState"));
		}

		if(event.equals("MissionAccepted"))
		{
			talk("Mission Accepted");
		}

		if(event.equals("ShipyardSwap"))
		{
			talk("Swapping your " + e.getString("StoreOldShip").replaceAll("-", " ") + " to your " + e.getString("ShipType_Localised").replaceAll("-", " "));
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

		if(event.equals("RefuelAll"))
		{
			talk("Refuled " + F.f(e.getDouble("Amount"), 1) + " tons of fuel");
		}

		if(event.equals("FuelScoop"))
		{
			talk("Scooped " + F.f(e.getDouble("Scooped"), 1) + " tons of fuel");
		}

		if(event.equals("MissionCompleted"))
		{
			talk("Mission Complete. Plus Respect");
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
			}

			if(cui != uiFocus)
			{
				cui = uiFocus;
				System.out.println("UI Focus: " + F.capitalize(UIFocus.values()[cui].name().toLowerCase()));
			}

			if(cfire != fireGroup)
			{
				cfire = fireGroup;
				System.out.println("Fire Group: #" + cfire);
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
