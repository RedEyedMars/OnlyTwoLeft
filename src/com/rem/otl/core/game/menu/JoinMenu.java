package com.rem.otl.core.game.menu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.rem.otl.core.duo.client.Client;
import com.rem.otl.core.duo.messages.EndGameMessage;
import com.rem.otl.core.duo.messages.JoinGameMessage;
import com.rem.otl.core.duo.messages.KickFromGameMessage;
import com.rem.otl.core.duo.messages.Message;
import com.rem.otl.core.duo.messages.PassMessage;
import com.rem.otl.core.editor.ButtonAction;
import com.rem.otl.core.editor.Editor;
import com.rem.otl.core.editor.TextWriter;
import com.rem.otl.core.game.Game;
import com.rem.otl.core.game.environment.Square;
import com.rem.otl.core.gui.Gui;
import com.rem.otl.core.gui.graphics.GraphicEntity;
import com.rem.otl.core.gui.graphics.GraphicText;
import com.rem.otl.core.gui.inputs.KeyBoardEvent;
import com.rem.otl.core.gui.inputs.KeyBoardListener;
import com.rem.otl.core.gui.inputs.ClickEvent;
import com.rem.otl.core.main.Hub;

public class JoinMenu extends Menu implements IDuoMenu{
	private Client client;
	private TextWriter name;
	private TextWriter ip;
	private MenuButton joinButton;
	private MenuButton gameButton;
	private MenuButton buttonList;
	private List<String[]> gamesList = new ArrayList<String[]>(){
		private static final long serialVersionUID = 6849911565108959389L;
		@Override
		public void clear(){
			super.clear();
			buttonList.changeText("");
		}
		@Override
		public boolean add(String[] game){
			boolean ret = super.add(game);
			if(ret){
				buttonList.changeText("");
			}
			return ret;
		}
	};
	private int gameTopIndex = 0;
	private int gameIndex = 0;
	private String officialName;
	private String officialGame;
	public JoinMenu() {
		super();

		GraphicEntity nameButton = new MenuButton("Name:"){
			{
				text.setJustified(GraphicText.LEFT_JUSTIFIED);
			}
			@Override
			public void performOnRelease(ClickEvent e){
				Hub.handler.removeOnType(ip);
				Hub.handler.giveOnType(name);
			}
		};
		nameButton.resize(0.8f, 0.15f);
		nameButton.reposition(0.1f,0.72f);

		name = new TextWriter("impact",Hub.defaultPlayerName){
			{
				setFontSize(GraphicText.FONT_SIZE_LARGE);
				resize(getWidth(), getHeight());
				charIndex=Hub.defaultPlayerName.length();
				index=Hub.defaultPlayerName.length();
			}
			@Override
			public void onType(KeyBoardEvent event){
				if(event.is(KeyBoardEvent.BACKSPACE)){
					super.onType(event);
				}
				else if(event.getChar()>=32){
					if(lines.get(0).size()==0||lines.get(0).getChild(lines.get(0).size()-1).getX()<0.9f-0.1f){
						super.onType(event);
					}
				}
			}

		};
		name.reposition(0.325f,0.74f);
		addChild(nameButton);
		addChild(name);
		//Gui.giveOnType(name);


		GraphicEntity ipButton = new MenuButton("IP:"){
			{
				text.setJustified(GraphicText.LEFT_JUSTIFIED);
			}
			@Override
			public void performOnRelease(ClickEvent e){
				Hub.handler.removeOnType(name);
				Hub.handler.giveOnType(ip);
			}
		};
		ipButton.resize(0.8f, 0.15f);
		ipButton.reposition(0.1f,0.56f);

		final JoinMenu self = this;
		ip = new TextWriter("impact","52.35.55.220"){
			{
				setFontSize(GraphicText.FONT_SIZE_LARGE);
				resize(getWidth(), getHeight());
				charIndex=0;
				index=0;
				ctrlCommands = new HashMap<Integer, ButtonAction>();
				ctrlCommands.put(47, new ButtonAction(){
					@Override
					public void act(ClickEvent event) {
						if(!"Connected".equals(ip.getText())){

							
								String string = Hub.creator.copyFromClipboard();
								string = string.replace("\n", "");
								string = string.replace("\t", "");
								if(string.length()>40){
									string = string.substring(0,40);
								}
								change(string);
								charIndex = string.length();
								index = string.length();
								new JoinThread(self).start();
							
						}
					}});
				change("52.35.55.220");
			}
			@Override
			public void change(String newText){
				super.change(newText);
				if(newText.length()==0){
					charIndex=0;
					index=0;
				}
			}
			@Override
			public void onType(KeyBoardEvent event){
				if(!"Connected".equals(ip.getText())){
					if(event.is(KeyBoardEvent.BACKSPACE)||event.is(KeyBoardEvent.CTRL_LEFT)||event.is(KeyBoardEvent.CTRL_RIGHT)){
						super.onType(event);
					}
					else if(ctrling){
						super.onType(event);
					}
					else if(event.getChar()>=32){
						super.onType(event);
						if(event.keyUp()){
							new JoinThread(self).start();
						}
					}
				}
			}
		};
		ip.reposition(0.23f,0.58f);
		addChild(ipButton);
		addChild(ip);

		buttonList = new MenuButton(""){
			private int botIndex = 1;
			private GraphicText gameNames = new GraphicText("impact","",Hub.MID_LAYER);
			private GraphicText mapNames = new GraphicText("impact","",Hub.MID_LAYER);
			private GraphicText colours = new GraphicText("impact","",Hub.MID_LAYER);
			GraphicEntity selectorSquare = new GraphicEntity("squares",Hub.MID_LAYER);
			{
				selectorSquare.setFrame(6);
				selectorSquare.setVisible(false);
				children.remove(text);
				addChild(selectorSquare);
				children.add(text);
				addChild(gameNames);
				addChild(mapNames);
				addChild(colours);
				text.setFontSize(GraphicText.FONT_SIZE_TALLER);
				gameNames.setFontSize(GraphicText.FONT_SIZE_TALLER);
				gameNames.setJustified(GraphicText.LEFT_JUSTIFIED);
				mapNames.setFontSize(GraphicText.FONT_SIZE_TALLER);
				mapNames.setJustified(GraphicText.MIDDLE_JUSTIFIED);
				colours.setFontSize(GraphicText.FONT_SIZE_TALLER);
				colours.setJustified(GraphicText.RIGHT_JUSTIFIED);

			}
			@Override
			public void performOnRelease(ClickEvent e){
				if(isVisible()==false)return;
				gameIndex=(botIndex-1)-(int) ((botIndex-1)*(e.getY()-getY())/(getHeight()-0.06f));
				reposition(getX(),getY());
			}
			@Override
			public void changeText(String newGame){
				reset();
			}
			public float offsetX(int index){
				if(getChild(index)==selectorSquare){
					return 0.04f;
				}
				return super.offsetX(index);
			}
			public float offsetY(int index){
				if(index>=4){
					 return getHeight()-0.065f;
				}
				else if(index==3){
					return getHeight()-0.06f-(gameIndex)*0.035f;
				}
				else return super.offsetY(index);
			}
			@Override
			public void resize(float x, float y){
				super.resize(x, y);
				if(selectorSquare!=null){
					selectorSquare.resize(0.725f,0.03f);
				}
			}
			@Override
			public void onMouseScroll(int dx){
				if(isVisible()==false)return;
				if(dx<0){//down
					if((gamesList.size()>=4&&gameIndex<3)||(gamesList.size()<4&&gameIndex<gamesList.size()-1)){
						++gameIndex;
					}
					else {
						++gameTopIndex;
						if(gameTopIndex+4>=gamesList.size()){
							if(gamesList.size()>=4){
								gameTopIndex=gamesList.size()-4;
							}
							else {
								gameTopIndex=0;
							}
						}
					}
				}
				else if(dx>0){//up
					if(gameIndex>0){
						--gameIndex;
					}
					else {
						--gameTopIndex;
						if(gameTopIndex<0){
							gameTopIndex=0;
						}
					}
				}
				reset();
			}
			private void reset(){
				StringBuilder gameBuilder = new StringBuilder();
				StringBuilder mapBuilder = new StringBuilder();
				StringBuilder colourBuilder = new StringBuilder();
				botIndex = gamesList.size();
				if(botIndex>4){
					botIndex=4;
				}
				if(joinButton.getText().startsWith("Join")){
					if(botIndex==0){
						setVisible(false);
						joinButton.setVisible(false);
					}
					else {
						setVisible(true);
						joinButton.setVisible(true);
					}
				}

				for(int i=gameTopIndex;i<gameTopIndex+4&&i<gamesList.size();++i){
					gameBuilder.append(gamesList.get(i)[0]);
					gameBuilder.append("\n");
					mapBuilder.append(gamesList.get(i)[1]);
					mapBuilder.append("\n");
					colourBuilder.append(gamesList.get(i)[2]);
					colourBuilder.append("\n");
				}

				gameNames.change(gameBuilder.toString());
				mapNames.change(mapBuilder.toString());
				colours.change(colourBuilder.toString());
				resize(0.8f, 0.04f+0.04f*(botIndex));
				reposition(getX(),0.35f+0.04f*(4-botIndex));
				selectorSquare.setVisible(botIndex!=0);
			}
		};
		buttonList.resize(0.8f, 0.20f);
		buttonList.reposition(0.1f,0.35f);
		buttonList.setVisible(false);
		addChild(buttonList);

		gameButton = new MenuButton("");
		gameButton.reposition(0.2f,0.35f);
		addChild(gameButton);
		gameButton.setVisible(false);

		joinButton = new MenuButton("Join"){
			@Override
			public void performOnRelease(ClickEvent e){
				progressGame();
			}
		};
		joinButton.reposition(0.2f,0.19f);
		joinButton.setVisible(false);
		addChild(joinButton);

		GraphicEntity returnButton = new MenuButton("Return"){
			@Override
			public void performOnRelease(ClickEvent e){
				returnToMain();
			}
		};
		returnButton.reposition(0.2f,0.03f);
		addChild(returnButton);
		new JoinThread(self).start();
	}
	public KeyBoardListener getDefaultKeyBoardListener(){
		return ip;
	}

