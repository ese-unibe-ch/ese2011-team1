package models;

import java.util.Date;
import java.util.LinkedList;

import models.Event.Visibility;


/**
 * The User class represents a User of this Calendar application.
 * Users may have multiple Calendars, all of which can contain multiple Events.
 * Users are responsible for maintaining the calendar, which includes adding, editing and removing Events.
 * 
 * @see {@link Calendar}
 *
 */
public class User {
	public String name;
	public LinkedList<Calendar> calendar;
	
	// in this list we store all calendars of other user which we want to display in our calendar
	public LinkedList<Calendar> observedCalendars;
	public LinkedList<Long> shownObservedCalendars;
	public String password;
	public Event birthday;
//	public boolean isPublicBirthday;
	public long id;
	private String nickname;
	private Calendar birthdayCalendar;
	private static long counter;

	/**
	 * 
	 * @param name
	 * @param password
	 * @param birthday
	 * @param nickname
	 */
	public User(String name, String password, Date birthday, String nickname) {
		// preconditions
		assert name != null : "Parameter not allowed to be null";
		assert name.isEmpty() == false : "Empty name, User must have a name";

		this.name = name;
		this.nickname = nickname;
		this.password = password;
		this.birthday = new Event(this, birthday, birthday, name + "'s birthday", Visibility.PRIVATE, true, 365);
		counter++;
		this.id = counter;

		calendar = new LinkedList<Calendar>();
		observedCalendars = new LinkedList<Calendar>();
		shownObservedCalendars = new LinkedList<Long>();
		birthdayCalendar = new Calendar("Birthdays", this);
		observedCalendars.add(birthdayCalendar);
		birthdayCalendar.addEvent(this.birthday);
		
		// each user x has a default a calender called: x's first calendar
		calendar.add(new Calendar(name + "'s first calendar", this));

		// postconditions
		assert this.name.equals(name);
		assert calendar != null;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return this.password;
	}
	
	public Event getBirthday() {
		return this.birthday;
	}

	public void setBirthdayDate(Date birthdayDate) {
		birthday.edit(birthdayDate, birthdayDate, birthday.name, birthday.visibility, true, 365);
	}
	
	public boolean isBirthdayPublic() {
		return birthday.isVisible();
	}
	
	public void setBirthdayPublic(boolean b) {
		Visibility visibility = b ? Visibility.PUBLIC : Visibility.PRIVATE;
		birthday.edit(birthday.start, birthday.end, birthday.name, visibility, true, 365);
		System.out.println("changed birthday to : " + visibility);
	}
	
	public String getNickname() {
		return this.nickname;
	}
	
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}


	// return all calendars of a user
	public LinkedList<Calendar> getCalendars() {
		return this.calendar;
	}

	// get default calendar back
	public Calendar getdefaultCalendar() {
		return this.calendar.getFirst();
	}

	// get a all calendars of a user - linkedlist
	// entferne dann noch argument für call
	public LinkedList<Calendar> getCalendarsByName(User user) {
		return user.getCalendars();
	}

	public Calendar getCalendarById(long calID) {
		Calendar result = null;
		for (Calendar cal : calendar) {
			if (cal.getId() == calID)
				result = cal;
		}
		return result;
	}
	
	/*
	 * add a new owned calendar into our calendar list 
	 */
	
	public void addCalendar(Calendar cal) {
		calendar.add(cal);
	}
	
	/*
	 * add a new Calendar of another user into our oberserved calendar list. 
	 */
	
	public void addObservedCalendar(Calendar cal){
		observedCalendars.add(cal);
	}
	
	public void removeObservedCalendar(Calendar cal){
		observedCalendars.remove(cal);
	}
	
	/**
	 * Adds a new ID from a observed calendar to be shown.
	 */
	public void addShownObservedCalendar(long calID){
		shownObservedCalendars.add(calID);
	}
	
	/**
	 * Adds a new ID for a observed calendar to be shown.
	 */
	public void removeShownObservedCalendar(long calID){
		shownObservedCalendars.remove(calID);
	}
	
	/**
	 * Returns true, if calendar with given calID is observed, false otherwise.
	 * 
	 */
	public boolean isCalendarObserved(long calID) {
		for (Calendar cal : observedCalendars) {
			if (cal.id == calID)
				return true;
		}
		return false;
	}
	
	public LinkedList<Calendar> getObservedCalendars() {
		return observedCalendars;
	}
	
	public LinkedList<Long> getShownObservedCalendars() {
		return shownObservedCalendars;
	}

	public Calendar getBirthdayCalendar() {
		return this.birthdayCalendar;
	}
	
	public boolean isBirthday(Event e) {
		return this.birthday.getId() == e.getBaseId();
	}

}
