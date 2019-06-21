package assignment2;

import java.util.ArrayList;

/**
 * Models a simple game tree that can be used to test the Minimax and Alpha-Beta Pruning algorithms.
 * @author Nickolas Gough and Sam Germain
 *
 */
public class TestState implements State{
	
	// Store the list of successors.
	private ArrayList<State> successors;
	
	// Store the utility if a terminal state.
	private Integer utility;
	
	// Store the name of the state for identification.
	private String name;
	
	// Store the current player.
	private PlayerEnum player = PlayerEnum.PLAYER1;
	
	/**
	 * Instantiate a TestState object.
	 */
	public TestState(){
		// Do nothing.
	}
	
	/**
	 * Instantiate the game tree of the test state.
	 */
	public void initializeMiniMaxTree(){
		// Create the root of the game tree.
		this.successors = new ArrayList<State>();
		this.utility = null;
		this.name = "root";
		
		// Create the second layer. 
		TestState s0 = this.createBranch("b0");
		TestState s1 = this.createBranch("b1");
		TestState s2 = this.createBranch("b2");
		this.successors.add(s0);
		this.successors.add(s1);
		this.successors.add(s2);
		
		// Create the third layer. 
		TestState s3 = this.createBranch("b3");
		TestState s4 = this.createBranch("b4");
//		TestState s5 = this.createBranch("b5");
		s0.successors.add(s3);
		s0.successors.add(s4);
//		s0.successors.add(s5);
		
		TestState s6 = this.createBranch("b6");
		TestState s7 = this.createBranch("b7");
		TestState s8 = this.createBranch("b8");
		s1.successors.add(s6);
		s1.successors.add(s7);
		s1.successors.add(s8);
		
		TestState s9 = this.createBranch("b9");
		TestState s10 = this.createBranch("b10");
//		TestState s11 = this.createBranch("b11");
		s2.successors.add(s9);
		s2.successors.add(s10);
//		s2.successors.add(s11);
		
		// Create the fourth layer.
		s3.successors.add(this.createTerminal("t0", 4));
		s3.successors.add(this.createTerminal("t1", 3));
//		s3.successors.add(this.createTerminal("t2", 6));
		
		s4.successors.add(this.createTerminal("t3", 6));
		s4.successors.add(this.createTerminal("t4", 2));
//		s4.successors.add(this.createTerminal("t5", 5));
		
//		s5.successors.add(this.createTerminal("t6", 5));
//		s5.successors.add(this.createTerminal("t7", 6));
//		s5.successors.add(this.createTerminal("t8", 7));
		
		s6.successors.add(this.createTerminal("t9", 2));
		s6.successors.add(this.createTerminal("t10", 1));
//		s6.successors.add(this.createTerminal("t11", 11));
		
		s7.successors.add(this.createTerminal("t12", 9));
		s7.successors.add(this.createTerminal("t13", 5));
//		s7.successors.add(this.createTerminal("t14", 2));
		
		s8.successors.add(this.createTerminal("t15", 3));
		s8.successors.add(this.createTerminal("t16", 1));
//		s8.successors.add(this.createTerminal("t17", 1));
		
		s9.successors.add(this.createTerminal("t18", 5));
		s9.successors.add(this.createTerminal("t19", 4));
//		s9.successors.add(this.createTerminal("t20", 2));
		
		s10.successors.add(this.createTerminal("t21", 7));
		s10.successors.add(this.createTerminal("t22", 5));
//		s10.successors.add(this.createTerminal("t23", 5));
		
//		s11.successors.add(this.createTerminal("t24", 8));
//		s11.successors.add(this.createTerminal("t25", 3));
//		s11.successors.add(this.createTerminal("t26", 7));
	}

