package models;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;

import models.Event.Visibility;

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
	 * Adds a given event to this calendars list of events. If the provided
	 * event is a repeating event, it will also be added to the repeatingEvents
	 * list.
	 * 
	 * @param e
	 *            event which is added to events, and if repeating, to
	 *            repeatingEvents.
	 */
	public void addEvent(Event e) {
		// teste ob dieser eventbereits in liste ist
		// wenn doch, dann adde diesen nicht!
		System.out.println(!compareCalendarEvents(e));
		if (!compareCalendarEvents(e)) {
			events.add(e);
			if (e.isRepeating()) {
				repeatingEvents.add(e);
			}
		}

	}

	// test if in this.events or in repeatingEvents is an event with same date
	// (start,end) and name as ev1
	private boolean compareCalendarEvents(Event ev1) {
		boolean flag = false;
		for (Event comp : this.events) {
			if (comp.name.equals(ev1.name))
				if (comp.start.getYear() == ev1.start.getYear()
						&& comp.start.getMonth() == ev1.start.getMonth()
						&& comp.start.getDate() == ev1.start.getDate()
						&& comp.start.getMinutes() == ev1.start.getMinutes()
						&& comp.start.getSeconds() == ev1.start.getSeconds()// &&
				// comp.end.getYear() == ev1.end.getYear() &&
				// comp.end.getMonth() == ev1.end.getMonth() &&
				// comp.end.getDate() == ev1.end.getDate() &&
				// comp.end.getMinutes() == ev1.end.getMinutes() &&
				// comp.end.getSeconds() == ev1.end.getSeconds()

				) {
					flag = true;
					break;
				}
		}
		if (!flag) {
			for (Event comp : this.repeatingEvents) {
				if (comp.name.equals(ev1.name))
					if (comp.start.getYear() == ev1.start.getYear()
							&& comp.start.getMonth() == ev1.start.getMonth()
							&& comp.start.getDate() == ev1.start.getDate()
							&& comp.start.getMinutes() == ev1.start
									.getMinutes()
							&& comp.start.getSeconds() == ev1.start
									.getSeconds() // &&
					// comp.end.getYear() == ev1.end.getYear() &&
					// comp.end.getMonth() == ev1.end.getMonth() &&
					// comp.end.getDate() == ev1.end.getDate() &&
					// comp.end.getMinutes() == ev1.end.getMinutes() &&
					// comp.end.getSeconds() == ev1.end.getSeconds()

					) {
						flag = true;
						break;
					}
			}
		}
		return flag;
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
				if (e.getVisibility() != Visibility.PRIVATE
						&& e.getStart().equals(day))
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
				if (e.getVisibility() != Visibility.PRIVATE
						&& (e.getStart().after(start) || e.getStart().equals(
								start)))
					events_tmp.add(e);
		}
		// Iterator itr = al.iterator();
		Iterator<Event> iter = events_tmp.iterator();
		return iter;
	}

	// return all visible events of a given months => for graphical calendar
	public LinkedList<Event> getEventsOfDay(int day, int month, int year,
			User requester) {
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

		LinkedList<Calendar> observedCals = owner.getObservedCalendars();
		LinkedList<Long> shownObservedCals = owner.getShownObservedCalendars();
		LinkedList<Event> repeatingEvents = new LinkedList<Event>(
				this.repeatingEvents);
		PriorityQueue<Event> events = new PriorityQueue<Event>(this.events);

		for (Calendar observedCal : observedCals) {
			if (shownObservedCals.contains(observedCal.getId())) {
				for (Event observedEvent : observedCal.getEvents()) {
					if (observedEvent.getVisibility() != Visibility.PRIVATE
							&& !events.contains(observedEvent)) {
						events.add(observedEvent);
					}
				}
				for (Event repeatingObservedEvent : observedCal
						.getRepeatingEvents()) {
					if (repeatingObservedEvent.getVisibility() != Visibility.PRIVATE
							&& !repeatingEvents
									.contains(repeatingObservedEvent)) {
						repeatingEvents.add(repeatingObservedEvent);
					}
				}
			}
		}

		boolean is_owner = owner == requester;
		for (Event e : events) {
			if (is_owner || e.getVisibility() != Visibility.PRIVATE) {
				if (e.start.getDate() == comp.getDate()
						&& e.start.getMonth() == comp.getMonth()
						&& e.start.getYear() == comp.getYear()) {
					if (!result.contains(e))
						result.add(e);
				}
			}
		}

		for (Event repeatingEvent : repeatingEvents) {
			if (is_owner
					|| repeatingEvent.getVisibility() != Visibility.PRIVATE) {
				Event repeatingEventOnDay = repeatingEvent
						.getRepetitionOnDate(comp);
				if (repeatingEventOnDay != null
						&& !containsSameElement(new LinkedList<Event>(events),
								repeatingEventOnDay)) {
					if (!repeatingEventOnDay.isDirty) {
						if (!compareCalendarEvents(repeatingEventOnDay)
								&& !result.contains(repeatingEventOnDay)) {
							result.add(repeatingEventOnDay);
						}
					}
				}
			}
		}
		for (Event e : result) {
			if (!this.events.contains(e)) {
				this.events.add(e);
			}
		}
		return result;
	}

	// mache hier auch reperatur wengen event adden!!!!
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

		LinkedList<Calendar> observedCals = owner.getObservedCalendars();
		LinkedList<Long> shownObservedCals = owner.getShownObservedCalendars();
		LinkedList<Event> repeatingEvents = new LinkedList<Event>(
				this.repeatingEvents);
		PriorityQueue<Event> events = new PriorityQueue<Event>(this.events);

		for (Calendar observedCal : observedCals) {
			if (shownObservedCals.contains(observedCal.getId())) {
				for (Event observedEvent : observedCal.getEvents()) {
					if (observedEvent.getVisibility() != Visibility.PRIVATE) {
						events.add(observedEvent);
					}
				}
				for (Event repeatingObservedEvent : observedCal
						.getRepeatingEvents()) {
					if (repeatingObservedEvent.getVisibility() != Visibility.PRIVATE) {
						repeatingEvents.add(repeatingObservedEvent);
					}
				}
			}
		}
		boolean is_owner = owner == requester;
		for (Event repeatingEvent : repeatingEvents) {
			if (is_owner
					|| repeatingEvent.getVisibility() != Visibility.PRIVATE) {
				Event repeatingEventOnDay = repeatingEvent
						.getRepetitionOnDate(comp);
				if (repeatingEventOnDay != null
						&& !containsSameElement(new LinkedList<Event>(events),
								repeatingEventOnDay)) {
					if (!repeatingEventOnDay.isDirty) {
						// System.out.println(repeatingEventOnDay.start);
						if (!compareCalendarEvents(repeatingEventOnDay))
							events.add(repeatingEventOnDay);
					}
				}
			}
		}

		for (Event e : events) {
			if (is_owner || e.getVisibility() != Visibility.PRIVATE) {
				if (e.start.getDate() == comp.getDate()
						&& e.start.getMonth() == comp.getMonth()
						&& e.start.getYear() == comp.getYear()) {
					flag = true;
				}
			}
		}
		return flag;
	}

	// / fixe hier das adden!!! wenn schon vorhanden, dann nicht nochmals
	// adden!!!
	public boolean hasEventOnDay(Date date, User requester) {
		boolean flag = false;
		Date comp = date;

		boolean is_owner = owner == requester;
		for (Event repeatingEvent : this.repeatingEvents) {
			if (is_owner
					|| repeatingEvent.getVisibility() != Visibility.PRIVATE) {
				Event repeatingEventOnDay = repeatingEvent
						.getRepetitionOnDate(comp);
				if (repeatingEventOnDay != null
						&& !containsSameElement(new LinkedList<Event>(
								this.events), repeatingEventOnDay)) {
					if (!repeatingEventOnDay.isDirty) {
						// System.out.println(repeatingEventOnDay.start);
						// check neu :::
						if (!compareCalendarEvents(repeatingEventOnDay))
							events.add(repeatingEventOnDay);
					}
				}
			}
		}

		for (Event e : events) {
			if (is_owner || e.getVisibility() != Visibility.PRIVATE) {
				if (e.start.getDate() == comp.getDate()
						&& e.start.getMonth() == comp.getMonth()
						&& e.start.getYear() == comp.getYear()) {
					flag = true;
				}
			}
		}
		return flag;
	}

	private boolean containsSameElement(LinkedList<Event> events,
			Event repeatingEvent) {
		boolean contains = false;
		for (Event e : this.events) {
			if (e.getBaseID() == repeatingEvent.getBaseID()
					&& e.start.getDate() == repeatingEvent.start.getDate()
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
	 * Removes an event from this calendars list of events and from the list of
	 * repeating events.
	 * 
	 * @param id
	 *            id of the event to be removed.
	 */
	@SuppressWarnings("deprecation")
	public void removeEvent(long id) {
		// we call the event we are going to delete victim event or simply
		// victim
		// REM:
		// funktioniert atm nur für repeating events mit intervall = 1 | 7 oder
		// für not-repeating events
		// ERROR end NOT equal d => fix it later!!! fix END
		// fix: conserve wholes after victim
		// fix: multiple times add same event

		// if our victim is a repeating event
		if (getEventById(id).is_repeating) {
			LinkedList<Event> events = new LinkedList<Event>(this.events);
			System.out.println("i am repeating");
			for (Event e : events) {
				if (e.getId() == id) {

					// calculate next event e' after e, depending on intervall
					// size
					// remove all those repeating events by calling
					// removeRepeatingEvents(Event event)
					// create new repeating event, which starts at e' and same
					// intervall value
					// 1. test, if e is a repeating event respectivly the
					// correlated event to e.baseID
					// if yes, then do what desribed abouve, else delete
					// regulary

					// calculate next date after victim event
					Event baseEvent = getEventById(e.baseID); // korrektes
																// nächstes date
					int intervall = baseEvent.intervall;
					Date nextRepStartDate = new Date(e.start.getYear(),
							e.start.getMonth(), e.start.getDate() + intervall,
							e.start.getHours(), e.start.getMinutes());
					Date nextRepEndDate = new Date(e.end.getYear(),
							e.end.getMonth(), e.end.getDate() + intervall,
							e.start.getHours(), e.start.getMinutes());
					Event nextEvent = new Event(this.owner, nextRepStartDate,
							nextRepEndDate, e.name, e.visibility, true,
							intervall);
					nextEvent.baseID = nextEvent.id;

					// System.out.println("old: "+e.start +
					// " new: "+nextRepStartDate);

					// this list contains all dates of repeating events of
					// baseEvent, till 1 event before victim event
					LinkedList<Date> previousDates = new LinkedList<Date>();
					ArrayList<String> descriptions = new ArrayList<String>();
					if (intervall == 7 || intervall == 1) {

						Date current = baseEvent.start;

						// iterate in interval steps iterate from
						// "baseEvent.start" to "nextRepStartDate"
						// to find dates, which we have to reinsert starting
						// from baseEven{old}
						// remark: there could be arbitrary many whole in the
						// interval previous and afterwards our victim a
						while (current.compareTo(nextRepStartDate) == -1) {
							// if this date is in the calendar as an event
							// then add it to the list of dates we should
							// recreate events
							// remark: this is needed since we could have whole
							// in repeating event series
							// if we would not do such a check, then we would
							// have a events with dates
							// we did not have in the calendar to it's previous
							// state
							if (hasEventOnDay(current, owner)) {
								// fix for wholes in interval previous victim
								LinkedList<Event> dayevents = getDayEvents(
										current, owner);
								
								for(Event eee : dayevents){
									if(eee.baseID == e.baseID){
										//System.out.println("date: "+eee.start+" ebeschr: "+eee.description);
										descriptions.add(eee.description);
									}
								}
								
								if (hasName(e.name, dayevents)){
									previousDates.add(current);
									//System.out.println("date: "+e.start+" ebeschr: "+e.description);
									
								}
									
							}

							// get next date depending an interval-step-size
							current = new Date(current.getYear(),
									current.getMonth(), current.getDate()
											+ intervall, current.getHours(),
									current.getMinutes());
						}

						// remove last element of list, this is the victim we
						// want to delete
						previousDates.removeLast();
					} else if (intervall == 30) {
						Date current = baseEvent.start;
						while (current.compareTo(nextRepStartDate) == -1) {
							if (hasEventOnDay(current, owner)) {
								// fix for wholes in interval previous victim
								LinkedList<Event> dayevents = getDayEvents(
										current, owner);
								if (hasName(e.name, dayevents))
									previousDates.add(current);
							}

							// get next date depending an interval-step-size
							current = new Date(current.getYear(),
									current.getMonth() + 1, current.getDate(),
									current.getHours(), current.getMinutes());
						}
					} else if (intervall == 365) {
						Date current = baseEvent.start;
						while (current.compareTo(nextRepStartDate) == -1) {
							if (hasEventOnDay(current, owner)) {
								// fix for wholes in interval previous victim
								LinkedList<Event> dayevents = getDayEvents(
										current, owner);
								if (hasName(e.name, dayevents))
									previousDates.add(current);
							}

							// get next date depending an interval-step-size
							current = new Date(current.getYear() + 1,
									current.getMonth(), current.getDate(),
									current.getHours(), current.getMinutes());
						}
					} else {
						// if we are inside this block something went horribly
						// wrong => doomed
						try {
							throw new Exception("huge error - damnit");
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}

					// remove all repeating events correlated to baseEvent
					removeRepeatingEvents(baseEvent);
					int index = 0;
					// add for each date in the collected date list a event into
					// this.events
					// equals all events previous victim
					for (Date d : previousDates) {
						// ERROR end NOT equal d => fix it later!!!
						Event ev = new Event(this.owner, d, d, e.name,
								e.visibility, false, intervall);
						ev.editDescription(descriptions.get(index));
						this.events.add(ev);
						index++;
					}

					// add the event after our victim (1 date interval
					// afterwards) into this.events and repeatingEvents
					// or check for next free slot , ie find this date c

					if (!compareCalendarEvents(nextEvent)) {
						this.events.add(nextEvent);
						if (nextEvent.isRepeating()) { // unnötig
							repeatingEvents.add(nextEvent);
						}
					}

				}
			}

			// else if our victim is not a repeating event
		} else {
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
	 * Removes all events with the same baseID from this calendars list of
	 * events and this calendars list of public events. The provided events
	 * baseID is compared with all events from this calendar, matching events
	 * will be removed.
	 * 
	 * @param event
	 *            the event to be removed along with all events with the same
	 *            baseID
	 * @see {@link Calendar#removeEvent(long id)}
	 */
	public void removeRepeatingEvents(Event event) {
		LinkedList<Event> events = new LinkedList<Event>(this.events);
		for (Event e : events) {
			if (e.getBaseID() == event.getBaseID()) {
				e.is_repeating = false; // ultra important , otherwise we will
										// have an infinite recursion!!!
				removeEvent(e.getId());
			}
		}
	}

	/**
	 * End repetition of an repeating event from a given date cancelDate remove
	 * all repeating dates after cancelDate, which are already calculated. Mark
	 * all such repeating events till and with canelDate as NOT repeating.
	 */
	public void cancelRepeatingEventRepetitionFromDate(Event cancelEvent) {
		System.out.println("cancel from this event: " + cancelEvent.start);
		boolean flag = true;
		Date cursor = cancelEvent.start;
		int intervall = cancelEvent.intervall;
		Event from = getEventById(cancelEvent.baseID);
		LinkedList<Event> res = getEventRepeatingFromTo(from, cancelEvent.start);
		res.add(cancelEvent);
		for (Event e : res)
			e.is_repeating = false;
		// output of events

		for (Event e : res)
			System.out.println(e.start);
		removeRepeatingEvents(cancelEvent);
		for (Event e : res)
			addEvent(e);
	}

	/**
	 * Starting from an Event "from" put in from.interval steps till "to" events
	 * in the calendar
	 * 
	 * @param from
	 * @param to
	 */
	public LinkedList<Event> getEventRepeatingFromTo(Event from, Date to) {
		Event cursor = from;
		LinkedList<Event> result = new LinkedList<Event>();
		while (true) {
			if (cursor.start.getYear() == to.getYear()
					&& cursor.start.getMonth() == to.getMonth()
					&& cursor.start.getDate() == to.getDate()
					&& cursor.start.getMinutes() == to.getMinutes()
					&& cursor.start.getSeconds() == to.getSeconds()// &&
			) {
				break;
			}

			result.add(cursor);
			// get next cursor
			cursor = cursor.getNextRepetitionEvent();
		}
		return result;
	}

	public PriorityQueue<Event> getEvents() {
		return this.events;
	}

	public LinkedList<Event> getRepeatingEvents() {
		return this.repeatingEvents;
	}

	/**
	 * helper to test if there are events in a given list which have the same
	 * name as "name" if yes, then return true, otherwise return false.
	 * 
	 * @param name
	 * @param dayevents
	 * @return boolean
	 */
	private boolean hasName(String name, LinkedList<Event> dayevents) {
		boolean flag = false;
		for (Event ev : dayevents)
			if (ev.name.equals(name))
				flag = true;

		return flag;
	}

}
