package ee.midaiganes.portlets.chat;

import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.procedure.TLongObjectProcedure;
import gnu.trove.procedure.TObjectProcedure;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Chats {
	private final Lock lock = new ReentrantLock();
	private final DummyChat dummyChat = new DummyChat();
	private final int MIN_NR_OF_CHATS = 5;
	private final TLongObjectHashMap<Chat> chats;
	private final AtomicLong chatIdGenerator = new AtomicLong(0);

	public Chats() {
		TLongObjectHashMap<Chat> chats = new TLongObjectHashMap<>();
		for (int i = 0; i < MIN_NR_OF_CHATS; i++) {
			long id = chatIdGenerator.getAndIncrement();
			chats.put(id, new ChatImpl(id));
		}
		this.chats = chats;
	}

	public Chat getChat(long id) {
		try {
			lock.lock();
			Chat chat = chats.get(id);
			return chat != null ? chat : dummyChat;
		} finally {
			lock.unlock();
		}
	}

	public List<ChatModel> getChats() {
		try {
			lock.lock();
			final List<ChatModel> list = new ArrayList<>();
			chats.forEachEntry(new TLongObjectProcedure<Chat>() {
				@Override
				public boolean execute(long chatId, Chat chat) {
					list.add(new ChatModel(chatId, Long.toString(chatId), chat.getNumberOfUsers()));
					return true;
				}
			});
			return list;
		} finally {
			lock.unlock();
		}
	}

	public void destroyAll() {
		lock.lock();
		try {
			this.chats.forEachValue(new TObjectProcedure<Chat>() {
				@Override
				public boolean execute(Chat chat) {
					chat.destroy();
					return true;
				}
			});
			this.chats.clear();
		} finally {
			lock.unlock();
		}
	}
}
