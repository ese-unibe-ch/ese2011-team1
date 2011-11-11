import java.util.Iterator;
import java.util.LinkedList;

import models.Calendar;
import models.Event;
import models.Event.Visibility;
import models.PointEvent;
import models.RepeatingEvent;
import models.User;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;

public class CalendarTest extends UnitTest {
	private User owner;
	private User francis;
	private Calendar calendarOfOwner;
	// private Calendar calendarOfFrancis;
	private Event event;
	private RepeatingEvent repeatingEvent;
	private Event eventToday;
	private Event eventToday2;

	@Before
	public void setUp() throws Exception {
		this.owner = new User("hans", "123", new DateTime(), "hans1");
		this.francis = new User("Francis", "1234", new DateTime(), "francis");
		/*
		 * this.calendarOfFrancis = new Calendar("Calendar of Francis",
		 * this.francis);
		 */
		this.calendarOfOwner = new Calendar("Calendar", this.owner);
		this.event = new PointEvent("anEvent", new DateTime(0), new DateTime(0), Visibility.PRIVATE, calendarOfOwner);
		this.repeatingEvent = new RepeatingEvent("repeatingEvent", new DateTime(0), new DateTime(0), Visibility.PRIVATE, calendarOfOwner, 7);
		this.repeatingEvent.init();
	}

	@Test
	public void testGetOwner() {
		assertEquals(owner, calendarOfOwner.getOwner());
	}

	@Test
	public void testGetName() {
		assertEquals("Calendar", calendarOfOwner.getName());
	}

	@Test
	public void testGetId() {
		long id = calendarOfOwner.getId();
		assertEquals(id, calendarOfOwner.getId());
	}

	@Test
	public void testAddEvent() {
		assertTrue(calendarOfOwner.getEventHeads().isEmpty());
		calendarOfOwner.addEvent(event);
		assertFalse(calendarOfOwner.getEventHeads().isEmpty());
		assertTrue(calendarOfOwner.getEventHeads().contains(event));
	}

	@Test
	public void testGetEventById() {
		long id = event.getId();
		calendarOfOwner.addEvent(event);
		assertEquals(event, calendarOfOwner.getEventById(id));
	}

	@Test
	public void testGetDayEvents() {
		this.eventToday = new PointEvent("eventToday", new DateTime(), new DateTime(), Visibility.PRIVATE, calendarOfOwner);
		this.eventToday2 = new PointEvent("eventToday2", new DateTime(), new DateTime(), Visibility.PUBLIC, calendarOfOwner);
		calendarOfOwner.addEvent(event);
		calendarOfOwner.addEvent(eventToday);
		calendarOfOwner.addEvent(eventToday2);
		LinkedList<Event> DayEvents = calendarOfOwner.getEventsOfDate(new LocalDate(), owner);
		LinkedList<Event> DayEventsFrancis = calendarOfOwner.getEventsOfDate(new LocalDate(), francis);
		assertFalse(DayEvents.isEmpty());
		assertFalse(DayEventsFrancis.isEmpty());
		assertEquals("[eventToday, eventToday2]", DayEvents.toString());
		assertEquals("[eventToday2]", DayEventsFrancis.toString());
		System.out.println(DayEvents);
		System.out.println(DayEventsFrancis);
	}

	// ITERATOR IS NO LONGER NEEDED AND MUST NOT BE TESTED!
//	@Test
//	public void testIterator1() {
//		Iterator<Event> events = calendarOfOwner.getEventList(new DateTime(0), owner);
//		assertNotNull(events);
//	}

	@Test
	public void testAddRepeatingEvent() {
		assertTrue(calendarOfOwner.getEventHeads().isEmpty());
		calendarOfOwner.addEvent(repeatingEvent);
		assertFalse(calendarOfOwner.getEventHeads().isEmpty());
		assertTrue(calendarOfOwner.getEventHeads().contains(repeatingEvent));
	}

	@Test
	public void testRemoveEvent() {
		calendarOfOwner.addEvent(event);
		assertTrue(calendarOfOwner.getEventHeads().contains(event));
		calendarOfOwner.removeEvent(event.getId());
		assertTrue(calendarOfOwner.getEventHeads().isEmpty());
		assertFalse(calendarOfOwner.getEventHeads().contains(event));
	}

	@Test
	public void testRemoveRepeatedEvents() {
		assertTrue(calendarOfOwner.getEventHeads().isEmpty());
		Event repeating1 = repeatingEvent.getNextReference();
		Event repeating2 = repeating1.getNextReference();
		Event repeating3 = repeating2.getNextReference();
		assertEquals(repeatingEvent.getBaseId(), repeating1.getBaseId());
		assertEquals(repeating1.getBaseId(), repeating2.getBaseId());
		assertEquals(repeating2.getBaseId(), repeating3.getBaseId());
		calendarOfOwner.addEvent(repeatingEvent);
		calendarOfOwner.addEvent(repeating1);
		calendarOfOwner.addEvent(repeating2);
		calendarOfOwner.addEvent(repeating3);
		assertFalse(calendarOfOwner.getEventHeads().isEmpty());
		assertTrue(calendarOfOwner.getEventHeads().contains(repeating1));
		assertTrue(calendarOfOwner.getEventHeads().contains(repeating2));
		assertTrue(calendarOfOwner.getEventHeads().contains(repeating3));
	}

}
