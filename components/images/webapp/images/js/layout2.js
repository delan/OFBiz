// layout2.js
// Cross-Browser.com & SitePoint.com - Equal Column Height Demo

if (document.getElementById || document.all) { // minimum dhtml support required
  document.write("<"+"script type='text/javascript' src='/images/js/x_core.js'><"+"/script>");
  document.write("<"+"script type='text/javascript' src='/images/js/x_event.js'><"+"/script>");
  document.write("<"+"style type='text/css'>#footer{visibility:hidden;}<"+"/style>");
  window.onload = winOnLoad;
}
function winOnLoad()
{
  var ele = xGetElementById('centerColumnLR');
  var addHook = false;
  if (ele && xDef(ele.style, ele.offsetHeight)) { // another compatibility check
    addHook = true;
  } else {
    ele = xGetElementById('centerColumnL');
    if (ele && xDef(ele.style, ele.offsetHeight)) {
      addHook = true;
    } else {
      ele = xGetElementById('centerColumnR');
      if (ele && xDef(ele.style, ele.offsetHeight)) {
        addHook = true;
      } else {
        ele = xGetElementById('centerColumn');
        if (ele && xDef(ele.style, ele.offsetHeight)) {
          addHook = true;
        }
      }
    }
  }
  if ( addHook == true ) {
    adjustLayout();
    xAddEventListener(window, 'resize', winOnResize, false);
  }
}
function winOnResize()
{
  adjustLayout();
}
function adjustLayout()
{
  // Get content heights
  var cColumn = 'centerColumnLR';
  var cHeight = xHeight('centerColumnContentLR');
  if ( cHeight == 0 ) {
    cHeight = xHeight('centerColumnContentL');
    if ( cHeight == 0 ) {
      cHeight = xHeight('centerColumnContent');
      if ( cHeight == 0 ) {
        cHeight = xHeight('centerColumnContentR');
        cColumn = 'centerColumnR';
      } else {
        cColumn = 'centerColumn';
      }
    } else {
      cColumn = 'centerColumnL';
    }
  }
  var lHeight = xHeight('leftColumnContent');
  var rHeight = xHeight('rightColumnContent');

  // Find the maximum height
  var maxHeight = Math.max(cHeight, Math.max(lHeight, rHeight));

  // Assign maximum height to all columns
  xHeight('leftColumn', maxHeight);
  xHeight(cColumn, maxHeight);
  xHeight('rightColumn', maxHeight);

  // Show the footer
  xShow('footer');
}
