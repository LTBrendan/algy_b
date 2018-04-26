package control;

import control.GenerationType;
import control.Messages;
import control.Controller.ANSWER_TYPE;
import control.Controller.EXECUTION_STATE;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import model.AlgoAddSub;
import model.AlgoArray;
import model.AlgoConst;
import model.AlgoElement;
import model.AlgoIndex;
import model.AlgoMacroCode;
import model.AlgoVarIndex;
import model.AlgoVariable;
import model.Algorithm;
import model.ConditionMaker;
import model.ExitLoopManager;
import model.InvalidNameException;
import model.Scale;
import model.Algorithm.AlgoTypes;
import view.AlgoTouchFrame;
import virtualTouchMachine.EndBlocException;
import virtualTouchMachine.SuspendExecutionException;
import virtualTouchMachine.SuspendForReadException;
import virtualTouchMachine.TouchMachineException;
import virtualTouchMachine.TwoOperandsOperationType;
import virtualTouchMachine.Code.BLOCK_TYPE;

public class Controller {
	public static final String REPLAY_THE_MACRO_CODE_TO_RECORD_THE_MISSING_PART = "Controller.RECORD_MACRO_CODE_MISSING_PART";
	public static final String CONTROLLER_RECORDING_MISSING_INSTRUCTIONS = "Controller.RECORDING_MISSING_INSTRUCTIONS";
	private static final String CONTROLLER_INVALID_NAME = "Controller.INVALID_NAME";
	private Algorithm myAlgorithm;
	private AlgoVariable counterTarget;
	private AlgoElement sourceElement;
	private boolean modified = false;
	private boolean recording = false;
	private AlgoTouchFrame myFrame;
	private int loopBound;
	private AlgoVariable cursor;
	private String currentMode;
	private BLOCK_TYPE type;
	private int currentExecutionAddress;
	private EXECUTION_STATE state;

	public Controller(Algorithm algorithm) {
		this.state = EXECUTION_STATE.IDLE;
		this.myAlgorithm = algorithm;
	}

	public void addArray(String name, String comment, AlgoTypes algoType) {
		byte min = 0;
		byte max = 100;
		switch ($SWITCH_TABLE$model$Algorithm$AlgoTypes()[algoType.ordinal()]) {
			case 2 :
				min = 97;
				max = 122;
			case 1 :
			default :
				int[] content = AlgoArray.randomContent(10, min, max);
				AlgoArray a = new AlgoArray(name, comment, algoType, content);
				String typeToken;
				switch ($SWITCH_TABLE$model$Algorithm$AlgoTypes()[algoType.ordinal()]) {
					case 1 :
						typeToken = "int";
						break;
					case 2 :
						typeToken = "char";
						break;
					default :
						typeToken = "unknown";
				}

				this.myAlgorithm.addDeclaration(typeToken + "[] " + a.getId() + " ;");
				this.myAlgorithm.addDeclaration(a.getId() + " = new " + typeToken + "[" + 10 + "] ;");
				this.myAlgorithm.add(a);
				this.createArrayLengthConst(a);
				this.myAlgorithm.setActive(a);
				this.myFrame.setPossibleActions(a);
				this.modified = true;
				this.myAlgorithm.notifyObservers();
		}
	}

	private void createArrayLengthConst(AlgoArray a) {
		String constName = a.getName() + ".length";
		AlgoConst arrayLength = new AlgoConst(constName, a.getSize());
		arrayLength.move(-arrayLength.getX(), -arrayLength.getY());
		arrayLength.move(a.getX() - 32, a.getY() + a.getHeight() - 32);
		a.addLengthConst(arrayLength);
		this.myAlgorithm.add(arrayLength);
	}

