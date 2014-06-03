package com.mc.smartassistant;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

public class BluetoothTogglingActivity extends Activity {

	boolean bluetooth = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bluetooth_toggling);
		ToggleButton bluetoothButton = new ToggleButton(getBaseContext());
		bluetoothButton.setTextOff("OFF");
		bluetoothButton.setTextOn("ON");
		BluetoothAdapter myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();    
		if (!myBluetoothAdapter.isEnabled())
		{
			bluetoothButton.setChecked(false);
		}
		else
			bluetoothButton.setChecked(true);
		Button button = (Button) findViewById(R.id.bluetoothToggle);
		button.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				bluetooth = !bluetooth;
			}

		});
		Button done = (Button) findViewById(R.id.bluetoothbutton);
		done.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
/*				BluetoothAdapter myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();    
				if(bluetooth)
				{
					if (!myBluetoothAdapter.isEnabled())
					{
						//Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
						myBluetoothAdapter.enable();
						
					    //startActivityForResult(enableBtIntent, 1);
					}
					try {
						Toast.makeText(getBaseContext(), "Turning ON bluetooth, please wait....", Toast.LENGTH_LONG).show();
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						
						e.printStackTrace();
					}
					//Toast.makeText(getBaseContext(), "true", Toast.LENGTH_LONG).show();
					
				}
				else
				{
					myBluetoothAdapter.disable();
					//Toast.makeText(getBaseContext(), "false", Toast.LENGTH_LONG).show();
				}*/
			
				Intent intent = new Intent(BluetoothTogglingActivity.this, SituationActivity.class);
				intent.putExtra("bluetooth", bluetooth);
			    //startActivityForResult(intent, 1);
				BluetoothTogglingActivity.this.setResult(RESULT_OK, intent);
				BluetoothTogglingActivity.this.finish();
			}

		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_bluetooth_toggling, menu);
		return true;
	}
}
