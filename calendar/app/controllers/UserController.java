package controllers;

import java.util.List;

import models.Database;
import models.Event;
import models.User;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import play.data.validation.Required;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class UserController extends Controller {
	
	final static DateTimeFormatter dateTimeInputFormatter = DateTimeFormat
			.forPattern("yyyy-MM-dd-HH-mm");
	final static DateTimeFormatter birthdayFormatter = DateTimeFormat
			.forPattern("yyyy-MM-dd");
			
	/**
	 * Searches for Users which can be added to a event.
	 * (renders searchUserForAdding.html)
	 * 
	 * @param meStr me
	 * @param userToAddStr the user to add
	 * @param calendarOwnerStr the calendar owner
	 * @param calendarId the id of the calendar
	 * @param eventId the id of the event
	 * @param s_eventDate the event date
	 */
	public static void searchUserForAdding(String userSearchStr, String calendarOwnerStr, 
			long eventCalendarId, long eventId, String s_eventDate, long calendarId) {
		User me = Database.users.get(Security.connected());
		
		if (userSearchStr.isEmpty())
			render(me, null);

		List<User> results = Database.searchUser(userSearchStr);
		DateTime activeDate = new DateTime();
		
		render(me, results, calendarId, eventCalendarId, eventId, s_eventDate, activeDate, calendarOwnerStr);
	}

	public static void searchUser(String userName) {
		User me = Database.users.get(Security.connected());

		if (userName.isEmpty())
			render(me, null);

		List<User> results = Database.searchUser(userName);

		render(me, results);
	}
	
	public static void deleteMyAccount() {
		User loggedUser = Database.users.get(Security.connected());
		Database.deleteUser(loggedUser.getName(), loggedUser.getPassword());

		try {
			Secure.logout();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	public static void showProfile(String userName) {
		User me = Database.users.get(Security.connected());
		User user = Database.getUserByName(userName);
		Event birthday = user.getBirthday();
		String nickname = user.getNickname();
		String emailP = user.getPrivateEmailAddress();
		String emailB = user.getBusinessEmailAdress();
		String telP = user.getPrivatePhoneNumber();
		String telB = user.getBusinessPhoneNumber();
		String notes = user.getDescription();

		render(me, user, nickname, birthday, emailP, emailB, telP, telB, notes);
	}

	public static void showEditProfile() {
		User me = Database.users.get(Security.connected());

		String name = me.getName();
		String oldname = name;
		String nickname = me.getNickname();
		String password = me.getPassword();
		String birthday = me.getBirthday().getStart().toString("yyyy-MM-dd"); //("dd/MM/yyyy");
		
		boolean is_visible = me.isBirthdayPublic();

		String emailP = me.getPrivateEmailAddress();
		boolean is_emailP_visible = me.getIsPrivateEmailVisible();

		String emailB = me.getBusinessEmailAdress();
		boolean is_emailB_visible = me.getIsBusinessEmailVisible();

		String telP = me.getPrivatePhoneNumber();
		boolean is_telP_visible = me.getIsPrivatePhoneNumberVisible();

		String telB = me.getBusinessPhoneNumber();
		boolean is_telB_visible = me.getIsBusinessPhoneNumberVisible();

		String notes = me.getDescription();
		boolean is_note_visible = me.getIsDescriptionVisible();

		render(me, name, oldname, nickname, password, birthday, is_visible,
				emailP, is_emailP_visible, emailB, is_emailB_visible, telP,
				is_telP_visible, telB, is_telB_visible, notes, is_note_visible);
	}

	public static void editProfile(@Required String name, String oldname,
			@Required String password, @Required String confirmPW,
			@Required String birthday, @Required String nickname,
			@Required boolean is_visible, String emailP,
			boolean is_emailP_visible, String emailB,
			boolean is_emailB_visible, String telP, boolean is_telP_visible,
			String telB, boolean is_telB_visible, String notes,
			boolean is_note_visible) {

		User user = Database.users.get(Security.connected());

		if (!(name.equals(user.getName()))
				&& Database.userAlreadyRegistrated(name)) {
			flash.error("Username (" + name + ") already exists!");
			params.flash();
			validation.keep();
			showEditProfile();

		} else if (!password.equals(confirmPW)) {
			params.flash();
			validation.keep();
			flash.error("Incorrect password confirmation!");
			showEditProfile();

		} else if (validation.hasErrors()) {
			params.flash();
			validation.keep();
			flash.error("All (*) fields required!");
			showEditProfile();
		} else {
			try {
				User newUser = user;
				DateTime birthdate = birthdayFormatter.parseDateTime(birthday);
				newUser.setName(name);
				newUser.setNickname(nickname);
				newUser.setPassword(password);
				newUser.setBirthdayDate(birthdate);
				newUser.setBirthdayPublic(is_visible);
				newUser.setPrivateEmailAddress(emailP);
				newUser.setIsPrivateEmailVisible(is_emailP_visible);
				newUser.setBusinessEmailAdress(emailB);
				newUser.setIsBusinessEmailVisible(is_emailB_visible);
				newUser.setPrivatePhoneNumber(telP);
				newUser.setIsPrivatePhoneNumberVisible(is_telP_visible);
				newUser.setbusinessPhoneNumber(telB);
				newUser.setIsBusinessPhoneNumberVisible(is_telB_visible);
				newUser.setDescription(notes);
				newUser.setIsDescriptionVisible(is_note_visible);

				// TODO delete old user
				Database.changeUserName(newUser, user.getName(),
						user.getPassword());

				Application.index(name);
			} catch (Exception e) {
				params.flash();
				validation.keep();
				flash.error("Invalid date format");
				showEditProfile();
			}
		}
	}
	
}
