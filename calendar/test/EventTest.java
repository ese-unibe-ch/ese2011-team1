import models.Calendar;
import models.Event;
import models.PointEvent;
import models.RepeatingEvent;
import models.User;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;
import enums.Interval;
import enums.Visibility;

public class EventTest extends UnitTest {

	private RepeatingEvent repeatingEvent;
	private User user;
	private User francis;
	private User simon;
	private DateTime today = new DateTime(0);
	private DateTime tomorrow = new DateTime(1);
	private Event event;
	private Event event2AfterEvent;
	private Event event3BeforeEvent;
	private Calendar calendar;
	private PointEvent attendingEvent;

	@Before
	public void setUp() throws Exception {
		this.user = new User("hans", "1234", today, "hans2");
		this.francis = new User("francis", "1234", today, "fran");
		this.simon = new User("simon", "1234", today, "sim");
		this.calendar = new Calendar("testCalendar", user);
		this.event = new PointEvent("anEvent", today, tomorrow,
				Visibility.PRIVATE, calendar);
		this.attendingEvent = new PointEvent("anAttendingEvent", today,
				tomorrow, Visibility.PUBLIC, calendar);
		this.event2AfterEvent = new PointEvent("event2AfterEvent",
				new DateTime(3), new DateTime(4), Visibility.PRIVATE, calendar);
		this.event3BeforeEvent = new PointEvent("event3BeforeEvent",
				new DateTime(-1), new DateTime(0), Visibility.PRIVATE, calendar);
		this.repeatingEvent = new RepeatingEvent("repeatingEvent", today,
				tomorrow, Visibility.PRIVATE, calendar, Interval.WEEKLY);
		this.repeatingEvent.init();
	}

	@Test
	public void testGetId() {
		assertEquals(event.getId(), event.getId());
	}

	@Test
	public void testGetName() {
		assertEquals("anEvent", event.getName());
	}

	@Test
	public void testGetStart() {
		assertEquals(new DateTime(0), event.getStart());
	}

	@Test
	public void testGetEnd() {
		assertEquals(new DateTime(1), event.getEnd());
	}

	@Test
	public void testisVisible() {
		assertFalse(event.getVisibility() != Visibility.PRIVATE);
	}

	@Test
	public void testGetParsedDate() {
		assertEquals("01/01/1970, 01:00", event.getParsedDate(today));
	}

	@Test
	public void testGetParsedStartDate() {
		assertEquals("01/01/1970, 01:00", event.getParsedStartDate());
	}

	@Test
	public void testGetParsedEndDate() {
		assertEquals("01/01/1970, 01:00", event.getParsedEndDate());
	}

	@Test
	public void testGetAttendingUsers() {
		attendingEvent.setOpen();
		attendingEvent.addUserToAttending(francis);
		attendingEvent.addUserToAttending(simon);
		assertEquals("francis, simon", attendingEvent.getAttendingUsers());

	}

	@Test
	public void testGetNameFor() {
		Event busyEvent = new PointEvent("anAttendingEvent", today, tomorrow,
				Visibility.BUSY, calendar);
		Event publicEvent = new PointEvent("public Event", today, tomorrow,
				Visibility.PUBLIC, calendar);
		assertEquals("public Event", publicEvent.getNameFor(francis));
		assertEquals("public Event", publicEvent.getNameFor(user));
		assertEquals("Busy", busyEvent.getNameFor(francis));
		assertEquals("anEvent", event.getNameFor(user));

	}

	@Test
	public void testGetDatesFor() {
		System.out.println(event.getDatesFor(today, user));
	}

	@Test
	public void testEditDescription() {
		event.editDescription("anEventChanged2");
		assertEquals("anEventChanged2", event.getDescription());
	}

	@Test
	public void testCompareTo() {
		assertEquals(0, event.getStart().compareTo(event.getStart()));
		assertEquals(-1,
				event3BeforeEvent.getStart().compareTo(event.getStart()));
		assertEquals(1, event2AfterEvent.getStart().compareTo(event.getStart()));
	}

	@Test
	public void testGetInterVall() {
		Interval interval = Interval.WEEKLY;
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

	/*
	 * @Test public void testGetNextRepetitionIntervallMonth() { repeatingEvent
	 * = new RepeatingEvent("repeatingEvent", repeatingEvent.getStart(),
	 * repeatingEvent.getEnd(), repeatingEvent.getVisibility(),
	 * repeatingEvent.getCalendar(), Interval.MONTHLY); repeatingEvent.init();
	 * assertEquals(repeatingEvent.getInterval(), 30); Event nextRepetition =
	 * repeatingEvent.getNextReference();
	 * assertEquals(repeatingEvent.getBaseId(), nextRepetition.getBaseId());
	 * assertEquals(repeatingEvent.getStart().plusMonths(1).getMillis(),
	 * repeatingEvent.getNextReference().getStart().getMillis());
	 * assertEquals(repeatingEvent.getStart().plusMonths(1).getMillis(),
	 * nextRepetition.getStart().getMillis());
	 * assertEquals(repeatingEvent.getStart().plusMonths(1).getYear(),
	 * nextRepetition.getStart().getYear()); }
	 * 
	 * @Test public void testGetNextRepetitionIntervallYear() { repeatingEvent =
	 * new RepeatingEvent("repeatingEvent", repeatingEvent.getStart(),
	 * repeatingEvent.getEnd(), repeatingEvent.getVisibility(),
	 * repeatingEvent.getCalendar(), Interval.YEARLY); repeatingEvent.init();
	 * assertEquals(repeatingEvent.getInterval(), 365); Event nextRepetition =
	 * repeatingEvent.getNextReference();
	 * assertEquals(repeatingEvent.getBaseId(), nextRepetition.getBaseId());
	 * assertEquals(repeatingEvent.getStart().plusYears(1).getMillis(),
	 * repeatingEvent.getNextReference().getStart().getMillis());
	 * assertEquals(repeatingEvent.getStart().getDayOfMonth(), nextRepetition
	 * .getStart().getDayOfMonth());
	 * assertEquals(repeatingEvent.getStart().plusYears(1).getMillis(),
	 * nextRepetition.getStart().getMillis()); }
	 */

}
