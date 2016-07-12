package duo.messages;

import java.net.Socket;

public class PassMessage extends Message {
	private static final long serialVersionUID = 63329407315714395L;
	
	private Message message;
	public PassMessage(Message msg){
		this.message = msg;
	}

	@Override
	public void act(Socket socket) {
		this.message.act(socket);
	}

}
