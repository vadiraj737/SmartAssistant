package com.mc.smartassistant;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.mc.dbtables.SituationTable;

public class MainActivity extends Activity {

	ListView situationList;
	ArrayList<String> situations;
	ArrayList<SituationTable> situationTables;
	ArrayAdapter<String> situationListAdapter;

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {

		super.onActivityResult(requestCode, resultCode, intent);
		Log.e("MainActivity", "request code:"+requestCode+" resultcode:"+resultCode);

		if(requestCode == 2 && resultCode == RESULT_CANCELED)
		{

		}
		if(requestCode == 2 && resultCode == 3)
		{
			Bundle extras = intent.getExtras();
			if(extras!=null)
			{

				int deletedSituationID = extras.getInt("deletedSituationID");
				Log.e("MainActivity", "deleted with "+deletedSituationID);
				if(situations!=null)
				{
					for(int i=0;i<situationTables.size();i++)
					{
						if(situationTables.get(i).getSituationID()==deletedSituationID)
						{
							situationTables.remove(i);
							situations.remove(i);

							situationListAdapter = new ArrayAdapter<String>(getBaseContext(),
									R.layout.situation_row, R.id.situationName, situations);
							situationList.setAdapter(situationListAdapter);
							break;
						}
					}
				}	
			}
		}
		if((requestCode == 2 && resultCode == RESULT_OK))
		{
			situations = new ArrayList<String>();
			situationTables = new ArrayList<SituationTable>();
			fillData();
			situationListAdapter = new ArrayAdapter<String>(getBaseContext(),
					R.layout.situation_row, R.id.situationName, situations);
			situationList.setAdapter(situationListAdapter);
		}
		if((requestCode == 1 && resultCode == RESULT_OK))
		{
			LocationDbAdapter mDbHelper = new LocationDbAdapter(this);
			mDbHelper.open();
			Bundle extras = intent.getExtras();


			if(situations==null)
			{
				String s = extras.getString("SituationName");
				situations = new ArrayList<String>();
				situationTables = new ArrayList<SituationTable>();
				situations.add(s);
				SituationTable tempSituationTable = new SituationTable();
				tempSituationTable.setSituationID(extras.getInt("SituationID"));
				tempSituationTable.setSituationName(s);
				if(requestCode == 1)
					situationTables.add(tempSituationTable);
				//Toast.makeText(getBaseContext(), "recieved:"+s, Toast.LENGTH_LONG).show();

			}
			else
			{
				String s = extras.getString("SituationName");
				situations.add(s);
				SituationTable tempSituationTable = new SituationTable();
				tempSituationTable.setSituationID(extras.getInt("SituationID"));
				tempSituationTable.setSituationName(s);
				situationTables.add(tempSituationTable);

			}
			situationListAdapter = new ArrayAdapter<String>(getBaseContext(),
					R.layout.situation_row, R.id.situationName, situations);
			situationList.setAdapter(situationListAdapter);
			Cursor fetchSituation =  mDbHelper.fetchAllSituations();
			if(fetchSituation.moveToFirst()){
				while (fetchSituation.moveToNext()) {
					SituationTable si = new SituationTable();
					si.setSituationName(fetchSituation.getString(fetchSituation.getColumnIndexOrThrow(LocationDbAdapter.KEY_SITUATION)));
					//Toast.makeText(getBaseContext(), si.getSituationName(), Toast.LENGTH_LONG).show();
				}
			}
/*			Cursor fetchLocation =  mDbHelper.fetchAllLocations();
			if(fetchLocation==null){
				Log.e("MainActivity", "The cursor is null and is not able to be fetched");

			}
			Toast.makeText(getBaseContext(), "after the cursor locastion fetched", Toast.LENGTH_LONG).show();
			while (fetchLocation.moveToNext()) {
				LocationValues lv = new LocationValues();
				lv.setBody(fetchLocation.getString(fetchLocation.getColumnIndexOrThrow(LocationDbAdapter.KEY_BODY)));
				lv.setAddr(fetchLocation.getString(fetchLocation.getColumnIndex(LocationDbAdapter.KEY_ADDR)));
				lv.setLat(fetchLocation.getDouble(fetchLocation.getColumnIndexOrThrow(LocationDbAdapter.KEY_LAT)));
				lv.setLon(fetchLocation.getDouble(fetchLocation.getColumnIndexOrThrow(LocationDbAdapter.KEY_LNG)));

				Log.e("MainActivity", "The body is :"+lv.getBody());
				Log.e("MainActivity", "The address is :"+lv.getAddr());
				Log.e("MainActivity", "The latitude is :"+lv.getLat());
				Log.e("MainActivity", "The longitude is :"+lv.getLon());

				Toast.makeText(getBaseContext(), lv.getBody(), Toast.LENGTH_LONG).show();
			}
*/
			mDbHelper.close();
		}
	}
	/*
	 * fills the situation data when the activity is created from the values in the Database
	 * 
	 */
	public void fillData()
	{
		LocationDbAdapter dbAdapter = new LocationDbAdapter(this);
		dbAdapter.open();
		Cursor situationCursor = dbAdapter.fetchAllSituations();
		if(situationCursor.moveToFirst())
		{
			situations = new ArrayList<String>();
			situationTables = new ArrayList<SituationTable>();
			while(situationCursor.moveToNext())
			{
				SituationTable tempSituation = new SituationTable();
				tempSituation.setSituationName(situationCursor.getString(situationCursor.getColumnIndex(LocationDbAdapter.KEY_SITUATION)));
				tempSituation.setSituationID(situationCursor.getInt(situationCursor.getColumnIndex(LocationDbAdapter.KEY_SITUATIONID)));
				situations.add(tempSituation.getSituationName());
				situationTables.add(tempSituation);
			}
		}
		dbAdapter.close();
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button bookmarkLoc = (Button) findViewById(R.id.loc_bookmarks);

		situationList = (ListView) findViewById(R.id.situationlist);
		//situations = new ArrayList<String>();
		fillData();
		situationListAdapter = new ArrayAdapter<String>(getBaseContext(),
				R.layout.situation_row, R.id.situationName, situations);
		situationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View item, int position,
					long id) {
				String situation = situationListAdapter.getItem(position);
				Intent intent = new Intent(MainActivity.this,
						SituationActivity.class);
				intent.putExtra("SituationName", situation);
				intent.putExtra("ReqCode", 2);
				intent.putExtra("SituationID", situationTables.get(position).getSituationID());
				startActivityForResult(intent, 2);
			}
		});
		if(situations!=null)
			situationList.setAdapter(situationListAdapter);
		Button addSituation = (Button) findViewById(R.id.AddSituation);
		addSituation.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						SituationActivity.class);
				intent.putExtra("ReqCode", 1);
				startActivityForResult(intent, 1);
			}
		});

		bookmarkLoc.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				Intent i = new Intent(MainActivity.this, BookmarkList.class);
				startActivity(i);
			}
		});  


	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		stopLocationService();
		return super.onOptionsItemSelected(item);
	}

	@SuppressWarnings("unused")
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

}
