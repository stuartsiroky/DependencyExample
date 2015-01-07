package depObj;

import org.apache.bcel.generic.Type;

public class VarNode extends GenVarNode {
	
	public VarNode(String argName, int sz, Type t) {
		super(argName,sz,t);
	}

	public String toString() {
		String s = "Var\t";
		s += super.toString();
		return s;
	}
	
}
