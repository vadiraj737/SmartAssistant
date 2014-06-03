package com.mc.dbtables;

public class BlacklistTable {

	int situationID;
	String number;
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}public BlacklistTable() {
		super();
	}
	public int getSituationID() {
		return situationID;
	}
	public void setSituationID(int situationID) {
		this.situationID = situationID;
	}
	public BlacklistTable(int situationID, String number) {
		super();
		this.situationID = situationID;
		this.number = number;
	}
	
}
