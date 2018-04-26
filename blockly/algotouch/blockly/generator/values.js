'use-strict'

/**
 * Generate the code for the {@link Block.integer|integer} block
 * @return the generated code
 */
AlgoTouch.Generator['integer'] = function(block) {
  var number_value = block.getFieldValue('VALUE');
  
  var code = 'pushValue ' + number_value;

  return [AlgoTouch.Generator.norm(code), 0];
};

/**
 * Generate the code for the {@link Block.char|char} block
 * @return the generated code
 */
AlgoTouch.Generator['char'] = function(block) {
  var number_value = block.getFieldValue('CHAR');

  var code = 'pushValue ' + number_value.charCodeAt(0);

  return [AlgoTouch.Generator.norm(code), 0];
};

/**
 * Generate the code for the {@link Block.read|read} block
 * @return the generated code
 */
AlgoTouch.Generator['read'] = function(block) {
  
  var message = block.getFieldValue('MESSAGE');
  var code;
  var to_read_block = block.getInputTargetBlock('TO_READ');

  if(to_read_block){
    code = AlgoTouch.Generator.norm((to_read_block.type.includes('int') ? 'int' : 'char') + 'Prompt ' + message); 
   

    if(to_read_block.type.includes('array')){
      code += AlgoTouch.Generator.valueToCode(to_read_block, 'INDEX', 0);
      code += 'indexedAssign ' + AlgoTouch.Generator.valueToCode(to_read_block, 'ARRAY', 0);
    }else{
      code += 'assign ' + to_read_block.getField('VAR').getVariable().name;
    }

  }else{
    code = AlgoTouch.Msg.ERROR_MISSING_FOR_READ;
  }

  return AlgoTouch.Generator.norm(code);
};

/**
 * Generate the code for the {@link Block.write|write} block
 * @return the generated code
 */
AlgoTouch.Generator['write'] = function(block) {
  
  var message = block.getFieldValue('MESSAGE');
  var code = '';
  var to_read_block = block.getInputTargetBlock('TO_WRITE');
  

  if(to_read_block){
    var type = to_read_block.type;
    
    if(type.includes('array') && !type.includes('set')){
      var array = to_read_block.getField('VAR').getText();
      code += AlgoTouch.Generator.norm('pushArray ' + array);
      code += AlgoTouch.Generator.norm('pushVar ' + array + '.length');
    }else{
      code += AlgoTouch.Generator.norm(AlgoTouch.Generator.valueToCode(block, 'TO_WRITE', 0));  
      code += AlgoTouch.Generator.norm('pushValue 1');
    }
    if(type === 'get_set_int_array')
      type = to_read_block.getInputTargetBlock('ARRAY').type;
    code += AlgoTouch.Generator.norm('pushValue ' + (type.includes('char') ? 0 : 1)); 
  }else{
    code += AlgoTouch.Generator.norm('pushValue 0');
    code += AlgoTouch.Generator.norm('pushValue 1');
  }
  code += AlgoTouch.Generator.norm('print ' + message); 

  return AlgoTouch.Generator.norm(code);
};


