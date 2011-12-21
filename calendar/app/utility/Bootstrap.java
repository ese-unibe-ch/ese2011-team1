package utility;

import models.Calendar;
import models.Database;
import models.Event;
import models.PointEvent;
import models.RepeatingEvent;
import models.User;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.mvc.Http.Response;
import enums.Interval;
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
	final static DateTimeFormatter dateTimeInputFormatter = DateTimeFormat
//			.forPattern("dd/MM/yyyy, HH:mm");
			.forPattern("yyyy-MM-dd-HH-mm");
	final static DateTimeFormatter birthdayFormatter = DateTimeFormat
//			.forPattern("dd/MM/yyyy");
			.forPattern("yyyy-MM-dd");

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
//		DateTime now1HourAgo = new DateTime().minusHours(1);
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
				birthdayFormatter.parseDateTime("1989-12-24"), "senderos",
				Database.messageSystem);
		simplay.setLastLogin(new DateTime());
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
				birthdayFormatter.parseDateTime("1989-12-24"), "fox",
				Database.messageSystem);
		mib.setBirthdayPublic(true);
		Database.addUser(mib);
		Calendar mibsPersonalCalendar = mib.getdefaultCalendar();
		mib.setLastLogin(new DateTime());
		
		// mibs personal calendar
		Event drinkABeerInTown = new PointEvent("Drink a Beer in Town",
				nowIn1Hour, nowIn2Hours, Visibility.PUBLIC,
				mibsPersonalCalendar);
		drinkABeerInTown.setOpen();
		drinkABeerInTown.addUserToAttending(mib);
		mibsPersonalCalendar.addEvent(drinkABeerInTown);
		simplay.addObservedCalendar(mib.getdefaultCalendar());
		simplay.addShownObservedCalendar(mibsPersonalCalendar.getId());

		Event mib_ev1_2 = new PointEvent("ESE Presentation", now, nowIn1Hour,
				Visibility.PUBLIC, mibsPersonalCalendar);
		mibsPersonalCalendar.addEvent(mib_ev1_2);

		Event mib_ev1_3 = new PointEvent("ESE Lecture", now2HourAgo, now,
				Visibility.PUBLIC, mibsPersonalCalendar);
		mibsPersonalCalendar.addEvent(mib_ev1_3);

		// mibs shopping calendar
		Calendar mibsShoppingCalendar = new Calendar("Shopping", mib);
		mib.addCalendar(mibsShoppingCalendar);

		Event mib_ev2_1 = new PointEvent("Buy some cigarettes", now,
				nowIn1Hour, Visibility.PUBLIC, mibsShoppingCalendar);
		mibsShoppingCalendar.addEvent(mib_ev2_1);

		Event mib_ev2_2 = new PointEvent("Meeting with Andy", nowIn1Hour,
				nowIn2Hours, Visibility.PUBLIC, mibsShoppingCalendar);
		mibsShoppingCalendar.addEvent(mib_ev2_2);

		Event mib_ev2_3 = new PointEvent("Make dinner for my dog", nowIn2Hours,
				nowIn3Hours, Visibility.PUBLIC, mibsShoppingCalendar);
		mibsShoppingCalendar.addEvent(mib_ev2_3);

		// dataset 3
		User simon = new User("Simon", "1337", now, "simu",
				Database.messageSystem);
		Database.addUser(simon);
		simon.setLastLogin(new DateTime());
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

		Calendar simonsMeetingCalendar = new Calendar("Meeting", simon);
		simon.addCalendar(simonsMeetingCalendar);

		Event meetingCoach = new PointEvent("Meeting with Coach", tomorrow,
				tomorrowPlus1Hour, Visibility.PUBLIC, simonsMeetingCalendar);
		simonsMeetingCalendar.addEvent(meetingCoach);

		Event meetingClient = new PointEvent("Meeting with Client",
				tomorrowPlus1Hour, tomorrowPlus2Hours, Visibility.PUBLIC,
				simonsMeetingCalendar);
		simonsMeetingCalendar.addEvent(meetingClient);

		Event meetingWithNewClient = new PointEvent(
				"Meeting with potential Client", tomorrowPlus2Hours,
				tomorrowPlus3Hours, Visibility.PUBLIC, simonsMeetingCalendar);
		simonsMeetingCalendar.addEvent(meetingWithNewClient);

		// dataset 4
		User anna = new User("Anna", "123",
				birthdayFormatter.parseDateTime("1989-12-24"),"Anna",
				Database.messageSystem);
		anna.setBirthdayPublic(true);
		Database.addUser(anna);
		Calendar annasPersonalCalendar = anna.getdefaultCalendar();
		anna.setLastLogin(new DateTime());
		
		// Anna's personal calendar
		Event meetWithTheGirls = new PointEvent("Meet with the Girls", now,
				nowIn3Hours, Visibility.PUBLIC, annasPersonalCalendar);
		annasPersonalCalendar.addEvent(meetWithTheGirls);
		meetWithTheGirls.setOpen();
		meetWithTheGirls.addUserToAttending(anna);
		// Anna following mib personal Calendar
		anna.addObservedCalendar(mib.getCalendars().get(1));
		anna.addShownObservedCalendar(mib.getCalendars().get(1).getId());

		Event buySomeShoes = new PointEvent("Shop some shoes", tomorrow,
				tomorrowPlus2Hours, Visibility.PUBLIC, annasPersonalCalendar);
		annasPersonalCalendar.addEvent(buySomeShoes);

		// dataset 5
		User bob = new User("Bob", "123",
				birthdayFormatter.parseDateTime("1989-12-24"), "Bob",
				Database.messageSystem);
		bob.setBirthdayPublic(true);
		Database.addUser(bob);
		Calendar bobPersonalCalendar = bob.getdefaultCalendar();
		bob.setLastLogin(new DateTime());
		
		// Bob's personal Calendar
		DateTime special = dateTimeInputFormatter
				.parseDateTime("2011-12-02-20-00");
		DateTime specialEnd = dateTimeInputFormatter
				.parseDateTime("2011-12-04-20-00");

		Event party = new RepeatingEvent("Party Every Weekend", special,
				specialEnd, Visibility.PUBLIC, bobPersonalCalendar,
				Interval.WEEKLY);
		bobPersonalCalendar.addEvent(party);

		// dataset 6
		User oskar = new User("Oskar", "123",
				birthdayFormatter.parseDateTime("1989-12-24"), "Oskar",
				Database.messageSystem);
		oskar.setBirthdayPublic(true);
		Database.addUser(oskar);
		oskar.setLastLogin(new DateTime());
		
		Calendar oskarsHolidaysCalendar = new Calendar("Holiday", oskar);
		oskar.addCalendar(oskarsHolidaysCalendar);
		DateTime holidayBeginn = dateTimeInputFormatter
				.parseDateTime("2011-12-23-06-00");
		DateTime holidayEnd = dateTimeInputFormatter
				.parseDateTime("2012-02-19-23-00");

		Event holidays = new RepeatingEvent("Holidays", holidayBeginn,
				holidayEnd, Visibility.PUBLIC, oskarsHolidaysCalendar,
				Interval.NONE);
		oskarsHolidaysCalendar.addEvent(holidays);
		simplay.addObservedCalendar(oskarsHolidaysCalendar);

		// dataset 7
		User stefanie = new User("Stefanie", "123",
				birthdayFormatter.parseDateTime("1989-12-24"), "Stefanie",
				Database.messageSystem);
		stefanie.setBirthdayPublic(true);
		Database.addUser(stefanie);
		stefanie.setLastLogin(new DateTime());
		
		// dataset 8
		User julian = new User("Julian", "123",
				birthdayFormatter.parseDateTime("1989-12-24"), "Julian",
				Database.messageSystem);
		julian.setBirthdayPublic(true);
		Database.addUser(julian);
		julian.setLastLogin(new DateTime());

		// dataset 9
		User bruno = new User("Bruno", "123",
				birthdayFormatter.parseDateTime("1989-12-24"), "Bruno",
				Database.messageSystem);
		bruno.setBirthdayPublic(true);
		Database.addUser(bruno);
		bruno.setLastLogin(new DateTime());
		
		// dataset 10
		User alibaba = new User("Alibaba", "123",
				birthdayFormatter.parseDateTime("1989-12-24"), "Alibaba",
				Database.messageSystem);
		alibaba.setBirthdayPublic(true);
		Database.addUser(alibaba);
		alibaba.setLastLogin(new DateTime());
		
		// dataset 11
		User carla = new User("Carla", "123",
				birthdayFormatter.parseDateTime("1989-12-24"), "Carla",
				Database.messageSystem);
		carla.setBirthdayPublic(true);
		Database.addUser(carla);
		carla.setLastLogin(new DateTime());
		
		// dataset 12
		User camillo = new User("Camillo", "123",
				birthdayFormatter.parseDateTime("1989-12-24"),"Camillo",
				Database.messageSystem);
		camillo.setBirthdayPublic(true);
		Database.addUser(camillo);
		camillo.setLastLogin(new DateTime());

		// dataset 13
		User debby = new User("Debby", "123",
				birthdayFormatter.parseDateTime("1989-12-24"), "Debby",
				Database.messageSystem);
		debby.setBirthdayPublic(true);
		Database.addUser(debby);
		debby.setLastLogin(new DateTime());

		// dataset 14
		User donald = new User("Donald", "123",
				birthdayFormatter.parseDateTime("1989-12-24"), "Donald",
				Database.messageSystem);
		donald.setBirthdayPublic(true);
		Database.addUser(donald);
		donald.setLastLogin(new DateTime());
		
		// dataset 15
		User emma = new User("Emma", "123",
				birthdayFormatter.parseDateTime("1989-12-24"), "Emma",
				Database.messageSystem);
		emma.setBirthdayPublic(true);
		Database.addUser(emma);
		emma.setLastLogin(new DateTime());
		
		// dataset 16
		User elvis = new User("Elvis", "123",
				birthdayFormatter.parseDateTime("1989-12-24"), "Elvis",
				Database.messageSystem);
		elvis.setBirthdayPublic(true);
		Database.addUser(elvis);
		elvis.setLastLogin(new DateTime());
		
		// dataset 17
		User francis = new User("Francis", "123",
				birthdayFormatter.parseDateTime("1989-12-24"), "Francis",
				Database.messageSystem);
		francis.setBirthdayPublic(true);
		Database.addUser(francis);
		francis.setLastLogin(new DateTime());
		
		// dataset 18
		User michael = new User("Michael", "123",
				birthdayFormatter.parseDateTime("1989-12-24"), "Michael",
				Database.messageSystem);
		michael.setBirthdayPublic(true);
		Database.addUser(michael);
		michael.setLastLogin(new DateTime());
		
		// dataset 19
		User adrianus = new User("Adrianus", "123",
				birthdayFormatter.parseDateTime("1989-12-24"), "Adrianus",
				Database.messageSystem);
		adrianus.setBirthdayPublic(true);
		Database.addUser(adrianus);
		adrianus.setLastLogin(new DateTime());

		// dataset 20
		User kate = new User("Kate", "123",
				birthdayFormatter.parseDateTime("1989-12-24"), "Kate",
				Database.messageSystem);
		kate.setBirthdayPublic(true);
		Database.addUser(kate);
		kate.setLastLogin(new DateTime());

		// dataset 21
		User nemo = new User("Nemo", "123",
				birthdayFormatter.parseDateTime("1989-12-24"), "Nemo",
				Database.messageSystem);
		nemo.setBirthdayPublic(true);
		Database.addUser(nemo);
		nemo.setLastLogin(new DateTime());

		// dataset 22
		User laura = new User("Laura", "123",
				birthdayFormatter.parseDateTime("1989-12-24"),"Laura",
				Database.messageSystem);
		laura.setBirthdayPublic(true);
		Database.addUser(laura);
		laura.setLastLogin(new DateTime());

		// dataset 23
		User tequilla = new User("Tequilla", "123",
				birthdayFormatter.parseDateTime("1989-12-24"), "Tequilla",
				Database.messageSystem);
		tequilla.setBirthdayPublic(true);
		Database.addUser(tequilla);
		tequilla.setLastLogin(new DateTime());

		// dataset 24
		User julia = new User("Julia", "123",
				birthdayFormatter.parseDateTime("1989-12-24"), "Julia",
				Database.messageSystem);
		julia.setBirthdayPublic(true);
		Database.addUser(julia);
		julia.setLastLogin(new DateTime());

		// dataset 25
		User romeo = new User("Romeo", "123",
				birthdayFormatter.parseDateTime("1989-12-24"), "Romeo",
				Database.messageSystem);
		romeo.setBirthdayPublic(true);
		Database.addUser(romeo);
		romeo.setLastLogin(new DateTime());

		// dataset 26
		User xavier = new User("Xavier", "123",
				birthdayFormatter.parseDateTime("1989-12-24"), "Xavier",
				Database.messageSystem);
		xavier.setBirthdayPublic(true);
		Database.addUser(xavier);
		xavier.setLastLogin(new DateTime());

		// dataset 27
		User obelix = new User("Obelix", "123",
				birthdayFormatter.parseDateTime("1989-12-24"), "Obelix",
				Database.messageSystem);
		obelix.setBirthdayPublic(true);
		Database.addUser(obelix);
		obelix.setLastLogin(new DateTime());

		// dataset 28
		User asterix = new User("Asterix", "123",
				birthdayFormatter.parseDateTime("1989-12-24"), "Asterix",
				Database.messageSystem);
		asterix.setBirthdayPublic(true);
		Database.addUser(asterix);
		asterix.setLastLogin(new DateTime());

		// dataset 29
		User geraldine = new User("Geraldine", "123",
				birthdayFormatter.parseDateTime("1989-12-24"), "Geraldine",
				Database.messageSystem);
		geraldine.setBirthdayPublic(true);
		Database.addUser(geraldine);
		geraldine.setLastLogin(new DateTime());

		// dataset 30
		User valentino = new User("Valentino", "123",
				birthdayFormatter.parseDateTime("1989-12-24"), "Valentino",
				Database.messageSystem);
		valentino.setBirthdayPublic(true);
		Database.addUser(valentino);
		valentino.setLastLogin(new DateTime());

	}
}