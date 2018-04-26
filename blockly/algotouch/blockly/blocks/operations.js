'use-strict'
/**
 * @namespace Ops
 * @author Daphnis
 */
AlgoTouch.Ops = {};

/**
 * Color of the operations blocks
 * @constant
 * @default
 */
AlgoTouch.Ops.HUE = 230;


Blockly.Blocks['operations'] = /** @lends Block.operations */{
	
	/**
	 * Define the operations block<br>
	 * Two fields are required and accept integers, chars, variables and indices<br>
	 * A drop down allows to select the type of operation (+ - / * %)
	 * @constructs
	 */
	init: function(){
		this.jsonInit({
			"type": "operations",
			"message0": "%1 %2 %3 %4",
			"args0": [
			{
			  "type": "input_value",
			  "name": "A",
			  "check": [
	        	  "integer",
	        	  "var_integer",
	        	  "char",
	        	  "var_char",
	        	  "index",
	        	  "constant"
	          ]
			},
			{
			  "type": "field_dropdown",
			  "name": "TYPE",
			  "options": [
			    [
			      "+",
			      "ADD"
			    ],
			    [
			      "-",
			      "SUB"
			    ],
			    [
			      "*",
			      "MULT"
			    ],
			    [
			      "/",
			      "DIV"
			    ],
			    [
			      "%",
			      "MOD"
			    ]
			  ]
			},
			{
			  "type": "input_dummy",
			  "align": "CENTRE"
			},
			{
			  "type": "input_value",
			  "name": "B",
			  "check": [
	        	"integer",
	        	"var_integer",
        	  	"char",
        	  	"var_char",
        	  	"index",
        	  	"constant"
	          ],
			  "align": "RIGHT"
			}
			],
			"inputsInline": true,
			"output": "operation_integer",
			"colour": AlgoTouch.Ops.HUE,
			"tooltip": "",
			"helpUrl": ""
		});
	}
};
