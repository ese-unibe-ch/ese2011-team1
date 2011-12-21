package models;

import java.util.LinkedList;
import java.util.PriorityQueue;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import enums.Interval;
import enums.Visibility;

/**
 * Calendar contains a list of heads of events "eventHeads". Heads are either of
 * type PointEvent, IntervalEvent or RepeatingEvent. It means the elements in
 * the head list are the 1st element of a series of events. We can add, search
 * for, get, delete events in this calendar by their unique id. furthermore we
 * can return a series of events depending on a given date. Each calendar has a
 * unique id, name, and an owner.
 * 
 * @autor team1
 */

public class Calendar {
	private String name;
	private User owner;
	private PriorityQueue<Event> eventHeads;
	private static long counter;
	private long id;

	/**
	 * Constructor for a calendar. a calendar has a name and an owner. this
	 * constructor sets a unique id for each new calendar and creates a head
	 * list.
	 * 
	 * @param name
	 *            the name for this calendar.
	 * @param owner
	 *            the owner for this calendar.
	 */
	public Calendar(String name, User owner) {
		this.name = name;
		this.owner = owner;
		counter++;
		this.id = counter;
		eventHeads = new PriorityQueue<Event>();
	}

	/*
	 * getters
	 */

	/**
	 * returns the name of this calendar.
	 * 
	 * @return this.name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * returns the owner of this calendar
	 * 
	 * @return this.owner
	 */
	public User getOwner() {
		return this.owner;
	}

	/**
	 * get the id of this calendar
	 * 
	 * @return this.id
	 */
	public long getId() {
		return this.id;
	}

	/**
	 * get the last element of a series of repeating events or if we have a
	 * point event, get back the event itself. only heads have a none null
	 * reference to leaf. care about this fact. this is due performance issues.
	 * 
	 * @param event
	 *            event from which we are looking for its tail.
	 * @return leaf of head-tail structure
	 */
	public Event getLeafOfEventSeries(Event event) {
		if (event.getBaseId() == event.getId())
			return event.getLeaf();
		else
			return getHeadById(event.getBaseId()).getLeaf();
	}

	/**
	 * returns the head list of this calendar, i.e. eventHeads
	 * 
	 * @return a priority queue of all heads of the calendar
	 */
	public PriorityQueue<Event> getEventHeads() {
		return this.eventHeads;
	}

	/**
	 * get an event of this calendar by a given id
	 * 
	 * @param id
	 *            of the event we are looking for.
	 * @return returns the event with id equals id of input argument.
	 */

	public Event getEventById(long id) {
		Event result = null;
		for (Event event : this.eventHeads) {
			result = event.findEventById(id);
			if (result != null)
				return result;
		}
		return result;
	}

	/**
	 * get an head from eventHeads by a given id. getEventById would be able to
	 * do this but this method is more efficient if we are just looking for an
	 * head.
	 * 
	 * @param id
	 *            this is the id of the head we are looking for.
	 * @return returns a head of eventHeads which has the same id as the input
	 *         argument of this method.
	 */
	public Event getHeadById(long id) {
		for (Event event : this.eventHeads)
			if (event.equalId(id))
				return event;
		return null;
	}

	/**
	 * Get all heads with same given origin id.
	 * 
	 * @param originId
	 * @return returns a linked list which contains all heads which have origin
	 *         id equals originId
	 */
	public LinkedList<Event> getHeadsByOriginId(long originId) {
		LinkedList<Event> result = new LinkedList<Event>();
		for (Event head : this.getEventHeads())
			if (head.equalOriginId(originId))
				result.add(head);
		return result;
	}

	/**
	 * get all events with same baseId, i.e. all events from a given head
	 * 
	 * @param originId
	 * @return returns a linked list which contains all events with same base id
	 */
	public LinkedList<Event> getSameBaseIdEvents(long baseId) {
		LinkedList<Event> result = new LinkedList<Event>();
		Event head = this.getHeadById(baseId);
		if (head != null) {
			Event cursor = head;
			while (cursor.hasNext()) {
				result.add(cursor);
				cursor = cursor.getNextReference();
			}
		}
		return result;
	}

