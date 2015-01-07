package depObj;

import org.apache.bcel.generic.Type;

public class GenVarNode extends Node {
	
	private int sz = 0; // size in words
	private Type varType;
	
	public GenVarNode(String argName, int sz, Type t) {
		super(argName);
		this.sz = sz;
		varType = t; 
	}

	public int getSz() {
		return sz;
	}

	public void setSz(int sz) {
		this.sz = sz;
	}

	public Type getVarType() {
		return varType;
	}

	public void setVarType(Type varType) {
		this.varType = varType;
	}

}
