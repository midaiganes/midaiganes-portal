package ee.midaiganes.portletsservices.webcontent;

public class InvalidStructureFieldTypeException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InvalidStructureFieldTypeException(String fieldType) {
		super("invalid structure field type '" + fieldType + "'");
	}
}