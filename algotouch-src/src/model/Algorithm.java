package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import model.AlgoAddSub;
import model.AlgoArray;
import model.AlgoConst;
import model.AlgoElement;
import model.AlgoIndex;
import model.AlgoMacroCode;
import model.AlgoOperator;
import model.AlgoVarIndex;
import model.AlgoVariable;
import model.CodeAndProgram;
import model.ExitLoopManager;
import model.InvalidNameException;
import model.Program;
import model.Scale;
import model.Algorithm.AlgoTypes;
import virtualTouchMachine.Assignment;
import virtualTouchMachine.Branch;
import virtualTouchMachine.CallMacroCode;
import virtualTouchMachine.Code;
import virtualTouchMachine.Continue;
import virtualTouchMachine.Instruction;
import virtualTouchMachine.PushRead;
import virtualTouchMachine.PushReadChar;
import virtualTouchMachine.PushValue;
import virtualTouchMachine.PushVariable;
import virtualTouchMachine.Todo;
import virtualTouchMachine.TouchMachine;
import virtualTouchMachine.TouchMachineException;
import virtualTouchMachine.TwoOperandsOperation;
import virtualTouchMachine.TwoOperandsOperationType;
import virtualTouchMachine.Variable;
import virtualTouchMachine.Branch.BRANCH_TYPE;
import virtualTouchMachine.Code.BLOCK_TYPE;

public class Algorithm extends Observable implements Serializable {
	private static final long serialVersionUID = -2863587015357887933L;
	private static final String TODO_SYMBOL = "TODO ";
	public static final String COMMENT_SYMBOL = " // ";
	public static final String START_BLOCK_SYMBOL = "{";
	public static final String END_BLOCK_SYMBOL = "}";
	public static final String ELSE_SYMBOL = "else ";
	public static final String NOT_SYMBOL = "not ";
	public static final String END_INSTRUCTION_SYMBOL = " ;";
	public static final String ASSIGNMENT_SYMBOL = " = ";
	public static final String INDENTATION = "   ";
	private static final String MAIN_BLOCK = "Main";
	public static final String LENGTH_SUFFIX = ".length";
	private static final String CODE_CHANGE = "CODE_CHANGED";
	private static final String READ_INSTRUCTION_SYMBOL = "Read ";
	private List<AlgoElement> varList = new ArrayList();
	private HashMap<String, CodeAndProgram> macroTable = new HashMap();
	private AlgoElement active = null;
	private AlgoElement movingValue = null;
	private AlgoElement target = null;
	private Deque<Scale> stackOfScales = new ArrayDeque();
	private Scale myScale;
	private Program myProgram = new Program("Main");
	private String myConsole = new String();
	private Code myCode = new Code("Main");
	private AlgoMacroCode myAlgoCode;
	private AlgoElement source;
	private boolean recording;
	private boolean operatorInUse;
	private AlgoAddSub myOperator;
	private static int todoNb = 0;
	private AlgoElement contextTarget;
	private TouchMachine myVirtualMachine;
	private ExitLoopManager myExitLoop;

	public Algorithm() {
		AlgoMacroCode.resetNbMacros();
		AlgoVariable.resetNbVariables();
		AlgoConst.resetNbConst();
		this.macroTable.put("Main", new CodeAndProgram(this.myCode, this.myProgram));
		this.myAlgoCode = new AlgoMacroCode("Main", "Main program");
		this.add(this.myAlgoCode);
		this.myScale = null;
		this.myOperator = null;
		this.createCommonConstants();
	}

	private void createCommonConstants() {
		AlgoConst constant = new AlgoConst("0", 0);
		constant.setElementType(AlgoTypes.INT);
		this.add(constant);
		constant = new AlgoConst("1", 1);
		constant.setElementType(AlgoTypes.INT);
		this.add(constant);
		constant = new AlgoConst("-1", -1);
		constant.setElementType(AlgoTypes.INT);
		this.add(constant);
		constant = new AlgoConst("\' \'", 32);
		constant.setElementType(AlgoTypes.CHAR);
		this.add(constant);
		constant = new AlgoConst("\'a\'", 97);
		constant.setElementType(AlgoTypes.CHAR);
		this.add(constant);
		constant = new AlgoConst("\'z\'", 122);
		constant.setElementType(AlgoTypes.CHAR);
		this.add(constant);
		constant = new AlgoConst("\'A\'", 65);
		constant.setElementType(AlgoTypes.CHAR);
		this.add(constant);
		constant = new AlgoConst("\'Z\'", 90);
		constant.setElementType(AlgoTypes.CHAR);
		this.add(constant);
		constant = new AlgoConst("\'0\'", 48);
		constant.setElementType(AlgoTypes.CHAR);
		this.add(constant);
		constant = new AlgoConst("\'9\'", 57);
		constant.setElementType(AlgoTypes.CHAR);
		this.add(constant);
	}

