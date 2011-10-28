package controllers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import models.Calendar;
import models.Database;
import models.Event;
import models.Event.Visibility;
import models.User;
import play.data.validation.Required;
import play.mvc.Controller;
import play.mvc.With;

//so far: jeder user kann neue user erzeugen mit default password 123

@With(Secure.class)
public class Application extends Controller {

	static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm");
	public static String message = null;

	public static void index() {
		User me = Database.users.get(Security.connected());
		List<User> users = Database.getUserList();
		String s_date = dateFormat.format(new Date());
		// todo: remove ourself from list
		render(users, me, s_date);
	}

	public static void showMe(String s_date) {
		User me = Database.users.get(Security.connected());
		List<User> users = Database.getUserList();
		Calendar defaultCalendar = me.getdefaultCalendar();
		LinkedList<Calendar> calendars = me.getCalendars();
		render(me, users, calendars, defaultCalendar, s_date);
	}

	public static void showCalendars(String username, String s_date) {
		User me = Database.users.get(Security.connected());
		User user = Database.users.get(username);
		LinkedList<Calendar> calendars = null;
		if (me != null && user != null) {
			calendars = user.getCalendars();
		}
		render(me, user, calendars, s_date);
	}

	public static void searchForUser(String userName, String s_date) {
		User me = Database.users.get(Security.connected());
		User user = Database.users.get(userName);

		if (user == null) {
			if (userName.length() == 0)
				flash.error("Please enter Name!");
			else
				flash.error("User (" + userName + ") not found!");

			index();
		} else {
			// if a user searches himself (you never know)
			if (me.getName().equals(user.getName()))
				showMe(s_date);

			showCalendars(userName, s_date);
		}
	}

	public static void showEvents(long calendarId, String username,
			String calendarName) {
		User me = Database.users.get(Security.connected());
		User user = Database.users.get(username);
		Date d = new Date(1, 1, 1);
		Iterator allVisibleEvents = user.getCalendarById(calendarId)
				.getEventList(d, me);
		Calendar calendars = user.getCalendarById(calendarId);
		LinkedList<Event> events = new LinkedList<Event>();

		while (allVisibleEvents.hasNext()) {
			events.add((Event) allVisibleEvents.next());
		}

		render(me, user, events, calendarName, calendars, calendarId);
	}

	/*
	 * I THINK WE COULD DELETE THIS
	 */
	// public static void showEventsOfDay(long calendarId, String username,
	// String calendarName, int day, int month, int year) {
	// User me = Database.users.get(Security.connected());
	// User user = Database.users.get(username);
	// // Date d = new Date(1,1,1);
	// LinkedList<Event> allVisibleEvents = user.getCalendarById(calendarId)
	// .getEventsOfDay(day, month, year, me);
	//
	// Calendar calendars = user.getCalendarById(calendarId);
	// LinkedList<Event> events = allVisibleEvents;
	//
	// render(me, user, events, calendarName, calendars, calendarId, day,
	// month, year);
	// }

	public static void showRegistration() {
		render();
	}

	public static void RegUser(@Required String name, @Required String password) {
		if (Database.userAlreadyRegistrated(name)) {
			flash.error("Username (" + name + ") already exists!");
			showRegistration();
		} else if (validation.hasErrors()) {
			params.flash();
			validation.keep();
			flash.error("All fields required!");
			showRegistration();
		} else {
			User user = new User(name, password);
			Database.addUser(user);
			index();
		}
	}

	public static void newUser(@Required String name) {
		User user;
		Event event;
		Date now = new Date();

		if (!name.isEmpty()) {
			// mache user mit default daten:
			user = new User(name, "123");
			event = new Event(user, now, now, "abc", Visibility.PUBLIC, false,
					0);
			// user.calendar.
			user.getdefaultCalendar().addEvent(event);

			Database.addUser(user);
			// Data d = new Data(value);

			renderJSON(user);
		}
	}

