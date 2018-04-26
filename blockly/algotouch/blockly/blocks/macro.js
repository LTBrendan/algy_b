'use-strict'

/**
 * @namespace Macro
 * @author Daphnis
 */
AlgoTouch.Macro = {};

/**
 * Store all the currents macro that are in the program
 * @constant
 */
AlgoTouch.Macro.macros = [];
AlgoTouch.Macro.index = 0;

/**
 * Color of the macro blocks
 * @constant
 * @default
 */
AlgoTouch.Macro.HUE = 120;

/**
 * Return the name of the macro using the block id and from the blocks displayed in blockly not the macros database
 * @function nameOf
 * @memberof Macro
 * @param {String} id id of the macro block 
 */
AlgoTouch.Macro.nameOf = function(id) {
	var block = AlgoTouch.App.workspace.getBlockById(id);
	return block.getFieldValue('NAME').toLowerCase();
}

/**
 * Tell if a macro exists or not in the database using its name
 * @function exists
 * @memberof Macro
 * @param {String} name name of the macro to find
 * @return true if the macro exists, false otherwise
 */
AlgoTouch.Macro.exists = function(name) {
	var exists = false;
	var i = 0;
	while (i < AlgoTouch.Macro.macros.length && !exists) {
		if (AlgoTouch.Macro.macros[i][1] === name)
			exists = true;
		i++;
	}
	return exists;
}

/**
 * Add a macro to the database when a macro block is created or when reloading after changing the language
 * @function addMacro
 * @memberof Macro
 * @param blockID {String} the block id of the new macro block
 * @param opt_name {String} the name of the macro - optional
 * @param opt_id {String} the id of the variable associated withe the macro name - optional
 */
AlgoTouch.Macro.addMacro = function(blockID, opt_name, opt_id) {
	var name = opt_name || AlgoTouch.Macro.nameOf(blockID);
	var changed = false;

	while(AlgoTouch.Macro.exists(name)){
		name = 'macro_' + AlgoTouch.Macro.next();
		changed = true;
	}

	AlgoTouch.App.workspace.getBlockById(blockID).setFieldValue(name, 'NAME');

	if(!AlgoTouch.Macro.exists(name)){
		var created = AlgoTouch.App.workspace.createVariable(name, 'macro', opt_id);
		AlgoTouch.Macro.macros.push([ blockID, name, created.getId() ]);
		if(!changed)
			AlgoTouch.Macro.index++;
	}else{
		AlgoTouch.App.workspace.delelete
	}
	
}

/**
 * Delete a macro from the database
 * @function delMacro
 * @memberof Macro
 * @param {String} block id of the macro or name of the macro
 */
AlgoTouch.Macro.delMacro = function(val) {
	for ( var i in AlgoTouch.Macro.macros) {
		if (AlgoTouch.Macro.macros[i][0] === val
				|| AlgoTouch.Macro.macros[i][1] === val) {
			if (AlgoTouch.App.workspace
					.getVariableById(AlgoTouch.Macro.macros[i][2]) != null)
				AlgoTouch.App.workspace
						.deleteVariableById(AlgoTouch.Macro.macros[i][2]);
			AlgoTouch.Macro.macros.splice(i, 1);
		}
	}
}

/**
 * Get the next index of the macro
 * @function next
 * @memberof Macro
 * @return the new index of a created macro
 */
AlgoTouch.Macro.next = function() {
	return AlgoTouch.Macro.index++;
}

/**
 * Get the name of a macro from the macros database using the specified value
 * @function getMacroName
 * @memberof Macro
 * @param val blockid of the macro block or id of the associated var
 * @return the name
 */
AlgoTouch.Macro.getMacroName = function(val) {
	var name = false;
	var i = 0;
	while (i < AlgoTouch.Macro.macros.length && !name) {
		if (AlgoTouch.Macro.macros[i][0] === val
				|| AlgoTouch.Macro.macros[i][2] === val)
			name = AlgoTouch.Macro.macros[i][1];

		i++;
	}
	return name;
}

/**
 * Get the macro block from the macros database using the specified value
 * @function getMacroBlock
 * @memberof Macro
 * @param name name of the macro
 * @return the macro block
 */
