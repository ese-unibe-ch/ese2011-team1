package models;

import java.util.LinkedList;

public class BirthdayCalendar extends Calendar{
	
	public String name;
	private static LinkedList<Event> birthdays;
	private static BirthdayCalendar instance;
	
	public BirthdayCalendar() {
		super(null);
		this.name = "Birthdays";
		birthdays = new LinkedList<Event>();
	}
	
	public static void addBirthday(BirthdayEvent birthday) {
		birthdays.add(birthday);
	}
	
	public static void removeBirthday(Event birthday) {
		birthdays.remove(birthday);
	}

	public LinkedList<Event> getRepeatingEvents() {
		return birthdays;
	}

	public static Event getBirthdayOf(User user) {
		Event birthday = null;
		for (Event e : birthdays) {
			if (user == e.owner) {
				birthday = e;
			}
		}
		assert birthday != null : "Birthdays must have an owner!";
		return birthday;
	}

	public static BirthdayCalendar getInstance() {
		if (instance == null) {
			instance = new BirthdayCalendar();
		}
		return instance;
	}

	public static LinkedList<Event> getBirthdays() {
		return birthdays;
	}

}
