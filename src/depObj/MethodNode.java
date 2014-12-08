package depObj;

public class MethodNode extends Node {

	public MethodNode(String argName) {
		super(argName);
		// TODO Auto-generated constructor stub
	}

	public String toString() {
		String s = "Method\t";
		s += super.toString();
		return s;
	}
	
}