AlgoTouch.Macro.getMacroBlock = function(name) {
	var block;
	var i = 0;
	while (i < AlgoTouch.Macro.macros.length && !block) {
		if (AlgoTouch.Macro.macros[i][1] === name)
			block = AlgoTouch.App.workspace.getBlockById(AlgoTouch.Macro.macros[i][0]);

		i++;
	}
	return block;
}

/**
 * Get all the information from the database
 * @function tupleByBlock
 * @memberof Macro 
 * @param {String} blockID the blockid of the macro block
 */
AlgoTouch.Macro.tupleByBlock = function(blockID) {
	var tuple = null;
	var i = 0;
	while (i < AlgoTouch.Macro.macros.length && !tuple) {
		if (AlgoTouch.Macro.macros[i][0] === blockID)
			tuple = AlgoTouch.Macro.macros[i];

		i++;
	}
	return tuple;
}

/**
 * Verify if the name entered for the specified blockID, if another macro has 
 * the same name, the current one is changed to a unique one.
 * @function checkNames
 * @memberof Macro
 * @param {String} blockID the block id of the macro block
 */
AlgoTouch.Macro.checkNames = function(blockID) {
	var block = AlgoTouch.App.workspace.getBlockById(blockID);

	var tuple = AlgoTouch.Macro.tupleByBlock(blockID);
	
	var old_name = tuple[1];
	var var_id = tuple[2];
	var new_name = AlgoTouch.Macro.nameOf(blockID);

	if(new_name.toLowerCase() === 'algotouch')
		alert('AlgoTouch, c\'est bien plus qu\'une simple macro');
	else if(new_name.toLowerCase() === 'lycee')
		alert('Le lycée, ce n\'est qu\'un centre aéré - JL. Eveno');
	else if(new_name.toLowerCase() === 'compliqué')
		alert('Mais c\est trivial enfin - F. Pouit');
	else if(new_name.toLowerCase() === 'chiant')
		alert('Si t\'es pas content va voir ailleur si j\'y suis');

	if (new_name.length == 0 || new_name.includes('.')) {
		alert(AlgoTouch.Msg.MACRO_INVALID_NAME);
		block.setFieldValue(old_name, 'NAME');

	} else {
		var changed = false;
		var i = 0;
		tuple[1] = new_name;

		while (i < AlgoTouch.Macro.macros.length && !changed) {
			var compare = AlgoTouch.Macro.macros[i][0];
			if (blockID !== compare) {

				if (AlgoTouch.Macro.macros[i][1] === new_name) {
					changed = true;
					var generatedName = 'macro_' + AlgoTouch.Macro.next();
					tuple[1] = generatedName;
				}

			}
			i++;
		}

		block.setFieldValue(tuple[1], 'NAME');
		AlgoTouch.App.workspace.renameVariableById(tuple[2], tuple[1]);
	}
}

Blockly.Blocks['get_macro'] = /** @lends Block.get_macro */ {
	/**
	 * Define the get_macro block<br>
	 * this block return the macro from the field macro that is displayed
	 * @constructs
	 */
	init : function() {
		this.appendDummyInput().appendField(
				new Blockly.FieldMacro('main', this.validate, [ 'macro' ]),
				'VAR');
		this.setOutput(true, 'macro');
		this.setColour(AlgoTouch.Macro.HUE);
		this.setTooltip("");
		this.setHelpUrl("");
		this.setOnChange(function(e) {
			this.validate();
		});
	},
	
	/**
	 * Every time this block is used, this function is fired in
	 * order to prevent recursion
	 */
	validate : function() {
		var current = this instanceof Blockly.FieldMacro ? this.sourceBlock_
				: this;
		var executed = current.getFieldValue('VAR');
		var executor = current.getParent();

		if (executor) {
			var tmp = executor.getParent();
			var macro = null;
			while(tmp != null){
				macro = tmp.type == 'macro' ? tmp : null;
				tmp = tmp.getParent();
			}

			if (macro && AlgoTouch.Macro.macros.length > 0) {
				var tuple;

				var seen = [ macro ];
				var i = 0;
				var ok = true;

				while (ok && i < seen.length) {
					macro = seen[i++];

					tuple = AlgoTouch.Macro.tupleByBlock(macro.id);
					if(tuple)
						if (tuple[2] === executed) {
							executor.unplug();
							executor.snapToGrid();
							ok = false;
						} else {
							var uses = AlgoTouch.App.workspace.getVariableUsesById(tuple[2]);

							for ( var j in uses) {
								var nexec = uses[j].getParent();
								var nmacro;
								if(nexec){
									tmp = nexec.getParent();
									while(tmp != null){
										nmacro = tmp.type == 'macro' ? tmp : null;
										tmp = tmp.getParent();
									}
								}
								if (nexec && nmacro && !seen.includes(nmacro))
									seen.push(nmacro);
							}

						}

				}

				if (ok === false)
					alert(AlgoTouch.Msg.MACRO_RECURSIVE_ALERT);

			}
		}
	},

	/**
	 * Get the macro block that contains the specified block
	 * @param {Block} block the block that needs to be analyzed
	 * @return null if the specified bloc is orphan, else the macro it is specified in
	 */
	container : function(block) {
		var parent = block.getParent();
		while (parent && parent.type !== 'macro')
			parent = parent.getParent();

		return parent;
	}

};

