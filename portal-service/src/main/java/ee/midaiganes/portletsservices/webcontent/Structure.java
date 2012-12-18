package ee.midaiganes.portletsservices.webcontent;

import java.util.List;

public class Structure {
	private long id;
	private String name;
	private List<StructureField> structureFields;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<StructureField> getStructureFields() {
		return structureFields;
	}

	public void setStructureFields(List<StructureField> structureFields) {
		this.structureFields = structureFields;
	}
}