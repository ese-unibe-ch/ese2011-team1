package models;
import java.util.Date;

public class Event implements Comparable<Event> {
	public Date start;
	public Date end;
	public String name;
	public boolean is_visible;
	public long id;
	private static long counter;
	
	public Event(Date start, Date end, String name, boolean is_visible){
		this.start = start;
		this.end = end;
		this.name = name;
		this.is_visible = is_visible;
		counter++;
		this.id = counter;
	}
	
	/*
	 * Getters
	 */
	
	public Date getStart(){
		return this.start;
	}
	
	public Date getEnd(){
		return this.end;
	}
	
	public boolean isVisible(){
		return this.is_visible;
	}
	
	public String getName(){
		return this.name;
	}
	
	public long getId(){
		return this.id;
	}
	
	public void edit(Date start, Date end, String name, boolean is_visible){
		this.start = start;
		this.end = end;
		this.name = name;
		this.is_visible = is_visible;
	}
	
	@Override
	public int compareTo(Event e) {
		return this.getStart().compareTo(e.getStart());
	}
	
	
	
}
