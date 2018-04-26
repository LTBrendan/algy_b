package model;

public enum Algorithm$AlgoTypes {
	INT, CHAR;

	public static String[] toStringArray() {
		int i = 0;
		String[] result = new String[values().length];
		Algorithm$AlgoTypes[] arg4;
		int arg3 = (arg4 = values()).length;

		for (int arg2 = 0; arg2 < arg3; ++arg2) {
			Algorithm$AlgoTypes algoType = arg4[arg2];
			result[i] = algoType.toString();
			++i;
		}

		return result;
	}
}
