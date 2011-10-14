package models;

import java.util.LinkedList;

public class User {
	public String name;
	public LinkedList<Calendar> calendar;
	public String password;
	public long id;
	private static long counter;
	
	public User(String name, String password){
		// preconditions
		assert name != null : "Parameter not allowed to be null";
		assert name.isEmpty()==false: "Empty name, User must have a name";
		
		this.name = name;
		this.password = password;
		counter++;
		this.id = counter;
		
		calendar = new LinkedList<Calendar>();
		// each user x has a default a calender called: x's first calendar
		calendar.add(new Calendar(name+"'s first calendar", this));
		
		// postconditions
		assert this.name.equals(name);
		assert calendar != null;
	}
	
	public void setPassword(String password){
		this.password = password;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getPassword(){
		return this.password;
	}
	
	// return all calendars of a user
	public LinkedList<Calendar> getCalendars(){
		return this.calendar;
	}
	
	// get default calendar back
	public Calendar getdefaultCalendar(){
		return this.calendar.getFirst();
	}
	
	// get a all calendars of a user - linkedlist
	// entferne dann noch argument für call
	public LinkedList<Calendar> getCalendarsByName(User user) {
		return user.getCalendars();
	}
	
	public Calendar getCalendarById(long calID){
		Calendar result = null;
		for(Calendar cal : calendar){
			if(cal.getId() == calID) result = cal;
		}
		return result;
	}
	
	public void addCalendar(Calendar cal){
		calendar.add(cal);
	}
	
}
