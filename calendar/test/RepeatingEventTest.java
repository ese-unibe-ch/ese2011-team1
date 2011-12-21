import models.Calendar;
import models.Database;
import models.Event;
import models.PointEvent;
import models.RepeatingEvent;
import models.User;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;
import enums.Interval;
import enums.Visibility;

//TODO: remove unused tests from EventTest
public class RepeatingEventTest extends UnitTest {
	DateTimeFormatter dateTimeInputFormatter = DateTimeFormat
			.forPattern("yyyy-MM-dd-HH-mm");
	private RepeatingEvent repeatingEvent;
	private User user;
	private User francis;
	private User stefan;
	private DateTime today = new DateTime(0);
	private DateTime tomorrow = new DateTime(1);
	private Calendar calendar;

	@Before
	public void setUp() throws Exception {
		Database.clearDatabase();
		this.user = new User("hans", "1234", today, "hans2",
				Database.messageSystem);
		this.francis = new User("francis", "1234", today, "fran",
				Database.messageSystem);
		Database.addUser(francis);
		assertTrue(Database.getUserList().contains(francis));
		this.stefan = new User("stefan", "1234", today, "stef",
				Database.messageSystem);
		Database.addUser(stefan);
		assertTrue(Database.getUserList().contains(stefan));
		this.calendar = new Calendar("testCalendar", user);
		this.repeatingEvent = new RepeatingEvent("anEvent", today, tomorrow,
				Visibility.PRIVATE, calendar, Interval.WEEKLY);
		this.repeatingEvent.init();
	}

	@Test
	public void testGetInterVall() {
		Interval interval = Interval.DAILY;
		RepeatingEvent repeatingEvent = new RepeatingEvent("test",
				new DateTime(0), new DateTime(0), Visibility.PRIVATE, calendar,
				interval);
		assertEquals(interval, repeatingEvent.getInterval());
	}

	@Test
	public void testGetNextRepetitionIntervall7() {
		assertEquals(Interval.WEEKLY, repeatingEvent.getInterval());
		Event nextRepetition = repeatingEvent.getNextReference();
		assertEquals(repeatingEvent.getBaseId(), nextRepetition.getBaseId());
		assertEquals(repeatingEvent.getStart().plusDays(7),
				nextRepetition.getStart());
		assertEquals(repeatingEvent.getStart().plusDays(7).dayOfMonth(),
				nextRepetition.getStart().dayOfMonth());
		assertEquals(repeatingEvent.getStart().plusDays(7).getYear(),
				nextRepetition.getStart().getYear());
	}

	@Test
	public void testPointEventConstructor() {
		String eventName = "newEvent";
		DateTime now = new DateTime();
		Visibility visibility = Visibility.PRIVATE;
		PointEvent event = new PointEvent(eventName, now, now, visibility,
				calendar);
		RepeatingEvent newEvent = new RepeatingEvent(event, Interval.DAILY);
		assertEquals(eventName, newEvent.getName());
		assertEquals(now, newEvent.getStart());
		assertEquals(now, newEvent.getEnd());
		assertEquals(calendar, newEvent.getCalendar());
	}

	@Test
	public void testSpecialDaysOfMonth() {
		// test new Event on 31 of month
		// should only show up on every 31st
		Interval interval = Interval.MONTHLY;
		DateTime special = dateTimeInputFormatter
				.parseDateTime("2011-01-31-12-20");
		RepeatingEvent specialEvent = new RepeatingEvent("testSpecial",
				special, special, Visibility.PRIVATE, calendar, interval);
		calendar.addEvent(specialEvent);
		calendar.generateNextEvents(specialEvent, special.plusMonths(4));
		assertFalse(calendar.hasEventOnDate(
				specialEvent.getStart().plusMonths(1).toLocalDate(), user));
		assertTrue(calendar.hasEventOnDate(specialEvent.getStart()
				.plusMonths(2).toLocalDate(), user));
		assertEquals(specialEvent.getStart().plusMonths(2), specialEvent
				.getNextReference().getStart());

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
	}
}