	/**
	 * Instantiate the game tree of the test state.
	 */
	public void initializeAlphaBetaTree(){
		// Create the root of the game tree.
		this.successors = new ArrayList<State>();
		this.utility = null;
		this.name = "root";
		
		// Create the second layer. 
		TestState s0 = this.createBranch("b0");
		TestState s1 = this.createBranch("b1");
		TestState s2 = this.createBranch("b2");
		this.successors.add(s0);
		this.successors.add(s1);
		this.successors.add(s2);
		
		// Create the third layer. 
		TestState s3 = this.createBranch("b3");
		TestState s4 = this.createBranch("b4");
//		TestState s5 = this.createBranch("b5");
		s0.successors.add(s3);
		s0.successors.add(s4);
//		s0.successors.add(s5);
		
		TestState s6 = this.createBranch("b6");
		TestState s7 = this.createBranch("b7");
		TestState s8 = this.createBranch("b8");
		s1.successors.add(s6);
		s1.successors.add(s7);
		s1.successors.add(s8);
		
		TestState s9 = this.createBranch("b9");
		TestState s10 = this.createBranch("b10");
//		TestState s11 = this.createBranch("b11");
		s2.successors.add(s9);
		s2.successors.add(s10);
//		s2.successors.add(s11);
		
		// Create the fourth layer.
		s3.successors.add(this.createTerminal("t0", 4));
		s3.successors.add(this.createTerminal("t1", 3));
//		s3.successors.add(this.createTerminal("t2", 6));
		
		s4.successors.add(this.createTerminal("t3", 6));
		s4.successors.add(this.createTerminal("t4", 2));
//		s4.successors.add(this.createTerminal("t5", 5));
		
//		s5.successors.add(this.createTerminal("t6", 5));
//		s5.successors.add(this.createTerminal("t7", 6));
//		s5.successors.add(this.createTerminal("t8", 7));
		
		s6.successors.add(this.createTerminal("t9", 2));
		s6.successors.add(this.createTerminal("t10", 1));
//		s6.successors.add(this.createTerminal("t11", 11));
		
		s7.successors.add(this.createTerminal("t12", 9));
		s7.successors.add(this.createTerminal("t13", 5));
//		s7.successors.add(this.createTerminal("t14", 2));
		
		s8.successors.add(this.createTerminal("t15", 3));
		s8.successors.add(this.createTerminal("t16", 1));
//		s8.successors.add(this.createTerminal("t17", 1));
		
		s9.successors.add(this.createTerminal("t18", 5));
		s9.successors.add(this.createTerminal("t19", 4));
//		s9.successors.add(this.createTerminal("t20", 2));
		
		s10.successors.add(this.createTerminal("t21", 7));
		s10.successors.add(this.createTerminal("t22", 5));
//		s10.successors.add(this.createTerminal("t23", 5));
		
//		s11.successors.add(this.createTerminal("t24", 8));
//		s11.successors.add(this.createTerminal("t25", 3));
//		s11.successors.add(this.createTerminal("t26", 7));
	}
	
	/**
	 * Create a branch state.
	 * @return - A branch state.
	 */
	public TestState createBranch(String name){
		TestState tempS = new TestState();
		tempS.successors = new ArrayList<State>();
		tempS.utility = null;
		tempS.name = name;
		return tempS;
	}
	
	/**
	 * Create a terminal state.
	 * @param utility - The utility to be assigned to the terminal state.
	 * @return - The terminal state.
	 */
	public TestState createTerminal(String name, int utility){
		TestState tempS = new TestState();
		tempS.successors = null;
		tempS.utility = utility;
		tempS.name = name;
		return tempS;
	}
	
	/**
	 * Retrieve the list of successor states.
	 * @return - The successor states.
	 */
	public ArrayList<State> newStates(){
		return this.successors;
	}
	
	/**
	 * Determine if the current state is a terminal state.
	 * @return - True if the state is a terminal state, false otherwise.
	 */
	public boolean isGameOver(){
		return this.utility != null && this.successors == null;
	}

	/**
	 * Compare this state to another state.
	 * @param state - The state to which to compare this state.
	 * @return - Negative or positive integer, or zero.
	 */
	public int compareTo(State state){
		// Store the utilities.
		int thisUtil = this.utility();
		int otherUtil = state.utility();
		
		// Determine which is larger.
		if (thisUtil > otherUtil){
			return 1;
		}
		else if (thisUtil < otherUtil){
			return -1;
		}
		else {
			return 0;
		}
	}
	
	/**
	 * Retrieve the estimated utility of the current state.
	 */
	public int utility(){
		return this.utility;
	}
	
	/**
	 * Switch the current player.
	 */
	public void switchPlayer(){
		if (this.player == PlayerEnum.PLAYER1){
			this.player = PlayerEnum.PLAYER2;
		}
		else {
			this.player = PlayerEnum.PLAYER1;
		}
	}
	
	/**
	 * Retrieve the current player.
	 */
	public PlayerEnum getPlayer(){
		return this.player;
	}
	
	/**
	 * Simply return the current state on a clone.
	 */
	public State clone(){
		return this;
	}
	
	/**
	 * Retrieve a representation of the board.
	 * @return - A string representation of the board.
	 */
	public String boardString(){
		return this.name;
	}
	
	/**
	 * Retrieve the string representation of the state.
	 */
	public String toString(){
		return this.name;
	}
}
