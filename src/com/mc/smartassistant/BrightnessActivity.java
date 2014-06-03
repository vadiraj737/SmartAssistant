package com.mc.smartassistant;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings.SettingNotFoundException;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class BrightnessActivity extends Activity {

	int curBrightnessValue = 0;
	int prevBrightnessValue =0;
	private void setBrightness(int value)
	{
		//set the system brightness using the brightness variable value  
		android.provider.Settings.System.putInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS, value);  
          
        //preview brightness changes at this window  
        //get the current window attributes  
        WindowManager.LayoutParams layoutpars = getWindow().getAttributes();  
        //set the brightness of this window
       // Toast.makeText(getBaseContext(), "brightness value set before clicking:"+prevBrightnessValue, Toast.LENGTH_LONG).show();
	        
        layoutpars.screenBrightness = value / (float)255;  
        //apply attribute changes to this window  
        getWindow().setAttributes(layoutpars);
       // Toast.makeText(getBaseContext(), "brightness value set after clicking:"+prevBrightnessValue, Toast.LENGTH_LONG).show();
	        
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
				
		try {
			prevBrightnessValue = android.provider.Settings.System.getInt(
					getContentResolver(),
			android.provider.Settings.System.SCREEN_BRIGHTNESS);
			//Toast.makeText(getBaseContext(), "prev brightness value:"+prevBrightnessValue, Toast.LENGTH_LONG).show();
		        
		} catch (SettingNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		setContentView(R.layout.activity_brightness);
		Button UpdateSystemSetting = (Button) findViewById(R.id.buttonbrightness);
		Bundle extras = getIntent().getExtras();
		int reqCode = extras.getInt("requestCode");
		UpdateSystemSetting.setOnClickListener(new Button.OnClickListener() {
		
			public void onClick(View arg0) {
			
				setBrightness(prevBrightnessValue);
                Intent intent = new Intent(BrightnessActivity.this,
						SituationActivity.class);

				intent.putExtra("brightnessValue", curBrightnessValue);
				BrightnessActivity.this.setResult(RESULT_OK,intent);
				BrightnessActivity.this.finish();

			}
		});
		SeekBar sb = (SeekBar) findViewById(R.id.brightnessBar);
		sb.setMax(255);
		try {
			if(reqCode == 1)
			{
				curBrightnessValue = android.provider.Settings.System.getInt(
						getContentResolver(),
				android.provider.Settings.System.SCREEN_BRIGHTNESS);
				sb.setProgress(curBrightnessValue);
				sb.setKeyProgressIncrement(1);
			}
			else if(reqCode == 2)
			{
				sb.setProgress(extras.getInt("brightness"));
				sb.setKeyProgressIncrement(1);
			}
			
			
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}

		sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			public void onProgressChanged(SeekBar v, int progress,
					boolean isUser) {
				

				if(progress<=20)  
                {  
                    //set the brightness to 20  
					curBrightnessValue=20;  
                }  
                else //brightness is greater than 20  
                {  
                    //sets brightness variable based on the progress bar   
                	curBrightnessValue = progress;  
                }  
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				setBrightness(curBrightnessValue);
			}
		});
	}

}
