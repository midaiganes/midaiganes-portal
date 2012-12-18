package ee.midaiganes.portletsservices.webcontent;

public class Template {
	private long id;
	private long structureId;
	private String name;
	private String templateContent;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getStructureId() {
		return structureId;
	}

	public void setStructureId(long structureId) {
		this.structureId = structureId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTemplateContent() {
		return templateContent;
	}

	public void setTemplateContent(String templateContent) {
		this.templateContent = templateContent;
	}
}