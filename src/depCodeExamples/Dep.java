package depCodeExamples;

public class Dep {
	protected static int c;
//	public InternalClass ic = new InternalClass();	
	AA ObjA3;
	
	public void main(String[] args) {
		int a = 5; 
//		int b; 
		c = -3;
		AA ObjAA = new AA();
		AA ObjA1 = new AA();	
		AA ObjA2 = new AA();	
		ObjA3 = new AA();
		
//		ObjAA.foo();
//		b = ObjA1.mm3(c,a);
		
		start(a, ObjAA);
		ObjA1.m3();
		ObjA2.foo();
		ObjA3.foo();
//		
//		ic.i1();
	}

	private void start(int a, AA Obj) {
		int b=0;
		b = m5();
//		if(test()) {
//			a++;
//		}
		a = b * c;
////		a = 5 * c;
		m6(a);

//	
//		ObjAA.aa = b*c;
		if(a>1) {
		Obj.m3();//			ObjAA.foo();
		}
//		else {
//			bar(a,c);
//		}
	}
	
	public int m5() {
		return 6;
	}

	public void m6(int in) {
	}
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
