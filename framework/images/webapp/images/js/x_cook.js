// x_cook.js
// Cookie functions.

// X v3.15, Cross-Browser DHTML Library from Cross-Browser.com
// Copyright (c) 2002,2003,2004 Michael Foster (mike@cross-browser.com)
// This library is distributed under the terms of the LGPL (gnu.org)

// cookie implementations based on code from Netscape Javascript Guide
function xSetCookie(name, value, expire, path) {
  document.cookie = name + "=" + escape(value) + ((!expire) ? "" : ("; expires=" + expire.toGMTString())) + "; path=/";
}
function xGetCookie(name) {
  var value=null, search=name+"=";
  if (document.cookie.length > 0) {
    var offset = document.cookie.indexOf(search);
    if (offset != -1) {
      offset += search.length;
      var end = document.cookie.indexOf(";", offset);
      if (end == -1) end = document.cookie.length;
      value = unescape(document.cookie.substring(offset, end));
    }
  }
  return value;
}
// end x_cook.js