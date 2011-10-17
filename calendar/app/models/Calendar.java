package models;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class Calendar implements Cloneable {
	private String name;
	public User owner;
	private PriorityQueue<Event> events;
	public long id;
	private LinkedList<Event> repeatingEvents;
	private static long counter;

	public Calendar(String name, User owner) {
		this.name = name;
		this.owner = owner;
		events = new PriorityQueue<Event>();
		repeatingEvents = new LinkedList<Event>();
		counter++;
		this.id = counter;
	}

	public User getOwner() {
		return this.owner;
	}

	public String getName() {
		return this.name;
	}

	public long getId() {
		return this.id;
	}

	public void addEvent(Date startDate, Date endDate, String name,
			boolean is_visible, boolean isRepeating, int intervall) {
		Event ev = new Event(startDate, endDate, name, is_visible, isRepeating,
				intervall);
		events.add(ev);
		if (ev.isRepeating()) {
			repeatingEvents.add(ev);
		}
	}

	public void addEvent(Event e) {
		events.add(e);
		if (e.isRepeating()) {
			repeatingEvents.add(e);
		}
	}
	
	public void addToRepeated(Event e) {
		this.repeatingEvents.add(e);
	}

	/**
	 * obtain a list of events a user is allowed to see in a calendar for a
	 * given date.
	 **/
	public LinkedList<Event> getDayEvents(Date day, User requester) {
		// temporary result linked list
		LinkedList<Event> events_tmp = new LinkedList<Event>();

		// test if requester references to same object as owner. if true, we
		// have the same user,
		// therefore, the requester gets full access to its calendar data (since
		// he is the owner of it)
		boolean is_owner = owner == requester;

		// requester is owner
		if (is_owner) {
			for (Event e : events)
				if (e.getStart().equals(day))
					events_tmp.add(e);

			// requester is not the owner of the calendar ==> do visibility
			// check.
		} else {
			for (Event e : events)
				if (e.isVisible() && e.getStart().equals(day))
					events_tmp.add(e);
		}
		return events_tmp;
	}

	public Iterator<Event> getEventList(Date start, User requester) {
		// temporary result linked list
		LinkedList<Event> events_tmp = new LinkedList<Event>();

		// test if requester references to same object as owner. if true, we
		// have the same user,
		// therefore, the requester gets full access to its calendar data (since
		// he is the owner of it)
		boolean is_owner = owner == requester;

		// requester is owner
		if (is_owner) {
			for (Event e : events)
				if ((e.getStart().after(start) || e.getStart().equals(start)))
					events_tmp.add(e);

			// requester is not the owner of the calendar ==> do visibility
			// check.
		} else {
			for (Event e : events)
				if (e.isVisible()
						&& (e.getStart().after(start) || e.getStart().equals(
								start)))
					events_tmp.add(e);
		}
		// Iterator itr = al.iterator();
		Iterator<Event> iter = events_tmp.iterator();
		return iter;
	}

	// return all visible events of a given months => for graphical calendar
	public LinkedList<Event> getEventsOfDay(int day, int month, int year) {
		LinkedList<Event> result = new LinkedList<Event>();

		Date comp = null;
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm");
		String dateString = Integer.toString(day) + "/"
				+ Integer.toString(month) + "/" + Integer.toString(year)
				+ ", 12:00";
		try {
			comp = dateFormat.parse(dateString);
		} catch (ParseException e) {
		}
		
		for (Event e : events)
			if (e.start.getDate() == comp.getDate()
					&& e.start.getMonth() == comp.getMonth()
					&& e.start.getYear() == comp.getYear()) {
					result.add(e);
			}

		return result;
	}

	public boolean hasEventOnDay(int day, int month, int year) {
		boolean flag = false;
		Date comp = null;
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm");
		String dateString = Integer.toString(day) + "/"
				+ Integer.toString(month) + "/" + Integer.toString(year)
				+ ", 12:00";
		try {
			comp = dateFormat.parse(dateString);
			
		} catch (ParseException e) {
		}
		
		for (Event repeatingEvent: this.repeatingEvents) {
			Event repeatingEventOnDay = repeatingEvent.getRepetitionOfDate(comp);
			if (repeatingEventOnDay != null && !containsSameElement(new LinkedList<Event>(this.events), repeatingEventOnDay)) {
				System.out.println("added: " + repeatingEventOnDay.start);
				events.add(repeatingEventOnDay);
			}
		}
		
		for (Event e : events)
			if (e.start.getDate() == comp.getDate()
					&& e.start.getMonth() == comp.getMonth()
					&& e.start.getYear() == comp.getYear()) {
				flag = true;
			}
		return flag;
	}

	private boolean containsSameElement(LinkedList<Event> events, Event repeatingEvent) {
		boolean contains = false;
		for (Event e : this.events) {
			if (e.getBaseID() == repeatingEvent.getBaseID() && e.start.getDate() == repeatingEvent.start.getDate()
					&& e.start.getMonth() == repeatingEvent.start.getMonth()
					&& e.start.getYear() == repeatingEvent.start.getYear()) {
				contains = true;
			}
		}
		return contains;
	}

	public Event getEventById(long id) {
		Event result = null;
		for (Event e : events)
			if (e.getId() == id)
				result = e;
		return result;
	}

	// removes an event by its id
	public void removeEvent(long id) {
		LinkedList<Event> events = new LinkedList<Event>(this.events);
		for (Event e : events)
			if (e.getId() == id) {
				this.events.remove(e);
				this.repeatingEvents.remove(e);
			}
	}
	
	public void removeRepeatingEvents(Event event) {
		LinkedList<Event> events = new LinkedList<Event>(this.events);
		for (Event e : events) {
			if(e.getBaseID() == event.getBaseID()) {
				removeEvent(e.getId());
			}
		}
	}
	
}
