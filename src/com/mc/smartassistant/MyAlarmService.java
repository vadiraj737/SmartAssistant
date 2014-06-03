package com.mc.smartassistant;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;
import com.mc.dbtables.BlacklistTable;
import com.mc.dbtables.SettingsTable;

public class MyAlarmService extends Service {

	private int hour;
	private int minute;
	private int situationId;
	private SettingsTable settings;
	private ArrayList<BlacklistTable> blacklist;
	BroadcastReceiver CallBlocker;
	TelephonyManager telephonyManager;
	ITelephony telephonyService;
	boolean startStatus=false;
	boolean endStatus=false;
	@Override
	public void onCreate() {
		
		//Toast.makeText(this, "Inside onCreate()", Toast.LENGTH_LONG).show();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		//Toast.makeText(this, "Inside onBind()", Toast.LENGTH_LONG).show();
		return null;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//Toast.makeText(this, "Inside onDestroy()", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);	
		//Toast.makeText(this, "Inside onStart()", Toast.LENGTH_LONG).show();
		handleCommand(intent);

	}
	void handleCommand(Intent intent) {
		
		final Calendar c = Calendar.getInstance();
		hour = c.get(Calendar.HOUR_OF_DAY);
		minute = c.get(Calendar.MINUTE);
		LocationDbAdapter eventDB = new LocationDbAdapter(this);
        Log.e("Corresponding Situation is","1");
		eventDB.open();
		Cursor cur=eventDB.fetchAllEvents();
		if(cur.moveToFirst()){
			Log.e("Corresponding Situation is","2");
			Log.e("Corresponding Situation is","hour:"+cur.getColumnIndex(LocationDbAdapter.COLUMN_STARTHOUR));
			Log.e("Corresponding Situation is","sys hour"+hour);
			Log.e("Corresponding Situation is","minute"+cur.getInt(cur.getColumnIndex(LocationDbAdapter.COLUMN_STARTHOUR)));
			Log.e("Corresponding Situation is","sys minute"+minute);
			
				if(hour==cur.getInt(cur.getColumnIndex(LocationDbAdapter.COLUMN_STARTHOUR)))
				{
					if(minute==cur.getInt(cur.getColumnIndex(LocationDbAdapter.COLUMN_STARTMINUTE)))
					{
						startStatus=true;
						situationId= cur.getInt(cur.getColumnIndex(LocationDbAdapter.KEY_SITUATIONID));
						Log.e("Corresponding Situation is","Situation retrieved"+situationId);
					}
				}
				while (cur.moveToNext()) {
					Log.e("Corresponding Situation is","3");
					
					if(hour==cur.getInt(cur.getColumnIndex(LocationDbAdapter.COLUMN_STARTHOUR)))
					{
						if(minute==cur.getInt(cur.getColumnIndex(LocationDbAdapter.COLUMN_STARTMINUTE)))
						{
							startStatus=true;
							situationId= cur.getInt(cur.getColumnIndex(LocationDbAdapter.KEY_SITUATIONID));
							Log.e("Corresponding Situation is","Situation retrieved"+situationId);
							break;
						}
					}
					
			}
		}
		if(cur.moveToFirst()){
			Log.e("Corresponding Situation is","2");
			Log.e("Corresponding Situation is","hour:"+cur.getColumnIndex(LocationDbAdapter.COLUMN_ENDHOUR));
			Log.e("Corresponding Situation is","sys hour"+hour);
			Log.e("Corresponding Situation is","minute"+cur.getInt(cur.getColumnIndex(LocationDbAdapter.COLUMN_ENDHOUR)));
			Log.e("Corresponding Situation is","sys minute"+minute);
			
				if(hour==cur.getInt(cur.getColumnIndex(LocationDbAdapter.COLUMN_ENDHOUR)))
				{
					if(minute==cur.getInt(cur.getColumnIndex(LocationDbAdapter.COLUMN_ENDMINUTE)))
					{
						endStatus=true;
						situationId= cur.getInt(cur.getColumnIndex(LocationDbAdapter.KEY_SITUATIONID));
						Log.e("Corresponding Situation is","Situation retrieved"+situationId);
					}
				}
				while (cur.moveToNext()) {
					Log.e("Corresponding Situation is","3");
					
					if(hour==cur.getInt(cur.getColumnIndex(LocationDbAdapter.COLUMN_ENDHOUR)))
					{
						if(minute==cur.getInt(cur.getColumnIndex(LocationDbAdapter.COLUMN_ENDMINUTE)))
						{
							endStatus=true;
							situationId= cur.getInt(cur.getColumnIndex(LocationDbAdapter.KEY_SITUATIONID));
							Log.e("Corresponding Situation is","Situation retrieved"+situationId);
							break;
						}
					}
					
			}
		}
		cur.close();
		
		settings= new SettingsTable();
		Cursor cur2= eventDB.fetchSettings(situationId);
		if(cur2.moveToFirst())
		{
			Log.e("Corresponding Situation is","settings retrieved");
			
			settings.setBluetoothStatus(cur2.getInt(cur2.getColumnIndex(LocationDbAdapter.COLUMN_BLUETOOTH)));
			settings.setWifiStatus(cur2.getInt(cur2.getColumnIndex(LocationDbAdapter.COLUMN_WIFI)));
			settings.setBrightnessValue(cur2.getDouble(cur2.getColumnIndex(LocationDbAdapter.COLUMN_BRIGHTNESS)));
			settings.setRingtoneID(cur2.getString(cur2.getColumnIndex(LocationDbAdapter.COLUMN_RINGTONEID)));
		}
		 
		cur2.close();
		
		blacklist= new ArrayList<BlacklistTable>();
		Cursor cur3= eventDB.fetchBlackList(situationId);
		if(cur3.moveToFirst())
		{
			Log.e("Corresponding Situation is","blacklist retrieved");
			BlacklistTable bl= new BlacklistTable();
			bl.setNumber(cur3.getString(cur3.getColumnIndex(LocationDbAdapter.COLUMN_NUMBER)));
			bl.setSituationID(situationId);

			blacklist.add(bl);
			while(cur3.moveToNext())
			{
				Log.e("Corresponding Situation is","blacklist retrieved");
				BlacklistTable bl2= new BlacklistTable();
				bl2.setNumber(cur3.getString(cur3.getColumnIndex(LocationDbAdapter.COLUMN_NUMBER)));
				bl2.setSituationID(situationId);
				blacklist.add(bl2);
			}
		}
		cur3.close();
		if(startStatus || endStatus)
		{
			Log.e("Corresponding Situation is","status is true");
			startCallBlocking();
			setBluetooth(settings.getBluetoothStatus());
			setWifi(settings.getWifiStatus());
			setBrightness(settings.getBrightnessValue());
			setRingtone();
		}
	}

