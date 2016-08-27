package duo.messages;

import duo.Handler;
import duo.client.Client;
import game.Hero;

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

	//The limit to which the partnered Hero will move in the X axis.
	private static float x_limit=0f;
	//The limit to which the partnered Hero will move in the Y axis.
	private static float y_limit=0f;

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
		//Initialize the y variable.
		this.y = y;
	}

	/**
	 * Adds to the dx2/dy2 variables. This causes the update function to then move the partnered {@link game.Hero}.
	 */
	@Override
	public void act(final Handler handler) {
		x_limit+=x;
		y_limit+=y;
	}

	/**
	 * This method determines if the amount of distance the {@link game.Hero} will travel given the amount of time during the frames is over the amount given by the act method.
	 * If the amount of travel distance is greater than the limit then only travel the amount to the limit and not further.
	 * @param seconds - Seconds since last frame. This is the factor by which the speed is determined.
	 * @param limit - The limit of the distance to travel. If the distance calculated is greater than this limit, the method will return just the distance to the limit.
	 * @return The amount of distance that will be traveled given the number of seconds since last frame.
	 */
	private static float getTravelDistance(double seconds, float limit){
		float toTravel = (float) (seconds*0.5f);
		if(limit>0){
			if(limit-toTravel<0){
				toTravel=limit;
			}
		}
		else {
			toTravel*=-1f;
			if(limit-toTravel>0){
				toTravel=limit;
			}
		}
		return toTravel;
	}
	/**
	 * On each update, the partnered {@link game.Hero} is moved towards the limit variables which were determined by the act method. 
	 * @param seconds - Seconds since last frame. This determines how fast the {@link game.Hero} should move towards the limits.
	 * @param hero - the partnered {@link game.Hero}
	 */
	public static void update(Double seconds, Hero hero){
		if(x_limit==0f&&y_limit==0)return;
		float toTravel = getTravelDistance(seconds,x_limit);
		x_limit-= toTravel;
		hero.setX((float) (hero.getX()+toTravel));

		toTravel = getTravelDistance(seconds,y_limit);		
		y_limit-= toTravel;
		hero.setY((float) (hero.getY()+toTravel));
	}


	private static final float lookAt = 0.0000001f;
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
		if(MoveHeroMessage.dx>lookAt||MoveHeroMessage.dx<lookAt||MoveHeroMessage.dy>lookAt||MoveHeroMessage.dy<lookAt){
			//Pass the dx and dy values to the partnered Client.
			Client.pass(new MoveHeroMessage(dx,dy));
			//Reset the delta X to 0 so that future movement as if from the 0,0 position.
			dx=0;
			//Reset the delta Y to 0 so that future movement as if from the 0,0 position.
			dy=0;
		}
	}
	
	public static void reset(){
		dx=0f;
		dy=0f;

		x_limit=0f;
		y_limit=0f;
	}

}
