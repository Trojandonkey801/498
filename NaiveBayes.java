import java.util.HashMap;
import java.util.Map;
import java.util.Iterator; import java.util.Set;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.ArrayList;
public class NaiveBayes{
	static final int vocabcount = 61188;
	ArrayList<Document> W_j = new ArrayList<Document>();
	static int Doc_count[] = new int[20];
	static String[] Words = new String[vocabcount];
	static float[] priori = new float[20];
	static long[] wordcount = new long[11269];
	static long[] docWordCount = new long[20];
	private static void init_vocab(){
		int index = 0;
		try{
			File F = new File("vocabulary.txt");
			Scanner S = new Scanner(F);
			while(S.hasNextLine()){
				Words[index] = S.nextLine();
				index++;
			}
		}catch(FileNotFoundException e){

		}
		for (int i = 0; i < 20; i++) {
			Doc_count[i] = 0;
			docWordCount[i] = 0;
		}
	}
	public static void main(String[] args) {
		init_vocab();
		/**
		if(args.length>0){
			parseFiles(args);
		}
		*/
		//System.out.println(Words[61187]);
		CountWords();
		wordsPer();
		for (int i = 0; i < 20; i++) {
			System.out.println("Document class :" + i + "--------------------------------------------------");
			System.out.println("Number of documents");
			System.out.println(Doc_count[i]);
			System.out.println("Priori");
			System.out.println(priori[i]);
			System.out.println("Total number of words in each document class");
			System.out.println(docWordCount[i]);
		}
	}
	public static void parseFiles(String[] args){
		for (int i = 0; i < args.length; i++) {
			try{
				File F = new File(args[i]);
			}catch(Exception e){
				System.out.println(e);
			}
		}
	}
	/**
	 * This program first counts the numbers of words in each file
	 * It then iterates through and adds the number of words in each file, and adds it to each class
	 *
	 */
	public static void wordsPer(){
		try{
			File F = new File("train_data.csv");
			Scanner S = new Scanner(F);
			while(S.hasNextLine()){
				String temp = S.nextLine();
				String[] split = temp.split(",");
				wordcount[Integer.valueOf(split[0])-1] += Integer.valueOf(split[2]);
			}
			F = new File("train_label.csv");
			S = new Scanner(F);
			int count = 0;
			while(S.hasNextLine()){
				docWordCount[Integer.valueOf(S.nextLine())-1] += wordcount[count];
			}
		}catch(FileNotFoundException e){
		}
	}
	public static void CountWords(){
		int total = 0;
		try{
			File F = new File("train_label.csv");
			Scanner S = new Scanner(F);
			while(S.hasNextLine()){
				String temp = S.nextLine();
				Doc_count[Integer.valueOf(temp)-1]++;
				total++;
			}
		}catch(FileNotFoundException e){
			System.out.println(e);
		}
		for (int i = 0; i < 20; i++) {
			priori[i] = (float)Doc_count[i]/total;
		}
	}
	public class Document{
		private int words;
		private float priori;
		HashMap<Integer, String> hmap = new HashMap<Integer, String>();
		public Document(File F){
			parseFile(F);
		}
		private void parseFile(File F){
			try{
				Scanner S = new Scanner(F);
				while(S.hasNextLine()){
					Scanner SS = new Scanner(S.nextLine());
					while(SS.hasNext()){
						hmap.put(SS.next().hashCode(),SS.next());
					}
				}
			}catch(FileNotFoundException e){
			}
		}
	}
}
