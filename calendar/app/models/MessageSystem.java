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
	
	public void notifyObservers(long userId){
		for(User user : listeners)
			if(user.getId() == userId) user.handle();
	}
	
	public void subscribe(User user){
		listeners.add(user);
	}
	
	public void unsubscribe(User user){
		listeners.remove(user);
	}
}
