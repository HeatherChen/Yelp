import java.io.IOException;
import java.util.*;


public class Community {
	
	
	public Community(){
		super();
		
	}
	
	private static final int NUMCOMM = 100;
	private static final double DELTA = 1.0;
	
	
	private static HashMap<String, Double>ubStar;
	private static LinkedList<HashSet<User>> clusters;
	
	
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
	
	
	private static double getJaccardBtw2Cluster(HashSet<User>users1, HashSet<User>users2){
		int count = users1.size() * users2.size();
		double totalSim = 0.0;
		for (User user1 : users1){
			for (User user2 : users2){
				totalSim += getJaccardBtw2User(user1, user2);
			}
		}
		return totalSim/count;
	}
	
	
	private static void clustering(HashSet<User> users){
		clusters = new LinkedList<HashSet<User>>();
		for (User user : users){
			HashSet<User> one = new HashSet<User>();
			one.add(user);
			clusters.add(one);
		}
		
		HashSet<User> largest = new HashSet<User>();
		while(clusters.size()!=NUMCOMM){
			System.out.println(clusters.size());
			double largestDist = getJaccardBtw2Cluster(clusters.get(0), clusters.get(1));
			int one = 0;
			int two = 1;
			for (int i = 0; i < clusters.size(); i++){
				for (int j = i + 1; j < clusters.size(); j++){
					double newDist = getJaccardBtw2Cluster(clusters.get(i), clusters.get(j));
					if ( newDist > largestDist ){
						largestDist = newDist;
						one = i;
						two = j;
					}
				}
			}
			HashSet<User> newCluster = new HashSet<User>();
			for (User user : clusters.get(one))
				newCluster.add(user);
			for (User user : clusters.get(two))
				newCluster.add(user);
			clusters.remove(one);
			clusters.remove(two);
			clusters.add(newCluster);
		}
	}
	
	
	
	public static void main(String args[]){
		try {
			GenData data = new GenData();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HashSet<User> users = new HashSet<User>(GenData.getUserMap().values());
		HashSet<Business> businesses = new HashSet<Business>(GenData.getBusinessMap().values());
		HashSet<Review> reviews = new HashSet<Review>(GenData.getReviewMap().values());
		
		Community.fillUBStar(reviews);
		System.out.println("fillUBStar finished");
		Community.clustering(users);
	}
}
