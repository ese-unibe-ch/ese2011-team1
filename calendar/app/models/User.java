package models;

import java.util.LinkedList;

public class User {
	public String name;
	public LinkedList<Calendar> calendar;
	
	// in this list we store all calendars of other user which we want to display in our calendar
	public LinkedList<Calendar> observedCalendars;
	public LinkedList<Long> shownObservedCalendars;
	public String password;
	public long id;
	private static long counter;

	public User(String name, String password) {
		// preconditions
		assert name != null : "Parameter not allowed to be null";
		assert name.isEmpty() == false : "Empty name, User must have a name";

		this.name = name;
		this.password = password;
		counter++;
		this.id = counter;

		calendar = new LinkedList<Calendar>();
		observedCalendars = new LinkedList<Calendar>();
		shownObservedCalendars = new LinkedList<Long>();
		
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

	public String getPassword() {
		return this.password;
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

}
