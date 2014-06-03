package com.mc.smartassistant;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.app.lib.Bookmark;
import com.mc.smartassistant.R;

public class BookmarkList extends ListActivity {
    
    /**
     * Activity intent request code to identify sent/received intents
     * 
     */
    private static enum Message{
    	EDIT_BOOKMARK, NEW_BOOKMARK   	
    }
    private BookmarkDbAdapter bmarkDbHelper;
    private static final String TAG = "BookmarkList";
    private LocationManager locManager;
    private LocationListener locListener;
	@SuppressWarnings("unused")
	private Location myLocation;
	
	// min distance interval (in meters) to update GPS location coordinates
    public static final float DIST_INTERVAL = 400;
    // min time (in milliseconds) to update GPS location coordinates
    public static final long TIME_INTERVAL = 60000;
        
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookmark_list);
        setTitle(R.string.bookmark_list);

        initLocationService(); // setup location service
        // initial value of myLocation
        myLocation = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        
        bmarkDbHelper = new BookmarkDbAdapter(this);
        bmarkDbHelper.open();
        fillData();

        //registerForContextMenu(getListView());
              
        Button addBookmark = (Button) findViewById(R.id.add_loc);
        
        addBookmark.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
            	addBookmark();
            }
        });

    }
    
    private void fillData() {
    	// Get all of the rows from the database and create the item list
        Cursor bookmarkCursor = bmarkDbHelper.fetchAllBookmarks();
        startManagingCursor(bookmarkCursor);
        
        ArrayList<Bookmark> bookmarks = new ArrayList<Bookmark>();
        
        while(bookmarkCursor.moveToNext()){
        	Bookmark bmark = new Bookmark();
        	long rowId = bookmarkCursor.getLong(
        			bookmarkCursor.getColumnIndexOrThrow(BookmarkDbAdapter.KEY_ROWID));
            String name = bookmarkCursor.getString(
        			bookmarkCursor.getColumnIndexOrThrow(BookmarkDbAdapter.KEY_NAME));
        	String address = bookmarkCursor.getString(
        			bookmarkCursor.getColumnIndexOrThrow(BookmarkDbAdapter.KEY_ADDR));
        	// NOTE: the lat/lon fields must be populated since the bookmark objects are passed
        	// into the BookmarkAdapter which looks for the lat/lon when the directions
        	// button is pressed (it defines the listner for the directions button) 
        	double lat = bookmarkCursor.getDouble(
        			bookmarkCursor.getColumnIndexOrThrow(BookmarkDbAdapter.KEY_LAT));
        	double lon = bookmarkCursor.getDouble(
        			bookmarkCursor.getColumnIndexOrThrow(BookmarkDbAdapter.KEY_LON));
        
        	// populate the bookmark object
        	bmark.setRowId(rowId);
        	bmark.setName(name);
        	bmark.setAddress(address);
        	bmark.setLat(lat);
        	bmark.setLon(lon);
        	bookmarks.add(bmark);
        }
        
        BookmarkAdapter bmarkAdapter = new BookmarkAdapter(this, R.layout.bookmark_entry, bookmarks);
        this.setListAdapter(bmarkAdapter);
    }

           
	/**
	 * Launches the BookmarkEdit activity for creating/editing bookmarks
	 */
	private void addBookmark() {
    	Intent i = new Intent(this, BookmarkEdit.class);
    	// identify the calling activity for BookmarkEdit
    	Log.e(TAG, "caller string: " + BookmarkEdit.Params.CALLER.toString());
    	Log.e(TAG, "caller value: " + BookmarkEdit.Caller.BOOKMARK_LIST.ordinal());
    	i.putExtra(BookmarkEdit.Params.CALLER.toString(), 
    			BookmarkEdit.Caller.BOOKMARK_LIST.ordinal());
    	// pass in -1 as rowid (invalid) since if this is not passed in
    	// when the params are extracted in BookmarkEdit, the value defaults to 0
    	i.putExtra(BookmarkDbAdapter.KEY_ROWID, BookmarkDbAdapter.DEFAULT_INVALID_ROWID);  
    	startActivityForResult(i, Message.NEW_BOOKMARK.ordinal());

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
      super.onCreateContextMenu(menu, v, menuInfo);
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.bookmark_context_menu, menu);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
      AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
      
      switch (item.getItemId()) {
      
      case R.id.edit_bookmark:
        editBookmark(info.id);
        return true;
      
      case R.id.delete_bookmark:
        deleteBookmark(info.id);
        return true;
      
      default:
        return super.onContextItemSelected(item);
      }
    }
    
    private void deleteBookmark(long id){
        bmarkDbHelper.deleteBookmark(id);
        fillData();
    }
    
    // Creates a confirmation alert when user is selecting the address from
    // the list of bookmarked addresses.
	private AlertDialog createSelectionConfirmAlert(){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		String message = getString(R.string.sel_addr_alert);
		// here methods can be chained since each method returns the AlertBuilder
		// object
		builder.setMessage(
				message)
		       .setCancelable(false)
		       .setPositiveButton(getString(R.string.sel_addr_cancel), new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   dialog.cancel(); 
		           }
		       })
		       .setNegativeButton(getString(R.string.sel_addr_confirm),new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   dialog.dismiss();
		        	   // finish this activity so it can be returned to caller
		        	   BookmarkList.this.finish();		        	   
		           }
		       })
		       ;    		       
		       
		return builder.create();
	}
    
	/**
     * Sends an intent to BookmarkEdit activity to edit the location
     * @param id
     */
    private void editBookmark(long id){
        Intent i = new Intent(this, BookmarkEdit.class);
        i.putExtra(BookmarkDbAdapter.KEY_ROWID, id);
        i.putExtra(BookmarkEdit.Params.CALLER.toString(), 
        		BookmarkEdit.Caller.BOOKMARK_LIST.ordinal());
        startActivityForResult(i, Message.EDIT_BOOKMARK.ordinal());   
    }
 

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	super.onActivityResult(requestCode, resultCode, intent);
    	// this method is called just before onResume. Database handle needs
    	// to be opened here since it was closed by onPause.
    	bmarkDbHelper.open();
    	fillData();
   	
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	Log.i(TAG, "RESUMED");
     	if(bmarkDbHelper == null){
     		bmarkDbHelper.open();
     	}
    	fillData();
    	
    	locManager.requestLocationUpdates(
    			LocationManager.GPS_PROVIDER, 
    			TIME_INTERVAL, // min time in ms between location updates
    			DIST_INTERVAL, // min distance in meters 
    			locListener);
    }
    
    @Override
    protected void onDestroy(){
    	
    	super.onDestroy();
    	Log.i(TAG, "DESTROYED");
    	if(bmarkDbHelper != null){
    		bmarkDbHelper.close();
    	}
    }
    
    
    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "PAUSED");
     	
        // stop listening for loc updates
        locManager.removeUpdates(locListener);
    }
    
    
    // custom adapter for binding data items in ItemFound ArrayList to each list entry
    // and list view
    private class BookmarkAdapter extends ArrayAdapter<Bookmark> {

        private ArrayList<Bookmark> bookmarks;
        
        public BookmarkAdapter(Context context, int textViewResourceId, ArrayList<Bookmark> bmarks) {
                super(context, textViewResourceId, bmarks);
                this.bookmarks = bmarks;
        }

       
        // Returns the database rowId of the specified bookmark
        private long getBookmarkCursorId(Bookmark bmark){
        	return bmark.getRowId();
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                
                if (v == null) {
                    LayoutInflater v_inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = v_inflater.inflate(R.layout.bookmark_entry, null);
                }

               // Button directions_btn = (Button) v.findViewById(R.id.bookmark_directions);
                   
                // define listener for directions button in the place_entry view
                /*directions_btn.setFocusable(true);
                directions_btn.setClickable(true);
                */
                Bookmark b = bookmarks.get(position);
                //directions_btn.setTag(b);
                
                Log.e(TAG, "Selected Bookmark: " + b.getLat() + " " + b.getLon());
             /*   directions_btn.setOnClickListener(new OnClickListener(){
                	@Override
                	public void onClick(View view){
                		Bookmark b = (Bookmark) view.getTag();
                		Log.e(TAG, "dir button on click: " + b.getLat() + " " + b.getLon());
                		Dialog directionsDialog = createDirectionsDialog(b);
                		directionsDialog.show();
                	}                	
                  } //OnClickListener    
                );
               */                 
                // Setting listener for the whole row
                long cursorId = getBookmarkCursorId(bookmarks.get(position));                
                //Log.e(TAG, "Item ID: " + getItemId(position));
            	//Log.e(TAG, "bookmark cursor ID: " + cursorId);                
                v.setOnClickListener(new OnBookmarkClickListener(cursorId));

                
                
                // define how to bind the ItemFound object data to associated view
                b = bookmarks.get(position);
                
                if (b != null){
                        TextView name = (TextView) v.findViewById(R.id.bmark_name);
                        TextView address = (TextView) v.findViewById(R.id.bmark_address);
                                                
                        if(name != null){
                        	name.setText( b.getName() );
                        }
                        if(address != null){
                        	address.setText("Address: "	+ b.getAddress());
                        }
                }               
                
                return v;
        }
		
    }   
    
    // Defines the listener for clicking on a bookmark row in bookmark list view
    private class OnBookmarkClickListener implements OnClickListener{

        private long mCursorRowId;
        OnBookmarkClickListener(long cursorId){
        	mCursorRowId = cursorId;    
        }
    	
		public void onClick(View v) {
	        Bundle extras = getIntent().getExtras();
	        // if extras bundle is included in the activity that started 
	        // this acitivity, then it must be the result of pressing
	        // "frombookmarks" button in locationEdit activity
	        // When user selects an entry, go back to locationEdit
	        if(extras != null){
	        	saveSelection(extras, mCursorRowId);
	        	AlertDialog confirmSelection = createSelectionConfirmAlert();
	        	confirmSelection.show();
	        }else{
	        	// 
	        	editBookmark(mCursorRowId); 
	        } 
		}
    	
    }
    

    /**
     * Extracts the lat, lon, address of the selected bookmark
     * and puts it in a bundle to send off to LocationEdit activity
     * Used when user selects the address to a location from the list
     * of bookmarks 
     * @param extras 
     * @param id database row id of the selected bookmark
     */
    private void saveSelection(Bundle extras, long id){
    	// extract lat, lon, address of the selected entry
    	Cursor bcursor = bmarkDbHelper.fetchBookmark(id);
    	startManagingCursor(bcursor);
    	String address = bcursor.getString(
    			bcursor.getColumnIndexOrThrow(BookmarkDbAdapter.KEY_ADDR));
    	double lat = bcursor.getDouble(
    			bcursor.getColumnIndexOrThrow(BookmarkDbAdapter.KEY_LAT));
    	double lon = bcursor.getDouble(
    			bcursor.getColumnIndexOrThrow(BookmarkDbAdapter.KEY_LON));
    	// put the lat, lon, address into an intent bundle and send it back to the 
    	// calling activity - which is the locationEdit.
    	Intent i = new Intent(this, LocationEdit.class);
    	i.putExtra(BookmarkDbAdapter.KEY_ADDR, address);
    	i.putExtra(BookmarkDbAdapter.KEY_LAT, lat);
    	i.putExtra(BookmarkDbAdapter.KEY_LON, lon);
    	this.setResult(RESULT_OK, i); //set the result code
    	//this.finish();  	
    }
    
   
   
    /**
     * Returns the URL of the google maps directions to the specified
     * bookmark from the user's current location
     * @param bmark bookmark object to which directions are needed
     * @return URL of the google map directions to bookmark from current location
     */
 	private void initLocationService(){
		
		locManager = (LocationManager) 
        getSystemService(BookmarkList.LOCATION_SERVICE);
		
		locListener = new LocationListener()
		{
		    public void onLocationChanged(Location myLoc) {
		   		myLocation = myLoc;
		   	}
		   	// the following abstract methods must be implemented
			public void onProviderDisabled(String provider) {
	            // TODO Auto-generated method stub
	        }
	        public void onProviderEnabled(String provider) {
	            // TODO Auto-generated method stub
	        }
	        public void onStatusChanged(String provider, int status, Bundle extras) {
	            // TODO Auto-generated method stub
	        }
		};
		
	}
    
}
