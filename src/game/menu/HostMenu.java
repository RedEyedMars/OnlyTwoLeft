package game.menu;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import duo.client.Client;
import duo.messages.AddGameMessage;
import duo.messages.EndGameMessage;
import duo.messages.EndServerMessage;
import duo.messages.KickFromGameMessage;
import duo.messages.PassMessage;
import duo.messages.ReadyWhenYouAreMessage;
import duo.messages.RemoveGameMessage;
import duo.messages.SendMapMessage;
import editor.TextWriter;
import game.Game;
import gui.Gui;
import gui.graphics.GraphicEntity;
import gui.graphics.GraphicText;
import gui.inputs.KeyBoardListener;
import gui.inputs.MotionEvent;
import main.Main;

public class HostMenu extends Menu implements IDuoMenu{
	private MenuButton ip;
	private Client client;
	private MenuButton mapButton;
	private File mapFile = null;
	private TextWriter name;
	private GraphicEntity blackButton;
	private GraphicEntity whiteButton;
	private GraphicEntity kickButton;
	private MenuButton gameButton;
	public HostMenu() {
		super();
		GraphicEntity button = new GraphicText("impact","Name:",1){
			{
				setWidthFactor(1.4f);
				setHeightFactor(3f);
			}
		};
		button.resize(0.2f, 0.15f);
		button.reposition(0.15f,0.69f);

		GraphicEntity nameButton = new MenuButton("");
		nameButton.resize(0.8f, 0.15f);
		nameButton.reposition(0.1f,0.67f);

		name = new TextWriter("impact","New Game"){
			{
				setWidthFactor(1.4f);
				setHeightFactor(3f);
				resize(getWidth(), getHeight());
				charIndex=8;
				index=8;
			}
			@Override
			public void keyCommand(boolean b, char c, int keycode){
				if("Create Game".equals(gameButton.getText())){
					if(keycode==14){
						super.keyCommand(b, c, keycode);
					}
					else if(c>=32){
						if(lines.get(0).getChild(lines.get(0).size()-1).getX()<0.9f-0.1f){
							super.keyCommand(b, c, keycode);
						}
					}
				}
			}
		};
		name.reposition(0.325f,0.69f);
		addChild(nameButton);
		addChild(name);
		addChild(button);

		button = new GraphicText("impact","Map:",1){
			{
				setWidthFactor(1.4f);
				setHeightFactor(3f);
			}
		};
		button.resize(0.2f, 0.15f);
		button.reposition(0.15f,0.53f);


		mapFile = new File("data"+File.separator+
				"maps"+File.separator+
				"races"+File.separator+
				"Rollacluster"+File.separator+
				"Purple Hurdle.map");
		mapButton = new MenuButton("Purple Hurdle"){
			@Override
			public void performOnRelease(MotionEvent e){
				if("Create Game".equals(gameButton.getText())){
					changeMap();
				}
			}
			@Override
			public float offsetX(int index){
				if(index<3){
					return super.offsetX(index);
				}
				else {
					return 0.18f;
				}
			}
		};
		mapButton.resize(0.8f, 0.15f);
		mapButton.reposition(0.1f,0.51f);
		addChild(mapButton);
		addChild(button);
		
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
			@Override
			public void performOnRelease(MotionEvent e){
				if("Create Game".equals(gameButton.getText())){
					flipColour();
				}
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
		button.resize(0.8f, 0.15f);
		button.reposition(0.1f,0.35f);
		addChild(button);
		addChild(whiteButton);
		addChild(blackButton);
		whiteButton.setVisible(false);

		button = new MenuButton("Return"){
			@Override
			public void performOnRelease(MotionEvent e){
				returnToMain();
			}
		};
		button.reposition(0.2f,0.03f);
		addChild(button);

		ip = new MenuButton(Client.defaultServerIP){
			@Override
			public void performOnRelease(MotionEvent e){
				Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
				StringSelection stringSelection = new StringSelection(this.getText());
				clip.setContents(stringSelection, null);
			}
		};
		ip.resize(0.6f, 0.15f);
		ip.reposition(0.2f,0.84f);
		addChild(ip);

		gameButton = new MenuButton("Create Game"){
			@Override
			public void performOnRelease(MotionEvent e){
				progressGame();
			}
		};
		gameButton.resize(0.6f, 0.15f);
		gameButton.reposition(0.2f,0.19f);
		addChild(gameButton);

		kickButton = new GraphicEntity("editor_button",1){
			@Override
			public void performOnRelease(MotionEvent e){
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
			long seed = Main.getNewRandomSeed();
			SendMapMessage.send(
					client,
					mapFile.getAbsolutePath(),
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
		File newMap = GetFileMenu.getFile(this,"maps");
		if(newMap!=null){
			mapFile = newMap;
			String name = mapFile.getName();
			int dotIndex = name.indexOf('.');
			if(dotIndex!=-1){
				name = name.substring(0, dotIndex);
			}
			mapButton.changeText(name);
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
	public void startGame(boolean colour, long seed) {
		Game game = new Game(colour,seed);
		Gui.setView(game);
	}
	public void returnToMain(){
		Client.endConnectionToTheServer();
		Gui.setView(new DuoMenu());
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
