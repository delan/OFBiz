/**************************************************************************
	
	Copyright (c) 2003 eInnovation, Inc.  All rights reserved.

	Modified February 9, 2002 by Eric Galluzzo (egalluzzo@einnovation.com)
	to add non-numeric IDs, cookie settings, and server-side expansion
	
	Modified February 19, 2002 by Eric Galluzzo (egalluzzo@einnovation.com)
	to prevent wrapping and add selected item highlighting
	
	Modified February 21, 2002 by Eric Galluzzo (egalluzzo@einnovation.com)
	to add user icons
	
	Rewritten February 25-26, 2002 by Eric Galluzzo
	(egalluzzo@einnovation.com) to improve efficiency and make
	object-oriented

	Modified March 4, 2002 by Eric Galluzzo (egalluzzo@einnovation.com)
	to persist the selected node in the cookie
	
	Modified January 6, 2003 by Eric Galluzzo (egalluzzo@einnovation.com)
	to add tree names, and to make cookies use the name rather than the ID
	
***************************************************************************
	
	This code is based on code with the following copyright statement:
	
	Copyright (c) 2001 Geir Landro (drop@destroydrop.com)
	JavaScript Tree - www.destroydrop.com/hugi/javascript/tree/
	Version 0.96
	
	This script can be used freely as long as all copyright messages are
	intact.
	
**************************************************************************/

// Global array of trees

var trees = new Array();
var nextTreeID = 1;

//////////////////////////////////////////////////////////////////////////////
// Class Node
//

/**
 * This class represents a single node within a tree.  It can be constructed
 * in a "chained" fashion such as:
 * <pre>
 *   new Node( "Grandparent", "grandpa.html", "page.gif", null, new Array(
 *       new Node( "Parent 1", "parent1.html", "page.gif", null, new Array(
 *           new Node( "Child 1", "child1.html", "page.gif" ),
 *           new Node( "Child 2", "child2.html", "page.gif" )
 *       ),
 *       new Node( "Parent 2", "parent2.html", "page.gif", "server-expand?nodeID=1.1.2" )
 *   );
 * </pre>
 *
 * @param text             The link text to display when this node is drawn
 * @param linkURL          The URL to which to go when this node is selected
 *                         (optional)
 * @param imageURL         The URL of the image to display to the left of this
 *                         node when it is drawn (optional)
 * @param serverExpandURL  The URL to replace the tree page when a server-side
 *                         expand is requested (optional).  Ideally, this
 *                         URL returns a tree that looks just like the old
 *                         tree page except that it has an extra level of
 *                         nodes.
 * @param children         An array of children of this node (optional).  A
 *                         node should have either a server expand URL or
 *                         children, not both.
 */
function Node( text, linkURL, imageURL, path, serverExpandURL, children )
{
	// Configurable parameters
	
	this.text = text ? text : "";
	this.linkURL = linkURL ? linkURL : null;
	this.path = path ? path : null;
	this.imageURL = imageURL ? imageURL : null;
	this.serverExpandURL = serverExpandURL ? serverExpandURL : null;
	this.children = children ? children : null;
	
	// Internal variables
	
	this.nodeID = null; // this will be set by the tree
	this.open = false;
	this.parent = null; // this will be set while constructing the parent
	if ( children )
	{
		for ( var i = 0; i < children.length; i++ )
		{
			this.children[i].parent = this;
		}
	}
	
	// Methods
	
	this.isLastSibling = Node_isLastSibling;
}

/**
 * Determine whether or not this node is the last sibling within its parent.
 *
 * @return <code>true</code> if the node has no parent or it is the last
 *         sibling within its parent, or <code>false</code> if it has a parent
 *         and it is not the last sibling within the parent
 */
function Node_isLastSibling()
{
	// If this node has a parent, the parent has children (it should!),
	// and the last child of the parent is this node, then this is the last
	// sibling.
	
	return ( !this.parent ||
		( this.parent.children &&
			( this.parent.children.length > 0 ) &&
			( this.parent.children[this.parent.children.length - 1] == this ) ) );
}


//////////////////////////////////////////////////////////////////////////////
// Class Tree
//

/**
 * This class represents a visual tree widget similar to those found in
 * Windows Explorer, Nautilus, Konqueror, and so forth.  Note that there are
 * some configurable parameters:
 * <ul>
 *   <li><code>targetFrame</code>: the target frame in which all node links
 *     should be directed
 *   <li><code>selectedColor</code>: the foreground color of a selected node
 *   <li><code>selectedBackgroundColor</code>: the background color of a
 *     selected node
 *   <li><code>name</code>: the tree's name, used for persisting open/closed
 *     state of nodes in cookies (i.e. must be unique)
 * </ul>
 *
 * @param rootNode  The root Node of this tree
 * @param name      (optional) The name of this tree.  If not specified, a name
 *                  will be automatically generated for the tree
 */
