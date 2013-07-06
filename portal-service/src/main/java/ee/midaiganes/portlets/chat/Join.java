package ee.midaiganes.portlets.chat;

import ee.midaiganes.model.User;

public class Join implements Command {
	private static final String CMD = "join";
	private final long userId;
	private final String username;

	public Join(User user) {
		userId = user.getId();
		username = user.getUsername();
	}

	@Override
	public String getCmd() {
		return CMD;
	}

	public long getUserId() {
		return userId;
	}

	public String getUsername() {
		return username;
	}

	public static boolean isSelfJoin(Command cmd, long userId) {
		return CMD.equals(cmd.getCmd()) && ((Join) cmd).getUserId() == userId;
	}
}
