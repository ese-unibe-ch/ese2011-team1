package models;

import org.joda.time.DateTime;

import enums.Interval;
import enums.Visibility;

/**
 * RepeatingEvent is a specialization of Event. A repeating event has an
 * interval, which defines the time step size for its repetition. there are also
 * the attributes, upperBound and lowerBound, which are for none IntervalEvent
 * always equal null. We have this attributes just for inheritance issues. an
 * repeating event can be edited, removed, set interval size. for each repeating
 * event, we can call the method generateNextEvents which generates its
 * successor event if certain conditions (see for that generateNextEvents) are
 * fulfilled, but it is important to mention: there is no upper bound for
 * generating repeating events (if they are not an IntervalEvnt). .
 * 
 * @author team1
 * 
 */

public class RepeatingEvent extends Event {
	private Interval interval;
	protected DateTime upperBound = null;
	protected DateTime lowerBound = null;

	/**
	 * Default constructor for an new repeating event.
	 * 
	 * @param name
	 *            name for this event.
	 * @param start
	 *            start date/time for this event.
	 * @param end
	 *            date/time for this event.
	 * @param visibility
	 *            visibility state for this event.
	 * @param calendar
	 *            the calendar to this event belongs to.
	 * @param interval
	 *            time-step size for repentance for this event.
	 */
	public RepeatingEvent(String name, DateTime start, DateTime end,
			Visibility visibility, Calendar calendar, Interval interval) {
		super(name, start, end, visibility, calendar);
		this.setBaseId(this.getId());
		this.interval = interval;
	}

	/**
	 * This constructor transform an PointEvent into a RepeatingEvent.
	 * 
	 * @param event
	 *            the point event which we are going to transform into a
	 *            RepeatingEvent.
	 * @param interval
	 *            time-step size for repentance for this event.
	 */
	public RepeatingEvent(PointEvent event, Interval interval) {
		super(event.getName(), event.getStart(), event.getEnd(), event
				.getVisibility(), event.getCalendar());
		this.interval = interval;
		this.forceSetId(event.getId());
		this.setBaseId(this.getId());
	}

	/**
	 * sets the next/following event of this event.
	 */
	@Override
	public void setNext(Event event) {
		this.next = event;
	}

	/**
	 * sets the previous event of this event.
	 */
	@Override
	public void setPrevious(Event event) {
		this.previous = event;
	}

	/**
	 * set the time-step size for this event.
	 * 
	 * @param interval
	 *            time-step size for repentance for this event.
	 */
	public void setInterval(Interval interval) {
		this.interval = interval;
	}

	/**
	 * Get the interval of this Events repetition.
	 * 
	 * @return DAILY, if this Event is repeated on a daily basis. WEEKLY, if
	 *         this Event is repeated weekly. MONTHLY, if this Event is repeated
	 *         every month. YEARLY, if this Event is repeated every year.
	 */
	public Interval getInterval() {
		return this.interval;
	}

	/**
	 * Generate the following events for this event till a given limit
	 * date/time, depending on the interval size of this event
	 * 
	 * @param limitDate
	 *            till this date/time we generate following events (the
	 *            successors) for this event.
	 */
	@Override
	public void generateNextEvents(DateTime limitDate) {
		Interval interval = this.getInterval();

		switch (interval) {
		case DAILY:
			generateDaylyOrWeekly(this, limitDate, interval);
			break;
		case WEEKLY:
			generateDaylyOrWeekly(this, limitDate, interval);
			break;
		case MONTHLY:
			generateMonthly(this, limitDate, interval);
			break;
		case YEARLY:
			generateYearly(this, limitDate, interval);
			break;
		}
	}

	/**
	 * generates for weekly or daily repeating events based on a given base
	 * event its following events till a given limit date/time. it handles the
	 * following special cases:
	 * 
	 * since IntervalEvent inherits from RepeatingEvent, this method works
	 * similar for interval events, expect that for interval events, there is an
	 * additional bound check, if current generated event is in between its
	 * lower and upper bound, besides the limit check for a given base event
	 * 
	 * @param base
	 *            base event from which we start our event generating process
	 * @param limiter
	 *            limit date for event generating process (generate till this
	 *            date/time)
	 */

