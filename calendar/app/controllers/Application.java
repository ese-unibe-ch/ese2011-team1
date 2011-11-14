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
import enums.Interval;
import enums.Visibility;
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

	public static void index(String username) {
		User me = Database.users.get(Security.connected());
		List<User> users = Database.getUserList();
		String s_activeDate = new DateTime().toString("dd/MM/yyyy, HH:mm");
		// todo: remove ourself from list
		
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

	public static void searchUser(String userName) {
		User me = Database.users.get(Security.connected());
		
		if (userName.equals(""))
			render(me, null);
		
		List<User> results = Database.searchUser(userName);
		
		System.out.println("Resultate: "+results);
		
		render(me, results);
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
		User me = Database.users.get(Security.connected());
		User user = Database.getUserByName(userName);
		Event birthday = user.getBirthday();
		String nickname = user.getNickname();
		String emailP = user.getEmailP();
		String emailB = user.getEmailB();
		String telP = user.getTelP();
		String telB = user.getTelB();
		String notes = user.getNotes();

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
		String emailP = me.getEmailP();
		boolean is_emailP_visible = me.getEmailPVis();

		String emailB = me.getEmailB();
		boolean is_emailB_visible = me.getEmailBVis();

		String telP = me.getTelP();
		boolean is_telP_visible = me.getTelPVis();

		String telB = me.getTelB();
		boolean is_telB_visible = me.getTelBVis();

		String notes = me.getNotes();
		boolean is_note_visible = me.getNotesVis();

		render(me, name, oldname, nickname, password, birthday, is_visible, emailP,
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
	public static void createEvent(@Required long calendarId, @Required String name, 
			@Required String start, @Required String end, Visibility visibility, 
			Interval interval, String description, String s_activeDate, boolean isOpen) {

		User me = Database.users.get(Security.connected());
		Calendar calendar = me.getCalendarById(calendarId);

		// convert dates
		DateTime d_start = null;
		DateTime d_end = null;
		
		if (name.length() < 1) {
			message = "INVALID INPUT: PLEASE ENTER A NAME!";
			addEditEvent(-1, calendarId, name, s_activeDate, message);
		}

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

		boolean repeated = interval != Interval.NONE;
		System.out.println("interval: " + interval + repeated);

		Event e;
		if (!repeated) {
			e = new PointEvent(name, d_start, d_end, visibility, calendar);
		} 
		else {
			e = new RepeatingEvent(name, d_start, d_end, visibility, calendar,
					interval);
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
		showCalendar(calendarId, me.getName(), start, d_start.getDayOfMonth(),
				message);
	}

	public static void saveEditedEvent(@Required long eventId,
			@Required long calendarId, @Required String name,
			@Required String start, @Required String end,
			Visibility visibility, Interval interval, String description,
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
		
		System.out.println("inteval size: " + interval);
		
		boolean repeated = interval != Interval.NONE;
		Event event = calendar.getEventById(eventId);
		System.out.println("event id: " + eventId + " " + event);
		
		for(Event head : calendar.getEventHeads()) calendar.PrintHeadAndHisTail(head);
		
		
		event.editDescription(description);
		
		
		event.edit(name, d_start, d_end, visibility, interval, d_start, d_start, description);
		
		//
		// if (repeated && !event.wasPreviouslyRepeating) {
		// event.wasPreviouslyRepeating = true;
		// calendar.addToRepeated(event);
		// }
		//
		//
	//	event.edit(d_start, d_end, name, visibility, repeated, interval);
		
		/*
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
				RepeatingEvent newEvent = new RepeatingEvent((PointEvent)event, interval);
				calendar.addEvent(newEvent);
				newEvent.editDescription(description);
				calendar.generateNextEvents(newEvent, newEvent.getStart());
			}
		}
		
		*/
		
		
		
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
		calendar.removeSerieOfRepeatingEvents(event);
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
		//System.out.println(eventsOfDate);
		
		
		// TODO pls fix this - it works but it is ugly
		// filter
		LinkedList<Event> tmp = new LinkedList<Event>(eventsOfDate);
		for(Event e : eventsOfDate){
			long currentId = e.getId();
			boolean flag = true;
			for(Event t : tmp){
				if(t.getId() == currentId){
					if(flag){
						flag = false;
						
					}else{
						// delete here duplicated event
						eventsOfDate.remove(t);
						break;
					}
				}
			}
		}
		
		// end of filter
		
		//calendar.PrintAllHeadTails();
		
		System.out.println("Events of current selcected date:");
		for(Event e : eventsOfDate)
			System.out.println("date " + e.getParsedStartDate() + " id " + e.getId() + " base " + e.getBaseId());
		System.out.println("done");
		
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

		showCalendar(calendarId, user.getName(), s_activeDate,
				activeDate.getDayOfMonth(), message);
	}

	public static void addUserToEvent(String eventOwner, long calendarId,
			long eventId, String s_activeDate) {
		User me = Database.getUserByName(Security.connected());
		User user = Database.getUserByName(eventOwner);
		Calendar cal = user.getCalendarById(calendarId);
		assert (cal != null);
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
		showCalendar(calendarId, user.getName(), s_activeDate,
				activeDate.getDayOfMonth(), null);
	}

	public static void removeUserFromEvent(String eventOwner,
			long calendarId, long eventId, String s_activeDate) {
		User me = Database.getUserByName(Security.connected());
		User user = Database.getUserByName(eventOwner);
		Calendar cal = user.getCalendarById(calendarId);
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
		showCalendar(calendarId, user.getName(), s_activeDate,
				activeDate.getDayOfMonth(), null);
	}
	
	/**
	 * Creates a new calendar and adds it to the calendars of the given user.
	 * 
	 * @param userName the user which wants a new calendar
	 * @param calenderName the name of the new calendar
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
	 * @param calendarId the calendar to delete
	 * @param userName the user who wants to delete
	 */
	public static void deleteCalendar(long calendarId) {
		User me = Database.users.get(Security.connected());
		me.deleteCalendar(calendarId);
		index(me.getName());
	}
}