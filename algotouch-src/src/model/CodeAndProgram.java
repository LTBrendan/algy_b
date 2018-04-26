package model;

import java.io.Serializable;
import model.Program;
import virtualTouchMachine.Code;

public class CodeAndProgram implements Serializable {
	private static final long serialVersionUID = 7373796820159094445L;
	private Code code;
	private Program program;

	public CodeAndProgram(Code code, Program program) {
		this.code = code;
		this.program = program;
	}

	public Code getCode() {
		return this.code;
	}

	public Program getProgram() {
		return this.program;
	}
}
