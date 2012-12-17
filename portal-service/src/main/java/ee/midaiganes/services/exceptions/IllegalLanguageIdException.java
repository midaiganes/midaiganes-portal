package ee.midaiganes.services.exceptions;

public class IllegalLanguageIdException extends Exception {
	private static final long serialVersionUID = 1L;

	public IllegalLanguageIdException(String languageId) {
		super("Illegal languageId '" + languageId + "'");
	}
}
