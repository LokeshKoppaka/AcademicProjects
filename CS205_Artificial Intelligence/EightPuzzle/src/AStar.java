/* @Course : CS205 Artificial Intelligence
 * @Author: Lokesh Koppaka, Student ID: 862123164
 * ClassName: AStar
 * Description : This class computes the solution for n-puzzle problem using A* Algorithm with Misplaced Tile & Manhatten Distance heuristics
 */

import java.util.ArrayList; // Imported to implement dynamic Queue
import java.util.Collections; // Imported to perform sort
import java.util.List; // Super class for ArrayList hence required to import
import java.util.Scanner; // Imported to scan input from user

public class AStar {
	
	public int puzzleSize; // instance variable - holds size of the puzzle
	private int maxQueueLen = 0; // instance variable - keeps track of max Queue length
	private int currentQueueLen; // instance variable - keeps track of current size of the Queue
	private int expandedNodes = 1;// instance variable - keeps track of No of expanded nodes
	private List<State> visitedList; // List - holds list of visited states, this is necessary to avoid search on already visited states
	private List<State> queue; // List - used to implement queue
	private int[][] deafultPuzz = {{8,7,1},{6,0,2},{5,4,3}}; // Default State for puzzle size 3x3
	private int[][] deafultPuzzFour = {{1,2,3,4},{5,6,7,8},{9,10,11,12},{13,14,15,0}}; // Default State for puzzle size 4X4
	private int[][] deafultPuzzFive = {{1,2,3,4,5},{6,7,8,9,10},{11,12,13,14,15},{16,17,18,19,20},{21,22,23,24,0}}; // Default State for puzzle size 5X5
	private int[][] GoalState;
	// Constructor - performs instantiation of instance variables
	AStar(){
		visitedList = new ArrayList<State>();
		queue = new ArrayList<State>();
	}
	/* Method Name - constructGoalState
	 * Description - construct goal state for given puzzle size
	 */
	private void constructGoalState() {
		GoalState = new int[puzzleSize][puzzleSize];
		int i = 1;
		for(int j = 0;  j < puzzleSize; j++) {
			for(int k = 0; k < puzzleSize; k++) {
				GoalState[j][k] = i;
				i++;
			}
		}
		GoalState[puzzleSize - 1][puzzleSize - 1] = 0;
	}
	/* Method Name - addRowsToPuzzle
	 * Parameters  - puzzle(The puzzle grid), row(row index where the vals need to added), vals(the values to be added)
	 * Description - Takes the puzzle grid and add the values in order at specified row index
	 * Return      - returns the puzzle grid with added values to specified row
	 */
	private int[][] addRowToPuzzle(int[][] puzzle, int row, String[] vals) {
		int len = vals.length;
		for(int i = 0; i< len; i++) {
			puzzle[row][i] = Integer.valueOf(vals[i]);
		}
		return puzzle;
	}
	/* Method Name - printPuzzle
	 * Parameters  - puzzle(The puzzle grid)
	 * Description - prints the values in the grid to standard output
	 */
	private void printPuzzle(int[][] puzzle) {
		for(int i = 0; i < puzzleSize; i++) {
			for(int j = 0; j < puzzleSize; j++) {
				System.out.print(puzzle[i][j] + " ");
			}
			System.out.println();
		}
	}
	/* Method Name - isStateVisited
	 * Parameters  - currentState(The current puzzle state)
	 * Description - checks if the currentState is visited by looking into visitedList
	 * Return      - returns true if it is visited else false
	 */
	private boolean isStateVisited(State currentState) {
		if(visitedList.contains(currentState)) {
			return true;
		}
		return false;
	}
	/* Method Name - genericSearch
	 * Parameters  - puzzle(The puzzle grid), queuingFunction 
	 * 				 queuingFunction 
	 *               1 - Uniform Cost Search
	 *               2 - A* with misplaced Tiles as heuristic
	 *               3 - A* with manhatten Dist as heuristic
	 * Description - performs generic search on the puzzle
	 */
	private void genericSearch(int[][] puzzle, int queuingFunction) {
	 int uCost = 0; // holds uniform cost
	 int hCost = 0; // holds heuristic cost
	 int[] blankLoc = new int[2]; // holds row and column index of the blank
	 // Logic to compute blank location indexes
	 for(int i = 0; i < puzzleSize; i++) {
		 for(int j = 0; j < puzzleSize; j++) {
			 if(puzzle[i][j] == 0) {
				 blankLoc[0] = i;
				 blankLoc[1] = j;
				 break;
			 }
		 }
	 }
	 // Logic to create current state and add it to queue
	 State currentState = new State(puzzle,puzzleSize, uCost, hCost, false, blankLoc);
	 queue.add(currentState);
	 maxQueueLen = 1;
	 currentQueueLen = 1;
	 long startTime = System.nanoTime();
	 long endTime;
	 while(!queue.isEmpty()) {
		 // Logic to pop the current State and add it to visitedList
		 currentState = queue.remove(0);
		 currentQueueLen --;
		 visitedList.add(currentState);
		 // Logic to check if the current State is the Goal State
		 if(isGoalState(currentState)) {
			 // Logic - if the current state is goal state
			 endTime = System.nanoTime();
			 long timeElapsed = endTime - startTime;
			 printPuzzle(currentState.puzzleState); // Print the puzzle
			 System.out.println("Hurray! Reached Goal State");
			 System.out.println("The time taken to solve the problem " + timeElapsed/ 1000000 + "milliseconds");
			 System.out.println("To solve this problem the search algorithm expanded a total of " + expandedNodes + " nodes" );
			 System.out.println("The maximum number of nodes in the queue at any one time was " + maxQueueLen);
			 System.out.println("The depth of the goal node was  " + currentState.uniformCost);
			 return;
		 }else {
			 // Logic - if the current state is not the goal state
			 System.out.println("The best state to expand with g(n) = " + currentState.uniformCost + "and h(n) = " + currentState.heuristicCost + "is...");
			 printPuzzle(currentState.puzzleState); // Print the puzzle
			 System.out.println("Expanding this node..."); 
			 expand(currentState, queuingFunction); // Logic to expand the current node and add it queue 
			 Collections.sort(queue); // Sort the queue - this cases the least cost state to be in front of the queue
		 }
	 }
	 System.out.println("Its impossible to find the goal state!");
	 System.out.println("To solve this problem the search algorithm expanded a total of " + expandedNodes + " nodes" );
	 System.out.println("The maximum number of nodes in the queue at any one time was " + maxQueueLen);
	 System.out.println("The depth of the goal node was  " + currentState.uniformCost);
	 
	}
	/* Method Name - expand
	 * Parameters  - currentState(The current puzzle state),queuingFunction 
	 * 				 queuingFunction 
	 *               1 - Uniform Cost Search
	 *               2 - A* with misplaced Tiles as heuristic
	 *               3 - A* with manhatten Dist as heuristic 
	 * Description - expands to all the possible states from the current state
	 */
	private void expand(State currentState, int queuingFunction) {
		// Logic to get the row and column index 
		int blankI = currentState.blankLocation[0];
		int blankJ = currentState.blankLocation[1];
		//queue = new ArrayList<State>(); // added new!
		// Logic if blank can Move Top
		if(blankI > 0){
			appendValidStatesToQueue(currentState,blankI- 1,blankJ, blankI, blankJ, queuingFunction);
		}
		// Logic if blank can Move bottom
		if(blankI < puzzleSize - 1) {
			appendValidStatesToQueue(currentState,blankI + 1,blankJ, blankI, blankJ, queuingFunction);
		}
		// Logic if blank can Move left
		if(blankJ < puzzleSize - 1) {
			appendValidStatesToQueue(currentState,blankI,blankJ + 1, blankI, blankJ, queuingFunction);
		}
		// Logic if blank can Move right
		if(blankJ > 0) {
			appendValidStatesToQueue(currentState,blankI,blankJ - 1, blankI, blankJ, queuingFunction);
		}
		
	}
	/* Method Name - appendValidStatesToQueue
	 * Parameters  - currentState(The current puzzle state)
	 * 			   - newI, newJ (The new indexes where the blank can be moved)
	 * 			   - curI, curJ (The current indexes of the blank )
	 * 				 queuingFunction 
	 *               1 - Uniform Cost Search
	 *               2 - A* with misplaced Tiles as heuristic
	 *               3 - A* with manhatten Dist as heuristic 
	 * Description - Creates a new State, checks if it is not visited then computes g(n) and h(n) and add it to the queue
	 */
	private void appendValidStatesToQueue(State curState, int newI, int newJ, int curI, int curJ, int queuingFunction) {
		int[][] puzzleState = new int[puzzleSize][puzzleSize];
		// Logic to copy the current puzzle state
		for(int i = 0; i < puzzleSize; i++) {
			for(int j = 0; j < puzzleSize; j++) {
				puzzleState[i][j] = curState.puzzleState[i][j];
			}
		}
		// Logic to create the new puzzle state
		puzzleState[curI][curJ] = puzzleState[newI][newJ];
		puzzleState[newI][newJ] = 0;
		// Logic to create new state check if it is not visited, then compute g(n), h(n) based on queuing Function
			State newState = new State(puzzleState,puzzleSize,0,0,false,new int[] {newI, newJ});
			if(!isStateVisited(newState)) {
				newState.uniformCost = curState.uniformCost + 1;
				if(queuingFunction == 2) {
					newState.heuristicCost = heuristicUsingMisplaceTiles(newState);
				}else if(queuingFunction == 3) {
					newState.heuristicCost = heuristicUsingManhattenDist(newState);
				}
				expandedNodes ++;
				queue.add(newState);
				currentQueueLen ++;
				// Logic to compute max Queue length
				if(currentQueueLen > maxQueueLen) {
					maxQueueLen = currentQueueLen;
				}
			}
	}
	/* Method Name - heuristicUsingMisplaceTiles
	 * Parameters  - currentState(The current puzzle state)
	 * Description - computes the heuristic cost by counting number of misplaced tiles
	 * Return      - returns heuristic cost
	 */
	private int heuristicUsingMisplaceTiles(State currentState){
		int[][] stateVals = currentState.puzzleState;
		int count = 0;
		// Logic to compute number of tiles that are misplaced
		for(int i = 0; i < puzzleSize; i++) {
			for(int j = 0; j < puzzleSize; j++) {
				if(stateVals[i][j] != GoalState[i][j]) {
					count ++;
				}
			}
		}
		return count;
	}
	/* Method Name - heuristicUsingManhattenDist
	 * Parameters  - currentState(The current puzzle state)
	 * Description - computes the heuristic cost by calculated the mahantten distance of each tile from its goal state
	 * Return      - returns heuristic cost
	 */
	private int heuristicUsingManhattenDist(State currentState) {
		int[][] stateVals = currentState.puzzleState;
		int count = 0;
		// Logic to compute heuristic
		for(int i = 0; i < puzzleSize; i++) {
			for(int j = 0;j < puzzleSize; j++) {
				// if it is blank skip it
				if(stateVals[i][j] == 0) {
					continue;
				}else if(stateVals[i][j] != GoalState[i][j]) {
					// Logic to compute mahantten distance
					int dist[] = returnLocation(stateVals[i][j]);
					count += Math.abs(i - dist[0]);
					count += Math.abs(j - dist[1]);
				}
			}
		}
		System.out.println("****** In Manhatten ************\n");
		printPuzzle(currentState.puzzleState);
		System.out.println("Hanhatten Dist = " + count);
		return count;
	}
	/* Method Name - returnLocation
	 * Parameters  - tile value
	 * Description - returns the location in of the tile value in terms i and j index
	 * Return      - array of size 2 holding the ith and jth index of the passed tile value
	 */
	private int[] returnLocation(int val) {
		for(int i = 0; i < puzzleSize; i++) {
			for(int j = 0; j < puzzleSize; j++) {
				if(val == GoalState[i][j]) {
					return new int[] {i,j};
				}
			}
		}
		return null;
	}
	/* Method Name - isGoalState
	 * Parameters  - currentState(The current puzzle state)
	 * Description - checks if the current state is a goal state
	 * Return      - returns true if current state is goal state else false
	 */
	private Boolean isGoalState(State currentState) {
		int[][] stateVals = currentState.puzzleState;
		// Logic to check if current state is a goal state
		for(int i = 0; i < puzzleSize; i++) {
			for(int j = 0; j < puzzleSize; j++) {
				if(stateVals[i][j] != GoalState[i][j]) {
					return false;
				}
			}
		}
		return true;
	}
	public static void main(String[] args) {
		AStar eightPuzzle =  new AStar(); // AStar instance
		//State
		Scanner sc = new Scanner(System.in); // used to get user input from standard input
		int algoChoice; // holds the user algorithm choice
		// Logic for prompting user preferences
		System.out.println("Welcome to CS 205 n puzzle Solver!!");
		System.out.println("Enter the puzzle size");
		eightPuzzle.puzzleSize = sc.nextInt();
		sc.nextLine();
		eightPuzzle.constructGoalState();
		System.out.println("Type \"1\" to use default puzzle, or \"2\" to enter your own puzzle.");
	    int puzzleChoice = sc.nextInt();
	    sc.nextLine();
	    int puzzle[][] = new int[eightPuzzle.puzzleSize][eightPuzzle.puzzleSize]; // holds the puzzle
	    // Logic for default puzzle choice
	    if(puzzleChoice == 1) {
	    	if(eightPuzzle.puzzleSize == 3) {
	    		puzzle = eightPuzzle.deafultPuzz;
	    	}else if(eightPuzzle.puzzleSize == 4){
	    		puzzle = eightPuzzle.deafultPuzzFour;
	    	}else if(eightPuzzle.puzzleSize == 5) {
	    		puzzle = eightPuzzle.deafultPuzzFive;
	    	}
	    }else if(puzzleChoice == 2) {
	    	// Logic for user inputing puzzle
	    	String[] input = new String[eightPuzzle.puzzleSize];
	    	puzzle = new int[eightPuzzle.puzzleSize][eightPuzzle.puzzleSize];
	    	System.out.println("Enter your puzzle, use  a zero to represent  the blank");
	    	for(int i = 0; i < eightPuzzle.puzzleSize; i++) {
	    		System.out.println("Enter the "+ (i+1) +" row, use space or tabs between numbers");
	    		String inputStr = sc.nextLine();
	    		input = inputStr.split("\\s+");
	    		puzzle = eightPuzzle.addRowToPuzzle(puzzle,i,input);
	    	}
	    }else {
	    	return;
	    }
	    System.out.println("Puzzle Intial State");
	    eightPuzzle.printPuzzle(puzzle); // print the puzzle
	    // User algo choice prompt
	    System.out.println("Enter your choice of algorithm");
	    System.out.println("1. Uniform Cost Search");
	    System.out.println("2. A* with the Misplaced Tile heuristic");
	    System.out.println("3. A* with the Manhattan distance heuristic");
	    algoChoice = sc.nextInt();// read user algo choice
	    eightPuzzle.genericSearch(puzzle, algoChoice); // call to generic search
	}

}
