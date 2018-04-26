AlgoTouch.Test.Logic = {};

AlgoTouch.Test.Logic.runTests = function() {
	startNewTest('logic');
	var ge = AlgoTouch.Generator;
	
	//Tests pour le bloc if_block
	var if_block = ws.newBlock('if_block');
	
	var value_0 = ws.newBlock('integer');
	var value_1 = ws.newBlock('integer');

	connect(if_block, value_0, 'A');
	connect(if_block, value_1, 'B');

	assertEquals(ge.blockToCode(if_block), 'createContinue\npushValue 0\npushValue 0\npushTwoOp EQUAL\npushBranch TOP_STACK_FALSE\ncreateContinue\npushBranch ALWAYS\npushLastContinue\npushContinue\n', 'error if_block 1');

	value_0.setFieldValue(5, 'VALUE');
	value_1.setFieldValue(10, 'VALUE');

	assertEquals(ge.blockToCode(if_block), 'createContinue\npushValue 5\npushValue 10\npushTwoOp EQUAL\npushBranch TOP_STACK_FALSE\ncreateContinue\npushBranch ALWAYS\npushLastContinue\npushContinue\n', 'error if_block 2');

}