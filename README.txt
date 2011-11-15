Install Guide for Calendar-App (Windows)
******************************************************************************************
Install the play framework

	- Make sure that you got the latest Java-Version

	- Download the last Play-Version at: http://www.playframework.org/download
	- Add the location of the downloaded folder to your PATH.

	(see also: http://www.playframework.org/documentation/1.2.3/install)  

Run the calendar-app
******************************************************************************************

	- Open the CommandLine (cmd.exe)
	- Guide to the directory which contains the calendar-folder
	- enter: play run calendar

	- Open your Browser and open your localhost via http://localhost:9000
	- Now you should be able to see the calendar-login


Login
******************************************************************************************

To log in you can register and create a new account or use a preregistered account.

The following accounts already exist:

User1: 

username: simplay
password: 123

User2:

username: mib
password: 1337

User3: 

username: simon
password: 1337


If you do not need them you can delete these accounts when you are logged in as the corresponding username via the option "delete account".

Run Tests
******************************************************************************************
Run the tests to check if your app runs properly.

	- Open the CommandLine (cmd.exe)
	- Guide to .../calendar/test
	- enter: play test
	
	- Open a browser to the http://localhost:9000/@tests URL to see the test runner