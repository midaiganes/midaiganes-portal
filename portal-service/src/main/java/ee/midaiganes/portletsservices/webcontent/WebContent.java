package ee.midaiganes.portletsservices.webcontent;

public class WebContent {
	private long id;
	private String defaultLanguageId;
	private String title;
	private long templateId;
	private long structureId;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDefaultLanguageId() {
		return defaultLanguageId;
	}

	public void setDefaultLanguageId(String defaultLanguageId) {
		this.defaultLanguageId = defaultLanguageId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(long templateId) {
		this.templateId = templateId;
	}

	public long getStructureId() {
		return structureId;
	}

	public void setStructureId(long structureId) {
		this.structureId = structureId;
	}
}