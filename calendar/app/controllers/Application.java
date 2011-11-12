package controllers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import models.Calendar;
import models.Calendar;
import models.Database;
import models.Event;
import models.Event.Visibility;
import models.IntervalEvent;
import models.PointEvent;
import models.RepeatingEvent;
import models.User;
import play.data.validation.Required;
import play.mvc.Controller;
import play.mvc.With;

//so far: jeder user kann neue user erzeugen mit default password 123

@With(Secure.class)
public class Application extends Controller {

	final static DateTimeFormatter dateTimeInputFormatter = DateTimeFormat
			.forPattern("dd/MM/yyyy, HH:mm");
	final static DateTimeFormatter birthdayFormatter = DateTimeFormat
			.forPattern("dd/MM/yyyy");
	public static String message = null;

	public static void index() {
		User me = Database.users.get(Security.connected());
		List<User> users = Database.getUserList();
		String s_activeDate = new DateTime().toString("dd/MM/yyyy, HH:mm");
		// todo: remove ourself from list
		render(users, me, s_activeDate);
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

	public static void searchForUser(String userName, String s_activeDate) {
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
				showMe(s_activeDate);

			showCalendarList(userName, s_activeDate);
		}
	}

	public static void showEvents(long calendarId, String username,
			String calendarName) {
		User me = Database.users.get(Security.connected());
		User user = Database.users.get(username);
		DateTime d = new DateTime();
		Iterator allVisibleEvents = user.getCalendarById(calendarId)
				.getEventList(d, me);
		Calendar calendars = user.getCalendarById(calendarId);
		LinkedList<Event> events = new LinkedList<Event>();

		while (allVisibleEvents.hasNext()) {
			events.add((Event) allVisibleEvents.next());
		}

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
		User user = Database.getUserByName(userName);
		Event birthday = user.getBirthday();
		String nickname = user.getNickname();
		String emailP = user.getEmailP();
		String emailB = user.getEmailB();
		String telP = user.getTelP();
		String telB = user.getTelB();
		String notes = user.getNotes();

		render(user, nickname, birthday, emailP, emailB, telP, telB, notes);
	}

	public static void showEditProfile() {
		User user = Database.users.get(Security.connected());

		String name = user.getName();
		String oldname = name;
		String nickname = user.getNickname();
		String password = user.getPassword();
		String birthday = null; // user.getBirthday().start.toString("dd/MM/yyyy");
		boolean is_visible = user.isBirthdayPublic();

		// NEW
		String emailP = user.getEmailP();
		boolean is_emailP_visible = user.getEmailPVis();

		String emailB = user.getEmailB();
		boolean is_emailB_visible = user.getEmailBVis();

		String telP = user.getTelP();
		boolean is_telP_visible = user.getTelPVis();

		String telB = user.getTelB();
		boolean is_telB_visible = user.getTelBVis();

		String notes = user.getNotes();
		boolean is_note_visible = user.getNotesVis();

		render(name, oldname, nickname, password, birthday, is_visible, emailP,
				is_emailP_visible, emailB, is_emailB_visible, telP,
				is_telP_visible, telB, is_telB_visible, notes, is_note_visible);
	}

	public static void editProfile(@Required String name, String oldname,
			@Required String password, @Required String birthday,
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

				// DateTime birthdate =
				// birthdayFormatter.parseDateTime(birthday);
				// newUser.setBirthdayDate(birthdate);
				// newUser.setBirthdayPublic(is_visible);
				newUser.setName(name);
				newUser.setNickname(nickname);
				newUser.setPassword(password);
				// newUser.setBirthdayPublic(is_visible);
				newUser.setEmailP(emailP);
				newUser.setEmailPVis(is_emailP_visible);
				newUser.setEmailB(emailB);
				newUser.setEmailBVis(is_emailB_visible);
				newUser.setTelP(telP);
				newUser.setTelPVis(is_telP_visible);
				newUser.setTelB(telB);
				newUser.setTelBVis(is_telB_visible);
				newUser.setNotes(notes);
				newUser.setNotesVis(is_note_visible);

				Database.addUser(newUser);

				// TODO delete old user
				// Database.changeUserName(user); //TODO does not work properly
				// jet!

				index();
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
			@Required String end, Visibility visibility, String is_repeated,
			String description, String s_activeDate, boolean isOpen) {

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
			addEditEvent(-1, calendarId, name, s_activeDate, message);
		}
		if (d_end.isBefore(d_start)) {
			message = "INVALID INPUT: START DATE MUST BE BEFORE END DATE!";
			addEditEvent(-1, calendarId, name, s_activeDate, message);
		}

