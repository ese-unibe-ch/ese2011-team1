import models.Calendar;
import models.Database;
import models.Event;
import models.PointEvent;
import models.RepeatingEvent;
import models.User;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
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
		Database.clearDatabase();
		this.user = new User("hans", "1234", today, "hans2",Database.messageSystem);
		this.francis = new User("francis", "1234", today, "fran",Database.messageSystem);
		this.stefan = new User("stefan", "1234", today, "stef",Database.messageSystem);
		Database.addUser(user);
		Database.addUser(francis);
		Database.addUser(stefan);
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
	public void testGetCalendarId() {
		Calendar calendar = event.getCalendar();
		assertEquals(calendar.getId(), event.getCalendarId());
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
		assertFalse(event.isPublic());
		assertFalse(event.isBusy());
		assertTrue(event.isPrivate());
	}
	
	@Test
	public void testSetVisibility() {
		assertTrue(event.isPrivate());
		assertFalse(event.isPublic());
		event.setVisiblility(Visibility.PUBLIC);
		assertTrue(event.isPublic());
		event.setVisiblility(Visibility.BUSY);
		assertTrue(event.isBusy());
	}
	
	@Test
	public void testGetOriginId() {
		event.setOriginId(27);
		assertEquals(27, event.getOriginId());
	}
	
	@Test
	public void testSetOriginId() {
		long originId = 27;
		event.setOriginId(originId);
		assertEquals(originId, event.getOriginId());
	}
	
	@Test
	public void testSetOpen() {
		assertFalse(event.isOpen());
		event.setOpen();
		assertTrue(event.isOpen());
	}
	
	@Test
	public void testSetClosed() {
		event.setOpen();
		assertTrue(event.isOpen());
		event.addUserToAttending(francis);
		assertTrue(Database.getUserList().contains(stefan));
		assertTrue(event.userIsAttending(francis.getName()));
		event.setClosed();
		assertFalse(event.isOpen());
		assertTrue(event.getAttendingUsers().isEmpty());
	}
	
	@Test
	public void testForceSetId() {
		assertNotSame(event.getId(), 27);
		event.forceSetId(27);
		assertEquals(27, event.getId());
	}
	
	@Test
	public void testPointEventIsRepeating() {
		assertTrue(event instanceof PointEvent);
		assertFalse(event.isRepeating());
	}
	
	@Test
	public void testEqualId() {
		long id = event.getId();
		assertTrue(event.equalId(id));
		id = 27;
		assertNotSame(event.getId(), 27);
		assertFalse(event.equalId(27));
	}
	
	@Test
	public void testEqualBaseId() {
		long baseId = event.getBaseId();
		assertTrue(event.equalBaseId(baseId));
		baseId = 27;
		assertNotSame(event.getBaseId(), 27);
		assertFalse(event.equalBaseId(27));
	}
	
	@Test
	public void testEqualOriginId() {
		long originId = event.getOriginId();
		assertTrue(event.equalOriginId(originId));
		originId = 27;
		assertNotSame(event.getOriginId(), 27);
		assertFalse(event.equalOriginId(27));
	}

	@Test
	public void testGetParsedDate() {
		assertEquals("1970-01-01-01-00", event.getParsedDate(today));
	}

	@Test
	public void testGetParsedStartDate() {
		assertEquals("1970-01-01-01-00", event.getParsedStartDate());
	}

	@Test
	public void testGetParsedEndDate() {
		assertEquals("1970-01-01-01-00", event.getParsedEndDate());
	}

	@Test
	public void testGetAttendingUsers() {
		attendingEvent.setOpen();
		assertTrue(attendingEvent.isOpen());
		attendingEvent.addUserToAttending(francis);
		attendingEvent.addUserToAttending(stefan);
		assertTrue(attendingEvent.getAttendingUsers().contains(francis));
		assertTrue(attendingEvent.getAttendingUsers().contains(stefan));
		assertEquals(2, attendingEvent.getAttendingUsers().size());
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
	public void testRemoveUserFromAttending() {
		event.setOpen();
		assertFalse(event.userIsAttending(stefan.getName()));
		event.addUserToAttending(stefan);
		assertTrue(event.userIsAttending(stefan.getName()));
		event.removeUserFromAttending(stefan);
		assertFalse(event.userIsAttending(stefan.getName()));
	}

	@Test
	public void testGetNameFor() {
		Event busyEvent = new PointEvent("anAttendingEvent", today, tomorrow,
				Visibility.BUSY, calendar);
		Event publicEvent = new PointEvent("public Event", today, tomorrow,
				Visibility.PUBLIC, calendar);
		Event privateEvent = new PointEvent("private Event", today, tomorrow,
				Visibility.PRIVATE, calendar);
		assertEquals("public Event", publicEvent.getNameFor(francis));
		assertEquals("public Event", publicEvent.getNameFor(user));
		assertEquals("Busy", busyEvent.getNameFor(francis));
		assertEquals("anEvent", event.getNameFor(user));
		privateEvent.addUserToAttending(francis);
		assertEquals("", event.getNameFor(stefan));
		assertEquals("private Event", privateEvent.getNameFor(francis));
	}

	@Test
	public void testGetDatesFor() {
		DateTimeFormatter dateTimeInputFormatter = DateTimeFormat
				.forPattern("yyyy-MM-dd-HH-mm");
		this.event = new PointEvent("anEvent",
				dateTimeInputFormatter.parseDateTime("2011-11-13-12-00"),
				dateTimeInputFormatter.parseDateTime("2011-11-13-13-00"),
				Visibility.PRIVATE, calendar);
		assertEquals("12:00 - 13:00",
				event.getDatesFor(dateTimeInputFormatter
						.parseDateTime("2011-11-13-12-00"), user));

		assertEquals("2011-11-13 12:00 - 2011-11-13 13:00",
				event.getDatesFor(dateTimeInputFormatter
						.parseDateTime("2011-11-20-12-00"), user));
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
	public void testFindHasEventOnDate() {
		User eventOwner = event.getOwner();
		LocalDate testDate = event.getStart().toLocalDate();
		assertNotNull(event.findHasEventOnDate(testDate, eventOwner));
		testDate = event.getEnd().toLocalDate();
		assertNotNull(event.findHasEventOnDate(testDate, eventOwner));
		testDate = new LocalDate(2000, 12, 12);
		assertFalse(event.happensOn(testDate));
		assertNull(event.findHasEventOnDate(testDate, eventOwner));
	}
	
	@Test
	public void testGetVisibilityFor() {
		Visibility eventVisibility = event.getVisibility();
		User eventOwner = event.getOwner();
		assertNotNull(event.getVisibilityFor(eventOwner));
		assertEquals(eventVisibility.toString(), event.getVisibilityFor(eventOwner));
		User notEventOwner = francis;
		assertNull(event.getVisibilityFor(notEventOwner));
	}
	
	@Test
	public void testOverlappingEvents() {
		
	}
	
}
