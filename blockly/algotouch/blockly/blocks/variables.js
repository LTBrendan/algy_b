'use strict'

/**
 * @namespace Variables
 * @author Daphnis
 */
AlgoTouch.Variables = {};

/**
 * Color of the variables blocks
 * @constant
 * @default
 */
AlgoTouch.Variables.HUE = 330;

/**
 * Types of variables
 * @constant
 * @default
 */
AlgoTouch.Variables.Types = ['char_array', 'integer_array', 'var_char', 'var_integer', 'constant', 'index'];

/**
 * Group of arrays
 * @constant
 * @default
 */
AlgoTouch.Variables.Arrays = [AlgoTouch.Variables.Types[0], AlgoTouch.Variables.Types[1]];

/**
 * Group of integers
 * @constant
 * @default
 */
AlgoTouch.Variables.Integers = [AlgoTouch.Variables.Types[2], AlgoTouch.Variables.Types[3], AlgoTouch.Variables.Types[4], 'operation_integer', 'integer', 'char', 'index'];

/**
 * Files variables type checks
 * @constant
 * @default
 */
AlgoTouch.Variables.Accept = [AlgoTouch.Variables.Arrays, AlgoTouch.Variables.Arrays, AlgoTouch.Variables.Integers, AlgoTouch.Variables.Integers, [AlgoTouch.Variables.Types[5]]]

var set = 'set_';
var get = 'get_';



// Récupère le nom de la première variable créée pour un type donné en paramètre
/**
 * Get the name of the first variable of the specified type
 * @function Variables.getDefaultVariableNameForType
 * @memberof Variables
 * @param {String} type type of the variable
 * @return {Variable} first variable of this type
 */
AlgoTouch.Variables.getDefaultVariableNameForType = function(type){
	var defaultVariable = null;
	var varname = AlgoTouch.App.workspace.getVariablesOfType(type);
	if(varname.length > 0)
		defaultVariable = varname[0].name;
	return defaultVariable;
}


//Définition des blocs pour les variables de type tableau de char


Blockly.Blocks[get + AlgoTouch.Variables.Types[0]] = /** @lends Block.getVar */ {
 
  /**
   * Define a block for getting a variable, multiple blocks exists but are the same, only the type check changes
   * @constructs
   */
  init: function() {
    this.appendDummyInput()
        .appendField(new Blockly.FieldArray(AlgoTouch.Variables.getDefaultVariableNameForType(AlgoTouch.Variables.Types[0]), null, [AlgoTouch.Variables.Types[0]], AlgoTouch.Variables.Types[0]), 'VAR');
    this.setOutput(true, AlgoTouch.Variables.Types[0]);
    this.setColour(AlgoTouch.Variables.HUE);
	 	this.setTooltip("");
	 	this.setHelpUrl("");
  }
};



//Définition des blocs pour les variables de type tableau d'entier

Blockly.Blocks[get + AlgoTouch.Variables.Types[1]] = {
  init: function() {
    this.appendDummyInput()
        .appendField(new Blockly.FieldArray(AlgoTouch.Variables.getDefaultVariableNameForType(AlgoTouch.Variables.Types[1]), null, [AlgoTouch.Variables.Types[1]], AlgoTouch.Variables.Types[1]), 'VAR');
    this.setOutput(true, AlgoTouch.Variables.Types[1]);
    this.setColour(AlgoTouch.Variables.HUE);
	 	this.setTooltip("");
	 	this.setHelpUrl("");
  }
};



//Définition des blocs pour les variables de type entier

Blockly.Blocks[get + AlgoTouch.Variables.Types[2]] = {
  init: function() {
    this.appendDummyInput()
        .appendField(new Blockly.FieldVariable(AlgoTouch.Variables.getDefaultVariableNameForType(AlgoTouch.Variables.Types[2]), null, [AlgoTouch.Variables.Types[2]], AlgoTouch.Variables.Types[2]), 'VAR');
    this.setOutput(true, AlgoTouch.Variables.Types[2]);
    this.setColour(AlgoTouch.Variables.HUE);
	 	this.setTooltip("");
	 	this.setHelpUrl("");
  }
};

Blockly.Blocks[set + AlgoTouch.Variables.Types[2]] = {
  init: function() {
    this.appendValueInput('VALUE')
    		.setCheck(AlgoTouch.Variables.Accept[2])
    		.appendField(AlgoTouch.Msg.VARIABLES_SET)
        .appendField(new Blockly.FieldVariable(AlgoTouch.Variables.getDefaultVariableNameForType(AlgoTouch.Variables.Types[2]), null, [AlgoTouch.Variables.Types[2]], AlgoTouch.Variables.Types[2]), 'VAR')
        .appendField(AlgoTouch.Msg.VARIABLES_TO);
    this.setColour(AlgoTouch.Variables.HUE);
    this.setPreviousStatement(['default']);
    this.setNextStatement(['default']);
	 	this.setTooltip("");
	 	this.setHelpUrl("");
  }
};



