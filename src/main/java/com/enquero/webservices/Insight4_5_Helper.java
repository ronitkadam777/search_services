package com.enquero.webservices;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.lang3.ArrayUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;

import com.enquero.utility.Constants;
import com.enquero.utility.GetQuarter;
import com.enquero.utility.QtdTimelines;
import com.enquero.utility.QuarterSalesStatistics;


public class Insight4_5_Helper {
	GetQuarter quarterObject = new GetQuarter();
	public static String target_type = Constants.target_type;
	public static String bookings_type = Constants.booking_type;
	HashMap<String, QuarterSalesStatistics> salesTargetMap = new HashMap<String, QuarterSalesStatistics>();
	String quarters[] = null;
	public BoolQueryBuilder createOrQuery(String filterField, String filters[]){
		String filter[]; 
		filter = filters;
		BoolQueryBuilder or_query = new BoolQueryBuilder();
		for (int i=0; i< filter.length; i++) {
			or_query.should(QueryBuilders.matchQuery(filterField, filter[i]));
		}
		return or_query;
	}
	
	public BoolQueryBuilder createRangeQuery(QtdTimelines dateRangeObject){
		String quarter_endAnalysisDate = dateRangeObject.end_of_analysis_date;
		BoolQueryBuilder range_query = new BoolQueryBuilder();
		range_query.must(QueryBuilders.rangeQuery("order_date")
				.to(quarter_endAnalysisDate)
				);
		return range_query;
	}
	
	public BoolQueryBuilder createAndQuery(BoolQueryBuilder geo_or_query, BoolQueryBuilder product_or_query, BoolQueryBuilder channel_or_query, BoolQueryBuilder class_or_query, BoolQueryBuilder range_query){
		BoolQueryBuilder and_query = new BoolQueryBuilder();
		and_query.must(geo_or_query);
		and_query.must(product_or_query);
		and_query.must(channel_or_query);
		and_query.must(class_or_query);
		and_query.must(range_query);
		return and_query;
	}
	
	public BoolQueryBuilder createAndQuery(BoolQueryBuilder category_or_query, BoolQueryBuilder range_query){
		BoolQueryBuilder and_query = new BoolQueryBuilder();
		
		and_query.must(category_or_query);
		and_query.must(range_query);
		return and_query;
	}
	String[] getAllQuarters(String date) throws ParseException{
		
		List<QtdTimelines> timelineChart = quarterObject.generateTimeline(0, date);
		String quarters[] = new String[timelineChart.size()];
		for(int i = 0; i< timelineChart.size(); i++){
			quarters[i] = timelineChart.get(i).quarter;
			quarters[i] = quarters[i].toUpperCase();
			QuarterSalesStatistics salesTargetObject = new  QuarterSalesStatistics();
			salesTargetObject.setQuarter(quarters[i]);
			salesTargetMap.put(quarters[i], salesTargetObject);
		}
		return quarters;
	}
	
	HashMap<String, QuarterSalesStatistics> getAllQuarterTargets(String filters[], String end_date) throws ParseException{
		
		DateFormat future_date = new SimpleDateFormat("yyyy-MM-dd");
		Date test_date_Date = (Date)future_date.parse(end_date); 
		quarters = getAllQuarters(end_date);
		String currentQuarter = quarterObject.getQuarter(test_date_Date);
		if(!ArrayUtils.contains(quarters, currentQuarter)){
			QuarterSalesStatistics salesTargetObject = new  QuarterSalesStatistics();
			salesTargetObject.setQuarter(currentQuarter);
			salesTargetMap.put(currentQuarter, salesTargetObject);
		}
		
		QtdTimelines dateRangeObject = new QtdTimelines(null, null, end_date);
		BoolQueryBuilder category_or_query = createOrQuery("Category",filters);
		ElasticClient elasticClientObject = new ElasticClient();
		SearchResponse sumAggregationResponse = elasticClientObject.twoLevelSumAggregationQuery(category_or_query, "Quarters", "Quarter", "sum", "Target", target_type);
		Terms Quarters = sumAggregationResponse.getAggregations().get("Quarters");
		for(Terms.Bucket aggregatedQuarters : Quarters.getBuckets()){
			Sum aggregatedSum = aggregatedQuarters.getAggregations().get("sum");
			String quarter = aggregatedQuarters.getKey();
			String count = aggregatedSum.getValueAsString();
			double sum = Double.parseDouble(count);
			
			if(salesTargetMap.get(quarter.toUpperCase()) != null){
				
				QuarterSalesStatistics salesTargetObject = salesTargetMap.get(quarter);
				salesTargetObject.setTarget(sum);
				salesTargetMap.put(quarter.toUpperCase(), salesTargetObject);
			}
		}
		return salesTargetMap;
	}
	
	
	
	SearchResponse queryElasticSearch(String geo_filters[], String product_filters[], String channel_filters[], String class_filters[], String end_date){
		QtdTimelines dateRangeObject = new QtdTimelines(null, null, end_date);
		BoolQueryBuilder geo_or_query = createOrQuery("geo",geo_filters);
		BoolQueryBuilder product_or_query = createOrQuery("product",product_filters);
		BoolQueryBuilder channel_or_query = createOrQuery("channel_name",channel_filters);
		BoolQueryBuilder class_or_query = createOrQuery("class",class_filters);
		BoolQueryBuilder range_query = createRangeQuery(dateRangeObject);
		BoolQueryBuilder and_query = createAndQuery(geo_or_query,product_or_query,channel_or_query,class_or_query, range_query);
		ElasticClient elasticClientObject = new ElasticClient();
		SearchResponse sumAggregationResponse = elasticClientObject.twoLevelSumAggregationQuery(and_query, "Quarters", "quarter", "sum", "sum", bookings_type);
		
		return sumAggregationResponse;
	}
	
