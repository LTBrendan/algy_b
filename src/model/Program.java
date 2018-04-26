package model;

import static model.Utils.msg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.omg.CORBA.StringHolder;

import controllers.jslink.Bridge;
import javafx.concurrent.Task;
import model.Utils.Level;
import model.machine.PushArray;
import model.machine.WriteInstruction;
import model.machine.exceptions.SuspendForWriteException;
import model.vars.CharArray;
import model.vars.CharVar;
import model.vars.ConstantVar;
import model.vars.Index;
import model.vars.IntArray;
import model.vars.IntVar;
import model.vars.listeners.VarListener;
import view.component.Popup;
import view.main.App;
import virtualTouchMachine.Array;
import virtualTouchMachine.Assignment;
import virtualTouchMachine.Branch;
import virtualTouchMachine.Branch.BRANCH_TYPE;
import virtualTouchMachine.CallMacroCode;
import virtualTouchMachine.CallMacroException;
import virtualTouchMachine.Code;
import virtualTouchMachine.Continue;
import virtualTouchMachine.EndBlocException;
import virtualTouchMachine.EndCodeException;
import virtualTouchMachine.IndexedAssignment;
import virtualTouchMachine.Instruction;
import virtualTouchMachine.IntegerStack;
import virtualTouchMachine.OutOfBoundsException;
import virtualTouchMachine.PushIndexedVar;
import virtualTouchMachine.PushRead;
import virtualTouchMachine.PushReadChar;
import virtualTouchMachine.PushValue;
import virtualTouchMachine.PushVariable;
import virtualTouchMachine.SuspendExecutionException;
import virtualTouchMachine.SuspendForReadException;
import virtualTouchMachine.TouchMachineException;
import virtualTouchMachine.TwoOperandsOperation;
import virtualTouchMachine.TwoOperandsOperationType;
import virtualTouchMachine.UnknownAddressException;
import virtualTouchMachine.Variable;

/**
 * The program class is defined to be process the pseudo code received from the web interface blockly
 * and is also able to run it on the AlgoTouch virtual machine
 * @author Daphnis
 *
 */
public class Program {

	/**
	 * HashMap containing all the pseudo code instruction associated with their own
	 * convertor lambda
	 */
	private static final HashMap<String, Convertor> COMPILER;

	/**
	 * In case an error append, this will take care of an unwanted line of code by
	 * doing nothing
	 */
	private static final Convertor DEFAULT;

	/**
	 * Execute program on another thread
	 */
	private static final ExecutorService INTERPRETER;

