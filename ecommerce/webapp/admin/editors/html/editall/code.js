
var command = "";

var fontname = "";
var fontsize = "";
var italic = false;
var underline = false;
var bold = false;
var unorderedlist = false;
var orderedlist = false;
var justify = 0; /* 1=left, 2=right, 3=center */
var cut = false;
var copy = false;
var paste = false;
var gotmousedown = false;
var firsttime = true;
var togglebuttons = new Array();


var gecko = false;
if (navigator.product == 'Gecko')
{
    gecko = true;
}

function InitToolbarButtons() {
  var kids = document.getElementsByTagName('DIV');

  for (var i=0; i < kids.length; i++) {
    if (kids[i].className == "imagebutton") {
      kids[i].onmouseover = tbmouseover;
      kids[i].onmouseout = tbmouseout;
      kids[i].onmousedown = tbmousedown;
      kids[i].onmouseup = tbmouseup;
      kids[i].onclick = tbclick;
    }
    if (kids[i].className == "imagebuttonClick") {
      togglebuttons[kids[i].id] = false;
      kids[i].onmouseover = tbmouseover;
      kids[i].onmouseout = tbmouseout;
      kids[i].onmousedown = tbmousedown;
      kids[i].onmouseup = tbmouseup;
      kids[i].onclick = tbclick;
    }
  }
}

function setButtons()
{
   var newfontname = getEditDocument().queryCommandValue("fontname");
   if (fontname != newfontname) {
     fontname = newfontname;
     var i;
     var numoptions = document.getElementById("fontname").length;
     for (i=0; i < numoptions; i++) {
       if ((document.getElementById("fontname").options[i].text) == newfontname) {
         document.getElementById("fontname").selectedIndex = i;
	 	 break;
       }
     }
     if (i == numoptions) {
       document.getElementById("fontname").options[numoptions] = new Option(newfontname, null);
       document.getElementById("fontname").selectedIndex = i;
     }
   }
   if (!gecko) { /* fontsize appears broken */
   var newfontsize = getEditDocument().queryCommandValue("fontsize");   
   if (fontsize != newfontsize) {
     fontsize = newfontsize;
     var i;
     for (i=0; i < document.getElementById("fontsize").length; i++) {
       if ((document.getElementById("fontsize").options[i].text) == newfontsize) {
         document.getElementById("fontsize").selectedIndex = i;
         break;
       }
     }
   }
   }
   var newbold = getEditDocument().queryCommandState("Bold");   
   if (bold != newbold) {
     bold = newbold;
     changeState( bold, document.getElementById("bold") );
   }
   var newitalic = getEditDocument().queryCommandState("Italic");   
   if (italic != newitalic) {
     italic = newitalic;
     changeState( italic, document.getElementById("italic") );
   }
   var newunderline = getEditDocument().queryCommandState("Underline");   
   if (underline != newunderline) {
     underline = newunderline;
     changeState( underline, document.getElementById("underline") );
   }
   
   var newunorderedlist = getEditDocument().queryCommandState("insertunorderedlist");
   if (unorderedlist != newunorderedlist) {
     unorderedlist = newunorderedlist;
     changeState( unorderedlist, document.getElementById("insertunorderedlist") );
   }
   var neworderedlist = getEditDocument().queryCommandState("insertorderedlist");
   if (orderedlist != neworderedlist) {
     orderedlist = neworderedlist;
     changeState( orderedlist, document.getElementById("insertorderedlist") );
   }

   var newjustify;
   var currjustify;
   currjustify = getEditDocument().queryCommandState("justifyleft");
   if (currjustify)
     newjustify = 1;
   currjustify = getEditDocument().queryCommandState("justifycenter");
   if (currjustify)
     newjustify = 2;
   currjustify = getEditDocument().queryCommandState("justifyright");
   if (currjustify)
     newjustify = 3;
   
   if (!newjustify)
     newjustify = 1;
   
   if (justify != newjustify) {
     justify = newjustify;
     changeState( false, document.getElementById("justifyright") );
     changeState( false, document.getElementById("justifycenter") );
     changeState( false, document.getElementById("justifyleft") );
     switch (newjustify) {
       case 1:
         changeState( true, document.getElementById("justifyleft") );
         break;
       case 2:
         changeState( true, document.getElementById("justifycenter") );
         break;
       case 3:
         changeState( true, document.getElementById("justifyright") );
         break;
     }
   }
   /*
   if (!gecko) {
   var newcut = getEditDocument().queryCommandEnabled("cut");   
   if (cut != newcut) {
     cut = newcut;
     if (cut)
       document.getElementById("cut").src = "cut.gif";
     else
       document.getElementById("cut").src = "cutdisabled.gif";
   }
   var newcopy = getEditDocument().queryCommandEnabled("copy");   
   if (copy != newcopy) {
     copy = newcopy;
     if (copy)
       document.getElementById("copy").src = "copy.gif";
     else
       document.getElementById("copy").src = "copydisabled.gif";
   }
   var newpaste = getEditDocument().queryCommandEnabled("paste");   
   if (paste != newpaste) {
     paste = newpaste;
     if (paste)
       document.getElementById("paste").src = "paste.gif";
     else
       document.getElementById("paste").src = "pastedisabled.gif";
   }
   }
   */
}

