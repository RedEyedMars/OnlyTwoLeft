package duo.messages;

import duo.Handler;
import duo.client.Client;

public class MoveHeroMessage extends Message{
	private static final long serialVersionUID = -3034006666938124327L;

	private static float dx=0f;
	private static float dy=0f;

	private float x;
	private float y;
	
	public MoveHeroMessage(float x, float y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public void act(Handler handler) {
		handler.getHero().getPartner().setX(handler.getHero().getPartner().getX()+x);
		handler.getHero().getPartner().setY(handler.getHero().getPartner().getY()+y);
	}
	
	public static void send(float x, float y){
		MoveHeroMessage.dx+=x;
		MoveHeroMessage.dy+=y;
		if(MoveHeroMessage.dx>0.0001f||MoveHeroMessage.dx<0.0001f||MoveHeroMessage.dy>0.0001f||MoveHeroMessage.dy<0.0001f){
			Client.pass(new MoveHeroMessage(dx,dy));
			dx=0;
			dy=0;
		}
	}
	
}
