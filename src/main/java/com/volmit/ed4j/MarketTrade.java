package com.volmit.ed4j;

import com.volmit.dumpster.F;
import com.volmit.ed4j.util.MarketSector;

public class MarketTrade {
    private final MarketSector buy;
    private final MarketSector sell;

    public MarketTrade(MarketSector buy, MarketSector sell)
    {
        this.buy = buy;
        this.sell = sell;
    }

    public MarketSector getBuy()
    {
        return buy;
    }

    public MarketSector getSell()
    {
        return sell;
    }

    public int getMaxTonnage()
    {
        return Math.min(buy.getSupply(), sell.getDemand());
    }

    public int getProfitPerTon()
    {
        return sell.getSellPrice() - buy.getBuyPrice();
    }

    public int getMaxProfit()
    {
        return getProfitPerTon() * getMaxTonnage();
    }

    public String toString()
    {
       return "Buy [" + buy.getCommodityId() + "] " + buy.getStationId() + " -> " + sell.getStationId() + " Profit: " + F.f(getProfitPerTon()) + "cr X " + F.f(getMaxTonnage()) + "t = " + F.f(getMaxProfit()) + "cr";
    }
}