	/**
	 * Initialize the compiler hashmap with all the lambdas
	 */
	static {
		INTERPRETER = Executors.newSingleThreadExecutor();
		DEFAULT = (p, c, args) -> {
			p.errors.append("Unknown instruction (" + args + ")\n");
		};

		COMPILER = new HashMap<>();
		COMPILER.put("error", (p, c, arg) -> p.errors.append(arg).append('\n'));
		COMPILER.put("macro", (p, c, arg) -> {
		});
		COMPILER.put("rename", (p, c, arg) -> {
			String[] names = arg.split(" ");
			if (p.variables.containsKey(names[0]))
				p.variables.put(names[1], p.variables.remove(names[0]).rename(names[1]));
			else if (p.arrays.containsKey(names[0])) {
				p.arrays.put(names[1], p.arrays.remove(names[0]).rename(names[1]));
				p.executeLine("rename " + names[0] + ".length " + names[1] + ".length");
			}
		});
		COMPILER.put("delete", (p, c, arg) -> {
			Optional.ofNullable(p.variables.remove(arg)).ifPresent(IntVar::delete);
			;
			Optional.ofNullable(p.arrays.remove(arg)).ifPresent(array -> {
				array.delete();
				p.executeLine("delete " + array.getName() + ".length");
			});
			;
		});
		COMPILER.put("create_index", (p, c, arg) -> {
			String[] args = arg.split(" ");
			p.putIndex(args[0], args[1]);
		});
		COMPILER.put("create_var_char", (p, c, arg) -> p.putVariableChar(arg));
		COMPILER.put("create_var_integer", (p, c, arg) -> p.putVariableInt(arg));
		COMPILER.put("create_constant", (p, c, arg) -> p.putConstant(arg, p.tempConstant.remove(arg)));
		COMPILER.put("create_integer_array", (p, c, arg) -> p.putArray("INT", arg, p.tempArrays.remove(arg)));
		COMPILER.put("create_char_array", (p, c, arg) -> p.putArray("CHAR", arg, p.tempArrays.remove(arg)));
		COMPILER.put("intPrompt", (p, c, arg) -> c.add(new PushRead(arg)));
		COMPILER.put("charPrompt", (p, c, arg) -> c.add(new PushReadChar(arg)));
		COMPILER.put("pushValue", (p, c, arg) -> c.add(new PushValue(toInt(arg))));
		COMPILER.put("pushVar", (p, c, arg) -> c.add(new PushVariable(p.getVariable(arg))));
		COMPILER.put("pushIndexedVar", (p, c, arg) -> c.add(new PushIndexedVar(p.getArray(arg))));
		COMPILER.put("assign", (p, c, arg) -> c.add(new Assignment(p.getVariable(arg))));
		COMPILER.put("indexedAssign", (p, c, arg) -> c.add(new IndexedAssignment(p.getArray(arg))));
		COMPILER.put("pushTwoOp",
				(p, c, arg) -> c.add(new TwoOperandsOperation(TwoOperandsOperationType.valueOf(arg))));
		COMPILER.put("createContinue", (p, c, arg) -> p.continues.get(c.getName()).push(new Continue()));
		COMPILER.put("pushBranch",
				(p, c, arg) -> c.add(new Branch(BRANCH_TYPE.valueOf(arg), p.continues.get(c.getName()).peek())));
		COMPILER.put("pushBranchTerminate", (p, c, arg) -> c.addBranchTrueTerminate());
		COMPILER.put("pushLastContinue", (p, c, arg) -> {
			int index = p.continues.get(c.getName()).size() - 2;
			Continue inst = p.continues.get(c.getName()).elementAt(index);
			p.continues.get(c.getName()).remove(index);
			c.add(inst);
		});
		COMPILER.put("pushContinue", (p, c, arg) -> c.add(p.continues.get(c.getName()).pop()));
		COMPILER.put("setLoop", (p, c, arg) -> c.setLoop());
		COMPILER.put("setCore", (p, c, arg) -> c.setCoreLoopInsertionAddress());
		COMPILER.put("setFrom", (p, c, arg) -> c.setFromInsertionAddress());
		COMPILER.put("setUntilInsertion", (p, c, arg) -> c.setUntilInsertionAddress());
		COMPILER.put("setTerm", (p, c, arg) -> c.setTerminateInsertionAddress());
		COMPILER.put("callMacro", (p, c, arg) -> c.add(new CallMacroCode(p.macros.get(arg))));
		COMPILER.put("pushArray", (p, c, arg) -> c.add(new PushArray(p.arrays.get(arg))));
		COMPILER.put("print", (p, c, arg) -> c.add(new WriteInstruction(arg)));
		COMPILER.put("putTempConstant",
				(p, c, arg) -> p.tempConstant.put(arg.split("\\s")[0], toInt(arg.split("\\s")[1])));
	}

	/**
	 * Handle events form blockly javascript
	 */
	private Bridge bridge;

	/**
	 * Errors during compilation
	 */
	private StringBuilder errors;

	/**
	 * Map containing all the variables used by the program
	 */
	private HashMap<String, IntVar> variables;

	/**
	 * Map containing all the arrays used by the program
	 */
	private HashMap<String, IntArray> arrays;

	/**
	 * Map containing all the arrays and their associated values
	 */
	private HashMap<String, Integer[]> tempArrays;

	/**
	 * Map containing all the macro names, associated with their respective value
	 */
	private HashMap<String, Integer> tempConstant;

	/**
	 * Map containing all the macro names, associated with their respective Code
	 */
	private HashMap<String, Code> macros;

	/**
	 * Map used to compile when there are conditions
	 */
	private HashMap<String, Stack<Continue>> continues;

	/**
	 * Listener for variables changes
	 */
	private VarListener listener;

	/**
	 * 
	 */
	private Task<Void> executed;

	/**
	 * True if execution is in step by step mode
	 */
	private boolean step;

	/**
	 * True if execution is in deep step by step mode
	 */
	private boolean deep;

	/**
	 * True if program is running
	 */
	private boolean resume;

	/**
	 * Time of a step in automatic mode
	 */
	private int timeout;

	/**
	 * True if compilation was ok
	 */
	private boolean compilation;

	/**
	 * Object to handle automatic step by step
	 */
	private Object LOCK = new Object();

