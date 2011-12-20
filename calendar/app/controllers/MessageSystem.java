package controllers;

import models.Database;
import models.User;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class MessageSystem extends Controller{
	
	public static void accept(long userId, long calendarId, long eventId){
		User me = Database.users.get(Security.connected());
		me.acceptInvitation(userId, calendarId, eventId);
		Application.index(me.getName());
	}
	
	public static void decline(long userId, long calendarId, long eventId){
		User me = Database.users.get(Security.connected());
		me.declineInvitation(userId, calendarId, eventId);
		Application.index(me.getName());
	}
}