	protected void generateDaylyOrWeekly(Event base, DateTime limiter,
			Interval interval) {
		Event cursor = base;

		// set up a cursor, starting form base.
		Event nextEvent = null;

		// as long as whole month is calculated or is in cursor is in bounds
		while (cursor.getStart().isBefore(limiter) && isCurrentInBounds(cursor)) {
			
			// if there is no next event, then create a new one.
			if (!cursor.hasNext()) {
				nextEvent = generateNextEventDailyWeekly(cursor);
				cursor.setNext(nextEvent);
				nextEvent.setPrevious(cursor);
				nextEvent.setBaseId(this.getBaseId());
			}

			// move cursor
			cursor = cursor.getNextReference();
		}
	}

	/**
	 * generates for monthly repeating events based on a given base event its
	 * following events till a given limit date/time. it handles the following
	 * special cases:
	 * 
	 * since IntervalEvent inherits from RepeatingEvent, this method works
	 * similar for interval events, expect that for interval events, there is an
	 * additional bound check, if current generated event is in between its
	 * lower and upper bound, besides the limit check for a given base event
	 * 
	 * @param base
	 *            base event from which we start our event generating process
	 * @param limiter
	 *            limit date for event generating process (generate till this
	 *            date/time)
	 */
	protected void generateMonthly(Event base, DateTime limiter,
			Interval interval) {

		// set up a cursor, starting form base.
		Event cursor = base;
		Event nextEvent = null;

		// as long as whole month is calculated or is in cursor is in bounds
		while (cursor.getStart().isBefore(limiter) && isCurrentInBounds(cursor)) {

			// if there is no next event, then create a new one.
			if (!cursor.hasNext()) {
				nextEvent = generateNextEventMonthly(cursor);
				cursor.setNext(nextEvent);
				nextEvent.setPrevious(cursor);
				nextEvent.setBaseId(this.getBaseId());
			}

			// move cursor
			cursor = cursor.getNextReference();
		}
	}

	/**
	 * generates for yearly repeating events based on a given base event its
	 * following events till a given limit date/time. it handles the following
	 * special cases:
	 * 
	 * since IntervalEvent inherits from RepeatingEvent, this method works
	 * similar for interval events, expect that for interval events, there is an
	 * additional bound check, if current generated event is in between its
	 * lower and upper bound, besides the limit check for a given base event
	 * 
	 * @param base
	 *            base event from which we start our event generating process
	 * @param limiter
	 *            limit date for event generating process (generate till this
	 *            date/time)
	 */
	protected void generateYearly(Event base, DateTime limiter,
			Interval interval) {

		// set up a cursor, starting form base.
		Event cursor = base;
		Event nextEvent = null;

		// as long as whole month is calculated
		while (cursor.getStart().isBefore(limiter) && isCurrentInBounds(cursor)) {

			// if there is no next event, then create a new one.
			if (!cursor.hasNext()) {
				nextEvent = generateNextEventYearly(cursor);
				cursor.setNext(nextEvent);
				nextEvent.setPrevious(cursor);
				nextEvent.setBaseId(this.getBaseId());
			}

			// move cursor
			cursor = cursor.getNextReference();
		}
	}
	
	/**
	 * Generates the next daily or weekly Event for event which cursor 
	 * represents depending on cursor's date/time.
	 * @param cursor base event based on we generate its successor event.
	 * @return returns the successor event of cursor 
	 */
	protected Event generateNextEventDailyWeekly(Event cursor){
		DateTime newStartDate = cursor.getStart().plusDays(
				getInterval().getDays());
		DateTime newEndDate = cursor.getEnd().plusDays(
				getInterval().getDays());
		
		Event nextEvent = new RepeatingEvent(this.getName(),
				newStartDate, newEndDate, cursor.getVisibility(),
				this.getCalendar(), this.getInterval());
		return nextEvent;
	}
	
	/**
	 * Generates the next monthly Event for event which cursor represents depending on cursor's date/time.
	 * @param cursor base event based on we generate its successor event.
	 * @return returns the successor event of cursor 
	 */
	protected Event generateNextEventMonthly(Event cursor){
		DateTime newStartDate = cursor.getStart();
		DateTime newEndDate = cursor.getEnd();
		
		newStartDate = monthDateSpecialCaseTransformer(newStartDate);
		newEndDate = monthDateSpecialCaseTransformer(newEndDate);
		
		Event nextEvent = new RepeatingEvent(this.getName(),
				newStartDate, newEndDate, cursor.getVisibility(),
				this.getCalendar(), this.getInterval());
		return nextEvent;
	}
	
