// x_tip.js
// X v3.15.1, Cross-Browser DHTML Library from Cross-Browser.com
// Copyright (c) 2002,2003,2004 Michael Foster (mike@cross-browser.com)
// This library is distributed under the terms of the LGPL (gnu.org)

// X Tooltip Groups
// v2.01, mf, 1Mar04,  Now works in IE 4 and 5. Used document.write
//                     instead of document.createElement.
// v2.00, mf, 13Dec03, Almost completely rewritten. Now supports html for
//                     tooltip text instead of using the title attribute.
// v1.01, mf, 12Dec03, Correction for when the mouse moves
//                     directly from one trigger element to another. 
// v1.00, mf, 11Dec03, Initial release.

document.write("<style type='text/css'>#xTooltipElement{position:absolute;visibility:hidden;}</style>");
document.write("<div id='xTooltipElement'>xTooltipElement</div>");

var xttTrigger = null; // current trigger element

function xTooltipGroup(grpClassOrIdList, tipClass, origin, xOffset, yOffset, textList)
{
  //// Public Methods

  this.show = function(trigEle, mx, my)
  {
    if (xttTrigger != trigEle) { // if not active or moved to an adjacent trigger
      this.t.className = trigEle.xTooltip.c;
      this.t.innerHTML = trigEle.xTooltipText ? trigEle.xTooltipText : trigEle.title;
      xttTrigger = trigEle;
    }  
    var x, y;
    switch(this.o) {
      case 'right':
        x = xPageX(trigEle) + xWidth(trigEle);
        y = xPageY(trigEle);
        break;
      case 'top':
        x = xPageX(trigEle);
        y = xPageY(trigEle) - xHeight(trigEle);
        break;
      case 'mouse':
        x = mx;
        y = my;
        break;
    }
    xMoveTo(this.t, x + this.x, y + this.y);
    xShow(this.t);
  }

  this.hide = function()
  {
    xMoveTo(this.t, -1000, -1000);
    xttTrigger = null;
  }

  //// Private Methods

  this.docOnMousemove = function(oEvent)
  {
    // this == document at runtime
    var o, e = new xEvent(oEvent);
    if (e.target && (o = e.target.xTooltip)) {
      o.show(e.target, e.pageX, e.pageY);
    }
    else if (xttTrigger) {
      xttTrigger.xTooltip.hide();
    }
  }

  //// Private Properties

  this.c = tipClass;
  this.o = origin;
  this.x = xOffset;
  this.y = yOffset;
  this.t = null; // tooltip element - all groups use the same element

  //// Constructor Code

  var i, tips;
  if (xStr(grpClassOrIdList)) {
    tips = xGetElementsByClassName(grpClassOrIdList);
    for (i = 0; i < tips.length; ++i) {
      tips[i].xTooltip = this;
    }
  }
  else {
    tips = new Array();
    for (i = 0; i < grpClassOrIdList.length; ++i) {
      tips[i] = xGetElementById(grpClassOrIdList[i]);
      if (!tips[i]) {
        alert('Element not found for id = ' + grpClassOrIdList[i]);
      }  
      else {
        tips[i].xTooltip = this;
        tips[i].xTooltipText = textList[i];
      }
    }
  }
  if (!this.t) { // only execute once
    this.t = xGetElementById('xTooltipElement');
    xAddEventListener(document, 'mousemove', this.docOnMousemove, false);
  }
} // end xTooltipGroup

// end x_tip.js
