package models;

import org.joda.time.DateTime;

public class BirthdayEvent extends Event {
	
	private User owner;
	private DateTime birthday;
	private Visibility visibility;
	private long id;

	

	public BirthdayEvent(User owner, DateTime start, Visibility visibility) {
		super(owner, start, start, "Birthday", visibility, true, 365);
		this.owner = owner;
		this.birthday = start;
		this.visibility = visibility;
		this.id = getId();
		this.is_repeating = true;
		this.intervall = 365;
		this.baseId = id;
	}
	
	
	public BirthdayEvent getNextRepetitionEvent() {
		assert intervall == 365;
		DateTime nextRepStartDate;

			// if we have a leap year, remember february is equals 1
			if (birthday.getDayOfMonth() == 29 && birthday.getMonthOfYear() == 2) {
				nextRepStartDate = new DateTime(birthday.getYear() + 4,
						birthday.getMonthOfYear(), birthday.getDayOfMonth(), birthday.getHourOfDay(),
						birthday.getMinuteOfHour());
			} else {
				nextRepStartDate = new DateTime(birthday.getYear() + 1,
						birthday.getMonthOfYear(), birthday.getDayOfMonth(), birthday.getHourOfDay(),
						start.getMinuteOfHour());
			}
		BirthdayEvent newEvent = new BirthdayEvent(owner, nextRepStartDate, visibility);
		newEvent.setBaseId(this.baseId);
		return newEvent;
	}
	
	
	public BirthdayEvent getRepetitionOnDate(DateTime compDate) {
		BirthdayEvent repeatingEventOnDay = null;
		BirthdayEvent repeatingEvent = this;
		if (repeatingEvent.start.toLocalDate().equals(compDate.toLocalDate())) {
			return repeatingEvent;
		}
		while (repeatingEvent.getStart().isBefore(compDate)) {
			repeatingEvent = repeatingEvent.getNextRepetitionEvent();
			if (repeatingEvent.getStart().getDayOfMonth() == compDate.getDayOfMonth()) {
				System.out.println("new repeatingEvent : " + repeatingEvent.start);
				repeatingEventOnDay = repeatingEvent;
			}
		}
		return repeatingEventOnDay;
	}
	
	private void setBaseId(long id) {
		this.baseId = id;
	}

}
