package com.enquero.webservices;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

import com.enquero.reqres.AllDetailsResponse;
import com.enquero.utility.Constants;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;

//TODO: Paginate the elastic queries
public class AllDetailsService {
	
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
	
	public BoolQueryBuilder createAndQuery(BoolQueryBuilder geo_or_query, BoolQueryBuilder product_or_query, BoolQueryBuilder channel_or_query, BoolQueryBuilder class_or_query, BoolQueryBuilder quarter_or_query){
		BoolQueryBuilder and_query = new BoolQueryBuilder();
		and_query.must(geo_or_query);
		and_query.must(product_or_query);
		and_query.must(channel_or_query);
		and_query.must(class_or_query);
		and_query.must(quarter_or_query);
		return and_query;
	}
	
	public SearchResponse queryElasticEngine(String geo_filters[], String product_filters[], String channel_filters[], String class_filters[], int pageSize, int pageNo){
		
		String CurrentQuarter[] = {};
		
		BoolQueryBuilder geo_or_query = createOrQuery("geo",geo_filters);
		BoolQueryBuilder product_or_query = createOrQuery("product",product_filters);
		BoolQueryBuilder channel_or_query = createOrQuery("channel_name",channel_filters);
		BoolQueryBuilder class_or_query = createOrQuery("class",class_filters);
		BoolQueryBuilder quarter_or_query = createOrQuery("quarter",CurrentQuarter);
		BoolQueryBuilder and_query = createAndQuery(geo_or_query,product_or_query,channel_or_query,class_or_query, quarter_or_query);
		
		SearchResponse allDetailsHits = elasticClientObject.matchQuery(and_query,Constants.booking_type,pageSize, pageNo);
		System.out.println("Result output: "+allDetailsHits);
		return allDetailsHits;
		
	}
	
	List<AllDetailsResponse> createResultObject(SearchResponse allDetailsHits, int pageSize){
		List<AllDetailsResponse> resultObject = new ArrayList<AllDetailsResponse>();
		if(allDetailsHits.getHits().totalHits() > 0){
			for(int i=0; i< pageSize; i++){
				AllDetailsResponse object = new AllDetailsResponse();
				
				String json = allDetailsHits.getHits().getAt(i).sourceAsString();
				ReadContext ctx = JsonPath.parse(json);
				String geo = ctx.read("$.geo");
				object.setGeo(geo);
				String product = ctx.read("$.product");
				object.setProduct(product);
				String channel = ctx.read("$.channel_name");
				object.setChannel(channel);
				String className = ctx.read("$.class");
				object.setClassName(className);
				String quarter = ctx.read("$.quarter");
				object.setQuarter(quarter);
				String reported_account_name = ctx.read("$.reported_account_name");
				object.setReported_account_name(reported_account_name);
				String product_group = ctx.read("$.product_group");
				object.setProduct_group(product_group);
				String product_platform = ctx.read("$.product_platform");
				object.setProduct_platform(product_platform);
				String platform_group = ctx.read("$.platform_group");
				object.setPlatform_group(platform_group);
				String platform = ctx.read("$.platform");
				object.setPlatform(platform);
				String gbl_ultimate_duns_num_string = ctx.read("$.gbl_ultimate_duns_num").toString();
				if(gbl_ultimate_duns_num_string.isEmpty()){
					gbl_ultimate_duns_num_string = "0";
				}
				long gbl_ultimate_duns_num = Long.parseLong(gbl_ultimate_duns_num_string); 
				object.setGbl_ultimate_duns_num(gbl_ultimate_duns_num);
				String gu_business_name = ctx.read("$.gu_business_name");
				object.setGu_business_name(gu_business_name);
				String order_id_string = ctx.read("$.order_id").toString();
				if(order_id_string.isEmpty()){
					order_id_string = "0";
				}
				long order_id = Long.parseLong(order_id_string); 
				object.setOrder_id(order_id);
				String order_date = ctx.read("$.order_date");
				object.setOrder_date(order_date);
				String sum_string = ctx.read("$.sum").toString();
				if(sum_string.isEmpty()){
					sum_string = "0";
				}
				double sum = Double.parseDouble(sum_string); 
				object.setSum(sum);
				
				resultObject.add(object);
		    }
		}
		return resultObject;
	}
	
	long getSize(SearchResponse allDetailsHits){
		long totalHits = allDetailsHits.getHits().totalHits();
		return totalHits;
	}
	
	public List<AllDetailsResponse> getAllDetailsResponse(String geo_filters[], String product_filters[], String channel_filters[], String class_filters[], int pageSize, int pageNo){
		SearchResponse allDetailsHits = queryElasticEngine(geo_filters, product_filters, channel_filters, class_filters, pageSize, pageNo);
		List<AllDetailsResponse>resultObject = createResultObject(allDetailsHits, pageSize);
		return resultObject;
	}
		
	
	
	public static void main(String[] args) {
		String geo_filters[] = {"EMEA"};
		String product_filters[] = {"VI"};
		String channel_filters[] = {"SUPPORT"};
		String class_filters[]={"SUPT & MAINT"};
		AllDetailsService allDetailsEndpointObject = new AllDetailsService();
		List<AllDetailsResponse> resultObject = allDetailsEndpointObject.getAllDetailsResponse(geo_filters,product_filters,channel_filters,class_filters,10,2);
		
		
	}

}
