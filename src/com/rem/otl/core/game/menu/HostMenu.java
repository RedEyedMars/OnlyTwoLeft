package com.rem.otl.core.game.menu;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import com.rem.otl.core.duo.client.Client;
import com.rem.otl.core.duo.messages.AddGameMessage;
import com.rem.otl.core.duo.messages.EndGameMessage;
import com.rem.otl.core.duo.messages.EndServerMessage;
import com.rem.otl.core.duo.messages.KickFromGameMessage;
import com.rem.otl.core.duo.messages.PassMessage;
import com.rem.otl.core.duo.messages.ReadyWhenYouAreMessage;
import com.rem.otl.core.duo.messages.RemoveGameMessage;
import com.rem.otl.core.duo.messages.SendMapMessage;
import com.rem.otl.core.editor.TextWriter;
import com.rem.otl.core.game.Game;
import com.rem.otl.core.gui.Gui;
import com.rem.otl.core.gui.graphics.GraphicEntity;
import com.rem.otl.core.gui.graphics.GraphicText;
import com.rem.otl.core.gui.inputs.KeyBoardEvent;
import com.rem.otl.core.gui.inputs.KeyBoardListener;
import com.rem.otl.core.gui.inputs.ClickEvent;
import com.rem.otl.core.main.Hub;
import com.rem.otl.core.storage.Resource;
import com.rem.otl.core.storage.Storage;

public class HostMenu extends Menu implements IDuoMenu{
	private MenuButton ip;
	private Client client;
	private GraphicText mapButton;
	private Resource<InputStream> mapFile = null;
	private TextWriter name;
	private GraphicEntity blackButton;
	private GraphicEntity whiteButton;
	private GraphicEntity kickButton;
	private MenuButton gameButton;
	public HostMenu() {
		super();
		GraphicEntity button = new GraphicText("impact","Name:",1){
			{
				setFontSize(GraphicText.FONT_SIZE_LARGE);
			}
		};
		button.resize(0.2f, 0.15f);
		button.reposition(0.15f,0.69f);

		GraphicEntity nameButton = new MenuButton("");
		nameButton.resize(0.8f, 0.15f);
		nameButton.reposition(0.1f,0.67f);

		name = new TextWriter("impact","New Game"){
			{
				setFontSize(GraphicText.FONT_SIZE_LARGE);
				
				charIndex=8;
				index=8;
			}
			@Override
			public void onType(KeyBoardEvent event){
				if("Create Game".equals(gameButton.getText())){
					if(event.is(KeyBoardEvent.BACKSPACE)){
						super.onType(event);
					}
					else if(event.getChar()>=32){
						if(lines.get(0).getChild(lines.get(0).size()-1).getX()<0.9f-0.1f){
							super.onType(event);
						}
					}
				}
			}
		};
		name.reposition(0.325f,0.69f);
		addChild(nameButton);
		addChild(name);
		addChild(button);

		button = new MenuButton("Map:"){
			{
				text.setJustified(GraphicText.LEFT_JUSTIFIED);
			}
			@Override
			public void performOnRelease(ClickEvent e){
				if("Create Game".equals(gameButton.getText())){
					changeMap();
				}
			}
		};
		button.resize(0.8f, 0.15f);
		button.reposition(0.1f,0.51f);


		mapFile = Hub.manager.createInputStream(Hub.defaultMapFile.replace('/', File.separatorChar));
		mapButton = new GraphicText("impact",Storage.getMapNameFromFileName(mapFile.getPath()),Hub.MID_LAYER);
		mapButton.setFontSize(GraphicText.FONT_SIZE_LARGE);
		mapButton.resize(0.8f, 0.15f);
		mapButton.reposition(0.27f,0.53f);
		addChild(button);
		addChild(mapButton);
		
		whiteButton = new MenuButton("White"){
			@Override
			public float offsetY(int index){
				if(index<3){
					return super.offsetY(index);
				}
				else {
					return 0.0075f;
				}
			}
		};
		whiteButton.resize(0.25f, 0.1f);
		whiteButton.reposition(0.6f,0.375f);
		blackButton = new MenuButton("Black",true){
			@Override
			public float offsetY(int index){
				if(index<3){
					return super.offsetY(index);
				}
				else {
					return 0.0075f;
				}
			}
		};
		blackButton.resize(0.25f, 0.1f);
		blackButton.reposition(0.35f,0.375f);
		button = new MenuButton("Colour:"){
			{
				text.setJustified(GraphicText.LEFT_JUSTIFIED);
			}
			@Override
			public void performOnRelease(ClickEvent e){
				if("Create Game".equals(gameButton.getText())){
					flipColour();
				}
			}
		};
		button.resize(0.8f, 0.15f);
		button.reposition(0.1f,0.35f);
		addChild(button);
		addChild(whiteButton);
		addChild(blackButton);
		whiteButton.setVisible(false);

		button = new MenuButton("Return"){
			@Override
			public void performOnRelease(ClickEvent e){
				returnToMain();
			}
		};
		button.reposition(0.2f,0.03f);
		addChild(button);

		ip = new MenuButton(Client.defaultServerIP){
			@Override
			public void performOnRelease(ClickEvent e){
				Hub.creator.copyToClipboard(this.getText());
			}
		};
		ip.resize(0.6f, 0.15f);
		ip.reposition(0.2f,0.84f);
		addChild(ip);

		gameButton = new MenuButton("Create Game"){
			@Override
			public void performOnRelease(ClickEvent e){
				progressGame();
			}
		};
		gameButton.resize(0.6f, 0.15f);
		gameButton.reposition(0.2f,0.19f);
		addChild(gameButton);

		kickButton = new GraphicEntity("editor_button",Hub.MID_LAYER){
			@Override
			public void performOnRelease(ClickEvent e){
				if(isVisible()){
					setVisible(false);
					Client.send(new KickFromGameMessage());
					gameButton.changeText("Waiting .");
					gameButton.resize(0.6f, 0.15f);
					gameButton.reposition(0.2f,0.19f);
				}
			}
		};
		kickButton.setFrame(1);
		kickButton.resize(0.137f,0.16f);
		kickButton.reposition(0.78f,0.19f);
		kickButton.setVisible(false);
		addChild(kickButton);

		new JoinThread(this).start();
	}
	public KeyBoardListener getDefaultKeyBoardListener(){
		return name;
	}

