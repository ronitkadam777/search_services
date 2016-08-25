package com.enquero.webservices;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;

import com.enquero.utility.Constants;



public class ElasticClient {
	
	public static Node node = NodeBuilder.nodeBuilder().node();
	public static Client client = node.client();
	
	public SearchResponse twoLevelSumAggregationQuery(BoolQueryBuilder and_query, String aggregation_type_1, String aggregation_field_1, String aggregation_type_2, String aggregation_field_2, String type_name){
		//Node node = NodeBuilder.nodeBuilder().node();
		//Client client = node.client();
		SearchResponse sumAggregationResponse = client.prepareSearch(Constants.index_name).setTypes(type_name)
				.setSize(0)
				.setQuery(and_query)
				.addAggregation(AggregationBuilders
						.terms(aggregation_type_1).field(aggregation_field_1)
						.size(0)
						.subAggregation(AggregationBuilders
								.sum(aggregation_type_2).field(aggregation_field_2)))
				.execute().actionGet();
		//node.close();
		return sumAggregationResponse;
	
	}
	
	public SearchResponse twoLevelSumAggregationQuery(String aggregation_type_1, String aggregation_field_1, String aggregation_type_2, String aggregation_field_2, String type_name){
		//Node node = NodeBuilder.nodeBuilder().node();
		//Client client = node.client();
		SearchResponse sumAggregationResponse = client.prepareSearch(Constants.index_name).setTypes(type_name)
				.setSize(0)
				.addAggregation(AggregationBuilders
						.terms(aggregation_type_1).field(aggregation_field_1)
						.size(0)
						.subAggregation(AggregationBuilders
								.sum(aggregation_type_2).field(aggregation_field_2)))
				.execute().actionGet();
		//node.close();
		return sumAggregationResponse;
	
	}
	
	public SearchResponse matchQuery(BoolQueryBuilder and_query, String typeName, int pageSize, int pageNo){
		/*Node node = NodeBuilder.nodeBuilder().node();
		Client client = node.client();*/
		int from = (pageNo-1)*pageSize;
		SearchResponse allDetailsHits = client.prepareSearch(Constants.index_name).setTypes(typeName)
			.setQuery(and_query)
			.setFrom(from).setSize(pageSize)
			.execute().actionGet();
		//node.close();
	return allDetailsHits;
	}
	
	public SearchResponse singleSumAggregationQuery(BoolQueryBuilder and_query, String aggregation_type, String aggregation_field, String type_name){
		/*Node node = NodeBuilder.nodeBuilder().node();
		Client client = node.client();*/
		SearchResponse sumAggregationResponse = client.prepareSearch(Constants.index_name).setTypes(type_name)
				.setSize(0)
				.setQuery(and_query)
				.addAggregation(AggregationBuilders.sum(aggregation_type).field(aggregation_field))
				.setFrom(0).setSize(0)
				.execute().actionGet();
		//node.close();
		return sumAggregationResponse;
	}
	
	public SearchResponse termAggregtionQuery(BoolQueryBuilder range_query){
		/*Node node = NodeBuilder.nodeBuilder().node();
		Client client = node.client();*/
		SearchResponse allQuartersResponse = client.prepareSearch(Constants.index_name).setTypes(Constants.booking_type)
			.setSize(0)
			.setQuery(range_query)
			.addAggregation(AggregationBuilders
					.terms("Quarters").field("quarter").order(Terms.Order.term(true))
					.size(0)
					)
			.execute().actionGet();
		//node.close();
		return allQuartersResponse;
	}
	
	public SearchResponse getPerGeoResponse(BoolQueryBuilder and_query){
		/*Node node = NodeBuilder.nodeBuilder().node();
		Client client = node.client();*/
		SearchResponse trendFullQuarterQueryResponse = client.prepareSearch(Constants.index_name).setTypes(Constants.booking_type)
				.setSize(0)
				.setQuery(and_query)
				.addAggregation(AggregationBuilders
						.terms("Quarters").field("quarter")
						.size(0)
						.subAggregation(AggregationBuilders
								.terms("geo").field("geo").size(0)
									.subAggregation(AggregationBuilders.sum("sum").field("sum"))))
				
				.execute().actionGet();
		//node.close();
		return trendFullQuarterQueryResponse;
	}
}

