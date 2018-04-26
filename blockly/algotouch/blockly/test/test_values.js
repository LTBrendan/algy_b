AlgoTouch.Test.Values = {};

AlgoTouch.Test.Values.runTests = function() {
	startNewTest('values');

	var ids = [];

	var block = ws.newBlock("char");

	block.setFieldValue("a", "CHAR");

	assertEquals(AlgoTouch.Generator.blockToCode(block)[0], "pushValue 97\n",
			"error");
	block = ws.newBlock("integer");

	block.setFieldValue("1", "VALUE");

	assertEquals(AlgoTouch.Generator.blockToCode(block)[0], "pushValue 1\n",
			"error");

    block = ws.newBlock ("read");

	ids.push(ws.createVariable ("var1", "var_integer").getId());
	var getVar1 = ws.newBlock ("get_var_integer");
	
	connect (block, getVar1, "TO_READ", function(){
		block.setFieldValue ("TEST", "MESSAGE");
	
		assertEquals (AlgoTouch.Generator.blockToCode (block), 'intPrompt TEST\nassign var1\n', "error");
	});
	
	


}