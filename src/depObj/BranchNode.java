package depObj;

public class BranchNode extends Node {

	private int startIndx;
	private int endIndx;
	private int targets[];
	
	public BranchNode(String argName, int start, int end, int targets[]) {
		super(argName);
		startIndx = start;
		endIndx = end;
		this.targets = targets;
	}

	public String toString() {
		String s = "Branch\t";
		s += super.toString();
		return s;
	}

	public int getStartIndx() {
		return startIndx;
	}

	public void setStartIndx(int startIndx) {
		this.startIndx = startIndx;
	}

	public int getEndIndx() {
		return endIndx;
	}

	public void setEndIndx(int endIndx) {
		this.endIndx = endIndx;
	}

	public int[] getTargets() {
		return targets;
	}

	public void setTargets(int[] targets) {
		this.targets = targets;
	}
	
}
