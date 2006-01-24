var popped = null;

function popup(file, w, h) {

	if (popped && popped.open && !popped.closed) {
		popped.focus();
		popped.resizeTo(w,h);
		popped = window.open(file,'popped');

	} else {
		strOptions = "toolbars=0,directories=0,scrollbars=1,location=0,";
		strOptions += "width="+w+",";
		strOptions += "innerWidth="+w+",";
		strOptions += "height="+h+",";
		strOptions += "innerHeight="+h;
		popped = window.open(file,'popped',strOptions);
	}

}

