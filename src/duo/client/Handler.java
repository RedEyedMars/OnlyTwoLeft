package duo.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import duo.messages.BlankMessage;
import duo.messages.MeetMeAtPortMessage;
import duo.messages.Message;
import duo.messages.PassMessage;
import duo.messages.PingMessage;

public class Handler {

	private int port;
	private boolean connected = true;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private List<Message> outgoingMail = new ArrayList<Message>();
	private Socket socket;
	private Client client;
	public Handler(Client client,int port) {
		this.client = client;
		try {
			this.port = port;
			socket = new Socket(client.getServerAddress(),port);
			output = new ObjectOutputStream(socket.getOutputStream());
			send(new PingMessage());
			input = new ObjectInputStream(socket.getInputStream());
			new HandlerOutputThread().start();
			new HandlerInputThread().start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void close(){
		connected = false;
	}
	
	private class HandlerInputThread extends Thread{
		@Override
		public void run(){
			while(connected){
				try {
					Message message = ((Message)input.readObject());
					message.act(socket);
					//System.out.println(message);
				} catch(SocketException s){
					client.close();
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
			while(connected){
				while(!outgoingMail.isEmpty()){
					try {
						//System.out.println("client send:"+outgoingMail.get(0));
						output.writeObject(outgoingMail.remove(0));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public Integer getPort() {
		return port;
	}

	public boolean isConnected() {
		return connected;
	}

}
