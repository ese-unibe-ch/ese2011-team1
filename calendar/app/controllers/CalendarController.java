package controllers;

import java.util.LinkedList;

import models.Calendar;
import models.Database;
import models.Event;
import models.User;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import play.data.validation.Required;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class CalendarController extends Controller {
	
	final static DateTimeFormatter dateTimeInputFormatter = DateTimeFormat
			.forPattern("yyyy-MM-dd-HH-mm");
	final static DateTimeFormatter birthdayFormatter = DateTimeFormat
			.forPattern("yyyy-MM-dd");
			
	public static void showCalendarList(String username, String s_activeDate) {
		User me = Database.users.get(Security.connected());
		User user = Database.users.get(username);
		LinkedList<Calendar> calendars = null;
		if (me != null && user != null) calendars = user.getCalendars();
		render(me, user, calendars, s_activeDate);
	}
	
	public static void showCalendar(long calendarId, String username,
			String activeDateStr, int counter, String message) {
		message = null;
		User me = Database.users.get(Security.connected());
		User user = Database.users.get(username);
		Calendar calendar = user.getCalendarById(calendarId);

		// get active date
		DateTime activeDate = null;
		DateTime today = new DateTime();
		
		try {
			activeDate = dateTimeInputFormatter.parseDateTime(activeDateStr);
		} catch (Exception e) {
			activeDate = today;
		}
		
		// set day of active date
		try {
			activeDate = activeDate.withDayOfMonth(counter);
		} catch (Exception e) {
			activeDate.withDayOfMonth(activeDate.getDayOfMonth());
		}

		LinkedList<Event> eventsOfDate = calendar.getAllVisibleEventsOfDate(
				activeDate.getDayOfMonth(), activeDate.getMonthOfYear(),
				activeDate.getYear(), me);

		// get bounds for calendar construction
		int bound = activeDate.withDayOfMonth(1).getDayOfWeek();
		int bound2 = activeDate.dayOfMonth().getMaximumValue();

		DateTime nextMonth = activeDate.plusMonths(1);
		DateTime prevMonth = activeDate.minusMonths(1);

		boolean faved = me.isCalendarObserved(calendarId);

		LinkedList<Calendar> observedCalendars = me.getObservedCalendars();
		LinkedList<Long> shownObservedCalendars = me
				.getShownObservedCalendars();

		calendar.generateAllNextEvents(activeDate);

		render(me, user, calendar, bound, bound2, prevMonth, nextMonth,
				activeDate, today, eventsOfDate, message, faved,
				observedCalendars, shownObservedCalendars);
	}

	/**
	 * Observe (or "follow") a certain calendar of a user.
	 */
	public static void addObserve(String username, long calendarId,
			String s_activeDate, String message) {
		User me = Database.users.get(Security.connected());
		User user = Database.users.get(username);

		// find calendar by Id
		Calendar cal = user.getCalendarById(calendarId);
		me.addObservedCalendar(cal);
		DateTime activeDate = dateTimeInputFormatter
				.parseDateTime(s_activeDate);
		showCalendar(calendarId, username, s_activeDate,
				activeDate.getDayOfMonth(), message);
	}

	public static void removeObserve(String username, long calendarId,
			String s_activeDate, String message) {
		User me = Database.users.get(Security.connected());
		User user = Database.users.get(username);

		// find calendar by ID
		Calendar cal = user.getCalendarById(calendarId);
		me.removeObservedCalendar(cal);
		DateTime activeDate = dateTimeInputFormatter
				.parseDateTime(s_activeDate);
		showCalendar(calendarId, username, s_activeDate,
				activeDate.getDayOfMonth(), message);
	}

	/**
	 * Changes which observed calendars are really shown.
	 * 
	 * @param calId
	 *            Id of the calendar to be removed / added to view
	 * @param chk
	 *            A boolean value, indicating if we're adding or removing a
	 *            observed calendar
	 */
	public static void changeObservedCalendars(@Required String username,
			@Required long calendarId, @Required String s_activeDate,
			@Required String message, @Required long calId,
			@Required boolean chk) {

		User user = Database.users.get(username);

		if (chk == true) user.addShownObservedCalendar(calId);
		else user.removeShownObservedCalendar(calId);
		
		DateTime activeDate = dateTimeInputFormatter
				.parseDateTime(s_activeDate);

		showCalendar(calendarId, user.getName(), s_activeDate,
				activeDate.getDayOfMonth(), message);
	}
	
	/**
	 * Creates a new calendar and adds it to the calendars of the given user.
	 * 
	 * @param userName
	 *            the user which wants a new calendar
	 * @param calenderName
	 *            the name of the new calendar
	 */
	public static void createCalendar(@Required String userName,
			@Required String calendarName) {
		User me = Database.users.get(Security.connected());
		Calendar cal = new Calendar(calendarName, me);
		me.addCalendar(cal);
		Application.index(userName);
	}

	/**
	 * Removes a calendar from the list of inherited calendars from the owner.
	 * 
	 * @param calendarId
	 *            the calendar to delete
	 * @param userName
	 *            the user who wants to delete
	 */
	public static void deleteCalendar(long calendarId) {
		User me = Database.users.get(Security.connected());
		me.deleteCalendar(calendarId);
		Application.index(me.getName());
	}
}

