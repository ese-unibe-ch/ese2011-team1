import models.Calendar;
import models.Event;
import models.PointEvent;
import models.User;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;
import enums.Interval;
import enums.Visibility;

public class PointEventTest extends UnitTest {
	private User user;
	private PointEvent event;
	private DateTime today = new DateTime(0);
	private DateTime tomorrow = new DateTime(1);
	private Calendar calendar;

	@Before
	public void setUp() throws Exception {
		this.user = new User("hans", "1234", today, "hans2");
		this.calendar = new Calendar("testCalendar", user);
		this.event = new PointEvent("anEvent", today, tomorrow,
				Visibility.PRIVATE, calendar);
	}

	@Test
	public void testGetType() {
		assertEquals("PointEvent", event.getType());
	}
	
	@Test
	public void testEditEvent() {
		// basic editing of attributes
		String newName = "newName";
		DateTime newStart = new DateTime(300000);
		DateTime newEnd = new DateTime(4000000);
		Visibility newVisibility = Visibility.PUBLIC;
		Interval newInterval = Interval.NONE;
		String newDescription = "new Description";
		event.edit(newName, newStart, newEnd, newVisibility, newInterval, null, null, newDescription);
		assertEquals(newName, event.getName());
		assertEquals(newStart, event.getStart());
		assertEquals(newEnd, event.getEnd());
		assertEquals(newVisibility, event.getVisibility());
		assertEquals(newDescription, event.getDescription());
		
		// change from PointEvent to RepeatingEvent
		newName = "newName";
		newStart = new DateTime(300000);
		newEnd = new DateTime(4000000);
		newVisibility = Visibility.PUBLIC;
		Interval newInterval = Interval.WEEKLY;
		newDescription = "new Description";
	}
}
