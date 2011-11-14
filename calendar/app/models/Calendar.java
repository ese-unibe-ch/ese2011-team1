package models;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

import enums.Interval;
import enums.Visibility;

import org.hibernate.transform.ToListResultTransformer;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;


/**
 * Calendar contains a list of heads of events "eventHeads". 
 * Heads are either of type PointEvent, IntervalEvent or RepeatingEvent.
 * It means the elements in the head list are the 1st element of a series of events. 
 * We can add, search for, get, delete events in this calendar by their unique id. 
 * furthermore we can return a series of events depending on a given date.
 * Each calendar has a unique id, name, and an owner.
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
	 * Constructor for a calendar. 
	 * a calendar has a name and an owner.
	 * this constructor sets a unique id for each new calendar and creates a head list.
	 * @param name the name for this calendar.
	 * @param owner the owner for this calendar.
	 */
	public Calendar(String name, User owner){
		this.name = name;
		this.owner = owner;
		this.id = counter;
		counter++;
		eventHeads = new PriorityQueue<Event>();
	}
	
	/*
	 * getters 
	 */
	
	/**
	 * returns the name of this calendar.
	 * @return this.name
	 */
	public String getName(){
		return this.name;
	}
	
	/**
	 * returns the owner of this calendar
	 * @return this.owner
	 */
	public User getOwner(){
		return this.owner;
	}
	
	/**
	 * get the id of this calendar
	 * @return this.id
	 */
	public long getId(){
		return this.id;
	}
	
	/**
	 * return head list of this calendar
	 * @return returns eventHeads
	 */
	public PriorityQueue<Event> getHeadList(){
		return this.eventHeads;
	}
	
	/**
	 *  get the last element of a series of repeating events or 
	 *  if we have a point event, get back the event itself.
	 *  only heads have a none null reference to leaf. care about this fact.
	 *  this is due performance issues.
	 *  @param event event from which we are looking for its tail.
	 *  @return leaf of head-tail structure
	 */
	// TODO currently not used. use this later in generateNextEvents for performance issues.
	public Event getLeafOfEventSeries(Event event){
		if(event.getBaseId() == event.getId()) return event.getLeaf();
		else return getHeadById(event.getBaseId()).getLeaf();
	}
	
	/**
	 * returns the head list of this calendar, i.e. eventHeads 
	 * @return a priority queue of all heads of the calendar
	 */
	public PriorityQueue<Event> getEventHeads(){
		return this.eventHeads;
	}
	
	
	/**
	 * get an event of this calendar by a given id
	 * @param id of the event we are looking for.
	 * @return returns the event with id equals id of input argument.
	 */
	public Event getEventById(long id){
		for(Event event : this.eventHeads){
			Event cursor = event;
			
			do{
				if(cursor.getId() == id) return cursor;
				cursor = cursor.getNextReference();
				if(cursor == null) break;
			}while(cursor.hasNext());
		}
		return null;
	}
	
	/**
	 * get an head from eventHeads by a given id.
	 * getEventById would be able to do this but this method is more efficient if we are just looking for an head.
	 * @param id this is the id of the head we are looking for.
	 * @return returns a head of eventHeads which has the same id as the input argument of this method.
	 */
	public Event getHeadById(long id){
		for(Event event : this.eventHeads)
			if(event.getId() == id) return event;
		
		return null;
	}
	
	/**
	 * Get all heads with same given origin id.
	 * @param originId
	 * @return returns a linked list which contains all heads which have origin id equals originId
	 */
	public LinkedList<Event> getHeadsByOriginId(long originId){
		LinkedList<Event> result = new LinkedList<Event>();
		for(Event head : this.getHeadList())
			if(head.getOriginId() == originId) result.add(head);
		return result;
	}
	
	/**
	 * get all events with same baseId, i.e. all events from a given head
	 * @param originId
	 * @return returns a linked list which contains all events with same base id
	 */
	public LinkedList<Event> getSameBaseIdEvents(long baseId) {
		LinkedList<Event> result = new LinkedList<Event>();
		Event head = this.getHeadById(baseId);
		System.out.println("id " + baseId + " head " + head);
		Event cursor = head;
		while(cursor.hasNext()){
			result.add(cursor);
			cursor = cursor.getNextReference();
		}
		return result;
	}
	
	/**
	 * get all events which are visible for given requester on given local date, correlated to day, month, year
	 * @param day
	 * @param month
	 * @param year
	 * @param requester
	 * @return returns a linked list which contains all for given requester visible events for a given date.
	 */
	public LinkedList<Event> getAllVisibleEventsOfDate(int day, int month, int year, User requester) {
		LocalDate compareDate = new LocalDate(year, month, day);
		LinkedList<Event> result = new LinkedList<Event>();
		
		result.addAll(this.getEventsOfDate(compareDate, requester));
		
		LinkedList<Calendar> observedCalendars = owner.getObservedCalendars();
		LinkedList<Long> shownObservedCalendars = owner.getShownObservedCalendars();
		for (Calendar observedCalendar : observedCalendars) {
			if (shownObservedCalendars.contains(observedCalendar.getId())) {
				result.addAll(observedCalendar.getEventsOfDate(compareDate, requester));
			}
		}
		return result;
	}
	
	/**
	 * get all events which are visible for given requester on given local date, correlated to day, month, year
	 * @param date
	 * @param requester
	 * @return returns a linked list which contains all for given requester visible events for a given date.
	 */
	// TODO use a priority queue instead of a linked list.
	public LinkedList<Event> getEventsOfDate(LocalDate date, User requester){
		LinkedList<Event> result = new LinkedList<Event>();
		// 1. go here through heads
		for(Event head : this.eventHeads){
			if(head.happensOn(date))
				if(head.getVisibility() != Visibility.PRIVATE 
						|| owner == requester) result.add(head);
			
			Event cursor = head;
			while(cursor.hasNext()){
				cursor = cursor.getNextReference();
				if(cursor.happensOn(date))
					if(cursor.getVisibility() != Visibility.PRIVATE 
							|| owner == requester) result.add(cursor);
			}
		}
		return result;
	}
	
	
	/**
	 * Does nothing, don't call this method, it is going to be removed.
	 */
	// TODO remove this method - we don't need it
	public Iterator<Event> getEventList(DateTime start, User requester) {
		return null;
	}
	

	/**
	 * generates the next couple of events for a given head till a limit date.
	 * each time we click for the next month in our calendar, we have to generate following events RepeatingEvents.
	 * the generating process is handled in the event classes itself and depends on the run-time type of an event.
	 * @param head for this head we are going to generate its following events.
	 * @param baseDate this DateTime object defines the limiter till which we generate new events for given head.
	 */
	
	// call this method, whenever we change the month in the calendar GUI
	// or added a new event an declared him as an IntervalEvent or RepeatingEvent
	// or modified an existing event and changed him to an IntervalEvent or RepeatingEvent
	// TODO change date stuff to DateTime
	// TODO check about corner cases, if there exists any.
	// TODO utilize getLeaf() for calculation improvement 
	//      => generate new events, starting from leaf
	
	public void generateNextEvents(Event head, DateTime baseDate){
		//DateTime currentDate = head.getStart();
		DateTime currentDate = baseDate;
		DateTime nextDate = currentDate.plusMonths(1);
		head.generateNextEvents(nextDate);
		
		// TODO later: set here new leaf for the head!
		// i think this would be the most efficient way to do that here.
		
		// for debugging purposes: see if this event has correct next and previous reference
			Event event = head;
			String ee = null;
			while(event.hasNext()){
				if(event.getPreviousReference() != null) ee = event.getPreviousReference().getParsedStartDate();
				System.out.println("current: " +event.getParsedStartDate() + " nextR:"+ event.getNextReference().getParsedStartDate() 
						+ " prevR:" + ee);
				event = event.getNextReference();
			}
			ee = null;
			if(event.getNextReference() != null) ee = event.getNextReference().getParsedStartDate();
			System.out.println("current: " +event.getParsedStartDate() + " nextR:"+ ee 
					+ "              prevR:" + event.getPreviousReference().getParsedStartDate());
		// end debugging
	}
	
	/**
	 * Generate the following events for all events in eventHeads depending on a given base date.
	 * @param baseDate basis for limit date - we generate up to this date plus one month new events
	 */
	public void generateNextEvents(DateTime baseDate){
		for(Event event : this.eventHeads){
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
	 * @param name string representation calendar name
	 */
	public void setName(String name){
		this.name = name;
	}
	
	/**
	 * Sets the owner of this calendar.
	 * @param owner user object
	 */
	public void setOwner(User owner){
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
	 * @param event a new head
	 */
	public void addEvent(Event event){
		this.eventHeads.add(event);
	}
	
	/**
	 * removes an head from the eventHeads list.
	 * @param event victim head which is going to be removed.
	 */
	public void removeHeadFromHeadList(Event event){
		this.eventHeads.remove(event);
	}
	 
	/*
	 * deleter
	 */
	
	/**
	 * removes an victim event from our calendar by a given event id.
	 * depending on the run-time type of an event, this method works differently.
	 * See in each event class for the remove() definition.
	 * @param id is the id of victim event
	 */
	public void removeEvent(long id) {
		Event victim = getEventById(id);
		victim.remove();
	}
	
	
	/*
	 *  modifier
	 */
	
	/**
	 * edits a given event, i.e. set new state for this event.
	 * how an event is actually edited is in each event class separately defined.
	 * depending on the run-time type of an event, some of these arguments are not set.
	 * @param event is victim event we want to edit.
	 * @param newName is the new name for the victim event.
	 * @param newStart is the new start date for the victim event.
	 * @param newEnd is the new end date for the victim event.
	 * @param newVisibility is the new visibility state for the victim event.
	 * @param newInterval is the new interval size for the victim event.
	 * @param newFrom is the new lower bound for the victim event (only set if event is of type IntervalEvent).
	 * @param newTo is the new upper bound for the victim event (only set if event is of type IntervalEvent).
	 * @param newDescription is the new description for the victim event.
	 */
	public void editEvent(Event event, String newName, DateTime newStart, DateTime newEnd, 
			Visibility newVisibility, Interval newInterval, DateTime newFrom, DateTime newTo, String newDescription){
		
		event.edit(newName, newStart, newEnd, newVisibility, newInterval, newFrom, newTo, newDescription);
	}
	
	/**
	 * remove whole series to which an event "member" belongs to.
	 * I.e. remove an head and its tail from this calendar.
	 * @param member this event is part of a head-tail series.
	 */
	public void removeSerieOfRepeatingEvents(Event member){
		// 1. get corresponding head
		// 2. remove victimHead from head list
		long baseId = member.getBaseId();
		Event victimHead = getHeadById(baseId);
		long originId = victimHead.getOriginId();
		LinkedList<Event> originHeads = this.getHeadsByOriginId(originId);
		for(Event head : originHeads){
			this.getHeadList().remove(head);
		}
		
		/*
		long baseId = member.getBaseId();
		Event victimHead = getHeadById(baseId);
		this.getHeadList().remove(victimHead);
		*/
	}
	
	/**
	 * end repentance of an series of RepeatingEvents from a given event on.
	 * this method takes all events from [head,cancelFromThis] and transforms them into IntervalEvents.
	 * @param cancelFromThis from this event on, the repentance gets canceled.
	 */
	public void cancelRepeatingEventRepetitionFromDate(Event cancelFromThis){
		// 1. get corresponding head, this is the new lower bound for IntervalEvent
		// 2. cancelFromThis is the upper bound for this new IntervalEvent
		// 3. Get next of cancelFromThis and kill back-reference from it to cancelFromThis.
		// 4. Remove next reference of cancelFromThis.
		// 5. Transform all Events from head to cancelFromThis into objects of type IntervalEvent
		
		long baseId = cancelFromThis.getBaseId();
		Event victimHead = getHeadById(baseId);
		Event nextFromCancel = cancelFromThis.getNextReference();
		
		cancelFromThis.setNext(null);
		nextFromCancel.setPrevious(null);
		
		this.getHeadList().remove(victimHead);
		Event newHead = new IntervalEvent(victimHead.getStart(),cancelFromThis.getStart(), (RepeatingEvent)victimHead);
		newHead.setBaseId(newHead.getId());
		this.addEvent(newHead);
		
		Event cursor = victimHead;
		Event intervalCursor = newHead;
		Event previous = null; //previous
		
		while(cursor.hasNext()){
			intervalCursor.setPrevious(previous);
			previous = intervalCursor; // store previous
			intervalCursor = new IntervalEvent(victimHead.getStart(), cancelFromThis.getStart(), (RepeatingEvent)cursor);
			intervalCursor.setBaseId(newHead.getBaseId());
			previous.setNext(intervalCursor);
			intervalCursor.setPrevious(previous);
			cursor = cursor.getNextReference();
		}
		
		previous = intervalCursor;
		intervalCursor = new IntervalEvent(victimHead.getStart(), cancelFromThis.getStart(), (RepeatingEvent)cursor);
		intervalCursor.setBaseId(newHead.getId());
		previous.setNext(intervalCursor);
		intervalCursor.setPrevious(previous);
		Event newEnd = new IntervalEvent(victimHead.getStart(),cancelFromThis.getStart(), (RepeatingEvent)cancelFromThis);
		newEnd.setBaseId(newHead.getId());
		intervalCursor.setNext(newEnd);
		newEnd.setPrevious(intervalCursor);
	}
	
	// checker
	
	/**
	 * Test if this Calendar has any Event on a given date that is visible for the specified user.
	 * 
	 * This method also considers Events of all shown observed Calendars.
	 * @param day The day of the date to be checked.
	 * @param month The month of the date to be checked.
	 * @param year The year of the date to be checked.
	 * @param requester The user which requests to see those events.
	 * @return <code>true</code> if there exists any event in either this or any of the shown observed calendars that happens the date.
	 * <code>false</code> if no such Event exists.
	 */
	// TODO add other lists, which we have to check
	public boolean hasEventOnDateIncludingObserved(int day, int month, int year, User requester){
		boolean hasEvent = false;
		LocalDate compareDate = null;
		try {
			compareDate = new LocalDate(year, month, day);
		} catch (Exception e) {}
		hasEvent = hasEventOnDate(compareDate, requester);
		
		// check observedCalendars
		if (!hasEvent) {
			LinkedList<Calendar> observedCalendars = owner.getObservedCalendars();
			LinkedList<Long> shownObservedCalendars = owner.getShownObservedCalendars();
			for (Calendar observedCalendar : observedCalendars) {
				if (shownObservedCalendars.contains(observedCalendar.getId())) {
					hasEvent = observedCalendar.hasEventOnDate(compareDate, requester);
				}
				if (hasEvent)
					break;
			}
		}
		return hasEvent;
	}
	
	/**
	 * Tests if this calendar has an Event which happens on the specified date and is visible for the given user.
	 * 
	 * @param date The date to be checked against.
	 * @param requester The user that requests to see an Event on this date.
	 * @return <code>true</code> if there exists any event in this calendar that happens the date.
	 * <code>false</code> if no such event exists.
	 */
	public boolean hasEventOnDate(LocalDate date, User requester){
		for(Event event : this.eventHeads){
			Event cursor = event;
			do{
				if(cursor.happensOn(date))
					if(requester == owner || cursor.getVisibility() != Visibility.PRIVATE) return true;
				
				cursor = cursor.getNextReference();
				if(cursor == null) break;
			}while(cursor.hasNext());
		}
		return false;
	}
	
	/*
	 * private helpers
	 * this methods we can only call from this class
	 * they are declared as private (never public or even protected) 
	 */
	
	/*
	 * public helpers 
	 */
	
	/**
	 * @return returns string representation of this event, i.e. its name and id.
	 */
	public String toString() {
		return this.name + " ["+this.id+"]";
	}
	
	/**
	 * for debugging
	 * print head and its tail, 
	 * the member itself can be any element of such tail-head structure.
	 * @param member a member of a head and it's tail.
	 */
	public void PrintHeadAndHisTail(Event member){
		long baseId = member.getBaseId();
		LinkedList<Event> events = this.getSameBaseIdEvents(baseId);
		for (Event event : events) 
			System.out.println(event.getParsedStartDate() + " baseid: " + event.getBaseId() + " id " + event.getId());
	}
	
	/**
	 * for debugging
	 * print for all heads in getHeadList the head and its tail, 
	 */
	public void PrintAllHeadTails(){
		for(Event event : this.getHeadList()){
			System.out.println();
			System.out.println("head: " + event.getParsedStartDate() + " id:"+event.getId() );
			this.PrintHeadAndHisTail(event);
		}
	}
}
