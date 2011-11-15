package models;

import java.util.LinkedList;

import org.joda.time.DateTime;

import enums.Interval;
import enums.Visibility;

import android.database.Cursor;

public class RepeatingEvent extends Event {
	private Interval interval;
	//protected Event current = null;
	protected DateTime upperBound = null;
	protected DateTime lowerBound = null;
	protected boolean hasBoundReached = isCurrentInBounds(null);
	
	/**
	 * Default constructor for an new repeating event.
	 * @param name name for this event.
	 * @param start start date/time for this event.
	 * @param end date/time for this event.
	 * @param visibility visibility state for this event.
	 * @param calendar the calendar to this event belongs to.
	 * @param interval time-step size for repentance for this event.
	 */
	public RepeatingEvent(String name, DateTime start, DateTime end,
			Visibility visibility, Calendar calendar, Interval interval) {
		super(name, start, end, visibility, calendar);
		this.setBaseId(this.getId());
		// this.setOriginId(this.getBaseId());
		this.interval = interval;
	}
	
	/**
	 * This constructor transform an PointEvent into a RepeatingEvent.
	 * @param event point event which we are going to transform into a RepeatingEvent.
	 * @param interval time-step size for repentance for this event.
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
	 * @param interval time-step size for repentance for this event.
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
	 * Calculate all next events for the current selected month. 
	 */
	@Override
	public void generateNextEvents(DateTime limitDate) {
		//this.current = null;
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

		//this.current = null;
	}

	// TODO add some comment
	protected void generateYearly(Event base, DateTime limiter,
			Interval interval) {
		Event cursor = base;
		//this.current = cursor;
		RepeatingEvent nextEvent = null;
		// as long as whole month is calculated
		while (cursor.getStart().isBefore(limiter) && isCurrentInBounds(cursor)) {

			// if there is no next event, then create a new one.
			if (!cursor.hasNext()) {

				DateTime newStartDate = cursor.getStart().plusYears(1);
				DateTime newEndDate = cursor.getEnd().plusYears(1);

				// corner case for 29feb problem
				newStartDate = correctDateForCornerCase(newStartDate);
				newEndDate = correctDateForCornerCase(newEndDate);

				nextEvent = new RepeatingEvent(this.getName(), newStartDate,
						newEndDate, cursor.getVisibility(), this.getCalendar(),
						this.getInterval());
				cursor.setNext(nextEvent);

				nextEvent.setPrevious(cursor);
				nextEvent.setBaseId(this.getBaseId());

				nextEvent.getPreviousReference();

			}

			// move cursor
			cursor = cursor.getNextReference();
			//this.current = cursor;
		}
	}

	// TODO highly buggy due to corner cases
	// TODO huge optimization potential: calculate no only events till limiter
	// but about always constant amount.
	protected void generateMonthly(Event base, DateTime limiter,
			Interval interval) {
		Event cursor = base;
		//this.current = cursor;
		// solange bis monat abgedeckt
		while (cursor.getStart().compareTo(limiter) == -1 && isCurrentInBounds(cursor)) {

			// wenn kein n�chster event
			if (!cursor.hasNext()) {

				DateTime newStartDate = cursor.getStart().plusMonths(1);
				DateTime newEndDate = cursor.getEnd().plusMonths(1);
				
				// corner case for 30th/31th of month problem
				newStartDate = correctDateForCornerCase(newStartDate);
				newEndDate = correctDateForCornerCase(newEndDate);

				RepeatingEvent nextEvent = new RepeatingEvent(this.getName(),
						newStartDate, newEndDate, cursor.getVisibility(),
						this.getCalendar(), this.getInterval());
				cursor.setNext(nextEvent);

				nextEvent.setPrevious(cursor);
				nextEvent.setBaseId(this.getBaseId());
			}

			// move cursor
			cursor = cursor.getNextReference();
			//this.current = cursor;
		}

	}

	/**
	 * Correct corner case Dates for 29/31 of month for repeatingEvents
	 * 
	 * If headEvent of argument happens on any of the above mentioned Dates,
	 * this method corrects the Date of the next repetition to the heads
	 * original date.
	 * 
	 * @param dateToCorrect
	 *            The date of the new repetition to be corrected.
	 * @return The possibly corrected date.
	 */
	private DateTime correctDateForCornerCase(DateTime dateToCorrect) {
		Event head = getCalendar().getHeadById(this.getBaseId());
		DateTime correctedDate = dateToCorrect;
		if (head.getStart().getDayOfMonth() > dateToCorrect.getDayOfMonth()) {
			correctedDate = dateToCorrect.dayOfMonth().withMaximumValue();
		}
		return correctedDate;
	}

