package ee.midaiganes.util;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface PropsValues {
	String PORTAL_PROPERTIES = "/portal.properties";
	String PORTAL_CONTEXT = PropsUtil.getString(PropsKeys.PORTAL_CONTEXT);
	String AUTODEPLOY_DIR = PropsUtil.getString(PropsKeys.AUTODEPLOY_DIR);
	String WEBAPPS_DIR = PropsUtil.getString(PropsKeys.WEBAPPS_DIR);
	boolean AUTODEPLOY_ENABLED = Boolean.parseBoolean(PropsUtil.getString(PropsKeys.AUTODEPLOY_ENABLED));

	interface PropsKeys {
		String PORTAL_CONTEXT = "portal.context";
		String AUTODEPLOY_DIR = "autodeploy.dir";
		String WEBAPPS_DIR = "webapps.dir";
		String AUTODEPLOY_ENABLED = "autodeploy.enabled";
	}

	static class PropsUtil {
		private static final Logger log = LoggerFactory.getLogger(PropsUtil.class);
		private static final Pattern pattern = Pattern.compile("^.*?\\$\\{([a-zA-Z\\.]*)\\}.*?$", Pattern.MULTILINE | Pattern.DOTALL);
		private static final String PREFIX = "${";
		private static final String SUFFIX = "}";
		private static ConcurrentHashMap<String, String> properties;

		static {
			properties = loadProperties();
		}

		private static ConcurrentHashMap<String, String> loadProperties() {
			Properties properties = new Properties();
			try (InputStreamReader reader = new InputStreamReader(PropsUtil.class.getResourceAsStream(PropsValues.PORTAL_PROPERTIES), CharsetPool.UTF_8)) {
				properties.load(reader);
				return replaceProperties(properties);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		private static ConcurrentHashMap<String, String> replaceProperties(Properties properties) {
			ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
			for (Object key : properties.keySet()) {
				String value = getPropertyValue((String) key, properties, new ArrayList<String>());
				log.debug("property key = '{}'; value = '{}'", key, value);
				map.put((String) key, value);
			}
			return map;
		}

		private static String getPropertyValue(String key, Properties props, List<String> keys) {
			String value = props.getProperty(key);
			if (value == null) {
				return value;
			}
			keys.add(key);
			while (true) {
				Matcher m = pattern.matcher(value);
				if (m.matches()) {
					String k = m.group(1);
					if (keys.contains(k)) {
						log.warn("found recursion in properties: {}", keys);
						break;
					}
					String vv = getPropertyValue(k, props, keys);
					value = value.replace(PREFIX + k + SUFFIX, vv == null ? StringPool.EMPTY : vv);
					continue;
				}
				break;
			}
			keys.remove(key);
			return value;
		}

		private static String getString(String key) {
			return properties.get(key);
		}
	}
}
