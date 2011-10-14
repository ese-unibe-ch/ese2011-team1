package models;

import java.util.Date;

import play.jobs.*;

@OnApplicationStart
public class Bootstrap extends Job {
    
    public void doJob() {
    	// Event(Date start, Date end, String name, boolean is_visible)
    	
    	User user;
    	Event event;
    	Date now = new Date();
    	
    	// dataset 1
    	user = new User("simplay", "123");
    	event=new Event(now, now,"abc",true);
    	user.getdefaultCalendar().addEvent(event);
    	
    	Calendar cal = new Calendar("2nd simplay", user);
    	user.addCalendar(cal);
    	
    	cal = new Calendar("3rd simplay", user);
    	user.addCalendar(cal);
    	Database.addUser(user);
    	
    	
    	// dataset 2
    	user = new User("mib", "1337");
    	event=new Event(now, now,"mib_ev1",true);
    	user.getdefaultCalendar().addEvent(event);
    	
    	event=new Event(now, now,"mib_ev2",false);
    	user.getdefaultCalendar().addEvent(event);
    	
    	event=new Event(now, now,"mib_ev3",true);
    	user.getdefaultCalendar().addEvent(event);
    	
    	
    	cal = new Calendar("second mib", user);
    	
    	event=new Event(now, now,"second mib_ev1",false);
    	cal.addEvent(event);
    	
    	event=new Event(now, now,"second mib_ev2",true);
    	cal.addEvent(event);
    	
    	event=new Event(now, now,"second mib_ev3",true);
    	cal.addEvent(event);
    	
    	user.addCalendar(cal);
    	
    	
    	Database.addUser(user);
    	
    	
    	user = new User("simon", "1337");
    	event=new Event(now, now,"simonb_ev1",true);
    	user.getdefaultCalendar().addEvent(event);
    	
    	event=new Event(now, now,"simon_ev2",false);
    	user.getdefaultCalendar().addEvent(event);
    	
    	event=new Event(now, now,"simon_ev3",true);
    	user.getdefaultCalendar().addEvent(event);
    	
    	
    	cal = new Calendar("second simon", user);
    	
    	event=new Event(now, now,"second simon_ev1",false);
    	cal.addEvent(event);
    	
    	event=new Event(now, now,"second simon_ev2",true);
    	cal.addEvent(event);
    	
    	event=new Event(now, now,"second simon_ev3",true);
    	cal.addEvent(event);
    	
    	user.addCalendar(cal);
    	
    	
    	Database.addUser(user);
    	
    }
}