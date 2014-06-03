package com.mc.smartassistant;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class RingtoneActivity extends Activity implements OnClickListener {

	Ringtone rt;
	RingtoneManager mRingtoneManager;
	TextView text;
	Button ringtoneSelect;
	Cursor mcursor;
	Intent Mringtone;
	String title;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ringtone);
		mRingtoneManager = new RingtoneManager(this);
		mcursor = mRingtoneManager.getCursor();
		title = RingtoneManager.EXTRA_RINGTONE_TITLE;
		text = (TextView) findViewById(R.id.ringtoneTextView);
		ringtoneSelect = (Button) findViewById(R.id.ringtoneSelect);
		ringtoneSelect.setOnClickListener(this);
		Button setRingtone = (Button) findViewById(R.id.setRingtone);
		setRingtone.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(RingtoneActivity.this,
						SituationActivity.class);
				if(title == null)
				{
					RingtoneActivity.this.setResult(RESULT_CANCELED, intent);
					RingtoneActivity.this.finish();
				}
				intent.putExtra("RingtoneName", title);
				//Toast.makeText(getBaseContext(), "" + title, Toast.LENGTH_LONG).show();

				RingtoneActivity.this.setResult(RESULT_OK, intent);
				RingtoneActivity.this.finish();
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.item1:
			if (rt.isPlaying()) {
				rt.stop();
				
			}
			finish();
			break;
		}
		return true;
	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent Mringtone) {
		switch (resultCode) {
		/*
		* 
		*/
		case RESULT_OK:
			// sents the ringtone that is picked in the Ringtone Picker Dialog
			Uri uri = Mringtone
					.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

			// send the output of the selected to a string
			//String test = uri.toString();

			// the program creates a "line break" when using the "\n" inside a
			// string value
			//text.setText("\n " + test + "\n " + title);

			// prints out the result in the console window
			Log.i("Sample", "uri " + uri);

			// this passed the ringtone selected from the user to a new method
			play(uri);

			// inserts another line break for more data, this times adds the
			// cursor count on the selected item
			//text.append("\n " + mcursor.getCount());

			// set default ringtone
			try {
				mRingtoneManager.stopPreviousRingtone();
				/*RingtoneManager.setActualDefaultRingtoneUri(this, resultCode,
						uri);
		*/	} catch (Exception localException) {

			}
			break;

		}

	}

	private void play(Uri uri) {
		// TODO Auto-generated method stub
		if (uri != null) {

			// in order to play the ringtone, you need to create a new Ringtone
			// with RingtoneManager and pass it to a variable
			rt = RingtoneManager.getRingtone(this, uri);
			rt.play();
			//title = rt.getTitle(getBaseContext());
			title = uri.toString();
			Log.e("Ringtone Activity", "The uri saved in db is:"+title);
			
		}
	}

	@SuppressWarnings("unused")
	public void onClick(View v) {
		Mringtone = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
		Mringtone.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,
				RingtoneManager.TYPE_RINGTONE);
		Mringtone.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE,
				"Pick the Ringtone");
		Mringtone.getBooleanExtra(RingtoneManager.EXTRA_RINGTONE_INCLUDE_DRM,
				true);
		String uri = null;
		if (uri != null) 
		{
			Mringtone.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,Uri.parse(uri)); } 
		else {
			Mringtone.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
					(Uri) null);
		}
		startActivityForResult(Mringtone, 0);

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(rt == null)
			return;
		if (rt.isPlaying()) {
			rt.stop();
		} else {

		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

	}

}
