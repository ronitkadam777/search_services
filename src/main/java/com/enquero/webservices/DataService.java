package com.enquero.webservices;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import com.enquero.utility.Constants;
import com.enquero.utility.GetQuarter;
import com.enquero.utility.QtdTimelines;



public class DataService {
	
	ElasticClient elasticClientObject = new ElasticClient();
	
	
	public BoolQueryBuilder createOrQuery(String filterField, String filters[]){
		String filter[]; 
		filter = filters;
		BoolQueryBuilder or_query = new BoolQueryBuilder();
		for (int i=0; i< filter.length; i++) {
			or_query.should(QueryBuilders.matchQuery(filterField, filter[i]));
		}
		return or_query;
	}
	
	public BoolQueryBuilder createAndQuery(BoolQueryBuilder geo_or_query, BoolQueryBuilder product_or_query, BoolQueryBuilder channel_or_query, BoolQueryBuilder class_or_query, BoolQueryBuilder quarter_or_query, BoolQueryBuilder range_query){
		BoolQueryBuilder and_query = new BoolQueryBuilder();
		and_query.must(geo_or_query);
		and_query.must(product_or_query);
		and_query.must(channel_or_query);
		and_query.must(class_or_query);
		and_query.must(quarter_or_query);
		and_query.must(range_query);
		return and_query;
	}
	
	public BoolQueryBuilder createRangeQuery(QtdTimelines dateRangeObject){
		String quarter_startDate = dateRangeObject.start_date;
		String quarter_endAnalysisDate = dateRangeObject.end_of_analysis_date;
		BoolQueryBuilder range_query = new BoolQueryBuilder();
		range_query.must(QueryBuilders.rangeQuery("order_date")
				.from(quarter_startDate)
				.to(quarter_endAnalysisDate)
				);
		return range_query;
	}
	
	public SearchResponse queryElasticEngine(String geo_filters[], String product_filters[], String channel_filters[], String class_filters[], String test_date) throws ParseException{
		String quarter_filters[] = new String[1];
		GetQuarter quarter = new GetQuarter();
		
		String test_date_string  = test_date;
		DateFormat quarter_start_date = new SimpleDateFormat("yyyy-MM-dd");
		Date test_date_Date = (Date)quarter_start_date.parse(test_date_string); 
		
		//Gets the Quarter corresponding to the test_date in the YY"Q"Q format
		String quarterName =  quarter.getQuarter(test_date_Date);
		quarter_filters[0] = quarterName;
		//Gets the start date of the corresponding quarter
		String start_date = quarter.getStartDateOfQuarter(test_date_Date);
		QtdTimelines dateRangeObject = new QtdTimelines(quarterName, start_date, test_date);
		BoolQueryBuilder geo_or_query = createOrQuery("geo",geo_filters);
		BoolQueryBuilder product_or_query = createOrQuery("product",product_filters);
		BoolQueryBuilder channel_or_query = createOrQuery("channel_name",channel_filters);
		BoolQueryBuilder class_or_query = createOrQuery("class",class_filters);
		BoolQueryBuilder quarter_or_query = createOrQuery("quarter",quarter_filters);
		BoolQueryBuilder range_query = createRangeQuery(dateRangeObject);
		BoolQueryBuilder and_query = createAndQuery(geo_or_query,product_or_query,channel_or_query,class_or_query, quarter_or_query, range_query);
		SearchResponse sumAggregationResponse = elasticClientObject.singleSumAggregationQuery(and_query, "sum", "sum", Constants.booking_type);
		return sumAggregationResponse;
		
	}
	
	public double getDataResponse(String geo_filters[], String product_filters[], String channel_filters[], String class_filters[], String test_date) throws ParseException{
		SearchResponse queryResponse = queryElasticEngine(geo_filters, product_filters, channel_filters, class_filters, test_date);
		Sum dataValue_Sum = queryResponse.getAggregations().get("sum");
		double dataValue = (double) dataValue_Sum.getValue()/1000000;
		return dataValue;
		
	}
	
	public static void main(String[] args) throws ParseException {
		
		String geo_filters[] = {};
		String product_filters[] = {};
		String channel_filters[] = {};
		String class_filters[]={};
		String test_date = "2015-01-07";
		DataService dataEnpointObject = new DataService();
		double dataValue = dataEnpointObject.getDataResponse(geo_filters,product_filters,channel_filters,class_filters, test_date);
		System.out.println("DataValue: "+dataValue);
	}
}
