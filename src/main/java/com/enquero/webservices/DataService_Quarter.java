package com.enquero.webservices;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;

import com.enquero.utility.Constants;


public class DataService_Quarter {
	
	ElasticClient elasticClientObject = new ElasticClient();
	String booking_typeName = Constants.booking_type;
	String target_typeName = Constants.target_type;
	public BoolQueryBuilder createOrQuery(String filterField, String filters[]){
		String filter[]; 
		filter = filters;
		BoolQueryBuilder or_query = new BoolQueryBuilder();
		for (int i=0; i< filter.length; i++) {
			or_query.should(QueryBuilders.matchQuery(filterField, filter[i]));
		}
		return or_query;
	}
	
	public BoolQueryBuilder createRangeQuery(String startDate, String endDate){
		String quarter_startDate = startDate;
		String quarter_endAnalysisDate = endDate;
		BoolQueryBuilder range_query = new BoolQueryBuilder();
		range_query.must(QueryBuilders.rangeQuery("order_date")
				.from(quarter_startDate)
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
	
	public SearchResponse queryElasticEngine(String geo_filters[], String product_filters[], String channel_filters[], String class_filters[], String startDate, String endDate){
		
		
		BoolQueryBuilder geo_or_query = createOrQuery("geo",geo_filters);
		BoolQueryBuilder product_or_query = createOrQuery("product",product_filters);
		BoolQueryBuilder channel_or_query = createOrQuery("channel_name",channel_filters);
		BoolQueryBuilder class_or_query = createOrQuery("class",class_filters);
		BoolQueryBuilder range_query = createRangeQuery(startDate, endDate);
		BoolQueryBuilder and_query = createAndQuery(geo_or_query,product_or_query,channel_or_query,class_or_query, range_query);
		SearchResponse sumAggregationResponse = elasticClientObject.singleSumAggregationQuery(and_query, "sum", "sum", booking_typeName);
		return sumAggregationResponse;
		
	}
	
	public double getDataResponse(String geo_filters[], String product_filters[], String channel_filters[], String class_filters[], String startDate, String endDate){
		
		SearchResponse queryResponse = queryElasticEngine(geo_filters, product_filters, channel_filters, class_filters, startDate, endDate);
		Sum dataValue_Sum = queryResponse.getAggregations().get("sum");
		double dataValue = (double) dataValue_Sum.getValue()/1000000;
		return dataValue;
		
	}
	
	public static void main(String[] args) {
		String geo_filters[] = {};
		String product_filters[] = {};
		String channel_filters[] = {};
		String class_filters[]={};
		String startDate = "2015-07-01";
		String endDate = "2015-09-30";
		DataService_Quarter dataEnpointObject = new DataService_Quarter();
		double dataValue = dataEnpointObject.getDataResponse(geo_filters,product_filters,channel_filters,class_filters, startDate, endDate);
		System.out.println("dataValue: "+dataValue);
	}
}
