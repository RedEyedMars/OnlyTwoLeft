package duo.messages;

import duo.Handler;

/**
 * PassMessages are special {@link duo.messages.Message}'s because they are handled by the Server in a unique way.
 * Instead of the Server acting on the enclosed {@link duo.messages.Message}, it passes that {@link duo.messages.Message} onto the partnered client, if there is one.
 * In this way {@link duo.messages.Message}'s can be sent from one {@link duo.client.Client} to another, without the Server having to worry about the meaning of the {@link duo.messages.Message}.
 * 
 * @author Geoffrey
 *
 */
public class PassMessage extends Message {
	//For Message sending.
	private static final long serialVersionUID = 63329407315714395L;
	
	//Message to be acted on by the partnered Client.
	private Message message;
	
	/**
	 * Initializes the PassMessage with the {@link duo.messages.Message} that will be acted upon by the partnered.
	 * To be clear, the PassMessage is not received by the {@link duo.client.Client}, only this enclosed {@link duo.messages.Message} is received.
	 * @param message - {@link duo.messages.Message} to be acted upon by the partnered {@link duo.client.Client}. 
	 */
	public PassMessage(Message message){
		this.message = message;
	}

	/**
	 * This method should not be called because a PassMessage should never reach a {@link duo.client.Client}.
	 * However if a PassMessage is received, the encompassed {@link duo.messages.Message} is acted upon.
	 */
	@Override
	public void act(Handler handler) {
		//Act upon the enclosed message.
		this.message.act(handler);
	}

}
