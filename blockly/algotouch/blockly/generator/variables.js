'use-strict'

/**
 * Generate the code for the {@link Block.getVar|getVar} block
 * @return the generated code
 */
AlgoTouch.Generator.pushVar = function(block) {
  var variable_name = block.getField('VAR').getText();
  var code= 'pushVar ' + variable_name;
  return [AlgoTouch.Generator.norm(code), 0];
};

/**
 * Generate the code for the {@link Block.get_set_array|get_set_array} block
 * @return the generated code
 */
AlgoTouch.Generator.pushIndexedVar = function(block) {
  var variable_name = block.getField('VAR').getText();
  var code = variable_name;
  return [AlgoTouch.Generator.norm(code), 0];
};

/**
 * Generate the code for the {@link Block.setVar|setVar} block
 * @return the generated code
 */
AlgoTouch.Generator.setVar = function(block) {
  var variable_name = block.getField('VAR').getText();
  var value = AlgoTouch.Generator.valueToCode(block, 'VALUE', 0);
  var code;
  if(value)
    code = value + 'assign ' + variable_name;
  else
    code = AlgoTouch.Msg.ERROR_VARIABLE_MISSING;
  
  return AlgoTouch.Generator.norm(code);
};

AlgoTouch.Generator['get_' + AlgoTouch.Variables.Types[0]] = function(block){
	return AlgoTouch.Generator.pushIndexedVar(block);
}

AlgoTouch.Generator['get_' + AlgoTouch.Variables.Types[1]] = function(block){
	return AlgoTouch.Generator.pushIndexedVar(block);
}

AlgoTouch.Generator['get_' + AlgoTouch.Variables.Types[2]] = function(block){
	return AlgoTouch.Generator.pushVar(block);
}

AlgoTouch.Generator['get_' + AlgoTouch.Variables.Types[3]] = function(block){
	return AlgoTouch.Generator.pushVar(block);
}

AlgoTouch.Generator['get_' + AlgoTouch.Variables.Types[4]] = function(block){
	return AlgoTouch.Generator.pushVar(block);
}

AlgoTouch.Generator['get_' + AlgoTouch.Variables.Types[5]] = function(block){
  return AlgoTouch.Generator.pushVar(block);
}

AlgoTouch.Generator['set_' + AlgoTouch.Variables.Types[2]] = function(block){
	return AlgoTouch.Generator.setVar(block);
}

AlgoTouch.Generator['set_' + AlgoTouch.Variables.Types[3]] = function(block){
	return AlgoTouch.Generator.setVar(block);
}

AlgoTouch.Generator['set_' + AlgoTouch.Variables.Types[5]] = function(block){
  return AlgoTouch.Generator.setVar(block);
}


