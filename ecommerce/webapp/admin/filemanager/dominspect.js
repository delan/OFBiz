// PPK's DOMparse

var readroot,writeroot;
var lvl = 1;
var xtemp = new Array();
var ytemp = new Array();
var ztemp = new Array();
var atemp = new Array();

function clearIt()
{
	if (!writeroot) return;
	while(writeroot.hasChildNodes())
	{
		writeroot.removeChild(writeroot.childNodes[0]);
	}

}

function init()
{
	if (!document.getElementById)
	{
		alert('This script doesn\'t work in your browser');
		return;
	}
	formroot = document.forms['nodeform'];
	read = formroot.write.value;
	if (read && document.getElementById(read)) readroot = document.getElementById(read);
	else readroot = document;
	writeroot = document.getElementById('nodemap');
	clearIt();
	tmp1 = document.createElement('P');
	tmp2 = document.createTextNode('Content of ' + readroot.nodeName + ' with ID = ' + readroot.id);
	tmp1.appendChild(tmp2);
	writeroot.appendChild(tmp1);
	level();
}

function level()
{
	atemp[lvl] = document.createElement('OL');
	for (var i=0;i<readroot.childNodes.length;i++)
	{
		x = readroot.childNodes[i];
		if (x.nodeType == 3 && formroot.hideempty.checked)
		{
			var hide = true;
			for (j=0;j<x.nodeValue.length;j++)
			{
				if (x.nodeValue.charAt(j) != '\n' && x.nodeValue.charAt(j) != ' ')
				{
					hide = false;
					break;
				}
			}
			if (hide) continue;
		}
		a1 = document.createElement('LI');
		a2 = document.createElement('SPAN');
		if (x.nodeType == 3) a2.className="text";
		a3 = document.createTextNode(x.nodeName);
		a2.appendChild(a3);
		a1.appendChild(a2);
		atemp[lvl].appendChild(a1);
		if (x.nodeType == 3 && formroot.showtext.checked)
		{
			a6 = document.createElement('BR');
			a5 = document.createTextNode(x.nodeValue);
			a2.appendChild(a6);
			a2.appendChild(a5);
		}
		if (x.attributes && formroot.showattr.checked)
		{
			a3 = document.createElement('SPAN');
			a3.className="attr";
			for (j=0;j<x.attributes.length;j++)
			{
				if (x.attributes[j].specified)
				{
					a5 = document.createElement('BR');
					a6 = document.createTextNode(x.attributes[j].nodeName + ' = ' + x.attributes[j].nodeValue);
					a3.appendChild(a5);
					a3.appendChild(a6);
				}
			}
			a2.appendChild(a3);
		}
		if (x.hasChildNodes())
		{
			lvl++;
			xtemp[lvl] = writeroot;
			ytemp[lvl] = readroot;
			ztemp[lvl] = i;
			readroot = readroot.childNodes[i];
			writeroot = atemp[lvl-1];
			level();
			i = ztemp[lvl];
			writeroot = xtemp[lvl];
			readroot = ytemp[lvl];
			lvl--;
		}
	}
	writeroot.appendChild(atemp[lvl]);
}

// End PPK's DOMparse
