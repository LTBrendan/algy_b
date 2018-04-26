 'use-strict'

/**
 * Create the generator for AlgoTouch pseudo code
 * 
 * @namespace
 * @author Daphnis
 */
AlgoTouch.Generator = new Blockly.Generator('AlgoTouch');

/**
 * No indent for the pseudo code
 */
AlgoTouch.Generator.INDENT = '';

/**
 * return null for every block that is on his own in the workspace
 */
AlgoTouch.Generator.scrubNakedValue = function(line) {
  return null;
};

/**
 * Normalize a string by adding a carriage return if necessary
 * @function norm
 * @memberof Generator
 * @param {String} line the line to normalize
 */
AlgoTouch.Generator.norm = function(line){
  return line.endsWith('\n') ? line : line + '\n';
}

/**
 * Append every next block at the end to create a full program
 * @function scrub_
 * @memberof Generator
 * @param {Block} block the block to use
 * @param {String} the present code
 */
AlgoTouch.Generator.scrub_ = function(block, code) {
  var nextBlock = block.nextConnection && block.nextConnection.targetBlock();
  var nextCode = AlgoTouch.Generator.blockToCode(nextBlock);
  return  code + nextCode;
};