	/**
	 * Helper which gets all events which are visible for given requester on given local date,
	 * correlated to day, month, year
	 * 
	 * @param day
	 *            the day of a month.
	 * @param month
	 *            the month of a year.
	 * @param year
	 *            year value.
	 * @param requester
	 *            user which request for events.
	 * @return returns a linked list which contains all for given requester
	 *         visible events for a given date.
	 */
	private LinkedList<Event> getAllVisibleEventsOfDateHelper(int day, int month,
			int year, User requester) {
		LocalDate compareDate = new LocalDate(year, month, day);
		LinkedList<Event> result = new LinkedList<Event>();

		result.addAll(this.getEventsOfDate(compareDate, requester));

		LinkedList<Calendar> observedCalendars = owner.getObservedCalendars();
		LinkedList<Long> shownObservedCalendars = owner
				.getShownObservedCalendars();
		for (Calendar observedCalendar : observedCalendars) {
			if (shownObservedCalendars.contains(observedCalendar.getId())) {
				result.addAll(observedCalendar.getEventsOfDate(compareDate,
						requester));
			}
		}
		return result;
	}
	
	/**
	 * get all events which are visible for given requester on given local date,
	 * correlated to day, month, year
	 * 
	 * @param day
	 *            the day of a month.
	 * @param month
	 *            the month of a year.
	 * @param year
	 *            year value.
	 * @param requester
	 *            user which request for events.
	 * @return returns a linked list which contains all for given requester
	 *         visible events for a given date.
	 */
	public LinkedList<Event> getAllVisibleEventsOfDate(int day, int month,
			int year, User requester) {
		
		return getAllVisibleEventsOfDateHelper(day, month, year, requester);
	}
	
	/**
	 * get all events which are visible for given requester on given local date,
	 * correlated to day, month, year
	 * 
	 * @param date
	 *            local date which represents day month year.
	 * @param requester
	 *            user which request for events.
	 * @return returns a linked list which contains all for given requester
	 *         visible events for a given date.
	 */
	public LinkedList<Event> getAllVisibleEventsOfDate(LocalDate date, User requester) {
		int day = date.getDayOfMonth();
		int month = date.getMonthOfYear();
		int year = date.getYear();
		return getAllVisibleEventsOfDateHelper(day, month, year, requester);
	}

	/**
	 * get all events which are visible for given requester on a given date.
	 * 
	 * @param date
	 *            for this date we want to get all events which are visible.
	 * @param requester
	 *            the visibility of an event depends on this user.
	 * @return returns a linked list which contains all for given requester
	 *         visible events for a given date.
	 */
	// TODO use a priority queue instead of a linked list.
	public LinkedList<Event> getEventsOfDate(LocalDate date, User requester) {
		LinkedList<Event> result = new LinkedList<Event>();
		// 1. go here through heads
		for (Event head : this.eventHeads) {
			if (head.happensOn(date))
				if (head.getVisibility() != Visibility.PRIVATE
						|| head.getOwner() == requester || head.getAttendingUsers().contains(requester))
					result.add(head);

			Event cursor = head;
			while (cursor.hasNext()) {
				cursor = cursor.getNextReference();
				if (cursor.happensOn(date))
					if (cursor.getVisibility() != Visibility.PRIVATE
							|| cursor.getOwner() == requester || cursor.getAttendingUsers().contains(requester))
						result.add(cursor);
			}
		}
		return result;
	}

	/**
	 * generates the next couple of events for a given head till a limit date.
	 * each time we click for the next month in our calendar, we have to
	 * generate following events RepeatingEvents. the generating process is
	 * handled in the event classes itself and depends on the run-time type of
	 * an event. call this method, whenever we change the month in the calendar
	 * GUI or added a new event an declared him as an IntervalEvent or
	 * RepeatingEvent or modified an existing event and changed him to an
	 * IntervalEvent or RepeatingEvent
	 * 
	 * @param head
	 *            for this head we are going to generate its following events.
	 * @param baseDate
	 *            this DateTime object defines the limiter till which we
	 *            generate new events for given head.
	 */