	public void addArray(String myName, String myRole, AlgoTypes algoType, int mySize, int myCste, int myMin, int myMax,
			int myMinRange, int myMaxRange, int[] content, GenerationType generationType) {
		content = this.makeContent(mySize, myCste, myMin, myMax, myMinRange, myMaxRange, generationType, content);
		AlgoArray a = new AlgoArray(myName, myRole, algoType, content);
		String typeToken;
		switch ($SWITCH_TABLE$model$Algorithm$AlgoTypes()[algoType.ordinal()]) {
			case 1 :
				typeToken = "int";
				break;
			case 2 :
				typeToken = "char";
				break;
			default :
				typeToken = "void";
		}

		this.myAlgorithm.addDeclaration(typeToken + "[] " + myName + " ;");
		this.myAlgorithm.addDeclaration(myName + " = new " + typeToken + "[" + mySize + "] ;");
		this.myAlgorithm.add(a);
		this.createArrayLengthConst(a);
		this.modified = true;
		this.myAlgorithm.notifyObservers();
	}

	private int[] makeContent(int mySize, int myCste, int myMin, int myMax, int myMinRange, int myMaxRange,
			GenerationType generationType, int[] content) {
		int tmp;
		switch ($SWITCH_TABLE$control$GenerationType()[generationType.ordinal()]) {
			case 1 :
				Arrays.fill(content, myCste);
				break;
			case 2 :
				for (tmp = 0; tmp < mySize; ++tmp) {
					content[tmp] = myMin + tmp;
				}

				return content;
			case 3 :
				for (tmp = 0; tmp < mySize; ++tmp) {
					content[tmp] = myMax - tmp;
				}

				return content;
			case 4 :
				content = AlgoArray.randomContent(mySize, myMinRange, myMaxRange);
				break;
			case 5 :
				content = AlgoArray.randomContent(mySize, myMinRange, myMaxRange);
				Arrays.sort(content);
				break;
			case 6 :
				content = AlgoArray.randomContent(mySize, myMinRange, myMaxRange);
				Arrays.sort(content);

				for (int i = 0; i < mySize / 2; ++i) {
					tmp = content[i];
					content[i] = content[mySize - i - 1];
					content[mySize - i - 1] = tmp;
				}
			case 7 :
			default :
				break;
			case 8 :
				AlgoArray.shuffleContent(content);
		}

		return content;
	}

	public void addIndex(String name, String comment) {
		AlgoElement selected = this.myAlgorithm.getActive();
		if (selected instanceof AlgoArray) {
			AlgoArray selectedArray = (AlgoArray) selected;
			AlgoVarIndex index = new AlgoVarIndex(name, comment);
			index.move(-index.getX(), -index.getY());
			index.setMyIndexedArray(selectedArray);
			index.move(selectedArray.getX() + 64, selectedArray.getY() + selectedArray.getHeight() + 64 + 5);
			AlgoIndex algoIndex = new AlgoIndex(index, selectedArray);
			this.myAlgorithm.add(index);
			this.myAlgorithm.add(algoIndex);
			this.myAlgorithm.setActive(index);
			this.myFrame.setPossibleActions(index);
			this.myAlgorithm.addDeclaration("int " + index.getId() + " ;");
			this.modified = true;
			this.myAlgorithm.notifyObservers();
		} else {
			this.myFrame.showMessageDialog(Messages.getString("NO_SELECTED_ARRAY"));
		}

	}

	public void addConstant(String name, String comment, AlgoTypes constType, int value) {
		AlgoConst a = new AlgoConst(name, value);
		a.setComment(comment);
		a.setElementType(constType);
		this.myAlgorithm.add(a);
		this.myAlgorithm.setActive(a);
		this.myFrame.setPossibleActions(a);
		this.modified = true;
		String typeToken;
		switch ($SWITCH_TABLE$model$Algorithm$AlgoTypes()[constType.ordinal()]) {
			case 1 :
				typeToken = "int";
				break;
			case 2 :
				typeToken = "char";
				break;
			default :
				typeToken = "void";
		}

		this.myAlgorithm.addDeclaration("const " + typeToken + " " + a.getId() + " = " + value + " ;");
		this.myAlgorithm.notifyObservers();
	}

