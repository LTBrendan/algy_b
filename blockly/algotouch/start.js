
/**
 * Called when loading the page for the first time, initialize the workspace with the main macro
 * @function Starter
 */
AlgoTouch.Starter = function() {
	if (!reloading) {
		var main = '<xml><block x="10" y="0" type="macro" editable="false" deletable="false" movable="false"><field name="NAME">main</field></block></xml>';
		Blockly.Xml.domToWorkspace(Blockly.Xml.textToDom(main),
				AlgoTouch.App.workspace);
	}

}

/**
 * Loop through array and remove items if the callback return true
 * @function removeIf
 * @param {Array} array the array to loop through
 * @param {function} callback to tell if the value need to be removed or not
 */
removeIf = function(array, callback) {
	var i = array.length;
	while (i--) {
		if (callback(array[i])) {
			array.splice(i, 1);
		}
	}
};

/**
 * Get the runnable code
 * @function getRunnableCode
 * @memberof App
 * @return {String} the runnable code exported from the workspace
 */
AlgoTouch.App.getRunnableCode = function() {
	var blocks = AlgoTouch.App.workspace.getTopBlocks(false);
	removeIf(blocks, function(block) {
		return block.type !== 'macro';
	});

	var code = '';

	for (var i = blocks.length - 1; i >= 0; i--) {
		code += AlgoTouch.Generator.blockToCode(AlgoTouch.App.workspace
				.getBlockById(blocks[i].id));
	}

	return AlgoTouch.Generator.finish(code);
}

/**
 * Get the selected macro
 * @function getSelectedMacro
 * @memberof App
 * @return {String} the name of the selected macro or null
 */
AlgoTouch.App.getSelectedMacro = function() {
	var macro = null;
	if (Blockly.selected) {
		macro = Blockly.selected;
		if(macro)
			macro = macro.getFieldValue('NAME');
	}
	return macro;
}

/**
 * Play the code by calling the java method
 * @function play
 */
play = function() {
	if (typeof controller !== 'undefined') {
		if (!stepBool) {
			controller.execute(AlgoTouch.App.getRunnableCode(), AlgoTouch.App
					.getSelectedMacro());
		} else {
			if (!controller.isRunning()) {
				controller.execute(AlgoTouch.App.getRunnableCode(),
						AlgoTouch.App.getSelectedMacro());
			}
			controller.steping(deepBool);
		}
	} else
		console.log(AlgoTouch.App.getRunnableCode());
}

var deepBool = false;
var stepBool = false;

/**
 * Change step mode
 * @function step
 * @param stepMode step or not
 * @param deepMode deepmode or not
 */
step = function(stepMode, deepMode) {
	deepBool = deepMode;
	stepBool = stepMode;
}

/**
 * Interrupt the program
 * @function interrupt
 */
interrupt = function() {
	if (typeof controller !== 'undefined')
		controller.normal();
	else
		console.log('interrupt')
}

/**
 * Show the parameters popup
 * @function showParams
 */
showParams = function() {
	if (typeof controller !== 'undefined')
		controller.openParams();
	else
		console.log("openParams");
}

/**
 * Execute a single line of code
 * @function executeCode
 */
executeCode = function(code) {
	if (typeof controller !== 'undefined')
		controller.exec(code);
	else
		console.log(code);
}

/**
 * Extract the part of a macro from a string
 * @function getPart
 * @param {String} str the current part of the macro
 * @return the macro bloc part associated with the specified string
 */
getPart = function(str) {
	str = str.toUpperCase();
	var part = null;
	if (str.includes('FROM'))
		part = 'FROM';
	else if (str.includes('EVAL'))
		part = 'UNTIL';
	else if (str.includes('CORE'))
		part = 'DO';
	else if (str.includes('ALGORITHM'))
		part = 'TERM';

	return part;
}