	// TODO utilize getLeaf() for calculation improvement
	// => generate new events, starting from leaf

	public void generateNextEvents(Event head, DateTime baseDate) {
		DateTime currentDate = baseDate;
		DateTime nextDate = currentDate.plusMonths(1);
		head.generateNextEvents(nextDate);

		// TODO later: set here new leaf for the head!
		// i think this would be the most efficient way to do that here.
	}

	/**
	 * Generate the following events for all events in eventHeads depending on a
	 * given base date.
	 * 
	 * @param baseDate
	 *            basis for limit date - we generate up to this date plus one
	 *            month new events
	 */
	public void generateAllNextEvents(DateTime baseDate) {
		this.generateNextEvents(baseDate);
		LinkedList<Calendar> observedCalendars = owner.getObservedCalendars();
		for (Calendar observedCalendar : observedCalendars) {
			if (!observedCalendar.equals(this)) {
				observedCalendar.generateNextEvents(baseDate);
			}
		}
	}

	/**
	 * Generate for each head in our head list the following/successor events
	 * till a given date base date
	 * 
	 * @param baseDate
	 *            this date/time defines the limit till which we generate
	 *            events.
	 */
	private void generateNextEvents(DateTime baseDate) {
		for (Event event : this.eventHeads) {
			DateTime currentDate = baseDate;
			DateTime nextDate = currentDate.plusMonths(1);
			event.generateNextEvents(nextDate);
		}
	}

	/*
	 * setter
	 */

	/**
	 * Sets name of this calendar.
	 * 
	 * @param name
	 *            string representation calendar name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the owner of this calendar.
	 * 
	 * @param owner
	 *            user object
	 */
	public void setOwner(User owner) {
		this.owner = owner;
	}

	/*
	 * add, delete modify, checks
	 */

	/*
	 * adder
	 */

	/**
	 * adds an new head to eventHeads list.
	 * 
	 * @param event
	 *            a new head
	 */
	public void addEvent(Event event) {
		this.eventHeads.add(event);
	}

	/**
	 * removes an head from the eventHeads list.
	 * 
	 * @param event
	 *            victim head which is going to be removed.
	 */
	public void removeHeadFromHeadList(Event event) {
		this.eventHeads.remove(event);
	}

	/*
	 * deleter
	 */

	/**
	 * removes an victim event from our calendar by a given event id. depending
	 * on the run-time type of an event, this method works differently. See in
	 * each event class for the remove() definition.
	 * 
	 * @param id
	 *            is the id of victim event
	 */
	public void removeEvent(long id) {
		Event victim = getEventById(id);
		victim.remove();
	}

	/*
	 * modifier
	 */

	/**
	 * edits a given event, i.e. set new state for this event. how an event is
	 * actually edited is in each event class separately defined. depending on
	 * the run-time type of an event, some of these arguments are not set.
	 * 
	 * @param event
	 *            is victim event we want to edit.
	 * @param newName
	 *            is the new name for the victim event.
	 * @param newStart
	 *            is the new start date for the victim event.
	 * @param newEnd
	 *            is the new end date for the victim event.
	 * @param newVisibility
	 *            is the new visibility state for the victim event.
	 * @param newInterval
	 *            is the new interval size for the victim event.
	 * @param newFrom
	 *            is the new lower bound for the victim event (only set if event
	 *            is of type IntervalEvent).
	 * @param newTo
	 *            is the new upper bound for the victim event (only set if event
	 *            is of type IntervalEvent).
	 * @param newDescription
	 *            is the new description for the victim event.
	 */
	public void editEvent(Event event, String newName, DateTime newStart,
			DateTime newEnd, Visibility newVisibility, Interval newInterval,
			DateTime newFrom, DateTime newTo, String newDescription) {

		event.edit(newName, newStart, newEnd, newVisibility, newInterval,
				newFrom, newTo, newDescription);
	}

