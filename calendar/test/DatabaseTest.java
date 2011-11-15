import models.Database;
import models.User;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;

public class DatabaseTest extends UnitTest {
	DateTimeFormatter dateTimeInputFormatter = DateTimeFormat
			.forPattern("dd/MM/yyyy, HH:mm");
	private User francis;
	private User stefan;
	private DateTime today = dateTimeInputFormatter
			.parseDateTime("15/11/2011, 12:00");

	@Before
	public void setUp() throws Exception {
		this.francis = new User("francis", "1234", today, "fran");
		this.stefan = new User("stefan", "1234", today, "stef");

	}

	@Test
	public void testAddUser() {
		Database.clearDatabase();
		Database.addUser(francis);
		Database.addUser(stefan);
		assertTrue(Database.getUserList().contains(francis));
		assertTrue(Database.getUserList().contains(stefan));
	}

	@Test
	public void testAddUserWihtoutObject() {
		Database.clearDatabase();
		Database.addUser("Steve", "12345", today, "Jobs");
		assertEquals("Steve", Database.getUserByName("Steve").getName());

	}

	@Test
	public void testDeleteUser() {
		Database.clearDatabase();
		Database.addUser(francis);
		Database.addUser(stefan);
		assertTrue(Database.getUserList().contains(francis));
		assertTrue(Database.getUserList().contains(stefan));
		Database.deleteUser("francis", "1234");
		assertFalse(Database.getUserList().contains(francis));
		assertTrue(Database.getUserList().contains(stefan));
	}

	@Test
	public void testChangePassword() {
		Database.clearDatabase();
		Database.addUser(francis);
		assertTrue(Database.getUserList().contains(francis));
		Database.changePassword("francis", "1234", "hello");
		assertEquals("hello", francis.getPassword());
	}

	@Test
	public void testSearchUser() {
		Database.clearDatabase();
		Database.addUser(francis);
		assertFalse(Database.searchUser("francis").isEmpty());
		assertTrue(Database.searchUser("stefan").isEmpty());
	}

	@Test
	public void testChangeUserName() {
		Database.clearDatabase();
		Database.addUser(francis);
		this.francis = new User("francisChanged", "1234", today, "fran");
		Database.changeUserName(francis, "francis", "1234");
		assertTrue(Database.getUserList().contains(francis));
		assertEquals(null, Database.getUserByName("francis"));
		assertEquals("francisChanged", Database.getUserByName("francisChanged")
				.getName());
		assertEquals("francisChanged", francis.getName());
	}

	@Test
	public void testUserAlreadyRegistrated() {
		Database.clearDatabase();
		Database.addUser(francis);
		assertFalse(Database.userAlreadyRegistrated("stefan"));
		assertTrue(Database.userAlreadyRegistrated("francis"));
	}

}
