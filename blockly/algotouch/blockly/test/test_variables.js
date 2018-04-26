AlgoTouch.Test.Vars = {};

AlgoTouch.Test.Vars.runTests = function() {
	startNewTest('variables');

	var ids = [];

	ids.push(ws.createVariable('array', 'integer_array').getId());
	var get_integer_array = ws.newBlock('get_integer_array');
	assertEquals(AlgoTouch.Generator.blockToCode(get_integer_array)[0], 'array\n', 'error integer_array 1');


	ids.push(ws.createVariable('arraychar', 'char_array').getId());
	var get_char_array = ws.newBlock('get_char_array');
	assertEquals(AlgoTouch.Generator.blockToCode(get_char_array)[0], 'arraychar\n', 'error char_array 1');


	ids.push(ws.createVariable('const', 'constant').getId());
	var get_constant = ws.newBlock('get_constant');
	assertEquals(AlgoTouch.Generator.blockToCode(get_constant)[0], 'pushVar const\n', 'error constant 1');


	ids.push(ws.createVariable('int', 'var_integer').getId());
	var get_var_integer = ws.newBlock('get_var_integer');
	assertEquals(AlgoTouch.Generator.blockToCode(get_var_integer)[0], 'pushVar int\n', 'error integer 1');


	ids.push(ws.createVariable('char', 'var_char').getId());
	var get_var_char = ws.newBlock('get_var_char');
	assertEquals(AlgoTouch.Generator.blockToCode(get_var_char)[0], 'pushVar char\n', 'error char 1');


	for (var i in ids)
		ws.deleteVariableById(ids[i]);

}