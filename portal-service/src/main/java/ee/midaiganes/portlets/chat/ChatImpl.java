package ee.midaiganes.portlets.chat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.midaiganes.portal.user.User;
import ee.midaiganes.portlets.chat.Chat.AddUserToChatResponse.AddUserToChatResponseStatus;
import ee.midaiganes.portlets.chat.Chat.SendAndRemoveUserChatMessages.SendAndRemoveUserChatMessagesStatus;
import ee.midaiganes.portlets.chat.Chat.SendAndRemoveUserChatMessagesRequest.AsyncCallback;
import ee.midaiganes.portlets.chat.Chat.SetUsersPublicInChatResponse.SetUsersPublicInChatResponseStatus;
import ee.midaiganes.portlets.chat.ChatCmd.JoinChatCmd;
import ee.midaiganes.portlets.chat.ChatCmd.PrivMsgChatCmd;
import ee.midaiganes.portlets.chat.ChatCmd.UserLeftPrivateChatCmd;
import ee.midaiganes.util.Pair;
import ee.midaiganes.util.ThreadUtil;
import ee.midaiganes.util.TimeProviderUtil;
import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.map.hash.TLongObjectHashMap;

public class ChatImpl implements Chat {
    private static final Logger log = LoggerFactory.getLogger(ChatImpl.class);
    private static final int MAX_USERS = 100;
    private static final long USER_CALLBACKS_WAIT_TIME_IN_MILLIS = 5000;
    private final long id;
    private final ArrayList<ChatUser> chatUsers = new ArrayList<>(MAX_USERS);
    private final ArrayList<ChatMessage> cmds = new ArrayList<>();
    private final ArrayList<Pair<User, User>> privateChats = new ArrayList<>();
    private final TLongObjectHashMap<AsyncCallback> userCallbacks = new TLongObjectHashMap<>();
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private final AtomicBoolean running = new AtomicBoolean(true);

    public ChatImpl(long id) {
        this.id = id;
        startUserCallbackThread();
    }

