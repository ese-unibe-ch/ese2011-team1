import java.util.Date;
import java.util.Iterator;

import models.Calendar;
import models.Event;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;

public class CalendarTest extends UnitTest {
	private User owner;
	private Calendar calendar;
	private Event event;
	private Event repeatingEvent;

	@Before
	public void setUp() throws Exception {
		this.owner = new User("hans", "123");
		this.calendar = new Calendar("Calendar", this.owner);
		this.event = new Event(owner, new Date(1, 1, 1), new Date(1, 1, 2),
				"anEvent", false, false, 0);
		this.repeatingEvent = new Event(owner, new Date(1, 1, 1), new Date(1,
				1, 2), "repeatingEvent", false, true, 7);
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
	public void testGetId() {
		long id = calendar.id;
		assertEquals(id, calendar.getId());
	}

	@Test
	public void testGetEventById() {
		long id = event.getId();
		calendar.addEvent(event);
		assertEquals(event, calendar.getEventById(id));
	}

	@Test
	public void testIterator1() {
		Iterator<Event> events = calendar
				.getEventList(new Date(1, 1, 1), owner);
		assertNotNull(events);
	}

	@Test
	public void testAddEvent() {
		assertTrue(calendar.getEvents().isEmpty());
		calendar.addEvent(event);
		assertFalse(calendar.getEvents().isEmpty());
		assertTrue(calendar.getEvents().contains(event));
		assertFalse(calendar.getRepeatingEvents().contains(event));
	}

	@Test
	public void testAddRepeatingEvent() {
		assertTrue(calendar.getEvents().isEmpty());
		assertTrue(calendar.getRepeatingEvents().isEmpty());
		calendar.addEvent(repeatingEvent);
		assertFalse(calendar.getEvents().isEmpty());
		assertTrue(calendar.getEvents().contains(repeatingEvent));
		assertTrue(calendar.getRepeatingEvents().contains(repeatingEvent));
	}

	@Test
	public void testAddToRepeated() {
		assertTrue(calendar.getEvents().isEmpty());
		assertTrue(calendar.getRepeatingEvents().isEmpty());
		calendar.addToRepeated(repeatingEvent);
		assertTrue(calendar.getEvents().isEmpty());
		assertTrue(calendar.getRepeatingEvents().contains(repeatingEvent));
	}

	@Test
	public void testRemoveEvent() {
		calendar.addEvent(event);
		assertTrue(calendar.getEvents().contains(event));
		calendar.removeEvent(event.getId());
		assertTrue(calendar.getEvents().isEmpty());
		assertFalse(calendar.getEvents().contains(event));
	}

	@Test
	public void testRemoveRepeatedEvents() {
		assertTrue(calendar.getEvents().isEmpty());
		assertTrue(calendar.getRepeatingEvents().isEmpty());
		Event repeating1 = repeatingEvent.getNextRepetitionEvent();
		Event repeating2 = repeating1.getNextRepetitionEvent();
		Event repeating3 = repeating2.getNextRepetitionEvent();
		assertEquals(repeatingEvent.getBaseID(), repeating1.getBaseID());
		assertEquals(repeating1.getBaseID(), repeating2.getBaseID());
		assertEquals(repeating2.getBaseID(), repeating3.getBaseID());
		calendar.addEvent(repeatingEvent);
		calendar.addEvent(repeating1);
		calendar.addEvent(repeating2);
		calendar.addEvent(repeating3);
		assertTrue(calendar.getRepeatingEvents().contains(repeatingEvent));
		assertTrue(calendar.getRepeatingEvents().contains(repeating1));
		assertTrue(calendar.getRepeatingEvents().contains(repeating2));
		assertTrue(calendar.getRepeatingEvents().contains(repeating3));
		calendar.removeRepeatingEvents(repeating2);
		assertTrue(calendar.getRepeatingEvents().isEmpty());
		assertTrue(calendar.getEvents().isEmpty());
	}

}
