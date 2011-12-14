import models.Calendar;
import models.Database;
import models.IntervalEvent;
import models.PointEvent;
import models.RepeatingEvent;
import models.User;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import enums.Interval;
import enums.Visibility;
import play.test.UnitTest;

public class OverlapEventsTest extends UnitTest {

	private User user;
	private Calendar calendar;
	private PointEvent testEvent;
	private RepeatingEvent repeatingEvent;

	@Before
	public void setUp() throws Exception {
		Database.clearDatabase();
		this.user = new User("hans", "1234", new DateTime(), "hans2",
				Database.messageSystem);
		this.calendar = new Calendar("testCalendar", user);
		DateTime startDate = new DateTime(2011, 05, 15, 12, 0, 0, 0);
		DateTime endDate = new DateTime(2011, 05, 15, 18, 0, 0, 0);
		this.testEvent = new PointEvent("test", startDate, endDate,
				Visibility.PRIVATE, calendar);
		calendar.addEvent(testEvent);
		this.repeatingEvent = new RepeatingEvent("anEvent", startDate, endDate,
				Visibility.PRIVATE, calendar, Interval.WEEKLY);
		this.repeatingEvent.init();
		calendar.addEvent(repeatingEvent);
	}

	// case 1: The created event starts before the testevent, but the end date
	// of the created event is after the testevents start date.
	@Test
	public void testPointEventCase1() {
		DateTime start = new DateTime(2011, 05, 15, 8, 0, 0, 0);
		DateTime overlappingEnd = new DateTime(2011, 05, 15, 14, 0, 0, 0);
		PointEvent overlappingEvent = new PointEvent("overlappingEvent", start,
				overlappingEnd, Visibility.PRIVATE, calendar);
		assertTrue(overlappingEvent.isOverlappingWithOtherEvent());
		assertTrue(overlappingEvent.getOverlappingEvents().contains(testEvent));
	}

	// case 2: The created event starts before the testevent ends.
	@Test
	public void testPointEventCase2() {
		DateTime overlappingStart = new DateTime(2011, 05, 15, 15, 0, 0, 0);
		DateTime end = new DateTime(2011, 05, 15, 23, 0, 0, 0);
		PointEvent overlappingEvent = new PointEvent("overlappingEvent",
				overlappingStart, end, Visibility.PRIVATE, calendar);
		assertTrue(overlappingEvent.isOverlappingWithOtherEvent());
		assertTrue(overlappingEvent.getOverlappingEvents().contains(testEvent));
	}

	// case 3: The created event starts before the testevent ends and ends after
	// the testevent ends, i.e. the testevent happens during the created event..
	@Test
	public void testPointEventCase3() {
		DateTime start = new DateTime(2011, 05, 15, 10, 0, 0, 0);
		DateTime end = new DateTime(2011, 05, 15, 20, 0, 0, 0);
		PointEvent overlappingEvent = new PointEvent("overlappingEvent",
				start, end, Visibility.PRIVATE, calendar);
		assertTrue(overlappingEvent.isOverlappingWithOtherEvent());
		assertTrue(overlappingEvent.getOverlappingEvents().contains(testEvent));
	}
	
	// repeatingEvent case 1
	@Test
	public void testRepeatingEventCase1() {
		DateTime start = new DateTime(2011, 05, 22, 8, 0, 0, 0);
		DateTime overlappingEnd = new DateTime(2011, 05, 22, 15, 0, 0, 0);
		PointEvent overlappingEvent = new PointEvent("overlappingEvent", start,
				overlappingEnd, Visibility.PRIVATE, calendar);
		assertTrue(overlappingEvent.isOverlappingWithOtherEvent());
		assertTrue(overlappingEvent.getOverlappingEvents().contains(repeatingEvent.getNextReference()));
	}

	// repeatingEvent case 2
		@Test
		public void testRepeatingEventCase2() {
			DateTime start = new DateTime(2011, 05, 22, 15, 0, 0, 0);
			DateTime overlappingEnd = new DateTime(2011, 05, 22, 23, 0, 0, 0);
			PointEvent overlappingEvent = new PointEvent("overlappingEvent", start,
					overlappingEnd, Visibility.PRIVATE, calendar);
			assertTrue(overlappingEvent.isOverlappingWithOtherEvent());
			assertTrue(overlappingEvent.getOverlappingEvents().contains(repeatingEvent.getNextReference()));
		}
		
		// repeatingEvent case 3
		@Test
		public void testRepeatingEventCase3() {
			DateTime start = new DateTime(2011, 05, 22, 10, 0, 0, 0);
			DateTime overlappingEnd = new DateTime(2011, 05, 22, 20, 0, 0, 0);
			PointEvent overlappingEvent = new PointEvent("overlappingEvent", start,
					overlappingEnd, Visibility.PRIVATE, calendar);
			assertTrue(overlappingEvent.isOverlappingWithOtherEvent());
			assertTrue(overlappingEvent.getOverlappingEvents().contains(repeatingEvent.getNextReference()));
		}
}
