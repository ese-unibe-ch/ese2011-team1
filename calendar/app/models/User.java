package models;

import java.util.LinkedList;

import org.joda.time.DateTime;

import enums.Interval;
import enums.Visibility;

/**
 * The User class represents a User of this Calendar application. Users may have
 * multiple Calendars, all of which can contain multiple Events. Users are
 * responsible for maintaining the calendar.
 * 
 * @see {@link Calendar}
 * 
 */
public class User {
	private String name;
	private LinkedList<Calendar> calendars;

	// in this list we store all calendars of other user which we want to
	// display in our calendar
	private LinkedList<Calendar> observedCalendars;
	private LinkedList<Long> shownObservedCalendars;
	private String password;
	private RepeatingEvent birthday;
	// public boolean isPublicBirthday;
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
	 * @see {@link User}
	 */
	public User(String name, String password, DateTime birthDate,
			String nickname) {
		// preconditions
		assert name != null : "Parameter not allowed to be null";
		assert name.isEmpty() == false : "Empty name, User must have a name";

		calendars = new LinkedList<Calendar>();
		observedCalendars = new LinkedList<Calendar>();
		shownObservedCalendars = new LinkedList<Long>();

		this.name = name;
		this.nickname = nickname;
		this.password = password;
		counter++;
		this.id = counter;
		initializeBirthday(birthDate);

		// each user x has a default a calender called: x's first calendar
		calendars.add(new Calendar(name + "'s first calendar", this));

		// postconditions
		assert this.name.equals(name);
		assert calendars != null;
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
		this.birthday = new RepeatingEvent("birthday", birthdayStart,
				birthdayEnd, Visibility.PRIVATE, birthdayCalendar,
				Interval.YEARLY);
		this.birthdayCalendar.addEvent(birthday);
		this.birthday.generateNextEvents(birthDate);
		observedCalendars.add(birthdayCalendar);
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
		birthday.edit(birthday.getName(), birthday.getStart(),
				birthday.getEnd(), visibility, birthday.getInterval());
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
		for (Calendar cal : calendars) {
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
		System.out.println("CalendarId to be deleted: " + calendarId);
		for (Calendar cal : calendars) {
			System.out.println("CalendarId checked: " + cal.getId());
			if (cal.getId() == calendarId) {
				this.calendars.remove(cal);
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
	 * 
	 */
	public String toString() {
		return this.name;
	}

}
