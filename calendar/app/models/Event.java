package models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Event implements Comparable<Event> {
	public Date start;
	public Date end;
	public String name;
	public boolean is_visible;
	public long id;
	public long baseID;
	public boolean is_repeating;
	public int intervall;
	private static long counter;
	private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm");

	
	/**
	 * 
	 * @param start the starting Date
	 * @param end the ending Date
	 * @param name name and description of Event
	 * @param is_visible flag, determines visibility for other users
	 * @param isRepeated flag, used for repeating Events
	 * @param intervall determines repetition interval. Possibilities: DAY (1), WEEK(7), MONTH(30), YEAR(265)
	 */
	public Event(Date start, Date end, String name, boolean is_visible,
			boolean is_repeating, int intervall) {
		this.start = start;
		this.end = end;
		this.name = name;
		this.is_visible = is_visible;
		counter++;
		this.id = counter;
		this.is_repeating = is_repeating;
		this.intervall = intervall;
	}

	/*
	 * Getters
	 */

	public Date getStart() {
		return this.start;
	}

	public Date getEnd() {
		return this.end;
	}

	public boolean isVisible() {
		return this.is_visible;
	}

	public String getName() {
		return this.name;
	}

	public long getId() {
		return this.id;
	}

	public String getParsedDate(Date d) {
		return dateFormat.format(d);
	}

	public void edit(Date start, Date end, String name, boolean is_visible) {
		this.start = start;
		this.end = end;
		this.name = name;
		this.is_visible = is_visible;
	}

	@Override
	public int compareTo(Event e) {
		return this.getStart().compareTo(e.getStart());
	}

	public boolean isRepeating() {
		return this.is_repeating;
	}

	public int getIntervall() {
		return this.intervall;
	}

	//TODO: fix ugly date instantiation
	public Event getNextRepetitionEvent() {
		Date nextRepStartDate = new Date(start.getYear(), start.getMonth(), start.getDate() + intervall);
		Date nextRepEndDate = new Date(end.getYear(), end.getMonth(), end.getDate() + intervall);
		Event newEvent = new Event(nextRepStartDate, nextRepEndDate, this.name, this.is_visible, this.is_repeating, this.intervall);
		newEvent.setBaseID(this.id);
		return newEvent;
	}

	public void setBaseID(long ID) {
		this.baseID = ID;
	}
	
	public long getBaseID() {
		return this.baseID;
	}

}
