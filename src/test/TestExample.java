package test;

import java.util.ArrayList;
import java.util.Set;

import codeGraph.CodeGraph;
import codeGraph.CodeGraphGenerator;
import depObj.Edge;
import depObj.MethodNode;
import depObj.Node;

public class TestExample {
	static long startTime;
	static long stopTime;
	static long origStartTime;
	static long totalTime;
	
	public static void main(String[] args) {
		System.out.println("==========================");
		System.out.println("========== Test 1 ========");
		System.out.println("==========================");
		Test1();
		System.out.println("==========================");
		System.out.println("==========================\n\n");

		System.out.println("==========================");
		System.out.println("========== Test 2 ========");
		System.out.println("==========================");
		//Test2();
		System.out.println("==========================");
		System.out.println("==========================\n\n");

		System.out.println("==========================");
		System.out.println("========== Test 3 ========");
		System.out.println("==========================");
		//Test3();
		System.out.println("==========================");
		System.out.println("==========================\n\n");

	
	}

	
	public static void Test1() {
		CodeGraphGenerator GG = new CodeGraphGenerator();
		GG.setDebugLevel(6);
		String[] fileList = new String[2];
		fileList[0] = "depCodeExamples.Dep";
		fileList[1] = "depCodeExamples.AA";
		//fileList[2] = "depCodeExamples.Dep$InternalClass";
		
		GG.createGraph(fileList);

		//System.out.println(GG.callGraphToString());
		System.out.println(GG.depGraphToString());
		String initialNodeName = "{ depCodeExamples.Dep.main([Ljava/lang/String;)V.this } depCodeExamples.Dep.start(ILdepCodeExamples/AA;)V";
		String finalNodeName = "{ depCodeExamples.Dep.start(ILdepCodeExamples/AA;)V.Obj } depCodeExamples.AA.m8()I";
//		String finalNodeName = "{ depCodeExamples.Dep.main([Ljava/lang/String;)V.ObjA1 } depCodeExamples.AA.m9()V";
//		String finalNodeName = "{ depCodeExamples.AA.m3()V.this } depCodeExamples.AA.foo()V";
		CodeGraph ReducedGraph = GG.getReducedGraph(initialNodeName, finalNodeName);
		//System.out.println(ReducedGraph.toString());
		//System.out.println(GG.depGraphToString());	
		MethodNode fNode = (MethodNode) GG.depGraph.getNode(finalNodeName);
		ArrayList <MethodNode> mnList = GG.depGraph.getMethodDep(fNode);
		System.out.println(fNode.toString()+" depends on:");
		for(Node n: mnList) {
			System.out.println("\t"+n.toString());
		}	

		ArrayList<MethodNode> skipList = GG.getSkipMethodList(fNode);
		System.out.println("==== Skip Method Nodes ====");
		int numSkippedMethods=0;
		for(MethodNode m: skipList) {
			numSkippedMethods++;
			System.out.println("\t"+m.toString());
		}
		System.out.println("==== Number skipped methods : "+numSkippedMethods);
	}
	
