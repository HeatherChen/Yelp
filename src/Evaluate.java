import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.StringTokenizer;

import util.CounterMap;


public class Evaluate {
	private static HashMap<String, Double>ubStar;
	private static HashMap<String, HashSet<String>> clusters;
	private static CounterMap<String, String> ubScore;
	
	private static void fillUBStar(HashSet<Review> reviews){
		ubStar = new HashMap<String, Double>();
		ubScore = new CounterMap<String, String> ();
		for (Review review : reviews){
			String keyV = review.u_id + ";" + review.b_id;
			Double starV = review.stars;
			ubStar.put(keyV, starV);
			ubScore.setCount(review.u_id,review.b_id,starV);
		}
	}
	
	public static double getPredictedScore(String user, Business business, Map<String, User> userMap){
		//String u_id = user.u_id;
		String b_id = business.id;
		String ubKey = user + ";" + b_id;
		Double star = ubStar.get(ubKey);
		if (star == null)
			return -2.0;
		for (HashSet<String> cluster : clusters.values()){
			if (cluster.contains(user)){
				double total = 0.0;
				int count = 0;
				double credTnt = 0.0;
				for (String user1 : cluster){
					Double otherStar = ubStar.get(user1 + ";" + business.id);
					User userO = userMap.get(user1);
					if (otherStar != null){
						count += 1;
						double credibility = userO.credibility;
						total += otherStar*credibility;
						credTnt += credibility;
					}
				}
				if (count != 0){
					double newScore = (total/credTnt + business.newstars)/(2);
					return newScore;
				}else{
					return -1.0;
				}
			}
		}
		return -1.0;
	}
	
	static Boolean othersHasBeenTo(HashSet<String> cluster, String bid, String uid) {
		for (String user: cluster) {
			if (!user.equals(uid)) {
				if (ubScore.getCount(user, bid) != 0) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static void main(String args[]) throws IOException{
		try {
			GenData data = new GenData();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HITS hits = new HITS();
		HashSet<User> users = new HashSet<User>(GenData.getUserMap().values());
		HashSet<Business> businesses = new HashSet<Business>(GenData.getBusinessMap().values());
		HITS.hitsScore(users, businesses);
		clusters = new HashMap<String, HashSet<String>> ();
		HashSet<Review> reviews = new HashSet<Review>(GenData.getReviewMap().values());
		fillUBStar(reviews);
		BufferedReader br = new BufferedReader(new FileReader("clusters.txt"));
		String newLine;
		while ((newLine = br.readLine()) != null) {
			StringTokenizer str = new StringTokenizer(newLine);
			String user = str.nextToken();
			//User user = GenData.userMap.get(userStr);
			HashSet<String> userCluster = new HashSet<String> ();
			while (str.hasMoreTokens()) {
				userCluster.add(str.nextToken());
			}
			clusters.put(user, userCluster);
		}
		br.close();
		int totalCnt = 0;
		int correctCnt = 0;
		for (HashSet<String> cluster : clusters.values()){
			for (String uid : cluster) {
				for (String bid:ubScore.getCounter(uid).keySet()) {
					if (othersHasBeenTo(cluster, bid, uid)) {
						totalCnt ++;
						if (Math.abs(getPredictedScore(uid, GenData.businessMap.get(bid),GenData.userMap) - ubScore.getCount(uid, bid)) <= 0.5) {
							correctCnt ++;
						}
					}
				}
			}
		}
	
		System.out.println("Result:");
		System.out.println(totalCnt);
		System.out.println(correctCnt);
		System.out.println((double)correctCnt/totalCnt);
		
		
	}

}