function Tree( rootNode, name )
{
	// Internal variables
	
	this.document = null;
	this.icons = new Array();
	this.nodes = new Array();
	this.oldPageXOffset = 0;
	this.oldPageYOffset = 0;
	this.root = rootNode;
	this.selectedNode = null;
	this.treeID = nextTreeID++;
	
	// Configurable parameters
	
    this.targetFrame = "edit";
    this.selectedColor = "#ffffff";
    this.selectedBackgroundColor = "#00007f";
    this.name = name ? name : ( "tree" + this.treeID );
	
	// Public methods (more or less)
	
	this.draw = Tree_draw;
	this.selectNode = Tree_selectNode;
	this.toggleNode = Tree_toggleNode;
	
	// Private methods
	
	this.addNode = Tree_addNode;
	this.drawInternal = Tree_drawInternal;
	this.getCookie = Tree_getCookie;
	this.getOpenNodeIDs = Tree_getOpenNodeIDs;
	this.getOpenNodeIDsInternal = Tree_getOpenNodeIDsInternal;
	this.readState = Tree_readState;
	this.preloadIcons = Tree_preloadIcons;
	this.storeState = Tree_storeState;
	
	// Initialization
	
	trees[this.treeID] = this;
	
	this.preloadIcons();
	this.addNode( rootNode, "1" );
}

/**
 * Add the given node and all its children to this tree.
 *
 * @param node    The node to add
 * @param nodeID  The node ID to assign to the given node
 */
function Tree_addNode( node, nodeID )
{
	node.nodeID = nodeID;
	this.nodes[nodeID] = node;
	if ( node.children )
	{
		for ( var i = 0; i < node.children.length; i++ )
		{
			this.addNode( node.children[i], nodeID + "." + ( i + 1 ) );
		}
	}
}

/**
 * Draw this tree on the given document.
 *
 * @param doc  The document on which to draw
 */
function Tree_draw( doc )
{
	this.document = doc ? doc : document;
	this.readState();
	this.drawInternal( this.root, new Array() );
	self.scrollTo( this.oldPageXOffset, this.oldPageYOffset );
}

/**
 * Draw a node of this tree and all its children.
 *
 * @param node         The node to draw
 * @param parentLines  A stack (Array) of booleans in order from root node to
 *                     the parent of this node; each boolean determines
 *                     whether or not to draw a line vertically from that
 *                     parent through the space to the left of this node's
 *                     text
 */
function Tree_drawInternal( node, parentLines )
{
	var doc = this.document;
	var lastSibling = node.isLastSibling();

	doc.write( "<div id=\"node" + this.treeID + "-" + node.nodeID +
		"\" nowrap=\"nowrap\">" );

	if ( node.parent )
	{
		// Write out the line & empty icons.

		for ( var i = 0; i < parentLines.length; i++ )
		{
			var imageName = null;
			doc.write(
				"<img src=\"/admin/images/tree/" +
				( parentLines[i] ? "line" : "empty" ) +
				".gif\" align=\"absbottom\" alt=\"\" />" );
		}

		// Push a line or empty icon into the parentLines stack for recursive
		// calls to this method.

		parentLines.push( !lastSibling );

		// Write out the expand/join icons, unless this is the root node.

		if ( node.children || node.serverExpandURL )
		{
			doc.write(
				"<a href=\"javascript:trees['" + this.treeID +
				"'].toggleNode('" + node.nodeID + "');\"><img id=\"join" +
				this.treeID + "-" + node.nodeID + "\" src=\"/admin/images/tree/" +
				( node.open ? "minus" : "plus" ) +
				( lastSibling ? "bottom" : "" ) +
				".gif\" align=\"absbottom\" alt=\"Open/Close node\" /></a>" );
		}
		else
		{
			doc.write(
				"<img src=\"/admin/images/tree/join" +
				( lastSibling ? "bottom" : "" ) +
				".gif\" align=\"absbottom\" alt=\"\" />" );
		}
	}

	// Start the link.

	if ( node.linkURL )
	{
		doc.write(
			"<a href=\"javascript:trees['" + this.treeID + "'].selectNode('" +
			node.nodeID + "');\" onmouseover=\"window.status='" + node.linkURL +
			"';return true;\" onmouseout=\"window.status=' ';return true;\">" );
	}

	// Write out the icon.
	// FIXME: Should we have customizable ALT text?

	if ( node.imageURL )
	{
		doc.write(
			"<img src=\"" + node.imageURL +
			"\" align=\"absbottom\" alt=\"\" />" );
	}

	// Write out the node name.

	doc.write( " <span id=\"text" + this.treeID + "-" + node.nodeID + "\"" );
	if ( node == this.selectedNode )
	{
		doc.write(
			" style=\"color: " + this.selectedColor +
			"; background-color: " + this.selectedBackgroundColor + "\"" );
	}
	doc.write( ">" + node.text + "</span>" );

	// End the link.

	if ( node.linkURL )
	{
		doc.write( "</a>" );
	}

	// End the line.

	doc.write( "</div>" );

	// If this node has children, write out a <div> surrounding the children
	// and recurse.

	if ( node.children )
	{
		doc.write( "<div id=\"div" + this.treeID + "-" + node.nodeID +
			"\"" );
		if ( !node.open )
		{
			doc.write( " style=\"display: none;\"" );
		}
		doc.write( ">" );
		for ( var i = 0; i < node.children.length; i++ )
		{
			this.drawInternal( node.children[i], parentLines );
		}
		doc.write( "</div>" );
	}

	// Remove the last line or empty icon from the stack.

	parentLines.pop();
}

