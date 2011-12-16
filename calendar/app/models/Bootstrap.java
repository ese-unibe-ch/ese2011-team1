package models;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import play.jobs.Job;
import play.jobs.OnApplicationStart;
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
	final static DateTimeFormatter birthdayFormatter = DateTimeFormat
			.forPattern("dd/MM/yyyy");
	final static DateTimeFormatter dateTimeInputFormatter = DateTimeFormat
			.forPattern("dd/MM/yyyy, HH:mm");

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
		Calendar mibsPersonalCalendar = mib.getdefaultCalendar();

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
				birthdayFormatter.parseDateTime("18/12/1989"), "Anna",
				Database.messageSystem);
		anna.setBirthdayPublic(true);
		Database.addUser(anna);
		Calendar annasPersonalCalendar = anna.getdefaultCalendar();

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
				birthdayFormatter.parseDateTime("24/12/1989"), "Bob",
				Database.messageSystem);
		bob.setBirthdayPublic(true);
		Database.addUser(bob);
		Calendar bobPersonalCalendar = bob.getdefaultCalendar();

		// Bob's personal Calendar
		DateTime special = dateTimeInputFormatter
				.parseDateTime("02/12/2011, 20:00");
		DateTime specialEnd = dateTimeInputFormatter
				.parseDateTime("04/12/2011, 20:00");

		Event party = new RepeatingEvent("Party Every Weekend", special,
				specialEnd, Visibility.PUBLIC, bobPersonalCalendar,
				Interval.WEEKLY);
		bobPersonalCalendar.addEvent(party);

		// dataset 6
		User oskar = new User("Oskar", "123",
				birthdayFormatter.parseDateTime("18/12/1960"), "Oskar",
				Database.messageSystem);
		oskar.setBirthdayPublic(true);
		Database.addUser(oskar);

		Calendar oskarsHolidaysCalendar = new Calendar("Holiday", oskar);
		oskar.addCalendar(oskarsHolidaysCalendar);
		DateTime holidayBeginn = dateTimeInputFormatter
				.parseDateTime("23/12/2011, 06:00");
		DateTime holidayEnd = dateTimeInputFormatter
				.parseDateTime("19/02/2012, 23:00");

		Event holidays = new RepeatingEvent("Holidays", holidayBeginn,
				holidayEnd, Visibility.PUBLIC, oskarsHolidaysCalendar,
				Interval.NONE);
		oskarsHolidaysCalendar.addEvent(holidays);
		simplay.addObservedCalendar(oskarsHolidaysCalendar);

		// dataset 7
		User stefanie = new User("Stefanie", "123",
				birthdayFormatter.parseDateTime("13/09/1967"), "Stefanie",
				Database.messageSystem);
		stefanie.setBirthdayPublic(true);
		Database.addUser(stefanie);

		// dataset 8
		User julian = new User("Julian", "123",
				birthdayFormatter.parseDateTime("27/08/1970"), "Julian",
				Database.messageSystem);
		julian.setBirthdayPublic(true);
		Database.addUser(julian);

		// dataset 9
		User bruno = new User("Bruno", "123",
				birthdayFormatter.parseDateTime("24/04/1985"), "Bruno",
				Database.messageSystem);
		bruno.setBirthdayPublic(true);
		Database.addUser(bruno);

		// dataset 10
		User alibaba = new User("Alibaba", "123",
				birthdayFormatter.parseDateTime("12/01/1955"), "Alibaba",
				Database.messageSystem);
		alibaba.setBirthdayPublic(true);
		Database.addUser(alibaba);

		// dataset 11
		User carla = new User("Carla", "123",
				birthdayFormatter.parseDateTime("01/02/1971"), "Carla",
				Database.messageSystem);
		carla.setBirthdayPublic(true);
		Database.addUser(carla);

		// dataset 12
		User camillo = new User("Camillo", "123",
				birthdayFormatter.parseDateTime("03/05/1999"), "Camillo",
				Database.messageSystem);
		camillo.setBirthdayPublic(true);
		Database.addUser(camillo);

		// dataset 13
		User debby = new User("Debby", "123",
				birthdayFormatter.parseDateTime("01/02/1945"), "Debby",
				Database.messageSystem);
		debby.setBirthdayPublic(true);
		Database.addUser(debby);

		// dataset 14
		User donald = new User("Donald", "123",
				birthdayFormatter.parseDateTime("09/02/1998"), "Donald",
				Database.messageSystem);
		donald.setBirthdayPublic(true);
		Database.addUser(donald);

		// dataset 15
		User emma = new User("Emma", "123",
				birthdayFormatter.parseDateTime("01/09/1995"), "Emma",
				Database.messageSystem);
		emma.setBirthdayPublic(true);
		Database.addUser(emma);

		// dataset 16
		User elvis = new User("Elvis", "123",
				birthdayFormatter.parseDateTime("10/10/1903"), "Elvis",
				Database.messageSystem);
		elvis.setBirthdayPublic(true);
		Database.addUser(elvis);

		// dataset 17
		User francis = new User("Francis", "123",
				birthdayFormatter.parseDateTime("15/02/1989"), "Francis",
				Database.messageSystem);
		francis.setBirthdayPublic(true);
		Database.addUser(francis);

		// dataset 18
		User michael = new User("Michael", "123",
				birthdayFormatter.parseDateTime("01/02/1990"), "Michael",
				Database.messageSystem);
		michael.setBirthdayPublic(true);
		Database.addUser(michael);

		// dataset 19
		User adrianus = new User("Adrianus", "123",
				birthdayFormatter.parseDateTime("19/06/1987"), "Adrianus",
				Database.messageSystem);
		adrianus.setBirthdayPublic(true);
		Database.addUser(adrianus);

		// dataset 20
		User kate = new User("Kate", "123",
				birthdayFormatter.parseDateTime("20/02/1945"), "Kate",
				Database.messageSystem);
		kate.setBirthdayPublic(true);
		Database.addUser(kate);

		// dataset 21
		User nemo = new User("Nemo", "123",
				birthdayFormatter.parseDateTime("21/11/2001"), "Nemo",
				Database.messageSystem);
		nemo.setBirthdayPublic(true);
		Database.addUser(nemo);

		// dataset 22
		User laura = new User("Laura", "123",
				birthdayFormatter.parseDateTime("08/08/1945"), "Laura",
				Database.messageSystem);
		laura.setBirthdayPublic(true);
		Database.addUser(laura);

		// dataset 23
		User tequilla = new User("Tequilla", "123",
				birthdayFormatter.parseDateTime("11/12/1980"), "Tequilla",
				Database.messageSystem);
		tequilla.setBirthdayPublic(true);
		Database.addUser(tequilla);

		// dataset 24
		User julia = new User("Julia", "123",
				birthdayFormatter.parseDateTime("26/04/1982"), "Julia",
				Database.messageSystem);
		julia.setBirthdayPublic(true);
		Database.addUser(julia);

		// dataset 25
		User romeo = new User("Romeo", "123",
				birthdayFormatter.parseDateTime("24/04/1983"), "Romeo",
				Database.messageSystem);
		romeo.setBirthdayPublic(true);
		Database.addUser(romeo);

		// dataset 26
		User xavier = new User("Xavier", "123",
				birthdayFormatter.parseDateTime("02/02/1960"), "Xavier",
				Database.messageSystem);
		xavier.setBirthdayPublic(true);
		Database.addUser(xavier);

		// dataset 27
		User obelix = new User("Obelix", "123",
				birthdayFormatter.parseDateTime("20/07/2000"), "Obelix",
				Database.messageSystem);
		obelix.setBirthdayPublic(true);
		Database.addUser(obelix);

		// dataset 28
		User asterix = new User("Asterix", "123",
				birthdayFormatter.parseDateTime("20/08/2001"), "Asterix",
				Database.messageSystem);
		asterix.setBirthdayPublic(true);
		Database.addUser(asterix);

		// dataset 29
		User geraldine = new User("Geraldine", "123",
				birthdayFormatter.parseDateTime("12/12/1980"), "Geraldine",
				Database.messageSystem);
		geraldine.setBirthdayPublic(true);
		Database.addUser(geraldine);

		// dataset 30
		User valentino = new User("Valentino", "123",
				birthdayFormatter.parseDateTime("24/12/1975"), "Valentino",
				Database.messageSystem);
		valentino.setBirthdayPublic(true);
		Database.addUser(valentino);

	}
}