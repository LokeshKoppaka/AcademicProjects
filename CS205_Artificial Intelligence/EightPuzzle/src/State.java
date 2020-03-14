/* @Author : Lokesh Koppaka, Student ID: 862123164
 * Class : State
 * Description : This class holds the information about the current state of the n puzzle.
 * 				 The following are the information it stores
 * 				 1. puzzleState - the current state tiles arrangement
 * 				 2. puzzleSize - holds the size of the puzzle 
 * 				 2. uniformCost - the uniform cost for the state i.e g(n)
 * 				 3. heuristicCost - the heuristicCost for the state i.e h(n)
 * 				 4. blankLocation - the row and col index of blank
 * 
 */

public class State implements Comparable<State>{
		 int[][] puzzleState; // instance variable - holds current tiles arrangement
		 int puzzleSize; // instance variable - holds the size of the puzzle
		 int uniformCost; // instance variable - holds uniform cost
		 int heuristicCost; // instance variable - holds heuristic cost
		 int[] blankLocation; // instance variable - holds row and col index of blank
		 // Constructor - performs instance variable initializations
		public State(int[][] puzzleState, int puzzleSize,int uniformCost,int heuristicCost, Boolean visited, int[] blankLocation){
			this.puzzleState = puzzleState;
			this.uniformCost = uniformCost;
			this.heuristicCost = heuristicCost;
			this.blankLocation = blankLocation;
			this.puzzleSize = puzzleSize;
		}
		// Overridden standard equals method to check equality of two objects based on puzzle arrangement
		public boolean equals(Object o) {
			// Logic to check if two puzzle arrangements are same
			if(o instanceof State) {
				State currentState = (State) o;
				for(int i = 0; i < puzzleSize; i++) {
					for(int j = 0; j < puzzleSize; j++) {
						if(puzzleState[i][j] != currentState.puzzleState[i][j]) {
							return false;
						}
					}
				}
				return true;
			}
			return false;
		}
		// Overridden standard compareTo method inorder to perform sort based on totalCost = g(n) + h(n)
		@Override
		public int compareTo(State currentState) {
			// TODO Auto-generated method stub
			return (this.uniformCost + this.heuristicCost) - (currentState.uniformCost + currentState.heuristicCost);
		}
		
	}