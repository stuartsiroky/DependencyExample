package depObj;

public class VarNode extends GenVarNode {
	
	public VarNode(String argName, int sz) {
		super(argName,sz);
	}

	public String toString() {
		String s = "Var\t";
		s += super.toString();
		return s;
	}
	
}
