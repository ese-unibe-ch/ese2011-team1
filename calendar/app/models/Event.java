package models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
	public boolean isDirty = false;
	
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
		this.baseID = id;
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

	public void edit(Date start, Date end, String name, boolean is_visible, boolean is_repeated, int intervall) {
		this.start = start;
		this.end = end;
		this.name = name;
		this.is_visible = is_visible;
		this.is_repeating = is_repeated;
		this.intervall = intervall;
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
		Date nextRepStartDate = new Date(start.getYear(), start.getMonth(), start.getDate() + intervall, start.getHours(), start.getMinutes());
		Date nextRepEndDate = new Date(end.getYear(), end.getMonth(), end.getDate() + intervall, start.getHours(), start.getMinutes());
		if (intervall == 30) {
			//System.out.println(start.getMonth());
			int k = start.getMonth()+1;
			System.out.println(start.getDate());
			int delta = 0;
			if(start.getDate()==31){ // if we have a month with 31 days as rep. event marked
					
				if(k==7 || k==12)delta = -1; // if we have december or june
				//delta--;
				nextRepStartDate = new Date(start.getYear(), start.getMonth()+2+delta, start.getDate(), start.getHours(), start.getMinutes());
				nextRepEndDate = new Date(end.getYear(), end.getMonth()+2+delta, end.getDate(), start.getHours(), start.getMinutes());
			}
			
		}
		if (intervall == 365) {	
			nextRepStartDate = new Date(start.getYear()+1, start.getMonth(), start.getDate(), start.getHours(), start.getMinutes());
			nextRepEndDate = new Date(end.getYear()+1, end.getMonth(), end.getDate(), start.getHours(), start.getMinutes());
		}
		Event newEvent = new Event(nextRepStartDate, nextRepEndDate, this.name, this.is_visible, this.is_repeating, this.intervall);
		newEvent.setBaseID(this.baseID);
		return newEvent;
	}

	public void setBaseID(long ID) {
		this.baseID = ID;
	}
	
	public long getBaseID() {
		return this.baseID;
	}

	
	/**
	 * This method compares a provided Date with the repetitions
	 * of this Event until the provided Date is smaller than the start date of the calculated repetition.
	 * If one of the repetitions has the same date as the provided date, this repetition will be returned.
	 * 
	 * @param compDate the date which is compared to the calculated repetitions.
	 * @return null if no repetition of any Event occurs on the specified Date.
	 * Event repeatingEventOnDay if repeatingEventOnDay.getStart().getDate() == compDate.getDate().
	 * 
	 */
	public Event getRepetitionOnDate(Date compDate) {
		Event repeatingEventOnDay = null;
		Event repeatingEvent = this;
		while(repeatingEvent.getStart().before(compDate)) {
			repeatingEvent = repeatingEvent.getNextRepetitionEvent();
			if (repeatingEvent.getStart().getDate() == compDate.getDate()) {
				repeatingEventOnDay = repeatingEvent;
			}
		}
		return repeatingEventOnDay;
	}

}
