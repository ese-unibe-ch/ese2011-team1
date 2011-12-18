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
import play.mvc.Scope.Flash;
import play.mvc.With;
import enums.Interval;
import enums.Visibility;

@With(Secure.class)
public class Application extends Controller {

	final static DateTimeFormatter dateTimeInputFormatter = DateTimeFormat
			.forPattern("dd/MM/yyyy, HH:mm");
	final static DateTimeFormatter birthdayFormatter = DateTimeFormat
			.forPattern("dd/MM/yyyy");
	public static String message = null;

	public static void index(String username) {
		User me = Database.users.get(Security.connected());
		List<User> users = Database.getUserList();
		String s_activeDate = new DateTime().toString("dd/MM/yyyy, HH:mm");

		User user = Database.users.get(username);
		LinkedList<Calendar> calendars = me.getCalendars();

		render(users, me, s_activeDate, calendars, user);
	}

	public static void showMe(String s_activeDate) {
		User me = Database.users.get(Security.connected());
		List<User> users = Database.getUserList();
		Calendar defaultCalendar = me.getdefaultCalendar();
		LinkedList<Calendar> calendars = me.getCalendars();
		render(me, users, calendars, defaultCalendar, s_activeDate);
	}

	public static void showCalendarList(String username, String s_activeDate) {
		User me = Database.users.get(Security.connected());
		User user = Database.users.get(username);
		LinkedList<Calendar> calendars = null;
		if (me != null && user != null) {
			calendars = user.getCalendars();
		}
		render(me, user, calendars, s_activeDate);
	}

	public static void searchUserForAdding(String userName,
			String calendarOwner, long calendarId, long eventId,
			String s_activeDate) {
		User me = Database.users.get(Security.connected());

		if (userName.isEmpty())
			render(me, null);

		List<User> results = Database.searchUser(userName);
		DateTime activeDate = new DateTime();

		render(me, results, userName, calendarId, eventId, activeDate);
	}

	public static void searchUser(String userName) {
		User me = Database.users.get(Security.connected());

		if (userName.isEmpty())
			render(me, null);

		List<User> results = Database.searchUser(userName);

		render(me, results);
	}

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

