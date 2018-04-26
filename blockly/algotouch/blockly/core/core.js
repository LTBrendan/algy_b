
/**
 * Main NameSpace
 * @namespace
 * @author Daphnis
 */
var AlgoTouch = {};

/**
 * @namespace
 * @author Daphnis
 */
AlgoTouch.Core = {};

// Enregistrement de la fonction whenUserPressVariables pour qu'elle soit exécutée lorsque l'utilisateur clique sur la catégory Variables
/**
 * Initialize the workspace and the tool box, register the event listeners
 * @function init
 * @memberof Core
 */
AlgoTouch.Core.init = function(){
	
	AlgoTouch.App.workspace.registerToolboxCategoryCallback('ALGOTOUCH_VARIABLE', AlgoTouch.Variables.whenUserPressVariables);
	AlgoTouch.Variables.Names = [AlgoTouch.Msg.VARIABLES_CREATE_CHAR_ARRAY, AlgoTouch.Msg.VARIABLES_CREATE_INTEGER_ARRAY, AlgoTouch.Msg.VARIABLES_CREATE_CHAR, AlgoTouch.Msg.VARIABLES_CREATE_INTEGER, AlgoTouch.Msg.VARIABLES_CREATE_CONSTANT, AlgoTouch.Msg.VARIABLES_CREATE_INDEX];

	var macro_name_changed = null;
	var just_created = null;

	isMacro = function(block){
		return block && block.type == 'macro';
	}

	onetime = function(e){
		if(e.type === Blockly.Events.BLOCK_MOVE){
			AlgoTouch.App.workspace.clearUndo();
			AlgoTouch.App.workspace.removeChangeListener(onetime);
		}
	}

	AlgoTouch.App.workspace.addChangeListener(onetime);


	AlgoTouch.App.workspace.addChangeListener(function (e){
		var block = AlgoTouch.App.workspace.getBlockById(e.blockId);
		var type = e.type;

		if(!reloading){

			if(type === Blockly.Events.BLOCK_CREATE && isMacro(block)){
				
				if(AlgoTouch.Macro.nameOf(e.blockId) === 'macro'){
					var num = AlgoTouch.Macro.index;
					block.setFieldValue('macro_' + num, 'NAME');	
				}

				just_created = true;
				AlgoTouch.Macro.addMacro(e.blockId);

			}else if(type === Blockly.Events.BLOCK_CHANGE && isMacro(block)){
				if(!just_created && !macro_name_changed)
					macro_name_changed = e.blockId;
				just_created = false;
				
			}else if(type === Blockly.Events.UI){
				if(macro_name_changed){
					AlgoTouch.Macro.checkNames(macro_name_changed);
					macro_name_changed = null;
				}
			}else if(type === Blockly.Events.BLOCK_DELETE){
				AlgoTouch.Macro.delMacro(e.blockId);
			}else if(type === Blockly.Events.VAR_DELETE && e.varType !== 'macro'){
				executeCode('delete ' + e.varName);
				AlgoTouch.App.workspace.getVariablesOfType('index_' + e.varId).forEach(function(m){
						AlgoTouch.App.workspace.deleteVariableById(m.getId())})
			}else if(type === Blockly.Events.VAR_RENAME && AlgoTouch.App.workspace.getVariableById(e.varId).type !== 'macro'){
				executeCode('rename ' + e.oldName + ' ' + e.newName);
			}else if(type === Blockly.Events.VAR_CREATE && e.varType !== 'macro'){
				if(e.varType.startsWith('index')){
					var arrayID = e.varType.replace('index_', '');
					var vartab = AlgoTouch.App.workspace.getVariableById(arrayID);
					if(vartab)
						executeCode('create_index ' + e.varName + ' ' + vartab.name);
					
				}else
					executeCode('create_' + e.varType + ' ' + e.varName);
			}else if(type === Blockly.Events.BLOCK_MOVE){
				var json = e.toJson();
				if(json.newInputName == 'ARRAY' || json.newInputName == 'INDEX'){
					var block = AlgoTouch.App.workspace.getBlockById(json.newParentId);
					if(block.type !== 'length_of')
					AlgoTouch.App.workspace.getBlockById(json.newParentId).updateArray();
				}
			
			}
		}else{
			if(type === Blockly.Events.VAR_CREATE){
				loadingVars--;
				reloading = loadingVars;
			}
		}
	});

}

/**
 * Clear all the workspace and the variables
 * @function clear
 * @memberof Core
 */
AlgoTouch.Core.clear = function(){
	AlgoTouch.Macro.macros = [];
	var vars = AlgoTouch.App.workspace.getAllVariables();
	for(i in vars)
		if(vars[i].type !== 'macro')
			executeCode('delete ' + vars[i].name);

	AlgoTouch.App.workspace.clear();
}