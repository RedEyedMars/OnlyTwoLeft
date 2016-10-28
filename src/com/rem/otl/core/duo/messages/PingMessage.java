package com.rem.otl.core.duo.messages;

import com.rem.otl.core.duo.Handler;

/**
 * Similar to the {@link duo.messages.BlackMessage}, this {@link com.rem.otl.core.duo.messages.Message} serves no real purpose besides releasing of the input stream.
 * It also provides a debug output.
 * @author Geoffrey
 *
 */
public class PingMessage extends Message{
	//For Message sending.
	private static final long serialVersionUID = 746508151288345062L;

	/**
	 * Issues a debug output, this is to notify that the {@link com.rem.otl.core.duo.messages.Message} has been received.
	 */
	@Override
	public void act(Handler handler) {
		//Debugging output.
		System.out.println("Client recieved a Ping");
	}

}
