package models;

import java.util.LinkedList;

import models.Event.Visibility;

import org.joda.time.DateTime;

/**
 * The User class represents a User of this Calendar application. Users may have
 * multiple Calendars, all of which can contain multiple Events. Users are
 * responsible for maintaining the calendar.
 * 
 * @see {@link Calendar}
 * 
 */
public class User {
	public String name;
	public LinkedList<Calendar> calendar;

	// in this list we store all calendars of other user which we want to
	// display in our calendar
	public LinkedList<Calendar> observedCalendars;
	public LinkedList<Long> shownObservedCalendars;
	public String password;
	public Event birthday;
	// public boolean isPublicBirthday;
	public long id;
	private String nickname;
	private Calendar birthdayCalendar;
	private String emailP;
	private boolean emailPVis;
	private String emailB;
	private boolean emailBVis;
	private String telP;
	private boolean telPVis;
	private String telB;
	private boolean telBVis;
	private String notes;
	private boolean notesVis;
	private static long counter;

	/**
	 * Create a new User for this calendar application.
	 * 
	 * @param name
	 *            The Users name.
	 * @param password
	 *            The Users password.
	 * @param birthday
	 *            The Users birthday.
	 * @param nickname
	 *            The Users nickname.
	 * @see {@link User}
	 */
	public User(String name, String password, DateTime birthday, String nickname) {
		// preconditions
		assert name != null : "Parameter not allowed to be null";
		assert name.isEmpty() == false : "Empty name, User must have a name";

		calendar = new LinkedList<Calendar>();
		observedCalendars = new LinkedList<Calendar>();
		shownObservedCalendars = new LinkedList<Long>();
		birthdayCalendar = new Calendar("Birthdays", this);
		observedCalendars.add(birthdayCalendar);

		this.name = name;
		this.nickname = nickname;
		this.password = password;
//		this.birthday = new Event(this, birthday, birthday, name
//				+ "'s birthday", Visibility.PRIVATE, true, 365,
//				this.birthdayCalendar.getId(), false);
		this.birthday = new PointEvent("birthday", birthday, birthday, Visibility.PRIVATE, birthdayCalendar);
		
		counter++;
		this.id = counter;

		birthdayCalendar.addEvent(this.birthday);

		// each user x has a default a calender called: x's first calendar
		calendar.add(new Calendar(name + "'s first calendar", this));

		// postconditions
		assert this.name.equals(name);
		assert calendar != null;
	}

	/**
	 * Get this Users password.
	 * 
	 * @return The <code>password</code> of this User.
	 */
	public String getPassword() {
		return this.password;
	}

	/**
	 * Set this Users password
	 * 
	 * @param password
	 *            The password to be set.
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Get this Users name.
	 * 
	 * @return The <code>name</code> of this User.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Set this Users name.
	 * 
	 * @param name
	 *            The name to be set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get this Users birthday.
	 * 
	 * @return The <code>birthday</code> of this User.
	 */
	public Event getBirthday() {
		return this.birthday;
	}

	/**
	 * Set this Users birthday date.
	 * 
	 * @param birthdayDate
	 *            The new date of the Users <code>birthday</code>.
	 */
//	public void setBirthdayDate(DateTime birthdayDate) {
//		birthday.edit(birthdayDate, birthdayDate, birthday.getName(),
//				birthday.getVisibility(), true, 365);
//	}

	/**
	 * Returns the visibility status of the Users birthday.
	 * 
	 * @return <code>true</code> if this Users birthday is either Public or
	 *         Busy. <code>false</code> otherwise.
	 * @see {@link Visibility}
	 */
	public boolean isBirthdayPublic() {
		return birthday.isVisible();
	}

	/**
	 * Set this Users birthday visible or hidden for other Users.
	 * 
	 * @param is_visible
	 *            If true, the birthday will be set to visible. If false, the
	 *            birthday will be set to private.
	 */
//	public void setBirthdayPublic(boolean is_visible) {
//		Visibility visibility = is_visible ? Visibility.PUBLIC
//				: Visibility.PRIVATE;
//		birthday.edit(birthday.start, birthday.end, birthday.name, visibility,
//				true, 365);
//	}

	/**
	 * Get this Users nickname.
	 * 
	 * @return The <code>nickname</code> of this User.
	 */
	public String getNickname() {
		return this.nickname;
	}

	/**
	 * Set this Users <code>nickname</code>.
	 * 
	 * @param nickname
	 *            The nickname to be set.
	 */
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	/**
	 * Return all calendars of a user
	 * 
	 * @return A list of all Calendars of this User.
	 * @see {@link Calendar}
	 */
	public LinkedList<Calendar> getCalendars() {
		return this.calendar;
	}

	/**
	 * Get this Users default calendar.
	 * 
	 * @return The first Calendar of this User.
	 */
	public Calendar getdefaultCalendar() {
		return this.calendar.getFirst();
	}