		boolean repeated = is_repeated.equals("0") ? false : true;
		int intervall = Integer.parseInt(is_repeated);

		Event e;
		if (!repeated) {
			e = new PointEvent(name, d_start, d_end, visibility, calendar);
		} 
		else {
			e = new RepeatingEvent(name, d_start, d_end, visibility, calendar,
					intervall);
			e.generateNextEvents(e.getStart());
		}

//		Event e = new Event(me, d_start, d_end, name, visibility, repeated,
//				intervall, calendarId, is_open);
		e.editDescription(description);
		if (isOpen) {
			e.setOpen();
			e.addUserToAttending(me);
		}
		System.out.println("Creating and adding "+e+" to calendar: "+calendar);
		
		calendar.addEvent(e);
		showCalendar(calendarId, me.name, start, d_start.getDayOfMonth(),
				message);
	}

	public static void saveEditedEvent(@Required long eventId,
			@Required long calendarId, @Required String name,
			@Required String start, @Required String end,
			Visibility visibility, String is_repeated, String description,
			String s_activeDate) {

		User me = Database.users.get(Security.connected());
		Calendar calendar = me.getCalendarById(calendarId);
		//
		// // covert dates
		DateTime d_start = null;
		DateTime d_end = null;
		try {
			d_start = dateTimeInputFormatter.parseDateTime(start);
			d_end = dateTimeInputFormatter.parseDateTime(end);
		} catch (Exception e) {
			message = "INVALID INPUT: PLEASE TRY AGAIN!";
			addEditEvent(eventId, calendarId, name, s_activeDate, message);
		}
	
		boolean repeated = is_repeated.equals("0") ? false : true;
		int interval = Integer.parseInt(is_repeated);
		System.out.println("Event ID ::::: " +eventId); 
		Event event = calendar.getEventById(eventId);
		event.editDescription(description);
		//
		// if (repeated && !event.wasPreviouslyRepeating) {
		// event.wasPreviouslyRepeating = true;
		// calendar.addToRepeated(event);
		// }
		//
		//
	//	event.edit(d_start, d_end, name, visibility, repeated, interval);
		
		
		if(!repeated){
			((PointEvent) event).edit(name, d_start, d_end, visibility);
		}else{
			// TODO here wo do have bugs...
			if(event instanceof IntervalEvent){
				((IntervalEvent) event).edit(name, d_start, d_end, visibility, interval, ((IntervalEvent) event).getFrom(), ((IntervalEvent) event).getTo());
			}else{
				// TODO irgendwas ist in showCalendar.html nicht iO, denn für den 1. Tag zeigt es nach edit den event doppelt an in liste. 
				// event ist nicht mehrfach gespeichert, habe das verifiziert - siehe print statements below
				// auch möglich, dass der bug in der methode showCalendar in der klasse application ist und die liste mit den daten,
				// welche dargestellt werden sollen, falsch berechnet wir...
				calendar.removeEvent(event.getBaseId());
				System.out.println("should be null " + calendar.getEventById(event.getBaseId()));
				System.out.println(calendar.getHeadList());
				RepeatingEvent newEvent = new RepeatingEvent((PointEvent)event, interval);
				calendar.addEvent(newEvent);
				System.out.println(calendar.getHeadList());
				newEvent.editDescription(description);
				calendar.generateNextEvents(newEvent, newEvent.getStart());
				System.out.println("head "+ newEvent.getParsedStartDate()+ " next "+ newEvent.getNextReference().getParsedStartDate());
			}
		}
		
		showCalendar(calendarId, me.getName(), s_activeDate,
				d_start.getDayOfMonth(), message);
	}
	
	/**
	 * Add a new or edit a given event.
	 * 
	 * @param eventId Id of the event, -1 if not given (= adding an event)
	 * @param calendarId The id of the calendar which is viewed
	 * @param name The name of event
	 * @param s_activeDate The given start date.
	 * @param message A message for the user in case an error occurs.
	 */
	public static void addEditEvent(long eventId, long calendarId, String name,
			String s_activeDate, String message) {
		User me = Database.users.get(Security.connected());
		Calendar calendar = me.getCalendarById(calendarId);
		boolean editingEvent = false;
		Event event = null;
		
		System.out.println("eventId: "+eventId);
		
		if (eventId >= 0) {
			event = calendar.getEventById(eventId);
			editingEvent = true;
		}
		DateTime activeDate = dateTimeInputFormatter.parseDateTime(s_activeDate);
		
		render(me, calendar, event, calendarId, eventId, activeDate, message, editingEvent);
	}
	
