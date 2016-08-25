package com.enquero.webservices;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;

import com.enquero.reqres.TrendFullQuarterResponse;
import com.enquero.utility.Constants;
import com.enquero.utility.GetQuarter;
import com.enquero.utility.QtdTimelines;




public class TrendQTDService {
	
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
	
	public BoolQueryBuilder createRangeQuery(QtdTimelines quarterDates){
		String quarter_startDate = quarterDates.start_date;
		String quarter_endAnalysisDate = quarterDates.end_of_analysis_date;
		BoolQueryBuilder range_query = new BoolQueryBuilder();
		range_query.must(QueryBuilders.rangeQuery("order_date")
				.from(quarter_startDate)
				.to(quarter_endAnalysisDate)
				);
		return range_query;
	}
	
	public BoolQueryBuilder createQuarterMatchQuery(QtdTimelines quarterDates){
		
		String quarter = quarterDates.quarter;
		BoolQueryBuilder quarter_query = new BoolQueryBuilder();
		quarter_query.must(QueryBuilders.matchQuery("quarter",quarter));
		return quarter_query;
	}
	
	
	
	public BoolQueryBuilder createAndQuery(BoolQueryBuilder geo_or_query, BoolQueryBuilder product_or_query, BoolQueryBuilder channel_or_query, BoolQueryBuilder class_or_query, BoolQueryBuilder range_query, BoolQueryBuilder quarter_query, boolean flag_allQuarters){
		BoolQueryBuilder and_query = new BoolQueryBuilder();
		if(!flag_allQuarters){
			and_query.must(geo_or_query);
		}
		and_query.must(product_or_query);
		and_query.must(channel_or_query);
		and_query.must(class_or_query);
		and_query.must(range_query);
		and_query.must(quarter_query);
		return and_query;
	}
	
	
	public SearchResponse getPerGeoResponse(BoolQueryBuilder and_query){
		SearchResponse getResponse = elasticClientObject.getPerGeoResponse(and_query);
		return getResponse;
	}
	
	public SearchResponse getAllQuartersResponse(BoolQueryBuilder and_query){
		SearchResponse trendFullQuarterQueryResponse = elasticClientObject.twoLevelSumAggregationQuery(and_query, "Quarters", "quarter", "sum", "sum", Constants.booking_type);
		return trendFullQuarterQueryResponse;
	}
	
	public List<List<SearchResponse>> queryElasticEngine(String[] geo_filters, String[] product_filters, String channel_filters[], String class_filters[], String test_date) throws ParseException{
		
		BoolQueryBuilder geo_or_query = createOrQuery("geo",geo_filters);
		BoolQueryBuilder product_or_query = createOrQuery("product",product_filters);
		BoolQueryBuilder channel_or_query = createOrQuery("channel_name",channel_filters);
		BoolQueryBuilder class_or_query = createOrQuery("class",class_filters);
		
		GetQuarter GetQuarterObject = new GetQuarter();
		DateFormat future_date = new SimpleDateFormat("yyyy-MM-dd");
		Date test_date_Date = (Date)future_date.parse(test_date); 
		
		int days_from_startOfQuarter = GetQuarterObject.getDifferenceFromStartOfQuarter(test_date_Date);
		
		List<QtdTimelines> timelineChart = GetQuarterObject.generateTimeline(days_from_startOfQuarter, test_date);
		List<SearchResponse> trendFullQuarterResponse_AllQuarters_list = new ArrayList<SearchResponse>();
		List<SearchResponse> trendFullQuarterQueryResponse_PerGeo_list = new ArrayList<SearchResponse>();
		
		for(QtdTimelines quarterDates: timelineChart){
			BoolQueryBuilder range_query = createRangeQuery(quarterDates);
			BoolQueryBuilder quarter_query = createQuarterMatchQuery(quarterDates);
			BoolQueryBuilder and_query_allQuarter = createAndQuery(geo_or_query,product_or_query,channel_or_query,class_or_query,range_query,quarter_query,true);
			BoolQueryBuilder and_query_geoAggregated = createAndQuery(geo_or_query,product_or_query,channel_or_query,class_or_query,range_query,quarter_query,false);
			SearchResponse trendFullQuarterResponse_AllQuarters = getAllQuartersResponse(and_query_allQuarter);
			trendFullQuarterResponse_AllQuarters_list.add(trendFullQuarterResponse_AllQuarters);
			SearchResponse trendFullQuarterQueryResponse_PerGeo = getPerGeoResponse(and_query_geoAggregated);
			trendFullQuarterQueryResponse_PerGeo_list.add(trendFullQuarterQueryResponse_PerGeo);
		}
		
		List<List<SearchResponse>> list = new ArrayList<List<SearchResponse>>();
		list.add(trendFullQuarterResponse_AllQuarters_list);
		list.add(trendFullQuarterQueryResponse_PerGeo_list);
		//System.out.println("list: "+list);
		
		return list;
		
	}
	
