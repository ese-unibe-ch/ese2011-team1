
import org.junit.Test;

import play.test.UnitTest;
import utility.Bootstrap;

public class BootstrapTest extends UnitTest {

	@Test
	public void testDoJob() {
		Bootstrap bs = new Bootstrap();
		bs.doJob();
	}
}