	public void addVariable(String name, String comment, AlgoTypes varType) {
		AlgoVariable a = new AlgoVariable(name, comment);
		a.setElementType(varType);
		String typeToken;
		switch ($SWITCH_TABLE$model$Algorithm$AlgoTypes()[varType.ordinal()]) {
			case 1 :
				typeToken = "int";
				a.setValue(0);
				break;
			case 2 :
				typeToken = "char";
				a.setValue(63);
				break;
			default :
				typeToken = "void";
		}

		this.myAlgorithm.add(a);
		this.myAlgorithm.setActive(a);
		this.myFrame.setPossibleActions(a);
		this.modified = true;
		this.myAlgorithm.addDeclaration(typeToken + " " + a.getId() + " ;");
		this.myAlgorithm.notifyObservers();
	}

	public boolean checkValidName(String myName) {
		boolean nameOk = true;
		if (!myName.equals("") && myName.matches("[a-zA-Z][a-zA-Z0-9]*")) {
			nameOk = this.myAlgorithm.unUsedName(myName);
		} else {
			nameOk = false;
		}

		return nameOk;
	}

	public void clear() {
		ANSWER_TYPE answer = this.myFrame.askForChoice(Messages.getString("WIPE"), Messages.getString("SURE"),
				Messages.getString("NO"), Messages.getString("YES"));
		if (answer == ANSWER_TYPE.YES) {
			this.myAlgorithm.clear(this.type);
			this.modified = true;
		}

	}

	public void count(int mousePressedX, int mousePressedY) {
		if (this.counterTarget != null) {
			String lineOfCode = this.counterTarget.getId() + " = " + this.counterTarget.getId();
			if (mousePressedX < this.counterTarget.getX()) {
				this.counterTarget.incValue();
				this.modified = true;
				this.myAlgorithm.addLineOfCode(lineOfCode + " + 1;");
				if (this.recording) {
					this.myAlgorithm.produceCounterCode(this.counterTarget, TwoOperandsOperationType.ADD);
				}
			} else if (mousePressedX > this.counterTarget.getX() + this.counterTarget.getWidth()) {
				this.counterTarget.decValue();
				this.modified = true;
				this.myAlgorithm.addLineOfCode(lineOfCode + " - 1;");
				if (this.recording) {
					this.myAlgorithm.produceCounterCode(this.counterTarget, TwoOperandsOperationType.SUB);
				}
			}

			this.counterTarget.setHighLight(false);
			this.counterTarget = null;
		}

		this.myAlgorithm.notifyObservers();
	}

	public void deleteScale() {
		this.myAlgorithm.terminateCondition();
		this.myAlgorithm.deleteCurrentScale();
		if (this.myAlgorithm.getScale() == null) {
			this.myFrame.enableStop();
			this.myFrame.enableScale();
		}

	}

	public void deselectElement() {
		this.myAlgorithm.setActive((AlgoElement) null);
		this.myAlgorithm.notifyObservers();
	}

	public void deselectValue() {
		this.myAlgorithm.setMovingValue((AlgoElement) null);
		this.myAlgorithm.unSetHighlight();
		this.myAlgorithm.notifyObservers();
	}

	public void drop() {
		this.myAlgorithm.assignSourceToTarget();
		this.myAlgorithm.produceCode();
		this.myAlgorithm.notifyObservers();
	}

	public Algorithm getAlgorithm() {
		return this.myAlgorithm;
	}

	public AlgoElement getCurrentElement() {
		return this.sourceElement;
	}

	public boolean isModified() {
		return this.modified;
	}

	public void load(File file) throws ClassNotFoundException, IOException {
		FileInputStream in = new FileInputStream(file);
		this.myAlgorithm = Algorithm.load(in);
		this.myAlgorithm.clean();
		this.setAlgorithmToViews(file.getName());
		in.close();
		this.myFrame.setNormalMode();
		if (this.myAlgorithm.containsProgram()) {
			this.myFrame.enableClearAndPlayMode();
		}

		this.modified = false;
	}

