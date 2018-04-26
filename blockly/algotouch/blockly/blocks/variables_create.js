'use-strict'


/**
 * Callback used to create the buttons for creating variables
 * @function whenUserPressVariables
 * @callback Category
 * @memberof Variables
 * @param {Workspace} workspace the workspace of the toolbox
 * @return the xml string used to build the variables creation tool box
 */
AlgoTouch.Variables.whenUserPressVariables = function(workspace) {
  var xml = [];
 
  for (var i = 0; i < AlgoTouch.Variables.Types.length; i++) {
    var button = goog.dom.createDom('button');
    button.setAttribute('text', AlgoTouch.Variables.Names[i]);
    button.setAttribute('callbackKey', 'CREATE_VARIABLE');

    workspace.registerButtonCallback('CREATE_VARIABLE', function(button) {
      var type = button.text_;
      type = AlgoTouch.Variables.Types[AlgoTouch.Variables.Names.indexOf(type)];

      if(type === AlgoTouch.Variables.Types[0] || type === AlgoTouch.Variables.Types[1]){
        AlgoTouch.Variables.createVariable('CA.' + type, function(name){AlgoTouch.App.workspace.createVariable(name, type)});  
      }else{
        AlgoTouch.Variables.createVariable(Blockly.Msg.NEW_VARIABLE_TITLE, function(name){
          var execType = type;
          if(type === AlgoTouch.Variables.Types[4])
            executeCode('putTempConstant ' + name + ' ' + prompt(AlgoTouch.Msg.VARIABLES_VALUE));
          else if(type === AlgoTouch.Variables.Types[5]){
            var arr = AlgoTouch.Variables.getArray();
            if(arr)
              execType = 'index_' + arr.getId();
            else
              execType = null;
          }else {
            if(name.toLowerCase() === 'constante')
              alert('Faudrait savoir quoi !!')
            else if(name.toLowerCase() === 'easter_egg')
              alert('Y\'en a beaucoup trop pour toi.. Abandonne, conseil d\'ami')
            else if(name.toLowerCase() === 'mdr' || name.toLowerCase() === 'ptdr')
              alert('Y\'a quoi de drôle ??')
          }

          if(execType)
            AlgoTouch.App.workspace.createVariable(name, execType);
  
        }); 

        
      }

    });

    xml.push(button);

    var blockList = AlgoTouch.Variables.createVariableGetterAndSetter(workspace, AlgoTouch.Variables.Types[i]);
    xml = xml.concat(blockList);
  }
  
  return xml;
};

/**
 * When the user creates an index, he is asked to choose an array for the index to be associated with<br>
 * While the array entered is not known, the user will be prompted until he entered a correct name or
 * he press cancel
 * @function getArray
 * @memberof Variables
 * @return {VariableModel} the array choosed or null if he press cancel
 */
AlgoTouch.Variables.getArray = function(){
  var arrays = AlgoTouch.App.workspace.getVariablesOfType('char_array').concat(AlgoTouch.App.workspace.getVariablesOfType('integer_array'));
  var found = false;
  var arr = null;
  if(arrays.length == 1)
    arr = arrays[0];
  else if(arrays.length > 1)
    while(!found){
      arr = prompt(AlgoTouch.Msg.ARRAY_CHOOSE);
      var i = arrays.length - 1;
      while(i >= 0 && !found){
        found = arr ? arrays[i].name == arr : true;
        i--;
      }
      if(found && arr)
        arr = arrays[i+1];
    }

  return arr;
}

/**
 * Prompt the user and create a variable if everything is correct
 * @function createVariable
 * @memberof Variables
 * @param {String} display the text to show in the prompt
 * @param {function} callback that will create the variable
 * @param {boolean} modifying if the user is prompt to rename a variable
 */
AlgoTouch.Variables.createVariable = function(display, creator, modifying) {
  var workspace = AlgoTouch.App.workspace;
  
  var promptAndCheckWithAlert = function(defaultName) {
    Blockly.Variables.promptName(display, defaultName,
        function(text) {
          if (text) {
            text = text.toLowerCase();
            var existing = false;
            var vars = AlgoTouch.App.workspace.getAllVariables();
            var i = vars.length;
            while(i-- > 0 && !existing)
              existing = (text === vars[i].name);
            

            var incorrect = text.includes(' ') || text.includes('.') || '0123456789'.includes(text[0]);

            if(!modifying && (existing || incorrect)){
              var msg = existing ? AlgoTouch.Msg.VARIABLES_NAME_ALREADY_EXISTS : AlgoTouch.Msg.VARIABLES_INVALID_NAME;
              Blockly.alert(msg, function() {
                    promptAndCheckWithAlert(text); 
              });
            }else {
              creator(text);
            }
          }
        });
  };
  promptAndCheckWithAlert('');
};


//Fonction permettant de créer les blocs pour chaque variable

/**
 * Create getter and setter for every variables type
 * @function createVariableGetterAndSetter
 * @memberof Variables
 * @param {Workspace} the workspace associated
 * @param {String} type the type of the variable
 * @return xml with the getter and setter blocks for the type of the variable
 */
AlgoTouch.Variables.createVariableGetterAndSetter = function(workspace, type) {

  var variableModelList = workspace.getVariablesOfType(type);
  if(type === 'index'){
    var all = workspace.getAllVariables();
    for (var i = all.length - 1; i >= 0; i--) {
      if(all[i].type.includes(type))
        variableModelList.push(all[i]);
    }
  }


  var xmlList = [];
  if (variableModelList.length > 0) {
    var firstVariable = variableModelList[0];

    if (Blockly.Blocks['set_' + type]) {

      var blockText = '<xml>' +
          '<block type="set_' + type + '">' +
          '</block>'+
          '</xml>';

      var block = Blockly.Xml.textToDom(blockText).firstChild;

      xmlList.push(block);
    }


    


    if (Blockly.Blocks['get_' + type]) {

      var blockText = '<xml>' +
          '<block type="get_' + type + '">' +
          '</block>'+
          '</xml>';

       
      var block = Blockly.Xml.textToDom(blockText).firstChild;
       
      xmlList.push(block);
    }
    
  }
  return xmlList;
};


