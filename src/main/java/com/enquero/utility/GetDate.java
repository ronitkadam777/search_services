package com.enquero.utility;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GetDate {

	public String getTodaysDate(){
		Date today = new Date();
		DateFormat quarter_start_date = new SimpleDateFormat("yyyy-MM-dd");
		String date = quarter_start_date.format(today);
		return date;
	}
	
	public static void main(String[] args) {
		GetDate dateObject = new GetDate();
		String todaysDate = dateObject.getTodaysDate();
	}

}