	public void progressGame() {
		if("Create Game".equals(gameButton.getText())){
			gameButton.changeText("Waiting .");
			Client.send(new AddGameMessage(name.getText(),mapFile.getName(),blackButton.isVisible()?"black":"white"));
		}
		else if(gameButton.getText().startsWith("Waiting")){
			gameButton.changeText("Create Game");
			Client.send(new RemoveGameMessage());
		}
		else if(gameButton.getText().startsWith("Start")){
			long seed = Hub.getNewRandomSeed();
			SendMapMessage.send(
					client,
					mapFile.getPath(),
					new ReadyWhenYouAreMessage(whiteButton.isVisible(),seed));
		}
	}
	public void flipColour(){
		if(whiteButton.isVisible()){
			whiteButton.setVisible(false);
			blackButton.setVisible(true);
		}
		else if(blackButton.isVisible()){
			blackButton.setVisible(false);
			whiteButton.setVisible(true);
		}
	}
	public void changeMap(){
		Resource<InputStream> newMap = GetFileMenu.getFile(this,"maps/races",false);
		if(newMap!=null){
			mapFile = newMap;
			String name = mapFile.getName();
			int dotIndex = name.indexOf('.');
			if(dotIndex!=-1){
				name = name.substring(0, dotIndex);
			}
			mapButton.change(name);
		}
	}
	public void playerJoins(String playerName) {
		if(gameButton.getText().startsWith("Waiting")){
			kickButton.setVisible(true);
			gameButton.changeText("Start w/"+ playerName);
			gameButton.resize(0.7f, 0.15f);
			gameButton.reposition(0.1f,0.19f);
		}
	}
	public void kick() {
		if(gameButton.getText().startsWith("Start")){
			kickButton.setVisible(false);
			gameButton.changeText("Waiting .");
			gameButton.resize(0.6f, 0.15f);
			gameButton.reposition(0.2f,0.19f);
		}
	}
	@Override
	public void startGame(boolean colour, long seed, long startTime) {
		Game game = new Game(colour,seed,new MainMenu(false), startTime);
		Hub.gui.setView(game);
	}
	public void returnToMain(){
		Client.endConnectionToTheServer();
		Hub.gui.setView(new DuoMenu());
	}

	private double dotter = 0f;
	@Override
	public void update(double seconds){
		super.update(seconds);
		dotter+=seconds;
		if(dotter>1f){
			dotter-=1f;
			String name = gameButton.getText();
			if(name.startsWith("Waiting")){
				String dots = name.substring(7);
				dots+=" .";
				if(dots.length()>=8){
					dots=" .";
				}
				gameButton.changeText(name.substring(0, 7)+dots);
			}
		}
	}

	private class HostThread extends Thread {
		private IDuoMenu host;
		public HostThread(IDuoMenu host){
			super();
			this.host = host;
		}
		@Override
		public void run(){
			try {
				final Process proc = Runtime.getRuntime().exec("java -jar server.jar");

				client = new Client(ip.getText(),"Player One"){
					private int sentEnd = 0;
					@Override
					public void closing(){
						handler.sendNow(new EndServerMessage());
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}							
					}
				};
				client.setMenu(host);
				client.establishConnectionWithTheServer();
			} catch (IOException e) {
				e.printStackTrace();
				returnToMain();
			}
		}
	}

	public static String getIpAddress() 
	{ 
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface iface = interfaces.nextElement();
				if (iface.isLoopback() || !iface.isUp() || iface.isVirtual() || iface.isPointToPoint())
					continue;

				Enumeration<InetAddress> addresses = iface.getInetAddresses();
				while(addresses.hasMoreElements()) {
					InetAddress addr = addresses.nextElement();

					final String ip = addr.getHostAddress();
					if(Inet4Address.class == addr.getClass()) return ip;
				}
			}
		} catch (SocketException e) {
			throw new RuntimeException(e);
		}
		return null;
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
						//handler.games = gamesList;
					}
					@Override
					public void close(){
						if(client.getHandler().getHero()!=null){
							handler.sendNow(new PassMessage(new EndGameMessage()));
						}
						if(gameButton.getText().startsWith("Waiting")||gameButton.getText().startsWith("Start")){
							handler.sendNow(new KickFromGameMessage());
						}
						super.close();
					}
				};
				client.setMenu(join);
				client.establishConnectionWithTheServer();
				ip.changeText("Connected");
			}
			catch(IOException e){				
				Client.endConnectionToTheServer();
				ip.changeText(getIpAddress());
				new HostThread(join).start();
			}

		}
	}
}
