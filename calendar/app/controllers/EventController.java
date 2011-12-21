package controllers;

import java.util.LinkedList;
import java.util.List;

import models.Calendar;
import models.Database;
import models.Event;
import models.PointEvent;
import models.RepeatingEvent;
import models.User;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import play.data.validation.Required;
import play.mvc.Controller;
import play.mvc.With;
import enums.Interval;
import enums.Visibility;

@With(Secure.class)
public class EventController extends Controller {

	final static DateTimeFormatter dateTimeInputFormatter = DateTimeFormat
			.forPattern("yyyy-MM-dd-HH-mm");
	final static DateTimeFormatter birthdayFormatter = DateTimeFormat
			.forPattern("yyyy-MM-dd");

	public static String message = null;

	public static void searchEvent(String eventName, String calendarOwner,
			String displayedCalendarId, String s_activeDate) {

		if (eventName.isEmpty())
			render();

		User curiousUser = Database.users.get(Security.connected());
		Calendar displayedCalendar = Database.getUserByName(calendarOwner)
				.getCalendarById(Long.parseLong(displayedCalendarId));
		DateTime activeDate = null;

		activeDate = new DateTime();

		List<Event> results = displayedCalendar.searchEvent(eventName,
				curiousUser, activeDate);
		DateTime today = new DateTime();
		render(results, curiousUser, today);
	}

	public static void showEvents(long calendarId, String username,
			String calendarName) {
		User me = Database.users.get(Security.connected());
		User user = Database.users.get(username);
		Calendar calendars = user.getCalendarById(calendarId);
		LinkedList<Event> events = new LinkedList<Event>();
		render(me, user, events, calendarName, calendars, calendarId);
	}

	/**
	 * 
	 * Creates a new event of the right subclass (PointEvent or RepeatingEvent)
	 * and adds them to the calendar of the user.
	 * 
	 */
	public static void createEvent(@Required long calendarId,
			@Required String name, @Required String start,
			@Required String end, Visibility visibility, Interval interval,
			String description, String s_activeDate, boolean isOpen,
			boolean forceCreate) {

		User me = Database.users.get(Security.connected());
		Calendar calendar = me.getCalendarById(calendarId);

		// convert dates
		DateTime d_start = null;
		DateTime d_end = null;

		if (name.length() < 1) {
			flash.error("Invalid Input: Please enter a name.");
			params.flash();
			validation.keep();
			addEditEvent(-1, calendarId, name, s_activeDate, message);
		}

		try {
			d_start = dateTimeInputFormatter.parseDateTime(start);
			d_end = dateTimeInputFormatter.parseDateTime(end);
		} catch (Exception e) {
			flash.error("Invalid Input: Please try again.");
			params.flash();
			validation.keep();
			addEditEvent(-1, calendarId, name, s_activeDate, message);
		}

		if (d_end.isBefore(d_start)) {
			flash.error("Invalid Input: Start date must be before end date.");
			params.flash();
			validation.keep();
			addEditEvent(-1, calendarId, name, s_activeDate, message);
		}

		boolean repeated = interval != Interval.NONE;

		Event event;
		if (!repeated) {
			event = new PointEvent(name, d_start, d_end, visibility, calendar);

		} else {
			if (!d_start.plusDays(interval.getDays()).isAfter(d_end)) {
				flash.error("Invalid Input: Repeating Event overlaps self on next occurence.");
				params.flash();
				validation.keep();
				addEditEvent(-1, calendarId, name, s_activeDate, message);
			}
			event = new RepeatingEvent(name, d_start, d_end, visibility,
					calendar, interval);
			event.setOriginId(event.getId());
			event.setBaseId(event.getId());

			event.generateNextEvents(event.getStart());
		}

		event.editDescription(description);
		if (isOpen) {
			event.setOpen();
			event.addUserToAttending(me);
		}

		calendar.generateAllNextEvents(d_start);

		if (!forceCreate) {
			if (event.isOverlappingWithOtherEvent()) {
				flash.error("Warning: This event overlaps with an existing Event. If you want to proceed, please verify your input and click 'proceed'.");
				params.flash();
				validation.keep();
				flash.put("overlapping", "overlapping");
				// List<Event> overlappingEvents = event.getOverlappingEvents();
				addEditEvent(-1, calendarId, name, start, message);
			}
		}

		calendar.addEvent(event);

		CalendarController.showCalendar(calendarId, me.getName(), start,
				d_start.getDayOfMonth(), message);
	}

