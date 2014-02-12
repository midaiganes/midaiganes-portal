package ee.midaiganes.portlets.chat;

import java.util.ArrayList;
import java.util.List;

import ee.midaiganes.portal.user.User;
import ee.midaiganes.portlets.chat.ChatCmd.MsgChatCmd;
import ee.midaiganes.util.TimeProviderUtil;

public class ChatMessage {
    private final User from;
    private final ArrayList<User> usersToSend;
    private final ChatCmd<?> chatCmd;
    private final long creationTime;
    private static final long ONE_MINUTE_IN_MILLIS = 60 * 1000;

    public ChatMessage(List<User> usersToSend, User from, String msg) {
        this(usersToSend, from, new MsgChatCmd(msg, from));
    }

    public ChatMessage(List<User> usersToSend, User from, ChatCmd<?> chatCmd) {
        this.usersToSend = new ArrayList<>(usersToSend);
        this.from = from;
        this.chatCmd = chatCmd;
        this.creationTime = TimeProviderUtil.currentTimeMillis();
    }

    public boolean isTimedOut(long currentTimeMillis) {
        return creationTime + ONE_MINUTE_IN_MILLIS < currentTimeMillis;
    }

    public User getFrom() {
        return from;
    }

    public ChatCmd<?> getChatCmd() {
        return chatCmd;
    }

    public List<User> getUsersToSend() {
        return usersToSend;
    }
}
