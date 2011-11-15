import models.Database;

import org.junit.Test;

import play.test.UnitTest;

public class DatabaseTest extends UnitTest {

	@Test
	public void testAddUser() {
		Database.addUser(user);
	}
}
