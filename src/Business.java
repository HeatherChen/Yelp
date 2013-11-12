import java.util.HashSet;
import java.util.List;


public class Business {
    double stars;
    double newstars;
    int review_count;
    String id;
    List<String> categories;
    HashSet<User> users;
    
    public Business(String id, List<String> categories, int review_count, double stars){
    	this.id = id;
    	this.categories = categories;
    	this.review_count = review_count;
    	this.stars = stars;
    	this.newstars = stars;
    }
    
}
