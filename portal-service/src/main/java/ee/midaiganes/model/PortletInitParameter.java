package ee.midaiganes.model;

import java.io.Serializable;
import java.util.List;

public class PortletInitParameter implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private String value;
	private List<Description> description;

	public PortletInitParameter(String id, String name, String value, List<Description> description) {
		this.id = id;
		this.name = name;
		this.value = value;
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public List<Description> getDescription() {
		return description;
	}

	public void setDescription(List<Description> description) {
		this.description = description;
	}

	public static class Description implements Serializable {
		private static final long serialVersionUID = 1L;
		private String lang;
		private String value;

		public Description(String lang, String value) {
			this.lang = lang;
			this.value = value;
		}

		public String getLang() {
			return lang;
		}

		public void setLang(String lang) {
			this.lang = lang;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}
}
