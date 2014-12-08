package codeGraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ACONST_NULL;
import org.apache.bcel.generic.ANEWARRAY;
import org.apache.bcel.generic.ARRAYLENGTH;
import org.apache.bcel.generic.ATHROW;
import org.apache.bcel.generic.ArithmeticInstruction;
import org.apache.bcel.generic.ArrayInstruction;
import org.apache.bcel.generic.BranchInstruction;
import org.apache.bcel.generic.CHECKCAST;
import org.apache.bcel.generic.CPInstruction;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.ConstantPushInstruction;
import org.apache.bcel.generic.ConversionInstruction;
import org.apache.bcel.generic.DCMPG;
import org.apache.bcel.generic.DCMPL;
import org.apache.bcel.generic.FCMPG;
import org.apache.bcel.generic.FCMPL;
import org.apache.bcel.generic.FieldInstruction;
import org.apache.bcel.generic.INSTANCEOF;
import org.apache.bcel.generic.INVOKEINTERFACE;
import org.apache.bcel.generic.IfInstruction;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.InvokeInstruction;
import org.apache.bcel.generic.LCMP;
import org.apache.bcel.generic.LDC;
import org.apache.bcel.generic.LDC2_W;
import org.apache.bcel.generic.LoadInstruction;
import org.apache.bcel.generic.LocalVariableGen;
import org.apache.bcel.generic.LocalVariableInstruction;
import org.apache.bcel.generic.MULTIANEWARRAY;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.NEW;
import org.apache.bcel.generic.NEWARRAY;
import org.apache.bcel.generic.RET;
import org.apache.bcel.generic.ReferenceType;
import org.apache.bcel.generic.ReturnInstruction;
import org.apache.bcel.generic.SWAP;
import org.apache.bcel.generic.Select;
import org.apache.bcel.generic.StackInstruction;
import org.apache.bcel.generic.StoreInstruction;
import org.apache.bcel.generic.Type;

import depObj.BranchNode;
import depObj.ConstNode;
import depObj.Edge;
import depObj.GenVarNode;
import depObj.LocVarNode;
import depObj.MethodNode;
import depObj.Node;
import depObj.VarNode;

public class CodeGraphGenerator {
	private int debugLevel = 0;
	private boolean create = true;
	// any interfaces (calls) found will be added to this list so we can replace
	// with actual implementation later.
	private ArrayList<String> interfaces_used = new ArrayList<String>();
	public CodeGraph depGraph = new CodeGraph();
	public CodeGraph callGraph = new CodeGraph();
	// when we encounter a class that implements an interface we will add that
	// here to help us
	// fix up the graph if any unimplemented interfaces are in the graph
	// the key is the interface and a list of what implements that interface
	private Map<String, List<String>> implemented_interfaces = new HashMap<String, List<String>>();
	// private String curClassName;
	private String curMethodName;
	private MethodNode curMethodNode;
	private Map<Integer, String> locVarHMap;
	private Stack<Node> locStack;
	private ArrayList<BranchNode> actBranch;

