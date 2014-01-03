package ee.midaiganes.util;

import java.io.IOException;
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
    long[] SUPERADMIN_USER_IDS = ArrayUtil.toPrimitiveLongArray(PropsUtil.getString(PropsKeys.SUPERADMIN_USER_IDS));
    boolean AUTODEPLOY_ENABLED = Boolean.parseBoolean(PropsUtil.getString(PropsKeys.AUTODEPLOY_ENABLED));
    boolean CACHE_DISABLED = Boolean.parseBoolean(PropsUtil.getString(PropsKeys.CACHE_DISABLED));
    String GUEST_GROUP_NAME = PropsUtil.getString(PropsKeys.GUEST_GROUP_NAME);
    String LOGGED_IN_GROUP_NAME = PropsUtil.getString(PropsKeys.LOGGED_IN_GROUP_NAME);
    String NOT_LOGGED_IN_GROUP_NAME = PropsUtil.getString(PropsKeys.NOT_LOGGED_IN_GROUP_NAME);
    String PERMISSIONS_RESOURCE_NAME = PropsUtil.getString(PropsKeys.PERMISSIONS_RESOURCE_NAME);
    String LOGIN_URL = PropsUtil.getString(PropsKeys.LOGIN_URL);

    interface PropsKeys {
        String PORTAL_CONTEXT = "portal.context";
        String AUTODEPLOY_DIR = "autodeploy.dir";
        String WEBAPPS_DIR = "webapps.dir";
        String AUTODEPLOY_ENABLED = "autodeploy.enabled";
        String SUPERADMIN_USER_IDS = "superadmin.user.ids";
        String CACHE_DISABLED = "cache.disabled";
        String GUEST_GROUP_NAME = "guest.group.name";
        String LOGGED_IN_GROUP_NAME = "logged.in.group.name";
        String NOT_LOGGED_IN_GROUP_NAME = "not.logged.in.group.name";
        String PERMISSIONS_RESOURCE_NAME = "permissions.resource.name";
        String LOGIN_URL = "login.url";
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
    }
}
