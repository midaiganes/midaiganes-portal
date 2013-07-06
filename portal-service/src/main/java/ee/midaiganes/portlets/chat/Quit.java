package ee.midaiganes.portlets.chat;

public class Quit implements Command {
	private final long userId;

	public Quit(long userId) {
		this.userId = userId;
	}

	public long getUserId() {
		return userId;
	}

	@Override
	public String getCmd() {
		return "quit";
	}
}
