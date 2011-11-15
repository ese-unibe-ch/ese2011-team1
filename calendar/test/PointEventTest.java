import models.Calendar;
import models.Event;
import models.PointEvent;
import models.User;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;
import enums.Visibility;

public class PointEventTest extends UnitTest {
	private User user;
	private Event event;
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

}
