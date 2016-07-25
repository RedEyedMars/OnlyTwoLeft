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

	public void backup(GraphicEntity e) {
		setSafeties(false,e);
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
	public void setSafeties(boolean safe, GraphicEntity... safetiesFound) {
		if(safetiesFound.length>0){
			boolean NW=!safe,NE=!safe,SW=!safe,SE=!safe;
			float N=1000f,E=1000f,S=1000f,W=1000f;
			
			for(GraphicEntity e:safetiesFound){
				if(getX()+getWidth()>=e.getX()&&getX()+getWidth()<=e.getX()+e.getWidth()){
					if(getY()+getHeight()>=e.getY()&&getY()+getHeight()<=e.getY()+e.getHeight()){
						NE=safe;
					}
					if(getY()<=e.getY()+e.getHeight()&&getY()>=e.getY()){
						SE=safe;
					}
				}
				if(getX()<=e.getX()+e.getWidth()&&getX()>=e.getX()){
					if(getY()<=e.getY()+e.getHeight()&&getY()>=e.getY()){
						SW=safe;
					}
					if(getY()+getHeight()>=e.getY()&&getY()+getHeight()<=e.getY()+e.getHeight()){
						NW=safe;
					}
					
				}
				
				float dx=0f;
				float dy=0f;
				if(safe){
					dx = (getX()+getWidth())-(e.getX()+e.getWidth());
				}
				else {
					dx = (getX()+getWidth())-(e.getX());
				}					
				if(dx>=0&&dx<E){
					E=dx;
				}

				if(safe){
					dx = e.getX()-getX();
				}
				else {
					dx = e.getX()-getX()+e.getWidth();
				}
				if(dx>=0&&dx<W){
					W=dx;
				}

				if(safe){
					dy=(getY()+getHeight())-(e.getY()+e.getHeight());
				}
				else {
					dy=(getY()+getHeight())-(e.getY());
				}
				if(dy>=0&&dy<N){
					N=dy;
				}
				if(safe){
					dy=-(getY())+(e.getY());
				}
				else {
					dy=-(getY())+(e.getY()+e.getHeight());
				}
				if(dy>=0&&dy<S){
					S=dy;
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
}
