package assignment2;

import java.util.ArrayList;

/**
 * An interface for the necessities of a state.
 * @author Nickolas Gough and Sam Germain
 */
public interface State extends Cloneable, Comparable<State>{

	/**
	 * Retrieve the current player.
	 * @return - The enumeration of the current player.
	 */
	public PlayerEnum getPlayer();
	
	/**
	 * Switch the current player to the other player.
	 */
	public void switchPlayer();
	
	/**
	 * A method for deriving new states from the current state.
	 * @return - A collection of states derived from the current state.
	 */
	public ArrayList<State> newStates();
	
	/**
	 * A method for determining if the current state is terminal.
	 * @return - True if the state is a terminal state, false otherwise.
	 */
	public boolean isGameOver();
	
	/**
	 * Determine the utility of the current state.
	 * @return - The estimated utility ofthe current state.
	 */
	public int utility();
	
	/**
	 * Retrieve a string representation of the board.
	 * @return - A string representation of the board.
	 */
	public String boardString();
	
	/**
	 * Allow the state to be cloneable.
	 * @return - A clone of the current state.
	 */
	public State clone();
}
