package controllers;

import java.util.LinkedList;
import java.util.List;

import models.Calendar;
import models.Database;
import models.Event;
import models.PointEvent;
import models.RepeatingEvent;
import models.User;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import play.data.validation.Required;
import play.mvc.Controller;
import play.mvc.Scope.Flash;
import play.mvc.With;
import enums.Interval;
import enums.Visibility;

@With(Secure.class)
public class Application extends Controller {

	final static DateTimeFormatter dateTimeInputFormatter = DateTimeFormat
			.forPattern("yyyy-MM-dd-HH-mm");
	final static DateTimeFormatter birthdayFormatter = DateTimeFormat
			.forPattern("yyyy-MM-dd");
	public static String message = null;

	public static void index(String username) {
		User me = Database.users.get(Security.connected());
		List<User> users = Database.getUserList();
		DateTime now = new DateTime();
		String s_activeDate = now.toString("yyyy-dd-MM-HH-mm");

		User user = Database.users.get(username);
		LinkedList<Calendar> calendars = me.getCalendars();
		
		boolean freshlyLoggedIn;
		// not notified yet
		if (!me.getLastLogin().plusHours(2).isBeforeNow() && !me.isNotified()) {
			freshlyLoggedIn = true;
			me.setNotified(true);
		}
		else freshlyLoggedIn = false;
		render(users, me, s_activeDate, calendars, user, freshlyLoggedIn);
	}

	public static void showMe(String s_activeDate) {
		User me = Database.users.get(Security.connected());
		List<User> users = Database.getUserList();
		Calendar defaultCalendar = me.getdefaultCalendar();
		LinkedList<Calendar> calendars = me.getCalendars();
		render(me, users, calendars, defaultCalendar, s_activeDate);
	}
}