//	public static void editEvent(long eventId, long calendarId, String name,
//		String s_activeDate, String message) {
//		User me = Database.users.get(Security.connected());
//		Calendar calendar = me.getCalendarById(calendarId);
//		Event event = calendar.getEventById(eventID);
//		render(me, calendar, event, calendarId, eventId, s_activeDate, message);
//	}
//
//	public static void addEvent(long calendarId, String name,
//			String s_activeDate, String message) {
//		User me = Database.users.get(Security.connected());
//		Calendar calendar = me.getCalendarById(calendarId);
//		DateTime activeDate = dateTimeInputFormatter
//				.parseDateTime(s_activeDate);
//		render(me, calendar, calendarId, activeDate, message);
//	}

	public static void removeEvent(long calendarId, long eventId,
			String s_activeDate) {
		User me = Database.users.get(Security.connected());
		Calendar calendar = me.getCalendarById(calendarId);
		System.out.print("we are going to remove this event: " + eventId);
		calendar.removeEvent(eventId);
		System.out.print("done");
		DateTime activeDate = dateTimeInputFormatter
			.parseDateTime(s_activeDate);
		showCalendar(calendarId, me.name, s_activeDate,
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
		showCalendar(calendarId, me.name, s_activeDate,
			activeDate.getDayOfMonth(), message);
	}

	public static void removeRepeatingEvents(long calendarId, long eventId,
			String s_activeDate) {
		User me = Database.users.get(Security.connected());
		Calendar calendar = me.getCalendarById(calendarId);
		Event event = calendar.getEventById(eventId);
		calendar.removeSerieOfRepeatingEvents(event);
		DateTime activeDate = dateTimeInputFormatter
				.parseDateTime(s_activeDate);
		showCalendar(calendarId, me.name, s_activeDate,
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
		System.out.println(eventsOfDate);
		// get bounds for calendar construction
		int bound = activeDate.withDayOfMonth(1).getDayOfWeek();
		int bound2 = activeDate.dayOfMonth().getMaximumValue();
		
		DateTime nextMonth = activeDate.plusMonths(1);
		DateTime prevMonth = activeDate.minusMonths(1);

		boolean faved = me.isCalendarObserved(calendarId);
		
		LinkedList<Calendar> observedCalendars = me.getObservedCalendars();
		LinkedList<Long> shownObservedCalendars = me.getShownObservedCalendars();
		
		calendar.generateNextEvents(activeDate);
		
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

		showCalendar(calendarId, user.name, s_activeDate,
				activeDate.getDayOfMonth(), message);
	}

	public static void addUserToEvent(String eventOwner, long calendarId,
			long eventId, String s_activeDate) {
		User me = Database.getUserByName(Security.connected());
		Calendar cal = me.getCalendarById(calendarId);
		DateTime activeDate = dateTimeInputFormatter
				.parseDateTime(s_activeDate);
		Event event = null;
		for (Event e : cal.getAllVisibleEventsOfDate(activeDate.getDayOfMonth(),
				activeDate.getMonthOfYear(), activeDate.getYear(), me)) {
			if (e.getId() == eventId) {
				event = e;
			}
		}
		assert (event != null);
		event.addUserToAttending(me);
		showCalendar(calendarId, me.getName(), s_activeDate,
				activeDate.getDayOfMonth(), null);
	}

	public static void removeUserFromEvent(String requesterName,
			long calendarId, long eventId, String s_activeDate) {
		User me = Database.getUserByName(requesterName);
		Calendar cal = me.getCalendarById(calendarId);
		DateTime activeDate = dateTimeInputFormatter
				.parseDateTime(s_activeDate);
		Event event = null;
		for (Event e : cal.getAllVisibleEventsOfDate(activeDate.getDayOfMonth(),
				activeDate.getMonthOfYear(), activeDate.getYear(), me)) {
			if (e.getId() == eventId) {
				event = e;
			}
		}
		assert (event != null);
		event.removeUserFromAttending(me);
		showCalendar(calendarId, requesterName, s_activeDate,
				activeDate.getDayOfMonth(), null);
	}

}