import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;

import models.*;
public class EventTest extends UnitTest{
	private Event event;
	private User user;
	//public Event(Date start, Date end, String name, boolean is_visible){
	@Before
	public void setUp() throws Exception {
		this.user = new User("hans", "1234");
		this.event = new Event(new Date(1,1,1), new Date(1,1,2),"anEvent", false);
	}
	
	@Test
	public void testGetName() {
		assertEquals("anEvent", event.getName());
	}
	
	@Test
	public void testGetStart() {
		assertEquals(new Date(1,1,1), event.getStart());
	}
	
	@Test
	public void testGetEnd() {
		assertEquals(new Date(1,1,2), event.getEnd());
	}
	
	@Test
	public void testisVisible() {
		assertFalse(event.isVisible());
	}
}