	public void moveElement(int x, int y) {
		AlgoElement selected = this.myAlgorithm.getActive();
		if (selected != null) {
			selected.move(x, y);
			this.modified = true;
			this.myAlgorithm.notifyObservers();
		}

	}

	public void moveValue(int dx, int dy, int targetX, int targetY) {
		AlgoElement selected = this.myAlgorithm.getMovingValue();
		if (selected != null) {
			this.myAlgorithm.findTarget(targetX, targetY);
			selected.move(dx, dy);
			this.myAlgorithm.notifyObservers();
		}

	}

	public void newAlgorithm() {
		this.myAlgorithm = new Algorithm();
		this.setAlgorithmToViews("New algo");
	}

	private void setAlgorithmToViews(String algoFileName) {
		this.myFrame.setAlgorithmView(this.myAlgorithm);
		this.myFrame.setCodeView(this.myAlgorithm);
		this.myFrame.setApplicationTitle(algoFileName);
		this.myFrame.setNormalMode();
		this.myAlgorithm.notifyObservers();
		this.modified = false;
	}

	public void play() {
		this.myFrame.setEnabledProgramListActions(false);
		int startAddress = this.myAlgorithm.getStartAddress(this.type);
		this.playAtThisAddress(startAddress);
		this.myFrame.setEnabledProgramListActions(true);
	}

	private void playAtThisAddress(int startAddress) {
		boolean resume = true;
		this.myAlgorithm.createVM();

		while (resume) {
			try {
				this.myAlgorithm.play(startAddress);
				resume = false;
			} catch (SuspendExecutionException arg5) {
				if (this.myAlgorithm.isRunning()) {
					String val1 = this.myAlgorithm.record(arg5.getSuspendedAddress(), arg5.getTodoStatement());
					val1 = Algorithm.convertName(val1);
					this.myFrame.showMessageDialog(
							val1 + '\n' + Messages.getString("Controller.RECORDING_MISSING_INSTRUCTIONS"));
					this.myFrame.recordingMode();
					this.recording = true;
					this.myFrame.enableStopInsert(val1);
					this.myAlgorithm.addDoneScale(val1);
					this.myAlgorithm.notifyObservers();
				} else {
					this.myFrame.showMessageDialog(Algorithm.convertName(arg5.getMessage()) + "\n"
							+ Messages.getString("Controller.RECORD_MACRO_CODE_MISSING_PART"));
				}

				resume = false;
			} catch (SuspendForReadException arg6) {
				String prompt = arg6.getPrompt();
				int val = this.myFrame.getInputValue(prompt, arg6.getMessage());
				this.myAlgorithm.push(val);
				startAddress = arg6.getSuspendedAddress() + 1;
			} catch (EndBlocException arg7) {
				if (this.myAlgorithm.isRunning()) {
					resume = false;
					this.myAlgorithm.notifyObservers();
				} else {
					startAddress = arg7.getSuspendedAddress() + 1;
				}
			} catch (TouchMachineException arg8) {
				this.myAlgorithm.notifyObservers();
				this.myFrame.showMessageDialog(arg8.getMessage());
				resume = false;
			}
		}

	}

	public void record() {
		if (!this.myAlgorithm.isEmpty(this.type)) {
			ANSWER_TYPE answer = this.myFrame.askForChoice(Messages.getString("HOW_TO_RECORD"),
					Messages.getString("CODE_IS_NOT_EMPTY"), Messages.getString("ADD_CODE"),
					Messages.getString("CLEAR_CODE"));
			if (answer == ANSWER_TYPE.YES) {
				this.myAlgorithm.clear(this.type);
			} else if (answer == ANSWER_TYPE.CANCEL) {
				return;
			}
		}

		this.myFrame.recordingMode();
		this.myFrame.setEnabledProgramListActions(false);
		this.myAlgorithm.record(this.type);
		this.myAlgorithm.notifyObservers();
		this.recording = true;
	}

