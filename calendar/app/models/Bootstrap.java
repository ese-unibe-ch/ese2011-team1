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
		event = new Event(simplay, now, now, "abc", Visibility.PUBLIC, false, 0);
		simplay.getdefaultCalendar().addEvent(event);

		Calendar cal = new Calendar("2nd simplay", simplay);
		simplay.addCalendar(cal);

		cal = new Calendar("3rd simplay", simplay);
		simplay.addCalendar(cal);
		Database.addUser(simplay);

		// dataset 2
		User user;
		user = new User("mib", "1337", now, "fox");
		event = new Event(user, now, now, "mib_ev1", Visibility.PUBLIC, false,
				0);
		user.getdefaultCalendar().addEvent(event);
		simplay.addObservedCalendar(user.getdefaultCalendar());
		simplay.addShownObservedCalendar(4);

		event = new Event(user, now, now, "mib_ev2", Visibility.PRIVATE, false,
				0);
		user.getdefaultCalendar().addEvent(event);

		event = new Event(user, now, now, "mib_ev3", Visibility.PUBLIC, false,
				0);
		user.getdefaultCalendar().addEvent(event);

		cal = new Calendar("second mib", user);

		event = new Event(user, now, now, "second mib_ev1", Visibility.PRIVATE,
				false, 0);
		cal.addEvent(event);

		event = new Event(user, now, now, "second mib_ev2", Visibility.PUBLIC,
				false, 0);
		cal.addEvent(event);

		event = new Event(user, now, now, "second mib_ev3", Visibility.PUBLIC,
				false, 0);
		cal.addEvent(event);

		user.addCalendar(cal);

		Database.addUser(user);

		user = new User("simon", "1337", now, "simu");
		event = new Event(user, now, now, "simonb_ev1", Visibility.PUBLIC,
				false, 0);
		user.getdefaultCalendar().addEvent(event);
		simplay.addObservedCalendar(user.getdefaultCalendar());

		event = new Event(user, now, now, "simon_ev2", Visibility.PRIVATE,
				false, 0);
		user.getdefaultCalendar().addEvent(event);

		event = new Event(user, now, now, "simon_ev3", Visibility.PUBLIC,
				false, 0);
		user.getdefaultCalendar().addEvent(event);

		cal = new Calendar("second simon", user);

		event = new Event(user, now, now, "second simon_ev1",
				Visibility.PRIVATE, false, 0);
		cal.addEvent(event);

		event = new Event(user, now, now, "second simon_ev2",
				Visibility.PUBLIC, false, 0);
		cal.addEvent(event);

		event = new Event(user, now, now, "second simon_ev3",
				Visibility.PUBLIC, false, 0);
		cal.addEvent(event);

		user.addCalendar(cal);

		Database.addUser(user);

	}
}