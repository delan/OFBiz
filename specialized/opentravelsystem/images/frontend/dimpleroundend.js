var clicked=""
var gtype=".gif"
var selstate="_over"
if (typeof(loc)=="undefined" || loc==""){
	var loc=""
	if (document.body&&document.body.innerHTML){
		var tt = document.body.innerHTML.toLowerCase();
		var last = tt.indexOf("dimpleroundend.js\"");
		if (last>0){
			var first = tt.lastIndexOf("\"", last);
			if (first>0 && first<last) loc = document.body.innerHTML.substr(first+1,last-first-1);
		}
	}
}

document.write("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr>");
tr(false);
writeButton("","http://www.rydges-chiangmai.com/profile.shtml","dimpleroundend_b1",172,22,"Profile","_self",0);
writeButton("","http://www.rydges-chiangmai.com/gallery.shtml","dimpleroundend_b2",172,22,"Gallery","_self",0);
writeButton("","http://www.rydges-chiangmai.com/roomfacilities.shtml","dimpleroundend_b3",172,22,"Facilities","_self",0);
writeButton("","http://www.rydges-chiangmai.com/rbc.shtml","dimpleroundend_b4",172,22,"Restaurant","_self",0);
writeButton("","http://www.rydges-chiangmai.com/group_travel.shtml","dimpleroundend_b5",172,22,"Group Travel","_self",0);
writeButton("","http://www.rydges-chiangmai.com/conferences.shtml","dimpleroundend_b6",172,22,"Conferences","_self",0);
writeButton("","http://www.rydges-chiangmai.com/bus_centre.shtml","dimpleroundend_b7",172,22,"Business Center","_self",0);
writeButton("","http://www.rydges-chiangmai.com/wireless.shtml","dimpleroundend_b8",172,22,"Wireless Internet Access","_self",0);
tr(true);
document.write("</tr></table>")
loc="";

function tr(b){if (b) document.write("<tr>");else document.write("</tr>");}

function turn_over(name) {
	if (document.images != null && clicked != name) {
		document[name].src = document[name+"_over"].src;
	}
}

function turn_off(name) {
	if (document.images != null && clicked != name) {
		document[name].src = document[name+"_off"].src;
	}
}

function reg(gname,name)
{
if (document.images)
	{
	document[name+"_off"] = new Image();
	document[name+"_off"].src = loc+gname+gtype;
	document[name+"_over"] = new Image();
	document[name+"_over"].src = loc+gname+"_over"+gtype;
	}
}

function evs(name){ return " onmouseover=\"turn_over('"+ name + "')\" onmouseout=\"turn_off('"+ name + "')\""}

function writeButton(urld, url, name, w, h, alt, target, hsp)
{
	gname = name;
	while(typeof(document[name])!="undefined") name += "x";
	reg(gname, name);
	tr(true);
	document.write("<td>");
	if (alt != "") alt = " alt=\"" + alt + "\"";
	if (target != "") target = " target=\"" + target + "\"";
	if (w > 0) w = " width=\""+w+"\""; else w = "";
	if (h > 0) h = " height=\""+h+"\""; else h = "";	
	if (url != "") url = " href=\"" + urld + url + "\"";
	
	document.write("<a " + url + evs(name) + target + ">");	
	
	if (hsp == -1) hsp =" align=\"right\"";
	else if (hsp > 0) hsp = " hspace=\""+hsp+"\"";
	else hsp = "";
	
	document.write("<img src=\""+loc+gname+gtype+"\" name=\"" + name + "\"" + w + h + alt + hsp + " border=\"0\" /></a></td>");
	tr(false);
}
