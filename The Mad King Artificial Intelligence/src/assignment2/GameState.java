package assignment2;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A class for representing the state of the board. 
 * @author Nickolas Gough and Sam Germain
 */
public class GameState implements State, Cloneable, Comparable<State>{

	// Store the board size. 
	private final int boardSize = 5;

	// Store the board.
	private Position[][] board;

	// Store the current player. 
	private PlayerEnum player;

	// Store the position of the king. 
	private Position kingPosition;

	// Store the Guard positions.
	private Position[] guardPositions;
	
	// Store the maximum number of Guards in the game.
	private final int numGuards = 3;

	// Store the Dragon positions. 
	private Position[] dragonPositions;
	
	// Store the maximum number of Dragons in the game.
	private final int numDragons = 8;

	// Store the index at which to place the new Dragon.
	private int newDragonIndex;
	
	// Store the maximum number of turns. 
	private final int maxTurns = 50;
	
	// Store the number of turns taken.
	private int numTurns;
	
	// Store the game history of states.
	private HashMap<String, Integer> states;
	
	// Store the maximum number of repeated states.
	private final int maxStates = 3;
	
	/**
	 * The utility of a state in which player 1 will win.
	 */
	public static int AUTOWP1 = 10000;
	
	/**
	 * The utility of a state in which player 2 will win.
	 */
	public static int AUTOWP2 = -10000;

	/**
	 * Constructor for the game.
	 */
	public GameState(){
		// Initialize the board.
		this.board = new Position[this.boardSize][this.boardSize];
		this.guardPositions = new Position[this.numGuards];
		this.dragonPositions = new Position[this.numDragons];
		this.newDragonIndex = 5;
		this.numTurns = 0;
		this.states = new HashMap<String, Integer>();

		// Initialize the player.
		this.player = PlayerEnum.PLAYER2;
	}

	/**
	 * Initializes the start of the game.
	 */
	public void initializeGameStart(){
		// Initialize the state.
		this.initializePositions();
		this.initializeBoard();
		
		// Add the seen state to the game history.
		this.states.put(this.boardString(), 1);
	}

	/**
	 * Clear the board and assign each space to be blank.
	 */
	public void clearBoard(){
		// Initialize the rest of the board. 
		for (int n = 0; n < this.board.length; n += 1){
			for (int m = 0; m < this.board[n].length; m += 1){
				this.board[n][m] = new Position(n, m);
			}
		}
	}

	/**
	 * Initialize the positions of the pieces.
	 */
	private void initializePositions(){
		// Initialize the King's position.
		this.kingPosition = new Position(0, 2, "K0");

		// Initialize the guards' positions.
		this.guardPositions[0] = new Position(1, 1, "G0");
		this.guardPositions[1] = new Position(1, 2, "G1");
		this.guardPositions[2] = new Position(1, 3, "G2");

		// Initialize the Dragons' positions.
		this.dragonPositions[0] = new Position(3, 0, "D0");
		this.dragonPositions[1] = new Position(3, 1, "D1");
		this.dragonPositions[2] = new Position(3, 2, "D2");
		this.dragonPositions[3] = new Position(3, 3, "D3");
		this.dragonPositions[4] = new Position(3, 4, "D4");
	}

	/**
	 * Initialize the game board.
	 */
	private void initializeBoard(){
		// Clear the board.
		this.clearBoard();

		// Initialize the King's position.
		int x = this.kingPosition.getX();
		int y = this.kingPosition.getY();
		this.board[x][y] = this.kingPosition;

		// Initialize the Guards.
		for (Position tempP : this.guardPositions){
			if (tempP != null){
				this.board[tempP.getX()][tempP.getY()] = tempP;
			}
		}

		// Initialize the Dragons.
		for (Position tempP : this.dragonPositions){
			if (tempP != null){
				this.board[tempP.getX()][tempP.getY()] = tempP;
			}
		}
	}
	
	/**
	 * Determine if a King is occupying the specified position.
	 * @param x - The x position to examine.
	 * @param y - The y position to examine.
	 * @return - True if the position is occupied by a King, false otherwise.
	 */
	private boolean isKingAt(int x, int y){
		// Check that the position is within bounds.
		if (!this.isInBounds(x, y)){
			return false;
		}

		return this.board[x][y].getEnum() == PieceEnum.KING;
	}
	
	/**
	 * Determine if a Guard is occupying the specified position.
	 * @param x - The x position to examine.
	 * @param y - The y position to examine.
	 * @return - True if the position is occupied by a Guard, false otherwise.
	 */
	private boolean isGuardAt(int x, int y){
		// Check that the position is within bounds.
		if (!this.isInBounds(x, y)){
			return false;
		}

		return this.board[x][y].getEnum() == PieceEnum.GUARD;
	}
	
	/**
	 * Determine if a Dragon is occupying the location.
	 * @param x - The x location to examine.
	 * @param y - The y location to examine.
	 * @return - True if the location is occupied by a Dragon, false otherwise.
	 */
	private boolean isDragonAt(int x, int y){
		// Check that the position is within bounds.
		if (!this.isInBounds(x, y)){
			return false;
		}

		// Determine if a Dragon occupies the location.
		return this.board[x][y].getEnum() == PieceEnum.DRAGON;
	}
	
	/**
	 * Determine if the jump is valid given the x, y, and direction of jump.
	 * @param x - The x position from which the King is jumping.
	 * @param y - The y position from which the King is jumping.
	 * @param directionX - The direction along the x axis of the jump.
	 * @param directionY - The direction along the y axis of the jump.
	 * @return - True if the jump is valid, false otherwise.
	 */
	private boolean isValidKingJump(int x, int y, int directionX, int directionY){
		// Determine where the Guard should exist.
		if (directionX > 0){
			x += -1;
		}
		else if (directionX < 0){
			x += 1;
		}
		if (directionY > 0){
			y += -1;
		}
		else if (directionY < 0){
			y += 1;
		}

		// Check that a guard exists there.
		return this.isGuardAt(x, y);
	}
	
