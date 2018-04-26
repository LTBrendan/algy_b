'use strict'

/**
 * @namespace Logic
 * @author Daphnis
 */
AlgoTouch.Logic = {}

/**
 * Color of the logics blocks
 * @constant
 * @default
 */
AlgoTouch.Logic.HUE = 210;



// Définition du bloc logique 'sinon' pour la boucle until des macros

// Blockly.Blocks['loop_out'] = {
// 	init: function() {
// 	    this.setColour(AlgoTouch.Logic.HUE);
// 	    this.itemCount_ = 0;
// 	    this.updateShape_();
// 	    this.setOutput(true, 'logic_boolean');
// 	    this.appendValueInput('ADD0').setCheck(['boolean']);
//     	this.appendValueInput('ADD1').setCheck(['boolean'])
//     		.appendField(AlgoTouch.Msg.COND_ELSE);
// 	    this.setMutator(new Blockly.Mutator(['else_if_item']));
// 	    this.setTooltip("");
// 	    this.setHelpUrl("");
//   },
//   /**
//    * Create XML to represent list inputs.
//    * @return {!Element} XML storage element.
//    * @this Blockly.Block
//    */
//   mutationToDom: function() {
//     var container = document.createElement('mutation');
//     container.setAttribute('items', this.itemCount_);
//     return container;
//   },
//   /**
//    * Parse XML to restore the list inputs.
//    * @param {!Element} xmlElement XML storage element.
//    * @this Blockly.Block
//    */
//   domToMutation: function(xmlElement) {
//     this.itemCount_ = parseInt(xmlElement.getAttribute('items'), 10);
//     this.updateShape_();
//   },
//   /**
//    * Populate the mutator's dialog with this block's components.
//    * @param {!Blockly.Workspace} workspace Mutator's workspace.
//    * @return {!Blockly.Block} Root block in mutator.
//    * @this Blockly.Block
//    */
//   decompose: function(workspace) {
//     var containerBlock = workspace.newBlock('else_if_container');
//     containerBlock.initSvg();
//     var connection = containerBlock.getInput('STACK').connection;
//     for (var i = 0; i < this.itemCount_; i++) {
//       var itemBlock = workspace.newBlock('else_if_item');
//       itemBlock.initSvg();
//       connection.connect(itemBlock.previousConnection);
//       connection = itemBlock.nextConnection;
//     }
//     return containerBlock;
//   },
//   /**
//    * Reconfigure this block based on the mutator dialog's components.
//    * @param {!Blockly.Block} containerBlock Root block in mutator.
//    * @this Blockly.Block
//    */
//   compose: function(containerBlock) {
//     var itemBlock = containerBlock.getInputTargetBlock('STACK');
//     // Count number of inputs.
//     var connections = [];
//     while (itemBlock) {
//       connections.push(itemBlock.valueConnection_);
//       itemBlock = itemBlock.nextConnection &&
//           itemBlock.nextConnection.targetBlock();
//     }
//     // Disconnect any children that don't belong.
//     for (var i = 2; i < this.itemCount_ + 2; i++) {
//       var connection = this.getInput('ADD' + i).connection.targetConnection;
//       if (connection && connections.indexOf(connection) == -1) {
//         connection.disconnect();
//       }
//     }
//     this.itemCount_ = connections.length;
//     this.updateShape_();
//     // Reconnect any child blocks.
//     for (var i = 2; i < this.itemCount_ + 2; i++) {
//       Blockly.Mutator.reconnect(connections[i], this, 'ADD' + i);
//     }
//   },
//   /**
//    * Store pointers to any connected child blocks.
//    * @param {!Blockly.Block} containerBlock Root block in mutator.
//    * @this Blockly.Block
//    */
//   saveConnections: function(containerBlock) {
//     var itemBlock = containerBlock.getInputTargetBlock('STACK');
//     var i = 2;
//     while (itemBlock) {
//       var input = this.getInput('ADD' + i);
//       itemBlock.valueConnection_ = input && input.connection.targetConnection;
//       i++;
//       itemBlock = itemBlock.nextConnection &&
//           itemBlock.nextConnection.targetBlock();
//     }
//   },
//   /**
//    * Modify this block to have the correct number of inputs.
//    * @private
//    * @this Blockly.Block
//    */
//   updateShape_: function() {
//     // Add new inputs.
//     for (var i = 2; i < this.itemCount_ + 2; i++) {
//       if (!this.getInput('ADD' + i)) {
//         var input = this.appendValueInput('ADD' + i);    
//         input.appendField(AlgoTouch.Msg.COND_ELSE).setCheck(['boolean']);
//       }
//     }
//     // Remove deleted inputs.
//     while (this.getInput('ADD' + i)) {
//       this.removeInput('ADD' + i);
//       i++;
//     }
//   }
// }