	// TODO highly buggy due to corner cases.
	// TODO huge optimization potential: calculate no only events till limiter
	// but about always constant amount.

	protected void generateDaylyOrWeekly(Event base, DateTime limiter,
			Interval interval) {
		Event cursor = base;
		//this.current = cursor;
		RepeatingEvent nextEvent = null;
		// as long as whole month is calculated
		while (cursor.getStart().isBefore(limiter) && isCurrentInBounds(cursor)) {

			// if there is no next event, then create a new one.
			if (!cursor.hasNext()) {

				DateTime newStartDate = cursor.getStart().plusDays(
						getInterval().getDays());
				DateTime newEndDate = cursor.getEnd().plusDays(
						getInterval().getDays());

				if (this instanceof IntervalEvent) {
					System.out.println("we are creating an intervalEvent");
					DateTime from = ((IntervalEvent) this).getFrom();
					DateTime to = ((IntervalEvent) this).getTo();
					nextEvent = new IntervalEvent(this.getName(), newStartDate,
							newEndDate, from, to, cursor.getVisibility(),
							this.getCalendar(), this.getInterval());
				} else {
					nextEvent = new RepeatingEvent(this.getName(),
							newStartDate, newEndDate, cursor.getVisibility(),
							this.getCalendar(), this.getInterval());
				}
				cursor.setNext(nextEvent);

				nextEvent.setPrevious(cursor);
				nextEvent.setBaseId(this.getBaseId());

			}

			// move cursor
			cursor = cursor.getNextReference();
			//this.current = cursor;
		}
		// nextEvent.setNext(null)
//		base.PrintThisAndTail();
	}

	/*
	 * checks
	 */


	/**
	 * checks the upper-and lower bound of this event are equal null 
	 * (since a none interval event has no bounds)
	 * @param event
	 */
	protected boolean isCurrentInBounds(Event event) {
		if (upperBound == null && lowerBound == null)
			return true;
		else return false;
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

	public void edit(String name, DateTime start, DateTime end,
			Visibility visibility, Interval interval) {
		this.setStart(start);
		this.setEnd(end);
		this.setName(name);
		this.setVisiblility(visibility);
		this.setInterval(interval);
	}
	
	/**
	 * Generates next events (for repeating events) till the following months
	 * for the first time we open the calendar, we have to call this method,
	 * to generate sufficient enough events for the calendar
	 */
	public void init() {
		generateNextEvents(this.getStart().plusMonths(1));
	}


	/**
	 * removes this event from the calendar to which it belongs to.
	// there are 4 cases which we have to consider for a deletion of a repeating event:
	// (a) victim equals current head => next of head gets new head
	// (b) victim equals next after head => head gets a PointEvent, victim.next a new head
	// (c) [head, posthead] | victim | [postVictim,inf]
	// (d) [head,..., previctim] | victim | [postVictim, +infinite]	
	// care about setting new baseId correctly.
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
			this.getCalendar().getHeadList().remove(head);

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
					postHead.getStart(), (RepeatingEvent)postHead);
			newIntervalEvent.editDescription(postHead.getDescription());
			newIntervalEvent.setBaseId(newIntervalEventHead.getId());
			  
			newIntervalEventHead.setNext(newIntervalEvent);
			newIntervalEvent.setPrevious(newIntervalEventHead);
			    
			this.getCalendar().addEvent(newIntervalEventHead);
//			this.getCalendar().PrintAllHeadTails();
			
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
	}
	
	/**
	 * Returns the interval value of this event if given requester is allowed to see it
	 * otherwise return an empty string.
	 * @param requester user which requests for this information.
	 */
	public String getRepetitionFor(User requester) {
		return requester == getOwner() ? "" + this.interval : new String();
	}
	
	/**
	 * get type of this event back
	 * @return a string equals "PointEvent"
	 */
	@Override
	public String getType() {
		return "RepeatingEvent";
	}

	/**
	 * Edits this event by given input arguments. Update the attributes of this RepeatingEvent.
	 * @param name new name of event
	 * @param start new start date/time of this event
	 * @param end new end date/time of this event
	 * @param visibility new visibility state of this event
	 * @param interval new interval size for time/date steps for repentance of event
	 * @param from new from date/time - is being ignored, since a point event never gets transformed to an interval event
	 * @param to new to date/time - is being ignored, since a point event never gets transformed to an interval event
	 * @param description new description of this event.
	 * actual design decision:
	 * there is no way to transform an repeating event to an point event by edit functionality at the moment.
	 * we can just reset trivial stats (i.e. not possible to set repentance by edit atm.
	 * same holds for interval events so. GUI does not support bound edit for them.
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

	}
}
