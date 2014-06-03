package com.mc.smartassistant;

import java.util.ArrayList;


import com.mc.smartassistant.SituationActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class ContactListActivity extends Activity {

	ListView myList;
	ArrayAdapter<ContactRow> listAdapter;
	private ArrayList<ContactRow> contactData;
	
	

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.listview);
		contactData = new ArrayList<ContactRow>();
		myList = (ListView) findViewById(R.id.list);
		myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View item, int position,
					long id) {
				ContactRow contact = listAdapter.getItem(position);
				contact.toggleChecked();
				SelectViewHolder viewHolder= (SelectViewHolder) item.getTag();
				viewHolder.getCheckBox().setChecked(contact.isChecked());
			}
		});
				// String[] from = new String[]
		// {ContactsContract.Contacts.DISPLAY_NAME,ContactsContract.Contacts.HAS_PHONE_NUMBER,ContactsContract.Contacts._ID};
		//String[] from = new String[] { ContactsContract.Contacts.DISPLAY_NAME };

		// String[] from = new String[] {"kar", "vadi"};
		// int[] to = new int[] {R.id.checkBox};
		// ContactListCursorAdapter adapter = new
		// ContactListCursorAdapter(getApplicationContext(), R.layout.listview,
		// cursor, from, to);
		Cursor cursor = getContentResolver().query(
				ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		if(cursor!=null)
		if(cursor.moveToFirst())
		 do{
			try {
				String contactId = cursor.getString(cursor
						.getColumnIndex(ContactsContract.Contacts._ID));
				String name = cursor
						.getString(cursor
								.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				 //String hasPhone =
				 //cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
				if (Integer
						.parseInt(cursor.getString(cursor
								.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
					Cursor phones = getContentResolver().query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ " = " + contactId, null, null);
					while (phones.moveToNext()) {
						String phoneNumber = phones
								.getString(phones
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						ContactRow c = new ContactRow();
						c.setName(name);
						c.setPhoneNumber(phoneNumber);
						//contactData.add(contactId);
				
				
				contactData.add(c);
					
					//phones.close();
					}
				}
			} catch (Exception e) {
			}
		}while (cursor.moveToNext());

		listAdapter = new SelectArrayAdapter(this, contactData);
		
		/*ArrayAdapter<String> adapter

		= new ArrayAdapter<String>(this,

		android.R.layout.simple_list_item_1,

		contactData);
		 */
		myList.setAdapter(listAdapter);

		Button donePickingContacts = (Button)findViewById(R.id.buttonContactPick);
		donePickingContacts.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Intent intent = new Intent(ContactListActivity.this, SituationActivity.class);
				//Bundle contactBundle = new Bundle();
				//contactBundle.putParcelableArrayList("contacts", contactData);
				//intent.putExtra("contactsBundle", contactBundle);
				
				//ArrayList <ContactRow> addyExtras = new ArrayList <ContactRow>();

				//intent.putExtra("contactSize", contactData.size());
				//Toast.makeText(getBaseContext(), ""+contactData.size()+"", Toast.LENGTH_LONG).show();
				int checkedContactContact = 0;
				for (int i = 0; i < contactData.size(); i++)
	            {
	            	if(contactData.get(i).getChecked())
	            	{
	            		
	            		intent.putExtra(""+checkedContactContact+"name", contactData.get(i).getName());
		            	intent.putExtra(""+checkedContactContact+"checked", contactData.get(i).getChecked());
		            	intent.putExtra(""+checkedContactContact+"number", contactData.get(i).getPhoneNumber());
		            	
		            	//Toast.makeText(getBaseContext(), contactData.get(i).getChecked().toString(), Toast.LENGTH_LONG).show();
		            	//Toast.makeText(getBaseContext(), contactData.get(i).getChecked().toString(), Toast.LENGTH_LONG).show();
		            	checkedContactContact++;
	            	}
	            	
					
	            }
				intent.putExtra("contactSize", checkedContactContact);
				//Toast.makeText(getBaseContext(), ""+checkedContactContact+"", Toast.LENGTH_LONG).show();
				
				//intent.putParcelableArrayListExtra("contacts", contactData);
				//intent.putParcelableArrayListExtra("contacts,com.mc.contactpicker", contactData);
				//intent.put
				//ContactRow c = (ContactRow)contactBundle.getParcelableArrayList("contacts").get(0);
				//intent.putExtra("str", c.getName());
				//Toast.makeText(getBaseContext(), c.getName(), Toast.LENGTH_LONG).show();
				//Toast.makeText(getBaseContext(), c.getChecked().toString(), Toast.LENGTH_LONG).show();
				
				ContactListActivity.this.setResult(RESULT_OK, intent);
				ContactListActivity.this.finish();
				
			}
		});		

	}

}
