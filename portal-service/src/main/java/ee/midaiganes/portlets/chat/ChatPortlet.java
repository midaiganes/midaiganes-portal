package ee.midaiganes.portlets.chat;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
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
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import ee.midaiganes.model.DefaultUser;
import ee.midaiganes.model.User;
import ee.midaiganes.portlets.BasePortlet;
import ee.midaiganes.services.UserRepository;
import ee.midaiganes.util.CharsetPool;
import ee.midaiganes.util.SessionUtil;
import ee.midaiganes.util.StringUtil;

public class ChatPortlet extends BasePortlet implements ResourceServingPortlet {
	private static final Logger log = LoggerFactory.getLogger(ChatPortlet.class);
	private final ChatService chatService = new ChatService();
	private static final String SHOW_VIEW = ChatPortlet.class.getName() + ".SHOW_VIEW";
	private static final String SHOW_NOTHING = ChatPortlet.class.getName() + ".SHOW_NOTHING";
	private static final Gson gson = new GsonBuilder().disableInnerClassSerialization().registerTypeAdapter(Message.class, new JsonSerializer<Message>() {
		@Override
		public JsonElement serialize(Message src, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject el = new JsonObject();
			el.addProperty("cmd", src.getCmd());
			el.addProperty("userId", src.getUserId());
			el.addProperty("message", src.getMessage());
			return el;
		}
	}).registerTypeAdapter(Join.class, new JsonSerializer<Join>() {
		@Override
		public JsonElement serialize(Join src, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject el = new JsonObject();
			el.addProperty("cmd", src.getCmd());
			el.addProperty("userId", src.getUserId());
			el.addProperty("username", src.getUsername());
			return el;
		}
	}).registerTypeAdapter(Quit.class, new JsonSerializer<Quit>() {

		@Override
		public JsonElement serialize(Quit src, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject el = new JsonObject();
			el.addProperty("cmd", src.getCmd());
			el.addProperty("userId", src.getUserId());
			return el;
		}
	}).create();

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
			handleJoinChat(request);
		} else if ("1".equals(request.getParameter("send-message"))) {
			handleSendMessage(request);
		}
	}

	private void handleJoinChat(ActionRequest request) {
		String chatId = getChatId(request);
		if (isValidChatId(chatId)) {
			long userId = SessionUtil.getUserId(request);
			if (userId != DefaultUser.DEFAULT_USER_ID) {
				if (chatService.addUserToChat(userId, chatId)) {
					log.info("User added to chat: '{}'", chatId);
				} else {
					log.info("User not added to chat! Allready in chat!");
				}
				setChatUsers(request, chatId);
				request.setAttribute(SHOW_VIEW, Boolean.TRUE);
			} else {
				log.info("User not logged in");
			}
		}
	}

	private void setChatUsers(PortletRequest request, String chatId) {
		long[] userIds = chatService.getChatUserIds(chatId);
		List<User> users = UserRepository.getInstance().getUsers(userIds);
		request.setAttribute("users", users);
	}

	private void handleSendMessage(ActionRequest request) {
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
						writeMessages(messages, userId, response);
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

	private String createMessagesJson(List<ChatMessage> messages, long userId) {
		StringBuilder sb = new StringBuilder(messages.size() * 100);
		sb.append("{\"messages\":[");
		boolean first = true;
		for (ChatMessage cm : messages) {
			if (!Join.isSelfJoin(cm.getCommand(), userId)) {
				if (!first) {
					sb.append(",");
				}
				first = false;
				gson.toJson(cm.getCommand(), sb);
			}
		}
		sb.append("]}");
		return sb.toString();
	}

	private void writeMessages(List<ChatMessage> messages, long userId, MimeResponse response) throws IOException {
		String json = createMessagesJson(messages, userId);
		try (OutputStream os = response.getPortletOutputStream()) {
			os.write(json.getBytes(CharsetPool.UTF_8));
		}
	}
}
