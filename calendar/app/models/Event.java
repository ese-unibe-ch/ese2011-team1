package models;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

/**
 * An Event represents a happening with a defined start date and end date.
 * 
 * The Event class provides multiple options to satisfy the needs for
 * modification, repetition and privacy. Events can be stored in a
 * {@link Calendar} to provide a graphical representation or attributed to a
 * {@link User} directly. Events are Comparable by their start date. The Event
 * class is must know its start/end date and in case of repetition know its
 * repeating status and next repetition.
 * 
 * @see {@link java.lang.Comparable}
 */
public class Event implements Comparable<Event> {

	/**
	 * Provides three layers of visibility to control the privacy of Events.
	 * 
	 */
	public enum Visibility {
		/**
		 * All Users are allowed to see this Event.
		 */
		PUBLIC,
		/**
		 * All Users are allowed to see this Events start and end date, but
		 * nothing more.
		 */
		BUSY,
		/**
		 * Only the User who created this Event is allowed to see it.
		 */
		PRIVATE
	}

	public long id;
	public long baseId;
	public User owner;
	public DateTime start;
	public DateTime end;
	public String name;
	public String description;
	public Visibility visibility;
	public boolean is_repeating;
	public int intervall;
	private static long counter;
	public boolean isDirty = false;
	public boolean wasPreviouslyRepeating = false;

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
	 *            determines repetition intervall. Possibilities: DAY (1),
	 *            WEEK(7), MONTH(30), YEAR(265)
	 */
	public Event(User owner, DateTime start, DateTime end, String name,
			Visibility visibility, boolean is_repeating, int intervall) {
		this.owner = owner;
		this.start = start;
		this.end = end;
		this.name = name;
		this.visibility = visibility;
		counter++;
		this.id = counter;
		this.is_repeating = is_repeating;
		this.intervall = intervall;
		this.baseId = id;
	}

	/*
	 * Getters
	 */

	/**
	 * Get start date of Event.
	 * 
	 * @return The <code>start</code> date of this Event.
	 */
	public DateTime getStart() {
		return this.start;
	}

	/**
	 * Get end date of Event.
	 * 
	 * @return The <code>end</code> date of this Event.
	 */
	public DateTime getEnd() {
		return this.end;
	}

	/**
	 * Get the visibility status of this Event.
	 * 
	 * @return The visibility of this Event.
	 * @see {@link Visibility}
	 */
	public Visibility getVisibility() {
		return this.visibility;
	}

	/**
	 * Get the name of this Event.
	 * 
	 * @return The <code>name</code> of this Event.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Get the unique id of this Event.
	 * 
	 * @return The <code>id</code> of this Event.
	 */
	public long getId() {
		return this.id;
	}

	/**
	 * Get a String representation of a given date.
	 * 
	 * Returns a String representation in the form "dd/MM/yyyy, HH:mm".
	 * 
	 * @param date
	 *            The date to be parsed.
	 * @return String representation of argument.
	 */
	public String getParsedDate(DateTime d) {
		return d.toString("dd/MM/yyyy, HH:mm");
	}

	/**
	 * Edit all attributes of an Event.
	 * 
	 * 
	 * @param start
	 *            The start date to be set.
	 * @param end
	 *            The end date to be set.
	 * @param name
	 *            The name to be set.
	 * @param visibility
	 *            The visibility to be set.
	 * @param is_repeated
	 *            The repetition status to be set.
	 * @param intervall
	 *            The repetition intervall to be set.
	 */
	public void edit(DateTime start, DateTime end, String name,
			Visibility visibility, boolean is_repeated, int intervall) {
		this.start = start;
		this.end = end;
		this.name = name;
		this.visibility = visibility;
		this.is_repeating = is_repeated;
		this.intervall = intervall;
	}

	/**
	 * Edit the description of this Event.
	 * 
	 * @param text
	 *            The new description to be set.
	 */
	public void editDescription(String text) {
		this.description = text;
	}

	/**
	 * Compare this Events start date with the arguments start date according to
	 * the definition of {@link Comparable#compareTo}
	 * 
	 * @param event
	 *            The event to compare this Event with.
	 * @returns a negative integer, zero, or a positive integer as this object
	 *          is less than, equal to, or greater than the specified object.
	 */
	@Override
	public int compareTo(Event event) {
		return this.getStart().compareTo(event.getStart());
	}

	/**
	 * Check if this Event is a repeating Event.
	 * 
	 * @return <code>true</code> if this Event is a repeating Event.
	 *         <code>false</code> otherwise.
	 */
	public boolean isRepeating() {
		return this.is_repeating;
	}

