package duo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import duo.client.Client;
import duo.messages.Message;
import duo.messages.PassMessage;
import duo.messages.PingMessage;
import game.Hero;
import game.menu.IDuoMenu;

/**
 * See {@link duo.client.Client} for more details on the client/handler relationship
 * The Handler is the more functional class, receiving commands from its host client.
 * The Handler handles input and output streams.
 * Receives {@link duo.messages.Message}'s and runs them.
 * Sends messages which have been added to a queue.
 * Holds some general objects that are needed by the message act methods. Such as a Player for the {@link game.Game} related actions, and a IDuo{@link game.menu.IDuoMenu Menu} for the pre-game menu actions. 
 * 
 * @see duo.client.Client Client
 * @author Geoffrey
 *
 */
public class Handler {
	//boolean which gates the input/output streams, when !connected the streams will close asap.
	private boolean connected = false;
	//The stream the sends Message's bound for the Server's handler, or for the partner client's handler.
	private ObjectOutputStream output;
	//The stream that receives Message's from the Server's handler and from the partner client.
	private ObjectInputStream input;
	//General socket object, used to close the connection after the conneciton is ended.
	private Socket socket;
	//The queue for outgoing Message's.
	private List<Message> outgoingMail = new ArrayList<Message>();
	//Host client, the client which control's this handler.
	private Client client;
	/**
	 * Basic Constructor.
	 * @param client - host {@link duo.client.Client}, the {@link duo.client.Client} which control's this handler.
	 */
	public Handler(Client client) {
		//Set the host Client.
		this.client = client;		
	}
	/**
	 * Called by the {@link duo.messages.MeetMeAtPortMessage}, which proves the Server port which this handler will do future communications through.
	 * Initializes the Input and Output streams/threads.
	 * @param port - Server port which this handler will connect for future communication.
	 */
	public void setup(int port){
		try {
			//Start the socket with the Server which will produce the communication streams.
			socket = new Socket(client.getServerAddress(),port);
			//Retrieve the output stream and wrap it in an ObjectOutputStream, allowing Message sending.
			output = new ObjectOutputStream(socket.getOutputStream());
			//Send a ping message, this is purely cosmetic, but allows someone watching the stream to see that a connection was made.
			send(new PingMessage());
			//Retrieve the input stream and wrap it in an ObjectInputStream, allowing Message receiving.
			input = new ObjectInputStream(socket.getInputStream());
			//Now that we have both our streams intact, the handler can be said to be "connected"
			this.connected = true;
			//Starts sending outgoing Messages which have been added to the outgoingMail.
			new HandlerOutputThread().start();
			//Starts receiving Message's sent to this handler.
			new HandlerInputThread(this).start();
			synchronized(client){
				//The client is waiting in Client.run for the handler to be setup, this releases that waiting loop.
				client.notifyAll();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * No future messages will be received or sent.
	 */
	public void disconnect(){
		connected = false;
	}

	/**
	 * This handler is open for sending or receiving messages.
	 * @return boolean, true if this handler can accept/receive {@link duo.messages.Message}'s, false otherwise.
	 */
	public boolean isConnected() {
		return connected;
	}

	/**
	 * Used by the {@link duo.messages.EndConnectionMessage} to shut down this client/handler.
	 */
	public void close(){
		//If this handler has already been disconnected, we should not try to close the client again.
		if(connected){
			//Closes the client, which will in turn disconnect the handler.
			client.close();
		}
	}

	/**
	 * Used when a message needs to be sent before any further messages are sent, less safe. 
	 * For example if the client was ending it's connection this method should be used to get off any last messages before that close.
	 * @param message - {@link duo.messages.Message} to send before anything else.
	 */
	public void sendNow(Message message) {
		try {
			//Send the message immediately.
			output.writeObject(message);
		} catch(NullPointerException n){

		} catch(SocketException s){

		} catch (IOException e) {
			e.printStackTrace();
		}
	}	

	public void sendAllMessagesNow() {
		while(!outgoingMail.isEmpty()){
			try {
				//Send the message immediately.
				output.writeObject(outgoingMail.remove(0));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Instead of a {@link duo.messages.Message}, this method sends a byte array. Currently this is used primarily to send Map files from the host client to the joining client.
	 * @param bytes - Byte array to be sent.
	 */
	public void sendBytes(byte[] bytes) {
		try {
			socket.getOutputStream().write(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Receives a byte array of a certain length, receives the bytes sent by Handler.sendBytes 
	 * @param length - length of the byte array to receive
	 * @return The byte array which has been received.
	 */
	public byte[] acceptBytes(int length) {
		//Initialize byte array.
		byte[] bytes = new byte[length];
		try{
			//For each byte sent, receive that byte.
			for(int i=0;i<length;++i){
				//place that byte in the accumulation array.
				bytes[i]=(byte)socket.getInputStream().read();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		//return the accumulated array
		return bytes; 
	}

	/**
	 * Thread which collects incoming messages to this handler and calls the {@link duo.messages.Message}.act method.
	 * Exits when Handler is no longer connected.
	 * If there is an error in receiving it is assumed to be the cause of the connection being interrupted and the client is shut down.
	 * @author Geoffrey
	 *
	 */
	private class HandlerInputThread extends Thread{
		//handler to use in message receiving.
		private Handler handler;
		/**
		 * Basic Constructor
		 * @param handler - takes the parent handler to use as the Message.act()'s subject.
		 */
		public HandlerInputThread(Handler handler){
			super();
			//stores the handler.
			this.handler = handler;
		}
		/**
		 * While the handler is connected reads incoming messages from the Server/partner {@link duo.client.Client}.
		 */
		@Override
		public void run(){
			//Lasts while the handler is connected.
			try {
				while(connected){
					//Reads a message.
					Message message = ((Message)input.readObject());
					//Calls that act method of the Message, completing the message's purpose.
					message.act(handler);
				}
			} catch(SocketException s){
				//If the socket is closed, the connection should be terminated.
				Client.endConnectionToTheServer();
			}
			catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			};

		}
	}
	/**
	 * Standard method to send {@link duo.messages.Message}'s to the Server, queues up the message to be sent after previous messages have been sent.
	 * @param message - {@link duo.messages.Message} to be sent to the Server.
	 */
	public void send(Message message){
		//Add the message to the queue.
		outgoingMail.add(message);
	}
	/**
	 * Sends a special "{@link duo.messages.PassMessage}" which causes the Server to relay the {@link duo.messages.Message} to the partnered {@link duo.client.Client} if there is such a client.
	 * If there is no partner, the {@link duo.messages.Message} is ignored.
	 * @param message - {@link duo.messages.Message} to be sent to the partner client.
	 */
	public void pass(Message message){
		send(new PassMessage(message));
	}
	/**
	 * Sends the outgoing mail that the handler has accumulated through the Handler.send method.
	 * Stops sending when the handler is disconnected.
	 * @author Geoffrey
	 *
	 */
	private class HandlerOutputThread extends Thread{
		/**
		 * Continually sends outgoing {@link duo.messages.Message}s while the Handler is connected
		 */
		@Override
		public void run(){
			try {
				//If the handler becomes disconnected, stop sending messages
				while(connected){
					//while there are messages to send, send them
					while(!outgoingMail.isEmpty()&&connected){
						//System.out.println("client send:"+outgoingMail.get(0));
						//Send outgoing mail
						output.writeObject(outgoingMail.remove(0));
					}
					try {
						//If there are no new Messages to send, wait a bit for new ones to fill the queue.
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

	//For the join menu, holds the games which the server is current offering to be joined.
	private List<String[]> games = new ArrayList<String[]>();	
	//The currently viewed Menu, either HostMenu or JoinMenu
	private IDuoMenu menu;
	//For the Game Message's which want to interact with the Hero in the Game being played.
	private Hero hero;
	/**
	 * Clears the list of games.
	 */
	public void clearGames() {
		//clears the list of games.
		games.clear();
	}
	/**
	 * Receives a "game" data packet, 3 strings, gameName, mapName, and hostColour, and adds it to the list of games stored.
	 * @param game - array of Strings, gameName, mapName, hostColour.
	 */
	public void addGame(String[] game) {
		games.add(game);
	}
	/**
	 * Sets the games object, this allows correspondence between the {@link game.menu.JoinMenu} interface and the handler.
	 * @param games - List of game data packets.
	 */
	public void setGames(List<String[]> games){
		//Sets the game object.
		this.games = games;
	}
	/**
	 * Sends the kick message to the menu, 
	 * this will result in 
	 * 	- the {@link game.menu.HostMenu} resuming to wait for someone to join
	 * and
	 * 	- the {@link game.menu.JoinMenu} returning to look at available games.
	 */
	public void kick() {
		//sends the kick message
		menu.kick();
	}
	/**
	 * Gets the hero controlled by this Handler.
	 * @return This Handler's {@link game.Hero}.
	 */
	public Hero getHero(){
		//return this handler's Hero.
		return hero;
	}
	/**
	 * Allows the {@link game.Game} to set this handler's hero.
	 * @param hero - {@link game.Hero} to be controlled by this handler.
	 */
	public void setHero(Hero hero) {
		//Set the hero.
		this.hero = hero;
	}
	/**
	 * Sets the menu which is controlling this Handler.
	 * @param menu - The {@link game.menu.IDuoMenu Menu} for this handler to control.
	 */
	public void setMenu(IDuoMenu menu) {
		//Set the menu.
		this.menu = menu;
	}
	/**
	 * Allows the relavent {@link duo.messages.Message}'s to interact with the controlling {@link game.menu.IDuoMenu Menu}.
	 * @return - This Handler's controlling {@link game.menu.IDuoMenu Menu}.
	 */
	public IDuoMenu getMenu() {
		//return the menu
		return menu;
	}
}