	/**
	 * Determine if a Dragon can be captured at the target position.
	 * @param target - The position at which the capture may occur. 
	 * @return - True if the capture is valid, false otherwise.
	 */
	private boolean isValidDragonCapture(Position target){
		// Store the necessary variables. 
		int x = target.getX();
		int y = target.getY();
		int numSurrGuards = 0;
		
		// Count the number of guards surrounding the Dragon.
		if (this.isGuardAt(x+1, y) || this.isKingAt(x+1, y)){
			numSurrGuards += 1;
		}
		if (this.isGuardAt(x-1, y) || this.isKingAt(x-1, y)){
			numSurrGuards += 1;
		}
		if (this.isGuardAt(x, y+1) || this.isKingAt(x, y+1)){
			numSurrGuards += 1;
		}
		if (this.isGuardAt(x, y-1) || this.isKingAt(x, y-1)){
			numSurrGuards += 1;
		}
		
		// Determine if the capture is valid.
		if (numSurrGuards >= 2){
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Determine if the move is valid for a King.
	 * @param location - The location of the King.
	 * @param target - The target location for the King.
	 * @param directionX - The direction of the proposed movement along the x-axis.
	 * @param directionY - The direction of the proposed movement along the y-axis.
	 * @return - True if the move is valid, otherwise false.
	 */
	private boolean isValidKingMove(Position target){
		// Store necessary variables. 
		int x = target.getX();
		int y = target.getY();
		PieceEnum tempE = this.board[x][y].getEnum();
		
		// Check that the King may occupy the space.
		boolean validDragonCapture = this.isValidDragonCapture(target);
		if (tempE == PieceEnum.DRAGON && !validDragonCapture){
			return false;
		}
		else if (tempE == PieceEnum.GUARD){
			return false;
		}

		// Compute the distance and direction traveled.
		int directionX = x-this.kingPosition.getX();
		int distanceX = Math.abs(directionX);
		int directionY = y-this.kingPosition.getY();
		int distanceY = Math.abs(directionY);
		int totalDistance = distanceX+distanceY;

		// Allow the King to move in the simple case.
		if (totalDistance == 1){
			return true;
		}

		// Check that the King is not moving outside of its limits.
		if (totalDistance > 2){
			return false;
		}

		// Check that the king is not moving diagonally.
		if (distanceX > 0 && distanceY > 0){
			return false;
		}

		// Determine if the Jump is valid.
		return this.isValidKingJump(x, y, directionX, directionY);
	}

	/**
	 * Determine if the move is valid for a Guard.
	 * @param location - The location of the Guard.
	 * @param target - The target move location.
	 * @return - True if the move is valid, false otherwise.
	 */
	private boolean isValidGuardMove(Position location, Position target){
		// Store necessary variables.
		int x = target.getX();
		int y = target.getY();
		PieceEnum tempE = this.board[x][y].getEnum();
		
		// Compute the distance and direction traveled.
		int directionX = x-location.getX();
		int distanceX = Math.abs(directionX);
		int directionY = y-location.getY();
		int distanceY = Math.abs(directionY);
		int totalDistance = distanceX+distanceY;

		// Check that the Guard is only moving one space horizontally or vertically.
		if (totalDistance > 1){
			return false;
		}

		// Check the space may be occupied by a Guard.
		if (tempE == PieceEnum.BLANK){
			return true;
		}
		if (tempE != PieceEnum.DRAGON){
			return false;
		}

		// Determine if the Dragon capture is valid.
		return this.isValidDragonCapture(target);
	}

	/**
	 * Determine if the move is valid for a Dragon.
	 * @param location - The location of the Dragon.
	 * @param target - The target location.
	 * @return - True if the move is valid, false otherwise.
	 */
	private boolean isValidDragonMove(Position location, Position target){
		// Store the necessary variables. 
		int x = target.getX();
		int y = target.getY();
		PieceEnum tempE = this.board[x][y].getEnum();
		
		// Check that the space can be occupied by the Dragon.
		if (tempE != PieceEnum.BLANK){
			return false;
		}

		// Compute the distance and direction traveled.
		int directionX = x-location.getX();
		int distanceX = Math.abs(directionX);
		int directionY = y-location.getY();
		int distanceY = Math.abs(directionY);
		int totalDistance = distanceX+distanceY;

		// Check the Dragon is moving within its limits.
		if (totalDistance > 2){
			return false;
		}

		// Check that the dragon does not move more than two spaces.
		boolean isLine = false;
		if ((distanceX > 0 && distanceY == 0) || (distanceX == 0 && distanceY > 0)){
			isLine = true;
		}
		if (totalDistance >= 2 && isLine){
			return false;
		}

		return true;
	}

	/**
	 * Determines if the move is valid.
	 * @return - True if the move is valid, false otherwise.
	 * @throws RuntimeException when no piece is being moved.
	 */
	public boolean isValidMove(Position pieceLocation, Position newLocation) throws RuntimeException{
		// Reject the move if it is out of bounds.
		if (!this.isInBounds(newLocation)){
			return false;
		}

		// Determine if the move is valid.
		PieceEnum tempE = pieceLocation.getEnum();
		if (tempE == PieceEnum.KING){
			return this.isValidKingMove(newLocation);
		}
		else if (tempE == PieceEnum.GUARD){
			return this.isValidGuardMove(pieceLocation, newLocation);
		}
		else if (tempE == PieceEnum.DRAGON){
			return this.isValidDragonMove(pieceLocation, newLocation);
		}
		else {
			throw new RuntimeException("Error: No game piece is being moved.");
		}
	}
	
	/**
	 * Count the number of Dragons surrounding a given position.
	 * @param target - The target to examine.
	 * @return - The number of Dragons surrounding the position.
	 */
	private int numDragonsSurrounding(Position target){
		// Store the necessary variables.
		int dCount = 0;
		int x = target.getX();
		int y = target.getY();

		// Count the Number of Dragons surrounding the Guard.
		if (this.isDragonAt(x+1, y)){
			dCount += 1;
		}
		if (this.isDragonAt(x-1, y)){
			dCount += 1;
		}
		if (this.isDragonAt(x, y+1)){
			dCount += 1;
		}
		if (this.isDragonAt(x, y-1)){
			dCount += 1;
		}

		return dCount;
	}

	/**
	 * Determine the number of Dragons surrounding a given position diagonally.
	 * @param target - The position to examine.
	 * @return - The number of Dragons surround the position diagonally.
	 */
	private int numDragonsSurroundingDiagonally(Position target){
		// Store the necessary variables.
		int dCount = 0;
		int x = target.getX();
		int y = target.getY();

		// Count the Number of Dragons surrounding the Guard.
		if (this.isDragonAt(x+1, y+1)){
			dCount += 1;
		}
		if (this.isDragonAt(x+1, y-1)){
			dCount += 1;
		}
		if (this.isDragonAt(x-1, y+1)){
			dCount += 1;
		}
		if (this.isDragonAt(x-1, y-1)){
			dCount += 1;
		}

		return dCount;
	}

	/**
	 * Count the number of Guards surrounding a given position.
	 * @param target - The target to examine.
	 * @return - The number of Guards surrounding the position.
	 */
	private int numGuardsSurrounding(Position target){
		// Store the necessary variables.
		int gCount = 0;
		int x = target.getX();
		int y = target.getY();

		// Count the Number of Dragons surrounding the Guard.
		if (this.isGuardAt(x+1, y) || this.isKingAt(x+1, y)){
			gCount += 1;
		}
		if (this.isGuardAt(x-1, y) || this.isKingAt(x-1, y)){
			gCount += 1;
		}
		if (this.isGuardAt(x, y+1) || this.isKingAt(x, y+1)){
			gCount += 1;
		}
		if (this.isGuardAt(x, y-1) || this.isKingAt(x, y-1)){
			gCount += 1;
		}
		
		return gCount;
	}
	
	/** 
	 * Determine the number of Guards and edges surround the given position.
	 * @param target - The target to examine.
	 * @return - The number of Guards and edges surrounding the position.
	 */
	private int numOthersSurrounding(Position target){
		// Store the necessary variables. 
		int x = target.getX();
		int y = target.getY();
		int numOthers = 0;
		
		// Count the number of surrounding Guards or edges. 
		if (this.isGuardAt(x+1, y) || !this.isInBounds(x+1, y)){
			numOthers += 1;
		}
		if (this.isGuardAt(x-1, y) || !this.isInBounds(x-1, y)){
			numOthers += 1;
		}
		if (this.isGuardAt(x, y+1) || !this.isInBounds(x, y+1)){
			numOthers += 1;
		}
		if (this.isGuardAt(x, y-1) || !this.isInBounds(x, y-1)){
			numOthers += 1;
		}
		
		return numOthers;
	}
	
	/**
	 * Remove all Guards that are now captured.
	 */
	private void captureAllGuards(){
		// Initialize helpful variables.
		int x = 0;
		int y = 0;
		Position tempP = null;

		// Count the number of Dragons surrounding each Guard and remove it if necessary.
		for (int n = 0; n < this.guardPositions.length; n += 1){
			tempP = this.guardPositions[n];
			
			// Check that the position is not null.
			if (tempP == null){
				continue;
			}
				
			// Convert the Guard if it has been captured.
			if (this.numDragonsSurrounding(tempP) >= 3){
				x = tempP.getX();
				y = tempP.getY();
				this.guardPositions[n] = null;
				tempP = new Position(x, y, "D"+this.newDragonIndex);
				this.dragonPositions[this.newDragonIndex] = tempP;
				this.board[x][y] = tempP;
				this.newDragonIndex += 1;
			}
		}
	}

	/**
	 * Move the original piece to the target.
	 * @param location - The original location of the position.
	 * @param target - The target to which to move the position.
	 */
	private void move(Position location, Position target){
		// Store the original location.
		int x = location.getX();
		int y = location.getY();
		Position tempP = this.board[x][y];

		// Make the position blank.
		this.board[x][y] = new Position(x, y);

		// Move the original piece.
		x = target.getX();
		y = target.getY();
		this.board[x][y] = tempP;
	}
	
	/**
	 * Move the King to the given position.
	 * @param target - The target to which to move the King.
	 */
	private void moveKing(Position target){
		// Move the King.
		this.kingPosition.setPosition(target);
		
		// Remove the Dragon if it is being captured.
		if (target.getEnum() == PieceEnum.DRAGON){
			this.dragonPositions[target.getIndex()] = null;
		}
	}

	/**
	 * Moves a Guard from its location to the target.
	 * @param location - The location of the target.
	 * @param target - The target location.
	 */
	private void moveGuard(Position location, Position target){
		// Move the guard.
		this.guardPositions[location.getIndex()].setPosition(target);

		// Remove the Dragon if it is being captured.
		if (target.getEnum() == PieceEnum.DRAGON){
			this.dragonPositions[target.getIndex()] = null;
		}
	}		

	/**
	 * Moves a Dragon from its location to the target.
	 * @param location - The location of the Dragon.
	 * @param target - The target to which to move the Dragon.
	 */
	private void moveDragon(Position location, Position target){
		// Move the dragon.
		this.dragonPositions[location.getIndex()].setPosition(target);

		// Remove all now captured guards.
		this.captureAllGuards();		
	}
	
	/**
	 * Move the piece based on the textual input.
	 * @param piece - A string representing which piece to move.
	 * @param target - The position to which to move the piece.
	 * @return - True if the move is successful, false otherwise.
	 * @throws NumberFormatException when the input is not correctly formated.
	 * @throws RuntimeException when no valid piece is input.
	 */
	public boolean movePiece(String piece, Position target) throws NumberFormatException, RuntimeException{
		// Determine the piece being moved.
		char tempC = piece.charAt(0);
		int index = Integer.parseInt(piece.substring(1));
		
		// Move the piece.
		if (tempC == 'K'){
			return this.movePiece(this.kingPosition, target);
		}
		else if (tempC == 'G'){
			return this.movePiece(this.guardPositions[index], target);
		}
		else if (tempC == 'D'){
			return this.movePiece(this.dragonPositions[index], target);
		}
		else {
			throw new RuntimeException("Error: No piece selected to move.");
		}
	}

	/**
	 * Move the specified piece from the location to the target.
	 * @param location - The current position of the piece to move.
	 * @param target - The target position to move the piece.
	 * @param piece - The enum of the piece to move.
	 * @return - True if the move is successful, false otherwise.
	 * @throws RuntimeException when no piece is being moved.
	 */
	public boolean movePiece(Position location, Position target) throws RuntimeException{
		// Ensure the move is valid.
		if (!this.isValidMove(location, target)){
			return false;
		}

		// Store the targeted position.
		Position tempP = this.board[target.getX()][target.getY()];
		
		// Make the move on the board.
		this.move(location, tempP);

		// Adjust the known positions accordingly.
		if (location.getEnum() == PieceEnum.KING){
			this.moveKing(tempP);
		}
		else if (location.getEnum() == PieceEnum.GUARD){
			this.moveGuard(location, tempP);
		}
		else if (location.getEnum() == PieceEnum.DRAGON){
			this.moveDragon(location, tempP);
		}
		else {
			throw new RuntimeException("Error: No piece selected to move.");
		}
		
		// Add the state to the game history.
		int tempV = 1;
		String boardState = this.boardString();
		if (this.states.containsKey(boardState)){
			tempV = this.states.get(boardState);
			this.states.put(boardState, tempV+1);
		}
		else {
			this.states.put(boardState, tempV);
		}

		return true;
	}

	/**
	 * Add a new state to the list of states
	 * @param states - The list of states to add to.
	 * @param location - The current position of the piece being moved.
	 * @param target - The target position to move the piece to.
	 * @param piece - The enum of the piece that is being moved.
	 */
	public void addState(ArrayList<State> states, Position location, Position target){
		GameState tempS = this.clone();
		if (tempS.movePiece(location, target)){
			states.add(tempS);
		}
	}

	/**
	 * Add the new state to the list of states generated from this state.
	 * @param states - The list to add to.
	 * @param location - The current location of the piece being moved.
	 * @param target - The target to which to move the piece.
	 * @param piece - The enum of the piece being moved.
	 */
	private void addStates(ArrayList<State> states, Position location, int increment){
		// Check that the location is not null.
		if (location == null){
			return;
		}
		
		// Instantiate the temp position and make the moves.
		Position tempP = null;
		int x = location.getX();
		int y = location.getY();

		// Make all the moves that can be made by the piece.
		tempP = location.clone();
		tempP.setPosition(x+increment, y);
		this.addState(states, location, tempP);
		tempP = location.clone();
		tempP.setPosition(x-increment, y);
		this.addState(states, location, tempP);
		tempP = location.clone();
		tempP.setPosition(x, y+increment);
		this.addState(states, location, tempP);
		tempP = location.clone();
		tempP.setPosition(x, y-increment);
		this.addState(states, location, tempP);

		// Make the additional moves that can be made by Dragons.
		if (location.getEnum() != PieceEnum.DRAGON){
			return;
		}

		tempP = location.clone();
		tempP.setPosition(x+increment, y+increment);
		this.addState(states, location, tempP);
		tempP = location.clone();
		tempP.setPosition(x+increment, y-increment);
		this.addState(states, location, tempP);
		tempP = location.clone();
		tempP.setPosition(x-increment, y+increment);
		this.addState(states, location, tempP);
		tempP = location.clone();
		tempP.setPosition(x-increment, y-increment);
		this.addState(states, location, tempP);
	}

	/**
	 * Generates the new states from the current game position.
	 * @return - The new states.
	 */
	public ArrayList<State> newStates(){
		// Instantiate the list of possible states.
		ArrayList<State> states = new ArrayList<State>();
		int increment = 0;

		// Generate the states based on player turn. 
		if (this.player == PlayerEnum.PLAYER1){
			// Generate states by moving the king by one.
			increment = 1;
			this.addStates(states, this.kingPosition, increment);

			// Generate the states by moving the king by two.
			increment = 2;
			this.addStates(states, this.kingPosition, increment);

			// Generate the states by moving the Guards.
			increment = 1;
			for (Position tempP : this.guardPositions){
				this.addStates(states, tempP, increment);
			}
		}
		else {
			// Generate the states by moving the Dragons.
			increment = 1;
			for (Position tempP : this.dragonPositions){
				this.addStates(states, tempP, increment);
			}
		}

		return states;
	}

	/**
	 * Switch the current player.
	 */
	public void switchPlayer(){
		// Switch the player.
		if (this.player == PlayerEnum.PLAYER1){
			this.player = PlayerEnum.PLAYER2;
		}
		else {
			this.player = PlayerEnum.PLAYER1;
		}
		
		// Increase the number of turns taken.
		this.numTurns += 1;
	}

	/**
	 * Determine if it is the player's turn based on the piece.
	 * @param piece - The piece for which to determine is active.
	 * @return - True if it is the player's turn, false otherwise.
	 */
	public boolean isPlayerTurn(PieceEnum piece){
		// Determine if it is player one's turn.
		boolean kingOrGuard = false;
		if ((piece == PieceEnum.KING || piece == PieceEnum.GUARD)){
			kingOrGuard = true;
		}
		if (this.player == PlayerEnum.PLAYER1 && kingOrGuard){
			return true;
		}
		
		// Determine if it is player two's turn.
		if (this.player == PlayerEnum.PLAYER2 && piece == PieceEnum.DRAGON){
			return true;
		}

		return false;
	}

	/**
	 * Retrieve the current player.
	 * @return - The player who is currently playing.
	 */
	public PlayerEnum getPlayer(){
		return this.player;
	}

	/**
	 * Determine if the specified position is within bounds.
	 * @param x - The x position to examine.
	 * @param y - The y position to examine.
	 * @return - True if the position is within bounds. 
	 */
	private boolean isInBounds(Position position){
		return this.isInBounds(position.getX(), position.getY());
	}

	/**
	 * Determine if the specified position is within bounds.
	 * @param x - The x position to examine.
	 * @param y - The y position to examine.
	 * @return - True if the position is within bounds. 
	 */
	private boolean isInBounds(int x, int y){
		if (x < 0 || x >= this.boardSize){
			return false;
		}
		if (y < 0 || y >= this.boardSize){
			return false;
		}

		return true;
	}
	
	/**
	 * Determine if the position is occupied.
	 * @param position - The position to examine. 
	 * @return - True if the position is occupied, false otherwise.
	 */
	private boolean isOccupied(Position position){
		return this.isOccupied(position.getX(), position.getY());
	}
	
	/**
	 * Determine if the position is occupied.
	 * @param x - The x position to examine.
	 * @param y - The y position to examine.
	 * @return - True if the position is occupied, false otherwise.
	 */
	private boolean isOccupied(int x, int y){
		// Check that the position is in bounds.
		if (!this.isInBounds(x, y)){
			return true;
		}
		
		return this.board[x][y].getEnum() != PieceEnum.BLANK;
	}
	
	/**
	 * Determine if the King can move to the given position.
	 * @param x - The x position.
	 * @param y - The y position.
	 * @return - True if the King can move, false otherwise.
	 */
	private boolean isKingCaptured(){
		// Determine if the King is completely surrounded.
		int numDragons = this.numDragonsSurrounding(this.kingPosition);
		int numOthers = this.numOthersSurrounding(this.kingPosition);

		// Determine if the King is captured.
		if (numDragons >= 3 && (numDragons+numOthers) >= 4){
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Determine if the King is capable of moving.
	 * @param increment - The increment by which to move the King.
	 * @return - True if the King can move, false otherwise.
	 */
	private boolean canKingMove(int increment){
		// Store the necessary variables.
		int x = this.kingPosition.getX();
		int y = this.kingPosition.getY();
		
		// Determine if the King has any valid moves.
		Position tempP = new Position(x+increment, y);
		if (this.isValidMove(this.kingPosition, tempP)){
			return true;
		}
		
		tempP = new Position(x-increment, y);
		if (this.isValidMove(this.kingPosition, tempP)){
			return true;
		}

		tempP = new Position(x, y+increment);
		if (this.isValidMove(this.kingPosition, tempP)){
			return true;
		}

		tempP = new Position(x, y-increment);
		if (this.isValidMove(this.kingPosition, tempP)){
			return true;
		}

		return false;
	}

	/**
	 * Determine if any Guard can make a move.
	 * @return - True if a Guard can make a move, false otherwise.
	 */
	private boolean canGuardMove(){
		// Store the necessary variables.
		int x = 0;
		int y = 0;
		Position tempP = null;
		Position gPosition = null;
		
		// Determine if there exists a Guard that can move.
		for (int n = 0; n < this.guardPositions.length ; n += 1){
			gPosition = this.guardPositions[n];
			
			// Check that the position is not null.
			if (gPosition == null){
				continue;
			}
			
			// Determine if the current Guard can move.
			x = gPosition.getX();
			y = gPosition.getY();
			tempP = new Position(x+1, y);
			if (this.isValidMove(gPosition, tempP)){
				return true;
			}

			tempP = new Position(x-1, y);
			if (this.isValidMove(gPosition, tempP)){
				return true;
			}

			tempP = new Position(x, y+1);
			if (this.isValidMove(gPosition, tempP)){
				return true;
			}

			tempP = new Position(x, y-1);
			if (this.isValidMove(gPosition, tempP)){
				return true;
			}
		}

		return false;
	}

	/**
	 * Determine if there exists a Dragon that can move.
	 * @return - True if a Dragon can move, false otherwise.
	 */
	private boolean canDragonMove(){
		// Store the necessary variables.
		int x = 0;
		int y = 0;
		Position tempP = null;
		Position dPosition = null;
		
		// Determine if there exists at least one Dragon that can move.
		for (int n = 0; n < this.dragonPositions.length; n += 1){
			dPosition = this.dragonPositions[n];
			
			// Check that the position is not null.
			if (dPosition == null){
				continue;
			}
			
			// Check the horizontal and vertical moves.
			x = dPosition.getX();
			y = dPosition.getY();
			tempP = new Position(x+1, y);
			if (this.isValidMove(dPosition, tempP)){
				return true;
			}

			tempP = new Position(x-1, y);
			if (this.isValidMove(dPosition, tempP)){
				return true;
			}

			tempP = new Position(x, y+1);
			if (this.isValidMove(dPosition, tempP)){
				return true;
			}

			tempP = new Position(x, y-1);
			if (this.isValidMove(dPosition, tempP)){
				return true;
			}

			// Check all the diagonal moves.
			tempP = new Position(x+1, y-1);
			if (this.isValidMove(dPosition, tempP)){
				return true;
			}

			tempP = new Position(x+1, y+1);
			if (this.isValidMove(dPosition, tempP)){
				return true;
			}

			tempP = new Position(x-1, y-1);
			if (this.isValidMove(dPosition, tempP)){
				return true;
			}

			tempP = new Position(x-1, y+1);
			if (this.isValidMove(dPosition, tempP)){
				return true;
			}
		}

		return false;
	}

	/**
	 * Determine if the game is over.
	 * @return - True if the game is over, false otherwise.
	 */
	public boolean isGameOver(){
		// Determine if the King has reached the bottom row.
		if (this.kingPosition.getX() >= (this.boardSize-1)){
			return true;
		}

		// Determine if the King has been captured.
		if (this.isKingCaptured()){
			return true;
		}
		
		// Determine if the last move has been played.
		if (this.numTurns >= this.maxTurns){
			return true;
		}
		
		// Determine the same state has been seen three times. 
		for (Integer tempI : this.states.values()){
			if (tempI >= this.maxStates){
				return true;
			}
		}

		// Determine if the player can make a play.
		if (this.player == PlayerEnum.PLAYER1){
			// Determine if the player can make a play with the King.
			if (this.canKingMove(1)){
				return false;
			}
			if (this.canKingMove(2)){
				return false;
			}

			// Determine if the player can make a play with a Guard.
			if (this.canGuardMove()){
				return false;
			}
		}
		else {
			// Determine if a Dragon can move.
			if (this.canDragonMove()){
				return false;
			}
		}

		return true;
	}

	/**
	 * Determine who has won the game.
	 * @return - The player that has won or draw if no player won.
	 */
	public PlayerEnum winner(){
		// Check that the game has ended.
		if (!this.isGameOver()){
			return null;
		}

		// Determine if the King has reached the bottom row.
		if (this.kingPosition.getX() >= (this.boardSize-1)){
			return PlayerEnum.PLAYER1;
		}

		// Determine if the King has been captured.
		if (this.isKingCaptured()){
			return PlayerEnum.PLAYER2;
		}

		// The game is a draw.
		return PlayerEnum.DRAW;
	}

	/**
	 * Determines if the King is in danger in its current position.
	 * @return - True if the king will be lost at this position, false otherwise.
	 */
	private boolean isKingInDanger(){
		// Initialize the variables.
		int x = this.kingPosition.getX();
		int y = this.kingPosition.getY();
		Position unoccupied = null;

		// Check if the King could be lost.
		int numDragons = this.numDragonsSurrounding(this.kingPosition);
		int numOthers = this.numOthersSurrounding(this.kingPosition);
		if (numDragons >= 3 || (numDragons >= 2 && numOthers > 0)){
			if (!this.isOccupied(x+1, y)){
				unoccupied = new Position(x+1, y);
			}
			if (!this.isOccupied(x-1, y)){
				unoccupied = new Position(x-1, y);
			}
			if (!this.isOccupied(x, y+1)){
				unoccupied = new Position(x, y+1);
			}
			if (!this.isOccupied(x, y-1)){
				unoccupied = new Position(x, y-1);
			}

			// Check if the King could be lost.
			if (unoccupied != null){
				if (this.numDragonsSurrounding(unoccupied) > 0){
					return true;
				}
				if (this.numDragonsSurroundingDiagonally(unoccupied) > 2){
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Determines if a Guard could be lost on the next turn.
	 * @return - The number of Dragons in danger.
	 */
	private int numGuardsInDanger(){
		// Initialize the variables.
		int x = 0;
		int y = 0;
		Position tempP = null;
		ArrayList<Position> unoccupied = null;
		int count = 0;
		
		// Count the Guards that could be lost.
		for (int i = 0; i < this.guardPositions.length; i += 1){
			// Store the position.
			tempP = this.guardPositions[i];
			unoccupied = new ArrayList<Position>();
			
			// Check if the Guard could be lost.
			if (tempP != null){
				x = tempP.getX();
				y = tempP.getY();
				if (this.numDragonsSurrounding(tempP) >= 2){
					if (!this.isOccupied(x+1, y)){
						unoccupied.add(new Position(x+1, y));
					}
					if (!this.isOccupied(x-1, y)){
						unoccupied.add(new Position(x-1, y));
					}
					if (!this.isOccupied(x, y+1)){
						unoccupied.add(new Position(x, y+1));
					}
					if (!this.isOccupied(x, y-1)){
						unoccupied.add(new Position(x, y-1));
					}
				}
			}

			// Increase the count for each Guard that could be lost.
			for (int n = 0; n < unoccupied.size(); n += 1){
				tempP = unoccupied.get(n);
				if (this.numDragonsSurrounding(tempP) > 0){
					count += 1;
					break;
				}
				if (this.numDragonsSurroundingDiagonally(tempP) > 1){
					count += 1;
					break;
				}
			}
		}
		
		return count;
	}
	
	/**
	 * Determines how many Dragons are in Danger.
	 * @return - The number of Dragons in danger.
	 */
	private int numDragonsInDanger(){
		// Initialize the variables.
		int x = 0;
		int y = 0;
		Position tempP = null;
		ArrayList<Position> unoccupied = null;
		int count = 0;

		// Count the Dragons that could be lost.
		for (int i = 0; i < this.dragonPositions.length; i += 1){
			// Store the position.
			tempP = this.dragonPositions[i];
			unoccupied = new ArrayList<Position>();

			// Check if the Dragon could be lost.
			if (tempP != null){
				x = tempP.getX();
				y = tempP.getY();
				if (this.numGuardsSurrounding(tempP) >= 1){
					if (!this.isOccupied(x+1, y)){
						unoccupied.add(new Position(x+1, y));
					}
					if (!this.isOccupied(x-1, y)){
						unoccupied.add(new Position(x-1, y));
					}
					if (!this.isOccupied(x, y+1)){
						unoccupied.add(new Position(x, y+1));
					}
					if (!this.isOccupied(x, y-1)){
						unoccupied.add(new Position(x, y-1));
					}
				}
			}
			
			// Increase the count if the Dragon could be lost.
			for (Position tempL : unoccupied){
				if (this.numGuardsSurrounding(tempL) > 0){
					count += 1;
					break;
				}
			}
		}

		return count;
	}
	
	/**
	 * Determine if the King is obstructed. 
	 * @param x - The x position of the King.
	 * @param y - The y position of the King.
	 * @return - True if the King is obstructed, false otherwise.
	 */
	private boolean isKingObstructed(int x, int y){
		// Determine the increment.
		int increment = 2;
		if (x == 3){
			increment = 1;
		}
		
		// Determine if the King is obstructed when just before the goal
		for (int n = x; n <= x+increment; n += 1){
			for (int m = y-increment; m <= y+increment; m += 1){
				if (this.isDragonAt(n, m)){
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Determine the utility of the current state.
	 * @return - The utility of the current state.
	 * TODO: Improve the evaluation function.
	 */
	public int utility(){
		// Define the values of each of the pieces and conditions for player 1.
		int kingValue = 2;
		int guardValue = 1;
		int dragonLossValue = 1;
		int kingDistance = 20;
		int automaticWinPlayer1 = GameState.AUTOWP1;	
		
		// Define values of each of the pieces and conditions for player 2.
		int dragonValue = -1;
		int guardLossValue = -1;
		int captureKing = -20;
		int automaticWinPlayer2 = GameState.AUTOWP2; 

		// Compute the utility of the state.
		int result = 0;
		int x = this.kingPosition.getX();
		int y = this.kingPosition.getY();
		
		// Return zero if the game is a draw, because nobody won. 
		PlayerEnum tempE = this.winner();
		if (tempE != null && tempE == PlayerEnum.DRAW){
			return 0;
		}
		
		// Determine if the King or Dragons are in a winning position.
		if (x >= (this.boardSize-1)){
			return automaticWinPlayer1;
		}
		if (this.isKingCaptured()){
			return automaticWinPlayer2;
		}

		// Determine if the King has an unobstructed path to victory.
		if (x >= 2 && !this.isKingObstructed(x, y)){
			return automaticWinPlayer1-1;
		}
		if (this.isKingInDanger()){
			return automaticWinPlayer2+1;
		}

		result += guardLossValue*this.numGuardsInDanger();
		result += dragonLossValue*this.numDragonsInDanger();
		
		// Count the number of Dragons surrounding the King.
		result += captureKing*this.numDragonsSurrounding(this.kingPosition);
		if (this.numOthersSurrounding(this.kingPosition) > 0){
			result += captureKing;
		}

		// Compute the King's distance from the goal.
		result += kingDistance*x;

		// Determine the weight of the King.
		result += kingValue;

		// Determine the weight of the Guards.
		for (int n = 0; n < this.guardPositions.length; n += 1){
			if (this.guardPositions[n] != null){
				result += guardValue;
			}
		}

		// Determine the weight of the Dragons.
		for (int n = 0; n < this.newDragonIndex; n += 1){
			if (this.dragonPositions[n] != null){
				result += dragonValue;
			}
		}
		
		return result;
	}

	/**
	 * Generate a deep clone of this state.
	 */
	public GameState clone(){
		// Instantiate the clone.
		GameState tempS = new GameState();
		tempS.clearBoard();
		Position tempP = null;

		// Assign the player.
		tempS.player = this.player;

		// Assign the king's position.
		tempS.kingPosition = this.kingPosition.clone();

		// Generate the positions of the guards.
		Position[] tempG = new Position[this.numGuards];
		for (int n = 0; n < this.guardPositions.length; n += 1){
			tempP = this.guardPositions[n];
			if (tempP != null){
				tempG[n] = tempP.clone();
			}
		}
		tempS.guardPositions = tempG;

		// Generate the positions of the dragons.
		Position[] tempD = new Position[this.numDragons];
		for (int n = 0; n < this.dragonPositions.length; n += 1){
			tempP = this.dragonPositions[n];
			if (tempP != null){
				tempD[n] = tempP.clone();
			}
		}
		tempS.dragonPositions = tempD;
		
		// Store the index for new Dragons. 
		tempS.newDragonIndex = this.newDragonIndex;

		// Initialize the board with the new positions.
		tempS.initializeBoard();
		
		// Copy the states over. 
		HashMap<String, Integer> newStates = new HashMap<String, Integer>();
		for (String tempString : this.states.keySet()){
			newStates.put(tempString, this.states.get(tempString));
		}
		tempS.states = newStates;

		return tempS;
	}
	
	/**
	 * Compare this state to another state.
	 * @param state - The state to which to compare this state.
	 * @return - A negative integer if this state is less than the specified state,
	 * zero if the states are equal, and a positive integer if this state is greater
	 * than the specified state.
	 */
	public int compareTo(State state){
		// Store the utiltities.
		int thisUtil = this.utility();
		int otherUtil = state.utility();

		// Determine which state is better.
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
	 * Determine if this state is equivalent to another state.
	 * @param state - The state to compare this state to.
	 * @return - True if the states are equal, false otherwise.
	 */
	public boolean equals(GameState state){
		// Check that the players are equal.
		if (this.player != state.player){
			return false;
		}

		// Check that the King position is equal.
		if (!this.kingPosition.equals(state.kingPosition)){
			return false;
		}

		// Check that the Guard positions are all equal. 
		Position tempP = null;
		for (int n = 0; n < this.guardPositions.length; n += 1){
			tempP = this.guardPositions[n];
			if (tempP != null && !state.guardPositions[n].equals(tempP)){
				return false;
			}
		}

		// Check that the dragon positions are the same.
		for (int n = 0; n < this.dragonPositions.length; n += 1){
			tempP = this.dragonPositions[n];
			if (tempP != null && !state.dragonPositions[n].equals(tempP)){
				return false;
			}
		}

		// Check that the board is in the same state.
		for (int n = 0; n < this.boardSize; n += 1){
			for (int m = 0; m < this.boardSize; m += 1){
				if (this.board[n][m].getEnum() != state.board[n][m].getEnum()){
					return false;
				}
			}
		}
		
		// Check that the Dragon indices are identical.
		if (this.newDragonIndex != state.newDragonIndex){
			return false;
		}
		
		// Check the number of plays. 
		if (this.numTurns != state.numTurns){
			return false;
		}
		
		// Check the states within the game history.
		for (String tempString : state.states.keySet()){
			if (!this.states.containsKey(tempString)){
				return false;
			}
			if (!this.states.get(tempString).equals(state.states.get(tempString))){
				return false;
			}
		}

		return true;
	}
	
	/**
	 * Retrieve a string representation of the board state.
	 * @return - A string representation of the board state.
	 */
	public String boardString(){
		// Initialize the result.
		String result = "";

		// Print the board with coordinates on the left.
		PieceEnum tempE = null;
		for (int n = 0; n < this.boardSize; n += 1){
			for (int m = 0; m < this.boardSize; m += 1){
				tempE = this.board[n][m].getEnum();
				result += tempE.toString() + " ";
			}
			
			result += System.lineSeparator();
		}

		return result;
	}

	/**
	 * Return a string of the game.
	 */
	public String toString(){
		// Initialize the result.
		String result = "";

		// Print the board with coordinates on the left.
		for (int n = 0; n < this.boardSize; n += 1){
			// Add some coordinates at the side.
			result += n + "|";
			for (int m = 0; m < this.boardSize; m += 1){
				result += this.board[n][m].getName() + " ";
				if (this.board[n][m].getEnum() == PieceEnum.BLANK){
					result += " ";
				}
			}
			
			result += System.lineSeparator();
		}

		// Add some coordinates at the bottom.
		result += " ---------------" + System.lineSeparator() + "  ";
		for (int n = 0; n < this.boardSize; n += 1){
			result += n + "  ";
		}

		return result;
	}

	/**
	 * Print the state to the console.
	 */
	public void printState(){
		System.out.println(this.toString());
	}

	/**
	 * Main method for testing purposes.
	 * @param args - The arguments to the main function.
	 */
	public static void main(String[] args){
		// Instantiate the state object.
		GameState state = new GameState();
		GameState tempS = null;
		Position tPosition = null;
		int x = 0;
		int y = 0;

		// Test the clear board method. 
		state.clearBoard();
		for (int n = 0; n < state.boardSize; n += 1){
			for (int m = 0; m < state.boardSize; m += 1){
				if (state.board[n][m].getEnum() != PieceEnum.BLANK){
					System.out.println("Error the game board is not cleared after clearing it.");
					n = state.boardSize;
					m = state.boardSize;
				}
			}
		}

		// Test the initializeGameStart method.
		state.initializeGameStart();
		if (state.kingPosition.getX() != 0 || state.kingPosition.getY() != 2){
			System.out.println("The King's position was not initialized correcly.");
		}
		for (int n = 1, m = 0; n < 4 && m < state.boardSize; n +=1, m += 1){
			tPosition = state.guardPositions[m];
			if (tPosition.getX() != 1 || tPosition.getY() != n){
				System.out.println("Error: The Guards' positions were not initialized correctly.");
				break;
			}
		}
		for (int n = 0, m = 0; n < state.boardSize && m < state.boardSize; n +=1, m += 1){
			tPosition = state.dragonPositions[m];
			if (tPosition.getX() != 3 || tPosition.getY() != n){
				System.out.println("Error: The Dragons' positions were not initialized correctly.");
				break;
			}
		}

		// Test the initialize board method.
		x = state.kingPosition.getX();
		y = state.kingPosition.getY();
		if (!state.board[x][y].equals(state.kingPosition)){
			System.out.println("The King's board position was not initialized correctly.");
		}
		for (Position tempP : state.guardPositions){
			if (tempP != null){
				x = tempP.getX();
				y = tempP.getY();
				if (!state.board[x][y].equals(tempP)){
					System.out.println("Error: The Guards' board positions were not initialized correctly.");
					break;
				}
			}
		}
		for (Position tempP : state.dragonPositions){
			if (tempP != null){
				x = tempP.getX();
				y = tempP.getY();
				if (!state.board[x][y].equals(tempP)){
					System.out.println("Error: The Dragons' board positions were not initialized correctly.");
					break;
				}
			}
		}

		// Test the movePiece method.
		// Jump the King forward over a Guard.
		tempS = state.clone();
		tPosition = new Position(2, 2);
		tempS.movePiece(tempS.kingPosition, tPosition);
		x = tempS.kingPosition.getX();
		y = tempS.kingPosition.getY();
		if (x != 2 || y != 2){
			System.out.println("Error: The movePieceMethod did not correctly move the King.");
		}
		if (tempS.board[x][y].getEnum() != PieceEnum.KING){
			System.out.println("Error: The movePieceMethod did not correctly move the King.");
		}
		// Jump the King back over a Guard.
		tPosition = new Position(0, 2);
		tempS.movePiece(tempS.kingPosition, tPosition);
		x = tempS.kingPosition.getX();
		y = tempS.kingPosition.getY();
		if (x != 0 || y != 2){
			System.out.println("Error: The movePieceMethod did not correctly move the King.");
		}
		if (tempS.board[x][y].getEnum() != PieceEnum.KING){
			System.out.println("Error: The movePieceMethod did not correctly move the King.");
		}
		// Jump the King right over a Guard.
		tPosition = new Position(0, 4);
		tempS.board[0][3] = tempS.guardPositions[0];
		tempS.movePiece(tempS.kingPosition, tPosition);
		x = tempS.kingPosition.getX();
		y = tempS.kingPosition.getY();
		if (x != 0 || y != 4){
			System.out.println("Error: The movePieceMethod did not correctly move the King.");
		}
		if (tempS.board[x][y].getEnum() != PieceEnum.KING){
			System.out.println("Error: The movePieceMethod did not correctly move the King.");
		}
		// Jump the King left over a Guard.
		tempS = state.clone();
		tPosition = new Position(0, 0);
		tempS.board[0][1] = tempS.guardPositions[0];
		tempS.movePiece(tempS.kingPosition, tPosition);
		x = tempS.kingPosition.getX();
		y = tempS.kingPosition.getY();
		if (x != 0 || y != 0){
			System.out.println("Error: The movePieceMethod did not correctly move the King.");
		}
		if (tempS.board[x][y].getEnum() != PieceEnum.KING){
			System.out.println("Error: The movePieceMethod did not correctly move the King.");
		}
		// Move the King To the left.
		tempS = state.clone();
		tPosition = new Position(0, 1);
		tempS.movePiece(tempS.kingPosition, tPosition);
		x = tempS.kingPosition.getX();
		y = tempS.kingPosition.getY();
		if (x != 0 || y != 1){
			System.out.println("Error: The movePieceMethod did not correctly move the King.");
		}
		if (tempS.board[x][y].getEnum() != PieceEnum.KING){
			System.out.println("Error: The movePieceMethod did not correctly move the King.");
		}
		// Move the King To the right.
		tempS = state.clone();
		tPosition = new Position(0, 3);
		tempS.movePiece(tempS.kingPosition, tPosition);
		x = tempS.kingPosition.getX();
		y = tempS.kingPosition.getY();
		if (x != 0 || y != 3){
			System.out.println("Error: The movePieceMethod did not correctly move the King.");
		}
		if (tempS.board[x][y].getEnum() != PieceEnum.KING){
			System.out.println("Error: The movePieceMethod did not correctly move the King.");
		}
		// Move the King forward.
		tempS = state.clone();
		tPosition = new Position(1, 2);
		tempS.board[1][2] = tPosition;
		tempS.movePiece(tempS.kingPosition, tPosition);
		x = tempS.kingPosition.getX();
		y = tempS.kingPosition.getY();
		if (x != 1 || y != 2){
			System.out.println("Error: The movePieceMethod did not correctly move the King.");
		}
		if (tempS.board[x][y].getEnum() != PieceEnum.KING){
			System.out.println("Error: The movePieceMethod did not correctly move the King.");
		}
		// Move the King backward.
		tPosition = new Position(0, 2);
		tempS.movePiece(tempS.kingPosition, tPosition);
		x = tempS.kingPosition.getX();
		y = tempS.kingPosition.getY();
		if (x != 0 || y != 2){
			System.out.println("Error: The movePieceMethod did not correctly move the King.");
		}
		if (tempS.board[x][y].getEnum() != PieceEnum.KING){
			System.out.println("Error: The movePieceMethod did not correctly move the King.");
		}

		// Move a Guard forward.
		tempS = state.clone();
		tPosition = new Position(2, 2);
		tempS.movePiece(tempS.guardPositions[1], tPosition);
		x = tempS.guardPositions[1].getX();
		y = tempS.guardPositions[1].getY();
		if (x != 2 || y != 2){
			System.out.println("Error: The movePieceMethod did not correctly move a Guard.");
		}
		if (tempS.board[x][y].getEnum() != PieceEnum.GUARD){
			System.out.println("Error: The movePieceMethod did not correctly move a Guard.");
		}
		// Move a Guard Backward.
		tPosition = new Position(1, 2);
		tempS.movePiece(tempS.guardPositions[1], tPosition);
		x = tempS.guardPositions[1].getX();
		y = tempS.guardPositions[1].getY();
		if (x != 1 || y != 2){
			System.out.println("Error: The movePieceMethod did not correctly move a Guard.");
		}
		if (tempS.board[x][y].getEnum() != PieceEnum.GUARD){
			System.out.println("Error: The movePieceMethod did not correctly move a Guard.");
		}
		// Move a Guard left.
		tempS = state.clone();
		tPosition = new Position(1, 0);
		tempS.movePiece(tempS.guardPositions[0], tPosition);
		x = tempS.guardPositions[0].getX();
		y = tempS.guardPositions[0].getY();
		if (x != 1 || y != 0){
			System.out.println("Error: The movePieceMethod did not correctly move a Guard.");
		}
		if (tempS.board[x][y].getEnum() != PieceEnum.GUARD){
			System.out.println("Error: The movePieceMethod did not correctly move a Guard.");
		}
		// Move a Guard right.
		tempS = state.clone();
		tPosition = new Position(1, 4);
		tempS.movePiece(tempS.guardPositions[2], tPosition);
		x = tempS.guardPositions[2].getX();
		y = tempS.guardPositions[2].getY();
		if (x != 1 || y != 4){
			System.out.println("Error: The movePieceMethod did not correctly move a Guard.");
		}
		if (tempS.board[x][y].getEnum() != PieceEnum.GUARD){
			System.out.println("Error: The movePieceMethod did not correctly move a Guard.");
		}

		// Move a Dragon forward.
		tempS = state.clone();
		tPosition = new Position(2, 0);
		tempS.movePiece(tempS.dragonPositions[0], tPosition);
		x = tempS.dragonPositions[0].getX();
		y = tempS.dragonPositions[0].getY();
		if (x != 2 || y != 0){
			System.out.println("Error: The movePieceMethod did not correctly move a Dragon.");
		}
		if (tempS.board[x][y].getEnum() != PieceEnum.DRAGON){
			System.out.println("Error: The movePieceMethod did not correctly move a Dragon.");
		}
		// Move a Dragon right.
		tPosition = new Position(2, 1);
		tempS.movePiece(tempS.dragonPositions[0], tPosition);
		x = tempS.dragonPositions[0].getX();
		y = tempS.dragonPositions[0].getY();
		if (x != 2 || y != 1){
			System.out.println("Error: The movePieceMethod did not correctly move a Dragon.");
		}
		if (tempS.board[x][y].getEnum() != PieceEnum.DRAGON){
			System.out.println("Error: The movePieceMethod did not correctly move a Dragon.");
		}
		// Move a Dragon left.
		tPosition = new Position(2, 0);
		tempS.movePiece(tempS.dragonPositions[0], tPosition);
		x = tempS.dragonPositions[0].getX();
		y = tempS.dragonPositions[0].getY();
		if (x != 2 || y != 0){
			System.out.println("Error: The movePieceMethod did not correctly move a Dragon.");
		}
		if (tempS.board[x][y].getEnum() != PieceEnum.DRAGON){
			System.out.println("Error: The movePieceMethod did not correctly move a Dragon.");
		}
		// Move a Dragon backwards.
		tPosition = new Position(3, 0);
		tempS.movePiece(tempS.dragonPositions[0], tPosition);
		x = tempS.dragonPositions[0].getX();
		y = tempS.dragonPositions[0].getY();
		if (x != 3 || y != 0){
			System.out.println("Error: The movePieceMethod did not correctly move a Dragon.");
		}
		if (tempS.board[x][y].getEnum() != PieceEnum.DRAGON){
			System.out.println("Error: The movePieceMethod did not correctly move a Dragon.");
		}
		// Move a Dragon forward and right.
		tPosition = new Position(2, 1);
		tempS.movePiece(tempS.dragonPositions[0], tPosition);
		x = tempS.dragonPositions[0].getX();
		y = tempS.dragonPositions[0].getY();
		if (x != 2 || y != 1){
			System.out.println("Error: The movePieceMethod did not correctly move a Dragon.");
		}
		if (tempS.board[x][y].getEnum() != PieceEnum.DRAGON){
			System.out.println("Error: The movePieceMethod did not correctly move a Dragon.");
		}
		// Move a Dragon forward and left.
		tPosition = new Position(1, 0);
		tempS.movePiece(tempS.dragonPositions[0], tPosition);
		x = tempS.dragonPositions[0].getX();
		y = tempS.dragonPositions[0].getY();
		if (x != 1 || y != 0){
			System.out.println("Error: The movePieceMethod did not correctly move a Dragon.");
		}
		if (tempS.board[x][y].getEnum() != PieceEnum.DRAGON){
			System.out.println("Error: The movePieceMethod did not correctly move a Dragon.");
		}
		// Move a Dragon backwards and right.
		tPosition = new Position(2, 1);
		tempS.movePiece(tempS.dragonPositions[0], tPosition);
		x = tempS.dragonPositions[0].getX();
		y = tempS.dragonPositions[0].getY();
		if (x != 2 || y != 1){
			System.out.println("Error: The movePieceMethod did not correctly move a Dragon.");
		}
		if (tempS.board[x][y].getEnum() != PieceEnum.DRAGON){
			System.out.println("Error: The movePieceMethod did not correctly move a Dragon.");
		}
		// Move a Dragon backwards and left.
		tPosition = new Position(3, 0);
		tempS.movePiece(tempS.dragonPositions[0], tPosition);
		x = tempS.dragonPositions[0].getX();
		y = tempS.dragonPositions[0].getY();
		if (x != 3 || y != 0){
			System.out.println("Error: The movePieceMethod did not correctly move a Dragon.");
		}
		if (tempS.board[x][y].getEnum() != PieceEnum.DRAGON){
			System.out.println("Error: The movePieceMethod did not correctly move a Dragon.");
		}

		// Try moving the King to a position already occupied.
		tempS = state.clone();
		tPosition = new Position(1, 2);
		tempS.movePiece(tempS.kingPosition, tPosition);
		x = tempS.kingPosition.getX();
		y = tempS.kingPosition.getY();
		if (x == 1 || y != 2){
			System.out.println("Error: The movePieceMethod did not correctly move the King.");
		}
		if (tempS.board[1][2].getEnum() == PieceEnum.KING){
			System.out.println("Error: The movePieceMethod did not correctly move the King.");
		}
		// Try moving a Guard to a position already occupied.
		tempS = state.clone();
		tPosition = new Position(0, 2);
		tempS.movePiece(tempS.guardPositions[1], tPosition);
		x = tempS.guardPositions[1].getX();
		y = tempS.guardPositions[1].getY();
		if (x == 0 || y != 2){
			System.out.println("Error: The movePieceMethod did not correctly move a Guard.");
		}
		if (tempS.board[0][2].getEnum() == PieceEnum.GUARD){
			System.out.println("Error: The movePieceMethod did not correctly move a Guard.");
		}
		// Try moving a Dragon to a position already occupied.
		tempS = state.clone();
		tempS.board[2][0] = tempS.guardPositions[0];
		tPosition = new Position(2, 0);
		tempS.movePiece(tempS.dragonPositions[0], tPosition);
		x = tempS.dragonPositions[0].getX();
		y = tempS.dragonPositions[0].getY();
		if (x == 2 || y != 0){
			System.out.println("Error: The movePieceMethod did not correctly move a Dragon.");
		}
		if (tempS.board[2][0].getEnum() == PieceEnum.DRAGON){
			System.out.println("Error: The movePieceMethod did not correctly move a Dragon.");
		}

		// Try moving a King two spaces horizontally without a Guard along the path.
		tempS = state.clone();
		tPosition = new Position(0, 4);
		tempS.movePiece(tempS.kingPosition, tPosition);
		x = tempS.kingPosition.getX();
		y = tempS.kingPosition.getY();
		if (x != 0 || y == 4){
			System.out.println("Error: The movePieceMethod did not correctly move the King.");
		}
		if (tempS.board[0][4].getEnum() == PieceEnum.KING){
			System.out.println("Error: The movePieceMethod did not correctly move the King.");
		}
		// Try moving a King two spaces vertically without a Guard along the path.
		tempS = state.clone();
		tempS.board[1][2] = new Position(1, 2);
		tPosition = new Position(2, 2);
		tempS.movePiece(tempS.kingPosition, tPosition);
		x = tempS.kingPosition.getX();
		y = tempS.kingPosition.getY();
		if (x == 1 || y != 2){
			System.out.println("Error: The movePieceMethod did not correctly move the King.");
		}
		if (tempS.board[1][2].getEnum() == PieceEnum.KING){
			System.out.println("Error: The movePieceMethod did not correctly move the King.");
		}
		// Try moving a King diagonally.
		tempS = state.clone();
		tempS.board[1][1] = new Position(1, 1);
		tPosition = new Position(1, 1);
		tempS.movePiece(tempS.kingPosition, tPosition);
		x = tempS.kingPosition.getX();
		y = tempS.kingPosition.getY();
		if (x == 1 || y == 1){
			System.out.println("Error: The movePieceMethod did not correctly move the King.");
		}
		if (tempS.board[1][1].getEnum() == PieceEnum.KING){
			System.out.println("Error: The movePieceMethod did not correctly move the King.");
		}
		// Try moving the King three spaces with a Guard.
		tempS = state.clone();
		tempS.board[4][2] = new Position(4, 2);
		tPosition = new Position(4, 2);
		tempS.movePiece(tempS.kingPosition, tPosition);
		x = tempS.kingPosition.getX();
		y = tempS.kingPosition.getY();
		if (x == 4 || y != 2){
			System.out.println("Error: The movePieceMethod did not correctly move the King.");
		}
		if (tempS.board[4][2].getEnum() == PieceEnum.KING){
			System.out.println("Error: The movePieceMethod did not correctly move the King.");
		}
		// Try moving the King off the board.
		tempS = state.clone();
		tPosition = new Position(0, 5);
		tempS.kingPosition = new Position(0, 4, "K0");
		tempS.board[0][4] = tempS.kingPosition;
		tempS.movePiece(tempS.kingPosition, tPosition);
		x = tempS.kingPosition.getX();
		y = tempS.kingPosition.getY();
		if (x != 0 || y == 5){
			System.out.println("Error: The movePieceMethod did not correctly move the King.");
		}
		if (tempS.board[0][4].getEnum() != PieceEnum.KING){
			System.out.println("Error: The movePieceMethod did not correctly move the King.");
		}
		// Try moving the King off the board.
		tempS = state.clone();
		tPosition = new Position(-1, 4);
		tempS.kingPosition = new Position(0, 4, "K0");
		tempS.board[0][4] = tempS.kingPosition;
		tempS.movePiece(tempS.kingPosition, tPosition);
		x = tempS.kingPosition.getX();
		y = tempS.kingPosition.getY();
		if (x == -1 || y!= 4){
			System.out.println("Error: The movePieceMethod did not correctly move the King.");
		}
		if (tempS.board[0][4].getEnum() != PieceEnum.KING){
			System.out.println("Error: The movePieceMethod did not correctly move the King.");
		}

		// Try moving a Guard more than one space vertically.
		tempS = state.clone();
		tempS.board[3][3] = new Position(3, 3);
		tPosition = new Position(3, 3);
		tempS.movePiece(tempS.guardPositions[2], tPosition);
		x = tempS.guardPositions[2].getX();
		y = tempS.guardPositions[2].getY();
		if (x == 3 || y != 3){
			System.out.println("Error: The movePieceMethod did not correctly move a Guard.");
		}
		if (tempS.board[3][3].getEnum() == PieceEnum.GUARD){
			System.out.println("Error: The movePieceMethod did not correctly move a Guard.");
		}
		// Try moving a Guard more than one space horizontally.
		tempS = state.clone();
		tempS.board[1][2] = new Position(1, 2);
		tempS.board[1][1] = new Position(1, 1);
		tPosition = new Position(1, 1);
		tempS.movePiece(tempS.guardPositions[2], tPosition);
		x = tempS.guardPositions[2].getX();
		y = tempS.guardPositions[2].getY();
		if ( x != 1 || y == 1){
			System.out.println("Error: The movePieceMethod did not correctly move a Guard.");
		}
		if (tempS.board[1][1].getEnum() == PieceEnum.GUARD){
			System.out.println("Error: The movePieceMethod did not correctly move a Guard.");
		}
		// Try moving a Guard diagonally.
		tempS = state.clone();
		tPosition = new Position(2, 4);
		tempS.movePiece(tempS.guardPositions[2], tPosition);
		x = tempS.guardPositions[2].getX();
		y = tempS.guardPositions[2].getY();
		if ( x != 1 || y != 3){
			System.out.println("Error: The movePieceMethod did not correctly move a Guard.");
		}
		if (tempS.board[2][4].getEnum() == PieceEnum.GUARD){
			System.out.println("Error: The movePieceMethod did not correctly move a Guard.");
		}
		// Try moving a Guard off the board.
		tempS = state.clone();
		tempS.guardPositions[0].setPosition(new Position(4, 0));
		tempS.board[4][0] = tempS.guardPositions[0];
		tPosition = new Position(5, 0);
		tempS.movePiece(tempS.guardPositions[0], tPosition);
		x = tempS.guardPositions[0].getX();
		y = tempS.guardPositions[0].getY();
		if (x == 5 || y != 0){
			System.out.println("Error: The movePieceMethod did not correctly move a Guard.");
		}
		if (tempS.board[4][0].getEnum() != PieceEnum.GUARD){
			System.out.println("Error: The movePieceMethod did not correctly move a Guard.");
		}

		// Try moving a Dragon more than one space vertically.
		tempS = state.clone();
		tPosition = new Position(1, 2);
		tempS.board[1][2] = tPosition;
		tempS.movePiece(tempS.dragonPositions[2], tPosition);
		x = tempS.dragonPositions[2].getX();
		y = tempS.dragonPositions[2].getY();
		if (x == 1 || y != 2){
			System.out.println("Error: The movePieceMethod did not correctly move a Dragon.");
		}
		if (tempS.board[1][2].getEnum() == PieceEnum.DRAGON){
			System.out.println("Error: The movePieceMethod did not correctly move a Dragon.");
		}
		// Try moving a Dragon more than one space horizontally.
		tempS = state.clone();
		tempS.board[3][1] = new Position(3, 1);
		tempS.board[3][2] = new Position(3, 2);
		tPosition = new Position(3, 2);
		tempS.movePiece(tempS.dragonPositions[0], tPosition);
		x = tempS.dragonPositions[0].getX();
		y = tempS.dragonPositions[0].getY();
		if (x != 3 || y == 2){
			System.out.println("Error: The movePieceMethod did not correctly move a Dragon.");
		}
		if (tempS.board[3][2].getEnum() == PieceEnum.DRAGON){
			System.out.println("Error: The movePieceMethod did not correctly move a Dragon.");
		}
		// Try moving a Dragon more than one space Diagonally.
		tempS = state.clone();
		tPosition = new Position(1, 4);
		tempS.movePiece(tempS.dragonPositions[2], tPosition);
		x = tempS.dragonPositions[2].getX();
		y = tempS.dragonPositions[2].getY();
		if (x == 1 || y == 4){
			System.out.println("Error: The movePieceMethod did not correctly move a Dragon.");
		}
		if (tempS.board[1][4].getEnum() == PieceEnum.DRAGON){
			System.out.println("Error: The movePieceMethod did not correctly move a Dragon.");
		}
		// Try moving a Dragon off the board.
		tempS = state.clone();
		tPosition = new Position(3, -1);
		tempS.movePiece(tempS.dragonPositions[0], tPosition);
		x = tempS.dragonPositions[0].getX();
		y = tempS.dragonPositions[0].getY();
		if (x != 3 || y == -1){
			System.out.println("Error: The movePieceMethod did not correctly move a Dragon.");
		}
		if (tempS.board[3][0].getEnum() != PieceEnum.DRAGON){
			System.out.println("Error: The movePieceMethod did not correctly move a Dragon.");
		}

		// Test that a Guard is captured when surrounded. Configuration 1.
		tempS = state.clone();
		tPosition = tempS.guardPositions[2];
		tempS.board[0][3] = tempS.dragonPositions[0];
		tempS.board[1][4] = tempS.dragonPositions[1];
		tempS.movePiece(tempS.dragonPositions[3], new Position(2, 3));
		x = tempS.dragonPositions[5].getX();
		y = tempS.dragonPositions[5].getY();
		if (tempS.guardPositions[2] != null || x != 1 || y != 3){
			System.out.println("Error: The movePieceMethod did not correctly move a Dragon.");
		}
		if (tempS.board[1][3].getEnum() == PieceEnum.GUARD || tempS.board[1][3].getEnum() != PieceEnum.DRAGON){
			System.out.println("Error: The movePieceMethod did not correctly move a Dragon.");
		}
		// Test that a Guard is captured when surrounded. Configuration 2.
		tempS = state.clone();
		tPosition = tempS.guardPositions[2];
		tempS.board[1][2] = tempS.dragonPositions[0];
		tempS.board[1][4] = tempS.dragonPositions[1];
		tempS.movePiece(tempS.dragonPositions[3], new Position(2, 3));
		x = tempS.dragonPositions[5].getX();
		y = tempS.dragonPositions[5].getY();
		if (tempS.guardPositions[2] != null || x != 1 || y != 3){
			System.out.println("Error: The movePieceMethod did not correctly move a Dragon.");
		}
		if (tempS.board[1][3].getEnum() == PieceEnum.GUARD || tempS.board[1][3].getEnum() != PieceEnum.DRAGON){
			System.out.println("Error: The movePieceMethod did not correctly move a Dragon.");
		}
		// Test that a Guard is captured when surrounded. Configuration 3.
		tempS = state.clone();
		tPosition = tempS.guardPositions[2];
		tempS.board[1][2] = tempS.dragonPositions[0];
		tempS.board[0][3] = tempS.dragonPositions[1];
		tempS.movePiece(tempS.dragonPositions[3], new Position(2, 3));
		x = tempS.dragonPositions[5].getX();
		y = tempS.dragonPositions[5].getY();
		if (tempS.guardPositions[2] != null || x != 1 || y != 3){
			System.out.println("Error: The movePieceMethod did not correctly move a Dragon.");
		}
		if (tempS.board[1][3].getEnum() == PieceEnum.GUARD || tempS.board[1][3].getEnum() != PieceEnum.DRAGON){
			System.out.println("Error: The movePieceMethod did not correctly move a Dragon.");
		}
		// Test that a Guard is captured when surrounded. Configuration 4.
		tempS = state.clone();
		tPosition = tempS.guardPositions[2];
		tempS.board[1][2] = tempS.dragonPositions[0];
		tempS.board[0][3] = tempS.dragonPositions[1];
		tempS.movePiece(tempS.dragonPositions[3], new Position(2, 4));
		tempS.movePiece(tempS.dragonPositions[3], new Position(1, 4));
		x = tempS.dragonPositions[5].getX();
		y = tempS.dragonPositions[5].getY();
		if (tempS.guardPositions[2] != null || x != 1 || y != 3){
			System.out.println("Error: The movePieceMethod did not correctly move a Dragon.");
		}
		if (tempS.board[1][3].getEnum() == PieceEnum.GUARD || tempS.board[1][3].getEnum() != PieceEnum.DRAGON){
			System.out.println("Error: The movePieceMethod did not correctly move a Dragon.");
		}

		// Test that a Dragon is captured when surrounded. Configuration 1.
		tempS = state.clone();
		tempS.movePiece(tempS.dragonPositions[3], new Position(2, 3));
		tempS.board[2][2] = tempS.guardPositions[0];
		tempS.movePiece(tempS.guardPositions[2], new Position(2, 3));
		if (tempS.dragonPositions[3] != null){
			System.out.println("Error: The movePieceMethod did not correctly move a Guard.");
		}
		if (tempS.board[2][3].getEnum() == PieceEnum.DRAGON || tempS.board[2][3].getEnum() != PieceEnum.GUARD){
			System.out.println("Error: The movePieceMethod did not correctly move a Guard.");
		}
		// Test that a Dragon is captured when surrounded. Configuration 2.
		tempS = state.clone();
		tempS.movePiece(tempS.dragonPositions[3], new Position(2, 3));
		tempS.board[3][3] = tempS.guardPositions[0];
		tempS.movePiece(tempS.guardPositions[2], new Position(2, 3));
		if (tempS.dragonPositions[3] != null){
			System.out.println("Error: The movePieceMethod did not correctly move a Guard.");
		}
		if (tempS.board[2][3].getEnum() == PieceEnum.DRAGON || tempS.board[2][3].getEnum() != PieceEnum.GUARD){
			System.out.println("Error: The movePieceMethod did not correctly move a Guard.");
		}
		// Test that a Dragon is captured when surrounded Configuration 3.
		tempS = state.clone();
		tempS.movePiece(tempS.dragonPositions[3], new Position(2, 3));
		tempS.board[2][4] = tempS.guardPositions[0];
		tempS.movePiece(tempS.guardPositions[2], new Position(2, 3));
		if (tempS.dragonPositions[3] != null){
			System.out.println("Error: The movePieceMethod did not correctly move a Guard.");
		}
		if (tempS.board[2][3].getEnum() == PieceEnum.DRAGON || tempS.board[2][3].getEnum() != PieceEnum.GUARD){
			System.out.println("Error: The movePieceMethod did not correctly move a Guard.");
		}
		// Test that a Dragon is captured when surrounded. Configuration 4.
		tempS = state.clone();
		tempS.movePiece(tempS.dragonPositions[3], new Position(2, 3));
		tempS.board[2][2] = tempS.guardPositions[0];
		tempS.movePiece(tempS.guardPositions[2], new Position(1, 4));
		tempS.movePiece(tempS.guardPositions[2], new Position(2, 4));
		tempS.movePiece(tempS.guardPositions[2], new Position(2, 3));
		if (tempS.dragonPositions[3] != null){
			System.out.println("Error: The movePieceMethod did not correctly move a Guard.");
		}
		if (tempS.board[2][3].getEnum() == PieceEnum.DRAGON || tempS.board[2][3].getEnum() != PieceEnum.GUARD){
			System.out.println("Error: The movePieceMethod did not correctly move a Guard.");
		}
		// Test that a Dragon can be captured by the King.
		tempS = state.clone();
		tempS.board[2][1] = tempS.guardPositions[0];
		tempS.movePiece(tempS.dragonPositions[1], new Position(2, 2));
		tempS.movePiece(tempS.kingPosition, new Position(2, 2));
		if (tempS.dragonPositions[1] != null){
			System.out.println("Error: The movePieceMethod did not correctly move a Guard.");
		}
		if (tempS.board[2][2].getEnum() == PieceEnum.DRAGON || tempS.board[2][2].getEnum() != PieceEnum.KING){
			System.out.println("Error: The movePieceMethod did not correctly move a Guard.");
		}
		
		// Test the isKingInDanger method.
		tempS = state.clone();
		tempS.board[0][1] = tempS.dragonPositions[0];
		tempS.board[0][3] = tempS.dragonPositions[1];
		tempS.board[2][2] = tempS.dragonPositions[2];
		tempS.board[1][2] = new Position(1, 2);
		if (!tempS.isKingInDanger()){
			System.out.println("Error: The isKingInDanger method does not correctly determine the King to be in danger.");
		}
		// Test the isKingInDanger method.
		tempS = state.clone();
		tempS.board[0][0] = tempS.dragonPositions[0];
		tempS.board[0][3] = tempS.dragonPositions[1];
		tempS.board[1][2] = tempS.dragonPositions[2];
		if (!tempS.isKingInDanger()){
			System.out.println("Error: The isKingInDanger method does not correctly determine the King to be in danger.");
		}
		// Test the isKingInDanger method.
		tempS = state.clone();
		tempS.board[0][1] = tempS.dragonPositions[0];
		tempS.board[0][4] = tempS.dragonPositions[1];
		tempS.board[1][2] = tempS.dragonPositions[2];
		if (!tempS.isKingInDanger()){
			System.out.println("Error: The isKingInDanger method does not correctly determine the King to be in danger.");
		}
		// Test the isKingInDanger method.
		tempS = state.clone();
		tempS.movePiece(tempS.kingPosition, new Position(2, 2));
		tempS.board[2][1] = tempS.dragonPositions[0];
		if (!tempS.isKingInDanger()){
			System.out.println("Error: The isKingInDanger method does not correctly determine the King to be in danger.");
		}
		// Test the isKingInDanger method.
		tempS = state.clone();
		tempS.movePiece(tempS.kingPosition, new Position(2, 2));
		tempS.board[2][3] = tempS.dragonPositions[0];
		if (!tempS.isKingInDanger()){
			System.out.println("Error: The isKingInDanger method does not correctly determine the King to be in danger.");
		}
		// Test the isKingInDanger method.
		tempS = state.clone();
		tempS.board[0][1] = tempS.dragonPositions[0];
		tempS.board[0][3] = tempS.dragonPositions[1];
		tempS.board[2][3] = tempS.dragonPositions[2];
		tempS.board[1][2] = new Position(1, 2);
		if (!tempS.isKingInDanger()){
			System.out.println("Error: The isKingInDanger method does not correctly determine the King to be in danger.");
		}
		// Test the isKingInDanger method.
		tempS = state.clone();
		tempS.board[0][1] = tempS.dragonPositions[0];
		tempS.board[0][3] = tempS.dragonPositions[1];
		tempS.board[2][1] = tempS.dragonPositions[2];
		tempS.board[1][2] = new Position(1, 2);
		if (!tempS.isKingInDanger()){
			System.out.println("Error: The isKingInDanger method does not correctly determine the King to be in danger.");
		}
		
		// Test the numGuardsInDanger method.
		tempS = state.clone();
		tempS.board[1][4] = tempS.dragonPositions[0];
		tempS.board[0][3] = tempS.dragonPositions[1];
		if (tempS.numGuardsInDanger() != 1){
			System.out.println("Error: The numGuardsInDanger method does not correctly determine the number of Guards to be in danger.");
		}
		// Test the numGuardsInDanger method.
		tempS = state.clone();
		tempS.board[1][4] = tempS.dragonPositions[0];
		tempS.board[0][3] = tempS.dragonPositions[0];
		tempS.board[1][0] = tempS.dragonPositions[4];
		tempS.board[0][1] = tempS.dragonPositions[4];
		if (tempS.numGuardsInDanger() != 2){
			System.out.println("Error: The numGuardsInDanger method does not correctly determine the number of Guards to be in danger.");
		}
		// Test the numGuardsInDanger method.
		tempS = state.clone();
		tempS.board[1][4] = tempS.dragonPositions[0];
		tempS.board[0][3] = tempS.dragonPositions[0];
		tempS.board[1][0] = tempS.dragonPositions[4];
		tempS.board[0][1] = tempS.dragonPositions[4];
		tempS.board[0][2] = tempS.dragonPositions[1];
		if (tempS.numGuardsInDanger() != 2){
			System.out.println("Error: The numGuardsInDanger method does not correctly determine the number of Guards to be in danger.");
		}
		
		// Test the numDragonsInDanger method.
		tempS = state.clone();
		tempS.board[4][2] = tempS.guardPositions[0];
		if (tempS.numDragonsInDanger() != 1){
			System.out.println("Error: The numDragonsInDanger method does not correctly determine the number of Dragons to be in danger.");
		}
		// Test the numDragonsInDanger method.
		tempS = state.clone();
		tempS.board[4][2] = tempS.guardPositions[0];
		tempS.board[4][3] = tempS.guardPositions[1];
		if (tempS.numDragonsInDanger() != 2){
			System.out.println("Error: The numDragonsInDanger method does not correctly determine the number of Dragons to be in danger.");
		}
		// Test the numDragonsInDanger method.
		tempS = state.clone();
		tempS.board[4][2] = tempS.guardPositions[0];
		tempS.board[4][3] = tempS.guardPositions[1];
		tempS.board[4][1] = tempS.guardPositions[2];
		if (tempS.numDragonsInDanger() != 3){
			System.out.println("Error: The numDragonsInDanger method does not correctly determine the number of Dragons to be in danger.");
		}
		// Test the numDragonsInDanger method.
		tempS = state.clone();
		tempS.board[2][3] = tempS.guardPositions[0];
		tempS.board[4][4] = tempS.guardPositions[1];
		if (tempS.numDragonsInDanger() != 2){
			System.out.println("Error: The numDragonsInDanger method does not correctly determine the number of Dragons to be in danger.");
		}
		
		// Test the isKingObstructedMethod.
		tempS = state.clone();
		tempS.clearBoard();
		tempS.kingPosition.setPosition(new Position(3, 2));
		tempS.board[3][2] = tempS.kingPosition;
		tempS.board[4][0] = tempS.dragonPositions[0];
		if (tempS.isKingObstructed(tempS.kingPosition.getX(), tempS.kingPosition.getY())){
			System.out.println("Error: The isKingObstructed method does not correctly determine if the King's path is obstructed.");
		}
		// Test the isKingObstructedMethod.
		tempS = state.clone();
		tempS.clearBoard();
		tempS.kingPosition.setPosition(new Position(3, 2));
		tempS.board[3][2] = tempS.kingPosition;
		tempS.board[4][1] = tempS.dragonPositions[0];
		if (!tempS.isKingObstructed(tempS.kingPosition.getX(), tempS.kingPosition.getY())){
			System.out.println("Error: The isKingObstructed method does not correctly determine if the King's path is obstructed.");
		}
		// Test the isKingObstructedMethod.
		tempS = state.clone();
		tempS.clearBoard();
		tempS.kingPosition.setPosition(new Position(3, 2));
		tempS.board[3][2] = tempS.kingPosition;
		tempS.board[3][1] = tempS.dragonPositions[0];
		if (!tempS.isKingObstructed(tempS.kingPosition.getX(), tempS.kingPosition.getY())){
			System.out.println("Error: The isKingObstructed method does not correctly determine if the King's path is obstructed.");
		}
		// Test the isKingObstructedMethod.
		tempS = state.clone();
		tempS.clearBoard();
		tempS.kingPosition.setPosition(new Position(3, 2));
		tempS.board[3][2] = tempS.kingPosition;
		tempS.board[3][3] = tempS.dragonPositions[0];
		if (!tempS.isKingObstructed(tempS.kingPosition.getX(), tempS.kingPosition.getY())){
			System.out.println("Error: The isKingObstructed method does not correctly determine if the King's path is obstructed.");
		}
		// Test the isKingObstructedMethod.
		tempS = state.clone();
		tempS.clearBoard();
		tempS.kingPosition.setPosition(new Position(3, 2));
		tempS.board[3][2] = tempS.kingPosition;
		tempS.board[4][2] = tempS.dragonPositions[0];
		if (!tempS.isKingObstructed(tempS.kingPosition.getX(), tempS.kingPosition.getY())){
			System.out.println("Error: The isKingObstructed method does not correctly determine if the King's path is obstructed.");
		}
		// Test the isKingObstructedMethod.
		tempS = state.clone();
		tempS.clearBoard();
		tempS.kingPosition.setPosition(new Position(2, 2));
		tempS.board[2][2] = tempS.kingPosition;
		tempS.board[2][3] = tempS.dragonPositions[0];
		if (!tempS.isKingObstructed(tempS.kingPosition.getX(), tempS.kingPosition.getY())){
			System.out.println("Error: The isKingObstructed method does not correctly determine if the King's path is obstructed.");
		}
		// Test the isKingObstructedMethod.
		tempS = state.clone();
		tempS.clearBoard();
		tempS.kingPosition.setPosition(new Position(2, 2));
		tempS.board[2][2] = tempS.kingPosition;
		tempS.board[2][0] = tempS.dragonPositions[0];
		if (!tempS.isKingObstructed(tempS.kingPosition.getX(), tempS.kingPosition.getY())){
			System.out.println("Error: The isKingObstructed method does not correctly determine if the King's path is obstructed.");
		}
		// Test the isKingObstructedMethod.
		tempS = state.clone();
		tempS.clearBoard();
		tempS.kingPosition.setPosition(new Position(2, 2));
		tempS.board[2][2] = tempS.kingPosition;
		tempS.board[2][4] = tempS.dragonPositions[0];
		if (!tempS.isKingObstructed(tempS.kingPosition.getX(), tempS.kingPosition.getY())){
			System.out.println("Error: The isKingObstructed method does not correctly determine if the King's path is obstructed.");
		}
		// Test the isKingObstructedMethod.
		tempS = state.clone();
		tempS.clearBoard();
		tempS.kingPosition.setPosition(new Position(2, 2));
		tempS.board[2][2] = tempS.kingPosition;
		tempS.board[3][0] = tempS.dragonPositions[0];
		if (!tempS.isKingObstructed(tempS.kingPosition.getX(), tempS.kingPosition.getY())){
			System.out.println("Error: The isKingObstructed method does not correctly determine if the King's path is obstructed.");
		}
		// Test the isKingObstructedMethod.
		tempS = state.clone();
		tempS.clearBoard();
		tempS.kingPosition.setPosition(new Position(2, 2));
		tempS.board[2][2] = tempS.kingPosition;
		tempS.board[3][4] = tempS.dragonPositions[0];
		if (!tempS.isKingObstructed(tempS.kingPosition.getX(), tempS.kingPosition.getY())){
			System.out.println("Error: The isKingObstructed method does not correctly determine if the King's path is obstructed.");
		}
		// Test the isKingObstructedMethod.
		tempS = state.clone();
		tempS.clearBoard();
		tempS.kingPosition.setPosition(new Position(2, 2));
		tempS.board[2][2] = tempS.kingPosition;
		tempS.board[4][0] = tempS.dragonPositions[0];
		if (!tempS.isKingObstructed(tempS.kingPosition.getX(), tempS.kingPosition.getY())){
			System.out.println("Error: The isKingObstructed method does not correctly determine if the King's path is obstructed.");
		}
		// Test the isKingObstructedMethod.
		tempS = state.clone();
		tempS.clearBoard();
		tempS.kingPosition.setPosition(new Position(2, 2));
		tempS.board[2][2] = tempS.kingPosition;
		tempS.board[4][4] = tempS.dragonPositions[0];
		if (!tempS.isKingObstructed(tempS.kingPosition.getX(), tempS.kingPosition.getY())){
			System.out.println("Error: The isKingObstructed method does not correctly determine if the King's path is obstructed.");
		}
		// Test the isKingObstructedMethod.
		tempS = state.clone();
		tempS.clearBoard();
		tempS.kingPosition.setPosition(new Position(2, 2));
		tempS.board[2][2] = tempS.kingPosition;
		tempS.board[4][2] = tempS.dragonPositions[0];
		if (!tempS.isKingObstructed(tempS.kingPosition.getX(), tempS.kingPosition.getY())){
			System.out.println("Error: The isKingObstructed method does not correctly determine if the King's path is obstructed.");
		}
		
		// Test that the game is over when the King reaches the bottom.
		tempS = state.clone();
		tempS.kingPosition = new Position(4, 0, "K");
		if (!tempS.isGameOver()){
			System.out.println("Error: The isGameOver method does not correctly determine the end of the game.");
		}
		// Test the game is over when the King is captured.
		tempS = state.clone();
		tempS.board[0][1] = tempS.dragonPositions[0];
		tempS.board[0][3] = tempS.dragonPositions[1];
		tempS.board[1][2] = tempS.dragonPositions[2];
		tempS.board[1][1] = new Position(1, 1);
		tempS.board[1][3] = new Position(1, 3);
		if (!tempS.isGameOver()){
			System.out.println("Error: The isGameOver method does not correctly determine the end of the game.");
		}
		// Test the game is over when the King is captured.
		tempS = state.clone();
		tempS.board[1][1] = tempS.dragonPositions[0];
		tempS.board[1][3] = tempS.dragonPositions[1];
		tempS.board[2][2] = tempS.dragonPositions[2];
		tempS.kingPosition.setPosition(new Position(1, 2));
		tempS.board[1][2] = tempS.kingPosition;
		tempS.board[0][2] = tempS.guardPositions[0];
		if (!tempS.isGameOver()){
			System.out.println("Error: The isGameOver method does not correctly determine the end of the game.");
		}
		// Test the game is over when the King is captured.
		tempS = state.clone();
		tempS.board[1][1] = tempS.dragonPositions[0];
		tempS.board[0][2] = tempS.dragonPositions[1];
		tempS.board[2][2] = tempS.dragonPositions[2];
		tempS.kingPosition.setPosition(new Position(1, 2));
		tempS.board[1][2] = tempS.kingPosition;
		tempS.board[1][3] = tempS.guardPositions[0];
		if (!tempS.isGameOver()){
			System.out.println("Error: The isGameOver method does not correctly determine the end of the game.");
		}
		// Test the game is not over when the King can escape.
		tempS = state.clone();
		tempS.board[1][1] = tempS.dragonPositions[0];
		tempS.board[2][2] = tempS.dragonPositions[1];
		tempS.board[0][2] = tempS.dragonPositions[2];
		tempS.kingPosition = new Position(1, 2, "K");
		tempS.board[1][2] = tempS.kingPosition;
		tempS.board[1][3] = new Position(1, 3);
		if (tempS.isGameOver()){
			System.out.println("Error: The isGameOver method does not correctly determine the end of the game.");
		}
		// Test the game is not over when the King can escape.
		tempS = state.clone();
		tempS.board[1][1] = tempS.dragonPositions[0];
		tempS.board[0][2] = tempS.dragonPositions[1];
		tempS.board[1][3] = tempS.dragonPositions[2];
		tempS.kingPosition.setPosition(new Position(1, 2));
		tempS.board[1][2] = tempS.kingPosition;
		if (tempS.isGameOver()){
			System.out.println("Error: The isGameOver method does not correctly determine the end of the game.");
		}
		// Test the game is over when the King is captured.
		tempS = state.clone();
		tempS.board[1][1] = tempS.dragonPositions[0];
		tempS.board[0][2] = tempS.dragonPositions[1];
		tempS.board[1][3] = tempS.dragonPositions[2];
		tempS.kingPosition.setPosition(new Position(1, 2));
		tempS.board[1][2] = tempS.kingPosition;
		tempS.board[2][2] = tempS.guardPositions[0];
		if (!tempS.isGameOver()){
			System.out.println("Error: The isGameOver method does not correctly determine the end of the game.");
		}
		// Test the game is over when the King is captured.
		tempS = state.clone();
		tempS.board[1][1] = tempS.dragonPositions[0];
		tempS.board[0][2] = tempS.dragonPositions[1];
		tempS.board[1][3] = tempS.dragonPositions[2];
		tempS.kingPosition.setPosition(new Position(1, 2));
		tempS.board[1][2] = tempS.kingPosition;
		tempS.board[2][2] = tempS.dragonPositions[3];
		tempS.board[3][2] = new Position(3, 2);
		if (!tempS.isGameOver()){
			System.out.println("Error: The isGameOver method does not correctly determine the end of the game.");
		}
		// Test the game is over when the King is captured.
		tempS = state.clone();
		tempS.board[1][0] = tempS.dragonPositions[0];
		tempS.board[3][0] = tempS.dragonPositions[1];
		tempS.board[2][1] = tempS.dragonPositions[2];
		tempS.kingPosition.setPosition(new Position(2, 0));
		tempS.board[2][0] = tempS.kingPosition;
		tempS.guardPositions[0].setPosition(new Position(2, 3));
		tempS.board[2][3] = tempS.guardPositions[0];
		tempS.board[1][1] = new Position(1, 1);
		if (!tempS.isGameOver()){
			System.out.println("Error: The isGameOver method does not correctly determine the end of the game.");
		}
		// Test the game is not over when the King is surrounded, but can move.
		tempS = state.clone();
		tempS.board[2][3] = tempS.dragonPositions[0];
		tempS.board[3][2] = tempS.dragonPositions[2];
		tempS.board[4][3] = tempS.dragonPositions[4];
		tempS.board[3][4] = tempS.guardPositions[2];
		tempS.kingPosition.setPosition(new Position(3, 3));
		tempS.board[3][3] = tempS.kingPosition;
		if (!tempS.isGameOver()){
			System.out.println("Error: The isGameOver method does not correctly determine the end of the game.");
		}
		// Test the game is over when the same state has been seen three times. 
		tempS = state.clone();
		tPosition = new Position(0, 2);
		tempS.movePiece(tempS.kingPosition, new Position(0, 3));
		tempS.movePiece(tempS.kingPosition, tPosition);
		tempS.movePiece(tempS.kingPosition, new Position(0, 3));
		tempS.movePiece(tempS.kingPosition, tPosition);
		tempS.movePiece(tempS.kingPosition, new Position(0, 3));
		tempS.movePiece(tempS.kingPosition, tPosition);
		if (!tempS.isGameOver()){
			System.out.println("Error: The game is not over after the same state has appeared three times.");
		}

		// Test the switchPlayers method.
		tempS = state.clone();
		tempS.player = PlayerEnum.PLAYER1;
		tempS.switchPlayer();
		if (tempS.getPlayer() != PlayerEnum.PLAYER2){
			System.out.println("Error: The switchPlayer method did not correctly switch the player.");
		}
		tempS.switchPlayer();
		if (tempS.getPlayer() != PlayerEnum.PLAYER1){
			System.out.println("Error: The switchPlayer method did not correctly switch the player.");
		}
		
		// Test the game is over once fifty turns have been taken.
		tempS = state.clone();
		for (int n = 0; n < 50; n += 1){
			tempS.switchPlayer();
		}
		if (!tempS.isGameOver()){
			System.out.println("The game is not over after fifty turns have been taken.");
		}

		// Test the isPlayerTurn method.
		state.player = PlayerEnum.PLAYER1;
		if (!state.isPlayerTurn(PieceEnum.KING)){
			System.out.println("Error: The isPlayer incorrectly determined the player's turn.");
		}
		state.player = PlayerEnum.PLAYER1;
		if (!state.isPlayerTurn(PieceEnum.GUARD)){
			System.out.println("Error: The isPlayer incorrectly determined the player's turn.");
		}		
		state.player = PlayerEnum.PLAYER2;
		if (!state.isPlayerTurn(PieceEnum.DRAGON)){
			System.out.println("Error: The isPlayer incorrectly determined the player's turn.");
		}
		state.player = PlayerEnum.PLAYER1;
		if (state.isPlayerTurn(PieceEnum.DRAGON)){
			System.out.println("Error: The isPlayer incorrectly determined the player's turn.");
		}
		state.player = PlayerEnum.PLAYER1;
		if (state.isPlayerTurn(PieceEnum.BLANK)){
			System.out.println("Error: The isPlayer incorrectly determined the player's turn.");
		}
		state.player = PlayerEnum.PLAYER2;
		if (state.isPlayerTurn(PieceEnum.KING)){
			System.out.println("Error: The isPlayer incorrectly determined the player's turn.");
		}
		state.player = PlayerEnum.PLAYER2;
		if (state.isPlayerTurn(PieceEnum.GUARD)){
			System.out.println("Error: The isPlayer incorrectly determined the player's turn.");
		}
		state.player = PlayerEnum.PLAYER2;
		if (state.isPlayerTurn(PieceEnum.BLANK)){
			System.out.println("Error: The isPlayer incorrectly determined the player's turn.");
		}

		// Test the clone and equals methods.
		tempS = state.clone();
		if (!tempS.equals(state)){
			System.out.println("Error: The clone and equals methods are not correctly implemented.");
		}
		
		// Test the boardString method.
		System.out.println(tempS.boardString());

		// Test the toString method.
		state.printState();

		// Test the newStates method.
		ArrayList<State> newStates = state.newStates();
		GameState tempState = null;
		for (int n = 0; n < newStates.size(); n += 1){
			tempState = (GameState) newStates.get(n);
			if (!tempState.board[tempState.kingPosition.getX()][tempState.kingPosition.getY()].equals(tempS.kingPosition)){
				System.out.println("The King's board position was not modified correctly." + " " + tempState.kingPosition.toString());
			}
			for (Position tempP : tempState.guardPositions){
				if (tempState.board[tempP.getX()][tempP.getY()].getEnum() != PieceEnum.GUARD){
					System.out.println("Error: The Guards' board positions were not modified correctly." + " " + tempP.toString());
					break;
				}
			}
//			tempState.printState();
		}
		state.switchPlayer();
		newStates = state.newStates();
		for (int n = 0; n < newStates.size(); n += 1){
			tempState = (GameState) newStates.get(n);
			for (Position tempP : tempState.dragonPositions){
				if (tempP != null && tempState.board[tempP.getX()][tempP.getY()].getEnum() != PieceEnum.DRAGON){
					System.out.println("Error: The Dragons' board positions were not modified correctly." + " " + tempP.toString());
					break;
				}
			}
//			tempState.printState();
		}
	}
}