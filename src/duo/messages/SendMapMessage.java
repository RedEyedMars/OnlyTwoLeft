package duo.messages;

import java.net.Socket;

import duo.Handler;
import duo.client.Client;
import storage.Storage;

public class SendMapMessage extends Message{
	private static final long serialVersionUID = -7927768988704831847L;
	private Message onEnd;
	private int length;
	public SendMapMessage(Message onEnd,int length){
		this.onEnd = onEnd;
		this.length = length;
	}

	@Override
	public void act(Handler handler) {
		Storage.loadMap(handler.acceptBytes(length));
		onEnd.act(handler);
	}
	
	public static void send(Client client,String filename, Message onEnd){
		byte[] file = Storage.readVerbatum(filename);
		client.getHandler().sendNow(new SendMapMessage(onEnd,file.length));
		client.getHandler().sendBytes(file);
		Storage.loadMap(file);
	}
	

}
