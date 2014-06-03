package com.mc.smartassistant;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.app.lib.GeoDist;
import com.app.lib.GpsPoint;
import com.app.lib.LocationValues;
import com.mc.dbtables.BlacklistTable;
import com.mc.dbtables.SettingsTable;

public class LocationService extends Service {

	BroadcastReceiver CallBlocker;
	TelephonyManager telephonyManager;
	ITelephony telephonyService;
	private ArrayList<BlacklistTable> blacklist;
	boolean startStatus=false;
	boolean endStatus=false;
	PowerManager.WakeLock wl;
	private SettingsTable settings;
	// flag for GPS status
	public static boolean isGPSEnabled = false;

	// flag for network status
	public static boolean isNetworkEnabled = false;

	/**
	 * min distance interval (in meters) to update GPS location coordinates
	 */
	public static final float DIST_INTERVAL = 10;
	/**
	 * 
	 * min time (in milliseconds) to update GPS location coordinates
	 */
	public static final long TIME_INTERVAL = 10000;
	/**
	 * Identifier used to uniquely identify location notifications
	 */

	public static boolean isRunning;

	private static final String TAG = "LocationService";

	private LocationManager lm;

	private int situationID;

	private LocationListener locationListener;

	private LocationDbAdapter mDbHelper;

	private GeoDist mGeoDist;

	private Location mLastLoc;

	private final IBinder mBinder = new LocationBinder();

	public class LocationBinder extends Binder {
		LocationService getService() {
			return LocationService.this;
		}
	}

	/*public void setBrightness(int value) {
		Log.e(TAG, "inside brightness value");
		float brightness;
		brightness = value / (float) 255;
		Intent intent = new Intent(getBaseContext(),
				DummyBrightnessActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this is important
		// in the next line 'brightness' should be a float number between 0.0
		// and 1.0
		intent.putExtra("brightness value", brightness);
		getApplication().startActivity(intent);
		Log.e(TAG, "outside brightness value");

	}
	 */
	public void setBrightness(double value) {
		Log.e("Brightness Value", "inside brightness value");
		float brightness;
		brightness = (float)value/(float)255;
		Intent intent = new Intent(getBaseContext(), DummyBrightnessActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //this is important
		//in the next line 'brightness' should be a float number between 0.0 and 1.0
		intent.putExtra("brightness value", brightness);
		intent.putExtra("reqCode", 999);
		getApplication().startActivity(intent);
		Log.e("Brightness Value", "outside brightness value");

	}
	/*public void setBluetooth(int bluetooth) {
		bluetooth = 1;
		BluetoothAdapter myBluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();
		if (bluetooth == 1) {
			if (!myBluetoothAdapter.isEnabled()) {
				// Intent enableBtIntent = new
				// Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				myBluetoothAdapter.enable();

				// startActivityForResult(enableBtIntent, 1);
			}
			try {
				Toast.makeText(getBaseContext(),
						"Turning ON bluetooth, please wait....",
						Toast.LENGTH_LONG).show();
				Thread.sleep(1000);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		}
	}
	 */
	public void setBluetooth(int bluetooth) {
		Log.e("Corresponding Situation is","bluetooth");

		BluetoothAdapter myBluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();
		if (bluetooth == 1) {
			if (!myBluetoothAdapter.isEnabled()) {
				myBluetoothAdapter.enable();
			}
			try {
				//Toast.makeText(getBaseContext(),
					//	"Turning ON bluetooth, please wait....",
						//Toast.LENGTH_LONG).show();
				Thread.sleep(1000);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		}
		else if(bluetooth==0)
		{
			if (myBluetoothAdapter.isEnabled()) {
				myBluetoothAdapter.disable();
			}
			try {
				//Toast.makeText(getBaseContext(),
					//	"Turning OFF bluetooth, please wait....",
						//Toast.LENGTH_LONG).show();
				Thread.sleep(1000);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		}

	}


	@Override
	public IBinder onBind(Intent arg0) {

		return mBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		 PowerManager pm = (PowerManager) getSystemService 
				  (Context.POWER_SERVICE); 
				  wl = pm.newWakeLock 
				  (PowerManager.FULL_WAKE_LOCK, "My Tag"); 
				                 wl.acquire(); 
	//	Toast.makeText(this, "Service created ...", Toast.LENGTH_LONG).show();
		mDbHelper = new LocationDbAdapter(this);
		mDbHelper.open();
		mGeoDist = new GeoDist();
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// getting GPS status
		isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		// getting network status
		isNetworkEnabled = lm
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		locationListener = new MyLocationListener();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	//	Toast.makeText(this, "Service destroyed ...", Toast.LENGTH_LONG).show();

		lm.removeUpdates(locationListener);

		// mlocationCursor.close();
		mDbHelper.close();
		wl.release();
		isRunning = false;
	}

	// called when location service is started
	@Override
	public void onStart(Intent intent, int startId) {
		handleCommand(intent);
		Log.i(TAG, "Received start id " + startId + ": " + intent);

	}

	void handleCommand(Intent intent) {
		if (!isGPSEnabled && !isNetworkEnabled) {
			// no network provider is enabled

		} else {
			if (isNetworkEnabled) {
				lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
						TIME_INTERVAL, DIST_INTERVAL, locationListener);
			} else if (isGPSEnabled) {
				lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
						TIME_INTERVAL, // time between location updates
						DIST_INTERVAL, // min distance between location updates
						locationListener);

			}

		}
		isRunning = true;
	}

	private class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location myLoc) {
			//Toast.makeText(getBaseContext(), "The location is:"+myLoc.getLatitude()+""+myLoc.getLongitude(), Toast.LENGTH_LONG).show();
			// DEBUG CODE START
			if (mLastLoc != null) {
				double radius = 1.0;
				GpsPoint last_point = new GpsPoint(mLastLoc.getLatitude(),
						mLastLoc.getLongitude());
				GpsPoint curr_point = new GpsPoint(myLoc.getLatitude(),
						myLoc.getLongitude());
				if (!mGeoDist.inRange(last_point, curr_point, radius)) {
					Log.e(TAG, "Moved more than " + radius + " meters");
				}
			}

