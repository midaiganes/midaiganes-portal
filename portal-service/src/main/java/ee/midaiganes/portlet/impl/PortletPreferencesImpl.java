package ee.midaiganes.portlet.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletPreferences;
import javax.portlet.ReadOnlyException;
import javax.portlet.ValidatorException;

import ee.midaiganes.services.PortletPreferencesRepository;

public class PortletPreferencesImpl implements PortletPreferences {

	private final Map<String, String[]> preferences;
	private final PortletPreferencesRepository portletPreferencesRepository;

	private final long portletInstanceId;

	public PortletPreferencesImpl(long portletInstanceId, PortletPreferencesRepository portletPreferencesRepository) {
		this.portletPreferencesRepository = portletPreferencesRepository;
		this.portletInstanceId = portletInstanceId;
		this.preferences = portletPreferencesRepository.getPortletPreferences(portletInstanceId);
	}

	@Override
	public boolean isReadOnly(String key) {
		// TODO Auto-generated method stub
		throw new RuntimeException("not implemented");
	}

	@Override
	public String getValue(String key, String def) {
		String[] values = preferences.get(key);
		if (values != null) {
			return values[0];
		}
		return def;
	}

	@Override
	public String[] getValues(String key, String[] def) {
		String[] values = preferences.get(key);
		if (values != null) {
			return values.clone();
		}
		return def;
	}

	@Override
	public void setValue(String key, String value) throws ReadOnlyException {
		if (value == null) {
			reset(key);
		} else {
			setValues(key, new String[] { value });
		}
	}

	@Override
	public void setValues(String key, String[] values) throws ReadOnlyException {
		if (values == null || values.length == 0) {
			reset(key);
		} else {
			preferences.put(key, values);
		}
	}

	@Override
	public Enumeration<String> getNames() {
		return Collections.enumeration(preferences.keySet());
	}

	@Override
	public Map<String, String[]> getMap() {
		return cloneMap(preferences);
	}

	@Override
	public void reset(String key) throws ReadOnlyException {
		preferences.remove(key);
	}

	@Override
	public void store() throws IOException, ValidatorException {
		portletPreferencesRepository.savePortletPreferences(portletInstanceId, preferences);
	}

	private Map<String, String[]> cloneMap(Map<String, String[]> map) {
		Map<String, String[]> clone = new CloneHashMap();
		for (Map.Entry<String, String[]> entry : map.entrySet()) {
			clone.put(entry.getKey(), entry.getValue().clone());
		}
		return clone;
	}

	private static class CloneHashMap extends HashMap<String, String[]> {
		private static final long serialVersionUID = 1L;

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder("PortletPreferences.Map [");
			for (Map.Entry<String, String[]> e : this.entrySet()) {
				sb.append(e.getKey() + "=" + Arrays.toString(e.getValue()) + ";");
			}
			sb.append("]");
			return sb.toString();
		}
	}
}
