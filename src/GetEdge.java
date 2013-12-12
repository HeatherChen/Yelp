import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import util.CounterMap;


public class GetEdge {
	private static ArrayList<String> clusters;
	
	
	
	public static void main(String args[]) throws IOException{
		clusters = new ArrayList<String> ();
		BufferedReader br = new BufferedReader(new FileReader("clusters.txt"));
		BufferedWriter bw = new BufferedWriter(new FileWriter("edges.txt"));
		String newLine;
		ArrayList<String> edges = new ArrayList<String>();
		int counter = 0;
		while ((newLine = br.readLine()) != null) {
			counter ++;
			if (counter % 30 == 0)
				edges.clear();
			StringTokenizer str = new StringTokenizer(newLine);
			String user = str.nextToken();
			while (str.hasMoreTokens()) {
				edges.add(str.nextToken());
			}
			if (counter %10 == 0 ){
				for (int i = 0; i < edges.size(); i++){
					for (int j = i + 1; j < edges.size(); j++){
						bw.write(edges.get(i) + " " + edges.get(j) + " 1");
						bw.newLine();
					}
				}
			}
		}
		for (int i = 0; i < edges.size(); i++){
			for (int j = i + 1; j < edges.size(); j++){
				bw.write(edges.get(i) + " " + edges.get(j) + " 1");
				bw.newLine();
			}
		}
		br.close();
		bw.close();
		
		
	}

}
