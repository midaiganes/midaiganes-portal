package ee.midaiganes.portlets.chat;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.ResourceServingPortlet;
import javax.servlet.AsyncContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;

import ee.midaiganes.beans.Utils;
import ee.midaiganes.portal.user.User;
import ee.midaiganes.portal.user.UserRepository;
import ee.midaiganes.portlets.BasePortlet;
import ee.midaiganes.portlets.chat.Chat.AddUserToChatResponse;
import ee.midaiganes.portlets.chat.Chat.AddUserToChatResponse.AddUserToChatResponseStatus;
import ee.midaiganes.portlets.chat.Chat.SendAndRemoveUserChatMessages;
import ee.midaiganes.portlets.chat.Chat.SendAndRemoveUserChatMessages.SendAndRemoveUserChatMessagesStatus;
import ee.midaiganes.portlets.chat.Chat.SendAndRemoveUserChatMessagesRequest;
import ee.midaiganes.portlets.chat.Chat.SendAndRemoveUserChatMessagesRequest.AsyncCallback;
import ee.midaiganes.portlets.chat.Chat.SendAndRemoveUserChatMessagesRequest.GetAsyncCallback;
import ee.midaiganes.portlets.chat.Chat.SendMessageToChat;
import ee.midaiganes.portlets.chat.Chat.SendPrivateMessageToUserResponse;
import ee.midaiganes.util.LongUtil;
import ee.midaiganes.util.RequestUtil;
import ee.midaiganes.util.SessionUtil;
import ee.midaiganes.util.StringUtil;
import ee.midaiganes.util.TimeProviderUtil;

public class ChatPortlet extends BasePortlet implements ResourceServingPortlet {
    private static final Logger log = LoggerFactory.getLogger(ChatPortlet.class);
    private final ChatService chatService = new ChatService();
    private static final String SHOW_CHAT = "showchat";
    private static final String CHATUSERS = "chatusers";
    private static final String CHAT_IS_FULL = "chatisfull";
    private static final String CHAT_NOT_FOUND = "chatnotfound";
    private static final String CHAT_ID = "chatid";
    private static final String EMTPY_RESPONSE = "EMTPY_RESPONSE";
    private static final String RESPONSE_MSG = "RESPONSE_MSG";
    private final UserRepository userRepository;

    public ChatPortlet() {
        userRepository = Utils.getInstance().getInstance(UserRepository.class);
    }

