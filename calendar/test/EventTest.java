import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;

import models.*;
public class EventTest extends UnitTest{
	private Event event;
	private Event repeatingEvent;
	private User user;
	//public Event(Date start, Date end, String name, boolean is_visible){
	@Before
	public void setUp() throws Exception {
		this.user = new User("hans", "1234");
		this.event = new Event(new Date(1,1,1), new Date(1,1,2),"anEvent", false, false, 0);
		this.repeatingEvent = new Event(new Date(1, 1, 1), new Date(1, 1, 1), "repeating", false, true, 7);
	}
	
	@Test
	public void testGetName() {
		assertEquals("anEvent", event.getName());
	}
	
	@Test
	public void testGetStart() {
		assertEquals(new Date(1,1,1), event.getStart());
	}
	
	@Test
	public void testGetEnd() {
		assertEquals(new Date(1,1,2), event.getEnd());
	}
	
	@Test
	public void testisVisible() {
		assertFalse(event.isVisible());
	}
	
	@Test
	public void testGetInterVall() {
		int intervall = 7;
		Event repeatingEvent = new Event(new Date(), new Date(), "test", false, true, intervall);
		assertEquals(intervall, repeatingEvent.getIntervall());
	}
	
	@Test
	public void testGetNextRepetitionIntervall7() {
		assertEquals(repeatingEvent.getIntervall(), 7);
		Event nextRepetition = repeatingEvent.getNextRepetitionEvent();
		assertEquals(repeatingEvent.getBaseID(), nextRepetition.getBaseID());
		assertEquals(repeatingEvent.getStart().getDate() + repeatingEvent.getIntervall(), nextRepetition.getStart().getDate());
		assertEquals(repeatingEvent.getStart().getMonth(), nextRepetition.getStart().getMonth());
		assertEquals(repeatingEvent.getStart().getYear(), nextRepetition.getStart().getYear());
	}
	
	@Test
	public void testGetNextRepetitionIntervallMonth() {
		repeatingEvent.edit(repeatingEvent.start, repeatingEvent.end, repeatingEvent.name,
				repeatingEvent.is_visible, repeatingEvent.is_repeating, 30);
		assertEquals(repeatingEvent.getIntervall(), 30);
		Event nextRepetition = repeatingEvent.getNextRepetitionEvent();
		assertEquals(repeatingEvent.getBaseID(), nextRepetition.getBaseID());
		assertEquals(repeatingEvent.getStart().getDate(), nextRepetition.getStart().getDate());
		assertEquals(repeatingEvent.getStart().getMonth() + 1, nextRepetition.getStart().getMonth());
		assertEquals(repeatingEvent.getStart().getYear(), nextRepetition.getStart().getYear());
	}
	
	@Test
	public void testGetNextRepetitionIntervallYear() {
		repeatingEvent.edit(repeatingEvent.start, repeatingEvent.end, repeatingEvent.name,
				repeatingEvent.is_visible, repeatingEvent.is_repeating, 265);
		assertEquals(repeatingEvent.getIntervall(), 265);
		Event nextRepetition = repeatingEvent.getNextRepetitionEvent();
		assertEquals(repeatingEvent.getBaseID(), nextRepetition.getBaseID());
		assertEquals(repeatingEvent.getStart().getDate(), nextRepetition.getStart().getDate());
		assertEquals(repeatingEvent.getStart().getMonth(), nextRepetition.getStart().getMonth());
		assertEquals(repeatingEvent.getStart().getYear() + 1, nextRepetition.getStart().getYear());
	}
	
	@Test
	public void testGetRepetitionOnDate() {
		long expectedBaseId = repeatingEvent.getBaseID();
		Date expectedStartDate = new Date(repeatingEvent.getStart().getYear(), repeatingEvent.getStart().getMonth(),
				repeatingEvent.getStart().getDate()+repeatingEvent.getIntervall());
		Event repetition = repeatingEvent.getRepetitionOnDate(expectedStartDate);
		assertEquals(expectedBaseId, repetition.getBaseID());
	}
}
