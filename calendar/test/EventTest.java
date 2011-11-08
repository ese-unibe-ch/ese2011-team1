import models.Event;
import models.Event.Visibility;
import models.User;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;

public class EventTest extends UnitTest {

	private Event repeatingEvent;
	private User user;
	private DateTime today = new DateTime(0);
	private DateTime tomorrow = new DateTime(1);
	private Event event;
	private Event event2AfterEvent;
	private Event event3BeforeEvent;

	// public Event(Date start, Date end, String name, boolean is_visible){
	@Before
	public void setUp() throws Exception {
		this.user = new User("hans", "1234", today, "hans2");
		this.event = new Event(user, today, tomorrow, "anEvent",
				Visibility.PRIVATE, false, 0, user.getdefaultCalendar().getId(), false);
		this.event2AfterEvent = new Event(user, new DateTime(3),
				new DateTime(4), "event2AfterEvent", Visibility.PRIVATE, false,
				0, user.getdefaultCalendar().getId(), false);
		this.event3BeforeEvent = new Event(user, new DateTime(-1),
				new DateTime(0), "event3BeforeEvent", Visibility.PRIVATE,
				false, 0, user.getdefaultCalendar().getId(), false);
		this.repeatingEvent = new Event(user, today, tomorrow, "repeating",
				Visibility.PRIVATE, true, 7, user.getdefaultCalendar().getId(), false);
	}

	@Test
	public void testGetId() {
		assertEquals(event.id, event.getId());
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
	public void testEditEvent() {
		event.edit(new DateTime(2), new DateTime(3), "anEventChanged",
				Visibility.BUSY, false, 0);
		assertNotNull(event);
		assertEquals("anEventChanged", event.getName());
		assertEquals(new DateTime(2), event.getStart());
		assertEquals(new DateTime(3), event.getEnd());
		assertTrue(event.getVisibility() == Visibility.BUSY);
		assertEquals(false, event.isRepeating());
		assertEquals(0, event.getIntervall());
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
		int intervall = 7;
		Event repeatingEvent = new Event(user, new DateTime(0),
				new DateTime(0), "test", Visibility.PRIVATE, true, intervall,
				user.getdefaultCalendar().getId(), false);
		assertEquals(intervall, repeatingEvent.getIntervall());
	}

	@Test
	public void testGetNextRepetitionIntervall7() {
		assertEquals(repeatingEvent.getIntervall(), 7);
		Event nextRepetition = repeatingEvent.getNextRepetitionEvent();
		assertEquals(repeatingEvent.getBaseId(), nextRepetition.getBaseId());
		assertEquals(repeatingEvent.getStart().plusDays(7), nextRepetition.getStart());
		assertEquals(repeatingEvent.getStart().plusDays(7).dayOfMonth(), nextRepetition.getStart().dayOfMonth());
		assertEquals(repeatingEvent.getStart().plusDays(7).getYear(), nextRepetition
				.getStart().getYear());
	}

	@Test
	public void testGetNextRepetitionIntervallMonth() {
		repeatingEvent.edit(repeatingEvent.start, repeatingEvent.end,
				repeatingEvent.name, repeatingEvent.visibility,
				repeatingEvent.is_repeating, 30);
		assertEquals(repeatingEvent.getIntervall(), 30);
		Event nextRepetition = repeatingEvent.getNextRepetitionEvent();
		assertEquals(repeatingEvent.getBaseId(), nextRepetition.getBaseId());
		assertEquals(repeatingEvent.getStart().plusMonths(1).getMillis(),
				repeatingEvent.getNextRepetitionEvent().getStart().getMillis());
		assertEquals(repeatingEvent.getStart().plusMonths(1).getMillis(),
				nextRepetition.getStart().getMillis());
		assertEquals(repeatingEvent.getStart().plusMonths(1).getYear(), nextRepetition
				.getStart().getYear());
	}

	@Test
	public void testGetNextRepetitionIntervallYear() {
		repeatingEvent.edit(repeatingEvent.start, repeatingEvent.end,
				repeatingEvent.name, repeatingEvent.visibility,
				repeatingEvent.is_repeating, 365);
		assertEquals(repeatingEvent.getIntervall(), 365);
		Event nextRepetition = repeatingEvent.getNextRepetitionEvent();
		assertEquals(repeatingEvent.getBaseId(), nextRepetition.getBaseId());
		assertEquals(repeatingEvent.getStart().plusYears(1).getMillis(),
				repeatingEvent.getNextRepetitionEvent().getStart().getMillis());
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
