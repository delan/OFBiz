// x_drag.js
// Drag events implementation.

// X v3.15.1, Cross-Browser DHTML Library from Cross-Browser.com
// Copyright (c) 2002,2003,2004 Michael Foster (mike@cross-browser.com)
// This library is distributed under the terms of the LGPL (gnu.org)

var xDragMgr = {ele:null, mm:false};

function xEnableDrag(id, fnDragStart, fnDrag, fnDragEnd)
{
  var ele = xGetElementById(id);
  ele.xDraggable = true;
  ele.xOnDragStart = fnDragStart;
  ele.xOnDrag = fnDrag;
  ele.xOnDragEnd = fnDragEnd;
  xAddEventListener(ele, 'mousedown', xdOnMousedown, false);
  if (!xDragMgr.mm) {
    xDragMgr.mm = true;
    xAddEventListener(document, 'mousemove', xdOnMousemove, false);
  }
}
function xdOnMousedown(e) // drag start
{
  var evt = new xEvent(e);
  var ele = evt.target;
  while(ele && !ele.xDraggable) {
    ele = xParent(ele);
  }
  if (ele) {
    if (e && e.preventDefault) e.preventDefault();
    else if (window.event) window.event.returnValue = false;
    ele.xDragX = evt.pageX;
    ele.xDragY = evt.pageY;
    xDragMgr.ele = ele;
    xAddEventListener(ele, 'mouseup', xdOnMouseup, false);
    if (ele.xOnDragStart) {
      ele.xOnDragStart(ele, evt.pageX, evt.pageY);
    }
  }
}
function xdOnMousemove(e) // drag
{
  var evt = new xEvent(e);
  if (xDragMgr.ele) {
    if (e && e.preventDefault) e.preventDefault();
    else if (window.event) window.event.returnValue = false;
    var ele = xDragMgr.ele;
    var dx = evt.pageX - ele.xDragX;
    var dy = evt.pageY - ele.xDragY;
    ele.xDragX = evt.pageX;
    ele.xDragY = evt.pageY;
    if (ele.xOnDrag) {
      ele.xOnDrag(ele, dx, dy);
    }
    else {
      xMoveTo(ele, xLeft(ele) + dx, xTop(ele) + dy);
    }
  }  
}
function xdOnMouseup(e) // drag end
{
  if (xDragMgr.ele) {
    if (e && e.preventDefault) e.preventDefault();
    else if (window.event) window.event.returnValue = false;
    xRemoveEventListener(xDragMgr.ele, 'mouseup', xdOnMouseup, false);
    if (xDragMgr.ele.xOnDragEnd) {
      var evt = new xEvent(e);
      xDragMgr.ele.xOnDragEnd(xDragMgr.ele, evt.pageX, evt.pageY);
    }
    xDragMgr.ele = null;
  }  
}
// end x_drag.js