	public static void saveEditedEvent(@Required long eventId,
			@Required long calendarId, @Required String name,
			@Required String start, @Required String end,
			Visibility visibility, Interval interval, String description,
			String s_activeDate, boolean isOpen, boolean forceCreate) {

		User me = Database.users.get(Security.connected());
		Calendar calendar = me.getCalendarById(calendarId);

		// convert dates
		DateTime d_start = null;
		DateTime d_end = null;
		try {
			d_start = dateTimeInputFormatter.parseDateTime(start);
			d_end = dateTimeInputFormatter.parseDateTime(end);
		} catch (Exception e) {
			flash.error("Invalid Input: Please try again.");
			params.flash();
			validation.keep();
			addEditEvent(eventId, calendarId, name, s_activeDate, message);
		}

		boolean repeated = interval != Interval.NONE;
		Event event = calendar.getEventById(eventId);

		if (!forceCreate) {
			if (event.isOverlappingWithOtherEvent()) {
				flash.error("Warning: This event overlaps with an existing Event."
						+ " If you want to proceed, please verify your input and click 'proceed'.");
				params.flash();
				validation.keep();
				flash.put("overlapping", "overlapping");
				addEditEvent(eventId, calendarId, name, start, message);
			}
		}

		event.editDescription(description);

		event.edit(name, d_start, d_end, visibility, interval, d_start,
				d_start, description);

		if (!isOpen) {
			event.setClosed();
		} else if (isOpen) {
			event.setOpen();
			event.addUserToAttending(me);
		}

		DateTime activeDate = dateTimeInputFormatter
				.parseDateTime(s_activeDate);
		calendar.generateAllNextEvents(activeDate);

		CalendarController.showCalendar(calendarId, me.getName(), s_activeDate,
				d_start.getDayOfMonth(), message);
	}

	/**
	 * Add a new or edit a given event.
	 * 
	 * @param eventId
	 *            Id of the event, -1 if not given (= adding an event)
	 * @param calendarId
	 *            The id of the calendar which is viewed
	 * @param name
	 *            The name of event
	 * @param s_activeDate
	 *            The given start date.
	 * @param message
	 *            A message for the user in case an error occurs.
	 */
	public static void addEditEvent(long eventId, long calendarId, String name,
			String s_activeDate, String message) {
		User me = Database.users.get(Security.connected());
		Calendar calendar = me.getCalendarById(calendarId);
		boolean editingEvent = false;
		Event event = null;

		if (eventId >= 0) {
			event = calendar.getEventById(eventId);
			editingEvent = true;
		}

		DateTime activeDate = dateTimeInputFormatter
				.parseDateTime(s_activeDate);

		render(me, calendar, event, calendarId, eventId, activeDate, message,
				editingEvent);
	}

	public static void removeEvent(long calendarId, long eventId,
			String s_activeDate) {
		User me = Database.users.get(Security.connected());

		Calendar calendar = me.getCalendarById(calendarId);
		calendar.removeEvent(eventId);
		DateTime activeDate = dateTimeInputFormatter
				.parseDateTime(s_activeDate);
		CalendarController.showCalendar(calendarId, me.getName(), s_activeDate,
				activeDate.getDayOfMonth(), message);
	}

	public static void cancelEventRepetition(long calendarId, long eventId,
			String s_activeDate) {
		User me = Database.users.get(Security.connected());
		Calendar calendar = me.getCalendarById(calendarId);
		calendar.cancelRepeatingEventRepetitionFromDate(calendar
				.getEventById(eventId));
		DateTime activeDate = dateTimeInputFormatter
				.parseDateTime(s_activeDate);
		CalendarController.showCalendar(calendarId, me.getName(), s_activeDate,
				activeDate.getDayOfMonth(), message);
	}

