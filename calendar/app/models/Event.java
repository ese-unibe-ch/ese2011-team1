package models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.DateTime;

public class Event implements Comparable<Event> {
	public User owner;
	public DateTime start;
	public DateTime end;
	public String name;
	public String description;
	public Visibility visibility;
	public long id;
	public long baseId;
	public boolean is_repeating;
	public int intervall;
	private static long counter;
	private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm");
	public boolean isDirty = false;

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
	 *            WEEK(7), MONTH(30), YEAR(265)
	 */

	public enum Visibility {
		PUBLIC, BUSY, PRIVATE
	}

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

	public DateTime getStart() {
		return this.start;
	}

	public DateTime getEnd() {
		return this.end;
	}

	public Visibility getVisibility() {
		return this.visibility;
	}

	public String getName() {
		return this.name;
	}

	public long getId() {
		return this.id;
	}

	public String getParsedDate(Date d) {
		return dateFormat.format(d);
	}

	public void edit(DateTime start, DateTime end, String name, Visibility visibility,
			boolean is_repeated, int intervall) {
		this.start = start;
		this.end = end;
		this.name = name;
		this.visibility = visibility;
		this.is_repeating = is_repeated;
		this.intervall = intervall;
	}
	
	public void editDescription(String text){
		this.description = text;
	}

	@Override
	public int compareTo(Event e) {
		return this.getStart().compareTo(e.getStart());
	}

	public boolean isRepeating() {
		return this.is_repeating;
	}

	public int getIntervall() {
		return this.intervall;
	}