	/**
	 * Get a Calendar from this Users list of Calendars based on its Id.
	 * 
	 * Iterate over all Calendars in <code>calendars</code>. If a Calendar has
	 * the same id as the argument, it will be returned.
	 * 
	 * @param calId
	 *            The Id to compare this Users Calendars with.
	 * @return <code>null</code> if no Calendar in <code>calendars</code> has
	 *         the same id as the argument. A Calendar with the same id as the
	 *         argument otherwise.
	 */
	public Calendar getCalendarById(long calId) {
		Calendar result = null;
		for (Calendar cal : calendar) {
			if (cal.getId() == calId)
				result = cal;
		}
		return result;
	}

	/**
	 * Add a new Calendar to this Users list of Calendars.
	 * 
	 * @param cal
	 *            The Calendar to be added to <code>calendars</code> .
	 */
	public void addCalendar(Calendar cal) {
		calendar.add(cal);
	}

	/**
	 * Add a new Calendar of another user to this Users observed calendar list.
	 * 
	 * @param cal
	 *            The Calendar to be added to <code>observedCalendars</code>.
	 */
	public void addObservedCalendar(Calendar cal) {
		observedCalendars.add(cal);
	}

	/**
	 * Remove a Calendar of another user from this Users observed Calendar list.
	 * 
	 * @param cal
	 *            The Calendar to be removed to <code>observedCalendars</code>.
	 */
	public void removeObservedCalendar(Calendar cal) {
		observedCalendars.remove(cal);
	}

	/**
	 * Adds a new ID from a observed calendar to be shown.
	 * 
	 * @param calId
	 *            The id to be added to <code>shownObservedCalendars</code>.
	 */
	public void addShownObservedCalendar(long calId) {
		shownObservedCalendars.add(calId);
	}

	/**
	 * Remove an Id from <code>shownObservedCalendars</code>.
	 * 
	 * @param calId
	 *            The id to be removed.
	 */
	public void removeShownObservedCalendar(long calId) {
		shownObservedCalendars.remove(calId);
	}

	/**
	 * Check if a Calendar is observed by this User, based on the Calendars id.
	 * 
	 * @param calId
	 *            The id of the Calendar to test if it is observed.
	 * @return <code>true</code> if <code>observedCalendars</code> contains a
	 *         Calendar with the same id as the argument. <code>false</code>
	 *         otherwise.
	 */
	public boolean isCalendarObserved(long calId) {
		for (Calendar cal : observedCalendars) {
			if (cal.getId() == calId)
				return true;
		}
		return false;
	}

	/**
	 * Get all observed Calendars of this User.
	 * 
	 * @return list of all observed Calendars of this User.
	 */
	public LinkedList<Calendar> getObservedCalendars() {
		return observedCalendars;
	}

	/**
	 * Get a List containing all id's of those observed Calendars which are
	 * currently shown.
	 * 
	 * @return list containing all id's of shown observed Calendars.
	 */
	public LinkedList<Long> getShownObservedCalendars() {
		return shownObservedCalendars;
	}

	/**
	 * Get the <code>birthdayCalendar</code> of this User.
	 * 
	 * @return A Users birthdayCalendar.
	 */
	public Calendar getBirthdayCalendar() {
		return this.birthdayCalendar;
	}

	/**
	 * Test if a given Event is this Users birthday.
	 * 
	 * @param event
	 *            The event to be compared.
	 * @return <code>true</code> if the argument is equal to this Users
	 *         birthday. <code>false</code> otherwise.
	 */
	public boolean isBirthday(Event event) {
		return this.birthday.getId() == event.getBaseId();
	}

	public String getEmailP() {
		return emailP;
	}

	public boolean getEmailPVis() {
		return emailPVis;
	}

	public String getEmailB() {
		return emailB;
	}

	public boolean getEmailBVis() {
		return emailBVis;
	}

	public String getTelP() {
		return telP;
	}

	public boolean getTelPVis() {
		return telPVis;
	}

	public String getTelB() {
		return telB;
	}

	public boolean getTelBVis() {
		return telBVis;
	}

	public String getNotes() {
		return notes;
	}

	public boolean getNotesVis() {
		return notesVis;
	}

	public void setEmailP(String emailP) {
		this.emailP = emailP;
	}

	public void setEmailPVis(boolean isEmailPVisible) {
		this.emailPVis = isEmailPVisible;
	}

	public void setEmailB(String emailB) {
		this.emailB = emailB;

	}

	public void setEmailBVis(boolean isEmailBVisible) {
		this.emailBVis = isEmailBVisible;
	}

	public void setTelP(String telP) {
		this.telP = telP;
	}

	public void setTelPVis(boolean isTelPVisible) {
		this.telPVis = isTelPVisible;
	}

	public void setTelB(String telB) {
		this.telB = telB;
	}

	public void setTelBVis(boolean isTelBVisible) {
		this.telBVis = isTelBVisible;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public void setNotesVis(boolean isNoteVisible) {
		this.notesVis = isNoteVisible;
	}

}
