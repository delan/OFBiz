Date.weekdays = $w('Пн Вт Ср Чт Пт Сб В�?');
Date.months = $w('Январь Февраль Март �?прель Май Июнь Июль �?вгу�?т Сент�?брь Окт�?брь �?о�?брь Декабрь');

Date.first_day_of_week = 1

_translations = {
  "OK": "OK",
  "Now": "Сейча�?",
  "Today": "Сегодн�?"
}

//load the data format
var dataFormatJs = "format_euro_24hr.js" // Not sure

var e = document.createElement("script");
e.src = "/images/calendarDateSelect/format/" + dataFormatJs;
e.type="text/javascript";
document.getElementsByTagName("head")[0].appendChild(e);
