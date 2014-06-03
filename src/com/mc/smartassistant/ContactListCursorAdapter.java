package com.mc.smartassistant;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.widget.SimpleCursorAdapter;

public class ContactListCursorAdapter extends SimpleCursorAdapter {
	private Context context;

	@SuppressWarnings("unused")
	private int layout;
	@SuppressWarnings("unused")
	private Cursor cursor;

	public ContactListCursorAdapter(Context context, int layout, Cursor cursor,
			String[] from, int[] to) {
		super(context, layout, cursor, from, to);
		this.context = context;
		this.layout = layout;
		this.cursor = cursor;
		
	}

	@Override
	public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
		/*String[] projection = new String[] {
				ContactsContract.Contacts.DISPLAY_NAME,
				ContactsContract.Contacts.HAS_PHONE_NUMBER,
				ContactsContract.Contacts._ID };*/
		String[] projection = {"kar", "vadi"};
		

		return context.getContentResolver().query(
				ContactsContract.Contacts.CONTENT_URI, projection, null, null,
				null);
	}

}
