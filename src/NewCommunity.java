import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.PriorityQueue;

import util.*;

public class NewCommunity {
	
	
	public NewCommunity(){
		super();
		
	}
	
	private static final int NUMCOMM = 100;
	private static final double DELTA = 1.0;
	private static final double SIM = 0.45;
	
	
	
	private static HashMap<String, Double>ubStar;
	private static HashMap<User, HashSet<User>> clusters;
	private static CounterMap<User, User> bigramCounterMap;
	private static int numEdges; //m
	private static HashMap<User,PQUser> sparseMtx;// delta Q
	
	private static PriorityQueue<deltaQ> maxHeap; //H
	private static CounterMap<User, User> deltaQMap;
	
	private static HashMap<User, Double> aMap;
	
	
	private static void fillUBStar(HashSet<Review> reviews){
		ubStar = new HashMap<String, Double>();
		for (Review review : reviews){
			String keyV = review.u_id + ";" + review.b_id;
			Double starV = review.stars;
			ubStar.put(keyV, starV);
		}
	}
	
	// Jaccard Similarity
	private static double getJaccardBtw2User(User u1, User u2){
		double same = 0.0;
		for (Business business1 : u1.businesses){
			for (Business business2 : u2.businesses){
				if(business1.id.equals(business2.id)){
					double star1 = ubStar.get(u1.u_id + ";" + business1.id);
					double star2 = ubStar.get(u2.u_id + ";" + business2.id);
					if (Math.abs(star1 - star2) <= DELTA){
						same += 1;
					}
				}
			}
		}
		
		int total = u1.businesses.size() + u2.businesses.size();
		return same/total;
	}
	

	private static void getAdjMtx(ArrayList<User> users){
		bigramCounterMap = new CounterMap<User, User>();
		for (int i = 0; i < users.size(); i++){
			for (int j = i + 1; j < users.size(); j++){
				User user1 = users.get(i);
				User user2 = users.get(j);
				double JaccardSim = getJaccardBtw2User(user1, user2);
				if (JaccardSim > SIM){
					numEdges += 1;
					//if (user1.u_id.compareTo(user2.u_id) <= 0) {
					bigramCounterMap.incrementCount(user1, user2, JaccardSim);
					//} else {
					bigramCounterMap.incrementCount(user2, user1, JaccardSim);
					//}
				}
			}
			if (i%1000 == 0)
				System.out.println(i);
		}
	}
	
	private static double getK(User user) {
		return bigramCounterMap.getCounter(user).totalCount();
	}
	
	private static void getInitDeltaQ(){
		deltaQMap = new CounterMap<User, User>();
		aMap = new HashMap<User, Double> ();
		for (User user1 : bigramCounterMap.keySet()) {
			Double user1_k = getK(user1);
			for (User user2 : bigramCounterMap.getCounter(user1).keySet()) {
				Double user2_k = getK(user2);
				double value = (double) 1 / (2*numEdges) - user1_k*user2_k/Math.pow((2*numEdges), 2);
				deltaQMap.setCount(user1, user2, value);
				deltaQMap.setCount(user2, user1, value);
				//System.out.println(value);
			}
			aMap.put(user1, user1_k/(2*numEdges));
		}
	}
	