/**
 * Get the cookie in this tree's document with the given name.
 *
 * @param cookieName  The name of the cookie to retrieve
 *
 * @return  The value of the given cookie, or <code>null</code> if no such
 *          cookie could be found
 */
function Tree_getCookie( cookieName )
{
	var doc = this.document;
	var label = cookieName + "=";
	var beginCookiePos = doc.cookie.indexOf( label );
	if ( beginCookiePos >= 0 )
	{
		beginCookiePos += label.length;
		endCookiePos = doc.cookie.indexOf( ";", beginCookiePos );
		if ( endCookiePos < 0 )
		{
			endCookiePos = doc.cookie.length;
		}
		return unescape( doc.cookie.substring( beginCookiePos, endCookiePos ) );
	}
	else
	{
		return null;
	}
}

/**
 * Build a list of the IDs of all the open nodes.
 *
 * @return  The list of open node IDs
 */
function Tree_getOpenNodeIDs()
{
	var openNodeIDs = new Array();
	this.getOpenNodeIDsInternal( this.root, openNodeIDs );
	return openNodeIDs;
}

/**
 * Store the open node IDs of all the open nodes from the given node on down
 * in the given array.
 *
 * @param node         The node to traverse
 * @param openNodeIDs  The list (Array) of open nodes
 */
function Tree_getOpenNodeIDsInternal( node, openNodeIDs )
{
	if ( node.open )
	{
		openNodeIDs.push( node.nodeID );
	}
	if ( node.children )
	{
		for ( var i = 0; i < node.children.length; i++ )
		{
			this.getOpenNodeIDsInternal( node.children[i], openNodeIDs );
		}
	}
}

/**
 * Pre-load all the "built-in" images in the tree so that we don't get ugly
 * loading effects when we're drawing it.
 */
function Tree_preloadIcons()
{
	var iconNames = new Array( "plus", "plusbottom", "minus", "minusbottom" );
	this.icons = new Array( iconNames.length );
	for ( var i = 0; i < iconNames.length; i++ )
	{
		this.icons[i] = new Image();
		this.icons[i].src = "/admin/images/tree/" + iconNames[i] + ".gif";
	}
}

/**
 * Read the tree's document's cookie to determine the open nodes, and set the
 * nodes to open.  The cookie is a list of node IDs separated by | characters
 * along with the selected node and page offset:
 *
 * <pre>tree1-openNodes=1|1.2|1.2.1|1.2.2|1.4|1.5; tree1-selectedNode=1.2; tree1-pageOffset=31,927</pre>
 */
function Tree_readState()
{
	var label = this.name + "-openNodes";
	var cookie = this.getCookie( label );
	if ( cookie )
	{
		var openNodes = cookie.split( "|" );
		
		for ( var i = 0; i < openNodes.length; i++ )
		{
			if ( this.nodes[openNodes[i]] )
			{
				this.nodes[openNodes[i]].open = true;
			}
		}
	}
	
	// The root node should _always_ be open, because there's no expander on
	// it.
	
	this.root.open = true;
	
	// Get the selected node out of the cookie.
	
	label = this.name + "-selectedNode";
	cookie = this.getCookie( label );
	if ( cookie )
	{
		this.selectedNode = this.nodes[cookie];
	}
	
	label = this.name + "-pageOffset";
	cookie = this.getCookie( label );
	if ( cookie )
	{
		var pageOffsets = cookie.split( "," );
		this.oldPageXOffset = pageOffsets[0];
		this.oldPageYOffset = pageOffsets[1];
	}
}

