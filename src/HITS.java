import java.io.IOException;
import java.util.*;

public class HITS {
	public HITS(){
		super();
	}
	
	public static void hitsScore(HashSet<User> users, HashSet<Business> businesses){
		double prev = 0.0;
		double curr = 0.0;
		
		int time = 0;
		int total = businesses.size();
		
		while (true){
			
//			if (time == 10)
//				break;
//			
			time++;
			
			int changed = 0;
			
			// business to user
			double totalCredit = 0.0;
			for (User user : users){
				//System.out.println("init" + user.credibility);
				HashSet<Review> reviews = user.reviews;
				double totalDiff = 0.0;
				for (Review review : reviews){
					String b_id = review.b_id;
					Map<String, Business> businessMap = GenData.getBusinessMap();
					Business b = businessMap.get(b_id);
					totalDiff += Math.abs(b.newstars - review.stars);
				}
				user.credibility = 1.0 /(1 +  totalDiff);
				totalCredit += user.credibility;
			}
			double newTotalCredit = 0.0;
			for (User user : users){
				user.credibility /= totalCredit;
				newTotalCredit += user.credibility;
			}


			// user to business
			double totalStar = 0.0;
			
			
			int i = 0;
			boolean flag = false;
			for (Business business : businesses){
				double totalWeight = 0.0;
				double newScore = 0.0;
				for (User user : business.users){
					HashSet<Review> reviews = user.reviews;
					Review review = getReviewWithBId(reviews, business.id);
					if (review == null)
						System.out.println("no related review found");
					newScore += review.stars * user.credibility;
					totalWeight += user.credibility;
				}
				
//				if (i >= 2100 && i < 3160){
//					System.out.println(i);
//					System.out.println(business.newstars);
//					System.out.println(newScore);
//					System.out.println(totalWeight);
//					System.out.println(newScore/totalWeight);
//					System.out.println("------------");
//				}
				
				newScore /= totalWeight;
				business.newstars = newScore;
				i++;
				totalStar += (business.newstars - business.stars);
				
				
				if (Math.abs((business.newstars - business.prevstars)/business.newstars) < 0.005){
					changed += 1;
				}
				
				business.prevstars = business.newstars;
				
				if (Double.isNaN(business.newstars) ){
					flag = true;
					System.out.println(i);
					System.out.println(business.id);
					System.out.println("shit!");
				}
				
//				if (i >= 2700 && i < 3000){
//					System.out.println(i);
//					System.out.println(business.newstars);
//					System.out.println(business.stars);
//					System.out.println("------------");
//					System.out.println("###############");
//					System.out.println(totalStar);
//					System.out.println("###############");
//				}
				//System.out.println("###############");
				//System.out.println(totalStar);
				//System.out.println("###############");
				//if (i < 100)
					//System.out.println(business.newstars);
					//System.out.println(business.stars);
					//System.out.println("------------");
			}
			
			System.out.println("total user credit at " + i + "is: " +newTotalCredit);
			System.out.println("total star difference is: " + totalStar);
			
			if ((double)changed/total > 0.99){
				System.out.println("stop at: " + time);
				break;
			}
		}
	}
	
	
	public static Review getReviewWithBId(HashSet<Review> reviews ,String b_id){
		for (Review review : reviews){
			if (review.b_id.equals(b_id))
				return review;
		}
		return null;
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
		hitsScore(users, businesses);
	}
}