	public void kick(){
		if(joinButton.getText().startsWith("Waiting")){
			buttonList.setVisible(true);
			gameButton.setVisible(false);
			joinButton.changeText("Join");
		}
	}

	@Override
	public void playerJoins(String playerName) {
	}

	@Override
	public void startGame(boolean colour, long seed, long startTime) {
		if(Message.debug)System.out.println("start game");
		Game game = new Game(colour,seed,new MainMenu(false), startTime);
		Hub.gui.setView(game);		
	}

	public void returnToMain(){
		Client.endConnectionToTheServer();
		Hub.gui.setView(new DuoMenu());
	}
	public void progressGame() {
		if("Join".equals(joinButton.getText())&&buttonList.isVisible()){
			joinButton.changeText("Waiting .");
			officialName = name.getText();
			officialGame = gamesList.get(gameIndex+gameTopIndex)[0];
			Hub.defaultPlayerName = officialName;
			gameButton.changeText(officialGame);
			gameButton.setVisible(true);
			buttonList.setVisible(false);
			Client.send(new JoinGameMessage(gamesList.get(gameIndex+gameTopIndex)[0],officialName));
		}
		else if(joinButton.getText().startsWith("Waiting")){
			Client.send(new KickFromGameMessage());
			joinButton.changeText("Join");
			gameButton.setVisible(false);
			buttonList.setVisible(true);
		}
	}

