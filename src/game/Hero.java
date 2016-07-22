package game;

import game.environment.FunctionalSquare;
import game.environment.Square;
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
	private Square safeSquare;
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
		double x = Math.cos(angle)*radius+getX()+radius;
		double y = Math.sin(angle)*radius+getY()+radius;
		return (x>=q.getX()&&x<=q.getX()+q.getWidth()&&
				y>=q.getY()&&y<=q.getY()+q.getHeight());
	}
	public boolean isCompletelyWithin(GraphicEntity q) {
		double x = getX()+radius+radius;
		double y = getY()+radius+radius;
		return (getX()>=q.getX()&&x<=q.getX()+q.getWidth()&&
				getY()>=q.getY()&&y<=q.getY()+q.getHeight());
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
		if(safeSquare!=null){
			float leftBorder = safeSquare.getX();
			float rightBorder = safeSquare.getX()+safeSquare.getWidth();
			float leftBorderSafe = leftBorder;
			float rightBorderSafe = rightBorder-getWidth();
			if(e.getX()>leftBorder){
				leftBorder=e.getX();
				leftBorderSafe = e.getX()-getWidth();
			}
			if(e.getX()+e.getWidth()<rightBorder){
				rightBorder=e.getX()+e.getWidth();
				rightBorderSafe= e.getX()+e.getWidth();
			}			

			float downBorder = safeSquare.getY();
			float upBorder = safeSquare.getY()+safeSquare.getHeight();
			float downBorderSafe = downBorder;
			float upBorderSafe = upBorder-getHeight();
			if(e.getY()>downBorder){
				downBorder=e.getY();
				downBorderSafe = e.getY()-getHeight();
			}
			if(e.getY()+e.getWidth()<upBorder){
				upBorder=e.getY()+e.getHeight();
				upBorderSafe= e.getY()+e.getHeight();
			}
			if(getX()<=leftBorder){
				setX(leftBorderSafe);
			}
			else if(getX()+getWidth()>=rightBorder){
				setX(rightBorderSafe);
			}
			if(getY()<=downBorder){
				setY(downBorderSafe);
			}
			else if(getY()+getHeight()>=upBorder){
				setY(upBorderSafe);
			}
		}
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
	public void safeSquare(Square target) {
		this.safeSquare = target;
	}
}
