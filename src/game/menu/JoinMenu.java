package game.menu;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import duo.client.Client;
import duo.messages.EndGameMessage;
import duo.messages.JoinGameMessage;
import duo.messages.KickFromGameMessage;
import duo.messages.PassMessage;
import editor.ButtonAction;
import editor.Editor;
import editor.TextWriter;
import game.Game;
import game.environment.Square;
import gui.Gui;
import gui.graphics.GraphicEntity;
import gui.inputs.KeyBoardListener;
import gui.inputs.MotionEvent;
import main.Main;

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
			@Override
			public void performOnRelease(MotionEvent e){
				Gui.removeOnType(ip);
				Gui.giveOnType(name);
			}
			@Override
			public float offsetX(int index){
				if(index<3){
					return super.offsetX(index);
				}
				else {
					return 0.05f;
				}
			}
		};
		nameButton.adjust(0.8f, 0.15f);
		nameButton.setX(0.1f);
		nameButton.setY(0.72f);

		name = new TextWriter("impact","Player Two"){
			{
				setWidthFactor(1.4f);
				setHeightFactor(3f);
				adjust(getWidth(), getHeight());
				charIndex=10;
				index=10;
			}
			@Override
			public void keyCommand(boolean b, char c, int keycode){
				if(keycode==14){
					super.keyCommand(b, c, keycode);
				}
				else if(c>=32){
					if(lines.get(0).getChild(lines.get(0).size()-1).getX()<0.9f-0.1f){
						super.keyCommand(b, c, keycode);
					}
				}
			}
		};
		name.setX(0.325f);
		name.setY(0.74f);
		addChild(nameButton);
		addChild(name);
		//Gui.giveOnType(name);


		GraphicEntity ipButton = new MenuButton("IP:"){
			@Override
			public void performOnRelease(MotionEvent e){
				Gui.removeOnType(name);
				Gui.giveOnType(ip);
			}
			@Override
			public float offsetX(int index){
				if(index<3){
					return super.offsetX(index);
				}
				else {
					return 0.05f;
				}
			}
		};
		ipButton.adjust(0.8f, 0.15f);
		ipButton.setX(0.1f);
		ipButton.setY(0.56f);

		final JoinMenu self = this;
		ip = new TextWriter("impact","52.35.55.220"){
			{
				setWidthFactor(1.4f);
				setHeightFactor(3f);
				adjust(getWidth(), getHeight());
				charIndex=0;
				index=0;
				ctrlCommands = new HashMap<Integer, ButtonAction>();
				ctrlCommands.put(47, new ButtonAction(){
					@Override
					public void act(Editor subject) {
						if(!"Connected".equals(ip.getText())){
							Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
							try {
								String string = (String) clip.getContents(null).getTransferData(DataFlavor.stringFlavor);
								string = string.replace("\n", "");
								string = string.replace("\t", "");
								if(string.length()>40){
									string = string.substring(0,40);
								}
								change(string);
								charIndex = string.length();
								index = string.length();
								new JoinThread(self).start();
							} catch (UnsupportedFlavorException | IOException e) {
								e.printStackTrace();
							}
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
			public void keyCommand(boolean b, char c, int keycode){
				if(!"Connected".equals(ip.getText())){
					if(keycode==14||keycode==29||keycode==157){
						super.keyCommand(b, c, keycode);
					}
					else if(ctrling){
						super.keyCommand(b, c, keycode);
					}
					else if(c>=32){
						super.keyCommand(b, c, keycode);
						if(b==false){
							new JoinThread(self).start();
						}
					}
				}
			}
		};
		ip.setX(0.23f);
		ip.setY(0.58f);
		addChild(ipButton);
		addChild(ip);

		buttonList = new MenuButton(""){
			private int botIndex = 1;
			GraphicEntity selectorSquare = new GraphicEntity("squares",1);
			{
				selectorSquare.setFrame(6);
				selectorSquare.setVisible(false);
				children.remove(text);
				addChild(selectorSquare);
				addChild(text);
				text.setWidthFactor(1f);
				text.setHeightFactor(1.4f);

			}
			@Override
			public void performOnRelease(MotionEvent e){
				if(isVisible()==false)return;
				gameIndex=(botIndex-1)-(int) ((botIndex-1)*(e.getY()-getY())/(getHeight()-0.06f));
				setY(getY());
			}
			@Override
			public void changeText(String newGame){
				reset();
			}
			public float offsetX(int index){
				return index==4?0.05f:index==3?0.05f:super.offsetX(index);
			}
			public float offsetY(int index){
				return index==4?getHeight()-0.065f:
					index==3?getHeight()-0.06f-(gameIndex)*0.035f:
						super.offsetY(index);
			}
			@Override
			public void adjust(float x, float y){
				super.adjust(x, y);
				if(selectorSquare!=null){
					selectorSquare.adjust(0.7f,0.03f);
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
				StringBuilder builder = new StringBuilder();
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
					builder.append(gamesList.get(i)[0]);
					for(int j=0;j<40-gamesList.get(i)[0].length();++j){
						builder.append(" ");
					}
					builder.append(gamesList.get(i)[1]);
					for(int j=0;j<40-gamesList.get(i)[1].length();++j){
						builder.append(" ");
					}
					builder.append(gamesList.get(i)[2]);
					builder.append("\n");
				}
				adjust(0.8f, 0.04f+0.04f*(botIndex));
				setX(getX());
				setY(0.35f+0.04f*(4-botIndex));
				selectorSquare.setVisible(botIndex!=0);
				super.changeText(builder.toString());
			}
		};
		buttonList.adjust(0.8f, 0.20f);
		buttonList.setX(0.1f);
		buttonList.setY(0.35f);
		buttonList.setVisible(false);
		addChild(buttonList);

		gameButton = new MenuButton("");
		gameButton.setX(0.2f);
		gameButton.setY(0.35f);
		addChild(gameButton);
		gameButton.setVisible(false);

		joinButton = new MenuButton("Join"){
			@Override
			public void performOnRelease(MotionEvent e){
				progressGame();
			}
		};
		joinButton.setX(0.2f);
		joinButton.setY(0.19f);
		joinButton.setVisible(false);
		addChild(joinButton);

		GraphicEntity button = new MenuButton("Return"){
			@Override
			public void performOnRelease(MotionEvent e){
				returnToMain();
			}
		};
		button.setX(0.2f);
		button.setY(0.03f);
		addChild(button);
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
	public void startGame(boolean colour) {
		System.out.println("start game");
		Game game = new Game(colour);
		Gui.setView(game);		
	}

	public void returnToMain(){
		Client.endConnectionToTheServer();
		Gui.setView(new DuoMenu());
	}
	public void progressGame() {
		if("Join".equals(joinButton.getText())&&buttonList.isVisible()){
			joinButton.changeText("Waiting .");
			officialName = name.getText();
			officialGame = gamesList.get(gameIndex+gameTopIndex)[0];
			gameButton.changeText(officialGame);
			gameButton.setVisible(true);
			buttonList.setVisible(false);
			Client.send(new JoinGameMessage(gamesList.get(gameIndex+gameTopIndex)[0],officialName));
		}
		else if(joinButton.getText().startsWith("Waiting")){
			Client.send(new KickFromGameMessage(officialGame));
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
								handler.sendNow(new KickFromGameMessage(officialGame));
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
