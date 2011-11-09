import models.Calendar;
import models.Event;
import models.Event.Visibility;
import models.PointEvent;
import models.RepeatingEvent;
import models.User;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;

public class EventTest extends UnitTest {

	private RepeatingEvent repeatingEvent;
	private User user;
	private DateTime today = new DateTime(0);
	private DateTime tomorrow = new DateTime(1);
	private Event event;
	private Event event2AfterEvent;
	private Event event3BeforeEvent;
	private Calendar calendar;

	// public Event(Date start, Date end, String name, boolean is_visible){
	@Before
	public void setUp() throws Exception {
		this.user = new User("hans", "1234", today, "hans2");
		this.calendar = new Calendar("testCalendar", user);
		this.event = new PointEvent("anEvent", today, tomorrow, Visibility.PRIVATE, calendar); 
		this.event2AfterEvent = new PointEvent("event2AfterEvent", new DateTime(3), new DateTime(4), Visibility.PRIVATE, calendar); 
		this.event3BeforeEvent = new PointEvent("event3BeforeEvent", new DateTime(-1), new DateTime(0), Visibility.PRIVATE, calendar); 
		this.repeatingEvent = new RepeatingEvent("repeatingEvent", today, tomorrow, Visibility.PRIVATE, calendar, 7);
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

	// EDIT EVENT NOT IMPLEMENTED IN NEW VERSION!
//	@Test
//	public void testEditEvent() {
//		event.edit(new DateTime(2), new DateTime(3), "anEventChanged",
//				Visibility.BUSY, false, 0);
//		assertNotNull(event);
//		assertEquals("anEventChanged", event.getName());
//		assertEquals(new DateTime(2), event.getStart());
//		assertEquals(new DateTime(3), event.getEnd());
//		assertTrue(event.getVisibility() == Visibility.BUSY);
//		assertEquals(false, event.isRepeating());
//		assertEquals(0, event.getIntervall());
//	}

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
		int intervall = 7;
		RepeatingEvent repeatingEvent = new RepeatingEvent("test", new DateTime(0),
				new DateTime(0), Visibility.PRIVATE, calendar, intervall);
		assertEquals(intervall, repeatingEvent.getInterval());
	}

	@Test
	public void testGetNextRepetitionIntervall7() {
		assertEquals(repeatingEvent.getInterval(), 7);
		System.out.println("repeating: " + repeatingEvent);
		Event nextRepetition = repeatingEvent.getNextReference();
		System.out.println("next: " + repeatingEvent.getNextReference() + "    sgbnsg");
		assertEquals(repeatingEvent.getBaseId(), nextRepetition.getBaseId());
		assertEquals(repeatingEvent.getStart().plusDays(7), nextRepetition.getStart());
		assertEquals(repeatingEvent.getStart().plusDays(7).dayOfMonth(), nextRepetition.getStart().dayOfMonth());
		assertEquals(repeatingEvent.getStart().plusDays(7).getYear(), nextRepetition
				.getStart().getYear());
	}

	@Test
	public void testGetNextRepetitionIntervallMonth() {
		repeatingEvent = new RepeatingEvent("repeatingEvent", repeatingEvent.getStart(), repeatingEvent.getEnd(), repeatingEvent.getVisibility(), repeatingEvent.getCalendar(), 30);
		assertEquals(repeatingEvent.getInterval(), 30);
		Event nextRepetition = repeatingEvent.getNextReference();
		assertEquals(repeatingEvent.getBaseId(), nextRepetition.getBaseId());
		assertEquals(repeatingEvent.getStart().plusMonths(1).getMillis(),
				repeatingEvent.getNextReference().getStart().getMillis());
		assertEquals(repeatingEvent.getStart().plusMonths(1).getMillis(),
				nextRepetition.getStart().getMillis());
		assertEquals(repeatingEvent.getStart().plusMonths(1).getYear(), nextRepetition
				.getStart().getYear());
	}

	@Test
	public void testGetNextRepetitionIntervallYear() {
		repeatingEvent = new RepeatingEvent("repeatingEvent", repeatingEvent.getStart(), repeatingEvent.getEnd(), repeatingEvent.getVisibility(), repeatingEvent.getCalendar(), 365);
		assertEquals(repeatingEvent.getInterval(), 365);
		Event nextRepetition = repeatingEvent.getNextReference();
		assertEquals(repeatingEvent.getBaseId(), nextRepetition.getBaseId());
		assertEquals(repeatingEvent.getStart().plusYears(1).getMillis(),
				repeatingEvent.getNextReference().getStart().getMillis());
		assertEquals(repeatingEvent.getStart().getDayOfMonth(), nextRepetition
				.getStart().getDayOfMonth());
		assertEquals(repeatingEvent.getStart().plusYears(1).getMillis(), nextRepetition
				.getStart().getMillis());
	}

	/*
	 * @Test public void testGetRepetitionOnDate() { long expectedBaseId =
	 * repeatingEvent.getBaseId(); Date expectedStartDate = new
	 * Date(repeatingEvent.getStart().getYear(),
	 * repeatingEvent.getStart().getDayOfMonth(), repeatingEvent .getStart() +
	 * repeatingEvent.getIntervall()); Event repetition = repeatingEvent
	 * .getRepetitionOnDate(expectedStartDate); assertEquals(expectedBaseId,
	 * repetition.getBaseId()); }
	 */
}
