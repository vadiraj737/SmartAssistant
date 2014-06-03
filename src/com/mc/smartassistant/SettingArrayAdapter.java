package com.mc.smartassistant;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SettingArrayAdapter extends ArrayAdapter<SettingRow> {

	private LayoutInflater inflater;
	public SettingArrayAdapter(Context context, 
			List<SettingRow> objects) {
		super(context, R.layout.setting_row, R.id.settingName, objects);
		inflater = LayoutInflater.from(context);
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		SettingRow settingToDisplay = (SettingRow) this.getItem(position);
		TextView settingName, settingValue;
		
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.setting_row, null);
			// find the child values
			settingName = (TextView) convertView.findViewById(R.id.settingName);
			settingValue = (TextView) convertView.findViewById(R.id.settingValue);
			convertView.setTag(new SettingViewHolder(settingName, settingValue));
			
		}
		// reuse existing row view
		else {
			SettingViewHolder viewHolder = (SettingViewHolder) convertView
					.getTag();
			settingName = viewHolder.getSettingName();
			settingValue = viewHolder.getValue();
		}
		settingName.setText(settingToDisplay.getSettingName());
		settingValue.setText(settingToDisplay.getValue());
		return convertView;

	}
	
}
