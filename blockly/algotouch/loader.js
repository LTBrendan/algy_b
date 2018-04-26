'use-strict'


/**
 * @namespace
 * @author Daphnis & Google
 */
AlgoTouch.App = {};

/**
 * Enumeration of all languages available
 * @readonly
 * @enum {String}
 */
AlgoTouch.App.languages = {
  'en': 'English',
  'fr': 'Français'
};

/**
 * Main workspace of the application
 */
AlgoTouch.App.workspace = null;

/**
 * If the application is reloading (for example after the language is changed)
 */
var reloading = false;

/**
 * Number of variables to load when reloading
 */
var loadingVars = 0;

/**
 * Get the language used
 * @function getLang
 * @memberof App
 * @return the language used or 'fr' by default
 */
AlgoTouch.App.getLang = function() {
  var url = new URL(window.location.href);
  var lang = url.searchParams ? url.searchParams.get("lang") : AlgoTouch.Lang.default;
  if (AlgoTouch.App.languages[lang] === undefined) {
    lang = AlgoTouch.Lang.default;
  }
  return lang;
};


/**
 * Load the blocks from the local storage
 * @function loadBlocks
 * @memberof App
 * @param defaultXml - optional
 */
AlgoTouch.App.loadBlocks = function(defaultXml) {
  try {
    var loadOnce = window.sessionStorage.loadOnceBlocks;
    var macros = window.sessionStorage.macros;
    var lst = window.sessionStorage.last;
    var lstprt = window.sessionStorage.lastpart;
    var ldg = window.sessionStorage.varsToLoad;
  } catch(e) {
    // Firefox sometimes throws a SecurityError when accessing sessionStorage.
    // Restarting Firefox fixes this, so it looks like a bug.
    var loadOnce = null;
  }
  if ('BlocklyStorage' in window && window.location.hash.length > 1) {
    // An href with #key trigers an AJAX call to retrieve saved blocks.
    BlocklyStorage.retrieveXml(window.location.hash.substring(1));
  } else if (loadOnce) {
    // Language switching stores the blocks during the reload.
    delete window.sessionStorage.loadOnceBlocks;
    delete window.sessionStorage.macros;
    reloading = true;

    var xml = Blockly.Xml.textToDom(loadOnce);
    Blockly.Xml.domToWorkspace(xml, AlgoTouch.App.workspace);

    var macros = JSON.parse(macros);
    for (var i = macros.length - 1; i >= 0; i--) {
    	AlgoTouch.Macro.addMacro(macros[i][0], macros[i][1], macros[i][2]);
    }
    AlgoTouch.Macro.index = AlgoTouch.Macro.macros.length;

    last = JSON.parse(lst);
    lastpart = JSON.parse(lstprt);
    loadingVars = ldg;
    
  } else if (defaultXml) {
    // Load the editor with default starting blocks.
    var xml = Blockly.Xml.textToDom(defaultXml);
    Blockly.Xml.domToWorkspace(xml, AlgoTouch.App.workspace);
  } else if ('BlocklyStorage' in window) {
    // Restore saved blocks in a separate thread so that subsequent
    // initialization is not affected from a failed load.
    window.setTimeout(BlocklyStorage.restoreBlocks, 0);
  }

};

/**
 * Save all the workspace and the information needed in the local storage
 * @function save
 * @memberof App
 */
AlgoTouch.App.save = function(){
  try{
      if (typeof Blockly != 'undefined' && window.sessionStorage) {
        var xml = Blockly.Xml.workspaceToDom(AlgoTouch.App.workspace);
        var text = Blockly.Xml.domToText(xml);
        window.sessionStorage.macros = JSON.stringify(AlgoTouch.Macro.macros);
        window.sessionStorage.last = JSON.stringify(last);
        window.sessionStorage.lastpart = JSON.stringify(lastpart);
        window.sessionStorage.loadOnceBlocks = text;
        window.sessionStorage.varsToLoad = AlgoTouch.App.workspace.getAllVariables().length;
      }
    }catch(e){

    }
}


/**
 * Save the blocks and reload with a different language.
 */
