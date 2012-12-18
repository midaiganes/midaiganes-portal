package ee.midaiganes.portletsservices.webcontent;

public class WebContentField {
	private long id;
	private long webContentId;
	private String languageId;
	private long structureId;
	private long structureFieldId;
	private String fieldValue;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getWebContentId() {
		return webContentId;
	}

	public void setWebContentId(long webContentId) {
		this.webContentId = webContentId;
	}

	public String getLanguageId() {
		return languageId;
	}

	public void setLanguageId(String languageId) {
		this.languageId = languageId;
	}

	public long getStructureId() {
		return structureId;
	}

	public void setStructureId(long structureId) {
		this.structureId = structureId;
	}

	public long getStructureFieldId() {
		return structureFieldId;
	}

	public void setStructureFieldId(long structureFieldId) {
		this.structureFieldId = structureFieldId;
	}

	public String getFieldValue() {
		return fieldValue;
	}

	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}
}