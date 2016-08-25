package com.enquero.reqres;

public class TrendFullQuarterResponse {
	String quarter;
	String geo;
	double sum;
	
	public TrendFullQuarterResponse(String quarter, String geo, double sum) {
		super();
		this.quarter = quarter;
		this.geo = geo;
		this.sum = sum;
	}
	
	public String getQuarter() {
		return quarter;
	}
	public void setQuarter(String quarter) {
		this.quarter = quarter;
	}
	public String getGeo() {
		return geo;
	}
	public void setGeo(String geo) {
		this.geo = geo;
	}
	public double getSum() {
		return sum;
	}
	public void setSum(double sum) {
		this.sum = sum;
	}
	
}
