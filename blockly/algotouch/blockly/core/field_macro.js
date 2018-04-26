'use-strict'

/**
 * Create a new FieldMacro
 * @class FieldMacro
 * @param {String} varname the varname of the variable
 * @param {function} validator callback to validate
 * @param {Array} var_types var types
 */
Blockly.FieldMacro = function(varname, validator, var_types){
	var a = Object.create(new Blockly.FieldVariable(varname, validator, var_types, var_types[0]));
	a.menuGenerator_ = Blockly.FieldMacro.dropdown;
	return a;
}

/**
 * Drop down of the field macro
 */
Blockly.FieldMacro.dropdown = function(){
	var vars = Blockly.FieldVariable.dropdownCreate.call(this);
	if(vars.length > 2){
		vars.pop();
		vars.pop();
	}
	return vars;
}

