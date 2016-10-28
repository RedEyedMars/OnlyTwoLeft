package com.rem.otl.core.duo.messages;

import com.rem.otl.core.duo.Handler;

/**
 * PassMessages are special {@link com.rem.otl.core.duo.messages.Message}'s because they are handled by the Server in a unique way.
 * Instead of the Server acting on the enclosed {@link com.rem.otl.core.duo.messages.Message}, it passes that {@link com.rem.otl.core.duo.messages.Message} onto the partnered client, if there is one.
 * In this way {@link com.rem.otl.core.duo.messages.Message}'s can be sent from one {@link com.rem.otl.core.duo.client.Client} to another, without the Server having to worry about the meaning of the {@link com.rem.otl.core.duo.messages.Message}.
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
	 * Initializes the PassMessage with the {@link com.rem.otl.core.duo.messages.Message} that will be acted upon by the partnered.
	 * To be clear, the PassMessage is not received by the {@link com.rem.otl.core.duo.client.Client}, only this enclosed {@link com.rem.otl.core.duo.messages.Message} is received.
	 * @param message - {@link com.rem.otl.core.duo.messages.Message} to be acted upon by the partnered {@link com.rem.otl.core.duo.client.Client}. 
	 */
	public PassMessage(Message message){
		this.message = message;
	}

	/**
	 * This method should not be called because a PassMessage should never reach a {@link com.rem.otl.core.duo.client.Client}.
	 * However if a PassMessage is received, the encompassed {@link com.rem.otl.core.duo.messages.Message} is acted upon.
	 */
	@Override
	public void act(Handler handler) {
		//Act upon the enclosed message.
		this.message.act(handler);
	}

}
