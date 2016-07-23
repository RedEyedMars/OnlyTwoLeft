package duo.messages;

import duo.Handler;

public class MoveHeroMessage extends Message{
	private static final long serialVersionUID = -3034006666938124327L;

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
	
}
