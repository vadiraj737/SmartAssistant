package com.mc.smartassistant;

import java.util.Calendar;
import java.util.Random;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.mc.smartassistant.LocationDbAdapter;
import com.mc.dbtables.EventDetails;

public class EventActivity extends Activity {
	private TextView dateDisplay;
	private TextView timeDisplay;
	private TextView endTimeDisplay;
 
	private Button btnDateChange;
	private Button btnTimeChange;
	private Button btnSendData;
	private Button btnEndTime;
	private Button btnGoBack;
	
	private int year;
	private int month;
	private int day;
	private int hour;
	private int minute;
	private int endHour;
	private int endMinute;
 
	Context ctx = this;
	LocationDbAdapter eventDB;
	EventDetails event;
	private  PendingIntent pendingIntent;
	private  int pendingIntentID;
	Random gen= new Random(1000);

	private Intent myIntent;
	int requestCode=0;
	int situationId=9;

	static final int DATE_DIALOG_ID = 999;
	static final int TIME_DIALOG_ID = 9999;
	static final int END_TIME_DIALOG_ID = 9998;
 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		requestCode = extras.getInt("REQ");
		situationId= extras.getInt(LocationDbAdapter.KEY_SITUATIONID);
		setContentView(R.layout.numberone);
		eventDB = new LocationDbAdapter(this);
		setTimeOnView();	
		setDateOnView();
		
