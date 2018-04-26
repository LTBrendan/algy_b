package virtualTouchMachine;

import java.util.Scanner;
import virtualTouchMachine.Array;
import virtualTouchMachine.Assignment;
import virtualTouchMachine.Branch;
import virtualTouchMachine.Code;
import virtualTouchMachine.Continue;
import virtualTouchMachine.EndBlocException;
import virtualTouchMachine.IndexedAssignment;
import virtualTouchMachine.IntArray;
import virtualTouchMachine.PushIndexedVar;
import virtualTouchMachine.PushRead;
import virtualTouchMachine.PushValue;
import virtualTouchMachine.PushVariable;
import virtualTouchMachine.SuspendForReadException;
import virtualTouchMachine.TouchMachine;
import virtualTouchMachine.TouchMachineException;
import virtualTouchMachine.TwoOperandsOperation;
import virtualTouchMachine.TwoOperandsOperationType;
import virtualTouchMachine.Var;
import virtualTouchMachine.Variable;
import virtualTouchMachine.Branch.BRANCH_TYPE;

public class Test {
	private static Variable a;
	private static Variable b;
	private static Variable c;
	private static Variable d;
	private static Variable index;
	private static Array t1;
	private static Code prog;
	private static TouchMachine myMachine;

	public static void main(String[] args) {
		a = new Var("a");
		b = new Var("b");
		c = new Var("c");
		d = new Var("d");
		index = new Var("index");
		t1 = new IntArray("t1", 10);
		prog = new Code("Test");
		testVariable();
		testArray();
		testBranchAlways();
		testFalseBranch();
		testFalseNoBranch();
		testTrueBranch();
		testTrueNoBranch();
		testRead();
	}

	private static void testFalseBranch() {
		prog = new Code("testFalseBranch");
		Continue endTest = new Continue();
		prog.setFromInsertionAddress();
		prog.add(new PushValue(1000));
		prog.add(new Assignment(d));
		prog.add(new PushValue(10));
		prog.add(new PushValue(7));
		prog.add(new TwoOperandsOperation(TwoOperandsOperationType.EQUAL));
		prog.add(new Branch(BRANCH_TYPE.TOP_STACK_FALSE, endTest));
		prog.add(new PushValue(-1000));
		prog.add(new Assignment(d));
		prog.add(endTest);
		prog.remove(prog.getTerminateInsertionAddress() - 1);
		System.out.println("\nBranch test");
		System.out.println(" d = 1000;\n if (10 equals 7); \n  d = -1000 ;\n else  exit");
		System.out.println("-----------\n");
		System.out.println(prog);
		System.out.println("-----------\n");
		runExecution();
		System.out.println("Valeur de d (1000) : " + d.getValue());
		System.out.println("---------------");
	}

	private static void testFalseNoBranch() {
		prog = new Code("testFalseNoBranch");
		Continue endTest = new Continue();
		prog.setFromInsertionAddress();
		prog.add(new PushValue(1000));
		prog.add(new Assignment(d));
		prog.add(new PushValue(10));
		prog.add(new PushValue(10));
		prog.add(new TwoOperandsOperation(TwoOperandsOperationType.EQUAL));
		prog.add(new Branch(BRANCH_TYPE.TOP_STACK_FALSE, endTest));
		prog.add(new PushValue(-1000));
		prog.add(new Assignment(d));
		prog.add(endTest);
		prog.remove(prog.getTerminateInsertionAddress() - 1);
		System.out.println("\nBranch test");
		System.out.println(" d = 1000;\n if (10 equals 10); \n  d = -1000 ;\n else  exit");
		System.out.println("-----------\n");
		System.out.println(prog);
		System.out.println("-----------\n");
		runExecution();
		System.out.println("Valeur de d (-1000) : " + d.getValue());
		System.out.println("---------------");
	}

	private static void testTrueBranch() {
		prog = new Code("testTrueBranch");
		Continue endTest = new Continue();
		prog.setFromInsertionAddress();
		prog.add(new PushValue(1000));
		prog.add(new Assignment(d));
		prog.add(new PushValue(10));
		prog.add(new PushValue(7));
		prog.add(new TwoOperandsOperation(TwoOperandsOperationType.EQUAL));
		prog.add(new Branch(BRANCH_TYPE.TOP_STACK_TRUE, endTest));
		prog.add(new PushValue(-1000));
		prog.add(new Assignment(d));
		prog.add(endTest);
		prog.remove(prog.getTerminateInsertionAddress() - 1);
		System.out.println("\nBranch test");
		System.out.println(" d = 1000;\n if (10 equals 7); \n  continue;\n else  d = -1000");
		System.out.println("-----------\n");
		System.out.println(prog);
		System.out.println("-----------\n");
		runExecution();
		System.out.println("Valeur de d (-1000) : " + d.getValue());
		System.out.println("---------------");
	}

