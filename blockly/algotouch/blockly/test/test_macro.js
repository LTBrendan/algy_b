AlgoTouch.Test.Macro = {};

AlgoTouch.Test.Macro.runTests = function() {
	startNewTest('macros');

	var ge = AlgoTouch.Generator;

	var macro = ws.newBlock('macro');

	assertEquals(ge.blockToCode(macro), 'macro macro\n', 'macro 1 failed');
	

}