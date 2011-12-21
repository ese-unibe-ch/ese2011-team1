package models;

// subscriber to a messagesystem.
import java.util.LinkedList;

import org.joda.time.DateTime;

import enums.Interval;
import enums.Visibility;

/**
 * The User class represents a User of this Calendar application. Users may have
 * multiple Calendars, all of which can contain multiple Events. Users are
 * responsible for maintaining the calendar. a user can observe calendars of
 * other users. a user observes a message system which notifies him if there is
 * a new message for him. A user stores all messages which he gets from the
 * message system in
 * 
 * @see {@link Calendar}
 * 
 */
public class User {
	private String name;
	private LinkedList<Calendar> calendars;
	private LinkedList<Calendar> observedCalendars;
	private LinkedList<Long> shownObservedCalendars;
	private LinkedList<Object[]> eventsToAccept;
	private String password;
	private RepeatingEvent birthday;
	private MessageSystem messageSystem;
	private long id;
	private String nickname;
	private Calendar birthdayCalendar;
	private String privateEmailAddress;
	private boolean isPrivateEmailVisible;
	private String businessEmailAdress;
	private boolean isBusinessEmailVisible;
	private String privatePhoneNumber;
	private boolean isPrivatePhoneNumberVisible;
	private String businessPhoneNumber;
	private boolean isBusinessPhoneNumberVisible;
	private String description;
	private boolean isDescriptionVisible;
	private static long counter;
	private DateTime lastLogin;
	private boolean notified;

	/**
	 * Create a new User for this calendar application.
	 * 
	 * @param name
	 *            The Users name.
	 * @param password
	 *            The Users password.
	 * @param birthDate
	 *            The Users birthday.
	 * @param nickname
	 *            The Users nickname.
	 * @param messageSystem
	 *            The message system.
	 * @see {@link User}
	 */
	public User(String name, String password, DateTime birthDate,
			String nickname, MessageSystem messageSystem) {

		calendars = new LinkedList<Calendar>();
		observedCalendars = new LinkedList<Calendar>();
		shownObservedCalendars = new LinkedList<Long>();
		eventsToAccept = new LinkedList<Object[]>();

		this.name = name;
		this.nickname = nickname;
		this.password = password;
		counter++;
		this.id = counter;
		initializeBirthday(birthDate);

		// each user x has a default a calender called: x's first calendar
		calendars.add(new Calendar("Personal", this));
		this.messageSystem = messageSystem;
		this.messageSystem.subscribe(this);
	}

	/**
	 * Initializes the birthday calendar and event for this user.
	 * 
	 * Sets the correct start and end time of the birthday and adds it to the
	 * birthdayCalendar
	 * 
	 * @param birthDate
	 *            The date of this birthday.
	 */
	private void initializeBirthday(DateTime birthDate) {
		this.birthdayCalendar = new Calendar("Birthdays", this);
		DateTime birthdayStart = birthDate.withHourOfDay(0).withMinuteOfHour(0);
		DateTime birthdayEnd = birthDate.withHourOfDay(23).withMinuteOfHour(59);
		this.birthday = new RepeatingEvent(this.name + "'s birthday",
				birthdayStart, birthdayEnd, Visibility.PRIVATE,
				birthdayCalendar, Interval.YEARLY);
		this.birthday.setOriginId(birthday.getId());
		this.birthdayCalendar.addEvent(birthday);
		this.birthday.generateNextEvents(birthDate.plusYears(1));
		observedCalendars.add(birthdayCalendar);
	}

	/**
	 * Returns if the user has been notified
	 */
	public boolean isNotified() {
		return notified;
	}

	/**
	 * Sets the login Date.
	 */
	public void setNotified(boolean b) {
		this.notified = b;
	}

	/**
	 * Gets the last login Date.
	 */
	public DateTime getLastLogin() {
		return lastLogin;
	}

