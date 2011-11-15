package models;
import enums.Interval;
import enums.Visibility;

import org.joda.time.DateTime;

public class IntervalEvent extends RepeatingEvent{

	public IntervalEvent(String name, DateTime start, DateTime end, DateTime from, DateTime to, Visibility visibility,Calendar calendar, Interval interval) {
		super(name, start, end, visibility,calendar, interval);
		//this.setOriginId(this.getBaseId());
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
	
	/**
	 * Edits this event by given input arguments. if interval is not none, this point event gets transformed to a repeating event.
	 * otherwise just update the attributes of this point event.
	 * @param name new name of event
	 * @param start new start date/time of this event
	 * @param end new end date/time of this event
	 * @param visibility new visibility state of this event
	 * @param interval new interval size for time/date steps for repentance of event
	 * @param from new from date/time - is being ignored, since a point event never gets transformed to an interval event
	 * @param to new to date/time - is being ignored, since a point event never gets transformed to an interval event
	 * @param description new description of this event.
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
	}

	@Override
	public int compareTo(Event event) {
		return this.getStart().compareTo(event.getStart());
	}
	
	/**
	 * removes this event from the calendar to which it belongs to.
	 * there are 4 cases which we have to consider for a deletion of a repeating event:
	 * (a) there are two elements in a given interval [head, posthead]
	 *     (i) victim == head ==> deduce a new point event of posthead
	 *     (ii)victim = posthead ==> deduce a new point event of head
	 * (b) there are three elements in a given interval [head, posthead, postposthead]
	 *     victim == posthead ==> deduce two new point events, one for head, and one for postposthead
	 * (c) [head, previctim] | victim | [postVictim,victim.getTo()]	
	 * (d) [head,..., previctim] | victim | [postVictim, +infinite]	
	 * care about setting new baseId correctly.
	 */
	@Override
	public void remove() {
		Event head = this.getCalendar().getHeadById(this.getBaseId());
		Event preVictim = this.getPreviousReference();
		Event postVictim = this.getNextReference();
		
		// interval structure: [head,victim], i.e. there are two elements
		if(preVictim == head && postVictim == null){
			this.setPrevious(null); // victim back reference null
			head.setNext(null);
			this.getCalendar().getHeadList().remove(head);		
			Event newPointEvent = new PointEvent((IntervalEvent)head);
			newPointEvent.editDescription(head.getDescription());
			newPointEvent.setBaseId(newPointEvent.getId());
			newPointEvent.setOriginId(head.getOriginId());
			this.getCalendar().addEvent(newPointEvent);
			
		// if we want to delete the head
		}else if(this == head){
			
			//[victim,posthead]
			if(postVictim.getNextReference() == null){
				head.setNext(null);
				postVictim.setPrevious(null);
				this.getCalendar().getHeadList().remove(head);
				Event newPointEvent = new PointEvent((IntervalEvent)postVictim);
				newPointEvent.editDescription(postVictim.getDescription());
				newPointEvent.setBaseId(newPointEvent.getId());
				newPointEvent.setOriginId(head.getOriginId());
				this.getCalendar().addEvent(newPointEvent);
				
			// [victim,posthead,...,leafOfInterval]
			}else{
				
			// TODO set new lower bound atm not problematic.
				Event postHead = head.getNextReference();
				this.getCalendar().getHeadList().remove(head);
				head.setNext(null);
				postHead.setPrevious(null);
				this.getCalendar().getHeadList().add(postHead);
				postHead.setBaseId(postHead.getId());
				postHead.setOriginId(head.getOriginId());
				
				// reset baseIds of tails of postHead
				Event cursor = postHead;
				while(cursor.hasNext()){
					cursor = cursor.getNextReference();
					cursor.setBaseId(postHead.getId());
				}
			}
		// if victim is the leaf, i.e. victim is the last element of the list and list has more than two elements.
		}else if(postVictim == null){
			// TODO set new upper bound;
			// TODO put this case in first case with a if... 
			preVictim.setNext(null);
			this.setPrevious(null);
			
			// [head,...,victim,postvictim]
		}else if(postVictim.getNextReference() == null){
			System.out.println("==============> this case ppv == null");
			
			//reset references
			postVictim.setPrevious(null);
			this.setNext(null);
			this.setPrevious(null);
			preVictim.setNext(null);
			
			//postVictim point creation
			Event newPoint = new PointEvent((IntervalEvent)postVictim);
			newPoint.editDescription(postVictim.getDescription());
			newPoint.setBaseId(newPoint.getId());
			newPoint.setOriginId(head.getOriginId());
			
			this.getCalendar().getHeadList().add(newPoint);
			
		//[head,victim,postvictim] ==> two point events	
		}else if(postVictim.getNextReference() == null && preVictim.getPreviousReference() == null){
	
			this.setNext(null);
			this.setPrevious(null);
			postVictim.setPrevious(null);
			preVictim.setNext(null);
			
			this.getCalendar().getHeadList().remove(head);
			
			Event newRightPointEvent = new PointEvent((IntervalEvent)postVictim);
			newRightPointEvent.editDescription(postVictim.getDescription());
			newRightPointEvent.setBaseId(newRightPointEvent.getBaseId());
			newRightPointEvent.setOriginId(head.getOriginId());
			
			Event newLeftPointEvent = new PointEvent((IntervalEvent)preVictim);
			newLeftPointEvent.editDescription(preVictim.getDescription());
			newLeftPointEvent.setBaseId(newLeftPointEvent.getBaseId());
			newLeftPointEvent.setOriginId(head.getOriginId());
			
			this.getCalendar().getHeadList().add(newRightPointEvent);
			this.getCalendar().getHeadList().add(newLeftPointEvent);
			
			
		// [head, previctim] | victim | [postVictim,victim.getTo()]	
		}else{
			//System.out.println("==============> this case");
			
			preVictim.setNext(null);
			postVictim.setPrevious(null);
			this.getCalendar().addEvent(postVictim);
			
			// set for all postvictims events their new baseId
			Event cursor = postVictim;
			postVictim.setBaseId(postVictim.getId());
			while(cursor.hasNext()){
				cursor = cursor.getNextReference();
				cursor.setBaseId(postVictim.getId());
			}
		}		
	}
	
	/**
	 * get type of this event back
	 * @return a string equals "PointEvent"
	 */
	@Override
	public String getType() {
		return "IntervalEvent";
	}
	
	// TODO verify if boundchecks are okay
	// bounds not null and then lowerBound < current < upperBound?
	@Override
	protected boolean isCurrentInBounds(Event event) {
		if (upperBound == null && lowerBound == null)
			return true;
		return (event.getStart().compareTo(upperBound) == -1 && event
				.getStart().compareTo(lowerBound) == 1);
	}
	
}
