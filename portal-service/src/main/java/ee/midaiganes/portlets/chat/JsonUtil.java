package ee.midaiganes.portlets.chat;

import java.lang.reflect.Type;
import java.util.Locale;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import ee.midaiganes.model.User;
import ee.midaiganes.portlets.chat.ChatCmd.Cmd;
import ee.midaiganes.portlets.chat.ChatCmd.JoinChatCmd;
import ee.midaiganes.portlets.chat.ChatCmd.MsgChatCmd;
import ee.midaiganes.portlets.chat.ChatCmd.MsgChatCmdData;
import ee.midaiganes.portlets.chat.ChatCmd.PrivMsgChatCmd;
import ee.midaiganes.portlets.chat.ChatCmd.PrivMsgChatCmdData;

public class JsonUtil {
	private static final Gson gson;
	static {
		gson = new GsonBuilder().disableInnerClassSerialization().registerTypeAdapter(ChatCmds.class, new ChatCmdsJsonSerializer())
				.registerTypeAdapter(MsgChatCmd.class, new MsgChatCmdJsonSerializer())
				.registerTypeAdapter(PrivMsgChatCmd.class, new PrivMsgChatCmdJsonSerializer())
				.registerTypeAdapter(JoinChatCmd.class, new JoinChatCmdJsonSerializer()).create();
	}

	private static class ChatCmdsJsonSerializer implements JsonSerializer<ChatCmds> {

		@Override
		public JsonElement serialize(ChatCmds src, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject jo = new JsonObject();
			JsonArray ja = new JsonArray();
			for (ChatCmd<?> m : src.getMessages()) {
				ja.add(context.serialize(m));
			}
			jo.add("messages", ja);
			return jo;
		}

	}

	private static class MsgChatCmdJsonSerializer extends ChatCmdJsonSerializer implements JsonSerializer<MsgChatCmd> {
		@Override
		public JsonElement serialize(MsgChatCmd src, Type typeOfSrc, JsonSerializationContext context) {
			return super.serialize(src, typeOfSrc, context);
		}
	}

	private static class PrivMsgChatCmdJsonSerializer extends ChatCmdJsonSerializer implements JsonSerializer<PrivMsgChatCmd> {
		@Override
		public JsonElement serialize(PrivMsgChatCmd src, Type typeOfSrc, JsonSerializationContext context) {
			return super.serialize(src, typeOfSrc, context);
		}
	}

	private static class JoinChatCmdJsonSerializer extends ChatCmdJsonSerializer implements JsonSerializer<JoinChatCmd> {
		@Override
		public JsonElement serialize(JoinChatCmd src, Type typeOfSrc, JsonSerializationContext context) {
			return super.serialize(src, typeOfSrc, context);
		}
	}

	private static class ChatCmdJsonSerializer {

		private JsonElement serialize(ChatCmd<?> src, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject jo = new JsonObject();
			Cmd cmd = src.getCmd();
			jo.addProperty("cmd", cmd.name().toLowerCase(Locale.US));
			if (Cmd.JOIN.equals(cmd)) {
				setJoinProperties(jo, src);
			} else if (Cmd.MSG.equals(cmd)) {
				setMsgProperties(jo, src);
			} else if (Cmd.PRIVMSG.equals(cmd)) {
				setPrivMsgProperties(jo, src);
			} else {
				jo.add("data", context.serialize(src.getObj()));
			}
			return jo;
		}

		private void setPrivMsgProperties(JsonObject jo, ChatCmd<?> src) {
			PrivMsgChatCmdData data = (PrivMsgChatCmdData) src.getObj();
			jo.addProperty("message", data.getMsg());
			jo.addProperty("userId", Long.valueOf(data.getFromUserId()));
		}

		private void setMsgProperties(JsonObject jo, ChatCmd<?> src) {
			MsgChatCmdData data = (MsgChatCmdData) src.getObj();
			jo.addProperty("userId", Long.valueOf(data.getUser().getId()));
			jo.addProperty("message", data.getMsg());
		}

		private void setJoinProperties(JsonObject jo, ChatCmd<?> src) {
			User user = (User) src.getObj();
			jo.addProperty("userId", Long.valueOf(user.getId()));
			jo.addProperty("username", user.getUsername());
		}
	}

	public static String toJson(ChatCmds o) {
		return gson.toJson(o);
	}
}
