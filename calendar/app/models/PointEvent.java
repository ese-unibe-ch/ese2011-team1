package models;

import org.joda.time.DateTime;

import enums.Interval;
import enums.Visibility;

/**
 * PointEvents inherits from Event. Objects of type PointEvent don't have a next
 * and previous reference, i.e. it is set to null. They are just Points. We can
 * delete them, edit it, i.e. update its attributes or transform it to an object
 * of type RepeatingEvent. furthermore we cannot generate successor events of a
 * point event, since a point event has no next reference.
 * 
 * @author team1
 * 
 */
public class PointEvent extends Event {
	/**
	 * Default constructor for a new PointEvent.
	 * 
	 * @param name
	 *            name for this event
	 * @param start
	 *            start date/time for this event
	 * @param end
	 *            end date/time for this event
	 * @param visibility
	 *            visibility state for this event
	 * @param calendar
	 *            the calendar to this event belongs to.
	 */
	public PointEvent(String name, DateTime start, DateTime end,
			Visibility visibility, Calendar calendar) {
		super(name, start, end, visibility, calendar);
		this.setBaseId(this.getId());
		this.setOriginId(this.getBaseId());
	}

	/**
	 * This constructor transform an InvervalEvent into a PointEvent. preserve
	 * id of given RepeatingEvent by calling forceSetId() and set originId
	 * 
	 * @param intervalEvent
	 *            from which we take its values
	 */
	public PointEvent(IntervalEvent intervalEvent) {
		super(intervalEvent.getName(), intervalEvent.getStart(), intervalEvent
				.getEnd(), intervalEvent.getVisibility(), intervalEvent
				.getCalendar());
		this.forceSetId(intervalEvent.getId());
		this.setBaseId(this.getId());
		this.setOriginId(this.getBaseId());
	}

	/**
	 * This constructor transform an Repeating Event into a PointEvent. preserve
	 * id of given RepeatingEvent by calling forceSetId() and set originId
	 * 
	 * @param repeatingEvent
	 *            from which we take its values
	 */
	public PointEvent(RepeatingEvent repeatingEvent) {
		super(repeatingEvent.getName(), repeatingEvent.getStart(),
				repeatingEvent.getEnd(), repeatingEvent.getVisibility(),
				repeatingEvent.getCalendar());
		this.forceSetId(repeatingEvent.getId());
		this.setBaseId(this.getId());
		this.setOriginId(this.getBaseId());
	}

	/**
	 * sets the next event for this event, but this is a point event and since a
	 * point event has no next event, don't do anything here.
	 */
	// TODO maybe throw later an exception to notify programmers they did
	// something against the model
	public void setNext(Event event) {
	}

	/**
	 * sets the previous event for this event, but this is a point event and
	 * since a point event has no previous event, don't do anything here.
	 * 
	 * @param event
	 *            for a point event we don't set the given event ad next
	 */
	@Override
	public void setPrevious(Event event) {
	}

	/**
	 * generates the following events for this event but since this event is a
	 * point event and point events don't have a successor, this method does
	 * nothing but return null
	 */
	@Override
	public void generateNextEvents(DateTime currentDate) {
		return;
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
	 * remove this event, i.e. for a PointEvent: just remove this event from its
	 * calendar's head list.
	 */
	@Override
	public void remove() {
		this.getCalendar().removeHeadFromHeadList(this);
		this.removeAttendantInvitations();
	}

	/**
	 * get type of this event back
	 * 
	 * @return a string equals "PointEvent"
	 */
	@Override
	public String getType() {
		return "PointEvent";
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
	@Override
	public void edit(String name, DateTime start, DateTime end,
			Visibility visibility, Interval interval, DateTime from,
			DateTime to, String description) {

		// if interval not 0, here we have create a new object of type
		// RepeatingEvent
		// remove old head from head list and put new head in list
		// update base id and create successor events for the new head.
		if (interval != Interval.NONE) {
			RepeatingEvent newHead = new RepeatingEvent(name, start, end,
					visibility, this.getCalendar(), interval);
			newHead.setOriginId(newHead.getId());
			this.getCalendar().addEvent(newHead);
			this.remove(); // call remove of a PointEvent

			// this event stay a point event - update attributes.
		} else {
			this.setName(name);
			this.setStart(start);
			this.setEnd(end);
			this.setVisiblility(visibility);
			this.editDescription(description);
		}
		this.removeAttendantInvitations();
	}

}
