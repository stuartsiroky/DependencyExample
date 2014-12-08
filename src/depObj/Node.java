package depObj;

public class Node implements Comparable<Node> {

	final String name;
	public enum COLOR { WHITE,GRAY,BLACK };

	private COLOR color;// -1=white, 0=gray, 1=black
	private int distance; // -1 is infinity cant have neg distance	
	
	public Node(final String argName) {
		name = argName;
		color = COLOR.WHITE;
		distance = -1;
	}

	public int compareTo(final Node argNode) {
		return argNode == this ? 0 : -1;
	}

	public String getName() {
		return name;
	}

	public String toString() {
//		String s = " ";
		String s = name;
//		s += " | ("+getName()+")";//USE FOR DEBUG 
		//s += " Color "+color;
//		s += " Dist "+distance;
//		s += " ";
		return s;	
	}

	public boolean equals(Object n) {
		Node nn = (Node) n;
		//System.out.println("Equals "+name+" to "+nn.name+" |"+name.equals(nn.name));
		return name.equals(nn.name);
	}

	public COLOR getColor() {
		return color;
	}

	public void setColor(COLOR color) {
		this.color = color;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int i) {
		distance = i;
	}

	
}
