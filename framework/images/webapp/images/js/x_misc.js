// x_misc.js
// X v3.15, Cross-Browser DHTML Library from Cross-Browser.com
// Copyright (c) 2002,2003,2004 Michael Foster (mike@cross-browser.com)
// This library is distributed under the terms of the LGPL (gnu.org)

function xLinearScale(value, inMin, inMax, outMin, outMax)
{
  var m = (outMax - outMin) / (inMax - inMin);
  var b = outMin - (inMin * m);
  return (m * value + b);
}

function xIntersection(e1, e2, o)
{
  var ix1, iy2, iw, ih, intersect = true;
  var e1x1 = e1.pageX();
  var e1x2 = e1x1 + e1.width();
  var e1y1 = e1.pageY();
  var e1y2 = e1y1 + e1.height();
  var e2x1 = e2.pageX();
  var e2x2 = e2x1 + e2.width();
  var e2y1 = e2.pageY();
  var e2y2 = e2y1 + e2.height();
  // horizontal
  if (e1x1 <= e2x1) {
    ix1 = e2x1;
    if (e1x2 < e2x1) intersect = false;
    else iw = Math.min(e1x2, e2x2) - e2x1;
  }
  else {
    ix1 = e1x1;
    if (e2x2 < e1x1) intersect = false;
    else iw = Math.min(e1x2, e2x2) - e1x1;
  }
  // vertical
  if (e1y2 >= e2y2) {
    iy2 = e2y2;
    if (e1y1 > e2y2) intersect = false;
    else ih = e2y2 - Math.max(e1y1, e2y1);
  }
  else {
    iy2 = e1y2;
    if (e2y1 > e1y2) intersect = false;
    else ih = e1y2 - Math.max(e1y1, e2y1);
  }
  // return intersected rectangle
  if (intersect && arguments.length == 3) {
    o.x = ix1;
    o.y = iy2 - ih;
    o.w = iw;
    o.h = ih;
  }
  return intersect;
}

// end x_misc.js