	public static void removeRepeatingEvents(long calendarId, long eventId,
			String s_activeDate) {
		User me = Database.users.get(Security.connected());
		Calendar calendar = me.getCalendarById(calendarId);
		Event event = calendar.getEventById(eventId);
		calendar.removeSeriesOfRepeatingEvents(event);
		DateTime activeDate = dateTimeInputFormatter
				.parseDateTime(s_activeDate);
		CalendarController.showCalendar(calendarId, me.getName(), s_activeDate,
				activeDate.getDayOfMonth(), message);
	}

	/**
	 * Add myself to event
	 * 
	 * @param calendarOwnerStr
	 *            watching
	 * @param eventOwnerStr
	 *            the user to add
	 */
	public static void addMyselfToEvent(String calendarOwnerStr,
			String eventOwnerStr, long eventCalendarId, long calendarId,
			long eventId, String s_activeDate) {
		DateTime activeDate = dateTimeInputFormatter
				.parseDateTime(s_activeDate);
		Event event = null;
		User me = Database.getUserByName(Security.connected());
		User eventOwner = Database.getUserByName(eventOwnerStr);

		Calendar cal = eventOwner.getCalendarById(eventCalendarId);

		for (Event e : cal.getAllVisibleEventsOfDate(
				activeDate.getDayOfMonth(), activeDate.getMonthOfYear(),
				activeDate.getYear(), eventOwner)) {
			if (e.getId() == eventId) {
				event = e;
			}
		}

		event.addUserToAttending(me);
		String user = calendarOwnerStr;

		CalendarController.showCalendar(calendarId, user, s_activeDate,
				activeDate.getDayOfMonth(), null);
	}

	/**
	 * Adds an user to a event. Be careful to not mess up with the different
	 * users involved:
	 * 
	 * @param userWatchingStr
	 *            the user clicking on 'add',
	 * @param userToAddStr
	 *            the user to add
	 * @param calendarOwnerStr
	 *            the owner of the calendar of the event
	 */
	public static void addUserToEvent(String userWatchingStr,
			String userToAddStr, String calendarOwnerStr, long eventCalendarId,
			long calendarId, long eventId, String s_eventDate) {
		DateTime activeDate = dateTimeInputFormatter.parseDateTime(s_eventDate);
		Event event = null;
		User me = Database.getUserByName(Security.connected());
		User userToAdd = Database.getUserByName(userToAddStr);
		User calendarOwner = Database.getUserByName(calendarOwnerStr);
		Calendar cal = calendarOwner.getCalendarById(eventCalendarId);

		for (Event e : cal.getAllVisibleEventsOfDate(
				activeDate.getDayOfMonth(), activeDate.getMonthOfYear(),
				activeDate.getYear(), me)) {
			if (e.getId() == eventId) {
				event = e;
			}
		}

		String s_activeDate = s_eventDate;

		if (event.isOpen()) {
			if (userToAdd == event.getOwner())
				event.addUserToAttending(userToAdd);
			else {
				if (me == userToAdd)
					event.addUserToAttending(userToAdd);
				else
					event.sendInvitationRequest(userToAdd);
			}
		} else
			event.sendInvitationRequest(userToAdd);

		String user = calendarOwnerStr;

		CalendarController.showCalendar(calendarId, user, s_activeDate,
				activeDate.getDayOfMonth(), null);
	}

	public static void removeUserFromEvent(String userToRemoveStr,
			String eventOwnerStr, long calendarId, long eventId,
			String s_activeDate) {

		User me = Database.getUserByName(Security.connected());
		User userToRemove = Database.getUserByName(userToRemoveStr);
		User eventOwner = Database.getUserByName(eventOwnerStr);
		Calendar cal = eventOwner.getCalendarById(calendarId);
		DateTime activeDate = dateTimeInputFormatter
				.parseDateTime(s_activeDate);
		Event event = null;
		for (Event e : cal.getAllVisibleEventsOfDate(
				activeDate.getDayOfMonth(), activeDate.getMonthOfYear(),
				activeDate.getYear(), me)) {
			if (e.getId() == eventId) {
				event = e;
				break;
			}
		}

		event.removeUserFromAttending(userToRemove);
		CalendarController.showCalendar(calendarId, eventOwner.getName(),
				s_activeDate, activeDate.getDayOfMonth(), null);
	}

}
