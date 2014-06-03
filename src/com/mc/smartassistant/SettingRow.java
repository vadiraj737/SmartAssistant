package com.mc.smartassistant;

public class SettingRow {
	String value;
	String settingName;
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getSettingName() {
		return settingName;
	}
	public void setSettingName(String settingName) {
		this.settingName = settingName;
	}
	/**
	 * @param value
	 * @param settingName
	 */
	public SettingRow(String value, String settingName) {
		super();
		this.value = value;
		this.settingName = settingName;
	}
	/**
	 * @param value
	 */
	public SettingRow(String value) {
		super();
		this.value = value;
	}
	
	public SettingRow() {
		super();
	}
	

}
