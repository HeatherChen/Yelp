import java.util.HashMap;
import java.util.HashSet;


public class User {
    double credibility = 3.0; //default
    String u_id;
    String name;
    int review_count;
    double average_stars;
    HashMap<String, Integer> votes;
    HashSet<Business> businesses;
    HashSet<Review> reviews;
    
    public User(String u_id, String name, int review_count, double average_stars, HashMap<String, Integer> votes){
    	this.u_id = u_id;
    	this.name = name;
    	this.review_count = review_count;
    	this.average_stars = average_stars;
    	this.votes = votes;
    }
}
