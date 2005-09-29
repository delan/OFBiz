
// Check Box Select/Toggle Functions for Select/Toggle All

function toggle(e) {
    e.checked = !e.checked;    
}

function checkToggle(e) {
    checkToggle(e, "selectAllForm");
}
function checkToggle(e, formName) {
    var cform = document[formName];
    if (e.checked) {      
        var len = cform.elements.length;
        var allchecked = true;
        for (var i = 0; i < len; i++) {
            var element = cform.elements[i];
            if (element.name.substring(0, 10) == "_rowSubmit" && !element.checked) {       
                allchecked = false;
            }
            cform.selectAll.checked = allchecked;            
        }
    } else {
        cform.selectAll.checked = false;
    }
}

function toggleAll(e) {
    toggleAll(e, "selectAllForm");
}
function toggleAll(e, formName) {
    var cform = document[formName];
    var len = cform.elements.length;
    for (var i = 0; i < len; i++) {
        var element = cform.elements[i];
        if (element.name.substring(0, 10) == "_rowSubmit" && element.checked != e.checked) {
            toggle(element);
        }
    }
}

function selectAll() {
    selectAll("selectAllForm");
}
function selectAll(formName) {
    var cform = document[formName];
    var len = cform.elements.length;
    for (var i = 0; i < len; i++) {
        var element = cform.elements[i];                   
        if ((element.name == "selectAll" || element.name.substring(0, 10) == "_rowSubmit") && !element.checked) {
            toggle(element);
        } 
    }     
}

function removeSelected() {
    removeSelected("selectAllForm");
}
function removeSelected(formName) {
    var cform = document[formName];
    cform.removeSelected.value = true;
    cform.submit();
}

// popup windows functions

function popUp(url, name, height, width) {
    popupWindow = window.open(url, name, 'location=no,scrollbars,width=' + width + ',height=' + height);
}
function popUpSmall(url, name) {
    popUp(url, name, '300', '450');
}

// hidden div functions

function getStyleObject(objectId) {
    if (document.getElementById && document.getElementById(objectId)) {
        return document.getElementById(objectId).style;
    } else if (document.all && document.all(objectId)) {
        return document.all(objectId).style;
    } else if (document.layers && document.layers[objectId]) {
        return document.layers[objectId];
    } else {
        return false;
    }
}
function changeObjectVisibility(objectId, newVisibility) {
    var styleObject = getStyleObject(objectId);
    if (styleObject) {
        styleObject.visibility = newVisibility;
        return true;
    } else {
        return false;
    }
}
