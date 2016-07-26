package game;

import java.util.List;

import duo.client.Client;
import duo.messages.MoveHeroMessage;
import game.environment.FunctionalSquare;
import game.environment.Square;
import gui.graphics.GraphicEntity;
import main.Hub;

public class Hero extends GraphicEntity{

	private static float radius = 0.020f;
	public static byte black = 0;
	public static byte white = 1;
	private float xVel=0f;
	private float yVel=0f;
	private float xAcc=0f;
	private float yAcc=0f;

	private Game game;
	private Hero partner;
	public Hero(Game game, byte colour) {
		super("circles");
		this.setFrame(colour);
		this.adjust(radius*2f, radius*2f);
		this.game = game;
	}
	public void setPartner(Hero hero){
		this.partner = hero;
	}
	public Hero getPartner(){
		return partner;
	}
	public int getColour() {
		return textureIndex();
	}
	@Override
	public void update(double secondsSinceLastFrame){
		xVel=xVel*0.9f+xAcc;
		if(Math.abs(xVel)>2f){
			xVel=Math.signum(xVel)*2f;
		}
		yVel=yVel*0.9f+yAcc;
		if(Math.abs(xVel)>2f){
			yVel=Math.signum(yVel)*2f;
		}
		move((float)(xVel*secondsSinceLastFrame),(float)(yVel*secondsSinceLastFrame));
		super.update(secondsSinceLastFrame);
	}

	public boolean isWithin(GraphicEntity q) {
		float dx = (q.getX()+q.getWidth() /2f)-(getX()+radius);
		float dy = (q.getY()+q.getHeight()/2f)-(getY()+radius);
		double angle = Math.atan2(dy, dx);
		double x1 = Math.signum(Math.cos(angle))*radius+getX()+radius;
		double y1 = Math.signum(Math.sin(angle))*radius+getY()+radius;


		double x2 = Math.cos(angle)*radius+getX()+radius;
		double y2 = Math.sin(angle)*radius+getY()+radius;
		return (x1>=q.getX()&&x1<=q.getX()+q.getWidth()&&
				y2>=q.getY()&&y2<=q.getY()+q.getHeight())||
				(x2>=q.getX()&&x2<=q.getX()+q.getWidth()&&
				y1>=q.getY()&&y1<=q.getY()+q.getHeight());
	}
	public boolean isCompletelyWithin(GraphicEntity e) {
		if((getX()>e.getX()&&getX()+getWidth()<e.getX()+e.getWidth()&&
				getY()>e.getY()&&getY()+getHeight()<e.getY()+e.getHeight())){
			return true;
		}
		return false;
	}

	public Action<FunctionalSquare> getOnHitAction(FunctionalSquare q) {
		return null;
	}

	public String getType() {
		return this.textureIndex()==0?"black":this.textureIndex()==1?"white":"OTHER";
	}

	protected void move(float x, float y) {
		setX(getX()+x);
		setY(getY()+y);
	}

	public void moveToByPartner(float x, float y) {
		setX(getPartner().getX()-x);
		setY(getPartner().getX()-y);
	}
	public void push(Square target) {

		float dx = (target.getX()+target.getWidth() /2f)-(getX()+radius);
		float dy = (target.getY()+target.getHeight()/2f)-(getY()+radius);
		double angle = Math.atan2(dy, dx);
		if((angle<=Math.PI*3f/4f&&angle>=Math.PI/4f)||(angle>=-Math.PI*3f/4f&&angle<=-Math.PI/4f)){
			if(getY()+getHeight()<target.getY()+target.getHeight()){
				target.setY(getY()+getHeight());
			}
			else if(getY()>target.getY()){
				target.setY(getY()-target.getHeight());
			}
		}
		else {
			if(getX()+getWidth()<target.getX()+target.getWidth()){
				target.setX(getX()+getWidth());
			}
			else if(getX()>target.getX()){
				target.setX(getX()-target.getWidth());
			}
		}

	}

	public void setXVelocity(float dx) {
		xVel = dx;
	}
	public void setYVelocity(float dy) {
		yVel = dy;
	}
	public void setXAcceleration(float dx) {
		xAcc=dx;
	}
	public void setYAcceleration(float dy) {
		yAcc=dy;
	}
	public float getXAcceleration() {
		return xAcc;
	}
	public float getYAcceleration() {
		return yAcc;
	}
	public void endGame() {
		game.endGame();
	}
	public void setSafeties(List<GraphicEntity> safetiesFound, List<Boolean> isSafes) {
		if(safetiesFound.size()==0)return;
		boolean NW=true,NE=true,SW=true,SE=true;
		float N=1000f,E=1000f,S=1000f,W=1000f;

		for(int i=safetiesFound.size()-1;i>=0;--i){
			GraphicEntity entity = safetiesFound.get(i);
			boolean safe = isSafes.get(i);
			if(getX()+getWidth()>=entity.getX()&&getX()+getWidth()<=entity.getX()+entity.getWidth()){
				if(getY()+getHeight()>=entity.getY()&&getY()+getHeight()<=entity.getY()+entity.getHeight()){
					if(safe){
						NE=true;
					}
					else {
						NE=false;
					}
				}
				if(getY()<=entity.getY()+entity.getHeight()&&getY()>=entity.getY()){
					if(safe){
						SE=true;
					}
					else {
						SE=false;
					}
				}
			}
			if(getX()<=entity.getX()+entity.getWidth()&&getX()>=entity.getX()){
				if(getY()<=entity.getY()+entity.getHeight()&&getY()>=entity.getY()){
					if(safe){
						SW=true;
					}
					else {
						SW = false;
					}
				}
				if(getY()+getHeight()>=entity.getY()&&getY()+getHeight()<=entity.getY()+entity.getHeight()){
					if(safe){
						NW = true;
					}
					else {
						NW = false;
					}
				}					
			}

			float e=0f,w=0f,n=0f,s=0f;
			if(safe){
				e = (getX()+getWidth())-(entity.getX()+entity.getWidth());
				w = entity.getX()-getX();
				n =(getY()+getHeight())-(entity.getY()+entity.getHeight());
				s =-(getY())+(entity.getY());
			}
			else {
				e = (getX()+getWidth())-(entity.getX());
				w = entity.getX()-getX()+entity.getWidth();
				n =(getY()+getHeight())-(entity.getY());
				s =-(getY())+(entity.getY()+entity.getHeight());
			}					
			if(e>=0&&e<E){
				E=e;
			}
			if(w>=0&&w<W){
				W=w;
			}
			if(n>=0&&n<N){
				N=n;
			}
			if(s>=0&&s<S){
				S=s;
			}
		}		
		float x = 0f;
		float y = 0f;
		if(!(NE&&NW&&SE&&SW)){
			if(NE&&SE&&SW){
				if(W<=N){
					x=W;						
				}
				if(N<=W){
					y=-N;						
				}
			}
			else if(NE&&NW&&SW){
				if(E<=S){
					x=-E;						
				}
				if(S<=E){
					y=S;
				}
			}
			else if(NW&&SE&&SW){
				if(E<=N){
					x=-E;						
				}
				if(N<=E){
					y=-N;
				}
			}
			else if(NE&&NW&&SE){
				if(W<=S){
					x=W;
				}
				if(S<=W){
					y=S;
				}
			}
			else {
				if((!(NE||SE))){
					x=-E;
				}
				else if(!(NW||SW)){
					x=W;
				}
				if((!(NE||NW))){
					y=-N;
				}
				else if(!(SW||SE)){
					y=S;
				}
			}
		}
		move(x,y);

	}
}