	public void add(AlgoElement v) {
		this.varList.add(0, v);
	}

	public void addInstruction(Instruction instruction) {
		if (this.recording) {
			this.myCode.add(instruction);
		}

	}

	public void addLineOfCode(String lineOfCode) {
		this.myConsole = this.myConsole + lineOfCode + '\n';
		if (this.recording) {
			this.myProgram.add("   " + lineOfCode);
		}

		this.notifyObservers();
	}

	public void addDeclaration(String lineOfCode) {
		this.myConsole = this.myConsole + lineOfCode + '\n';
		this.notifyObservers();
	}

	public void addScale() {
		if (this.myScale != null) {
			this.myScale.setAsleep(true);
			this.stackOfScales.push(this.myScale);
		}

		this.myScale = new Scale(600, 150);
		this.add(this.myScale);
		this.setActive(this.myScale);
		this.setOperatorInUse(true);
	}

	public void assignSourceToTarget() {
		if (this.target != null && !this.target.getTargetId().equals(this.source.getSourceId())) {
			this.target.setTargetValue(this.source.getSourceValue());
			if (!(this.contextTarget instanceof AlgoOperator)) {
				String lineOfCode = this.target.getTargetId() + " = " + this.source.getSourceId() + " ;";
				this.addLineOfCode(lineOfCode);
			}
		}

		this.setMovingValue((AlgoElement) null);
		if (this.target != null) {
			this.target.setHighLight(false);
		}

	}

	public void deleteCurrentScale() {
		this.remove(this.myScale);
		this.myScale = null;
		if (!this.stackOfScales.isEmpty()) {
			this.myScale = (Scale) this.stackOfScales.pop();
			this.myScale.setAsleep(false);
		}

		this.notifyObservers();
	}

	public AlgoElement findActiveObject(int x, int y) {
		AlgoElement activeObject = null;
		Iterator arg4 = this.varList.iterator();

		while (arg4.hasNext()) {
			AlgoElement a = (AlgoElement) arg4.next();
			if (a.includes(x, y)) {
				activeObject = a;
				break;
			}
		}

		return activeObject;
	}

	public AlgoElement findActiveValue(int x, int y) {
		Object activeValue = null;
		Iterator arg4 = this.varList.iterator();

		while (arg4.hasNext()) {
			AlgoElement a = (AlgoElement) arg4.next();
			if (a.includes(x, y)) {
				activeValue = a;
				AlgoVariable deeper = a.findActiveValue(x, y);
				if (deeper != null) {
					activeValue = deeper;
				}
				break;
			}
		}

		return (AlgoElement) activeValue;
	}

	public AlgoVariable findActiveVariable(int x, int y) {
		AlgoVariable activeObject = null;
		Iterator arg4 = this.varList.iterator();

		while (arg4.hasNext()) {
			AlgoElement a = (AlgoElement) arg4.next();
			if (a instanceof AlgoVariable && a.includes(x, y)) {
				activeObject = (AlgoVariable) a;
				break;
			}
		}

		return activeObject;
	}

	public AlgoElement findTarget(int targetX, int targetY) {
		if (this.target != null) {
			this.target.setHighLight(false);
		}

		this.target = null;
		this.contextTarget = this.findActiveObject(targetX, targetY);
		if (this.contextTarget != null) {
			AlgoElement realTarget = this.contextTarget.selectTarget(targetX, targetY);
			if (realTarget != null
					&& (this.operatorInUse && this.contextTarget instanceof AlgoOperator || !this.operatorInUse)) {
				this.target = this.contextTarget;
				this.target.setHighLight(true);
			}
		}

		return this.target;
	}