	public void save(File file) throws IOException {
		FileOutputStream out = new FileOutputStream(file);
		this.myAlgorithm.save(out);
		out.close();
		this.myFrame.setApplicationTitle(file.getName());
		this.modified = false;
	}

	public AlgoElement selectElement(int x, int y) {
		AlgoElement v = this.myAlgorithm.findActiveValue(x, y);
		this.myAlgorithm.setActive(v);
		this.myFrame.setPossibleActions(v);
		this.myAlgorithm.notifyObservers();
		return v;
	}

	public void selectIncDecValue(int mousePressedX, int x, int y) {
		this.counterTarget = this.myAlgorithm.findActiveVariable(x, y);
		if (this.counterTarget instanceof AlgoConst) {
			this.counterTarget = null;
		}

		if (this.counterTarget != null) {
			if (mousePressedX > this.counterTarget.getX()
					&& mousePressedX < this.counterTarget.getX() + this.counterTarget.getWidth()) {
				this.counterTarget = null;
				this.myAlgorithm.unSetHighlight();
			} else {
				this.myAlgorithm.setHighLight(this.counterTarget);
			}
		} else {
			this.myAlgorithm.unSetHighlight();
		}

		this.myAlgorithm.notifyObservers();
	}

	public AlgoElement selectSource(int x, int y) {
		this.sourceElement = this.myAlgorithm.selectSource(x, y);
		if (this.sourceElement != null) {
			this.myAlgorithm.setMovingValue(this.sourceElement.getSourceCopy());
			this.myAlgorithm.notifyObservers();
		}

		return this.sourceElement;
	}

	public void modifyElementName(AlgoElement who, String name, String comment) {
		try {
			this.myAlgorithm.changeName(who, name, comment);
			this.modified = true;
			if (who instanceof AlgoArray) {
				AlgoConst e = ((AlgoArray) who).getLengthConst();
				this.myAlgorithm.changeName(e, name + ".length", comment);
			}

			this.myAlgorithm.notifyObservers();
		} catch (InvalidNameException arg4) {
			this.myFrame.showMessageDialog(arg4.getMessage() + "\n" + Messages.getString("Controller.INVALID_NAME"));
		}

	}

	public void setDisplayMode(String mode) {
		if (mode.equals("BLIND_MODE")) {
			this.myAlgorithm.setBlind(true);
		} else {
			this.myAlgorithm.setBlind(false);
		}

		this.myAlgorithm.notifyObservers();
	}

	public void setView(AlgoTouchFrame frame) {
		this.myFrame = frame;
		this.setCurrentMode("LOOP");
	}

	public AlgoElement showElement(int x, int y) {
		this.sourceElement = this.myAlgorithm.findActiveValue(x, y);
		return this.sourceElement;
	}

	public void showScale() {
		this.myFrame.disableAll();
		this.myAlgorithm.addScale();
		this.myAlgorithm.notifyObservers();
	}

	public void startScale() {
		this.myFrame.enableAll();
		TwoOperandsOperationType testType = this.myAlgorithm.getCondition();
		String leftOp = this.myAlgorithm.getScaleLeftOperand();
		String rightOp = this.myAlgorithm.getScaleRightOperand();
		testType = this.myFrame.validateConditionType(testType, leftOp, rightOp);
		String currentIfStatement = this.myAlgorithm.validateScale(testType);
		this.myFrame.enableStopCheck(currentIfStatement);
		this.myFrame.showMessageDialog(Messages.getString("CONTINUE_IF_CASE"));
	}

	public void stopRecording() {
		this.myAlgorithm.stopRecording();
		this.recording = false;
		this.myFrame.setEnabledProgramListActions(true);
	}

	public void export(File file) {
		try {
			this.myAlgorithm.export(file);
		} catch (FileNotFoundException arg2) {
			arg2.printStackTrace();
		}

	}

	public void wipeScale() {
		this.myAlgorithm.deleteCurrentScale();
		this.stopRecording();
		this.playAtThisAddress(this.myAlgorithm.getRestartAddress());
		this.myFrame.enableClearAndPlayMode();
	}

