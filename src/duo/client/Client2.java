package duo.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import duo.messages.AddGameMessage;
import duo.messages.AddPlayerMessage;
import duo.messages.EndConnectionMessage;
import duo.messages.JoinGameMessage;
import duo.messages.Message;
import duo.messages.PingMessage;

public class Client2 {

	public static void main(String[] args){
		Client client = new Client("127.0.0.1","Belma");
		try {
			client.run();
			Client.send(new JoinGameMessage("newb"));
			Client.pass(new PingMessage());
			Client.send(new EndConnectionMessage());
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
