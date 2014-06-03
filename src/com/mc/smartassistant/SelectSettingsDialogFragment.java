package com.mc.smartassistant;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class SelectSettingsDialogFragment extends DialogFragment {

	public interface NoticeDialogListener {
        public void onDialogBrightnessClick(DialogFragment dialog);
        public void onDialogRingtoneClick(DialogFragment dialog);
        public void onDialogBluetoothClick(DialogFragment dialog);
        public void onDialogWifiClick(DialogFragment dialog);
        public void onDialogCallBlockClick(DialogFragment dialog);
        
        
    }
	 // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;
    
    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
	public SelectSettingsDialogFragment() {
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		String settingsList[] = {"Brightness", "Ringtone", "Bluetooth", "WiFi", "Block Calls"};
		 AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		    builder.setTitle("Select Settings");
		    
		    builder.setItems(settingsList, new DialogInterface.OnClickListener() {
		               public void onClick(DialogInterface dialog, int which) {
		               
		            	   if(which == 0)
		            		   mListener.onDialogBrightnessClick(SelectSettingsDialogFragment.this);
		            	   if(which ==1)
		            		   mListener.onDialogRingtoneClick(SelectSettingsDialogFragment.this);
		            	   if(which == 2)
		            		   mListener.onDialogBluetoothClick(SelectSettingsDialogFragment.this);
		            	   if(which == 3)
		            		   mListener.onDialogWifiClick(SelectSettingsDialogFragment.this);
		            	   if(which == 4)
		            		   mListener.onDialogCallBlockClick(SelectSettingsDialogFragment.this);

		   				
		       		
		           }
		    });
		    return builder.create();
	}
}
