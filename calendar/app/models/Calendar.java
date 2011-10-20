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
	public LinkedList<Event> getEventsOfDay(int day, int month, int year, User requester) {
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
		boolean is_owner = owner == requester;
		for (Event e : events)
			if(is_owner || e.is_visible){
				if (e.start.getDate() == comp.getDate()
						&& e.start.getMonth() == comp.getMonth()
						&& e.start.getYear() == comp.getYear()) {
						result.add(e);
				}
			}

		return result;
	}

	public boolean hasEventOnDay(int day, int month, int year, User requester) {
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
		boolean is_owner = owner == requester;
		for (Event repeatingEvent: this.repeatingEvents) {
			if(is_owner || repeatingEvent.is_visible){
				Event repeatingEventOnDay = repeatingEvent.getRepetitionOnDate(comp);
				if (repeatingEventOnDay != null && !containsSameElement(new LinkedList<Event>(this.events), repeatingEventOnDay)) {
					if(!repeatingEventOnDay.isDirty){
						//System.out.println(repeatingEventOnDay.start);
						events.add(repeatingEventOnDay);
					}
				}
			}
		}
		
		for (Event e : events) {
			if(is_owner || e.is_visible){
				if (e.start.getDate() == comp.getDate()
						&& e.start.getMonth() == comp.getMonth()
						&& e.start.getYear() == comp.getYear()) {
					flag = true;
				}
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
		// funktioniert atm nur für repeating events mit intervall = 1 | 7 oder für not-repeating events
		
		if(getEventById(id).is_repeating){
			LinkedList<Event> events = new LinkedList<Event>(this.events);
			System.out.println("i am repeating");
			for (Event e : events){
				if (e.getId() == id) {
					System.out.println("i am repeating");
					// mache hier löschprozedur für repeating events
					// idee1: berechne nächten event e' nach e gem. intervall
					// entferne alle diese repeating events via removeRepeatingEvents(Event event)
					// erzeuge neuen repeating event, startend bei e' mit selben intervall 
					// dazu mache: 1. finde heraus ob e ist repeating event bzw der assozierte e.baseID event.
					// wenn ja, mache das oben, sonst reguläres löschen
					Event baseEvent = getEventById(e.baseID); // korrektes nächstes date
					int intervall = baseEvent.intervall;
					Date nextRepStartDate = new Date(e.start.getYear(),e.start.getMonth(), e.start.getDate() + intervall, e.start.getHours(), e.start.getMinutes());
					Date nextRepEndDate = new Date(e.end.getYear(), e.end.getMonth(), e.end.getDate() + intervall, e.start.getHours(), e.start.getMinutes());
					Event nextEvent = new Event(nextRepStartDate, nextRepEndDate, e.name, e.is_visible, true, intervall);		
					System.out.println("old: "+e.start + " new: "+nextRepStartDate);
					// finde daten heraus, welche man wieder in ab baseEven{old} einfügen muss
					// bemerke: kann beliebig viele "löcher" haben.
					// iteriere ab base bis kleiner als lücke und ermittle daten: 
					// if(has_an_event_in date_i) then vermerke, dass zu adden
					// nach removeRepeatingEvents diese dann in this.events hinzufügen
					
					// HIER CODE EINFÜGEN
					
					// entferne die repeating events und mache dann neu
					removeRepeatingEvents(baseEvent);
					
					// adde hier alle in this.events.add(nextEvent) bis 1 kleiner als lücke
					// HIER CODE EINFÜGEN
					this.events.add(base);
					
					// füge neuer repeating baseEvent ein (ein intervall nach der lücke)
					this.events.add(nextEvent);
					if (nextEvent.isRepeating()) { //unnötig
						repeatingEvents.add(nextEvent);
					}
				}
			}
		// sonst, wenn event kein repeating event ist	
		}else{ 
			LinkedList<Event> events = new LinkedList<Event>(this.events);
			System.out.println("im NOT repeating");
			for (Event e : events)
				if (e.getId() == id) {
					this.events.remove(e);
					this.repeatingEvents.remove(e);
				}
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
				e.is_repeating = false; // ultra important , otherwise we will have an infinite recursion!!!
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
