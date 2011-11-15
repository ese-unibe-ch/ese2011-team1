package models;

import java.util.ArrayList;
import java.util.List;

import org.h2.expression.Comparison;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import enums.Interval;
import enums.Visibility;

// TODO find in an efficient and correct way (without any side-effects) the last event of a series of events.
// TODO improve some performance issues.
// TODO add birthday stuff and observed stuff - shouldn't be that hard 
//		but think about a global date structure for all the "to be rendered" stuff..

/**
 * Event is an abstract class which provides some base methods and fields for concrete event classes.
 * we can identify an event as a double linked data structure with a reference to its next and to its previous event.
 * An event is either one of a series of events (types: IntervalEvent, RepeatingEvent)
 * or a lone point (type: PointEvent) event. an event can be a head or a successor of a head or an other event. 
 * we call this head-tail structure. a head is similar to an root - i.e. the 1st element of a series of events. 
 * A series of events has also a leaf, i.e. the last element.
 * if an event is of type PointEvent, then this event has no next and previous references
 * each event has an unique id, a baseId and a originId. 
 * id defines an unique identifier for each event 
 * the baseId indicates the id of its head. the originId indicates a set of correlated heads 
 * and is set only for head events. See (*) for further explanation.
 * An event has a name, an end date, an start date, and a visibility
 * Remark: We assume: if an event.next == null, then event is a leaf 
 * and if event.previous == null, then event is a root so care about references.
 * @author team1
 * 
 * (*) suppose we have a Repeating series of events [head,...,victim,...,inf]
 *     now we remove victim from this series and we get:
 *     [head,...,preVictim] and [postVictim,...,inf] both this series have same originId
 *     we need this id to delete correlated intervals as a whole, i.e. removeAll() functionality.
 */
public abstract class Event implements Comparable<Event>{

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
	private long originId = -1; // for deleting different depending event-series ==> for removeAll(), default = -1
	private Visibility visibility;
	private boolean isOpen;
	