	/**
	 * True if execution is in pause
	 */
	private boolean pause;

	/**
	 * App linked to this program
	 */
	private App app;

	/**
	 * Count number of instruction executed
	 */
	private long cpt;

	/**
	 * Create a new Program and initialize all the fields
	 */
	public Program() {
		errors = new StringBuilder();
		variables = new HashMap<>();
		arrays = new HashMap<>();
		tempArrays = new HashMap<>();
		tempConstant = new HashMap<>();
		macros = new HashMap<>();
		continues = new HashMap<>();
		timeout = 0;
		step = false;
		deep = false;
	}

	/**
	 * Print the variable and its value</br>
	 * useful when used with a forEach loop on the variables hashmap</br>
	 * {@code variables.forEach(this::printVariable)}
	 * @param name the name of the variable
	 * @param v the variable itself
	 */
	private void printVariable(String name, Variable v) {
		String type = "INTEGER";
		if (v instanceof CharVar)
			type = "CHARACTER";
		else if (v instanceof ConstantVar)
			type = "CONSTANT";
		else if (v instanceof Index)
			type = "INDEX (" + ((Index) v).getAssociatedArray().getName() + ")";
		Utils.printLog(Level.RUN, type + " : " + name + " = " + v.getValue());
	}

	/**
	 * Print the array name and all its values</br>
	 * useful when used with a forEach loop on the arrays hashmap</br>
	 * {@code arrays.forEach(this::printArray)}
	 * @param name the name of the array
	 * @param a the array itself
	 */
	private void printArray(String name, Array a) {
		String type = "INTEGER_ARRAY";
		if (a instanceof CharArray)
			type = "CHARACTER_ARRAY";
		StringBuilder builder = new StringBuilder();
		builder.append(type + " : " + name + " =  [" + a.getValue(0));
		for (int i = 1; i < a.getSize(); i++)
			builder.append(", " + a.getValue(i));
		builder.append("]");
		Utils.printLog(Level.RUN, builder.toString());
	}

	/**
	 * Run the main macro
	 */
	public void run() {
		run("main");
	}

	/**
	 * Execute the current program First displays all variables and arrays then run
	 * the program and redisplay the variables after the execution
	 * @param macro macro to run
	 */
	public void run(String macro) {

		if (compilation) {

			executed = new Task<Void>() {

				@Override
				protected Void call() throws Exception {
					Utils.printLog(Level.RUN, "==========================================================================");
					Utils.printLog(Level.RUN, "                                EXECUTION                                 ");
					Utils.printLog(Level.RUN, "==========================================================================");
					Utils.printLog(Level.RUN, "---------------------- VARIABLES BEFORE EXECUTION ------------------------");

					// print variables
					variables.forEach(Program.this::printVariable);
					arrays.forEach(Program.this::printArray);

					if (macros.size() != 0) {

						Code main = macros.get(macro);

						Utils.printLog(Level.RUN, "------------------------------- STARTING ---------------------------------");
						
						app.addExecutionIcon();
						resume = true;
						bridge.setMovable(false);
						execute(main, step);
						bridge.setMovable(true);
						resume = false;
						app.removeExecutionIcon();
						if (step)
							bridge.highlight(null, null, true);

					}

					Utils.printLog(Level.RUN, "----------------------- VARIABLES AFTER EXECUTION ------------------------");
					variables.forEach(Program.this::printVariable);
					arrays.forEach(Program.this::printArray);
					Utils.printLog(Level.RUN, "==========================================================================");
					Utils.printLog(Level.INFO, "Number of instructions : " + cpt);
					cpt = 0;
					return null;
				}
			};

			INTERPRETER.submit(executed);
		} else {
			Popup.showConfirmPopup(msg("Popup.error"), msg("Program.compilation"));
			Popup.showMessagePopup(msg("Popup.error"), errors.toString());
		}

	}

	/**
	 * Associate the code with a stack
	 * @param code Code to execute
	 * @param step if the execution must stop after this step
	 */
	private void execute(Code code, boolean step) {
		IntegerStack stack = new IntegerStack();
		execute(code, stack, step);
	}

