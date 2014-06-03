package com.app.lib;

import java.io.Serializable;


@SuppressWarnings("serial")
public class LocationValues implements Serializable {

	private String body;
    private String addr;
    private double range;
    private int event;
    private double lat;
    private double lon;
    private int situationID;
    
    public int getSituationID() {
		return situationID;
	}
    public void setSituationID(int situationID) {
		this.situationID = situationID;
	};
    
    // values of alert field
    
   	
    // values of event field
    public static enum Event {    	
    	ON_ENTRY, 
    	ON_EXIT, 
    	ON_ENTRY_EXIT
    }
    
    public static double DEFAULT_RANGE = 0.25; // approx. 0.25 miles
    
    public static Event DEFAULT_EVENT = Event.ON_ENTRY;
    
	public LocationValues() {
		// TODO Auto-generated constructor stub
	}
	public LocationValues(String title, String body, String addr, int state,
			int alert, double range, int event, double lat, double lon, int situationID) {
		super();
		this.body = body;
		this.addr = addr;
		this.range = range;
		this.event = event;
		this.lat = lat;
		this.lon = lon;
		this.situationID = situationID;
	}

	/**
	 * @return the lat
	 */
	public double getLat() {
		return lat;
	}

	/**
	 * @param lat the lat to set
	 */
	public void setLat(double lat) {
		this.lat = lat;
	}

	/**
	 * @return the lon
	 */
	public double getLon() {
		return lon;
	}

	/**
	 * @param lon the lon to set
	 */
	public void setLon(double lon) {
		this.lon = lon;
	}



	/**
	 * @return the body
	 */
	public String getBody() {
		return body;
	}


	/**
	 * @param body the body to set
	 */
	public void setBody(String body) {
		this.body = body;
	}


	/**
	 * @return the addr
	 */
	public String getAddr() {
		return addr;
	}


	/**
	 * @param addr the addr to set
	 */
	public void setAddr(String addr) {
		this.addr = addr;
	}



	/**
	 * @return the range
	 */
	public double getRange() {
		return range;
	}


	/**
	 * @param range the range to set
	 */
	public void setRange(double range) {
		this.range = range;
	}


	/**
	 * @return the event
	 */
	public int getEvent() {
		return event;
	}


	/**
	 * @param event the event to set
	 */
	public void setEvent(int event) {
		this.event = event;
	}


}
