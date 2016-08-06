package duo.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import duo.Handler;
import duo.messages.AddGameMessage;
import duo.messages.AddPlayerMessage;
import duo.messages.EndConnectionMessage;
import duo.messages.Message;
import duo.messages.PingMessage;
import duo.messages.SendMapMessage;
import duo.messages.StartGameMessage;
import game.menu.IDuoMenu;

public class Client {
	private static Client client;
	protected Handler handler;
	private String serverAddress;
	private String playerName;
	public Client(String severAddress, String playerName){
		this.serverAddress = severAddress;				
		this.playerName = playerName;
		this.handler = new Handler(this);
	}
	public void run() throws IOException{
		if(client!=null){
			System.err.println("multiple clients per app have been created!");
		}

		Socket socket = new Socket(serverAddress,8000);
		client = this;
		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
		oos.writeObject(new AddPlayerMessage(client.getPlayerName()));
		ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
		try {
			((Message)ois.readObject()).act(handler);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		oos.close();
		ois.close();
		socket.close();

		synchronized(this){
			try {
				while(!handler.isConnected()){
					wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}


	public void setHandler(Handler handler) {		
		this.handler = handler;
		synchronized(this){
			notifyAll();
		}
	}
	public Handler getHandler() {
		return this.handler;
	}
	public static void send(Message msg) {
		if(client!=null){
			client.handler.send(msg);
		}
	}
	public static void pass(Message msg) {
		if(client!=null){
			client.handler.pass(msg);
		}
	}
	
	public static boolean isConnected(){
		return client!=null;
	}

	public static void endConnection() {
		if(client!=null){
			client.close();
			client = null;
		}
	}
	public void close(){
		handler.sendNow(new EndConnectionMessage());
		handler.disconnect();
	}

	public static void main(String[] args){
		Client client = new Client("127.0.0.1","Geoff");
		try {
			client.run();
			Client.send(new AddGameMessage("newb","Forest1","black"));
			System.out.println("Waiting");
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("Sending");
			SendMapMessage.send(client,
					"data/maps/Forest1.map",
					new StartGameMessage(false));
			System.out.println("Sent");
			//Client.endConnection();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	public boolean isRunning() {
		return handler.isConnected();
	}
	public String getPlayerName() {
		return playerName+" ("+serverAddress+")";
	}
	public String getServerAddress() {
		return serverAddress;
	}
	public void setMenu(IDuoMenu menu) {
		this.handler.setMenu(menu);
	}
	
}
