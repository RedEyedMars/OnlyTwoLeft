package com.rem.otl.core.duo.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.rem.otl.core.duo.messages.AddGameMessage;
import com.rem.otl.core.duo.messages.AddPlayerMessage;
import com.rem.otl.core.duo.messages.EndConnectionMessage;
import com.rem.otl.core.duo.messages.JoinGameMessage;
import com.rem.otl.core.duo.messages.Message;
import com.rem.otl.core.duo.messages.PingMessage;

public class Client2 {

	public static void main(String[] args){
		Client client = new Client("127.0.0.1","Belma");
		try {
			client.establishConnectionWithTheServer();
			Client.send(new JoinGameMessage("newb","Belma"));			
			//client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
