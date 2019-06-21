package assignment2;

import java.util.Scanner;

/**
 * Represents a match between two players.
 * @author Nickolas Gough and Sam Germain
 */
public class Match {

	// Store the state of the game.
	private GameState state;
	
	// Store the input scanner.
	private Scanner input;
	
	// Store the human player.
	private final PlayerEnum human = PlayerEnum.PLAYER1;
	
	// Allow for a two player match.
	private final int numPlayers = 1;
	
	/**
	 * Constructor for the match.
	 */
	public Match(){
		// Initialize the Game and input.
		this.state = new GameState();
		this.state.initializeGameStart();
		this.input = new Scanner(System.in);
	}
	
	/**
	 * Print the prompt for input.
	 */
	private void printPrompt(){
		// Determine whose turn it is.
		String prompt = clearPrompts(this.state.getPlayer()) + ": ";
		
		if (!this.isHumanTurn()){
			prompt = "Please wait...";
		}
		
		System.out.print(prompt);
	}
	
	/**
	 * Returns a prompt saying either human or comp, or player1 and player2 if there are 2 players or 2 computers
	 */
	private String clearPrompts(PlayerEnum player) {
		String prompt;
		if (numPlayers == 1) {
			if (player == human) {
				prompt = "Player";
			}else if (player == PlayerEnum.DRAW){
				prompt = "DRAW";
			}else {
				prompt = "Comp";
			}
		}else {
			prompt = player + "";
		}
		return prompt;
	}
	
	/**
	 * Print the error to the console.
	 * @param error - The error message to be printed.
	 */
	private void printError(String error){
		System.out.println(error);
	}
	
	/**
	 * Print the game board in its current state.
	 */
	private void printBoard(){
		this.state.printState();
	}
	
	/**
	 * Print a separator to separate plays.
	 */
	private void printSeparator(){
		System.out.println("-------------------");
	}
	
	/**
	 * Print the end game message.
	 */
	private void printEndGameMessage(){
		// Determine the winner.
		String winner = this.clearPrompts(this.state.winner());
		
		// Determine if the game is actually a draw.
		String result = "The game has ended! " + winner + " has won the match!";
		if (winner == "DRAW") {
			result = "The game has ended in a " + winner + "!";
		}
		
		// Print the end game message.
		System.out.println(result);
	}
	
	/**
	 * Determine if it is a human player's turn.
	 * @return - True if it is a human player's turn, false otherwise.
	 */
	private boolean isHumanTurn(){
		// Determine if it is the human's turn.
		if (this.state.getPlayer() == this.human && this.numPlayers == 1){
			return true;
		}
		if (this.numPlayers == 2){
			return true;
		}
		
		return false;
	}
	
	/**
	 * Retrieve the next line of input.
	 * @return - The String of input.
	 */
	private String getInput(){
		return this.input.nextLine();
	}
	
	/**
	 * Parse the character representing the piece.
	 * @param piece - The character representing the piece to move.
	 * @return - The piece enum of the piece to move.
	 * @throws RuntimeException when no valid piece has been specified.
	 */
	public PieceEnum parsePiece(char piece) throws RuntimeException{
		// Initialize the enumeration.
		PieceEnum tempE = null;
		
		// Determine which piece has been specified.
		if (piece == 'K'){
			tempE = PieceEnum.KING;
		}
		else if (piece == 'G'){
			tempE = PieceEnum.GUARD;
		}
		else if (piece == 'D'){
			tempE = PieceEnum.DRAGON;
		}
		else {
			throw new RuntimeException("Error: Piece to move has not been specified.");
		}
		
		return tempE;
	}
	
	/**
	 * Parse the input coordinate to a position.
	 * @param position - The string representation of the input position. 
	 * @return - The position as a position object.
	 */
	private Position parsePosition(String position){
		// Initialize the variables.
		int x = 0;
		int y = 0;
		
		// Parse the position.
		x = Integer.parseInt(position.substring(1, 2));
		y = Integer.parseInt(position.substring(3, 4));
		
		return new Position(x, y);
	}
	
	/**
	 * Make the play based on the input provided.
	 * @param input - The input from the user. 
	 * @throws RuntimeException - When the input is invalid or when the play is illegal.
	 */
	public void playInput(String input) throws RuntimeException{
		// Initialize the necessary variables.
		PieceEnum tempE = null;
		
		// Determine which piece is to be moved.
		tempE = this.parsePiece(input.charAt(0));
		
		// Check that it is the Player's turn.
		if (!state.isPlayerTurn(tempE)){
			throw new RuntimeException("That is an invalid move for " + state.getPlayer() + ". Please make a valid move.");
		}
		
		// Determine the initial position of the piece.
		Position target = this.parsePosition(input.substring(3, 8));
		
		// Determine the piece that is being moved.
		String piece = input.substring(0, 2);
		
		// Make the move.
		if (!state.movePiece(piece, target)){
			throw new RuntimeException("That move is illegal. Please make a legal move.");
		}
	}
	
	/**
	 * Play a match until the game is over.
	 */
	public void playMatch(){
		// Initialize the necessary variables.
		String input = null;
		boolean valid = false;

		// Continue playing until the match is over.
		while (!this.state.isGameOver()){
			// Print the board and prompt.
			System.out.print("\n");
			this.printBoard();
			this.printPrompt();
			
			// Either get input from the human player or allow the AI to move.
			if (this.isHumanTurn()){
				// Get input and try the move until a valid move is made.
				while (!valid){
					input = this.getInput();
					try {
						this.playInput(input);
						valid = true;
					}
					catch (Exception e){
						this.printError("Error: " + e.getMessage());
						this.printPrompt();
					}
				}
				
				valid = false;
			}
			else {
				// Allow the AI to make a move. 
//				this.state = (GameState) MiniMax.search(this.state);
				this.state = (GameState) AlphaBeta.search(this.state);
			}
			
			// Print separation and switch the player.
			this.printSeparator();
			this.state.switchPlayer();	
		}
		
		// Print the state of the board.
		System.out.print("\n");
		this.state.printState();
		
		// Print the end game message.
		this.printEndGameMessage();
	}
	
	/**
	 * Begin a match and play until the game is over.
	 * @param args - The arguments to the main function.
	 */
	public static void main(String[] args){
		// Begin the game.
		Match match = new Match();
		match.playMatch();
	}
}
