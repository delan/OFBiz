
// Check Box Select/Toggle Functions for Select/Toggle All

function toggle(e) {
    e.checked = !e.checked;    
}
function checkToggle(e) {
    var cform = document.receiveform;
    if (e.checked) {      
        var len = cform.elements.length;
        var allchecked = true;
        for (var i = 0; i < len; i++) {
            var element = cform.elements[i];
            var elementName = new java.lang.String(element.name);          
            if (elementName.startsWith("_rowSubmit") && !element.checked) {       
                allchecked = false;
            }
            cform.selectAll.checked = allchecked;            
        }
    } else {
        cform.selectAll.checked = false;
    }
}
function toggleAll(e) {
    var cform = document.receiveform;
    var len = cform.elements.length;
    for (var i = 0; i < len; i++) {
        var element = cform.elements[i];                   
        var eName = new java.lang.String(element.name);                
        if (eName.startsWith("_rowSubmit") && element.checked != e.checked) {
            toggle(element);
        } 
    }     
}
function selectAll() {
    var cform = document.receiveform;
    var len = cform.elements.length;
    for (var i = 0; i < len; i++) {
        var element = cform.elements[i];                   
        var eName = new java.lang.String(element.name);                
        if ((element.name == "selectAll" || eName.startsWith("_rowSubmit")) && !element.checked) {
            toggle(element);
        } 
    }     
}
function removeSelected() {
    var cform = document.receiveform;
    cform.removeSelected.value = true;
    cform.submit();
}
