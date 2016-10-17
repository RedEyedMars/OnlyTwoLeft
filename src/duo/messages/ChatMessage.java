package duo.messages;

import duo.Handler;
import game.chat.Chat;
import game.chat.ChatLogEntry;

public class ChatMessage extends Message{

	private static final long serialVersionUID = 1116023491496884530L;

	private static Chat chat;
	private String log;
	public ChatMessage(String log){
		super();
		this.log = log;
	}
	
	public static void setChatBox(Chat chat){
		ChatMessage.chat = chat;
	}
	
	@Override
	public void act(Handler handler) {
		if(ChatMessage.chat!=null){
			chat.logChat(new ChatLogEntry(ChatLogEntry.YOURS, log));
		}
	}
	
}
