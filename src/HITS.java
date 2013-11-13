import java.io.IOException;
import java.util.*;

public class HITS {
	public HITS(){
		super();
	}
	
	public static void hitsScore(HashSet<User> users, HashSet<Business> businesses){
		double prev = 0.0;
		double curr = 0.0;
		while (true){
			// business to user
			double totalCredit = 0.0;
			for (User user : users){
				System.out.println(user.name);
				HashSet<Review> reviews = user.reviews;
				double totalDiff = 0.0;
				for (Review review : reviews){
					String b_id = review.b_id;
					Map<String, Business> businessMap = GenData.getBusinessMap();
					Business b = businessMap.get(b_id);
					totalDiff += Math.abs(b.newstars - review.stars);
				}
				user.credibility = 1.0 / totalDiff;
				totalCredit += user.credibility;
			}
			for (User user : users){
				user.credibility /= totalCredit;
			}


			// user to business
			double totalStar = 0.0;
			
			double newScore = 0.0;
			for (Business business : businesses){
				System.out.println(business.id);
				double totalWeight = 0.0;
				for (User user : business.users){
					HashSet<Review> reviews = user.reviews;
					Review review = getReviewWithBId(reviews, business.id);
					if (review == null)
						System.out.println("no related review found");
					newScore += review.stars * user.credibility;
					totalWeight += user.credibility;
				}
				newScore /= totalWeight;
				business.newstars = newScore;
				
				totalStar += business.newstars - business.stars;
			}
			
			
			System.out.println("total user credit is: " +totalCredit);
			System.out.println("total star difference is: " + totalStar);
			break;
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
