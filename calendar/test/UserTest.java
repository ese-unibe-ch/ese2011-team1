import models.Calendar;
import models.User;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;

public class UserTest extends UnitTest {
	final static DateTimeFormatter birthdayFormatter = DateTimeFormat
			.forPattern("dd/MM/yyyy");
	private User user;
	private Calendar calendar;
	private DateTime myBirthday = birthdayFormatter.parseDateTime("24/10/1989");

	@Before
	public void setUp() throws Exception {
		this.user = new User("hans", "1234", myBirthday, "hansli");
		this.calendar = new Calendar("Calendar", user);
	}

	@Test
	public void testGetName() {
		assertEquals("hans", user.getName());
	}

	@Test
	public void testGetPassword() {
		assertEquals("1234", user.getPassword());

	}

	@Test
	public void testIsBirthday() {
		assertEquals(user.birthday, user.getBirthday());

	}

	@Test
	public void testGetBrithdayVisibility() {
		assertEquals(false, user.isBirthdayPublic());
	}

	@Test
	public void testGetNickname() {
		assertEquals("hansli", user.getNickname());
	}

	@Test
	public void testGetCalendar() {
		assertEquals(user.getCalendars().get(0), user.getdefaultCalendar());
	}

	@Test
	public void testGetCalendarIteratorUser() {
		assertNotNull(user.getCalendars());
	}

}