		addListenerOnButton();
		addListenerOnButton();	
		
	}
 
	public void setDateOnView() {
 
		if(requestCode==1)
		{
		dateDisplay = (TextView) findViewById(R.id.dateView);
 
		final Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
 
		// Set Current Date/Update
		dateDisplay.setText(new StringBuilder().append(month + 1).append("-").append(day).append("-").append(year).append(" "));
 
		}
		else if(requestCode==2)
		{
			Log.e("S","Thimmappa");

			dateDisplay = (TextView) findViewById(R.id.dateView);
			 
			eventDB = new LocationDbAdapter(this);
			event = new EventDetails();
			
			eventDB.open();
			Cursor cur=eventDB.fetchEvent((long)situationId);
			//Cursor cur = eventDB.fetchAllEvents();
			if(cur.moveToFirst()){
				if(situationId==cur.getInt(cur.getColumnIndex(LocationDbAdapter.KEY_SITUATIONID)))
				{
				day= cur.getInt(cur.getColumnIndex(LocationDbAdapter.COLUMN_DAY));

				month= cur.getInt(cur.getColumnIndex(LocationDbAdapter.COLUMN_MONTH));
				year= cur.getInt(cur.getColumnIndex(LocationDbAdapter.COLUMN_YEAR));
				}
			while (cur.moveToNext()) {
					
				if(situationId==cur.getInt(cur.getColumnIndex(LocationDbAdapter.KEY_SITUATIONID)))
				{
				day= cur.getInt(cur.getColumnIndex(LocationDbAdapter.COLUMN_DAY));
				Log.e("S",""+day);

				month= cur.getInt(cur.getColumnIndex(LocationDbAdapter.COLUMN_MONTH));
				year= cur.getInt(cur.getColumnIndex(LocationDbAdapter.COLUMN_YEAR));
				}
				}
			}
			cur.close();
			
			// Set Current Date/Update
			dateDisplay.setText(new StringBuilder().append(month + 1).append("-").append(day).append("-").append(year).append(" "));
			
		}
 
	}
 
	private DatePickerDialog.OnDateSetListener datePickerListener 
                = new DatePickerDialog.OnDateSetListener() {
 
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {
			year = selectedYear;
			month = selectedMonth;
			day = selectedDay;
 
			// Update Date on View
			dateDisplay.setText(new StringBuilder().append(month + 1).append("-").append(day).append("-").append(year).append(" "));
 
		
 
		}
	};
	
		public void setTimeOnView() {
	 
			if(requestCode==1)
			{
				timeDisplay = (TextView) findViewById(R.id.timeView);
				endTimeDisplay = (TextView) findViewById(R.id.endTimeView);
				final Calendar c = Calendar.getInstance();
				hour = c.get(Calendar.HOUR_OF_DAY);
				minute = c.get(Calendar.MINUTE);
				endHour = c.get(Calendar.HOUR_OF_DAY);
				endMinute = c.get(Calendar.MINUTE);
				
		 
				// Set Current Time
				timeDisplay.setText(new StringBuilder().append(pad(hour)).append(":").append(pad(minute)));
				endTimeDisplay.setText(new StringBuilder().append(pad(hour)).append(":").append(pad(minute)));
			}
			else if(requestCode==2)
			{
				timeDisplay = (TextView) findViewById(R.id.timeView);
				endTimeDisplay = (TextView) findViewById(R.id.endTimeView);
				 
				eventDB = new LocationDbAdapter(this);
				event = new EventDetails();
				
				eventDB.open();
				Cursor cur = eventDB.fetchEvent((long)situationId);
				if(cur.moveToFirst()){
					hour= cur.getInt(cur.getColumnIndex(LocationDbAdapter.COLUMN_STARTHOUR));
					minute= cur.getInt(cur.getColumnIndex(LocationDbAdapter.COLUMN_STARTMINUTE));
					//Toast.makeText(getBaseContext(),""+hour+":"+minute,Toast.LENGTH_LONG).show();
					endHour= cur.getInt(cur.getColumnIndex(LocationDbAdapter.COLUMN_ENDHOUR));
					endMinute= cur.getInt(cur.getColumnIndex(LocationDbAdapter.COLUMN_ENDMINUTE));
				while (cur.moveToNext()) {
					
						if(situationId==cur.getInt(cur.getColumnIndex(LocationDbAdapter.KEY_SITUATIONID)))
						{
						hour= cur.getInt(cur.getColumnIndex(LocationDbAdapter.COLUMN_STARTHOUR));
						minute= cur.getInt(cur.getColumnIndex(LocationDbAdapter.COLUMN_STARTMINUTE));
						//Toast.makeText(getBaseContext(),""+hour+":"+minute,Toast.LENGTH_LONG).show();
						endHour= cur.getInt(cur.getColumnIndex(LocationDbAdapter.COLUMN_ENDHOUR));
						endMinute= cur.getInt(cur.getColumnIndex(LocationDbAdapter.COLUMN_ENDMINUTE));

						}
						
					}
				timeDisplay.setText(new StringBuilder().append(pad(hour)).append(":").append(pad(minute)));
				endTimeDisplay.setText(new StringBuilder().append(pad(endHour)).append(":").append(pad(endMinute)));
				cur.close();
				
			}
					
			}
		}
	
		
		private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
			public void onTimeSet(TimePicker view, int selectedHour,
					int selectedMinute) {
				hour = selectedHour;
				minute = selectedMinute;
				timeDisplay.setText(new StringBuilder().append(pad(hour)).append(":").append(pad(minute)));	 
			}
		};
		
		private TimePickerDialog.OnTimeSetListener timePickerListener2 = new TimePickerDialog.OnTimeSetListener() {
			public void onTimeSet(TimePicker view, int selectedHour,
					int selectedMinute) {
				endHour = selectedHour;
				endMinute = selectedMinute;
				endTimeDisplay.setText(new StringBuilder().append(pad(endHour)).append(":").append(pad(endMinute)));	 
			}
		};
		@Override
		protected Dialog onCreateDialog(int id) {
			switch (id) {
			case TIME_DIALOG_ID:
				// Start time picker
				return new TimePickerDialog(this,timePickerListener, hour, minute,false);
		    case DATE_DIALOG_ID:
				   // Date Picker
				   return new DatePickerDialog(this, datePickerListener,year, month,day);
		    case END_TIME_DIALOG_ID:
		    		//End time picker
		    		return new TimePickerDialog(this, timePickerListener2, endHour, endMinute, false);
			}
			return null;
		}
	 
		private static String pad(int c) {
			if (c >= 10)
			   return String.valueOf(c);
			else
			   return "0" + String.valueOf(c);
		}
		
		
		public void addListenerOnButton() {
			btnDateChange = (Button) findViewById(R.id.btnChangeDate);
			btnDateChange.setOnClickListener(new OnClickListener() {
	 
				public void onClick(View v) {
					showDialog(DATE_DIALOG_ID);
				}
			});
			
			btnSendData = (Button) findViewById(R.id.btnSetup);
			btnSendData.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					pendingIntentID=  gen.nextInt();
					if(requestCode==1)
					{
					event = new EventDetails();
					myIntent=new Intent(EventActivity.this, MyAlarmService.class);
					pendingIntent = PendingIntent.getService(EventActivity.this, pendingIntentID, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);
					AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(System.currentTimeMillis());
					cal.clear();
					cal.set(year, month, day);
					cal.set(Calendar.HOUR_OF_DAY, hour);
					cal.set(Calendar.MINUTE,minute);
					cal.set(Calendar.SECOND,0);
					cal.set(Calendar.MILLISECOND, 0);
					/*eventDB.open();
					event.setDay(day);
					event.setMonth(month);
					event.setYear(year);
					event.setStartHour(hour);
					event.setStartMinute(minute);
					event.setEndHour(endHour);
					event.setEndMinute(endMinute);
					eventDB.createEvent(event);*/
					alarmManager.set(AlarmManager.RTC, cal.getTimeInMillis(), pendingIntent);
					//Toast.makeText(EventActivity.this, "Start Alarm", Toast.LENGTH_LONG).show();
					pendingIntentID=  gen.nextInt();
					
					Intent myIntent2=new Intent(EventActivity.this, MyAlarmService.class);
					PendingIntent pendingIntent2 = PendingIntent.getService(EventActivity.this, pendingIntentID, myIntent2, PendingIntent.FLAG_CANCEL_CURRENT);
					AlarmManager alarmManager2 = (AlarmManager)getSystemService(ALARM_SERVICE);
					Calendar cal2 = Calendar.getInstance();
					cal2.setTimeInMillis(System.currentTimeMillis());
					cal2.clear();
					cal2.set(year, month, day);
					cal2.set(Calendar.HOUR_OF_DAY, endHour);
					cal2.set(Calendar.MINUTE,endMinute);
					cal2.set(Calendar.SECOND,0);
					cal2.set(Calendar.MILLISECOND, 0);
					/*eventDB.open();
					event.setDay(day);
					event.setMonth(month);
					event.setYear(year);
					event.setStartHour(hour);
					event.setStartMinute(minute);
					event.setEndHour(endHour);
					event.setEndMinute(endMinute);
					eventDB.createEvent(event);*/
					alarmManager2.set(AlarmManager.RTC, cal2.getTimeInMillis(), pendingIntent2);
					//Toast.makeText(EventActivity.this, "Start End Alarm", Toast.LENGTH_LONG).show();
					pendingIntentID=  gen.nextInt();
					}
					else if(requestCode==2)
					{
						event = new EventDetails();
						myIntent= new Intent(EventActivity.this, MyAlarmService.class);
						pendingIntent = PendingIntent.getService(EventActivity.this, pendingIntentID, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);
						AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
						Calendar cal = Calendar.getInstance();
						cal.setTimeInMillis(System.currentTimeMillis());
						cal.clear();
						cal.set(year, month, day);
						cal.set(Calendar.HOUR_OF_DAY, hour);
						cal.set(Calendar.MINUTE,minute);						
						cal.set(Calendar.SECOND,0);
						cal.set(Calendar.MILLISECOND, 0);
						/*eventDB.open();
						event.setDay(day);
						event.setMonth(month);
						event.setYear(year);
						event.setStartHour(hour);
						event.setStartMinute(minute);
						event.setEndHour(endHour);
						event.setEndMinute(endMinute);
						eventDB.updateEvent(situationId,event);*/
						alarmManager.set(AlarmManager.RTC, cal.getTimeInMillis(), pendingIntent);
						//Toast.makeText(EventActivity.this, "Start Alarm", Toast.LENGTH_LONG).show();
						pendingIntentID=  gen.nextInt();
						

						Intent myIntent2=new Intent(EventActivity.this, MyAlarmService.class);
						PendingIntent pendingIntent2 = PendingIntent.getService(EventActivity.this, pendingIntentID, myIntent2, PendingIntent.FLAG_CANCEL_CURRENT);
						AlarmManager alarmManager2 = (AlarmManager)getSystemService(ALARM_SERVICE);
						Calendar cal2 = Calendar.getInstance();
						cal2.setTimeInMillis(System.currentTimeMillis());
						cal2.clear();
						cal2.set(year, month, day);
						cal2.set(Calendar.HOUR_OF_DAY, endHour);
						cal2.set(Calendar.MINUTE,endMinute);
						cal2.set(Calendar.SECOND,0);
						cal2.set(Calendar.MILLISECOND, 0);
						/*eventDB.open();
						event.setDay(day);
						event.setMonth(month);
						event.setYear(year);
						event.setStartHour(hour);
						event.setStartMinute(minute);
						event.setEndHour(endHour);
						event.setEndMinute(endMinute);
						eventDB.createEvent(event);*/
						alarmManager2.set(AlarmManager.RTC, cal2.getTimeInMillis(), pendingIntent2);
						//Toast.makeText(EventActivity.this, "Start End Alarm", Toast.LENGTH_LONG).show();
						pendingIntentID=  gen.nextInt();
					}
				}
	 
			});
			
			btnTimeChange = (Button) findViewById(R.id.btnChangeTime);
			btnTimeChange.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {				
					showDialog(TIME_DIALOG_ID);
				}
			});
			
			btnEndTime = (Button) findViewById(R.id.btnEndTime);
			btnEndTime.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {				
					showDialog(END_TIME_DIALOG_ID);
				}
			});
	 
			btnGoBack = (Button) findViewById(R.id.btnGoBack);
			btnGoBack.setOnClickListener(new OnClickListener() {
	 
				public void onClick(View v) {
					Intent goBackIntent = new Intent(EventActivity.this, SituationActivity.class);
					goBackIntent.putExtra(LocationDbAdapter.COLUMN_DAY,day);
					goBackIntent.putExtra(LocationDbAdapter.COLUMN_MONTH,month);
					goBackIntent.putExtra(LocationDbAdapter.COLUMN_YEAR,year);
					goBackIntent.putExtra(LocationDbAdapter.COLUMN_STARTHOUR,hour);
					goBackIntent.putExtra(LocationDbAdapter.COLUMN_STARTMINUTE,minute);
					goBackIntent.putExtra(LocationDbAdapter.COLUMN_ENDHOUR,endHour);
					goBackIntent.putExtra(LocationDbAdapter.COLUMN_ENDMINUTE,endMinute);
					EventActivity.this.setResult(RESULT_OK, goBackIntent);
					EventActivity.this.finish();
				}
			});
		}
}
