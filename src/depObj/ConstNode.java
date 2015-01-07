package depObj;

import org.apache.bcel.generic.Type;

public class ConstNode extends GenVarNode {

	public ConstNode(String argName, int sz) {
		super(argName,sz,Type.NULL);
	}

	public String toString() {
		String s = "Const\t";
		s += super.toString();
		return s;
	}
	
}