function UpdateTimer() {
 	setButtons();
    setTimeout("UpdateTimer()", 200);
}

function changeState( on, thediv )
{
	togglebuttons[thediv.id] = on;
	if ( on )
	{
		goDown( thediv );
	}	
	else
	{
		goUp( thediv );
	}
}

function goDown( thediv )
{
  thediv.firstChild.style.left = 1;
  thediv.firstChild.style.top = 1;
  thediv.className = "imagebuttonDown";
}

function goUp( thediv )
{
  if ( togglebuttons[thediv.id] )
  {
    thediv.className = "imagebuttonDown";
  }
  else
  {
    thediv.firstChild.style.left = 0;
    thediv.firstChild.style.top = 0;
    thediv.className = "imagebutton";
  }
}

function goHover( thediv )
{
  if ( togglebuttons[thediv.id] )
  {
    thediv.className = "imagebuttonDownHover";
  }
  else
  {
    thediv.firstChild.style.left = 0;
    thediv.firstChild.style.top = 0;
    thediv.className = "imagebuttonHover";
  }
}

function tbmousedown()
{
	goDown( this );
	setButtons();
}

function tbmouseup()
{
	goHover( this );
}

function tbmouseout()
{
	goUp( this );
	//setButtons(); //this does not work since it causes it to not repaint
}

function tbmouseover()
{
	goHover( this );
}

  function insertNodeAtSelection(win, insertNode)
  {
      // get current selection
      var sel = win.getSelection();

      // get the first range of the selection
      // (there's almost always only one range)
      var range = sel.getRangeAt(0);

      // deselect everything
      sel.removeAllRanges();

      // remove content of current selection from document
      range.deleteContents();

      // get location of current selection
      var container = range.startContainer;
      var pos = range.startOffset;

      // make a new range for the new selection
      range=document.createRange();

      if (container.nodeType==3 && insertNode.nodeType==3) {

        // if we insert text in a textnode, do optimized insertion
        container.insertData(pos, insertNode.nodeValue);

        // put cursor after inserted text
        range.setEnd(container, pos+insertNode.length);
        range.setStart(container, pos+insertNode.length);

      } else {


        var afterNode;
        if (container.nodeType==3) {

          // when inserting into a textnode
          // we create 2 new textnodes
          // and put the insertNode in between

          var textNode = container;
          container = textNode.parentNode;
          var text = textNode.nodeValue;

          // text before the split
          var textBefore = text.substr(0,pos);
          // text after the split
          var textAfter = text.substr(pos);

          var beforeNode = document.createTextNode(textBefore);
          var afterNode = document.createTextNode(textAfter);

          // insert the 3 new nodes before the old one
          container.insertBefore(afterNode, textNode);
          container.insertBefore(insertNode, afterNode);
          container.insertBefore(beforeNode, insertNode);

          // remove the old node
          container.removeChild(textNode);

        } else {

          // else simply insert the node
          afterNode = container.childNodes[pos];
          container.insertBefore(insertNode, afterNode);
        }

        range.setEnd(afterNode, 0);
        range.setStart(afterNode, 0);
      }

      sel.addRange(range);
  }

function selectColor( color )
{
	document.getElementById("colorpalette").style.visibility="hidden";
	checkRange();

	//this is required by IE
	getEditWindow().focus();

	//FIXME: for some reason we end up the the start of the document in IE
	getEditDocument().execCommand(this.command,false, color);
	
	getEditWindow().focus();
	
}

/**
 * Find the closest ancestor of anything in the current selection with the
 * given element name and/or attribute name.
 *
 * @param elementName    The name of the element to look for (may be null)
 * @param attributeName  The name of the attribute to look for (may be null)
 */
function findAncestorFromSelection( elementName, attributeName )
{
	var selection = checkRange();
	return findAncestor( elementName, attributeName, selection.commonAncestorContainer );
}

/**
 * Find the highest descendants of the current selection with the given element
 * name and/or attribute name.
 *
 * @param elementName    The name of the element to look for (may be null)
 * @param attributeName  The name of the attribute to look for (may be null)
 */
