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

/**
 * The Calendar class represents a container to store and arrange multiple Events.
 * 
 * A Calendar is a container for multiple Events. It knows which Event happens
 * on which Date and lets its user interfere with its Events. Calendars are responsible for maintaining all
 * Events they contain, which includes editing/removing.
 * 
 * @see {@link Event}
 * @see {@link User}
 * 
 */
public class Calendar {
	public long id;
	public User owner;
	private String name;
	private PriorityQueue<Event> events;
	private LinkedList<Event> repeatingEvents;
	private static long counter;

	/**
	 * Create a new Calendar.
	 * 
	 * @param name
	 *            The name of this Calendar.
	 * @param owner
	 *            The owner of this Calendar.
	 * @see {@link User}
	 */
	public Calendar(String name, User owner) {
		this.name = name;
		this.owner = owner;
		events = new PriorityQueue<Event>();
		repeatingEvents = new LinkedList<Event>();
		counter++;
		this.id = counter;
	}

	/**
	 * Get the owner of this calendar.
	 * 
	 * @return the owner of this calendar.
	 */
	public User getOwner() {
		return this.owner;
	}

	/**
	 * Get the name of this Calendar.
	 * 
	 * @return The name of this Calendar.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Get the unique Id of this Calendar.
	 * 
	 * @return the id of this Calendar.
	 */
	public long getId() {
		return this.id;
	}

	/**
	 * Adds a given event to this calendars list of events. If the provided
	 * event is a repeating event, it will also be added to the repeatingEvents
	 * list.
	 * 
	 * @param event
	 *            event which is added to events, and if repeating, to
	 *            repeatingEvents.
	 */
	public void addEvent(Event event) {
		// teste ob dieser eventbereits in liste ist
		// wenn doch, dann adde diesen nicht!
		System.out.println(!compareCalendarEvents(event));
		if (!compareCalendarEvents(event)) {
			events.add(event);
			if (event.isRepeating()) {
				repeatingEvents.add(event);
			}
		}

	}

