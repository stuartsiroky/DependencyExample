package depCodeExamples;

public class Dep {
	protected static int c;
//	public InternalClass ic = new InternalClass();	
	
	public void main(String[] args) {
//		int a = 5; 
//		c = -3;
		AA ObjAA = new AA();
		AA ObjA1 = new AA();	
		
		ObjAA.foo();
		ObjA1.m3();
		
//		start(a, ObjAA);
//		
//		ic.i1();
	}

//	private void start(int a, AA ObjAA) {
//		int b;
//		b = m5();
//		if(test()) {
//			a++;
//		}
//		a = b * c;
////		a = 5 * c;
//		m6(a);
//	
//		ObjAA.aa = b*c;
//		if(a>1) {
////		if(ObjAA.aa>1) {
//			ObjAA.foo();
//			a++;
//		}
//		else {
//			bar(a,c);
//		}
//	}
//	
//	public int m5() {
//		return 6;
//	}
//
//	public void m6(int in) {
//	}
//	
//	public void bar(int aa, int cc) {
//	}
//	
//	public boolean test() {
//		return true;
//	}
//	
//	
//	public class InternalClass {
//		public void i1() {
//			m6(5);
//		}
//	}
}
