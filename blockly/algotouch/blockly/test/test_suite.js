AlgoTouch.Test = {};

AlgoTouch.Test.logs = '';

AlgoTouch.Test.allTestsOk = 0;
AlgoTouch.Test.allTestsFailed = 0;

AlgoTouch.Test.currentTestsOk = 0;
AlgoTouch.Test.currentTestsFailed = 0;

AlgoTouch.Test.currentTitle = null;

ws = null;

AlgoTouch.Test.loadTest = function(){
    if(!window.location.href.includes('?test=true'))
        if(window.location.href.includes('?'))
            window.location.href += '&test=true';
        else
            window.location.href += '?test=true';
    else
        window.location.href = window.location.href;
}

AlgoTouch.Test.runTests = function(){
    ws = AlgoTouch.App.workspace;
    logTitle('Résultat des tests :');

    AlgoTouch.Test.Vars.runTests();
    AlgoTouch.Test.Arrays.runTests();
    AlgoTouch.Test.Logic.runTests();
    AlgoTouch.Test.Op.runTests();
    AlgoTouch.Test.Values.runTests();
    AlgoTouch.Test.Macro.runTests();

    startNewTest('Fin des tests');
    
    console.log(AlgoTouch.Test.logs);
    alert(AlgoTouch.Test.logs);
}


startNewTest = function(title){
    if(AlgoTouch.Test.currentTitle != null){
        log('Résultat ' + AlgoTouch.Test.currentTitle + ' : ' + AlgoTouch.Test.currentTestsOk / (AlgoTouch.Test.currentTestsFailed + AlgoTouch.Test.currentTestsOk) * 100 + '%');
        AlgoTouch.Test.allTestsFailed += AlgoTouch.currentTestsFailed;
        AlgoTouch.Test.allTestsOk += AlgoTouch.currentTestsOk;
        AlgoTouch.Test.currentTestsFailed = 0;
        AlgoTouch.Test.currentTestsOk = 0;
    }
    logTitle(title);
    AlgoTouch.Test.currentTitle = title;
}

logTitle = function(title){
    AlgoTouch.Test.logs += title + '\n';
}

log = function(text){
    AlgoTouch.Test.logs += '\t' + text + '\n';
}

error = function(text){
    AlgoTouch.Test.logs += '\terror: ' + text + '\n';
}

connect = function(source, input, input_name, andthen){
    var connection = new Blockly.Connection(input, Blockly.OUTPUT_VALUE);
    source.getInput(input_name).connection.connect(connection, Blockly.INPUT_VALUE);
    if(andthen)
        andthen();
}

assertTrue = function(res, exception){
    if(res != true){
        if(exception)
            error(exception);
        AlgoTouch.Test.currentTestsFailed++;
    }else{
        AlgoTouch.Test.currentTestsOk++;
    }
}

assertFalse = function(res, exception){
    if(res != false){
        if(exception)
            error(exception);
        AlgoTouch.Test.currentTestsFailed++;
    }else{
        AlgoTouch.Test.currentTestsOk++;
    }
}

assertContains = function(what, inwhat, exception){
    if(!inwhat.includes(what)){
        if(exception)
            error(exception);
        AlgoTouch.Test.currentTestsFailed++;
    }else{
        AlgoTouch.Test.currentTestsOk++;
    }
}

assertEquals = function(o1, o2, exception){
    if(o1 != o2){
        if(exception)
            error(exception + '\nexpected: ' + o2 + '\ngot: ' + o1);
        AlgoTouch.Test.currentTestsFailed++;
    }else{
        AlgoTouch.Test.currentTestsOk++;
    }
}