	public static void createEvent(@Required long calendarID,
			@Required String name, @Required String start,
			@Required String end, Visibility visibility, String is_repeated,
			String s_date, int dday, int mmonth, int yyear) {

		User me = Database.users.get(Security.connected());
		Calendar calendar = me.getCalendarById(calendarID);

		// convert dates
		Date d_start = null;
		Date d_end = null;

		try {
			d_start = dateFormat.parse(start);
			d_end = dateFormat.parse(end);
		} catch (Exception e) {
			message = "INVALID INPUT: PLEASE TRY AGAIN!";
			addEvent(calendarID, name, s_date, dday, mmonth, yyear, message);
		}
		boolean repeated = is_repeated.equals("0") ? false : true;
		int intervall = Integer.parseInt(is_repeated);
		Event e = new Event(me, d_start, d_end, name, visibility, repeated,
				intervall);

		calendar.addEvent(e);
		showTest(calendarID, me.name, calendar.getName(), s_date, dday, mmonth,
				yyear, message);
	}

	public static void saveEditedEvent(@Required long eventID,
			@Required long calendarID, @Required String name,
			@Required String start, @Required String end,
			Visibility visibility, String is_repeated, String s_date, int dday,
			int mmonth, int yyear) {

		User me = Database.users.get(Security.connected());
		Calendar calendar = me.getCalendarById(calendarID);

		// covert dates

		Date d_start = null;
		Date d_end = null;
		boolean repeated = is_repeated.equals("0") ? false : true;
		int intervall = Integer.parseInt(is_repeated);
		Event event = calendar.getEventById(eventID);
		if (repeated) {
			calendar.addToRepeated(event);
		}

		try {
			d_start = dateFormat.parse(start);
			d_end = dateFormat.parse(end);
		} catch (Exception e) {
			message = "INVALID INPUT: PLEASE TRY AGAIN!";
			editEvent(eventID, calendarID, name, s_date, dday, mmonth, yyear,
					message);
		}

		event.edit(d_start, d_end, name, visibility, repeated, intervall);
		showTest(calendarID, me.name, calendar.getName(), s_date, dday, mmonth,
				yyear, message);
	}

	public static void editEvent(long eventID, long calendarID, String name,
			String s_date, int dday, int mmonth, int yyear, String message) {
		User me = Database.users.get(Security.connected());
		Calendar calendar = me.getCalendarById(calendarID);
		Event event = calendar.getEventById(eventID);
		render(me, calendar, event, calendarID, eventID, s_date, dday, mmonth,
				yyear, message);
	}

	public static void addEvent(long calendarID, String name, String s_date,
			int dday, int mmonth, int yyear, String message) {
		User me = Database.users.get(Security.connected());
		Calendar calendar = me.getCalendarById(calendarID);
		render(me, calendar, calendarID, s_date, dday, mmonth, yyear, message);
	}

	public static void removeEvent(long calendarID, long eventID,
			String s_date, int dday, int mmonth, int yyear) {
		User me = Database.users.get(Security.connected());
		Calendar calendar = me.getCalendarById(calendarID);
		System.out.println("event ID: " + eventID);
		calendar.removeEvent(eventID);
		showTest(calendarID, me.name, calendar.getName(), s_date, dday, mmonth,
				yyear, message);
	}

	public static void cancelRepEvent(long calendarID, long eventID,
			String s_date, int dday, int mmonth, int yyear) {
		User me = Database.users.get(Security.connected());
		Calendar calendar = me.getCalendarById(calendarID);
		Date cancelDate = calendar.getEventById(eventID).start;
		calendar.cancelRepeatingEventRepetitionFromDate(calendar
				.getEventById(eventID));
		showTest(calendarID, me.name, calendar.getName(), s_date, dday, mmonth,
				yyear, message);
	}

	public static void removeRepeatingEvents(long calendarID, long eventId,
			String s_date, int dday, int mmonth, int yyear) {
		User me = Database.users.get(Security.connected());
		Calendar calendar = me.getCalendarById(calendarID);
		Event event = calendar.getEventById(eventId);
		calendar.removeRepeatingEvents(event);
		showTest(calendarID, me.name, calendar.getName(), s_date, dday, mmonth,
				yyear, message);
	}

	public static void showTest(long calendarId, String username,
			String calendarName, String s_date, int dday, int mmonth,
			int yyear, String message) {

		User me = Database.users.get(Security.connected());
		User user = Database.users.get(username);
		Calendar calendar = user.getCalendarById(calendarId);

		LinkedList<Event> allVisibleEvents = user.getCalendarById(calendarId)
				.getEventsOfDay(dday, mmonth, yyear, me);
		Calendar calendars = user.getCalendarById(calendarId); // just for
																// hotfix -->
																// remove later
		LinkedList<Event> events = allVisibleEvents;

		Date d = null;

		// "today" is used for calculating the current day/year/month and
		// coloring it blue
		java.util.Calendar today = java.util.Calendar.getInstance();
		java.util.Calendar cal = java.util.Calendar.getInstance();

		Date date = null;

		try {
			date = dateFormat.parse(s_date);
		} catch (ParseException e) {
		}
		cal.setTime(date);
		cal.set(java.util.Calendar.DAY_OF_MONTH, 1);
		int bound = (((cal.get(java.util.Calendar.DAY_OF_WEEK) - 2) + 7) % 7);
		int bound2 = cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);

