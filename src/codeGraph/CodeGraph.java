package codeGraph;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import depObj.AdjacencyList;
import depObj.Edge;
import depObj.Node;
import depObj.MethodNode;
import depObj.BranchNode;
import depObj.Node.COLOR;
import depObj.VarNode;
import depObj.LocVarNode;

public class CodeGraph {
	int debugLevel = 2;

	ArrayList<Node> nodeList = new ArrayList<Node>();
	AdjacencyList adjList = new AdjacencyList();

	int nodeVisitCnt;
	int numNodesStarting;
	int numNodesRev;
	int numNodesReduced;

	private Queue<Node> workListQ = new LinkedList<Node>();
	int nodevisitcnt;

	public void addEdge(Node src, Node target) {
		adjList.addEdge(src, target);
	}

	private Node addNode(Node n) {
		if (!nodeList.contains(n)) {
			nodeList.add(n);
			return n;
		} else {
			return getNode(n.getName());
		}
	}

	public boolean strictAddNode(Node n) {
		if (!nodeList.contains(n)) {
			nodeList.add(n);
			return true;
		} else {
			return false;
		}
	}

	public MethodNode addMethodNode(String name) {
		MethodNode mn = new MethodNode(name);
		return (MethodNode) addNode(mn);
	}

	public BranchNode addBranchNode(String name, int start, int end,
			int targets[]) {
		BranchNode bn = new BranchNode(name, start, end, targets);
		return (BranchNode) addNode(bn);
	}

	public VarNode addVarNode(String name, int sz) {
		VarNode vn = new VarNode(name, sz);
		return (VarNode) addNode(vn);
	}

	public LocVarNode addLocVarNode(String name, int sz) {
		LocVarNode lvn = new LocVarNode(name, sz);
		return (LocVarNode) addNode(lvn);
	}

	public boolean removeNode(Node n) {
		return nodeList.remove(n);
	}

	public void removeEdge(Edge edge) {
		adjList.remove(edge);
	}

	public String toString() {
		String out = "";
		for (Node n : nodeList) {
			out += n.toString() + "\n";
		}
		out += "\n\n";
		out += adjList.toString();
		return out;
	}

	public Node getNode(String string) {
		Node n = new Node(string);

		if (nodeList.contains(n)) {
			int indx = nodeList.indexOf(n);
			return nodeList.get(indx);
		} else {
			return null;
		}
	}

	public List<Edge> getAdjacent(Node source) {
		return adjList.getAdjacent(source);
	}

	public ArrayList<MethodNode> getMethodDep(MethodNode startMethod) {
		ArrayList<MethodNode> methodList = new ArrayList<MethodNode>();
		// do a breath first search on the graph starting from
		// the given method node.
		if (nodeList.contains(startMethod)) {
			Queue<Node> q = new LinkedList<Node>();
			int cnt = 0;
			int methodCnt = 0;
			initSearch(startMethod);
			q.add(startMethod);
			while (!q.isEmpty()) {
				Node n = q.remove();
				if (n instanceof MethodNode) {
					methodList.add((MethodNode) n);
					methodCnt++;
				}
				cnt++;

				List<Edge> toList = adjList.getAdjacent(n);
				for (Edge e : toList) {
					Node v = e.getTo();
					if (v.getColor() == COLOR.WHITE) {
						v.setColor(COLOR.GRAY);
						v.setDistance(n.getDistance() + 1);
						q.add(v);
						workListQ.add(v);
					} // if WHITE
				} // for edge
				n.setColor(COLOR.BLACK);
			} // !q.isEmpty
			printDebug("BFSearch nodes visited = " + cnt + " Methods found = "
					+ methodCnt, 2);
		}
		// Get any <init> methods and add them to the list
		//addInitMethods(methodList);
		return methodList;
	}

	public void addInitMethods(ArrayList<MethodNode> methodList) {
		for (Node n : nodeList) {
			if (n instanceof MethodNode) {
				if (n.getName().contains("<init>") || n.getName().contains("<clinit>")) {
					methodList.add((MethodNode) n);
				}
			}
		}

	}

	public CodeGraph reduceGraph(Node initialNode, Node finalNode) {
		return searchRedGraph(finalNode, true).searchRedGraph(initialNode,
				false);
	}

	private CodeGraph searchRedGraph(Node node, boolean rev) {
		Queue<Node> q = new LinkedList<Node>();
		CodeGraph ncg = new CodeGraph();
		AdjacencyList cAdjList;
		printDebug("Number of node in starting graph " + nodeList.size(), 2);
		numNodesStarting = nodeList.size();
		int cnt = 0;
		initSearch(node);
		q.add(node);
		ncg.strictAddNode(node);
		if (rev) {
			cAdjList = adjList.getReversedList();
		} else {
			cAdjList = adjList;
		}

		while (!q.isEmpty()) {
			Node n = q.remove();
			cnt++;
			List<Edge> eList = cAdjList.getAdjacent(n);
			if (eList != null) {
				for (Edge e : eList) {
					Node v = e.getTo();
					if (v.getColor() == COLOR.WHITE) {
						v.setColor(COLOR.GRAY);
						v.setDistance(n.getDistance() + 1);
						q.add(v);
						ncg.strictAddNode(v);
					} // if WHITE

					if (rev) {
						ncg.addEdge(v, n);
					} else {
						ncg.addEdge(n, v);
					}
				} // for edge
			}// not null
			n.setColor(COLOR.BLACK);
		} // !q.isEmpty
		printDebug("SearchPredecessors nodes visited = " + cnt, 2);
		numNodesRev = cnt;
		printDebug(ncg.toString(), 2);
		return ncg;
	}

	private void initSearch(Node startNode) {
		for (Node n : nodeList) {
			n.setColor(COLOR.WHITE);
			n.setDistance(-1);
		}
		startNode.setColor(COLOR.GRAY);
		startNode.setDistance(0);
		workListQ.clear();
	}

	public void setDebugLevel(int debugLevel) {
		if (debugLevel < 0) {
			this.debugLevel = 0;
		} else {
			this.debugLevel = debugLevel;
		}
	}

	private void printDebug(String msg, int level) {
		if (debugLevel >= level) {
			System.out.println(msg);
		}
	}


	public ArrayList<Node> getNodeList() {
		return nodeList;
	}

}
