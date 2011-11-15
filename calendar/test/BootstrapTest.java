import models.Bootstrap;

import org.junit.Test;

import play.test.UnitTest;

public class BootstrapTest extends UnitTest {

	@Test
	public void testDoJob() {
		Bootstrap bs = new Bootstrap();
		bs.doJob();
	}
}
