package models;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class Calendar {
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

	/**
	 * Adds a given event to this calendars list of events.
	 * If the provided event is a repeating event, it will also be added to the repeatingEvents list.
	 * @param e event which is added to events, and if repeating, to repeatingEvents.
	 */
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
	 * 
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
			System.out.println("comp.getDate: " + comp.getDate() + ", " + comp.getMonth() + ", " + comp.getYear());
			
		} catch (ParseException e) {
			System.out.println("failed to parse date");
		}
		
		for (Event repeatingEvent: this.repeatingEvents) {
			Event repeatingEventOnDay = repeatingEvent.getRepetitionOnDate(comp);
			if (repeatingEventOnDay != null && !containsSameElement(new LinkedList<Event>(this.events), repeatingEventOnDay)) {
				events.add(repeatingEventOnDay);
			}
		}
		
		for (Event e : events) {
			if (e.start.getDate() == comp.getDate()
					&& e.start.getMonth() == comp.getMonth()
					&& e.start.getYear() == comp.getYear()) {
				flag = true;
			}
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

	/**
	 * Removes an event from this calendars list of events and from the list of repeating events.
	 * @param id id of the event to be removed.
	 */
	public void removeEvent(long id) {
		LinkedList<Event> events = new LinkedList<Event>(this.events);
		for (Event e : events)
			if (e.getId() == id) {
				this.events.remove(e);
				this.repeatingEvents.remove(e);
			}
	}
	
	/**
	 * Removes all events with the same baseID from this calendars list of events and this calendars list of public events.
	 * The provided events baseID is compared with all events from this calendar, matching events will be removed.
	 * @param event the event to be removed along with all events with the same baseID
	 * @see {@link Calendar#removeEvent(long id)}
	 */
	public void removeRepeatingEvents(Event event) {
		LinkedList<Event> events = new LinkedList<Event>(this.events);
		for (Event e : events) {
			if(e.getBaseID() == event.getBaseID()) {
				removeEvent(e.getId());
			}
		}
	}

	public PriorityQueue<Event> getEvents() {
		return this.events;
	}

	public LinkedList<Event> getRepeatingEvents() {
		return this.repeatingEvents;
	}
	
}
