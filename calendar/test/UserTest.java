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
	private Calendar calendarTest;
	private DateTime myBirthday = birthdayFormatter.parseDateTime("24/10/1989");

	@Before
	public void setUp() throws Exception {
		this.user = new User("hans", "1234", myBirthday, "hansli");
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

	// We can't access birthday directly anymore!
//	@Test
//	public void testIsBirthday() {
//		assertEquals(user.birthday, user.getBirthday());
//
//	}

	// BIRTHDAY STUFF NOT IMPLEMENTED IN NEW VERSION!
//	@Test
//	public void testSetBirthdayDate() {
//		user.setBirthdayDate(birthdayFormatter.parseDateTime("25/10/1989"));
//		assertEquals(user.birthday, user.getBirthday());
//	}
//
//	@Test
//	public void testSetBirthdayPublic() {
//		assertTrue(user.getBirthday().isPrivate());
//		user.setBirthdayPublic(true);
//		assertTrue(user.getBirthday().isPublic());
//		user.setBirthdayPublic(false);
//		assertTrue(user.getBirthday().isPrivate());
//
//	}

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

	/*
	 * @Test public void testAddAndRemoveShownObservedCalendar() { // Add the Id
	 * of the calendar to the shownObservedCalendars LinkedList Calendar
	 * newShownObservedTestCalendar1 = new Calendar( "newObservedTestCalendar1",
	 * this.user); Calendar newShownObservedTestCalendar2 = new Calendar(
	 * "newObservedTestCalendar2", this.user); long calendarId1 =
	 * newShownObservedTestCalendar1.getId(); long calendarId2 =
	 * newShownObservedTestCalendar2.getId();
	 * user.addShownObservedCalendar(calendarId1);
	 * user.addShownObservedCalendar(calendarId2);
	 * assertNotNull(user.getShownObservedCalendars().getFirst());
	 * assertSame(calendarId1, user.getShownObservedCalendars().getFirst());
	 * assertNotNull(user.getShownObservedCalendars().getLast());
	 * assertSame(calendarId2, user.getShownObservedCalendars().getLast()); //
	 * And remove it again user.removeShownObservedCalendar(calendarId1);
	 * assertSame(calendarId2, user.getShownObservedCalendars().getLast());
	 * 
	 * }
	 */

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
		// hehe ;-) --> mah nüm^
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