//Définition des blocs pour les variables de type char

Blockly.Blocks[get + AlgoTouch.Variables.Types[3]] = {
  init: function() {
    this.appendDummyInput()
        .appendField(new Blockly.FieldVariable(AlgoTouch.Variables.getDefaultVariableNameForType(AlgoTouch.Variables.Types[3]), null, [AlgoTouch.Variables.Types[3]], AlgoTouch.Variables.Types[3]), 'VAR');
    this.setOutput(true, AlgoTouch.Variables.Types[3]);
    this.setColour(AlgoTouch.Variables.HUE);
	 	this.setTooltip("");
	 	this.setHelpUrl("");
  }
};

Blockly.Blocks[set + AlgoTouch.Variables.Types[3]] = /** @lends Block.setVar */ {
  
   /**
    * Define a block for setting a variable, multiple blocks exists but are the same, only the type check changes<br>
    * Contains one field for the value of the variable that will be set
    * @constructs
    */
	init: function() {
    this.appendValueInput('VALUE')
    		.setCheck(AlgoTouch.Variables.Accept[3])
    		.appendField(AlgoTouch.Msg.VARIABLES_SET)
        .appendField(new Blockly.FieldVariable(AlgoTouch.Variables.getDefaultVariableNameForType(AlgoTouch.Variables.Types[3]), null, [AlgoTouch.Variables.Types[3]], AlgoTouch.Variables.Types[3]), 'VAR')
        .appendField(AlgoTouch.Msg.VARIABLES_TO);
    this.setColour(AlgoTouch.Variables.HUE);
    this.setPreviousStatement(true, ['default']);
    this.setNextStatement(true, ['default']);
	 	this.setTooltip("");
	 	this.setHelpUrl("");
  }
};



//Définition des blocs pour les variables constantes

Blockly.Blocks[get + AlgoTouch.Variables.Types[4]] = {
  init: function() {
    this.appendDummyInput()
        .appendField(new Blockly.FieldVariable(AlgoTouch.Variables.getDefaultVariableNameForType(AlgoTouch.Variables.Types[4]), null, [AlgoTouch.Variables.Types[4]], AlgoTouch.Variables.Types[4]), 'VAR');
    this.setOutput(true, AlgoTouch.Variables.Types[4]);
    this.setColour(AlgoTouch.Variables.HUE);
	 	this.setTooltip("");
	 	this.setHelpUrl("");
  }
};


//Définition des blocs pour les variables de type index

Blockly.Blocks[get + AlgoTouch.Variables.Types[5]] = {
  init: function() {
    var indices = AlgoTouch.Variables.getAllIndexes();
    this.appendDummyInput('I')
        .appendField(new Blockly.FieldVariable(AlgoTouch.Variables.getDefaultVariableNameForType(indices[0]), null, indices, indices[0]), 'VAR');
    this.setOutput(true, AlgoTouch.Variables.Types[5]);
    this.setColour(AlgoTouch.Variables.HUE);
    this.setTooltip("");
    this.setHelpUrl("");
  }
};

Blockly.Blocks[set + AlgoTouch.Variables.Types[5]] = {
  init: function() {
    var indices = AlgoTouch.Variables.getAllIndexes();
    this.appendValueInput('VALUE')
        .setCheck(AlgoTouch.Variables.Accept[5])
        .appendField(AlgoTouch.Msg.VARIABLES_SET)
        .appendField(new Blockly.FieldVariable(AlgoTouch.Variables.getDefaultVariableNameForType(indices[0]), null, indices, indices[0]), 'VAR')
        .appendField(AlgoTouch.Msg.VARIABLES_TO);
    this.setColour(AlgoTouch.Variables.HUE);
    this.setPreviousStatement(true, ['default']);
    this.setNextStatement(true, ['default']);
    this.setTooltip("");
    this.setHelpUrl("");
  }
};

/**
 * As each index variable type contains the id of the array they are associated, this method helps retrieve
 * all the different types of indices
 * @function getAllIndexes
 * @memberof Variables
 * @return array of all index type that exists
 */
AlgoTouch.Variables.getAllIndexes = function(){
  var all = AlgoTouch.App.workspace.getAllVariables();
  var types = new Set();
  for (var i = all.length - 1; i >= 0; i--) {
    if(all[i].type.includes('index'))
      types.add(all[i].type);
  }
  if(types.length == 0)
    types.add('index');
  return Array.from(types);
}
