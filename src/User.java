import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


public class User {
    double credibility = 0.5; //default
    double ruleCredibility;
    String u_id;
    String name;
    int review_count;
    double average_stars;
    HashMap<String, Integer> votes;
    HashSet<Business> businesses = new HashSet<Business>();
    HashSet<Review> reviews = new HashSet<Review>();
    List<Double> features = new ArrayList<Double>();
    
    public User(String u_id, String name, int review_count, double average_stars, HashMap<String, Integer> votes){
    	this.u_id = u_id;
    	this.name = name;
    	this.review_count = review_count;
    	this.average_stars = average_stars;
    	this.votes = votes;
    	int totalvotes = votes.get("funny")+votes.get("useful")+votes.get("cool");
    	features.add(average_stars);
    	features.add(new Double((double)review_count));
    	features.add(new Double((double)votes.get("funny")));
    	features.add(new Double((double)votes.get("useful")));
    	features.add(new Double((double)votes.get("cool")));
    	features.add(new Double((double)totalvotes));
    }
    
    public void addDiffFeature(){
    	HashMap<String, Double> difference = new HashMap<String, Double>();
    	for(Business b: businesses){
    		double truescore = b.stars;
    		//difference.put(b.id, b.stars);
    		for(Review r:reviews){
    			if(r.b_id.equals(b.id)){
    				difference.put(b.id, r.stars-truescore);
    			}
    		}
    	}
    	double total = 0;
    	double abstotal = 0;
    	for(String bid:difference.keySet()){
    		total+=difference.get(bid);
    		abstotal += Math.abs(difference.get(bid));
    	}
    	features.add(total);
    	features.add(abstotal);

    }
    
    public List<Double> multiply(Business bus, double[] maxPerFeature){
    	double scoregiven = 0;
    	List<Double> sample = new ArrayList<Double>();
    	for(Review r:reviews){
    		if(r.b_id.equals(bus.id)){
    			scoregiven = r.stars;
    			break;
    		}
    	}
    	for(int i=0; i<features.size();i++){
    		sample.add(features.get(i)*scoregiven/maxPerFeature[i]);
    		features.set(i, features.get(i)/maxPerFeature[i]); 
    	}
    	return sample;
    }
}
