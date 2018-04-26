'use-strict'


/**
 * Generate the code for the {@link Block.macro|macro} block
 * @return the generated code
 */
AlgoTouch.Generator['macro'] = function(block) {
  var text_name = block.getFieldValue('NAME');
  var statements_from = AlgoTouch.Generator.statementToCode(block, 'FROM');
  var value_until = AlgoTouch.Generator.statementToCode(block, 'UNTIL');
  var statements_do = AlgoTouch.Generator.statementToCode(block, 'DO');
  var statements_term = AlgoTouch.Generator.statementToCode(block, 'TERM');
  
  var code = AlgoTouch.Generator.norm('macro ' + text_name);
  if(value_until)
  	code += AlgoTouch.Generator.norm('setLoop');

  if(statements_from)
  	code += AlgoTouch.Generator.norm('setFrom') + statements_from;

  if(value_until){
 		code += AlgoTouch.Generator.norm('setUntilInsertion') + value_until;
	 	if(statements_do)
	 		code += AlgoTouch.Generator.norm('setCore') + statements_do;
    else
      code += AlgoTouch.Generator.norm(AlgoTouch.Msg.ERROR_MISSING_LOOP);
	}else if(statements_do){
    code += AlgoTouch.Generator.norm(AlgoTouch.Msg.ERROR_MISSING_CONDITION);
  }

	if(statements_term)
  	code += AlgoTouch.Generator.norm('setTerm') + statements_term;

  return code;
};

/**
 * Generate the code for the {@link Block.get_macro|get_macro} block
 * @return the generated code
 */
AlgoTouch.Generator['get_macro'] = function(block){
  var var_name = block.getFieldValue('VAR');
  return [AlgoTouch.Generator.norm(var_name), 0];
}

/**
 * Generate the code for the {@link Block.execute|execute} block
 * @constant
 */
AlgoTouch.Generator['execute'] = function(block) {
  var targetmacro = block.getInputTargetBlock('MACRO');
  var code;
  if(targetmacro)
   	code = 'callMacro ' + AlgoTouch.Macro.getMacroName(targetmacro.getFieldValue('VAR'));
  else
    code = AlgoTouch.Msg.ERROR_MISSING_MACRO;
  
  return AlgoTouch.Generator.norm(code);
};

/**
 * Generate the code for the {@link Block.macro_cond|macro_cond} block
 * @constant
 */
AlgoTouch.Generator['macro_cond'] = function(block){
  var value_a = AlgoTouch.Generator.norm(AlgoTouch.Generator.valueToCode(block, 'A', 0));
  var dropdown_name = AlgoTouch.Generator.norm(block.getFieldValue('COND'));
  var value_b = AlgoTouch.Generator.norm(AlgoTouch.Generator.valueToCode(block, 'B', 0));

  var code;
  if(value_a.length == 1 || value_b.length == 1)
    code = AlgoTouch.Generator.norm(AlgoTouch.Msg.ERROR_INVALID_CONDITION);
  else
    code = value_a + value_b + 'pushTwoOp ' + dropdown_name + 'pushBranchTerminate';

  return AlgoTouch.Generator.norm(code);
};