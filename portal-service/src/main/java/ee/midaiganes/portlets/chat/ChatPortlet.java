package ee.midaiganes.portlets.chat;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.MimeResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.ResourceServingPortlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import ee.midaiganes.model.DefaultUser;
import ee.midaiganes.portlets.BasePortlet;
import ee.midaiganes.util.CharsetPool;
import ee.midaiganes.util.SessionUtil;
import ee.midaiganes.util.StringUtil;

public class ChatPortlet extends BasePortlet implements ResourceServingPortlet {
	private static final Logger log = LoggerFactory.getLogger(ChatPortlet.class);
	private final ChatService chatService = new ChatService();
	private static final String SHOW_VIEW = ChatPortlet.class.getName() + ".SHOW_VIEW";
	private static final String SHOW_NOTHING = ChatPortlet.class.getName() + ".SHOW_NOTHING";
	private static final Gson gson = new Gson();

	@Override
	public void render(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		if (Boolean.TRUE.equals(request.getAttribute(SHOW_VIEW))) {
			super.include("chat/chat", request, response);
		} else if (!Boolean.TRUE.equals(request.getAttribute(SHOW_NOTHING))) {
			super.include("chat/available-chats", request, response);
		}
	}

	@Override
	public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {
		if ("1".equals(request.getParameter("join"))) {
			String chatId = getChatId(request);
			if (isValidChatId(chatId)) {
				long userId = SessionUtil.getUserId(request);
				if (userId != DefaultUser.DEFAULT_USER_ID) {
					if (chatService.addUserToChat(userId, chatId)) {
						log.info("User added to chat: '{}'", chatId);
					} else {
						log.info("User not added to chat! Allready in chat!");
					}
					request.setAttribute(SHOW_VIEW, Boolean.TRUE);
				} else {
					log.info("User not logged in");
				}
			}
		} else if ("1".equals(request.getParameter("send-message"))) {
			String message = request.getParameter("message");
			long userId = SessionUtil.getUserId(request);
			String chatId = getChatId(request);
			if (isValidChatId(chatId) && userId != DefaultUser.DEFAULT_USER_ID && chatService.isUserInChat(userId, chatId)) {
				if (!StringUtil.isEmpty(message)) {
					chatService.addMessage(userId, chatId, message);
					log.info("Message added to chat: '{}'", chatId);
					request.setAttribute(SHOW_NOTHING, Boolean.TRUE);
				} else {
					log.info("Message is empty");
				}
			} else {
				log.info("User not logged in or user not in chat. UserId = {}", userId);
			}
		}
	}

	@Override
	public void serveResource(ResourceRequest request, ResourceResponse response) throws PortletException, IOException {
		long userId = SessionUtil.getUserId(request);
		if (userId != DefaultUser.DEFAULT_USER_ID) {
			String chatId = getChatId(request);
			if (chatService.isUserInChat(userId, chatId)) {
				// TODO
				List<ChatMessage> messages = chatService.getUserMessages(userId, chatId);
				if (messages != null) {
					if (!messages.isEmpty()) {
						writeMessages(messages, response);
					} else {
						log.debug("No messages for user");
					}
				} else {
					log.debug("No messages for user. User not in chat?");
				}
			} else {
				log.debug("User not in chat");
			}
		} else {
			log.debug("User not logged in");
		}
	}

	private String getChatId(PortletRequest request) {
		return request.getParameter("chat-id");
	}

	private boolean isValidChatId(String chatId) {
		return !StringUtil.isEmpty(chatId);
	}

	private String createMessagesJson(List<ChatMessage> messages) {
		StringBuilder sb = new StringBuilder(messages.size() * 100);
		sb.append("{\"messages\":[");
		boolean first = true;
		for (ChatMessage cm : messages) {
			if (!first) {
				sb.append(",");
			}
			first = false;
			sb.append(gson.toJson(cm.getMessage()));
		}
		sb.append("]}");
		return sb.toString();
	}

	private void writeMessages(List<ChatMessage> messages, MimeResponse response) throws IOException {
		String json = createMessagesJson(messages);
		try (OutputStream os = response.getPortletOutputStream()) {
			os.write(json.getBytes(CharsetPool.UTF_8));
		}
	}
}
