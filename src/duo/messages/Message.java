package duo.messages;

import java.io.Serializable;

import duo.Handler;

/**
 * The only thing that the {@link duo.Handler} has to know, on either side of the Server/Client dichotomy is that the message received has an "act" method.
 * This class serves as a way for the {@link duo.Handler} to call said act method, without it needing to know which exact message was received.
 * In this way messages are sent and received, and the commands they carry are passed along, without the Server/Client needing to incorporate any extra methods to deal with the Message's specific tasks.  
 * @author Geoffrey
 *
 */
public abstract class Message implements Serializable{
	//For Message sending.
	private static final long serialVersionUID = 2753949064349001175L;

	/**
	 * This method is for being overridden.
	 * It is called on being received by the {@link duo.client.Client}'s {@link duo.Handler}.
	 * The {@link duo.client.Client} doesn't have to worry about the act method on the Server's side, the Server will take care of the message as it sees fit.
	 * A special Message is the {@link duo.message.PassMessage}, which the server handles by passing its enclosed Message to the partner {@link duo.client.Client}.
	 * @param handler
	 */
	public abstract void act(Handler handler);
}