	public void setRingtone()
	{
		Log.e("Ringtone", "inside setRingtone");
		String uriString = settings.getRingtoneID();
		Intent intent = new Intent(getBaseContext(), DummyRingtoneActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //this is important
		//in the next line 'brightness' should be a float number between 0.0 and 1.0
		intent.putExtra("title", uriString); 
		intent.putExtra("end", endStatus);
		
		getApplication().startActivity(intent);
		Log.e("Ringtone Value", "outside ringtone value");
		
	}
	public void setWifi(int wifi)
	{
		WifiManager wifiManager = (WifiManager)getBaseContext().getSystemService(Context.WIFI_SERVICE);
		if(endStatus)
		{
			if(wifi==1)
			{
				wifi=0;
			}
			else if(wifi==0)
			{
				wifi=1;
			}
		}
			if(wifi==1)
			{
				if(!wifiManager.isWifiEnabled())
				{
					wifiManager.setWifiEnabled(true);
				//Toast.makeText(getBaseContext(), "Wifi Turned On", Toast.LENGTH_LONG).show();
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
	
	public void setBrightness(double value) {
		Log.e("Brightness Value", "inside brightness value");
		float brightness;
		brightness = (float)value/(float)255;
		Intent intent = new Intent(getBaseContext(), DummyBrightnessActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //this is important
		//in the next line 'brightness' should be a float number between 0.0 and 1.0
		intent.putExtra("brightness value", brightness);
		intent.putExtra("reqCode", 999);
		intent.putExtra("end", endStatus);
		getApplication().startActivity(intent);
		Log.e("Brightness Value", "outside brightness value");
		
	}
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

				//Toast.makeText(getBaseContext(), "onrecieve",Toast.LENGTH_LONG).show();

				telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
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
						//Toast.makeText(getBaseContext(), "number:"+incomingNumber,
								//Toast.LENGTH_LONG).show();
						if (blacklist!=null) {
							Log.e("Alarm Service", "ContactData not null-----------------------------");
							try {
								boolean isBlocked = false;
								for (int i = 0; i < blacklist.size(); i++) {
									Log.e("Alarm Service", "numbers: "+blacklist.get(i).getNumber());
											
									if(incomingNumber.contains(""+blacklist.get(i).getNumber())||incomingNumber.contains("1"+blacklist.get(i).getNumber()))
									{
										//Toast.makeText(getBaseContext(), "call blocked from: "+blacklist.get(i).getNumber(),
												//Toast.LENGTH_LONG).show();
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
	
	public void setBluetooth(int bluetooth) {
		Log.e("Corresponding Situation is","bluetooth");
		
		BluetoothAdapter myBluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();
		
		if(endStatus)
		{
			if(bluetooth==1)
			{
				bluetooth=0;
			}
			else if(bluetooth==0)
			{
				bluetooth=1;
			}
		}
		if (bluetooth == 1) {
			if (!myBluetoothAdapter.isEnabled()) {
				myBluetoothAdapter.enable();
			}
			try {
			//	Toast.makeText(getBaseContext(),
				//		"Turning ON bluetooth, please wait....",
					//	Toast.LENGTH_LONG).show();
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
			//	Toast.makeText(getBaseContext(),
				//		"Turning OFF bluetooth, please wait....",
					//	Toast.LENGTH_LONG).show();
				Thread.sleep(1000);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		}
		
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		//Toast.makeText(this, "MyAlarmService.onUnbind()", Toast.LENGTH_LONG).show();
		return super.onUnbind(intent);
	}

}