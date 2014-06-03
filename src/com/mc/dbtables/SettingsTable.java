package com.mc.dbtables;

public class SettingsTable {
	int situationID;
	String ringtoneID;
	int bluetoothStatus, wifiStatus;
	double brightnessValue;


	public SettingsTable(int situationID, String ringtoneID,
			int bluetoothStatus, int wifiStatus, double brightnessValue) {
		super();
		this.situationID = situationID;
		this.ringtoneID = ringtoneID;
		this.bluetoothStatus = bluetoothStatus;
		this.wifiStatus = wifiStatus;
		this.brightnessValue = brightnessValue;
	}
	
	public SettingsTable() {
		super();
		this.situationID = 0;
		this.ringtoneID = "NULL";
		this.bluetoothStatus = 999;
		this.wifiStatus = 999;
		this.brightnessValue = 0.0;
	}
	

	public int getSituationID() {
		return situationID;
	}

	public void setSituationID(int situationID) {
		this.situationID = situationID;
	}

	public String getRingtoneID() {
		return ringtoneID;
	}

	public void setRingtoneID(String ringtoneID) {
		this.ringtoneID = ringtoneID;
	}

	public int getBluetoothStatus() {
		return bluetoothStatus;
	}

	public void setBluetoothStatus(int bluetoothStatus) {
		this.bluetoothStatus = bluetoothStatus;
	}

	public int getWifiStatus() {
		return wifiStatus;
	}

	public void setWifiStatus(int wifiStatus) {
		this.wifiStatus = wifiStatus;
	}

	public double getBrightnessValue() {
		return brightnessValue;
	}

	public void setBrightnessValue(double brightnessValue) {
		this.brightnessValue = brightnessValue;
	}

	
	
}
