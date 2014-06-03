package com.mc.smartassistant;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;

public class SituationArrayAdapter extends ArrayAdapter<String> {

	@SuppressWarnings("unused")
	private LayoutInflater inflater;

	public SituationArrayAdapter(Context context, 
			List<String> objects) {
		super(context, R.layout.list_row, R.id.rowTextView, objects);
		inflater = LayoutInflater.from(context);
	}
	
}
