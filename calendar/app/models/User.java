package models;

import java.util.LinkedList;

import org.joda.time.DateTime;

import models.Event.Visibility;

public class User {
	public String name;
	public LinkedList<UserCalendar> calendar;
	
	// in this list we store all calendars of other user which we want to display in our calendar
	public LinkedList<Calendar> observedCalendars;
	public LinkedList<Long> shownObservedCalendars;
	public String password;
	public DateTime birthday;
	public boolean isPublicBirthday;
	public long id;
	private String nickname;
	private static long counter;

	public User(String name, String password, DateTime birthday, String nickname) {
		// preconditions
		assert name != null : "Parameter not allowed to be null";
		assert name.isEmpty() == false : "Empty name, User must have a name";

		this.name = name;
		this.nickname = nickname;
		this.password = password;
		this.birthday = birthday;
		counter++;
		this.id = counter;

		calendar = new LinkedList<UserCalendar>();
		observedCalendars = new LinkedList<Calendar>();
		shownObservedCalendars = new LinkedList<Long>();
		observedCalendars.add(BirthdayCalendar.getInstance());
		
		initializeBirthday(birthday);
		
		// each user x has a default a calender called: x's first calendar
		calendar.add(new UserCalendar(name + "'s first calendar", this));

		// postconditions
		assert this.name.equals(name);
		assert calendar != null;
	}

	private void initializeBirthday(DateTime birthday) {
		BirthdayEvent e = new BirthdayEvent(this, birthday, Visibility.PRIVATE);
		BirthdayCalendar.addBirthday(e);
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
	
	public DateTime getBirthday() {
		return this.birthday;
	}

	public void setBirthday(DateTime birthday) {
		this.birthday = birthday;
	}
	
	public boolean isBirthdayPublic() {
		return this.isPublicBirthday;
	}
	
	public void setBirthdayPublic(boolean b) {
		this.isPublicBirthday = b;
		Event birthday = BirthdayCalendar.getBirthdayOf(this);
		Visibility visibility = Visibility.PRIVATE;
		if (b) visibility = Visibility.PUBLIC; 
		birthday.edit(birthday.start, birthday.end, birthday.name, visibility, birthday.is_repeating, birthday.intervall);
	}
	
	public String getNickname() {
		return this.nickname;
	}
	
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}


	// return all calendars of a user
	public LinkedList<UserCalendar> getCalendars() {
		return this.calendar;
	}

	// get default calendar back
	public UserCalendar getdefaultCalendar() {
		return this.calendar.getFirst();
	}

	// get a all calendars of a user - linkedlist
	// entferne dann noch argument für call
	public LinkedList<UserCalendar> getCalendarsByName(User user) {
		return user.getCalendars();
	}

	public UserCalendar getCalendarById(long calID) {
		UserCalendar result = null;
		for (UserCalendar cal : calendar) {
			if (cal.getId() == calID)
				result = cal;
		}
		return result;
	}
	
	/*
	 * add a new owned calendar into our calendar list 
	 */
	
	public void addCalendar(UserCalendar cal) {
		calendar.add(cal);
	}
	
	/*
	 * add a new Calendar of another user into our oberserved calendar list. 
	 */
	
	public void addObservedCalendar(UserCalendar cal){
		observedCalendars.add(cal);
	}
	
	public void removeObservedCalendar(UserCalendar cal){
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

}