	public List<TrendFullQuarterResponse> getListForAllQuarters(List<List<SearchResponse>> trendFullQuarterQueryResponse){
		List<SearchResponse> allQuarterResponse = trendFullQuarterQueryResponse.get(0);
		List<TrendFullQuarterResponse> trendFullQuarterResponseList = new ArrayList<TrendFullQuarterResponse>();
		for(SearchResponse response: allQuarterResponse){
			Terms Quarters = response.getAggregations().get("Quarters");
			for(Terms.Bucket aggregatedQuarters : Quarters.getBuckets()){
				Sum aggregatedSum = aggregatedQuarters.getAggregations().get("sum");
				String quarter = aggregatedQuarters.getKey();
				String count = aggregatedSum.getValueAsString();
				double sum = Double.parseDouble(count);
				sum = sum/1000000;
				TrendFullQuarterResponse trendFullQuarterResponseObject = new TrendFullQuarterResponse(quarter, "ALL", sum);
				trendFullQuarterResponseList.add(trendFullQuarterResponseObject);
			}
		}
		
		return trendFullQuarterResponseList;
	}
	
	public List<TrendFullQuarterResponse> getListForPerGeo(List<TrendFullQuarterResponse> trendFullQuarterResponseList, List<List<SearchResponse>> trendFullQuarterQueryResponse){
		List<SearchResponse> allQuarterResponse = trendFullQuarterQueryResponse.get(1);
		for(SearchResponse response: allQuarterResponse){
			Terms Quarters_Geo = response.getAggregations().get("Quarters");
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
		}
		
		return trendFullQuarterResponseList;
	}
	
	
	List<TrendFullQuarterResponse> getTrendFullQuarterResponseList(List<List<SearchResponse>> trendFullQuarterQueryResponse){
		List<TrendFullQuarterResponse> trendAllQuartersList = getListForAllQuarters(trendFullQuarterQueryResponse);
		List<TrendFullQuarterResponse> trendFullQuarterResponseList = getListForPerGeo(trendAllQuartersList, trendFullQuarterQueryResponse);
		return trendFullQuarterResponseList;
	}
	
	List<TrendFullQuarterResponse> getTrendFullQuarterResponse(String geo_filters[], String product_filters[], String channel_filters[], String class_filters[], String test_date) throws ParseException{
		
		List<List<SearchResponse>> trendFullQuarterQueryResponse = queryElasticEngine(geo_filters, product_filters, channel_filters, class_filters, test_date);
		List<TrendFullQuarterResponse> trendFullQuarterResponseList = getTrendFullQuarterResponseList(trendFullQuarterQueryResponse);
		return trendFullQuarterResponseList;
	}
	
	
	public List<TrendFullQuarterResponse> getTrendQTDResponse(String geo_filters[], String product_filters[], String channel_filters[], String class_filters[], String prev_date) throws ParseException{
		List<TrendFullQuarterResponse> trendQuarterServiceResponse = getTrendFullQuarterResponse(geo_filters,product_filters,channel_filters,class_filters, prev_date);
		return trendQuarterServiceResponse;
	}
	
	public static void main(String[] args) throws ParseException {
		
		String geo_filters[] = {};
		String product_filters[] = {};
		String channel_filters[] = {};  
		String class_filters[]={};
		String prev_date = "2016-02-28";
		TrendQTDService trendQTDObject = new TrendQTDService();
		List<TrendFullQuarterResponse> trendQuarterServiceResponse = trendQTDObject.getTrendFullQuarterResponse(geo_filters,product_filters,channel_filters,class_filters, prev_date);
		
	}
	
}
