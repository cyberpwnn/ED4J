package com.volmit.ed4j;

public class MarketSector
{
	private int stationId;
	private int commodityId;
	private int idSupply;
	private int supply;
	private int supplyBracket;
	private int buyPrice;
	private int sellPrice;
	private int demand;
	private int demandBracket;
	private int collectedAt;

	public MarketSector(int stationId, int commodityId, int idSupply, int supply, int supplyBracket, int buyPrice, int sellPrice, int demand, int demandBracket, int collectedAt)
	{
		this.stationId = stationId;
		this.commodityId = commodityId;
		this.idSupply = idSupply;
		this.supply = supply;
		this.supplyBracket = supplyBracket;
		this.buyPrice = buyPrice;
		this.sellPrice = sellPrice;
		this.demand = demand;
		this.demandBracket = demandBracket;
		this.collectedAt = collectedAt;
	}

	public int getStationId()
	{
		return stationId;
	}

	public void setStationId(int stationId)
	{
		this.stationId = stationId;
	}

	public int getCommodityId()
	{
		return commodityId;
	}

	public void setCommodityId(int commodityId)
	{
		this.commodityId = commodityId;
	}

	public int getIdSupply()
	{
		return idSupply;
	}

	public void setIdSupply(int idSupply)
	{
		this.idSupply = idSupply;
	}

	public int getSupply()
	{
		return supply;
	}

	public void setSupply(int supply)
	{
		this.supply = supply;
	}

	public int getSupplyBracket()
	{
		return supplyBracket;
	}

	public void setSupplyBracket(int supplyBracket)
	{
		this.supplyBracket = supplyBracket;
	}

	public int getBuyPrice()
	{
		return buyPrice;
	}

	public void setBuyPrice(int buyPrice)
	{
		this.buyPrice = buyPrice;
	}

	public int getSellPrice()
	{
		return sellPrice;
	}

	public void setSellPrice(int sellPrice)
	{
		this.sellPrice = sellPrice;
	}

	public int getDemand()
	{
		return demand;
	}

	public void setDemand(int demand)
	{
		this.demand = demand;
	}

	public int getDemandBracket()
	{
		return demandBracket;
	}

	public void setDemandBracket(int demandBracket)
	{
		this.demandBracket = demandBracket;
	}

	public int getCollectedAt()
	{
		return collectedAt;
	}

	public void setCollectedAt(int collectedAt)
	{
		this.collectedAt = collectedAt;
	}

	@Override
	public String toString()
	{
		return "MarketSector [stationId=" + stationId + ", commodityId=" + commodityId + ", idSupply=" + idSupply + ", supply=" + supply + ", supplyBracket=" + supplyBracket + ", buyPrice=" + buyPrice + ", sellPrice=" + sellPrice + ", demand=" + demand + ", demandBracket=" + demandBracket + ", collectedAt=" + collectedAt + "]";
	}
}
