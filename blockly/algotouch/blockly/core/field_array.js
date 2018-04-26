'use-strict'

/**
 * Get a field that is initialize to handle arrays (add the modify option)
 * @class FieldArray
 * @param {String} varname the varname of the variable
 * @param {function} callback to validate
 * @param {Array} var_types var types 
 */
Blockly.FieldArray = function(varname, validator, var_types){
	var a = Object.create(new Blockly.FieldVariable(varname, validator, var_types, var_types[0]));
	a.menuGenerator_ = Blockly.FieldArray.dropdown;
	a.oldItem = a.onItemSelected;
	a.onItemSelected = function(menu, menuItem){
		
		var id = menuItem.getValue();
		if(id === 'CHANGE_ARRAY'){
			var v = a.getVariable();
			var type = v.type;
			AlgoTouch.Variables.createVariable('CA.' + type + ' ' + v.name, function(name){executeCode('create_' + type + ' ' + name)}, true);
			return;
		}
		a.oldItem(menu, menuItem);
		var source = a.sourceBlock_;
		if(source){
			var parent = source.getParent();
			if(parent && parent.type === 'get_set_int_array'){
				parent.updateArray();
			}
		}
		
	}
	return a;
}

/**
 * Drop down of the field array
 */
Blockly.FieldArray.dropdown = function(){
	var vars = Blockly.FieldVariable.dropdownCreate.call(this);
	vars.push ([AlgoTouch.Msg.ARRAY_MODIFY, 'CHANGE_ARRAY']);
	return vars;
}