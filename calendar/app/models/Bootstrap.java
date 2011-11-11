package models;


import org.joda.time.DateTime;

import models.Event.Visibility;
import play.jobs.Job;
import play.jobs.OnApplicationStart;


/**
 * Bootstrap executes defined actions on every start of the play framework.
 * 
 * @see {@link Job}
 * @see {@link Bootstrap#doJob}
 *
 */
@OnApplicationStart
public class Bootstrap extends Job {

	/**
	 * Loads several Users along with their default Calendars and some Events.
	 * 
	 * Used for testing the Calendar app.
	 * @see {@link User}
	 * @see {@link Calendar}
	 * @see {@link Event}
	 */
	public void doJob() {
		// Event(Date start, Date end, String name, boolean is_visible)

		User simplay;
		Event event;
		DateTime now = new DateTime();
		// dataset 1
		simplay = new User("simplay", "123", now, "senderos");
		Calendar cal = simplay.getdefaultCalendar();
		event = new PointEvent("abc", now, now, Visibility.PUBLIC, cal);
		cal.addEvent(event);

		cal = new Calendar("2nd simplay", simplay);
		simplay.addCalendar(cal);
		
		cal = new Calendar("3rd simplay", simplay);
		simplay.addCalendar(cal);
		
		Database.addUser(simplay);

		// dataset 2
		User user;
		user = new User("mib", "1337", now, "fox");
		cal = new Calendar("Work", user);
		event = new PointEvent("mib_ev1", now, now, Visibility.PUBLIC, cal);
		event.setOpen();
		event.addUserToAttending(user);
		user.getdefaultCalendar().addEvent(event);
		simplay.addObservedCalendar(user.getdefaultCalendar());
		simplay.addShownObservedCalendar(cal.getId());

		event = new PointEvent("mib_ev2", now, now, Visibility.PUBLIC, cal);
		user.getdefaultCalendar().addEvent(event);

		event = new PointEvent("mib_ev3", now, now, Visibility.PUBLIC, cal);
		user.getdefaultCalendar().addEvent(event);

		cal = new Calendar("second mib", user);

		event = new PointEvent("second mib_ev1", now, now, Visibility.PUBLIC, cal);
		cal.addEvent(event);

		event = new PointEvent("second mib_ev2", now, now, Visibility.PUBLIC, cal);
		cal.addEvent(event);

		event = new PointEvent("second mib_ev3", now, now, Visibility.PUBLIC, cal);
		cal.addEvent(event);

		user.addCalendar(cal);

		Database.addUser(user);

		user = new User("simon", "1337", now, "simu");
		cal = new Calendar("Simon Work", user);
		event = new PointEvent("simonb_ev1", now, now, Visibility.PUBLIC, cal);
		user.getdefaultCalendar().addEvent(event);
		simplay.addObservedCalendar(user.getdefaultCalendar());

		event = new PointEvent("simonb_ev2", now, now, Visibility.PUBLIC, cal);
		user.getdefaultCalendar().addEvent(event);

		event = 
				new PointEvent("simonb_ev3", now, now, Visibility.PUBLIC, cal);
		user.getdefaultCalendar().addEvent(event);

		cal = new Calendar("second simon", user);

		event = new PointEvent("second simon_ev1", now, now, Visibility.PUBLIC, cal);
		cal.addEvent(event);

		event = new PointEvent("second simon_ev2", now, now, Visibility.PUBLIC, cal);
		cal.addEvent(event);

		event = new PointEvent("second simon_ev3", now, now, Visibility.PUBLIC, cal);
		cal.addEvent(event);

		user.addCalendar(cal);

		Database.addUser(user);

	}
}