package depCodeExamples;

public class AA {
	int aa;
	
	public void foo() {
		int b = 0;
		switch(aa) {
		case 0: aa = 0; break;
		case 1: m2(); break;
		case 2: aa = b--;
		case 3: aa = aa*aa; break;
		case 4: ;break;
		}	
	}
	
	public void m2() {
		
	}
	
	public void m3() {
		foo();
	}
}
