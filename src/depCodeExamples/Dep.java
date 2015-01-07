package depCodeExamples;

public class Dep {
	public void main(String[] args) {
		AA ObjAA = new AA();
		int b=8; 
	
		
//		ObjAA.aa = m7();
		ObjAA.m9();
		start(b,ObjAA);
		
	}


	private void start(int a, AA Obj) {
		Obj.m8();
	}
	
	public int m7() {
		return 5;
	}

	
	
	
//	protected static int c;
////	public InternalClass ic = new InternalClass();	
////	AA ObjA3;
//	
//	public void main(String[] args) {
//		int a = 5; 
//		int b=6; 
//		c = -3;
//		AA ObjAA = new AA();
////		AA ObjA1 = new AA();	
////		AA ObjA2 = new AA();	
////		ObjA3 = new AA();
//		
////		ObjAA.foo();
////		b = ObjA1.mm3(c,a);
//		ObjAA.aa = m7();
////		start(a, ObjAA);
//		
////		ObjA1.m9();
////		ObjA1.m3();
//		//ObjA2.foo();
//		//ObjA3.foo();	
////		
////		ic.i1();
//	}
//
////	private void start(int a, AA Obj) {
//////		int b=0;
//////		b = m5();
//////		if(test()) {
//////			a++;
//////		}
//////		a = b * c;
////////		a = 5 * c;
//////		m6(a);
//////		Obj.m8(a, c);
//////	
//////		ObjAA.aa = b*c;
//////		if(a>1) {
//////		Obj.m3();//			ObjAA.foo();
//////		}
//////		else {
//////			bar(a,c);
//////		}
////	}
//	
////	public int m5() {
////		return 6;
////	}
////
//	public int m7() {
//		return 5;
//	}
////	
////	public void m6(int in) {
////	}
////	
////	public void bar(int aa, int cc) {
////	}
////	
////	public boolean test() {
////		return true;
////	}
////	
////	
////	public class InternalClass {
////		public void i1() {
////			m6(5);
////		}
////	}
}
