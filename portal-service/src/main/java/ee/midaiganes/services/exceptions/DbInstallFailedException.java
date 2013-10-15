package ee.midaiganes.services.exceptions;

public class DbInstallFailedException extends Exception {
	private static final long serialVersionUID = 1L;

	public DbInstallFailedException(Throwable t) {
		super(t);
	}
}