	public void createGraph(String[] fileList) {
		for (String f : fileList) {
			if (f != null) {
				try {
					parseClass(f);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		cleanupAnyPureInterface(callGraph);	
		cleanupAnyPureInterface(depGraph);
	}	
	
	public void parseClass(String className) throws ClassNotFoundException {

		JavaClass jc = Repository.lookupClass(className);

		ClassGen cg = new ClassGen(jc);
		ConstantPoolGen cpg = cg.getConstantPool();
		printDebug("====================================", 2);
		printDebug("== CLASS : " + cg.getClassName(), 2);
		printDebug("====================================\n", 2);
		// curClassName = cg.getClassName();

		getInterfaces(className,cg);
		extractFields(cg);
		extractMethods(cg);

		printDebug("=========== Instructions ===========\n", 2);
		for (Method m : cg.getMethods()) {
			actBranch = new ArrayList<BranchNode>();
			MethodGen mg = new MethodGen(m, cg.getClassName(), cpg);
			LocalVariableGen lvg[] = mg.getLocalVariables();
			InstructionList il = mg.getInstructionList();
			InstructionHandle[] handles = il.getInstructionHandles();

			curMethodName = printMethodName(cg, m);
			curMethodNode = depGraph.addMethodNode(curMethodName);
			@SuppressWarnings("unused")
			boolean newAddition = callGraph.strictAddNode(curMethodNode);

			printDebug("==== " + curMethodName + " ====", 2);

			extractMethodVariables(cg, m, lvg);
			if (!(curMethodName.contains("<init>") || curMethodName.contains("<clinit>"))) {
				// ignore the initialization since they will all be included
				decodeInstructions(cpg, handles);
			}

			printDebug("\n", 2);
		} // methods
		printDebug("==============================\n", 2);
	} // createCFG

	private void extractFields(ClassGen cg) {
		Field fs[] = cg.getFields();
		printDebug("=========== Fields ===========", 2);
		for (Field f : fs) {
			printDebug("  " + f.toString(), 2);
			String name = cg.getClassName() + "." + f.getName();
			@SuppressWarnings("unused")
			VarNode vn = depGraph.addVarNode(name, f.getType().getSize());
		}
		printDebug("==============================\n", 2);
	}

	private void extractMethods(ClassGen cg) {
		printDebug("=========== Methods ===========", 2);
		for (Method m : cg.getMethods()) {
			locStack = new Stack<Node>();
			String NodeName = printMethodName(cg, m);
			printDebug("  " + NodeName, 2);
			MethodNode mn = depGraph.addMethodNode(NodeName);
			@SuppressWarnings("unused")
			boolean newAdditon = callGraph.strictAddNode(mn);
		}
		printDebug("==============================\n", 2);
	}

	private void extractMethodVariables(ClassGen cg, Method m,
			LocalVariableGen[] lvg) {
		locVarHMap = new HashMap<Integer, String>();
		for (LocalVariableGen l : lvg) {
			String mName = printMethodName(cg, m);
			printDebug("LocVar " + l.toString() + ":" + l.getIndex(), 2);
			@SuppressWarnings("unused")
			LocVarNode lvn = depGraph.addLocVarNode(mName + "." + l.getName(),
					l.getType().getSize());
			locVarHMap.put(l.getIndex(), mName + "." + l.getName());
		}
	}

	private void decodeInstructions(ConstantPoolGen cpg,
			InstructionHandle[] handles) {
		for (InstructionHandle ih : handles) {
			Instruction insn = ih.getInstruction();

			if( insn instanceof ACONST_NULL) {
				decodeNULL(cpg,ih,insn);
			} else if (insn instanceof ArithmeticInstruction) {
				decodeArithmeticInstruction(cpg, ih, insn);
			} else if (insn instanceof BranchInstruction) {
				decodeBranchInstruction(cpg, ih, insn);
			} else if (insn instanceof ConversionInstruction) {
				decodeConversionInstruction(cpg, ih, insn);
			} else if (insn instanceof CPInstruction) {
				if (insn instanceof InvokeInstruction) {
					decodeInvokeInstruction(cpg, ih, insn);
				} else if (insn instanceof FieldInstruction) {
					decodeFieldInstruction(cpg, ih, insn);
				} else {
					decodeObjAlocInstruction(cpg, ih, insn);
				}
			} else if (insn instanceof LocalVariableInstruction) {
				// includes IINC but do nothing in this case
				decodeLDSTInstruction(cpg, ih, insn);
			} else if (insn instanceof StackInstruction) {
				decodeStackInstruction(cpg, ih, insn);
			} else if (insn instanceof ReturnInstruction) {
				decodeReturnInstruction(cpg, ih, insn);
			} else if (insn instanceof ConstantPushInstruction) {
				decodeConstInstruction(cpg, ih, insn);
			} else {
				decodeOtherInstruction(cpg, ih, insn);
			}
		}
	}

	private void decodeNULL(ConstantPoolGen cpg, InstructionHandle ih,
			Instruction insn) {
		printInstruction("NULL    ", ih, insn);
		printDebug("\tcstack " + insn.consumeStack(cpg), 4);
		printDebug("\tpstack " + insn.produceStack(cpg), 4);
		if (create) {
			ConstNode cn = new ConstNode(insn.toString(), insn.produceStack(cpg));
			locStack.push(cn);
			printDebug("PUSH NULL " + insn.toString(), 3);
		}		
	}

	private void decodeArithmeticInstruction(ConstantPoolGen cpg,
			InstructionHandle ih, Instruction insn) {
		ArithmeticInstruction ai = (ArithmeticInstruction) insn;
		printInstruction("Arith  ", ih, insn);
		printDebug("\tcstack " + ai.consumeStack(cpg), 4);
		printDebug("\tpstack " + ai.produceStack(cpg), 4);
		if (create) {
			Node depNode = null;
			if (ai.produceStack(cpg) > 0) {
				InstructionHandle nxih = ih.getNext();
				Instruction ni = nxih.getInstruction();
				if (ni instanceof FieldInstruction) {
					FieldInstruction fi = (FieldInstruction) ni;
					ReferenceType firt = fi.getReferenceType(cpg);
					depNode = depGraph.getNode(firt.toString() + "."
							+ fi.getFieldName(cpg));
					if (depNode == null) {
						depNode = depGraph.addVarNode(firt.toString() + "."
								+ fi.getFieldName(cpg), fi.getType(cpg)
								.getSize());
					}
				} else if (ni instanceof StoreInstruction) {
					StoreInstruction sti = (StoreInstruction) ni;
					if (create) {
						depNode = depGraph.getNode(locVarHMap.get(sti
								.getIndex()));
					}
				}
			}
			int cstack = ai.consumeStack(cpg);
			while (cstack > 0) {
				GenVarNode n = (GenVarNode) locStack.pop();
				cstack -= n.getSz();
				if (!(n instanceof ConstNode)) {
					depGraph.addEdge(depNode, n);
				}
			}
		}
	}

	private void decodeBranchInstruction(ConstantPoolGen cpg,
			InstructionHandle ih, Instruction insn) {
		BranchInstruction bi = (BranchInstruction) insn;
		printInstruction("Branch ", ih, insn);
		printDebug("\tcstack " + bi.consumeStack(cpg), 4);
		printDebug("\tpstack " + bi.produceStack(cpg), 4);
		int bEndIndx = 0;
		int indc[] = null;
		if (bi instanceof IfInstruction) {
			printDebug("\ttarget     " + bi.getTarget().toString(), 4);
			printDebug("\ttarget     " + ih.getNext().toString(), 4);
			indc = new int[2];
			indc[0] = ih.getNext().getPosition();
			indc[1] = bi.getTarget().getPosition();
			// look at the instruction just before the target to determine the
			// end
			InstructionHandle fih = bi.getTarget().getPrev();
			if (fih.getInstruction() instanceof BranchInstruction) {
				BranchInstruction bbi = (BranchInstruction) fih
						.getInstruction();
				bEndIndx = bbi.getTarget().getPosition();
			} else {
				bEndIndx = bi.getTarget().getPosition();
			}
		} else if (bi instanceof Select) {
			Select si = (Select) bi;
			bEndIndx = ih.getPosition();
			indc = new int[si.getIndices().length];
			InstructionHandle t_list[] = si.getTargets();
			InstructionHandle fih = t_list[t_list.length - 1].getPrev();
			if (fih.getInstruction() instanceof BranchInstruction) {
				BranchInstruction bbi = (BranchInstruction) fih
						.getInstruction();
				bEndIndx = bbi.getTarget().getPosition();
			} else {
				bEndIndx = bi.getTarget().getPosition();
			}
			int cnt = 0;
			for (InstructionHandle h : t_list) {
				indc[cnt] = h.getPosition();
				cnt++;
				printDebug("\ttarget     " + h.toString(), 4);
			}
		} else {
			printDebug("\ttarget     " + bi.getTarget().toString(), 4);
			indc = new int[1];
			indc[0] = bi.getTarget().getPosition();
			bEndIndx = ih.getPosition();
		}
		for (int i : indc) {
			printDebug("\tindicies " + i, 4);
		}

		String bName = curMethodName + "." + ih.getPosition() + "_"
				+ bi.toString(cpg.getConstantPool());
		if (create) {
			BranchNode bNode = depGraph.addBranchNode(bName, ih.getPosition(),

			bEndIndx, indc);
			actBranch.add(bNode);
			int cstack = bi.consumeStack(cpg);
			while (cstack > 0) {
				Node n = locStack.pop();
				printDebug("POP " + n.toString(), 3);
				if (n instanceof GenVarNode) {
					GenVarNode gvn = (GenVarNode) n;
					cstack -= gvn.getSz();
				} else if (n instanceof MethodNode) {
					cstack--;
				}

				if (!(n instanceof ConstNode)) {
					depGraph.addEdge(bNode, n);
				}
			}
		}
	}

	private void decodeConversionInstruction(ConstantPoolGen cpg,
			InstructionHandle ih, Instruction insn) {
		printInstruction("CONV    ", ih, insn);
		ConversionInstruction ci = (ConversionInstruction) insn;
		printDebug("\tcstack      " + ci.consumeStack(cpg), 4);
		printDebug("\tpstack      " + ci.produceStack(cpg), 4);
	}

	private void decodeInvokeInstruction(ConstantPoolGen cpg,
			InstructionHandle ih, Instruction insn) {
		InvokeInstruction ii = (InvokeInstruction) insn;
		ReferenceType rt = ii.getReferenceType(cpg);
		String cName = rt.toString();
		String methodName = ii.getMethodName(cpg);
		String signatureName = ii.getSignature(cpg);
		String tNodeName = cName + "." + methodName + signatureName;
		printInstruction("Method ", ih, insn);
		tNodeName = ii.getReturnType(cpg) + " " + tNodeName;
		if (ii instanceof INVOKEINTERFACE) {
			interfaces_used.add(tNodeName);
		}

		Type t[] = ii.getArgumentTypes(cpg);
		int numArgs = t.length;
		printDebug("\tcstack      " + ii.consumeStack(cpg), 4);
		printDebug("\tpstack      " + ii.produceStack(cpg), 4);
		printDebug("\tname        " + tNodeName, 2);
		if (create) {
			MethodNode tNode = depGraph.addMethodNode(tNodeName);
			@SuppressWarnings("unused")
			boolean newAddition = callGraph.strictAddNode(tNode);
			depGraph.addEdge(tNode, curMethodNode);
			callGraph.addEdge(curMethodNode, tNode);
			ArrayList<BranchNode> bl = getCurBranches(ih.getPosition());
			for (BranchNode bn : bl) {
				depGraph.addEdge(tNode, bn);
			}
			int cstack = ii.consumeStack(cpg);
			if (ii.produceStack(cpg) > 0) {
				locStack.push(tNode);
				printDebug("PUSH Mnode " + tNode.toString(), 3);
				InstructionHandle nxih = ih.getNext();
				Instruction ni = nxih.getInstruction();
				if (ni instanceof FieldInstruction) {
					FieldInstruction fi = (FieldInstruction) ni;
					ReferenceType firt = fi.getReferenceType(cpg);
					Node n = depGraph.getNode(firt.toString() + "."
							+ fi.getFieldName(cpg));
					depGraph.addEdge(n, tNode);
				} else if (ni instanceof StoreInstruction) {
					StoreInstruction sti = (StoreInstruction) ni;
					LocVarNode n = (LocVarNode) depGraph.getNode(locVarHMap
							.get(sti.getIndex()));
					depGraph.addEdge(n, tNode);
				}
			}
			if (cstack > 0) {
				while (numArgs > 0) {
					Node pNode = locStack.pop();
					printDebug("POP node " + pNode.toString(), 3);
					if (!(pNode instanceof ConstNode)) {
						depGraph.addEdge(tNode, pNode);
					}
					numArgs--;
				}
			}
		}
	}

	private void decodeFieldInstruction(ConstantPoolGen cpg,
			InstructionHandle ih, Instruction insn) {
		FieldInstruction fi = (FieldInstruction) insn;
		printInstruction("Field  ", ih, insn);
		printDebug("\tcstack " + fi.consumeStack(cpg), 4);
		printDebug("\tpstack " + fi.produceStack(cpg), 4);
		ReferenceType rt = fi.getReferenceType(cpg);
		if (create) {
			VarNode vn = depGraph.addVarNode(

			rt.toString() + "." + fi.getFieldName(cpg), fi.getFieldType(cpg)
					.getSize());
			if (fi.consumeStack(cpg) > 0) {
				showPOP("POP field ");
			}
			if (fi.produceStack(cpg) > 0) {
				locStack.push(vn);
				printDebug("PUSH field" + vn.toString(), 3);
			}
		}
	}

	private void decodeObjAlocInstruction(ConstantPoolGen cpg,
			InstructionHandle ih, Instruction insn) {
		CPInstruction cpi = (CPInstruction) insn;
		printInstruction("ObjAloc ", ih, insn);
		printDebug("\tcstack " + cpi.consumeStack(cpg), 4);
		printDebug("\tpstack " + cpi.produceStack(cpg), 4);
		if (cpi instanceof NEW) {
			NEW ni = (NEW) cpi;
			printDebug("\tNEW", 2);
			if (create) {
				ConstNode cn = new ConstNode(ni.toString(),
						ni.produceStack(cpg));
				locStack.push(cn);
				printDebug("PUSH Const " + cn.toString(), 3);
			}
		} else if (cpi instanceof LDC) {
			LDC ldci = (LDC) cpi;
			printDebug("\ttype  " + ldci.getValue(cpg), 3);
			printDebug("\tLDC", 2);
		} else if (cpi instanceof LDC2_W) {
			LDC2_W ldci = (LDC2_W) cpi;
			printDebug("\ttype  " + ldci.getValue(cpg), 3);
			printDebug("\tLDC2_W", 2);
		} else if (cpi instanceof ANEWARRAY) {
			printDebug("\tANEWARRAY", 2);
		} else if (cpi instanceof CHECKCAST) {
			printDebug("\tCHECKCAST", 2);
		} else if (cpi instanceof INSTANCEOF) {
			printDebug("\tINSTANCEOF", 2);
		} else if (cpi instanceof MULTIANEWARRAY) {
			printDebug("\tMULTILANEWARRAY", 2);
		} else {
			printDebug("\tUNKNOWN", 2);
		}

	}

	private void decodeLDSTInstruction(ConstantPoolGen cpg,
			InstructionHandle ih, Instruction insn) {
		LocalVariableInstruction lvi = (LocalVariableInstruction) insn;
		printInstruction("LdSt   ", ih, insn);

		if (insn instanceof LoadInstruction) {
			// LoadInstruction ldi = (LoadInstruction) insn;
			printDebug("\tldst  " + "LOAD", 2);
		} else if (insn instanceof StoreInstruction) {
			// StoreInstruction sti = (StoreInstruction) insn;
			printDebug("\tldst  " + "STORE", 2);
		}
		printDebug("\tcstack " + lvi.consumeStack(cpg), 4);
		printDebug("\tpstack " + lvi.produceStack(cpg), 4);
		if (create) {
			LocVarNode n = (LocVarNode) depGraph.getNode(locVarHMap.get(lvi
					.getIndex()));
			if (insn instanceof LoadInstruction) {
				locStack.push(n);
				printDebug("PUSH ST " + n.toString(), 3);
			} else if (insn instanceof StoreInstruction) {
				showPOP("POP LD");
			}
		}
	}

	private void decodeStackInstruction(ConstantPoolGen cpg,
			InstructionHandle ih, Instruction insn) {
		StackInstruction si = (StackInstruction) insn;
		printInstruction("Stack   ", ih, insn);
		int cstack = si.consumeStack(cpg);
		int pstack = si.produceStack(cpg);
		printDebug("\tcstack " + cstack, 4);
		printDebug("\tpstack " + pstack, 4);
		// Figure out the dup or pop, swap

		if (si instanceof SWAP) {
			printDebug("SWAP ", 2);
			if (create) {
				Node t1 = locStack.pop();
				printDebug("POP Const " + t1.toString(), 3);
				Node t2 = locStack.pop();
				printDebug("POP Const " + t2.toString(), 3);
				locStack.push(t2);
				locStack.push(t1);
				printDebug("PUSH Const " + t2.toString(), 3);
				printDebug("PUSH Const " + t1.toString(), 3);
			}
		} else { // dup or pop
			printDebug("DUP/POP ", 2);
			Node list[];
			list = new Node[cstack];
			if (create) {
				for (int i = cstack - 1; i >=0; i--) {
					list[i] = locStack.pop();
					printDebug("POP Const " + list[i].toString(), 3);
				}
				for (int i = 0; i < pstack; i++) {
					locStack.push(list[i % cstack]);
					printDebug("PUSH Const " + list[i % cstack].toString(), 3);
				}
			}
		}
	}

	private void decodeReturnInstruction(ConstantPoolGen cpg,
			InstructionHandle ih, Instruction insn) {
		printInstruction("Return ", ih, insn);
		printDebug("\tcstack " + insn.consumeStack(cpg), 4);
		printDebug("\tpstack " + insn.produceStack(cpg), 4);
	}

	private void decodeConstInstruction(ConstantPoolGen cpg,
			InstructionHandle ih, Instruction insn) {
		ConstantPushInstruction cpi = (ConstantPushInstruction) insn;
		printInstruction("Const   ", ih, insn);
		printDebug("\tpstack " + cpi.produceStack(cpg), 4);
		if (create) {
			ConstNode cn = new ConstNode(cpi.toString(), cpi.produceStack(cpg));
			locStack.push(cn);
			printDebug("PUSH Const " + cn.toString(), 3);
		}
	}

	private void decodeOtherInstruction(ConstantPoolGen cpg,
			InstructionHandle ih, Instruction insn) {
		printInstruction("Other   ", ih, insn);
		printDebug("\tcstack " + insn.consumeStack(cpg), 4);
		printDebug("\tpstack " + insn.produceStack(cpg), 4);
		if (insn instanceof ArrayInstruction) {
			printDebug("\tArrayInstruction", 4);
		}
		if (insn instanceof ARRAYLENGTH) {
			printDebug("\tARRAYLENGTH", 4);
		} else if (insn instanceof ATHROW) {
			printDebug("\tATHROW", 4);
		} else if (insn instanceof DCMPG) {
			printDebug("\tDCMPG", 4);
		} else if (insn instanceof DCMPL) {
			printDebug("\tDCMPL", 4);
		} else if (insn instanceof FCMPG) {
			printDebug("\tFCMPG", 4);
		} else if (insn instanceof FCMPL) {
			printDebug("\tFCMPL", 4);
		} else if (insn instanceof LCMP) {
			printDebug("\tLCMP", 4);
		} else if (insn instanceof NEWARRAY) {
			printDebug("\tNEWARRAY", 4);
		} else if (insn instanceof RET) {
			printDebug("\tRET", 4);
		} else {
			printDebug("\tUNKNOWN", 4);
		}

	}

	private String extractInstruction(InstructionHandle ih, Instruction insn) {
		return "@" + ih.getPosition() + " insn = " + insn.toString();
	}

	private void printInstruction(String itype, InstructionHandle ih,
			Instruction insn) {
		printDebug(" " + itype + " " + extractInstruction(ih, insn), 2);
	}

	private String printMethodName(ClassGen cg, Method m) {
		return m.getReturnType() + " " + cg.getClassName() + "." + m.getName()
				+ m.getSignature();
	}

	private String printIFMethodName(String IFname, Method m) {
		return m.getReturnType() + " " + IFname + "." + m.getName()
				+ m.getSignature();
	}

	private void showPOP(String string) {
		if (locStack.size() > 0) {
			Node n = locStack.pop();
			printDebug(string + n.toString(), 3);
		} else {
			printDebug("POP NULL", 3);
		}
	}

	private ArrayList<BranchNode> getCurBranches(int curIndx) {
		ArrayList<BranchNode> curBN = new ArrayList<BranchNode>();
		for (BranchNode bn : actBranch) {
			if (bn.getEndIndx() >= curIndx && bn.getStartIndx() <= curIndx) {
				curBN.add(bn);
			}
		}
		actBranch = curBN;
		return curBN;
	}

	private void getInterfaces(String className, ClassGen cg) throws ClassNotFoundException {
		JavaClass jcinterfaces[] = Repository.getInterfaces(className);
		for (JavaClass j : jcinterfaces) {
			JavaClass[] ji = j.getAllInterfaces();
			for (JavaClass i : ji) {
				for (Method m : i.getMethods()) {
					String i_name = printIFMethodName(i.getClassName(),m);
					String m_name = printMethodName(cg, m);//"." + m.getName() + m.getSignature();
					insertInterfacePair(i_name, m_name);
				}
			} // i: ji
		} // j: jcinterfaces
		// print_impl_interface();
	}

	private void insertInterfacePair(String intf, String impl) {
		List<String> list;
		if (!implemented_interfaces.containsKey(intf)) {
			list = new ArrayList<String>();
			list.add(impl);
			implemented_interfaces.put(intf, list);
			//System.out.println("ADDED INTERFACE1 :"+intf+" to "+impl);
		} else {
			list = implemented_interfaces.get(intf);
			if (!list.contains(impl)) {
				list.add(impl);
				implemented_interfaces.put(intf, list);
				//System.out.println("ADDED INTERFACE2 :"+intf+" to "+impl);
			}
		}
	}

	private void cleanupAnyPureInterface(CodeGraph graph) {
		for (String i_used : interfaces_used) {
			//System.out.println("INTERFACE USED : "+i_used.toString());
			if (implemented_interfaces.containsKey(i_used)) {
				Node intfNode = graph.getNode(i_used);
				Collection<Edge> e_all = graph.adjList.getAllEdges();
				ArrayList<Edge> e_from = new ArrayList<Edge>();
				ArrayList<Edge> e_to   = new ArrayList<Edge>();

				if (!e_all.isEmpty()) {
					for (Edge e : e_all) {  
						Node to   = e.getTo();
						Node from = e.getFrom();
						if (to.equals(intfNode)) {
							e_to.add(e);	
						}
						if (from.equals(intfNode)) {
							e_from.add(e);
						}
					}
				}	
//				for(Edge e: e_to) {
//					System.out.println("\tTO  : "+e.toString());
//				}
//				for(Edge e: e_from) {
//					System.out.println("\tFROM: "+e.toString());
//				}
				List<String> s_list = implemented_interfaces.get(i_used);
				for (String s_im : s_list) {
//					printDebug("\tREPLACE " + i_used + " with " + s_im,0);
					// create the node for s_im
					MethodNode mNode = graph.addMethodNode(s_im);
					for(Edge e: e_to) { 
						graph.addEdge(e.getFrom(), mNode);
//						System.out.println("ADDED edge "+e.getFrom()+"->"+mNode.toString());
					}
					for(Edge e: e_from) {
						graph.addEdge(mNode,e.getTo());
					}
				} // replacement
				
				// remove the i_used node and edges
				graph.removeNode(intfNode);
				if (!e_from.isEmpty()) {
					for (Edge e : e_from) {
						graph.removeEdge(e);
					}
				}
				if (!e_to.isEmpty()) {
					for (Edge e : e_to) {
						graph.removeEdge(e);
					}
				}
				
			}
		}// i_used: interfaces_used
	}

	public void printDebug(String msg, int level) {
		if (debugLevel >= level) {
			System.out.println(msg);
		}
	}

	public int getDebugLevel() {
		return debugLevel;
	}

	public void setDebugLevel(int debugLevel) {
		if (debugLevel < 0) {
			this.debugLevel = 0;
		} else {
			this.debugLevel = debugLevel;
		}
	}

	public String callGraphToString() {
		return callGraph.toString();
	}

	public String depGraphToString() {
		return depGraph.toString();
	}

	public CodeGraph getReducedGraph(String initialNodeName, String finalNodeName) {
		Node initialNode = callGraph.getNode(initialNodeName);
		Node finalNode = callGraph.getNode(finalNodeName);
		return callGraph.reduceGraph(initialNode, finalNode);
	}
	
	private ArrayList<MethodNode> getAllPathMethods(CodeGraph reducedGraph) {
		ArrayList<MethodNode> depMethodList = new ArrayList<MethodNode>();
		ArrayList<Node> callNodes = reducedGraph.getNodeList();

		for(Node n: callNodes) {
			MethodNode mn = (MethodNode) n;
			if(!depMethodList.contains(mn)) {
				depMethodList.add(mn);
			}
			ArrayList<MethodNode> depMNList = depGraph.getMethodDep(mn);
			for(MethodNode m: depMNList) {
				if(!depMethodList.contains(m)) {
					depMethodList.add(m);
				}
			}
		}
		depGraph.addInitMethods(depMethodList);
		return depMethodList;
	}
	
	public ArrayList <MethodNode> getSkipMethodList(CodeGraph reducedGraph) {
		ArrayList <MethodNode> keepList = getAllPathMethods(reducedGraph);
		ArrayList <MethodNode> skipList = new ArrayList<MethodNode>();
		
		for(Node n: callGraph.getNodeList()) {
			if(!keepList.contains(n)) {
				skipList.add((MethodNode)n);
			}
		}
		return skipList;
	}
	
} // Class CodeGraphGenerator
