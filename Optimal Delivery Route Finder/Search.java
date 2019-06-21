import java.util.PriorityQueue;
import java.util.TreeSet;

/**
 * An object in which to store the implementations of the search algorithms.
 * @author Samuel
 */
public class SimulatedAnnealingSearch {
	
	/**
	 * A simulated annealing working on a reduced graph.
	 * @param state - The initial state of the problem.
	 * @return - The cost of the solution.
	 */
	public Integer simulatedAnnealingReduced(State state){
				
		// Initialize the parameters of the search.
		State currentState = state.generateSimulatedAnnealingState();
		
		return currentState.getCostValue();
	}
	
	/**
	 * Perform the specified search on the specified state.
	 * @param type - The type of state to perform.
	 * @param reduced - True if the search is to use a reduced map.
	 * @param state - The initial state from which to start the search.
	 */
	public int performSearch(String type, boolean reduced, State state){
		// Initialize variables to check.
		System.out.println("test1");
		String aStar = "aStar";
		String iterativeDeepening = "iterativeDeepening";
		String simulatedAnnealing = "simulatedAnnealing";
		int result = 0;
		
		// Determine which search to perform.
		if (type.equals(aStar) && !reduced){
			result = this.aStarSearch(state);
		}
		else if (type.equals(iterativeDeepening) && !reduced){
			result = this.iterativeDeepeningSearch(state);
		}
		else if (type.equals(aStar) && reduced){
			result = this.aStarSearchOnReduced(state);
		}
		else if (type.equals(iterativeDeepening) && reduced){
			result = this.iterativeDeepeningSearchOnReduced(state);
		}
		else if (type.equals(simulatedAnnealing) && reduced){
			result = this.simulatedAnnealingReduced(state);
		}
		
		return result;
	}

	/**
	 * Main method for testing.
	 * @param args - The arguments to the main testing method.
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args){
		// Instantiate the initial state.
		State state = new State(8, 8, 1000);

	
		int cols = 100;
		int rows = 10;

		// Generate a graph and print it.
		state.generateGraph("grid", true, rows, cols);
		//		System.out.println(state.toString());
		System.out.println(state.problemSize());
		
		// Print the reduced graph.
//		System.out.println(state.getReducedMap().toString());

		// Instantiate the search object.
		Search search = new Search();

		
		// Compute the result of a search on an initial state.
		// Commented search names for convenience: aStar iterativeDeepening simulatedAnnealing
		long time = System.currentTimeMillis();
		System.out.println("SA:");
		System.out.println("Result: " + search.performSearch("simulatedAnnealing", true, state));
		time = System.currentTimeMillis()-time;
		System.out.println("Time: " + time + "ms");
		
		time = System.currentTimeMillis();
//		System.out.println("ID: ");
//		System.out.println("Result: " + search.performSearch("iterativeDeepening", true, state));
//		time = System.currentTimeMillis()-time;
//		System.out.println("Time: " + time + "ms");	

//		time = System.currentTimeMillis();
//		System.out.println("A*: ");
//		System.out.println("Result: " + search.performSearch("aStar", true, state));
//		time = System.currentTimeMillis()-time;
//		System.out.println("Time: " + time + "ms");
		
		// Visualize the graph.
//		Visual visual = new Visual(state.getMap());
		
		// Store the sum of each algorithm and the number of trials.
		long saSum = 0;
		long idSum = 0;
		long asSum = 0;
		int numTrials = 1;	

//		
//		// Perform the trials. 
		time = 0;
		rows = 10;
		cols = 100;
		for (int n = 0; n < numTrials; n += 1){
//			// Generate the new graph.
			state.generateGraph("grid", true, rows, cols);
//			
//			// Simulated Annealing.
			time = System.currentTimeMillis();
			search.performSearch("simulatedAnnealing", true, state);
			saSum += System.currentTimeMillis()-time;
//			
//			// Iterative Deepening.
			time = System.currentTimeMillis();
			search.performSearch("iterativeDeepening", true, state);
			idSum += System.currentTimeMillis()-time;
//		
//			// A*.
			time = System.currentTimeMillis();
			search.performSearch("aStar", true, state);
			asSum += System.currentTimeMillis()-time;
//		}
//		
//		// Print the results of the trials. 
		double result = saSum/numTrials;
		System.out.println("SA: " + result);
//		result = idSum/numTrials;
//		System.out.println("ID: " + result);
//		result = asSum/numTrials;
//		System.out.println("A*: " + result);
	}
}
}
