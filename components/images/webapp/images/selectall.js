
// Check Box Select/Toggle Functions for Select/Toggle All

function toggle(e) {
    e.checked = !e.checked;    
}
function checkToggle(e) {
    var cform = document.selectAllForm;
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
    var cform = document.selectAllForm;
    var len = cform.elements.length;
    for (var i = 0; i < len; i++) {
        var element = cform.elements[i];                   
        if (element.name.substring(0, 10) == "_rowSubmit" && element.checked != e.checked) {
            toggle(element);
        } 
    }     
}
function selectAll() {
    var cform = document.selectAllForm;
    var len = cform.elements.length;
    for (var i = 0; i < len; i++) {
        var element = cform.elements[i];                   
        if ((element.name == "selectAll" || element.name.substring(0, 10) == "_rowSubmit") && !element.checked) {
            toggle(element);
        } 
    }     
}
function removeSelected() {
    var cform = document.selectAllForm;
    cform.removeSelected.value = true;
    cform.submit();
}