	/**
	 * remove whole series to which an event "member" belongs to. I.e. remove an
	 * head and its tail from this calendar and all correlated head-tails which
	 * have the same origin id like the origin id of the given member's head.
	 * 
	 * @param member
	 *            this event is part of a head-tail series.
	 */
	public void removeSeriesOfRepeatingEvents(Event member) {
		// 1. get corresponding head
		// 2. remove victimHead from head list
		long baseId = member.getBaseId();
		Event victimHead = getHeadById(baseId);
		long originId = victimHead.getOriginId();
		LinkedList<Event> originHeads = this.getHeadsByOriginId(originId);
		for (Event head : originHeads) {
			this.getEventHeads().remove(head);
		}

	}

	/**
	 * end repentance of an series of RepeatingEvents from a given event on.
	 * this method takes all events from [head,cancelFromThis] and transforms
	 * them into IntervalEvents.
	 * 
	 * @param cancelFromThis
	 *            from this event on, the repentance gets canceled.
	 */
	public void cancelRepeatingEventRepetitionFromDate(Event cancelFromThis) {
		// 1. get corresponding head, this is the new lower bound for
		// IntervalEvent
		// 2. cancelFromThis is the upper bound for this new IntervalEvent
		// 3. Get next of cancelFromThis and kill back-reference from it to
		// cancelFromThis.
		// 4. Remove next reference of cancelFromThis.
		// 5. Transform all Events from head to cancelFromThis into objects of
		// type IntervalEvent

		long baseId = cancelFromThis.getBaseId();
		Event victimHead = getHeadById(baseId);
		Event nextFromCancel = cancelFromThis.getNextReference();

		LinkedList<Event> sameOriginHeads = this.getHeadsByOriginId(victimHead
				.getOriginId());

		// case: cancel from not a , given some repeating sequence of event
		// [a,b],[c,...,d],[e,..,inf] or [a,b] or [a,b],{c}
		if (cancelFromThis != victimHead) {

			cancelFromThis.setNext(null);
			if (nextFromCancel != null)
				nextFromCancel.setPrevious(null);

			this.getEventHeads().remove(victimHead);
			Event newHead = new IntervalEvent(victimHead.getStart(),
					cancelFromThis.getStart(), (RepeatingEvent) victimHead);
			newHead.editDescription(victimHead.getDescription());
			newHead.setOriginId(victimHead.getOriginId());
			newHead.setBaseId(newHead.getId());
			this.addEvent(newHead);

			Event cursor = victimHead;
			Event intervalCursor = newHead;
			Event previous = null; // previous

			while (cursor.hasNext()) {
				intervalCursor.setPrevious(previous);
				previous = intervalCursor; // store previous
				intervalCursor = new IntervalEvent(victimHead.getStart(),
						cancelFromThis.getStart(), (RepeatingEvent) cursor);
				intervalCursor.editDescription(cursor.getDescription());
				intervalCursor.setBaseId(newHead.getBaseId());
				previous.setNext(intervalCursor);
				intervalCursor.setPrevious(previous);
				cursor = cursor.getNextReference();
			}

			previous = intervalCursor;
			intervalCursor = new IntervalEvent(victimHead.getStart(),
					cancelFromThis.getStart(), (RepeatingEvent) cancelFromThis);
			intervalCursor.editDescription(cancelFromThis.getDescription());
			intervalCursor.setBaseId(newHead.getId());
			previous.setNext(intervalCursor);
			intervalCursor.setPrevious(previous);

			// case: cancel from a , given [a,b],[c,...,d],[e,..,inf] or [a,b]
			// or [a,b],{c}
		} else {
			victimHead.setNext(null);
			cancelFromThis.setPrevious(null);
			this.eventHeads.remove(victimHead);
			Event newPointHead = new PointEvent((RepeatingEvent) victimHead);
			newPointHead.editDescription(victimHead.getDescription());
			newPointHead.setBaseId(newPointHead.getId());
			newPointHead.setOriginId(victimHead.getOriginId());
			this.eventHeads.add(newPointHead);
		}

		// remove all same origin heads to the right of us (in terms of time).
		for (Event head : sameOriginHeads) {
			if (victimHead.getStart().isBefore(head.getStart()))
				this.eventHeads.remove(head);
		}

	}

