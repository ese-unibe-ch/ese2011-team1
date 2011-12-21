package controllers;

import models.Database;
import models.User;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import play.data.validation.Required;
import play.mvc.Controller;

public class publicApplication extends Controller {
	final static DateTimeFormatter birthdayFormatter = DateTimeFormat
			.forPattern("yyyy-MM-dd");

	public static void showRegistration() {
		render();
	}

	public static void RegUser(@Required String name,
			@Required String nickname, @Required String password, @Required String confirmPW,
			@Required String birthday, @Required boolean is_visible,
			String emailP, boolean is_emailP_visible, String emailB,
			boolean is_emailB_visible, String telP, boolean is_telP_visible,
			String telB, boolean is_telB_visible, String notes,
			boolean is_note_visible) {

		if (Database.userAlreadyRegistrated(name)) {
			flash.error("Username (" + name + ") already exists!");
			params.flash();
			validation.keep();
			showRegistration();

		} else if (!password.equals(confirmPW)) {
			params.flash();
			validation.keep();
			flash.error("Incorrect password confirmation!");
			showRegistration();

		} else if (validation.hasErrors()) {
			params.flash();
			validation.keep();
			flash.error("All (*) fields required!");
			showRegistration();
			
		} else {
			try {
				DateTime birthdate = birthdayFormatter.parseDateTime(birthday);
				User user = new User(name, password, birthdate, nickname, Database.messageSystem);

				user.setPrivateEmailAddress(emailP);
				user.setIsPrivateEmailVisible(is_emailP_visible);

				user.setBusinessEmailAdress(emailB);
				user.setIsBusinessEmailVisible(is_emailB_visible);

				user.setPrivatePhoneNumber(telP);
				user.setIsPrivatePhoneNumberVisible(is_telP_visible);

				user.setbusinessPhoneNumber(telB);
				user.setIsBusinessPhoneNumberVisible(is_telB_visible);

				user.setDescription(notes);
				user.setIsDescriptionVisible(is_note_visible);

				Database.addUser(user);

				try {
					Secure.authenticate(name, password, false);
					Secure.login();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				params.flash();
				validation.keep();
				flash.error("Invalid date format");
				showRegistration();
			}
		}
	}
}
