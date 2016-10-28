package com.rem.otl.core.duo.messages;

import com.rem.otl.core.duo.Handler;
import com.rem.otl.core.game.menu.MainMenu;
import com.rem.otl.core.main.Hub;

/**
 * When the {@link com.rem.otl.core.game.Game} ends (usually due to a {@link com.rem.otl.core.game.hero.Hero} dying), this {@link com.rem.otl.core.duo.messages.Message} is sent.
 * Tells the other Game to also end.
 * @author Geoffrey
 *
 */
public class EndGameMessage extends Message{
	//
	private static final long serialVersionUID = -4259968371200229807L;

	/**
	 * Tells this {@link com.rem.otl.core.game.Game} to end.
	 */
	@Override
	public void act(Handler handler) {
		//For debug purposes, tells us that the message to end the game has been received.
		if(Message.debug)System.out.println("end game");
		//Abruptly returns the Client to the main menu, this is caused by the partnered Client closing.
		Hub.gui.setView(new MainMenu());
	}

}
