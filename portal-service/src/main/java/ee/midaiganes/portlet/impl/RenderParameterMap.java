package ee.midaiganes.portlet.impl;

import java.util.HashMap;
import java.util.Map;

public class RenderParameterMap extends HashMap<String, String[]> {
	private static final long serialVersionUID = 1L;

	public String[] put(String key, String value) {
		if (value == null) {
			return remove(key);
		} else {
			return super.put(key, new String[] { value });
		}
	}

	@Override
	public String[] put(String key, String[] value) {
		if (value == null) {
			return remove(key);
		} else {
			String[] vals = new String[value.length];
			System.arraycopy(value, 0, vals, 0, value.length);
			return super.put(key, vals);
		}
	}

	@Override
	public void putAll(Map<? extends String, ? extends String[]> map) {
		if (map == null) {
			throw new IllegalArgumentException();
		}
		for (Map.Entry<? extends String, ? extends String[]> entry : map.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	public Map<String, String[]> getCopy() {
		Map<String, String[]> map = new HashMap<String, String[]>();
		for (Map.Entry<String, String[]> entry : this.entrySet()) {
			String[] vals = new String[entry.getValue().length];
			System.arraycopy(entry.getValue(), 0, vals, 0, vals.length);
			map.put(entry.getKey(), vals);
		}
		return map;
	}

}
