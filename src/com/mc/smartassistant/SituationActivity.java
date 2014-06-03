package com.mc.smartassistant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.lib.GpsPoint;
import com.app.lib.LocationValues;
import com.mc.dbtables.BlacklistTable;
import com.mc.dbtables.EventDetails;
import com.mc.dbtables.SettingsTable;
import com.mc.dbtables.SituationTable;

public class SituationActivity extends FragmentActivity implements
SelectSettingsDialogFragment.NoticeDialogListener, SelectConditionsDialogFragment.ConditionsNoticeDialogListener {
	static LocationValues mLocationValues;
	SettingsTable settingsTable;
	SituationTable situationTable;
	ArrayList<BlacklistTable> blacklistTables;
	ListView settingsList;
	Integer situationID = 0;
	ArrayAdapter<SettingRow> settingsListAdapter;
	int reqCode;
	static int LOCATION_REQUEST_CODE = 1;
	static int EVENT_REQUEST_CODE = 9;
	static int BRIGHTNESS_REQUEST_CODE = 2;
	static int BRIGHTNESS_EDIT_REQUEST_CODE = 22;

	static int RINGTONE_REQUEST_CODE = 3;
	static int BLUETOOTH_REQUEST_CODE = 4;
	static int WIFI_REQUEST_CODE = 5;
	static int BLOCKEDCALLS_REQUEST_CODE = 6;
	String addr, situationName;
	private ProgressDialog m_ProgressDialog = null;

	@SuppressWarnings("unused")
	private Long mRowId;
	LocationDbAdapter mDbHelper;
	private EventDetails event; 

	private enum Action {CONFIRM, CANCEL, DELETE};
	public static enum ResultCode{
		SAVED, INVALID, SAVE_ERROR
	}
	public void showNoticeDialog() {
		// Create an instance of the dialog fragment and show it
		DialogFragment dialog = new SelectSettingsDialogFragment();
		dialog.show(getSupportFragmentManager(), "SelectSettingsDialogFragment");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		if (resultCode == RESULT_OK && requestCode == EVENT_REQUEST_CODE) {
			Bundle extras= intent.getExtras();
			if(extras!=null)
			{
				event= new EventDetails();
				event.setDay(extras.getInt(LocationDbAdapter.COLUMN_DAY));
				event.setMonth(extras.getInt(LocationDbAdapter.COLUMN_MONTH));
				event.setYear(extras.getInt(LocationDbAdapter.COLUMN_YEAR));
				event.setStartHour(extras.getInt(LocationDbAdapter.COLUMN_STARTHOUR));
				event.setStartMinute(extras.getInt(LocationDbAdapter.COLUMN_STARTMINUTE));
				event.setEndHour(extras.getInt(LocationDbAdapter.COLUMN_ENDHOUR));
				event.setEndMinute(extras.getInt(LocationDbAdapter.COLUMN_ENDMINUTE));

			}
		}
		if (resultCode == RESULT_OK && requestCode == LOCATION_REQUEST_CODE) {
			// Vadya Code to fetch the location details that have to be saved
			// Toast.makeText(getBaseContext(), "Testing Result again",
			// Toast.LENGTH_LONG).show();
			Bundle extras = intent.getExtras();
			if(extras !=null){
				mLocationValues = new LocationValues();
				mLocationValues.setAddr(extras.getString("address"));
				mLocationValues.setBody(extras.getString("body"));
				mLocationValues.setEvent(extras.getInt("event"));
				mLocationValues.setLat(extras.getDouble("lat"));
				mLocationValues.setLon(extras.getDouble("lon"));
				mLocationValues.setRange(extras.getDouble("range"));

				/*				Toast.makeText(getBaseContext(), "The address is:"+mLocationValues.getAddr(),
						Toast.LENGTH_LONG).show();
				 */				
				TextView locView = (TextView) findViewById(R.id.locationValue);
				locView.setText(mLocationValues.getBody());
			}
			else{



			}/*
			GpsPoint point = new GpsPoint(
					(int) (mLocationValues.getLat() * 1E6),
					(int) (mLocationValues.getLon() * 1E6));
			addr = convertPointToLocation(point);
			 */	} else if (resultCode == RESULT_OK
					 && requestCode == BLOCKEDCALLS_REQUEST_CODE) {

				 if(blacklistTables==null)
				 {
					 blacklistTables = new ArrayList<BlacklistTable>();
				 }
				 Bundle extras = intent.getExtras();
				 int size = 0;
				 size = extras.getInt("contactsSize");

				 if(settingsTable == null)
				 {
					 settingsTable = new SettingsTable();
				 }
				 TextView callBlockingValue = (TextView) findViewById(R.id.blockcallsvalue);
				 callBlockingValue.setText("" + size + " Contacts Selected To Block");

				 for (int i = 0; i < size; i++) {
					 ContactRow c = new ContactRow();

					 BlacklistTable tempBlacklistTable = new BlacklistTable();

					 c.setName(intent.getStringExtra("" + i + "name"));
					 c.setChecked(true);
					 c.setPhoneNumber(intent.getStringExtra(""+i+"number"));
				//	 Toast.makeText(getBaseContext(), c.getPhoneNumber(),
					//		 Toast.LENGTH_LONG).show();
					 tempBlacklistTable.setSituationID(situationID);
					 tempBlacklistTable.setNumber(c.getPhoneNumber());
					 // c.setChecked(data.getBooleanExtra(""+i+"checked", false));
					 // Toast.makeText(getBaseContext(), c.getName(),
					 // Toast.LENGTH_LONG).show();
					 blacklistTables.add(tempBlacklistTable);
				 }
				 // Toast.makeText(getBaseContext(), s, Toast.LENGTH_LONG).show();
			 }


			 else if (requestCode == RINGTONE_REQUEST_CODE) {

				 if(resultCode == RESULT_CANCELED)
				 {
					 if(settingsTable == null)
					 {
						 settingsTable = new SettingsTable();
					 }
					 settingsTable.setRingtoneID("null");
				 }
				 if(resultCode == RESULT_OK)
				 {

					 if(settingsTable == null)
					 {
						 settingsTable = new SettingsTable();
					 }

					 Bundle extras = intent.getExtras();
					 String s = extras.getString("RingtoneName");
					 TextView ringtoneValue = (TextView) findViewById(R.id.ringtonevalue);
					 ringtoneValue.setText(s);
					 settingsTable.setRingtoneID(s);
				 }

			 } else if (resultCode == RESULT_OK
					 && requestCode == BLUETOOTH_REQUEST_CODE) {


				 if(settingsTable == null)
				 {
					 settingsTable = new SettingsTable();
				 }

				 Bundle extras = intent.getExtras();
				 boolean s = false;
				 s = extras.getBoolean("bluetooth");
				 TextView bluetoothValue = (TextView) findViewById(R.id.bluetoothvalue);
				 if (s)
				 {
					 bluetoothValue.setText("ON");
					 settingsTable.setBluetoothStatus(1);

				 }

				 else
				 {
					 bluetoothValue.setText("OFF");
					 settingsTable.setBluetoothStatus(0);

				 }


			 } else if (resultCode == RESULT_OK && requestCode == WIFI_REQUEST_CODE) {


				 if(settingsTable == null)
				 {
					 settingsTable = new SettingsTable();
				 }

				 Bundle extras = intent.getExtras();
				 boolean s = false;
				 s = extras.getBoolean("wifi");
				 TextView wifiValue = (TextView) findViewById(R.id.wifivalue);
				 if (s)
				 {
					 wifiValue.setText("ON");
					 settingsTable.setWifiStatus(1);
				 }

				 else
				 {
					 wifiValue.setText("OFF");
					 settingsTable.setWifiStatus(0);
				 }


			 } else if (resultCode == RESULT_OK
					 && requestCode == BRIGHTNESS_REQUEST_CODE) {


				 if(settingsTable == null)
				 {
					 settingsTable = new SettingsTable();
				 }

				 TextView brightnessValue = (TextView) findViewById(R.id.brightnessvalue);
				 Bundle extras = intent.getExtras();
				 double val = extras.getInt("brightnessValue");
				 val = val / 2.56;
				 brightnessValue.setText("" + val + "%");
				 settingsTable.setBrightnessValue(val);
			 } else {
				 // Toast.makeText(getBaseContext(),
				 // "recieved nothing",Toast.LENGTH_LONG).show();
			 }

	}

	public void setDefaultTextViews() {
		TextView brightnessValue, bluetoothValue, ringtoneValue, wifiValue, callBlockingValue;

		// setting system brightness value
		brightnessValue = (TextView) findViewById(R.id.brightnessvalue);
		/*try {
			int temp = 0;
			temp = android.provider.Settings.System.getInt(
					getContentResolver(),
					android.provider.Settings.System.SCREEN_BRIGHTNESS);
			double percentage = temp / 2.56;
			brightnessValue.setText("" + percentage + "%");
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}
		 */
		brightnessValue.setText(R.string.valueNotSet);
		// setting current bluetooth value

		bluetoothValue = (TextView) findViewById(R.id.bluetoothvalue);
		bluetoothValue.setText(R.string.valueNotSet);
		
		wifiValue = (TextView) findViewById(R.id.wifivalue);
		wifiValue.setText(R.string.valueNotSet);
		/*
		WifiManager wifiManager = (WifiManager) getBaseContext()
				.getSystemService(Context.WIFI_SERVICE);
		if (wifiManager.isWifiEnabled())
			wifiValue.setText("ON");
		else
			wifiValue.setText("OFF");
		 */
		// setting system ringtone value
		ringtoneValue = (TextView) findViewById(R.id.ringtonevalue);
		ringtoneValue.setText(R.string.valueNotSet);
		/*
		Ringtone ringtone = RingtoneManager.getRingtone(this,
				Settings.System.DEFAULT_RINGTONE_URI);
		if (ringtone != null) {
			ringtoneValue.setText(ringtone.getTitle(getBaseContext()));

		}

		else {

			ringtoneValue.setText("<unknown>");
		}
		 */

		// setting callblocking
		callBlockingValue = (TextView) findViewById(R.id.blockcallsvalue);
		callBlockingValue.setText(R.string.valueNotSet);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_situation);
		Button addSetting = (Button) findViewById(R.id.addSettings);
		Button addCondition = (Button) findViewById(R.id.addCondition);
		Bundle extras = getIntent().getExtras();

		mDbHelper = new LocationDbAdapter(this);
		mDbHelper.open();
		mRowId = (savedInstanceState == null) ? null :
			(Long) savedInstanceState.getSerializable(LocationDbAdapter.KEY_SITUATIONID);
		/*if (mRowId == null) {
			Bundle extras1 = getIntent().getExtras();
			mRowId = extras1 != null ? extras1.getLong(LocationDbAdapter.KEY_SITUATIONID)
									: null;
		}
		 */	/*	Toast.makeText(getBaseContext(), "Row id:"+mRowId, Toast.LENGTH_LONG).show();
		 */	
		setDefaultTextViews();
		situationTable = new SituationTable();
		//settingsTable = new SettingsTable();
		reqCode = extras.getInt("ReqCode");
		if (reqCode == 1) {
			addSetting.setText("Add Settings");
		} else if (reqCode == 2) {
			addCondition.setText("Edit Conditions");
			addSetting.setText("Edit Settings");
			situationName = extras.getString("SituationName");
			situationID = extras.getInt("SituationID");
			fillTextViews(situationID);
		}
		// settingsData = new ArrayList<SettingRow>();


		Button deleteButton = (Button) findViewById(R.id.deleteButton);
		deleteButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				deleteSituation(situationID);
				Intent i = new Intent(SituationActivity.this,
						MainActivity.class);
				i.putExtra("deletedSituationID", situationID);
				SituationActivity.this.setResult(3, i);
				SituationActivity.this.finish();
			}
		});

		Button cancelButton = (Button) findViewById(R.id.cancelButton);
		cancelButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Intent i = new Intent(SituationActivity.this,
						MainActivity.class);
				SituationActivity.this.setResult(RESULT_CANCELED, i);
				SituationActivity.this.finish();
			}
		});

		Button saveButton = (Button) findViewById(R.id.saveButton);
		saveButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Intent i = new Intent(SituationActivity.this,
						MainActivity.class);
				EditText editTextValue = (EditText) findViewById(R.id.situationNameTextBox);
				situationName = editTextValue.getText().toString();
				situationTable.setSituationName(situationName);
				performAction(Action.CONFIRM);
				i.putExtra("SituationName", editTextValue.getText().toString());
				i.putExtra("SituationID", situationID);
				SituationActivity.this.setResult(RESULT_OK, i);
				SituationActivity.this.finish();
			}
		});

		addSetting.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				DialogFragment newFragment = new SelectSettingsDialogFragment();
				newFragment.show(getSupportFragmentManager(),
						"SelectSettingsDialogFragment");

			}
		});

		addCondition.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				DialogFragment newFragment = new SelectConditionsDialogFragment();
				newFragment.show(getSupportFragmentManager(),
						"SelectConditionsDialogFragment");
			}

		});

	}
	@Override
	protected void onPause() {
		Log.e("SituationActivity", "Inside pause called");
		//mDbHelper.close();
		super.onPause();
	}
	@Override
	protected void onResume() {
		Log.e("SituationActivity", "Inside resume called");
		if(mDbHelper==null){
			mDbHelper.open();
		}
		super.onResume();
	}/*
	@Override
	protected void onDestroy() {
		Log.e("SituationActivity", "Inside destroy called");
		mDbHelper.close();
		super.onDestroy();
	}
	*/protected void deleteSituation(Integer situationID2) {
		LocationDbAdapter deleteHelper = new LocationDbAdapter(this);
		deleteHelper.open();
		boolean calls = deleteHelper.deleteBlockedCall((long)situationID2);
		boolean location = deleteHelper.deleteLocation((long)situationID2);
		boolean settings = deleteHelper.deleteSettings((long)situationID2);
		boolean event= deleteHelper.deleteEvent((long)situationID2);
		boolean test = deleteHelper.deleteSituations((long)situationID2);
		if(test && settings && calls && location&& event){
			Log.e("SituationActivity", "The situation is being deleted with id:"+situationID2);
		}
		deleteHelper.close();
	}

	private void fillTextViews(int id) {
		TextView  bluetoothValue, ringtoneValue, wifiValue, callBlockingValue;
		double brightness;
		if(id!=0){
			Cursor situationCursor = mDbHelper.fetchSituation(id);
			Cursor settingsCursor = mDbHelper.fetchSettings(id);
			//long rowid = (long)id;
			Cursor callBlockCursor = mDbHelper.fetchAllContactsToBlock();
			Cursor locationCursor = mDbHelper.fetchLocation(id);
			startManagingCursor(situationCursor);

			EditText editTextValue = (EditText) findViewById(R.id.situationNameTextBox);
			editTextValue.setText(situationCursor.getString(situationCursor.getColumnIndex(LocationDbAdapter.KEY_SITUATION)));
			Log.e("situationActivuty","brightness value before");

			if(settingsCursor.moveToFirst()){
				brightness = settingsCursor.getDouble(settingsCursor.getColumnIndex(LocationDbAdapter.COLUMN_BRIGHTNESS));
				if(brightness!=0.0){
					Log.e("situationActivuty","brightness value not found to be zero :"+brightness);
					TextView brightnessValue = (TextView) findViewById(R.id.brightnessvalue);
					brightnessValue.setText(""+brightness+"%");
				}
				else
				{
					Log.e("situationActivuty","brightness value found to be zero :"+brightness);
					TextView brightnessValue = (TextView) findViewById(R.id.brightnessvalue);
					brightnessValue.setText("NOT SET");
				}
				int wifiInt = settingsCursor.getInt(settingsCursor.getColumnIndex(LocationDbAdapter.COLUMN_WIFI));
				if(wifiInt!=999)
				{
					Log.e("situationActivuty","wifi value found to be set :"+wifiInt);
					wifiValue = (TextView) findViewById(R.id.wifivalue);
					if(wifiInt == 0)
						wifiValue.setText("OFF");
					else if(wifiInt == 1)
						wifiValue.setText("ON");

				}
				else
				{
					Log.e("situationActivuty","wifi value found to be NOT SET :"+wifiInt);
					wifiValue = (TextView) findViewById(R.id.wifivalue);
					wifiValue.setText("NOT SET");

				}
				int bluetoothInt = settingsCursor.getInt(settingsCursor.getColumnIndex(LocationDbAdapter.COLUMN_BLUETOOTH));
				if(bluetoothInt!=999)
				{
					Log.e("situationActivuty","bluetooth value found to be set :"+bluetoothInt);
					bluetoothValue = (TextView) findViewById(R.id.bluetoothvalue);
					if(bluetoothInt == 0)
						bluetoothValue.setText("OFF");
					else if(bluetoothInt == 1)
						bluetoothValue.setText("ON");

				}
				else
				{
					Log.e("situationActivuty","bluetooth value found to be set :"+bluetoothInt);
					bluetoothValue = (TextView) findViewById(R.id.bluetoothvalue);
					bluetoothValue.setText("NOT SET");
				}
				String ringtoneName = settingsCursor.getString(settingsCursor.getColumnIndex(LocationDbAdapter.COLUMN_RINGTONEID));
				ringtoneValue = (TextView) findViewById(R.id.ringtonevalue);
				if(!ringtoneName.equals("NULL"))
				{
					Log.e("situationActivuty","ringtone value found to be set :"+settingsCursor.getString(settingsCursor.getColumnIndex(LocationDbAdapter.COLUMN_RINGTONEID)));
					ringtoneValue.setText(""+settingsCursor.getString(settingsCursor.getColumnIndex(LocationDbAdapter.COLUMN_RINGTONEID)));

				}
				else
				{
					Log.e("situationActivuty","ringtone value found to be NOT SET");
					ringtoneValue.setText("NOT SET");
				}
				if(callBlockCursor.moveToFirst())
				{
					Log.e("SituationName", "after move first");
					blacklistTables = new ArrayList<BlacklistTable>();
					{
						BlacklistTable tempBlacklistTable = new BlacklistTable();
						tempBlacklistTable.setNumber(callBlockCursor.getString(callBlockCursor.getColumnIndex(LocationDbAdapter.COLUMN_NUMBER)));
						tempBlacklistTable.setSituationID(situationID);
						Log.e("SituationName", "adding a contact to block with number "+tempBlacklistTable.getNumber());
						blacklistTables.add(tempBlacklistTable);
					}
					while(callBlockCursor.moveToNext())
					{
						Log.e("SituationName", "entering cursor while loop");
						Log.e("SituationName", "blacklist entry with contact :"+callBlockCursor.getString(callBlockCursor.getColumnIndex(LocationDbAdapter.COLUMN_NUMBER)));
						Log.e("SituationName", "blacklist entry with id :"+callBlockCursor.getInt((callBlockCursor.getColumnIndex(LocationDbAdapter.KEY_SITUATIONID))));

						if(situationID==callBlockCursor.getInt(callBlockCursor.getColumnIndex(LocationDbAdapter.KEY_SITUATIONID)))
						{
							BlacklistTable tempBlacklistTable = new BlacklistTable();
							tempBlacklistTable.setNumber(callBlockCursor.getString(callBlockCursor.getColumnIndex(LocationDbAdapter.COLUMN_NUMBER)));
							tempBlacklistTable.setSituationID(situationID);
							Log.e("SituationName", "adding a contact to block with number "+tempBlacklistTable.getNumber());
							blacklistTables.add(tempBlacklistTable);
						}

					}
					callBlockingValue = (TextView) findViewById(R.id.blockcallsvalue);
					callBlockingValue.setText(""+blacklistTables.size()+" selected");
				}
			}
			//Toast.makeText(getBaseContext(), "outside fetching location situation", Toast.LENGTH_LONG).show();
			if(locationCursor.moveToFirst()){
			do {
				mLocationValues.setBody(locationCursor.getString(locationCursor.getColumnIndex(LocationDbAdapter.KEY_BODY)));
				TextView locView = (TextView) findViewById(R.id.locationValue);
				locView.setText(mLocationValues.getBody());
				//Toast.makeText(getBaseContext(), "inside fetching location situation", Toast.LENGTH_LONG).show();
			}while (locationCursor.moveToNext());				
			}
		}

	}
	public void onDialogBrightnessClick(DialogFragment dialog) {
		Intent intent = new Intent(SituationActivity.this,
				BrightnessActivity.class);
		TextView brightnessView = (TextView)findViewById(R.id.brightnessvalue);
		intent.putExtra("brightness", brightnessView.getText());

		if(reqCode==1)
		{
			intent.putExtra("requestCode", reqCode);
			startActivityForResult(intent, BRIGHTNESS_REQUEST_CODE);
		}

		else if(reqCode==2)
		{
			intent.putExtra("requestCode", reqCode);
			startActivityForResult(intent, BRIGHTNESS_EDIT_REQUEST_CODE);
		}

	}

	public void onDialogRingtoneClick(DialogFragment dialog) {
		Intent intent = new Intent(SituationActivity.this,
				RingtoneActivity.class);

		startActivityForResult(intent, RINGTONE_REQUEST_CODE);

	}

	public void onDialogBluetoothClick(DialogFragment dialog) {
		Intent intent = new Intent(SituationActivity.this,
				BluetoothTogglingActivity.class);

		startActivityForResult(intent, BLUETOOTH_REQUEST_CODE);
	}

	public void onDialogWifiClick(DialogFragment dialog) {
		Intent intent = new Intent(SituationActivity.this,
				WifiTogglingActivity.class);

		startActivityForResult(intent, WIFI_REQUEST_CODE);
	}

	public void onDialogCallBlockClick(DialogFragment dialog) {
		Intent intent = new Intent(SituationActivity.this,
				CallBlockingActivity.class);

		startActivityForResult(intent, BLOCKEDCALLS_REQUEST_CODE);
	}

	public String convertPointToLocation(GpsPoint point) {
		String address = "";
		Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
		try {
			List<Address> addresses = geoCoder.getFromLocation(
					point.getLat() / 1E6, point.getLon() / 1E6, 1);

			if (addresses.size() > 0) {
				for (int index = 0; index < addresses.get(0)
						.getMaxAddressLineIndex(); index++)
					address += addresses.get(0).getAddressLine(index) + " ";
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return address;
	}
	// new code to save into database

	private void performAction(Action action) {

		switch( action ){

		case CONFIRM: 

			Thread thread =  new Thread(null, new SaveToDatabase(situationTable,mLocationValues,settingsTable,blacklistTables,event), "Background Thread");

			// start background thread
			thread.start();
			// show progress dialog while the results are being verified and saved
			m_ProgressDialog = ProgressDialog.show(this,    
					getString(R.string.rem_progress_dialog_title), 
					getString(R.string.rem_progress_dialog_body), 
					true);
			break;

		case CANCEL:
			setResult(RESULT_CANCELED);
			finish();
			break;

		case DELETE:    		
			break;
		}
	}
	final class SaveToDatabase implements Runnable{
		private ResultCode result;
		private SituationTable s;
		private LocationValues l;
		private SettingsTable sT;
		private EventDetails e;
		int locationId;
		List<BlacklistTable> b;
		public SaveToDatabase(SituationTable s, LocationValues l, SettingsTable sT,List<BlacklistTable> b,EventDetails e){
			this.s = s;
			this.l = l;
			this.b = b;
			this.sT = sT;
			this.e = e;
			this.result = ResultCode.INVALID;
		}

		@Override
		public void run() {
			Log.e("SituationActivity", "sing inside this");

			if (situationID == 0) { 

				long id = mDbHelper.createSituation(s);
				//long settingsID = mDbHelper.createSettings(settingsTable);
				Log.e("SituationActivity", "Situation created with situation id:"+id);
				if (id > 0) {
					Log.e("SituationActivity", "database saved" +
							"");

					// updated RowId of saved situation so it
					// can be restored
					situationID = (int)id;
					String locId = situationID.toString();
					locationId = Integer.parseInt(locId);

					if(l!=null)
					{
						l.setSituationID(locationId);
						long returnloc = mDbHelper.createLocation(l);
						Log.e("SituationAVtivity","The location is created and the id is:"+returnloc);

					}
					if(e!=null)
					{
						e.setSituationID(situationID);
						long returnEvent= mDbHelper.createEvent(e);
						Log.e("Event Activity","The Event is created and the id is:"+returnEvent);
					}
					if(settingsTable!=null)
					{
						sT.setSituationID(locationId);
						long returnSeting = mDbHelper.createSettings(sT);
						Log.e("SituationAVtivity","The settings is created and the id is:"+returnSeting);
						Log.e("SituationAVtivity","The brightness is created and the id is:"+sT.getBrightnessValue());
					}
					if(b != null)
					{
						for(int blackListTableIterator = 0; blackListTableIterator<b.size();blackListTableIterator++)
						{
							b.get(blackListTableIterator).setSituationID(situationID);
							long returnBlockCalls = mDbHelper.createBlackList(b.get(blackListTableIterator));
							Log.e("SituationAVtivity","The blacklist table is created with "+ b.get(blackListTableIterator).getNumber()+"and the id is:"+returnBlockCalls);

						}

					}

					result = ResultCode.SAVED;

				}else{
					result = ResultCode.SAVE_ERROR;
				}
			} else { // update existing situation
				if(mDbHelper.updateSituations((long)situationID, s)){

					if(l!=null)
					{
						l.setSituationID(locationId);
						boolean locationBool = mDbHelper.updateLocation((long)situationID, l);
						if(locationBool)
							Log.e("Situation Activity","The location is updated and the id is:"+situationID);
						else
							Log.e("Situation Activity","The location is not updated");

					}
					if(e!=null)
					{
						e.setSituationID(situationID);
						boolean eventBool= mDbHelper.updateEvent((long) situationID, e);
						if(eventBool)
							Log.e("Event Activity","The event is updated and the id is:"+situationID);
						else
							Log.e("Event Activity","The event is not updated");
					}
					if(settingsTable!=null)
					{
						sT.setSituationID(situationID);
						boolean settingsBool = mDbHelper.updateSettings((long)situationID, sT);
						if(settingsBool)
							Log.e("SituationAVtivity","The Setting is updated and the id is:"+situationID);
						else
							Log.e("SituationAVtivity","The Setting is not updated and the id is:"+situationID);

					}
					if(b != null)
					{
						for(int blackListTableIterator = 0; blackListTableIterator<b.size();blackListTableIterator++)
						{
							b.get(blackListTableIterator).setSituationID(situationID);
							boolean blacklistBool = mDbHelper.updateBlackList((long)situationID, b.get(blackListTableIterator));
							if(blacklistBool)
								Log.e("SituationActivity","The blacklist wid number is updated");
							else
								Log.e("SituationActivity","The blacklist wid number is not updated");						
						}
					}


					result = ResultCode.SAVED;
				}else{
					result = ResultCode.SAVE_ERROR;
				}
			}		  	
			runOnUiThread(new ShowStatus(result));

		}

	}

	final class ShowStatus implements Runnable{

		private ResultCode result;

		public ShowStatus(ResultCode s){
			this.result = s;
		}

		public AlertDialog createFailureAlert(){

			AlertDialog.Builder builder = new AlertDialog.Builder(SituationActivity.this);

			String message = "";

			switch(result){
			case INVALID:
				message = getString(R.string.invalid_address);
				break;
			case SAVE_ERROR:
				message = getString(R.string.save_error);
				break;
			}   		

			// here methods can be chained since each method returns the AlertBuilder
			// object
			builder.setMessage(
					message)
					.setCancelable(false)
					.setPositiveButton("Close", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel(); 
						}
					});    		       

			return builder.create();
		}

		@Override
		public void run() {
			// dismiss progress dialog
			m_ProgressDialog.dismiss();

			//       	Log.e(TAG, "result: " + result.name() );

			if(result == ResultCode.INVALID || result == ResultCode.SAVE_ERROR){ // failed
				AlertDialog failAlert = createFailureAlert();
				failAlert.show();
			}else{
				// succeeded 
				SituationActivity.this.setResult(RESULT_OK);
				SituationActivity.this.finish();
			}


		};
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.location_options_menu, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {

		case R.id.start_loc_svc:
			startLocationService();
			return true;

		case R.id.stop_loc_svc:
			stopLocationService();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}
	private void startLocationService(){
		// send intent to start location service
		Intent start_loc_svc = new Intent(this, LocationService.class);
		//Toast.makeText(getBaseContext(), "starting service", Toast.LENGTH_LONG).show();
		this.startService(start_loc_svc);
	}

	private void stopLocationService(){
		// send intent to stop location service
		Intent stop_loc_svc = new Intent(this, LocationService.class);     	
		this.stopService(stop_loc_svc);
	}

	@Override
	public void onDialogLocationClick(DialogFragment dialog) {
		if(reqCode == 1){
			Intent i = new Intent(SituationActivity.this,
					LocationEdit.class);
			i.putExtra(LocationDbAdapter.KEY_SITUATIONID, 0);
			startActivityForResult(i, LOCATION_REQUEST_CODE);	
		}
		else if(reqCode == 2){
			Intent i = new Intent(SituationActivity.this,
					LocationEdit.class);

			i.putExtra(LocationDbAdapter.KEY_SITUATIONID, situationID);
			i.putExtra("reqCode", reqCode);
			startActivityForResult(i, LOCATION_REQUEST_CODE);
		}

	}



	@Override
	public void onDialogEventClick(DialogFragment dialog) {
		if(reqCode == 1){
			Intent i = new Intent(SituationActivity.this,
					EventActivity.class);
			i.putExtra("REQ", 1);
			i.putExtra(LocationDbAdapter.KEY_SITUATIONID, 0);
			startActivityForResult(i, EVENT_REQUEST_CODE);	
		}
		if(reqCode == 2)
		{
			Intent i = new Intent(SituationActivity.this,
					EventActivity.class);
			i.putExtra("REQ", 2);
			i.putExtra(LocationDbAdapter.KEY_SITUATIONID, situationID);
			startActivityForResult(i, EVENT_REQUEST_CODE);
		}
	}


}