	public void cancelScale() {
		this.myAlgorithm.setOperatorInUse(false);
		this.myAlgorithm.deleteCurrentScale();
		this.myFrame.enableAll();
		this.myFrame.enableScale();
	}

	public void setCurrentVariableValue(String prompt, int value) {
		this.myAlgorithm.changeValue(this.sourceElement, prompt, value);
		this.modified = true;
	}

	public AlgoElement getActiveElement() {
		return this.myAlgorithm.getActive();
	}

	public void activateElement(int x, int y) {
		AlgoElement element = this.myAlgorithm.findActiveObject(x, y);
		if (element != null) {
			if (element instanceof Scale) {
				ConditionMaker selectedOperator = (ConditionMaker) element;
				if (selectedOperator.isReadyToCompare()) {
					this.startScale();
				} else if (selectedOperator.isOperating()) {
					this.deleteScale();
				} else if (selectedOperator.isDone()) {
					this.wipeScale();
				} else {
					this.cancelScale();
				}
			} else if (element instanceof ExitLoopManager) {
				ExitLoopManager selectedOperator1 = (ExitLoopManager) element;
				if (selectedOperator1.isReadyToCompare()) {
					this.startExitLoopManager();
				} else if (selectedOperator1.isOperating()) {
					this.wipeExitLoopManager();
					this.myFrame.setEnabledProgramListActions(true);
				} else {
					selectedOperator1.isDone();
				}
			} else if (element instanceof AlgoIndex) {
				AlgoIndex selectedOperator2 = (AlgoIndex) element;
				this.arrayIterate(selectedOperator2);
			} else if (element instanceof AlgoAddSub) {
				AlgoAddSub selectedOperator3 = (AlgoAddSub) element;
				if (selectedOperator3.isReady()) {
					this.wipeOperator();
				}
			} else if (element instanceof AlgoMacroCode) {
				this.executeMacro(element);
			}
		}

	}

	public void startExitLoopManager() {
		this.myAlgorithm.evalExitCondition();
		TwoOperandsOperationType testType = this.myAlgorithm.getExitCondition();
		String leftOperand = this.myAlgorithm.getExitLeftOperand();
		String rightOperand = this.myAlgorithm.getExitRightOperand();
		testType = this.myFrame.validateConditionType(testType, leftOperand, rightOperand);
		this.myAlgorithm.validateExitCondition(testType);
	}

	private void wipeExitLoopManager() {
		this.myFrame.enableAll();
		this.myAlgorithm.terminateExitLoopManager();
	}

	private void wipeOperator() {
		this.myFrame.enableAll();
		this.myAlgorithm.terminateCurrentOperator();
	}

	private void arrayIterate(AlgoIndex index) {
		this.initLoop(index);
		this.myFrame.startArrayIterateTimer();
	}

	public void initLoop(AlgoIndex index) {
		this.cursor = index.getVariable();
		this.cursor.setValue(0);
		this.loopBound = index.getArray().getSize();
		this.myAlgorithm.createVM();
	}

	public boolean hasLoopNextStep() {
		return this.cursor.getValue() < this.loopBound;
	}

	public void nextLoopStep() {
		this.play();
		this.cursor.incValue();
	}

	public void showOperator(String operation) {
		AlgoElement selected = this.myAlgorithm.getActive();
		if (selected != null) {
			this.myAlgorithm.addOperator(operation, selected);
			this.myFrame.disableAll();
			this.myAlgorithm.notifyObservers();
		}

	}

	public void setCurrentMode(String mode) {
		String blocTitle = "";
		this.currentMode = mode;
		if (this.currentMode.equals("FROM")) {
			this.type = BLOCK_TYPE.FROM;
			blocTitle = "From";
		} else if (this.currentMode.equals("LOOP")) {
			this.type = BLOCK_TYPE.CORE_LOOP;
			blocTitle = "Loop";
		} else if (this.currentMode.equals("TERMINATE")) {
			this.type = BLOCK_TYPE.TERMINATE;
			blocTitle = "Terminate";
		}

		this.myFrame.setEnabledProgramListActions(false);
		this.myAlgorithm.updateProgramIndex(blocTitle);
		this.myAlgorithm.notifyObservers();
		this.myFrame.setEnabledProgramListActions(true);
	}

