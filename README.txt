Install Guide for Calendar-App (Windows)
******************************************************************************************
Install the play framework

	- Make sure that you got the latest Java-Version

	- Download the last Play-Version at: http://www.playframework.org/download
	- Add the location of the downloaded folder to your PATH.

	(see also: http://www.playframework.org/documentation/1.2.3/install)  
******************************************************************************************
Install Guide for Calendar-App for MAC OS X

Short Guide:
The first thing you need to do is to install Homebrew this can be done by running the 
command below in a terminal window:

$	/usr/bin/ruby -e "$(curl -fsSL https://raw.github.com/gist/323731)"

then you have to update brew:

$	brew update

then type in the same window

$	brew install play

Everything should now work and you can create a new project with:

$	play new app_name


You can find the exact installation guide online on the following page:
http://www.arenpatel.com/blog/7-how-to-install-play-framework-in-osx

Hombrew on GitHub:
http://mxcl.github.com/homebrew/

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


Further Notes
******************************************************************************************
Optimized for all major browsers and Operating systems.
Although there are some combinations that might display certain things not as intended:

	- On Ubuntu 11.x, use Chromium for best user experience.


