package com.mc.smartassistant;

import com.app.lib.LocationValues;
import com.mc.dbtables.BlacklistTable;
import com.mc.dbtables.EventDetails;
import com.mc.dbtables.SettingsTable;
import com.mc.dbtables.SituationTable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LocationDbAdapter {

	public static final String KEY_ROWID = "_id";
	public static final String KEY_BODY = "body";
    public static final String KEY_LAT = "lat";
    public static final String KEY_LNG = "lng";
    public static final String KEY_ADDR = "address";
    public static final String KEY_EVENT = "event";
    public static final String KEY_RADIUS = "radius";
    
    public static final String COLUMN_SETTINGSID = "SETTINGID";
    public static final String COLUMN_RINGTONEID = "RINGTONEID";
    public static final String COLUMN_BLUETOOTH = "BLUETOOTH";
    public static final String COLUMN_WIFI = "WIFI";
    public static final String COLUMN_BRIGHTNESS = "BRIGHTNESS";
    public static final String COLUMN_BLACKLISTid = "BLACKLISTID";

    public static final String COLUMN_id_BLACKLIST = "ID_BLACKLIST";   
    public static final String COLUMN_NUMBER = "NUMBER";
    public static final String COLUMN_EVENTID = "EVENTID";
    
    public static final String COLUMN_STARTDATE = "STARTDATE";
    public static final String COLUMN_ENDDATE = "ENDDATE";
    public static final String COLUMN_STARTTIME = "STARTTIME";
    public static final String COLUMN_ENDTIME = "ENDTIME";
    
    public static final String KEY_SITUATIONID = "SITUATIONID";
    public static final String KEY_SETTINGID = "SETTING_ID";
    public static final String KEY_LOCATIONID = "LOCATION_ID";
    public static final String KEY_TIMEID = "TIME_ID";
    public static final String KEY_SITUATION = "SITUATION";
    
    public static final String KEY_EVENTID = "EVENTID";
	public static final String COLUMN_DAY = "DAY";
	public static final String COLUMN_MONTH = "MONTH";
	public static final String COLUMN_YEAR = "YEAR";
	public static final String COLUMN_STARTHOUR = "STARTHOUR";
	public static final String COLUMN_STARTMINUTE = "STARTMINUTE";
	public static final String COLUMN_ENDHOUR = "ENDHOUR";
	public static final String COLUMN_ENDMINUTE = "ENDMINUTE";
    
    
    
    //public static final String KEY_TIME = "time";

    private static final String TAG = "LocationDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    
    /**
     * Database creation sql statement
     */
    private static final String SETTINGS_TABLE_CREATE =
        "create table Settings ("+KEY_SITUATIONID + " integer primary key,"+ 
        COLUMN_RINGTONEID + " text, " +
        COLUMN_BLUETOOTH + " integer, " +
        COLUMN_WIFI + " integer, " +
        COLUMN_BRIGHTNESS + " real" +
        		");";
    
    private static final String BLOCKCALLS_TABLE_CREATE =
            "create table Blockcalls ("+KEY_SITUATIONID+" integer"+","+COLUMN_BLACKLISTid+" integer primary key autoincrement, " + 
            COLUMN_NUMBER + " text);";
    
    private static final String EVENT_TABLE = "Event";
    
   	private static final String EVENTS_TABLE_CREATE =
   		        "create table Event ("+KEY_EVENTID + " Integer primary key autoincrement,"+ 
   		   		KEY_SITUATIONID + " integer, " +
   		        COLUMN_DAY + " integer, " +
   		        COLUMN_MONTH + " integer, " +
   		        COLUMN_YEAR + " integer, " +
   		        COLUMN_STARTHOUR + " integer, " +
   		        COLUMN_STARTMINUTE + " integer, " +
   		        COLUMN_ENDHOUR + " integer, " +
   		        COLUMN_ENDMINUTE + " integer" +
   		        		");";
   	
        
    private static final String LOCATION_CREATE = "create table Locations ("+KEY_SITUATIONID+" integer primary key, " + 
        KEY_BODY + " text, " +
        KEY_LAT + " real, " +
        KEY_LNG + " real, " +
        KEY_ADDR + " text, " +
        KEY_RADIUS + " real, " + 
        KEY_EVENT + " integer );";
    
    private static final String SITUATION_TABLE_CREATE =  "create table Situations ("+KEY_SITUATIONID+" integer primary key autoincrement, " + 
            KEY_SITUATION + " text not null" +
    		");";
            
    private static final String DATABASE_NAME = "Smart_Assistant_Database";
    private static final String BLOCKCALLS_TABLE = "Blockcalls";
    //private static final String EVENT_TABLE = "Event";
    private static final String LOCATION_TABLE = "Locations";
    private static final String SETTINGS_TABLE = "Settings";
    private static final String SITUATION_TABLE = "Situations";

    private static final int DATABASE_VERSION = 2;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        	
        	db.execSQL(BLOCKCALLS_TABLE_CREATE);
        	db.execSQL(EVENTS_TABLE_CREATE);
        	db.execSQL(LOCATION_CREATE);
            db.execSQL(SETTINGS_TABLE_CREATE);
            db.execSQL(SITUATION_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS Locations");
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public LocationDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the location database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public LocationDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    
    /**
     * Create a new location using the title provided. If the location is
     * successfully created return the new rowId for that location, otherwise return
     * a -1 to indicate failure.
     * 
     * @param Body the body of the location
     * @return rowId or -1 if failed
     */
    public long createLocation(LocationValues l) {
        ContentValues initialValues = new ContentValues();
        Log.e(TAG, "THIS IS A TEST OF INSERTION OF LOCATION");
        initialValues.put(KEY_SITUATIONID, l.getSituationID());
        initialValues.put(KEY_BODY, l.getBody());
        initialValues.put(KEY_ADDR, l.getAddr());
        initialValues.put(KEY_RADIUS, l.getRange());
        initialValues.put(KEY_EVENT, l.getEvent());
        Log.e(TAG, "THIS IS A TEST OF INSERTION:"+l.getLat());
    	initialValues.put(KEY_LAT, l.getLat());
    	initialValues.put(KEY_LNG, l.getLon());
    	return mDb.insert(LOCATION_TABLE, null, initialValues);
    }
    
    public long createEvent(EventDetails event) {
        ContentValues initialValues = new ContentValues();

      //  initialValues.put(KEY_EVENTID, event.getEventID());
        initialValues.put(COLUMN_DAY, event.getDay());
        initialValues.put(KEY_SITUATIONID, event.getSituationID());
        initialValues.put(COLUMN_MONTH, event.getMonth());
        initialValues.put(COLUMN_YEAR, event.getYear());
        initialValues.put(COLUMN_STARTMINUTE, event.getStartMinute());
        initialValues.put(COLUMN_STARTHOUR, event.getStartHour());
        initialValues.put(COLUMN_ENDMINUTE, event.getEndMinute());
        initialValues.put(COLUMN_ENDHOUR, event.getEndHour());
        
    	return mDb.insert(EVENT_TABLE, null, initialValues);
    }
    public long createBlackList(BlacklistTable bl) {
        ContentValues initialValues = new ContentValues();
        Log.e(TAG, "THIS IS A TEST OF INSERTION OF BLACKLIST with situation ID:"+bl.getSituationID());
        initialValues.put(COLUMN_NUMBER, bl.getNumber());
        initialValues.put(KEY_SITUATIONID, bl.getSituationID());
        
    	return mDb.insert(BLOCKCALLS_TABLE, null, initialValues);
    }
    
    public long createSettings(SettingsTable s) {
        ContentValues initialValues = new ContentValues();
        Log.e(TAG, "THIS IS A TEST OF INSERTION OF SETTINGS");
        initialValues.put(COLUMN_RINGTONEID, s.getRingtoneID());
        initialValues.put(COLUMN_BLUETOOTH, s.getBluetoothStatus());
        initialValues.put(COLUMN_WIFI, s.getWifiStatus());
        Log.e(TAG, "The brightness value in database create :"+s.getBrightnessValue());
        initialValues.put(COLUMN_BRIGHTNESS, s.getBrightnessValue());
        initialValues.put(KEY_SITUATIONID, s.getSituationID());
        Log.e(TAG, "THIS IS A TEST OF INSERTION OF settings");
        
    	return mDb.insert(SETTINGS_TABLE, null, initialValues);
    }
    
    /*private static final String SITUATION_TABLE_CREATE =  "create table Situations ("+KEY_SITUATIONID+" integer primary key autoincrement, " + 
            KEY_SETTINGID + " integer, " +
            KEY_LOCATIONID + " integer, " +
            KEY_TIMEID + " integer,"+
            "FOREIGN KEY("+KEY_SETTINGID+")"+"references Settings"+"("+COLUMN_SETTINGSID+") ON UPDATE CASCADE ON DELETE RESTRICT,"+
            "FOREIGN KEY("+KEY_LOCATIONID+")"+"references Settings"+"("+KEY_ROWID+") ON UPDATE CASCADE ON DELETE RESTRICT"+
            
            ");";*/
    public long createSituation(SituationTable s) {
        ContentValues initialValues = new ContentValues();
        Log.e(TAG, "THIS IS A TEST OF INSERTION OF SITUATION");
        //initialValues.put(KEY_SITUATIONID, 1);
        initialValues.put(KEY_SITUATION, s.getSituationName());
    	long l = mDb.insert(SITUATION_TABLE, null, initialValues);
        return l;
    }
    /**
     * Update the location using the details provided. The location to be updated is
     * specified using the rowId, and it is altered to use the title and body
     * values passed in
     * 
     * @param rowId id of location to update
     * @return true if the location was successfully updated, false otherwise
     */
    public boolean updateLocation(long rowId, LocationValues l){
        ContentValues args = new ContentValues();
        args.put(KEY_SITUATIONID,l.getSituationID());
        args.put(KEY_BODY, l.getBody());
        args.put(KEY_LAT, l.getLat());
        args.put(KEY_LNG, l.getLon());
        args.put(KEY_ADDR, l.getAddr());
        args.put(KEY_RADIUS, l.getRange());
        args.put(KEY_EVENT, l.getEvent());
        return mDb.update(LOCATION_TABLE, args, KEY_SITUATIONID + "=" + rowId, null) > 0;
    }
    
    
    public boolean updateEvent(long rowId, EventDetails event){
    	ContentValues initialValues = new ContentValues();
        //Log.e(TAG, "THIS IS A TEST OF INSERTION OF LOCATION");
       // initialValues.put(KEY_EVENTID, event.getEventID());
    	initialValues.put(KEY_SITUATIONID, event.getSituationID());
        initialValues.put(COLUMN_DAY, event.getDay());
        initialValues.put(COLUMN_MONTH, event.getMonth());
        initialValues.put(COLUMN_YEAR, event.getYear());
        initialValues.put(COLUMN_STARTMINUTE, event.getStartMinute());
        initialValues.put(COLUMN_STARTHOUR, event.getStartHour());
        initialValues.put(COLUMN_ENDMINUTE, event.getEndMinute());
        initialValues.put(COLUMN_ENDHOUR, event.getEndHour());
        return mDb.update(EVENT_TABLE, initialValues, KEY_EVENTID + "=" + rowId, null) > 0;
    }
    public boolean updateBlackList(long rowId, BlacklistTable bl){
        ContentValues args = new ContentValues();
        args.put(COLUMN_NUMBER, bl.getNumber());
        args.put(KEY_SITUATIONID, bl.getSituationID());
          return mDb.update(BLOCKCALLS_TABLE, args, KEY_SITUATIONID + "=" + rowId, null) > 0;   
    }
    /*
    private static final String SETTINGS_TABLE_CREATE =
            "create table Settings ("+COLUMN_SETTINGSID+" integer primary key autoincrement, " + 
            COLUMN_RINGTONEID + " text, " +
            COLUMN_BLUETOOTH + " integer, " +
            COLUMN_WIFI + " integer, " +
            COLUMN_BRIGHTNESS + " real, " +
            COLUMN_BLACKLISTid + " integer" +
            "FOREIGN KEY("+COLUMN_BLACKLISTid+")"+"references Blockcalls"+"("+COLUMN_BLACKLISTid+") ON UPDATE CASCADE ON DELETE RESTRICT"+
            
            		");";
        */
    public boolean updateSettings(long rowId, SettingsTable s){
        ContentValues args = new ContentValues();
        args.put(KEY_SITUATIONID, s.getSituationID());
    	
        args.put(COLUMN_RINGTONEID, s.getRingtoneID());
        args.put(COLUMN_BLUETOOTH, s.getBluetoothStatus());
        args.put(COLUMN_WIFI, s.getWifiStatus());
        args.put(COLUMN_BRIGHTNESS, s.getBrightnessValue());
        //args.put(COLUMN_BLACKLISTid, s.getBlacklistID());
    	 return mDb.update(SETTINGS_TABLE, args, KEY_SITUATIONID + "=" + rowId, null) > 0;   
    }
    public boolean updateSituations(long rowId, SituationTable s){
        ContentValues args = new ContentValues();
        args.put(KEY_SITUATION, s.getSituationName()); 
        return mDb.update(SITUATION_TABLE, args, KEY_SITUATIONID + "=" + rowId, null) > 0;   
    }
    
    
  
    /**
     * Delete the row with the given rowId
     * 
     * @param rowId id of each table to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteLocation(long rowId) {
    	Log.e(TAG,"inside delete location");
        return mDb.delete(LOCATION_TABLE, KEY_SITUATIONID + "=" + rowId, null) > 0;
    }
    public boolean deleteBlockedCall(long rowId) {
    	Log.e(TAG,"inside delete block call");
        return mDb.delete(BLOCKCALLS_TABLE, KEY_SITUATIONID + "=" + rowId, null) > 0;
    }
    public boolean deleteSettings(long rowId) {
    	Log.e(TAG,"inside delete settings");
        return mDb.delete(SETTINGS_TABLE, KEY_SITUATIONID + "=" + rowId, null) > 0;
    }
    public boolean deleteSituations(long rowId) {
    	Log.e(TAG,"inside delete situation");
        return mDb.delete(SITUATION_TABLE, KEY_SITUATIONID + "=" + rowId, null) > 0;
    }
    /**
     * Return a Cursor over the list of all locations in the database
     * 
     * @return Cursor over all location
     */
    public Cursor fetchAllLocations() throws SQLException {
    	Log.e(TAG, "THIS IS A TEST OF Fetching the LOCATION");
        
        return mDb.query(LOCATION_TABLE, 
        		new String[] { KEY_SITUATIONID,  KEY_ADDR, KEY_BODY, KEY_LAT, KEY_LNG,
        		 KEY_RADIUS, KEY_EVENT}, 
        		null, null, null, null, null); 
    }
   
    public Cursor fetchAllSituations() throws SQLException {
    	
        return mDb.query(SITUATION_TABLE, 
        		new String[] { KEY_SITUATIONID,  KEY_SITUATION /*, KEY_TIMEID*/}, 
        		null, null, null, null, null); 
    }

    /*
    private static final String SETTINGS_TABLE_CREATE =
            "create table Settings ("+COLUMN_SETTINGSID+" integer primary key autoincrement, " + 
            COLUMN_RINGTONEID + " text, " +
            COLUMN_BLUETOOTH + " integer, " +
            COLUMN_WIFI + " integer, " +
            COLUMN_BRIGHTNESS + " real, " +
            COLUMN_BLACKLISTid + " integer" +
            "FOREIGN KEY("+COLUMN_BLACKLISTid+")"+"references Blockcalls"+"("+COLUMN_BLACKLISTid+") ON UPDATE CASCADE ON DELETE RESTRICT"+
            
            		");";
        */
    public Cursor fetchAllSettings() throws SQLException {

        return mDb.query(SETTINGS_TABLE, 
        		new String[] {COLUMN_RINGTONEID, COLUMN_BLUETOOTH, COLUMN_WIFI, COLUMN_BRIGHTNESS,
        		KEY_SITUATIONID}, 
        		null, null, null, null, null); 
    }
    public Cursor fetchAllContactsToBlock() throws SQLException {

        return mDb.query(BLOCKCALLS_TABLE, 
        		new String[] {KEY_SITUATIONID,COLUMN_BLACKLISTid,COLUMN_NUMBER}, 
        		null, null, null, null, null); 
    }
/*
    public Cursor fetchAllContactsToBlock() throws SQLException {

        return mDb.query(SETTINGS_TABLE, 
        		new String[] {COLUMN_RINGTONEID, COLUMN_BLUETOOTH, COLUMN_WIFI, COLUMN_BRIGHTNESS,
        		KEY_SITUATIONID}, 
        		null, null, null, null, null); 
    }
*/
    /*
    *//**
     * Return a Cursor over the list of all locations in the database over specified
     * 
     * @return Cursor over all locations
     */
    public Cursor fetchAllLocations(String[] fields) throws SQLException {

        return mDb.query(LOCATION_TABLE, 
        		fields, 
        		null, null, null, null, null); // show enabled Location at top of list
    }
    
    
    /**
     * Return a Cursor positioned at the location id that matches the given rowId
     * 
     * @param rowId id of location to retrieve
     * @return Cursor positioned to matching location, if found
     * @throws SQLException if location could not be found/retrieved
     */
    public Cursor fetchLocation(long rowId) throws SQLException {
    	Log.e(TAG, "inside secth");
    	Cursor mCursor = mDb.query(true, LOCATION_TABLE, new String[] {KEY_SITUATIONID, KEY_BODY,
                     KEY_LAT, KEY_LNG, KEY_ADDR, 
                     KEY_RADIUS, KEY_EVENT}, 
                     KEY_SITUATIONID + "=" + rowId, null, null, null, null, null);
        return mCursor;

    }
    
    /*private static final String SITUATION_TABLE_CREATE =  "create table Situations ("+KEY_SITUATIONID+" integer primary key autoincrement, " + 
            KEY_SETTINGID + " integer, " +
            KEY_LOCATIONID + " integer, " +
            KEY_TIMEID + " integer,"+
            "FOREIGN KEY("+KEY_SETTINGID+")"+"references Settings"+"("+COLUMN_SETTINGSID+") ON UPDATE CASCADE ON DELETE RESTRICT,"+
            "FOREIGN KEY("+KEY_LOCATIONID+")"+"references Settings"+"("+KEY_ROWID+") ON UPDATE CASCADE ON DELETE RESTRICT"+
            
            ");";*/
  
    public Cursor fetchSituation(long rowId) throws SQLException {

        Cursor mCursor =

            mDb.query(true, SITUATION_TABLE, new String[] {KEY_SITUATIONID,
            		KEY_SITUATION /*,KEY_TIMEID*/ 
                     }, 
                    KEY_SITUATIONID + "=" + rowId, null,
                    null, null, null, null);
        
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        
        return mCursor;

    }

    /*
    private static final String SETTINGS_TABLE_CREATE =
            "create table Settings ("+COLUMN_SETTINGSID+" integer primary key autoincrement, " + 
            COLUMN_RINGTONEID + " text, " +
            COLUMN_BLUETOOTH + " integer, " +
            COLUMN_WIFI + " integer, " +
            COLUMN_BRIGHTNESS + " real, " +
            COLUMN_BLACKLISTid + " integer" +
            "FOREIGN KEY("+COLUMN_BLACKLISTid+")"+"references Blockcalls"+"("+COLUMN_BLACKLISTid+") ON UPDATE CASCADE ON DELETE RESTRICT"+
            
            		");";
        */
    public Cursor fetchSettings(long rowId) throws SQLException {

        Cursor mCursor =

            mDb.query(true, SETTINGS_TABLE, new String[] {KEY_SITUATIONID,
            		COLUMN_RINGTONEID, COLUMN_BLUETOOTH, COLUMN_WIFI, 
            		COLUMN_BRIGHTNESS}, 
            		KEY_SITUATIONID + "=" + rowId, null,
                    null, null, null, null);
        
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        
        return mCursor;

    }
    /*private static final String BLOCKCALLS_TABLE_CREATE =
            "create table Blockcalls ("+COLUMN_id_BLACKLIST+
            " integer primary key autoincrement"+COLUMN_BLACKLISTid+" integer, " + 
            COLUMN_NUMBER + " text)";
    
    */
    public Cursor fetchBlackList(long rowId) throws SQLException {
    	
        Cursor mCursor =

            mDb.query(true, BLOCKCALLS_TABLE, new String[] {KEY_SITUATIONID,
            		 COLUMN_NUMBER}, 
            		KEY_SITUATIONID + "=" + rowId, null,
                    null, null, null, null);
        
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        Log.e(TAG, "inside fetchblaclist");
        return mCursor;

    }
    
    
    public boolean deleteEvent(long rowId) {

        return mDb.delete(EVENT_TABLE, KEY_SITUATIONID + "=" + rowId, null) > 0;
    }

    public Cursor fetchAllEvents() throws SQLException {
   // 	Log.e(TAG, "THIS IS A TEST OF Fetching the LOCATION");
        
        return mDb.query(EVENT_TABLE, 
        		new String[] { KEY_EVENTID, KEY_SITUATIONID, COLUMN_DAY,  COLUMN_MONTH, COLUMN_YEAR, COLUMN_STARTHOUR, COLUMN_STARTMINUTE,
        		 COLUMN_ENDHOUR, COLUMN_ENDMINUTE}, 
        		null, null, null, null, null); 
    }
    
    public Cursor fetchEvent(Long rowID) throws SQLException {
    	Cursor mCursor = mDb.query(true, EVENT_TABLE, new String[] {KEY_EVENTID, KEY_SITUATIONID, COLUMN_DAY,  COLUMN_MONTH, COLUMN_YEAR, COLUMN_STARTHOUR, COLUMN_STARTMINUTE,
       		 COLUMN_ENDHOUR, COLUMN_ENDMINUTE}, KEY_SITUATIONID +"=" + rowID, null, null, null, null, null);
    	 if (mCursor != null) {
             mCursor.moveToFirst();
         }
         
         return mCursor;

    }
    
    
    
}
