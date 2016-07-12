package duo.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import duo.messages.AddGameMessage;
import duo.messages.AddPlayerMessage;
import duo.messages.EndConnectionMessage;
import duo.messages.Message;
import duo.messages.PingMessage;

public class Client {
	public static Client client;
	private Handler handler;
	public String severAddress;
	public Client(String severAddress){
		this.severAddress = severAddress;
				
	}
	public void run(){
		try {
			Socket socket = new Socket(severAddress,8000);
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(new AddPlayerMessage("Geoff"));
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			((Message)ois.readObject()).act(socket);
			oos.close();
			ois.close();
			socket.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}


	public void setHandler(Handler handler) {
		this.handler = handler;
	}
	public Handler getHandler() {
		return this.handler;
	}
	public void send(Message msg){
		this.handler.send(msg);
	}
	public void pass(Message message) {
		this.handler.pass(message);
		
	}
	public void close(){
		this.handler.close();
	}
	public static void main(String[] args){
		Client.client = new Client("127.0.0.1");
		client.run();
		client.send(new AddGameMessage("newb","Forest1"));
		client.pass(new PingMessage());
		client.send(new EndConnectionMessage());
		client.close();
	}
}
