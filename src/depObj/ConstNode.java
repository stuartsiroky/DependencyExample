package depObj;

public class ConstNode extends GenVarNode {

	public ConstNode(String argName, int sz) {
		super(argName,sz);
	}

	public String toString() {
		String s = "Const\t";
		s += super.toString();
		return s;
	}
	
}
