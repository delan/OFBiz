// Collapsible Element Support
// Mike Foster, Cross-Browser.com

document.write("<style type='text/css'>#header h1, #leftColumn h3{cursor:pointer;}</style>");

function setupCollapsibleH3s(bMin)
{
  setupCollapsibleHs('H3', H3_OnClick, bMin);
}

function setupCollapsibleColumn()
{
  var h, i, c, list, lc;
  h = xGetElementById('header');
  h.collapsed = true;
  h.onclick = H1_OnClick;
  h.onclick();
  return;
}

function setupCollapsibleHs(sTag, fnOnClick, bMin)
{
  var h, i, c, list, lc;
  lc = xGetElementById('leftColumn');
  aH = xGetElementsByTagName(sTag, lc); // from leftColumn only
  aC = xGetElementsByClassName('collapsible', lc);
  for (i = 0; i < aH.length; ++i) {
    h = aH[i]; // sTag element (trigger)
    c = aC[i]; // collapsible element (target)
    if (h && c) {
      h.collapsibleEle = c;
      h.collapsed = !bMin;
      h.onclick = fnOnClick;
      h.onclick();
    }
  }
}

function H3_OnClick()
{
  var d, t;
  if (this.collapsed) {
    d = 'block';
    t = 'Hide';
  }
  else {
    d = 'none';
    t = 'Show';
  }
  this.collapsibleEle.style.display = d;
  this.setAttribute('title', 'Click to ' + t + ' Section');
  this.collapsed = !this.collapsed;
}

function H1_OnClick()
{
  var d, t, w;
  var lc = xGetElementById('leftColumn');
  var rc = xGetElementById('rightColumn');
  if (this.collapsed) {
    d = 'block';
    t = 'Hide';
    w = '70';
  }
  else {
    d = 'none';
    t = 'Show';
    w = '96';
  }
  rc.style.display = d;
  lc.style.width = w + '%';
  this.setAttribute('title', 'Click to ' + t + ' Side Panel');
  this.collapsed = !this.collapsed;
}
