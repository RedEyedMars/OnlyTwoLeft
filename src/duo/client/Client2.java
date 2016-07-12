package duo.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import duo.messages.AddGameMessage;
import duo.messages.AddPlayerMessage;
import duo.messages.EndConnectionMessage;
import duo.messages.JoinGameMessage;
import duo.messages.Message;
import duo.messages.PingMessage;

public class Client2 {

	public static void main(String[] args){
		Client.client = new Client("127.0.0.1");
		Client.client.run();
		Client.client.send(new JoinGameMessage("newb"));
		Client.client.pass(new PingMessage());
		Client.client.send(new EndConnectionMessage());
		Client.client.close();
	}
}
