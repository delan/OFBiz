// x_dom.js
// X v3.15.1, Cross-Browser DHTML Library from Cross-Browser.com
// Copyright (c) 2002,2003,2004 Michael Foster (mike@cross-browser.com)
// This library is distributed under the terms of the LGPL (gnu.org)

/* xWalkTree()
   Perform a preorder traversal
   on the subtree starting at oNode
   and pass each Element node to fnVisit.
*/
function xWalkTree(oNode, fnVisit)
{
  if (oNode) {
    if (oNode.nodeType == 1) {fnVisit(oNode);}
    for (var c = oNode.firstChild; c; c = c.nextSibling) {
      xWalkTree(c, fnVisit);
    }
  }
}

/* xGetComputedStyle
   For sProp use the css property name, not the object property name.
   For finding width this works in Moz and Op, but in IE this only works
   if padding and border use pixel units in the CSS.
*/
function xGetComputedStyle(oEle, sProp)
{
  var p = 0;
  if(document.defaultView && document.defaultView.getComputedStyle){
    p = document.defaultView.getComputedStyle(oEle,'').getPropertyValue(sProp)
  }
  else if(oEle.currentStyle) {
    // convert css property name to object property name for IE (can this be done with RE?)
    var a = sProp.split('-');
    sProp = a[0];
    for (var i=1; i<a.length; ++i) {
      c = a[i].charAt(0);
      sProp += a[i].replace(c, c.toUpperCase());
    }   
    p = oEle.currentStyle[sProp];
  }
  return parseInt(p) || 0;
}

/* xGetElementsByClassName()
   Returns an array of elements which are
   descendants of parentEle and have tagName and clsName.
   If parentEle is null or not present, document will be used.
   if tagName is null or not present, "*" will be used.
*/
function xGetElementsByClassName(clsName, parentEle, tagName) {
	var found = new Array();
	var re = new RegExp('\\b'+clsName+'\\b', 'i');
  var list = xGetElementsByTagName(tagName, parentEle);
	for (var i = 0; i < list.length; ++i) {
		if (list[i].className.search(re) != -1) {
			found[found.length] = list[i];
		}
	}
	return found;
}

/* xGetElementsByTagName()
   Returns an array of elements which are
   descendants of parentEle and have tagName.
   If parentEle is null or not present, document will be used.
   if tagName is null or not present, "*" will be used.
*/
function xGetElementsByTagName(tagName, parentEle)
{
	var list = null;
  tagName = tagName || '*';
  parentEle = parentEle || document;
  if (xIE4 || xIE5) {
    if (tagName == '*') list = parentEle.all;
    else list = parentEle.all.tags(tagName);
  }
  else if (parentEle.getElementsByTagName) list = parentEle.getElementsByTagName(tagName);
  return list || new Array();
}

/* xGetElementsByAttribute
   Return an array of all sTag elements whose sAtt attribute matches sRE.
   sAtt can also be a property name but the property must be of type string.
*/
function xGetElementsByAttribute(sTag, sAtt, sRE)
{
  var a, list, found = new Array(), re = new RegExp(sRE, 'i');
  list = xGetElementsByTagName(sTag);
  for (var i = 0; i < list.length; ++i) {
    a = list[i].getAttribute(sAtt);
    if (!a) {a = list[i][sAtt];}
    if (typeof(a)=='string' && a.search(re) != -1) {
      found[found.length] = list[i];
    }
  }
  return found;
}

// end x_dom.js
