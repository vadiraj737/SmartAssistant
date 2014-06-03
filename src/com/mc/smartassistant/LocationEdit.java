package com.mc.smartassistant;




import com.app.lib.GeoCodec;
import com.app.lib.GpsPoint;
import com.app.lib.LocationValues;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class LocationEdit extends Activity {
	// flag for GPS status
	boolean isGPSEnabled = false;

	// flag for network status
	boolean isNetworkEnabled = false;

	// flag for GPS status
	boolean canGetLocation = false;
	private Integer mRowId;
	private EditText mAddrText;
	private EditText mBodyText;
	private CheckBox mEntry;
	private CheckBox mExit;
	private LocationValues mLocationValues;
	private EditText mRangeText;
	private LocationDbAdapter mDbHelper;
	GeoCodec geoCodec;

	private enum Action {CONFIRM, CANCEL, DELETE};

	/**
	 * Activity intent request code to identify sent/received intents
	 * 
	 */
	private static enum Message{

		SELECT_BOOKMARK,
		SELECT_MAP
	}

	@SuppressWarnings("unused")
	private String mLastAddr;

	public static enum ResultCode{
		SAVED, INVALID_ADDR, SAVE_ERROR
	}

	public static enum AddrSource{
		BOOKMARKS, // address is obtained from bookmarked locations
		CURRENT,  // "use current" location button is used 
		MANUAL,
		MAP// address entered manually by use
	}
	// represents the source where the address is obtained from
	private AddrSource mAddrSource;

	private static final String TAG = "LocationEdit";
	private LocationManager locManager;
	private LocationListener locListener;
	private Location myLocation;
	// min distance interval (in meters) to update GPS location coordinates
	public static final float DIST_INTERVAL = 10;
	// min time (in milliseconds) to update GPS location coordinates
	public static final long TIME_INTERVAL = 10000;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location_edit);
		mBodyText = (EditText) findViewById(R.id.body);
		mAddrText = (EditText) findViewById(R.id.addr);

		mEntry = (CheckBox) findViewById(R.id.on_entry);
		mExit = (CheckBox) findViewById(R.id.on_exit);
		setLocationEvent(LocationValues.DEFAULT_EVENT.ordinal());

		mDbHelper = new LocationDbAdapter(this);
		mDbHelper.open();

		mRangeText = (EditText) findViewById(R.id.range);
		// default range is 1 m
		mRangeText.setText(Double.toString(LocationValues.DEFAULT_RANGE)); 

		mLocationValues = new LocationValues();

		initLocationService(); // setup location service
		// initial value of myLocation
		//myLocation = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		getLocation();
		//Toast.makeText(getBaseContext(), "location is:"+myLocation.getLatitude()+", "+myLocation.getLongitude(), Toast.LENGTH_LONG).show();
		mAddrSource = AddrSource.MANUAL; // default is manual addr entry

		geoCodec = new GeoCodec(this);

		mLastAddr = new String();

		mRowId = (savedInstanceState == null) ? null :
			(Integer) savedInstanceState.getSerializable(LocationDbAdapter.KEY_SITUATIONID);
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras.getInt(LocationDbAdapter.KEY_SITUATIONID)
					: null;
		}
		Log.e(TAG, "Rowid = " + mRowId);
		populateFields();
		setupButtons();
	}
	//newly inserted code
	// Verifies (by geocoding) and saves user entered addresses by
	// performing database Create/update operation 
	// User prompted to re-enter address if invalid in a pop up dialog




	@Override
	protected void onPause() {
		super.onPause();
		Log.i(TAG, "ON PAUSE CALLED");
		//saveState();
		mDbHelper.close(); 

		// stop listening for loc updates
		locManager.removeUpdates(locListener);
	}


	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "ON RESUME CALLED");
		if(mDbHelper == null){
			mDbHelper.open();
		}
		//populateFields();
		locManager.requestLocationUpdates(
				LocationManager.GPS_PROVIDER, 
				TIME_INTERVAL, // min time in ms between location updates
				DIST_INTERVAL, // min distance in meters 
				locListener);
	}

	@Override
	protected void onDestroy(){

		super.onDestroy();
		Log.i(TAG, "ON DESTROY CALLED");
		if(mDbHelper != null){
			mDbHelper.close();
		}
	}

	//end of the verify and show status

	// populates the fields in the location edit acitivity
	// with what's in the database when an existing location is opened. 
	// Does nothing if a new location is being created
	private void populateFields() {
		Log.e(TAG, "Inside populate fields");
		if (mRowId != null) {
			Log.e(TAG, "Inside populate fields with row id:"+mRowId);

			Cursor location = mDbHelper.fetchLocation((long)mRowId);
			startManagingCursor(location);
			if(location.moveToFirst()){

				Log.e(TAG, "Inside location First:");
				mBodyText.setText(location.getString(
						location.getColumnIndexOrThrow(LocationDbAdapter.KEY_BODY)));

				// update address field view from database only if 
				// user does not choose address from bookmarks
				if(mAddrSource != AddrSource.BOOKMARKS || mAddrSource != AddrSource.MAP){

					mAddrText.setText(location.getString(
							location.getColumnIndexOrThrow(LocationDbAdapter.KEY_ADDR)));
				}

				// set the range
				double range = location.getDouble(
						location.getColumnIndexOrThrow(LocationDbAdapter.KEY_RADIUS));
				mRangeText.setText(Double.toString(range));

				// set location event checkbox
				setLocationEvent(location.getInt(
						location.getColumnIndexOrThrow(LocationDbAdapter.KEY_EVENT)));

			}
		}

		// save the initial value of address field
		mLastAddr = mAddrText.getText().toString().trim();
	}


	private void setupButtons(){

		Button useCurrentButton = (Button) findViewById(R.id.use_current);
		Button fromBookmarksButton = (Button) findViewById(R.id.select_from_bookmarks);
		Button confirmButton = (Button) findViewById(R.id.confirm_rem);
		Button cancelButton = (Button) findViewById(R.id.cancel_rem);
		Button deleteButton = (Button) findViewById(R.id.delete_rem);
		Button map = (Button) findViewById(R.id.map);

		// Listener for "Use Current" button
		useCurrentButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				mAddrText.setText(getString(R.string.my_current_addr));
				mAddrSource = AddrSource.CURRENT;
			}

		});

		// Listener for "From Bookmarks" button
		fromBookmarksButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				// results in populating the addr field and gps coordinate fields 
				// of the location object
				selectAddressFromBookmarks(); 
				mAddrSource = AddrSource.BOOKMARKS;
			}

		});        

		// Listener for confirm button
		confirmButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				performAction(Action.CONFIRM);
			}

		});


		// Listener for cancel button
		cancelButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				performAction(Action.CANCEL);
			}

		});

		// Listener for delete button
		deleteButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				performAction(Action.CANCEL);

				if( mRowId != null){
					mDbHelper.deleteLocation((long)mRowId);
				}
				setResult(RESULT_OK);
				finish();
			}

		});

		map.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {

				selectAddressFromMap();
				mAddrSource = AddrSource.MAP;
			}

		});

	}


	private void performAction(Action action) {

		switch( action ){

		case CONFIRM: 
			mLocationValues.setBody(mBodyText.getText().toString());
			mLocationValues.setRange(Double.parseDouble(mRangeText.getText().toString()));
			// if both are unchecked then it will be treated as on entry by default
			mLocationValues.setEvent(getLocationEvent(mEntry, mExit));
			// If mannual addr mode then use the addr that is in the text box
			if(mAddrSource == AddrSource.MANUAL){
				mLocationValues.setAddr(mAddrText.getText().toString());
			}
			// if "current addr" mode is selected, use the lat/lon of current loc
			// at the time the "Save" button is clicked. Translation of gps into address
			// is done in the verifyAndSave thread
			if(mAddrSource == AddrSource.CURRENT){
				// set the lat/lon for the location 
					mLocationValues.setLat(myLocation.getLatitude());
					mLocationValues.setLon(myLocation.getLongitude());
					//Toast.makeText(getBaseContext(), "The location is:"+myLocation.getLatitude()+","+myLocation.getLongitude(), Toast.LENGTH_LONG).show();
					GpsPoint p = new GpsPoint(mLocationValues.getLat(), mLocationValues.getLon());
					Log.e(TAG, "Current GPS point: " + p.getPointAsString());
					String address = geoCodec.getAddressFromGpsPoint(p);

					// if the gps points could not be correctly translated into
					// an address, use the gps coordinates for the address field.
					if(address.length() == 0){
						address = p.getPointAsString();
					}

					Log.e(TAG, "Current Address: " + address);
					// update the address attribute of location
					mLocationValues.setAddr(address);
					// set the addr field to my current loc and
				
			}
			/*    			
			 */
			// create thread to verify and save results
			//Thread thread =  new Thread(null, new VerifyAndSave(mLocationValues), "Background Thread");
			// start background thread
			//thread.start();
			// show progress dialog while the results are being verified and saved
			/* m_ProgressDialog = ProgressDialog.show(this,    
    	              getString(R.string.rem_progress_dialog_title), 
    	              getString(R.string.rem_progress_dialog_body), 
    	              true);

			 */
			//	Toast.makeText(getBaseContext(), mLocationValues.getAddr(), Toast.LENGTH_LONG).show();
			Intent i = new Intent(LocationEdit.this, SituationActivity.class);
			String s =  mLocationValues.getAddr();
			i.putExtra("address",s);
			i.putExtra("lat", mLocationValues.getLat());
			i.putExtra("lon", mLocationValues.getLon());
			i.putExtra("event", mLocationValues.getEvent());
			i.putExtra("body", mLocationValues.getBody());
			i.putExtra("range", mLocationValues.getRange());
			LocationEdit.this.setResult(RESULT_OK, i);
			LocationEdit.this.finish();
			break;

		case CANCEL:
			setResult(RESULT_CANCELED);
			finish();
			break;

		case DELETE:    		
			break;
		}

	}

	private void initLocationService(){

		locManager = (LocationManager) 
				getSystemService(LOCATION_SERVICE);

		locListener = new LocationListener()
		{
			@Override
			public void onLocationChanged(Location myLoc) {
				myLocation = myLoc;
			}
			// the following abstract methods must be implemented
			@Override
			public void onProviderDisabled(String provider) {

			}
			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
			}
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				// TODO Auto-generated method stub
			}
		};

	}

	private void setLocationEvent(int event) {

		//Log.e(TAG, "getLocationEvent event is " + event );

		LocationValues.Event event_type = LocationValues.Event.values()[event];

		switch(event_type){

		case ON_ENTRY:
			mEntry.setChecked(true);
			mExit.setChecked(false);
			//Log.e(TAG, "setting on entry");
			break;
		case ON_EXIT:
			mEntry.setChecked(false);
			mExit.setChecked(true);
			//Log.e(TAG, "setting on exit");
			break;
		case ON_ENTRY_EXIT:
			mEntry.setChecked(true);
			mExit.setChecked(true);
			//Log.e(TAG, "setting on entry and exit");
			break;
		default:
			// NOTE: if user unchecks both entry and exit, it will be interpreted as 
			// on entry
			mEntry.setChecked(true);
			mExit.setChecked(false);
			//Log.e(TAG, "setting default");
			break;
		}

	}

	private void selectAddressFromBookmarks(){
		Intent i = new Intent(this, BookmarkList.class);
		// put something inside the extras bundle (doesn't matter what) so the BookmarkList
		// class can distinguish between intents received from this activity
		// and others and take appropriate action.
		i.putExtra(Message.SELECT_BOOKMARK.toString(), Message.SELECT_BOOKMARK.ordinal());
		startActivityForResult(i, Message.SELECT_BOOKMARK.ordinal());   
	}
	private void selectAddressFromMap(){

		Intent i = new Intent(this, Map.class);
		// put something inside the extras bundle (doesn't matter what) so the Map
		// class can distinguish between intents received from this activity
		// and others and take appropriate action.
		i.putExtra(Message.SELECT_MAP.toString(), Message.SELECT_MAP.ordinal());
		startActivityForResult(i, Message.SELECT_MAP.ordinal());   
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		// this method is called just before onResume. Database handle needs
		// to be opened here since it was closed by onPause.
		/*if(mDbHelper == null){
    		mDbHelper.open();
    	}*/

		// is soemthing bad happened display message to user and switch to manual address mode
		// if result code is not RESULT_OK
		// the selection did not work, default to manual address entry
		if(resultCode != RESULT_OK){
			mAddrSource = AddrSource.MANUAL;
			return;
		}
		// extract the result of selecting a bookmarked location here
		Message message_code = Message.values()[requestCode];
		switch(message_code){
		case SELECT_BOOKMARK:
			// extract address and gps point from bookmark selection
			Bundle extras = intent.getExtras();
			// populate location object fields with the info obtained
			mLocationValues.setAddr(extras.getString(BookmarkDbAdapter.KEY_ADDR));
			mLocationValues.setLat(extras.getDouble(BookmarkDbAdapter.KEY_LAT));
			mLocationValues.setLon(extras.getDouble(BookmarkDbAdapter.KEY_LON));    			
			// update the view with the new address obtained
			Log.e("TAG", "Address from the text box of bookmark "+mLocationValues.getAddr());

			mAddrText.setText(""+mLocationValues.getAddr());

			break;
		case SELECT_MAP:
			Bundle extras1 = intent.getExtras();
			double lat=extras1.getDouble("lat");
			double lon=extras1.getDouble("lon");
			// populate location object fields with the info obtained
			GpsPoint p = new GpsPoint(lat, lon);
			Log.e(TAG, "Current GPS point from the map: " + p.getPointAsString());
			String address = geoCodec.getAddressFromGpsPoint(p);

			// if the gps points could not be correctly translated into
			// an address, use the gps coordinates for the address field.
			if(address.length() == 0){
				address = p.getPointAsString();
			}

			Log.e(TAG, "Current Address from the map: " + address);
			// update the address attribute of location

			mLocationValues.setAddr(address);
			mLocationValues.setLat(lat);
			mLocationValues.setLon(lon);    			
			// update the view with the new address obtained
			Log.e("TAG", "Address from the text box "+mLocationValues.getAddr());
			mAddrText.setText(""+mLocationValues.getAddr());

		}
	}
	private int getLocationEvent(CheckBox entry, CheckBox exit) {

		LocationValues.Event event = LocationValues.Event.ON_ENTRY;

		if(entry.isChecked() && exit.isChecked()){
			event = LocationValues.Event.ON_ENTRY_EXIT;
		}
		if(entry.isChecked() && !exit.isChecked()){
			event = LocationValues.Event.ON_ENTRY;
		}
		if(!entry.isChecked() && exit.isChecked()){
			event = LocationValues.Event.ON_EXIT;
		}


		return event.ordinal(); // ordinal returns integer position of enum constant
	}
	public void getLocation() {
		try {
			Log.e(TAG, "inside get location");
			// getting GPS status
			isGPSEnabled = locManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);
			// getting network status
			isNetworkEnabled = locManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled && !isNetworkEnabled) {
				// no network provider is enabled

				Log.e(TAG, "No location is enabled");
			} else {
				this.canGetLocation = true;
				// First get location from Network Provider
				if (isNetworkEnabled) {

					Log.e(TAG, "Network is enabled");
					locManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER,
							TIME_INTERVAL,
							DIST_INTERVAL, locListener);
					if (locManager != null) {
						myLocation = locManager
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
					}
				}
				// if GPS Enabled get lat/long using GPS Services
				if (isGPSEnabled) {

					Log.e(TAG, "GPS is enabled");
					if (myLocation == null) {

						locManager.requestLocationUpdates(
								LocationManager.GPS_PROVIDER,
								TIME_INTERVAL,
								DIST_INTERVAL, locListener);
						if (locManager != null) {
							myLocation = locManager
									.getLastKnownLocation(LocationManager.GPS_PROVIDER);
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	public boolean canGetLocation() {
		return this.canGetLocation;
	}

	public void showSettingsAlert() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

		// Setting Dialog Title
		alertDialog.setTitle("GPS is settings");

		// Setting Dialog Message
		alertDialog
				.setMessage("GPS is not enabled. Do you want to go to settings menu?");

		// On pressing Settings button
		alertDialog.setPositiveButton("Settings",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(
								Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						startActivity(intent);
					}
				});

		// on pressing cancel button
		alertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		// Showing Alert Message
		alertDialog.show();
	}
}
