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

import ee.midaiganes.beans.BeanUtil;
import ee.midaiganes.model.DefaultUser;
import ee.midaiganes.model.User;
import ee.midaiganes.portlets.BasePortlet;
import ee.midaiganes.portlets.chat.Chat.AddUserToChatResponse;
import ee.midaiganes.portlets.chat.Chat.SendAndRemoveUserChatMessages;
import ee.midaiganes.portlets.chat.Chat.SendAndRemoveUserChatMessagesRequest;
import ee.midaiganes.portlets.chat.Chat.SendMessageToChat;
import ee.midaiganes.portlets.chat.Chat.AddUserToChatResponse.AddUserToChatResponseStatus;
import ee.midaiganes.portlets.chat.Chat.SendAndRemoveUserChatMessages.SendAndRemoveUserChatMessagesStatus;
import ee.midaiganes.portlets.chat.Chat.SendAndRemoveUserChatMessagesRequest.AsyncCallback;
import ee.midaiganes.portlets.chat.Chat.SendAndRemoveUserChatMessagesRequest.GetAsyncCallback;
import ee.midaiganes.services.UserRepository;
import ee.midaiganes.util.CharsetPool;
import ee.midaiganes.util.LongUtil;
import ee.midaiganes.util.RequestUtil;
import ee.midaiganes.util.SessionUtil;
import ee.midaiganes.util.StringUtil;

public class ChatPortlet extends BasePortlet implements ResourceServingPortlet {
	private static final Logger log = LoggerFactory.getLogger(ChatPortlet.class);
	private final ChatService chatService = new ChatService();
	private static final String SHOW_CHAT = "showchat";
	private static final String CHATUSERS = "chatusers";
	private static final String CHAT_IS_FULL = "chatisfull";
	private static final String CHAT_NOT_FOUND = "chatnotfound";
	private static final String CHAT_ID = "chatid";
	private static final String EMTPY_RESPONSE = "EMTPY_RESPONSE";

	@Override
	public void render(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		if (SessionUtil.getUserId(request) != DefaultUser.DEFAULT_USER_ID) {
			if (Boolean.TRUE.equals(request.getAttribute(EMTPY_RESPONSE))) {
				// empty response...
			} else if (Boolean.TRUE.equals(request.getAttribute(SHOW_CHAT))) {
				super.include("chat2/chat", request, response);
			} else {
				List<ChatModel> chats = chatService.getChats();
				request.setAttribute("chats", chats);
				super.include("chat2/chats", request, response);
			}
		}
	}

	@Override
	public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {
		long userId = SessionUtil.getUserId(request);
		if (userId != DefaultUser.DEFAULT_USER_ID) {
			processLoggedInUserAction(request, response, userId);
		} else {
			log.debug("User is not logged in");
		}
	}

	private void processLoggedInUserAction(ActionRequest request, ActionResponse response, long userId) {
		String join = request.getParameter("join");
		String sendMessage = request.getParameter("send-message");
		String chat = request.getParameter("chat");
		String message = request.getParameter("message");
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
			SendMessageToChat resp = chatService.sendMessageToChat(getUser(userId), Long.parseLong(chat), message);
			if (SendMessageToChat.SUCCESS.equals(resp)) {
				request.setAttribute(EMTPY_RESPONSE, Boolean.TRUE);
			} else if (SendMessageToChat.CHAT_NOT_FOUND.equals(resp)) {
				request.setAttribute(CHAT_NOT_FOUND, Boolean.TRUE);
			}
		} else {
			log.debug("Invalid parameters");
		}
	}

	@Override
	public void serveResource(ResourceRequest request, ResourceResponse response) throws PortletException, IOException {
		long userId = SessionUtil.getUserId(request);
		if (userId != DefaultUser.DEFAULT_USER_ID) {
			String chatId = request.getParameter("chat");
			if (LongUtil.isNonNegativeLong(chatId)) {
				User user = getUser(userId);
				SendAndRemoveUserChatMessagesRequest call = new SendAndRemoveUserChatMessagesRequest(user, new ChatGetAsyncCallback(request));
				SendAndRemoveUserChatMessages resp = chatService.sendAndRemoveUserChatMessages(Long.parseLong(chatId), call);
				if (SendAndRemoveUserChatMessagesStatus.SUCCESS.equals(resp.getStatus())) {
					String json = JsonUtil.toJson(resp.getCommands());
					log.debug("Messages json is: '{}'", json);
					byte[] bytes = json.getBytes(CharsetPool.UTF_8);
					try (OutputStream os = response.getPortletOutputStream()) {
						os.write(bytes);
						os.flush();
					}
				}
			}
		}
	}

	private User getUser(long userId) {
		UserRepository userRepository = BeanUtil.getBean(UserRepository.class);
		return userRepository.getUser(userId);
	}

	private static class ChatAsyncCallback implements AsyncCallback {
		final AsyncContext asyncContext;

		private ChatAsyncCallback(AsyncContext asyncContext) {
			this.asyncContext = asyncContext;
		}

		@Override
		public void call(ChatCmds messages) {
			log.debug("Sending messages: '{}'", messages);
			try (ServletOutputStream outputStream = asyncContext.getResponse().getOutputStream()) {
				outputStream.write(JsonUtil.toJson(messages).getBytes(CharsetPool.UTF_8));
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
	}

	private static class ChatGetAsyncCallback implements GetAsyncCallback {
		private final ResourceRequest request;

		private ChatGetAsyncCallback(ResourceRequest request) {
			this.request = request;
		}

		@Override
		public AsyncCallback getAsyncCallback() {
			HttpServletRequest req = RequestUtil.getHttpServletRequest(request);
			AsyncContext asyncContext = req.startAsync();
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
