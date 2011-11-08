import java.util.Iterator;
import java.util.LinkedList;

import models.Calendar;
import models.Event;
import models.Event.Visibility;
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
	private Event repeatingEvent;
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
		this.event = new Event(owner, new DateTime(0), new DateTime(1),
				"anEvent", Visibility.PRIVATE, false, 0, owner
						.getdefaultCalendar().getId(), false);
		this.repeatingEvent = new Event(owner, new DateTime(0),
				new DateTime(1), "repeatingEvent", Visibility.PRIVATE, true, 7,
				owner.getdefaultCalendar().getId(), false);
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
		long id = calendarOfOwner.id;
		assertEquals(id, calendarOfOwner.getId());
	}

	@Test
	public void testAddEvent() {
		assertTrue(calendarOfOwner.getEvents().isEmpty());
		calendarOfOwner.addEvent(event);
		assertFalse(calendarOfOwner.getEvents().isEmpty());
		assertTrue(calendarOfOwner.getEvents().contains(event));
		assertFalse(calendarOfOwner.getRepeatingEvents().contains(event));
	}

	@Test
	public void testGetEventById() {
		long id = event.getId();
		calendarOfOwner.addEvent(event);
		assertEquals(event, calendarOfOwner.getEventById(id));
	}

	@Test
	public void testGetDayEvents() {
		this.eventToday = new Event(owner, new DateTime(), new DateTime(),
				"eventToday", Visibility.PRIVATE, false, 0, owner
						.getdefaultCalendar().getId(), false);
		this.eventToday2 = new Event(owner, new DateTime(), new DateTime(),
				"eventToday2", Visibility.PUBLIC, false, 0, owner
						.getdefaultCalendar().getId(), false);
		calendarOfOwner.addEvent(event);
		calendarOfOwner.addEvent(eventToday);
		calendarOfOwner.addEvent(eventToday2);
		LinkedList<Event> DayEvents = calendarOfOwner.getDayEvents(
				new LocalDate(), owner);
		LinkedList<Event> DayEventsFrancis = calendarOfOwner.getDayEvents(
				new LocalDate(), francis);
		assertFalse(DayEvents.isEmpty());
		assertFalse(DayEventsFrancis.isEmpty());
		assertEquals("[eventToday, eventToday2]", DayEvents.toString());
		assertEquals("[eventToday2]", DayEventsFrancis.toString());
		System.out.println(DayEvents);
		System.out.println(DayEventsFrancis);
	}

	@Test
	public void testIterator1() {
		Iterator<Event> events = calendarOfOwner.getEventList(new DateTime(0),
				owner);
		assertNotNull(events);
	}

	@Test
	public void testAddRepeatingEvent() {
		assertTrue(calendarOfOwner.getEvents().isEmpty());
		assertTrue(calendarOfOwner.getRepeatingEvents().isEmpty());
		calendarOfOwner.addEvent(repeatingEvent);
		assertFalse(calendarOfOwner.getEvents().isEmpty());
		assertTrue(calendarOfOwner.getEvents().contains(repeatingEvent));
		assertTrue(calendarOfOwner.getRepeatingEvents()
				.contains(repeatingEvent));
	}

	@Test
	public void testAddToRepeated() {
		assertTrue(calendarOfOwner.getEvents().isEmpty());
		assertTrue(calendarOfOwner.getRepeatingEvents().isEmpty());
		calendarOfOwner.addToRepeated(repeatingEvent);
		assertTrue(calendarOfOwner.getEvents().isEmpty());
		assertTrue(calendarOfOwner.getRepeatingEvents()
				.contains(repeatingEvent));
	}

	@Test
	public void testRemoveEvent() {
		calendarOfOwner.addEvent(event);
		assertTrue(calendarOfOwner.getEvents().contains(event));
		calendarOfOwner.removeEvent(event.getId());
		assertTrue(calendarOfOwner.getEvents().isEmpty());
		assertFalse(calendarOfOwner.getEvents().contains(event));
	}

	@Test
	public void testRemoveRepeatedEvents() {
		assertTrue(calendarOfOwner.getEvents().isEmpty());
		assertTrue(calendarOfOwner.getRepeatingEvents().isEmpty());
		Event repeating1 = repeatingEvent.getNextRepetitionEvent();
		Event repeating2 = repeating1.getNextRepetitionEvent();
		Event repeating3 = repeating2.getNextRepetitionEvent();
		assertEquals(repeatingEvent.getBaseId(), repeating1.getBaseId());
		assertEquals(repeating1.getBaseId(), repeating2.getBaseId());
		assertEquals(repeating2.getBaseId(), repeating3.getBaseId());
		calendarOfOwner.addEvent(repeatingEvent);
		calendarOfOwner.addEvent(repeating1);
		calendarOfOwner.addEvent(repeating2);
		calendarOfOwner.addEvent(repeating3);
		assertTrue(calendarOfOwner.getRepeatingEvents()
				.contains(repeatingEvent));
		assertTrue(calendarOfOwner.getRepeatingEvents().contains(repeating1));
		assertTrue(calendarOfOwner.getRepeatingEvents().contains(repeating2));
		assertTrue(calendarOfOwner.getRepeatingEvents().contains(repeating3));
		calendarOfOwner.removeRepeatingEvents(repeating2);
		assertTrue(calendarOfOwner.getRepeatingEvents().isEmpty());
		assertTrue(calendarOfOwner.getEvents().isEmpty());
	}

}