	private static Double getQ(User one, User two){
		if (one.compareTo(two) <= 0)
			return deltaQMap.getCount(one, two);
		else
			return deltaQMap.getCount(two, one);
	}
	
	
	private static void putQ(User one, User two, double q){
		//if (one.compareTo(two) <= 0)
			deltaQMap.setCount(one, two, q);
		//else
			deltaQMap.setCount(two, one, q);
	}
	
	
	private static void mergeCommunity(ArrayList<User> users){
		// set initial clusters, it only contains the new users (17430)
		clusters = new HashMap<User, HashSet<User>>();
		Set<User> newUsers = bigramCounterMap.keySet();
//		for (User user : newUsers){
//			HashSet<User> one = new HashSet<User>();
//			one.add(user);
//			clusters.add(one);
//		}
		
		// set initial sparseMtx
		sparseMtx = new HashMap<User,PQUser>();
		for (User user : newUsers){
			PQUser pq = new PQUser(user);
			Set<User> row = bigramCounterMap.getCounter(user).keySet();
			for (User userInRow : row){
				// user < userInRow
				pq.add(userInRow);
				sparseMtx.put(user,pq);
			}
		}
		
		// populate the max-heap with the largest element of each row
		maxHeap = new PriorityQueue<deltaQ>(20000, Collections.reverseOrder());
		for (User user : sparseMtx.keySet()){
			User user2 = sparseMtx.get(user).peek();
			maxHeap.add(new deltaQ(user, user2, deltaQMap.getCount(user, user2)));
		}
		
		int count = newUsers.size();
		//step 2
		while (count != 0){
			count -= 1;
			if (count%1000 == 0)
				System.out.println("count is " + count);
			
			
			deltaQ largest = maxHeap.peek();
			if (largest == null)
				break;
			// i is already < j
			User i = largest.getI();
			if (i == null)
				System.out.println("i is null");
			User j = largest.getJ();
			if (j == null)
				System.out.println("j is null");
			//join largest i and largest j
			
			// 1. update the deltaQMap, remove i row, modify j row
			Set<User> ithRow = deltaQMap.getCounter(i).keySet();
			if (ithRow == null)
				System.out.println("ithRow is null");
			Set<User> jthRow = deltaQMap.getCounter(j).keySet();
			if (jthRow == null)
				System.out.println("jthRow is null");
				// traverse k in (ithRow, jthRow), update j row in deltaQMap
			for (User k: ithRow){
				if (jthRow.contains(k)){
					double newQ = getQ(i,k) + getQ(j,k);
					putQ(j,k,newQ);
				}else{
					Double getQP = getQ(i,k);
					if (getQP == null)
						System.out.println("getQ is null");
					Double getJ = aMap.get(j);
					if (getJ == null){
						System.out.println("getJ is null");
					}
					Double getK = aMap.get(k);
					if (getK == null)
						System.out.println("getK is null");
					double newQ = getQ(i,k) - 2 * aMap.get(j) * aMap.get(k);
					putQ(j,k,newQ);
				}
			}
			
			for (User k : jthRow){
				if (!ithRow.contains(k)){
					double newQ = getQ(j,k) - 2 * aMap.get(i) * aMap.get(k);
					putQ(j,k,newQ);
				}
			}
			
			// countermap deltaQMap removes ith row
			deltaQMap.rmEntry(i);
			
			// remove the ith PriorityQueue(sparseMtx)
			sparseMtx.remove(i);
			
			for (PQUser pqu: sparseMtx.values()){
				// modify element in every PriorityQueue ith column and heapify
				if (pqu.contains(i)){
					pqu.remove(i);
				}
			}
					

			// 3. update clusters
//			User large = i.compareTo(j) >= 0 ? i : j;
//			User small = i.compareTo(j) < 0 ? i : j;
			if (clusters.containsKey(i) && clusters.containsKey(j)){
				clusters.get(j).addAll(clusters.get(i));
				clusters.remove(i);
			} else if (clusters.containsKey(j) && !clusters.containsKey(i)){
				clusters.get(j).add(i);
			} else if (clusters.containsKey(i) && !clusters.containsKey(j)){
				HashSet<User> newClu = new HashSet<User>(clusters.get(i));
				newClu.add(j);
				clusters.remove(i);
				clusters.put(j, newClu);
			} else{
				HashSet<User> newClu = new HashSet<User>();
				newClu.add(i);
				newClu.add(j);
				clusters.put(j, newClu);
			}
			
			
			// 4. update aMap
			aMap.put(j, aMap.get(i) + aMap.get(j));
			aMap.put(i, 0.0);
			
			// 5. update maxHeap
			maxHeap.clear();
			for (User user : sparseMtx.keySet()){
				User user2 = sparseMtx.get(user).peek();
				if (user2 != null)
					maxHeap.add(new deltaQ(user, user2, deltaQMap.getCount(user, user2)));
			}
		}
	}
	
	//TODO 
	//may be wrong
	private static class PQUser extends PriorityQueue<User> implements Comparator<User>{
		private User user1;
		//PriorityQueue<User> pq;
		
		public PQUser(User user1){
			this.user1 = user1;
			//this.pq = new PriorityQueue<User>();
		}
		
		@Override
		public int compare(User u1, User u2){
			double diff = deltaQMap.getCount(user1, u1) - deltaQMap.getCount(user1, u2);
			if ( diff > 0)
				return -1;
			else if (diff < 0)
				return 1;
			else
				return 0;
		}
		
	}
	
	public static double getPredictedScore(User user, Business business){
		String u_id = user.u_id;
		String b_id = business.id;
		String ubKey = u_id + ";" + b_id;
		Double star = ubStar.get(ubKey);
		if (star == null)
			return -2.0;
		for (HashSet<User> cluster : clusters.values()){
			if (cluster.contains(user)){
				double total = 0.0;
				int count = 0;
				for (User user1 : cluster){
					Double otherStar = ubStar.get(user1.u_id + ";" + business.id);
					if (otherStar != null){
						count += 1;
						total += otherStar;
					}
				}
				if (count != 0){
					double newScore = (total + count * business.newstars)/(count * 2);
					return newScore;
				}else{
					return -1.0;
				}
			}
		}
		return -1.0;
	}
	
	public static void main(String args[]) throws IOException{
		try {
			GenData data = new GenData();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<User> users = new ArrayList<User>(GenData.getUserMap().values());
		//HashSet<Business> businesses = new HashSet<Business>(GenData.getBusinessMap().values());
		HashSet<Review> reviews = new HashSet<Review>(GenData.getReviewMap().values());
		
		NewCommunity.fillUBStar(reviews);
		System.out.println("fillUBStar finished");
		NewCommunity.getAdjMtx(users);
		System.out.println(NewCommunity.bigramCounterMap.keySet().size());
		System.out.println(NewCommunity.numEdges);
		
		NewCommunity.getInitDeltaQ();
		
		System.out.println(NewCommunity.aMap.size());
		
		NewCommunity.mergeCommunity(users);
		BufferedWriter writer = new BufferedWriter(new FileWriter("clusters.txt"));
		for (User user : NewCommunity.clusters.keySet()){
			if (clusters.get(user).size() >= 10){
				writer.write(user.u_id + "	");
				for (User userInCluster : clusters.get(user)){
					writer.write(userInCluster.u_id + "	");
				}
				writer.newLine();
			}
		}
		writer.close();
		
	}
}
