package duo.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

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
	public Handler(int port) {
		try {
			this.port = port;
			socket = new Socket(Client.client.severAddress,port);

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
					((Message)input.readObject()).act(socket);
				} catch (ClassNotFoundException | IOException e) {
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
		// TODO Auto-generated method stub
		return null;
	}

}