Blockly.Blocks['macro'] = /** @lends Block.macro */ {
	
	/**
	 * Define the macro bloc<br>
	 * It contains :<br>
	 * - an editable field for the name of the macro that must be unique between all macros<br>
	 * - a statement for the initialization (can be void)<br>
	 * - a statement for the until condition (can be void but in this case, the do statement will not be analyzed)<br>
	 * - a statement for the do (can be void but should not be) at least an indent of a variable should be set inside in order
	 * not to create an infinite loop<br> 
	 * - a statement for the termination (can be void)
	 * @constructs
	 */
	init : function() {
		this.appendDummyInput().appendField(AlgoTouch.Msg.MACRO_NAME)
				.appendField(new Blockly.FieldTextInput("macro"), "NAME");
		this.appendStatementInput("FROM").setCheck(['default']).appendField(
				AlgoTouch.Msg.MACRO_FROM);
		this.appendStatementInput("UNTIL").setCheck(
				["macro_cond" ]).appendField(
				AlgoTouch.Msg.MACRO_REPEAT);
		this.appendStatementInput("DO").setCheck(['default']).appendField(
				AlgoTouch.Msg.MACRO_DO);
		this.appendStatementInput("TERM").setCheck(['default']).appendField(
				AlgoTouch.Msg.MACRO_END);
		this.setColour(AlgoTouch.Macro.HUE);
		this.setTooltip("");
		this.setHelpUrl("");
	}
};

Blockly.Blocks['execute'] = /** @lends Block.execute */ {
	
	/**
	 * This block allows to call a macro from another
	 * it contains one input that only accept {@link Block.get_macro|get_macro} block
	 * @constructs
	 */
	init : function() {
		this.appendValueInput("MACRO").setCheck("macro").appendField(
				AlgoTouch.Msg.MACRO_EXECUTE);
		this.setPreviousStatement(true, ['default']);
		this.setNextStatement(true, ['default']);
		this.setColour(AlgoTouch.Macro.HUE);
		this.setTooltip("");
		this.setHelpUrl("");
	}
};

Blockly.Blocks['macro_cond'] = /** @lends Block.macro_cond */ {
	
	/**
	 * This block is used to add a condition to the until statement of the {@link Block.macro|macro} block<br>
	 * It behaves like the conditions of the {@link Block.if_block|if_block}
	 * @constructs
	 */
	init : function() {
		this.appendValueInput("A").setCheck(
				[ "integer", "var_integer", "char", "var_char", "index" ]);
		this.appendDummyInput().appendField(
				new Blockly.FieldDropdown([ [ "=", "EQUAL" ],
						[ "≠", "DIFFERENT" ], [ "<", "LOWER" ],
						[ "<=", "LOWER_EQUAL" ], [ ">", "GREATER" ],
						[ ">=", "GREATER_EQUAL" ] ]), "COND");
		;
		this.appendValueInput("B").setCheck(
				[ "integer", "var_integer", "char", "var_char", "index"  ]);
		this.setPreviousStatement(true, [ 'macro', 'macro_cond' ]);
		this.setNextStatement(true, [ 'macro_cond' ]);
		this.setColour(AlgoTouch.Macro.HUE);
		this.setTooltip("");
		this.setHelpUrl("");
	}
};