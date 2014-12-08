package depObj;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Collection;

import depObj.Node;
import depObj.Edge;

public class AdjacencyList {

	private HashMap<Node, List<Edge>> adjacencies = new HashMap<Node, List<Edge>>();
	
	public void addEdge(Node source, Node target){
		List<Edge> list;
		if(!adjacencies.containsKey(source)){
			list = new ArrayList<Edge>();
			adjacencies.put(source, list);
		}
		else {
			list = adjacencies.get(source);
		}
		Edge e = new Edge(source,target);
		if(!list.contains(e)) {
			list.add(e);
		}	
	}

	public void remove(Edge edge) {
		List<Edge> list;
		Node from = edge.getFrom();
		if(adjacencies.containsKey(from)){
			list = adjacencies.get(from);
			list.remove(edge);
			adjacencies.put(from, list);
		}
	}
	
	public List<Edge> getAdjacent(Node source){
		if(adjacencies.containsKey(source)) {
			return adjacencies.get(source);
		}
		else {
			return new ArrayList<Edge>();
		}
	}

	public Set<Node> getSourceNodeSet(){
		return adjacencies.keySet();
	}

	public Collection<Edge> getAllEdges(){
		List<Edge> edges = new ArrayList<Edge>();
		for(List<Edge> e : adjacencies.values()){
			edges.addAll(e);
		}
		return edges;
	}

	public boolean containsNode(Node n) {
		return adjacencies.containsKey(n);
	}

	public AdjacencyList getReversedList(){
		AdjacencyList newlist = new AdjacencyList();
		for(List<Edge> edges : adjacencies.values()){
			for(Edge e : edges){
				newlist.addEdge(e.getTo(), e.getFrom());
			}
		}
		return newlist;
	}
	
	public String toString() {
		String out = "";
		Set<Node> sourceNodes;
		sourceNodes = getSourceNodeSet();
		for (Node n : sourceNodes) {
			List<Edge> toEdges;
			toEdges = getAdjacent(n);
			for (Edge e : toEdges) {
				out += "\t" + e.toString() + "\n";
			}
			out += "\n";
		}
		return out;
	}
}
