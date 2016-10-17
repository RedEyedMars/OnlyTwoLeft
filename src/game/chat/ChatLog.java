package game.chat;

import java.util.ArrayList;

public class ChatLog extends ArrayList<ChatLogEntry>{

	private int numberOfLines = 0;
	@Override
	public boolean add(ChatLogEntry entry){
		numberOfLines += entry.getLog().split("\n").length;
		return super.add(entry);
	}
	@Override
	public boolean remove(Object entry){
		if(super.remove(entry)){
			numberOfLines -= ((ChatLogEntry)entry).getLog().split("\n").length;
			return true;
		}
		else return false;
	}
	@Override
	public ChatLogEntry remove(int i){
		ChatLogEntry entry = super.remove(i);
		numberOfLines -= entry.getLog().split("\n").length;
		return entry;
	}
	public int numberOfLines() {
		return numberOfLines;
	}

}