	/**
	 * Generates the next yearly Event for event which cursor represents depending on cursor's date/time.
	 * @param cursor base event based on we generate its successor event.
	 * @return returns the successor event of cursor 
	 */
	protected Event generateNextEventYearly(Event cursor){
		DateTime newStartDate = cursor.getStart();
		DateTime newEndDate = cursor.getEnd();
		
		newStartDate = yearDateSpecialCaseTransformer(newStartDate);
		newEndDate = yearDateSpecialCaseTransformer(newEndDate);
		
		Event nextEvent = new RepeatingEvent(this.getName(),
				newStartDate, newEndDate, cursor.getVisibility(),
				this.getCalendar(), this.getInterval());
		return nextEvent;
	}

	/**
	 * this method calculate based on given base date the next date for a
	 * monthly repeating event. there are 3 special cases: if date is the 29th,
	 * 30th or 31st day of month. if a following month does not have this day,
	 * for example, a February has never an 30 or even a 31 day of month, but
	 * suppose we have a monthly repeating event for every 30th of month, then
	 * on February, there wont be any event, since there is no 30th February.
	 * For regular cases, just increment this base date by one month.
	 * 
	 * @param baseDate
	 *            date/time on which our calculation is based on.
	 */
	protected DateTime monthDateSpecialCaseTransformer(DateTime baseDate) {
		int dayOfmonth = baseDate.getDayOfMonth();
		int monthOfYear = baseDate.getMonthOfYear();

		// we have a monthly repeating event on a 29th, 30th or 31th.
		if (dayOfmonth == 31) {

			// if we have a 31er month
			if (monthOfYear == 1 || monthOfYear == 3 || monthOfYear == 5
					|| monthOfYear == 7 || monthOfYear == 8
					|| monthOfYear == 10 || monthOfYear == 12) {
				if (monthOfYear == 7 || monthOfYear == 12) {
					return baseDate.plusMonths(1);
				} else {
					return baseDate.plusMonths(2);
				}

			} else
				return baseDate.plusMonths(1);

			// case day 30 of month
		} else if (dayOfmonth == 30) {
			if (monthOfYear == 1)
				return baseDate.plusMonths(2);
			else
				return baseDate.plusMonths(1);

			// case day 29 of month
		} else if (dayOfmonth == 29) {
			if (monthOfYear == 1
					&& baseDate.dayOfYear().getMaximumValue() == 365) {
				return baseDate.plusMonths(2);
			} else {
				return baseDate.plusMonths(1);
			}

			// regular dates are handled here, i.e.
			// dates not on a 29th, 30th or 31st.
		} else {
			return baseDate.plusMonths(1);
		}

	}

	/**
	 * this method calculate based on given base date the next date for a yearly
	 * repeating event. there are 3 special cases: if date is the 29th of month
	 * and we have a leap year, then the following date for a February is just
	 * in 4 years again. otherwise for all other cases increment given date by
	 * one year.
	 * 
	 * @param baseDate
	 *            date/time on which our calculation is based on.
	 */
	protected DateTime yearDateSpecialCaseTransformer(DateTime baseDate) {

		int dayOfmonth = baseDate.getDayOfMonth();
		int monthOfYear = baseDate.getMonthOfYear();
		if (dayOfmonth == 29 && monthOfYear == 2) {
			return baseDate.plusYears(4);
		} else
			return baseDate.plusYears(1);
	}

	/*
	 * checks
	 */

	/**
	 * checks the upper-and lower bound of this event are equal null (since a
	 * none interval event has no bounds)
	 * 
	 * @param event
	 */
	protected boolean isCurrentInBounds(Event event) {
		if (upperBound == null && lowerBound == null)
			return true;
		else
			return false;
	}

	/**
	 * Compare this Events start date with the arguments start date according to
	 * the definition of {@link Comparable#compareTo}
	 * 
	 * @param event
	 *            The event to compare this Event with.
	 * @returns a negative integer, zero, or a positive integer as this object
	 *          is less than, equal to, or greater than the specified object.
	 */
	@Override
	public int compareTo(Event event) {
		return this.getStart().compareTo(event.getStart());
	}

