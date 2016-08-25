package com.enquero.reqres;

public class RequestFilters {
	
	String geo_filters[];
	String product_filters[];
	String channel_filters[];
	String class_filters[];
	String date;
	String prev_date;
	double input_value;
	
	public double getInput_value() {
		return input_value;
	}
	public void setInput_value(double input_value) {
		this.input_value = input_value;
	}
	public String getPrev_date() {
		return prev_date;
	}
	public void setPrev_date(String prev_date) {
		this.prev_date = prev_date;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String[] getGeo_filters() {
		return geo_filters;
	}
	public void setGeo_filters(String[] geo_filters) {
		this.geo_filters = geo_filters;
	}
	public String[] getProduct_filters() {
		return product_filters;
	}
	public void setProduct_filters(String[] product_filters) {
		this.product_filters = product_filters;
	}
	public String[] getChannel_filters() {
		return channel_filters;
	}
	public void setChannel_filters(String[] channel_filters) {
		this.channel_filters = channel_filters;
	}
	public String[] getClass_filters() {
		return class_filters;
	}
	public void setClass_filters(String[] class_filters) {
		this.class_filters = class_filters;
	}
	
	
	
	
}