AlgoTouch.App.changeLanguage = function() {
  AlgoTouch.App.save();
  

  var languageMenu = document.getElementById('languageMenu');
  var newLang = encodeURIComponent(
      languageMenu.options[languageMenu.selectedIndex].value);
  var search = window.location.search;
  if (search.length <= 1) {
    search = '?lang=' + newLang;
  } else if (search.match(/[?&]lang=[^&]*/)) {
    search = search.replace(/([?&]lang=)[^&]*/, '$1' + newLang);
  } else {
    search = search.replace(/\?/, '?lang=' + newLang + '&');
  }

  if(typeof controller != 'undefined')
    controller.changeLanguage(newLang);
  else
    console.log('changing language to ' + newLang);

  window.location = window.location.protocol + '//' +
      window.location.host + window.location.pathname + search;
};


AlgoTouch.App.getBBox_ = function(element) {
  var height = element.offsetHeight;
  var width = element.offsetWidth;
  var x = 0;
  var y = 0;
  do {
    x += element.offsetLeft;
    y += element.offsetTop;
    element = element.offsetParent;
  } while (element);
  return {
    height: height,
    width: width,
    x: x,
    y: y
  };
};


/**
 * User's language (e.g. "en").
 * @type {string}
 */
AlgoTouch.App.LANG = AlgoTouch.App.getLang();




/**
 * Initialize Blockly.  Called on page load.
 */
AlgoTouch.App.init = function() {
  AlgoTouch.App.initLanguage();

  var container = document.getElementById('content_area');

  var onresize = function(e) {
    var bBox = AlgoTouch.App.getBBox_(container);

    var el = document.getElementById('content_blocks');
    el.style.top = bBox.y + 'px';
    el.style.left = bBox.x + 'px';
    // Height and width need to be set, read back, then set again to
    // compensate for scrollbars.
    el.style.height = bBox.height + 'px';
    el.style.height = (2 * bBox.height - el.offsetHeight) + 'px';
    el.style.width = bBox.width + 'px';
    el.style.width = (2 * bBox.width - el.offsetWidth) + 'px';
    
    
  };
  window.addEventListener('resize', onresize, false);


  // Construct the toolbox XML, replacing translated variable names.
  var toolboxText = document.getElementById('toolbox').outerHTML;

  toolboxText = toolboxText.replace(/@(\w+)\.(\w+)/g,
      function(m, p1, p2) {
        return AlgoTouch[p1][p2];
      });

  var toolboxXml = Blockly.Xml.textToDom(toolboxText);

  AlgoTouch.App.workspace = Blockly.inject('content_blocks',
      {grid:
          {spacing: 20,
           length: 2,
           colour: '#ccc',
           snap: true},
       media: '../blockly-master/media/',
       toolbox: toolboxXml,
       sounds: false,
       rtl: false,
       zoom: {
        controls: true,
        wheel: true,
        startScale: 1.0,
        maxScale: 3,
        minScale: 0.5,
        scaleSpeed: 1.2
      }

      });

  AlgoTouch.Core.init();
  AlgoTouch.App.loadBlocks('');

  AlgoTouch.Starter();
  var url = new URL(window.location.href);
  var test = url.searchParams.get("test") || false;

  if(test === 'true')
    AlgoTouch.Test.runTests();

  if ('BlocklyStorage' in window) {
    // Hook a save function onto unload.
    BlocklyStorage.backupOnUnload(AlgoTouch.App.workspace);
  }
 
  
  onresize();
  Blockly.svgResize(AlgoTouch.App.workspace);


};


/**
 * Initialize the page language.
 */
AlgoTouch.App.initLanguage = function() {

  document.head.parentElement.setAttribute('lang', AlgoTouch.App.LANG);

  // Sort languages alphabetically.
  var languages = [];
  for (var lang in AlgoTouch.App.languages) {
    languages.push([AlgoTouch.App.languages[lang], lang]);
  }
  var comp = function(a, b) {
    if (a[0] > b[0]) return 1;
    if (a[0] < b[0]) return -1;
    return 0;
  };

  languages.sort(comp);
  // Populate the language selection menu.
  var languageMenu = document.getElementById('languageMenu');
  languageMenu.options.length = 0;
  for (var i = 0; i < languages.length; i++) {
    var tuple = languages[i];
    var lang = tuple[tuple.length - 1];
    var option = new Option(tuple[0], lang);
    if (lang == AlgoTouch.App.LANG) {
      option.selected = true;
    }
    languageMenu.options.add(option);
  }
  languageMenu.addEventListener('change', AlgoTouch.App.changeLanguage, true);

};



document.write('<script src="blockly/msg/' + AlgoTouch.App.LANG + '.js"></script>\n');

document.write('<script src="../blockly-master/msg/js/' + AlgoTouch.App.LANG + '.js"></script>\n');

window.addEventListener('load', AlgoTouch.App.init);