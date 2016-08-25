package com.enquero.reqres;

public class TrendQTDResponse {
	
	String Quarter;
	String Geo;
	String start_date;
	String end_date;
	int value;
	
	public TrendQTDResponse(String quarter, String geo, int value, String start_date, String end_date) {
		super();
		Quarter = quarter;
		Geo = geo;
		this.value = value;
		this.start_date = start_date;
		this.end_date = end_date;
	}
	public String getQuarter() {
		return Quarter;
	}
	public void setQuarter(String quarter) {
		Quarter = quarter;
	}
	public String getGeo() {
		return Geo;
	}
	public void setGeo(String geo) {
		Geo = geo;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public String getStart_date() {
		return start_date;
	}
	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}
	public String getEnd_date() {
		return end_date;
	}
	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}
	
}
