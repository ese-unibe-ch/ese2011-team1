package models;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

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
	final static DateTimeFormatter birthdayFormatter = DateTimeFormat
			.forPattern("dd/MM/yyyy");

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
		DateTime now2HourAgo = new DateTime().minusHours(2);
		DateTime now1HourAgo = new DateTime().minusHours(1);
		DateTime now = new DateTime();
		DateTime nowIn1Hour = new DateTime().plusHours(1);
		DateTime nowIn2Hours = new DateTime().plusHours(2);
		DateTime nowIn3Hours = new DateTime().plusHours(3);
		DateTime tomorrow = new DateTime().plusDays(1);
		DateTime tomorrowPlus1Hour = new DateTime().plusDays(1).plusHours(1);
		DateTime tomorrowPlus2Hours = new DateTime().plusDays(1).plusHours(2);
		DateTime tomorrowPlus3Hours = new DateTime().plusDays(1).plusHours(3);
		// dataset 1
		User simplay = new User("simplay", "123",
				birthdayFormatter.parseDateTime("24/12/1989"), "senderos",
				Database.messageSystem);
		Calendar simplayDefaultCal = simplay.getdefaultCalendar();
		Event football_Evening = new PointEvent("Football Evening at my Home",
				now, nowIn3Hours, Visibility.PUBLIC, simplayDefaultCal);
		simplayDefaultCal.addEvent(football_Evening);

		Calendar schoolCalendar = new Calendar("School", simplay);
		simplay.addCalendar(schoolCalendar);

		Calendar ExamsCalendar = new Calendar("Exam", simplay);
		simplay.addCalendar(ExamsCalendar);

		Database.addUser(simplay);

		// dataset 2
		User mib = new User("mib", "1337",
				birthdayFormatter.parseDateTime("12/12/1989"), "fox",
				Database.messageSystem);
		mib.setBirthdayPublic(true);
		Database.addUser(mib);
		Calendar mibsFirstCalendar = mib.getdefaultCalendar();

		// mibs personal calendar
		Event drinkABeerInTown = new PointEvent("Drink a Beer in Town",
				nowIn1Hour, nowIn2Hours, Visibility.PUBLIC, mibsFirstCalendar);
		drinkABeerInTown.setOpen();
		drinkABeerInTown.addUserToAttending(mib);
		mibsFirstCalendar.addEvent(drinkABeerInTown);
		simplay.addObservedCalendar(mib.getdefaultCalendar());
		simplay.addShownObservedCalendar(mibsFirstCalendar.getId());

		Event mib_ev1_2 = new PointEvent("ESE Presentation", now, nowIn1Hour,
				Visibility.PUBLIC, mibsFirstCalendar);
		mibsFirstCalendar.addEvent(mib_ev1_2);

		Event mib_ev1_3 = new PointEvent("ESE Lecture", now2HourAgo, now,
				Visibility.PUBLIC, mibsFirstCalendar);
		mibsFirstCalendar.addEvent(mib_ev1_3);

		// mibs University calendar
		Calendar mibsSecondCalendar = new Calendar("Shopping", mib);
		mib.addCalendar(mibsSecondCalendar);

		Event mib_ev2_1 = new PointEvent("Buy some cigarettes", now,
				nowIn1Hour, Visibility.PUBLIC, mibsSecondCalendar);
		mibsSecondCalendar.addEvent(mib_ev2_1);

		Event mib_ev2_2 = new PointEvent("Meeting with Andy", nowIn1Hour,
				nowIn2Hours, Visibility.PUBLIC, mibsSecondCalendar);
		mibsSecondCalendar.addEvent(mib_ev2_2);

		Event mib_ev2_3 = new PointEvent("Make dinner for my dog", nowIn2Hours,
				nowIn3Hours, Visibility.PUBLIC, mibsSecondCalendar);
		mibsSecondCalendar.addEvent(mib_ev2_3);

		// dataset 3
		User simon = new User("simon", "1337", now, "simu",
				Database.messageSystem);
		Database.addUser(simon);
		Calendar simonsFirstCalendar = simon.getdefaultCalendar();

		Event getSomeFood = new PointEvent("Shop some food for tonight", now,
				nowIn1Hour, Visibility.PUBLIC, simonsFirstCalendar);
		simonsFirstCalendar.addEvent(getSomeFood);
		simplay.addObservedCalendar(simon.getdefaultCalendar());

		Event playBasketball = new PointEvent("Play Basketball", now2HourAgo,
				now, Visibility.PUBLIC, simonsFirstCalendar);
		simonsFirstCalendar.addEvent(playBasketball);

		Event watchTV = new PointEvent("Watch TV", now, nowIn3Hours,
				Visibility.PUBLIC, simonsFirstCalendar);
		simonsFirstCalendar.addEvent(watchTV);

		Calendar simonsSecondCalendar = new Calendar("Meeting", simon);
		simon.addCalendar(simonsSecondCalendar);

		Event meetingCoach = new PointEvent("Meeting with Coach", tomorrow,
				tomorrowPlus1Hour, Visibility.PUBLIC, simonsSecondCalendar);
		simonsSecondCalendar.addEvent(meetingCoach);

		Event meetingClient = new PointEvent("Meeting with Client",
				tomorrowPlus1Hour, tomorrowPlus2Hours, Visibility.PUBLIC,
				simonsSecondCalendar);
		simonsSecondCalendar.addEvent(meetingClient);

		Event meetingWithNewClient = new PointEvent(
				"Meeting with potential Client", tomorrowPlus2Hours,
				tomorrowPlus3Hours, Visibility.PUBLIC, simonsSecondCalendar);
		simonsSecondCalendar.addEvent(meetingWithNewClient);

	}
}