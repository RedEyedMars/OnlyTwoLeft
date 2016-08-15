package duo.messages;

import duo.Handler;

/**
 * Just a blank message for sending to handlers in order to break any inputstream block.
 * @author Geoffrey
 *
 */
public class BlankMessage extends Message{
	//Serial for message sending.
	private static final long serialVersionUID = 746508151288345062L;

	/**
	 * Does nothing when received.
	 */
	@Override
	public void act(Handler handler) {
	}

}
