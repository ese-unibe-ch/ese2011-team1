import models.Calendar;
import models.Database;
import models.IntervalEvent;
import models.RepeatingEvent;
import models.User;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import enums.Interval;
import enums.Visibility;
import play.test.UnitTest;


public class IntervalEventTest extends UnitTest{
	
	private User user;
	private Calendar calendar;
	private IntervalEvent event;

	@Before
	public void setUp() throws Exception {
		Database.clearDatabase();
		DateTime today = new DateTime();
		this.user = new User("hans", "1234", today, "hans2");
		this.calendar = new Calendar("testCalendar", user);
		this.event = new IntervalEvent("intervalEvent", today, today, today, today.plusWeeks(1), Visibility.PRIVATE, calendar, Interval.DAILY);
		this.event.init();
	}
	
	@Test
	public void testSetFrom() {
		DateTime from = new DateTime(23423);
		IntervalEvent testEvent = new IntervalEvent("test", new DateTime(), new DateTime(), from, new DateTime(), Visibility.PRIVATE, calendar, Interval.WEEKLY);
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
		IntervalEvent testEvent = new IntervalEvent("test", new DateTime(), new DateTime(), new DateTime(), to, Visibility.PRIVATE, calendar, Interval.WEEKLY);
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
		event.edit(newName, newStart, newEnd, newVisibility, newInterval, from, to);
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
		IntervalEvent testRemove = new IntervalEvent("test", new DateTime(), new DateTime(), new DateTime(), new DateTime(), Visibility.PRIVATE, calendar, Interval.DAILY);
		calendar.addEvent(testRemove);
		calendar.generateAllNextEvents(testRemove.getStart());
		assertTrue(calendar.hasEventOnDate(testRemove.getStart().toLocalDate(), user));
		testRemove.remove();
		assertFalse(calendar.hasEventOnDate(testRemove.getStart().toLocalDate(), user));
	}

}
