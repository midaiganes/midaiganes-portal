package ee.midaiganes.services;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ee.midaiganes.model.PortletName;
import ee.midaiganes.services.SingleVmPool.Cache;
import ee.midaiganes.services.rowmapper.PortletPreferencesResultSetExtractor;
import ee.midaiganes.util.StringPool;
import ee.midaiganes.util.StringUtil;

public class PortletPreferencesRepository {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	private static final String GET_PORTLET_PREFERENCES = "SELECT pp.preferenceName, ppv.preferenceValue FROM PortletInstance pi JOIN PortletPreference pp ON (pi.id = pp.portletInstanceId) JOIN PortletPreferenceValue ppv ON (pp.id = ppv.portletPreferenceId) WHERE portletName = ? AND windowID = ? AND portletContext = ?";
	private static final String INSERT_INTO_PORTLETPREFERENCE = "INSERT INTO PortletPreference(portletInstanceId, preferenceName) VALUES((SELECT id FROM PortletInstance WHERE portletName = ? AND windowID = ? AND portletContext = ?), ?)";
	private static final String INSERT_INTO_PORTLETPREFERENCEVALUES = "INSERT INTO PortletPreferenceValue (portletPreferenceId, preferenceValue) VALUES ((SELECT id FROM PortletPreference WHERE preferenceName = ? AND portletInstanceId = (SELECT id FROM portletInstance WHERE portletName = ? AND windowID = ? AND portletContext = ?)), ?)";

	private final Cache cache = SingleVmPool.getCache(PortletPreferencesRepository.class.getName());
	private static final PortletPreferencesResultSetExtractor getPortletPreferencesExtractor = new PortletPreferencesResultSetExtractor();

	public Map<String, String[]> getPortletPreferences(PortletName portletName, String windowID) {
		String key = portletName.getFullName() + "#" + windowID;
		SingleVmPool.Cache.Element element = cache.getElement(key);
		if (element != null) {
			return element.get();
		}
		Map<String, String[]> preferences = loadPortletPreferences(portletName, windowID);
		cache.put(key, preferences);
		return preferences;
	}

	private Map<String, String[]> loadPortletPreferences(PortletName portletName, String windowID) {
		return jdbcTemplate.query(GET_PORTLET_PREFERENCES, getPortletPreferencesExtractor, portletName.getName(), windowID, portletName.getContext());
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, isolation = Isolation.DEFAULT)
	public void savePortletPreferences(final PortletName portletName, final String windowID, Map<String, String[]> preferences) {
		List<String> keys = new ArrayList<String>(preferences.keySet());
		List<String[]> values = new ArrayList<String[]>();
		for (String key : keys) {
			for (String k : preferences.get(key)) {
				values.add(new String[] { key, k });
			}
		}
		try {
			savePortletPreferences(portletName, windowID, keys, values);
		} finally {
			cache.remove(portletName.getFullName() + "#" + windowID);
		}
	}

	private void savePortletPreferences(final PortletName portletName, final String windowID, final List<String> keys, final List<String[]> values) {
		if (keys.size() > 0) {
			Object[] arguments = new String[keys.size() + 3];
			arguments[0] = portletName.getName();
			arguments[1] = windowID;
			arguments[2] = portletName.getContext();
			for (int i = 0; i < keys.size(); i++) {
				arguments[3 + i] = keys.get(i);
			}
			jdbcTemplate
					.update("DELETE FROM PortletPreference WHERE portletInstanceId = (SELECT id FROM PortletInstance WHERE portletName = ? AND windowID = ? AND portletContext = ?) AND preferenceName IN ("
							+ StringUtil.repeat(StringPool.QUESTION, StringPool.COMMA, keys.size()) + ")", arguments);

		}
		jdbcTemplate.batchUpdate(INSERT_INTO_PORTLETPREFERENCE, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setString(1, portletName.getName());
				ps.setString(2, windowID);
				ps.setString(3, portletName.getContext());
				ps.setString(4, keys.get(i));
			}

			@Override
			public int getBatchSize() {
				return keys.size();
			}
		});

		jdbcTemplate.batchUpdate(INSERT_INTO_PORTLETPREFERENCEVALUES, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setString(1, values.get(i)[0]);
				ps.setString(2, portletName.getName());
				ps.setString(3, windowID);
				ps.setString(4, portletName.getContext());
				ps.setString(5, values.get(i)[1]);
			}

			@Override
			public int getBatchSize() {
				return values.size();
			}
		});
	}
}
