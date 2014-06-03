package com.mc.smartassistant;

import java.util.List;

import com.mc.smartassistant.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class SelectArrayAdapter extends ArrayAdapter<ContactRow> {

	private LayoutInflater inflater;

	public SelectArrayAdapter(Context context, 
			List<ContactRow> objects) {
		super(context, R.layout.list_row, R.id.rowTextView, objects);
		inflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ContactRow contactToDisplay = (ContactRow) this.getItem(position);

		CheckBox checkBox;
		TextView textView;

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_row, null);
			// find the child values
			textView = (TextView) convertView.findViewById(R.id.rowTextView);
			checkBox = (CheckBox) convertView.findViewById(R.id.contactCheckBox);
			convertView.setTag(new SelectViewHolder(checkBox, textView));
			checkBox.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					CheckBox cb = (CheckBox) v;
					ContactRow c = (ContactRow) cb.getTag();
					c.setChecked(c.isChecked());
				}
			});

		}
		// reuse existing row view
		else {
			SelectViewHolder viewHolder = (SelectViewHolder) convertView
					.getTag();
			checkBox = viewHolder.getCheckBox();
			textView = viewHolder.getTextView();
		}
		checkBox.setTag(contactToDisplay);
		// displaying contact data
		checkBox.setChecked(contactToDisplay.isChecked());
		textView.setText(contactToDisplay.getName());
		return convertView;

	}
}
