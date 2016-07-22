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

import duo.messages.AddGameMessage;
import duo.messages.AddPlayerMessage;
import duo.messages.EndConnectionMessage;
import duo.messages.Message;
import duo.messages.PingMessage;

public class Client {
	private static Client client;
	private Handler handler;
	private String serverAddress;
	private String playerName;
	protected List<String[]> games = new ArrayList<String[]>();
	public Client(String severAddress, String playerName){
		this.serverAddress = severAddress;				
		this.playerName = playerName;
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
			((Message)ois.readObject()).act(socket);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		oos.close();
		ois.close();
		socket.close();

		synchronized(this){
			try {
				while(handler==null){
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
		client.sendMessage(msg);
	}
	public static void pass(Message msg) {
		client.passMessage(msg);
	}
	public void sendMessage(Message msg){
		this.handler.send(msg);
	}
	public void passMessage(Message message) {
		this.handler.pass(message);

	}
	public void close(){
		if(handler!=null){
			handler.close();
		}
	}
	public static void endConnection(){
		if(client!=null){
			client.close();
		}
	}

	public static void main(String[] args){
		Client client = new Client("127.0.0.1","Geoff");
		try {
			client.run();
			Client.send(new AddGameMessage("newb","Forest1","black"));
			Client.pass(new PingMessage());
			Client.send(new EndConnectionMessage());
			client.close();
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
	public static void setHandler(int port) {
		client.setHandler(new Handler(client,port));
	}
	public String getServerAddress() {
		return serverAddress;
	}
	public static void clearGames() {
		if(client!=null){
			client.games.clear();
		}
	}
	public static void addGame(String[] game) {
		if(client!=null){
			client.games.add(game);
		}
	}
}