	public static void deleteMyAccount() {
		User loggedUser = Database.users.get(Security.connected());
		Database.deleteUser(loggedUser.getName(), loggedUser.getPassword());

		try {
			Secure.logout();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public static void showProfile(String userName) {
		User me = Database.users.get(Security.connected());
		User user = Database.getUserByName(userName);
		Event birthday = user.getBirthday();
		String nickname = user.getNickname();
		String emailP = user.getPrivateEmailAddress();
		String emailB = user.getBusinessEmailAdress();
		String telP = user.getPrivatePhoneNumber();
		String telB = user.getBusinessPhoneNumber();
		String notes = user.getDescription();

		render(me, user, nickname, birthday, emailP, emailB, telP, telB, notes);
	}

	public static void showEditProfile() {
		User me = Database.users.get(Security.connected());

		String name = me.getName();
		String oldname = name;
		String nickname = me.getNickname();
		String password = me.getPassword();
		String birthday = me.getBirthday().getStart().toString("dd/MM/yyyy");
		boolean is_visible = me.isBirthdayPublic();

		// NEW
		String emailP = me.getPrivateEmailAddress();
		boolean is_emailP_visible = me.getIsPrivateEmailVisible();

		String emailB = me.getBusinessEmailAdress();
		boolean is_emailB_visible = me.getIsBusinessEmailVisible();

		String telP = me.getPrivatePhoneNumber();
		boolean is_telP_visible = me.getIsPrivatePhoneNumberVisible();

		String telB = me.getBusinessPhoneNumber();
		boolean is_telB_visible = me.getIsBusinessPhoneNumberVisible();

		String notes = me.getDescription();
		boolean is_note_visible = me.getIsDescriptionVisible();

		render(me, name, oldname, nickname, password, birthday, is_visible,
				emailP, is_emailP_visible, emailB, is_emailB_visible, telP,
				is_telP_visible, telB, is_telB_visible, notes, is_note_visible);
	}

	public static void editProfile(@Required String name, String oldname,
			@Required String password, @Required String confirmPW, @Required String birthday,
			@Required String nickname, @Required boolean is_visible,
			String emailP, boolean is_emailP_visible, String emailB,
			boolean is_emailB_visible, String telP, boolean is_telP_visible,
			String telB, boolean is_telB_visible, String notes,
			boolean is_note_visible) {

		User user = Database.users.get(Security.connected());

		if (!(name.equals(user.getName()))
				&& Database.userAlreadyRegistrated(name)) {
			flash.error("Username (" + name + ") already exists!");
			params.flash();
			validation.keep();
			showEditProfile();
			//NEW***
		} else if (!password.equals(confirmPW)) {
			params.flash();
			validation.keep();
			flash.error("Incorrect password confirmation!");
			showEditProfile();
			//***
		} else if (validation.hasErrors()) {
			params.flash();
			validation.keep();
			flash.error("All (*) fields required!");
			showEditProfile();
		} else {
			try {
				// User user = Database.getUserByName(oldname);
				User newUser = user;
				// Database.deleteUser(user.getName(), user.getPassword());

				DateTime birthdate = birthdayFormatter.parseDateTime(birthday);
				newUser.setName(name);
				newUser.setNickname(nickname);
				newUser.setPassword(password);
				newUser.setBirthdayDate(birthdate);
				newUser.setBirthdayPublic(is_visible);
				newUser.setPrivateEmailAddress(emailP);
				newUser.setIsPrivateEmailVisible(is_emailP_visible);
				newUser.setBusinessEmailAdress(emailB);
				newUser.setIsBusinessEmailVisible(is_emailB_visible);
				newUser.setPrivatePhoneNumber(telP);
				newUser.setIsPrivatePhoneNumberVisible(is_telP_visible);
				newUser.setbusinessPhoneNumber(telB);
				newUser.setIsBusinessPhoneNumberVisible(is_telB_visible);
				newUser.setDescription(notes);
				newUser.setIsDescriptionVisible(is_note_visible);

				// Database.addUser(newUser);

				// TODO delete old user
				Database.changeUserName(newUser, user.getName(),
						user.getPassword());

				index(name);
			} catch (Exception e) {
				params.flash();
				validation.keep();
				flash.error("Invalid date format");
				showEditProfile();
			}

			// Database.deleteUser(user.getName(), user.getPassword());
		}
	}

	/**
	 * 
	 * Creates a new event of the right subclass (PointEvent or RepeatingEvent)
	 * and adds them to the calendar of the user.
	 * 
	 * @param calendarId
	 * @param name
	 * @param start
	 * @param end
	 * @param visibility
	 * @param is_repeated
	 * @param description
	 * @param s_activeDate
	 * @param isOpen
	 */
	public static void createEvent(@Required long calendarId,
			@Required String name, @Required String start,
			@Required String end, Visibility visibility, Interval interval,
			String description, String s_activeDate, boolean isOpen, boolean forceCreate) {

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
			event.setBaseId(event.getId()); // nicht notwendig

			event.generateNextEvents(event.getStart());
		}

		// Event e = new Event(me, d_start, d_end, name, visibility, repeated,
		// intervall, calendarId, is_open);
		event.editDescription(description);
		if (isOpen) {
			event.setOpen();
			event.addUserToAttending(me);
		}

		calendar.generateAllNextEvents(d_start);
		
		// Must be commented out until next week, this is a requirement for next
		// week
		// TODO: Add flash notice instead of message, ask customer if Events can
		// still be created/edited or redirect to create page?
		if (!forceCreate) {
			if (event.isOverlappingWithOtherEvent()) {
				flash.error("Warning: This event overlaps an existing Event. Do you want to proceed?");
				params.flash();
				validation.keep();
				flash.put("overlapping", "overlapping");
				addEditEvent(-1, calendarId, name, s_activeDate, message);
			}
		}

		calendar.addEvent(event);
		showCalendar(calendarId, me.getName(), start, d_start.getDayOfMonth(),
				message);
	}

	public static void saveEditedEvent(@Required long eventId,
			@Required long calendarId, @Required String name,
			@Required String start, @Required String end,
			Visibility visibility, Interval interval, String description,
			String s_activeDate, boolean isOpen) {

		User me = Database.users.get(Security.connected());
		Calendar calendar = me.getCalendarById(calendarId);

		// convert dates
		DateTime d_start = null;
		DateTime d_end = null;
		try {
			d_start = dateTimeInputFormatter.parseDateTime(start);
			d_end = dateTimeInputFormatter.parseDateTime(end);
		} catch (Exception e) {
			message = "INVALID INPUT: PLEASE TRY AGAIN!";
			addEditEvent(eventId, calendarId, name, s_activeDate, message);
		}

		boolean repeated = interval != Interval.NONE;
		Event event = calendar.getEventById(eventId);

		// for(Event head : calendar.getEventHeads())
		// calendar.PrintHeadAndHisTail(head);

		event.editDescription(description);

		event.edit(name, d_start, d_end, visibility, interval, d_start,
				d_start, description);

		//
		// if (repeated && !event.wasPreviouslyRepeating) {
		// event.wasPreviouslyRepeating = true;
		// calendar.addToRepeated(event);
		// }
		//
		//
		// event.edit(d_start, d_end, name, visibility, repeated, interval);

		/*
		 * if(!repeated){
		 * 
		 * ((PointEvent) event).edit(name, d_start, d_end, visibility); }else{
		 * // TODO here wo do have bugs... if(event instanceof IntervalEvent){
		 * ((IntervalEvent) event).edit(name, d_start, d_end, visibility,
		 * interval, ((IntervalEvent) event).getFrom(), ((IntervalEvent)
		 * event).getTo()); }else{ // TODO irgendwas ist in showCalendar.html
		 * nicht iO, denn für den 1. Tag zeigt es nach edit den event doppelt an
		 * in liste. // event ist nicht mehrfach gespeichert, habe das
		 * verifiziert - siehe print statements below // auch möglich, dass der
		 * bug in der methode showCalendar in der klasse application ist und die
		 * liste mit den daten, // welche dargestellt werden sollen, falsch
		 * berechnet wir... calendar.removeEvent(event.getBaseId());
		 * RepeatingEvent newEvent = new RepeatingEvent((PointEvent)event,
		 * interval); calendar.addEvent(newEvent);
		 * newEvent.editDescription(description);
		 * calendar.generateNextEvents(newEvent, newEvent.getStart()); } }
		 */

		if (!isOpen) {
			event.setClosed();
		} else if (isOpen) {
			event.setOpen();
			event.addUserToAttending(me);
		}

		// Must be commented out until next week, this is a requirement for next
		// week
		// TODO: Add flash notice instead of message, ask customer if Events can
		// still be created/edited or redirect to create page?
//		if (event.isOverlappingWithOtherEvent()) {
//			message = "OVERLAPPING WITH OTHER EVENT! Overlapping events:\n"
//					+ event.getOverlappingEvents();
//			addEditEvent(eventId, calendarId, name, s_activeDate, message);
//		}

		DateTime activeDate = dateTimeInputFormatter
				.parseDateTime(s_activeDate);
		calendar.generateAllNextEvents(activeDate);

		showCalendar(calendarId, me.getName(), s_activeDate,
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

	// public static void editEvent(long eventId, long calendarId, String name,
	// String s_activeDate, String message) {
	// User me = Database.users.get(Security.connected());
	// Calendar calendar = me.getCalendarById(calendarId);
	// Event event = calendar.getEventById(eventID);
	// render(me, calendar, event, calendarId, eventId, s_activeDate, message);
	// }
	//
	// public static void addEvent(long calendarId, String name,
	// String s_activeDate, String message) {
	// User me = Database.users.get(Security.connected());
	// Calendar calendar = me.getCalendarById(calendarId);
	// DateTime activeDate = dateTimeInputFormatter
	// .parseDateTime(s_activeDate);
	// render(me, calendar, calendarId, activeDate, message);
	// }

	public static void removeEvent(long calendarId, long eventId,
			String s_activeDate) {
		User me = Database.users.get(Security.connected());

		Calendar calendar = me.getCalendarById(calendarId);
		calendar.removeEvent(eventId);
		DateTime activeDate = dateTimeInputFormatter
				.parseDateTime(s_activeDate);
		showCalendar(calendarId, me.getName(), s_activeDate,
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
		showCalendar(calendarId, me.getName(), s_activeDate,
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
		showCalendar(calendarId, me.getName(), s_activeDate,
				activeDate.getDayOfMonth(), message);
	}

	public static void showCalendar(long calendarId, String username,
			String s_activeDate, int counter, String message) {
		message = null;
		User me = Database.users.get(Security.connected());
		User user = Database.users.get(username);
		Calendar calendar = user.getCalendarById(calendarId);
		assert (calendar != null) : "Calendar must not be null";

		// get active date
		DateTime activeDate = null;
		DateTime today = new DateTime();
		try {
			activeDate = dateTimeInputFormatter.parseDateTime(s_activeDate);
		} catch (Exception e) {
			activeDate = today;
		}
		// set day of active date
		try {
			activeDate = activeDate.withDayOfMonth(counter);
		} catch (Exception e) {
			activeDate.withDayOfMonth(activeDate.getDayOfMonth());
		}
		assert (activeDate != null) : "must not be null!";

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

		if (chk == true) {
			user.addShownObservedCalendar(calId);
		} else {
			user.removeShownObservedCalendar(calId);
		}

		DateTime activeDate = dateTimeInputFormatter
				.parseDateTime(s_activeDate);

		showCalendar(calendarId, user.getName(), s_activeDate,
				activeDate.getDayOfMonth(), message);
	}

	public static void addUserToEvent(String userToAddStr, long calendarId,
			long eventId, String s_activeDate) {
		User me = Database.getUserByName(Security.connected());
		User userToAdd = Database.getUserByName(userToAddStr);
		Calendar cal = me.getCalendarById(calendarId);
		assert (cal != null);
		DateTime activeDate = dateTimeInputFormatter
				.parseDateTime(s_activeDate);
		Event event = null;

//		System.out
//				.println("====================================================================");
//		System.out.println("Trying to fetch the calendar " + calendarId
//				+ " from User " + me.getName());
//		System.out
//				.println("====================================================================");

		System.out.println("cal:" + cal.getName());

		if (!cal.getAllVisibleEventsOfDate(activeDate.getDayOfMonth(),
				activeDate.getMonthOfYear(), activeDate.getYear(), me).equals(
				null)) {
			for (Event e : cal.getAllVisibleEventsOfDate(
					activeDate.getDayOfMonth(), activeDate.getMonthOfYear(),
					activeDate.getYear(), me)) {
				if (e.getId() == eventId) {
					event = e;
				}
			}
		}

		assert (event != null);
		event.sendInvitationRequest(userToAdd); //TODO rename, check
		showCalendar(calendarId, me.getName(), s_activeDate,
				activeDate.getDayOfMonth(), null);
	}

	public static void removeUserFromEvent(String userToRemoveStr, String eventOwner, long calendarId,
			long eventId, String s_activeDate) {
		System.out.println("Called removeUserFromEvent with eventOwner:"+eventOwner);
		System.out.println("calendarId: "+calendarId+", eventId: "+eventId);
		System.out.println("User to remove:"+userToRemoveStr);
		
		User me = Database.getUserByName(Security.connected());
		User userToRemove = Database.getUserByName(userToRemoveStr);
		Calendar cal = me.getCalendarById(calendarId);
		DateTime activeDate = dateTimeInputFormatter
				.parseDateTime(s_activeDate);
		Event event = null;
		for (Event e : cal.getAllVisibleEventsOfDate(
				activeDate.getDayOfMonth(), activeDate.getMonthOfYear(),
				activeDate.getYear(), me)) {
			if (e.getId() == eventId) {
				event = e;
			}
		}
		assert (event != null);
		event.removeUserFromAttending(userToRemove);
		showCalendar(calendarId, me.getName(), s_activeDate,
				activeDate.getDayOfMonth(), null);
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

		index(userName);
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
		index(me.getName());
	}

}