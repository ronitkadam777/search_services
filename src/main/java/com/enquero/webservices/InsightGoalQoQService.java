package com.enquero.webservices;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.enquero.utility.Constants;
import com.enquero.utility.GetQuarter;
import com.enquero.utility.QtdTimelines;

public class InsightGoalQoQService {
	public static String typeName = Constants.target_type;
	public String previous_quarter = "";
	GetQuarter quarterObject = new GetQuarter();
	
	public String getQuarter(Date date){
		
		String quarter = quarterObject.getQuarter(date);
		String year = quarter.substring(0, 2);
		int previous_year = Integer.parseInt(year);
		previous_year --;
		String previous = String.valueOf(previous_year);
		String previous_quarter = previous+quarter.substring(2,4);
		return previous_quarter;
	}
	
	int getCurrentQuarterTarget(String[] filters, Date date){
		
		String current_quarter = getQuarter(date);
		QuarterTargetService object = new QuarterTargetService();
		int value = object.getTargetCurrentQuarter(current_quarter,filters);
		return value;
	}
	
	double getCurrentQuarterSales(String geoFilters[], String productFilters[], String channelFilters[], String classFilters[], Date date) throws ParseException{
		
		String[] geo_filters = geoFilters;
		String[] channel_filters = channelFilters;
		String[] class_filters = classFilters;
		String[] product_filters = productFilters;
		String start_date = null;
		String end_of_analysis = null;
		
		/*
		start_date = quarterObject.getStartDateOfQuarter(date);
		int year = Integer.parseInt(start_date.substring(0, 4));
		year = year - 1;
		start_date = String.valueOf(year)+start_date.substring(4,10);
		
		end_of_analysis = new SimpleDateFormat("yyyy-MM-dd").format(date);
		end_of_analysis = String.valueOf(year)+end_of_analysis.substring(4,10);
		*/
		
		String end_date = new SimpleDateFormat("yyyy-MM-dd").format(date);
		int difference_from_start = quarterObject.getDifferenceFromStartOfQuarter(date);
		List<QtdTimelines> timelineChart = quarterObject.generateTimeline(difference_from_start, end_date);
		String quarter = getQuarter(date);
		for(int i=0; i< timelineChart.size(); i++){
			if(timelineChart.get(i).quarter.equalsIgnoreCase(quarter)){
				start_date = timelineChart.get(i).start_date;
				end_of_analysis = timelineChart.get(i).end_of_analysis_date;
				
			}
		}
		
		DataService_Quarter q = new DataService_Quarter();
		double value = q.getDataResponse(geo_filters, product_filters, channel_filters, class_filters, start_date, end_of_analysis);
		return value;
	}
	
	public double getCurrentQuarterGoal(String geo_filters[], String product_filters[], String channel_filters[], String class_filters[], String test_date) throws ParseException{
		
		String filters[] = new String[5];
		if( geo_filters.length > 0){
			filters = geo_filters;
		}
		if(channel_filters.length > 0){
			filters = channel_filters;
		}
		else{
			filters = geo_filters;
		}
		DateFormat future_date = new SimpleDateFormat("yyyy-MM-dd");
		Date test_date_Date = (Date)future_date.parse(test_date); 
		
		double value_target = getCurrentQuarterTarget(filters, test_date_Date);
		double value_sales = getCurrentQuarterSales(geo_filters, product_filters, channel_filters, class_filters, test_date_Date);
		double value_performance = (value_sales/value_target)*100;
		return value_performance;
	}
	
	public static void main(String[] args) throws ParseException {
		
		InsightGoalQoQService object = new InsightGoalQoQService();
		String geo_filters[] = {};
		String product_filters[] = {};
		String channel_filters[] = {};
		String class_filters[]={};
		String test_date = "2016-12-31";
		double performance = object.getCurrentQuarterGoal(geo_filters, product_filters, channel_filters, class_filters, test_date);
		System.out.println("Performance for last year's corresponding quarter is: "+ performance+"%");
	}
}
