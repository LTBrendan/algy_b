package virtualTouchMachine;

import java.io.Serializable;
import virtualTouchMachine.Code;

public class CodeAddress implements Serializable {
	private static final long serialVersionUID = 8052107082329541639L;
	private Code code;
	private int address;

	public CodeAddress(Code code, int address) {
		this.code = code;
		this.address = address;
	}

	public Code getCode() {
		return this.code;
	}

	public int getAddress() {
		return this.address;
	}
}
