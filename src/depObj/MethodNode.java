package depObj;

public class MethodNode extends Node {

	private String inst;
	private String meth;
	
	public MethodNode(String methName, String inst) {
		super("{ "+inst+" } "+methName);
		this.inst = inst;
		this.meth = methName;
	}

	public String toString() {
		String s = "Method\t";
		s += super.toString();
		return s;
	}

	public String getInstName() {
		return inst;
	}

	public String getMethodName() {
		return meth;
	}
	
	public boolean instEqual(Object n) {
		MethodNode mn = (MethodNode) n;
		return inst.equals(mn.getInstName());
	}
	
	public boolean methodsEqual(Object n) {
		MethodNode mn = (MethodNode) n;
		return meth.equals(mn.getMethodName());
	}
	

}