// Définition du bloc logique de comparaisons

// Blockly.Blocks['logic_comparisons'] = {
//   init: function() {
//     this.jsonInit({
//       "type": "logic_comparisons",
//       "message0": "%1 %2 %3",
//       "args0": [
//         {
//           "type": "input_value",
//           "name": "A",
//           "check": [
//         	  "integer",
//         	  "var_integer",
//         	  "char",
//         	  "var_char"
//         	]
        
//         },
//         {
//           "type": "field_dropdown",
//           "name": "NAME",
//           "options": [
//             [
//               "=",
//               "EQUAL"
//             ],
//             [
//               "≠",
//               "DIFFERENT"
//             ],
//             [
//               "<",
//               "LOWER"
//             ],
//             [
//               "<=",
//               "LOWER_EQUAL"
//             ],
//             [
//               ">",
//               "GREATER"
//             ],
//             [
//               ">=",
//               "GREATER_EQUAL"
//             ]
//           ]
//         },
//         {
//           "type": "input_value",
//           "name": "B",
//           "check": [
//         	  "integer",
//         	  "var_integer",
//         	  "char",
//         	  "var_char"
//           ]
//         }
//       ],
//       "inputsInline": true,
//       "output": "boolean",
//       "colour": AlgoTouch.Logic.HUE,
//       "tooltip": "",
//       "helpUrl": ""
//     });
//   }
// };




Blockly.Blocks['if_block'] = /** @lends Block.if_block */ {
		
  /**
   * Define the if_block.
   * The block contains 2 fields and 1 drop down for the condition and 2 statements, one if the
   * condition is true, the other for the else part. 
   * The two fields accepts integers, chars, variables of type integer and chars
   * The drop down gives the choice between (= != <= >= < >)
   * Everything is accepted in the statements except for the macro condition block
   * @constructs
   */
  init: function() {
    this.appendValueInput("A")
        .setCheck([
            "integer",
            "var_integer",
            "char",
            "var_char",
            "index",
            "constant"
          ])
        .appendField(AlgoTouch.Msg.COND_IF);
    this.appendValueInput("B")
        .setCheck([
            "integer",
            "var_integer",
            "char",
            "var_char",
            "index",
            "constant"
          ])
        .appendField(new Blockly.FieldDropdown([["=","EQUAL"], ["≠", "DIFFERENT"], ["<", "LOWER"], ["<=", "LOWER_EQUAL"], [">", "GREATER"], [">=", "GREATER_EQUAL"]]), "TYPE");
    this.appendStatementInput("DO")
        .setCheck(['default'])
        .appendField(AlgoTouch.Msg.COND_THEN);
    this.appendStatementInput("ELSE")
        .setCheck(['default'])
        .appendField(AlgoTouch.Msg.COND_ELSE);
    this.setInputsInline(true);
    this.setPreviousStatement(true, ['default']);
    this.setNextStatement(true, ['default']);
    this.setColour(AlgoTouch.Logic.HUE);
    this.setTooltip("");
    this.setHelpUrl("");
  }
};
