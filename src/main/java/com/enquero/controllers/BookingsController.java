package com.enquero.controllers;

import java.text.ParseException;
import java.util.List;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.enquero.reqres.AllDetailsResponse;
import com.enquero.reqres.RequestFilters;
import com.enquero.reqres.TrendFullQuarterResponse;
import com.enquero.utility.GetDate;
import com.enquero.webservices.AllDetailsService;
import com.enquero.webservices.DataService;
import com.enquero.webservices.InsightGoalQTRService;
import com.enquero.webservices.InsightGoalQoQService;
import com.enquero.webservices.InsightGoalYOYService;
import com.enquero.webservices.Insight_4;
import com.enquero.webservices.Insight_5;
import com.enquero.webservices.TrendFullQuarterService;
import com.enquero.webservices.TrendQTDService;




@RestController
public class BookingsController {
	
	@CrossOrigin
    @RequestMapping(value="/bookings/data", method={RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public double getDataValue(
    		@RequestBody RequestFilters requestObject) throws ParseException{
		String geo_filters[];    
		String product_filters[];
		String channel_filters[];
		String class_filters[];
		String prev_date;
		GetDate date = new GetDate();
		
		geo_filters = (requestObject.getGeo_filters() == null) ? new String[0] : requestObject.getGeo_filters();
		product_filters = (requestObject.getProduct_filters() == null) ? new String[0] : requestObject.getProduct_filters();
		channel_filters = (requestObject.getChannel_filters() == null) ? new String[0] : requestObject.getChannel_filters();
		class_filters = (requestObject.getClass_filters() == null) ? new String[0] : requestObject.getClass_filters();
		prev_date = (requestObject.getPrev_date() == null) ? date.getTodaysDate(): requestObject.getPrev_date();
		DataService dataEndpointObject = new DataService();
		double dataValue = 0.0;
		dataValue = dataEndpointObject.getDataResponse(geo_filters,product_filters,channel_filters,class_filters, prev_date);
		return dataValue;
    }
	
	
	@CrossOrigin
    @RequestMapping(value="/bookings/trend/full_qtr", method={RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public  List<TrendFullQuarterResponse> getTrendFullQuarter(@RequestBody RequestFilters requestObject) throws ParseException{
		String geo_filters[];
		String product_filters[];
		String channel_filters[];
		String class_filters[];
		String prev_date;
		GetDate date = new GetDate();
		
		geo_filters = (requestObject.getGeo_filters() == null) ? new String[0] : requestObject.getGeo_filters();
		product_filters = (requestObject.getProduct_filters() == null) ? new String[0] : requestObject.getProduct_filters();
		channel_filters = (requestObject.getChannel_filters() == null) ? new String[0] : requestObject.getChannel_filters();
		class_filters = (requestObject.getClass_filters() == null) ? new String[0] : requestObject.getClass_filters();
		prev_date = (requestObject.getPrev_date() == null) ? date.getTodaysDate(): requestObject.getPrev_date();
		
		TrendFullQuarterService dataEndpointObject = new TrendFullQuarterService();
		List<TrendFullQuarterResponse> responseList;
		responseList = dataEndpointObject.getTrendFullQuarterResponse(geo_filters,product_filters,channel_filters,class_filters, prev_date);
		return responseList;
    }
	
	@CrossOrigin
    @RequestMapping(value="/bookings/trend/qtd", method={RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public  List<TrendFullQuarterResponse> getTrendQTDResponse(@RequestBody RequestFilters requestObject) throws ParseException{
		String geo_filters[];
		String product_filters[];
		String channel_filters[];
		String class_filters[];
		String prev_date;
		GetDate date = new GetDate();
		
		geo_filters = (requestObject.getGeo_filters() == null) ? new String[0] : requestObject.getGeo_filters();
		product_filters = (requestObject.getProduct_filters() == null) ? new String[0] : requestObject.getProduct_filters();
		channel_filters = (requestObject.getChannel_filters() == null) ? new String[0] : requestObject.getChannel_filters();
		class_filters = (requestObject.getClass_filters() == null) ? new String[0] : requestObject.getClass_filters();
		prev_date = (requestObject.getPrev_date() == null) ? date.getTodaysDate(): requestObject.getPrev_date();
		
		TrendQTDService dataEndpointObject = new TrendQTDService();
		List<TrendFullQuarterResponse> responseList;
		responseList = dataEndpointObject.getTrendQTDResponse(geo_filters,product_filters,channel_filters,class_filters, prev_date);
		return responseList;
    }
	
	
	@CrossOrigin
    @RequestMapping(value="/bookings/all_details", method={RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public List<AllDetailsResponse> testMethod_1(
    		@RequestBody RequestFilters requestObject, 
    		@RequestParam("size") int pageSize, 
    		@RequestParam("page") int pageNo) throws ParseException{
		String geo_filters[];
		String product_filters[];
		String channel_filters[];
		String class_filters[];
		int page_size = pageSize;
		int page_no = pageNo;
		geo_filters = (requestObject.getGeo_filters() == null) ? new String[0] : requestObject.getGeo_filters();
		product_filters = (requestObject.getProduct_filters() == null) ? new String[0] : requestObject.getProduct_filters();
		channel_filters = (requestObject.getChannel_filters() == null) ? new String[0] : requestObject.getChannel_filters();
		class_filters = (requestObject.getClass_filters() == null) ? new String[0] : requestObject.getClass_filters();
		
		AllDetailsService allDetailsEndpointObject = new AllDetailsService();
		List<AllDetailsResponse> allDetailsList;
		allDetailsList = allDetailsEndpointObject.getAllDetailsResponse(geo_filters,product_filters,channel_filters,class_filters, page_size, page_no);
		return allDetailsList;
    }
	
	@CrossOrigin
    @RequestMapping(value="/insights/1", method={RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public double testMethod_2(@RequestBody RequestFilters requestObject) throws ParseException{
		String geo_filters[];
		String product_filters[];
		String channel_filters[];
		String class_filters[];
		String test_date;
		GetDate date = new GetDate();
		
		geo_filters = (requestObject.getGeo_filters() == null) ? new String[0] : requestObject.getGeo_filters();
		product_filters = (requestObject.getProduct_filters() == null) ? new String[0] : requestObject.getProduct_filters();
		channel_filters = (requestObject.getChannel_filters() == null) ? new String[0] : requestObject.getChannel_filters();
		class_filters = (requestObject.getClass_filters() == null) ? new String[0] : requestObject.getClass_filters();
		test_date = (requestObject.getPrev_date() == null) ? date.getTodaysDate(): requestObject.getPrev_date();
		
		InsightGoalQTRService allDetailsEndpointObject = new InsightGoalQTRService();
		double performance;
		performance = allDetailsEndpointObject.getCurrentQuarterGoal(geo_filters,product_filters,channel_filters,class_filters, test_date);
		return performance;
    }
	
	@CrossOrigin
    @RequestMapping(value="/insights/2", method={RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public double testMethod_3(@RequestBody RequestFilters requestObject) throws ParseException{
		String geo_filters[];
		String product_filters[];
		String channel_filters[];
		String class_filters[];
		String test_date;
		GetDate date = new GetDate();
		
		geo_filters = (requestObject.getGeo_filters() == null) ? new String[0] : requestObject.getGeo_filters();
		product_filters = (requestObject.getProduct_filters() == null) ? new String[0] : requestObject.getProduct_filters();
		channel_filters = (requestObject.getChannel_filters() == null) ? new String[0] : requestObject.getChannel_filters();
		class_filters = (requestObject.getClass_filters() == null) ? new String[0] : requestObject.getClass_filters();
		test_date = (requestObject.getPrev_date() == null) ? date.getTodaysDate(): requestObject.getPrev_date();
		
		InsightGoalQoQService allDetailsEndpointObject = new InsightGoalQoQService();
		double performance;
		performance = allDetailsEndpointObject.getCurrentQuarterGoal(geo_filters,product_filters,channel_filters,class_filters, test_date);
		return performance;
    }
	
	@CrossOrigin
    @RequestMapping(value="/insights/3", method={RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public double testMethod_4(@RequestBody RequestFilters requestObject) throws ParseException{
		String geo_filters[];
		String product_filters[];
		String channel_filters[];
		String class_filters[];
		String test_date;
		GetDate date = new GetDate();
		
		geo_filters = (requestObject.getGeo_filters() == null) ? new String[0] : requestObject.getGeo_filters();
		product_filters = (requestObject.getProduct_filters() == null) ? new String[0] : requestObject.getProduct_filters();
		channel_filters = (requestObject.getChannel_filters() == null) ? new String[0] : requestObject.getChannel_filters();
		class_filters = (requestObject.getClass_filters() == null) ? new String[0] : requestObject.getClass_filters();
		test_date = (requestObject.getPrev_date() == null) ? date.getTodaysDate(): requestObject.getPrev_date();
		
		InsightGoalYOYService allDetailsEndpointObject = new InsightGoalYOYService();
		double performance;
		performance = allDetailsEndpointObject.getCurrentQuarterGoal(geo_filters,product_filters,channel_filters,class_filters,test_date);
		return performance;
    }
	
	@CrossOrigin
    @RequestMapping(value="/insights/4", method={RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public double testMethod_5(@RequestBody RequestFilters requestObject) throws ParseException{
		String geo_filters[];
		String product_filters[];
		String channel_filters[];
		String class_filters[];
		String test_date;
		double input_value;
		GetDate date = new GetDate();
		geo_filters = (requestObject.getGeo_filters() == null) ? new String[0] : requestObject.getGeo_filters();
		product_filters = (requestObject.getProduct_filters() == null) ? new String[0] : requestObject.getProduct_filters();
		channel_filters = (requestObject.getChannel_filters() == null) ? new String[0] : requestObject.getChannel_filters();
		class_filters = (requestObject.getClass_filters() == null) ? new String[0] : requestObject.getClass_filters();
		test_date = (requestObject.getPrev_date() == null) ? date.getTodaysDate(): requestObject.getPrev_date();
		input_value = (requestObject.getInput_value() == 0.0) ? 0.0: requestObject.getInput_value();
		
		Insight_4 allDetailsEndpointObject = new Insight_4();
		double performance;
		performance = allDetailsEndpointObject.getInsight4Value(geo_filters,product_filters,channel_filters,class_filters, test_date, input_value);
		return performance;
    }
	
	@CrossOrigin
    @RequestMapping(value="/insights/5", method={RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public double testMethod_6(@RequestBody RequestFilters requestObject) throws ParseException{
		String geo_filters[];
		String product_filters[];
		String channel_filters[];
		String class_filters[];
		String future_Date;
		String test_date;
		GetDate date = new GetDate();
		geo_filters = (requestObject.getGeo_filters() == null) ? new String[0] : requestObject.getGeo_filters();
		product_filters = (requestObject.getProduct_filters() == null) ? new String[0] : requestObject.getProduct_filters();
		channel_filters = (requestObject.getChannel_filters() == null) ? new String[0] : requestObject.getChannel_filters();
		class_filters = (requestObject.getClass_filters() == null) ? new String[0] : requestObject.getClass_filters();
		future_Date = (requestObject.getDate() == null) ? date.getTodaysDate() : requestObject.getDate();
		test_date = (requestObject.getPrev_date() == null) ? date.getTodaysDate(): requestObject.getPrev_date();
		
		Insight_5 insightObject = new Insight_5();
		double testValue = insightObject.getInsight5Value(geo_filters,product_filters,channel_filters,class_filters, future_Date, test_date);
		return testValue;
    }
}
