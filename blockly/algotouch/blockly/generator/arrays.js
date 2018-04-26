'use-strict'

/**
 * Generate the code for the {@link Block.length_of|length_of} block
 * @return the generated code
 */
AlgoTouch.Generator['length_of'] = function(block){
	var array = AlgoTouch.Generator.valueToCode(block, 'ARRAY', 0);
	var code;
	if(array)
		code = [AlgoTouch.Generator.norm('pushVar ' + array.replace('\n', '') + '.length'), 0]
	else
		code = [AlgoTouch.Generator.norm(AlgoTouch.Msg.ERROR_MISSING_ARRAY), 0];
	return code;
}

/**
 * Generate the code for the {@link Block.get_set_array|get_set_array} block
 * @return the generated code
 */
AlgoTouch.Generator['get_set_int_array'] = function(block){
	var mode = block.getFieldValue('MODE');
	var array = AlgoTouch.Generator.valueToCode(block, 'ARRAY', 0);
	var index = AlgoTouch.Generator.valueToCode(block, 'INDEX', 0);

	var code;

	
	if(mode === 'GET'){
		if(array)
			code = [index + AlgoTouch.Generator.norm('pushIndexedVar ' + array)];
		else
			code = [AlgoTouch.Generator.norm(AlgoTouch.Msg.ERROR_MISSING_ARRAY)];
	}else{
		if(array){
			var set  = AlgoTouch.Generator.valueToCode(block, 'SET');
			if(set)
				code = AlgoTouch.Generator.norm(set + index + 'indexedAssign ' + array.replace('\n', ''));
			else
				code = AlgoTouch.Generator.norm(AlgoTouch.Msg.ERROR_MISSING_ARRAY_VALUE);	
		}else{
			code = AlgoTouch.Generator.norm(AlgoTouch.Msg.ERROR_MISSING_ARRAY);
		}

		
	}

	return code;
}