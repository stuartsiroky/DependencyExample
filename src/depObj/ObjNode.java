package depObj;

public class ObjNode extends GenVarNode {

	String instName;
	
	public ObjNode(String argName, int sz) {
		super(argName,sz);
		setInstanceName(argName);
	}

	public String toString() {
		String s = "ObjVar\t";
		s += super.toString();
		return s;
	}
	
	private void setInstanceName(String name) {
		instName = name;//FIXME
	}
}
