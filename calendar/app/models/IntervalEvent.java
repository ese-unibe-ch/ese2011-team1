package models;
import models.Event.Visibility;

import org.joda.time.DateTime;

public class IntervalEvent extends RepeatingEvent{

	public IntervalEvent(String name, DateTime start, DateTime end, DateTime from, DateTime to, Visibility visibility,Calendar calendar, int interval) {
		super(name, start, end, visibility,calendar, interval);
		this.lowerBound = from;
		this.upperBound = to;
		// TODO fixe vieles...
	}
	
	// we need this constructor, to create an object of type IntervalEvent from a given object of type RepeatingEvent
	// important: preserve id of given RepeatingEvent by calling forceSetId().
	public IntervalEvent(DateTime from, DateTime to, RepeatingEvent repeatingEvent) {
		super(repeatingEvent.getName(),repeatingEvent.getStart(), repeatingEvent.getEnd(), 
				repeatingEvent.getVisibility(),repeatingEvent.getCalendar(), repeatingEvent.getInterval());
		this.forceSetId(repeatingEvent.getId());
		this.lowerBound = from;
		this.upperBound = to;
		// TODO fixe vieles... falg usw
	}
	
	public DateTime getFrom(){
		return this.lowerBound;
	}
	
	public DateTime getTo(){
		return this.upperBound;
	}
	

	public void setFrom(DateTime from){
		this.lowerBound = from;
	}
	
	public void setTo(DateTime to){
		this.upperBound = to;
	}
	
	public void edit(String name, DateTime start, DateTime end,
			Visibility visibility, int interval, DateTime from, DateTime to) {
		this.setStart(start);
		this.setEnd(end);
		this.setName(name);
		this.setVisiblility(visibility);
		this.setInterval(interval);
		this.setFrom(from);
		this.setTo(to);
	}

	@Override
	public int compareTo(Event event) {
		return this.getStart().compareTo(event.getStart());
	}
}
