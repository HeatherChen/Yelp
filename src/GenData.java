import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.*;

public class GenData {

	BufferedReader reader1;
	BufferedReader reader2;
	BufferedReader reader3;
	//BufferedReader reader4;
	Map<String, Business> businessMap = new HashMap<String, Business>();
	Map<String, Review> reviewMap = new HashMap<String, Review>();
	Map<String, User> userMap = new HashMap<String, User>();
	
	public GenData() throws IOException{
		reader1 = new BufferedReader(new FileReader("yelp_phoenix_academic_dataset/yelp_academic_dataset_business.json"));
		reader2 = new BufferedReader(new FileReader("yelp_phoenix_academic_dataset/yelp_academic_dataset_user.json"));
		reader3 = new BufferedReader(new FileReader("yelp_phoenix_academic_dataset/yelp_academic_dataset_review.json"));
		//reader4 = new BufferedReader(new FileReader("yelp_phoenix_academic_dataset/test.json"));
	}
	
	Map<String, Business> getBusinessMap() {
		return businessMap;
	}
	
	Map<String, Review> getReviewMap() {
		return reviewMap;
	}
	
	Map<String, User> getUserMap() {
		return userMap;
	}
	
	public void getBusiness() throws IOException{
		while (true) {
			String line = reader1.readLine();
			if (line==null) break;
			try{
				JSONObject business = new JSONObject(line);
				String b_id = business.getString("business_id");
				List<String> categories = new ArrayList<String>();
				JSONArray rawcategories = business.getJSONArray("categories");
				for(int i=0; i<rawcategories.length();i++){
					categories.add(rawcategories.getString(i));
				}
				
				int review_count = business.getInt("review_count");
				double stars = business.getDouble("stars");
				Business b = new Business(b_id, categories, review_count, stars);
				businessMap.put(b_id, b);
			}catch(JSONException e) {
				e.printStackTrace();
				System.out.print(line + System.getProperty("line.separator"));
			}
			
		}

	}
	
	public void getReview() throws IOException{
		while (true) {
			String line = reader3.readLine();
			if (line==null) break;
			try{
				JSONObject review = new JSONObject(line);
				String b_id = review.getString("business_id");
				String u_id = review.getString("user_id");
				String r_id = review.getString("review_id");
				String text = review.getString("text");
				String date = review.getString("date");
				double stars = review.getDouble("stars");
				HashMap<String, Integer> votes = new HashMap<String, Integer>();
				JSONObject vote = review.getJSONObject("votes");
				votes.put("funny", vote.getInt("funny"));
				votes.put("useful", vote.getInt("useful"));
				votes.put("cool", vote.getInt("cool"));
				
				Review r = new Review(r_id, b_id, u_id, text, date, stars, votes);
				reviewMap.put(r_id, r);
				
				//Update user and business
				Business b = businessMap.get(b_id);
				User u = userMap.get(u_id);
				b.users.add(u);
				 
				u.businesses.add(b);
				u.reviews.add(r);
				
			}catch(JSONException e) {
				e.printStackTrace();
				System.out.print(line + System.getProperty("line.separator"));
			}
			
		}
	}
	
	public void getUser() throws IOException{
		while(true){
			String line = reader2.readLine();
			if (line == null) break;
			try {
				JSONObject user = new JSONObject(line);
				String u_id = user.getString("user_id");
				String u_name = user.getString("name");
				int u_reviewCount = user.getInt("review_count");
				double u_avgStar = user.getDouble("average_stars");
				HashMap<String, Integer> votes = new HashMap<String, Integer>();
				JSONObject vote = user.getJSONObject("votes");
				votes.put("funny", vote.getInt("funny"));
				votes.put("useful", vote.getInt("useful"));
				votes.put("cool", vote.getInt("cool"));
				User u  = new User(u_id, u_name, u_reviewCount, u_avgStar, votes);
				userMap.put(u_id, u);
			}catch(JSONException e) {
				e.printStackTrace();
				System.out.print(line + System.getProperty("line.separator"));
			}
		}
	}
	
	public static void main(String args[]) throws IOException {
		GenData data = new GenData();
		data.getBusiness();
		data.getUser();
		data.getReview();
	}
}
