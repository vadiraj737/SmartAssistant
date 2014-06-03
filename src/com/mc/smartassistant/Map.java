package com.mc.smartassistant;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class Map extends MapActivity{
	GeoPoint p;
	static double lat,lon;
	private MapView mapView;
	private MapController mapController;
	private LocationManager locManager;
	private LocationListener locListener;
	private MyLocationOverlay myLocOverlay;
	
	private BookmarkDbAdapter bmarkDbHelper;

	// min distance interval (in meters) to update GPS location coordinates
	public static final float DIST_INTERVAL = 100;
	// min time (in milliseconds) to update GPS location coordinates
	public static final long TIME_INTERVAL = 10000;

	
	public static final int DEFAULT_ZOOM = 12;
	// keeps track of the current zoom level set by the user
	private int zoomLevel = 0;
	class MapOverlay extends com.google.android.maps.Overlay
	{
		/*@Override
        public boolean draw(Canvas canvas, MapView mapView, 
        boolean shadow, long when) 
        {
            super.draw(canvas, mapView, shadow);                   
            Log.e("Maps Tag", "This is of animating point");
            //---translate the GeoPoint to screen pixels---
            Point screenPts = new Point();
            mapView.getProjection().toPixels(p, screenPts);
            Log.e("Maps Tag", "This is of animating point 2");
            
            //---add the marker---
            Bitmap bmp = BitmapFactory.decodeResource(
                getResources(), R.drawable.pushpin);  
            Log.e("Maps Tag", "This is of animating point 3");
            
            canvas.drawBitmap(bmp, screenPts.x, screenPts.y-50, null);
            Log.e("Maps Tag", "This is of animating point til d end");
            return true;
        }*/
		@Override
		public boolean onTouchEvent(MotionEvent event, MapView mapView) 
		{   
			Bundle extras = getIntent().getExtras();
			//---when user lifts his finger---
			if (event.getAction() == 1) {                
				GeoPoint p = mapView.getProjection().fromPixels(
						(int) event.getX(),
						(int) event.getY());
				/*  Toast.makeText(getBaseContext(), 
                        p.getLatitudeE6() / 1E6 + "," + 
                        p.getLongitudeE6() /1E6 , 
                        Toast.LENGTH_SHORT).show();*/
				lat=p.getLatitudeE6() / 1E6;
				lon=p.getLongitudeE6() /1E6;
				saveSelection(extras,lat,lon);
			}                            
			return false;
		}

	} 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.map_menu, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add_location: 
			AlertDialog confirmSelection = createSelectionConfirmAlert();
			confirmSelection.show();
			break;
		}
		return true;
	}
	/** Called when the activity is first created. */
	@SuppressWarnings("unused")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_me);
		Bundle extras = getIntent().getExtras();     
		bmarkDbHelper = new BookmarkDbAdapter(this);
		bmarkDbHelper.open();

		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapController = mapView.getController();
		//zoomLevel = DEFAULT_ZOOM;
		
		
		/*createBookmarkOverlays();*/

		MapOverlay mapOverlay = new MapOverlay();
		List<Overlay> listOfOverlays = mapView.getOverlays();
		listOfOverlays.clear();
		listOfOverlays.add(mapOverlay);
		
		// shows my location on the map
		
		// initialize loc manager and loc listener
		initLocationService();
		createMyLocationOverlay();
	}

	private void initLocationService(){

		locManager = (LocationManager) 
		getSystemService(Map.LOCATION_SERVICE);

		locListener = new LocationListener()
		{
			public void onLocationChanged(Location myLoc) {
				showLocation(myLoc);
			}
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
	private void saveSelection(Bundle extras, double latitude,double  longitude){
		extras = getIntent().getExtras();
		Intent i;
		if(extras!=null){
			i = new Intent(this, LocationEdit.class);		
			i.putExtra("lat", latitude);
			i.putExtra("lon", longitude);
			//set the result code
			this.setResult(RESULT_OK, i);
		}
		else{
			i = new Intent(this, BookmarkEdit.class);
			i.putExtra("lat", latitude);

			i.putExtra("lon", longitude);
			//set the result code

			this.setResult(RESULT_OK, i);

		}
	}
	//alert dialog box is created
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
						Map.this.finish();		        	   
					}
				})
				;    		       

		return builder.create();
	}

	/**
	 * Animates map to my current location
	 * @param myLoc
	 */
	private void showLocation(Location myLoc) {
		if(myLoc != null){			
			GeoPoint me = getGeoPoint(myLoc.getLatitude(), myLoc.getLongitude());			
		//	Toast.makeText(getBaseContext(), "New Loc", Toast.LENGTH_LONG).show();			
			mapController.animateTo(me);			
			mapController.setZoom(mapView.getZoomLevel());
		}
	}

	/**
	 * Shows my location on the map
	 * 
	 */
	private void createMyLocationOverlay(){
		//Toast.makeText(getBaseContext(), "New Loc overlay", Toast.LENGTH_LONG).show();			
		
		myLocOverlay = new MyLocationOverlay(this, mapView);
		mapView.getOverlays().add(myLocOverlay);
		mapView.postInvalidate();
	}
	/**
	 * Shows icons where each bookmark is set for on the map
	 * 
	 */
	/*private void createBookmarkOverlays(){

		Drawable marker = this.getResources().getDrawable(R.drawable.bmark);

		marker.setBounds(0,0,marker.getIntrinsicWidth(), marker.getIntrinsicHeight());

		BookmarkOverlay itemizedoverlay = new BookmarkOverlay(marker, this);

		Cursor bookmarkCursor = bmarkDbHelper.fetchAllBookmarks(); 

		startManagingCursor(bookmarkCursor);

		while(bookmarkCursor.moveToNext()){

			String name = bookmarkCursor.getString(
					bookmarkCursor.getColumnIndexOrThrow(BookmarkDbAdapter.KEY_NAME));

			double lat = bookmarkCursor.getDouble(
					bookmarkCursor.getColumnIndexOrThrow(BookmarkDbAdapter.KEY_LAT));

			double lon = bookmarkCursor.getDouble(
					bookmarkCursor.getColumnIndexOrThrow(BookmarkDbAdapter.KEY_LON));

			String addr = bookmarkCursor.getString(
					bookmarkCursor.getColumnIndexOrThrow(BookmarkDbAdapter.KEY_ADDR));

			GeoPoint point = getGeoPoint(lat, lon);

			OverlayItem overlayitem = new OverlayItem(point, name, addr);

			itemizedoverlay.addOverlay(overlayitem);
		}

		if(itemizedoverlay.size() > 0){
			mapView.getOverlays().add(itemizedoverlay);
		}

	}	
*/

	/**
	 * Converts lat/lon pair into GeoPoint
	 * @param lat
	 * @param lon
	 * @return GeoPoint
	 */
	private GeoPoint getGeoPoint(double lat, double lon){

		Double d_lat = lat*1E6;
		Double d_lon = lon*1E6;

		GeoPoint p = new GeoPoint(d_lat.intValue(), d_lon.intValue());

		return p;
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}     

	@Override
	protected boolean isLocationDisplayed(){
		return myLocOverlay.isMyLocationEnabled();
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i("MapMe", "ON PAUSE CALLED");
		// stop listening for loc updates
		locManager.removeUpdates(locListener);
		// stop detecting/updating my current location
		myLocOverlay.disableMyLocation();
	}


	@Override
	protected void onResume() {
		super.onResume();
		Log.i("MapMe", "ON RESUME CALLED");

		if(bmarkDbHelper == null){
			bmarkDbHelper.open();
		}

		Location lastLoc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		showLocation(lastLoc);
		locManager.requestLocationUpdates(
				LocationManager.GPS_PROVIDER, 
				TIME_INTERVAL, // min time in ms between location updates
				DIST_INTERVAL, // min distance in meters 
				locListener);
		myLocOverlay.enableMyLocation(); // register for location updates from location service
		myLocOverlay.runOnFirstFix(new Runnable(){
			public void run(){
				mapController.setCenter(myLocOverlay.getMyLocation());
			}
		});

		// restore zoom level if setup or set to default
		if(zoomLevel == 0){
			zoomLevel = DEFAULT_ZOOM;
		}else{
			zoomLevel = mapView.getZoomLevel();
		}
	}

	@Override
	protected void onDestroy(){

		super.onDestroy();
		Log.i("MapMe", "ON DESTROY CALLED");
		myLocOverlay.disableMyLocation();

		if(bmarkDbHelper != null){
			bmarkDbHelper.close();
		}
	}


	@SuppressWarnings("rawtypes")
	public class BookmarkOverlay extends ItemizedOverlay{

		private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();

		private Context mContext;

		public BookmarkOverlay(Drawable defaultMarker, Context context) {
			//super(defaultMarker);
			super(boundCenterBottom(defaultMarker));
			mContext = context;
		}

		@Override
		protected boolean onTap(int index) {
			OverlayItem item = mOverlays.get(index);
			AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
			dialog.setTitle(item.getTitle());
			dialog.setMessage(item.getSnippet());
			dialog.show();
			return true;
		}

		public void addOverlay(OverlayItem overlay) {
			mOverlays.add(overlay);
			populate();
		}

		@Override
		protected OverlayItem createItem(int i) {
			return mOverlays.get(i);
		}

		@Override
		public int size() {
			// TODO Auto-generated method stub
			return mOverlays.size();
		}

	}

}