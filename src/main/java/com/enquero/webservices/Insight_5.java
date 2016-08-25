package com.enquero.webservices;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.enquero.utility.GetQuarter;
import com.enquero.utility.QuarterSalesStatistics;



public class Insight_5 {

	HashMap<String, QuarterSalesStatistics> getSalesTargetStatisticsMap(String geo_filters[], String product_filters[], String channel_filters[], String class_filters[], int future_date_from_quarterStart, String testDate) throws ParseException{
		Insight4_5_Helper object = new Insight4_5_Helper();
		HashMap<String, QuarterSalesStatistics> salesTargetMap_3 = object.getStatisticsForInsight5(geo_filters, product_filters, channel_filters, class_filters, future_date_from_quarterStart, testDate);
		return salesTargetMap_3;
	}
	
	void calculateTable(HashMap<String, QuarterSalesStatistics> salesTargetMap_3){
		for (Map.Entry<String, QuarterSalesStatistics> entry : salesTargetMap_3.entrySet()) {
		    String key = entry.getKey();
		    QuarterSalesStatistics value = entry.getValue();
		    if(value.getSales_for_qtd() == 0 || value.getTarget() == 0){
		    	value.setTarget_percentage(0.0);
		    }
		    else{
		    	double target_percentage = (value.getSales_for_qtd() / value.getTarget())*100;
		    	value.setTarget_percentage(target_percentage);
		    }
		    
		    if(value.getSales_for_qtd() == 0 || value.getSales_for_quarter() == 0){
		    	value.setSales_for_quarter_percentage(0.0);
		    }
		    else{
		    	double target_percentage = (value.getSales_for_qtd() / value.getSales_for_quarter())*100;
		    	value.setSales_for_quarter_percentage(target_percentage);
		    }
		    /*
		    System.out.println("---------------------------------");
		    System.out.println("Quarter: "+ value.getQuarter());
		    System.out.println("Target %: "+ value.getTarget_percentage());
		    System.out.println("Sales %: "+ value.getSales_for_quarter_percentage());*/
		 }
	}
	
	int getFutureDateDifference(String date, String testDate) throws ParseException{
		
		DateFormat quarter_start_date = new SimpleDateFormat("yyyy-MM-dd");
		Date test_date_Date = (Date)quarter_start_date.parse(testDate); 
		GetQuarter dateObject = new GetQuarter();
		
		int today_from_startOfCurrentQuarter = dateObject.getDifferenceFromStartOfQuarter(test_date_Date);
		Date future_Date = (Date)quarter_start_date.parse(date); 
		
		int future_date_from_startOfCurrentQuarter = dateObject.getDifferenceFromStartOfQuarter(future_Date);
		int future_date_from_quarterStart = -10;
		if(future_date_from_startOfCurrentQuarter < today_from_startOfCurrentQuarter){
			//System.out.println("Previous date entered");
		}
		else if(future_date_from_startOfCurrentQuarter > 90){
			future_date_from_quarterStart = 90;
			//System.out.println("Hockey date entered");
		}
		else{
			future_date_from_quarterStart = future_date_from_startOfCurrentQuarter;
			//System.out.println("Apt date entered");
		}
		return future_date_from_quarterStart;
	}
	
	public double getInsight5Value(String geo_filters[], String product_filters[], String channel_filters[], String class_filters[], String futureDate, String testDate) throws ParseException{
		int future_date_from_quarterStart = getFutureDateDifference(futureDate,testDate);
		double dollar_value = 0.0;
		if(future_date_from_quarterStart != -10){
			HashMap<String, QuarterSalesStatistics> salesTargetMap_3 = getSalesTargetStatisticsMap(geo_filters, product_filters, channel_filters, class_filters, future_date_from_quarterStart, testDate);
			calculateTable(salesTargetMap_3);
			List<Double> percentageValues = new ArrayList<Double>();
			DateFormat quarter_start_date = new SimpleDateFormat("yyyy-MM-dd");
			Date test_date_Date = (Date)quarter_start_date.parse(testDate); 
			GetQuarter quarterObject = new GetQuarter();
			String current_quarter = quarterObject.getQuarter(test_date_Date);
			for (Map.Entry<String, QuarterSalesStatistics> entry : salesTargetMap_3.entrySet()) {
				String quarter = entry.getValue().getQuarter();
				
				if(!quarter.equals(current_quarter)){
					/*
					System.out.println("Adding following values: ");
					System.out.println("Quarter: "+quarter);
					System.out.println("Actuals: "+entry.getValue().getSales_for_quarter_percentage());
					System.out.println("Target: "+entry.getValue().getTarget_percentage());*/
					percentageValues.add(entry.getValue().getSales_for_quarter_percentage());
					percentageValues.add(entry.getValue().getTarget_percentage());
				}
			}
			
			double currentTargetValue = salesTargetMap_3.get(current_quarter).getTarget();
			Collections.sort(percentageValues);
			Collections.reverse(percentageValues);
			double percentile = 0.8;
			double no_of_filtered_elements = (1-percentile)*percentageValues.size();
			no_of_filtered_elements = Math.round(no_of_filtered_elements);
			double average_future_percentage = 0.0;
			for(int i=0; i< no_of_filtered_elements; i++){
				average_future_percentage = average_future_percentage + percentageValues.get(i);
			}
			average_future_percentage = average_future_percentage/no_of_filtered_elements;
			
			InsightGoalQTRService object = new InsightGoalQTRService();
			double current_percentage = object.getCurrentQuarterGoal(geo_filters, product_filters, channel_filters, class_filters, testDate);
			double difference = average_future_percentage-current_percentage;
			System.out.println("******* FINAL STATISTICS *********");
			System.out.println("Average future %"+ average_future_percentage);
			System.out.println("Current Percentage % [Insight 1]"+ current_percentage);
			System.out.println("Difference: "+ difference);
			
			if(average_future_percentage > current_percentage && (difference > 5)){
				dollar_value = difference*currentTargetValue*0.01;
				System.out.println("Answer is Target * "+difference+"%"+" = "+ dollar_value);
			}
			else{
				dollar_value = 5*0.01*currentTargetValue;
				System.out.println("Answer is Target * 5%"+" = "+ dollar_value);
			}
		}
		return dollar_value;
	}
	
	public static void main(String[] args) throws ParseException {
		// TODO Auto-generated method stub
		String[] geo_filters = {};  
		String[] channel_filters = {};
		String[] class_filters = {};
		String[] product_filters = {};
		String date = "2016-08-10";
		String test_date = "2016-08-05";
		Insight_5 insight_object = new Insight_5();
		double dollar_value = insight_object.getInsight5Value(geo_filters, product_filters, channel_filters, class_filters, date, test_date);
		System.out.println("Dollar Value: "+ dollar_value);
	}

}
