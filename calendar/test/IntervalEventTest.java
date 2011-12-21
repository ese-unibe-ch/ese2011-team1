import models.Calendar;
import models.Database;
import models.Event;
import models.IntervalEvent;
import models.RepeatingEvent;
import models.User;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import enums.Interval;
import enums.Visibility;
import play.test.UnitTest;

public class IntervalEventTest extends UnitTest {

	private User user;
	private Calendar calendar;
	private IntervalEvent event;
	private DateTime today;
	private DateTime tomorrow;

	@Before
	public void setUp() throws Exception {
		Database.clearDatabase();
		this.today = new DateTime(0);
		this.tomorrow = new DateTime(1);
		this.user = new User("hans", "1234", today, "hans2",
				Database.messageSystem);
		this.calendar = new Calendar("testCalendar", user);
		this.event = new IntervalEvent("intervalEvent", today, tomorrow, today,
				today.plusWeeks(1), Visibility.PRIVATE, calendar,
				Interval.DAILY);
		this.event.init();
	}

	@Test
	public void testSetFrom() {
		DateTime from = new DateTime(23423);
		IntervalEvent testEvent = new IntervalEvent("test", new DateTime(),
				new DateTime(), from, new DateTime(), Visibility.PRIVATE,
				calendar, Interval.WEEKLY);
		assertEquals(from, testEvent.getFrom());
	}

	@Test
	public void testGetFrom() {
		DateTime from = new DateTime(32434);
		event.setFrom(from);
		assertEquals(from, event.getFrom());
	}

	@Test
	public void testGetTo() {
		DateTime to = new DateTime(32434);
		event.setTo(to);
		assertEquals(to, event.getTo());
	}

	@Test
	public void testSetTo() {
		DateTime to = new DateTime(23423);
		IntervalEvent testEvent = new IntervalEvent("test", new DateTime(),
				new DateTime(), new DateTime(), to, Visibility.PRIVATE,
				calendar, Interval.WEEKLY);
		assertEquals(to, testEvent.getTo());
	}

	@Test
	public void testEditEvent() {
		// basic editing of attributes
		String newName = "newName";
		DateTime newStart = new DateTime();
		DateTime newEnd = new DateTime();
		Visibility newVisibility = Visibility.PUBLIC;
		Interval newInterval = Interval.DAILY;
		DateTime from = new DateTime(2342);
		DateTime to = new DateTime(3434);
		event.edit(newName, newStart, newEnd, newVisibility, newInterval, from,
				to);
		assertEquals(newName, event.getName());
		assertEquals(newStart, event.getStart());
		assertEquals(newEnd, event.getEnd());
		assertEquals(newVisibility, event.getVisibility());
		assertEquals(newInterval, event.getInterval());
		assertEquals(from, event.getFrom());
		assertEquals(to, event.getTo());

	}

	@Test
	public void testRemove() {
		DateTime now = new DateTime();
		Event testRemove = new RepeatingEvent("test", now, now,
				Visibility.PRIVATE, calendar, Interval.DAILY);
		calendar.addEvent(testRemove);
		testRemove.generateNextEvents(now.plusMonths(1));
		// calendar.generateNextEvents(testRemove, now.plusMonths(1));
		assertTrue(calendar.hasEventOnDate(testRemove.getStart().toLocalDate(),
				user));
		Event nextReference = testRemove.getNextReference();
		nextReference = nextReference.getNextReference();
		nextReference = nextReference.getNextReference();
		testRemove.getNextReference().getNextReference().getNextReference()
				.remove();
		assertFalse(calendar.hasEventOnDate(nextReference.getStart()
				.toLocalDate(), user));
		nextReference = nextReference.getPreviousReference();
		Event event = calendar.getEventById(testRemove.getId());
		Event next = event.getNextReference();
		next.remove();
	}

	@Test
	public void testRemove2() {
		DateTime now = new DateTime();
		Event testRemove = new RepeatingEvent("test", now, now,
				Visibility.PRIVATE, calendar, Interval.DAILY);
		calendar.addEvent(testRemove);
		testRemove.generateNextEvents(now.plusMonths(1));
		assertTrue(calendar.hasEventOnDate(testRemove.getStart().toLocalDate(),
				user));
		Event nextReference = testRemove.getNextReference();
		nextReference = nextReference.getNextReference();
		nextReference = nextReference.getNextReference();
		testRemove.getNextReference().getNextReference().getNextReference()
				.remove();
		assertFalse(calendar.hasEventOnDate(nextReference.getStart()
				.toLocalDate(), user));
		nextReference = nextReference.getPreviousReference();
		Event event = calendar.getEventById(testRemove.getId());
		event.remove();
	}

	@Test
	public void testRemove3() {
		DateTime now = new DateTime();
		Event testRemove = new RepeatingEvent("test", now, now,
				Visibility.PRIVATE, calendar, Interval.DAILY);
		calendar.addEvent(testRemove);
		testRemove.generateNextEvents(now.plusMonths(1));
		assertTrue(calendar.hasEventOnDate(testRemove.getStart().toLocalDate(),
				user));
		Event nextReference = testRemove.getNextReference();
		nextReference = nextReference.getNextReference();
		nextReference = nextReference.getNextReference();
		nextReference = nextReference.getNextReference();
		testRemove.getNextReference().getNextReference().getNextReference()
				.getNextReference().remove();
		assertFalse(calendar.hasEventOnDate(nextReference.getStart()
				.toLocalDate(), user));
		nextReference = nextReference.getPreviousReference();
		Event event = calendar.getEventById(testRemove.getId());
		Event next = event.getNextReference();
		next = next.getNextReference();
		next.remove();
	}

}