	public AlgoElement getActive() {
		return this.active;
	}

	public List<AlgoElement> getElements() {
		return this.varList;
	}

	public AlgoElement getMovingValue() {
		return this.movingValue;
	}

	public String[] getReadableProgram(Program program) {
		String[] tmp = (String[]) program.toArray(new String[0]);
		String[] result = new String[tmp.length];
		int i = 0;
		String[] arg7 = tmp;
		int arg6 = tmp.length;

		for (int arg5 = 0; arg5 < arg6; ++arg5) {
			String line = arg7[arg5];
			result[i] = convertName(line);
			++i;
		}

		return result;
	}

	public String[] getReadableProgram() {
		return this.getReadableProgram(this.myProgram);
	}

	public String getReadableConsole() {
		return convertName(this.myConsole);
	}

	public static String convertName(String line) {
		return line.replace("$", "").replace("#", "");
	}

	public AlgoElement getScale() {
		return this.myScale;
	}

	public AlgoVariable getTarget() {
		return (AlgoVariable) this.target;
	}

	public static Algorithm load(InputStream in) throws ClassNotFoundException, IOException {
		ObjectInputStream ois = new ObjectInputStream(in);
		return (Algorithm) ois.readObject();
	}

	public void notifyObservers() {
		this.setChanged();
		super.notifyObservers(this);
	}

	public void play(int startAddress) throws TouchMachineException {
		this.myVirtualMachine.run(startAddress);
		this.notifyObservers();
	}

	public void produceCode() {
		if (this.target != null && !this.source.getSourceId().equals(this.target.getTargetId())) {
			if (this.contextTarget == this.myScale) {
				this.myScale.storeCode(this.source);
			} else if (this.contextTarget == this.myExitLoop) {
				this.myExitLoop.storeCode(this.source);
			} else if (this.contextTarget == this.myOperator) {
				this.myOperator.storeCode(this.source);
			} else if (this.recording) {
				this.source.produceExpressionCode(this.myCode);
				this.target.produceAssignmentCode(this.myCode);
			}
		}

	}

	public void produceCounterCode(Variable counterTarget, TwoOperandsOperationType operationType) {
		this.addInstruction(new PushVariable(counterTarget));
		this.addInstruction(new PushValue(1));
		this.addInstruction(new TwoOperandsOperation(operationType));
		this.addInstruction(new Assignment(counterTarget));
	}

	public void produceExitCaseCode() {
		if (this.recording) {
			this.myExitLoop.produceExitCaseCode(this.myCode);
			this.myCode.setLoop();
		}

		String exitStatement = this.myExitLoop.produceExitLineOfCode();
		this.addLineOfCode(exitStatement);
	}

	public String produceTestCode() {
		if (this.recording) {
			this.myScale.produceBranchCode(this.myCode);
		}

		String ifStatement = this.myScale.produceTestLineOfCode();
		this.addLineOfCode(ifStatement);
		return ifStatement;
	}

	public void record(BLOCK_TYPE type) {
		switch ($SWITCH_TABLE$virtualTouchMachine$Code$BLOCK_TYPE()[type.ordinal()]) {
			case 1 :
				this.myCode.setFromInsertionAddress();
				this.myProgram.setFromCurrentInsertionLine();
			case 2 :
			default :
				break;
			case 3 :
				this.myCode.setCoreLoopInsertionAddress();
				this.myProgram.setLoopCurrentInsertionLine();
				break;
			case 4 :
				this.myCode.setTerminateInsertionAddress();
				this.myProgram.setTerminateCurrentInsertionLine();
		}

		this.recording = true;
	}

	public String record(int suspendedAddress, String todoStatement) {
		this.recording = true;
		this.myCode.remove(suspendedAddress);
		this.myCode.setCurrentInsertionAdress(suspendedAddress);
		int insertionLine = this.myProgram.indexOf(todoStatement);
		this.myProgram.remove(insertionLine);
		String todoComment = (String) this.myProgram.get(insertionLine);
		++insertionLine;
		this.myProgram.setCurrentInsertionLine(insertionLine);
		return todoComment;
	}

