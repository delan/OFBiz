// x_img.js
// Image rollover functions.

// X v3.15, Cross-Browser DHTML Library from Cross-Browser.com
// Copyright (c) 2002,2003,2004 Michael Foster (mike@cross-browser.com)
// This library is distributed under the terms of the LGPL (gnu.org)

/* xImgRollSetup
   Can not be called before the window onload event.
   Pass image IDs starting with 4th argument.
   Assumes this image file naming convention:
     out img = path + imgEleId + fileExt
     over img = path + imgEleId + ovrSuffix + fileExt
*/  

function xImgRollSetup(path, ovrSuffix, fileExt) 
{
  var ele, id;
  for (var i=3; i<arguments.length; ++i) {
    id = arguments[i];
    if (ele = xGetElementById(id)) {
      ele.xOutUrl = path + id + fileExt;
      ele.xOvrObj = new Image();
      ele.xOvrObj.src = path + id + ovrSuffix + fileExt;
      ele.onmouseout = xImgOnMouseout;
      ele.onmouseover = xImgOnMouseover;
    }
  }
}  

function xImgOnMouseout(e)
{
  if (this.xOutUrl) {
    this.src = this.xOutUrl;
  }
}

function xImgOnMouseover(e)
{
  if (this.xOvrObj && this.xOvrObj.complete) {
    this.src = this.xOvrObj.src;
  }
}
// end x_img.js