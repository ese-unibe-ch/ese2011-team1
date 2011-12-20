function send() {
	//format yyyy-MM-dd-HH-mm
	var yy = document.forms.startdate.syear.selectedIndex+2011;
	var mm = document.forms.startdate.smonth.selectedIndex+1;
	var dd = document.forms.startdate.sday.selectedIndex+1;
	var hh = document.forms.startdate.shour.selectedIndex;
	var mmi = (document.forms.startdate.smins.selectedIndex)*5;
	
	var str = yy + '-' + mm + '-' + dd + '-' + hh + '-' + mmi;
	//alert(str);
	
	document.forms.hiddenform.start.value = str;
	
	yy = document.forms.enddate.year.selectedIndex+2011;
	mm = document.forms.enddate.month.selectedIndex+1;
	dd = document.forms.enddate.day.selectedIndex+1;
	hh = document.forms.enddate.hour.selectedIndex;
	mmi = (document.forms.enddate.mins.selectedIndex)*5;
	
	str = yy + '-' + mm + '-' + dd + '-' + hh + '-' + mmi;
	
	document.forms.hiddenform.end.value = str;
	document.forms.hiddenform.name.value = document.forms.before.titlename.value;
	//return true;
}


function checkdate(m, d, y) {
    return m > 0 && m < 13 && y > 0 && y < 32768 && d > 0 && d <= (new Date(y, m, 0)).getDate();
}

function printDate(d) {
	var weekday = new Array(7);	
	weekday[0] = "Sunday";
	weekday[1] = "Monday";
	weekday[2] = "Tuesday";
	weekday[3] = "Wednesday";
	weekday[4] = "Thursday";
	weekday[5] = "Friday";
	weekday[6] = "Saturday";
	var ret = weekday[d.getDay()];
	return ret;
}

function printStartDay() {
	var d = new Date();
	var yy = document.forms.startdate.syear.selectedIndex+2011;
	var mm = document.forms.startdate.smonth.selectedIndex;
	var dd = document.forms.startdate.sday.selectedIndex+1;
	//alert("Startday:"+dd+" "+mm+" "+yy);
	
	d.setFullYear(yy,mm,dd);
	document.getElementById("startday").innerHTML = "("+printDate(d)+")";
	return true;
}

function sameDate() {	
	var ya = document.forms.startdate.syear.selectedIndex+2011;
	var ma = document.forms.startdate.smonth.selectedIndex;
	var da = document.forms.startdate.sday.selectedIndex+1;
	
	var yb = document.forms.enddate.year.selectedIndex+2011;
	var mb = document.forms.enddate.month.selectedIndex;
	var db = document.forms.enddate.day.selectedIndex+1;
	
	if (ya==yb && ma==mb && da==db) {return true;}
	else {return false;}
}

function printEndDay() {
	var d = new Date();
	var yy = document.forms.enddate.year.selectedIndex+2011;
	var mm = document.forms.enddate.month.selectedIndex;
	var dd = document.forms.enddate.day.selectedIndex+1;

	d.setFullYear(yy,mm,dd);
	document.getElementById("endday").innerHTML = "("+printDate(d)+")";

	if (sameDate() == true) {
		document.getElementById("endday").innerHTML = ""; }

	return true;
}

function printWeekDays() {
	printStartDay();
	printEndDay();
	return true;
}

// ENDDATE IS CHANGED
function OnChangeEndDate() {
	var d = document.forms.enddate.day.selectedIndex+1;
	var m = document.forms.enddate.month.selectedIndex+1;
	var y = document.forms.enddate.year.selectedIndex+2011;
	
	// check if date is correct
	if (checkdate(m, d, y) == false) {
		alert("Please enter a valid date.");
		var lastday = 0;
		for (i=1;i<5;i++) {
			lastday = d-i;
			if (checkdate(m,lastday,y) == true) break;
		}
		//set last day of month
		document.forms.enddate.day.selectedIndex = lastday-1;
	}
		
	printWeekDays();
}

// STARTDATE IS CHANGED
function OnChange(dropdown) {
	var d = document.forms.startdate.sday.selectedIndex+1;
	var m = document.forms.startdate.smonth.selectedIndex+1;
	var y = document.forms.startdate.syear.selectedIndex+2011;
	
	// check if date is correct
	if (checkdate(m, d, y) == false) {
		alert("Please enter a valid date.");
		var lastday = 0;
		for (i=1;i<5;i++) {
			lastday = d-i;
			if (checkdate(m,lastday,y) == true) break;
		}
		//set last day of month
		document.forms.startdate.sday.selectedIndex = lastday-1;
	}

	var myindex  = dropdown.selectedIndex;
	var SelValue = dropdown.options[myindex].value;

	// reset date
	document.forms.enddate.day.selectedIndex = document.forms.startdate.sday.selectedIndex;
	document.forms.enddate.month.selectedIndex = document.forms.startdate.smonth.selectedIndex;
	document.forms.enddate.year.selectedIndex = document.forms.startdate.syear.selectedIndex;	
	document.forms.enddate.hour.selectedIndex = document.forms.startdate.shour.selectedIndex+1;	
	document.forms.enddate.mins.selectedIndex = document.forms.startdate.smins.selectedIndex;
	
	// control variable
	var chk = false;
	var chk2= false;

	// OVERFLOW: HOUR-FIELD --> change day-field
	myindex = document.forms.startdate.shour.selectedIndex;
	if (document.forms.startdate.shour.options[myindex].value == 23) { 
		document.forms.enddate.hour.selectedIndex = 0;
		document.forms.enddate.day.selectedIndex = document.forms.enddate.day.selectedIndex + 1;
		chk = true;
	}
	
	// OVERFLOW: DAY-FIELD	--> change month-field
	//myindex = document.forms.startdate.sday.selectedIndex; //day should be dynamic
	d = document.forms.startdate.sday.selectedIndex+1;
	m = document.forms.startdate.smonth.selectedIndex+1;
	y = document.forms.startdate.syear.selectedIndex+2011;
	
	if (chk && checkdate(m, d+1, y) == false) { 
		document.forms.enddate.day.selectedIndex = 0;
		document.forms.enddate.month.selectedIndex = document.forms.enddate.month.selectedIndex + 1;
		chk2 = true;
	}
	
	// OVERFLOW: MONTH-FIELD --> change year-field
	myindex = document.forms.startdate.smonth.selectedIndex;
	if (chk2 && document.forms.startdate.smonth.options[myindex].value == 12) { 
		document.forms.enddate.month.selectedIndex = 0;
		document.forms.enddate.year.selectedIndex = document.forms.enddate.year.selectedIndex + 1;
	}
	
	printWeekDays();

	return true;
}