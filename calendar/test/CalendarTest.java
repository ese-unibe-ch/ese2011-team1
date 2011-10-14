import static org.junit.Assert.*;

import models.*;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;

public class CalendarTest extends UnitTest{
	private User owner;
	private Calendar calendar;
	private Event event;

	@Before
	public void setUp() throws Exception {
		this.owner = new User("hans", "123");
		this.calendar = new Calendar("Calendar", this.owner);
		this.event = new Event(new Date(1,1,1), new Date(1,1,2),"anEvent", false);
	}
	
	@Test
	public void testGetName() {
		assertEquals("Calendar", calendar.getName());
	}
	
	@Test
	public void testGetOwner() {
		assertEquals(owner, calendar.getOwner());
	}
	
	@Test
	public void testIterator1() {
		Iterator<Event> events = calendar.getEventList(new Date(1,1,1), owner);
		assertNotNull(events);
	}
}
