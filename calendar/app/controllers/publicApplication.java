package controllers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import models.Database;
import models.User;
import play.data.validation.Required;
import play.mvc.Controller;

public class publicApplication extends Controller
{
	final static DateTimeFormatter birthdayFormatter = DateTimeFormat
			.forPattern("dd/MM/yyyy");
	
	public static void showRegistration() 
	{
		render();
	}
	
	public static void RegUser(@Required String name, @Required String nickname, @Required String password, @Required String birthday, @Required boolean is_visible,
	String emailP, boolean is_emailP_visible, String emailB, boolean is_emailB_visible,
	String telP, boolean is_telP_visible, String telB, boolean is_telB_visible, String notes, boolean is_note_visible)
	{
		
    	if(Database.userAlreadyRegistrated(name))
    	{
    		flash.error("Username (" + name + ") already exists!");
    		params.flash();
    		validation.keep();
    		showRegistration();
    	}
    	else if(validation.hasErrors())
    	{
    		params.flash();
    		validation.keep();
    		flash.error("All (*) fields required!");
    		showRegistration();
    	}
    	else
    	{
    		try 
    		{
    			DateTime birthdate = birthdayFormatter.parseDateTime(birthday);
    			User user = new User(name, password, birthdate, nickname);
    			
    			user.setEmailP(emailP);
    	    	user.setEmailPVis(is_emailP_visible);
    	    	
    	    	user.setEmailB(emailB);
    	    	user.setEmailBVis(is_emailB_visible);
    	    	
    	    	user.setTelP(telP);
    	    	user.setTelPVis(is_telP_visible);
    	    	
    	    	user.setTelB(telB);
    	    	user.setTelBVis(is_telB_visible);
    	    	
    	    	user.setNotes(notes);
    	    	user.setNotesVis(is_note_visible);
    			
    			Database.addUser(user);
    			user.setBirthdayPublic(is_visible);
    		
    			try {Secure.login();} catch (Throwable e) {e.printStackTrace();}
    		} 
    		catch (Exception e) 
    		{
    			params.flash();
        		validation.keep();
    			flash.error("Invalid date format");
    			showRegistration();
    		}	
    	}
    }

}