	/**
	 * Generates next events (for repeating events) till the following months
	 * for the first time we open the calendar, we have to call this method, to
	 * generate sufficient enough events for the calendar
	 */
	public void init() {
		generateNextEvents(this.getStart().plusMonths(1));
	}

	/**
	 * removes this event from the calendar to which it belongs to. // there are
	 * 4 cases which we have to consider for a deletion of a repeating event: //
	 * (a) victim equals current head => next of head gets new head // (b)
	 * victim equals next after head => head gets a PointEvent, victim.next a
	 * new head // (c) [head, posthead] | victim | [postVictim,inf] // (d)
	 * [head,..., previctim] | victim | [postVictim, +infinite] // care about
	 * setting new baseId correctly.
	 */
	@Override
	public void remove() {

		Event head = this.getCalendar().getHeadById(this.getBaseId());
		Event preVictim = this.getPreviousReference();
		Event postVictim = this.getNextReference();

		// case (a)
		if (this == head) {
			Event postHead = this.getNextReference();
			postHead.setPrevious(null);
			this.setNext(null);
			this.getCalendar().removeHeadFromHeadList(this);
			this.getCalendar().addEvent(postHead);

			// go through posthead tail
			postHead.setBaseId(postHead.getId());
			postHead.setOriginId(head.getOriginId());
			Event cursor = postHead;
			while (cursor.hasNext()) {
				cursor = cursor.getNextReference();
				cursor.setBaseId(postHead.getBaseId());
			}

			// case (b)
		} else if (this == head.getNextReference()) {
			Event postPostHead = this.getNextReference();
			postPostHead.setPrevious(null);
			this.setPrevious(null);
			this.setNext(null);
			head.setNext(null);

			Event newPointEvent = new PointEvent((RepeatingEvent) head);
			newPointEvent.editDescription(head.getDescription());
			newPointEvent.setOriginId(head.getOriginId());

			this.getCalendar().removeHeadFromHeadList(head);
			this.getCalendar().addEvent(newPointEvent);
			this.getCalendar().addEvent(postPostHead);

			postPostHead.setOriginId(head.getOriginId());

			postPostHead.setBaseId(postPostHead.getId());
			Event cursor = postPostHead;

			while (cursor.hasNext()) {
				cursor = cursor.getNextReference();
				cursor.setBaseId(postPostHead.getId());
			}
			postPostHead.generateNextEvents(postPostHead.getNextReference()
					.getStart());

			// case (c)
		} else if (head.getNextReference().getNextReference() == this) {
			Event postPostHead = this; // this one we gonna kill - harhar
			Event postHead = head.getNextReference();
			// work to do: create a intervalEvent series of [head, posthead] and
			// repeatingEvent series [postVictim,inf]

			// remove all references to and from victim.
			postVictim.setPrevious(null);
			postPostHead.setNext(null);
			postPostHead.setPrevious(null);
			postHead.setNext(null);

			// remove head from head list
			this.getCalendar().getEventHeads().remove(head);

			// 1. build [postVictim,inf]

			// add new head into head list
			this.getCalendar().addEvent(postVictim);
			postVictim.setOriginId(head.getOriginId());
			postVictim.setBaseId(postVictim.getId());
			// reset base id for tail of postVictim
			Event cursor = postVictim;
			while (cursor.hasNext()) {
				cursor = cursor.getNextReference();
				cursor.setBaseId(postVictim.getId());
			}

			// 2. build [head, posthead]
			Event newIntervalEventHead = new IntervalEvent(head.getStart(),
					postHead.getStart(), (RepeatingEvent) head);
			newIntervalEventHead.editDescription(head.getDescription());
			newIntervalEventHead.setOriginId(head.getOriginId());
			newIntervalEventHead.setBaseId(newIntervalEventHead.getId());

			Event newIntervalEvent = new IntervalEvent(head.getStart(),
					postHead.getStart(), (RepeatingEvent) postHead);
			newIntervalEvent.editDescription(postHead.getDescription());
			newIntervalEvent.setBaseId(newIntervalEventHead.getId());

			newIntervalEventHead.setNext(newIntervalEvent);
			newIntervalEvent.setPrevious(newIntervalEventHead);

			this.getCalendar().addEvent(newIntervalEventHead);

			// case (d)
		} else {
			this.setNext(null);
			this.setPrevious(null);
			preVictim.setNext(null);
			postVictim.setPrevious(null);
			this.getCalendar().removeHeadFromHeadList(head);

			// this is a future new head.
			IntervalEvent newIntervalEvent = new IntervalEvent(head.getStart(),
					preVictim.getStart(), (RepeatingEvent) head);
			newIntervalEvent.editDescription(head.getDescription());
			newIntervalEvent.setOriginId(head.getOriginId());
			newIntervalEvent.setBaseId(newIntervalEvent.getId());

			this.getCalendar().addEvent(newIntervalEvent);

			IntervalEvent newIntervalCursor = null;
			Event cursor = head;
			Event prev = newIntervalEvent;

			// add tail elements to left interval head
			while (cursor.hasNext()) {
				cursor = cursor.getNextReference();
				newIntervalCursor = new IntervalEvent(head.getStart(),
						preVictim.getStart(), (RepeatingEvent) cursor);
				newIntervalCursor.editDescription(cursor.getDescription());
				newIntervalCursor.setBaseId(newIntervalEvent.getId());
				prev.setNext(newIntervalCursor);
				newIntervalCursor.setPrevious(prev);
				prev = newIntervalCursor;
			}
			newIntervalCursor.setNext(null);

			// set new base id for right interval
			this.getCalendar().addEvent(postVictim);
			postVictim.setBaseId(postVictim.getId());
			postVictim.setOriginId(head.getOriginId());
			cursor = postVictim;
			while (cursor.hasNext()) {
				cursor = cursor.getNextReference();
				cursor.setBaseId(postVictim.getId());
			}
		}
		this.removeAttendantInvitations();
	}

