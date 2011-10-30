package models;

import java.util.Date;

public class BirthdayEvent extends Event {
	
	private User owner;
	private Date birthday;
	private Visibility visibility;
	private long id;

	

	public BirthdayEvent(User owner, Date start, Visibility visibility) {
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
		Date nextRepStartDate;

			// if we have a leap year, remember february is equals 1
			if (birthday.getDate() == 29 && birthday.getMonth() == 1) {
				nextRepStartDate = new Date(birthday.getYear() + 4,
						birthday.getMonth(), birthday.getDate(), birthday.getHours(),
						birthday.getMinutes());
			} else {
				nextRepStartDate = new Date(birthday.getYear() + 1,
						birthday.getMonth(), birthday.getDate(), birthday.getHours(),
						start.getMinutes());
			}
		BirthdayEvent newEvent = new BirthdayEvent(owner, nextRepStartDate, visibility);
		newEvent.setBaseId(this.baseId);
		return newEvent;
	}
	
	
	public BirthdayEvent getRepetitionOnDate(Date compDate) {
		BirthdayEvent repeatingEventOnDay = null;
		BirthdayEvent repeatingEvent = this;
		if (repeatingEvent.start.getYear() == compDate.getYear()
				&& repeatingEvent.start.getMonth() == compDate.getMonth()
				&& repeatingEvent.start.getDate() == compDate.getDate()) {
			return repeatingEvent;
		}
		while (repeatingEvent.getStart().before(compDate)) {
			repeatingEvent = repeatingEvent.getNextRepetitionEvent();
			if (repeatingEvent.getStart().getDate() == compDate.getDate()) {
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
