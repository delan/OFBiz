<script>
// Title: Timestamp picker
// Description: See the demo at url
// URL: http://us.geocities.com/tspicker/
// Script featured on: http://javascriptkit.com/script/script2/timestamp.shtml
// Version: 1.0
// Date: 12-05-2001 (mm-dd-yyyy)
// Author: Denis Gritcyuk <denis@softcomplex.com>; <tspicker@yahoo.com>
// Notes: Permission given to use this script in any kind of applications if
//    header lines are left unchanged. Feel free to contact the author
//    for feature requests and/or donations

function setCalendarHasFocus(aFormObj, aHasFocus) {
	// Set hidden variable to show that a popup calendar is about to
	// be displayed.  This variable will be tested in the onBeforeUnload
	// handling to avoid popping up a confimation prompt.
	alert("setCalendarHasFocus start - " + aHasFocus);
	var vCalendarHasFocusObj = aFormObj.elements.item("calendarHasFocus");
	if (vCalendarHasFocusObj == null) {
		//alert("Did not find calendarHasFocus hidden field");
	} else {
		//alert("Found calendarHasFocus hidden field");
		if (aHasFocus) {
			//alert("Setting calendarHasFocus to true");
			vCalendarHasFocusObj.value = "true";
		} else {
			//alert("Setting calendarHasFocus to false");
			vCalendarHasFocusObj.value = "false";
		}
	}
	return false;
}

