AlgoTouch.Test.Arrays = {};

AlgoTouch.Test.Arrays.runTests = function() {
	startNewTest('arrays');

	var ids = [];

	//Test du bloc length of
	var length_of = ws.newBlock('length_of');

	ids.push(ws.createVariable('array', 'integer_array').getId());
	var array = ws.newBlock('get_integer_array');

	connect(length_of, array, 'ARRAY');

	assertEquals(AlgoTouch.Generator.blockToCode(length_of)[0], 'pushVar array.length\n', 'error length_of 2');





	//Test du get at index 
	var get = ws.newBlock('get_set_int_array');
	var value = ws.newBlock('integer');
	

	connect(get, array, 'ARRAY');
	connect(get, value, 'INDEX');

	assertEquals(AlgoTouch.Generator.blockToCode(get)[0], 'pushValue 0\npushIndexedVar array\n', 'error get_set_int_array 2');


	ids.push(ws.createVariable('var', 'var_integer').getId());
	var vararray = ws.newBlock('get_var_integer');

	get = ws.newBlock('get_set_int_array');

	connect(get, array, 'ARRAY');
	connect(get, vararray, 'INDEX');

	assertEquals(AlgoTouch.Generator.blockToCode(get)[0], 'pushVar var\npushIndexedVar array\n', 'error get_set_int_array 3');

	get.getInputWithBlock(array).connection.disconnect();
	get.getInputWithBlock(vararray).connection.disconnect();

	for (var i in ids)
		ws.deleteVariableById(ids[i]);
}