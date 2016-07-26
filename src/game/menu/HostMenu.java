package game.menu;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import duo.client.Client;
import duo.messages.AddGameMessage;
import duo.messages.AddPlayerMessage;
import duo.messages.EndConnectionMessage;
import duo.messages.EndServerMessage;
import duo.messages.GameListMessage;
import duo.messages.KickFromGameMessage;
import duo.messages.PingMessage;
import duo.messages.RemoveGameMessage;
import duo.messages.SendMapMessage;
import duo.messages.StartGameMessage;
import editor.MapEditor;
import editor.OnCreateSquareEditor;
import editor.TextWriter;
import game.Game;
import game.environment.Square;
import gui.Gui;
import gui.graphics.GraphicEntity;
import gui.graphics.GraphicText;
import gui.graphics.GraphicView;
import gui.inputs.KeyBoardListener;
import gui.inputs.MotionEvent;
import main.Hub;
import main.Main;
import storage.Storage;

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
	public HostMenu(List<Square> squares) {
		super();
		GraphicEntity button = new GraphicText("impact","Name:",1){
			{
				setWidthFactor(1.4f);
				setHeightFactor(3f);
			}
		};
		button.adjust(0.2f, 0.15f);
		button.setX(0.15f);
		button.setY(0.69f);

		GraphicEntity nameButton = new MenuButton("");
		nameButton.adjust(0.8f, 0.15f);
		nameButton.setX(0.1f);
		nameButton.setY(0.67f);

		name = new TextWriter("impact","New Game"){
			{
				setWidthFactor(1.4f);
				setHeightFactor(3f);
				adjust(getWidth(), getHeight());
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
		name.setX(0.325f);
		name.setY(0.69f);
		addChild(nameButton);
		addChild(name);
		addChild(button);

		button = new GraphicText("impact","Map:",1){
			{
				setWidthFactor(1.4f);
				setHeightFactor(3f);
			}
		};
		button.adjust(0.2f, 0.15f);
		button.setX(0.15f);
		button.setY(0.53f);

		mapButton = new MenuButton("Forest1"){
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
		mapButton.adjust(0.8f, 0.15f);
		mapButton.setX(0.1f);
		mapButton.setY(0.51f);
		addChild(mapButton);
		addChild(button);
		mapFile = new File("data"+File.separator+"maps"+File.separator+"Forest1.map");

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
		whiteButton.adjust(0.25f, 0.1f);
		whiteButton.setX(0.6f);
		whiteButton.setY(0.375f);
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
		blackButton.adjust(0.25f, 0.1f);
		blackButton.setX(0.35f);
		blackButton.setY(0.375f);
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
		button.adjust(0.8f, 0.15f);
		button.setX(0.1f);
		button.setY(0.35f);
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
		button.setX(0.2f);
		button.setY(0.03f);
		addChild(button);

		ip = new MenuButton(getIpAddress()){
			@Override
			public void performOnRelease(MotionEvent e){
				Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
				StringSelection stringSelection = new StringSelection(this.getText());
				clip.setContents(stringSelection, null);
			}
		};
		ip.adjust(0.6f, 0.15f);
		ip.setX(0.2f);
		ip.setY(0.84f);
		addChild(ip);

		gameButton = new MenuButton("Create Game"){
			@Override
			public void performOnRelease(MotionEvent e){
				progressGame();
			}
		};
		gameButton.adjust(0.6f, 0.15f);
		gameButton.setX(0.2f);
		gameButton.setY(0.19f);
		addChild(gameButton);

		kickButton = new GraphicEntity("editor_button",1){
			{
				this.listenToRelease = true;
			}
			@Override
			public void performOnRelease(MotionEvent e){
				if(isVisible()){
					setVisible(false);
					Client.send(new KickFromGameMessage(name.getText()));
					gameButton.changeText("Waiting .");
				}
			}
		};
		kickButton.setFrame(1);
		kickButton.adjust(0.137f,0.16f);
		kickButton.setX(0.07f);
		kickButton.setY(0.19f);
		kickButton.setVisible(false);
		addChild(kickButton);


		for(Square square:squares){
			addChild(square);
		}
		this.squares = squares;
		
		new HostThread(this).start();
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
			Client.send(new RemoveGameMessage(name.getText()));
		}
		else if(gameButton.getText().startsWith("Start")){
			SendMapMessage.send(
					client,
					mapFile.getAbsolutePath(),
					new StartGameMessage(whiteButton.isVisible()));
			Game game = new Game(blackButton.isVisible());
			client.getHandler().setHero(game.getHero());
			Gui.setView(game);
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
		File newMap = Gui.userSave("maps");
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
		}
	}
	public void kick() {
		if(gameButton.getText().startsWith("Start")){
			kickButton.setVisible(false);
			gameButton.changeText("Waiting .");
		}
	}
	@Override
	public void startGame(boolean colour) {		
	}
	public void returnToMain(){
		client.close();
		Gui.setView(new DuoMenu(squares));
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
					@Override
					public void close(){
						send(new EndServerMessage());
						super.close();
						if(proc!=null&&proc.isAlive()){
							proc.destroy();
							System.out.println("destroyed process");
						}
						if(proc!=null&&proc.isAlive()){
							proc.destroyForcibly();
							System.out.println("destoryedForced");
						}
					}
				};
				client.setMenu(host);
				client.run();
			} catch (IOException e) {
				e.printStackTrace();
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
}
