package ee.midaiganes.model;

import java.io.Serializable;

public class LayoutTitle implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id;
	private long layoutId;
	private String languageId;
	private String title;

	public LayoutTitle() {
	}

	public LayoutTitle(long layoutId, String languageId, String title) {
		this.layoutId = layoutId;
		this.languageId = languageId;
		this.title = title;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getLayoutId() {
		return layoutId;
	}

	public void setLayoutId(long layoutId) {
		this.layoutId = layoutId;
	}

	public String getLanguageId() {
		return languageId;
	}

	public void setLanguageId(String languageId) {
		this.languageId = languageId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return "LayoutTitle [id=" + id + ", layoutId=" + layoutId + ", languageId=" + languageId + ", title=" + title + "]";
	}
}
