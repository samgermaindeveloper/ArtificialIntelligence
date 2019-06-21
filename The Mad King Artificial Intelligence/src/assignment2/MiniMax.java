package assignment2;

/**
 * Represents a search using Minimax and Alpha-Beta Pruning.
 * @author Nickolas Gough and Sam Germain
 */
public class MiniMax {
	
	// Store the maximum depth of the Minimax search
	private static final int MAX_DEPTH = 5; 

	/**
	 * Do not allow the Search class to be instantiated.
	 */
	private MiniMax(){
		// Do nothing.
	}
	
	/**
	 * Determine the state of best minimax value of the specified state.
	 * @param state - The state from which to begin the search.
	 * @return - The best resulting state derived from the specified state.
	 */
	public static State search(State state){
		// Initialize the player and result.
		PlayerEnum player = state.getPlayer();
		State result = null;
		
		// Determine the best move for the player.
		if (player == PlayerEnum.PLAYER1){
			result = MiniMax.maxValue(state);
		}
		else {
			result = MiniMax.minValue(state);
		}
		
		// Ensure the state is correct.
		if (result.getPlayer() != player){
			result.switchPlayer();
		}
		
		return result;
	}
	
	/**
	 * Perform a search for Max, the player maximizing utility.
	 * @param state - The state from which to begin the search. 
	 * @return - The state of best choice for Max.
	 */
	private static State maxValue(State state){
		// Initialize the max value and best state.
		int maxValue = Integer.MIN_VALUE;
		int tempValue = maxValue;
		State bestState = null;
		
		// Search for the best state considering opposition.
		for (State tempS : state.newStates()){
			tempValue = MiniMax.minValueHelper(tempS, 1);
			if (tempValue > maxValue){
				maxValue = tempValue;
				bestState = tempS;
			}
		}
		
		return bestState;
	}
	
	/**
	 * Determine the Max value for the specified state.
	 * @param state - The state for which to determine the Max value.
	 * @return - The derived state of maximum utility.
	 * @throws RuntimeException when the incorrect player is playing.
	 */
	private static int maxValueHelper(State state, int depth) throws RuntimeException{
		// Check for terminal state.
		if (state.isGameOver() || depth >= MiniMax.MAX_DEPTH){
			return state.utility();
		}
		
		// Switch the current player.
		state.switchPlayer();
		if (state.getPlayer() != PlayerEnum.PLAYER1){
			throw new RuntimeException("Error: Player 1 is assumed to be Max.");
		}
		
		// Recursively determine the best play.
		int maxValue = Integer.MIN_VALUE;
		int tempValue = maxValue;
		for (State tempS : state.newStates()){
			tempValue = MiniMax.minValueHelper(tempS, depth+1);
			maxValue = Integer.max(maxValue, tempValue);
		}

		return maxValue;
	}
	
	/**
	 * Perform a search for Max, the player maximizing utility.
	 * @param state - The state from which to begin the search. 
	 * @return - The state of best choice for Max.
	 */
	private static State minValue(State state){
		// Initialize the min value and the best state.
		int minValue = Integer.MAX_VALUE;
		int tempValue = minValue;
		State bestState = null;
		
		// Search for the best state considering opposition.
		for (State tempS : state.newStates()){
			tempValue = MiniMax.maxValueHelper(tempS, 1);
			if (tempValue < minValue){
				minValue = tempValue;
				bestState = tempS;
			}
		}
		
		return bestState;
	}
	
	/**
	 * Determine the Min value for the specified state.
	 * @param state - The state for which to determine the Min value.
	 * @return - The derived state of minimum utility.
	 * @throws RuntimeException when the incorrect player is playing.
	 */
	private static int minValueHelper(State state, int depth) throws RuntimeException{
		// Check for terminal state.
		if (state.isGameOver() || depth >= MiniMax.MAX_DEPTH){
			return state.utility();
		}
		
		// Switch the current player.
		state.switchPlayer();
		if (state.getPlayer() != PlayerEnum.PLAYER2){
			throw new RuntimeException("Error: Player 2 is assumed to be Min.");
		}
	
		// Recursively determine the best play.
		int minValue = Integer.MAX_VALUE;
		int tempValue = minValue;
		for (State tempS : state.newStates()){
			tempValue = MiniMax.maxValueHelper(tempS, depth+1);
			minValue = Integer.min(minValue, tempValue);
		}

		return minValue;
	}
	
	/**
	 * Main method for testing and running search simulations.
	 * @param args - The arguments to the main method.
	 */
	public static void main(String[] args){
		// Test the Minimax algorithm using the TestState.
//		TestState test = new TestState();
//		test.initializeMiniMaxTree();
//		test = (TestState) MiniMax.search(test);
//		if (test.toString() != "b2"){
//			System.out.println("Error: MiniMax failed to determine the optimal branch.");
//		}
		
		// Run the Minimax algorithm with the GameState.
		GameState game = new GameState();
		game.initializeGameStart();
		System.out.println("Result:");
		System.out.println(MiniMax.search(game));
	}
}
