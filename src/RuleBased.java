import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class RuleBased {
    int feature_num;
    GenData data;
    List<Double> labels = new ArrayList<Double>();
    List<List<Double>> values = new ArrayList<List<Double>>();
    int featuresize = 8;
    BufferedWriter writer;
    BufferedWriter y;
    double[] maxPerFeature = new double[featuresize];
    double[] weightsLeanrt = new double[]{1.25687047, 0.8287024, 0.21196656, 5.12348454, -6.8873236, -0.35090351,-0.35062001, 0.18453315};
    // pasted from output of python
    
    public RuleBased(GenData gen) throws IOException{
    	this.data = gen;
    	writer =  new BufferedWriter(new FileWriter("featureMatrix.txt"));
    	y =  new BufferedWriter(new FileWriter("label.txt"));
    	for(User u:data.userMap.values()){
    		u.addDiffFeature();
    		for(int i=0; i<featuresize; i++){
    		   if(u.features.get(i) > maxPerFeature[i]){
    			   maxPerFeature[i] = u.features.get(i);
    		   }	
    		}    		
    	}
    	
    	for(Business b:data.businessMap.values()){
    		labels.add(b.stars);
    		List<Double> sample;
    		double [] featureForWeight = new double[featuresize];
    		int numuser = 0;
    		for(User u:b.users){
    			numuser++;
    			sample = u.multiply(b, maxPerFeature);
    			aggregate(sample, featureForWeight);
    		}
    		for(int i=0; i<featuresize; i++){
    			writer.write(featureForWeight[i]+"\t");
    		}
    		y.write(numuser*b.stars+"\n");
    		writer.write("\n");
    	}
    	writer.close();
    	y.close();
    	
    	// add reading the weights learnt and then call calcCredibility
    }
    
    public void aggregate(List<Double> sample, double[] featureForWeight){
         for(int i=0; i<sample.size();i++){
        	 featureForWeight[i]+=sample.get(i);
       
         }
    }
    
    public void calcCredibility(double[] weightsLearnt){
    	for(User u:data.userMap.values()){
    		for(int i=0; i<featuresize; i++){
    			u.ruleCredibility += weightsLearnt[i]*u.features.get(i);
    		}    		
    	}
    }
 
    public static void main(String args[]) throws IOException {
		GenData gen = new GenData();
		new RuleBased(gen);
	}
}
