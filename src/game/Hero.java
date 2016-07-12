package game;

import game.environment.FunctionalSquare;
import gui.graphics.GraphicEntity;

public class Hero extends GraphicEntity{

	private static float radius = 0.025f;
	public static byte black = 0;
	public static byte white = 1;
	private float xVel=0f;
	private float yVel=0f;
	private float xAcc=0f;
	private float yAcc=0f;
	
	private Game game;
	private float previousX=0f;
	private float previousY=0f;
	public Hero(Game game, byte colour) {
		super("circles");
		this.setFrame(colour);
		this.adjust(radius*2f, radius*2f);
		this.game = game;
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

	public boolean isWithin(FunctionalSquare q) {
		float dx = (q.getX()+q.getWidth() /2f)-(getX()+radius);
		float dy = (q.getY()+q.getHeight()/2f)-(getY()+radius);
		double angle = Math.atan2(dy, dx);
		double x = Math.cos(angle)*radius+getX()+radius;
		double y = Math.sin(angle)*radius+getY()+radius;
		return (x>=q.getX()&&x<=q.getX()+q.getWidth()&&
				y>=q.getY()&&y<=q.getY()+q.getHeight());
	}
	public boolean isCompletelyWithin(FunctionalSquare q) {
		float dx = (q.getX()+q.getWidth() /2f)-(getX()+radius);
		float dy = (q.getY()+q.getHeight()/2f)-(getY()+radius);
		double angle = Math.atan2(dy, dx);
		double x = -Math.cos(angle)*radius+getX()+radius;
		double y = -Math.sin(angle)*radius+getY()+radius;
		return (x>=q.getX()&&x<=q.getX()+q.getWidth()&&
				y>=q.getY()&&y<=q.getY()+q.getHeight());
	}

	public Action<FunctionalSquare> getOnHitAction(FunctionalSquare q) {
		return null;
	}

	public String getType() {
		return this.textureIndex()==0?"black":this.textureIndex()==1?"white":"OTHER";
	}

	private void move(float x, float y) {
		previousX = getX();
		previousY = getY();
		setX(getX()+x);
		setY(getY()+y);
	}

	public void backup(GraphicEntity e) {
		
		setX(previousX);
		setY(previousY);
		xVel=0f;yVel=0f;
		xAcc=0f;yAcc=0f;
		/*
		float mx = getX()+getWidth()/2f;
		float my = getY()+getHeight()/2f;
		float ox = e.getX()+e.getWidth()/2f;
		float oy = e.getY()+e.getHeight()/2f;
		
		float angle = (float) Math.atan2(oy-my,ox-mx);
		if(angle>Math.PI/4f&&angle<Math.PI*3/4f){//down
			setY(e.getY()-getHeight());
		}
		else if(Math.abs(angle)>Math.PI*3/4f){//right
			setX(e.getX()+e.getWidth());
		}
		else if(Math.abs(angle)<Math.PI/4f){//left
			setX(e.getX()-getWidth());
		}
		else if(angle<-Math.PI/4f&&angle>-Math.PI*3f/4f){//up
			setY(e.getY()+e.getHeight());
		}*/
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
}