	/**
	 * Execute the code with a stack
	 * @param code Code to execute
	 * @param stack Stack to use in execution
	 * @param stop if the execution must stop after this step
	 */
	private void execute(Code code, IntegerStack stack, boolean stop) {
		final int loopTrap = 1000000;
		int loopCount = 0;
		int address = 0;
		int max = code.size();
		while (resume && address < max) {
			while(pause) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {}
			}
			try {
				if (loopCount++ > loopTrap)
					throw new Exception(msg("Program.infiniteloop"));
				this.execute(code, address, stack);
			} catch (EndBlocException ebe) {
				if(step) {
					if (stop) {
						synchronized (LOCK) {
							try {
								int tm = timeout;
								String part = code.get(ebe.getSuspendedAddress()).toText(code);
								if (part.contains("algorithm") && timeout == 0
										&& !Thread.currentThread().getStackTrace()[3].getMethodName().contains("execute"))
									tm = 1000;
								if (bridge != null)
									bridge.highlight(code.getName(), part, true);
								LOCK.wait(tm);
							} catch (InterruptedException e) {
								Utils.printLog(Level.ERROR, e.getMessage() + " see logs file for more informations");
								Utils.writeException(e);
							}
						}
					}
				}
				address = ebe.getSuspendedAddress() + 1;
			} catch (CallMacroException cme) {
				execute(cme.getCodeToExecute(), deep);
				address = cme.getReturnAddress();
			} catch (EndCodeException ece) {
				resume = false;
			} catch (SuspendForWriteException sfwe) {
				Utils.printLog(Level.RUN, msg("Program.write") + " : " + sfwe.getMessage());
				Popup.showMessagePopup(msg("Program.write"), sfwe.getMessage());
				address = sfwe.getReturnAddress() + 1;
			} catch (SuspendForReadException sfre) {

				String result = Popup.showInputPopup(msg("Program.input"), sfre.getPrompt());
				if (result == null) {
					resume = false;
				} else {
					Integer entered = null;
					while (entered == null) {
						try {
							switch (sfre.getMessage()) {
							case "INTEGER":
								entered = toInt(result);
								break;
							case "CHAR":
								entered = (int) result.charAt(0);
								break;
							}
						} catch (NumberFormatException | StringIndexOutOfBoundsException ne) {
						}
					}
					Utils.printLog(Level.RUN, msg("Program.print.entry") + " : " + entered);
					stack.push(entered);
					address = sfre.getSuspendedAddress() + 1;
				}
			} catch (OutOfBoundsException oobe) {
				resume = false;
				Popup.showMessagePopup(msg("Popup.error"), oobe.getMessage());
				Utils.printLog(Level.ERROR, oobe.getMessage() + " see logs file for more informations");
				Utils.writeException(oobe);
			} catch (TouchMachineException tme) {
				Utils.printLog(Level.ERROR, tme.getMessage() + " see logs file for more informations");
				Utils.writeException(tme);
				resume = false;
			} catch (Exception e) {
				resume = false;
				Popup.showMessagePopup(msg("Popup.error"), e.getMessage());
				Utils.printLog(Level.ERROR, e.getMessage() + " see logs file for more informations");
				Utils.writeException(e);
			}
		}

	}

	/**
	 * Execute instructions of the code
	 * @param code Code to execute
	 * @param myInstructionCounter pointer to execute
	 * @param myStack Stack of execution
	 * @throws TouchMachineException Throwned during execution if error or stop
	 */
	private void execute(Code code, int myInstructionCounter, IntegerStack myStack) throws TouchMachineException {
		int currentCounter = myInstructionCounter;
		cpt++;
		while (currentCounter < code.size()) {
			Instruction i = (Instruction) code.get(currentCounter);

			try {
				currentCounter = i.execute(currentCounter, myStack);
			} catch (UnknownAddressException arg7) {
				Instruction instructionToBranch = arg7.getBranchInstruction();
				int addressToBranch = code.indexOf(instructionToBranch);
				currentCounter = addressToBranch;
			} catch (SuspendExecutionException arg8) {
				throw arg8;
			} catch (SuspendForReadException arg9) {
				throw arg9;
			} catch (TouchMachineException arg10) {
				throw arg10;
			}
		}

		throw new EndCodeException("End code");
	}

	/**
	 * Execute a single line
	 * @param line the line to execute
	 */
	public void executeLine(String line) {
		apply(line);
	}

	public static final String RETURN = "\n|\n\r";
	
	/**
	 * Analyze the code provided in argument
	 * @param code code to analyze
	 * @return the program corresponding to the code provided
	 */
	public Program analyse(String code) {

		Utils.printLog(Level.COMPIL, "==========================================================================");
		Utils.printLog(Level.COMPIL, "                               COMPILATION                                ");
		Utils.printLog(Level.COMPIL, "==========================================================================");
		Stream.of(code.split(RETURN)).forEach(line -> Utils.printLog(Level.COMPIL, line));

		final StringHolder num = new StringHolder();
		this.macros.clear();
		this.errors.setLength(0);

		Map<String, List<String>> mapped = Stream.of(code.split(RETURN))
				.map(String::trim)
				.filter(str -> str.length() > 0)
				.collect(Collectors.groupingBy(t -> t.startsWith("macro") ? num.value = t.split(" ", 2)[1] : num.value));

		mapped.forEach(this::putMacro);

		mapped.forEach((name, list) -> list.forEach(str -> this.apply(str, this.macros.get(name))));

		Utils.printLog(Level.BTCODE, "==========================================================================");
		Utils.printLog(Level.BTCODE, "                                 PROGRAM                                  ");
		Utils.printLog(Level.BTCODE, "==========================================================================");
		macros.forEach((n, c) -> {
			Utils.printLog(Level.BTCODE, n);
			c.stream().forEach(i -> Utils.printLog(Level.BTCODE, i.toText(c)));
		});

		if (this.errors.length() > 0) {
			compilation = false;
			String[] errStr = this.errors.toString().split("\n");
			for (String err : errStr) {
				Utils.printLog(Level.ERROR, err);
			}

		} else {
			compilation = true;
		}
		return this;
	}

	/**
	 * Generate the code associated with the line
	 * @param str the line to analyze
	 * @param code the code if needed
	 */
	private void apply(String str, Code... code) {
		String[] arr = str.split(" ", 2);
		String inst = arr[0];
		String arg = arr.length > 1 ? arr[1] : null;
		COMPILER.getOrDefault(inst, DEFAULT).apply(this, code.length > 0 ? code[0] : null, arg);
	}

	/**
	 * Create the code object for the name
	 * @param name the name of the macro
	 * @param vals not needed but to be used with a forEach loop
	 */
	private void putMacro(String name, List<String> vals) {
		macros.put(name, new Code(name));
		continues.put(name, new Stack<>());
	}

	/**
	 * Get the variable with the specified name
	 * @param name name of the variable to retrieve
	 * @return the variable associated with the name
	 */
	public Variable getVariable(String name) {
		return variables.get(name);
	}

	/**
	 * Create a new char variable with the specified name
	 * @param name name of the variable
	 */
	private void putVariableChar(String name) {
		if (getVariable(name) == null) {
			CharVar var = new CharVar(name);
			Optional.ofNullable(listener).ifPresent(var::setListener);
			variables.put(name, var);
		}
	}

	/**
	 * Create a new int variable with the specified name
	 * @param name name of the variable
	 */
	private void putVariableInt(String name) {
		if (getVariable(name) == null) {
			IntVar var = new IntVar(name);
			Optional.ofNullable(listener).ifPresent(var::setListener);
			variables.put(name, var);
		}
	}

	/**
	 * Create a new char variable with the specified name
	 * @param name name of the variable
	 * @param array the array the index is associated to
	 */
	private void putIndex(String name, String array) {
		if (getVariable(name) == null) {
			Index var = new Index(name, getArray(array));
			Optional.ofNullable(listener).ifPresent(var::setListener);
			variables.put(name, var);
		}
	}

	/**
	 * Create a new variable with the specified name
	 * @param name name of the variable
	 * @param value the constant's value
	 */
	private void putConstant(String name, Integer value) {
		if (getVariable(name) == null) {
			int val = Optional.ofNullable(value).orElse(0);
			ConstantVar var = new ConstantVar(name, val);
			Optional.ofNullable(listener).ifPresent(var::setListener);
			variables.put(name, var);

		}
	}

	/**
	 * Put an array and initialize the length variable
	 * @param type type of array
	 * @param name name of the array
	 * @param values values of the array
	 */
	private void putArray(String type, String name, Integer[] values) {
		Array array = null;

		if (type.equals("INT")) {
			array = putIntArray(name, values);
		} else if (type.equals("CHAR")) {
			array = putCharArray(name, values);
		}

		if (array != null) {
			String length = array.getName() + ".length";
			executeLine("delete " + length);
			executeLine("create_constant " + length);
		}

	}

	/**
	 * Create a new int array with the specified name and values
	 * @param name the name of the array
	 * @param values the values of the array
	 * @return the array
	 */
	private Array putIntArray(String name, Integer[] values) {
		IntArray array = (IntArray) getArray(name);
		if (array == null) {
			array = new IntArray(name, values);
			Optional.ofNullable(listener).ifPresent(array::setListener);
		} else {
			array.redefine(values);
		}

		arrays.put(name, array);
		return array;
	}

	/**
	 * Create a new char array with the specified name and values
	 * @param name the name of the array
	 * @param values the values of the array
	 * @return the array
	 */
	private Array putCharArray(String name, Integer[] values) {
		IntArray array = (IntArray) getArray(name);
		if (array == null) {
			array = new CharArray(name, values);
			Optional.ofNullable(listener).ifPresent(array::setListener);
		} else {
			array.redefine(values);
		}

		arrays.put(name, array);
		return array;
	}

	/**
	 * Retrieve all the arrays
	 * @return the arrays
	 */
	public ArrayList<IntArray> getAllArrays() {
		return new ArrayList<IntArray>(arrays.values());
	}

	/**
	 * Retrieve all the variables
	 * @return the variables
	 */
	public ArrayList<Variable> getAllVariables() {
		return new ArrayList<Variable>(variables.values());
	}

	/**
	 * Retrieve the array with the specified name
	 * @param name the name of the array
	 * @return the array associated with the name
	 */
	public IntArray getArray(String name) {
		return arrays.get(name);
	}

	/**
	 * Return all the macros as a string
	 * @return the representation as a string of the program
	 */
	public String toString() {
		StringBuilder builder = new StringBuilder();

		macros.forEach((name, code) -> {
			builder.append(code.toString());
		});

		return builder.toString();
	}

	/**
	 * Convert the value to an integer
	 * 
	 * @param value
	 *            the value to be converted
	 * @return the integer parsed or 0
	 */
	private static int toInt(String value) {
		int val = 0;
		try {
			val = Integer.parseInt(value);
		} catch (NumberFormatException e) {
			try {
				val = value.charAt(0);
			} catch (StringIndexOutOfBoundsException e1) {
				Utils.printLog(Level.ERROR, "Error occured during parsing to integer a value see logs file for more informations");
				Utils.writeException(e1);
			}
		}
		return val;
	}

	/**
	 * Set the variable listener
	 * @param listener the new variable listener
	 */
	public void setListener(VarListener listener) {
		this.listener = Objects.requireNonNull(listener, msg("Popup.error.listener"));
	}

	/**
	 * Add a new temporary array for initialization
	 * @param name name of the array
	 * @param array the array
	 */
	public void putTempArray(String name, Integer[] array) {
		tempArrays.put(name, array);
	}

	/**
	 * Put a new constant in the constant array
	 * @param name Name of the constant
	 * @param value Value of the constant
	 */
	public void putTempConstant(String name, Integer value) {
		tempConstant.put(name, value);
	}

	/**
	 * Set the bridge between java and javascript
	 * @param bridge The bridge to link
	 */
	public void setBridge(Bridge bridge) {
		this.bridge = bridge;
	}

	/**
	 * Interrupt the execution
	 */
	public void interrupt() {
		resume = false;
		runNextStep();
		Optional.ofNullable(executed).ifPresent(Task::cancel);
	}

	/**
	 * Test if an execution is running
	 * @return true if a program is running
	 */
	public boolean isRunning() {
		return resume;
	}

	/**
	 * Test if execution is in auto mode
	 * @return true if execution is in auto mode
	 */
	public boolean isAuto() {
		return isRunning() && timeout != 0;
	}

	/**
	 * Set execution in step by step or not
	 * @param step true to set in step mode
	 */
	public void setStep(boolean step) {
		this.step = step;
	}

	/**
	 * Set step by step execution in deep mode or not
	 * @param deep true to set in deep mode
	 */
	public void setDeep(boolean deep) {
		this.deep = deep;
	}

	/**
	 * Time to wait before two automatic steps
	 * @param timeout time of a step
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	/**
	 * Continue the execution
	 */
	public void runNextStep() {
		synchronized (LOCK) {
			LOCK.notify();
		}
	}

	/**
	 * Set execution in pause
	 * @param p true only if the execution must be paused
	 */
	public void setPause(boolean p) {
		this.pause = p;
	}

	/**
	 * Set a new App object as field
	 * @param app App object set
	 */
	public void setApplication(App app) {
		this.app = app;
	}
}