	// TODO: fix ugly date instantiation and fix correct calculation for monthly
	// repeating events
	public Event getNextRepetitionEvent() {
		
		DateTime nextStart = null;
		DateTime nextEnd = null;
		switch (intervall) {
		case 1: {
			nextStart = this.start.plusDays(1);
			nextEnd = this.end.plusDays(1);
		} break;
		case 7: {
			nextStart = this.start.plusDays(7);
			nextEnd = this.end.plusDays(7);
		} break;
		case 30: {
			nextStart = this.start.plusMonths(1);
			nextEnd = this.end.plusMonths(1);
		} break;
		case 365: {
			nextStart = this.start.plusYears(1);
			nextEnd = this.end.plusYears(1);
		} break;
		}
		Event nextRepetition = new Event(this.owner, nextStart, nextEnd, this.name, this.visibility, this.is_repeating, this.intervall);
		return nextRepetition;
		
//		DateTime nextRepStartDate = new DateTime(start.getYear(), start.getMonth(),
//				start.getDate() + intervall, start.getHours(),
//				start.getMinutes());
//		DateTime nextRepEndDate = new DateTime(end.getYear(), end.getMonth(),
//				end.getDate() + intervall, end.getHours(), end.getMinutes());
//
//		if (intervall == 30) {
//
//			// get month of start to be corrected: add a extra variable for
//			// end.getMonth()
//			int k = start.getMonth() + 1;
//			int delta = 0;
//			int yearType = (start.getYear() + 1900) % 4;
//			// leap year: yearType = 0;
//			// normal year: yearType = 1|2|3;
//			int month = start.getMonth() + 1;
//
//			// if it is jan, mar, mai, jun, jul, okt or dez ==> 31er months
//			if (month == 1 || month == 3 || month == 5 || month == 7
//					|| month == 8 || month == 10 || month == 12) {
//
//				// if we have a event on a 31th of month, then feb, apr, aug,
//				// sep, nov wont have an event,
//				// since they have no 31th. Therefore +2
//				if (start.getDate() == 31) {
//
//					// if we have december or june
//					if (k == 7 || k == 12)
//						delta = -1;
//
//					nextRepStartDate = new DateTime(start.getYear(),
//							start.getMonth() + 2 + delta, start.getDate(),
//							start.getHours(), start.getMinutes());
//					nextRepEndDate = new DateTime(end.getYear(), end.getMonth() + 2
//							+ delta, end.getDate(), end.getHours(),
//							end.getMinutes());
//
//					// if we have an event on the 29th of a month
//				} else if (start.getDate() == 29) {
//
//					// if current month is january and a leap-year
//					if (k == 1 && yearType != 0) {
//						nextRepStartDate = new DateTime(start.getYear(),
//								start.getMonth() + 2 + delta, start.getDate(),
//								start.getHours(), start.getMinutes());
//						nextRepEndDate = new DateTime(end.getYear(), end.getMonth()
//								+ 2 + delta, end.getDate(), end.getHours(),
//								end.getMinutes());
//						// if we have mar, mai, jun, jul, okt or dez
//					} else {
//						nextRepStartDate = new DateTime(start.getYear(),
//								start.getMonth() + 1, start.getDate(),
//								start.getHours(), start.getMinutes());
//						nextRepEndDate = new DateTime(end.getYear(),
//								end.getMonth() + 1, end.getDate(),
//								end.getHours(), end.getMinutes());
//					}
//					// all other days as event in the months: jan, mar, mai,
//					// jun, jul, okt or dez
//				} else {
//					nextRepStartDate = new DateTime(start.getYear(),
//							start.getMonth() + 1, start.getDate(),
//							start.getHours(), start.getMinutes());
//					nextRepEndDate = new DateTime(end.getYear(),
//							end.getMonth() + 1, end.getDate(), end.getHours(),
//							end.getMinutes());
//				}
//
//				// if it is feb ==> 28er or 29er month
//			} else if (month == 2) {
//				// if we have a leap year
//				if (yearType == 0) {
//					nextRepStartDate = new DateTime(start.getYear(),
//							start.getMonth() + 1, start.getDate(),
//							start.getHours(), start.getMinutes());
//					nextRepEndDate = new DateTime(end.getYear(),
//							end.getMonth() + 1, end.getDate(), end.getHours(),
//							end.getMinutes());
//				} else {
//					nextRepStartDate = new DateTime(start.getYear(),
//							start.getMonth() + 1, start.getDate(),
//							start.getHours(), start.getMinutes());
//					nextRepEndDate = new DateTime(end.getYear(),
//							end.getMonth() + 1, end.getDate(), end.getHours(),
//							end.getMinutes());
//				}
//
//				// if it is apr, aug, sep or nov => 30er months
//			} else {
//				nextRepStartDate = new DateTime(start.getYear(),
//						start.getMonth() + 1, start.getDate(),
//						start.getHours(), start.getMinutes());
//				nextRepEndDate = new DateTime(end.getYear(), end.getMonth() + 1,
//						end.getDate(), end.getHours(), end.getMinutes());
//			}
//		}
//		if (intervall == 365) {
//			// if we have a leap year, remember february is equals 1
//			if (start.getDate() == 29 && start.getMonth() == 1) {
//				nextRepStartDate = new DateTime(start.getYear() + 4,
//						start.getMonth(), start.getDate(), start.getHours(),
//						start.getMinutes());
//				nextRepEndDate = new DateTime(end.getYear() + 4, end.getMonth(),
//						end.getDate(), end.getHours(), end.getMinutes());
//			} else {
//				nextRepStartDate = new DateTime(start.getYear() + 1,
//						start.getMonth(), start.getDate(), start.getHours(),
//						start.getMinutes());
//				nextRepEndDate = new DateTime(end.getYear() + 1, end.getMonth(),
//						end.getDate(), end.getHours(), end.getMinutes());
//			}
//		}
//		Event newEvent = new Event(this.owner, nextRepStartDate,
//				nextRepEndDate, this.name, this.visibility, this.is_repeating,
//				this.intervall);
//		newEvent.setBaseId(this.baseId);
//		return newEvent;
	}

	private void setBaseId(long Id) {
		this.baseId = Id;
	}

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
	public Event getRepetitionOnDate(DateTime compDate) {
		Event repeatingEventOnDay = null;
		Event repeatingEvent = this;
		while (repeatingEvent.getStart().isBefore(compDate)) {
			repeatingEvent = repeatingEvent.getNextRepetitionEvent();
			if (repeatingEvent.getStart().getDayOfMonth() == compDate.getDayOfMonth()) {
				System.out.println("new repeatingEvent : " + repeatingEvent.start);
				repeatingEventOnDay = repeatingEvent;
			}
		}
		return repeatingEventOnDay;
	}

	public String toString() {
		return this.name;
	}

	public boolean isVisible() {
		return this.visibility != Visibility.PRIVATE;
	}

}
