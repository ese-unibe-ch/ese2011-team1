package controllers;

import java.util.LinkedList;
import java.util.List;

import models.Calendar;
import models.Database;
import models.Event;
import models.User;
import play.mvc.Controller;

public class Debug extends Controller {

	public static void index() {
		List<User> users = Database.getUserList();
		List<Calendar> calendars = new LinkedList<Calendar>();
		for (User user : users) {
			calendars.add(user.getBirthdayCalendar());
			calendars.addAll(user.getCalendars());
		}

		List<Event> events = new LinkedList<Event>();
		for (Calendar cal : calendars) {
			events.addAll(cal.getEventHeads());
		}
		render(users, calendars, events);
	}
}