function findDescendantsFromSelection( elementName, attributeName )
{
	var selection = checkRange();
	var contents = selection.cloneContents(); // FIXME: Would extractContents() do as well?
	var elements = new Array();
	findDescendantsOfNode( contents, elementName, attributeName, elements );
	return elements;
}

/**
 * Find the highest descendants of the given node with the given element name
 * and/or attribute name.
 *
 * @param node           The node whose descendants to search
 * @param elementName    The name of the element to look for (may be null)
 * @param attributeName  The name of the attribute to look for (may be null)
 * @param nodeList       The array to which to append nodes
 */
function findDescendantsOfNode( node, elementName, attributeName, nodeList )
{
	for ( var i = 0; i < node.childNodes.length; i++ )
	{
		var child = node.childNodes.item( i );
		if ( child.nodeType == 1 ) // element
		{
			findDescendantsOfElement( child, elementName, attributeName, nodeList );
		}
	}
}

/**
 * Find the highest descendants of the given node with the given element name
 * and/or attribute name.  If the given element satisfies the criteria, it will
 * be added to the node list.
 *
 * @param element        The element to search, along with its descendants
 * @param elementName    The name of the element to look for (may be null)
 * @param attributeName  The name of the attribute to look for (may be null)
 * @param nodeList       The array to which to append nodes
 */
function findDescendantsOfElement( element, elementName, attributeName, nodeList )
{
	var found = true;
	if ( found && elementName && element.name == elementName )
	{
		found = false;
	}
	if ( found && attributeName && element.getAttribute( attributeName ) != null )
	{
		found = false;
	}
	
	if ( found )
	{
		nodeList[nodeList.length] = element;
	}
	else
	{
		findDescendantsOfNode( element, elementName, attributeName, nodeList );
	}
}

/**
 * Find the closest ancestor of the given element that has the given element
 * name and/or attribute name.  The element itself will be returned if it
 * matches the criteria.
 *
 * @param element        The element whose ancestors to traverse
 * @param elementName    The name of the element to look for (may be null)
 * @param attributeName  The name of the attribute to look for (may be null)
 */
function findAncestor( element, elementName, attributeName )
{
	var found = true;
	if ( found && elementName && element.name != elementName )
	{
		found = false;
	}
	if ( found && attributeName && element.getAttribute( attributeName ) == null )
	{
		found = false;
	}
	
	if ( found )
	{
		return element;
	}
	else
	{
		if ( element.parentNode )
		{
			return findAncestor( element.parentNode, elementName, attributeName );
		}
		else
		{
			return null;
		}
	}
}

function checkRange()
{
	
    if (getEditDocument().selection) 
   	{
   		if (getEditDocument().selection.type == "None") {
     		getEditDocument().selection.createRange();
   		}   		
   		var r = getEditDocument().selection.createRange();
   		return r;
   	}
   	else
   	{
   		return getEditDocument().createRange();
	}
}


function getOffsetTop(elm) {

  var mOffsetTop = elm.offsetTop;
  var mOffsetParent = elm.offsetParent;

  while(mOffsetParent){
    mOffsetTop += mOffsetParent.offsetTop;
    mOffsetParent = mOffsetParent.offsetParent;
  }
 
  return mOffsetTop;
}

function getOffsetLeft(elm) {

  var mOffsetLeft = elm.offsetLeft;
  var mOffsetParent = elm.offsetParent;

  while(mOffsetParent){
    mOffsetLeft += mOffsetParent.offsetLeft;
    mOffsetParent = mOffsetParent.offsetParent;
  }
 
  return mOffsetLeft;
}
function GetSelectionText(sel) {
  if (gecko)
    return sel.toString();
  else
    return sel.text;
}

function dumpProps( obj )
{
	var str = "";
	if ( obj )
	{
		for ( var prop in obj )
		{
			str += prop + " ";
		}
	}
	else
	{
		str = "(object is null)";
	}
	
	alert( str );
}

