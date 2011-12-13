package models;

import org.joda.time.DateTime;

import play.jobs.Job;
import play.jobs.OnApplicationStart;
import enums.Visibility;

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
	 * 
	 * @see {@link User}
	 * @see {@link Calendar}
	 * @see {@link Event}
	 */
	public void doJob() {
		// Event(Date start, Date end, String name, boolean is_visible)

		DateTime now = new DateTime();
		// dataset 1
		User simplay = new User("simplay", "123", now, "senderos", Database.messageSystem);
		Calendar simplayDefaultCal = simplay.getdefaultCalendar();
		Event abc = new PointEvent("abc", now, now, Visibility.PUBLIC,
				simplayDefaultCal);
		simplayDefaultCal.addEvent(abc);

		Calendar secondSimplay = new Calendar("2nd simplay", simplay);
		simplay.addCalendar(secondSimplay);

		Calendar thirdSimplay = new Calendar("3rd simplay", simplay);
		simplay.addCalendar(thirdSimplay);

		Database.addUser(simplay);

		// dataset 2
		User mib = new User("mib", "1337", now, "fox", Database.messageSystem);
		mib.setBirthdayPublic(true);
		Database.addUser(mib);
		Calendar mibsFirstCalendar = mib.getdefaultCalendar();

		// mibs first calendar
		Event mib_ev1_1 = new PointEvent("mib_ev1", now, now,
				Visibility.PUBLIC, mibsFirstCalendar);
		mib_ev1_1.setOpen();
		mib_ev1_1.addUserToAttending(mib);
		mibsFirstCalendar.addEvent(mib_ev1_1);
		simplay.addObservedCalendar(mib.getdefaultCalendar());
		simplay.addShownObservedCalendar(mibsFirstCalendar.getId());

		Event mib_ev1_2 = new PointEvent("mib_ev2", now, now,
				Visibility.PUBLIC, mibsFirstCalendar);
		mibsFirstCalendar.addEvent(mib_ev1_2);

		Event mib_ev1_3 = new PointEvent("mib_ev3", now, now,
				Visibility.PUBLIC, mibsFirstCalendar);
		mibsFirstCalendar.addEvent(mib_ev1_3);

		// mibs second calendar
		Calendar mibsSecondCalendar = new Calendar("second mib", mib);
		mib.addCalendar(mibsSecondCalendar);

		Event mib_ev2_1 = new PointEvent("second mib_ev1", now, now,
				Visibility.PUBLIC, mibsSecondCalendar);
		mibsSecondCalendar.addEvent(mib_ev2_1);

		Event mib_ev2_2 = new PointEvent("second mib_ev2", now, now,
				Visibility.PUBLIC, mibsSecondCalendar);
		mibsSecondCalendar.addEvent(mib_ev2_2);

		Event mib_ev2_3 = new PointEvent("second mib_ev3", now, now,
				Visibility.PUBLIC, mibsSecondCalendar);
		mibsSecondCalendar.addEvent(mib_ev2_3);

		User simon = new User("simon", "1337", now, "simu", Database.messageSystem);
		Database.addUser(simon);
		Calendar simonsFirstCalendar = simon.getdefaultCalendar();

		Event simon1_1 = new PointEvent("simonb_ev1", now, now,
				Visibility.PUBLIC, simonsFirstCalendar);
		simonsFirstCalendar.addEvent(simon1_1);
		simplay.addObservedCalendar(simon.getdefaultCalendar());

		Event simon1_2 = new PointEvent("simonb_ev2", now, now,
				Visibility.PUBLIC, simonsFirstCalendar);
		simonsFirstCalendar.addEvent(simon1_2);

		Event simon1_3 = new PointEvent("simonb_ev3", now, now,
				Visibility.PUBLIC, simonsFirstCalendar);
		simonsFirstCalendar.addEvent(simon1_3);

		Calendar simonsSecondCalendar = new Calendar("second simon", simon);
		simon.addCalendar(simonsSecondCalendar);

		Event simon2_1 = new PointEvent("second simon_ev1", now, now,
				Visibility.PUBLIC, simonsSecondCalendar);
		simonsSecondCalendar.addEvent(simon2_1);

		Event simon2_2 = new PointEvent("second simon_ev2", now, now,
				Visibility.PUBLIC, simonsSecondCalendar);
		simonsSecondCalendar.addEvent(simon2_2);

		Event simon2_3 = new PointEvent("second simon_ev3", now, now,
				Visibility.PUBLIC, simonsSecondCalendar);
		simonsSecondCalendar.addEvent(simon2_3);

	}
}