package assignment2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Represents a search using Minimax and Alpha-Beta Pruning.
 * @author Nickolas Gough and Sam Germain
 */
public class AlphaBeta {
	
	// The maximum depth of the search.
	private static final int MAX_DEPTH = 8;
	
	// Allow the search to go deeper on certain conditions. 
	private static final int DEEPEST_DEPTH = 10;
	
	// Store the beam depth.
	private static final int BEAM_DEPTH = 7;

	// Store the maximum depth of the beam search.
	private static final int MAX_BEAM_DEPTH = 12;

	// Store the deepest beam depth;
	private static final int DEEPEST_BEAM_DEPTH = 15;

	// Store the threshold of the first player.
	private static final int MAX_THRESHOLD = 35;

	// Store the threshold of the second player.
	private static final int MIN_THRESHOLD = -35;

	// Store the transposition table.
	private static final HashMap<String, Integer> transMaxTable = new HashMap<String, Integer>();

	// Store the transposition table.
	private static final HashMap<String, Integer> transMinTable = new HashMap<String, Integer>();

	/**
	 * Do not allow the Search class to be instantiated.
	 */
	private AlphaBeta(){
		// Do nothing.
	}
	
	/**
	 * Determine the state of best minimax value of the specified state.
	 * @param state - The state from which to begin the search.
	 * @return - The best resulting state derived from the specified state.
	 */
	public static State search(State state){
		// Initialize the player and the result.
		PlayerEnum player = state.getPlayer();
		State result = null;
		
		// Search for the best play for the player.
		if (player == PlayerEnum.PLAYER1){
			result = AlphaBeta.maxValue(state);
//			result = AlphaBeta.maxValueBeam(state);
		}
		else {
			result = AlphaBeta.minValue(state);
//			result = AlphaBeta.minValueBeam(state);
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
		// Initialize all variables.
		int maxValue = Integer.MIN_VALUE;
		int tempValue = maxValue;
		State bestState = null;
		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;
		
		// Order the states in terms of what currently appears best.
		ArrayList<State> states = state.newStates();
		Collections.sort(states);
		Collections.reverse(states);
		
		// Search for the best play considering opposition.
		String boardString = null;
		for (State tempS : states){
			boardString = tempS.boardString();
			if (!AlphaBeta.transMaxTable.containsKey(boardString)){
				tempValue = AlphaBeta.minValueHelper(tempS, 1, alpha, beta);
				AlphaBeta.transMaxTable.put(boardString, tempValue);
			}
			else {
				tempValue = AlphaBeta.transMaxTable.get(boardString);
			}
			
			if (tempValue > maxValue){
				maxValue = tempValue;
				bestState = tempS;
			}
			alpha = Integer.max(tempValue, alpha);
		}
		
		return bestState;
	}
	
	/**
	 * Perform a search for Max, the player maximizing utility.
	 * @param state - The state from which to begin the search. 
	 * @return - The state of best choice for Max.
	 */
	private static State maxValueBeam(State state){
		// Initialize all variables.
		int maxValue = Integer.MIN_VALUE;
		int tempValue = maxValue;
		State bestState = null;
		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;
		
		// Order the states in terms of what currently appears best.
		ArrayList<State> states = state.newStates();
		Collections.sort(states);
		Collections.reverse(states);
		
		// Search for the best play considering opposition.
		String boardString = null;
		for (State tempS : states){
			boardString = tempS.boardString();
			if (!AlphaBeta.transMaxTable.containsKey(boardString)){
				tempValue = AlphaBeta.minValueHelperBeam(tempS, 1, alpha, beta);
				AlphaBeta.transMaxTable.put(boardString, tempValue);
			}
			else {
				tempValue = AlphaBeta.transMaxTable.get(boardString);
			}
			
			if (tempValue > maxValue){
				maxValue = tempValue;
				bestState = tempS;
			}
			alpha = Integer.max(tempValue, alpha);
		}
		
		return bestState;
	}
	
	/**
	 * Determine the Max value for the specified state.
	 * @param state - The state for which to determine the Max value.
	 * @return - The derived state of maximum utility.
	 * @throws RuntimeException when the incorrect player is playing.
	 */
	public static int maxValueHelper(State state, int depth, int alpha, int beta) throws RuntimeException{
		// Check for terminal state.
		boolean gameOver = state.isGameOver();
		if (gameOver || depth >= AlphaBeta.MAX_DEPTH){
			int utility = state.utility();
			if (gameOver){
				return utility;
			}
			if (utility <= AlphaBeta.MAX_THRESHOLD || depth >= AlphaBeta.DEEPEST_DEPTH){
				return utility;
			}
		}
		
		// Switch the current player.
		state.switchPlayer();
		if (state.getPlayer() != PlayerEnum.PLAYER1){
			throw new RuntimeException("Error: Player 1 is assumed to be Max.");
		}
		
		// Order the states in terms of what currently appears best.
		ArrayList<State> states = state.newStates();
		Collections.sort(states);
		Collections.reverse(states);
		
		// Recursively determine the best play.
		int maxValue = Integer.MIN_VALUE;
		int tempValue = maxValue;
		String boardString = null;
		for (State tempS : states){
			boardString = tempS.boardString();
			if (!AlphaBeta.transMaxTable.containsKey(boardString)){
				tempValue = AlphaBeta.minValueHelper(tempS, depth+1, alpha, beta);
			}
			else {
				tempValue = AlphaBeta.transMaxTable.get(boardString);
			}
			
			maxValue = Integer.max(maxValue, tempValue);
			if (maxValue >= beta){
				return maxValue;
			}
			alpha = Integer.max(maxValue, alpha);
		}

		return maxValue;
	}
	
	/**
	 * Determine the Max value for the specified state.
	 * @param state - The state for which to determine the Max value.
	 * @return - The derived state of maximum utility.
	 * @throws RuntimeException when the incorrect player is playing.
	 */
	private static int maxValueHelperBeam(State state, int depth, int alpha, int beta) throws RuntimeException{
		// Check for terminal state.
		boolean gameOver = state.isGameOver();
		if (gameOver || depth >= AlphaBeta.MAX_BEAM_DEPTH){
			int utility = state.utility();
			if (gameOver){
				return utility;
			}
			if (utility <= AlphaBeta.MAX_THRESHOLD || depth >= AlphaBeta.DEEPEST_BEAM_DEPTH){
				return utility;
			}
		}
		
		// Switch the current player.
		state.switchPlayer();
		if (state.getPlayer() != PlayerEnum.PLAYER1){
			throw new RuntimeException("Error: Player 1 is assumed to be Max.");
		}
		
		// Order the states in terms of what currently appears best.
		ArrayList<State> states = state.newStates();
		Collections.sort(states);
		Collections.reverse(states);
		
		// Determine if the beam search should begin.
		if (depth >= AlphaBeta.BEAM_DEPTH){
			for (int n = 3; n < states.size(); n += 1){
				states.remove(n);
			}
		}
		
		// Recursively determine the best play.
		int maxValue = Integer.MIN_VALUE;
		int tempValue = maxValue;
		String boardString = null;
		for (State tempS : states){
			if (!AlphaBeta.transMaxTable.containsKey(boardString)){
				tempValue = AlphaBeta.minValueHelper(tempS, depth+1, alpha, beta);
			}
			else {
				tempValue = AlphaBeta.transMaxTable.get(boardString);
			}
			
			maxValue = Integer.max(maxValue, tempValue);
			if (maxValue >= beta){
				return maxValue;
			}
			alpha = Integer.max(maxValue, alpha);
		}

		return maxValue;
	}
	
	/**
	 * Perform a search for Max, the player maximizing utility.
	 * @param state - The state from which to begin the search. 
	 * @return - The state of best choice for Max.
	 */
	private static State minValue(State state){
		// Initialize all variables.
		int minValue = Integer.MAX_VALUE;
		int tempValue = minValue;
		State bestState = null;
		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;
		
		// Order the states in terms of what currently appears best.
		ArrayList<State> states = state.newStates();
		Collections.sort(states);
		
		// Search for the best play considering opposition.
		String boardString = null;
		for (State tempS : states){
			boardString = tempS.boardString();
			if (!AlphaBeta.transMinTable.containsKey(boardString)){
				tempValue = AlphaBeta.maxValueHelper(tempS, 1, alpha, beta);
				AlphaBeta.transMinTable.put(boardString, tempValue);
			}
			else {
				tempValue = transMinTable.get(boardString);
			}
			
			if (tempValue < minValue){
				minValue = tempValue;
				bestState = tempS;
			}
			beta = Integer.min(tempValue, beta);
		}
		
		return bestState;
	}
	
	/**
	 * Perform a search for Max, the player maximizing utility.
	 * @param state - The state from which to begin the search. 
	 * @return - The state of best choice for Max.
	 */
	private static State minValueBeam(State state){
		// Initialize all variables.
		int minValue = Integer.MAX_VALUE;
		int tempValue = minValue;
		State bestState = null;
		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;
		
		// Order the states in terms of what currently appears best.
		ArrayList<State> states = state.newStates();
		Collections.sort(states);
		
		// Search for the best play considering opposition.
		String boardString = null;
		for (State tempS : states){
			boardString = tempS.boardString();
			if (!AlphaBeta.transMinTable.containsKey(boardString)){
				tempValue = AlphaBeta.maxValueHelperBeam(tempS, 1, alpha, beta);
				AlphaBeta.transMinTable.put(boardString, tempValue);
			}
			else {
				tempValue = transMinTable.get(boardString);
			}
			
			if (tempValue < minValue){
				minValue = tempValue;
				bestState = tempS;
			}
			beta = Integer.min(tempValue, beta);
		}
		
		return bestState;
	}
	
	/**
	 * Determine the Min value for the specified state.
	 * @param state - The state for which to determine the Min value.
	 * @return - The derived state of minimum utility.
	 * @throws RuntimeException when the incorrect player is playing.
	 */
	public static int minValueHelper(State state, int depth, int alpha, int beta) throws RuntimeException{
		// Check for terminal state.
		boolean gameOver = state.isGameOver();
		if (gameOver || depth >= AlphaBeta.MAX_DEPTH){
			int utility = state.utility();
			if (gameOver){
				return utility;
			}
			if (utility >= AlphaBeta.MIN_THRESHOLD || depth >= AlphaBeta.DEEPEST_DEPTH){
				return utility;
			}
		}
		
		// Switch the current player.
		state.switchPlayer();
		if (state.getPlayer() != PlayerEnum.PLAYER2){
			throw new RuntimeException("Error: Player 2 is assumed to be Min.");
		}
		
		// Order the states in terms of what currently appears best.
		ArrayList<State> states = state.newStates();
		Collections.sort(states);
	
		// Recursively determine the best play.
		int minValue = Integer.MAX_VALUE;
		int tempValue = minValue;
		String boardString = null;
		for (State tempS : states){
			boardString = tempS.boardString();
			if (!AlphaBeta.transMinTable.containsKey(boardString)){
				tempValue = AlphaBeta.maxValueHelper(tempS, depth+1, alpha, beta);
			}
			else {
				tempValue = AlphaBeta.transMinTable.get(boardString);
			}
			
			minValue = Integer.min(minValue, tempValue);
			if (minValue <= alpha){
				return minValue;
			}
			beta = Integer.min(minValue, beta);
		}

		return minValue;
	}
	
	/**
	 * Determine the Min value for the specified state.
	 * @param state - The state for which to determine the Min value.
	 * @return - The derived state of minimum utility.
	 * @throws RuntimeException when the incorrect player is playing.
	 */
	private static int minValueHelperBeam(State state, int depth, int alpha, int beta) throws RuntimeException{
		// Check for terminal state.
		boolean gameOver = state.isGameOver();
		if (gameOver || depth >= AlphaBeta.MAX_BEAM_DEPTH){
			int utility = state.utility();
			if (gameOver){
				return utility;
			}
			if (utility >= AlphaBeta.MIN_THRESHOLD || depth >= AlphaBeta.DEEPEST_BEAM_DEPTH){
				return utility;
			}
		}
		
		// Switch the current player.
		state.switchPlayer();
		if (state.getPlayer() != PlayerEnum.PLAYER2){
			throw new RuntimeException("Error: Player 2 is assumed to be Min.");
		}
		
		// Order the states in terms of what currently appears best.
		ArrayList<State> states = state.newStates();
		Collections.sort(states);
		
		// Determine if the beam search should begin.
		if (depth >= AlphaBeta.BEAM_DEPTH){
			for (int n = 3; n < states.size(); n += 1){
				states.remove(n);
			}
		}
	
		// Recursively determine the best play.
		int minValue = Integer.MAX_VALUE;
		int tempValue = minValue;
		String boardString = null;
		for (State tempS : states){
			boardString = tempS.boardString();
			if (!AlphaBeta.transMinTable.containsKey(boardString)){
				tempValue = AlphaBeta.maxValueHelperBeam(tempS, depth+1, alpha, beta);
			}
			else {
				tempValue = AlphaBeta.transMinTable.get(boardString);
			}
			
			minValue = Integer.min(minValue, tempValue);
			if (minValue <= alpha){
				return minValue;
			}
			beta = Integer.min(minValue, beta);
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
//		test = (TestState) AlphaBeta.search(test);
//		if (test.toString() != "b2"){
//			System.out.println("Error: MiniMax failed to determine the optimal branch.");
//		}
		
		// Run the Minimax algorithm with the GameState.
		GameState game = new GameState();
		game.initializeGameStart();
		System.out.println("Result:");
		System.out.println(AlphaBeta.search(game));
	}
}
