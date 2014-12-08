package depObj;

public class GenVarNode extends Node {

	private int sz = 0; // size in words
	
	public GenVarNode(String argName, int sz) {
		super(argName);
		this.sz = sz;
	}

	public int getSz() {
		return sz;
	}

	public void setSz(int sz) {
		this.sz = sz;
	}

}
