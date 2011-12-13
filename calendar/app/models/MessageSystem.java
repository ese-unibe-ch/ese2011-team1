// observable
package models;

import java.util.ArrayList;
/**
 * 
 * 
 * @author team1
 *
 */
public class MessageSystem {
	private ArrayList<User> listeners = new ArrayList<User>();
	
	public void notifyObservingUser(long userId, String message){
		for(User user : listeners)
			if(user.getId() == userId) user.receiveMessage(message);
	}
	
	public void subscribe(User user){
		listeners.add(user);
	}
	
	public void unsubscribe(User user){
		listeners.remove(user);
	}
}
