package models;
import org.joda.time.DateTime;

import android.database.Cursor;

public class RepeatingEvent extends Event{
	private int interval;
	protected Event current = null;
	protected DateTime upperBound = null;
	protected DateTime lowerBound = null;
	protected boolean hasBoundReached = isCurrentInBounds();
	
	public RepeatingEvent(String name, DateTime start, DateTime end, Visibility visibility, Calendar calendar, int interval) {
		super(name, start, end, visibility, calendar);
		this.interval = interval;
	}
	
	public RepeatingEvent(PointEvent event, int interval) {
		super(event.getName(), event.getStart(), event.getEnd(), event.getVisibility(), event.getCalendar());
		this.interval = interval;
		this.forceSetId(event.getId());
		this.setBaseId(this.getId());
	}

	@Override
	public void setNext(Event event) {
		this.next = event;
	}
	
	@Override
	public void setPrevious(Event event) {
		this.previous = event;
	}
	
	public void setInterval(int interval){
		this.interval = interval;
	}
	
	/**
	 * Get the interval of this Events repetition.
	 * @return 
	 * 1, if this Event is repeated on a daily basis.
	 * 7, if this Event is repeated weekly.
	 * 30, if this Event is repeated every month.
	 * 365, if this Event is repeated every year.
	 */
	public int getInterval(){
		return this.interval;
	}
	
	/**
	 * Calculate all next events for the current selected month.
	 * precondition and postcondition: current = null
	 */
	@Override
	public void generateNextEvents(DateTime limitDate) {
		this.current = null;
		int interval = this.getInterval();
		
		if(interval == 7 || interval == 1)
			generateDaylyOrWeekly(this, limitDate, interval);
		else if(interval == 30)
			generateMonthly(this, limitDate, interval);
		else if(interval == 365)
			generateYearly(this, limitDate, interval);
		else
			System.out.println("this case not handled");
		this.current = null;
	}
	
	// TODO add some comment
	protected void generateDaylyOrWeekly(Event base, DateTime limiter, int interval){
		Event cursor = base;
		this.current = cursor;
		// as long as whole month is calculated
		while(cursor.getStart().isBefore(limiter) && !hasBoundReached){
			
			// if there is no next event, then create a new one.
			if(!cursor.hasNext()){
					
				DateTime newStartDate = cursor.getStart().plusDays(getInterval());
				DateTime newEndDate = cursor.getEnd().plusDays(getInterval());
				
				RepeatingEvent nextEvent = new RepeatingEvent(this.getName(), newStartDate, newEndDate, cursor.getVisibility(), this.getCalendar(), this.getInterval());
				cursor.setNext(nextEvent);
				
				nextEvent.setPrevious(cursor);
				nextEvent.setBaseId(this.getBaseId());
			}
			
			//move cursor
			cursor = cursor.getNextReference();
			this.current = cursor;
		}
	}
	
	// TODO highly buggy due to corner cases
	// TODO huge optimization potential: calculate no only events till limiter but about always constant amount.
	protected void generateMonthly(Event base, DateTime limiter, int interval){
		Event cursor = base;
		this.current = cursor;
		// solange bis monat abgedeckt
		while(cursor.getStart().compareTo(limiter) == -1 && !hasBoundReached){
			
			// wenn kein n�chster event
			if(!cursor.hasNext()){
				
				DateTime newStartDate = cursor.getStart().plusMonths(1);
				DateTime newEndDate = cursor.getEnd().plusMonths(1);
				
				RepeatingEvent nextEvent = new RepeatingEvent(this.getName(), newStartDate, newEndDate, cursor.getVisibility(), this.getCalendar(), this.getInterval());
				cursor.setNext(nextEvent);
				
				nextEvent.setPrevious(cursor);
				nextEvent.setBaseId(this.getBaseId());
			}
			
			//move cursor
			cursor = cursor.getNextReference();
			this.current = cursor;
		}
	}
	
