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

	public static void createEvent(@Required long calendarID,
			@Required String name, @Required String start,
			@Required String end, Visibility visibility, String is_repeated,
			String description, String s_activeDate, boolean isOpen) {

		User me = Database.users.get(Security.connected());
		Calendar calendar = me.getCalendarById(calendarID);

		// convert dates
		DateTime d_start = null;
		DateTime d_end = null;

		try {
			d_start = dateTimeInputFormatter.parseDateTime(start);
			d_end = dateTimeInputFormatter.parseDateTime(end);
		} catch (Exception e) {
			message = "INVALID INPUT: PLEASE TRY AGAIN!";
			addEvent(calendarID, name, s_activeDate, message);
		}
		if (d_end.isBefore(d_start)) {
			message = "INVALID INPUT: START DATE MUST BE BEFORE END DATE!";
			addEvent(calendarID, name, s_activeDate, message);
		}

		boolean repeated = is_repeated.equals("0") ? false : true;
		int intervall = Integer.parseInt(is_repeated);

		Event e = null;
		if (!repeated) {
			e = new PointEvent(name, d_start, d_end, visibility, calendar);
		} else {
			e = new RepeatingEvent(name, d_start, d_end, visibility, calendar,
					intervall);
			e.generateNextEvents(e.getStart());
		}

		// Event e = new Event(me, d_start, d_end, name, visibility, repeated,
		// intervall, calendarID, is_open);
		e.editDescription(description);
		if (isOpen) {
			e.setOpen();
			e.addUserToAttending(me);
		}
		calendar.addEvent(e);
		showCalendar(calendarID, me.name, start, d_start.getDayOfMonth(),
				message);
	}

	public static void saveEditedEvent(@Required long eventID,
			@Required long calendarID, @Required String name,
			@Required String start, @Required String end,
			Visibility visibility, String is_repeated, String description,
			String s_activeDate) {

		User me = Database.users.get(Security.connected());
		Calendar calendar = me.getCalendarById(calendarID);
		//
		// // covert dates
		DateTime d_start = null;
		DateTime d_end = null;
		try {
			d_start = dateTimeInputFormatter.parseDateTime(start);
			d_end = dateTimeInputFormatter.parseDateTime(end);
		} catch (Exception e) {
			message = "INVALID INPUT: PLEASE TRY AGAIN!";
			editEvent(eventID, calendarID, name, s_activeDate, message);
		}

		 boolean repeated = is_repeated.equals("0") ? false : true;
		 int interval = Integer.parseInt(is_repeated);
		 Event event = calendar.getEventById(eventID);
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
				// TODO diser cast ist scheisse, denn buggy!!!
				// models.PointEvent cannot be cast to models.RepeatingEvent 
				// mache richtig, wenn wach... gleiches oben im instanceof IntervalEvent..
				//((RepeatingEvent) event).edit(name, d_start, d_end, visibility, interval);
				calendar.removeEvent(event.getBaseId());
				RepeatingEvent newEvent = new RepeatingEvent((PointEvent)event, interval);
				calendar.addEvent(newEvent);
				calendar.generateNextEvents(newEvent, newEvent.getStart());
				System.out.println("Bis hier Okay! :)");
			}
		}
		
		showCalendar(calendarID, me.getName(), s_activeDate,
				d_start.getDayOfMonth(), message);
	}

	public static void editEvent(long eventID, long calendarID, String name,
		String s_activeDate, String message) {
		User me = Database.users.get(Security.connected());
		Calendar calendar = me.getCalendarById(calendarID);
		Event event = calendar.getEventById(eventID);
		render(me, calendar, event, calendarID, eventID, s_activeDate, message);
	}

	public static void addEvent(long calendarID, String name,
			String s_activeDate, String message) {
		User me = Database.users.get(Security.connected());
		Calendar calendar = me.getCalendarById(calendarID);
		DateTime activeDate = dateTimeInputFormatter
				.parseDateTime(s_activeDate);
		render(me, calendar, calendarID, activeDate, message);
	}

	public static void removeEvent(long calendarID, long eventID,
			String s_activeDate) {
		User me = Database.users.get(Security.connected());
		Calendar calendar = me.getCalendarById(calendarID);
		calendar.removeEvent(eventID);
		DateTime activeDate = dateTimeInputFormatter
			.parseDateTime(s_activeDate);
		showCalendar(calendarID, me.name, s_activeDate,
			activeDate.getDayOfMonth(), message);
	}

	public static void cancelEventRepetition(long calendarID, long eventID,
			String s_activeDate) {
		User me = Database.users.get(Security.connected());
		Calendar calendar = me.getCalendarById(calendarID);
		calendar.cancelRepeatingEventRepetitionFromDate(calendar
			.getEventById(eventID));
		DateTime activeDate = dateTimeInputFormatter
			.parseDateTime(s_activeDate);
		showCalendar(calendarID, me.name, s_activeDate,
			activeDate.getDayOfMonth(), message);
	}

	public static void removeRepeatingEvents(long calendarID, long eventId,
			String s_activeDate) {
		User me = Database.users.get(Security.connected());
		Calendar calendar = me.getCalendarById(calendarID);
		Event event = calendar.getEventById(eventId);
		calendar.removeSerieOfRepeatingEvents(event);
		DateTime activeDate = dateTimeInputFormatter
				.parseDateTime(s_activeDate);
		showCalendar(calendarID, me.name, s_activeDate,
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

		LinkedList<Event> eventsOfDate = calendar.getEventsOfDate(
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
		for (Calendar observedCalendar : observedCalendars) {
			if (shownObservedCalendars.contains(observedCalendar.getId())) {
				eventsOfDate.addAll(observedCalendar.getEventsOfDate(
						activeDate.getDayOfMonth(), activeDate.getMonthOfYear(),
						activeDate.getYear(), me));
			}
		}
		
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

		// find calendar by ID
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
	 * @param calID
	 *            ID of the calendar to be removed / added to view
	 * @param chk
	 *            A boolean value, indicating if we're adding or removing a
	 *            observed calendar
	 */
	public static void changeObservedCalendars(@Required String username,
			@Required long calendarId, @Required String s_activeDate,
			@Required String message, @Required long calID,
			@Required boolean chk) {

		User user = Database.users.get(username);

		if (chk == true) {
			user.addShownObservedCalendar(calID);
		} else {
			user.removeShownObservedCalendar(calID);
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
		for (Event e : cal.getEventsOfDate(activeDate.getDayOfMonth(),
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
		for (Event e : cal.getEventsOfDate(activeDate.getDayOfMonth(),
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