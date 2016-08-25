package com.enquero.utility;

import org.elasticsearch.common.lang3.builder.ToStringBuilder;

public class QtdTimelines {
	
	public String quarter;
	public String start_date;
	public String end_of_analysis_date;
	
	public String toString() {

        return ToStringBuilder.reflectionToString(this);
	}
	
	public QtdTimelines(){
		
	}
	public QtdTimelines(String quarter, String start_date, String end_of_analysis_date) {
		super();
		this.quarter = quarter;
		this.start_date = start_date;
		this.end_of_analysis_date = end_of_analysis_date;
	}
	
}
