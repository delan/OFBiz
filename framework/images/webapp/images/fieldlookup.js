
// ================= FIELD LOOKUP METHODS ============================

var target = null;
var target2 = null;

function call_fieldlookup(target, viewName, formName,viewWidth,viewheight) {   
    var fieldLookup = new fieldLookup1(target);  
    if (! viewWidth) viewWidth = 350;
    if (! viewheight) viewheight = 200;
    fieldLookup.popup(viewName, formName, viewWidth, viewheight);
}
function call_fieldlookup2(target, viewName) {   
    var fieldLookup = new fieldLookup1(target, arguments);  
    fieldLookup.popup2(viewName);
}
function call_fieldlookup3(target, target2, viewName) {
    var fieldLookup = new fieldLookup2(target, target2);
    fieldLookup.popup2(viewName);
}

function fieldLookup1(obj_target, args) {
    this.args = args;
	// passing methods
	this.popup = lookup_popup1;
	this.popup2 = lookup_popup2;

	// validate input parameters
	if (!obj_target)
		return lookup_error("Error calling the field lookup: no target control specified");
	if (obj_target.value == null)
		return cal_error("Error calling the field lookup: parameter specified is not valid tardet control");
    //this.target = obj_target; 
    target = obj_target; 
	
	// register in global collections
	//this.id = lookups.length;
	//lookups[this.id] = this;
}
function fieldLookup2(obj_target, obj_target2) {
    // passing methods
    this.popup    = lookup_popup1;
    this.popup2    = lookup_popup2;

    // validate input parameters
    if (!obj_target)
        return lookup_error("Error calling the field lookup: no target control specified");
    if (obj_target.value == null)
        return cal_error("Error calling the field lookup: parameter specified is not valid tardet control");
    target = obj_target;
    // validate input parameters
    if (!obj_target2)
        return lookup_error("Error calling the field lookup: no target control specified");
    if (obj_target2.value == null)
        return cal_error("Error calling the field lookup: parameter specified is not valid tardet control");
    target2 = obj_target2;


    // register in global collections
    //this.id = lookups.length;
    //lookups[this.id] = this;
}

function lookup_popup1 (view_name, form_name, viewWidth, viewheight) {
	var obj_lookupwindow = window.open(view_name + '?formName=' + form_name + '&id=' + this.id,'FieldLookup', 'width='+viewWidth+',height='+viewheight+',scrollbars=yes,status=no,resizable=yes,top='+my+',left='+mx+',dependent=yes,alwaysRaised=yes');
	obj_lookupwindow.opener = window;
	obj_lookupwindow.focus();
}
function lookup_popup2 (view_name) {
    var argString = "";
    if (this.args.length > 2) {
        for(var i=2; i < this.args.length; i++) {
            argString += "&parm" + (i-2) + "=" + this.args[i];
        }
    }
	var obj_lookupwindow = window.open(view_name + '?id=' + this.id + argString,'FieldLookup', 'width=750,height=550,scrollbars=yes,status=no,resizable=yes,top='+my+',left='+mx+',dependent=yes,alwaysRaised=yes');
	obj_lookupwindow.opener = window;
	obj_lookupwindow.focus();
}
function lookup_error (str_message) {
	alert (str_message);
	return null;
}
