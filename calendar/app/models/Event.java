package models;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

// TODO find in an efficient and correct way (without any side-effects) the last event of a series of events.
// TODO improve some performance issues.
// TODO add birthday stuff and observed stuff - shouldn't be that hard 
//		but think about a global date structure for all the "to be rendered" stuff..

/**
 * Event is an abstract class which provides some base methods and fields for concrete event classes.
 * we can identify an event as a double linked data structure with a reference to its next and to its previous event.
 * An event is either one of a series of events (types: IntervalEvent, RepeatingEvent)
 * or a lone point (type: PointEvent) event. an event can be a head or a successor of a head.
 * a head is similar to an root - i.e. the 1st element of a series of events. 
 * if an event is of type PointEvent, then this event has no next and previous references
 * each event has an unique id and a baseId. the baseId indicates the id of its head. 
 * An event has a name, an end date, an start date, and a visibility
 * Remark: We assume: if an event.next == null, then event is a leaf 
 * and if event.previous == null, then event is a root so care about references.
 * @author team1
 *
 */
public abstract class Event implements Comparable<Event>{
	
	/**
	 * Provides three layers of visibility to control the privacy of Events.
	 */
	public enum Visibility {
		/**
		 * All Users are allowed to see this Event.
		 */
		PUBLIC,
		/**
		 * All Users are allowed to see this Events start and end date, but nothing more.
		 */
		BUSY,
		/**
		 * Only the User who created this Event is allowed to see it.
		 */
		PRIVATE
	}
	
	private Calendar calendar;
	private List<User> attendingUsers;
	protected Event next;
	protected Event previous;
	
	// last element of a series of linked events - remember only an event in the head list 
	// has a reference to a leaf which is not null (due performance issues) 
	private Event leaf = null; 
	private DateTime start;
	private DateTime end;
	private String name;
	private String description;
	private static long counter;
	private long id;
	private long baseId;
	private Visibility visibility;
	private boolean isOpen;
	
	/**
	 * 
	 * @param start
	 *            the starting Date
	 * @param end
	 *            the ending Date
	 * @param name
	 *            name and description of Event
	 * @param visibility
	 *            flag, determines visibility for other users
	 * @param isRepeated
	 *            flag, used for repeating Events
	 * @param intervall
	 *            determines repetition interval. Possibilities: DAY (1),
	 *            WEEK(7), MONTH(30), YEAR(365)
	 */
	public Event(String name, DateTime start, DateTime end, Visibility visibility, Calendar calendar) {
		this.name = name;
		this.start = start;
		this.end = end;
		this.visibility = visibility;
		this.calendar = calendar;
		this.attendingUsers = new ArrayList<User>();
		this.id = counter;
		counter++;	
	}
	
	/*
	 * getters
	 */
	
	/**
	 * Get the name of this Event.
	 * @return The <code>name</code> of this Event.
	 */
	public String getName(){
		return this.name;
	}
	
	public String getDescription(){
		return this.description;
	}
	
	/**
	 * Get start date of Event.
	 * @return The <code>start</code> date of this Event.
	 */
	public DateTime getStart(){
		return this.start;
	}
	
	/**
	 * Get end date of Event.
	 * @return The  <code>end</code> date of this Event.
	 */
	public DateTime getEnd(){
		return this.end;
	}
	
	/**
	 * Get the unique id of this Event.
	 * @return The <code>id</code> of this Event.
	 */
	public long getId(){
		return this.id;
	}
	
	/**
	 * Get the baseId of this Event.
	 * @return The baseId of this Event.
	 */
	public long getBaseId(){
		return this.baseId;
	}
	
	public long getCalendarId() {
		return this.calendar.getId();
	}
	/**
	 * Get the visibility status of this Event.
	 * @return The visibility of this Event.
	 * @see�{@link Visibility}
	 */
	public Visibility getVisibility() {
		return this.visibility;
	}
	
	public User getOwner() {
		return this.calendar.getOwner();
	}
	
	// returns the reference to its next event, if exists, otherwise null;
	// if null is returned it means of of these things:
	// event is a leaf, i.e. either we have an RepeatingEvent and for those, we only generate 
	// as many successors as we need (till current month)
	// or we have an IntervalEvent which holds the same expect, there is a bound.
	// event is a PointEvent in head list. those types of events don't have any successors 
	public Event getNextReference(){
		//assert (this.next != null);
		return this.next;
	}
	
