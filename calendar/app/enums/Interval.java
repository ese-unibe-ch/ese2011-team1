package enums;

/**
 * The Interval enum provides an easy way to represent the repetition status of an Event.
 *
 */
public enum Interval {
	
	/**
	 * This Event is not repeated.
	 */
	NONE(0),
	/**
	 * This Event is repeated every day.
	 */
	DAILY(1),
	/**
	 * This Event is repeated every week.
	 */
	WEEKLY(7),
	/**
	 * This Event is repeated every month.
	 */
	MONTHLY(30),
	/**
	 * This Event is repeated every year.
	 */
	YEARLY(365);
	
	private int days;
	
	private Interval(int days) {
		this.days = days;
	}
	
	public int getDays() {
		return this.days;
	}
	
}

