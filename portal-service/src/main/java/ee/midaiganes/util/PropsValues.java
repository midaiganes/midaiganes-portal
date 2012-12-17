package ee.midaiganes.util;

import java.io.InputStreamReader;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public interface PropsValues {
	String PORTAL_PROPERTIES = "/META-INF/portal.properties";
	String PORTAL_CONTEXT = PropsUtil.getString(PropsKeys.PORTAL_CONTEXT);

	interface PropsKeys {
		String PORTAL_CONTEXT = "portal.context";
	}

	static class PropsUtil {
		private static ConcurrentHashMap<Object, Object> properties;
		static {
			properties = loadProperties();
		}

		private static ConcurrentHashMap<Object, Object> loadProperties() {
			Properties properties = new Properties();
			try (InputStreamReader reader = new InputStreamReader(PropsUtil.class.getResourceAsStream(PropsValues.PORTAL_PROPERTIES), "UTF-8")) {
				properties.load(reader);
				return new ConcurrentHashMap<>(properties);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		private static String getString(String key) {
			return (String) properties.get(key);
		}
	}
}
