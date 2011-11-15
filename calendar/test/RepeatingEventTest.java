
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
		this.user = new User("hans", "1234", today, "hans2");
		this.francis = new User("francis", "1234", today, "fran");
		Database.addUser(francis);
		assertTrue(Database.getUserList().contains(francis));
		this.stefan = new User("stefan", "1234", today, "stef");
		Database.addUser(stefan);
		assertTrue(Database.getUserList().contains(stefan));
		this.calendar = new Calendar("testCalendar", user);
		this.repeatingEvent = new RepeatingEvent("anEvent", today, tomorrow,
				Visibility.PRIVATE, calendar, Interval.WEEKLY);
		this.repeatingEvent.init();
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
}

