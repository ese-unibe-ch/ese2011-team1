package models;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import models.Event.Visibility;

public class UserCalendar extends Calendar{
	private String name;
	public User owner;
	private PriorityQueue<Event> events;
	public long id;
	private LinkedList<Event> repeatingEvents;
	private static long counter;

	public UserCalendar(String name, User owner) {
		super(owner);
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
				if (comp.getStart().equals(ev1.getStart())) {
					flag = true;
					break;
				}
		}
		if (!flag) {
			for (Event comp : this.repeatingEvents) {
				if (comp.name.equals(ev1.name))
					if (comp.getStart().equals(ev1.getStart())) {
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
	public LinkedList<Event> getDayEvents(DateTime day, User requester) {
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

	public Iterator<Event> getEventList(DateTime start, User requester) {
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
				if ((e.getStart().isAfter(start) || e.getStart().equals(start)))
					events_tmp.add(e);

			// requester is not the owner of the calendar ==> do visibility
			// check.
		} else {
			for (Event e : events)
				if (e.getVisibility() != Visibility.PRIVATE
						&& (e.getStart().isAfter(start) || e.getStart().equals(
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
		DateTimeFormatter dateTimeInputFormatter = DateTimeFormat.forPattern("dd/MM/yyyy, HH:mm");

		DateTime comp = null;
//		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm");
		String dateString = Integer.toString(day) + "/"
				+ Integer.toString(month) + "/" + Integer.toString(year)
				+ ", 12:00";
		comp = dateTimeInputFormatter.parseDateTime(dateString);

		LinkedList<Calendar> observedCals = owner.getObservedCalendars();
		LinkedList<Long> shownObservedCals = owner.getShownObservedCalendars();
		LinkedList<Event> repeatingEvents = new LinkedList<Event>();
		repeatingEvents.addAll(this.repeatingEvents);
		PriorityQueue<Event> events = new PriorityQueue<Event>();
		events.addAll(this.events);
		
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
					if (repeatingObservedEvent instanceof BirthdayEvent) {
						if (this.owner == repeatingObservedEvent.owner) {
							repeatingEvents.add(repeatingObservedEvent);
						} else {
							for (Calendar cal : observedCals) {
								if (cal.owner == repeatingObservedEvent.owner && repeatingObservedEvent.isVisible()) {
									repeatingEvents.add(repeatingObservedEvent);
								}
							}
						}
						
					} else {
						if (repeatingObservedEvent.getVisibility() != Visibility.PRIVATE
								&& !repeatingEvents.contains(repeatingObservedEvent)) {
							repeatingEvents.add(repeatingObservedEvent);
						}
					}
				}
			}
		}

		boolean is_owner = owner == requester;
		for (Event e : events) {
			if (is_owner || e.getVisibility() != Visibility.PRIVATE) {
//				e.start.getDate() == comp.getDate()
//						&& e.start.getMonth() == comp.getMonth()
//						&& e.start.getYear() == comp.getYear()
				System.out.println("e:localDate: " + e.getStart().toLocalDate() + "comp:localDate: " + comp.toLocalDate());
				if (e.getStart().toLocalDate().equals(comp.toLocalDate())) {
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
		
		System.out.println("all possible events to display: " + result);
		return result;
	}
	
	// return a list of events which have the same baseId
	// if an event is not repeating then this method returns
	// the same event like getEventById(id). 
	// We for the events in this.events and this.repeatingEvents
	public LinkedList<Event> getSameBaseIdEvents(long baseId){
		LinkedList<Event> result = new LinkedList<Event>();
		for(Event event : this.events){
			if(event.baseId==baseId){
				result.add(event);
			}
		}
		
		for(Event event : this.repeatingEvents){
			if(event.baseId==baseId){
				result.add(event);
			}
		}
		// we should sort this list by its event's start dates
		// this is a ToDo
		return result;
	}
	
	// mache hier auch reperatur wengen event adden!!!!
	public boolean hasEventOnDay(int day, int month, int year, User requester) {
		DateTimeFormatter dateTimeInputFormatter = DateTimeFormat.forPattern("dd/MM/yyyy, HH:mm");

		boolean flag = false;
		DateTime comp = null;
//		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm");
		String dateString = Integer.toString(day) + "/"
				+ Integer.toString(month) + "/" + Integer.toString(year)
				+ ", 12:00";
		comp = dateTimeInputFormatter.parseDateTime(dateString);

		LinkedList<Calendar> observedCals = owner.getObservedCalendars();
		LinkedList<Long> shownObservedCals = owner.getShownObservedCalendars();
		LinkedList<Event> repeatingEvents = new LinkedList<Event>();
		repeatingEvents.addAll(this.repeatingEvents);
		PriorityQueue<Event> events = new PriorityQueue<Event>();
		events.addAll(this.events);
		
		for (Calendar observedCal : observedCals) {
			if (shownObservedCals.contains(observedCal.getId())) {
				for (Event observedEvent : observedCal.getEvents()) {
					if (observedEvent.getVisibility() != Visibility.PRIVATE) {
						events.add(observedEvent);
					}
				}
				for (Event repeatingObservedEvent : observedCal.getRepeatingEvents()) {
					if (repeatingObservedEvent instanceof BirthdayEvent) {
						if (this.owner == repeatingObservedEvent.owner) {
							repeatingEvents.add(repeatingObservedEvent);
						} else {
							for (Calendar cal : observedCals) {
								if (cal.owner == repeatingObservedEvent.owner && repeatingObservedEvent.isVisible()) {
									repeatingEvents.add(repeatingObservedEvent);
								}
							}
						}
					}
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
						if (!compareCalendarEvents(repeatingEventOnDay))
							events.add(repeatingEventOnDay);
					}
				}
			}
		}

		for (Event e : events) {
			if (is_owner || e.getVisibility() != Visibility.PRIVATE) {
				if (e.getStart().toLocalDate().equals(comp.toLocalDate())) {
					flag = true;
				}
			}
		}
		return flag;
	}

	// / fixe hier das adden!!! wenn schon vorhanden, dann nicht nochmals
	// adden!!!
	public boolean hasEventOnDay(DateTime date, User requester) {
		boolean flag = false;
		DateTime comp = date;

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
				if (e.getStart().toLocalDate().equals(comp.toLocalDate())) {
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
			if (e.getStart().toLocalDate().equals(repeatingEvent.getStart().toLocalDate())) {
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
			//System.out.println("i am repeating");
			Event sentinel = getEventById(id);
			LinkedList<Event> interestingevents = getSameBaseIdEvents(sentinel.baseId);
			
			for(Event event : interestingevents){
				System.out.println("e date"+event.start);
			}
			
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
					Event baseEvent = getEventById(e.baseId); // korrektes
																// nächstes date
					
					int intervall = baseEvent.intervall;
					DateTime nextRepStartDate = new DateTime(e.start.getYear(),
							e.start.getMonthOfYear(), e.start.getDayOfMonth() + intervall,
							e.start.getHourOfDay(), e.start.getMinuteOfHour());
					DateTime nextRepEndDate = new DateTime(e.end.getYear(),
							e.end.getMonthOfYear(), e.end.getDayOfMonth() + intervall,
							e.start.getHourOfDay(), e.start.getMinuteOfHour());
					Event nextEvent = new Event(this.owner, nextRepStartDate,
							nextRepEndDate, e.name, e.visibility, true,
							intervall);
					nextEvent.baseId = nextEvent.id;

					// System.out.println("old: "+e.start +
					// " new: "+nextRepStartDate);

					// this list contains all dates of repeating events of
					// baseEvent, till 1 event before victim event
					LinkedList<DateTime> previousDates = new LinkedList<DateTime>();
					ArrayList<String> descriptions = new ArrayList<String>();
					if (intervall == 7 || intervall == 1) {

						DateTime current = baseEvent.start;

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
								
								// get for each event before victim its event description 
								for(Event eee : dayevents){
									if(eee.baseId == e.baseId){
										descriptions.add(eee.description);
									}
								}
								
								if (hasName(e.name, dayevents)){
									previousDates.add(current);
								}
							}

							// get next date depending an interval-step-size
							current = new DateTime(current.getYear(),
									current.getMonthOfYear(), current.getDayOfMonth()
											+ intervall, current.getHourOfDay(),
									current.getMinuteOfHour());
						}

						// remove last element of list, this is the victim we
						// want to delete
						previousDates.removeLast();
					} else if (intervall == 30) {
						DateTime current = baseEvent.start;
						while (current.compareTo(nextRepStartDate) == -1) {
							if (hasEventOnDay(current, owner)) {
								// fix for wholes in interval previous victim
								LinkedList<Event> dayevents = getDayEvents(
										current, owner);
								for(Event eee : dayevents)
									if(eee.baseId == e.baseId)
										descriptions.add(eee.description);
									
								if (hasName(e.name, dayevents))
									previousDates.add(current);
							}

							// get next date depending an interval-step-size
							current = new DateTime(current.getYear(),
									current.getMonthOfYear() + 1, current.getDayOfMonth(),
									current.getHourOfDay(), current.getMinuteOfHour());
						}
					} else if (intervall == 365) {
						DateTime current = baseEvent.start;
						while (current.compareTo(nextRepStartDate) == -1) {
							if (hasEventOnDay(current, owner)) {
								// fix for wholes in interval previous victim
								LinkedList<Event> dayevents = getDayEvents(
										current, owner);
								for(Event eee : dayevents)
									if(eee.baseId == e.baseId)
										descriptions.add(eee.description);
								
								if (hasName(e.name, dayevents))
									previousDates.add(current);
							}

							// get next date depending an interval-step-size
							current = new DateTime(current.getYear() + 1,
									current.getMonthOfYear(), current.getDayOfMonth(),
									current.getHourOfDay(), current.getMinuteOfHour());
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
					// before we remove the events after victim
					// preserve all descriptions after victim
					
					// remove all repeating events correlated to baseEvent
					removeRepeatingEvents(baseEvent);
					int index = 0;
					// add for each date in the collected date list a event into
					// this.events
					// equals all events previous victim
					for (DateTime d : previousDates) {
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
	 * @see {@link UserCalendar#removeEvent(long id)}
	 */
	public void removeRepeatingEvents(Event event) {
		LinkedList<Event> events = new LinkedList<Event>(this.events);
		for (Event e : events) {
			if (e.getBaseId() == event.getBaseId()) {
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
//		boolean flag = true;
//		DateTime cursor = cancelEvent.start;
//		int intervall = cancelEvent.intervall;
		Event from = getEventById(cancelEvent.baseId);
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
	public LinkedList<Event> getEventRepeatingFromTo(Event from, DateTime to) {
		Event cursor = from;
		LinkedList<Event> result = new LinkedList<Event>();
		while (true) {
			if (cursor.start.toLocalDate() == to.toLocalDate()) {
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
