package com.enquero.webservices;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;

import com.enquero.utility.Constants;



public class QuarterTargetService {
	String target_typeName = Constants.target_type;
	int targetValue = 0;
	ElasticClient clientObject = new ElasticClient();
	public BoolQueryBuilder createOrQuery(String filterField, String filters[]){
		String filter[]; 
		filter = filters;
		BoolQueryBuilder or_query = new BoolQueryBuilder();
		for (int i=0; i< filter.length; i++) {
			or_query.should(QueryBuilders.matchQuery(filterField, filter[i]));
		}
		return or_query;
	}
	
	public BoolQueryBuilder createAndQuery(BoolQueryBuilder category_or_query, BoolQueryBuilder quarter_or_query){
		BoolQueryBuilder and_query = new BoolQueryBuilder();
		and_query.must(category_or_query);
		and_query.must(quarter_or_query);
		return and_query;
	}
	
	public int getTargetValue(SearchResponse currentQuarterTargetResponse){
		Sum dataValue_Sum = currentQuarterTargetResponse.getAggregations().get("sum");
		int dataValue = (int) dataValue_Sum.getValue();
		return dataValue;
	}
	
	
	public int getTargetCurrentQuarter(String Quarter, String[] category_filters){
		String[] current_quarter = new String[1];
		current_quarter[0] = Quarter;
		BoolQueryBuilder category_or_query = createOrQuery("Category",category_filters);
		BoolQueryBuilder quarter_or_query = createOrQuery("Quarter",current_quarter);
		BoolQueryBuilder and_query = createAndQuery(category_or_query, quarter_or_query);
		SearchResponse currentQuarterTargetResponse = clientObject.singleSumAggregationQuery(and_query, "sum", "Target", target_typeName);
		int targetValue = getTargetValue(currentQuarterTargetResponse);
		return targetValue;
		
	}
	
	public static void main(String[] args) {
		String[] filters = {"APJ", "EMEA", "RENEWALS"};
		QuarterTargetService object = new QuarterTargetService();
		int targetValue = object.getTargetCurrentQuarter("16Q3",filters);
		System.out.println("targetValue: "+ targetValue);
		
	}

}
