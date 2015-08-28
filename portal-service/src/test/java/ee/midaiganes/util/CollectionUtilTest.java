package ee.midaiganes.util;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;
import org.testng.Assert;

import ee.midaiganes.util.CollectionUtil;

public class CollectionUtilTest {
	@Test
	public void getFirstElement() {
		Assert.assertEquals(CollectionUtil.getFirstElementOrNull(Arrays.asList("a", "b")), "a");
	}

	@Test
	public void getFirstElementFromEmptyList() {
		Assert.assertNull(CollectionUtil.getFirstElementOrNull(Collections.emptyList()));
	}

	@Test
	public void getFirstElementFromNullList() {
		Assert.assertNull(CollectionUtil.getFirstElementOrNull(null));
	}
}
