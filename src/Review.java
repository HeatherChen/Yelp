import java.util.HashMap;


public class Review {
	String r_id;
	String b_id;
	String u_id;
	String text;
	String date;
	double stars;
	HashMap<String, Integer> votes;
	
	public Review(String r_id, String b_id, String u_id, String text, String date, double stars, HashMap<String, Integer> votes){
		this.r_id = r_id;
		this.b_id = b_id;
		this.u_id = u_id;
		this.text = text;
		this.date = date;
		this.stars = stars;
		this.votes = votes;
	}
}
