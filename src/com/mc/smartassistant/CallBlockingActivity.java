package com.mc.smartassistant;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;


public class CallBlockingActivity extends Activity {

	ArrayList<ContactRow> contactData;
	BroadcastReceiver CallBlocker;
	TelephonyManager telephonyManager;
	ITelephony telephonyService;
	CheckBox blockAll_cb;
	int contactsPicked=0;
	/**
	 * @param contactData
	 */
	public CallBlockingActivity(ArrayList<ContactRow> contactData) {
		super();
		this.contactData = contactData;
	}

	/**
	 * 
	 */
	public CallBlockingActivity() {
		super();
		// this.contactData = new ArrayList<ContactRow>();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		//Toast.makeText(getBaseContext(), "out", Toast.LENGTH_LONG).show();
		if (resultCode == RESULT_OK) {
			contactData = new ArrayList<ContactRow>();
			// Toast.makeText(getBaseContext(), "inside",
			// Toast.LENGTH_LONG).show();
			int size = data.getIntExtra("contactSize", 0);
			contactsPicked = size;
			//Toast.makeText(getBaseContext(), ""+size+" contacts selected to be blocked",
				//	Toast.LENGTH_LONG).show();
			for (int i = 0; i < size; i++) {
				ContactRow c = new ContactRow();
				c.setName(data.getStringExtra("" + i + "name"));
				c.setChecked(true);
				c.setPhoneNumber(data.getStringExtra(""+i+"number"));
				//Toast.makeText(getBaseContext(), c.getPhoneNumber(),
					//	Toast.LENGTH_LONG).show();
				
				// c.setChecked(data.getBooleanExtra(""+i+"checked", false));
				// Toast.makeText(getBaseContext(), c.getName(),
				// Toast.LENGTH_LONG).show();
				contactData.add(c);
			}
			
			// Bundle bundle = data.getExtras();
			// Bundle contacts = data.getBundleExtra("contactsBundle");
			// contactData= contacts.getParcelableArrayList("contacts");
			// String msg = data.getStringExtra("str");
			// Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_blocking);
	//	Toast.makeText(getBaseContext(), "create", Toast.LENGTH_LONG).show();
		initviews();
		Button setCallBlocking = (Button) findViewById(R.id.setCallBlocking);
		setCallBlocking.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(CallBlockingActivity.this,
						SituationActivity.class);
				intent.putExtra("contactsSize", contactsPicked);
				if(contactData!=null)
				{
					int checkedContactContact = 0;
					for (int i = 0; i < contactData.size(); i++)
		            {
		            	if(contactData.get(i).getChecked())
		            	{
		            		
		            		intent.putExtra(""+checkedContactContact+"name", contactData.get(i).getName());
			            	intent.putExtra(""+checkedContactContact+"checked", contactData.get(i).getChecked());
			            	intent.putExtra(""+checkedContactContact+"number", contactData.get(i).getPhoneNumber());
			            	
			            	//Toast.makeText(getBaseContext(), contactData.get(i).getChecked().toString(), Toast.LENGTH_LONG).show();
			            	//Toast.makeText(getBaseContext(), contactData.get(i).getChecked().toString(), Toast.LENGTH_LONG).show();
			            	checkedContactContact++;
		            	}
		            	
						
		            }
					intent.putExtra("contactSize", checkedContactContact);
					
				}
				
				intent.putExtra("EMPTY_CONTACTS", "No contacts picked");
				CallBlockingActivity.this.setResult(RESULT_OK, intent);
				CallBlockingActivity.this.finish();
			}
		});
		Button pickContact = (Button) findViewById(R.id.callBlockButton);
		pickContact.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(CallBlockingActivity.this,
						ContactListActivity.class);

				startActivityForResult(intent, 1);

			}
		});
		