	// TODO highly buggy due to corner cases.
	// TODO huge optimization potential: calculate no only events till limiter but about always constant amount.
	protected void generateYearly(Event base, DateTime limiter, int interval){
		Event cursor = base;
		this.current = cursor;
		// solange bis monat abgedeckt
		while(cursor.getStart().compareTo(limiter) == -1 && !hasBoundReached){
			
			// wenn kein n�chster event
			if(!cursor.hasNext()){
				
				DateTime newStartDate = cursor.getStart().plusYears(1);
				DateTime newEndDate = cursor.getEnd().plusYears(1);
				
				RepeatingEvent nextEvent = new RepeatingEvent(this.getName(), newStartDate, newEndDate, cursor.getVisibility(), this.getCalendar(), this.getInterval());
				cursor.setNext(nextEvent);
				
				nextEvent.setPrevious(cursor);
				nextEvent.setBaseId(this.getBaseId());
			}
			
			//move cursor
			cursor = cursor.getNextReference();
			this.current = cursor;
		}
	}
	
	/*
	 * checks
	 */
	
	// TODO verify if boundchecks are okay
	// bounds not null and then lowerBound < current < upperBound?
	protected boolean isCurrentInBounds(){
		if(upperBound == null && lowerBound == null) return false;
		return (this.current.getStart().compareTo(upperBound) == -1 
				&& this.current.getStart().compareTo(lowerBound) == 1);
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

	
	public void edit(String name, DateTime start, DateTime end,
			Visibility visibility, int interval) {
		this.setStart(start);
		this.setEnd(end);
		this.setName(name);
		this.setVisiblility(visibility);
		this.setInterval(interval);
	}

	public void init() {
		generateNextEvents(this.getStart().plusMonths(1));
	}

	// possible resulting interval structures after deletion
	// there are 3 cases which we have to consider:
	// (a) victim equals current head => next of head gets new head
	// (b) victim equals next after head => head gets a PointEvent, victim.next a new head 
	// (c) [head, previctim] | victim | [postVictim, +infinite]
	// care about setting new baseId correctly.
	@Override
	public void remove() {

		Event head = this.getCalendar().getHeadById(this.getBaseId());
		Event preVictim = this.getPreviousReference();
		Event postVictim = this.getNextReference();

		// case (a) -- seems to work after some testing
		if(this == head){
			Event postHead = this.getNextReference();
			postHead.setPrevious(null);
			this.setNext(null);
			this.getCalendar().removeHeadFromHeadList(this);
			this.getCalendar().addEvent(postHead);
			
			// go through posthead tail	
			postHead.setBaseId(postHead.getId());
			Event cursor = postHead; 
			while(cursor.hasNext()){
				cursor = cursor.getNextReference();
				cursor.setBaseId(postHead.getBaseId());
			}
			
		// case (b)
		}else if(this == head.getNextReference()){
			Event postPostHead = this.getNextReference();
			postPostHead.setPrevious(null);
			this.setPrevious(null);
			this.setNext(null);
			head.setNext(null);
	
			Event newPointEvent = new PointEvent((RepeatingEvent)head);

			this.getCalendar().removeHeadFromHeadList(head);
			this.getCalendar().addEvent(newPointEvent);	
			this.getCalendar().addEvent(postPostHead);

			postPostHead.setBaseId(postPostHead.getId());
			Event cursor = postPostHead; 
			
			while(cursor.hasNext()){
				cursor = cursor.getNextReference();
				cursor.setBaseId(postPostHead.getId());
			}
			postPostHead.generateNextEvents(postPostHead.getNextReference().getStart());
			
		// case (c)
		}else{
			System.out.println("case c entrered");
			this.setNext(null);
			this.setPrevious(null);
			preVictim.setNext(null);
			postVictim.setPrevious(null);
			this.getCalendar().removeHeadFromHeadList(head);
			
			/*
			IntervalEvent newIntervalEvent = new IntervalEvent(head.getStart(), preVictim.getStart(), (RepeatingEvent)head);
			newIntervalEvent.setBaseId(newIntervalEvent.getId());
			
			
			Event cursor = newIntervalEvent; 
			
			while(cursor.hasNext()){
				cursor = cursor.getNextReference();
				IntervalEvent newIntervalCursor = new IntervalEvent(head.getStart(), preVictim.getStart(), (RepeatingEvent)cursor);
				newIntervalCursor.setPrevious(cursor);
				cursor.setNext(newIntervalCursor);
				newIntervalCursor.setBaseId(newIntervalEvent.getId());
				// set bound too
			}
			
			this.getCalendar().addEvent(newIntervalEvent);
			*/
		}
		
	}
	
	public String getRepetitionFor(User requester) {
		return requester == getOwner() ? ""+this.interval : new String();
	}
}
