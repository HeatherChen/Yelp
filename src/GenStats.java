import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.BoxAndWhiskerToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


public class GenStats {
	Map<String, Business> businessMap;
	Map<String, Review> reviewMap;
	Map<String, User> userMap;
	class ValueComparator implements Comparator<String> {

		Map<String, Integer> base;
		public ValueComparator(Map<String, Integer> base) {
			this.base = base;
		}

		// Note: this comparator imposes orderings that are inconsistent with equals.    
		public int compare(String a, String b) {
			if (base.get(a) >= base.get(b)) {
				return -1;
			} else {
				return 1;
			} // returning 0 would merge keys
		}
	}

	public GenStats() throws IOException {
		GenData data = new GenData();
		System.out.println("finish reading data");
		businessMap = GenData.getBusinessMap();
		reviewMap = GenData.getReviewMap();
		userMap = GenData.getUserMap();
	}
	public void categoryDist() {
		int totalCategoryCnt = 0;
		int totalBusinessCnt = 0;
		Map<String, Integer> categoryCntMap = new HashMap<String,Integer>();
		for (Business business:businessMap.values()) {
			//System.out.println("hello");
			for (String category:business.categories) {
				totalBusinessCnt += 1;
				if (!categoryCntMap.containsKey(category)) {
					categoryCntMap.put(category,0);
					totalCategoryCnt += 1;
				}
				categoryCntMap.put(category,categoryCntMap.get(category)+1);
			}
		}
		ValueComparator bvc =  new ValueComparator(categoryCntMap);
		//		TreeMap<String, Integer> sorted_map_1 = new TreeMap<String, Integer> ();
		//		sorted_map_1.put("a",1);
		//		for (String a: sorted_map_1.keySet()) {
		//			System.out.println(a);
		//			System.out.println(sorted_map_1.get(a));
		//		}
		TreeMap<String,Integer> sorted_map = new TreeMap<String,Integer>(bvc);
		for (String key:categoryCntMap.keySet()) {
			sorted_map.put(key,categoryCntMap.get(key));
		}
		//sorted_map.putAll(categoryCntMap);
		int count = 0;
		DefaultPieDataset pieDataset = new DefaultPieDataset();
		double percentTot = 0.0;
		System.out.println("begin plotting");
		System.out.println(sorted_map.keySet());
		System.out.println(sorted_map.values());
		List<String> keys = new ArrayList<String> (sorted_map.keySet());
		List<Integer> values = new ArrayList<Integer>(sorted_map.values());
		//		for (String key:sorted_map.keySet()) {
		//			//System.out.println("hello");
		//			System.out.println(key);
		//			System.out.println(categoryCntMap.get(key));
		//			System.out.println(sorted_map.get(key));
		//			double percent = (double)sorted_map.get(key)/totalBusinessCnt;
		//			percentTot += percent;
		//			pieDataset.setValue(key, percent);
		//			count ++;
		//			if (count == 10) break;
		//		}

		for (int i = 0; i < keys.size(); i++) {
			double percent = (double)values.get(i)/totalBusinessCnt;
			percentTot += percent;
			pieDataset.setValue(keys.get(i), percent);
			count ++;
			if (count == 10) break;
		}
		System.out.println(percentTot);
		if (percentTot != 1) {
			pieDataset.setValue("other",1-percentTot);
		}

		JFreeChart chart = ChartFactory.createPieChart ("Category Distribution", pieDataset, true, true, false);
		try {
			ChartUtilities.saveChartAsJPEG(new File("piechart.jpg"), chart, 500, 300);
		} catch (Exception e) {
			System.out.println("Problem occurred creating chart.");
		}
	}
	public void businessScoreDist() {
		double totalScore = 0;
		int totalBusinessCnt = 0;
		Map<Double, Integer> businessScoreMap = new TreeMap<Double, Integer>();
		for (Business business:businessMap.values()) {
			//System.out.println("hello");
			totalBusinessCnt += 1;
			double star = business.stars;
			totalScore += star;
			if (!businessScoreMap.containsKey(star)) {
				businessScoreMap.put(star,0);
			}
			businessScoreMap.put(star,businessScoreMap.get(star)+1);
		}
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (Double d:businessScoreMap.keySet()) {
			dataset.setValue((double)businessScoreMap.get(d)/totalBusinessCnt,"Percentage",String.valueOf(d));
		}
		JFreeChart chart = ChartFactory.createBarChart("Business Score Distribution",
				"Business Score", "Percentage", dataset, PlotOrientation.VERTICAL, false, true, false);
		try {
			ChartUtilities.saveChartAsJPEG(new File("barchart.jpg"), chart, 500, 300);
		} catch (IOException e) {
			System.err.println("Problem occurred creating chart.");
		}
	}

