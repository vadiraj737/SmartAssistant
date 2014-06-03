package com.mc.smartassistant;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class SelectConditionsDialogFragment extends DialogFragment {

	public interface ConditionsNoticeDialogListener {
        public void onDialogLocationClick(DialogFragment dialog);
        public void onDialogEventClick(DialogFragment dialog);
       }
	ConditionsNoticeDialogListener mListener;
    
    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (ConditionsNoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
	public SelectConditionsDialogFragment() {
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		String settingsList[] = {"Location", "Event"};
		 AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		    builder.setTitle("Select Condition");
		    
		    builder.setItems(settingsList, new DialogInterface.OnClickListener() {
		               public void onClick(DialogInterface dialog, int which) {
		               
		            	   if(which == 0)
		            		   mListener.onDialogLocationClick(SelectConditionsDialogFragment.this);
		            	   if(which ==1)
		            		   mListener.onDialogEventClick(SelectConditionsDialogFragment.this);
		            	  }
		    });
		    return builder.create();
	}
}
