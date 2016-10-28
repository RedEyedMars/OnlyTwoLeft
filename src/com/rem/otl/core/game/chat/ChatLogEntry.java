package com.rem.otl.core.game.chat;
public class ChatLogEntry {
	public static final boolean MINE = true;
	public static final boolean YOURS = false;
	private String log;
	private boolean owner;
	public ChatLogEntry(boolean owner, String log){
		this.log = log;
		while(this.log.endsWith("\n")||this.log.endsWith(" ")){
			this.log = this.log.substring(0, this.log.length()-1);
		}
		this.owner = owner;
	}
	public String getLog(){
		return log;
	}
	public boolean getOwner(){
		return owner;
	}
}