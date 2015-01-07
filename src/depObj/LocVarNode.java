package depObj;

import org.apache.bcel.generic.Type;

public class LocVarNode extends GenVarNode {

	public LocVarNode(String argName, int sz, Type t) {
		super(argName,sz,t);
	}

	public String toString() {
		String s = "LocVar\t";
		s += super.toString();
		return s;
	}
	
}
