// x_load.js
// X v3.15.1, Cross-Browser DHTML Library from Cross-Browser.com
// Copyright (c) 2002,2003,2004 Michael Foster (mike@cross-browser.com)
// This library is distributed under the terms of the LGPL (gnu.org)

// Global Variables:
// now i've got these vars in 2 lib files - crap. i'd like to leave them here... but i'm not sure
var xVersion='3.15.1',xNN4=false,xOp7=false,xOp5or6=false,xIE4Up=false,xIE4=false,xIE5=false,xUA=navigator.userAgent.toLowerCase();
if(window.opera){
  xOp7=(xUA.indexOf('opera 7')!=-1 || xUA.indexOf('opera/7')!=-1);
  if (!xOp7) xOp5or6=(xUA.indexOf('opera 5')!=-1 || xUA.indexOf('opera/5')!=-1 || xUA.indexOf('opera 6')!=-1 || xUA.indexOf('opera/6')!=-1);
}
else if (document.layers) {xNN4=true;}
else if (document.all) {
  xIE4Up=xUA.indexOf('msie')!=-1 && parseInt(navigator.appVersion)>=4;
  xIE4=xUA.indexOf('msie 4')!=-1;
  xIE5=xUA.indexOf('msie 5')!=-1;
}

// xInclude()
// Call before the window.onload event.
// Only supports files with extensions '.js' and '.css'.
// Automatically loads files with NN4 support if browser is NN4.
// Returns true if browser has minimal dhtml support, else returns false.

function xInclude(url1, url2, etc)
{
  if (document.getElementById || document.all || document.layers) {
    var h, f;
    for (var i=0; i<arguments.length; ++i) {
      h = ''; // html to be written
      f = arguments[i].toLowerCase(); // lowercase file url
      // JS
      if (f.indexOf('.js') != -1) {
        if (xNN4) {
          var c='x_core', e='x_event', n='_nn4';
          if (f.indexOf(c) != -1) { f = f.replace(c, c+n); }
          else if (f.indexOf(e) != -1) { f = f.replace(e, e+n); }
        }
        h = "<script type='text/javascript' src='" + f + "'></script>";
      }
      // CSS
      else if (f.indexOf('.css') != -1) {
        h = "<link rel='stylesheet' type='text/css' href='" + f + "'>";
      }    
      // Write the html into the document
      if (h.length) { document.writeln(h); }
    }
    return true;
  }
  return false;
}
// end x_load.js