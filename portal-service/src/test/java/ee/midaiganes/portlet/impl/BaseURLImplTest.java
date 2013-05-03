package ee.midaiganes.portlet.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.testng.Assert;

public class BaseURLImplTest {
	private BaseURLImpl baseURL;
	private static final String URL = "http://localhost/";

	@Before
	public void beforeMethod() {
		baseURL = new BaseURLImpl("localhost", 80, "/", false);
	}

	@Test
	public void setNullStringParameter() {
		baseURL.setParameter("test", (String) null);
		Assert.assertEquals(baseURL.toString(), URL + "?");
	}

	@Test
	public void setStringParameter() {
		baseURL.setParameter("test", "1");
		Assert.assertEquals(baseURL.toString(), URL + "?test=1");
	}

	@Test
	public void setStringParameterAndRemoveParameter() {
		baseURL.setParameter("test", "1");
		baseURL.setParameter("test", (String) null);
		Assert.assertEquals(baseURL.toString(), URL + "?");
	}

	@Test
	public void setNullStringArrayParameter() {
		baseURL.setParameter("test", (String[]) null);
		Assert.assertEquals(baseURL.toString(), URL + "?");
	}

	@Test
	public void setStringArrayParameter() {
		baseURL.setParameter("test", new String[] { "2", "3" });
		Assert.assertEquals(baseURL.toString(), URL + "?test=2&test=3");
	}

	@Test
	public void write() throws IOException {
		StringWriter sw = new StringWriter();
		baseURL.setParameter("test", new String[] { "2", "3" });
		baseURL.write(sw);
		Assert.assertEquals(sw.toString(), URL + "?test=2&test=3");
	}

	@Test
	public void setParameters() throws IOException {
		StringWriter sw = new StringWriter();
		Map<String, String[]> p = new LinkedHashMap<>();
		p.put("test", new String[] { "2", "3" });
		baseURL.setParameters(p);
		baseURL.write(sw);
		Assert.assertEquals(sw.toString(), URL + "?test=2&test=3");
	}
}
