	import java.util.ArrayList;
	import java.util.HashMap;
	import java.util.Set;
	import java.util.TreeMap;
	
	import org.jgrapht.Graph;
	import org.jgrapht.VertexFactory;
	import org.jgrapht.alg.ConnectivityInspector;
	import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
	import org.jgrapht.alg.util.Pair;
	import org.jgrapht.generate.GnpRandomGraphGenerator;
	import org.jgrapht.generate.GridGraphGenerator;
	import org.jgrapht.generate.LinearGraphGenerator;
	import org.jgrapht.generate.RingGraphGenerator;
	import org.jgrapht.generate.StarGraphGenerator;
	import org.jgrapht.generate.WheelGraphGenerator;
	import org.jgrapht.graph.DefaultEdge;
	import org.jgrapht.graph.DefaultWeightedEdge;
	import org.jgrapht.graph.SimpleGraph;
	import org.jgrapht.graph.SimpleWeightedGraph;
	
	public class State implements Comparable<State>{
	
		// Store the list of vehicles that will carry the packages. 
		private ArrayList<Vehicle> vehicles;
	
		// Store the list of packages that must be delivered.
		private ArrayList<Package> packages;
	
		// Store the garage.
		private Pair<Integer, Integer> garage;
	
		// Store the map that the vehicles must traverse. 
		private SimpleGraph<Pair<Integer, Integer>, DefaultEdge> map;
	
		// Store the reduced version of the map separately.
		private SimpleWeightedGraph<Pair<Integer, Integer>, DefaultWeightedEdge> reducedMap;
	
		// Store the cost of the current state. 
		private int cost;
	
		// Store the values used to generate the problem.
		//n is the number of vehicles, k is the number of packages, m is the number of vertices in the graph
		private int n, k, m;
	
		// Compute the shortest path between two vertices using disjkstra's algorithm.
		private DijkstraShortestPath<Pair<Integer, Integer>, DefaultEdge> dijkstraAlg;
	
		// Compute the shortest path between two vertices using disjkstra's algorithm.
		private DijkstraShortestPath<Pair<Integer, Integer>, DefaultWeightedEdge> dijkstraAlgReduced;
	
		// Store the cost of the minimum path to. 
		private int minPathCost;
		
		/**
		 * Constructor for the state.
		 * @param n - The number of vehicles. 
		 * @param k - The number of packages.
		 * @param m - The number of vertices.
		 */
		public State(int n, int k, int m){
			// Instantiate the map, vehicles, and packages.
			this.map = new SimpleGraph<Pair<Integer, Integer>, DefaultEdge>(DefaultEdge.class);
			this.reducedMap = new SimpleWeightedGraph<Pair<Integer, Integer>, DefaultWeightedEdge>(DefaultWeightedEdge.class);
			this.vehicles = new ArrayList<Vehicle>();
			this.packages = new ArrayList<Package>();
			this.cost = 0;
			this.n = n;
			this.k = k;
			this.m = m;
			this.minPathCost = 0;
		}
	
		/**
		 * Set the vehicles stored by the state.
		 * @param vehicles - The vehicles to be stored by the state.
		 */
		private void setVehicles(ArrayList<Vehicle> vehicles){
			this.vehicles = vehicles;
		}
	
		/**
		 * Set the packages to be stored by the state.
		 * @param packages - The packages to be stored by the state.
		 */
		private void setPackages(ArrayList<Package> packages){
			this.packages = packages;
		}
	
		/**
		 * Set the garage of the state. 
		 * @param garage - The garage to be stored by the state.
		 */
		private void setGarage(Pair<Integer, Integer> garage){
			this.garage = garage;
		}
	
		/**
		 * Set the dijkstra algorithm object used to compute shortest paths.
		 * @param dijkstraAlg - The Dijkstra shortest paths object.
		 */
		private void setDijkstra(DijkstraShortestPath<Pair<Integer, Integer>, DefaultEdge> dijkstraAlg){
			this.dijkstraAlg = dijkstraAlg;
		}
	
		/**
		 * Set the dijkstra algorithm object used to compute shortest paths for reduced.
		 * @param dijkstraAlg - The Dijkstra shortest paths object.
		 */
		private void setDijkstraReduced(DijkstraShortestPath<Pair<Integer, Integer>, DefaultWeightedEdge> dijkstraAlg){
			this.dijkstraAlgReduced = dijkstraAlg;
		}
	
		/**
		 * Set the map to be stored by this state.
		 * @param map - The map to be stored by this state.
		 */
		private void setMap(SimpleGraph<Pair<Integer, Integer>, DefaultEdge> map) throws RuntimeException{
			// Check that the map is correct.
			
			this.map = map;
		}
	
		/**
		 * Set the reduced map stored by the state.
		 * @param reducedMap - The reduced map to be stored by this state.
		 */
		private void setReducedMap(SimpleWeightedGraph<Pair<Integer, Integer>, DefaultWeightedEdge> reducedMap) throws RuntimeException{
			// Check that the map is reduced.
			if (reducedMap.vertexSet().size() > (this.m*2)+1){
				throw new RuntimeException("Error: The set reduced map is not equal to the original.");
			}
			
			this.reducedMap = reducedMap;
		}
		
		/**
		 * Set the minimum path cost of this state.
		 * @param cost  The new minimum path cost.
		 */
		public void setMinPathCostReduced(int cost){
			this.minPathCost = cost;
		}
	
		/**
		 * Retrieve the map of the state.
		 */
		public Graph<Pair<Integer, Integer>, DefaultEdge> getMap(){
			return this.map;
		}
	
		/**
		 * Retrieve the reduced map of the state.
		 */
		public Graph<Pair<Integer, Integer>, DefaultWeightedEdge> getReducedMap(){
			return this.reducedMap;
		}
	
		/**
		 * Set the cost of the state.
		 * @param cost - The cost of the state.
		 */
		private void setCostValue(int cost){
			this.cost = cost;
		}
	
		/**
		 * Retrieve the cost of the state.
		 * @return - The cost of the state.
		 */
		public int getCostValue(){
			return this.cost;
		}
	
		/**
		 * Retrieve the value of the heuristic for this state.
		 * @return - The estimated cost of the heuristic.
		 */
		public int getSimpleHeuristicValue(){
			// The result is initially zero.
			int result = 0;
			final int unPickedUpP = 3; 
			final int pickedUpP = 2;
			final int notHomeV = 1;
	
			// Increase the cost for each package that has not been picked up.
			for (Package tempP : this.packages){
				if (!tempP.delivered()){
					result += unPickedUpP;
				}
			}
	
			// Increase the cost for each picked up package.
			for (Vehicle tempV : this.vehicles){
				if (tempV.getPackage() != null && !tempV.getPackage().delivered() && tempV.getPackage().pickedup()){
					result += pickedUpP;
				}
			}
	
			// Increase the cost for each vehicle that is not home after all the packages have been delivered.
			boolean allDelivered = true;
			for (Package tempP : this.packages){
				if (!tempP.delivered()){
					allDelivered = false;
				}
			}
			for (Vehicle tempV : this.vehicles){
				if (tempV.getPackage() != null && !tempV.getPackage().delivered()){
					allDelivered = false;
				}
			}
			if (allDelivered){
				for (Vehicle tempV : this.vehicles){
					if (!tempV.getLocation().equals(this.garage)){
						result += notHomeV;
					}
				}
			}
	
			return result;
		}
	
		/**
		 * Compute a more advanced heuristic using shortest paths that is likely to be more accurate.
		 * @return - The cost of the heuristic function.
		 */
		public int getAdvancedHeuristicValue(){
			// Initialize the cost to zero.
			int result = 0;
			final int nonPickedUp = 2;
			final int pickedUp = 1;
	
			// Increase cost by shortest path between source and destination plus two for each non-picked-up package.
			for (Package tempP : this.packages){
				if (!tempP.delivered() && !tempP.pickedup()){
					result += this.shortestPath(tempP.getSource(), tempP.getDestination())+nonPickedUp;
				}
			}
	
			// Increase cost by shortest path between source and destination for each picked-up package.
			for (Vehicle tempV : this.vehicles){
				if (tempV.getPackage() != null && !tempV.getPackage().delivered()){
					result += this.shortestPath(tempV.getPackage().getLocation(), tempV.getPackage().getDestination())+pickedUp;
				}
			}
	
			// Increase cost by shortest path between vehicle and garage after packages are delivered.
			boolean allDelivered = true;
			for (Package tempP : this.packages){
				if (!tempP.delivered()){
					allDelivered = false;
				}
			}
			if (allDelivered){
				for (Vehicle tempV : this.vehicles){
					if (tempV.getPackage() != null){
						result += this.shortestPath(tempV.getLocation(), this.garage);
					}
				}
			}
	
			return result;
		}
	
		/**
		 * Compute a more advanced heuristic using shortest paths that is likely to be more accurate.
		 * @return - The cost of the heuristic function.
		 */
		public int getAdvancedHeuristicValueReduced(){
			// Initialize the cost to zero.
			int result = 0;
			final int nonPickedUp = 2*this.minPathCost;
			final int pickedUp = this.minPathCost;
	
			// Increase cost by shortest path between source and destination plus two for each non-picked-up package.
			for (Package tempP : this.packages){
				if (!tempP.delivered() && !tempP.pickedup()){
					result += this.shortestPathReduced(tempP.getSource(), tempP.getDestination())+nonPickedUp;
				}
			}
	
			// Increase cost by shortest path between location and destination for each picked-up package.
			for (Vehicle tempV : this.vehicles){
				if (tempV.getPackage() != null && !tempV.getPackage().delivered()){
					result += this.shortestPathReduced(tempV.getPackage().getLocation(), tempV.getPackage().getDestination())+pickedUp;
				}
			}
	
			// Increase cost by shortest path between vehicle and garage after no packages to deliver.
			boolean packagesToDeliver = false;
			for (Package tempP : this.packages){
				if (!tempP.delivered()){
					packagesToDeliver = true;
				}
			}
			if (!packagesToDeliver){
				for (Vehicle tempV : this.vehicles){
					if (tempV.getPackage() == null && !tempV.getLocation().equals(this.garage)){
						result += this.minPathCost;
					}
				}
			}
	
			return result;
		}
	
		/**
		 * Determine the estimated cost of reaching the goal from this state.
		 * @return - The estimated cost of reaching the goal from this state.
		 */
		public int getEvaluationCost(){
			// Compute the result using the simple heuristic.
	//		return this.getCostValue()+this.getSimpleHeuristicValue();
	
			// Compute the result using the advanced heuristic.
	//		return this.getCostValue()+this.getAdvancedHeuristicValue();
	
			// Compute the result using the advanced heuristic value on the reduced graph.
			return this.getCostValue()+this.getAdvancedHeuristicValueReduced();
		}
	
		/**
		 * Initializes the Dijkstra algorithm used to compute shortest paths. 
		 */
		public void initializeDijsktra(){
			this.dijkstraAlg = new DijkstraShortestPath<Pair<Integer, Integer>, DefaultEdge>(this.map);
		}
	
		/**
		 * Initializes the Dijkstra algorithm used to compute shortest paths on the reduced map. 
		 */
		public void initializeDijsktraReduced(){
			this.dijkstraAlgReduced = new DijkstraShortestPath<Pair<Integer, Integer>, DefaultWeightedEdge>(this.reducedMap);
		}
	
		/**
		 * Retrieve the cost of the shortest path between two vertices.
		 * @param source - The source vertex.
		 * @param target - The target vertex.
		 * @return - The cost of the shortest path between the two vertices.
		 */
		public int shortestPath(Pair<Integer, Integer> source, Pair<Integer, Integer> target){
			return (int)this.dijkstraAlg.getPathWeight(source, target);
		}
	
		/**
		 * Retrieve the cost of the shortest path between two vertices.
		 * @param source - The source vertex.
		 * @param target - The target vertex.
		 * @return - The cost of the shortest path between the two vertices.
		 */
		public int shortestPathReduced(Pair<Integer, Integer> source, Pair<Integer, Integer> target){
			return (int)this.dijkstraAlgReduced.getPathWeight(source, target);
		}
	
		/**
		 * Compare the estimated total cost of this state to that of another state.
		 * @param otherState - Another state to which to compare this state to.
		 */
		public int compareTo(State otherState){
			return this.getEvaluationCost()-otherState.getEvaluationCost();
		}
	
		/**
		 * Determine if another state is equal to this state.
		 * @param state - Another state to which to compare this state.
		 * @return - True if the two states are equal; false otherwise.
		 */
		public boolean equals(State state){
			// Determine if the passed state is null.
			if (state == null){
				return false;
			}
	
			// Compare the multiple aspects of the object.
			if (this.getCostValue() != state.getCostValue()){
				return false;
			}
			if (this.packages.size() != state.packages.size()){
				return false;
			}
			for (int n = 0; n < this.packages.size(); n += 1){
				if (!this.packages.get(n).equals(state.packages.get(n))){
					return false;
				}
			}
			if (this.vehicles.size() != state.vehicles.size()){
				return false;
			}
			for (int n = 0; n < this.vehicles.size(); n += 1){
				if (!this.vehicles.get(n).equals(state.vehicles.get(n))){
					return false;
				}
			}
	
			return true;
		}
	
		/**
		 * Generates a wheel graph.
		 */
		public void wheelGraph(){
			// Instantiate the vertex factory.
			MyVertexFactory vertexFactory = new MyVertexFactory();
	
			// Instantiate the result map.
			HashMap<String, Pair<Integer, Integer>> result = new HashMap<String, Pair<Integer, Integer>>();
	
			// Instantiate the grid graph generator.
			WheelGraphGenerator<Pair<Integer, Integer>, DefaultEdge> graphGenerator = new WheelGraphGenerator<Pair<Integer, Integer>, DefaultEdge>(this.m);
	
			// Generate the graph.
			boolean generated = false;
			while (!generated){
				try {
					// Generate the graph.
					graphGenerator.generateGraph(this.map, vertexFactory, result);
	
					// Indicate the graph has been generated.
					generated = true;
	
					// Determine if the graph is connected.
					ConnectivityInspector<Pair<Integer, Integer>, DefaultEdge> inspector = new ConnectivityInspector<Pair<Integer, Integer>, DefaultEdge>(this.map);
					if (!inspector.isGraphConnected()){
						generated = false;
					}
				}
				catch(Exception e){
					// Do nothing and repeat until a graph is generated.
				}
			}
		}
	
		/**
		 * Generates a ring graph.
		 */
		public void ringGraph(){
			// Instantiate the vertex factory.
			MyVertexFactory vertexFactory = new MyVertexFactory();
	
			// Instantiate the result map.
			HashMap<String, Pair<Integer, Integer>> result = new HashMap<String, Pair<Integer, Integer>>();
	
			// Instantiate the grid graph generator.
			RingGraphGenerator<Pair<Integer, Integer>, DefaultEdge> graphGenerator = new RingGraphGenerator<Pair<Integer, Integer>, DefaultEdge>(this.m);
	
			// Generate the graph.
			boolean generated = false;
			while (!generated){
				try {
					// Generate the graph.
					graphGenerator.generateGraph(this.map, vertexFactory, result);
	
					// Indicate the graph has been generated.
					generated = true;
	
					// Determine if the graph is connected.
					ConnectivityInspector<Pair<Integer, Integer>, DefaultEdge> inspector = new ConnectivityInspector<Pair<Integer, Integer>, DefaultEdge>(this.map);
					if (!inspector.isGraphConnected()){
						generated = false;
					}
				}
				catch(Exception e){
					// Do nothing and repeat until a graph is generated.
				}
			}
		}
	
		/**
		 * Generates a star graph.
		 */
		public void starGraph(){
			// Instantiate the vertex factory.
			MyVertexFactory vertexFactory = new MyVertexFactory();
	
			// Instantiate the result map.
			HashMap<String, Pair<Integer, Integer>> result = new HashMap<String, Pair<Integer, Integer>>();
	
			// Instantiate the grid graph generator.
			StarGraphGenerator<Pair<Integer, Integer>, DefaultEdge> graphGenerator = new StarGraphGenerator<Pair<Integer, Integer>, DefaultEdge>(this.m);
	
			// Generate the graph.
			boolean generated = false;
			while (!generated){
				try {
					// Generate the graph.
					graphGenerator.generateGraph(this.map, vertexFactory, result);
	
					// Indicate the graph has been generated.
					generated = true;
	
					// Determine if the graph is connected.
					ConnectivityInspector<Pair<Integer, Integer>, DefaultEdge> inspector = new ConnectivityInspector<Pair<Integer, Integer>, DefaultEdge>(this.map);
					if (!inspector.isGraphConnected()){
						generated = false;
					}
				}
				catch(Exception e){
					// Do nothing and repeat until a graph is generated.
				}
			}
		}
	
		/**
		 * Generates a linear graph.
		 */
		public void linearGraph(){
			// Instantiate the vertex factory.
			MyVertexFactory vertexFactory = new MyVertexFactory();
	
			// Instantiate the result map.
			HashMap<String, Pair<Integer, Integer>> result = new HashMap<String, Pair<Integer, Integer>>();
	
			// Instantiate the grid graph generator.
			LinearGraphGenerator<Pair<Integer, Integer>, DefaultEdge> graphGenerator = new LinearGraphGenerator<Pair<Integer, Integer>, DefaultEdge>(this.m);
	
			// Generate the graph.
			boolean generated = false;
			while (!generated){
				try {
					// Generate the graph.
					graphGenerator.generateGraph(this.map, vertexFactory, result);
	
					// Indicate the graph has been generated.
					generated = true;
	
					// Determine if the graph is connected.
					ConnectivityInspector<Pair<Integer, Integer>, DefaultEdge> inspector = new ConnectivityInspector<Pair<Integer, Integer>, DefaultEdge>(this.map);
					if (!inspector.isGraphConnected()){
						generated = false;
					}
				}
				catch(Exception e){
					// Do nothing and repeat until a graph is generated.
				}
			}
		}
	
		/**
		 * Generates a grid graph.
		 * @param rows - The number of rows to create.
		 * @param columns - The number of columns to create.
		 * @throws RuntimeException - When the number of vertices does not match the problem definition.
		 */
		public void gridGraph(int rows, int columns) throws RuntimeException{
			// Check that the number of vertices is correct.
			if (rows*columns != this.m){
				throw new RuntimeException("The rows and columns do not match the number of vertices specified.");
			}
			
			// Instantiate the vertex factory.
			MyVertexFactory vertexFactory = new MyVertexFactory();
	
			
			// Instantiate the result map.
			TreeMap<String, Pair<Integer, Integer>> result = new TreeMap<String, Pair<Integer, Integer>>();
	
			// Instantiate the grid graph generator.
			GridGraphGenerator<Pair<Integer, Integer>, DefaultEdge> graphGenerator = new GridGraphGenerator<Pair<Integer, Integer>, DefaultEdge>(rows, columns);
	
			// Generate the graph.
			boolean generated = false;
			while (!generated){
				try {
					// Generate the graph.
					graphGenerator.generateGraph(this.map, vertexFactory, result);
	
					// Indicate the graph has been generated.
					generated = true;
	
					// Determine if the graph is connected.
					ConnectivityInspector<Pair<Integer, Integer>, DefaultEdge> inspector = new ConnectivityInspector<Pair<Integer, Integer>, DefaultEdge>(this.map);
					if (!inspector.isGraphConnected()){
						generated = false;
					}
					
				}
				catch(Exception e){
					// Do nothing and repeat until a graph is generated.
				}
			}
		}
	
		/**
		 * Generates a random State using the parameters.
		 */
		public void randomGraph(){		
			// Instantiate the vertex factory.
			MyVertexFactory vertexFactory = new MyVertexFactory();
	
			// Instantiate the result map.
			HashMap<String, Pair<Integer, Integer>> result = new HashMap<String, Pair<Integer, Integer>>();
	
			// Instantiate the random graph generator. 
			//		int e = m-1;//0+(int)(Math.random()*((this.m-1)*(this.m-1)));
			//		GnmRandomGraphGenerator<Pair<Integer, Integer>, DefaultEdge> graphGenerator = new GnmRandomGraphGenerator<Pair<Integer, Integer>, DefaultEdge>(this.m, e);
	
			// Instantiate the random graph generator. 
			double probability = 0.5;
			GnpRandomGraphGenerator<Pair<Integer, Integer>, DefaultEdge> graphGenerator = new GnpRandomGraphGenerator<Pair<Integer, Integer>, DefaultEdge>(this.m, probability);
	
			// Generate the graph.
			boolean generated = false;
			while (!generated){
				try {
					// Generate the graph.
					graphGenerator.generateGraph(this.map, vertexFactory, result);
	
					// Indicate the graph has been generated.
					generated = true;
	
					// Determine if the graph is connected.
					ConnectivityInspector<Pair<Integer, Integer>, DefaultEdge> inspector = new ConnectivityInspector<Pair<Integer, Integer>, DefaultEdge>(this.map);
					if (!inspector.isGraphConnected()){
						generated = false;
					}
				}
				catch(Exception e){
					// Do nothing and repeat until a graph is generated.
				}
			}
		}
	
		/**
		 * Generates the specified graph type and initializes the necessary map.
		 * @param graphType - The type of graph to initialize.
		 * @param reduced - True if reduced map should be used; false otherwise
		 * @param rows - The number of rows for the map. Used only when graph type is grid.
		 * @param columns - The number of columns for the map. Used only when the graph type is grid.
		 */
		public void generateGraph(String graphType, boolean reduced, int rows, int columns){
			// Determine which map to generate and generate it.
			if (graphType.equals("random")){
				this.randomGraph();
			}
			else if (graphType.equals("grid")){
				this.gridGraph(rows, columns);
			}
			else if (graphType.equals("star")){
				this.starGraph();
			}
			else if (graphType.equals("wheel")){
				this.wheelGraph();
			}
			else if (graphType.equals("ring")){
				this.ringGraph();
			}
			else if (graphType.equals("linear")){
				this.linearGraph();
			}
	
			
			// Generate the state.
			this.initializeDijsktra();
			
			this.generateState();
	
			
			// Reduce the graph.
			this.reduceGraph();
	
			// Determine which map is to be used and initialize it. 
			if (reduced){
				this.initializeDijsktraReduced();
			}
		}
	
		/**
		 * Reduce the graph to only the required vertices.
		 * @throws RuntimeException - When the graph representing the map is not initialized.
		 */
		public void reduceGraph() throws RuntimeException{
			// Ensure the graph has been initialized.
			if (this.map == null){
				throw new RuntimeException("The map must be initialized before it can be reduced.");
			}
	
			// Ensure the Dijkstra algorithm has been initialized.
			if (this.dijkstraAlg == null){
				throw new RuntimeException("The Dijkstra algorithm must be initialized before the map can be reduced.");
			}
	
			// Add the garage.
			this.reducedMap.addVertex(this.garage);
	
			// Reduce the map from the garage to each of the package sources.
			for (Package tempP : this.packages){
				if (!this.reducedMap.containsVertex(tempP.getSource())){
					this.reducedMap.addVertex(tempP.getSource());
					this.reducedMap.addEdge(this.garage, tempP.getSource());
					int edgeWeight = this.shortestPath(this.garage, tempP.getSource());
					DefaultWeightedEdge edge = (DefaultWeightedEdge)this.reducedMap.getEdge(this.garage, tempP.getSource());
					this.reducedMap.setEdgeWeight(edge, edgeWeight);
				}
			}
	
			// Reduce the map from the garage to each of the package destinations.
			for (Package tempP : this.packages){
				if (!this.reducedMap.containsVertex(tempP.getDestination())){
					this.reducedMap.addVertex(tempP.getDestination());
					this.reducedMap.addEdge(this.garage, tempP.getDestination());
					int edgeWeight = this.shortestPath(this.garage, tempP.getDestination());
					DefaultWeightedEdge edge = (DefaultWeightedEdge)this.reducedMap.getEdge(this.garage, tempP.getDestination());
					this.reducedMap.setEdgeWeight(edge, edgeWeight);
				}
			}
	
			// Reduce the map from each package to its destination.
			for (Package tempP : this.packages){
				if (!this.reducedMap.containsEdge(tempP.getSource(), tempP.getDestination())){
					this.reducedMap.addEdge(tempP.getSource(), tempP.getDestination());
					int edgeWeight = this.shortestPath(tempP.getSource(), tempP.getDestination());
					DefaultWeightedEdge edge = (DefaultWeightedEdge)this.reducedMap.getEdge(tempP.getSource(), tempP.getDestination());
					this.reducedMap.setEdgeWeight(edge, edgeWeight);
				}
			}
	
			// Reduce the map from each package source to each other package source. 
			for (int n = 0; n < this.packages.size(); n += 1){
				Package tempP1 = this.packages.get(n);
				for (int m = n+1; m < this.packages.size(); m += 1){
					Package tempP2 = this.packages.get(m);
					if (!tempP1.getSource().equals(tempP2.getSource())){
						this.reducedMap.addEdge(tempP1.getSource(), tempP2.getSource());
						int edgeWeight = this.shortestPath(tempP1.getSource(), tempP2.getSource());
						DefaultWeightedEdge edge = (DefaultWeightedEdge)this.reducedMap.getEdge(tempP1.getSource(), tempP2.getSource());
						this.reducedMap.setEdgeWeight(edge, edgeWeight);
					}
				}
			}
	
			// Reduce the map from each package destination to each other package destination.
			for (int n = 0; n < this.packages.size(); n += 1){
				Package tempP1 = this.packages.get(n);
				for (int m = n+1; m < this.packages.size(); m += 1){
					Package tempP2 = this.packages.get(m);
					if (!tempP1.getDestination().equals(tempP2.getDestination())){
						this.reducedMap.addEdge(tempP1.getDestination(), tempP2.getDestination());
						int edgeWeight = this.shortestPath(tempP1.getDestination(), tempP2.getDestination());
						DefaultWeightedEdge edge = (DefaultWeightedEdge)this.reducedMap.getEdge(tempP1.getDestination(), tempP2.getDestination());
						this.reducedMap.setEdgeWeight(edge, edgeWeight);
					}
				}
			}
	
			// Reduce the map from each package source to each other package destination.
			for (int n = 0; n < this.packages.size(); n += 1){
				Package tempP1 = this.packages.get(n);
				for (int m = 0; m < this.packages.size(); m += 1){
					if (n != m){
						Package tempP2 = this.packages.get(m);
						if (!tempP1.getSource().equals(tempP2.getDestination())){
							this.reducedMap.addEdge(tempP1.getSource(), tempP2.getDestination());
							int edgeWeight = this.shortestPath(tempP1.getSource(), tempP2.getDestination());
							DefaultWeightedEdge edge = (DefaultWeightedEdge)this.reducedMap.getEdge(tempP1.getSource(), tempP2.getDestination());
							this.reducedMap.setEdgeWeight(edge, edgeWeight);
						}
					}
				}
			}
			
			// Instantiate the Dijkstra algorithm.
			this.initializeDijsktraReduced();
			
			// Determine the minimum path cost. 
			int min = Integer.MAX_VALUE;
			int current = 0;
			for (Package tempP : this.packages){
				current = this.shortestPathReduced(this.garage, tempP.getSource());
				if (current < min){
					min = current;
				}
				current = this.shortestPathReduced(this.garage, tempP.getDestination());
				if (current < min){
					min = current;
				}
			}
			for (Package tempP1 : this.packages){
				for (Package tempP2 : this.packages){
					current = this.shortestPathReduced(tempP1.getDestination(), tempP2.getSource());
					if (!tempP1.equals(tempP2) && current < min){
						min = current;
					}
				}
			}
			this.minPathCost = min;
		}
	
		/**
		 * Generate a random state.
		 */
		@SuppressWarnings("unchecked")
		public void generateState(){
			// Generate the random garage. 
			int choice = 0;
			do{
				choice = 0+(int)(Math.random()*this.m);
			} while(choice >= this.map.vertexSet().toArray().length);
			this.garage = (Pair<Integer, Integer>)this.map.vertexSet().toArray()[choice];
	
			
			// Generate the vehicles.
			for (int v = 0; v < this.n; v +=1){
				Vehicle veh = new Vehicle(this.garage, v);
				this.vehicles.add(veh);
			}
			
	
			// Generate the random package sources and destinations. 
			int counter = 0;
			int x = 0, y = 0;
			Object[] arr = this.map.vertexSet().toArray();
			
			ArrayList<Pair<Integer, Integer>> chosenVertices = new ArrayList<Pair<Integer, Integer>>();
			
			while (counter < this.k){
				
				do{
					x = 0+(int)(Math.random()*this.m);
					y = 0+(int)(Math.random()*this.m);
				}while (x>arr.length-1 || y>arr.length-1);
				Pair<Integer, Integer> source = (Pair<Integer, Integer>)arr[x];
				
				Pair<Integer, Integer> destination = (Pair<Integer, Integer>)arr[y];
				
				
				if (x != y){
					
					if (!source.equals(this.garage)){
						
						if(!destination.equals(this.garage)){
							
							if(!chosenVertices.contains(source)){
								
								if(!chosenVertices.contains(destination)){
							
				
					
					Package pack = new Package(source, destination, counter);
					this.packages.add(pack);
					
					counter += 1;
					
					chosenVertices.add(source);
					
					chosenVertices.add(destination);
					
								}
								}
							}
				}
				}
			}
		}
	
		/**
		 * Generate a custom state.
		 */
		public void customState() throws RuntimeException{
			// Create the custom vertices.
			Pair<Integer, Integer> v1 = new Pair<Integer, Integer>(1, 2);
			Pair<Integer, Integer> v2 = new Pair<Integer, Integer>(4, 5);
			Pair<Integer, Integer> v3 = new Pair<Integer, Integer>(9, 10);
			Pair<Integer, Integer> v4 = new Pair<Integer, Integer>(20, 30);
			Pair<Integer, Integer> v5 = new Pair<Integer, Integer>(30, 20);
			Pair<Integer, Integer> v6 = new Pair<Integer, Integer>(7, 9);
			Pair<Integer, Integer> v7 = new Pair<Integer, Integer>(0, 24);
			Pair<Integer, Integer> v8 = new Pair<Integer, Integer>(32, 65);
			Pair<Integer, Integer> v9 = new Pair<Integer, Integer>(45, 56);
			Pair<Integer, Integer> v10 = new Pair<Integer, Integer>(9, 3);
	
			// Add the custom vertices. 
			this.map.addVertex(v1);
			this.map.addVertex(v2);
			this.map.addVertex(v3);
			this.map.addVertex(v4);
			this.map.addVertex(v5);
			this.map.addVertex(v6);
			this.map.addVertex(v7);
			this.map.addVertex(v8);
			this.map.addVertex(v9);
			this.map.addVertex(v10);
	
			// Create the custom edges. 
			this.map.addEdge(v1, v2);
			this.map.addEdge(v2, v3);
			this.map.addEdge(v3, v4);
			this.map.addEdge(v4, v5);
			this.map.addEdge(v5, v6);
			this.map.addEdge(v6, v7);
			this.map.addEdge(v7, v8);
			this.map.addEdge(v8, v9);
			this.map.addEdge(v9, v10);
			this.map.addEdge(v10, v1);		
	
			// Assign the garage.
			this.garage = v1;
	
			// Create the vehicles. 
			Vehicle ve1 = new Vehicle(this.garage, 0);
			Vehicle ve2 = new Vehicle(this.garage, 1);
			this.vehicles.add(0, ve1);
			this.vehicles.add(1, ve2);
	
			// Create the packages. 
			Package p1 = new Package(v3, v6, 0);
			Package p2 = new Package(v4, v9, 1);
			this.packages.add(0, p1);
			this.packages.add(1, p2);
	
			// Initialize the Dijkstra algorithm.
			this.initializeDijsktra();
	
			// Check that the specified parameters have been met. 
			if (this.map.vertexSet().size() != this.m){
				throw new RuntimeException("The custom state does not meet the specified node requirements.");
			}
			if (this.packages.size() != this.k){
				throw new RuntimeException("The custom state does not meet the specified package requirements.");
			}
			if (this.vehicles.size() != this.n){
				throw new RuntimeException("The custom state does not meet the specified vehicle requirements.");
			}
		}
	
		/**
		 * Increment the cost of the state by one.
		 */
		public void incrementCost(){
			this.cost += 1;
		}
	
		/**
		 * Increment the cost by the specified amount.
		 * @param cost - The amount by which to increase the cost.
		 */
		public void incrementCost(int cost){
			this.cost += cost;
		}
	
		/**
		 * Move the vehicle.
		 * @param index - The index of the vehicle to move.
		 * @param location - The new location to assign the vehicle.
		 */
		public void moveVehicle(int index, Pair<Integer, Integer> location){
			// Assign the vehicle a new location.
			Vehicle veh = this.vehicles.get(index);
			veh.setLocation(location);
	
			// Increment the cost of the state. 
			this.incrementCost();
	
			// Assign the package the vehicle is carrying, if any, to the same location. Deliver package if possible.
			Package pack = veh.getPackage();
			if (pack != null){
				pack.setLocation(location);
				if (pack.getDestination().equals(location)){
					this.packages.add(pack);
					veh.deliverPackage();
				}
			}
	
			// Pickup a package if one is present at the new location that hasn't been delivered.
			pack = veh.getPackage();
			for (int n = 0; n < this.packages.size(); n += 1){
				Package tempP = this.packages.get(n);
				if (tempP.getLocation().equals(location) && !tempP.pickedup() && !tempP.delivered() && pack == null){
					veh.pickupPackage(tempP);
					this.packages.remove(tempP);
				}
			}
		}
	
		/**
		 * Move the vehicle.
		 * @param index - The index of the vehicle to move.
		 * @param location - The new location to assign the vehicle.
		 * @param cost - The cost to add to the cumulative cost.
		 */
		public void moveVehicle(int index, Pair<Integer, Integer> location, int cost){
			// Assign the vehicle a new location.
			Vehicle vehicle = this.vehicles.get(index);
			vehicle.setLocation(location);
	
			// Increment the cost of the state. 
			this.incrementCost(cost);
	
			// Assign the package the vehicle is carrying, if any, to the same location. Deliver package if possible.
			Package pack = vehicle.getPackage();
			if (pack != null){
				pack.setLocation(location);
				if (pack.getDestination().equals(location)){
					this.packages.add(pack);
					vehicle.deliverPackage();
				}
			}
	
			// Pickup a package if one is present at the new location that hasn't been delivered.
			pack = vehicle.getPackage();
			if (pack == null){
				for (int n = 0; n < this.packages.size(); n += 1){
					Package tempP = this.packages.get(n);
					if (tempP.getLocation().equals(location) && !tempP.pickedup() && !tempP.delivered()){
						vehicle.pickupPackage(tempP);
						this.packages.remove(tempP);
					}
				}
			}
		}
	
		/**
		 * Retrieve the new set of states given the current state.
		 * @return - A set of states that can be derived from the current state.
		 */
		public ArrayList<State> newStates(){
			// Construct the list of states.
			ArrayList<State> states = new ArrayList<State>();
	
			// Generate the list of outgoing edges from the current vehicle positions. 
			ArrayList<Set<DefaultEdge>> edges = new ArrayList<Set<DefaultEdge>>();
			for (Vehicle currentVehicle : this.vehicles){
				edges.add(currentVehicle.getIndex(), this.map.edgesOf(currentVehicle.getLocation()));
			}
	
			// Variable for simple modification of first index.
			int first = -1;
	
			// Initialize the necessary variables. 
			int[] counters = new int[edges.size()];
			for (int n = 0; n < counters.length; n += 1){
				counters[n] = first;
			}
			boolean finished = false;
	
			// Generate the outcomes by moving the vehicles simultaneously in one of its possible directions. 
			while (!finished){
				// Compute one of the new states by either moving or not moving each vehicle.
				State newState = this.clone();
				for (int n = 0; n < counters.length; n += 1){
					if (counters[n] > first){
						DefaultEdge edge = (DefaultEdge)edges.get(n).toArray()[counters[n]];
						Pair<Integer, Integer> target = this.map.getEdgeTarget(edge);
						if (this.vehicles.get(n).getLocation().equals(target)){
							target = this.map.getEdgeSource(edge);
						}
						newState.moveVehicle(n, target);
					}
				}
				states.add(newState);
	
				// Adjust the counters.
				counters[counters.length-1] += 1;
				for (int n = counters.length-1; n > 0; n -= 1){
					if (counters[n] == edges.get(n).size()){
						counters[n-1] += 1;
					}
					if (counters[n-1] == edges.get(n-1).size()){
						counters[n-1] = first;
					}
				}
				if (counters[counters.length-1] == edges.get(counters.length-1).size()){
					counters[counters.length-1] = first;
				}
	
				// Determine if the computation is finished. 
				finished = true;
				for (int n = 0; n < counters.length; n += 1){
					if (counters[n] > -1){
						finished = false;
					}
				}
			}
	
			return states;
		}
	
		/**
		 * Determines if the move is logically valid.
		 * @param index - The index of the vehicle in question.
		 * @param target - The node to which the vehicle is planning to move.
		 * @return - True if the move is logical, false otherwise.
		 */
		public boolean isValidMove(int index, Pair<Integer, Integer> target){
			// Retrieve the vehicle in question.
			Vehicle vehicle = this.vehicles.get(index);
	
			// Not moving is always a valid move. 
			if (vehicle.getLocation().equals(target)){
				return true;
			}
	
			// If the vehicle is carrying a package, determine if the target is its destination.
			if (vehicle.getPackage() != null){
				if (vehicle.getPackage().getDestination().equals(target)){
					return true;
				}
				else {
					return false;
				}
			}
	
			// Determine if the vehicle should return home.
			boolean home = true;
			for (Package tempP : this.packages){
				if (!tempP.delivered() && !tempP.pickedup()){
					home = false;
				}
			}
			if (home){
				if (this.garage.equals(target)){
					return true;
				}
				else {
					return false;
				}
			}
	
			// Otherwise determine if the target contains a package source. 
			for (Package tempP : this.packages){
				if (tempP.getSource().equals(target) && !tempP.delivered() && !tempP.pickedup()){
					return true;
				}
			}
			return false;
		}
	
		/**
		 * Retrieve the new set of states given the current state and its reduced map.
		 * @return - The set of reduced states from the reduced map.
		 */
		public ArrayList<State> newReducedStates(){
			// Construct the list of states.
			ArrayList<State> states = new ArrayList<State>();
	
			// Generate the list of outgoing edges from the current vehicle positions. 
			ArrayList<Set<DefaultWeightedEdge>> edges = new ArrayList<Set<DefaultWeightedEdge>>();
			for (Vehicle currentVehicle : this.vehicles){
				edges.add(currentVehicle.getIndex(), this.reducedMap.edgesOf(currentVehicle.getLocation()));
			}
	
			// Variable for simple modification of first index.
			int first = -1;
	
			// Initialize the necessary variables. 
			int[] counters = new int[edges.size()];
			for (int n = 0; n < counters.length; n += 1){
				counters[n] = first;
			}
			boolean finished = false;
	
			// Generate the outcomes by moving the vehicles simultaneously in one of its possible directions. 
			while (!finished){
				// Compute one of the new states by either moving or not moving each vehicle.
				State newState = this.clone();
				boolean allValidMoves = true;
				Vehicle tempV = null;
				for (int n = 0; n < counters.length && allValidMoves; n += 1){
					if (counters[n] > first){
						DefaultWeightedEdge edge = (DefaultWeightedEdge)edges.get(n).toArray()[counters[n]];
						Pair<Integer, Integer> target = this.reducedMap.getEdgeTarget(edge);
						tempV = this.vehicles.get(n);
						if (tempV.getLocation().equals(target)){
							target = this.reducedMap.getEdgeSource(edge);
						}
						int weight = (int)this.reducedMap.getEdgeWeight(edge);
						allValidMoves = newState.isValidMove(n, target);
						newState.moveVehicle(n, target, weight);
					}
				}
				if (allValidMoves){
					states.add(newState);
				}
	
				// Adjust the counters.
				counters[counters.length-1] += 1;
				for (int n = counters.length-1; n > 0; n -= 1){
					if (counters[n] == edges.get(n).size()){
						counters[n-1] += 1;
					}
					if (counters[n-1] == edges.get(n-1).size()){
						counters[n-1] = first;
					}
				}
				if (counters[counters.length-1] == edges.get(counters.length-1).size()){
					counters[counters.length-1] = first;
				}
	
				// Determine if the computation is finished. 
				finished = true;
				for (int n = 0; n < counters.length; n += 1){
					if (counters[n] > -1){
						finished = false;
					}
				}
			}
	
			return states;
		}
	
		/**
		 * Determine if the goal state has been reached. 
		 * @return - True if the goal state has been reached; false otherwise.
		 */
		public boolean isGoal(){
			// Check that each package has been delivered.
			for (Package tempP : this.packages){
				if (!tempP.delivered()){
					return false;
				}
			}
	
			// Check that each truck is back at the garage.
			for (Vehicle tempV : this.vehicles){
				if (!tempV.getLocation().equals(this.garage)){
					return false;
				}
				if (tempV.getPackage() != null && !tempV.getPackage().delivered()){
					return false;
				}
			}
	
			return true;
		}
		
		/**
		 * Clone the current state enough to separate it from other states.
		 */
		public State clone() throws RuntimeException{
			// Instantiate the new State object.
			State state = new State(this.n, this.k, this.m);
	
			// Adjust the cost as necessary.
			state.setCostValue(this.cost);
			state.setMinPathCostReduced(this.minPathCost);
	
			// Set the garage.
			Pair<Integer, Integer> newGarage = new Pair<Integer, Integer>(this.garage.getFirst(), this.garage.getSecond());
			state.setGarage(newGarage);
	
			// Set the vehicles.
			ArrayList<Vehicle> newVehicles = new ArrayList<Vehicle>();
			for (Vehicle veh : this.vehicles){
				newVehicles.add(veh.clone());
			}
			state.setVehicles(newVehicles);
	
			// Set the packages. 
			ArrayList<Package> newPackages = new ArrayList<Package>();
			for (Package pack : this.packages){
				newPackages.add(pack.clone());
			}
			state.setPackages(newPackages);
	
			// Set the map.
			state.setMap(this.map);
	
			// Set the reduced map.
			state.setReducedMap(this.reducedMap);
	
			// Set the Dijkstra algorithms.
			state.setDijkstra(this.dijkstraAlg);
			state.setDijkstraReduced(this.dijkstraAlgReduced);
	
			// Ensure the clone is equal to the original.
			if (!this.equals(state)){
				throw new RuntimeException("Error: The state clone is not equal to the original.");
			}
	
			return state;
		}
	
		/**
		 * Retrieve a string representation of the state.
		 */
		public String toString(){
			String result = "----------" + System.lineSeparator();
			result += "Garage:" + System.lineSeparator();
			result += this.garage.toString() + System.lineSeparator();
			result += "Vehicles: " + this.n + System.lineSeparator();
			for (Vehicle tempV : this.vehicles){
				result += tempV.toString() + System.lineSeparator();
			}
			result += "Packages: " + this.k + System.lineSeparator();
			for (Package tempP : this.packages){
				result += tempP.toString() + System.lineSeparator();
			}
			result += "Map: " + this.m + System.lineSeparator();
			result += this.map.toString() + System.lineSeparator();
			result += "Reduced Map: " + this.reducedMap.vertexSet().size() + System.lineSeparator();
			result += this.reducedMap.toString() + System.lineSeparator();
			result += "Cost: " + this.cost + System.lineSeparator();
			result += "----------";
	
			return result;
		}
	
		/**
		 * Retrieves a simplified string representation of the problem size.
		 * @return - A string of the problem description in terms of its size.
		 */
		public String problemSize(){
			String result = "----------" + System.lineSeparator();
			result += "Original M: " + this.m + System.lineSeparator();
			result += "Original E: " + this.map.edgeSet().size() + System.lineSeparator();
			result += "N: " + this.n + System.lineSeparator();
			result += "K: " + this.k + System.lineSeparator();
			result += "Reduced M: " + this.reducedMap.vertexSet().size() + System.lineSeparator();
			result += "Reduced E: " + this.reducedMap.edgeSet().size() + System.lineSeparator();
			result += "----------";
	
			return result;
		}
	
		// Define and instantiate the vertex generator. 
		class MyVertexFactory implements VertexFactory<Pair<Integer, Integer>>{
			public Pair<Integer, Integer> createVertex(){
				int min = 0;
				int max = 50;
				int x = min+(int)(Math.random()*max);
				int y = min+(int)(Math.random()*max);
				Pair<Integer, Integer> v = new Pair<Integer, Integer>(x, y);
				return v;
			}
		}
		
		/**
		 * Generate a random configuration state of vehicles delivering packages.
		 * @return - A random state configuration.
		 * @throws RuntimeException - When number of undelivered packages is greater than zero.
		 */
		@SuppressWarnings("unchecked")
		public State generateRandomConfigurationReduced() throws RuntimeException{
			// Clone the current state.
			State tempS = this.clone();
			
			// Reset the cost of the configuration to zero.
			tempS.setCostValue(0);
			
			// Randomly determine the number of packages each vehicle will deliver. 
			int[] numPackages = new int[tempS.vehicles.size()];
			int remainingPackages = tempS.packages.size();
			int result = 0;
			while (remainingPackages > 0){
				remainingPackages = tempS.packages.size();
				for (int n = 0; n < numPackages.length; n += 1){
					result = (int)Math.ceil(Math.random()*remainingPackages);
					remainingPackages -= result;
					numPackages[n] = result;
				}
			}
			if (remainingPackages > 0){
				throw new RuntimeException("Error: Number of remaining packages undelivered is greater than zero.");
			}
			
			// Assign packages to be delivered by each vehicle randomly. 
			ArrayList<Package>[] vehicles = (ArrayList<Package>[])(new ArrayList[tempS.vehicles.size()]);
			ArrayList<Package> packages = null;
			int choice = 0;
			Package tempP = null;
			for (int n = 0; n < numPackages.length; n += 1){
				packages = new ArrayList<Package>();
				for (int m = 0; m < numPackages[n]; m += 1){
					choice = (int)Math.floor(Math.random()*tempS.packages.size());
					tempP = tempS.packages.get(choice);
					tempS.packages.remove(choice);
					packages.add(tempP);
				}
				vehicles[n] = packages;
			}
			
			// Determine the total cost of each vehicle sequentially delivering its random packages.
			ArrayList<Package> tempL = null;
			Pair<Integer, Integer> location = null;
			int cost = 0;
			for (int n = 0; n < vehicles.length; n += 1){
				tempL = vehicles[n];
				location = this.garage;
				for (Package pack : tempL){
					cost += tempS.shortestPathReduced(location, pack.getSource());
					cost += tempS.shortestPathReduced(pack.getSource(), pack.getDestination());
					location = pack.getDestination();
				}
				if (!location.equals(tempS.garage)){
					cost += tempS.shortestPathReduced(location, tempS.garage);
				}
			}
			
			// Set the final cost of the configuration.
			tempS.setCostValue(cost);
			
			// Replace the packages.
			for (ArrayList<Package> tempA : vehicles){
				for (Package returnPack : tempA){
					tempS.packages.add(returnPack);
				}
			}
			
			return tempS;
		}
	
		/**
		 * Generate a random configuration state of vehicles delivering packages. Then use simulated annealing to make this state better
		 * @return - A random state configuration.
		 * @throws RuntimeException - When number of undelivered packages is greater than zero.
		 */
		@SuppressWarnings("unchecked")
		public State generateSimulatedAnnealingState() throws RuntimeException{
			// Clone the current state.
			State tempS = this.clone();
			// Reset the cost of the configuration to zero.
			tempS.setCostValue(0);
			// Randomly determine the number of packages each vehicle will deliver. 
			int[] numPackages = new int[tempS.vehicles.size()];
			int remainingPackages = tempS.packages.size();
			int result = 0;
			while (remainingPackages > 0){
				remainingPackages = tempS.packages.size();
				for (int n = 0; n < numPackages.length; n += 1){
					result = (int)Math.ceil(Math.random()*remainingPackages);
					remainingPackages -= result;
					numPackages[n] = result;
				}
			}
			if (remainingPackages > 0){
				throw new RuntimeException("Error: Number of remaining packages undelivered is greater than zero.");
			}
			
			// Assign packages to be delivered by each vehicle randomly. 
			ArrayList<Package>[] vehicles = (ArrayList<Package>[])(new ArrayList[tempS.vehicles.size()]);
			ArrayList<Package> packages = null;
			int choice = 0;
			Package tempP = null;
			for (int n = 0; n < numPackages.length; n += 1){
				packages = new ArrayList<Package>();
				for (int m = 0; m < numPackages[n]; m += 1){
					choice = (int)Math.floor(Math.random()*tempS.packages.size());
					tempP = tempS.packages.get(choice);
					tempS.packages.remove(choice);
					packages.add(tempP);
				}
				vehicles[n] = packages;
			}
		
			// Determine the total cost of each vehicle sequentially delivering its random packages.
			ArrayList<Package> tempL = null;
			Pair<Integer, Integer> location = null;
			int cost = 0;
			for (int n = 0; n < vehicles.length; n += 1){
				tempL = vehicles[n];
				location = this.garage;
				for (Package pack : tempL){
					cost += tempS.shortestPathReduced(location, pack.getSource());
					cost += tempS.shortestPathReduced(pack.getSource(), pack.getDestination());
					location = pack.getDestination();
				}
				if (!location.equals(tempS.garage)){
					cost += tempS.shortestPathReduced(location, tempS.garage);
				}
			}
			
			// Set the final cost of the configuration.
			tempS.setCostValue(cost);
			
			// Replace the packages.
			for (ArrayList<Package> tempA : vehicles){
				for (Package returnPack : tempA){
					tempS.packages.add(returnPack);
				}
			}
			
			int temperature = 10000;
			
			// Search for a solution according to simulated annealing.
			State newState = null;
			int deltaE = 0;
			double probability = 0;
			double exponent = 0;
						
			while (this.getCostValue() > 0 && temperature > 0){
				newState = tempS.clone();
				int choicePackage = (int)Math.floor(Math.random()*newState.packages.size());
				int choiceVehicle = (int)Math.floor(Math.random()*newState.vehicles.size());
				Package tempPackage = newState.packages.get(choicePackage);
				ArrayList<Package>[] vehiclesCopy = (ArrayList<Package>[])(new ArrayList[tempS.vehicles.size()]);
				vehiclesCopy[choiceVehicle].remove(choicePackage);
				vehiclesCopy.
				newState.vehicles[choicePackage];
				deltaE = newState.getCostValue()-tempS.getCostValue();
				if (deltaE < 0){
					tempS = newState;
				}
				else {
					exponent = deltaE/temperature;
					probability = Math.pow(Math.E, exponent);
					if ((Math.random()*100) > probability){
						tempS = newState;
					}
				}
				temperature -= 1;
			}
			
			return tempS;
		}
		
		/**
		 * Main method for testing purposes.
		 * @param args - The arguments to the main method.
		 */
		public static void main(String[] args){
			// Test 0: Instantiate the state object.
			State state = new State(1, 1, 10);
			state.generateGraph("grid", true, 2, 5);
	
			// Test 1: Test simple methods.
			if (state.getCostValue() != 0){
				System.out.println("Error: getCostValue() does not return the correct result.");
			}
			state.incrementCost();
			if (state.getCostValue() != 1){
				System.out.println("Error: incrementCost() does not set the correct result.");
			}
			state.incrementCost(5);
			if (state.getCostValue() != 6){
				System.out.println("Error: incrementCost() does not set the correct result.");
			}
			state.setCostValue(0);
			if (state.getCostValue() != 0){
				System.out.println("Error: setCostValue() does not return the correct result.");
			}
			State tempS = state.clone();
			Package source = state.packages.get(0);
			state.moveVehicle(0, source.getSource());
			if (state.getCostValue() != 1 || !state.vehicles.get(0).getLocation().equals(source.getSource()) || state.vehicles.get(0).getPackage() == null){
				System.out.println("Error: moveVehicle() method does not correctly move the vehicle.");
			}
			state.moveVehicle(0, source.getDestination(), 2);
			if (state.getCostValue() != 3 || !state.vehicles.get(0).getLocation().equals(state.packages.get(0).getDestination()) || state.vehicles.get(0).getPackage() != null){
				System.out.println("Error: moveVehicle() method does not correctly move the vehicle.");
			}
			state = tempS;
	
			// Test 3: Test shortest path and heuristic methods.
			System.out.println("Shortest path: " + state.shortestPath(state.garage, state.packages.get(0).getSource()));
			System.out.println("Simple heuristic: " + state.getSimpleHeuristicValue());
			System.out.println("Advanced heuristic: " + state.getAdvancedHeuristicValue());
	
			// Test4: Test the clone and equals methods.
			State state2 = state.clone();
			if (!state2.equals(state)){
				System.out.println("Error: clone not equal to the original.");
			}
			state2.incrementCost();
			if (state2.equals(state)){
				System.out.println("Error: clone incorrectly equal to the original.");
			}
			state2.moveVehicle(0, state2.packages.get(0).getSource());
			state2.setCostValue(0);
			if (state2.equals(state)){
				System.out.println("Error: clone incorrectly equal to the original.");
			}
			state2.moveVehicle(0, state2.garage);
			state2.setCostValue(0);
			if (state2.equals(state)){
				System.out.println("Error: clone incorrectly equal to the original.");
			}
			State state3 = state.clone();
			Package tempP = state.packages.get(0).clone();
			state3.packages.add(tempP);
			if (state3.equals(state)){
				System.out.println("Error: clone incorrectly equal to the original.");
			} 
			State state4 = state.clone();
			Vehicle tempV = state.vehicles.get(0).clone();
			state4.vehicles.add(tempV);
			if (state3.equals(state)){
				System.out.println("Error: clone incorrectly equal to the original.");
			} 
			if (state3.equals(null)){
				System.out.println("Error: clone incorrectly equal to the original.");
			} 
	
			// Test 5: Test the compare to method.
			State state5 = state.clone();
			state5.setCostValue(1);
			if (!(state5.compareTo(state) > 0)){
				System.out.println("Error: compareTo() method incorrectly compares to states.");
			}
			state5.setCostValue(-1);
			if (!(state5.compareTo(state) < 0)){
				System.out.println("Error: compareTo() method incorrectly compares to states.");
			}
			state5.setCostValue(0);
			if (!(state5.compareTo(state) == 0)){
				System.out.println("Error: compareTo() method incorrectly compares to states.");
			}
	
			// Test 6: Test the is goal method.
			if (state.isGoal()){
				System.out.println("Error: isGoal() returns incorrect value.");
			}
			tempP = state.packages.get(0);
			state.moveVehicle(0, tempP.getSource());
			if (state.isGoal()){
				System.out.println("Error: isGoal() returns incorrect value.");
			}
			state.moveVehicle(0, tempP.getDestination());
			if (state.isGoal()){
				System.out.println("Error: isGoal() returns incorrect value.");
			}
			state.moveVehicle(0, state.garage);
			if (!state.isGoal()){
				System.out.println("Error: isGoal() returns incorrect value.");
			}
	
			// Test 2: Print a random graph.
	//		System.out.println(state.toString());
			
			// Test 8: Test the is valid move method.
			state = new State(2, 2, 10);
			state.generateGraph("grid", true, 2, 5);
			Package tempP1 = state.packages.get(0);
			state.moveVehicle(0, tempP1.getSource(), 1);
			if (state.isValidMove(0, state.garage)){
				System.out.println("Error: The isValidMove() returns the incorrect result.");	
			}
			if (state.isValidMove(1, tempP1.getSource())){
				System.out.println("Error: The isValidMove() returns the incorrect result.");	
			}
	
			// Test 7: Print the states resulting from the current state.
			state = tempS;
			for (State state6 : state.newStates()){
				System.out.println(state6.toString());
			}
	
	
			System.out.println("Tests complete.");
		}
	}