	/**
	 * 
	 * @param start
	 *            the starting Date.
	 * @param end
	 *            the ending Date.
	 * @param name
	 *            name and description of Event.
	 * @param visibility
	 *            flag, determines visibility for other users.
	 * @param calendar
	 * 			  calendar the event belongs to.
	 */
	public Event(String name, DateTime start, DateTime end, Visibility visibility, Calendar calendar) {
		this.name = name;
		this.start = start;
		this.end = end;
		this.visibility = visibility;
		this.calendar = calendar;
		this.attendingUsers = new ArrayList<User>();
		counter++;
		this.id = counter;
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
	
	/**
	 * Get the description of this event.
	 * @return The <code>description</code> of this Event.
	 */
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
	
	/**
	 * Get the originId of this Event.
	 * @return The originId of this Event.
	 */
	public long getOriginId(){
		return this.originId;
	}
	
	/**
	 * Get the id of the corresponding calendar of this Event.
	 * @return this.calendar.getId().
	 */
	public long getCalendarId() {
		return this.calendar.getId();
	}
	
	/**
	 * Get the visibility status of this Event.
	 * @return The visibility of this Event.
	 * @see {@link Visibility}
	 */
	public Visibility getVisibility() {
		return this.visibility;
	}
	
	/**
	 * Get the owner of this event.
	 * @return this.calendar.getOwner()
	 */
	public User getOwner() {
		return this.calendar.getOwner();
	}
	
	
	/**
	 * returns the reference to its next event, if exists, otherwise null;
	 * if null is returned it means of of these things:
	 * event is a leaf, i.e. either we have an RepeatingEvent and for those, we only generate 
	 * as many successors as we need (till current month)
	 * or we have an IntervalEvent which holds the same expect, there is a bound.
	 * event is a PointEvent in head list. those types of events don't have any successors 
	 * @return this.next
	 */
	public Event getNextReference(){
		return this.next;
	}
	
	/**
	 * returns the reference to its previous event, if exists, otherwise null;
	 * if null is returned, it means, that this event is an head event, since heads have no root
	 * @return this.previous
	 */
	public Event getPreviousReference(){
		return this.previous;
	}
	
	/**
	 * returns the last element of a head-tail structure of this event, i.e. the leaf.
	 * @return this.leaf
	 */
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
	
	/**
	 * return type of an event as a string. defined in each event class separately.
	 * @return returns string representation of the type of an event
	 */
	public abstract String getType();
	
	/**
	 * get string representation for the start of this event 
	 * by the format dd/MM/yyyy, HH:mm
	 * @return formated start date
	 */
	public String getParsedStartDate(){
		return this.getStart().toString("dd/MM/yyyy, HH:mm");
	}
	
	/**
	 * get string representation for the end of this event 
	 * by the format dd/MM/yyyy, HH:mm
	 * @return formated end date
	 */
	public String getParsedEndDate(){
		return this.getEnd().toString("dd/MM/yyyy, HH:mm");
	}
	
	/**
	 * get the corresponding calendar of this event
	 * @return this.calendar
	 */
	public Calendar getCalendar(){
		return this.calendar;
	}
	
	/**
	 * get all attending users from the attending user list of this event
	 * @return returns users which are attending this event.
	 */
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
	
	/**
	 * 
	 * @param requester
	 * @return
	 */
	// TODO write javadocs
	public String getNameFor(User requester) {
		String visibleName = null;
		if (this.isPublic())
			visibleName = this.getName();
		if (this.isBusy())
			visibleName = "Busy";
		if (this.getOwner().equals(requester))
			visibleName = this.getName();
		return  visibleName;
	}
	
	/**
	 * 
	 * @param activeDate
	 * @param requester
	 * @return
	 */
	// TODO write javadocs
	public String getDatesFor(DateTime activeDate, User requester) {
		StringBuffer sb = new StringBuffer();
		LocalDate activeLocalDate = activeDate.toLocalDate();
		if (!start.toLocalDate().equals(activeLocalDate))
			sb.append(start.toString("dd/MM/yyyy") + " ");
		sb.append(start.toString("HH:mm") + " - ");
		if (!end.toLocalDate().equals(activeLocalDate))
			sb.append(end.toString("dd/MM/yyyy") + " ");
		sb.append(end.toString("HH:mm"));
		return sb.toString();
	}
	
	/**
	 * get the description of this event for a given user, depending on the visibility of this event.
	 * @param requester user which requests for the description of this event
	 * @return returns the description of this event if requester is allowed to see it
	 */
	public String getDescriptionFor(User requester) {
		String visibleDescription = null;
		if (this.isPublic() || this.getOwner().equals(requester))
			visibleDescription = this.getDescription();
		return visibleDescription;
	}
	
	// TODO remove this method soon.
	public String getRepetitionFor(User requester) {
		return null;
	}
	
	/**
	 * get the visibility of this event as a string for a given user
	 * @param requester user for which we are going to check the visibility of this event.
	 * @return a string representation of the visibility of this event if requester is the owner of this event.
	 */
	public String getVisibilityFor(User requester) {
		return requester == getOwner() ? this.visibility.toString() : null;
	}

	/**
	 * depending on what kind of event we are
	 * generate next events for a head if allowed.
	 * PointEvent: has no next event
	 * IntervalEvent: if this event has already a pointer to an "nextEvent" then call getNextEvent on our nextEvent
	 * this method is defined in each subclass of event separately.
	 */
	public abstract void generateNextEvents(DateTime currentDate);
	
	/*
	 * setters 
	 */
	
	/**
	 * sets the next event after this event for this event
	 * @param event new next event for this event
	 */
	public abstract void setNext(Event event);
	
	/**
	 * sets the previous event before this event for this event
	 * @param event new previous event for this event
	 */
	public abstract void setPrevious(Event event);
	
	/**
	 * sets the new leaf for this event.
	 * @param newLeaf new leaf for this event.
	 */
	public void setLeaf(Event newLeaf){
		if(this.baseId == this.id) this.leaf = newLeaf;
	}
	
	/**
	 * sets an start time for this event.
	 * @param start start date/time of this event
	 */
	public void setStart(DateTime start){
		this.start = start;
	}
	
	/**
	 * sets an end time for this event.
	 * @param end end date/time of this event
	 */
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
	
	/**
	 * Set the originId
	 * @param id for originId which is to be set.
	 */
	public void setOriginId(long originId){
		this.originId = originId;
	}
	
	/**
	 * Set name of an event
	 * @param name represented as a string.
	 */
	public void setName(String name){
		this.name = name;
	}
	
	/**
	 * sets the visibility state of this event
	 * @param visibility visibility state of this event
	 */
	public void setVisiblility(Visibility visibility){
		this.visibility = visibility;
	}
	
	/**
	 * set this event as open for attending users.
	 */
	public void setOpen() {
		this.isOpen = true;
	}
	
	/**
	 * close the event from a previously open (or already closed, doesn't matter) state .
	 */
	public void setClosed() {
		this.isOpen = false;
		this.attendingUsers = new ArrayList<User>();
		
	}
	
	/**
	 * Edit the description of this Event.
	 * @param text The new description to be set.
	 */
	public void editDescription(String text){
		this.description = text;
	}
	
	/**
	 * Edit this event.
	 * 
	 * @param name
	 *            new name for event
	 * @param start
	 *            new the starting Date
	 * @param end
	 *            new the ending Date
	 * @param visibility
	 *            flag, determines visibility for other users
	 * @param interval
	 *            determines repetition interval. Possibilities: NONE, DAY,
	 *            WEEK, MONTH, YEAR
	 *            @see{@link Interval}
	 *   
	 * @param from
	 * 			  new lower bound(for IntervalEvent)
	 * @param to
	 * 			  new upper bound(for IntervalEvent)
	 * 
	 * @param description
	 * 			 new description,note for event
	 */
	public abstract void edit(String name, DateTime start, DateTime end, 
			Visibility visibility, Interval interval, DateTime from, DateTime to, String description);
	
	/**
	 * WARNING! Use this method only for special constructors
	 * @param id
	 */
	public void forceSetId(long id){
		this.id = id;
	}
	
	/**
	 * Removes/deletes this event. 
	 */
	public abstract void remove();
	
	/**
	 * find an event in head-tails event structure starting from this event on which has an id equals input argument.
	 * this method implements a chain pattern for finding events.
	 * @param id id of event we are looking for
	 * @return this, if this event has id, equals id, 
	 * 		   a successor event of this, if successor has id equals id or 
	 *         null if there is no event in this head-tail event structure which has an id equals id
	 */
	public Event findEventById(long id){
		if(this.getId() == id) return this;
		else{
			if(this.hasNext()) return this.getNextReference().findEventById(id);
			else return null;
		}
	}
	
	/*
	 * checks
	 */
	
	/**
	 * @return if this event has a next reference
	 */
	public boolean hasNext(){
		return this.next != null;
	}
	
	/**
	 * @return if this event has a previous reference
	 */
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
	
	/**
	 * check if this event is declared as a busy event
	 * @return returns a boolean: is this event a busy event?
	 */
	public boolean isBusy(){
		return this.visibility == Visibility.BUSY;
	}
	
	/**
	 * check if this event is declared as a public event
	 * @return returns a boolean: is this event a public event?
	 */
	public boolean isPublic(){
		return this.visibility == Visibility.PUBLIC;
	}
	
	/**
	 * check if this event is declared as a private event
	 * @return returns a boolean: is this event a private event?
	 */
	public boolean isPrivate(){
		return this.visibility == Visibility.PRIVATE;
	}
	
	/**
	 * checks if the user correlated to the input argument name 
	 * is in the attending list of this event
	 * @param name name of user we are looking for.
	 * @return is a user with name like input argument in the 
	 *         attending user list of this event? 
	 */
	public boolean userIsAttending(String name) {
		User user = Database.getUserByName(name);
		return this.attendingUsers.contains(user);
	}
	
	/**
	 * check if this event has a run-time type of RepeatingEvent
	 * @return returns a boolean: is this event an RepeatingEvent event?
	 */
	public boolean isRepeating() {
		return (this instanceof RepeatingEvent); 
	}
	
	/**
	 * check if this event is declared as an open event
	 * @return returns a boolean: is this event an open event?
	 */
	public boolean isOpen() {
		return this.isOpen;
	}
	
	/**
	 * this method compares this event by a compare date 
	 * and checks if start equals compare date, end equals compare date or
	 * start < compare date and end > compare date
	 * @param compareDate is the date time object we are going to compare with this event's start and end.
	 * @return returns the result of above's comparison.
	 */
	public boolean happensOn(LocalDate compareDate) {
		return (this.getStart().toLocalDate().equals(compareDate) 
				|| this.getEnd().toLocalDate().equals(compareDate) 
				|| (this.getStart().toLocalDate().isBefore(compareDate)
						&& this.getEnd().toLocalDate().isAfter(compareDate)));
	}
	
	/**
	 * has this event an id equals id
	 * @param id id of an event
	 * @return returns true of id of this event is equals input argument.
	 */
	public boolean equalId(long id){
		return this.getId() == id;
	}
	
	/**
	 * has this event an base id equals baseId
	 * @param baseId base id of an event
	 * @return returns true of base id of this event is equals input argument.
	 */
	public boolean equalBaseId(long baseId){
		return this.getBaseId() == baseId;
	}
	
	/**
	 * has this event an origin id equals originId
	 * @param originId origin id of an event
	 * @return returns true of origin id of this event is equals input argument.
	 */
	public boolean equalOriginId(long originId){
		return this.getOriginId() == originId;
	}
	
	/*
	 * helpers
	 */
	
	/**
	 * this method returns the name of this event as a string.
	 * @return returns name of this event
	 */
	public String toString() {
		return this.name;
	}
	
	/**
	 * adds an user to the attending user list for this event.
	 * @param user user which we want to add to attendingUsers list.
	 */
	public void addUserToAttending(User user) {
		this.attendingUsers.add(user);
		
	}
	
	/**
	 * removes an user from the attending user list for this event.
	 * @param user user which we want to remove from attendingUsers list.
	 */
	public void removeUserFromAttending(User user) {
		this.attendingUsers.remove(user);
	}
	
	public void PrintThisAndTail(Event head){
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
	}

}
