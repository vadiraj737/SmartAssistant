package com.mc.smartassistant;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

@SuppressLint("HandlerLeak")
public class DummyRingtoneActivity extends Activity{
	private static final int DELAYED_MESSAGE = 500;
	private Handler handler;

	Ringtone rt;
	RingtoneManager mRingtoneManager;
	TextView text;
	Button ringtoneSelect;
	Cursor mcursor;
	Intent Mringtone;
	String title;
	boolean endStatus;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_ringtone);
		mRingtoneManager = new RingtoneManager(this);
		handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == DELAYED_MESSAGE) {
                	DummyRingtoneActivity.this.finish();
                }
                super.handleMessage(msg);
            }
        };
        Intent ringtoneIntent = this.getIntent();
        Bundle extras = ringtoneIntent.getExtras();
		String uriString = extras.getString("title");
		endStatus= extras.getBoolean("end");
		
		
		
		if(uriString.equals("android.intent.extra.ringtone.TITLE"))
		{
			getBaseContext();
			AudioManager audio_mngr = (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);
			audio_mngr.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		}
		else
		{
			getBaseContext();
			AudioManager audio_mngr = (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);
			audio_mngr.setRingerMode(AudioManager.RINGER_MODE_NORMAL);	
		Log.e("DUMMY", "recieved uri at the dummy:"+uriString);
		Uri uri = Uri.parse(uriString);
		
		RingtoneManager.setActualDefaultRingtoneUri(this, -1,
				uri);
		
		}
		if(endStatus)
		{
		//	Log.e(tag, msg)
			if(uriString.equals("android.intent.extra.ringtone.TITLE"))
			{
				getBaseContext();
				AudioManager audio_mngr = (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);
				audio_mngr.setRingerMode(AudioManager.RINGER_MODE_NORMAL);	
				Uri uri = Uri.parse("content://settings/system/ringtone");
				RingtoneManager.setActualDefaultRingtoneUri(this, -1,uri);
			}
			else
			{
				Uri uri = Uri.parse("content://settings/system/ringtone");
				RingtoneManager.setActualDefaultRingtoneUri(this, -1,uri);
			}
			
		}
		Message message = handler.obtainMessage(DELAYED_MESSAGE);
        //this next line is very important, you need to finish your activity with slight delay
        handler.sendMessageDelayed(message,1000);
	}
	
}

