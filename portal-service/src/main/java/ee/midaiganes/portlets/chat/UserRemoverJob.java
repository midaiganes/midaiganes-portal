package ee.midaiganes.portlets.chat;

import ee.midaiganes.services.JobSchedulingService.AbstractIntervalJob;

public class UserRemoverJob extends AbstractIntervalJob {
	private final Chats chats;

	public UserRemoverJob(Chats chats) {
		this.chats = chats;
	}

	@Override
	public void run() {
		chats.removeTimedOutUsers(ChatUser.getMinLastActiveTime());
	}

	@Override
	public long getIntervalInMillis() {
		return 5000;
	}

}