	private static void testTrueNoBranch() {
		prog = new Code("testTrueNoBranch");
		Continue endTest = new Continue();
		prog.setFromInsertionAddress();
		prog.add(new PushValue(1000));
		prog.add(new Assignment(d));
		prog.add(new PushValue(10));
		prog.add(new PushValue(10));
		prog.add(new TwoOperandsOperation(TwoOperandsOperationType.EQUAL));
		prog.add(new Branch(BRANCH_TYPE.TOP_STACK_TRUE, endTest));
		prog.add(new PushValue(-1000));
		prog.add(new Assignment(d));
		prog.add(endTest);
		prog.remove(prog.getTerminateInsertionAddress() - 1);
		System.out.println("\nBranch test");
		System.out.println(" d = 1000;\n if (10 equals 10); \n  continue;\n else  d = -1000");
		System.out.println("-----------\n");
		System.out.println(prog);
		System.out.println("-----------\n");
		runExecution();
		System.out.println("Valeur de d (1000) : " + d.getValue());
		System.out.println("---------------");
	}

	private static void testBranchAlways() {
		prog = new Code("testBranchAlways");
		Continue endTest = new Continue();
		prog.setFromInsertionAddress();
		prog.add(new PushValue(-1000));
		prog.add(new Assignment(d));
		prog.add(new Branch(BRANCH_TYPE.ALWAYS, endTest));
		prog.add(new PushValue(99));
		prog.add(new Assignment(d));
		prog.add(endTest);
		prog.remove(prog.getTerminateInsertionAddress() - 1);
		System.out.println("\nBranch always instruction test");
		System.out.println(" d = -1000;\n branch endtest;\n d = 99; \n endTest : ");
		System.out.println("-----------\n");
		System.out.println(prog);
		System.out.println("-----------\n");
		runExecution();
		System.out.println("Valeur de d (-1000) : " + d.getValue());
		System.out.println("---------------");
	}

	private static void testArray() {
		prog = new Code("testArray");
		prog.setFromInsertionAddress();
		prog.add(new PushValue(4));
		prog.add(new PushValue(1));
		prog.add(new IndexedAssignment(t1));
		prog.add(new PushValue(1));
		prog.add(new PushIndexedVar(t1));
		prog.add(new Assignment(d));
		prog.add(new PushValue(1));
		prog.add(new Assignment(index));
		prog.add(new PushVariable(index));
		prog.add(new PushIndexedVar(t1));
		prog.add(new PushValue(1));
		prog.add(new TwoOperandsOperation(TwoOperandsOperationType.ADD));
		prog.add(new PushVariable(index));
		prog.add(new IndexedAssignment(t1));
		prog.remove(prog.getTerminateInsertionAddress() - 1);
		System.out.println("\nArray test");
		System.out.println(" T1[1] = 4;\n d = T1[1];\n index = 1;\n T1[index]++;");
		System.out.println("-----------\n");
		System.out.println(prog);
		System.out.println("-----------\n");
		runExecution();
		System.out.println("Valeur de T1[1] (5): " + t1.getValue(1));
		System.out.println("Valeur de d (4) : " + d.getValue());
		System.out.println("Valeur de T1[index] (5) : " + t1.getValue(index.getValue()));
		System.out.println("---------------");
	}

	private static void testVariable() {
		prog = new Code("testVariable");
		prog.setFromInsertionAddress();
		prog.add(new PushValue(10));
		prog.add(new Assignment(a));
		prog.add(new PushVariable(a));
		prog.add(new PushValue(1));
		prog.add(new TwoOperandsOperation(TwoOperandsOperationType.ADD));
		prog.add(new Assignment(a));
		prog.add(new PushValue(20));
		prog.add(new Assignment(b));
		prog.add(new PushVariable(a));
		prog.add(new PushVariable(b));
		prog.add(new TwoOperandsOperation(TwoOperandsOperationType.SUB));
		prog.add(new Assignment(c));
		prog.remove(prog.getTerminateInsertionAddress() - 1);
		System.out.println("\nVariable test");
		System.out.println(" a=10; a++;\n b=20;\n c = a-b;");
		System.out.println("-----------\n");
		System.out.println(prog);
		System.out.println("-----------\n");
		runExecution();
		System.out.println("Valeur de a (11): " + a.getValue());
		System.out.println("Valeur de c (-9) : " + c.getValue());
		System.out.println("---------------");
	}

	private static void runExecution() {
		myMachine = new TouchMachine(prog);
		System.out.println("Execution");
		int startAddress = 0;
		boolean resume = true;

		while (resume) {
			try {
				myMachine.run(startAddress);
			} catch (EndBlocException arg4) {
				System.out.println(arg4.getMessage());
				if (arg4.getMessage().equals("End algorithm")) {
					resume = false;
				} else {
					startAddress = arg4.getSuspendedAddress() + 1;
				}
			} catch (SuspendForReadException arg5) {
				System.out.println(arg5.getPrompt());
				Scanner s = new Scanner(System.in);
				int x = s.nextInt();
				s.close();
				myMachine.pushValue(x);
				startAddress = arg5.getSuspendedAddress() + 1;
			} catch (TouchMachineException arg6) {
				arg6.printStackTrace();
				resume = false;
			}
		}

	}

	private static void testRead() {
		prog = new Code("testRead");
		prog.setFromInsertionAddress();
		prog.add(new PushRead("Give a value"));
		prog.add(new Assignment(a));
		prog.remove(prog.getTerminateInsertionAddress() - 1);
		System.out.println("\nVariable read test");
		System.out.println(" a=read;");
		System.out.println("-----------\n");
		System.out.println(prog);
		System.out.println("-----------\n");
		runExecution();
		System.out.println("Valeur de a : " + a.getValue());
		System.out.println("---------------");
	}
}
