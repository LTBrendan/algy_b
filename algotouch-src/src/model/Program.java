package model;

import java.util.LinkedList;
import java.util.ListIterator;
import model.AlgoElement;
import virtualTouchMachine.Code.BLOCK_TYPE;

public class Program extends LinkedList<String> {
	private static final long serialVersionUID = -9003427502846546775L;
	public static final String FROM = "From";
	public static final String UNTIL = "Until";
	public static final String LOOP = "Loop";
	public static final String TERMINATE = "Terminate";
	public static final String END = "End";
	private static final String FROM_SYMBOL = "$From#";
	private static final String UNTIL_SYMBOL = "$Until#";
	private static final String LOOP_SYMBOL = "$Loop#";
	private static final String TERMINATE_SYMBOL = "$Terminate#";
	private static final String END_SYMBOL = "$End#";
	private static final String NEW_LINE = "// ...";
	private int currentInsertionLine = 0;

	public Program(String blocName) {
		this.add(AlgoElement.getId(blocName));
		this.add("$From#");
		this.add("// ...");
		this.add("$Until#");
		this.add("// ...");
		this.add("$Loop#");
		this.add("// ...");
		this.add("$Terminate#");
		this.add("// ...");
		this.add("$End#");
	}

	public int getCurrentInsertionLine() {
		return this.currentInsertionLine;
	}

	public void setCurrentInsertionLine(int currentInsertionLine) {
		this.currentInsertionLine = currentInsertionLine;
	}

	public boolean add(String line) {
		this.add(this.currentInsertionLine, line);
		return true;
	}

	public void add(int index, String line) {
		super.add(this.currentInsertionLine, line);
		++this.currentInsertionLine;
	}

	public String getLastInsertedLine() {
		String result;
		if (this.currentInsertionLine == 0) {
			result = "";
		} else {
			result = (String) this.get(this.currentInsertionLine - 1);
		}

		return result;
	}

	public void changeName(String id, String name) {
		this.changeVariableName(id, name);
		String arrayId = id.replace('#', '[');
		String arrayName = name.replace('#', '[');
		this.changeVariableName(arrayId, arrayName);
	}

	private void changeVariableName(String id, String name) {
		ListIterator it = (ListIterator) this.iterator();

		while (it.hasNext()) {
			String s = (String) it.next();
			s = s.replace(id, name);
			it.set(s);
		}

	}

	public boolean searchId(String id) {
		String arrayId = id.replace('#', '[');
		boolean found = false;

		String s;
		for (ListIterator it = (ListIterator) this.iterator(); it.hasNext()
				&& !found; found = s.contains(id) || s.contains(arrayId)) {
			s = (String) it.next();
		}

		return found;
	}

	public void clear(BLOCK_TYPE blocType) {
		this.currentInsertionLine = 0;
	}

	public void clearBloc(String startInstructionBloc, String endInstructionBloc) {
		int addressToRemove = this.indexOf(startInstructionBloc) + 1;

		for (String currentInstruction = (String) this.get(addressToRemove); !currentInstruction
				.equals(endInstructionBloc); currentInstruction = (String) this.get(addressToRemove)) {
			this.remove(addressToRemove);
		}

		this.currentInsertionLine = addressToRemove;
		this.add("// ...");
		--this.currentInsertionLine;
	}

	public void clearFrom() {
		this.clearBloc("$From#", "$Until#");
	}

	public void clearEval() {
		this.clearBloc("$Until#", "$Loop#");
	}

	public void clearLoop() {
		this.clearBloc("$Loop#", "$Terminate#");
	}

	public void clearTerminate() {
		this.clearBloc("$Terminate#", "$End#");
	}

	public void setFromCurrentInsertionLine() {
		this.currentInsertionLine = this.indexOf("$Until#");
		if (((String) this.get(this.currentInsertionLine - 1)).equals("// ...")) {
			--this.currentInsertionLine;
		}

	}

	public void setEvalCurrentInsertionLine(boolean firstPlace) {
		if (firstPlace) {
			this.currentInsertionLine = this.indexOf("$Until#") + 1;
		} else {
			this.currentInsertionLine = this.indexOf("$Loop#");
			if (((String) this.get(this.currentInsertionLine - 1)).equals("// ...")) {
				--this.currentInsertionLine;
			}
		}

	}

	public void setLoopCurrentInsertionLine() {
		this.currentInsertionLine = this.indexOf("$Terminate#");
		if (((String) this.get(this.currentInsertionLine - 1)).equals("// ...")) {
			--this.currentInsertionLine;
		}

	}

	public void setTerminateCurrentInsertionLine() {
		this.currentInsertionLine = this.indexOf("$End#");
		if (((String) this.get(this.currentInsertionLine - 1)).equals("// ...")) {
			--this.currentInsertionLine;
		}

	}

	public void setCurrentEndBlockLine(String blockName) {
		if (blockName.equals("From")) {
			this.setFromCurrentInsertionLine();
		} else if (blockName.equals("Until")) {
			this.setEvalCurrentInsertionLine(false);
		} else if (blockName.equals("Loop")) {
			this.setLoopCurrentInsertionLine();
		} else if (blockName.equals("Terminate")) {
			this.setTerminateCurrentInsertionLine();
		} else if (blockName.equals("End")) {
			this.setTerminateCurrentInsertionLine();
		} else {
			this.currentInsertionLine = 0;
		}

	}

	public String findBlock(int line) {
		boolean found = false;
		String result = "";
		this.currentInsertionLine = line;
		int i = line;

		while (!found && i >= 0) {
			String currentString = (String) this.get(i);
			found = true;
			if (currentString.equals("$From#")) {
				result = "FROM";
			} else if (currentString.equals("$Until#")) {
				result = "UNTIL";
			} else if (currentString.equals("$Loop#")) {
				result = "LOOP";
			} else if (currentString.equals("$Terminate#")) {
				result = "TERMINATE";
			} else if (currentString.equals("$End#")) {
				result = "END";
			} else {
				--i;
				found = false;
			}
		}

		return result;
	}

	public void setCurrentLine(String blocTitle) {
		this.currentInsertionLine = this.indexOf('$' + blocTitle + '#');
	}
}
