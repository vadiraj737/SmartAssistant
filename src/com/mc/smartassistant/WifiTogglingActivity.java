package com.mc.smartassistant;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

public class WifiTogglingActivity extends Activity {

	boolean wifi;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wifi_toggling);
		/*WifiManager wifiManager = (WifiManager) getBaseContext()
				.getSystemService(Context.WIFI_SERVICE);*/
		ToggleButton wifiButton = (ToggleButton)findViewById(R.id.wifitoggleButton);
		
		wifiButton.setTextOff("OFF");
		wifiButton.setTextOn("ON");
		/*if(wifiManager.isWifiEnabled())
		{
			wifiButton.setChecked(true);
			wifi = true;
			Toast.makeText(getBaseContext(), "1"+"and "+wifi, Toast.LENGTH_LONG).show();
			
		}
		else
		{
			wifiButton.setChecked(false);
			wifi = false;
			Toast.makeText(getBaseContext(), "2"+"and "+wifi, Toast.LENGTH_LONG).show();
			
		}*/
		Button button = (Button) findViewById(R.id.wifitoggleButton);
		button.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				wifi = !wifi;
				//Toast.makeText(getBaseContext(), "toogled to "+wifi, Toast.LENGTH_LONG).show();
			}

		});
		Button done = (Button) findViewById(R.id.setwifibutton);
		done.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				/*WifiManager wifiManager = (WifiManager)getBaseContext().getSystemService(Context.WIFI_SERVICE);
				if(wifi)
				{
					wifiManager.setWifiEnabled(true);
					Toast.makeText(getBaseContext(), "true", Toast.LENGTH_LONG).show();
					
				}
				else
				{
					wifiManager.setWifiEnabled(false);
					Toast.makeText(getBaseContext(), "false", Toast.LENGTH_LONG).show();
					
				}
			*/
				Intent intent = new Intent(WifiTogglingActivity.this, SituationActivity.class);
				intent.putExtra("wifi", wifi);
			    WifiTogglingActivity.this.setResult(RESULT_OK, intent);
			    WifiTogglingActivity.this.finish();
			}

		});

	}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	
        getMenuInflater().inflate(R.menu.activity_wifi_toggling, menu);
        return true;
    }
}
