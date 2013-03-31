package ee.midaiganes.model;

import java.io.Serializable;

public class LayoutTitle implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id;
	private long layoutId;
	private long languageId;
	private String title;

	public LayoutTitle(LayoutTitle layoutTitle) {
		id = layoutTitle.id;
		layoutId = layoutTitle.layoutId;
		languageId = layoutTitle.languageId;
		title = layoutTitle.title;
	}

	public LayoutTitle() {
	}

	public LayoutTitle(long layoutId, long languageId, String title) {
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

	public long getLanguageId() {
		return languageId;
	}

	public void setLanguageId(long languageId) {
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
