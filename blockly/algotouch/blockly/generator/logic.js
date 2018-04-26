'use-strict'

// AlgoTouch.Generator['loop_out'] = function(block) {
//   var code = '';
//   for (var i = 0; i < block.itemCount_ + 2; i++) {
//     code += AlgoTouch.Generator.valueToCode(block, 'ADD' + i, 0);
//   }
  
//   return [AlgoTouch.Generator.norm(code), 0];
// };


// AlgoTouch.Generator['logic_comparisons'] = function(block) {
//   var value_a = AlgoTouch.Generator.valueToCode(block, 'A', 0);
//   var dropdown_name = block.getFieldValue('NAME');
//   var value_b = AlgoTouch.Generator.valueToCode(block, 'B', 0);

//   var code = value_a + value_b + 'pushTwoOp ' + dropdown_name;

//   return [AlgoTouch.Generator.norm(code), 0];
// };

/**
 * Generate the code for the {@link Block.if_block|if_block} block
 * @return the generated code
 */
AlgoTouch.Generator['if_block'] = function(block) {
  var value_a = AlgoTouch.Generator.valueToCode(block, 'A', 0);
  var cond = block.getFieldValue('TYPE');
  var value_b = AlgoTouch.Generator.valueToCode(block, 'B', 0);
  var statements_do = AlgoTouch.Generator.statementToCode(block, 'DO');
  var statements_else = AlgoTouch.Generator.statementToCode(block, 'ELSE');

  var code;
  if(!value_a || !value_b)
    code = AlgoTouch.Generator.norm(AlgoTouch.Msg.ERROR_MISSING_CONDITION);

  if(!code){
    code = AlgoTouch.Generator.norm('createContinue');
    code += AlgoTouch.Generator.norm(value_a + value_b + 'pushTwoOp ' + cond) + AlgoTouch.Generator.norm('pushBranch TOP_STACK_FALSE')
    code += statements_do + AlgoTouch.Generator.norm('createContinue') + AlgoTouch.Generator.norm('pushBranch ALWAYS') + AlgoTouch.Generator.norm('pushLastContinue')
    code += statements_else + AlgoTouch.Generator.norm('pushContinue');
  }

  return code;
};