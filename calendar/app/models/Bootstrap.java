package models;

import java.util.Date;

import models.Event.Visibility;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

@OnApplicationStart
public class Bootstrap extends Job {

	public void doJob() {
		// Event(Date start, Date end, String name, boolean is_visible)

		User simplay;
		Event event;
		Date now = new Date();
		// dataset 1
		simplay = new User("simplay", "123", now, "senderos");
		event = new Event(simplay, now, now, "abc", Visibility.PUBLIC, false, 0);
		simplay.getdefaultCalendar().addEvent(event);

		UserCalendar cal = new UserCalendar("2nd simplay", simplay);
		simplay.addCalendar(cal);

		cal = new UserCalendar("3rd simplay", simplay);
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

		cal = new UserCalendar("second mib", user);

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

		cal = new UserCalendar("second simon", user);

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