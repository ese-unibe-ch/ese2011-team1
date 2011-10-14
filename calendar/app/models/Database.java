package models;


import java.util.Date;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

import controllers.Secure;

public class Database {

public static HashMap<String, User> users = new HashMap<String, User>();
    
    public static void addUser(User user) {
    	if(!users.containsKey(user.getName())) {
    		users.put(user.getName(), user);
    	}
    }

    public static void addUser(String username, String password) {
    	if(!users.containsKey(username)) {
    		users.put(username, new User(username, password));
    	}
    }
    
    public static void deleteUser(String username, String password) {
    	if(users.containsKey(username) && users.get(username).getPassword().equals(password)) {
    		users.remove(username);
    	}
    }
    
    public static void changePassword(String username, String oldPassword, String newPassword) {
    	if(users.containsKey(username) && users.get(username).getPassword().equals(oldPassword)) {
    		users.get(username).setPassword(newPassword);
    	}
    }
    
    public static List<User> getUserList() {
    	List<User> userList = new ArrayList<User>();
    	userList.addAll(users.values());
    	return userList;
    }
}