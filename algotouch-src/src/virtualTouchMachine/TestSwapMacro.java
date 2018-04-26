package virtualTouchMachine;

import virtualTouchMachine.Assignment;
import virtualTouchMachine.CallMacroCode;
import virtualTouchMachine.Code;
import virtualTouchMachine.EndBlocException;
import virtualTouchMachine.PushVariable;
import virtualTouchMachine.TouchMachine;
import virtualTouchMachine.TouchMachineException;
import virtualTouchMachine.Var;
import virtualTouchMachine.Variable;

public class TestSwapMacro {
	private static Variable tmp;
	private static Variable x;
	private static Variable y;
	private static Code main;
	private static Code swap;
	private static TouchMachine myMachine;

	public static void main(String[] args) {
		boolean resume = true;
		tmp = new Var("tmp");
		x = new Var("x");
		y = new Var("y");
		x.setValue(10);
		y.setValue(50);
		main = new Code("main");
		swap = new Code("swap");
		main.setFromInsertionAddress();
		main.add(new CallMacroCode(swap));
		swap.setFromInsertionAddress();
		swap.add(new PushVariable(x));
		swap.add(new Assignment(tmp));
		swap.add(new PushVariable(y));
		swap.add(new Assignment(x));
		swap.add(new PushVariable(tmp));
		swap.add(new Assignment(y));
		System.out.println("Test call macro");
		System.out.println("---------------");
		System.out.println("call Swap");
		System.out.println("---------------");
		System.out.println("Swap");
		System.out.println("  tmp = x");
		System.out.println("  x = y");
		System.out.println("  y = tmp");
		System.out.println("---------------");
		System.out.println();
		System.out.println(main);
		System.out.println(swap);
		System.out.println("Valeur de x : " + x.getValue());
		System.out.println("Valeur de y : " + y.getValue());
		System.out.println("---------------");
		myMachine = new TouchMachine(main);
		System.out.println("Execution");
		System.out.println("---------------");
		int startAddress = 0;

		while (resume) {
			try {
				myMachine.run(startAddress);
			} catch (EndBlocException arg3) {
				System.out.println(arg3.getMessage());
				if (arg3.getMessage().equals("End algorithm")) {
					resume = false;
				} else {
					startAddress = arg3.getSuspendedAddress() + 1;
				}
			} catch (TouchMachineException arg4) {
				arg4.printStackTrace();
				resume = false;
			}
		}

		System.out.println("Valeur de x : " + x.getValue());
		System.out.println("Valeur de y : " + y.getValue());
		System.out.println("---------------");
	}
}
