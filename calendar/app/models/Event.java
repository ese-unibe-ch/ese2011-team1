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
	public boolean is_repeatable;
	public int intervall;
	private static long counter;
	private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm");

	public Event(Date start, Date end, String name, boolean is_visible,
			boolean isRepeated, int intervall) {
		this.start = start;
		this.end = end;
		this.name = name;
		this.is_visible = is_visible;
		counter++;
		this.id = counter;
		this.is_repeatable = isRepeated;
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

	public boolean isRepeatable() {
		return this.is_repeatable;
	}

	public int getIntervall() {
		return this.intervall;
	}

}
