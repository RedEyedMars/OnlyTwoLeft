package com.rem.otl.core.game.chat;

import java.util.ArrayList;
import java.util.List;

import com.rem.otl.core.duo.client.Client;
import com.rem.otl.core.duo.messages.ChatMessage;
import com.rem.otl.core.editor.TextWriter;
import com.rem.otl.core.game.hero.Hero;
import com.rem.otl.core.game.menu.MenuButton;
import com.rem.otl.core.gui.Gui;
import com.rem.otl.core.gui.graphics.GraphicEntity;
import com.rem.otl.core.gui.graphics.GraphicText;
import com.rem.otl.core.gui.inputs.KeyBoardEvent;
import com.rem.otl.core.gui.inputs.KeyBoardListener;
import com.rem.otl.core.gui.inputs.ClickEvent;
import com.rem.otl.core.main.Hub;

public class Chat extends GraphicEntity implements KeyBoardListener {

	private static final int LOG_LENGTH = 19;
	private static final float MAX_LINE_WIDTH = 0.2f;
	private ChatLog log = new ChatLog();
	private GraphicText logDisplayMine;
	private GraphicText logDisplayYours;
	private int scrollIndex = 0;
	private TextWriter writer;
	private MenuButton chatButton;
	public Chat(int layer, boolean myColour) {
		super("chatbox", Hub.MID_LAYER);
		String myColourString = myColour==Hero.BLACK_BOOL?"":myColour==Hero.WHITE_BOOL?"White":"NONE";
		String yourColourString = myColour==Hero.BLACK_BOOL?"White":myColour==Hero.WHITE_BOOL?"":"NONE";
		logDisplayMine = new GraphicText("impact"+myColourString,"",Hub.TOP_LAYER);
		logDisplayYours = new GraphicText("impact"+yourColourString,"",Hub.TOP_LAYER);
		logDisplayYours.setJustified(GraphicText.RIGHT_JUSTIFIED);
		writer = new TextWriter("impact"+myColourString,"");

		writer.turnBlinkerOff();

		addChild(logDisplayMine);
		addChild(logDisplayYours);
		addChild(writer);


		final Chat chatBox = this;
		chatButton = new MenuButton(""){
			GraphicEntity openChat;
			{
				openChat = new GraphicEntity("chatbox",Hub.TOP_LAYER);
				openChat.setFrame(1);
				addChild(openChat);
			}
			@Override
			public void performOnRelease(ClickEvent event){
				if(this.isVisible()){
					Hub.handler.handleEvent(new KeyBoardEvent(KeyBoardEvent.KEY_UP,' ',KeyBoardEvent.ENTER));
				}
			}
			@Override
			public float offsetX(int index){
				if(getChild(index)==openChat){
					return 0.0125f;
				}
				else return super.offsetX(index);
			}
			@Override
			public float offsetY(int index){
				if(getChild(index)==openChat){
					return 0.01f;
				}
				else return super.offsetY(index);
			}
			@Override
			public void resize(float x, float y){
				super.resize(x, y);
				getChild(0).resize(0.045f, y);
				getChild(2).resize(0.045f, y);
				getChild(1).resize(0, y);
				if(openChat!=null)
					openChat.resize(x*0.8f, y*0.8f);
			}
			@Override
			public void setFrame(int frame){
				openChat.setFrame(frame);
			}
		};
		chatButton.reposition(0.03f, 0.77f);
		addChild(chatButton);

		resize(0.3f,0.6f);
		reposition(getX(),getY());

		for(GraphicEntity child:children){
			child.setLayer(Hub.TOP_LAYER);
		}
	}
	@Override
	public void onType(KeyBoardEvent event) {
		if(event.keyUp()&&event.is(KeyBoardEvent.ENTER)){
			//setVisible(false);
			String newText = writer.getText();
			if(!"".equals(newText)){
				ChatLogEntry entry = new ChatLogEntry(ChatLogEntry.MINE,newText);
				log.add(entry);
				Client.pass(new ChatMessage(newText));
				scrollIndex = log.numberOfLines()-LOG_LENGTH;
				if(scrollIndex<0)scrollIndex=0;
			}
			updateLog();
			writer.clearText();
			Hub.handler.removeOnType(this.getDefaultKeyBoardListener());
			Hub.handler.removeOnClick(this);
			writer.turnBlinkerOff();
			setVisible(false);
		}
		else if(event.is(KeyBoardEvent.ENTER)){
			writer.onType(event);
			writer.wrap(MAX_LINE_WIDTH);
		}
	}
	@Override
	public boolean continuousKeyboard() {
		return writer.continuousKeyboard();
	}
	@Override
	public float offsetX(int index){
		if(getChild(index)==chatButton){
			return getChild(index).getX()-getX();
		}
		else return super.offsetX(index);
	}
	@Override
	public float offsetY(int index){
		if(getChild(index)==chatButton){
			return getChild(index).getY()-getY();
		}
		else if(getChild(index)==writer){
			return 0.075f;
		}
		else if(getChild(index)==logDisplayMine||getChild(index)==logDisplayYours){
			if(log.size()<LOG_LENGTH ){
				return log.numberOfLines()*0.025f+0.0825f;
			}
			else {
				return LOG_LENGTH*0.025f+0.0825f;
			}
		}
		else return offsetY(index);
	}
	public void updateLog(){
		if(log.numberOfLines()<LOG_LENGTH ){
			StringBuilder myBuilder = new StringBuilder();
			StringBuilder yourBuilder = new StringBuilder();
			for(ChatLogEntry entry:log){
				if(entry.getOwner()==ChatLogEntry.MINE)
					myBuilder.append(entry.getLog());
				else if(entry.getOwner()==ChatLogEntry.YOURS)
					yourBuilder.append(entry.getLog());
				myBuilder.append('\n');
				yourBuilder.append('\n');
			}
			logDisplayMine.change(myBuilder.toString());
			logDisplayYours.change(yourBuilder.toString());
		}
		else {
			StringBuilder myBuilder = new StringBuilder();
			StringBuilder yourBuilder = new StringBuilder();
			for(int i=scrollIndex;i<scrollIndex+LOG_LENGTH&&i<log.numberOfLines();++i){
				if(log.get(i).getOwner()==ChatLogEntry.MINE)
					myBuilder.append(log.get(i).getLog());
				else if(log.get(i).getOwner()==ChatLogEntry.YOURS)
					yourBuilder.append(log.get(i).getLog());
				myBuilder.append('\n');
				yourBuilder.append('\n');
			}
			logDisplayMine.change(myBuilder.toString());
			logDisplayMine.change(yourBuilder.toString());
		}

		reposition(getX(),getY());
	}
	public void blinkerOn() {
		writer.turnBlinkerOn();
	}
	@Override
	public void onMouseScroll(int distance){
		scrollIndex+=-distance/120;
		if(scrollIndex>=log.numberOfLines()-LOG_LENGTH){
			scrollIndex=log.numberOfLines()-LOG_LENGTH-1;
		}
		if(scrollIndex<0){
			scrollIndex=0;
		}
		updateLog();
	}

	public void logChat(ChatLogEntry entry){
		log.add(entry);
		updateLog();
		if(entry.getOwner()==ChatLogEntry.YOURS&&chatButton.isVisible()){
			chatButton.setFrame(2);
		}
		setVisible(this.isVisible());
	}

	@Override
	public void resize(float x, float y){
		super.resize(x, y);
		chatButton.resize(0.09f, 0.08f);
	}
	@Override
	public void setVisible(boolean visible){
		super.setVisible(visible);
		chatButton.setVisible(!visible);
		if(visible){
			chatButton.setFrame(1);
		}
	}
	public GraphicEntity getOpenChatButton() {
		return chatButton;
	}
}