	public void showExitLoopManager() {
		boolean firstPlace = true;
		if (!this.myAlgorithm.isEmpty(BLOCK_TYPE.EVAL)) {
			ANSWER_TYPE answer = this.myFrame.askForChoice(Messages.getString("FIRST_PLACE"),
					Messages.getString("CONDITIONS_ALREADY_EXIST"), Messages.getString("INSERT_CONDITION"),
					Messages.getString("ADD_CONDITION"));
			if (answer == ANSWER_TYPE.YES) {
				firstPlace = false;
			} else if (answer == ANSWER_TYPE.CANCEL) {
				return;
			}
		}

		this.myFrame.disableAll();
		this.myFrame.setEnabledProgramListActions(false);
		this.myAlgorithm.addExitLoop(firstPlace);
		this.myAlgorithm.notifyObservers();
	}

	public void playAll() {
		if (this.state == EXECUTION_STATE.IDLE) {
			this.myFrame.disableAll();
		}

		this.myFrame.setEnabledProgramListActions(false);
		this.state = EXECUTION_STATE.STOPPED;
		this.myFrame.enableExecutionControl();
		this.currentExecutionAddress = this.myAlgorithm.getStartAddress(BLOCK_TYPE.FROM);
		this.myAlgorithm.updateProgramIndexFromExceptionMessage("");
		this.myAlgorithm.createVM();
		this.myAlgorithm.notifyObservers();
	}

	private void runOrResume() {
		boolean resume = true;
		this.myFrame.setEnabledProgramListActions(false);

		while (resume) {
			try {
				this.myAlgorithm.play(this.currentExecutionAddress);
				resume = false;
				this.state = EXECUTION_STATE.TERMINATED;
			} catch (SuspendForReadException arg4) {
				String prompt = arg4.getPrompt();
				int val = this.myFrame.getInputValue(prompt, arg4.getMessage());
				this.myAlgorithm.push(val);
				this.currentExecutionAddress = arg4.getSuspendedAddress() + 1;
			} catch (EndBlocException arg5) {
				if (this.myAlgorithm.isRunning()) {
					this.myAlgorithm.updateProgramIndexFromExceptionMessage(arg5.getMessage());
					this.myAlgorithm.notifyObservers();
					this.currentExecutionAddress = arg5.getSuspendedAddress() + 1;
					resume = false;
					this.state = EXECUTION_STATE.STOPPED;
				} else {
					this.currentExecutionAddress = arg5.getSuspendedAddress() + 1;
				}
			} catch (TouchMachineException arg6) {
				this.myAlgorithm.notifyObservers();
				this.myFrame.showMessageDialog(arg6.getMessage());
				resume = false;
				this.state = EXECUTION_STATE.ON_ERROR;
			}
		}

		switch ($SWITCH_TABLE$control$Controller$EXECUTION_STATE()[this.state.ordinal()]) {
			case 2 :
			case 4 :
				this.abort();
			case 1 :
			case 3 :
			default :
		}
	}

	public void modifyArray(AlgoArray myArray, int myCste, int myMin, int myMax, int myMinValue, int myMaxValue,
			int[] myContent, GenerationType generationType) {
		if (generationType == GenerationType.ENUMERATED) {
			myArray.setContent(myContent);
		} else {
			int[] content = myArray.getContent();
			content = this.makeContent(myArray.getSize(), myCste, myMin, myMax, myMinValue, myMaxValue, generationType,
					content);
			myArray.setContent(content);
		}

		this.modified = true;
		this.myAlgorithm.notifyObservers();
	}