    @Override
    public void destroy() {
        lock.lock();
        try {
            running.set(false);
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int getNumberOfUsers() {
        try {
            lock.lock();
            return chatUsers.size();
        } finally {
            lock.unlock();
        }
    }

    private boolean isChatFull() {
        return chatUsers.size() == MAX_USERS;
    }

    @Override
    public AddUserToChatResponse addUserToChat(User user) {
        try {
            lock.lock();
            if (isChatFull()) {
                return new AddUserToChatResponse(AddUserToChatResponseStatus.CHAT_IS_FULL);
            }
            if (isUserInChat(user.getId())) {
                setUserPublicInChatWithoutLocking(user);
                return new AddUserToChatResponse(AddUserToChatResponseStatus.USER_ALREADY_IN_CHAT, getUsers());
            }
            ArrayList<User> usersWhoAreNotInPrivateChat = getUsersWhoAreNotInPrivateChat();
            chatUsers.add(new ChatUser(user));
            addCmds(new ChatMessage(usersWhoAreNotInPrivateChat, user, new JoinChatCmd(user)));
            return new AddUserToChatResponse(AddUserToChatResponseStatus.SUCCESS, getUsers());
        } finally {
            lock.unlock();
        }
    }

    private void addCmds(ChatMessage cm) {
        log.debug("Add command: '{}'", cm);
        cmds.add(cm);
        condition.signalAll();
    }

    private boolean isUserInChat(long userId) {
        for (ChatUser u : chatUsers) {
            if (u.getUser().getId() == userId) {
                return true;
            }
        }
        return false;
    }

    @Override
    public SendMessageToChat sendMessageToChat(User user, String msg) {
        try {
            lock.lock();
            long userId = user.getId();
            if (isUserInChat(userId)) {
                Pair<User, User> pair = getPrivatePair(userId);
                if (pair == null) {
                    addCmds(new ChatMessage(getUsersWhoAreNotInPrivateChat(), user, msg));
                } else {
                    User chatPartner = pair.getFirst().getId() == userId ? pair.getSecond() : pair.getFirst();
                    addCmds(new ChatMessage(Arrays.asList(chatPartner, user), user, msg));
                }
                return SendMessageToChat.SUCCESS;
            }
            return SendMessageToChat.USER_NOT_IN_CHAT;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public SendPrivateMessageToUserResponse sendPrivateMessageToUser(User from, User to, String msg) {
        lock.lock();
        try {
            if (isUserInChat(from.getId()) && isUserInChat(to.getId())) {
                if (!isAtLeastOneInPrivateChat(new long[] { from.getId(), to.getId() })) {
                    List<User> usersToSend = to.getId() == from.getId() ? Arrays.asList(from) : Arrays.asList(to, from);
                    addCmds(new ChatMessage(usersToSend, from, new PrivMsgChatCmd(msg, from.getId())));
                    return SendPrivateMessageToUserResponse.SUCCESS;
                }
                return SendPrivateMessageToUserResponse.AT_LEAST_ONE_USER_IS_IN_PRIVATE_CHAT;
            }
            return SendPrivateMessageToUserResponse.USER_NOT_IN_CHAT;
        } finally {
            lock.unlock();
        }
    }

    private static final class MessageSender implements Runnable {
        private final AsyncCallback callback;
        private final ChatCmds cmds;
        private final boolean timeout;

        private MessageSender(AsyncCallback callback, ChatCmds cmds) {
            this.callback = callback;
            this.cmds = cmds;
            this.timeout = false;
        }

        private MessageSender(AsyncCallback callback) {
            this.callback = callback;
            this.cmds = null;
            this.timeout = true;
        }

        @Override
        public void run() {
            if (timeout) {
                callback.timeout();
            } else {
                if (cmds != null) {
                    callback.call(cmds);
                } else {
                    callback.userNotInChat();
                }
            }
        }
    }

    private void startUserCallbackThread() {
        new Thread(new Runnable() {
            private void removeTimedOutUsers(ArrayList<ChatUser> chatUsers) {
                long currentTimeMillis = TimeProviderUtil.currentTimeMillis();
                for (int i = chatUsers.size() - 1; i >= 0; i--) {
                    if (chatUsers.get(i).isTimedOut(currentTimeMillis)) {
                        // maybe it is faster to create new list?
                        chatUsers.remove(i);
                    }
                }
            }

            private void doInLock() {
                removeTimedOutUsers(chatUsers);
                if (!userCallbacks.isEmpty()
                // && !cmds.isEmpty()
                ) {
                    long currentTimeInMillis = System.currentTimeMillis();
                    for (TLongObjectIterator<AsyncCallback> iter = userCallbacks.iterator(); iter.hasNext();) {
                        iter.advance();
                        AsyncCallback asyncCallback = iter.value();
                        long userId = iter.key();
                        List<ChatCmd<?>> commands = getAndRemoveUserChatMessagesWithoutLock(userId);
                        if (!commands.isEmpty()) {
                            iter.remove();
                            ThreadUtil.execute(new MessageSender(asyncCallback, isUserInChat(userId) ? new ChatCmds(commands) : null));
                        } else if (asyncCallback.isTimedOut(currentTimeInMillis)) {
                            iter.remove();
                            ThreadUtil.execute(new MessageSender(asyncCallback));
                        }
                    }
                }
            }

            @Override
            public void run() {
                while (running.get()) {
                    try {
                        lock.lock();
                        doInLock();
                        condition.await(USER_CALLBACKS_WAIT_TIME_IN_MILLIS, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                        log.warn(e.getMessage(), e);
                    } catch (RuntimeException e) {
                        log.error(e.getMessage(), e);
                    } finally {
                        lock.unlock();
                    }
                }
                log.info("Chat " + id + " destroyed");
            }
        }, ChatImpl.class.getName() + ".USER_CALLBACKS_" + id).start();
    }

    @Override
    public SendAndRemoveUserChatMessages sendAndRemoveUserChatMessages(SendAndRemoveUserChatMessagesRequest request) {
        try {
            lock.lock();
            long userId = request.getUser().getId();
            if (!isUserInChat(userId)) {
                return new SendAndRemoveUserChatMessages(SendAndRemoveUserChatMessagesStatus.USER_NOT_IN_CHAT);
            }
            updateUserActiveTime(userId);
            List<ChatCmd<?>> commands = getAndRemoveUserChatMessagesWithoutLock(userId);
            if (commands.isEmpty()) {
                userCallbacks.put(userId, request.getCallback().getAsyncCallback());
                return new SendAndRemoveUserChatMessages(SendAndRemoveUserChatMessagesStatus.SUCCESS_WAITING);
            }
            return new SendAndRemoveUserChatMessages(SendAndRemoveUserChatMessagesStatus.SUCCESS, new ChatCmds(commands));
        } finally {
            lock.unlock();
        }
    }

    private void updateUserActiveTime(long userId) {
        for (ChatUser u : this.chatUsers) {
            if (u.getUser().getId() == userId) {
                u.updateActiveTime(TimeProviderUtil.currentTimeMillis());
                return;
            }
        }
    }

    private List<ChatCmd<?>> getAndRemoveUserChatMessagesWithoutLock(long userId) {
        if (!cmds.isEmpty()) {
            ArrayList<ChatCmd<?>> chatCommands = new ArrayList<>();
            Iterator<ChatMessage> cmdsIterator = cmds.iterator();
            long currentTimeMillis = TimeProviderUtil.currentTimeMillis();
            while (cmdsIterator.hasNext()) {
                ChatMessage cm = cmdsIterator.next();
                if (cm.isTimedOut(currentTimeMillis)) {
                    cm.getUsersToSend().clear();// clear users...
                    cmdsIterator.remove();
                } else {
                    Iterator<User> userIterator = cm.getUsersToSend().iterator();
                    while (userIterator.hasNext()) {
                        if (userId == userIterator.next().getId()) {
                            chatCommands.add(cm.getChatCmd());
                            userIterator.remove();
                            break;
                        }
                    }
                    if (cm.getUsersToSend().isEmpty()) {
                        cmdsIterator.remove();
                    }
                }
            }
            return chatCommands;
        }
        return Collections.emptyList();
    }

    @Override
    public SetUsersPriveInChatResponse setUsersPrivateInChat(User user1, User user2) {
        try {
            lock.lock();
            if (isUserInChat(user1.getId()) && isUserInChat(user2.getId())) {
                if (!isAtLeastOneInPrivateChat(new long[] { user1.getId(), user2.getId() })) {
                    privateChats.add(new Pair<>(user1, user2));
                    return SetUsersPriveInChatResponse.SUCCESS;
                }
                return SetUsersPriveInChatResponse.USER_ALREADY_IN_PRIVATE_CHAT;
            }
            return SetUsersPriveInChatResponse.USER_NOT_IN_CHAT;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public SetUsersPublicInChatResponse setUserPublicInChat(User currentUser) {
        try {
            lock.lock();
            return setUserPublicInChatWithoutLocking(currentUser);
        } finally {
            lock.unlock();
        }
    }

    private SetUsersPublicInChatResponse setUserPublicInChatWithoutLocking(User currentUser) {
        int size = privateChats.size();
        long currentUserId = currentUser.getId();
        for (int i = 0; i < size; i++) {
            Pair<User, User> p = privateChats.get(i);
            if (isUserInPair(p, currentUserId)) {
                privateChats.remove(i);
                User chatPartner = isUserId(p.getFirst(), currentUserId) ? p.getSecond() : p.getFirst();
                addCmds(new ChatMessage(Arrays.asList(chatPartner), currentUser, new UserLeftPrivateChatCmd(getUsers())));
                return new SetUsersPublicInChatResponse(SetUsersPublicInChatResponseStatus.SUCCESS, getUsers());
            }
        }
        return new SetUsersPublicInChatResponse(SetUsersPublicInChatResponseStatus.USER_NOT_IN_PRIVATE_CHAT);
    }

    private static boolean isUserInPair(Pair<User, User> p, long userId) {
        return isUserId(p.getFirst(), userId) || isUserId(p.getSecond(), userId);
    }

    private static boolean isUserId(User user, long userId) {
        return user != null && user.getId() == userId;
    }

    private Pair<User, User> getPrivatePair(long userId) {
        for (Pair<User, User> p : privateChats) {
            if (isUserInPair(p, userId)) {
                return p;
            }
        }
        return null;
    }

    private boolean isAtLeastOneInPrivateChat(long[] userIds) {
        for (long userId : userIds) {
            if (getPrivatePair(userId) != null) {
                return true;
            }
        }
        return false;
    }

    private ArrayList<User> getUsersWhoAreNotInPrivateChat() {
        ArrayList<User> usrs = new ArrayList<>();
        for (ChatUser user : chatUsers) {
            if (getPrivatePair(user.getUser().getId()) == null) {
                usrs.add(user.getUser());
            }
        }
        return usrs;
    }

    private ArrayList<User> getUsers() {
        ArrayList<User> list = new ArrayList<>(chatUsers.size());
        for (ChatUser u : chatUsers) {
            list.add(u.getUser());
        }
        return list;
    }
}