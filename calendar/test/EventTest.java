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

public class EventTest extends UnitTest {

	private RepeatingEvent repeatingEvent;
	private User user;
	private User francis;
	private User stefan;
	private DateTime today = new DateTime(0);
	private DateTime tomorrow = new DateTime(1);
	private Event event;
	private Event event2AfterEvent;
	private Event event3BeforeEvent;
	private PointEvent attendingEvent;
	private Calendar calendar;
	private Calendar francisCalendar;

	@Before
	public void setUp() throws Exception {
		this.user = new User("hans", "1234", today, "hans2");
		this.francis = new User("francis", "1234", today, "fran");
		this.stefan = new User("stefan", "1234", today, "stef");
		this.calendar = new Calendar("testCalendar", user);
		this.francisCalendar = new Calendar("francisTestCalendar", francis);
		this.event = new PointEvent("anEvent", today, tomorrow,
				Visibility.PRIVATE, calendar);
		this.attendingEvent = new PointEvent("anAttendingEvent", today,
				tomorrow, Visibility.PUBLIC, francisCalendar);
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
		assertTrue(attendingEvent.isOpen());
		attendingEvent.addUserToAttending(francis);
		attendingEvent.addUserToAttending(stefan);
		assertEquals("francis, stefan", attendingEvent.getAttendingUsers());

	}

	@Test
	public void testUserIsAttending() {
		Database.clearDatabase();
		Database.addUser(francis);
		Database.addUser(stefan);
		attendingEvent.setOpen();
		attendingEvent.addUserToAttending(francis);
		attendingEvent.addUserToAttending(stefan);
		assertTrue(attendingEvent.userIsAttending("francis"));
		assertTrue(attendingEvent.userIsAttending("stefan"));
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
		DateTimeFormatter dateTimeInputFormatter = DateTimeFormat
				.forPattern("dd/MM/yyyy, HH:mm");
		this.event = new PointEvent("anEvent",
				dateTimeInputFormatter.parseDateTime("13/11/2011, 12:00"),
				dateTimeInputFormatter.parseDateTime("13/11/2011, 13:00"),
				Visibility.PRIVATE, calendar);
		assertEquals("12:00 - 13:00",
				event.getDatesFor(dateTimeInputFormatter
						.parseDateTime("13/11/2011, 12:00"), user));

		assertEquals("13/11/2011 12:00 - 13/11/2011 13:00",
				event.getDatesFor(dateTimeInputFormatter
						.parseDateTime("20/11/2011, 12:00"), user));
	}

	@Test
	public void testGetDescriptionFor() {
		event.editDescription("Hello World!");
		attendingEvent.editDescription("ESE Meeting");
		assertEquals("Hello World!", event.getDescriptionFor(user));
		assertEquals(null, event.getDescriptionFor(francis));
		assertEquals("ESE Meeting", attendingEvent.getDescriptionFor(user));
		assertEquals("ESE Meeting", attendingEvent.getDescriptionFor(francis));
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
