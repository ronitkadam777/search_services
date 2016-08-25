package com.enquero.utility;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;

import com.enquero.webservices.ElasticClient;




public class GetQuarter {
	
	public static String startDates[] = {"-01-01","-04-01","-07-01","-10-01"};
	public static Calendar cal = Calendar.getInstance();
	ElasticClient obj = new ElasticClient();
	
	public BoolQueryBuilder createRangeQuery(QtdTimelines dateRangeObject){
		String quarter_endAnalysisDate = dateRangeObject.end_of_analysis_date;
		BoolQueryBuilder range_query = new BoolQueryBuilder();
		range_query.must(QueryBuilders.rangeQuery("order_date")
				.to(quarter_endAnalysisDate)
				);
		return range_query;
	}
	
	
	public int getQuarter(){
		Date date = new Date();
		cal.setTime(date);
		int month = cal.get(Calendar.MONTH); 
		int quarter = (month / 3) + 1;
		return quarter;
	}
	
	
	public String getQuarter(Date date){
		int month  = date.getMonth();
		int quarter_no = (month / 3) + 1;
		int year = date.getYear()-100;
		String quarter = year+"Q"+quarter_no;
		return quarter;
	}
	
	public String getStartDateOfQuarter(Date date){
		int month  = date.getMonth();
		int quarter = (month / 3) + 1;
		int year = 1900+date.getYear();
		String startDate = year+startDates[quarter-1];
		return startDate;
	}
	public String getYear(){
		DateFormat year_yy = new SimpleDateFormat("yyyy"); 
		String formattedDate = year_yy.format(Calendar.getInstance().getTime());
		return formattedDate;
	}
	public Date getStartOfQuarter() throws ParseException{
		int Quarter = getQuarter();
		String year = getYear();
		String startOfQuarter = year+startDates[Quarter-1];
		DateFormat quarter_start_date = new SimpleDateFormat("yyyy-MM-dd");
		Date startDateOfCurrentQuarter = (Date)quarter_start_date.parse(startOfQuarter); 
		return startDateOfCurrentQuarter;
	}
	
	public int getDifferenceFromStartOfQuarter(Date date) throws ParseException{
		Date end_of_analysis_date = date;
		String date_quarter_start = getStartDateOfQuarter(date);
		DateFormat quarter_start_date = new SimpleDateFormat("yyyy-MM-dd");
		Date startDateOfCurrentQuarter = (Date)quarter_start_date.parse(date_quarter_start);
		int difference = (int)ChronoUnit.DAYS.between(startDateOfCurrentQuarter.toInstant(), end_of_analysis_date.toInstant());
		return difference;
	}
	
	public List<String> getAllQuarters(String end_date){
		QtdTimelines dateRangeObject = new QtdTimelines(null, null, end_date);
		BoolQueryBuilder range_query = createRangeQuery(dateRangeObject);
		SearchResponse allQuartersResponse = obj.termAggregtionQuery(range_query);
		List<String> all_quarters = new ArrayList<String>();
		Terms Quarters = allQuartersResponse.getAggregations().get("Quarters");
		for(Terms.Bucket aggregatedQuarters : Quarters.getBuckets()){
			all_quarters.add(aggregatedQuarters.getKey());
		}
		
		return all_quarters;
	}
	
	
	public String getEndDateForAnalysis(String start_date, int difference_in_days) throws ParseException{
		DateFormat quarter_start_date = new SimpleDateFormat("yyyy-MM-dd");
		Date startDateOfQuarter = (Date)quarter_start_date.parse(start_date); 
	    Calendar c = Calendar.getInstance();
	    c.setTime(startDateOfQuarter); 
	    c.add(Calendar.DATE, difference_in_days); 
	    String endDateForAnalysis = quarter_start_date.format(c.getTime());
	    return endDateForAnalysis;
	}
	public List<QtdTimelines> generateTimelineChart(int difference_in_days, String end_date) throws ParseException{
		List<String> all_quarters = getAllQuarters(end_date);
		List<QtdTimelines> QtdTimelineList = new ArrayList<QtdTimelines>();
			for(String quarter: all_quarters){
				int year_yy = Integer.parseInt(quarter.substring(0, 2));
				int quarter_no = Integer.parseInt(quarter.substring(3));
				String start_date = "20"+year_yy+startDates[quarter_no-1];
				String end_date_for_analysis = getEndDateForAnalysis(start_date, difference_in_days);
				QtdTimelines qtdObject = new QtdTimelines(quarter,start_date,end_date_for_analysis);
				QtdTimelineList.add(qtdObject);
		}
		return QtdTimelineList;
	}
	
	public List<QtdTimelines> generateTimeline() throws ParseException{
		Date date = new Date();
		int difference_in_days = getDifferenceFromStartOfQuarter(date);
		String date_string = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		List<QtdTimelines> timelineChart = generateTimelineChart(difference_in_days, date_string);
		return timelineChart;
	}
	   
	public List<QtdTimelines> generateTimeline(int futureDate_difference, String end_date) throws ParseException{
		int difference_in_days = futureDate_difference;
		List<QtdTimelines> timelineChart = generateTimelineChart(difference_in_days, end_date);
		return timelineChart;
	}
	
	public static void main(String[] args) throws ParseException {
		GetQuarter GetQuarterObject = new GetQuarter();
		List<QtdTimelines> timelineChart = GetQuarterObject.generateTimeline();
	}

}
