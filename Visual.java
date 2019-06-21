import java.awt.Dimension;

import javax.swing.JFrame;

import org.jgraph.JGraph;
import org.jgrapht.Graph;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.DefaultEdge;

public class Visual extends JFrame{
	
	// The default serial ID.
	private static final long serialVersionUID = 1L;

	// Store the size of the window.
	private Dimension size = new Dimension(800, 800);
	
	// Store the graph adapter needed by JGraph.
	private JGraphModelAdapter<Pair<Integer, Integer>, DefaultEdge> adapter;
	
	// Store the visualized graph itself.
	private JGraph graph;

	/**
	 * Construct the visual.
	 */
	public Visual(Graph<Pair<Integer, Integer>, DefaultEdge> graph){
		// Initialize the window. 
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setSize(this.size);
		
		// Initialize the visualization.
		this.adapter = new JGraphModelAdapter<Pair<Integer, Integer>, DefaultEdge>(graph);
		this.graph = new JGraph(this.adapter);
		this.getContentPane().add(this.graph);
		
		// Make the window visible.
		this.setVisible(true);
	}
}
