'use-strict'

/**
 * @namespace Values
 * @author Daphnis
 */
AlgoTouch.Values = {};

/**
 * Color of the values blocks
 * @constant
 * @default
 */
AlgoTouch.Values.HUE = 160;


/**
 * Verify that the number entered in the text field is an integer
 * @function CheckInteger
 * @memberof Values
 * @callback Blockly.FieldNumber
 * @param text the text to parse
 * @return null if incorrect, else the value parsed
 */
AlgoTouch.Values.CheckInteger = function(text) {
        var res = null;
         if (text !== null) {
           
          text = String(text);
          text = text.replace(/O/ig, '0');
          text = text.replace(/,/g, '');
          
          res = parseInt(text);
          if (isNaN(res)) {
            res = null;
          }
        }

        return res;
      }



/**
 * Verify that the text entered in the text field is a char
 * @function CheckChar
 * @memberof Values
 * @callback Blockly.FieldTextInput
 * @param text the text to parse
 * @return null if incorrect, else the value parsed
 */
AlgoTouch.Values.CheckChar = function(text) {
      
      var res = null;
      if (text !== null && text.length == 1)
        res = String(text);  

      return res;

    }



Blockly.Blocks['integer'] = /** @lends Block.integer */ {

  /**
   * Define a simple value container for an integer
   * @constructs
   */
  init: function() {
    this.jsonInit({
  	  "type": "integer",
  	  "output": "integer",
  	  "colour": AlgoTouch.Values.HUE,
  	  "tooltip": "",
  	  "helpUrl": ""
	  });
    this.appendDummyInput().appendField(new Blockly.FieldNumber(0, null, null, null, AlgoTouch.Values.CheckInteger), 'VALUE');

  }
};



Blockly.Blocks['char'] = /** @lends Block.char */ {
  
 /**
  * Define a simple value container for a char
  * @constructs
  */
  init: function() {
    this.jsonInit({
    "type": "text",
    "output": "char",
    "colour": AlgoTouch.Values.HUE,
    "helpUrl": "",
    "tooltip": "",
    "extensions": [
    "parent_tooltip_when_inline"
    ]
  });

    this.appendDummyInput().appendField(new Blockly.FieldTextInput('a', AlgoTouch.Values.CheckChar), 'CHAR');


 }

};


Blockly.Blocks['read'] = /** @lends Block.read */ {
  
  /**
   * Define a block for asking a value to the user at runtime<br>
   * Contains a field for a variable which value will be changed by the user input<br>
   * A text field to display a message to the user
   * @constructs
   */
  init: function() {
    this.appendValueInput("TO_READ")
        .setCheck(["var_char","var_integer", "index"])
        .appendField(AlgoTouch.Msg.VALUES_READ);
    this.appendDummyInput()
        .appendField(AlgoTouch.Msg.VALUES_WITH_MSG + " \"")
        .appendField(new Blockly.FieldTextInput(""), "MESSAGE")
        .appendField("\"");
    this.setInputsInline(true);
    this.setColour(AlgoTouch.Values.HUE);
    this.setPreviousStatement(true, ['default']);
    this.setNextStatement(true, ['default']);
	this.setTooltip("");
	this.setHelpUrl("");
  }
};


Blockly.Blocks['write'] = /** @lends Block.write */ {

   /**
    * Define a block for displaying a value and/or a message to the user at runtime<br>
    * Contains a field for a variable which value will be displayed - optional<br>
    * A text field to display a message to the user
    * @constructs
    */
	init: function() {
      this.appendValueInput("TO_WRITE")
          .setCheck(["var_char","var_integer", "integer_array", "char_array", "integer", "char", "index", "constant"])
          .appendField(AlgoTouch.Msg.VALUES_WRITE);
      this.appendDummyInput()
          .appendField(AlgoTouch.Msg.VALUES_WITH_MSG + " \"")
          .appendField(new Blockly.FieldTextInput(""), "MESSAGE")
          .appendField("\"");
      this.setInputsInline(true);
      this.setColour(AlgoTouch.Values.HUE);
      this.setPreviousStatement(true, ['default']);
      this.setNextStatement(true, ['default']);
    this.setTooltip("");
    this.setHelpUrl("");
    }
  };