	private double dotter = 0f;
	@Override
	public void update(double seconds){
		super.update(seconds);
		dotter+=seconds;
		if(dotter>1f){
			dotter-=1f;
			String name = joinButton.getText();
			if(name.startsWith("Waiting")){
				String dots = name.substring(7);
				dots+=" .";
				if(dots.length()>=8){
					dots=" .";
				}
				joinButton.changeText(name.substring(0, 7)+dots);
			}
		}
	}	
	private class JoinThread extends Thread {
		private IDuoMenu join;
		public JoinThread(IDuoMenu joinMenu){
			super();
			this.join = joinMenu;
		}
		@Override
		public void run(){
			try{
				client = new Client(ip.getText(),name.getText()){
					{
						handler.setGames(gamesList);
					}
					@Override
					public void close(){
						if("Connected".equals(ip.getText())){
							if(client.getHandler().getHero()!=null){
								handler.sendNow(new PassMessage(new EndGameMessage()));
							}
							if(joinButton.getText().startsWith("Waiting")){
								handler.sendNow(new KickFromGameMessage());
							}
							disconnect();
							handler.clearGames();
						}
						super.close();
					}
				};
				client.setMenu(join);
				client.establishConnectionWithTheServer();
				ip.change("Connected");
			}
			catch(IOException e){
				Client.endConnectionToTheServer();
			}

		}
	}
	public void disconnect() {
		ip.change("");
		buttonList.setVisible(false);
	}



}