	/**
	 * Get the intervall of this Events repetition.
	 * 
	 * @return 0, if this Event is not repeating. 1, if this Event is repeated
	 *         on a daily basis. 7, if this Event is repeated weekly. 30, if
	 *         this Event is repeated every month. 365, if this Event is
	 *         repeated every year.
	 */
	public int getIntervall() {
		return this.intervall;
	}

	/**
	 * Get the next Repetition for this Event, based on its repetition status.
	 * 
	 * This method is so ugly i will not even try to understand what's going on.
	 * Change is coming, looking forward to introduce org.joda.DateTime soon.
	 * 
	 * @return An Event with the same baseId as this Event, whose start date is
	 *         calculated based on this Events repetition status.
	 */
	// TODO: fix ugly date instantiation and fix correct calculation for monthly
	// repeating events
	public Event getNextRepetitionEvent() {

		DateTime nextStart = null;
		DateTime nextEnd = null;
		switch (intervall) {
		case 1: {
			nextStart = this.start.plusDays(1);
			nextEnd = this.end.plusDays(1);
		}
			break;
		case 7: {
			nextStart = this.start.plusDays(7);
			nextEnd = this.end.plusDays(7);
		}
			break;
		case 30: {
			nextStart = this.start.plusMonths(1);
			nextEnd = this.end.plusMonths(1);
		}
			break;
		case 365: {
			nextStart = this.start.plusYears(1);
			nextEnd = this.end.plusYears(1);
		}
			break;
		}
		Event nextRepetition = new Event(this.owner, nextStart, nextEnd,
				this.name, this.visibility, this.is_repeating, this.intervall);
		nextRepetition.setBaseId(this.baseId);
		return nextRepetition;

		// DateTime nextRepStartDate = new DateTime(start.getYear(),
		// start.getMonth(),
		// start.getDate() + intervall, start.getHours(),
		// start.getMinutes());
		// DateTime nextRepEndDate = new DateTime(end.getYear(), end.getMonth(),
		// end.getDate() + intervall, end.getHours(), end.getMinutes());
		//
		// if (intervall == 30) {
		//
		// // get month of start to be corrected: add a extra variable for
		// // end.getMonth()
		// int k = start.getMonth() + 1;
		// int delta = 0;
		// int yearType = (start.getYear() + 1900) % 4;
		// // leap year: yearType = 0;
		// // normal year: yearType = 1|2|3;
		// int month = start.getMonth() + 1;
		//
		// // if it is jan, mar, mai, jun, jul, okt or dez ==> 31er months
		// if (month == 1 || month == 3 || month == 5 || month == 7
		// || month == 8 || month == 10 || month == 12) {
		//
		// // if we have a event on a 31th of month, then feb, apr, aug,
		// // sep, nov wont have an event,
		// // since they have no 31th. Therefore +2
		// if (start.getDate() == 31) {
		//
		// // if we have december or june
		// if (k == 7 || k == 12)
		// delta = -1;
		//
		// nextRepStartDate = new DateTime(start.getYear(),
		// start.getMonth() + 2 + delta, start.getDate(),
		// start.getHours(), start.getMinutes());
		// nextRepEndDate = new DateTime(end.getYear(), end.getMonth() + 2
		// + delta, end.getDate(), end.getHours(),
		// end.getMinutes());
		//
		// // if we have an event on the 29th of a month
		// } else if (start.getDate() == 29) {
		//
		// // if current month is january and a leap-year
		// if (k == 1 && yearType != 0) {
		// nextRepStartDate = new DateTime(start.getYear(),
		// start.getMonth() + 2 + delta, start.getDate(),
		// start.getHours(), start.getMinutes());
		// nextRepEndDate = new DateTime(end.getYear(), end.getMonth()
		// + 2 + delta, end.getDate(), end.getHours(),
		// end.getMinutes());
		// // if we have mar, mai, jun, jul, okt or dez
		// } else {
		// nextRepStartDate = new DateTime(start.getYear(),
		// start.getMonth() + 1, start.getDate(),
		// start.getHours(), start.getMinutes());
		// nextRepEndDate = new DateTime(end.getYear(),
		// end.getMonth() + 1, end.getDate(),
		// end.getHours(), end.getMinutes());
		// }
		// // all other days as event in the months: jan, mar, mai,
		// // jun, jul, okt or dez
		// } else {
		// nextRepStartDate = new DateTime(start.getYear(),
		// start.getMonth() + 1, start.getDate(),
		// start.getHours(), start.getMinutes());
		// nextRepEndDate = new DateTime(end.getYear(),
		// end.getMonth() + 1, end.getDate(), end.getHours(),
		// end.getMinutes());
		// }
		//
		// // if it is feb ==> 28er or 29er month
		// } else if (month == 2) {
		// // if we have a leap year
		// if (yearType == 0) {
		// nextRepStartDate = new DateTime(start.getYear(),
		// start.getMonth() + 1, start.getDate(),
		// start.getHours(), start.getMinutes());
		// nextRepEndDate = new DateTime(end.getYear(),
		// end.getMonth() + 1, end.getDate(), end.getHours(),
		// end.getMinutes());
		// } else {
		// nextRepStartDate = new DateTime(start.getYear(),
		// start.getMonth() + 1, start.getDate(),
		// start.getHours(), start.getMinutes());
		// nextRepEndDate = new DateTime(end.getYear(),
		// end.getMonth() + 1, end.getDate(), end.getHours(),
		// end.getMinutes());
		// }
		//
		// // if it is apr, aug, sep or nov => 30er months
		// } else {
		// nextRepStartDate = new DateTime(start.getYear(),
		// start.getMonth() + 1, start.getDate(),
		// start.getHours(), start.getMinutes());
		// nextRepEndDate = new DateTime(end.getYear(), end.getMonth() + 1,
		// end.getDate(), end.getHours(), end.getMinutes());
		// }
		// }
		// if (intervall == 365) {
		// // if we have a leap year, remember february is equals 1
		// if (start.getDate() == 29 && start.getMonth() == 1) {
		// nextRepStartDate = new DateTime(start.getYear() + 4,
		// start.getMonth(), start.getDate(), start.getHours(),
		// start.getMinutes());
		// nextRepEndDate = new DateTime(end.getYear() + 4, end.getMonth(),
		// end.getDate(), end.getHours(), end.getMinutes());
		// } else {
		// nextRepStartDate = new DateTime(start.getYear() + 1,
		// start.getMonth(), start.getDate(), start.getHours(),
		// start.getMinutes());
		// nextRepEndDate = new DateTime(end.getYear() + 1, end.getMonth(),
		// end.getDate(), end.getHours(), end.getMinutes());
		// }
		// }
		// Event newEvent = new Event(this.owner, nextRepStartDate,
		// nextRepEndDate, this.name, this.visibility, this.is_repeating,
		// this.intervall);
		// newEvent.setBaseId(this.baseId);
		// return newEvent;
	}

