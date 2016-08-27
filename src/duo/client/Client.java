package duo.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import duo.Handler;
import duo.messages.AddGameMessage;
import duo.messages.AddPlayerMessage;
import duo.messages.EndConnectionMessage;
import duo.messages.Message;
import duo.messages.SendMapMessage;
import duo.messages.StartGameMessage;
import game.Hero;
import game.menu.IDuoMenu;

/**
 * Client is the general, less functional class which handles communication between the two Players in the game, and the Server
 * The Client wraps a {@link duo.Handler}, which controls the actual streams of input/output.
 * Basically Client is the buffer for commands to handler.
 * It also handles the setting up of the {@link duo.Handler} by the initial communication with the Server.
 * @see duo.Handler Halder
 * @author Geoffrey
 */
public class Client {
	//For the static Client method to interact with the inner client method.
	//Also, if this client is null, the client is said to be disconnected/closed/off.
	private static Client client;
	//Slave Handler object that will do the interacting with the streams after the initial initialization.
	protected Handler handler;
	//The server address this client will try to initially connect to.
	private String serverAddress;
	//The Server needs a player name for this client. It does not need to be unique, just something to display
	//in the game list/start game.
	private String playerName;
	/**
	 * Basic constructor, initializes a serverAddress and a playerName
	 * @param severAddress - the Server IP address that this Client is suppose to connect to.
	 * @param playerName - the player name that this Client will identify as. Can be any string.
	 */
	public Client(String severAddress, String playerName){
		this.serverAddress = severAddress;				
		this.playerName = playerName;
		this.handler = new Handler(this);
	}

	/**
	 * Attempts to establish a {@link duo.Handler} to server.Handler link between the Server handler and this Client's handler.
	 * Will block until that link is established. If a hang happens it is because the Server's handler is failing to respond.
	 * (Note: this cannot be the cause of not being able to connect to the server, if that happens an exception is thrown and no block happens)
	 * @throws IOException - If there is an error communicating with the server, will throw that error.
	 */
	public void establishConnectionWithTheServer() throws IOException{
		//If there is a client already connected, display an error, but continue anyway. 
		if(client!=null){
			System.err.println("multiple clients per app have been created!");
		}

		//Attempt to establish a socket with the main server.
		Socket socket = new Socket(serverAddress,8000);
		//If the socket is established without an IOException being thrown, make this client the client which is "connected.
		client = this;
		//Grab the output stream.
		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
		//Send an "AddPlayerMessage" which will add this client as a Player to the Server 
		oos.writeObject(new AddPlayerMessage(getPlayerName()));
		//Grab the input stream.
		ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
		try {
			//Should receive a "MeetMeAtPortMessage", but if another message comes, act on that also.
			((Message)ois.readObject()).act(handler);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		//Now that we know the port that the server wants to communicate on, close this socket.
		oos.close();
		ois.close();
		socket.close();

		synchronized(this){
			try {
				//Wait until the handler is connected before releasing the block.
				while(!handler.isConnected()){
					wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * Allows access to the enclosed {@link duo.Handler}
	 * @return {@link duo.Handler} which this client is using to control the input/output streams.
	 */
	public Handler getHandler() {
		return this.handler;
	}
	/**
	 * If a client is connected, sends {@link duo.messages.Message} to the handler to send.
	 * @param message {@link duo.messages.Message} to send to the server's Handler
	 */
	public static void send(Message message) {
		if(client!=null){
			client.handler.send(message);
		}
	}
	/**
	 * If a client is connected, sends {@link duo.messages.Message} to the partnered Client.
	 * If no partner is known by the Server, this Message is consumed without effect.
	 * @param message {@link duo.messages.Message} to send to the partnered Client's {@link duo.Handler}.
	 */
	public static void pass(Message message) {
		if(client!=null){
			client.handler.pass(message);
		}
	}

	/**
	 * Askes if there is a Client that is currently connected.
	 * @return true - if there is a Client that is connected to the Server. false - if there is no client that can currently send {@link duo.messages.Message}'s.
	 */
	public static boolean isConnected(){
		return client!=null;
	}

	/**
	 * If there is a client currently connected, closes that connection to the server.
	 * This method should be called if the application running the client is the one closing the connection.
	 */
	public static void endConnectionToTheServer() {
		if(client!=null){
			client.handler.sendAllMessagesNow();
			client.closing();
		}
	}

	protected void closing(){	
		client.handler.sendNow(new EndConnectionMessage());
		client.close();
		client = null;
	}

	/**
	 * If there is a client currently connected, closes that connection to the server.
	 * This method should be called if the Server is the one closing the connection.
	 */
	public static void serverEndThisConnection() {
		if(client!=null){
			client.close();
			client = null;
		}
	}
	/**
	 * Prevents any further {@link duo.messages.Message}'s being sent by the handler.
	 * The socket/outputStream/inputStream are all subsequently closed.
	 */
	public void close(){
		handler.disconnect();
	}

	/**
	 * Testing method
	 * @param args
	 */
	public static void main(String[] args){
		Client client = new Client("127.0.0.1","Geoff");
		try {
			client.establishConnectionWithTheServer();
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
	/**
	 * Access to the name that represents this Client.
	 * @return string representing this Client.
	 */
	public String getPlayerName() {
		return playerName;//+" ("+serverAddress+")";
	}
	/**
	 * Access to the IP address to which this Client will attempt to connect.
	 * @return string of the IP address.
	 */
	public String getServerAddress() {
		return serverAddress;
	}
	/**
	 * Set method for the {@link game.menu.IDuoMenu menu} to which the {@link duo.Handler} is beholden.
	 * @param menu - the {@link game.menu.IDuoMenu menu} which the {@link duo.Handler} will answer to.
	 */
	public void setMenu(IDuoMenu menu) {
		this.handler.setMenu(menu);
	}

	/**
	 * Sets the {@link game.Hero} handled by this Client.
	 * @param hero - to be handled
	 */
	public static void setHero(Hero hero) {
		//Set the hero.
		client.getHandler().setHero(hero);		
	}

	public static void sendMapMessage(String filename, Message onEnd) {
		SendMapMessage.send(client, filename, onEnd);
	}

}
