package com.mc.dbtables;

public class SituationTable {
	String situationName;
	int situationID;
	public String getSituationName() {
		return situationName;
	}
	public void setSituationName(String situationName) {
		this.situationName = situationName;
	}
	public int getSituationID() {
		return situationID;
	}
	public void setSituationID(int situationID) {
		this.situationID = situationID;
	}
	public SituationTable(String situationName, int situationID) {
		super();
		this.situationName = situationName;
		this.situationID = situationID;
	}
	public SituationTable() {
		super();
	} 
	
}