			Cursor locationCursor = mDbHelper.fetchAllLocations();

			if (locationCursor == null) {
				mLastLoc = myLoc;
				// no matching locations found, take no action
				return;
			}

			Log.e(TAG, "Check point 1");
			if (locationCursor.moveToFirst()) {
				Log.e(TAG, "Inside the");

				do{
					// check if trigger conditions are met
					Log.e(TAG,
							"Inside the test with location coordinates from DDMS:"
									+ myLoc.getLatitude() + " "
									+ myLoc.getLongitude());
				//	Toast.makeText(getBaseContext(), "Inside the test with location coordinates from DDMS:"
					//		+ myLoc.getLatitude() + " "
						//	+ myLoc.getLongitude(), Toast.LENGTH_LONG).show();
					Log.e(TAG,
							"Inside the test with location coordinates"
									+ locationCursor.getDouble(locationCursor
											.getColumnIndex(LocationDbAdapter.KEY_LAT))
											+ " "
											+ locationCursor.getDouble(locationCursor
													.getColumnIndex(LocationDbAdapter.KEY_LNG)));

					situationID = locationCursor.getInt(locationCursor
							.getColumnIndex(LocationDbAdapter.KEY_SITUATIONID));
					if (checkTriggerConditions(myLoc, locationCursor)) {
					//	Toast.makeText(getBaseContext(), "Inside the range of the location", Toast.LENGTH_LONG).show();
						Log.e(TAG, "Inside the range of the location");
						// setBluetooth(1);
						blacklist= new ArrayList<BlacklistTable>();
						Cursor cur3= mDbHelper.fetchBlackList(situationID);
						if(cur3.moveToFirst())
						{
							Log.e("Corresponding Situation is","blacklist retrieved");
							BlacklistTable bl= new BlacklistTable();
							bl.setNumber(cur3.getString(cur3.getColumnIndex(LocationDbAdapter.COLUMN_NUMBER)));
							bl.setSituationID(situationID);

							blacklist.add(bl);
							while(cur3.moveToNext())
							{
								Log.e("Corresponding Situation is","blacklist retrieved");
								BlacklistTable bl2= new BlacklistTable();
								bl2.setNumber(cur3.getString(cur3.getColumnIndex(LocationDbAdapter.COLUMN_NUMBER)));
								bl2.setSituationID(situationID);
								blacklist.add(bl2);
							}
						}
						cur3.close();
						settings= new SettingsTable();
						Cursor cur2= mDbHelper.fetchSettings(situationID);
						if(cur2.moveToFirst())
						{
							Log.e("Corresponding Situation is","settings retrieved");
							
							settings.setBluetoothStatus(cur2.getInt(cur2.getColumnIndex(LocationDbAdapter.COLUMN_BLUETOOTH)));
							settings.setWifiStatus(cur2.getInt(cur2.getColumnIndex(LocationDbAdapter.COLUMN_WIFI)));
							settings.setBrightnessValue(cur2.getDouble(cur2.getColumnIndex(LocationDbAdapter.COLUMN_BRIGHTNESS)));
							settings.setRingtoneID(cur2.getString(cur2.getColumnIndex(LocationDbAdapter.COLUMN_RINGTONEID)));
						}
						 
						cur2.close();
						
						if(settings==null)
						{
							Log.e("Location Service", "No settings to change");
						}
						startCallBlocking();
						setBluetooth(settings.getBluetoothStatus());
						setWifi(settings.getWifiStatus());
						setBrightness(settings.getBrightnessValue());
						setRingtone();
						setBrightness(200);
						Log.e(TAG, "Check point 2");
					}
				}while (locationCursor.moveToNext()) ;
			}