    @Override
    public void render(RenderRequest request, RenderResponse response) {
        try {
            if (SessionUtil.getUserId(request) != User.DEFAULT_USER_ID) {
                if (Boolean.TRUE.equals(request.getAttribute(EMTPY_RESPONSE))) {
                    // empty response...
                } else if (Boolean.TRUE.equals(request.getAttribute(SHOW_CHAT))) {
                    super.include("chat/chat", request, response);
                } else {
                    String responseMsg = (String) request.getAttribute(RESPONSE_MSG);
                    if (responseMsg != null) {
                        try (OutputStream os = response.getPortletOutputStream()) {
                            os.write(responseMsg.getBytes(Charsets.UTF_8));
                        }
                    } else {
                        List<ChatModel> chats = chatService.getChats();
                        request.setAttribute("chats", chats);
                        super.include("chat/chats", request, response);
                    }
                }
            }
        } catch (IOException | PortletException | RuntimeException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {
        long userId = SessionUtil.getUserId(request);
        if (userId != User.DEFAULT_USER_ID) {
            processLoggedInUserAction(request, userId);
        } else {
            log.debug("User is not logged in");
        }
    }

    private void processLoggedInUserAction(ActionRequest request, long userId) {
        String join = request.getParameter("join");
        String sendMessage = request.getParameter("send-message");
        String chat = request.getParameter("chat");
        String message = request.getParameter("message");
        String messageToUserId = request.getParameter("to");
        if (LongUtil.isNonNegativeLong(join)) {
            User user = getUser(userId);
            AddUserToChatResponse addUserToChatResponse = chatService.addUserToChat(user, Long.parseLong(join));
            AddUserToChatResponseStatus status = addUserToChatResponse.getStatus();
            if (AddUserToChatResponseStatus.SUCCESS.equals(status) || AddUserToChatResponseStatus.USER_ALREADY_IN_CHAT.equals(status)) {
                request.setAttribute(SHOW_CHAT, Boolean.TRUE);
                request.setAttribute(CHATUSERS, addUserToChatResponse.getUsers());
                request.setAttribute(CHAT_ID, join);
            } else if (AddUserToChatResponseStatus.CHAT_IS_FULL.equals(status)) {
                request.setAttribute(CHAT_IS_FULL, Boolean.TRUE);
            } else {
                request.setAttribute(CHAT_NOT_FOUND, Boolean.TRUE);
            }
        } else if (!StringUtil.isEmpty(message) && "1".equals(sendMessage) && LongUtil.isNonNegativeLong(chat)) {
            if (LongUtil.isNonNegativeLong(messageToUserId)) {
                User to = getUser(Long.parseLong(messageToUserId));
                if (to != null) {
                    SendPrivateMessageToUserResponse resp = chatService.sendPrivateMessageToUser(getUser(userId), to, Long.parseLong(chat), message);
                    if (SendPrivateMessageToUserResponse.SUCCESS.equals(resp)) {
                        request.setAttribute(EMTPY_RESPONSE, Boolean.TRUE);
                    } else if (SendPrivateMessageToUserResponse.USER_NOT_IN_CHAT.equals(resp)) {
                        // TODO
                        request.setAttribute(RESPONSE_MSG, "{\"status\":\"error\",\"message\":\"User not in chat\"}");
                    } else if (SendPrivateMessageToUserResponse.AT_LEAST_ONE_USER_IS_IN_PRIVATE_CHAT.equals(resp)) {
                        request.setAttribute(RESPONSE_MSG, "{\"status\":\"error\",\"message\":\"User in private chat\"}");
                    } else if (SendPrivateMessageToUserResponse.CHAT_NOT_FOUND.equals(resp)) {
                        request.setAttribute(RESPONSE_MSG, "{\"status\":\"error\",\"message\":\"Chat not found\"}");
                    } else {
                        request.setAttribute(RESPONSE_MSG, "{\"status\":\"error\",\"message\":\"error\"}");
                    }
                } else {
                    request.setAttribute(RESPONSE_MSG, "{\"status\":\"error\",\"message\":\"User not in chat\"}");
                }
            } else {
                SendMessageToChat resp = chatService.sendMessageToChat(getUser(userId), Long.parseLong(chat), message);
                if (SendMessageToChat.SUCCESS.equals(resp)) {
                    request.setAttribute(EMTPY_RESPONSE, Boolean.TRUE);
                } else if (SendMessageToChat.CHAT_NOT_FOUND.equals(resp)) {
                    request.setAttribute(CHAT_NOT_FOUND, Boolean.TRUE);
                }
            }
        } else {
            log.debug("Invalid parameters");
        }
    }

    @Override
    public void serveResource(ResourceRequest request, ResourceResponse response) throws PortletException, IOException {
        long userId = SessionUtil.getUserId(request);
        if (userId != User.DEFAULT_USER_ID) {
            String chatId = request.getParameter("chat");
            if (LongUtil.isNonNegativeLong(chatId)) {
                User user = getUser(userId);
                SendAndRemoveUserChatMessagesRequest call = new SendAndRemoveUserChatMessagesRequest(user, new ChatGetAsyncCallback(request));
                SendAndRemoveUserChatMessages resp = chatService.sendAndRemoveUserChatMessages(Long.parseLong(chatId), call);
                if (SendAndRemoveUserChatMessagesStatus.SUCCESS.equals(resp.getStatus())) {
                    String json = JsonUtil.toJson(resp.getCommands());
                    log.debug("Messages json is: '{}'", json);
                    byte[] bytes = json.getBytes(Charsets.UTF_8);
                    try (OutputStream os = response.getPortletOutputStream()) {
                        os.write(bytes);
                        os.flush();
                    }
                } else if (SendAndRemoveUserChatMessagesStatus.USER_NOT_IN_CHAT.equals(resp.getStatus())) {
                    // TODO
                } else if (SendAndRemoveUserChatMessagesStatus.CHAT_NOT_FOUND.equals(resp.getStatus())) {
                    // TODO
                } else if (SendAndRemoveUserChatMessagesStatus.SUCCESS_WAITING.equals(resp.getStatus())) {
                    // Do nothing
                } else {
                    log.error("Illegal state " + resp.getStatus());
                }
            }
        }
    }

    private User getUser(long userId) {
        return userRepository.getUser(userId);
    }

    private static class ChatAsyncCallback implements AsyncCallback {
        private final AsyncContext asyncContext;
        private final long timeoutTimeInMillis;

        private ChatAsyncCallback(AsyncContext asyncContext) {
            this.asyncContext = asyncContext;
            this.timeoutTimeInMillis = TimeProviderUtil.currentTimeMillis() + (1000 * 30);
        }

        @Override
        public void call(ChatCmds messages) {
            log.debug("Sending messages: '{}'", messages);
            try (ServletOutputStream outputStream = asyncContext.getResponse().getOutputStream()) {
                outputStream.write(JsonUtil.toJson(messages).getBytes(Charsets.UTF_8));
                outputStream.flush();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            } finally {
                asyncContext.complete();
            }
        }

        @Override
        public void userNotInChat() {
            asyncContext.complete();
        }

        @Override
        public void timeout() {
            asyncContext.complete();
        }

        @Override
        public boolean isTimedOut(long currentTimeInMillis) {
            return timeoutTimeInMillis < currentTimeInMillis;
        }
    }

    private static class ChatGetAsyncCallback implements GetAsyncCallback {
        private static final long ASYNC_CONTEXT_TIMEOUT_IN_MILLIS = 60_000;
        private final ResourceRequest request;

        private ChatGetAsyncCallback(ResourceRequest request) {
            this.request = request;
        }

        @Override
        public AsyncCallback getAsyncCallback() {
            HttpServletRequest req = RequestUtil.getHttpServletRequest(request);
            AsyncContext asyncContext = req.startAsync();
            asyncContext.setTimeout(ASYNC_CONTEXT_TIMEOUT_IN_MILLIS);
            // TODO
            // HttpServletResponse resp =
            // ResponseUtil.getHttpServletResponse(response);
            // AsyncContext asyncContext = req.startAsync(req,
            // resp);
            return new ChatAsyncCallback(asyncContext);
        }
    }

    @Override
    public void destroy() {
        chatService.destroyAll();
        super.destroy();
    }
}
