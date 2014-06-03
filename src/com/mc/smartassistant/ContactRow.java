/**
 * 
 */
package com.mc.smartassistant;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author karthik
 *
 */
public class ContactRow implements Parcelable{
	private String name = "";
	private String phoneNumber = "";
	private String checked ="false";
	
	public String getPhoneNumber() {
		return phoneNumber;
	}

	
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getName() {
		return name;
	}
	
	public boolean isChecked()
	{
		if(checked.equalsIgnoreCase("true"))
		return true;
		else return false; 
	}
	public void toggleChecked()
	{
		if(checked.equalsIgnoreCase("true"))
			checked = "false";
		else checked = "true"; 
		
	}
	public void setName(String name) {
		this.name = name;
	}
	public Boolean getChecked() {
		if(checked.equalsIgnoreCase("true"))
		return true;
		else return false;
	}
	public void setChecked(Boolean checked) {
		this.checked = "true";
	}
	/**
	 * @param name
	 * @param checked
	 */
	public ContactRow(String name, Boolean checked) {
		super();
		this.name = name;
		if(checked)
		this.checked = "true";
		else
			this.checked = "false";
	}
	/**
	 * @param name
	 */
	public ContactRow(String name) {
		super();
		this.name = name;
		
	}
	/**
	 * @param checked
	 */
	public ContactRow(Boolean checked) {
		super();
		if(checked)
			this.checked = "true";
			else
				this.checked = "false";
		
	}
	/**
	 * 
	 */
	public ContactRow() {
		super();
	}
	public ContactRow(Parcel in) {
		// TODO Auto-generated constructor stub
	}
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(checked);
		
		
	}
	
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
	         public ContactRow createFromParcel(Parcel in) {
	             return new ContactRow(in); 
	         }
	  
	         public ContactRow[] newArray(int size) {
	             return new ContactRow[size];
	         }
	     };
	 }
	


