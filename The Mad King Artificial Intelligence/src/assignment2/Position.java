package assignment2;

/**
 * Represents a board position.
 * @author Nickolas Gough and Sam Germain
 */
public class Position implements Cloneable{

	// Store the x position.
	private int x;

	// Store the y position.
	private int y;
	
	// Store the name of the Position;
	private String name;
	
	// Store the index of the position.
	private int index;
	
	// Store the PieceEnum of the piece.
	private PieceEnum pieceEnum;
	
	/**
	 * The Position constructor.
	 * @param x - The x position.
	 * @param y - The y position.
	 */
	public Position(int x, int y){
		this.x = x;
		this.y = y;
		this.index = -1;
		this.name = ".";
		this.pieceEnum = PieceEnum.BLANK;
	}
	
	/**
	 * The Position constructor.
	 * @param x - The x position.
	 * @param y - The y position.
	 * @throws NumberFormatException when the name is not correctly formatted with the index.
	 * @throws RuntimeException when the name is not correctly formatted with the piece character.
	 */
	public Position(int x, int y, String name) throws NumberFormatException, RuntimeException{
		// Initialize the position.
		this.x = x;
		this.y = y;
		this.name = name;

		// Determine the position's enumeration.
		String temp = name.substring(0, 1);
		switch (temp){
			case "K": 
				this.pieceEnum = PieceEnum.KING;
				this.index = -1;
				break;
			case "G": 
				this.pieceEnum = PieceEnum.GUARD;
				this.index = Integer.parseInt(name.substring(1));
				break;
			case "D": 
				this.pieceEnum = PieceEnum.DRAGON;
				this.index = Integer.parseInt(name.substring(1));
				break;
			default: 
				throw new RuntimeException("Error: Incorrect position name.");
		}
	}

	/**
	 * Retrieve the x position.
	 * @return - The x position.
	 */
	public int getX(){
		return this.x;
	}

	/**
	 * Retrieve the y position.
	 * @return - The y position.
	 */
	public int getY(){
		return this.y;
	}
	
	/**
	 * Set the x and y of this position.
	 * @param x - The x position.
	 * @param y - The y position.
	 */
	public void setPosition(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Set the x and y of this position to the new position.
	 * @param position - The new position for which to use the x and y.
	 */
	public void setPosition(Position position){
		this.setPosition(position.getX(), position.getY());
	}
	
	/**
	 * Retrieve the index of this position.
	 * @return - The index of this position.
	 */
	public int getIndex(){
		return this.index;
	} 
	
	/**
	 * Retrieve the name of the position.
	 * @return - The name of the position.
	 */
	public String getName(){
		return this.name;
	}
	
	/**
	 * Retrieve the enumeration of the piece. 
	 * @return - The PieceEnum of the position.
	 */
	public PieceEnum getEnum(){
		return this.pieceEnum;
	}
	
	/**
	 * Retrieve a clone of the position.
	 */
	public Position clone(){
		Position tempP = new Position(this.x, this.y);
		tempP.index = this.index;
		tempP.name = this.name;
		tempP.pieceEnum = this.pieceEnum;
		return tempP;
	}
	
	/**
	 * Retrieve a string representation of the position.
	 */
	public String toString(){
		return this.name + " at (" + this.x + ", " + this.y + ")";
	}
	
	/**
	 * Determine if another position is equal to this one. 
	 * @param position - The other position to check for equality.
	 * @return - True if the other position is equal to this one, false otherwise.
	 */
	public boolean equals(Position position){
		// Check the position.
		if (this.x != position.getX() || this.y != position.getY()){
			return false;
		}
		
		// Check the name, index, and enumeration.
		if (this.name != position.getName() || this.index != position.getIndex() || this.pieceEnum != position.getEnum()){
			return false; 
		}
		
		return true;
	}
}
