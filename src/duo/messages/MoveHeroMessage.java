package duo.messages;

import duo.Handler;
import duo.client.Client;

/**
 * MoveHeroMessages are {@link duo.messages.Message}'s that are sent when the {@link game.Hero} of the {@link game.Game} moves a minimum distance.
 * Its purpose is to tell the partnered {@link duo.client.Client} to move the {@link game.Hero} on its screen as well.
 * @author Geoffrey
 *
 */
public class MoveHeroMessage extends Message{
	//For Message sending.
	private static final long serialVersionUID = -3034006666938124327L;

	//The delta X coordinate, or, how much the Hero has moved on the X axis.
	private static float dx=0f;
	//The delta Y coordinate, or, how much the Hero has moved on the Y axis.
	private static float dy=0f;

	//The variable used to send the X coordinate information about the Hero moving, to the other Client.
	private float x;
	//The variable used to send the Y coordinate information about the Hero moving, to the other Client.
	private float y;
	
	/**
	 * Initializes the MoveHeroMessage with the amount of movement this {@link duo.client.Client} has moved.
	 * @param x -  the amount of distance on the X axis this {@link duo.client.Client}'s {@link game.Hero} has moved.
	 * @param y -  the amount of distance on the Y axis this {@link duo.client.Client}'s {@link game.Hero} has moved.
	 */
	public MoveHeroMessage(float x, float y) {
		//Initialize the x variable.
		this.x = x;
		//Initialize the y variable;
		this.y = y;
	}

	/**
	 * When received this {@link duo.messages.Message} causes the {@link game.Hero} partnered to this {@link duo.client.Client}'s {@link game.Hero} to move a set amount.
	 */
	@Override
	public void act(Handler handler) {
		//move the partnered hero a set amount in the x direction.
		handler.getHero().getPartner().setX(handler.getHero().getPartner().getX()+x);
		//move the partnered hero a set amoutn in the y direction.
		handler.getHero().getPartner().setY(handler.getHero().getPartner().getY()+y);
	}
	
	/**
	 * On every game tick this method is called, refreshing the distance that the hero has moved since the last tick.
	 * If the {@link game.Hero} has moved significantly, a MoveHeroMessage is sent to the partnered client so that the {@link game.Hero} can move on their screen as well.
	 * @param x - the amount of distance on the x axis this {@link duo.client.Client}'s {@link game.Hero} just moved.
	 * @param y - the amount of distance on the y axis this {@link duo.client.Client}'s {@link game.Hero} just moved.
	 */
	public static void send(float x, float y){
		//Increase the delta x from last tick.
		MoveHeroMessage.dx+=x;
		//Increase the delta y from last tick.
		MoveHeroMessage.dy+=y;
		//If the dx or dy has changed by at least 0.0001f, that is said to be a significant change and a MoveHeroMessage is passed to the partnered Client.
		if(MoveHeroMessage.dx>0.0001f||MoveHeroMessage.dx<0.0001f||MoveHeroMessage.dy>0.0001f||MoveHeroMessage.dy<0.0001f){
			//Pass the dx and dy values to the partnered Cleint.
			Client.pass(new MoveHeroMessage(dx,dy));
			//Reset the delta X to 0 so that future movement as if from the 0,0 position.
			dx=0;
			//Reset the delta Y to 0 so that future movement as if from the 0,0 position.
			dy=0;
		}
	}
	
}
