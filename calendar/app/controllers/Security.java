package controllers;
import org.joda.time.DateTime;

import play.*;
import models.Database;
import models.User;
public class Security extends Secure.Security {
    
    public static boolean authentify(String username, String password) {
        if(Database.users.containsKey(username)) {
        	User u = Database.users.get(username);
        	if (u.getPassword().equals(password)) {
        		u.setLastLogin(new DateTime());
        		return true;
        	}
        	else {
        		return false;
        	}
        }
        return false;
    }
    
}

