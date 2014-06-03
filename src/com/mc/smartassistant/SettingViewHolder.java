package com.mc.smartassistant;

import android.widget.TextView;

public class SettingViewHolder {

	private TextView value;
	private TextView settingName;
	public TextView getValue() {
		return value;
	}
	public void setValue(TextView value) {
		this.value = value;
	}
	public TextView getSettingName() {
		return settingName;
	}
	public void setSettingName(TextView settingName) {
		this.settingName = settingName;
	}
	/**
	 * @param value
	 * @param settingName
	 */
	public SettingViewHolder(TextView value, TextView settingName) {
		super();
		this.value = value;
		this.settingName = settingName;
	}
	
}
