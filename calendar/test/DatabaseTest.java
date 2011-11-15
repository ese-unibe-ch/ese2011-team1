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
	private User user;
	private User francis;
	private User stefan;
	private DateTime today = dateTimeInputFormatter
			.parseDateTime("15/11/2011, 12:00");
	private DateTime tomorrow = dateTimeInputFormatter
			.parseDateTime("16/11/2011, 13:00");

	@Before
	public void setUp() throws Exception {
		this.user = new User("hans", "1234", today, "hans2");
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
}