/**
 * Highlight a group of blocks
 * @function highlightGroup
 * @param {String} name the name of the macro
 * @param {String} part the part of the macro to highlight
 * @param {boolean} if highlight or not
 */
highlightGroup = function(name, part, highlight) {
	var macro = AlgoTouch.Macro.getMacroBlock(name);
	if (macro) {
		var tmp = macro.getInputTargetBlock(part);
		var hg = function(block, highlight) {
			if (block) {
				block.setShadow(highlight);
				block.getChildren().forEach(function(b) {
					hg(b, highlight)
				});
			}

		};
		hg(tmp, highlight);
	}
}

last = null;
lastpart = null;

/**
 * Highlight the new group of blocks and "unhighlight" the old one
 * @function highlight
 * @param {String} name the name of the macro
 * @param {String} part the part of the macro to highlight
 */
highlight = function(name, part) {
	part = getPart(part);
	if (last && lastpart) {
		highlightGroup(last, lastpart, false);
	}
	last = name;
	lastpart = part;
	highlightGroup(name, part, true);

}

/**
 * Export the workspace to a file by calling Java function
 * @function exportWorkspace
 */
exportWorkspace = function() {
	var workspace = Blockly.Xml.domToText(Blockly.Xml.workspaceToDom(
			AlgoTouch.App.workspace, true));
	var vars = Blockly.Xml.domToText(Blockly.Xml
			.variablesToDom(AlgoTouch.App.workspace.getAllVariables()));
	if (typeof controller !== 'undefined')
		controller.save(workspace, vars);
	else {
		console.log(workspace);
		console.log(vars);
	}

}

/**
 * Load a workspace from parameters that are passed by Java after choosing a file
 * @function loadWorkspace
 */
loadWorkspace = function() {

	if (typeof controller !== 'undefined') {
		var retrieved = controller.load();
		if (retrieved != null) {
			AlgoTouch.Core.clear();
			Blockly.Xml.domToVariables(Blockly.Xml.textToDom(retrieved[1]),
					AlgoTouch.App.workspace);
			Blockly.Xml.domToWorkspace(Blockly.Xml.textToDom(retrieved[0]),
					AlgoTouch.App.workspace);
		}
	} else
		console.log("Loading file");
}

/**
 * Open the documentation
 * @function openDoc
 */
openDoc = function() {
	if (typeof switcher != 'undefined') {
		switcher.openDoc();
	}
}

/**
 * Export the workspace as a text program
 * @function exportTxt
 */
exportTxt = function() {
	if (typeof controller !== 'undefined')
		controller.exportTxt(AlgoTouch.App.getRunnableCode());
	else
		console.log("error");
}

/**
 * Handle keyboard shortcuts
 * @function handler
 * @param e the event
 */
handler = function(e) {
	if (e.ctrlKey) {
		if (e.keyCode == 79) {
			loadWorkspace();
		} else if (e.keyCode == 83) {
			exportWorkspace();
		} else if (e.keyCode == 84) {
			exportTxt();
		} else if (e.keyCode == 68) {
			openDoc();
		}
	} else {
		if (e.keyCode == 116) {
			play();
		}
	}
}

/**
 * Set all blocks not movable or not
 * @function setMovable
 * @param {boolean} movable true if movable, false otherwise 
 */
setMovable = function(movable) {
	AlgoTouch.App.workspace.getAllBlocks().forEach(function(b) {
		b.setMovable(movable)
	})
}

// register the handler
document.addEventListener('keyup', handler, false);

document.getElementById("load").title = AlgoTouch.Msg.LOAD
document.getElementById("text").title = AlgoTouch.Msg.TEXT;
document.getElementById("save").title = AlgoTouch.Msg.SAVE;
document.getElementById("run").title = AlgoTouch.Msg.RUN;
document.getElementById("stop").title = AlgoTouch.Msg.STOP;
document.getElementById("doc").title = AlgoTouch.Msg.DOC;
document.getElementById("params").title = AlgoTouch.Msg.PARAMS;