import models.Calendar;
import models.Database;
import models.User;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;

public class UserTest extends UnitTest {
	final static DateTimeFormatter birthdayFormatter = DateTimeFormat
			.forPattern("yyyy-MM-dd");
	private User user;
	private Calendar calendarTest;
	private DateTime myBirthday = birthdayFormatter.parseDateTime("1989-10-24");

	@Before
	public void setUp() throws Exception {
		this.user = new User("hans", "1234", myBirthday, "hansli",Database.messageSystem);
		this.calendarTest = new Calendar("CalendarTest", user);
		user.addCalendar(calendarTest);
	}

	@Test
	public void testGetName() {
		assertEquals("hans", user.getName());
	}

	@Test
	public void testSetName() {
		user.setName("Hans");
		assertEquals("Hans", user.getName());
	}

	@Test
	public void testGetPassword() {
		assertEquals("1234", user.getPassword());

	}

	@Test
	public void testSetPassword() {
		user.setPassword("12345");
		assertEquals("12345", user.getPassword());
	}

	@Test
	public void testSetBrithdayDate() {
		final DateTimeFormatter birthdayFormatter = DateTimeFormat
				.forPattern("yyyy-MM-dd");
		user.setBirthdayDate(birthdayFormatter.parseDateTime("1989-10-24"));
		assertEquals("1989-10-24",
				user.getBirthday().getStart().toString(birthdayFormatter));
		assertEquals("1989-10-24",
				user.getBirthday().getEnd().toString(birthdayFormatter));
	}

	@Test
	public void testSetBirthdayPublic() {
		user.setBirthdayPublic(true);
		assertEquals(true, user.isBirthdayPublic());
		user.setBirthdayPublic(false);
		assertEquals(false, user.isBirthdayPublic());
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
	public void testSetNickname() {
		user.setNickname("hampuma");
		assertEquals("hampuma", user.getNickname());
	}

	@Test
	public void testGetCalendar() {
		assertEquals(user.getCalendars().get(0), user.getdefaultCalendar());
	}

	@Test
	public void testGetCalendarById() {
		long id = calendarTest.getId();
		assertEquals(this.calendarTest, user.getCalendarById(id));
	}

	@Test
	public void testAddCalendar() {
		Calendar newTestCalendar = new Calendar("newTestCalendar", this.user);
		user.addCalendar(newTestCalendar);
		assertEquals(newTestCalendar, user.getCalendars().getLast());
	}

	@Test
	public void testAddAndRemoveObservedCalendar() {
		// Create and add a new observed Calendar
		Calendar newObservedTestCalendar = new Calendar(
				"newObservedTestCalendar", this.user);
		user.addObservedCalendar(newObservedTestCalendar);
		assertEquals(newObservedTestCalendar, user.getObservedCalendars()
				.getLast());
		// And then remove again
		user.removeObservedCalendar(newObservedTestCalendar);
		assertNotSame(newObservedTestCalendar, user.getObservedCalendars()
				.getLast());
	}

	@Test
	public void testAddAndRemoveShownObservedCalendar() {
		// Add the Id of the calendar to the shownObservedCalendar LinkedList
		// Calendar

		Calendar ShownObservedTestCalendar1 = new Calendar(
				"newObservedTestCalendar1", this.user);
		Calendar ShownObservedTestCalendar2 = new Calendar(
				"newObservedTestCalendar2", this.user);
		long calendarId1 = ShownObservedTestCalendar1.getId();
		long calendarId2 = ShownObservedTestCalendar2.getId();
		user.addShownObservedCalendar(calendarId1);
		user.addShownObservedCalendar(calendarId2);
		assertNotNull(user.getShownObservedCalendars().getFirst());
		assertEquals(calendarId1, (long) user.getShownObservedCalendars()
				.getFirst());
		assertNotNull(user.getShownObservedCalendars().getLast());
		assertEquals(calendarId2, (long) user.getShownObservedCalendars()
				.getLast());
		// And remove it again
		user.removeShownObservedCalendar(calendarId1);
		assertEquals(calendarId2, (long) user.getShownObservedCalendars()
				.getLast());

	}

	@Test
	public void testNewCalendar() {
		user.newCalendar("hans second calendar", this.user);
		assertEquals("hans second calendar", user.getCalendars().getLast()
				.getName().toString());
	}

	@Test
	public void testDeleteCalendar() {
		Calendar testCalendar = new Calendar("TestCalendar", this.user);
		long id = testCalendar.getId();
		user.deleteCalendar(id);
		assertNotSame(user.getCalendars().getLast(), testCalendar);
	}

	@Test
	public void testIsCalendarObserved() {
		Calendar newObservedTestCalendar3 = new Calendar(
				"newObservedTestCalendar3", this.user);
		long calendarId3 = newObservedTestCalendar3.getId();
		user.addObservedCalendar(newObservedTestCalendar3);
		assertFalse(user.isCalendarObserved(000000));
		assertTrue(user.isCalendarObserved(calendarId3));

	}

	@Test
	public void testGetBirthdayCalendar() {
		assertNotNull(user.getBirthdayCalendar());
	}

	@Test
	public void testIsBrithday() {
		assertTrue(user.isBirthday(user.getBirthday()));
	}

	@Test
	public void testGetCalendarIteratorUser() {
		assertNotNull(user.getCalendars());
	}

}
