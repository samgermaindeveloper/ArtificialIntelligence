import org.jgrapht.alg.util.Pair;

/**
 * Represents a package that may be moved around the graph by a vehicle.
 * Each package has a source and a destination.
 * @author nickolas
 */
public class Package {

	// Store the current location of the package.
	private Pair<Integer, Integer> location;

	// Store the source of the package. 
	private Pair<Integer, Integer> source;

	// Store the destination of the package. 
	private Pair<Integer, Integer> destination;

	// Store a boolean to determine if a package is already being carried by a vehicle.
	private boolean pickedup;

	// Store a boolean to represent if the package has been delivered. 
	private boolean delivered;

	// Store the index of the package.
	private int index;

	/**
	 * The constructor for the package. 
	 * @param source - The pair of coordinates representing the package's source vertex.
	 * @param destination - The pair of coordinates representing the package's destination vertex. 
	 */
	public Package(Pair<Integer, Integer> source, Pair<Integer, Integer> destination, int index){
		this.location = source;
		this.source = source;
		this.destination = destination;
		this.pickedup = false;
		this.delivered = false;
		this.index = index;
	}

	/**
	 * Set the current location of the package.
	 * @param location - The new location of the package.
	 */
	public void setLocation(Pair<Integer, Integer> location){
		this.location = location;
	}

	/**
	 * Retrieve the current location of the package.
	 * @return - The Pair object representing the current location of the package.
	 */
	public Pair<Integer, Integer> getLocation(){
		return this.location;
	}

	/**
	 * Retrieve the source vertex of the package. 
	 * @return - The Pair object representing the source vertex.
	 */
	public Pair<Integer, Integer> getSource(){
		return this.source;
	}

	/**
	 * Retrieve the destination vertex of the package. 
	 * @return - The Pair object representing the destination vertex.
	 */
	public Pair<Integer, Integer> getDestination(){
		return this.destination;
	}

	/**
	 * Pick up the package. 
	 */
	public void pickup(){
		this.pickedup = true;
	}

	/**
	 * Determine if the package has been picked up.
	 * @return  True if the package has been picked up; false otherwise. 
	 */
	public boolean pickedup(){
		return this.pickedup;
	}

	/**
	 * Deliver the package.
	 */
	public void deliver(){
		this.delivered = true;
		this.pickedup = false;
	}

	/**
	 * Determine if the package has been delivered. 
	 * @return - True if the package has been delivered, false otherwise.
	 */
	public boolean delivered(){
		return this.delivered;
	}

	/**
	 * Retrieve the index of the package.
	 * @return - The assigned index of the package.
	 */
	public int getIndex(){
		return this.index;
	}

	/**
	 * Clone the package.
	 */
	public Package clone() throws RuntimeException{
		// Instantiate the package.
		Pair<Integer, Integer> source = new Pair<Integer, Integer>(this.source.getFirst(), this.source.getSecond());
		Pair<Integer, Integer> destination = new Pair<Integer, Integer>(this.destination.getFirst(), this.destination.getSecond());
		Package newPackage = new Package(source, destination, this.index);
		Pair<Integer, Integer> location = new Pair<Integer, Integer>(this.location.getFirst(), this.location.getSecond());
		newPackage.setLocation(location);

		// Adjust default values accordingly. 
		if (this.delivered){
			newPackage.deliver();
		}
		if (this.pickedup){
			newPackage.pickup();
		}

		// Ensure the clone is equal to the original.
		if (!this.equals(newPackage)){
			throw new RuntimeException("Error: The package clone is not equal to the original.");
		}

		return newPackage;
	}