	/**
	 * Sets the login Date.
	 */
	public void setLastLogin(DateTime lastL) {
		this.lastLogin = lastL;
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
	public void setBirthdayDate(DateTime birthdayDate) {
		DateTime newBirthdayStart = birthday.getStart().withDate(
				birthdayDate.getYear(), birthdayDate.getMonthOfYear(),
				birthdayDate.getDayOfMonth());
		DateTime newBirthdayEnd = birthday.getEnd().withDate(
				birthdayDate.getYear(), birthdayDate.getMonthOfYear(),
				birthdayDate.getDayOfMonth());
		birthday.edit(birthday.getName(), newBirthdayStart, newBirthdayEnd,
				birthday.getVisibility(), birthday.getInterval());
	}

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
	public void setBirthdayPublic(boolean is_visible) {
		Visibility visibility = is_visible ? Visibility.PUBLIC
				: Visibility.PRIVATE;
		RepeatingEvent birthday = this.birthday;
		birthday.edit(birthday.getName(), birthday.getStart(),
				birthday.getEnd(), visibility, birthday.getInterval());
		while (birthday.hasNext()) {
			birthday = (RepeatingEvent) birthday.getNextReference();
			birthday.edit(birthday.getName(), birthday.getStart(),
					birthday.getEnd(), visibility, birthday.getInterval());
		}
	}

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
		return this.calendars;
	}

	/**
	 * Get this Users default calendar.
	 * 
	 * @return The first Calendar of this User.
	 */
	public Calendar getdefaultCalendar() {
		return this.calendars.getFirst();
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
		for (Calendar cal : this.calendars) {
			if (cal.getId() == calId)
				result = cal;
		}
		return result;
	}

	public long getId() {
		return this.id;
	}

	/**
	 * Add a new Calendar to this Users list of Calendars.
	 * 
	 * @param cal
	 *            The Calendar to be added to <code>calendars</code> .
	 */
	public void addCalendar(Calendar cal) {
		calendars.add(cal);
	}

	/**
	 * Add a new Calendar of another user to this Users observed calendar list.
	 * 
	 * @param cal
	 *            The Calendar to be added to <code>observedCalendars</code>.
	 */
	public void addObservedCalendar(Calendar cal) {
		observedCalendars.add(cal);
		Event birthday = cal.getOwner().getBirthday();
		birthdayCalendar.addEvent(birthday);
	}

