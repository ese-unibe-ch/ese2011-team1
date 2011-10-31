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

import models.BirthdayCalendar;
import models.BirthdayEvent;
import models.Calendar;
import models.UserCalendar;
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

//	static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm");
//	static DateFormat birthdayDateFormat = new SimpleDateFormat("dd/MM/yyyy");
	final static DateTimeFormatter dateTimeInputFormatter = DateTimeFormat.forPattern("dd/MM/yyyy, HH:mm");
	final static DateTimeFormatter birthdayFormatter = DateTimeFormat.forPattern("dd/MM/yyyy");
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
		UserCalendar defaultCalendar = me.getdefaultCalendar();
		LinkedList<UserCalendar> calendars = me.getCalendars();
		render(me, users, calendars, defaultCalendar, s_activeDate);
	}

	public static void showCalendarList(String username, String s_activeDate) {
		User me = Database.users.get(Security.connected());
		User user = Database.users.get(username);
		LinkedList<UserCalendar> calendars = null;
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
		Iterator allVisibleEvents = user.getCalendarById(calendarId).getEventList(d, me);
		UserCalendar calendars = user.getCalendarById(calendarId);
		LinkedList<Event> events = new LinkedList<Event>();

		while (allVisibleEvents.hasNext()) {
			events.add((Event) allVisibleEvents.next());
		}

		render(me, user, events, calendarName, calendars, calendarId);
	}

	public static void showRegistration() {
		render();
	}
	
	public static void RegUser(@Required String name, @Required String nickname, @Required String password, @Required String birthday, @Required boolean is_visible)
    {

    	if(Database.userAlreadyRegistrated(name))
    	{
    		flash.error("Username (" + name + ") already exists!");
    		params.flash();
    		validation.keep();
    		showRegistration();
    	}
    	else if(validation.hasErrors())
    	{
    		params.flash();
    		validation.keep();
    		flash.error("All fields required!");
    		showRegistration();
    	}
    	else
    	{
    		try 
    		{
    			DateTime birthdate = birthdayFormatter.parseDateTime(birthday);
    			User user = new User(name, password, birthdate, nickname);
    			Database.addUser(user);
    			user.setBirthdayPublic(is_visible);
    			index();
    		} 
    		catch (Exception e) 
    		{
    			params.flash();
        		validation.keep();
    			flash.error("Invalid date format");
    			showRegistration();
    		}	
    	}
    }
	
	 public static void showProfile(String userName)
	    {
	    	User user = Database.getUserByName(userName);
	    	DateTime birthday = user.getBirthday();
	    	String nickname = user.getNickname();
	    	boolean is_visible = user.isBirthdayPublic();
	    	
	    	String pub = "public";
	    	if(!is_visible) pub = "private";
	    		
	    	render(user, nickname, birthday, pub);
	    }
	    
	    public static void showEditProfile()
	    {
	    	User user = Database.users.get(Security.connected());
	    	
	    	String name = user.getName();
	    	String nickname = user.getNickname();
	    	String password = user.getPassword();
	    	String birthday = user.getBirthday().toString("dd/MM/yyyy");
	    	boolean is_visible = user.isBirthdayPublic();
	    	
	    	render(name, nickname, password, birthday, is_visible);
	    }
	    
	    public static void editProfile(@Required String name, @Required String password, @Required String birthday, @Required String nickname, @Required boolean is_visible)
	    {
	    	User user = Database.users.get(Security.connected());
	    	
	    	if(!(name.equals(user.getName())) && Database.userAlreadyRegistrated(name))
	    	{
	    		flash.error("Username (" + name + ") already exists!");
	    		params.flash();
	    		validation.keep();
	    		showEditProfile();
	    	}
	    	else if(validation.hasErrors())
	    	{
	    		params.flash();
	    		validation.keep();
	    		flash.error("All fields required!");
	    		showEditProfile();
	    	}
	    	else
	    	{
	    		try 
	    		{
	    			DateTime birthdate = birthdayFormatter.parseDateTime(birthday);
	    			user.setBirthday(birthdate);
	    			user.setBirthdayPublic(is_visible);
	    			user.setName(name);
	    			user.setNickname(nickname);
	    			user.setPassword(password);
	    			
	    			Database.changeUserName(user); //TODO does not work properly jet!
	    			Event birthdayEvent = BirthdayCalendar.getBirthdayOf(user);
	    			Visibility visibility = Visibility.PRIVATE;
	    			if (is_visible) visibility = Visibility.PUBLIC;
	    			
	    			birthdayEvent.edit(birthdate, birthdate, birthdayEvent.name, visibility, birthdayEvent.is_repeating, birthdayEvent.getIntervall());
	    			index();
	    		} 
	    		catch (Exception e) 
	    		{
	    			params.flash();
	        		validation.keep();
	    			flash.error("Invalid date format");
	    			showEditProfile();
	    		}
	    	}
	    }

	public static void createEvent(@Required long calendarID,
			@Required String name, @Required String start,
			@Required String end, Visibility visibility, String is_repeated,
			String description, String s_activeDate) {

		User me = Database.users.get(Security.connected());
		UserCalendar calendar = me.getCalendarById(calendarID);

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
		boolean repeated = is_repeated.equals("0") ? false : true;
		int intervall = Integer.parseInt(is_repeated);
		Event e = new Event(me, d_start, d_end, name, visibility, repeated,
				intervall);
		e.editDescription(description);
		calendar.addEvent(e);
		showCalendar(calendarID, me.name, s_activeDate, d_start.getDayOfMonth(), message);
	}

	public static void saveEditedEvent(@Required long eventID,
			@Required long calendarID, @Required String name,
			@Required String start, @Required String end,
			Visibility visibility, String is_repeated, 
			String description, String s_activeDate) {

		User me = Database.users.get(Security.connected());
		UserCalendar calendar = me.getCalendarById(calendarID);

		// covert dates

		DateTime d_start = null;
		DateTime d_end = null;
		boolean repeated = is_repeated.equals("0") ? false : true;
		int intervall = Integer.parseInt(is_repeated);
		Event event = calendar.getEventById(eventID);
		event.editDescription(description);
		if (repeated) {
			calendar.addToRepeated(event);
		}

		try {
			d_start = dateTimeInputFormatter.parseDateTime(start);
			d_end = dateTimeInputFormatter.parseDateTime(end);
		} catch (Exception e) {
			message = "INVALID INPUT: PLEASE TRY AGAIN!";
			editEvent(eventID, calendarID, name, s_activeDate, message);
		}

		event.edit(d_start, d_end, name, visibility, repeated, intervall);
		showCalendar(calendarID, me.name, s_activeDate, d_start.getDayOfMonth(), message);
	}

	public static void editEvent(long eventID, long calendarID, String name,
			String s_activeDate, String message) {
		User me = Database.users.get(Security.connected());
		UserCalendar calendar = me.getCalendarById(calendarID);
		Event event = calendar.getEventById(eventID);
		render(me, calendar, event, calendarID, eventID, s_activeDate, message);
	}

	public static void addEvent(long calendarID, String name, String s_activeDate, String message) {
		User me = Database.users.get(Security.connected());
		UserCalendar calendar = me.getCalendarById(calendarID);
		render(me, calendar, calendarID, s_activeDate, message);
	}

	public static void removeEvent(long calendarID, long eventID,
			String s_activeDate) {
		User me = Database.users.get(Security.connected());
		UserCalendar calendar = me.getCalendarById(calendarID);
		System.out.println("removed: event ID: " + eventID);
		for (Event e : BirthdayCalendar.getBirthdays()) {
			System.out.println("bday id: " + e.id + " baseid: " + e.baseId);
		}
		calendar.removeEvent(eventID);
		DateTime activeDate = dateTimeInputFormatter.parseDateTime(s_activeDate);
		showCalendar(calendarID, me.name, s_activeDate, activeDate.getDayOfMonth(), message);
	}

	public static void cancelEventRepetition(long calendarID, long eventID,
			String s_activeDate) {
		User me = Database.users.get(Security.connected());
		UserCalendar calendar = me.getCalendarById(calendarID);
		calendar.cancelRepeatingEventRepetitionFromDate(calendar
				.getEventById(eventID));
		DateTime activeDate = dateTimeInputFormatter.parseDateTime(s_activeDate);
		showCalendar(calendarID, me.name, s_activeDate, activeDate.getDayOfMonth(), message);
	}

	public static void removeRepeatingEvents(long calendarID, long eventId,
			String s_activeDate) {
		User me = Database.users.get(Security.connected());
		UserCalendar calendar = me.getCalendarById(calendarID);
		Event event = calendar.getEventById(eventId);
		calendar.removeRepeatingEvents(event);
		DateTime activeDate = dateTimeInputFormatter.parseDateTime(s_activeDate);
		showCalendar(calendarID, me.name, s_activeDate, activeDate.getDayOfMonth(), message);
	}

	public static void showCalendar(long calendarId, String username, String s_activeDate, int counter, String message) {
		message = null;
		
		System.out.println("activeDate incoming: " + s_activeDate);
		System.out.println("format required: dd/MM/yyyy, HH:mm");

		User me = Database.users.get(Security.connected());
		User user = Database.users.get(username);
		UserCalendar calendar = user.getCalendarById(calendarId);
		System.out.println("calID: " + calendar.id);
		assert (calendar != null) : "AEHSHAGF>GHS";
		
		DateTime activeDate = null;
		DateTime today = new DateTime();

		try {
			activeDate = dateTimeInputFormatter.parseDateTime(s_activeDate);
		} catch (Exception e) {
//			message = "catch: showTest parse s_activeDate to activeDate: " + s_activeDate;
			activeDate = today;
		}
		try {
			activeDate = activeDate.withDayOfMonth(counter);
		} catch (Exception e) {
//			message = "catch: showTest set counter as DayOfMonth for activeDate.";
			activeDate.withDayOfMonth(activeDate.getDayOfMonth());
		}
		assert (activeDate != null) : "must not be null!";
		
		LinkedList<Event> eventsOfDay = calendar.getEventsOfDay(activeDate, me);
		
		int bound = (activeDate.withDayOfMonth(1).getDayOfWeek());
		int bound2 = activeDate.dayOfMonth().getMaximumValue();
		DateTime nextMonth = activeDate.plusMonths(1);
    	DateTime prevMonth = activeDate.minusMonths(1);
    	
		boolean faved = me.isCalendarObserved(calendarId);
		LinkedList<Calendar> observedCalendars = me.getObservedCalendars();
		LinkedList<Long> shownObservedCalendars = me
				.getShownObservedCalendars();
		System.out.println("Markierte beim Neuladen: "
				+ shownObservedCalendars.size());
		
		PriorityQueue<Event> allEvents = calendar.getEvents();
		for (Event e : eventsOfDay) {
			if (e.owner != me || e instanceof BirthdayEvent) {
				allEvents.remove(e);
			}
			
			
		}
		
    	render(me, user, calendar, bound, bound2, prevMonth, nextMonth, activeDate, today, eventsOfDay, message, faved, observedCalendars, shownObservedCalendars);
	}
	
	
	/**
	 * Observe (or "follow") a certain calendar of a user.
	 */
	public static void addObserve(String username, long calendarId, String s_activeDate, String message) {
		User me = Database.users.get(Security.connected());
		User user = Database.users.get(username);

		// find calendar by ID
		UserCalendar cal = user.getCalendarById(calendarId);
		me.addObservedCalendar(cal);
		DateTime activeDate = dateTimeInputFormatter.parseDateTime(s_activeDate);
		showCalendar(calendarId, username, s_activeDate, activeDate.getDayOfMonth(), message);
	}

	public static void removeObserve(String username, long calendarId, String s_activeDate, String message) {
		User me = Database.users.get(Security.connected());
		User user = Database.users.get(username);

		// find calendar by ID
		UserCalendar cal = user.getCalendarById(calendarId);
		me.removeObservedCalendar(cal);
		DateTime activeDate = dateTimeInputFormatter.parseDateTime(s_activeDate);
		showCalendar(calendarId, username, s_activeDate, activeDate.getDayOfMonth(), message);
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
			@Required long calendarId,
			@Required String s_activeDate, @Required String message,
			@Required long calID, @Required boolean chk) {

		User user = Database.users.get(username);
		System.out.println("username = " + username + ", calID = " + calID
				+ ", chk = " + chk);

		if (chk == true) {
			user.addShownObservedCalendar(calID);
		} else {
			user.removeShownObservedCalendar(calID);
		}
		
		DateTime activeDate = dateTimeInputFormatter.parseDateTime(s_activeDate);

		showCalendar(calendarId, user.name, s_activeDate, activeDate.getDayOfMonth(), message);
	}
}