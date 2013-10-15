package ee.midaiganes.model;

import java.io.Serializable;

public class LayoutTitle implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final long DEFAULT_LAYOUT_TITLE_ID = 0;
	private final long id;
	private final long layoutId;
	private final long languageId;
	private final String title;

	public LayoutTitle(long id, long layoutId, long languageId, String title) {
		this.id = id;
		this.layoutId = layoutId;
		this.languageId = languageId;
		this.title = title;
	}

	private LayoutTitle(long layoutId, long languageId, String title) {
		this.id = DEFAULT_LAYOUT_TITLE_ID;
		this.layoutId = layoutId;
		this.languageId = languageId;
		this.title = title;
	}

	public static LayoutTitle getDefault(long layoutId, long languageId, String title) {
		return new LayoutTitle(layoutId, languageId, title);
	}

	public long getId() {
		return id;
	}

	public long getLayoutId() {
		return layoutId;
	}

	public long getLanguageId() {
		return languageId;
	}

	public String getTitle() {
		return title;
	}

	@Override
	public String toString() {
		return "LayoutTitle [id=" + id + ", layoutId=" + layoutId + ", languageId=" + languageId + ", title=" + title + "]";
	}
}