	/**
	 * Set the baseId
	 * 
	 * @param id
	 *            The baseId to be set.
	 */
	private void setBaseId(long id) {
		this.baseId = id;
	}

	/**
	 * Get the baseId of this Event.
	 * 
	 * @return The baseId of this Event.
	 */
	public long getBaseId() {
		return this.baseId;
	}

	/**
	 * This method compares a provided Date with the repetitions of this Event
	 * until the provided Date is smaller than the start date of the calculated
	 * repetition. If one of the repetitions has the same date as the provided
	 * date, this repetition will be returned.
	 * 
	 * @param compDate
	 *            the date which is compared to the calculated repetitions.
	 * @return null if no repetition of any Event occurs on the specified Date.
	 *         Event repeatingEventOnDay if
	 *         repeatingEventOnDay.getStart().getDate() == compDate.getDate().
	 * 
	 */
	public Event getRepetitionOnDate(LocalDate compDate) {
		Event repeatingEventOnDay = null;
		Event repeatingEvent = this;
		while (repeatingEvent.getStart().toLocalDate().isBefore(compDate)) {
			repeatingEvent = repeatingEvent.getNextRepetitionEvent();
			if (repeatingEvent.getStart().getDayOfMonth() == compDate
					.getDayOfMonth()) {
				System.out.println("new repeatingEvent : "
						+ repeatingEvent.start);
				repeatingEventOnDay = repeatingEvent;
			}
		}
		return repeatingEventOnDay;
	}

	/**
	 * Get a String representation for this Event.
	 * 
	 * @return The <code>name</code> of this Event.
	 */
	public String toString() {
		return this.name;
	}

	public String getDescription() {
		return this.description;
	}

	/**
	 * Check if this Event is visible.
	 * 
	 * @return <code>true</code> if the visibility status of this Event is
	 *         either PUBLIC or BUSY. <code>false</code> if the visibility
	 *         status if PRIVATE.
	 */
	public boolean isVisible() {
		return this.visibility != Visibility.PRIVATE;
	}

	public boolean isBusy() {
		return this.visibility == Visibility.BUSY;
	}

	public boolean isPublic() {
		return this.visibility == Visibility.PUBLIC;
	}

	public boolean isPrivate() {
		return this.visibility == Visibility.PRIVATE;
	}

}
