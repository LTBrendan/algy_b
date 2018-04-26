package virtualTouchMachine;

import virtualTouchMachine.Assignment;
import virtualTouchMachine.CallMacroCode;
import virtualTouchMachine.Code;
import virtualTouchMachine.EndBlocException;
import virtualTouchMachine.PushValue;
import virtualTouchMachine.PushVariable;
import virtualTouchMachine.TouchMachine;
import virtualTouchMachine.TouchMachineException;
import virtualTouchMachine.TwoOperandsOperation;
import virtualTouchMachine.TwoOperandsOperationType;
import virtualTouchMachine.Var;
import virtualTouchMachine.Variable;

public class TestNestedLoops {
	private static Variable i;
	private static Variable j;
	private static Variable k;
	private static Variable ZERO;
	private static Variable ONE;
	private static Variable TEN;
	private static Code main;
	private static Code internalLoop;
	private static TouchMachine myMachine;

	public static void main(String[] args) {
		boolean resume = true;
		i = new Var("i");
		j = new Var("j");
		k = new Var("k");
		ZERO = new Var("ZERO");
		ONE = new Var("ONE");
		TEN = new Var("TEN");
		ZERO.setValue(0);
		ONE.setValue(1);
		TEN.setValue(10);
		main = new Code("main");
		main.setLoop();
		internalLoop = new Code("internal");
		internalLoop.setLoop();
		main.setFromInsertionAddress();
		main.add(new PushVariable(ZERO));
		main.add(new Assignment(k));
		main.add(new PushVariable(ZERO));
		main.add(new Assignment(i));
		main.setUntilInsertionAddress();
		main.add(new PushVariable(i));
		main.add(new PushVariable(TEN));
		main.add(new TwoOperandsOperation(TwoOperandsOperationType.EQUAL));
		main.addBranchTrueTerminate();
		main.setCoreLoopInsertionAddress();
		main.add(new CallMacroCode(internalLoop));
		main.add(new PushVariable(i));
		main.add(new PushValue(1));
		main.add(new TwoOperandsOperation(TwoOperandsOperationType.ADD));
		main.add(new Assignment(i));
		internalLoop.setFromInsertionAddress();
		internalLoop.add(new PushVariable(ZERO));
		internalLoop.add(new Assignment(j));
		internalLoop.setUntilInsertionAddress();
		internalLoop.add(new PushVariable(j));
		internalLoop.add(new PushVariable(TEN));
		internalLoop.add(new TwoOperandsOperation(TwoOperandsOperationType.EQUAL));
		internalLoop.addBranchTrueTerminate();
		internalLoop.setCoreLoopInsertionAddress();
		internalLoop.add(new PushVariable(k));
		internalLoop.add(new PushValue(1));
		internalLoop.add(new TwoOperandsOperation(TwoOperandsOperationType.ADD));
		internalLoop.add(new Assignment(k));
		internalLoop.add(new PushVariable(j));
		internalLoop.add(new PushValue(1));
		internalLoop.add(new TwoOperandsOperation(TwoOperandsOperationType.ADD));
		internalLoop.add(new Assignment(j));
		System.out.println("Test nested loops");
		System.out.println("-----------------");
		System.out.println("From");
		System.out.println(" k=0");
		System.out.println(" i=0");
		System.out.println("Until");
		System.out.println(" i==10");
		System.out.println("Loop");
		System.out.println(" call InternalLoop");
		System.out.println(" i++");
		System.out.println("Terminate");
		System.out.println("-----------------");
		System.out.println("InternalLoop");
		System.out.println("From");
		System.out.println(" j=0");
		System.out.println("Until");
		System.out.println(" j==10");
		System.out.println("Loop");
		System.out.println(" k++");
		System.out.println(" j++");
		System.out.println("Terminate");
		System.out.println("-----------------");
		System.out.println(main);
		System.out.println(internalLoop);
		System.out.println("Valeur de i : " + i.getValue());
		System.out.println("Valeur de j : " + j.getValue());
		System.out.println("Valeur de k : " + k.getValue());
		System.out.println("---------------");
		myMachine = new TouchMachine(main);
		System.out.println("Execution");
		int startAddress = 0;

		while (resume) {
			try {
				myMachine.run(startAddress);
				resume = false;
			} catch (EndBlocException arg3) {
				System.out.println(arg3.getMessage());
				startAddress = arg3.getSuspendedAddress() + 1;
			} catch (TouchMachineException arg4) {
				arg4.printStackTrace();
				resume = false;
			}
		}

		System.out.println("Valeur de i : " + i.getValue());
		System.out.println("Valeur de j : " + j.getValue());
		System.out.println("Valeur de k : " + k.getValue());
		System.out.println("---------------");
	}
}
