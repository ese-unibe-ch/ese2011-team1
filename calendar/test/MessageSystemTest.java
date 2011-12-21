import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import enums.Visibility;

import models.Calendar;
import models.Database;
import models.Event;
import models.MessageSystem;
import models.PointEvent;
import models.User;
import play.test.UnitTest;



public class MessageSystemTest extends UnitTest {
	
	MessageSystem messageSystem;
	private User francis;
	private Calendar calendar;
	private User stefan;
	private Event event;
	
	@Before
	public void setUp() {
		Database.clearDatabase();
		this.messageSystem = Database.messageSystem;
		this.francis = new User("Francis", "1234", new DateTime(), "francis", Database.messageSystem);
		this.stefan = new User("Dani", "1234", new DateTime(), "dani", Database.messageSystem);
		this.event = new PointEvent("anEvent", new DateTime(0),
				new DateTime(0), Visibility.PRIVATE, calendar);
		this.calendar = new Calendar("Calendar", stefan);
		stefan.addCalendar(calendar);
		calendar.addEvent(event);
		Database.addUser(stefan);
	}
	
	@Test
	public void testSubscribe() {
		messageSystem.subscribe(francis);
		assertTrue(messageSystem.getListeners().contains(francis));
	}
	
	@Test
	public void testUnsibscribe() {
		messageSystem.subscribe(francis);
		assertTrue(messageSystem.getListeners().contains(francis));
		messageSystem.unsubscribe(francis);
		assertFalse(messageSystem.getListeners().contains(francis));
	}
	
	@Test
	public void testNotifyObservingUser() {
		messageSystem.subscribe(francis);
		messageSystem.notifyObservingUser(francis.getId(), stefan.getId(), calendar.getId(), event.getId(), "Hello There");
		assertFalse(francis.getEventsToAccept().isEmpty());
		francis.acceptInvitation(stefan.getId(), calendar.getId(), event.getId());
		assertTrue(event.getAttendingUsers().contains(francis));
	}
	
}