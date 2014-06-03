package com.mc.smartassistant;

import android.widget.CheckBox;
import android.widget.TextView;

public class SelectViewHolder {
	private CheckBox checkBox;
	private TextView textView;
	/**
	 * @param checkBox
	 * @param textView
	 */
	public SelectViewHolder(CheckBox checkBox, TextView textView) {
		super();
		this.checkBox = checkBox;
		this.textView = textView;
	}
	/**
	 * 
	 */
	public SelectViewHolder() {
		super();
	}
	public CheckBox getCheckBox() {
		return checkBox;
	}
	public void setCheckBox(CheckBox checkBox) {
		this.checkBox = checkBox;
	}
	public TextView getTextView() {
		return textView;
	}
	public void setTextView(TextView textView) {
		this.textView = textView;
	}
	

}
