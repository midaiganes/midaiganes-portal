package ee.midaiganes.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;

public final class PropsValues {
    public static final String PORTAL_PROPERTIES = "/portal.properties";
    public static final String PORTAL_CONTEXT = PropsUtil.getString(PropsKeys.PORTAL_CONTEXT);
    public static final String AUTODEPLOY_DIR = PropsUtil.getString(PropsKeys.AUTODEPLOY_DIR);
    public static final String WEBAPPS_DIR = PropsUtil.getString(PropsKeys.WEBAPPS_DIR);
    @Nonnull
    public static final long[] SUPERADMIN_USER_IDS = ArrayUtil.toPrimitiveLongArray(PropsUtil.getString(PropsKeys.SUPERADMIN_USER_IDS));
    public static final boolean AUTODEPLOY_ENABLED = Boolean.parseBoolean(PropsUtil.getString(PropsKeys.AUTODEPLOY_ENABLED));
    public static final boolean CACHE_DISABLED = Boolean.parseBoolean(PropsUtil.getString(PropsKeys.CACHE_DISABLED));
    public static final String GUEST_GROUP_NAME = PropsUtil.getString(PropsKeys.GUEST_GROUP_NAME);
    public static final String LOGGED_IN_GROUP_NAME = PropsUtil.getString(PropsKeys.LOGGED_IN_GROUP_NAME);
    public static final String NOT_LOGGED_IN_GROUP_NAME = PropsUtil.getString(PropsKeys.NOT_LOGGED_IN_GROUP_NAME);
    @Nonnull
    public static final String PERMISSIONS_RESOURCE_NAME = PropsUtil.getNonnullString(PropsKeys.PERMISSIONS_RESOURCE_NAME);
    public static final String LOGIN_URL = PropsUtil.getString(PropsKeys.LOGIN_URL);

    public static class PropsUtil {
        private static final Logger log = LoggerFactory.getLogger(PropsUtil.class);
        private static final Pattern pattern = Pattern.compile("^.*?\\$\\{([a-zA-Z\\.]*)\\}.*?$", Pattern.MULTILINE | Pattern.DOTALL);
        private static final String PREFIX = "${";
        private static final String SUFFIX = "}";
        public static final ConcurrentHashMap<String, String> properties;

        static {
            properties = loadProperties();
        }

        private static ConcurrentHashMap<String, String> loadProperties() {
            Properties properties = new Properties(System.getProperties());
            try (InputStreamReader reader = new InputStreamReader(new BufferedInputStream(PropsUtil.class.getResourceAsStream(PropsValues.PORTAL_PROPERTIES)), Charsets.UTF_8)) {
                properties.load(reader);
                return replaceProperties(properties);
            } catch (IOException e) {
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

        @Nonnull
        private static String getNonnullString(String key) {
            String value = getString(key);
            if (value == null) {
                throw new IllegalStateException("Property '" + key + "' value is null.");
            }
            return value;
        }
    }
}
