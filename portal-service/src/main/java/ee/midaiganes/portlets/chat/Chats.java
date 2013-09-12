package ee.midaiganes.portlets.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Chats {
	private final Lock lock = new ReentrantLock();
	private final DummyChat dummyChat = new DummyChat();
	private final int MIN_NR_OF_CHATS = 5;
	private final ConcurrentHashMap<Long, Chat> chats;
	private final AtomicLong chatIdGenerator = new AtomicLong(0);

	public Chats() {
		ConcurrentHashMap<Long, Chat> chats = new ConcurrentHashMap<>();
		for (int i = 0; i < MIN_NR_OF_CHATS; i++) {
			long id = chatIdGenerator.getAndIncrement();
			chats.put(Long.valueOf(id), new ChatImpl(id));
		}
		this.chats = chats;
	}

	public Chat getChat(long id) {
		try {
			lock.lock();
			Chat chat = chats.get(Long.valueOf(id));
			return chat != null ? chat : dummyChat;
		} finally {
			lock.unlock();
		}
	}

	public List<ChatModel> getChats() {
		try {
			lock.lock();
			List<ChatModel> list = new ArrayList<>();
			for (Map.Entry<Long, Chat> entry : this.chats.entrySet()) {
				list.add(new ChatModel(entry.getKey().longValue(), entry.getKey().toString(), entry.getValue().getNumberOfUsers()));
			}
			return list;
		} finally {
			lock.unlock();
		}
	}

	public void destroyAll() {
		lock.lock();
		try {
			for (Map.Entry<Long, Chat> entry : this.chats.entrySet()) {
				entry.getValue().destroy();
			}
			this.chats.clear();
		} finally {
			lock.unlock();
		}
	}
}
