package models;

import org.joda.time.DateTime;

import enums.Interval;
import enums.Visibility;

/**
 * IntervalEvent inherits from RepeatingEvent. An IntevalEvent event has an
 * interval, which defines the time step size for its repetition. there are also
 * the attributes, upperBound and lowerBound, which are set by its constructors.
 * they define bound for generating successor events and bound for the event
 * itself. an interval event can be edited, removed, set interval size, set
 * upper and lower bound. for each repeating event, we can call the method
 * generateNextEvents which generates its successor event if certain conditions
 * (see for that generateNextEvents) are fulfilled.
 * 
 * @author team1
 */

public class IntervalEvent extends RepeatingEvent {

	/**
	 * Default constructor for an new interval event.
	 * 
	 * @param name
	 *            name for this event.
	 * @param start
	 *            start date/time for this event.
	 * @param end
	 *            date/time for this event.
	 * @param from
	 *            lower date/time bound for this event.
	 * @param to
	 *            upper date/time bound for this event.
	 * @param end
	 *            date/time for this event.
	 * @param visibility
	 *            visibility state for this event.
	 * @param calendar
	 *            the calendar to this event belongs to.
	 * @param interval
	 *            time-step size for repentance for this event.
	 */
	public IntervalEvent(String name, DateTime start, DateTime end,
			DateTime from, DateTime to, Visibility visibility,
			Calendar calendar, Interval interval) {
		super(name, start, end, visibility, calendar, interval);
		this.lowerBound = from;
		this.upperBound = to;
	}

	/**
	 * This constructor transform an RepeatingEvent into a IntervalEvent.
	 * 
	 * @param from
	 *            lower date/time bound for this event.
	 * @param to
	 *            upper date/time bound for this event.
	 * @param repeatingEvent
	 *            the repeating event which we are going to transform into a
	 *            RepeatingEvent.
	 */
	public IntervalEvent(DateTime from, DateTime to,
			RepeatingEvent repeatingEvent) {
		super(repeatingEvent.getName(), repeatingEvent.getStart(),
				repeatingEvent.getEnd(), repeatingEvent.getVisibility(),
				repeatingEvent.getCalendar(), repeatingEvent.getInterval());
		this.forceSetId(repeatingEvent.getId());
		this.lowerBound = from;
		this.upperBound = to;
	}

	/**
	 * return lower bound of this event.
	 * 
	 * @return returns lower bound of this event
	 */
	public DateTime getFrom() {
		return this.lowerBound;
	}

	/**
	 * return upper bound of this event.
	 * 
	 * @return returns upper bound of this event
	 */
	public DateTime getTo() {
		return this.upperBound;
	}

	/**
	 * set a lower bound for this event.
	 * 
	 * @param from
	 *            lower bound.
	 */
	public void setFrom(DateTime from) {
		this.lowerBound = from;
	}

	/**
	 * set an upper bound for this event.
	 * 
	 * @param to
	 *            upper bound.
	 */
	public void setTo(DateTime to) {
		this.upperBound = to;
	}