	/**
	 * Returns the interval of this event as a lower case String.
	 * All users are allowed to see the repetition status of an event.
	 * 
	 * @param requester
	 *            user which requests for this information.
	 */
	public String getRepetitionFor(User requester) {
		return this.interval.toString().toLowerCase();
	}

	/**
	 * get type of this event back
	 * 
	 * @return a string equals "PointEvent"
	 */
	@Override
	public String getType() {
		return "RepeatingEvent";
	}

	/**
	 * Edits this event by given input arguments. Update the attributes of this
	 * RepeatingEvent.
	 * 
	 * @param name
	 *            new name of event
	 * @param start
	 *            new start date/time of this event
	 * @param end
	 *            new end date/time of this event
	 * @param visibility
	 *            new visibility state of this event
	 * @param interval
	 *            new interval size for time/date steps for repentance of event
	 * @param from
	 *            new from date/time - is being ignored, since a point event
	 *            never gets transformed to an interval event
	 * @param to
	 *            new to date/time - is being ignored, since a point event never
	 *            gets transformed to an interval event
	 * @param description
	 *            new description of this event. actual design decision: there
	 *            is no way to transform an repeating event to an point event by
	 *            edit functionality at the moment. we can just reset trivial
	 *            stats (i.e. not possible to set repentance by edit atm. same
	 *            holds for interval events so. GUI does not support bound edit
	 *            for them.
	 */
	@Override
	public void edit(String name, DateTime start, DateTime end,
			Visibility visibility, Interval interval, DateTime from,
			DateTime to, String description) {

		this.setName(name);
		this.setStart(start);
		this.setEnd(end);
		this.setVisiblility(visibility);
		this.editDescription(description);
		this.removeAttendantInvitations();
	}

	/**
	 * overloaded version for editing this event.
	 * 
	 * @param name
	 *            new name of event
	 * @param start
	 *            new start date/time of this event
	 * @param end
	 *            new end date/time of this event
	 * @param visibility
	 *            new visibility state of this event
	 * @param interval
	 *            new interval size for time/date steps for repentance of event
	 * @param description
	 *            new description of this event.
	 */
	public void edit(String name, DateTime start, DateTime end,
			Visibility visibility, Interval interval) {
		this.setStart(start);
		this.setEnd(end);
		this.setName(name);
		this.setVisiblility(visibility);
		this.setInterval(interval);
		this.removeAttendantInvitations();
	}
}
