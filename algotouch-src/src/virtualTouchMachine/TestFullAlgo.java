package virtualTouchMachine;

import java.util.Scanner;
import virtualTouchMachine.Assignment;
import virtualTouchMachine.Code;
import virtualTouchMachine.EndBlocException;
import virtualTouchMachine.PushValue;
import virtualTouchMachine.PushVariable;
import virtualTouchMachine.SuspendForReadException;
import virtualTouchMachine.TouchMachine;
import virtualTouchMachine.TouchMachineException;
import virtualTouchMachine.TwoOperandsOperation;
import virtualTouchMachine.TwoOperandsOperationType;
import virtualTouchMachine.Var;
import virtualTouchMachine.Variable;

public class TestFullAlgo {
	private static Variable val;
	private static Variable stop;
	private static Variable size;
	private static Variable index;
	private static Code prog;
	private static TouchMachine myMachine;

	public static void main(String[] args) {
		val = new Var("val");
		stop = new Var("stop");
		size = new Var("size");
		index = new Var("index");
		System.out.println("Test loop exit");
		System.out.println("--------------\n");
		System.out.println("From");
		System.out.println("  index = 0");
		System.out.println("  val = 0");
		System.out.println("  size = 10");
		System.out.println("Until");
		System.out.println("  index == size");
		System.out.println("  val == stop");
		System.out.println("Loop");
		System.out.println("  index++");
		System.out.println("  val++");
		System.out.println("Terminate");
		System.out.println("End");
		System.out.println("--------------\n");
		System.out.println("Test with stop=5");
		stop.setValue(5);
		testLoopConditions();
		System.out.println("Test with stop=20");
		System.out.println("--------------\n");
		stop.setValue(20);
		testLoopConditions();
	}

	private static void testLoopConditions() {
		prog = new Code("testLoopConditions");
		prog.setLoop();
		prog.setFromInsertionAddress();
		prog.add(new PushValue(0));
		prog.add(new Assignment(index));
		prog.add(new PushValue(0));
		prog.add(new Assignment(val));
		prog.add(new PushValue(10));
		prog.add(new Assignment(size));
		prog.setUntilInsertionAddress();
		prog.add(new PushVariable(index));
		prog.add(new PushVariable(size));
		prog.add(new TwoOperandsOperation(TwoOperandsOperationType.EQUAL));
		prog.addBranchTrueTerminate();
		prog.add(new PushVariable(val));
		prog.add(new PushVariable(stop));
		prog.add(new TwoOperandsOperation(TwoOperandsOperationType.EQUAL));
		prog.addBranchTrueTerminate();
		prog.setCoreLoopInsertionAddress();
		prog.add(new PushVariable(index));
		prog.add(new PushValue(1));
		prog.add(new TwoOperandsOperation(TwoOperandsOperationType.ADD));
		prog.add(new Assignment(index));
		prog.add(new PushVariable(val));
		prog.add(new PushValue(1));
		prog.add(new TwoOperandsOperation(TwoOperandsOperationType.ADD));
		prog.add(new Assignment(val));
		System.out.println("\nTest");
		System.out.println(" ");
		System.out.println(prog);
		runExecution();
		System.out.println("Value of index : " + index.getValue());
		System.out.println("Value of val : " + val.getValue());
		System.out.println("---------------\n");
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
}