	private static void Test2() {
		CodeGraphGenerator GG = new CodeGraphGenerator();
		GG.setDebugLevel(2);
		String[] fileList = new String[17];

		fileList[0] = "calc.controller.AbstractController";
		fileList[1] = "calc.controller.CalculatorController";
		fileList[2] = "calc.model.AbstractModel";
		fileList[3] = "calc.model.CalculatorModel";
		fileList[4] = "calc.model.ModelEvent";
		fileList[5] = "calc.noSwing.ActionEvent";
		fileList[6] = "calc.noSwing.BorderLayout";
		fileList[7] = "calc.noSwing.Container";
		fileList[8] = "calc.noSwing.GridLayout";
		fileList[9] = "calc.noSwing.JButton";
		fileList[10] = "calc.noSwing.JPanel";
		fileList[11] = "calc.noSwing.JTextField";
		fileList[12] = "calc.noSwing.MyJFrame";
		fileList[13] = "calc.view.JFrameView";
		fileList[14] = "calc.view.GridLayout";
		fileList[15] = "calc.view.CalculatorView";
		fileList[16] = "calc.view.CalculatorView$Handler";
		startTime = System.currentTimeMillis();		
		origStartTime = startTime;
	
		GG.createGraph(fileList);	

	
		String initialNodeName = "{ calc.view.CalculatorView.equals(Lcalc/view/CalculatorView;)V.this } calc.view.CalculatorView.equals(Lcalc/view/CalculatorView;)V";
		String finalNodeName = "{ calc.view.CalculatorView.modelChanged(Lcalc/model/ModelEvent;)V.this } calc.view.CalculatorView.modelChanged(Lcalc/model/ModelEvent;)V";
		//System.out.println(GG.callGraphToString());
		CodeGraph ReducedGraph = GG.getReducedGraph(initialNodeName, finalNodeName);

		System.out.println("START REDUCED GRAPH ====================== ");
		System.out.println(ReducedGraph.toString());
		System.out.println("====================== END REDUCED GRAPH ");

		MethodNode fNode = (MethodNode) GG.depGraph.getNode(finalNodeName);
		ArrayList <MethodNode> mnList = GG.depGraph.getMethodDep(fNode);
		System.out.println(fNode.toString()+" depends on:");
		for(Node n: mnList) {
			System.out.println("\t"+n.toString());
		}	

		ArrayList<MethodNode> skipList = GG.getSkipMethodList(fNode);
		System.out.println("==== Skip Method Nodes ====");
		int numSkippedMethods=0;
		for(MethodNode m: skipList) {
			numSkippedMethods++;
			System.out.println("\t"+m.toString());
		}
		System.out.println("==== Number skipped methods : "+numSkippedMethods);
		stopTime = System.currentTimeMillis();
		
		totalTime = stopTime - origStartTime;
		
		System.out.println("=========================");
		System.out.println("=========================\n");
		System.out.println("\nTotal Time           : "+totalTime);
	}

	private static void Test3() {
		CodeGraphGenerator GG = new CodeGraphGenerator();
		GG.setDebugLevel(6);
		String[] fileList = new String[16];

		long graphGenTime;
		long graphReduceTime;
		long graphPathTime;

		fileList[0] = "falsecalc.controller.AbstractController";
		fileList[1] = "falsecalc.controller.CalculatorController";
		fileList[2] = "falsecalc.model.AbstractModel";
		fileList[3] = "falsecalc.model.CalculatorModel";
		fileList[4] = "falsecalc.model.ModelEvent";
		fileList[5] = "falsecalc.noSwing.ActionEvent";
		fileList[6] = "falsecalc.noSwing.BorderLayout";
		fileList[7] = "falsecalc.noSwing.Container";
		fileList[8] = "falsecalc.noSwing.GridLayout";
		fileList[9] = "falsecalc.noSwing.JButton";
		fileList[10] = "falsecalc.noSwing.JPanel";
		fileList[11] = "falsecalc.noSwing.JTextField";
		fileList[12] = "falsecalc.noSwing.MyJFrame";
		fileList[13] = "falsecalc.view.JFrameView";
		fileList[14] = "falsecalc.view.CalculatorView";
		fileList[15] = "falsecalc.view.CalculatorView$Handler";
		startTime = System.currentTimeMillis();		
		origStartTime = startTime;
	
		GG.createGraph(fileList);	

		String initialNodeName = "{  } falsecalc.view.CalculatorView.equals(Lfalsecalc/view/CalculatorView;)V";
		String finalNodeName = "{ calc.view.CalculatorView.modelChanged(Lcalc/model/ModelEvent;)V.this } falsecalc.view.CalculatorView.modelChanged(Lfalsecalc/model/ModelEvent;)V";
System.out.println(GG.callGraphToString());

CodeGraph ReducedGraph = GG.getReducedGraph(initialNodeName, finalNodeName);
		System.out.println(ReducedGraph.toString());
		
		System.out.println("=====================");
	}



	
} // end TestExample
