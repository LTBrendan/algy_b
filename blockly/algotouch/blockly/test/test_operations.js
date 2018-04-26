AlgoTouch.Test.Op = {};

AlgoTouch.Test.Op.runTests = function() {
	startNewTest('operations');
	
	var block = ws.newBlock ("operations");
	
	var value1 = ws.newBlock ("integer");
	var value2 = ws.newBlock ("integer");
	
	value1.setFieldValue (4, "VALUE");
	value2.setFieldValue (3, "VALUE");
	
	connect (block, value1, "A");	
	connect (block, value2, "B");
	
	assertEquals (AlgoTouch.Generator.blockToCode (block)[0], "pushValue 4\npushValue 3\npushTwoOp ADD\n", "error");
	
	block = ws.newBlock ("operations");
	
	var var1 = ws.createVariable ("var1", "var_integer");
	var getVar1 = ws.newBlock ("get_var_integer");
	
	connect (block, getVar1, "A");	
	connect (block, value2, "B");
	
	assertEquals (AlgoTouch.Generator.blockToCode (block)[0], "pushVar var1\npushValue 3\npushTwoOp ADD\n", "error");
	
	block.setFieldValue ("SUB", "TYPE");
	
	assertEquals (AlgoTouch.Generator.blockToCode (block)[0], "pushVar var1\npushValue 3\npushTwoOp SUB\n", "error");
	
	//TODO	
}