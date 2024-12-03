package com.volmit.ed4j.util;

public class MarketSector
{
	private int id;
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

	public MarketSector()
	{
		
	}
	
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
	
	

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public int getStationId()
	{
		return stationId;
	}

	public void setStation_id(int stationId)
	{
		this.stationId = stationId;
	}

	public int getCommodityId()
	{
		return commodityId;
	}

	public void setCommodity_id(int commodityId)
	{
		this.commodityId = commodityId;
	}

	public int getIdSupply()
	{
		return idSupply;
	}
	
	

	public void setId_supply(int idSupply)
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

	public void setSupply_bracket(int supplyBracket)
	{
		this.supplyBracket = supplyBracket;
	}

	public int getBuyPrice()
	{
		return buyPrice;
	}

	public void setBuy_price(int buyPrice)
	{
		this.buyPrice = buyPrice;
	}

	public int getSellPrice()
	{
		return sellPrice;
	}

	public void setSell_price(int sellPrice)
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

	public void setDemand_bracket(int demandBracket)
	{
		this.demandBracket = demandBracket;
	}

	public int getCollectedAt()
	{
		return collectedAt;
	}

	public void setCollected_at(int collectedAt)
	{
		this.collectedAt = collectedAt;
	}

	@Override
	public String toString()
	{
		return "MarketSector [stationId=" + stationId + ", commodityId=" + commodityId + ", idSupply=" + idSupply + ", supply=" + supply + ", supplyBracket=" + supplyBracket + ", buyPrice=" + buyPrice + ", sellPrice=" + sellPrice + ", demand=" + demand + ", demandBracket=" + demandBracket + ", collectedAt=" + collectedAt + "]";
	}
}
