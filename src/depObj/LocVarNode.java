package depObj;

public class LocVarNode extends GenVarNode {

	public LocVarNode(String argName, int sz) {
		super(argName,sz);
	}

	public String toString() {
		String s = "LocVar\t";
		s += super.toString();
		return s;
	}
	
}