function show_calendar(str_target, str_datetime, isDateTime) {
//	alert("show_calendar start");
	var arr_months = ["January", "February", "March", "April", "May", "June",
		"July", "August", "September", "October", "November", "December"];
	var week_days = ["Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"];
	var n_weekstart = 1; // day week starts from (normally 0 or 1)

	var dt_datetime = (str_datetime == null || str_datetime =="" ?  new Date() :  str2datetime(str_datetime) );
	var dt_prev_month = new Date(dt_datetime);
	dt_prev_month.setMonth(dt_datetime.getMonth()-1);
	var dt_next_month = new Date(dt_datetime);
	dt_next_month.setMonth(dt_datetime.getMonth()+1);
	var dt_firstday = new Date(dt_datetime);
	dt_firstday.setDate(1);
	dt_firstday.setDate(1-(7+dt_firstday.getDay()-n_weekstart)%7);
	var dt_lastday = new Date(dt_next_month);
	dt_lastday.setDate(0);
	
	// html generation (feel free to tune it for your particular application)
	// print calendar header
	var str_buffer = new String (
		"<html>\n"+
		" <head>\n"+
		"  <title>Calendar</title>\n"+
		" </head>\n"+
		" <body bgcolor=\"White\">\n"+
		"  <table class=\"clsOTable\" cellspacing=\"0\" border=\"0\" width=\"100%\">\n" +
		"   <tr>\n" +
		"    <td bgcolor=\"#4682B4\">\n" +
		"     <table cellspacing=\"1\" cellpadding=\"3\" border=\"0\" width=\"100%\">\n" +
		"      <tr>\n" +
		"       <td bgcolor=\"#4682B4\">\n" +
		"        <a href=\"javascript:window.opener.show_calendar('"+
		           str_target + "', '" + dt2dtstr(dt_prev_month)+"'+' ' +document.cal.time.value);\">" +
		"         <img src=\"/images/prev.gif\" width=\"16\" height=\"16\" border=\"0\"" +
		"           alt=\"previous month\">\n" +
		"        </a>\n" +
		"       </td>\n" +
		"       <td bgcolor=\"#4682B4\" colspan=\"5\">\n" +
		"        <font color=\"white\" face=\"tahoma, verdana\" size=\"2\">\n" +
		"         " + arr_months[dt_datetime.getMonth()] + " " + dt_datetime.getFullYear() + "\n" +
		"        </font>\n" +
		"       </td>\n" +
		"       <td bgcolor=\"#4682B4\" align=\"right\">\n" +
		"        <a href=\"javascript:window.opener.show_calendar('" +
		           str_target+"', '"+dt2dtstr(dt_next_month)+"'+' ' +document.cal.time.value);\">\n" +
		"         <img src=\"/images/next.gif\" width=\"16\" height=\"16\" border=\"0\"" +
		"           alt=\"next month\">\n" +
		"        </a>\n" +
		"       </td>\n" +
		"      </tr>\n"
	);

	var dt_current_day = new Date(dt_firstday);
	// print weekdays titles
	str_buffer += "      <tr>\n";
	for (var n=0; n<7; n++)
		str_buffer += "       <td bgcolor=\"#87CEFA\">\n" +
		"       <font color=\"white\" face=\"tahoma, verdana\" size=\"2\">\n" +
		"        " + week_days[(n_weekstart+n)%7] + "\n" +
		"       </font></td>\n";
	// print calendar table
	str_buffer += "      </tr>\n";
	while (dt_current_day.getMonth() == dt_datetime.getMonth() ||
		dt_current_day.getMonth() == dt_firstday.getMonth()) {
		// print row heder
		str_buffer += "      <tr>\n";
		for (var n_current_wday=0; n_current_wday<7; n_current_wday++) {
				if (dt_current_day.getDate() == dt_datetime.getDate() &&
					dt_current_day.getMonth() == dt_datetime.getMonth())
					// print current date
					str_buffer += "       <td bgcolor=\"#FFB6C1\" align=\"right\">\n";
				else if (dt_current_day.getDay() == 0 || dt_current_day.getDay() == 6)
					// weekend days
					str_buffer += "       <td bgcolor=\"#DBEAF5\" align=\"right\">\n";
				else
					// print working days of current month
					str_buffer += "       <td bgcolor=\"white\" align=\"right\">\n";

				if (isDateTime == "1" )
					str_buffer += "        <a href=\"javascript:window.opener." + str_target +
						".value='"+dt2dtstr(dt_current_day)+"'+' ' +document.cal.time.value;window.close();\">\n";
				else
					str_buffer += "        <a href=\"javascript:window.opener." + str_target +
						".value='"+dt2dtstr(dt_current_day)+"';window.close();\">\n";
					
				if (dt_current_day.getMonth() == dt_datetime.getMonth())
					// print days of current month
					str_buffer += "         <font color=\"black\" face=\"tahoma, verdana\" size=\"2\">\n";
				else 
					// print days of other months
					str_buffer += "         <font color=\"gray\" face=\"tahoma, verdana\" size=\"2\">\n";
					
				str_buffer += "          " + dt_current_day.getDate() + "\n" +
				"         </font>\n" +
				"        </a>\n" +
				"       </td>\n";
				dt_current_day.setDate(dt_current_day.getDate()+1);
		}
		// print row footer
		str_buffer += "      </tr>\n";
	}
	// print calendar footer
	str_buffer +=
		"      <form name=\"cal\">\n" +
		"       <tr>\n" +
		"        <td colspan=\"7\" bgcolor=\"#87CEFA\">\n" +
		"         <font color=\"White\" face=\"tahoma, verdana\" size=\"2\">\n" +
		"          Time:\n" +
		"          <input type=\"text\" name=\"time\" value=\"" + dt2tmstr(dt_datetime) +
		"\" size=\"8\" maxlength=\"8\">\n" +
		"         </font>\n" +
		"        </td>\n" +
		"       </tr>\n" +
		"      </form>\n" +
		"     </table>\n" +
		"    </tr>\n" +
		"   </td>\n" +
		"  </table>\n" +
		" </body>\n" +
		"</html>\n";

	//alert("Fixin to open window");
	var vWinCal = window.open("", "Calendar", 
		"width=200,height=250,status=no,resizable=yes,top=200,left=200");
	//alert("Fixin to set window opener to self");
	vWinCal.opener = self;
	var calc_doc = vWinCal.document;
	//alert("Fixin to write str_buffer");
	calc_doc.write (str_buffer);
	calc_doc.close();
}
// datetime parsing and formatting routimes. modify them if you wish other datetime format
function str2datetime (str_datetime) {
	var re_date = /^(\d+)\-(\d+)\-(\d+)\s+(\d+)\:(\d+)\:(\d+)$/;
	if (!re_date.exec(str_datetime))
		return str2date(str_datetime)
	return (new Date (RegExp.$3, RegExp.$1-1, RegExp.$2, RegExp.$4, RegExp.$5, RegExp.$6));
}
function str2date (str_date) {
	var re_date = /^(\d+)\-(\d+)\-(\d+)$/;
	if (!re_date.exec(str_date)) return alert("Invalid Date format: "+ str_date);
	return (new Date (RegExp.$3, RegExp.$1-1, RegExp.$2, 0, 0, 0));
}

function dt2dtstr (dt_datetime) {
	return (new String (
			dt_datetime.getFullYear()+"-"+(dt_datetime.getMonth()+1)+"-"+dt_datetime.getDate()));
}
function dt2tmstr (dt_datetime) {
	return (new String (
			dt_datetime.getHours()+":"+dt_datetime.getMinutes()+":"+dt_datetime.getSeconds()));
}

</script>