	public void remove(AlgoElement v) {
		this.varList.remove(v);
	}

	public void removeVarIndex(AlgoVarIndex v) {
		boolean found = false;
		AlgoArray associatedArray = v.getMyIndexedArray();
		if (associatedArray != null) {
			Iterator it = this.varList.iterator();

			while (it.hasNext() && !found) {
				AlgoElement e = (AlgoElement) it.next();
				if (e instanceof AlgoIndex && ((AlgoIndex) e).getVariable() == v) {
					it.remove();
					found = true;
				}
			}

			associatedArray.removeIndex(v);
		}

		this.varList.remove(v);
	}

	public void save(OutputStream out) throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(out);
		oos.writeObject(this);
	}

	public AlgoElement selectSource(int x, int y) {
		this.source = null;
		this.target = null;
		AlgoElement potentielSource = this.findActiveObject(x, y);
		if (potentielSource != null) {
			AlgoElement realSource = potentielSource.selectSource(x, y);
			if (realSource != null) {
				this.source = potentielSource;
			}
		}

		return this.source;
	}

	public void setActive(AlgoElement active) {
		if (this.active != null) {
			this.active.setSelected(false);
		}

		if (active != null) {
			this.active = active;
			this.active.setSelected(true);
			if (this.varList.remove(active)) {
				this.varList.add(0, active);
			}

			if (active instanceof AlgoArray) {
				AlgoArray algoArray = (AlgoArray) active;
				Iterator arg3 = algoArray.myIndexes.iterator();

				while (arg3.hasNext()) {
					AlgoVarIndex index = (AlgoVarIndex) arg3.next();
					this.placeIndexOnTop(index);
					this.varList.remove(index);
					this.varList.add(0, index);
				}

				this.varList.remove(algoArray.getLengthConst());
				this.varList.add(0, algoArray.getLengthConst());
			}
		} else {
			this.active = null;
		}

	}

	private void placeIndexOnTop(AlgoVarIndex index) {
		Iterator arg2 = this.varList.iterator();

		while (arg2.hasNext()) {
			AlgoElement element = (AlgoElement) arg2.next();
			if (element instanceof AlgoIndex) {
				AlgoIndex algoIndex = (AlgoIndex) element;
				if (algoIndex.myIndex == index) {
					this.varList.remove(element);
					this.varList.add(0, element);
					break;
				}
			}
		}

	}

	public void setBlind(boolean b) {
		AlgoElement.setBlind(b);
	}

	public void setHighLight(AlgoElement target) {
		this.unSetHighlight();
		this.target = target;
		this.target.setHighLight(true);
	}

	public void setMovingValue(AlgoElement movingValue) {
		if (movingValue != null) {
			this.movingValue = movingValue.getSourceCopy();
		} else {
			this.movingValue = null;
		}

	}

	public boolean isOperatorInUse() {
		return this.operatorInUse;
	}

	public void setOperatorInUse(boolean operatorInUse) {
		this.operatorInUse = operatorInUse;
	}

	public void stopRecording() {
		this.recording = false;
	}

	public void terminateCondition() {
		String currentIfStatement = this.myScale.getReverseCondition();
		String insertionLine = this.terminateConditionLineOfCode(currentIfStatement);
		if (this.recording) {
			this.terminateConditionCode(currentIfStatement, insertionLine);
		}

	}

	private void terminateConditionCode(String currentIfStatement, String todoStatement) {
		Continue endIf = new Continue();
		this.addInstruction(new Branch(BRANCH_TYPE.ALWAYS, endIf));
		Continue elseInstruction = new Continue();
		this.addInstruction(elseInstruction);
		this.myCode.set(this.myScale.getAddressToFix(), new Branch(BRANCH_TYPE.TOP_STACK_FALSE, elseInstruction));
		this.addInstruction(new Todo(currentIfStatement, todoStatement));
		this.addInstruction(endIf);
	}

	private String terminateConditionLineOfCode(String currentIfStatement) {
		this.addLineOfCode("}");
		this.addLineOfCode("else {");
		++todoNb;
		this.addLineOfCode(" // TODO " + todoNb);
		String result = this.myProgram.getLastInsertedLine();
		this.addLineOfCode(" // " + currentIfStatement);
		this.addLineOfCode("}");
		return result;
	}

	public void unSetHighlight() {
		if (this.target != null) {
			this.target.setHighLight(false);
		}

		this.target = null;
	}

	public void updateScale() {
		this.myScale.update();
		this.myScale.setConditionAsName();
		this.myScale.setOperating();
		this.notifyObservers();
	}

	public boolean unUsedName(String myName) {
		boolean validName = true;
		Iterator arg3 = this.varList.iterator();

		while (arg3.hasNext()) {
			AlgoElement a = (AlgoElement) arg3.next();
			if (a.getName().equals(myName)) {
				validName = false;
				break;
			}
		}

		return validName;
	}

	public String getConsole() {
		return this.myConsole;
	}

	public boolean containsProgram() {
		return this.myCode != null;
	}

	public void changeName(AlgoElement sourceElement, String name, String comment) throws InvalidNameException {
		sourceElement.setComment(comment);
		if (!sourceElement.getName().equals(name)) {
			if (!this.unUsedName(name)) {
				throw new InvalidNameException(name);
			}

			String oldName = sourceElement.getName();
			String oldId = sourceElement.getId();
			sourceElement.setName(name);
			this.changeAllNamesInPrograms(oldId, sourceElement.getId());
			if (sourceElement instanceof AlgoMacroCode) {
				this.changeMacroName(oldName, name);
			}
		}

	}

	private void changeMacroName(String name, String newName) {
		CodeAndProgram cp = (CodeAndProgram) this.macroTable.get(name);
		cp.getCode().setName(newName);
		this.macroTable.remove(name);
		this.macroTable.put(newName, cp);
	}

	private void changeAllNamesInPrograms(String oldName, String newName) {
		Iterator arg3 = this.macroTable.values().iterator();

		while (arg3.hasNext()) {
			CodeAndProgram cp = (CodeAndProgram) arg3.next();
			cp.getProgram().changeName(oldName, newName);
		}

	}

	public int usedElement(String id) {
		int count = 0;
		Iterator arg3 = this.macroTable.values().iterator();

		while (arg3.hasNext()) {
			CodeAndProgram cp = (CodeAndProgram) arg3.next();
			if (cp.getProgram().searchId(id)) {
				++count;
			}
		}

		return count;
	}

	public void export(File file) throws FileNotFoundException {
		PrintWriter p = new PrintWriter(file);
		Iterator arg3 = this.macroTable.keySet().iterator();

		while (arg3.hasNext()) {
			String macroName = (String) arg3.next();
			AlgoMacroCode currentMacro = this.findMacroByName(macroName);
			if (currentMacro != null) {
				p.println("// --------------------");
				p.println(currentMacro.getComment());
				p.println("// --------------------");
			}

			p.print("Define ");
			String[] arg8;
			int arg7 = (arg8 = this
					.getReadableProgram(((CodeAndProgram) this.macroTable.get(macroName)).getProgram())).length;

			for (int arg6 = 0; arg6 < arg7; ++arg6) {
				String line = arg8[arg6];
				p.println(line);
			}

			p.println();
		}

		p.close();
	}

	private AlgoMacroCode findMacroByName(String macroName) {
		AlgoMacroCode result = null;
		Iterator it = this.varList.iterator();

		while (it.hasNext() && result == null) {
			AlgoElement current = (AlgoElement) it.next();
			if (current.getName().equals(macroName) && current instanceof AlgoMacroCode) {
				result = (AlgoMacroCode) current;
			}
		}

		return result;
	}

	public void addScale(String message) {
		this.addScale();
		this.myScale.setName(message);
	}

	public void changeValue(AlgoElement sourceElement, String prompt, int value) {
		if (sourceElement instanceof AlgoVariable) {
			((AlgoVariable) sourceElement).setValue(value);
			Object instruction = null;
			switch ($SWITCH_TABLE$model$Algorithm$AlgoTypes()[sourceElement.getElementType().ordinal()]) {
				case 1 :
					instruction = new PushRead(prompt);
					break;
				case 2 :
					instruction = new PushReadChar(prompt);
			}

			this.addInstruction((Instruction) instruction);
			this.addInstruction(new Assignment((Variable) sourceElement));
			String lineOfCode = "Read " + sourceElement.getId();
			this.addLineOfCode(lineOfCode);
		}

	}

	public void clear(BLOCK_TYPE blocType) {
		switch ($SWITCH_TABLE$virtualTouchMachine$Code$BLOCK_TYPE()[blocType.ordinal()]) {
			case 1 :
				this.myProgram.clearFrom();
				this.myCode.clearFrom();
				break;
			case 2 :
				this.myProgram.clearEval();
				this.myCode.clearEval();
				break;
			case 3 :
				this.myProgram.clearLoop();
				this.myCode.clearLoop();
				break;
			case 4 :
				this.myProgram.clearTerminate();
				this.myCode.clearTerminate();
		}

		this.notifyObservers();
	}

	public String validateScale(TwoOperandsOperationType testType) {
		this.myScale.setTestType(testType);
		return this.produceTestCode();
	}

	public TwoOperandsOperationType getCondition() {
		this.setOperatorInUse(false);
		this.updateScale();
		return this.myScale.getCondition();
	}

	public void addDoneScale(String toDoMessage) {
		this.addScale(toDoMessage);
		this.myScale.setDone();
		this.setOperatorInUse(false);
	}

	public void arrayIterate(AlgoIndex index) throws TouchMachineException {
		AlgoVariable cursor = index.getVariable();
		int myArraySize = index.getArray().getSize();
		cursor.setValue(0);

		while (cursor.getValue() < myArraySize) {
			this.play(0);
			cursor.incValue();
		}

		this.notifyObservers();
	}

	public void addOperator(String operation, AlgoElement varSelected) {
		this.myOperator = new AlgoAddSub(600, 150, operation, varSelected);
		this.add(this.myOperator);
		this.setActive(this.myOperator);
		this.setOperatorInUse(true);
	}

	public void terminateCurrentOperator() {
		this.setOperatorInUse(false);
		String lineOfCode = this.myOperator.terminate();
		this.addLineOfCode(lineOfCode);
		if (this.recording) {
			this.myOperator.produceOperatorCode(this.myCode);
		}

		this.remove(this.myOperator);
	}

	public void push(int valueRead) {
		this.myVirtualMachine.pushValue(valueRead);
	}

	public void createVM() {
		this.myVirtualMachine = new TouchMachine(this.myCode);
	}

	public int getStartAddress(BLOCK_TYPE type) {
		int address;
		switch ($SWITCH_TABLE$virtualTouchMachine$Code$BLOCK_TYPE()[type.ordinal()]) {
			case 1 :
				address = this.myCode.getFromInsertionAddress();
				break;
			case 2 :
			default :
				address = 0;
				break;
			case 3 :
				address = this.myCode.getCoreLoopInsertionAddress();
				break;
			case 4 :
				address = this.myCode.getTerminateInsertionAddress();
		}

		return address;
	}

	public void addExitLoop(boolean firstPlace) {
		this.myExitLoop = new ExitLoopManager(600, 150);
		this.myExitLoop.setInsertPlace(firstPlace);
		this.myProgram.setEvalCurrentInsertionLine(firstPlace);
		this.add(this.myExitLoop);
		this.setOperatorInUse(true);
		this.recording = true;
	}

	public void evalExitCondition() {
		this.myExitLoop.update();
		this.myExitLoop.setConditionAsName();
	}

	public TwoOperandsOperationType getExitCondition() {
		return this.myExitLoop.getCondition();
	}

	public void validateExitCondition(TwoOperandsOperationType testType) {
		this.myExitLoop.setTestType(testType);
		this.myExitLoop.setConditionAsName();
		this.myExitLoop.setOperating();
		this.produceExitCaseCode();
	}

	public void terminateExitLoopManager() {
		this.remove(this.myExitLoop);
		this.recording = false;
		this.setOperatorInUse(false);
	}

	public void addMacro(String name, String comment) {
		this.myCode = new Code(name);
		this.myProgram = new Program(name);
		this.macroTable.put(name, new CodeAndProgram(this.myCode, this.myProgram));
		this.myAlgoCode = new AlgoMacroCode(name, comment);
		this.add(this.myAlgoCode);
		this.setActive(this.myAlgoCode);
	}

	public void setMacro(String name) {
		CodeAndProgram macro = (CodeAndProgram) this.macroTable.get(name);
		this.myCode = macro.getCode();
		this.myProgram = macro.getProgram();
	}

	public void createVM(String name) {
		Code codeToExecute = ((CodeAndProgram) this.macroTable.get(name)).getCode();
		this.myVirtualMachine = new TouchMachine(codeToExecute);
	}

	public void createCallMacro(String name) {
		Code codeToExecute = ((CodeAndProgram) this.macroTable.get(name)).getCode();
		this.addInstruction(new CallMacroCode(codeToExecute));
	}

	public boolean isRunning() {
		boolean result;
		if (this.myCode == this.myVirtualMachine.getCode()) {
			result = true;
		} else {
			result = false;
		}

		return result;
	}

	public boolean isEmpty(BLOCK_TYPE type) {
		return this.myCode.isEmpty(type);
	}

	public String getReadableCode() {
		return this.myCode.toString();
	}

	public int getRestartAddress() {
		return this.myCode.getCurrentInsertionAdress();
	}

	public int getCurrentProgramLine() {
		return this.myProgram.getCurrentInsertionLine();
	}

	public void updateProgramIndexFromExceptionMessage(String message) {
		String key = this.convertMessageToKey(message);
		this.myProgram.setCurrentEndBlockLine(key);
	}

	private String convertMessageToKey(String message) {
		String result = "";
		if (message.equals("End From")) {
			result = "From";
		} else if (message.equals("End Eval")) {
			result = "Until";
		} else if (message.equals("End core loop")) {
			result = "Loop";
		} else if (message.equals("End algorithm")) {
			result = "End";
		}

		return result;
	}

	public String getScaleLeftOperand() {
		return convertName(this.myScale.leftLineOfCode);
	}

	public String getScaleRightOperand() {
		return convertName(this.myScale.rightLineOfCode);
	}

	public String getExitLeftOperand() {
		return convertName(this.myExitLoop.leftLineOfCode);
	}

	public String getExitRightOperand() {
		return convertName(this.myExitLoop.rightLineOfCode);
	}

	public void updateProgramIndex(String blocTitle) {
		this.myProgram.setCurrentLine(blocTitle);
	}

	public String findProgramBlock(int line) {
		return this.myProgram.findBlock(line);
	}

	public void clean() {
		this.myScale = null;
		this.stackOfScales.clear();
		this.myOperator = null;
		this.myExitLoop = null;
	}

	public void removeArray(AlgoArray who) {
		this.remove(who);
		this.remove(who.getConstantLength());
		Iterator e = who.myIndexes.iterator();

		while (e.hasNext()) {
			AlgoVarIndex it = (AlgoVarIndex) e.next();
			it.setMyIndexedArray((AlgoArray) null);
		}

		Iterator it1 = this.varList.iterator();

		while (it1.hasNext()) {
			AlgoElement e1 = (AlgoElement) it1.next();
			if (e1 instanceof AlgoIndex && ((AlgoIndex) e1).getArray() == who) {
				it1.remove();
			}
		}

	}

	public void removeMacro(AlgoMacroCode who) {
		this.varList.remove(who);
		this.macroTable.remove(who.getName());
	}

	public String getCurrentMacroName() {
		return this.myCode.getName();
	}

	public boolean hasRemoved(AlgoElement who) {
		boolean removed = false;
		int elementOccurences = this.usedElement(who.getId());
		if (who instanceof AlgoVarIndex) {
			if (elementOccurences == 0) {
				this.removeVarIndex((AlgoVarIndex) who);
				removed = true;
			}
		} else if (who instanceof AlgoVariable) {
			if (elementOccurences == 0) {
				this.remove(who);
				removed = true;
			}
		} else if (who instanceof AlgoArray) {
			if (elementOccurences == 0) {
				this.removeArray((AlgoArray) who);
				removed = true;
			}
		} else if (who instanceof AlgoMacroCode && elementOccurences == 1
				&& !this.getCurrentMacroName().equals(who.getName())) {
			this.removeMacro((AlgoMacroCode) who);
			removed = true;
		}

		return removed;
	}

	public void clearConsole() {
		this.myConsole = new String();
	}
}