	/**
	 * Remove a Calendar of another user from this Users observed Calendar list.
	 * 
	 * @param cal
	 *            The Calendar to be removed to <code>observedCalendars</code>.
	 */
	public void removeObservedCalendar(Calendar cal) {
		observedCalendars.remove(cal);
		Event birthday = cal.getOwner().getBirthday();
		birthdayCalendar.removeSeriesOfRepeatingEvents(birthday);
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
	 * Adds a calendar to the user's list.
	 * 
	 * @param name
	 *            The name of the calendar to be added
	 * @param owner
	 *            The owner of the calendar to be added
	 */
	public void newCalendar(String name, User owner) {
		Calendar calendar = new Calendar(name, owner);
		this.calendars.add(calendar);
	}

	/**
	 * Removes a calendar from the user's list.
	 * 
	 * @param name
	 *            The name of the calendar to be added
	 * @param owner
	 *            The owner of the calendar to be added
	 */
	public void deleteCalendar(long calendarId) {
		for (Calendar cal : calendars) {
			if (cal.getId() == calendarId) {
				this.calendars.remove(cal);

				// use here instead an observer pattern
				for (User user : Database.getUserList()) {
					for (Calendar calendar : user.getObservedCalendars()) {
						if (calendar == cal) {
							user.removeObservedCalendar(calendar);
							break;
						}
					}
				}
				// end ugly null pointer fix
				break;
			}
		}
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
		return (this.birthday.getId() == event.getBaseId());
	}

	/**
	 * get the Private Private Email of this user.
	 * 
	 * @return returns Private Email of this user.
	 */
	public String getPrivateEmailAddress() {
		return this.privateEmailAddress;
	}

	/**
	 * checks if the Private Email for this user is visible
	 * 
	 * @return returns if Private Email of this user is visible
	 */
	public boolean getIsPrivateEmailVisible() {
		return this.isPrivateEmailVisible;
	}

	/**
	 * get the Private Business Email of this user.
	 * 
	 * @return returns Business Email of this user.
	 */
	public String getBusinessEmailAdress() {
		return this.businessEmailAdress;
	}

	/**
	 * checks if the Business Email for this user is visible
	 * 
	 * @return returns if Business Email of this user is visible
	 */
	public boolean getIsBusinessEmailVisible() {
		return this.isBusinessEmailVisible;
	}

	/**
	 * get the Private Phone Number of this user.
	 * 
	 * @return returns Private Phone Number of this user.
	 */
	public String getPrivatePhoneNumber() {
		return this.privatePhoneNumber;
	}

	/**
	 * checks if the Private Phone Number for this user is visible
	 * 
	 * @return returns if Private Phone Number of this user is visible
	 */
	public boolean getIsPrivatePhoneNumberVisible() {
		return this.isPrivatePhoneNumberVisible;
	}

	/**
	 * get the Business Phone Number of this user.
	 * 
	 * @return returns Business Phone Number of this user.
	 */
	public String getBusinessPhoneNumber() {
		return this.businessPhoneNumber;
	}

	/**
	 * checks if the Business Phone Number for this user is visible
	 * 
	 * @return returns if Business Phone Number of this user is visible
	 */
	public boolean getIsBusinessPhoneNumberVisible() {
		return this.isBusinessPhoneNumberVisible;
	}

	/**
	 * get the description of this user.
	 * 
	 * @return returns description of this user.
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * checks if the description for this user is visible
	 * 
	 * @return returns if description of this user is visible
	 */
	public boolean getIsDescriptionVisible() {
		return this.isDescriptionVisible;
	}

	/**
	 * get invitation list of this user
	 * 
	 * @return
	 */
	public LinkedList getEventsToAccept() {
		return this.eventsToAccept;
	}

	/**
	 * sets Private Email for this user.
	 * 
	 * @param privateEmailAddress
	 *            new Private Email.
	 */
	public void setPrivateEmailAddress(String privateEmailAddress) {
		this.privateEmailAddress = privateEmailAddress;
	}

	/**
	 * sets if Private Email for user is visible.
	 * 
	 * @param isPrivateEmailVisible
	 *            visibility for user's PrivateEmail
	 */
	public void setIsPrivateEmailVisible(boolean isPrivateEmailVisible) {
		this.isPrivateEmailVisible = isPrivateEmailVisible;
	}

	/**
	 * sets Business Email for this user.
	 * 
	 * @param businessEmailAdress
	 *            new Business Email.
	 */
	public void setBusinessEmailAdress(String businessEmailAdress) {
		this.businessEmailAdress = businessEmailAdress;

	}

	/**
	 * sets if Business Email for user is visible.
	 * 
	 * @param isBusinessEmailVisible
	 *            visibility for user's Business Email
	 */
	public void setIsBusinessEmailVisible(boolean isBusinessEmailVisible) {
		this.isBusinessEmailVisible = isBusinessEmailVisible;
	}

	/**
	 * sets Private Phone Number for this user.
	 * 
	 * @param privatePhoneNumber
	 *            new Private Phone Number.
	 */
	public void setPrivatePhoneNumber(String privatePhoneNumber) {
		this.privatePhoneNumber = privatePhoneNumber;
	}

	/**
	 * sets if Private Phone Number for user is visible.
	 * 
	 * @param isPrivatePhoneNumberVisible
	 *            visibility for user's Private Phone Number
	 */
	public void setIsPrivatePhoneNumberVisible(
			boolean isPrivatePhoneNumberVisible) {
		this.isPrivatePhoneNumberVisible = isPrivatePhoneNumberVisible;
	}

	/**
	 * sets business phone number for this user.
	 * 
	 * @param businessPhoneNumber
	 *            new business phone number.
	 */
	public void setbusinessPhoneNumber(String businessPhoneNumber) {
		this.businessPhoneNumber = businessPhoneNumber;
	}

	/**
	 * sets if business phone number for user is visible.
	 * 
	 * @param isBusinessPhoneNumberVisible
	 *            visibility for user's BusinessPhoneNumber
	 */
	public void setIsBusinessPhoneNumberVisible(
			boolean isBusinessPhoneNumberVisible) {
		this.isBusinessPhoneNumberVisible = isBusinessPhoneNumberVisible;
	}

	/**
	 * sets description for this user.
	 * 
	 * @param description
	 *            new description.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * sets if description for user is visible.
	 * 
	 * @param isDescriptionVisible
	 *            visibility for user's description
	 */
	public void setIsDescriptionVisible(boolean isDescriptionVisible) {
		this.isDescriptionVisible = isDescriptionVisible;
	}

	/**
	 * returns the name of this user.
	 */
	public String toString() {
		return this.name;
	}

	/**
	 * this user wants to send a message to user with with given id userId use
	 * this method in event
	 */
	public void sendMessage(long targetUserId, long fromUserId,
			long calendarId, long eventId, String message) {
		this.messageSystem.notifyObservingUser(targetUserId, fromUserId,
				calendarId, eventId, message);
	}

	/**
	 * Receive message from message system and store the message (i.e. a
	 * quartet, consisting of userId, calendarId, eventId, message) in
	 * eventsToAccept.
	 */
	public void receiveMessage(long userId, long calendarId, long eventId,
			String message) {
		Object[] quartet = { userId, calendarId, eventId, message };
		this.eventsToAccept.add(quartet);
	}

	/**
	 * this user accept invitation to the event which belongs to given user with
	 * user id, calendar with calendar id, event with event id from this user's
	 * accepting list.
	 * 
	 * @param userId
	 *            id of user which owns the event we are invited
	 * @param calendarId
	 *            calendar id to which the event we are invited belongs to
	 * @param eventId
	 *            id of event we are invited to
	 */
	public void acceptInvitation(long userId, long calendarId, long eventId) {
		getEventByUserCalendarEventId(userId, calendarId, eventId)
				.addUserToAttending(this);
		this.removeInvitation(userId, calendarId, eventId);
		User user = Database.getUserById(userId);
		Calendar cal = user.getCalendarById(calendarId);
		if(!this.getObservedCalendars().contains(cal))
			this.addObservedCalendar(cal);
	}

	/**
	 * removes this user from invitation list, i.e. this user declined offered
	 * invitation to the event which belongs to given user with user id,
	 * calendar with calendar id, event with event id from this user's accepting
	 * list.
	 * 
	 * @param userId
	 *            id of user which owns the event we are invited
	 * @param calendarId
	 *            calendar id to which the event we are invited belongs to
	 * @param eventId
	 *            id of event we are invited to
	 */
	public void declineInvitation(long userId, long calendarId, long eventId) {
		getEventByUserCalendarEventId(userId, calendarId, eventId)
				.removeUserFromPendingAttending(this);
		this.removeInvitation(userId, calendarId, eventId);

	}

	/**
	 * removes invitation to the event which belongs to given user with user id,
	 * calendar with calendar id, event with event id from this user's accepting
	 * list.
	 * 
	 * @param userId
	 *            id of user which owns the event we are invited
	 * @param calendarId
	 *            calendar id to which the event we are invited belongs to
	 * @param eventId
	 *            id of event we are invited to
	 */
	public void removeInvitation(long userId, long calendarId, long eventId) {

		for (Object[] invitation : this.eventsToAccept) {
			long compareUserId = (Long) invitation[0];
			long compareCalendarId = (Long) invitation[1];
			long compareEventId = (Long) invitation[2];
			if (compareUserId == userId && compareCalendarId == calendarId
					&& compareEventId == eventId) {
				this.eventsToAccept.remove(invitation);
				return;
			}
		}
	}

	/**
	 * private helper method get the event which corresponds to the given user-,
	 * calendar-and event id
	 * 
	 * @param userId
	 *            id of user which owns the event we are invited
	 * @param calendarId
	 *            calendar id to which the event we are invited belongs to
	 * @param eventId
	 *            id of event we are invited to
	 * @return returns the event for which we are looking for.
	 */
	private Event getEventByUserCalendarEventId(long userId, long calendarId,
			long eventId) {
		User user = Database.getUserById(userId);
		Calendar calendar = user.getCalendarById(calendarId);
		Event event = calendar.getEventById(eventId);
		return event;
	}

	/**
	 * checks, if this user already has an invitation to corresponding event
	 * which corresponds to the given user-, calendar-and event id
	 * 
	 * @param userId
	 *            id of user which owns the event we are invited
	 * @param calendarId
	 *            calendar id to which the event we are invited belongs to
	 * @param eventId
	 *            id of event we are invited to
	 * @return has this user an invitation to corresponding event?
	 */
	public boolean hasSuchInvitation(long userId, long calendarId, long eventId) {
		for (Object[] invitation : this.eventsToAccept) {
			long compareUserId = (Long) invitation[0];
			long compareCalendarId = (Long) invitation[1];
			long compareEventId = (Long) invitation[2];
			if (compareUserId == userId && compareCalendarId == calendarId
					&& compareEventId == eventId)
				return true;
		}
		return false;
	}

}
