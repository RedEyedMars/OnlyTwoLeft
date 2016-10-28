package com.rem.otl.core.duo.messages;

import com.rem.otl.core.duo.Handler;
import com.rem.otl.core.duo.client.Client;

/**
 * This {@link com.rem.otl.core.duo.messages.Message} has two purposes:
 *  - Tell the Server that this {@link com.rem.otl.core.duo.client.Client} is disconnecting.
 *  - The Server forcing this {@link com.rem.otl.core.duo.client.Client} to close, from a Server close for example. 
 * @author Geoffrey
 *
 */
public class EndConnectionMessage extends Message{
	//Serial for message sending.
	private static final long serialVersionUID = -2064522306521990853L;

	/**
	 * When the Server sends a forceful close of this {@link com.rem.otl.core.duo.client.Client}'s connection, this method forces the client to close.
	 */
	@Override
	public void act(Handler handler) {
		//Tell the Client that the Server demands the Client close.
		Client.serverEndThisConnection();
	}

}
