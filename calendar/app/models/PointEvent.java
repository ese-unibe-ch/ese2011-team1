package models;
import org.joda.time.DateTime;

import enums.Visibility;

public class PointEvent extends Event{

	public PointEvent(String name, DateTime start, DateTime end, Visibility visibility, Calendar calendar) {
		super(name, start, end, visibility, calendar);
		this.setBaseId(this.getId());
		this.setOriginId(this.getBaseId());
	}
	
	// transform an InvervalEvent into a PointEvent
	// important: preserve id of given RepeatingEvent by calling forceSetId().
	public PointEvent(IntervalEvent intervalEvent){
		super(intervalEvent.getName(), intervalEvent.getStart(), 
				intervalEvent.getEnd(), intervalEvent.getVisibility(), intervalEvent.getCalendar());
		this.forceSetId(intervalEvent.getId());
		this.setBaseId(this.getId());
	}
	
	// transform an Repeating Event into a PointEvent
	// important: preserve id of given RepeatingEvent by calling forceSetId().
	public PointEvent(RepeatingEvent repeatingEvent){
		super(repeatingEvent.getName(), repeatingEvent.getStart(), 
				repeatingEvent.getEnd(), repeatingEvent.getVisibility(), repeatingEvent.getCalendar());
		this.forceSetId(repeatingEvent.getId());
		this.setBaseId(this.getId());
	}
	
	// since a point event has no next event, don't do anything here.
	// maybe throw later an exception to notify programmers they did something against the model
	public void setNext(Event event){}
	
	@Override
	public void setPrevious(Event event) {}
	
	// force to return null, so we never have actually a problem, even a programmer failed to intent the model standards.
	@Override
	public void generateNextEvents(DateTime currentDate) {
		return;
	}
	
	/**
	 * Compare this Events start date with the arguments start date according to the definition of {@link Comparable#compareTo}
	 * @param event The event to compare this Event with.
	 * @returns a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object.
	 */
	@Override
	public int compareTo(Event event) {
		return this.getStart().compareTo(event.getStart());
	}

	@Override
	// a) PointEvent: just remove it from head list, done.
	public void remove() {
		this.getCalendar().removeHeadFromHeadList(this);
	}

	@Override
	public String getType() {
		return "PointEvent";
	}
	
	// TODO implementation
	// there are two cases 
	// point -> point : update stats
	// point -> repeating : updatete stats + reset to repeating (b)
	// to decide which case happened, test for flag 
	// TODO write explicit whats about this flag...

	@Override
	public void edit(String name, DateTime start, DateTime end,
			Visibility visibility, int interval, DateTime from, DateTime to,
			String description) {
	
		// if interval not 0, then we have case (b)
		// here we have create a new object of type RepeatingEvent
		// remove old head from head list and put new head in list
		// update base id and create successor events for the new head.
		if(interval != 0){
			RepeatingEvent newHead = new RepeatingEvent(name, start, end, visibility, this.getCalendar(), interval);
			this.getCalendar().addEvent(newHead);
			this.remove(); // call remove of a PointEvent
			
		}else{
			this.setName(name);
			this.setStart(start);
			this.setEnd(end);
			this.setVisiblility(visibility);
			this.editDescription(description);
		}
		
	}
	

}
