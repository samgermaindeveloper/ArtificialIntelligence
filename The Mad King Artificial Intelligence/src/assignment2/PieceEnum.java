package assignment2;

/**
 * An enum that represents the different pieces that can be placed onto the b
 * @author Nickolas Gough and Sam Germain
 */
public enum PieceEnum {
	KING,
	GUARD,
	DRAGON,
	BLANK;
	
	/**
	 * Retrieve a String representation of the enum.
	 */
	public String toString(){
		if (this == KING){
			return "K";
		}
		else if (this == GUARD){
			return "G";
		}
		else if (this == DRAGON) {
			return "D";
		}
		else {
			return ".";
		}
	}
}
