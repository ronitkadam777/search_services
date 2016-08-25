package com.enquero.webservices;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


import com.enquero.utility.GetQuarter;
import com.enquero.utility.QuarterSalesStatistics;



public class Insight_4 {
	GetQuarter quarterObject =  new GetQuarter();
	double input_value = 0.0;
	String[] geo_filters = {};  
	String[] channel_filters = {};
	String[] class_filters = {};
	String[] product_filters = {};
	HashMap<String, QuarterSalesStatistics> getSalesTargetStatisticsMap(String geo_filters[], String product_filters[], String channel_filters[], String class_filters[], String test_date) throws ParseException{
		Insight4_5_Helper object = new Insight4_5_Helper();
		HashMap<String, QuarterSalesStatistics> salesTargetMap_3 = object.getCurrentQuarterGoal(geo_filters, product_filters, channel_filters, class_filters, test_date);
		
		return salesTargetMap_3;
	}
	
	void calculateTable(HashMap<String, QuarterSalesStatistics> salesTargetMap_3, String test_date, double input_value) throws ParseException{
		for (Map.Entry<String, QuarterSalesStatistics> entry : salesTargetMap_3.entrySet()) {
		    String key = entry.getKey();
		    QuarterSalesStatistics value = entry.getValue();
		    DateFormat quarter_start_date = new SimpleDateFormat("yyyy-MM-dd");
			Date test_date_Date = (Date)quarter_start_date.parse(test_date); 
			String current_quarter = quarterObject.getQuarter(test_date_Date);
			
		    if(value.getSales_for_qtd() == 0 || value.getTarget() == 0){
		    	value.setTarget_percentage(0.0);
		    }
		    else{
		    	double qtd_to_target;
		    	
		    	if((value.getQuarter().equals(current_quarter)) && (input_value != 0.0)){
		    		qtd_to_target = (input_value / value.getTarget())*100;
		    	}
		    	else{
		    		qtd_to_target = (value.getSales_for_qtd() / value.getTarget())*100;
		    	}
		    	value.setTarget_percentage(qtd_to_target);
		    }
		    
		    if(value.getSales_for_qtd() == 0 || value.getSales_for_quarter() == 0){
		    	value.setSales_for_quarter_percentage(0.0);
		    }
		    else{
		    	double qtd_to_actual;
		    	
		    	if((value.getQuarter().equals(current_quarter)) && (input_value != 0.0)){
		    		qtd_to_actual = (input_value / value.getSales_for_quarter())*100;
		    	}
		    	else{
		    		qtd_to_actual = (value.getSales_for_qtd() / value.getSales_for_quarter())*100;
		    	}
		    	value.setSales_for_quarter_percentage(qtd_to_actual);
		    }
		    /*
		    System.out.println("Quarter: "+ value.getQuarter());
		    System.out.println("Target %: "+ value.getTarget_percentage());
		    System.out.println("Sales %: "+ value.getSales_for_quarter_percentage());
		    System.out.println("---------------------------------");*/
		 }
	}
	
	public double getInsight4Value(String geo_filters[], String product_filters[], String channel_filters[], String class_filters[], String test_date, double input_value) throws ParseException{
		HashMap<String, QuarterSalesStatistics> salesTargetMap_3 = getSalesTargetStatisticsMap(geo_filters, product_filters, channel_filters, class_filters, test_date);
		calculateTable(salesTargetMap_3, test_date, input_value);
		double qtd_to_target_Current;
		DateFormat quarter_start_date = new SimpleDateFormat("yyyy-MM-dd");
		Date test_date_Date = (Date)quarter_start_date.parse(test_date); 
		String current_quarter = quarterObject.getQuarter(test_date_Date);
		QuarterSalesStatistics object = salesTargetMap_3.get(current_quarter);
		qtd_to_target_Current = object.getTarget_percentage();
		
		double greater_qtd_targets = 0.0;
		double greater_qtd_sales = 0.0;
		double total_quarters = 0.0;
		for (Map.Entry<String, QuarterSalesStatistics> entry : salesTargetMap_3.entrySet()) {
			String quarter_iterator = entry.getValue().getQuarter();
			String quarter_current = current_quarter;
			
			if(!quarter_iterator.equals(quarter_current) && entry.getValue().getTarget_percentage() <= qtd_to_target_Current){
				greater_qtd_targets ++;
			}
			if(!quarter_iterator.equals(quarter_current) && entry.getValue().getSales_for_quarter_percentage() <= qtd_to_target_Current){
				greater_qtd_sales ++;
			}
			total_quarters++;
		}
		total_quarters--;
		System.out.println("Target Numerator: "+greater_qtd_targets);
		System.out.println("Sales Numerator: "+greater_qtd_sales);
		System.out.println("Total Targets: "+ total_quarters);
		double value = ((greater_qtd_targets/total_quarters)+(greater_qtd_sales/total_quarters))*50;
		return value;
	}
	
	public static void main(String[] args) throws ParseException {
		String[] geo_filters = {};  
		String[] channel_filters = {};
		String[] class_filters = {};
		String[] product_filters = {};
		String test_date = "2016-08-22";
		double input_value = 0.0;
		Insight_4 insight_object = new Insight_4();
		double value = insight_object.getInsight4Value(geo_filters, product_filters, channel_filters, class_filters, test_date, input_value);
		System.out.println("Value: "+ value +"%");
	}

}
