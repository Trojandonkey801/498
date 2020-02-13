import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Iterator;
import java.util.Set;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.ArrayList;
public class NaiveBayes{
	boolean print = false;
	static final int vocabcount = 61188;
	static Document[] allDocs = new Document[11269];
	public float[] prioris = new float[20];
	int[] numDocClass = new int[20]; // Number of documents in each class
	int[] numWordClass = new int[20]; // Number of words in each class
	int[][] wordOccurencePerClass = new int[20][61188];
	float[][] MLE = new float[20][61188];
	float[][] PBE = new float[20][61188];
	int[][] confusion_MLE = new int[20][20];
	int[][] confusion_PBE = new int[20][20];

	public void Read_In(){
		File F = new File("train_data.csv");
		File FF = new File("train_label.csv");
		int classes[] = new int[11269];
		try{
			Scanner S = new Scanner(F);
			Scanner SS = new Scanner(FF);
			int count = 0;
			while(SS.hasNextLine()){
				classes[count] = Integer.valueOf(SS.nextLine());
				allDocs[count] = new Document();
					count++;
			}
			while(S.hasNextLine()){
				String line = S.nextLine();
				String[] splitted = line.split(",");
				int ref = Integer.valueOf(splitted[0])-1;
				allDocs[ref].class_ID = classes[ref];
				allDocs[ref].ID = ref;
				allDocs[ref].Word_Occurence.put(Integer.valueOf(splitted[1]),Integer.valueOf(splitted[2]));
			}
		}catch(FileNotFoundException e){
			System.out.println(e);
		}
	}
	public class Document{
		public int ID;
		public int class_ID;
		HashMap<Integer,Integer> Word_Occurence = new HashMap<Integer,Integer>();
		int getID(){
			return ID;
		}
		int getclass_ID(){
			return class_ID;
		}
		void print_Doc(){
			System.out.println("ID :" + ID);
			System.out.println("class_ID :" + class_ID);
		}
	}
	public void calcuatePriori(){
		for (int i = 0; i < 11269; i++) {
			numDocClass[allDocs[i].class_ID-1]++;
		}
		for (int i = 0; i < 20; i++) {
			prioris[i] = ((float)numDocClass[i])/11269;
		}
	}

	/**
	 * This function counts the total number of words in a document class W_j
	 * It traverses all of the documents, and increments an array index corresponding to the class index
	 * 
	 */
	public void countWordsInClass(){
		for (int i = 0; i < allDocs.length; i++) {
			for (Integer key : allDocs[i].Word_Occurence.keySet()) {
				int val = allDocs[i].Word_Occurence.get(key);
				numWordClass[allDocs[i].class_ID-1] += val;
			}
		}
	}

	/**
	 * This function Counts the occurence of each word for each document class
	 * It creates a 2 dimensional array 
	 *
	 */
	public void countWordOccurrenceInClass(){
		for (int i = 0; i < allDocs.length; i++) {
			for (Integer key : allDocs[i].Word_Occurence.keySet()) {
				int val = allDocs[i].Word_Occurence.get(key);
				wordOccurencePerClass[allDocs[i].class_ID-1][key-1] += val;
			}
		}
	}
	
	public void MLE(){
		for (int i = 0; i < 20; i++) {
			for (int k = 0; k < vocabcount; k++) {
				MLE[i][k] = ((float)wordOccurencePerClass[i][k])/numWordClass[i];
			}
		}
	}

	public void PBE(){
		for (int i = 0; i < 20; i++) {
			for (int k = 0; k < vocabcount; k++) {
				PBE[i][k] = ((float)(wordOccurencePerClass[i][k] + 1)) /
				   	(numWordClass[i] + vocabcount);
			}
		}
	}

	public void printPriori(){
		for (int i = 0; i < prioris.length; i++) {
			System.out.println("P(Omega = " + i + ") = " + prioris[i]);
		}
	}

	public int calcPBE(Document D){
		double max = 0;
		int max_class = 0;
		for (int i = 0; i < 20; i++) {
			double val = Math.log(prioris[i]);
			for (Integer key : D.Word_Occurence.keySet()) {
				double temp = PBE[i][key];
				if(temp != 0){
					val += ((float)D.Word_Occurence.get(key)) * Math.log(temp);
				}
			}
			if(max == 0){
				max_class = i;
				max = val;
			}
			if(max > val){
				max_class = i;
				max = val;
			}
		}
		return max_class+1;
	}
	/**
	 * Calculates the argmax
	 *
	 */
	public int calcMLE(Document D){
		double max = 0;
		int max_class = 0;
		for (int i = 0; i < 20; i++) {
			double val = Math.log(prioris[i]);
			if(i == 15 && D.ID == 1000)
				System.out.println(val);
			for (Integer key : D.Word_Occurence.keySet()) {
				double temp = MLE[i][key];
				if(temp != 0){
					val += ((float)D.Word_Occurence.get(key)) * Math.log(temp);
				}
			}
			if(max == 0){
				max_class = i;
				max = val;
			}
			if(max > val){
				max_class = i;
				max = val;
			}
		}
		return max_class+1;
	}

	public void runDocs(){
		int matching_PBE = 0;
		int matching_MLE = 0;
		int at_least = 0;
		for (int i = 0; i < allDocs.length; i++) {
			int PBE_out = calcPBE(allDocs[i]);
			int MLE_out = calcMLE(allDocs[i]);
			if(PBE_out == MLE_out)
				at_least++;
			if(PBE_out == allDocs[i].class_ID){
				matching_PBE++;
			}
			if(MLE_out == allDocs[i].class_ID){
				matching_MLE++;
			}
			//System.out.println("PBE :" + PBE_out + " MLE :" + MLE_out + " actual :" + allDocs[i].class_ID );
		}
		float toprint_PBE = ((float)matching_PBE)/allDocs.length;
		float toprint_MLE = ((float)matching_MLE)/allDocs.length;
		float toprint_atl = ((float)at_least/allDocs.length);
		System.out.println(toprint_PBE);
		System.out.println(toprint_MLE);
		System.out.println(toprint_atl);
	}
	public static void main(String[] args) {
		NaiveBayes N = new NaiveBayes();
		N.Read_In();
		N.calcuatePriori();
		N.countWordsInClass();
		N.countWordOccurrenceInClass();
		N.printPriori();
		N.MLE();
		N.PBE();
		N.runDocs();
		/**
		for (int i = 0; i < 20; i++) {
			System.out.println("printing for " + i );
			System.out.println("--------------------------------------------------");
			System.out.println(N.MLE[i][0]);
			System.out.println(N.MLE[i][419]);
			System.out.println(N.MLE[i][6968]);
			System.out.println("printing MLE");
			System.out.println(N.PBE[i][0]);
			System.out.println(N.PBE[i][419]);
			System.out.println(N.PBE[i][6968]);
			System.out.println();
			System.out.println();
			System.out.println();
		}
		*/
	}
}