	/**
	 * Edits this event by given input arguments. if interval is not none, this
	 * point event gets transformed to a repeating event. otherwise just update
	 * the attributes of this point event.
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
	 *            new description of this event.
	 * 
	 */
	public void edit(String name, DateTime start, DateTime end,
			Visibility visibility, Interval interval, DateTime from, DateTime to) {
		this.setStart(start);
		this.setEnd(end);
		this.setName(name);
		this.setVisiblility(visibility);
		this.setInterval(interval);
		this.setFrom(from);
		this.setTo(to);
		this.removeAttendantInvitations();
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
	 * Generates the next daily or weekly Event for event which cursor 
	 * represents depending on cursor's date/time.
	 * @param cursor base event based on we generate its successor event.
	 * @return returns the successor event of cursor 
	 */
	@Override
	protected Event generateNextEventDailyWeekly(Event cursor){
		DateTime newStartDate = cursor.getStart().plusDays(
				getInterval().getDays());
		DateTime newEndDate = cursor.getEnd().plusDays(
				getInterval().getDays());
		
		DateTime from = ((IntervalEvent) this).getFrom();
		DateTime to = ((IntervalEvent) this).getTo();
		Event nextEvent = new IntervalEvent(this.getName(), newStartDate,
				newEndDate, from, to, cursor.getVisibility(),
				this.getCalendar(), this.getInterval());
		
		return nextEvent;
	}
	
	/**
	 * Generates the next monthly or yearly Event for event which cursor represents depending on cursor's date/time.
	 * @param cursor base event based on we generate its successor event.
	 * @return returns the successor event of cursor 
	 */
	@Override
	protected Event generateNextEventMonthly(Event cursor){
		DateTime newStartDate = cursor.getStart();
		DateTime newEndDate = cursor.getEnd();
		
		newStartDate = monthDateSpecialCaseTransformer(newStartDate);
		newEndDate = monthDateSpecialCaseTransformer(newEndDate);
		
		DateTime from = ((IntervalEvent) this).getFrom();
		DateTime to = ((IntervalEvent) this).getTo();
		Event nextEvent = new IntervalEvent(this.getName(), newStartDate,
				newEndDate, from, to, cursor.getVisibility(),
				this.getCalendar(), this.getInterval());
		
		return nextEvent;
	}
	
	/**
	 * Generates the next monthly or yearly Event for event which cursor represents depending on cursor's date/time.
	 * @param cursor base event based on we generate its successor event.
	 * @return returns the successor event of cursor 
	 */
	@Override
	protected Event generateNextEventYearly(Event cursor){
		DateTime newStartDate = cursor.getStart();
		DateTime newEndDate = cursor.getEnd();
		
		newStartDate = yearDateSpecialCaseTransformer(newStartDate);
		newEndDate = yearDateSpecialCaseTransformer(newEndDate);
		
		DateTime from = ((IntervalEvent) this).getFrom();
		DateTime to = ((IntervalEvent) this).getTo();
		Event nextEvent = new IntervalEvent(this.getName(), newStartDate,
				newEndDate, from, to, cursor.getVisibility(),
				this.getCalendar(), this.getInterval());
		
		return nextEvent;
	}

	/**
	 * removes this event from the calendar to which it belongs to. there are 4
	 * cases which we have to consider for a deletion of a repeating event: (a)
	 * there are two elements in a given interval [head, posthead] (i) victim ==
	 * head ==> deduce a new point event of posthead (ii)victim = posthead ==>
	 * deduce a new point event of head (b) there are three elements in a given
	 * interval [head, posthead, postposthead] victim == posthead ==> deduce two
	 * new point events, one for head, and one for postposthead (c) [head,
	 * previctim] | victim | [postVictim,victim.getTo()] (d) [head,...,
	 * previctim] | victim | [postVictim, +infinite]
	 */
	
	// TODO set new lower bound atm not problematic.
	@Override
	public void remove() {
		Event head = this.getCalendar().getHeadById(this.getBaseId());

		Event preVictim = this.getPreviousReference();
		Event postVictim = this.getNextReference();

		// interval structure: [head,victim], i.e. there are two elements
		if (preVictim == head && postVictim == null) {
			this.setPrevious(null); // victim back reference null
			head.setNext(null);
			this.getCalendar().getEventHeads().remove(head);
			Event newPointEvent = new PointEvent((IntervalEvent) head);
			newPointEvent.editDescription(head.getDescription());
			newPointEvent.setBaseId(newPointEvent.getId());
			newPointEvent.setOriginId(head.getOriginId());
			this.getCalendar().addEvent(newPointEvent);

			// [head,victim,....bound]
		} else if (preVictim == head) {

			head.setNext(null);
			this.setPrevious(null);
			Event newHead = new PointEvent((IntervalEvent) head);
			newHead.editDescription(head.getDescription());
			newHead.setBaseId(newHead.getId());
			newHead.setOriginId(head.getOriginId());
			this.getCalendar().getEventHeads().remove(head);
			this.getCalendar().getEventHeads().add(newHead);

			this.getCalendar().getEventHeads().add(postVictim);
			postVictim.setBaseId(postVictim.getId());
			postVictim.setOriginId(head.getOriginId());

			// reset baseIds of tails of postHead
			Event cursor = postVictim;
			while (cursor.hasNext()) {
				cursor = cursor.getNextReference();
				cursor.setBaseId(postVictim.getId());
			}

			// if we want to delete the head
		} else if (this == head) {

			// [victim,posthead]
			if (postVictim.getNextReference() == null) {
				head.setNext(null);
				postVictim.setPrevious(null);
				this.getCalendar().getEventHeads().remove(head);
				Event newPointEvent = new PointEvent((IntervalEvent) postVictim);
				newPointEvent.editDescription(postVictim.getDescription());
				newPointEvent.setBaseId(newPointEvent.getId());
				newPointEvent.setOriginId(head.getOriginId());
				this.getCalendar().addEvent(newPointEvent);

				// [victim,posthead,...,leafOfInterval]
			} else {

				Event postHead = head.getNextReference();
				this.getCalendar().getEventHeads().remove(head);
				head.setNext(null);
				postHead.setPrevious(null);
				this.getCalendar().getEventHeads().add(postHead);
				postHead.setBaseId(postHead.getId());
				postHead.setOriginId(head.getOriginId());

				// reset baseIds of tails of postHead
				Event cursor = postHead;
				while (cursor.hasNext()) {
					cursor = cursor.getNextReference();
					cursor.setBaseId(postHead.getId());
				}
			}

			// if victim is the leaf, i.e. victim is the last element of the
			// list and list has more than two elements.
		} else if (postVictim == null) {
			// TODO set new upper bound;
			// TODO put this case in first case with a if...
			preVictim.setNext(null);
			this.setPrevious(null);

			// [head,victim,postvictim] ==> two point events
		} else if (postVictim.getNextReference() == null
				&& preVictim.getPreviousReference() == null) {
			this.setNext(null);
			this.setPrevious(null);
			postVictim.setPrevious(null);
			preVictim.setNext(null);

			this.getCalendar().getEventHeads().remove(head);

			Event newRightPointEvent = new PointEvent(
					(IntervalEvent) postVictim);
			newRightPointEvent.editDescription(postVictim.getDescription());
			newRightPointEvent.setBaseId(newRightPointEvent.getBaseId());
			newRightPointEvent.setOriginId(head.getOriginId());

			Event newLeftPointEvent = new PointEvent((IntervalEvent) preVictim);
			newLeftPointEvent.editDescription(preVictim.getDescription());
			newLeftPointEvent.setBaseId(newLeftPointEvent.getBaseId());
			newLeftPointEvent.setOriginId(head.getOriginId());

			this.getCalendar().getEventHeads().add(newRightPointEvent);
			this.getCalendar().getEventHeads().add(newLeftPointEvent);

			// [head,...,victim,postvictim]
		} else if (postVictim.getNextReference() == null) {

			// reset references
			postVictim.setPrevious(null);
			this.setNext(null);
			this.setPrevious(null);
			preVictim.setNext(null);

			// postVictim point creation
			Event newPoint = new PointEvent((IntervalEvent) postVictim);
			newPoint.editDescription(postVictim.getDescription());
			newPoint.setBaseId(newPoint.getId());
			long originId = head.getOriginId();
			newPoint.setOriginId(originId);

			this.getCalendar().getEventHeads().add(newPoint);

			// [head,..., previctim] | victim | [postVictim,victim.getTo()]
		} else {

			preVictim.setNext(null);
			postVictim.setPrevious(null);

			this.getCalendar().addEvent(postVictim);
			postVictim.setBaseId(postVictim.getId());
			postVictim.setOriginId(head.getOriginId());
			((IntervalEvent) head).setFrom(preVictim.getStart());

			Event cursor = head;
			while (cursor.hasNext()) {
				cursor = cursor.getNextReference();
				cursor.setBaseId(head.getId());
				((IntervalEvent) cursor).setTo(preVictim.getStart());
			}

			((IntervalEvent) postVictim).setFrom(postVictim.getStart());

			// set for whole post victim tail events their new baseId
			cursor = postVictim;
			while (cursor.hasNext()) {
				cursor = cursor.getNextReference();
				cursor.setBaseId(postVictim.getId());
				((IntervalEvent) cursor).setTo(postVictim.getStart());
			}
		}
		this.removeAttendantInvitations();
	}

	/**
	 * get type of this event back
	 * 
	 * @return a string equals "PointEvent"
	 */
	@Override
	public String getType() {
		return "IntervalEvent";
	}

	/**
	 * checks the upper-and lower bound of this event are equal null or if
	 * lowerBound < event < upperBound holds
	 * 
	 * @param event
	 *            event we are going to check if it is in between its bounds
	 */
	@Override
	protected boolean isCurrentInBounds(Event event) {
		if (upperBound == null && lowerBound == null)
			return true;
		return (event.getStart().compareTo(upperBound) == -1 && event
				.getStart().compareTo(lowerBound) == 1);
	}

}
