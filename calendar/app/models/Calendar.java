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
				cursor = event.getNextReference();
			}while(event.hasNext());
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
		
		Event cursor = head;
		while(cursor.hasNext()){
			result.add(cursor);
			cursor = cursor.getNextReference();
		}
		
		return result;
	}
	
	public LinkedList<Event> getEventsOfDate(int day, int month, int year, User requester) {
		LocalDate compareDate = new LocalDate(year, month, day);
		LinkedList<Event> result = new LinkedList<Event>();
		// 1. go here through heads
		for(Event head : this.eventHeads){
			//if(head.getStart().toLocalDate().equals(compareDate)) // adde hier check
			if(checkHappensOn(head.getStart().toLocalDate(), head.getEnd().toLocalDate(), compareDate))	
				if(head.getVisibility() != Visibility.PRIVATE 
						|| owner == requester) result.add(head);
			
			Event cursor = head;
			while(cursor.hasNext()){
				//if(cursor.getStart().toLocalDate().equals(compareDate))
				if(checkHappensOn(cursor.getStart().toLocalDate(), cursor.getEnd().toLocalDate(), compareDate))	
					if(cursor.getVisibility() != Visibility.PRIVATE 
							|| owner == requester) result.add(cursor);
				cursor = cursor.getNextReference();
			}
		}
		// TODO go through other lists as observed and so on...
		return result;
	}
	
	// return a list which contains all dates depending on input date
	// where we only compare its year, month and day for equality
	// TODO use a priority queue instead of a linked list.
	public LinkedList<Event> getEventsOfDate(DateTime day, User requester){
		LinkedList<Event> result = new LinkedList<Event>();
		// 1. go here through heads
		for(Event head : this.eventHeads){
			//if(head.getStart().equals(day)) // adde hier check
			if(checkHappensOn(head, day))
				if(head.getVisibility() != Visibility.PRIVATE 
						|| owner == requester) result.add(head);
			
			Event cursor = head;
			while(cursor.hasNext()){
				//if(cursor.getStart().equals(day))
				if(checkHappensOn(cursor, day))
					if(cursor.getVisibility() != Visibility.PRIVATE 
							|| owner == requester) result.add(cursor);
				cursor = cursor.getNextReference();
			}
		}
		// TODO go through other lists as observed and so on...
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
	public void generateNextEvents(Event head, DateTime baseDate){
		//DateTime currentDate = head.getStart();
		DateTime currentDate = baseDate;
		DateTime nextDate = currentDate.plusMonths(1);
		
		//Date nextDate = new Date(currentDate.getYear(),
		//		currentDate.getMonth()+1, currentDate.getDate(),
		//		currentDate.getHours(), currentDate.getMinutes());
		
		head.generateNextEvents(nextDate);
		
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
		Event victim = getEventById(id);
		
		// case a)
		if(victim instanceof PointEvent){
			this.eventHeads.remove(victim);
		}
		
		// case b)
		else if(victim instanceof IntervalEvent){
			// TODO statt casten in neue types, verwende neue konstruktoren!
			// TODO set new from and to
			// new interval structure:
			// [head ,previctim] | victim | [postVictim,victim.getTo()]
			// create at most two object of type IntervalEvent.
			
			Event head = this.getHeadById(victim.getBaseId());
			Event preVictim = victim.getPreviousReference();
			Event postVictim = victim.getNextReference();
			
			// wenn intervall der form: [head,victim], d.h. 2. elemente
			if(preVictim == head && postVictim == null){
				//head = (PointEvent) head;
				head.setNext(null);
				head = new PointEvent((IntervalEvent) head);
				
			// victim == head
			}else if(victim == head){
				postVictim.setPrevious(null);
				this.eventHeads.remove(victim);
				this.addEvent(postVictim);
				Event cursor = postVictim;
				postVictim.setBaseId(postVictim.getId());
				while(cursor.hasNext()){
					cursor = cursor.getNextReference();
					cursor.setBaseId(postVictim.getId());
				}
			// if victim is a leaf, i.e. victim is the last element of the list.
			}else if(victim.getNextReference() == null){
				preVictim.setNext(null);
				victim.setPrevious(null);
				
			}else{
				preVictim.setNext(null);
				postVictim.setPrevious(null);
				this.addEvent(postVictim);
				
				// set for all postvictims events their new baseId
				Event cursor = postVictim;
				postVictim.setBaseId(postVictim.getId());
				while(cursor.hasNext()){
					cursor = cursor.getNextReference();
					cursor.setBaseId(postVictim.getId());
				}
			}
		}
		
		// case c)
		else if(victim instanceof RepeatingEvent){
			// possible resulting interval structures after deletion
			// [head, previctim] | victim | [postVictim, +infinite]
			
		}
	}
	
	
	/*
	 *  modifier
	 */
	
	// call here the corresponding event.edit methods
	// care about type changes if we change from a PointEvent to an RepeatingEvent.
	// at the moment we only can change an PointEvent to an RepeatingEvent
	// or keep a PointEvent a PointEvent, keep a RepeatingEvent...
	// TODO care about other cases!
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
		// 2. remove next reference
		// 3. remove victimHead from head list
		
		long baseId = member.getBaseId();
		Event victimHead = getHeadById(baseId);
		victimHead.setNext(null);
		this.removeEvent(baseId);
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
		// transform,care about references
		
		Event newHead = new IntervalEvent(victimHead.getStart(),cancelFromThis.getStart(), (RepeatingEvent)victimHead);
		Event cursor = victimHead;
		Event item = null;
		while(cursor.hasNext()){
			cursor.setPrevious(item);
			item = new IntervalEvent(victimHead.getStart(), cancelFromThis.getStart(), (RepeatingEvent)cursor);
			cursor.setNext(item);
			item.setPrevious(cursor);
			cursor = cursor.getNextReference();
		}
		
		this.removeEvent(baseId);
		this.addEvent(newHead);
		
		
	}
	
	// checker
	
	// TODO add method description
	// TODO add other lists, which we have to check
	public boolean hasEventOnDate(int day, int month, int year, User requester){
		
		LocalDate compareDate = null;
		try {
			compareDate = new LocalDate(year, month, day);
		} catch (Exception e) {}
		
		for(Event event : this.eventHeads){
			Event cursor = event;
			do{
				//if(cursor.getStart().toLocalDate().compareTo(compareDate) == 0)
				if(checkHappensOn(cursor.getStart().toLocalDate(), cursor.getEnd().toLocalDate(), compareDate)) 
					if(requester == owner || cursor.getVisibility() != Visibility.PRIVATE) return true;
				
				cursor = event.getNextReference();
			}while(event.hasNext());
		}
		return false;
	}
	
	// TODO add method description
	// TODO add other lists, which we have to check
	public boolean hasEventOnDate(DateTime date, User requester){
		for(Event event : this.eventHeads){
			Event cursor = event;
			do{
				//if(cursor.getStart().compareTo(date) == 0) 
				if(checkHappensOn(cursor, date))
					if(requester == owner || cursor.getVisibility() != Visibility.PRIVATE) return true;
				
				cursor = event.getNextReference();
			}while(event.hasNext());
		}
		return false;
	}
	
	/*
	 * private helpers
	 * this methods we can only call from this class
	 * they are declared as private (never public or even protected) 
	 */
	
	private boolean checkHappensOn(Event event, DateTime compareDate){
		return (event.getStart().equals(compareDate) 
					|| event.getEnd().equals(compareDate) 
					|| (event.getStart().isBefore(compareDate) && event.getEnd().isAfter(compareDate)));
	}
	
	private boolean checkHappensOn(LocalDate timeStart, LocalDate timeEnd, LocalDate compareDate){
		return (timeStart.equals(compareDate) 
					|| timeEnd.equals(compareDate) 
					|| (timeStart.isBefore(compareDate) && timeEnd.isAfter(compareDate)));
	}
}