	HashMap<String, QuarterSalesStatistics> getAllQuarterSales(String geo_filters[], String product_filters[], String channel_filters[], String class_filters[], String end_date){
		SearchResponse sumAggregationResponse = queryElasticSearch(geo_filters,product_filters,channel_filters,class_filters, end_date);
		
		Terms Quarters = sumAggregationResponse.getAggregations().get("Quarters");
		for(Terms.Bucket aggregatedQuarters : Quarters.getBuckets()){
			Sum aggregatedSum = aggregatedQuarters.getAggregations().get("sum");
			String quarter = aggregatedQuarters.getKey();
			String count = aggregatedSum.getValueAsString();
			double sum = Double.parseDouble(count);
			sum = sum/1000000;
			if(salesTargetMap.get(quarter.toUpperCase()) != null){
				QuarterSalesStatistics q = salesTargetMap.get(quarter.toUpperCase());
				q.setSales_for_quarter(sum);
				salesTargetMap.put(quarter.toUpperCase(), q);
			}
			
		}
		return salesTargetMap;
	}
	
	HashMap<String, QuarterSalesStatistics> getAllQTDSales(String geo_filters[], String product_filters[], String channel_filters[], String class_filters[], Date date) throws ParseException{
		
		String end_date = new SimpleDateFormat("yyyy-MM-dd").format(date);
		int difference_from_start = quarterObject.getDifferenceFromStartOfQuarter(date);
		List<QtdTimelines> timelineChart = quarterObject.generateTimeline(difference_from_start, end_date);
		for(int i =0; i< timelineChart.size(); i++){
			DataService_Quarter serviceObject = new DataService_Quarter();
			String startDate = timelineChart.get(i).start_date;
			String endDate = timelineChart.get(i).end_of_analysis_date;
			String quarter = timelineChart.get(i).quarter;
			
			double value = serviceObject.getDataResponse(geo_filters, product_filters, channel_filters, class_filters, startDate, endDate);
			
			if(salesTargetMap.get(quarter.toUpperCase()) != null){
				QuarterSalesStatistics q1 = salesTargetMap.get(quarter.toUpperCase());
				q1.setSales_for_qtd(value);
				salesTargetMap.put(quarter.toUpperCase(), q1);
				
			}
		}
		return salesTargetMap;
	}
	
HashMap<String, QuarterSalesStatistics> getAllFutureQTDSales(String geo_filters[], String product_filters[], String channel_filters[], String class_filters[], int difference, String testDate) throws ParseException{
		
		List<QtdTimelines> timelineChart = quarterObject.generateTimeline(difference, testDate);
		
		for(int i =0; i< timelineChart.size(); i++){
			DataService_Quarter serviceObject = new DataService_Quarter();
			
			String startDate = timelineChart.get(i).start_date;
			String endDate = timelineChart.get(i).end_of_analysis_date;
			String quarter = timelineChart.get(i).quarter;
			//System.out.println("Quarter: "+ quarter +"---> "+ "Start Date: "+startDate+"End Date: "+ endDate);
			double value = serviceObject.getDataResponse(geo_filters, product_filters, channel_filters, class_filters, startDate, endDate);
			if(salesTargetMap.get(quarter.toUpperCase()) != null){
				QuarterSalesStatistics q1 = salesTargetMap.get(quarter.toUpperCase());
				q1.setSales_for_qtd(value);
				salesTargetMap.put(quarter.toUpperCase(), q1);
				
			}
		}
		return salesTargetMap;
	}
	
	public HashMap<String, QuarterSalesStatistics> getCurrentQuarterGoal(String geo_filters[], String product_filters[], String channel_filters[], String class_filters[], String test_date) throws ParseException{
		String filters[];
		filters = geo_filters;
		DateFormat future_date = new SimpleDateFormat("yyyy-MM-dd");
		Date test_date_Date = (Date)future_date.parse(test_date); 
		
		HashMap<String, QuarterSalesStatistics> salesTargetMap_1 =  getAllQuarterTargets(filters, test_date);
		HashMap<String, QuarterSalesStatistics> salesTargetMap_2 = getAllQuarterSales(geo_filters,product_filters,channel_filters,class_filters, test_date);
		HashMap<String, QuarterSalesStatistics> salesTargetMap_3 = getAllQTDSales(geo_filters,product_filters,channel_filters,class_filters, test_date_Date);
		return salesTargetMap_3;
	}
	
	public HashMap<String, QuarterSalesStatistics> getStatisticsForInsight5(String geo_filters[], String product_filters[], String channel_filters[], String class_filters[], int difference, String testDate) throws ParseException{
		
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
		HashMap<String, QuarterSalesStatistics> salesTargetMap_1 =  getAllQuarterTargets(filters, testDate);
		HashMap<String, QuarterSalesStatistics> salesTargetMap_2 = getAllQuarterSales(geo_filters,product_filters,channel_filters,class_filters, testDate);
		HashMap<String, QuarterSalesStatistics> salesTargetMap_3 = getAllFutureQTDSales(geo_filters,product_filters,channel_filters,class_filters, difference, testDate);
		
		return salesTargetMap_3;
	}
	
	public static void main(String[] args) throws ParseException {
		
		String[] geo_filters = {};  
		String[] channel_filters = {};
		String[] class_filters = {};
		String[] product_filters = {};
		String test_date = "2016-03-03";   
		Insight4_5_Helper obj = new Insight4_5_Helper();
		HashMap<String, QuarterSalesStatistics> i = obj.getCurrentQuarterGoal(geo_filters, product_filters, channel_filters, class_filters, test_date);
		
	}
}
