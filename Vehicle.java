import org.jgrapht.alg.util.Pair;

/**
 * Represents a vehicle that can carry one package around the map.
 * @author nickolas
 */
public class Vehicle {

	// Store the vehicle's location.
	private Pair<Integer, Integer> location;
	
	// Store the package if the vehicle is carrying a package. 
	private Package pack;
	
	// Store the index of the vehicle.
	private int index;
	
	/**
	 * Constructor for the vehicle. 
	 * @param location - The initial location of the package, represented by a Pair of integers. 
	 */
	public Vehicle(Pair<Integer, Integer> location, int index){
		this.location = location;
		this.index = index;
		this.pack = null;
	}
	
	/**
	 * Set the location of the vehicle. 
	 * @param location - The new location of the vehicle. 
	 */
	public void setLocation(Pair<Integer, Integer> location){
		this.location = location;
	}
	
	/**
	 * Retrieve the location of the vehicle.
	 * @return - The Pair object representing the location of the vehicle on the map. 
	 */
	public Pair<Integer, Integer> getLocation(){
		return this.location;
	}
	
	/**
	 * Pickup a package.
	 * @param pack - The package to pickup.
	 */
	public void pickupPackage(Package pack){
		pack.pickup();
		this.pack = pack;
	}
	
	/**
	 * Deliver the package.
	 */
	public void deliverPackage(){
		this.pack.deliver();
		this.pack = null;
	}
	
	/**
	 * Retrieve the package. 
	 * @return - The package if the vehicle is carrying one; null otherwise.
	 */
	public Package getPackage(){
		return this.pack;
	}
	
	/**
	 * Retrieve the index of the vehicle.
	 * @return - The assigned index of the vehicle.
	 */
	public int getIndex(){
		return this.index;
	}
	
	/**
	 * Compare this vehicle to another and determine if they are equal.
	 * @param vehicle - The other vehicle to compare this vehicle to.
	 * @return - True if the two vehicles are equal; false otherwise.
	 */
	public boolean equals(Vehicle vehicle){
		// Determine if the other vehicle is null.
		if (vehicle == null){
			return false;
		}
		
		// Compare the various aspects of the vehicle.
		if (!this.location.equals(vehicle.getLocation())){
			return false;
		}
		if (this.getIndex() != vehicle.getIndex()){
			return false;
		}
		if (this.pack != null && !this.pack.equals(vehicle.getPackage())){
			return false;
		}
		
		return true;
	}
	
	/**
	 * Clone the vehicle.
	 */
	public Vehicle clone() throws RuntimeException{
		// Instantiate the new vehicle. 
		Pair<Integer, Integer> location = new Pair<Integer, Integer>(this.location.getFirst(), this.location.getSecond());
		Vehicle newVehicle = new Vehicle(location, this.index);
		
		// Assign the values accordingly.
		if (this.pack != null){
			newVehicle.pickupPackage(this.pack.clone());
		}
		
		// Ensure the clone is equal to the original.
		if (!this.equals(newVehicle)){
			throw new RuntimeException("Error: The vehicle clone is not equal to the original.");
		}
		
		return newVehicle;
	}
	
	/**
	 * Retrieve a string representation of the vehicle.
	 */
	public String toString(){
		String result = "[" + this.location + ", ";
		if (this.pack != null){
			result += this.pack.toString();
		}
		else {
			result += "null";
		}
		result +=  "]";
		return result;
	}
	
	/**
	 * A main method for testing purposes.
	 * @param args - The arguments to be provided to the main method.
	 */
	public static void main(String[] args){
		// Test 0: Instantiate a vehicle.
		Pair<Integer, Integer> location = new Pair<Integer, Integer>(0, 0);
		Vehicle vehicle = new Vehicle(location, 0);
		
		// Test 1: Test the get and set location methods. 
		if (!vehicle.getLocation().equals(location)){
			System.out.println("Error: getLocation() method does not return the correct result.");
		}
		Pair<Integer, Integer> location2 = new Pair<Integer, Integer>(1, 1);
		vehicle.setLocation(location2);
		if (!vehicle.getLocation().equals(location2)){
			System.out.println("Error: setLocation() method does not set the correct result.");
		}
		vehicle.setLocation(location);
		
		// Test 2: Test the pickup, deliver, and get package methods. 
		if (vehicle.getPackage() != null){
			System.out.println("Error: getPackage() method does not return the correct result.");
		}
		Package pack = new Package(location, location2, 0);
		vehicle.pickupPackage(pack);
		if (!vehicle.getPackage().equals(pack) || !pack.pickedup() || pack.delivered()){
			System.out.println("Error: setPackage() method does not set the correct result.");
		}
		vehicle.deliverPackage();
		if (vehicle.getPackage() != null || pack.pickedup() || !pack.delivered()){
			System.out.println("Error: deliverPackage() method does not set the correct result.");
		}
		
		// Test 3: Test the get index method.
		if (vehicle.getIndex() != 0){
			System.out.println("Error: getIndex() method does not return the correct result.");
		}
		
		// Test 4: Test the equals method.
		Vehicle vehicle2 = new Vehicle(location, 0);
		if (!vehicle.equals(vehicle2)){
			System.out.println("Error: equals() method does not return the correct result.");
		}
		vehicle.setLocation(location2);
		if (vehicle.equals(vehicle2)){
			System.out.println("Error: equals() method does not return the correct result.");
		}
		vehicle.setLocation(location);
		vehicle.pickupPackage(pack);
		if (vehicle.equals(vehicle2)){
			System.out.println("Error: equals() method does not return the correct result.");
		}
		vehicle.deliverPackage();
		if (!vehicle.equals(vehicle2)){
			System.out.println("Error: equals() method does not return the correct result.");
		}
		Vehicle vehicle3 = new Vehicle(location, 1);
		if (vehicle.equals(vehicle3)){
			System.out.println("Error: equals() method does not return the correct result.");
		}
		
		// Test 5: Test the clone method. 
		if (!vehicle.clone().equals(vehicle)){
			System.out.println("Error: clone() method does not return the correct result.");
		}
		
		// Test 6: Test the to string method.
		System.out.println(vehicle.toString());
		
		System.out.println("Tests complete.");
	}
}