	// checker

	/**
	 * Test if this Calendar has any Event on a given date that is visible for
	 * the specified user.
	 * 
	 * This method also considers Events of all shown observed Calendars.
	 * 
	 * @param day
	 *            The day of the date to be checked.
	 * @param month
	 *            The month of the date to be checked.
	 * @param year
	 *            The year of the date to be checked.
	 * @param requester
	 *            The user which requests to see those events.
	 * @return <code>true</code> if there exists any event in either this or any
	 *         of the shown observed calendars that happens the date.
	 *         <code>false</code> if no such Event exists.
	 */
	public boolean hasEventOnDateIncludingObserved(int day, int month,
			int year, User requester) {
		boolean hasEvent = false;
		LocalDate compareDate = null;

		try {
			compareDate = new LocalDate(year, month, day);
		} catch (Exception e) {
		}

		hasEvent = hasEventOnDate(compareDate, requester);

		// check observedCalendars
		if (!hasEvent) {
			LinkedList<Calendar> observedCalendars = owner
					.getObservedCalendars();
			LinkedList<Long> shownObservedCalendars = owner
					.getShownObservedCalendars();
			for (Calendar observedCalendar : observedCalendars) {
				if (shownObservedCalendars.contains(observedCalendar.getId())) {
					hasEvent = observedCalendar.hasEventOnDate(compareDate,
							requester);
				}
				if (hasEvent)
					break;
					
			}
		}
		return hasEvent;
	}

	/**
	 * Tests if this calendar has an Event which happens on the specified date
	 * and is visible for the given user.
	 * 
	 * @param date
	 *            The date to be checked against.
	 * @param requester
	 *            The user that requests to see an Event on this date.
	 * @return <code>true</code> if there exists any event in this calendar that
	 *         happens the date. <code>false</code> if no such event exists.
	 */
	public boolean hasEventOnDate(LocalDate date, User requester) {
		for (Event event : this.eventHeads) {
			if (event.findHasEventOnDate(date, requester) != null)
				return true;
		}
		return false;
	}

	/**
	 * Search for all events in the displayed calendar
	 * containing a certain input string and get back a list containing them.
	 * 
	 * @param eventName part of an event name.
	 * @param curiousUser User currently logged in who is actually searching an event.
	 *            This user can be someone different than the owner of the displayed calendar.
	 * @param activeDate Date selected in the calendar. Used to determine search limits.
	 * @return eventsFound A list with all the events containing the input string.
	 */
	public LinkedList<Event> searchEvent(String eventName, User requester, DateTime activeDate){
		LinkedList<Event> eventsFound = new LinkedList<Event>();
		DateTime lowerSearchLimit = activeDate.minusDays(31);
		DateTime upperSearchLimit = activeDate.plusDays(31);
		for(DateTime selected = lowerSearchLimit; upperSearchLimit.compareTo(selected) >= 0; selected = selected.plusDays(1)){
			LinkedList<Event> eventsList = this.getAllVisibleEventsOfDate(new LocalDate(selected), requester);
			
			for(Event specificEvent : eventsList){
				String searchForName = specificEvent.getNameFor(requester);
				
				if(searchForName == null) searchForName = "";
				
				String searchForDescription = specificEvent.getDescriptionFor(requester);
				if(searchForDescription == null) searchForDescription = "";
				
				if ((searchForName.toLowerCase().contains(eventName.toLowerCase())
						|| searchForDescription.toLowerCase().contains(eventName.toLowerCase())
						|| specificEvent.isBusy())
					&& !eventsFound.contains(specificEvent)){
					
					eventsFound.add(specificEvent);;
				}
			}
		}
		return eventsFound;
	}

	/*
	 * private helpers this methods we can only call from this class they are
	 * declared as private (never public or even protected)
	 */

	/*
	 * public helpers
	 */

	/**
	 * @return returns string representation of this event, i.e. its name and
	 *         id.
	 */
	public String toString() {
		return this.name + " [" + this.id + "]";
	}

}