/*		blockAll_cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				CallBlocker = new BroadcastReceiver() {
					@Override
					public void onReceive(Context context, Intent intent) {

						Toast.makeText(getBaseContext(), "onrecieve",
								Toast.LENGTH_LONG).show();

						telephonyManager = (TelephonyManager) context
								.getSystemService(Context.TELEPHONY_SERVICE);
						// Java Reflections
						@SuppressWarnings("rawtypes")
						Class c = null;
						try {
							c = Class.forName(telephonyManager.getClass()
									.getName());
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
						Method m = null;
						try {
							m = c.getDeclaredMethod("getITelephony");
						} catch (SecurityException e) {
							e.printStackTrace();
						} catch (NoSuchMethodException e) {
							e.printStackTrace();
						}
						m.setAccessible(true);
						try {
							telephonyService = (ITelephony) m
									.invoke(telephonyManager);
						} catch (IllegalArgumentException e) {
						
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						}
						telephonyManager.listen(callBlockListener,
								PhoneStateListener.LISTEN_CALL_STATE);
					}// onReceive()

					PhoneStateListener callBlockListener = new PhoneStateListener() {
						public void onCallStateChanged(int state,
								String incomingNumber) {
							if (state == TelephonyManager.CALL_STATE_RINGING) {
								Toast.makeText(getBaseContext(), "number:"+incomingNumber,
										Toast.LENGTH_LONG).show();
								if (blockAll_cb.isChecked()) {
									try {
										boolean isBlocked = false;
										for (int i = 0; i < contactData.size(); i++) {
											
											if(incomingNumber.equalsIgnoreCase(""+contactData.get(i).getPhoneNumber()))
											{
												Toast.makeText(getBaseContext(), "call blocked from: "+contactData.get(i).getName(),
														Toast.LENGTH_LONG).show();
												isBlocked = true;
												
											}
											
										}
										if(isBlocked)
										telephonyService.endCall();
									} catch (RemoteException e) {
										
										e.printStackTrace();
									}
								}
							}
						}
					};
				};// BroadcastReceiver
				IntentFilter filter = new IntentFilter(
						"android.intent.action.PHONE_STATE");
				registerReceiver(CallBlocker, filter);
			}
		});*/
		
//		// blocking
//		blockCall = (Button) findViewById(R.id.button2);
//		blockCall.setOnClickListener(new View.OnClickListener() {
//
//			public void onClick(View v) {
//
//				Toast.makeText(getBaseContext(), "click",
//						Toast.LENGTH_LONG).show();
//				CallBlocker = new BroadcastReceiver() {
//					@Override
//					public void onReceive(Context context, Intent intent) {
//
//						Toast.makeText(getBaseContext(), "onrecieve",
//								Toast.LENGTH_LONG).show();
//
//						telephonyManager = (TelephonyManager) context
//								.getSystemService(Context.TELEPHONY_SERVICE);
//						// Java Reflections
//						Class c = null;
//						try {
//							c = Class.forName(telephonyManager.getClass()
//									.getName());
//						} catch (ClassNotFoundException e) {
//							e.printStackTrace();
//						}
//						Method m = null;
//						try {
//							m = c.getDeclaredMethod("getITelephony");
//						} catch (SecurityException e) {
//							e.printStackTrace();
//						} catch (NoSuchMethodException e) {
//							e.printStackTrace();
//						}
//						m.setAccessible(true);
//						try {
//							telephonyService = (ITelephony) m
//									.invoke(telephonyManager);
//						} catch (IllegalArgumentException e) {
//							e.printStackTrace();
//						} catch (IllegalAccessException e) {
//							e.printStackTrace();
//						} catch (InvocationTargetException e) {
//							e.printStackTrace();
//						}
//						telephonyManager.listen(callBlockListener,
//								PhoneStateListener.LISTEN_CALL_STATE);
//					}// onReceive()
//
//					PhoneStateListener callBlockListener = new PhoneStateListener() {
//						public void onCallStateChanged(int state,
//								String incomingNumber) {
//							if (state == TelephonyManager.CALL_STATE_RINGING) {
//
//								try {
//									telephonyService.endCall();
//								} catch (RemoteException e) {
//									e.printStackTrace();
//								}
//								
//
//							}
//						}
//					};
//				};// BroadcastReceiver
//
//				IntentFilter filter = new IntentFilter(
//						"android.intent.action.PHONE_STATE");
//				registerReceiver(CallBlocker, filter);
//			}
//		});
//		// blocking done
//		// unblocking
//
//		Button unblockCall = (Button) findViewById(R.id.button3);
//		unblockCall.setOnClickListener(new View.OnClickListener() {
//
//			public void onClick(View v) {
//				Toast.makeText(getBaseContext(), "onrecieve",
//						Toast.LENGTH_LONG).show();
//				unregisterReceiver(CallBlocker);
//				CallBlocker = null;
//			}
//		});
//
//		// unblocking done
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	public void initviews() {
		//blockAll_cb = (CheckBox) findViewById(R.id.cbBlockAll);
		// blockcontacts_cb=(CheckBox)findViewById(R.id.cbBlockContacts);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (CallBlocker != null) {
			unregisterReceiver(CallBlocker);
			CallBlocker = null;
		}
	}

}