	// returns the reference to its previous event, if exists, otherwise null;
	// if null is returned, it means, that this event is an head event, since heads have no root
	public Event getPreviousReference(){
		return this.previous;
	}
	
	public Event getLeaf(){
		return this.leaf;
	}
	
	/**
	 * Get a String representation of a given date.
	 * 
	 * Returns a String representation in the form "dd/MM/yyyy, HH:mm".
	 * @param date The date to be parsed.
	 * @return String representation of argument.
	 */
	public String getParsedDate(DateTime date) {
		return date.toString("dd/MM/yyyy, HH:mm");
	}
	
	public String getParsedStartDate(){
		return this.getStart().toString("dd/MM/yyyy, HH:mm");
	}
	
	public String getParsedEndDate(){
		return this.getEnd().toString("dd/MM/yyyy, HH:mm");
	}
	
	public Calendar getCalendar(){
		return this.calendar;
	}
	
	public String getAttendingUsers() {
		StringBuffer sb = new StringBuffer();
		for (User user : this.attendingUsers) {
			sb.append(user.getName());
			sb.append(", ");
		}
		if (sb.length() > 0) {
			sb.setLength(sb.length()-2);
		}
		return sb.toString();
	}
	
	// depending on what kind of event we are
	// generate next events for a head if allowed.
	// summary: 
	// PointEvent: has no next event
	// IntervalEvent: if this event has already a pointer to an "nextEvent" then call getNextEvent on our nextEvent
	public abstract void generateNextEvents(DateTime currentDate);
	
	/*
	 * setters 
	 */
	
	public abstract void setNext(Event event);
	public abstract void setPrevious(Event event);
	
	public void setLeaf(Event newLeaf){
		if(this.baseId == this.id) this.leaf = newLeaf;
	}
	
	public void setStart(DateTime start){
		this.start = start;
	}
	
	public void setEnd(DateTime end){
		this.end = end;
	}
	
	/**
	 * Set the baseId
	 * @param id The baseId to be set.
	 */
	public void setBaseId(long baseId){
		this.baseId = baseId;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setVisiblility(Visibility visibility){
		this.visibility = visibility;
	}
	
	public void setOpen() {
		this.isOpen = true;
	}
	
	/**
	 * Edit the description of this Event.
	 * @param text The new description to be set.
	 */
	public void editDescription(String text){
		this.description = text;
	}
	
	public void edit(String name, DateTime start, DateTime end, Visibility visibility){
		this.setStart(start);
		this.setEnd(end);
		this.setName(name);
		this.setVisiblility(visibility);	
	}
	
	/**
	 * WARNING! Use this method only for special constructors
	 * @param id
	 */
	public void forceSetId(long id){
		this.id = id;
	}
	
	public abstract void remove();
	
	/*
	 * checks
	 */
	
	public boolean hasNext(){
		return this.next != null;
	}
	
	public boolean hasPrevious(){
		return this.previous != null;
	}
	
	/**
	 * Check if this Event is visible.
	 * @return <code>true</code> if the visibility status of this Event is either PUBLIC or BUSY.
	 * <code>false</code> if the visibility status if PRIVATE.
	 */
	public boolean isVisible() {
		return this.visibility != Visibility.PRIVATE;
	}
	
	public boolean isBusy(){
		return this.visibility == Visibility.BUSY;
	}
	
	public boolean isPublic(){
		return this.visibility == Visibility.PUBLIC;
	}
	
	public boolean isPrivate(){
		return this.visibility == Visibility.PRIVATE;
	}
	
	public boolean userIsAttending(String name) {
		User user = Database.getUserByName(name);
		return this.attendingUsers.contains(user);
	}
	
	public boolean isRepeating() {
		return (this instanceof RepeatingEvent); 
	}
	
	public boolean isOpen() {
		return this.isOpen;
	}
	
	/*
	 * helpers
	 */
	
	public String toString() {
		return this.name;
	}

	public void addUserToAttending(User user) {
		this.attendingUsers.add(user);
		
	}

	public void removeUserFromAttending(User user) {
		this.attendingUsers.remove(user);
	}
	
	// too ugly for this new design - how can we drop this without trashing our view?
	public int getPreviousIntervalValue(){
		if(this instanceof RepeatingEvent) return ((RepeatingEvent)this).getInterval();
		return 0;
	}

}