	/**
	 * Test if this Calendar contains an Event with the same name and start date
	 * as the argument.
	 * 
	 * The given argument is compared to all events in this Calendars
	 * <code>events</code> and <code>repeatingEvents</code>.
	 * 
	 * @param event
	 *            The event to be compared with all Events in
	 *            <code>events</code> and <code>repeatingEvents</code>.
	 * @return <code>true</code> if any Event in <code>events</code> and
	 *         <code>repeatingEvents</code> has the same name and start date as
	 *         the given argument. <code>false</code> otherwise.
	 */
	// test if in this.events or in repeatingEvents is an event with same date
	// (start,end) and name as ev1
	private boolean compareCalendarEvents(Event event) {
		boolean flag = false;
		for (Event comp : this.events) {
			if (comp.name.equals(event.name))
				if (comp.start.getYear() == event.start.getYear()
						&& comp.start.getMonth() == event.start.getMonth()
						&& comp.start.getDate() == event.start.getDate()
						&& comp.start.getMinutes() == event.start.getMinutes()
						&& comp.start.getSeconds() == event.start.getSeconds()// &&
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
				if (comp.name.equals(event.name))
					if (comp.start.getYear() == event.start.getYear()
							&& comp.start.getMonth() == event.start.getMonth()
							&& comp.start.getDate() == event.start.getDate()
							&& comp.start.getMinutes() == event.start
									.getMinutes()
							&& comp.start.getSeconds() == event.start
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

	/**
	 * Adds a given Event to this Calendars list of repeatingEvents.
	 * 
	 * @param event
	 *            The event to be added to <code>repeatingEvents</code>
	 */
	public void addToRepeated(Event event) {
		this.repeatingEvents.add(event);
	}

	/**
	 * Obtain a list of Events a User is allowed to see in a Calendar for a
	 * given date.
	 * 
	 * @param day
	 *            The date on which the Events must happen in order to be
	 *            returned.
	 * @param requester
	 *            The User which requests the List of Events.
	 * @return
	 */
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

	/**
	 * Get an Iterator over all Events in this Calendar which occur after the
	 * specified date.
	 * 
	 * The returned Iterator contains different Events depending on the
	 * requesting Users permissions.
	 * 
	 * @param start
	 *            The start date for the Iterator.
	 * @param requester
	 *            The User which requests this Iterator.
	 * @return Iterator over all Events of this Calendar after the specified
	 *         Date.
	 */
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

	/**
	 * Get all visible Events of this calendar for a specified Date.
	 * 
	 * Returns a list of Events containing all Events who are visible for the
	 * requester and whose start date is equal to the date composed of
	 * <code>day</code>, <code>month</code> and <code>year</code>.
	 * 
	 * 
	 * @param day
	 *            The day on which an Event must happen in order to be returned.
	 * @param month
	 *            The month in which an Event must happen in order to be
	 *            returned.
	 * @param year
	 *            The year in which an Event must happen in order to be
	 *            returned.
	 * @param requester
	 *            The user which requests this List.
	 * @return List of Events which happen on the Date specified by
	 *         <code>day</code>, <code>month</code> and <code>year</code>
	 * @see {@link Event}
	 */
	// return all visible events of a given months => for graphical calendar
	public LinkedList<Event> getEventsOfDay(int day, int month, int year,
			User requester) {
		LinkedList<Event> result = new LinkedList<Event>();

		Date comp = null;
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm");
		String dateString = Integer.toString(day) + "/"
				+ Integer.toString(month) + "/" + Integer.toString(year)
				+ ", 00:00";
		try {
			comp = dateFormat.parse(dateString);
		} catch (ParseException e) {
		}

		LinkedList<Calendar> observedCals = owner.getObservedCalendars();
		LinkedList<Long> shownObservedCals = owner.getShownObservedCalendars();
		LinkedList<Event> repeatingEvents = new LinkedList<Event>();
		repeatingEvents.addAll(this.repeatingEvents);
		PriorityQueue<Event> events = new PriorityQueue<Event>();
		events.addAll(this.events);
		LinkedList<Event> otherUsersBirthdays = new LinkedList<Event>();


		if (shownObservedCals.contains(owner.getBirthdayCalendar().id)) {
			for (Calendar observedCal : observedCals) {
				otherUsersBirthdays.add(observedCal.owner.getBirthday());
			}
		}
		
		for (Event e : otherUsersBirthdays) {
			if (e.isVisible()) {
				for (Calendar observedCal : observedCals) {
					if (observedCal.getId() == owner.getBirthdayCalendar().getId() && !observedCal.compareCalendarEvents(e)) {
						observedCal.addEvent(e);
					}
				}
			}
		}
		
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
					for (Calendar cal : observedCals) {
						if (cal.owner == repeatingObservedEvent.owner
								&& repeatingObservedEvent.isVisible()) {
							repeatingEvents.add(repeatingObservedEvent);
						}
					}
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

	/**
	 * Returns a list of Events which have the same baseId as the argument. If
	 * an Event is not repeating, this method returns the same Event like
	 * {@link Calendar#getEventById}
	 * 
	 * @param baseId
	 *            The Id which is compared to all Events in this Calendars
	 *            <code>events</code> and <code>repeatingEvents</code>
	 * @return A List of events containing all Events of this Calendar with the
	 *         same baseId as the argument.
	 * 
	 */
	public LinkedList<Event> getSameBaseIdEvents(long baseId) {
		LinkedList<Event> result = new LinkedList<Event>();
		for (Event event : this.events) {
			if (event.baseId == baseId) {
				result.add(event);
			}
		}

		for (Event event : this.repeatingEvents) {
			if (event.baseId == baseId) {
				result.add(event);
			}
		}
		// we should sort this list by its event's start dates
		// this is a ToDo
		return result;
	}

	// mache hier auch reperatur wengen event adden!!!!
	/**
	 * Tests if this Calendar contains an Event which happens on a specified
	 * date and is visible for the requester.
	 * 
	 * Compares a date composed of <code>day</code>, <code>month</code> and
	 * <code>year</code> with all Events in this calendars <code>events</code>
	 * and <code>repeatingEvents</code> and all Events of all shown observed
	 * Calendars. If any of the above mentioned contains a date equal to the
	 * specified date, it returns true.
	 * 
	 * @param day
	 *            The day compared to all Events of this calendar.
	 * @param month
	 *            The month used to compose the comparing date.
	 * @param year
	 *            The year used to compose the comparing date.
	 * @param requester
	 *            The user that wants to know if any Events happen on this day
	 *            that are visible for him.
	 * @return <code>true</code> if any visible Event of this Calendar happens
	 *         on the same date as the one specified by the arguments.
	 *         <code>false</code> otherwise.
	 */
	public boolean hasEventOnDay(int day, int month, int year, User requester) {
		boolean flag = false;
		Date comp = null;
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm");
		String dateString = Integer.toString(day) + "/"
				+ Integer.toString(month) + "/" + Integer.toString(year)
				+ ", 00:00";
		try {
			comp = dateFormat.parse(dateString);

		} catch (ParseException e) {
		}

		LinkedList<Calendar> observedCals = owner.getObservedCalendars();
		LinkedList<Long> shownObservedCals = owner.getShownObservedCalendars();
		LinkedList<Event> repeatingEvents = new LinkedList<Event>();
		repeatingEvents.addAll(this.repeatingEvents);
		PriorityQueue<Event> events = new PriorityQueue<Event>();
		events.addAll(this.events);
		LinkedList<Event> otherUsersBirthdays = new LinkedList<Event>();


		if (shownObservedCals.contains(owner.getBirthdayCalendar().id)) {
			for (Calendar observedCal : observedCals) {
				otherUsersBirthdays.add(observedCal.owner.getBirthday());
			}
		}
		
		for (Event e : otherUsersBirthdays) {
			if (e.isVisible()) {
				for (Calendar observedCal : observedCals) {
					if (observedCal.getId() == owner.getBirthdayCalendar().getId()) {
						observedCal.addEvent(e);
					}
				}
			}
		}

		for (Calendar observedCal : observedCals) {
			if (shownObservedCals.contains(observedCal.getId())) {
				for (Event observedEvent : observedCal.getEvents()) {
					if (observedEvent.getVisibility() != Visibility.PRIVATE) {
						events.add(observedEvent);
					}
				}
				for (Event repeatingObservedEvent : observedCal
						.getRepeatingEvents()) {
					for (Calendar cal : observedCals) {
						if (cal.owner == repeatingObservedEvent.owner
								&& repeatingObservedEvent.isVisible()) {
							repeatingEvents.add(repeatingObservedEvent);
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

	/**
	 * Does the exact same thing as {@link Calendar#hasEventOnDay}
	 * 
	 * @param date
	 *            The date which is compared to this Calendars events.
	 * @param requester
	 *            The user which requests to check if an Event happens on the
	 *            specified date.
	 * @return <code>true</code> if any Event which is visible to the requester
	 *         happens on the specified date. <code>false</code> otherwise.
	 */
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
			if (e.getBaseId() == repeatingEvent.getBaseId()
					&& e.start.getDate() == repeatingEvent.start.getDate()
					&& e.start.getMonth() == repeatingEvent.start.getMonth()
					&& e.start.getYear() == repeatingEvent.start.getYear()) {
				contains = true;
			}
		}
		return contains;
	}

	/**
	 * Iterates through this Calendars <code>events</code> and returns an Event
	 * with the same id as the argument.
	 * 
	 * @param id
	 *            The id to compare with all Events' id's of this Calendar.
	 * @return The event with the same id as the argument, if this Calendars
	 *         <code>events</code> contains such an Event. <code>null</code>
	 *         otherwise.
	 */
	public Event getEventById(long id) {
		Event result = null;
		for (Event e : events)
			if (e.getId() == id)
				result = e;
		return result;
	}

	/**
	 * Removes an Event from this Calendars <code>events</code> and
	 * <code>repeatingEvents</code> if they contain the Event.
	 * 
	 * @param id
	 *            id of the Event to be removed.
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
			// System.out.println("i am repeating");
			Event sentinel = getEventById(id);
			LinkedList<Event> interestingevents = getSameBaseIdEvents(sentinel.baseId);

			for (Event event : interestingevents) {
				System.out.println("e date" + event.start);
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
					Date nextRepStartDate = new Date(e.start.getYear(),
							e.start.getMonth(), e.start.getDate() + intervall,
							e.start.getHours(), e.start.getMinutes());
					Date nextRepEndDate = new Date(e.end.getYear(),
							e.end.getMonth(), e.end.getDate() + intervall,
							e.start.getHours(), e.start.getMinutes());
					Event nextEvent = new Event(this.owner, nextRepStartDate,
							nextRepEndDate, e.name, e.visibility, true,
							intervall);
					nextEvent.baseId = nextEvent.id;

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

								// get for each event before victim its event
								// description
								for (Event eee : dayevents) {
									if (eee.baseId == e.baseId) {
										descriptions.add(eee.description);
									}
								}

								if (hasName(e.name, dayevents)) {
									previousDates.add(current);
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
								for (Event eee : dayevents)
									if (eee.baseId == e.baseId)
										descriptions.add(eee.description);

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
								for (Event eee : dayevents)
									if (eee.baseId == e.baseId)
										descriptions.add(eee.description);

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
					// before we remove the events after victim
					// preserve all descriptions after victim

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
	 * Removes all Events with the same baseId from this Calendars list of
	 * events and this Calendars list of public Events. The provided Events'
	 * baseId is compared with all Events from this Calendar, matching Events
	 * will be removed.
	 * 
	 * @param event
	 *            the Event to be removed along with all Events with the same
	 *            baseId
	 * @see {@link Calendar#removeEvent(long id)}
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
	 * End repetition of a repeating Event from a given date cancelDate remove
	 * all repeating dates after cancelDate, which are already calculated. Mark
	 * all such repeating Events up to and including canelDate as NOT repeating.
	 * 
	 * @param cancelEvent
	 */
	public void cancelRepeatingEventRepetitionFromDate(Event cancelEvent) {
		System.out.println("cancel from this event: " + cancelEvent.start);
		boolean flag = true;
		Date cursor = cancelEvent.start;
		int intervall = cancelEvent.intervall;
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
	 * Returns a list of Events containing all Events with the same baseId from
	 * a given Event to another Event.
	 * 
	 * Starting from an Event <code>from</code> put in from.interval steps till
	 * <code>to</code> events in the Calendar.
	 * 
	 * @param from
	 *            The Event from whose start date the List starts.
	 * @param to
	 *            The last Event in the List.
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

	/**
	 * Get all Events of this Calendar.
	 * 
	 * @return this calendars <code>events</code>.
	 */
	public PriorityQueue<Event> getEvents() {
		return this.events;
	}

	/**
	 * Get all repeating Events of this Calendar.
	 * 
	 * @return this Calendars <code>repeatingEvents</code>.
	 */
	public LinkedList<Event> getRepeatingEvents() {
		return this.repeatingEvents;
	}

	/**
	 * String representation of this Calendar.
	 * 
	 * @return this Calendars <code>name</code>.
	 */
	public String toString() {
		return this.name;
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