/**
 * Select the given node, by altering its color to the selected color and
 * replacing the target frame with the link URL.
 *
 * @param nodeID  The ID of the node to select
 */
function Tree_selectNode( nodeID )
{
	var node = this.nodes[nodeID];
	
	// Unselect the old node.
	
	if ( this.selectedNode != null )
	{
		var selectedDOMNode = this.document.getElementById(
			"text" + this.treeID + "-" + this.selectedNode.nodeID );
		selectedDOMNode.style.color = "";
		selectedDOMNode.style.backgroundColor = "";
	}
	
	// Select the new node.
	
	if ( nodeID )
	{
		var newSelectedDOMNode = this.document.getElementById(
			"text" + this.treeID + "-" + node.nodeID );
		newSelectedDOMNode.style.color = this.selectedColor;
		newSelectedDOMNode.style.backgroundColor = this.selectedBackgroundColor;
	}
	this.selectedNode = node;
	this.storeState();
	
	// Follow the link.
	
	if ( nodeID )
	{
		if ( this.targetFrame )
		{
			parent.frames[this.targetFrame].location.href = node.linkURL;
		}
		else
		{
			document.location.href = node.linkURL;
		}
	}
}

/**
 * Store the node IDs of all the open nodes in the tree and the currently
 * selected node in a cookie on the tree's document, for this session only.
 */
function Tree_storeState()
{
	document.cookie = this.name + "-openNodes=" +
		this.getOpenNodeIDs().join( "|" ) + "; path=/";
	if ( this.selectedNode )
	{
		// Believe it or not, this is how you set two cookies from the same
		// document.  Strange but true.
		
		document.cookie = this.name + "-selectedNode=" +
			this.selectedNode.nodeID + "; path=/";
	}
	
	// Determine where in the document the user has scrolled to.
	
	var pageXOffset = 0;
	var pageYOffset = 0;
	if ( document.layers )
	{
		// Netscape
		pageXOffset = window.pageXOffset;
		pageYOffset = window.pageYOffset;
	}
	else if ( document.all )
	{
		// IE
		pageXOffset = document.body.scrollLeft;
		pageYOffset = document.body.scrollTop;
	}
	
	// Write a cookie with the page offsets.
	
	document.cookie = this.name + "-pageOffset=" +
		pageXOffset + "," + pageYOffset + "; path=/";
}

/**
 * Toggle the given node's expansion state.  If a node has children, it will
 * be expanded or collapsed on the client; otherwise, if it has a server-side
 * expansion link, that link will be followed.  Note that this tree
 * <em>must</em> be drawn upon a document in order for this method to work.
 *
 * @param nodeID  The ID of the node to toggle
 */
function Tree_toggleNode( nodeID )
{
	var node = this.nodes[nodeID];
	
	if ( node.children )
	{
		var theDiv = this.document.getElementById(
			"div" + this.treeID + "-" + node.nodeID );
		var theJoin = this.document.getElementById(
			"join" + this.treeID + "-" + node.nodeID );
		
		if ( theDiv.style.display == 'none' )
		{
			theJoin.src = ( node.isLastSibling() ? this.icons[3].src :
				this.icons[2].src );
			theDiv.style.display = '';
			node.open = true;
		}
		else
		{
			theJoin.src = ( node.isLastSibling() ? this.icons[1].src :
				this.icons[0].src );
			theDiv.style.display = 'none';
			node.open = false;
		}
		this.storeState();
	}
	else if ( node.serverExpandURL )
	{
		node.open = true;
		this.storeState();
		this.document.location.href = node.serverExpandURL;
	}
}

// Push and pop are not implemented in IE

if ( !Array.prototype.push )
{
	function Array_push()
	{
		for ( var i = 0; i < arguments.length; i++ )
		{
			this[this.length] = arguments[i];
		}
		return this.length;
	}
	Array.prototype.push = Array_push;
}

if ( !Array.prototype.pop )
{
	function Array_pop()
	{
		lastElement = this[this.length - 1];
		this.length = Math.max( this.length - 1, 0 );
		return lastElement;
	}
	Array.prototype.pop = Array_pop;
}
