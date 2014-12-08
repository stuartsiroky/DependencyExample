package depObj;

public class Edge implements Comparable<Edge> {

	final Node from;
	private final Node to;

	public Edge(final Node argFrom, final Node argTo){
		from = argFrom;
		to = argTo;
	}

	public Node getTo() {
		return to;
	}

	public Node getFrom() {
		return from;
	}

	public String toString() {
		return from.toString()+" -> "+to.toString();
	}

	@Override
	public boolean equals(Object e) {
		Edge ee = (Edge) e;
		return from.equals(ee.getFrom()) && to.equals(ee.getTo());
	}

	@Override
	public int compareTo(Edge o) {
		System.out.println("Edge CompareTo");
		return  0;//from.equals(o.getFrom()) && to.equals(o.getTo());
	}	
}