package duo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import duo.client.Client;
import duo.messages.BlankMessage;
import duo.messages.EndServerMessage;
import duo.messages.MeetMeAtPortMessage;
import duo.messages.Message;
import duo.messages.PassMessage;
import duo.messages.PingMessage;
import game.Hero;
import game.menu.IDuoMenu;

public class Handler {

	private int port;
	private boolean connected = false;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private Socket socket;
	private List<Message> outgoingMail = new ArrayList<Message>();
	private Client client;
	private boolean readMessages = true;
	public Handler(Client client) {
		this.client = client;		
	}
	public void setup(int port){
		try {
			this.port = port;
			socket = new Socket(client.getServerAddress(),port);
			output = new ObjectOutputStream(socket.getOutputStream());
			send(new PingMessage());
			input = new ObjectInputStream(socket.getInputStream());
			this.connected = true;
			new HandlerOutputThread().start();
			new HandlerInputThread(this).start();
			synchronized(client){
				client.notifyAll();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void disconnect(){
		connected = false;
	}

	public void close(){
		if(connected){
			client.close();
		}
	}

	public Integer getPort() {
		return port;
	}

	public boolean isConnected() {
		return connected;
	}

	public void sendNow(Message message) {
		try {
			output.writeObject(message);
		} catch(NullPointerException n){
			
		} catch(SocketException s){

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void sendBytes(byte[] bytes) {
		try {
			socket.getOutputStream().write(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public byte[] acceptBytes(int length) {
		byte[] bytes = new byte[length];
		try{
			for(int i=0;i<length;++i){
				bytes[i]=(byte)socket.getInputStream().read();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bytes; 
	}

	private class HandlerInputThread extends Thread{
		private Handler handler;
		public HandlerInputThread(Handler handler){
			super();
			this.handler = handler;
		}
		@Override
		public void run(){
			while(connected){
				try {
					while(readMessages&&connected){
						Message message = ((Message)input.readObject());
						message.act(handler);
					}
					while(!readMessages&&connected){
						float x = input.readFloat();
						float y = input.readFloat();

						handler.getHero().getPartner().setX(handler.getHero().getPartner().getX()+x);
						handler.getHero().getPartner().setY(handler.getHero().getPartner().getY()+y);
					}
					//System.out.println(message);
				} catch(SocketException s){
					Client.endConnection();
				}
				catch (ClassNotFoundException | IOException e) {
					e.printStackTrace();
				};
			}

		}
	}
	public void send(Message message){
		outgoingMail.add(message);
	}
	public void pass(Message message){
		outgoingMail.add(new PassMessage(message));
	}
	private class HandlerOutputThread extends Thread{
		@Override
		public void run(){
			try {
				while(connected){
					while(!outgoingMail.isEmpty()){
						//System.out.println("client send:"+outgoingMail.get(0));
						output.writeObject(outgoingMail.remove(0));
					}
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} catch (SocketException s){
				s.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	public List<String[]> games = new ArrayList<String[]>();	
	private IDuoMenu menu;
	private Hero hero;
	public void clearGames() {
		games.clear();
	}
	public void addGame(String[] game) {
		games.add(game);
	}
	public void playerJoins(String playerName) {
	}
	public void kick() {
		menu.kick();
	}
	public Hero getHero(){
		return hero;
	}
	public void setHero(Hero hero) {
		this.hero = hero;
	}
	public void setMenu(IDuoMenu menu) {
		this.menu = menu;
	}
	public IDuoMenu getMenu() {
		return menu;
	}
}
