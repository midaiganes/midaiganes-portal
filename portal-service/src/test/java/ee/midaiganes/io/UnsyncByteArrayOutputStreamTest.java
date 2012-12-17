package ee.midaiganes.io;

import java.io.IOException;

import org.junit.Test;
import org.testng.Assert;

public class UnsyncByteArrayOutputStreamTest {
	@Test
	public void sizeTest() throws IOException {
		try (UnsyncByteArrayOutputStream ubaos = new UnsyncByteArrayOutputStream()) {
			byte[] b = new byte[5];
			ubaos.write(b);
			Assert.assertEquals(ubaos.getSize(), 5);
		}
	}

	@Test
	public void bigSizeTest() throws IOException {
		try (UnsyncByteArrayOutputStream ubaos = new UnsyncByteArrayOutputStream()) {
			byte[] b = new byte[4000];
			ubaos.write(b);
			Assert.assertEquals(ubaos.getSize(), 4000);
		}
	}

	@Test
	public void multiWriteTest() throws IOException {
		try (UnsyncByteArrayOutputStream ubaos = new UnsyncByteArrayOutputStream()) {
			byte[] b = new byte[4000];
			ubaos.write(b);
			ubaos.write(b);
			Assert.assertEquals(ubaos.getSize(), 8000);
		}
	}
}