	/**
	 * Compare another package to this package and determine if they are equal.
	 * @param otherPackage - The other package to compare.
	 * @return - True if the packages are equal; false otherwise.
	 */
	public boolean equals(Package otherPackage){
		// Determine if the other package is null.
		if (otherPackage == null){
			return false;
		}

		// Compare the different aspects of the packages.
		if (!this.source.equals(otherPackage.getSource())){
			return false;
		}
		if (!this.destination.equals(otherPackage.getDestination())){
			return false;
		}
		if (!this.location.equals(otherPackage.getLocation())){
			return false;
		}
		if (this.index != otherPackage.getIndex()){
			return false;
		}
		if (this.delivered != otherPackage.delivered()){
			return false;
		}
		if (this.pickedup != otherPackage.pickedup()){
			return false;
		}

		return true;
	}

	/**
	 * Retrieve a string representation of the package. 
	 */
	public String toString(){
		String result = "[" + this.location.toString() + ", " + this.source.toString() + ", " + this.destination.toString() + ", " + this.delivered +"]";
		return result;
	}

	/**
	 * Main method for testing.
	 * @param args - The arguments passed to the main method.
	 */
	public static void main(String[] args){
		// Test 0: Successfully instantiate the package object.
		Pair<Integer, Integer> source = new Pair<Integer, Integer>(1, 2);
		Pair<Integer, Integer> destination = new Pair<Integer, Integer>(2, 3);
		Package pack = new Package(source, destination, 0);

		// Test 1: Test the set and get location methods.
		if (!pack.getLocation().equals(source)){
			System.out.println("Error: getLocation() method does not return correct result.");
		}
		Pair<Integer, Integer> location = new Pair<Integer, Integer>(3, 7);
		pack.setLocation(location);
		if (!pack.getLocation().equals(location)){
			System.out.println("Error: setLocation() method does not set correct result.");
		}
		pack.setLocation(source);

		// Test 2: Test the get source and destination methods.
		if (!pack.getSource().equals(source)){
			System.out.println("Error: getSource() method does not set correct result.");
		}
		if (!pack.getDestination().equals(destination)){
			System.out.println("Error: getDestination() method does not set correct result.");
		}

		// Test 3: Test the pickup and pickedup methods.
		if (pack.pickedup() == true){
			System.out.println("Error: pickedup() method does not return the correct result.");
		}
		pack.pickup();
		if (pack.pickedup() != true){
			System.out.println("Error: pickup() method does not set the correct result.");
		}

		// Test 3: Test the pickup and pickedup methods.
		if (pack.delivered() == true){
			System.out.println("Error: delivered() method does not return the correct result.");
		}
		pack.deliver();
		if (pack.delivered() != true){
			System.out.println("Error: deliver() method does not set the correct result.");
		}

		// Test 4: Test the get index method.
		if (pack.getIndex() != 0){
			System.out.println("Error: getIndex() method does not return the correct result.");
		}
		
		// Test 5: Construct an equivalent package and test the equals method.
		Pair<Integer, Integer> source2 = new Pair<Integer, Integer>(1, 2);
		Pair<Integer, Integer> destination2 = new Pair<Integer, Integer>(2, 3);
		Package pack2 = new Package(source2, destination2, 0);
		if (pack.equals(pack2)){
			System.out.println("Error: equals() method does not return the correct result.");
		}
		pack2.pickup();
		if (pack.equals(pack2)){
			System.out.println("Error: equals() method does not return the correct result.");
		}
		pack2.deliver();
		if (!pack.equals(pack2)){
			System.out.println("Error: equals() method does not return the correct result.");
		}
		Pair<Integer, Integer> location2 = new Pair<Integer, Integer>(0, 9);
		pack2.setLocation(location2);
		if (pack.equals(pack2)){
			System.out.println("Error: equals() method does not return the correct result.");
		}
		
		// Test 6: Test to determine the clone method works as intended.
		if (!pack.clone().equals(pack)){
			System.out.println("Error: The clone function does not return an equivalent package.");
		}

		// Test 7: Test to determine the to string method works as intended.
		System.out.println(pack.toString());
		
		System.out.println("Tests completed.");
	}
}
