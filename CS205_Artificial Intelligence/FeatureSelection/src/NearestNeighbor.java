/* @Author : Lokesh Koppaka(862123164)
 * Course  : CS 205 Artificial Intelligence
 * Description : This class performs  Nearest Neighbor and measures the accuracy of it across various feature set using following 
 * 				 Algorithms and outputs the feature set (Strong features) with maximum accuracy 
 * 				 1. Forward Selection
 * 				 2. Backward Elimination
 * 				 3. Improved performance Algorithm
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;


public class NearestNeighbor {
	/* Class Name : FeatureSet
	 * Description : Inner class to hold the feature set and accuracy together. The instance of this object is later used to retrieve
	 * 				 Feature set with maximum accuracy
	 */
	class FeatureSet implements Comparable<FeatureSet>{
		 List<Integer> features; // List - holds the list of features
		 Double accuracy; // Double - holds the accuracy
		 // Constructor performs Initialization
		 FeatureSet(List<Integer> features, Double accuracy){
			 this.features = features;
			 this.accuracy = accuracy;
		 }
		 // Custom compare to sort the elements based on accuracy
		@Override
		public int compareTo(FeatureSet fs) {
			return accuracy.compareTo(fs.accuracy);
		}
		 
	}
	/* Class Name : DistanceToPoint
	 * Description : Inner class to hold the distance and point together. The instance of this object is later used to retrieve
	 * 				 data point with minimum Euclidean distance
	 */
	class DistanceToPoint{
		Double distance; // Double - holds the Euclidean distance to the current point
		DataPoint point; // DataPoint Instance - holds the data point
		 // Constructor performs Initialization
		DistanceToPoint(Double distance, DataPoint point){
			this.distance = distance;
			this.point = point;
		}
	}
	/* Class Name : DataPoint
	 * Description : Inner class represents a  single data point
	 */
	class DataPoint{
		Double classLabel; // Double classLabel - holds the data point class label
		List<Double> featureList; // List featureList - holds data point features
		// Constructor performs Initialization
		DataPoint(Double classLabel, List<Double> featureList){
			this.classLabel = classLabel;
			this.featureList = featureList;
		}
	}
	
	List<DataPoint> trainingDataSet; // List trainingDataSet - holds all the dataPoints 
	private Integer numberOfFeatures; // Integer - holds no of features
	public Double defaultRate;        // Double - holds the default rate
	double maxAccuracy;				  // Double - holds max Accuracy at that current instance of time
	int maxWrongGuess;                // Double - holds max Wrong guesses can made, this is later used to improve the performance
	// Constructor performs Initialization
	public NearestNeighbor() {
		trainingDataSet = new ArrayList<DataPoint>();
		numberOfFeatures = 0;
		maxAccuracy = 0.0;
		maxWrongGuess = 0;
	}
	/* Method Name	: computeNearestNeighbhor
	 * Parameters	: featureList - the list of features to consider while computing nearestNeighbhor
	 * 				  myOwnAlgo   - Boolean flag to run improved version of the algorithm for fast search using Alpha Beta pruning 
	 * Description 	: Computes Nearest Neighbors by performing K fold cross validation where K = 1 and returns the accuracy
	 * 				  achieved using the featureSet.
	 *                Additionally, the method holds logic to perform Alpha Beta pruning to improve search 
	 */
	public double computeNearestNeighbhor(List<Integer> featureList, Boolean myOwnAlgo) {
		double accuracy = 0.0;
		int correctDataPoints =0;
		int size = trainingDataSet.size();
		int wrongDataPoints = 0; // Int - holds the number of data points classified wrong 
		/* List pointsGuessedWrong - holds the wronging classified data points, later these are pushed to the top of
		 * 							 training set to make Search faster
		 */
		List<DataPoint> pointsGuessedWrong = new ArrayList<DataPoint>(); 
		Boolean flag =  false;
		// Logic to perform NearestNeighbhor using K fold cross validation where K = 1
		for(int i = 0 ; i < size ; i++) {
			DistanceToPoint minDist = new DistanceToPoint(Double.MAX_VALUE,null);
			for(int j = 0; j < size; j++) {
				if(i != j) {
					double sum = 0.0;
					// Logic to compute Euclidean distance , consider only the features in the featureList parameter
					for(int k  = 0; k < featureList.size() ; k ++) {
						sum += Math.pow((trainingDataSet.get(j).featureList.get(featureList.get(k) - 1) - trainingDataSet.get(i).featureList.get(featureList.get(k) - 1)),2);
					}
					sum = Math.sqrt(sum);
					if(sum < minDist.distance) {
						minDist = new DistanceToPoint(sum,trainingDataSet.get(j));
					}
				}
			}
			// Logic to keep track correctly classified data points and wrongly classified data points
			if(minDist.point.classLabel.equals(trainingDataSet.get(i).classLabel)) {
				correctDataPoints++;
			}else {
				pointsGuessedWrong.add(trainingDataSet.get(i));
				wrongDataPoints ++;
			}
			// if wrongly classified data points exceeded the maxWrongGuess limit set accuracy as zero - Alpha beta pruning
			if(myOwnAlgo && wrongDataPoints > maxWrongGuess) {
				flag = true;
				break;
			}
		}
		if(myOwnAlgo) {
			// Logic to push wrongly guessed data points to the top of training set.
			for(DataPoint p : pointsGuessedWrong) {
				trainingDataSet.remove(p);
			}
			pointsGuessedWrong.addAll(trainingDataSet);
			trainingDataSet = pointsGuessedWrong;
			// Logic to return accuracy as 0 if wrongly classified data points exceeded the maxWrongGuess
			if(flag) {
				return 0.0;
			}
		}
		// Logic to compute accuracy
		accuracy = (double)correctDataPoints/trainingDataSet.size();
		// Logic to set the threshold maxWrongGuess, this is used during pruning
		if(accuracy > maxAccuracy) {
			maxAccuracy = accuracy;
			maxWrongGuess = size - correctDataPoints;
		}
		return accuracy;
	}
	/* Method Name	: forwardSelection
	 * Parameters	: myOwnAlgo -  Boolean flag to run improved version of the algorithm for fast search using Alpha Beta pruning
	 * 				  the value is passed to compute NearestNeighbhor
	 * Description	: performs Forward Feed Search and output the feature set with max accuracy
	 */
	public void forwardSelection(Boolean myown) {
		List<Integer> featureSet = new ArrayList<Integer>(); // List featureSet - holds the list of features
		List<Integer> bestFeatureSet = new ArrayList<Integer>();// List bestFestureSet - holds best features
		int i = 1;
		/* PriorityQueue featurePriorityQueue - holds the instances of featureSet at each level, this is used to retrieve the best 
		 * 										feature set at a particular level
		 * PriorityQueue topFeatures		  - holds the instances of featureSet at entire tree level, this is used to retrieve the
		 * 										best feature set across the tree
		 */
		PriorityQueue<FeatureSet> featurePriorityQueue = new PriorityQueue<FeatureSet>(Collections.reverseOrder());
		PriorityQueue<FeatureSet> topFeatures = new PriorityQueue<FeatureSet>(Collections.reverseOrder());
		// Logic - generates feature set at each level and computes Nearest Neighbors for the generated feature set
		while(bestFeatureSet.size() < numberOfFeatures) {
			featureSet = new ArrayList<Integer>();
			if(!bestFeatureSet.isEmpty()) {
				featureSet.addAll(bestFeatureSet);
			}
			FeatureSet fs;
			if(!featureSet.contains(i)) {
				featureSet.add(i);
				// Logic to call NearestNeighbhot method to retrieve the accuracy for the generated feature Set
				Double accuracy = computeNearestNeighbhor(featureSet, myown);
				//System.out.println("\tUsing feature(s) " + featureSet + " accuracy is " + (accuracy*100) + "%");
				fs = new FeatureSet(featureSet,accuracy);
				featurePriorityQueue.add(fs);
			}
			i ++;
			// Logic to get the best feature set at each level 
			if(i % (numberOfFeatures + 1) == 0) {
				bestFeatureSet = new ArrayList<Integer>();
				fs = featurePriorityQueue.poll();
				System.out.println();
				System.out.println("Feature Set " + fs.features + " was best, accuracy is " + (fs.accuracy * 100) +"%");
				System.out.println();
				topFeatures.add(fs);
				bestFeatureSet.addAll(fs.features);
				i = 1;	
				featurePriorityQueue = new PriorityQueue<FeatureSet>(Collections.reverseOrder());
				if(fs.accuracy > maxAccuracy) {
					maxAccuracy = fs.accuracy;
					maxWrongGuess = trainingDataSet.size() - (int)(maxAccuracy * trainingDataSet.size());
				}
			}	
		}
		//System.out.println("Top Features");
		FeatureSet fs = topFeatures.poll();
		System.out.println("Finished search!! The best feature subset is " + fs.features + ", which has accuracy of "+ (fs.accuracy * 100) +"%");
		//System.out.println("For Features = " + fs.features + " obtains Max accuracy of = " + fs.accuracy);
		// Once done setting back maxAccuracy and maxWrongGuess to default values
		maxAccuracy = 0;
		maxWrongGuess = trainingDataSet.size();
	}
	// Something going wrong - check this!
	// Check this!
	/* Method Name	: backwardEllimination
	 * Parameters	: myOwnAlgo -  Boolean flag to run improved version of the algorithm for fast search using Alpha Beta pruning
	 * 				  the value is passed to compute NearestNeighbhor
	 * Description	: performs Back propagation Search and output the feature set with max accuracy
	 */
	public void backwardElimination(Boolean myown) {
		List<Integer> featureSet = new ArrayList<Integer>(); // List featureSet - holds the list of features
		List<Integer> bestFeatureSet = new ArrayList<Integer>(); // List bestFestureSet - holds best features
		for(int i = 1; i <= numberOfFeatures ; i++) {
			bestFeatureSet.add(i);
		}
		int i = 1;
		/* PriorityQueue featurePriorityQueue - holds the instances of featureSet at each level, this is used to retrieve the best 
		 * 										feature set at a particular level
		 * PriorityQueue topFeatures		  - holds the instances of featureSet at entire tree level, this is used to retrieve the
		 * 										best feature set across the tree
		 */
		PriorityQueue<FeatureSet> featurePriorityQueue = new PriorityQueue<FeatureSet>(Collections.reverseOrder());
		PriorityQueue<FeatureSet> topFeatures = new PriorityQueue<FeatureSet>(Collections.reverseOrder());
		while(bestFeatureSet.size() > 1) {
			featureSet = new ArrayList<Integer>();
			if(!bestFeatureSet.isEmpty()) {
				featureSet.addAll(bestFeatureSet);
			}
			FeatureSet fs;
			if(featureSet.contains(i)) {
				featureSet.remove(featureSet.indexOf(i));
				if(!featureSet.equals(bestFeatureSet)) {
					// Logic to call NearestNeighbhot method to retrieve the accuracy for the generated feature Set
					Double accuracy = computeNearestNeighbhor(featureSet,myown);
					System.out.println("\tUsing feature(s) " + featureSet + " accuracy is " + (accuracy*100) + "%");
					fs = new FeatureSet(featureSet,accuracy);
					featurePriorityQueue.add(fs);
				}
			}
			i ++;
			// Logic to get the best feature set at each level 
			if(i % (numberOfFeatures + 1) == 0) {
				bestFeatureSet = new ArrayList<Integer>();
				fs = featurePriorityQueue.poll();
				System.out.println();
				System.out.println("Feature Set " + fs.features + " was best, accuracy is " + (fs.accuracy * 100) +"%");
				System.out.println();
				topFeatures.add(fs);
				bestFeatureSet.addAll(fs.features);
				i = 1;	
				featurePriorityQueue = new PriorityQueue<FeatureSet>(Collections.reverseOrder());
				if(fs.accuracy > maxAccuracy) {
					maxAccuracy = fs.accuracy;
					maxWrongGuess = trainingDataSet.size() - (int)(maxAccuracy * trainingDataSet.size());
				}
			}	
		}
		//System.out.println("Top Features");
		FeatureSet fs = topFeatures.poll();
		System.out.println("Finished search!! The best feature subset is " + fs.features + ", which has accuracy of "+ (fs.accuracy * 100) +"%");
		//System.out.println("For Features = " + fs.features + " obtains accuracy of = " + fs.accuracy);
		// Once done setting back maxAccuracy and maxWrongGuess to default values
		maxAccuracy = 0;
		maxWrongGuess = trainingDataSet.size();
	}
	/* Method Name: loadDataPoints
	 * parameters : Location - the file location of the training data set
	 * Description: Reads the data points and constructs the training DataSet List and computes default rate
	 */
	public void loadDataPoints(String location) {
		//System.out.println("Analyzing the input file ... and building Training and Validation Data set");
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(location));
			int lines = 0;
			while (reader.readLine() != null) lines++;
			int linesRead = 1;
			reader = new BufferedReader(new FileReader(location));
			String line = reader.readLine();
			int classOneCount = 0;
			int classTwoCount = 0;
			while (line != null) {
				DataPoint dp = createDataPointInstance(line);
				if(dp.classLabel.equals(1.0)) {
					classOneCount ++;
				}else if(dp.classLabel.equals(2.0)) {
					classTwoCount ++;
				}
				trainingDataSet.add(dp);
				// read next line
				line = reader.readLine();
				linesRead = linesRead + 1;
			}
			defaultRate = (double)Math.max(classOneCount, classTwoCount)/lines;
			 maxAccuracy = 0;
			 maxWrongGuess = lines;
			 System.out.println("This dataset has "+ numberOfFeatures + " features (not including the class attribute) with " + lines + " instances");
			 System.out.println();
			 System.out.println("Running nearest neighbor with all " + numberOfFeatures + " features, using \"leaving-one-out\" evaluation, I get an accuracy of " + (defaultRate * 100) + "%");
			 System.out.println();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/* Method : createDataPointInstance
	 * Parameter : datapoint - the stringify version of the data point
	 * Description : Helper method, converts the stringify version of the data point to DataPoint class instance 
	 */
	public DataPoint createDataPointInstance(String datapoint){
		datapoint = datapoint.trim();
		String [] datapointArr = datapoint.split("\\s+");
		int dpLen = datapointArr.length;
		List<Double> featureList = new ArrayList<Double>();
		for(int i = 1; i < dpLen ; i++) {
			featureList.add(Double.valueOf(datapointArr[i]));
		}
		if(numberOfFeatures == 0) {
			numberOfFeatures = featureList.size();
		}
        return new DataPoint(Double.valueOf(datapointArr[0]), featureList);
		
	}
	
	public static void main(String args[]) {
		System.out.println("Welcome to CS205 Feature Selection Algorithm.");
		System.out.println("Type in the name of the file to test :");
		Scanner sc = new Scanner(System.in);
		String location = sc.nextLine();
		System.out.println("Type the number of the algorithm you want to run.");
		System.out.println("\t1. Forward Selection");
		System.out.println("\t2. Backward Elimination");
		System.out.println("\t3. Improved performance Algorithm");
		int choice = sc.nextInt();
		NearestNeighbor nn = new NearestNeighbor();
		nn.loadDataPoints(location);
		System.out.println("Beginning search.");
		System.out.println();
		if(choice == 1) {
			long startTime = System.nanoTime();
			nn.forwardSelection(false);
			long endTime = System.nanoTime();
			System.out.println("Time Taken = "+ (endTime - startTime)/1000000 + " ms");
		}else if(choice == 2) {
			long startTime = System.nanoTime();
			nn.backwardElimination(false);
			long endTime = System.nanoTime();
			System.out.println("Time Taken = "+ (endTime - startTime)/1000000 + " ms");
		}else if(choice == 3) {
			long startTime = System.nanoTime();
			nn.forwardSelection(true);
			long endTime = System.nanoTime();
			System.out.println("Time Taken = "+ (endTime - startTime)/1000000 + " ms");
		}else {
			System.out.println("Please Select valid choice..");
		}
	}

}
