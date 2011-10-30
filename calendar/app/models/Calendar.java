package models;

import java.util.LinkedList;
import java.util.PriorityQueue;

public class Calendar {

	private PriorityQueue<Event> events;
	private LinkedList<Event> repeatingEvents;
	private static int counter;
	public long id;
	public User owner;
	
	public Calendar(User owner) {
		events = new PriorityQueue<Event>();
		repeatingEvents = new LinkedList<Event>();
		counter++;
		this.id = counter;
	}

	public long getId() {
		return this.id;
	}

	public PriorityQueue<Event> getEvents() {
		return this.events;
	}

	public LinkedList<Event> getRepeatingEvents() {
		return this.repeatingEvents;
	}
}
