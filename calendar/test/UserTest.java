import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;

import models.*;
public class UserTest extends UnitTest{
	private User user;
	private Calendar calendar;

	@Before
	public void setUp() throws Exception {
		this.user = new User("hans", "1234");
		this.calendar = new Calendar("Calendar", user);
	}
	
	@Test
	public void testGetName() {
		assertEquals("hans", user.getName());
	}
	
	@Test
	public void testGetCalendarIteratorUser() {
		assertNotNull(user.getCalendar());
	}
}
