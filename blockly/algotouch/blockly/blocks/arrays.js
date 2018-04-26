'use-strcit'

/**
 * @namespace
 * @author Daphnis
 */
AlgoTouch.Arrays = {};

/**
 * Color of the arrays blocks
 * @constant
 * @default
 */
AlgoTouch.Arrays.HUE = 260;


Blockly.Blocks['get_set_int_array'] = /** @lends Block.get_set_int_array */{
  /**
   * Define the block that will be used to access 
   * an index of the array or change the value at a specified index<br>
   * When in getter mode, the first field accept an array and the second accept an integer or an index variable<br>
   * When in setter mode, an other field is present and accept any type of variable (integer or char) or a simple value
   * @constructs
   */
  init: function() {

  	var MODE = [[AlgoTouch.Msg.ARRAY_GET, 'GET'],
        [AlgoTouch.Msg.ARRAY_SET, 'SET']];

    var block = this;
    var modeMenu = new Blockly.FieldDropdown(MODE, function(value) {
      block.updateStatement(value != 'GET');
    });

    modeMenu.oldSetter = modeMenu.setValue;
    modeMenu.setValue = function(value){
      this.oldSetter(value);
      block.getRootBlock().updateStatement(value != 'GET');
    }
    this.setColour(AlgoTouch.Arrays.HUE);

    this.appendDummyInput()
        .appendField(modeMenu, 'MODE');

    this.appendValueInput('ARRAY')
        .setCheck(['char_array', 'integer_array']);

    this.appendDummyInput().appendField("[");
    this.appendValueInput('INDEX').setCheck(["integer", "index"]);
    this.appendDummyInput().appendField("]");
    
    this.setInputsInline(true);
    this.setOutput(true, ['var_integer']);

  },

  /**
   * Function called when an array is put inside the array field.
   * The index field is updated to only accept index variables that
   * are defined for this array in particular
   */
  updateArray: function(){

    var model = this.getInputTargetBlock('ARRAY');
    if(model && model.getField('VAR')){
      model = model.getField('VAR').getVariable();
      var type = 'index_' + model.getId();
      var input = this.getInputTargetBlock('INDEX');
      if(input){
        input = input.getInput('I');
        if(input){
           input.removeField('VAR');
          input.appendField(new Blockly.FieldVariable(AlgoTouch.Variables.getDefaultVariableNameForType(type), null, [type], type), 'VAR');
        }
      }
    }
  },


  /**
   * Switch between a value block and a statement block.
   * @param {boolean} newStatement True if the block should be a statement.
   *     False if the block should be a value.
   * 
   */
  updateStatement: function(newStatement) {
    var oldStatement = !this.outputConnection;
    if (newStatement != oldStatement) {
      this.unplug(true, true);
      if (newStatement) {
        this.setOutput(false);
        this.setPreviousStatement(true, ['default']);
        this.setNextStatement(true, ['default']);
        this.appendValueInput('SET').setCheck(["char", "var_char", "integer", "var_integer", "operation_integer", "index", "constant"]).appendField('=');
      } else {
        this.setPreviousStatement(false);
        this.setNextStatement(false);
        this.setOutput(true, ['var_char', 'var_integer']);
        this.removeInput('SET');
      }
    }
  }
 
  
};


Blockly.Blocks['length_of'] = /** @lends Block.length_of */{
	/**
	 * Block to get the length of an array.<br>
	 * Only contains one input that accept a char array or an integer array
	 * @constructs
	 */
	init: function() {
    this.appendValueInput("ARRAY")
        .setCheck(["char_array", "integer_array"])
        .appendField(AlgoTouch.Msg.ARRAY_LENGTH);
    this.setOutput(true, 'integer');
    this.setColour(AlgoTouch.Arrays.HUE);
    this.setTooltip("");
    this.setHelpUrl("");
  }
};
