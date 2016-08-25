package com.enquero.webservices;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import com.enquero.reqres.TrendFullQuarterResponse;
import com.enquero.utility.Constants;
import com.enquero.utility.QtdTimelines;



public class TrendFullQuarterService {
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
	
	public BoolQueryBuilder createAndQuery(BoolQueryBuilder geo_or_query, BoolQueryBuilder product_or_query, BoolQueryBuilder channel_or_query, BoolQueryBuilder class_or_query, BoolQueryBuilder range_query){
		BoolQueryBuilder and_query = new BoolQueryBuilder();
		and_query.must(geo_or_query);
		and_query.must(product_or_query);
		and_query.must(channel_or_query);
		and_query.must(class_or_query);
		and_query.must(range_query);
		return and_query;
	}
	
	public BoolQueryBuilder createRangeQuery(QtdTimelines dateRangeObject){
		String quarter_endAnalysisDate = dateRangeObject.end_of_analysis_date;
		BoolQueryBuilder range_query = new BoolQueryBuilder();
		range_query.must(QueryBuilders.rangeQuery("order_date")
				.to(quarter_endAnalysisDate)
				);
		return range_query;
	}
	
	public SearchResponse getPerGeoResponse(BoolQueryBuilder and_query){
		SearchResponse getResponse = elasticClientObject.getPerGeoResponse(and_query);
		return getResponse;
	}
	
	public SearchResponse getAllQuartersResponse(BoolQueryBuilder and_query){
		SearchResponse trendFullQuarterQueryResponse = elasticClientObject.twoLevelSumAggregationQuery(and_query, "Quarters", "quarter", "sum", "sum", Constants.booking_type);
		return trendFullQuarterQueryResponse;
	}
	
	public SearchResponse[] queryElasticEngine(String[] geo_filters, String[] product_filters, String channel_filters[], String class_filters[], String test_date) throws ParseException{
		
		QtdTimelines dateRangeObject = new QtdTimelines(null, null, test_date);
		
		BoolQueryBuilder geo_or_query = createOrQuery("geo",geo_filters);
		BoolQueryBuilder product_or_query = createOrQuery("product",product_filters);
		BoolQueryBuilder channel_or_query = createOrQuery("channel_name",channel_filters);
		BoolQueryBuilder class_or_query = createOrQuery("class",class_filters);
		BoolQueryBuilder range_query = createRangeQuery(dateRangeObject);
		
		BoolQueryBuilder and_query = createAndQuery(geo_or_query,product_or_query,channel_or_query,class_or_query, range_query);
		SearchResponse results[] = new SearchResponse[10];
		SearchResponse trendFullQuarterResponse_AllQuarters = getAllQuartersResponse(range_query);
		SearchResponse trendFullQuarterQueryResponse_PerGeo = getPerGeoResponse(and_query);
		results[0] = trendFullQuarterResponse_AllQuarters;
		results[1] = trendFullQuarterQueryResponse_PerGeo;
		return results;
		
	}
	
	public List<TrendFullQuarterResponse> getListForAllQuarters(SearchResponse trendFullQuarterQueryResponse[]){
		List<TrendFullQuarterResponse> trendFullQuarterResponseList = new ArrayList<TrendFullQuarterResponse>();
		
		Terms Quarters = trendFullQuarterQueryResponse[0].getAggregations().get("Quarters");
		for(Terms.Bucket aggregatedQuarters : Quarters.getBuckets()){
			Sum aggregatedSum = aggregatedQuarters.getAggregations().get("sum");
			String quarter = aggregatedQuarters.getKey();
			String count = aggregatedSum.getValueAsString();
			double sum = Double.parseDouble(count);
			sum = sum/1000000;
			TrendFullQuarterResponse trendFullQuarterResponseObject = new TrendFullQuarterResponse(quarter, "ALL", sum);
			trendFullQuarterResponseList.add(trendFullQuarterResponseObject);
		}
		return trendFullQuarterResponseList;
	}
	
	public List<TrendFullQuarterResponse> getListForPerGeo(List<TrendFullQuarterResponse> trendFullQuarterResponseList, SearchResponse trendFullQuarterQueryResponse[]){
		Terms Quarters_Geo = trendFullQuarterQueryResponse[1].getAggregations().get("Quarters");
		for(Terms.Bucket aggregatedQuarter : Quarters_Geo.getBuckets()){
			Terms geo = aggregatedQuarter.getAggregations().get("geo");
			for(Terms.Bucket aggregatedGeo : geo.getBuckets()){
				Sum aggregatedSum = aggregatedGeo.getAggregations().get("sum");
				String quarter = aggregatedQuarter.getKey();
				String geoName = aggregatedGeo.getKey();
				String count = aggregatedSum.getValueAsString();
				double sum = Double.parseDouble(count);
				sum = sum/1000000;
				TrendFullQuarterResponse trendFullQuarterResponseObject = new TrendFullQuarterResponse(quarter, geoName, sum);
				trendFullQuarterResponseList.add(trendFullQuarterResponseObject);
			}
		}
		return trendFullQuarterResponseList;
	}
	
	
	List<TrendFullQuarterResponse> getTrendFullQuarterResponseList(SearchResponse trendFullQuarterQueryResponse[]){
		List<TrendFullQuarterResponse> trendAllQuartersList = getListForAllQuarters(trendFullQuarterQueryResponse);
		List<TrendFullQuarterResponse> trendFullQuarterResponseList = getListForPerGeo(trendAllQuartersList, trendFullQuarterQueryResponse);
		return trendFullQuarterResponseList;
	}
	
	public List<TrendFullQuarterResponse> getTrendFullQuarterResponse(String geo_filters[], String product_filters[], String channel_filters[], String class_filters[], String test_date) throws ParseException{
		SearchResponse[] trendFullQuarterQueryResponse = queryElasticEngine(geo_filters, product_filters, channel_filters, class_filters, test_date);
		List<TrendFullQuarterResponse> trendFullQuarterResponseList = getTrendFullQuarterResponseList(trendFullQuarterQueryResponse);
		return trendFullQuarterResponseList;
	}
		
	public static void main(String[] args) throws ParseException {
		String geo_filters[] = {};
		String product_filters[] = {};
		String channel_filters[] = {};
		String class_filters[]={};
		String test_date = "2015-12-31";
		TrendFullQuarterService trendFullQuarterServiceObject = new TrendFullQuarterService();
		List<TrendFullQuarterResponse> trendQuarterServiceResponse = trendFullQuarterServiceObject.getTrendFullQuarterResponse(geo_filters,product_filters,channel_filters,class_filters, test_date);
		System.out.println("trendQuarterServiceResponse: "+trendQuarterServiceResponse);
	}
}
