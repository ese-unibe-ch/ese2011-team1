package controllers;
import play.*;
import models.Database;
public class Security extends Secure.Security {
    
    public static boolean authentify(String username, String password) {
        if(Database.users.containsKey(username)) {
        	return Database.users.get(username).getPassword().equals(password);
        }
        return false;
    }
    
}

