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
	

	@SuppressWarnings("unused")
	@Override
	public void remove() {
		Event head = this.getCalendar().getHeadById(this.getBaseId());
		Event preVictim = this.getPreviousReference();
		Event postVictim = this.getNextReference();
		System.out.println("***************** " + this +" date " +this.getParsedStartDate());
		System.out.println("***************** " + postVictim +" date " +postVictim.getParsedStartDate());
		
		// interval structure: [head,victim], i.e. there are two elements
		if(preVictim == head && postVictim == null){
			postVictim.setPrevious(null);

			this.getCalendar().getHeadList().remove(this);
			this.getCalendar().addEvent(postVictim);			
			
			Event cursor = postVictim;
			postVictim.setBaseId(postVictim.getId());
			
			while(cursor.hasNext()){
				cursor = cursor.getNextReference();
				cursor.setBaseId(postVictim.getId());
			}
			
		// if we want to delete the head
		}else if(this == head){
		// TODO set new lower bound atm not problematic.
			Event postHead = head.getNextReference();
			this.getCalendar().getHeadList().remove(head);
			head.setNext(null);
			postHead.setPrevious(null);
			this.getCalendar().getHeadList().add(postHead);
			postHead.setBaseId(postHead.getId());
			
			// reset baseIds of tails of postHead
			Event cursor = postHead;
			while(cursor.hasNext()){
				cursor = cursor.getNextReference();
				cursor.setBaseId(postHead.getId());
			}
			
		// if victim is the leaf, i.e. victim is the last element of the list.
		}else if(postVictim == null){
			// TODO set new upper bound;
			System.out.println("this case i am currently reworking, right?!");
			preVictim.setNext(null);
			this.setPrevious(null);
			
		// [head ,previctim] | victim | [postVictim,victim.getTo()]
		}else if(preVictim.previous == null && postVictim.getNextReference() == null){
			System.out.println("special case ");
		}else{
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
	
	@Override
	public String getType() {
		return "IntervalEvent";
	}
	
}