function tbclick()
{
  if ((this.id == "forecolor") || (this.id == "backcolor")) {
  	
  	checkRange();
    parent.command = this.id;
    buttonElement = document.getElementById(this.id);
    document.getElementById("colorpalette").style.left = getOffsetLeft(buttonElement);
    document.getElementById("colorpalette").style.top = getOffsetTop(buttonElement) + buttonElement.offsetHeight;
    document.getElementById("colorpalette").style.visibility="visible";
   } else if ( this.id == "save" ) {
   	saveAndClose();
  } else if (this.id == "createlink") {
  	
	var oSel = checkRange();
	dumpProps( oSel );
	var link ="";
	if ( !gecko )
	{
		// This doesn't work in Gecko; it uses all sorts of IE-specific
		// methods like duplicate(), parentElement(), etc.
		
		var itemcopy = oSel.duplicate();
		itemcopy.collapse(); //to limit the selection to where the actual cursor is

	    if ( itemcopy.parentElement() != null && itemcopy.parentElement().tagName == "A" ) //this if may not be needed
	    {
	    	link = itemcopy.parentElement().href;
	    }
	    if ( link == null )
	    {
	    	link = "";
	    }
	}
	var szURL = prompt("Enter a URL for " + GetSelectionText(oSel), link);
	getEditWindow().focus();
	getEditDocument().execCommand("CreateLink",false,szURL)	
  	
  } else if ( this.id == "showImagePicker" )  {
  		window.open( '/admin/editors/html/mozilla/imagepicker.html', 'picker','alwaysRaised=yes,menubar=no,scrollbars=yes,width=600,height=550,resizable=yes');

  } else if (this.id == "createtable") {
    e = document.getElementById("edit");
    rowstext = prompt("enter rows");
    colstext = prompt("enter cols");
    rows = parseInt(rowstext);
    cols = parseInt(colstext);
    if ((rows > 0) && (cols > 0)) {
      table = e.contentDocument.createElement("table");
      table.setAttribute("border", "1");
      table.setAttribute("cellpadding", "2");
      table.setAttribute("cellspacing", "2");
      tbody = e.contentDocument.createElement("tbody");
      for (var i=0; i<rows; i++) {
        tr =e.contentDocument.createElement("tr");
        for (var j=0; j<cols; j++) {
          td =e.contentDocument.createElement("td");
          br =e.contentDocument.createElement("br");
          td.appendChild(br);
          tr.appendChild(td);
        }
        tbody.appendChild(tr);
      }
      table.appendChild(tbody);      
      getEditWindow().focus();

      insertNodeAtSelection(e.contentWindow, table);
    }
  } else {
  	 getEditWindow().focus();

    getEditDocument().execCommand(this.id, false, null);
  }
}
function Select(selectname)
{
  var cursel = document.getElementById(selectname).selectedIndex;
  /* First one is always a label */
  if (cursel != 0) {
    var selected = document.getElementById(selectname).options[cursel].value;
    getEditDocument().execCommand(selectname, false, selected);
    document.getElementById(selectname).selectedIndex = 0;
  }
//  getEditWindow().focus();
}

function dismisscolorpalette()
{
  document.getElementById("colorpalette").style.visibility="hidden";
}

//http://www.scottandrew.com/printable/cbs-events_p.html
function addEvent(obj, evType, fn, useCapture){
  if (obj.addEventListener){
    obj.addEventListener(evType, fn, useCapture);
    return true;
  } else if (obj.attachEvent){
    var r = obj.attachEvent("on"+evType, fn);
    return r;
  } else {
    alert("Handler could not be attached");
  }
}

function getEditDocument() {

  if (gecko) // gecko
    return document.getElementById("edit").contentDocument;
  else
    return frames.edit.document;  
}
function getEditWindow() {

  if (gecko) // gecko
  {
    return document.getElementById('edit').contentWindow;
  }
  else
  {  	
    return document.edit;  
  }
}

function viewsource(source){
  if (source) {
    var html = getEditDocument().createTextNode( getEditDocument().body.innerHTML);
    getEditDocument().body.innerHTML = "";
    
    getEditDocument().body.appendChild(html );
    
    document.getElementById("toolbar1").style.visibility="hidden";
    document.getElementById("toolbar2").style.visibility="hidden";
    
  } else {
    var bodyText;
    
	if (getEditDocument().body.createTextRange) 
	{
		//IE
     	bodyText = getEditDocument().body.createTextRange().text;
    }
    else if (getEditDocument().createRange) 
    {
     	var range = getEditDocument().createRange();
     	range.selectNodeContents(getEditDocument().body);
     	bodyText = range.toString();
    }    
        
    getEditDocument().body.innerHTML = bodyText;
    document.getElementById("toolbar1").style.visibility="visible";
    document.getElementById("toolbar2").style.visibility="visible";
    
  }
}
/*
http://www.faqts.com/knowledge_base/view.phtml/aid/7786

function dokeypress()
{
     tr = doc.selection.createRange();
     event = doc.parentWindow.event;
     if (event.keyCode == 13)
     {
          if (tr.htmlText.length == 0)
          {
               tr.moveEnd('character',1);
               tr.pasteHTML('<br>'+tr.htmlText);
               tr = doc.selection.createRange();
               tr.moveEnd('character',-1);
               tr.select();
          }
          else
          {
               tr.pasteHTML('<br>');
          };
          event.keyCode = 0;
     };
};
doc.onkeypress = dokeypress;
*/