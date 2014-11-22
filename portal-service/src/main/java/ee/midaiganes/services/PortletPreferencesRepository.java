package ee.midaiganes.services;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import ee.midaiganes.cache.Element;
import ee.midaiganes.cache.SingleVmCache;
import ee.midaiganes.cache.SingleVmPoolUtil;
import ee.midaiganes.services.rowmapper.PortletPreferencesResultSetExtractor;
import ee.midaiganes.util.StringPool;
import ee.midaiganes.util.StringUtil;

public class PortletPreferencesRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String GET_PORTLET_PREFERENCES = "SELECT pp.preferenceName, ppv.preferenceValue FROM PortletPreference pp JOIN PortletPreferenceValue ppv ON (pp.id = ppv.portletPreferenceId) WHERE pp.portletInstanceId = ?";
    private static final String INSERT_INTO_PORTLETPREFERENCE = "INSERT INTO PortletPreference(portletInstanceId, preferenceName) VALUES(?, ?)";
    private static final String INSERT_INTO_PORTLETPREFERENCEVALUES = "INSERT INTO PortletPreferenceValue (portletPreferenceId, preferenceValue) VALUES ((SELECT id FROM PortletPreference WHERE preferenceName = ? AND portletInstanceId = ?), ?)";

    private final SingleVmCache cache = SingleVmPoolUtil.getCache(PortletPreferencesRepository.class.getName());
    private static final PortletPreferencesResultSetExtractor getPortletPreferencesExtractor = new PortletPreferencesResultSetExtractor();

    @Inject
    public PortletPreferencesRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public Map<String, String[]> getPortletPreferences(long portletInstanceId) {
        String key = Long.toString(portletInstanceId);
        Element element = cache.getElement(key);
        if (element != null) {
            return element.get();
        }

        Map<String, String[]> preferences = null;
        try {
            preferences = loadPortletPreferences(portletInstanceId);
        } finally {
            cache.put(key, preferences);
        }
        return preferences;
    }

    @Transactional
    public void savePortletPreferences(long portletInstanceId, Map<String, String[]> preferences) {
        List<String> keys = new ArrayList<>(preferences.keySet());
        List<String[]> values = new ArrayList<>();
        for (String key : keys) {
            for (String k : preferences.get(key)) {
                values.add(new String[] { key, k });
            }
        }
        try {
            savePortletPreferences(portletInstanceId, keys, values);
        } finally {
            cache.remove(Long.toString(portletInstanceId));
        }
    }

    private Map<String, String[]> loadPortletPreferences(long portletInstanceId) {
        return jdbcTemplate.query(GET_PORTLET_PREFERENCES, getPortletPreferencesExtractor, Long.valueOf(portletInstanceId));
    }

    private void savePortletPreferences(final long portletInstanceId, final List<String> keys, final List<String[]> values) {
        final int keysSize = keys.size();
        if (!keys.isEmpty()) {
            Object[] arguments = new Object[keysSize + 1];
            arguments[0] = Long.valueOf(portletInstanceId);
            for (int i = 0; i < keysSize; i++) {
                arguments[1 + i] = keys.get(i);
            }
            jdbcTemplate.update(
                    "DELETE FROM PortletPreference WHERE portletInstanceId = ? AND preferenceName IN (" + StringUtil.repeat(StringPool.QUESTION, StringPool.COMMA, keysSize) + ")",
                    arguments);

        }
        jdbcTemplate.batchUpdate(INSERT_INTO_PORTLETPREFERENCE, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, portletInstanceId);
                ps.setString(2, keys.get(i));
            }

            @Override
            public int getBatchSize() {
                return keysSize;
            }
        });

        jdbcTemplate.batchUpdate(INSERT_INTO_PORTLETPREFERENCEVALUES, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, values.get(i)[0]);
                ps.setLong(2, portletInstanceId);
                ps.setString(3, values.get(i)[1]);
            }

            @Override
            public int getBatchSize() {
                return values.size();
            }
        });
    }
}
