// x_win.js
// X v3.15, Cross-Browser DHTML Library from Cross-Browser.com
// Copyright (c) 2002,2003,2004 Michael Foster (mike@cross-browser.com)
// This library is distributed under the terms of the LGPL (gnu.org)

/* xWindow()
   Create an xWindow object for each child window the page will need.
   Pass a zero for width, height, left, and top and the window will
   have default size and position. Pass a zero or one for the boolean
   parameters (location field, menubar, etc.).
*/
function xWindow(name, w, h, x, y, loc, men, res, scr, sta, too)
{
  var f = '';
  if (w && h) {
    if (document.layers) f = 'screenX=' + x + ',screenY=' + y;
    else f = 'left=' + x + ',top=' + y;
    f += ',width=' + w + ',height=' + h + ',';
  }
  f += ('location='+loc+',menubar='+men+',resizable='+res
    +',scrollbars='+scr+',status='+sta+',toolbar='+too);
  this.features = f;
  this.name = name;
  this.load = function(sUrl) {
    if (this.wnd && !this.wnd.closed) this.wnd.location.href = sUrl;
    else this.wnd = window.open(sUrl, this.name, this.features);
    this.wnd.focus();
    return false;
  }
}
// end x_win.js