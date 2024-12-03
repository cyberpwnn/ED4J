package com.volmit.ed4j;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.volmit.dumpster.*;
import com.volmit.ed4j.handler.IEventHandler;
import com.volmit.ed4j.handler.IO;
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
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;


public class ED4J
{
	public static final String EDDB_VERSION = "v6";

	public static File target;
	public static File status;
	public static File voices;
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
	public static IEventHandler h;
	public static EliteState state = new EliteState();
	public static GraphicsDevice device = null;
	public static int monitor = -2;
	private static long last = M.ms();

	private static void downloadMarketData() throws IOException
	{
		File f = new File("mkt.csv");
		//download(new URL("https://eddb.io/archive/v6/listings.csv"), f);
		doIt(f);
	}

	public static void printLazy(String v)
	{
		if(M.ms() - last > 500)
		{
			last = M.ms();
			System.out.println(v);
		}
	}

	public static void doIt(File csvm) throws FileNotFoundException, IOException
	{
		try(ICsvBeanReader beanReader = new CsvBeanReader(new FileReader(csvm), CsvPreference.STANDARD_PREFERENCE))
		{
			final String[] headers = new String[] {"id", "station_id", "commodity_id", "supply", "supply_bracket", "buy_price", "sell_price", "demand", "demand_bracket", "collected_at"};
			final CellProcessor[] processors = getProcessors();
			MarketSector sec;
			GList<MarketSector> sectors = new GList();
			System.out.println("Reading Market Data... Just a sec");
			while((sec = beanReader.read(MarketSector.class, headers, processors)) != null)
			{
				sectors.add(sec);
				printLazy("Digesting " + F.f(sectors.size()) + " Price Entries");
			}

			System.out.println("Analyzing " + F.f(sectors.size() * sectors.size()) + " Potential Trades...");

			double v = sectors.size() * sectors.size();
			AtomicInteger t = new AtomicInteger(0);
			GList<MarketTrade> trades = new GList<>();
			AtomicInteger vv = new AtomicInteger(0);
			ExecutorService e = Executors.newWorkStealingPool(Runtime.getRuntime().availableProcessors());

			for(int ii = 0; ii < sectors.size(); ii++)
			{
				int i= ii;
				e.submit(() -> {
					MarketSector a = sectors.get(i);
					for(int j = 0; j < sectors.size(); j++)
					{
						if(trades.size() > 0)
						{
							printLazy("Found " + F.f(vv.get()) + " Profitable Trades (" + F.pc(t.getAndIncrement() / v, 0) + ") Top Trade: " + trades.get(0));
						}

						MarketSector b = sectors.get(j);
						if(makeTrade(a, b, trades))
						{
							vv.incrementAndGet();
						}

						if(trades.size() > 10000)
						{
							synchronized (trades)
							{
								clean(trades);
							}
						}
					}
				});
			}

			e.shutdown();
			try {
				e.awaitTermination(10000, TimeUnit.DAYS);
			} catch (InterruptedException interruptedException) {
				interruptedException.printStackTrace();
			}

			for(MarketTrade i : trades)
			{
				System.out.println(i.toString());
			}
		}
	}

	private static void clean(GList<MarketTrade> trades) {
		if(trades.size() > 10000)
		{
			trades.sort(Comparator.comparingInt(MarketTrade::getMaxProfit).reversed());
			while(trades.size() > 100)
			{
				trades.removeLast();
			}
		}
	}

	private static boolean makeTrade(MarketSector a, MarketSector b, GList<MarketTrade> trades) {
		if(
				a.getBuyPrice() > 0 && a.getSupply() > 0
						&& b.getSellPrice() > 0 && b.getDemand() > 0
						&& a.getBuyPrice() < b.getSellPrice()
		)
		{
			synchronized (trades)
			{
				trades.add(new MarketTrade(a, b));
			}
			return true;
		}

		else if(
				b.getBuyPrice() > 0 && b.getSupply() > 0
						&& a.getSellPrice() > 0 && a.getDemand() > 0
						&& b.getBuyPrice() < a.getSellPrice()
		)
		{
			synchronized (trades)
			{
				trades.add(new MarketTrade(b, a));
			}
			return true;
		}
		return false;
	}

	private static CellProcessor[] getProcessors()
	{
		final CellProcessor[] processors = new CellProcessor[] {
				new Optional(new ParseInt()),
				new Optional(new ParseInt()),
				new Optional(new ParseInt()),
				new Optional(new ParseInt()),
				new Optional(new ParseInt()),
				new Optional(new ParseInt()),
				new Optional(new ParseInt()),
				new Optional(new ParseInt()),
				new Optional(new ParseInt()),
				new Optional(new ParseInt()),};

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
	
	private static void download(URL u, File f) throws IOException
	{
		URLConnection conn = u.openConnection();
		long estimatedSize = conn.getContentLengthLong();
		System.out.print(" -> Downloading: " + F.fileSize(estimatedSize));
		FileOutputStream out = new FileOutputStream(f);
		IO.fillTransfer(conn.getInputStream(), out);
		out.close();
		System.out.println(" -> Done");
	}

	private static boolean handle(JSONObject e)
	{
		updateGraphicsDevice();
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
		
		if(event.equals("LoadGame"))
		{
			h.onLoadGame(e.getString("Commander"), e.getString("Ship"), e.getLong("Credits"));
		}
		
		if(event.equals("ReceiveText"))
		{
			h.onReceiveText(e.getString("Channel"), e.getString("Message_Localized"), e.getString("From"), e.getString("Message"));
		}
		
		if(event.equals("Location"))
		{
			h.onLocation();
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

	private static void updateGraphicsDevice()
	{
		int v = monitor;
		monitor = findMonitor();
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();

		try
		{
			device = gs[monitor];
			if(v != monitor)
			{
				System.out.println("Graphics Screens: " + gs.length);
				System.out.println("Found Elite Dangerous on Monitor " + monitor + " (" + device.getIDstring() + ")");
			}
		}

		catch(Throwable e)
		{
			if(v != monitor)
			{
				System.out.println("Failed to find Graphics Device");
			}
		}
	}

	public static String readAll(File f) throws IOException
	{
		BufferedReader bu = new BufferedReader(new FileReader(f));
		String c = "";
		String l = "";

		while((l = bu.readLine()) != null)
		{
			c += l + "\n";
		}

		bu.close();

		return c;
	}

	private static int findMonitor()
	{
		File f = new File(System.getenv("APPDATA"));
		f = f.getParentFile();
		f = new File(f, "local/Frontier Developments/Elite Dangerous/Options/Graphics/DisplaySettings.xml");

		if(f.exists())
		{
			try
			{
				String o = readAll(f);

				for(String i : o.split("\\Q\n\\E"))
				{
					if(i.contains("<Monitor>"))
					{
						return Integer.valueOf(i.split("\\Q</\\E")[0].split("\\Q>\\E")[1]);
					}
				}
			}

			catch(IOException e)
			{
				e.printStackTrace();
			}
		}

		else
		{
			System.out.println("Cannot find graphics options at " + f.getAbsolutePath());
		}

		return -1;
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

					voices = new File("C:\\Program Files (x86)\\VoiceAttack\\Sounds\\voices");
					System.out.println("Journal Directory: " + folder.getAbsolutePath());
					System.out.println("Status File: " + status.getAbsolutePath());
					System.out.println("Voices: " + voices.getAbsolutePath());
					h = new SoundEventHandler(voices);
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