	public void clearExit() {
		ANSWER_TYPE answer = this.myFrame.askForChoice(Messages.getString("WIPE"), Messages.getString("SURE"),
				Messages.getString("NO"), Messages.getString("YES"));
		if (answer == ANSWER_TYPE.YES) {
			this.myAlgorithm.clear(BLOCK_TYPE.EVAL);
			this.modified = true;
		}

	}

	public void addMacro(String name, String comment) {
		this.myAlgorithm.addMacro(name, comment);
		this.modified = true;
		this.myAlgorithm.notifyObservers();
	}

	public void checkMacroSelected() {
		AlgoElement element = this.getActiveElement();
		if (element instanceof AlgoMacroCode) {
			this.myAlgorithm.setMacro(element.getName());
			this.myAlgorithm.notifyObservers();
		}

	}

	public void executeSelectedMacro() {
		AlgoElement element = this.getActiveElement();
		this.executeMacro(element);
	}

	private void executeMacro(AlgoElement element) {
		if (element instanceof AlgoMacroCode) {
			int startAddress = 0;
			boolean resume = true;
			if (this.recording) {
				this.myAlgorithm.createCallMacro(element.getName());
			}

			this.myAlgorithm.addLineOfCode(element.getId());
			this.myAlgorithm.createVM(element.getName());

			while (resume) {
				try {
					this.myAlgorithm.play(startAddress);
					resume = false;
				} catch (SuspendForReadException arg6) {
					String prompt = arg6.getPrompt();
					int val = this.myFrame.getInputValue(prompt, arg6.getMessage());
					this.myAlgorithm.push(val);
					startAddress = arg6.getSuspendedAddress() + 1;
				} catch (EndBlocException arg7) {
					startAddress = arg7.getSuspendedAddress() + 1;
				} catch (TouchMachineException arg8) {
					this.myAlgorithm.notifyObservers();
					this.myFrame.showMessageDialog(arg8.getMessage());
					resume = false;
				}
			}
		}

	}

	public void next() {
		this.runOrResume();
	}

	public void abort() {
		this.state = EXECUTION_STATE.IDLE;
		this.myFrame.stopTimer();
		this.myFrame.disableExecutionControl();
		this.myFrame.setEnabledProgramListActions(true);
		this.myFrame.enableAll();
	}

	public void auto() {
		this.myFrame.enablePause();
		this.state = EXECUTION_STATE.STOPPED;
		this.myFrame.startGeneralLoopTimer();
	}

	public void generalLoopTimer() {
		switch ($SWITCH_TABLE$control$Controller$EXECUTION_STATE()[this.state.ordinal()]) {
			case 1 :
			default :
				break;
			case 2 :
			case 4 :
				this.myFrame.stopTimer();
				break;
			case 3 :
				this.runOrResume();
		}

	}

	public void pause() {
		this.myFrame.enableLoop();
		this.myFrame.stopTimer();
	}

	public AlgoElement findElement(int x, int y) {
		AlgoElement v = this.myAlgorithm.findActiveObject(x, y);
		return v;
	}

	public void programLineSelected(int firstIndex) {
		this.currentMode = this.myAlgorithm.findProgramBlock(firstIndex);
		if (this.currentMode.equals("FROM")) {
			this.type = BLOCK_TYPE.FROM;
		} else if (this.currentMode.equals("LOOP")) {
			this.type = BLOCK_TYPE.CORE_LOOP;
		} else if (this.currentMode.equals("TERMINATE")) {
			this.type = BLOCK_TYPE.TERMINATE;
		}

		this.myFrame.setCurrentBlock(this.currentMode);
	}

	public void deleteElement(AlgoElement who) {
		if (this.myAlgorithm.hasRemoved(who)) {
			this.modified = true;
			this.myAlgorithm.notifyObservers();
		} else {
			this.myFrame.showMessageDialog(Messages.getString("ELEMENT_IN_USE"));
		}

	}

	public void clearConsole() {
		this.myAlgorithm.clearConsole();
		this.modified = true;
		this.myAlgorithm.notifyObservers();
	}
}