		int day = date.getDate() + 1;
		int month = date.getMonth() + 1;
		int year = date.getYear() + 1900;

		int dfyear = 0;
		if (date.getMonth() == 10) {
			dfyear++;
		}
		if (date.getMonth() == 11) {
			dfyear++;
		}

		String next = Integer.toString(day) + "/"
				+ Integer.toString((month + 1) % 12) + "/"
				+ Integer.toString(year + dfyear) + ", 12:00";
		String prev = Integer.toString(day) + "/"
				+ Integer.toString((month - 1)) + "/" + Integer.toString(year)
				+ ", 12:00";

		boolean faved = me.isCalendarObserved(calendarId);
		LinkedList<Calendar> observedCalendars = me.getObservedCalendars();
		LinkedList<Long> shownObservedCalendars = me
				.getShownObservedCalendars();
		System.out.println("Markierte beim Neuladen: "
				+ shownObservedCalendars.size());

		/*
		 * TODO
		 * 
		 * IDs von markierten, befreundeten Kalendern sind in
		 * shownObservedCalendars gespeichert. Die Events dieser Kalender müssen
		 * nun noch zu den angezeigten Events hinzugefügt werden. => werden in
		 * Calendar::hasEventOnDay und Calendar::getEventOnDay hinzugefügt,
		 * Werden hier gelöscht, damit sie nach dem unchecken der anderen
		 * Calendars nicht mehr angezeigt werden.
		 */
		PriorityQueue<Event> calEvents = calendar.getEvents();
		for (Calendar c : observedCalendars) {
			for (Event e : c.getEvents()) {
				if (e.owner != user && e.owner != me) {
					calEvents.remove(e);
				}
			}
		}

		render(me, date, cal, bound, bound2, calendar, user, prev, next,
				s_date, today, events, calendarName, calendars, calendarId,
				dday, mmonth, yyear, message, faved, observedCalendars,
				shownObservedCalendars);
	}

	/**
	 * Observe (or "follow") a certain calendar of a user.
	 */
	public static void addObserve(String username, long calendarId,
			String calendarName, String s_date, int dday, int mmonth,
			int yyear, String message) {
		User me = Database.users.get(Security.connected());
		User user = Database.users.get(username);

		// find calendar by ID
		Calendar cal = user.getCalendarById(calendarId);
		me.addObservedCalendar(cal);
		showTest(calendarId, username, calendarName, s_date, dday, mmonth,
				yyear, message);
	}

	public static void removeObserve(String username, long calendarId,
			String calendarName, String s_date, int dday, int mmonth,
			int yyear, String message) {
		User me = Database.users.get(Security.connected());
		User user = Database.users.get(username);

		// find calendar by ID
		Calendar cal = user.getCalendarById(calendarId);
		me.removeObservedCalendar(cal);
		showTest(calendarId, username, calendarName, s_date, dday, mmonth,
				yyear, message);
	}

	/**
	 * Changes which observed calendars are really shown.
	 * 
	 * @param calID
	 *            ID of the calendar to be removed / added to view
	 * @param chk
	 *            A boolean value, indicating if we're adding or removing a
	 *            observed calendar
	 */
	public static void changeObservedCalendars(@Required String username,
			@Required long calendarId, @Required String calendarName,
			@Required String s_date, @Required int dday, @Required int mmonth,
			@Required int yyear, @Required String message,
			@Required long calID, @Required boolean chk) {

		User user = Database.users.get(username);
		System.out.println("username = " + username + ", calID = " + calID
				+ ", chk = " + chk);

		if (chk == true) {
			user.addShownObservedCalendar(calID);
		} else {
			user.removeShownObservedCalendar(calID);
		}

		showTest(calendarId, user.name, calendarName, s_date, dday, mmonth,
				yyear, message);
	}
}