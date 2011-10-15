package controllers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import models.Calendar;
import models.Database;
import models.Event;
import models.User;
import play.data.validation.Required;
import play.mvc.Controller;
import play.mvc.With;

//so far: jeder user kann neue user erzeugen mit default password 123

@With(Secure.class)
public class Application extends Controller {

	static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm");

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

	public static void showEventsOfDay(long calendarId, String username,
			String calendarName, int day, int month, int year) {
		User me = Database.users.get(Security.connected());
		User user = Database.users.get(username);
		// Date d = new Date(1,1,1);
		// System.out.println(username);
		LinkedList<Event> allVisibleEvents = user.getCalendarById(calendarId)
				.getEventsOfDay(day, month, year);

		Calendar calendars = user.getCalendarById(calendarId);
		LinkedList<Event> events = allVisibleEvents;

		render(me, user, events, calendarName, calendars, calendarId, day,
				month, year);
	}

	public static void newUser(@Required String name) {
		User user;
		Event event;
		Date now = new Date();

		if (!name.isEmpty()) {
			// mache user mit default daten:
			user = new User(name, "123");
			event = new Event(now, now, "abc", true, false, 0);
			// user.calendar.
			user.getdefaultCalendar().addEvent(event);

			Database.addUser(user);
			// Data d = new Data(value);

			renderJSON(user);
		}
	}

	public static void creatEvent(@Required long calendarID,
			@Required String name, @Required String start,
			@Required String end, boolean is_visible, String is_repeated) {

		User me = Database.users.get(Security.connected());
		Calendar calendar = me.getCalendarById(calendarID);

		// covert dates

		Date d_start = null;
		Date d_end = null;

		try {
			d_start = dateFormat.parse(start);
			d_end = dateFormat.parse(end);
		} catch (Exception e) {
			d_start = new Date(1, 1, 1);
			d_end = new Date(1, 1, 1);
		}
		System.out.println(is_repeated);
		boolean repeated = is_repeated.equals("0") ? false : true;
		int intervall = Integer.parseInt(is_repeated);
		Event e = new Event(d_start, d_end, name, is_visible, repeated,
				intervall);

		calendar.addEvent(e);
		showEvents(calendarID, me.name, calendar.getName());
	}

	public static void saveEditedEvent(@Required long eventID,
			@Required long calendarID, @Required String name,
			@Required String start, @Required String end, boolean is_visible) {

		User me = Database.users.get(Security.connected());
		Calendar calendar = me.getCalendarById(calendarID);

		// covert dates

		Date d_start = null;
		Date d_end = null;

		try {
			d_start = dateFormat.parse(start);
			d_end = dateFormat.parse(end);
		} catch (Exception e) {
			d_start = new Date(1, 1, 1);
			d_end = new Date(1, 1, 1);
		}

		Event event = calendar.getEventById(eventID);
		event.edit(d_start, d_end, name, is_visible);
		showEvents(calendarID, me.name, calendar.getName());
	}

	public static void editEvent(long eventID, long calendarID, String name) {
		User me = Database.users.get(Security.connected());
		Calendar calendar = me.getCalendarById(calendarID);
		Event event = calendar.getEventById(eventID);
		render(me, calendar, event, calendarID, eventID);
	}

	public static void addEvent(long calendarID, String name) {
		User me = Database.users.get(Security.connected());
		Calendar calendar = me.getCalendarById(calendarID);
		render(me, calendar, calendarID);
	}

	public static void removeEvent(long calendarID, long eventID) {
		User me = Database.users.get(Security.connected());
		Calendar calendar = me.getCalendarById(calendarID);
		calendar.removeEvent(eventID);
		showEvents(calendarID, me.name, calendar.getName());
	}

	public static void showTest(long calendarId, String username,
			String calendarName, String s_date) {
		User me = Database.users.get(Security.connected());
		User user = Database.users.get(username);
		Calendar calendar = user.getCalendarById(calendarId);
		PriorityQueue<Event> eventsCopy = new PriorityQueue<Event>(
				calendar.getEvents());
		GregorianCalendar c = new java.util.GregorianCalendar();
		for (Event a : calendar.getEvents()) {
			if (a.isRepeatable()) {
				Event e = a;
				int newMonth = e.getStart().getMonth();
				int newYear = e.getStart().getYear();
				int intervall = e.getIntervall();

				int newStartDay = e.getStart().getDate();
				int newEndDay = e.getEnd().getDate();

				// daily and weekly events
				if (intervall == 1 || intervall == 7) {

					c.set(c.DAY_OF_MONTH, e.getStart().getMonth());
					int maxMonthDay = c.getActualMaximum(c.DAY_OF_MONTH);
					while (newStartDay + intervall < maxMonthDay) {
						newStartDay = e.getStart().getDate() + intervall;
						newEndDay = e.getEnd().getDate() + intervall;
						System.out.println("startDay: "
								+ e.getStart().getDate() + "/ new Start Day: "
								+ newStartDay);
						Date newstartDate = new Date(newYear, newMonth,
								newStartDay);
						Date newEndDate = new Date(newYear, newMonth, newEndDay);
						Event newEvent = new Event(newstartDate, newEndDate,
								a.name, a.is_visible, false, intervall);
						System.out.println("start: " + newEvent.start + "end: "
								+ newEvent.end);
						e = newEvent;
						if (!eventsCopy.contains(newEvent)) {
							eventsCopy.add(newEvent);
						}
					}
				}

				if (intervall == 30) {
					newStartDay = e.getStart().getDate();
					newEndDay = e.getEnd().getDate();
					System.out.println("startDay: " + e.getStart().getDate()
							+ "/ new Start Day: " + newStartDay);
					Date newstartDate = new Date(newYear, (newMonth + 1) % 12,
							newStartDay);
					Date newEndDate = new Date(newYear, (newMonth + 1) % 12,
							newEndDay);
					Event newEvent = new Event(newstartDate, newEndDate,
							a.name, a.is_visible, true, intervall);
					System.out.println("start: " + newEvent.start + "end: "
							+ newEvent.end);
					e = newEvent;
					eventsCopy.add(newEvent);
				}

			}
		}
		Calendar newCalendar = new Calendar(calendarName, user);
		calendar.setEvents(eventsCopy);

		// DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		Date d = null;
		// String date = "02/10/2011";

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

		// showEvents(long calendarId, String username, String calendarName)

		int day = date.getDate() + 1;
		int month = date.getMonth() + 1;
		int year = date.getYear() + 1900;

		String next = Integer.toString(day) + "/"
				+ Integer.toString((month + 1) % 12) + "/"
				+ Integer.toString(year);
		String prev = Integer.toString(day) + "/"
				+ Integer.toString((month - 1)) + "/" + Integer.toString(year);
		System.out.println("prev: " + prev + " next: " + next);
		render(me, date, cal, bound, bound2, calendar, newCalendar, user, prev,
				next, s_date);
	}

}