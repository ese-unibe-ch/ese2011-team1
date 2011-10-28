import java.util.Date;

import models.Event;
import models.Event.Visibility;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;

public class EventTest extends UnitTest {
	private Event event;
	private Event repeatingEvent;
	private User user;

	// public Event(Date start, Date end, String name, boolean is_visible){
	@Before
	public void setUp() throws Exception {
		this.user = new User("hans", "1234");
		this.event = new Event(user, new Date(1, 1, 1), new Date(1, 1, 2),
				"anEvent", Visibility.PRIVATE, false, 0);
		this.repeatingEvent = new Event(user, new Date(1, 1, 1), new Date(1, 1,
				1), "repeating", Visibility.PRIVATE, true, 7);
	}

	@Test
	public void testGetName() {
		assertEquals("anEvent", event.getName());
	}

	@Test
	public void testGetStart() {
		assertEquals(new Date(1, 1, 1), event.getStart());
	}

	@Test
	public void testGetEnd() {
		assertEquals(new Date(1, 1, 2), event.getEnd());
	}

	@Test
	public void testisVisible() {
		assertFalse(event.getVisibility() != Visibility.PRIVATE);
	}

	@Test
	public void testGetInterVall() {
		int intervall = 7;
		Event repeatingEvent = new Event(user, new Date(), new Date(), "test",
				Visibility.PRIVATE, true, intervall);
		assertEquals(intervall, repeatingEvent.getIntervall());
	}

	@Test
	public void testGetNextRepetitionIntervall7() {
		assertEquals(repeatingEvent.getIntervall(), 7);
		Event nextRepetition = repeatingEvent.getNextRepetitionEvent();
		assertEquals(repeatingEvent.getBaseID(), nextRepetition.getBaseID());
		assertEquals(
				repeatingEvent.getStart().getDate()
						+ repeatingEvent.getIntervall(), nextRepetition
						.getStart().getDate());
		assertEquals(repeatingEvent.getStart().getMonth(), nextRepetition
				.getStart().getMonth());
		assertEquals(repeatingEvent.getStart().getYear(), nextRepetition
				.getStart().getYear());
	}

	@Test
	public void testGetNextRepetitionIntervallMonth() {
		repeatingEvent.edit(repeatingEvent.start, repeatingEvent.end,
				repeatingEvent.name, repeatingEvent.visibility,
				repeatingEvent.is_repeating, 30);
		assertEquals(repeatingEvent.getIntervall(), 30);
		Event nextRepetition = repeatingEvent.getNextRepetitionEvent();
		assertEquals(repeatingEvent.getBaseID(), nextRepetition.getBaseID());
		assertEquals(repeatingEvent.getStart().getDate(), nextRepetition
				.getStart().getDate());
		assertEquals(repeatingEvent.getStart().getMonth() + 1, nextRepetition
				.getStart().getMonth());
		assertEquals(repeatingEvent.getStart().getYear(), nextRepetition
				.getStart().getYear());
	}

	@Test
	public void testGetNextRepetitionIntervallYear() {
		repeatingEvent.edit(repeatingEvent.start, repeatingEvent.end,
				repeatingEvent.name, repeatingEvent.visibility,
				repeatingEvent.is_repeating, 265);
		assertEquals(repeatingEvent.getIntervall(), 265);
		Event nextRepetition = repeatingEvent.getNextRepetitionEvent();
		assertEquals(repeatingEvent.getBaseID(), nextRepetition.getBaseID());
		assertEquals(repeatingEvent.getStart().getDate(), nextRepetition
				.getStart().getDate());
		assertEquals(repeatingEvent.getStart().getMonth(), nextRepetition
				.getStart().getMonth());
		assertEquals(repeatingEvent.getStart().getYear() + 1, nextRepetition
				.getStart().getYear());
	}

	@Test
	public void testGetRepetitionOnDate() {
		long expectedBaseId = repeatingEvent.getBaseID();
		Date expectedStartDate = new Date(repeatingEvent.getStart().getYear(),
				repeatingEvent.getStart().getMonth(), repeatingEvent.getStart()
						.getDate() + repeatingEvent.getIntervall());
		Event repetition = repeatingEvent
				.getRepetitionOnDate(expectedStartDate);
		assertEquals(expectedBaseId, repetition.getBaseID());
	}
}
