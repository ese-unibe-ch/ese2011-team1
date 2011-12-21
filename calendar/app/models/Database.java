package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.joda.time.DateTime;

/**
 * The Database class is responsible for maintaining Users in a database-like
 * structure.
 * 
 * It provides methods for obtaining single or multiple Users of this calendar
 * application. The Database class stores all Users in a HashMap, the key is the
 * username. This means that usernames are unique.
 * 
 * @see {@link HashMap}
 * @see {@link User}
 * 
 */
public class Database {

	public static HashMap<String, User> users = new HashMap<String, User>();
	public static MessageSystem messageSystem = new MessageSystem();
	/**
	 * Add a User to the Database.
	 * 
	 * @param user
	 *            The user to be added.
	 */
	public static void addUser(User user) {
		if (!users.containsKey(user.getName())) {
			users.put(user.getName(), user);
		}
	}

	/**
	 * Add a new User to the Database.
	 * 
	 * The User will be generated based on the arguments.
	 * 
	 * @param username
	 *            The username of the new User.
	 * @param password
	 *            The password of the new User.
	 * @param birthday
	 *            The birthday date of the new User.
	 * @param nickname
	 *            The nickname of the new User.
	 */
	public static void addUser(String username, String password,
			DateTime birthday, String nickname) {
		if (!users.containsKey(username)) {
			users.put(username,
					new User(username, password, birthday, nickname, messageSystem));
		}
	}

	/**
	 * Delete a User from the Database.
	 * 
	 * @param username
	 *            The username of the User to be deleted.
	 * @param password
	 *            The password of the User to be deleted.
	 */
	public static void deleteUser(String username, String password) {
		if (users.containsKey(username)
				&& users.get(username).getPassword().equals(password)) {
			
			// null pointer fix ugly
			User victimUser = getUserByName(username);
			for(User user : getUserList()){
				LinkedList<Calendar> helper = new LinkedList<Calendar>(user.getObservedCalendars());
				for (Calendar calendar : helper) {
					if(calendar.getOwner() == victimUser){
						user.removeObservedCalendar(calendar);
					}
				}
			}
			// end null pointer fix
			users.remove(username);
		}
	}

	/**
	 * Change the password of a User.
	 * 
	 * @param username
	 *            The username of the User.
	 * @param oldPassword
	 *            The users old password.
	 * @param newPassword
	 *            The users new password.
	 */
	public static void changePassword(String username, String oldPassword,
			String newPassword) {
		if (users.containsKey(username)
				&& users.get(username).getPassword().equals(oldPassword)) {
			users.get(username).setPassword(newPassword);
		}
	}

	/**
	 * Obtain a List of all Users in this Database.
	 * 
	 * @return List of all Users in Database.
	 */
	public static List<User> getUserList() {
		List<User> userList = new ArrayList<User>();
		userList.addAll(users.values());
		return userList;
	}

	/**
	 * Get a User based on its username.
	 * 
	 * @param name
	 *            The name of the User to be found.
	 * @return <code>null</code> if no User with the given name exists in the
	 *         Database. A User whose username equals <code>name</code>
	 *         otherwise.
	 * @see {@link HashMap#get}
	 */
	public static User getUserByName(String name) {
		return users.get(name);
	}
	
	/**
	 * returns a user by an given id.
	 * @param userId
	 * @return
	 */
	public static User getUserById(long userId){
		User result = null;
		for(User user : getUserList())
			if(user.getId() == userId) result = user;
		return result;
	}

	/**
	 * Search for all users containing a certain input String and get back a
	 * list containing them.
	 * 
	 * @param name
	 *            part of a user name
	 * @return usersFound a list with all the users containing the input string
	 */
	public static List<User> searchUser(String query) {
		List<User> userList = getUserList();
		List<User> usersFound = new ArrayList<User>();

		for (int i = 0; i < userList.size(); i++) {
			if (userList.get(i).getName().toLowerCase()
					.contains(query.toLowerCase())) {
				usersFound.add(userList.get(i));
			}
		}

		return usersFound;
	}

	/**
	 * Change a Users username.
	 * 
	 * @param user
	 *            The user to have its name changed.
	 * @param password
	 * @param username
	 */
	public static void changeUserName(User user, String username,
			String password) {
		Database.deleteUser(username, password);
		users.put(user.getName(), user);

	}

	/**
	 * Test if a User is already registered, based on the username.
	 * 
	 * @param newUserName
	 *            The username to be tested against all existing usernames in
	 *            the Database.
	 * @return <code>true</code> if the Database contains a User with the same
	 *         username as <code>newUserName</code>. <code>false</code>
	 *         otherwise.
	 */
	public static boolean userAlreadyRegistrated(String newUserName) {
		List<User> userList = new ArrayList<User>();
		userList.addAll(users.values());

		for (User u : userList) {
			if (u.getName().equals(newUserName))
				return true;
		}
		return false;
	}

	/**
	 * Removes all users from the hashmap <code>users</code>.
	 */
	public static void clearDatabase() {
		users.clear();
	}

}