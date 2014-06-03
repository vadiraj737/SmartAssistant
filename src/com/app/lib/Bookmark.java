package com.app.lib;

public class Bookmark {


	private String name;
	private double lat;
	private double lon;
	private String address;
	private long rowId;
	
	public static enum Field{
		NAME, ADDR, LAT, LON 
	}
	
	
	public Bookmark() {
	}


	public long getRowId() {
		return rowId;
	}


	public void setRowId(long rowId) {
		this.rowId = rowId;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public double getLat() {
		return lat;
	}


	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}


	public String getAddress() {
		return address;
	}


	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

}
