// observable
package models;

import java.util.ArrayList;

/**
 * message system which is observable by users (we call them listeners). a user can send a message with 
 * a message and a destination user id to the message system. the message system itself
 * delivers this message to the destination user.
 * 
 * @author team1
 *
 */

public class MessageSystem {
	private ArrayList<User> listeners = new ArrayList<User>();
	
	/**
	 * notifies the user with id iserId that there is a message for him
	 * and let him receive this message.
	 * @param userId id of destination user
	 * @param message massage for destination user
	 */
	
	public void notifyObservingUser(long userId, long calendarId, long eventId, String message){
		for(User user : listeners)
			if(user.getId() == userId) user.receiveMessage(userId, calendarId, eventId, message);
	}
	
	
	/**
	 * Subscribe a given user to this message system.
	 * @param user subscribed user
	 */
	public void subscribe(User user){
		listeners.add(user);
	}
	
	/**
	 * Unsubscribe a given user from this message system
	 * @param user unsubscribed user
	 */
	public void unsubscribe(User user){
		listeners.remove(user);
	}
}
