package models;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

import models.Event.Visibility;

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
	
	public String getName(){
		return this.name;
	}
	
	public User getOwner(){
		return this.owner;
	}
	
//	@SuppressWarnings("unchecked")
//	public Queue<Event> getOwnersObersvedEventQueue(){
//		return owner.getObservedEvents();
//	}
	
//	@SuppressWarnings("unchecked")
//	public Queue<Event> getOwnersFriendsBirthdaysQueue(){
//		return owner.getFriendsBirthdays();
//	}
	
	public long getId(){
		return this.id;
	}
	
	public PriorityQueue<Event> getHeadList(){
		return this.eventHeads;
	}
	
	// get the last element of a series of repeating events or 
	// if we have a point event, get back the event itself.
	// only heads have a none null reference to leaf. care about this fact. 
	// this is due performance issues.
	public Event getLeafOfEventSeries(Event event){
		if(event.getBaseId() == event.getId()) return event.getLeaf();
		else return getHeadById(event.getBaseId()).getLeaf();
	}
	
	public PriorityQueue<Event> getEventHeads(){
		return this.eventHeads;
	}
	
	// mache vor call dieser methode immer check, hasEvent(long id)
	// und mache call dieser methode nur, wenn hasEvent yields true;
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
	
	// getEventById would be able to do this but this method is more efficient if we are just looking for an head.
	public Event getHeadById(long id){
		for(Event event : this.eventHeads)
			if(event.getId() == id) return event;
		
		return null;
	}
	
	// get all events with same baseId, i.e. all events from a given head
	// look in head list for given id
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
	
	// return a list which contains all dates depending on input date
	// where we only compare its year, month and day for equality
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
	
	// TODO fix this method. uses DateTime
	public Iterator<Event> getEventList(DateTime start, User requester) {
		return null;
	}
	
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
		
		//Date nextDate = new Date(currentDate.getYear(),
		//		currentDate.getMonth()+1, currentDate.getDate(),
		//		currentDate.getHours(), currentDate.getMinutes());
		
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
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setOwner(User owner){
		this.owner = owner;
	}
	
	
	/*
	 * add, delete modify, checks 
	 */
	
	// adder
	
	// add an event into head list eventHeads
	public void addEvent(Event event){
		// care about future filters...
		this.eventHeads.add(event);
	}
	
	public void removeHeadFromHeadList(Event event){
		this.eventHeads.remove(event);
	}
	
	

	
	// deleter
	
	// 1. find event and point with a courser to him
	// 2. check type of this event, depending on type do different algorithm to delete event
	// a) PointEvent: just remove it from head list, done.
	// b) IntervalEvent: 
	//		case: victim is not in head		
	//			split interval into two smaller intervals left and right, relatively to victim
	//			Reset references and put 1st element of 2nd smaller interval into head list
	//		case: victim is in head
	//			get next event after head and put it into head list, remove head from head list
	// c) RepeatingEvent:
	public void removeEvent(long id) {
		System.out.println("id " + id);
		Event victim = getEventById(id);
		//System.out.println("this date we want to remove: " + victim.getParsedStartDate());
		victim.remove();
	}
	
	
	/*
	 *  modifier
	 */
	
	// call here the corresponding event.edit methods
	// care about type changes if we change from a PointEvent to an RepeatingEvent.
	// at the moment we only can change an PointEvent to an RepeatingEvent
	// or keep a PointEvent a PointEvent, keep a RepeatingEvent...
	// TODO care about other cases!
	// TODO do this in Eventclasses and just call here event.edit(...)
	public void editEvent(Event event, String newName, DateTime newStart, DateTime newEnd, 
			Visibility newVisibility, int newInterval, DateTime newFrom, DateTime newTo, String newDescription){
		
		// make a point event, suppose we are a pointevent
		// other possibilities: changing from an intervalEvent or even an RepeatingEvent are somehow silly
		// TODO talk with others about this assumption...
		
		if(event instanceof PointEvent){
			
			if(newInterval == 0){
				event.edit(name, newStart, newEnd, newVisibility);	
				event.editDescription(newDescription);
			}else{
				Event newEvent = new RepeatingEvent((PointEvent)event, newInterval);
				newEvent.setStart(newStart);
				newEvent.setEnd(newEnd);
				newEvent.editDescription(newDescription);
				newEvent.generateNextEvents(newStart);
			}
			
		// at the moment we only can change an PointEvent to an RepeatingEvent
		}else{
			// as well here. we cannot cast from an IntervalEvent or even an 
			// RepeatingEvent to each other or a PointEvent.
			// care about ordering of this if statements due the inheritance hierarchy
			
			// TODO atm buggy, apply this edit function to head and all his referenced events!
			
			if(event instanceof IntervalEvent){
				Event cursor = this.getHeadById(event.getBaseId());	
				do{
					((IntervalEvent)cursor).edit(newName, newStart, newEnd, newVisibility, newInterval, newFrom, newTo);
					cursor = cursor.getNextReference();
				}while(cursor.hasNext());
				
			}else if( event instanceof RepeatingEvent){
				Event cursor = this.getHeadById(event.getBaseId());
				do{
					((RepeatingEvent)cursor).edit(newName, newStart, newEnd, newVisibility, newInterval);
					cursor = cursor.getNextReference();
				}while(cursor.hasNext());
				
			}else{
				System.out.println("ERROR CASE in editEvent() in class Calendar");
			}
			event.editDescription(newDescription);
		}
	}
	
	// remove whole series to which an event "member" belongs to
	public void removeSerieOfRepeatingEvents(Event member){
		// 1. get corresponding head
		// 2. remove victimHead from head list
		
		long baseId = member.getBaseId();
		Event victimHead = getHeadById(baseId);
		this.getHeadList().remove(victimHead);
	}
	
	
	// TODO check if references are correctly set.
	// set this event series from RepeatingEvent to IntervalEvent
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
		this.addEvent(newHead);
		
		Event cursor = victimHead;
		Event intervalCursor = newHead;
		Event previous = null; //prev
		
		while(cursor.hasNext()){
			intervalCursor.setPrevious(previous);
			previous = intervalCursor; // store previous
			intervalCursor = new IntervalEvent(victimHead.getStart(), cancelFromThis.getStart(), (RepeatingEvent)cursor);
			previous.setNext(intervalCursor);
			intervalCursor.setPrevious(previous);
			cursor = cursor.getNextReference();
		}
		
		previous = intervalCursor;
		intervalCursor = new IntervalEvent(victimHead.getStart(), cancelFromThis.getStart(), (RepeatingEvent)cursor);
		previous.setNext(intervalCursor);
		intervalCursor.setPrevious(previous);
		Event newEnd = new IntervalEvent(victimHead.getStart(),cancelFromThis.getStart(), (RepeatingEvent)cancelFromThis);
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
			//	System.out.println("happens on is: " + cursor.happensOn(date) + " for:st " + cursor.getParsedStartDate() +" nd "+ cursor.getParsedEndDate() + " d "+ date); 
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
	
	public String toString() {
		return this.name + " ["+this.id+"]";
	}
	
	// for debugging:
	// input a member of a head and it's tail. 
	//the member itself can be any element of such tail-head structure.
	public void PrintHeadAndHisTail(Event member){
		long baseId = member.getBaseId();
		LinkedList<Event> events = this.getSameBaseIdEvents(baseId);
		for (Event event : events) 
			System.out.println(event.getParsedStartDate() + " baseid: " + event.getBaseId() + " id " + event.getId());
	}
	
	public void PrintAllHeadTails(){
		for(Event event : this.getHeadList()){
			System.out.println();
			System.out.println("head: " + event.getParsedStartDate() + " id:"+event.getId() );
			this.PrintHeadAndHisTail(event);
		}
	}
}