	public double roundScore(double score) {
		double result = Math.floor(score);
		if ((score-result) < 0.25) return result;
		else if ((score-result) < 0.75) return result+0.5;
		else return result+1; 
	}

	public void UserAvgStarDist() {
		int totalUserCnt = 0;
		Map<Double, Integer> UserAvgStarMap = new TreeMap<Double, Integer>();
		for (User user:userMap.values()) {
			//System.out.println("hello");
			double score = roundScore(user.average_stars);
			totalUserCnt += 1;
			if (!UserAvgStarMap.containsKey(score)) {
				UserAvgStarMap.put(score,0);
			}
			UserAvgStarMap.put(score,UserAvgStarMap.get(score)+1);
		}
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (Double d:UserAvgStarMap.keySet()) {
			dataset.setValue((double)UserAvgStarMap.get(d)/totalUserCnt,"Percentage",String.valueOf(d));
		}
		JFreeChart chart = ChartFactory.createBarChart("User Average Score Distribution",
				"Avg Score", "Percentage", dataset, PlotOrientation.VERTICAL, false, true, false);
		try {
			ChartUtilities.saveChartAsJPEG(new File("barchart_user.jpg"), chart, 500, 300);
		} catch (IOException e) {
			System.err.println("Problem occurred creating chart.");
		}
	}

	double calculateAverage(List <Integer> marks) {
		double sum = 0;
		if(!marks.isEmpty()) {
			for (int mark : marks) {
				sum += mark;
			}
			return  (double)sum / marks.size();
		}
		return sum;
	}

	public void scoreCountCorrelate() {
		double totalScore = 0;
		int totalBusinessCnt = 0;
		Map<Double, List<Integer>> businessScoreCountMap = new TreeMap<Double, List<Integer>>();
		for (Business business:businessMap.values()) {
			//System.out.println("hello");
			totalBusinessCnt += 1;
			double star = business.stars;
			totalScore += star;
			if (!businessScoreCountMap.containsKey(star)) {
				businessScoreCountMap.put(star,new ArrayList<Integer> ());
			}
			businessScoreCountMap.get(star).add(business.review_count);
		}
		//		DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
		//		for (Double d:businessScoreCountMap.keySet()) {
		//			System.out.println(Collections.max(businessScoreCountMap.get(d)));
		//			System.out.println(businessScoreCountMap.get(d));
		//			dataset.add(businessScoreCountMap.get(d),String.valueOf(d),String.valueOf(d));
		//		}
		//		CategoryAxis xAxis = new CategoryAxis("Business Score");
		//		final NumberAxis yAxis = new NumberAxis("Review Count");
		//		yAxis.setAutoRangeIncludesZero(false);
		//		final BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
		//		renderer.setFillBox(false);
		//		renderer.setToolTipGenerator(new BoxAndWhiskerToolTipGenerator());
		//		final CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis, renderer);
		//
		//		final JFreeChart chart = new JFreeChart(
		//				"Review Count vs Business Score Box Plot",
		//				new Font("SansSerif", Font.BOLD, 14),
		//				plot,
		//				true
		//				);
		XYSeries series = new XYSeries("XYGraph");
		XYSeries series2 = new XYSeries("XYGraph2");
		XYSeries series3 = new XYSeries("XYGraph3");
		for (Double d:businessScoreCountMap.keySet()) {
			//series.add(d,Collections.max(businessScoreCountMap.get(d)));
			//series2.add(d,Collections.min(businessScoreCountMap.get(d)));
			series3.add(d, new Double(calculateAverage(businessScoreCountMap.get(d))));
			//series3.add(d,avg);
			//series3.add(d,avg);
		}
		// Add the series to your data set
		XYSeriesCollection dataset = new XYSeriesCollection();
		//dataset.addSeries(series);
		//dataset.addSeries(series2);
		dataset.addSeries(series3);
		// Generate the graph
		JFreeChart chart = ChartFactory.createXYLineChart(
				"Review Count vs Business Score Plot",
				"Business Score",
				"Review Count",
				dataset,
				PlotOrientation.VERTICAL,  // Plot Orientation
				true,                      // Show Legend
				true,                      // Use tooltips
				false                      // Configure chart to generate URLs?
				);


		try {
			ChartUtilities.saveChartAsJPEG(new File("maxplot.jpg"), chart, 800, 400);
		} catch (IOException e) {
			System.err.println("Problem occurred creating chart.");
		}
	}
	public static void main(String args[]) throws IOException {
		GenStats stats = new GenStats();
		stats.categoryDist();
		stats.businessScoreDist();
		stats.UserAvgStarDist();
		stats.scoreCountCorrelate();
	}

}
