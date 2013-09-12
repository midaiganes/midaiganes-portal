package ee.midaiganes.util;

import org.junit.Test;
import org.testng.Assert;

public class LongUtilTest {
	@Test
	public void isNonNegativeLong() {
		Assert.assertTrue(LongUtil.isNonNegativeLong(Long.toString(0)));
		for (long l = 1; l < Long.MAX_VALUE / 1000; l *= 2) {
			Assert.assertTrue(LongUtil.isNonNegativeLong(Long.toString(l)));
		}
		Assert.assertFalse(LongUtil.isNonNegativeLong(Long.toString(-1)));
		Assert.assertFalse(LongUtil.isNonNegativeLong(Long.toString(Long.MIN_VALUE)));
		Assert.assertTrue(LongUtil.isNonNegativeLong(Long.toString(Long.MAX_VALUE)));
		Assert.assertFalse(LongUtil.isNonNegativeLong("abc"));
	}
}
