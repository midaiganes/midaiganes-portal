package ee.midaiganes.portletsservices.webcontent;

public class StructureField {
	private long id;
	private long structureId;
	private String fieldName;
	private String fieldType;

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

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}
}