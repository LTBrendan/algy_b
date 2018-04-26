'use-strict'

/**
 * Generate the code for the {@link Block.operations|operations} block
 * @return the generated code
 */
AlgoTouch.Generator['operations'] = function(block) {
  var value_a = AlgoTouch.Generator.valueToCode(block, 'A', 0);
  var dropdown_type = block.getFieldValue('TYPE');
  var value_b = AlgoTouch.Generator.valueToCode(block, 'B', 0);

  var code = value_a + value_b + 'pushTwoOp ' + dropdown_type;

  return [AlgoTouch.Generator.norm(code), 0];
};