			locationCursor.close();

			// update my last loc.
			mLastLoc = myLoc;
		}


		public void setRingtone()
		{
			Log.e("Ringtone", "inside setRingtone");
			String uriString = settings.getRingtoneID();
			Intent intent = new Intent(getBaseContext(), DummyRingtoneActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //this is important
			//in the next line 'brightness' should be a float number between 0.0 and 1.0
			intent.putExtra("title", uriString); 
			getApplication().startActivity(intent);
			Log.e("Ringtone Value", "outside ringtone value");

		}

		public void setWifi(int wifi)
		{
			WifiManager wifiManager = (WifiManager)getBaseContext().getSystemService(Context.WIFI_SERVICE);

			if(wifi==1)
			{
				if(!wifiManager.isWifiEnabled())
				{
					wifiManager.setWifiEnabled(true);
				//	Toast.makeText(getBaseContext(), "Wifi Turned On", Toast.LENGTH_LONG).show();
				}
			}
			else if(wifi==0)
			{
				if(wifiManager.isWifiEnabled())
				{	
					wifiManager.setWifiEnabled(false);
					//Toast.makeText(getBaseContext(), "Wifi Turned Off", Toast.LENGTH_LONG).show();
				}
			}
		}
		/*private void startCallBlocking() {
			Log.e("Corresponding Situation is", "blocking call");

			// starting listener to call recieving
			CallBlocker = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {

					Toast.makeText(getBaseContext(), "onrecieve",
							Toast.LENGTH_LONG).show();

					telephonyManager = (TelephonyManager) context
							.getSystemService(Context.TELEPHONY_SERVICE);
					// Java Reflections
					@SuppressWarnings("rawtypes")
					Class c = null;
					try {
						c = Class
								.forName(telephonyManager.getClass().getName());
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					Method m = null;
					try {
						m = c.getDeclaredMethod("getITelephony");
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					}
					m.setAccessible(true);
					try {
						telephonyService = (ITelephony) m
								.invoke(telephonyManager);
					} catch (IllegalArgumentException e) {

						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
					telephonyManager.listen(callBlockListener,
							PhoneStateListener.LISTEN_CALL_STATE);
				}// onReceive()

				PhoneStateListener callBlockListener = new PhoneStateListener() {
					public void onCallStateChanged(int state,
							String incomingNumber) {
						if (state == TelephonyManager.CALL_STATE_RINGING) {
							Toast.makeText(getBaseContext(),
									"number:" + incomingNumber,
									Toast.LENGTH_LONG).show();
							if (contactData != null) {
								Log.e("Alarm Service",
										"ContactData not null-----------------------------");
								try {
									boolean isBlocked = false;
									for (int i = 0; i < contactData.size(); i++) {
										Log.e("Alarm Service", "numbers: "
												+ contactData.get(i)
														.getPhoneNumber());

										if (incomingNumber.contains(""
												+ contactData.get(i).getPhoneNumber())|| incomingNumber.contains("1"+ contactData.get(i).getPhoneNumber())||incomingNumber.contains("11"+ contactData.get(i).getPhoneNumber())) {
											Toast.makeText(
													getBaseContext(),
													"call blocked from: "
															+ contactData
																	.get(i)
																	.getPhoneNumber(),
													Toast.LENGTH_LONG).show();
											isBlocked = true;
										}

									}
									if (isBlocked)
										telephonyService.endCall();
								} catch (RemoteException e) {

									e.printStackTrace();
								}
							}
						}
					}
				};
			};// BroadcastReceiver
			IntentFilter filter = new IntentFilter(
					"android.intent.action.PHONE_STATE");
			registerReceiver(CallBlocker, filter);
		}
		 */
		private void startCallBlocking() {
			Log.e("Corresponding Situation is","blocking call");
			if(endStatus)
			{			
				unregisterReceiver(CallBlocker);
				CallBlocker = null;
			}

			//starting listener to call recieving
			CallBlocker = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {

					//Toast.makeText(getBaseContext(), "onrecieve",
						//	Toast.LENGTH_LONG).show();

					telephonyManager = (TelephonyManager) context
							.getSystemService(Context.TELEPHONY_SERVICE);
					// Java Reflections
					@SuppressWarnings("rawtypes")
					Class c = null;
					try {
						c = Class.forName(telephonyManager.getClass()
								.getName());
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					Method m = null;
					try {
						m = c.getDeclaredMethod("getITelephony");
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					}
					m.setAccessible(true);
					try {
						telephonyService = (ITelephony) m
								.invoke(telephonyManager);
					} catch (IllegalArgumentException e) {

						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
					telephonyManager.listen(callBlockListener,
							PhoneStateListener.LISTEN_CALL_STATE);
				}// onReceive()

				PhoneStateListener callBlockListener = new PhoneStateListener() {
					public void onCallStateChanged(int state,
							String incomingNumber) {
						if (state == TelephonyManager.CALL_STATE_RINGING) {
						//	Toast.makeText(getBaseContext(), "number:"+incomingNumber,
							//		Toast.LENGTH_LONG).show();
							if (blacklist!=null) {
								Log.e("Alarm Service", "ContactData not null-----------------------------");
								try {
									boolean isBlocked = false;
									for (int i = 0; i < blacklist.size(); i++) {
										Log.e("Alarm Service", "numbers: "+blacklist.get(i).getNumber());

										String splitNumber[] = blacklist.get(i).getNumber().split("-");
										StringBuffer sbf = new StringBuffer();
										for(int j=0;j<splitNumber.length;j++)
										{
											sbf.append(splitNumber[j]);
										}
										String processedNumber = sbf.toString();
										if(incomingNumber.contains(processedNumber)||incomingNumber.contains("1"+processedNumber))
										{
										//	Toast.makeText(getBaseContext(), "call blocked from: "+blacklist.get(i).getNumber(),
											//		Toast.LENGTH_LONG).show();
											isBlocked = true;
										}

									}
									if(isBlocked)
										telephonyService.endCall();
								} catch (RemoteException e) {

									e.printStackTrace();
								}
							}
						}
					}
				};
			};// BroadcastReceiver
			IntentFilter filter = new IntentFilter(
					"android.intent.action.PHONE_STATE");
			registerReceiver(CallBlocker, filter);

			if(endStatus)
			{			
				unregisterReceiver(CallBlocker);
				CallBlocker = null;
			}
		}

		@Override
		public void onProviderDisabled(String provider) {

		}

		@Override
		public void onProviderEnabled(String provider) {

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

	}

	private boolean checkTriggerConditions(Location myLoc, Cursor locationCursor) {

		// initially, last loc is same as my current location
		if (mLastLoc == null) {
			mLastLoc = myLoc;
			/*
			 * DEBUG JFK coordinates mLastLoc = new
			 * Location(LocationManager.GPS_PROVIDER);
			 * mLastLoc.setLatitude(40.6444122);
			 * mLastLoc.setLongitude(-73.782745);
			 */
		}

		Log.e(TAG,
				"LastLoc: " + mLastLoc.getLatitude() + ", "
						+ mLastLoc.getLongitude());
		Log.e(TAG,
				"myLoc: " + myLoc.getLatitude() + ", " + myLoc.getLongitude());

		boolean lastloc_in_range = inProximity(mLastLoc, locationCursor);
		boolean currloc_in_range = inProximity(myLoc, locationCursor);

		Log.e(TAG, "last loc in range?: " + lastloc_in_range);
		Log.e(TAG, "curr loc in range?: " + currloc_in_range);

		int trigger_event = locationCursor.getInt(locationCursor
				.getColumnIndexOrThrow(LocationDbAdapter.KEY_EVENT));// get from
		// locationCursor

		// for DEBUG only

		// entry event
		// last loc not in radius, current is
		if (!lastloc_in_range && currloc_in_range) {

			// trigger location if it is set for on_entry
			return (trigger_event == LocationValues.Event.ON_ENTRY.ordinal() || trigger_event == LocationValues.Event.ON_ENTRY_EXIT
					.ordinal());
		}

		// exit event
		// last loc in radius, current not
		if (lastloc_in_range && !currloc_in_range) {

			// trigger location of it is set for on_exit
			return (trigger_event == LocationValues.Event.ON_EXIT.ordinal() || trigger_event == LocationValues.Event.ON_ENTRY_EXIT
					.ordinal());
		}

		Log.e(TAG, "------------------------------------");
		// when last loc and curr loc are both within range or both out of range
		// ie, no entry or exit events
		return false;

	}

	private boolean inProximity(Location myLoc, Cursor locationCursor) {

		// get specified radius from database
		double radius = locationCursor.getDouble(locationCursor
				.getColumnIndexOrThrow(LocationDbAdapter.KEY_RADIUS));
		; // get from location cursor
		// lat of location
		double lat = locationCursor.getDouble(locationCursor
				.getColumnIndexOrThrow(LocationDbAdapter.KEY_LAT));
		// lon of location
		double lon = locationCursor.getDouble(locationCursor
				.getColumnIndexOrThrow(LocationDbAdapter.KEY_LNG));

		
		GpsPoint myGpsLoc = new GpsPoint(myLoc.getLatitude(),
				myLoc.getLongitude());

		// gps loc the location is set for
		GpsPoint remGpsLoc = new GpsPoint(lat, lon);
		// whether or not my curr gps loc is in range of rem's gps loc
		return mGeoDist.inRange(myGpsLoc, remGpsLoc, radius);
	}

}
