package game;

import java.util.ArrayList;
import java.util.List;

import duo.client.Client;
import duo.messages.MoveHeroMessage;
import game.environment.OnStepSquare;
import game.environment.Square;
import gui.graphics.GraphicEntity;
import main.Hub;

public class Hero extends GraphicEntity{

	private static float radius = 0.010f;
	public static byte black = 0;
	public static byte white = 1;
	private float xVel=0f;
	private float yVel=0f;
	private float xAcc=0f;
	private float yAcc=0f;

	private Game game;
	private Hero partner;

	private boolean southWallFound=false;
	private boolean northWallFound=false;
	private boolean onCorner=false;
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
		return (x1>q.getX()&&x1<q.getX()+q.getWidth()&&
				y2>q.getY()&&y2<q.getY()+q.getHeight())||
				(x2>q.getX()&&x2<q.getX()+q.getWidth()&&
						y1>q.getY()&&y1<q.getY()+q.getHeight());
	}
	public boolean isCompletelyWithin(GraphicEntity e) {
		if((getX()>e.getX()&&getX()+getWidth()<e.getX()+e.getWidth()&&
				getY()>e.getY()&&getY()+getHeight()<e.getY()+e.getHeight())){
			return true;
		}
		return false;
	}

	public Action<OnStepSquare> getOnHitAction(OnStepSquare q) {
		return null;
	}

	public String getType() {
		return this.textureIndex()==0?"black":this.textureIndex()==1?"white":"OTHER";
	}

	protected void move(float x, float y) {
		setX(getX()+x);
		setY(getY()+y);
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
	public float getYVelocity() {
		return yVel;
	}
	public boolean foundSouthWall() {
		if(southWallFound){
			southWallFound = false;
			return true;
		} else return false;
	}
	public boolean foundNorthWall() {
		if(northWallFound){
			northWallFound = false;
			return true;
		} else return false;
	}
	public boolean isOnCorner() {
		if(onCorner){
			onCorner = false;
			return true;
		} else return false;
	}
	public void endGame() {
		game.endGame();
	}
	public static Object[] handleWalls(GraphicEntity target,List<GraphicEntity> squaresFound, List<Boolean> isSafes) {
		if(squaresFound.size()<=1){
			if(squaresFound.size()==0)return new Object[]{0f,0f,null,null,null,null};
			return new Object[]{0f,0f,squaresFound.get(0),null,null,null};
		}
		boolean NW=true,NE=true,SW=true,SE=true;
		GraphicEntity NWs=null,NEs=null,SWs=null,SEs=null;
		float N=1000f,E=1000f,S=1000f,W=1000f;

		for(int i=squaresFound.size()-1;i>=0;--i){
			GraphicEntity entity = squaresFound.get(i);
			boolean safe = isSafes.get(i);
			if(target.getX()+target.getWidth()>=entity.getX()&&target.getX()+target.getWidth()<=entity.getX()+entity.getWidth()){
				if(target.getY()+target.getHeight()>=entity.getY()&&target.getY()+target.getHeight()<=entity.getY()+entity.getHeight()){
					NE=safe;
					NEs=entity;
				}
				if(target.getY()<=entity.getY()+entity.getHeight()&&target.getY()>=entity.getY()){
					SE=safe;
					SEs=entity;
				}
			}
			if(target.getX()<=entity.getX()+entity.getWidth()&&target.getX()>=entity.getX()){
				if(target.getY()<=entity.getY()+entity.getHeight()&&target.getY()>=entity.getY()){
					SW=safe;
					SWs=entity;
				}
				if(target.getY()+target.getHeight()>=entity.getY()&&target.getY()+target.getHeight()<=entity.getY()+entity.getHeight()){
					NW = safe;
					NWs=entity;
				}					
			}

			float e=0f,w=0f,n=0f,s=0f;
			if(safe){
				e =(target.getX()+target.getWidth())-(entity.getX()+entity.getWidth());
				w =entity.getX()-target.getX();
				n =(target.getY()+target.getHeight())-(entity.getY()+entity.getHeight());
				s =-(target.getY())+(entity.getY());
			}
			else {
				e = (target.getX()+target.getWidth())-(entity.getX());
				w = entity.getX()+entity.getWidth()-target.getX();
				n = (target.getY()+target.getHeight())-(entity.getY());
				s = -(target.getY())+(entity.getY()+entity.getHeight());
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
		//System.out.println(NE+" "+SE+" "+SW+" "+NW);
		if(!(NE&&NW&&SE&&SW)&&(NE||NW||SE||SW)){
			float x = 0f;
			float y = 0f;
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
				if(!NE&&!SE){
					x=-E;
					if(!NE&&!NW){
						y=-N;
					}
					else if(!SW&&!SE){
						y=S;
					}
				}
				else if(!NW&&!SW){
					x=W;
					if(!NE&&!NW){
						y=-N;
					}
					else if(!SW&&!SE){
						y=S;
					}
				}
				else if(!NE&&!NW){
					y=-N;
				}
				else if(!SW&&!SE){
					y=S;
				}//
				else if(!NW&&!SE){
					if(N<=S){
						y=-N;
					}
					else {
						y=S;
					}
					if(E<=W){
						x=-E;
					}
					else {
						x=W;
					}
				}
				else if(!SW&&!NE){
					if(N<=S){
						y=-N;
					}
					else {
						y=S;
					}
					if(E<=W){
						x=-E;
					}
					else {
						x=W;
					}
				}
			}
			return new Object[]{x,y,NEs,NWs,SEs,SWs};
		}
		return new Object[]{0f,0f,null,null,null,null};
	}
	public void handleWalls(List<GraphicEntity> squaresFound,List<Boolean> safeties){
		Object[] params = Hero.handleWalls(this, squaresFound, safeties);
		float x = (float) params[0];
		float y = (float) params[1];
		OnStepSquare 
		NEs=(OnStepSquare) params[2],
		NWs=(OnStepSquare) params[3],
		SEs=(OnStepSquare) params[4],
		SWs=(OnStepSquare) params[5];		
		move(x+(Math.signum(x)*0.001f),y+(Math.signum(y)*0.001f));
		if(y!=0){
			yVel=0;
			if(y>0){
				southWallFound=true;
			}
			if(y<0){
				northWallFound=true;
			}
		}
		if(x!=0){
			xVel=0;
		}
		if(NEs!=null){
			NEs.getOnHitAction(this).act(this);
		}
		if(NWs!=null&&NWs!=NEs){
			NWs.getOnHitAction(this).act(this);
		}
		if(SEs!=null&&SEs!=NWs&&SEs!=NEs){
			SEs.getOnHitAction(this).act(this);
		}
		if(SWs!=null&&SWs!=NWs&&SWs!=NEs&&SWs!=SEs){
			SWs.getOnHitAction(this).act(this);
		}
	}
	public boolean push(Square subject) {
		List<GraphicEntity> squaresFound = new ArrayList<GraphicEntity>();
		List<Boolean> safeties = new ArrayList<Boolean>();
		List<OnStepSquare> mapSquares = Hub.map.getFunctionalSquares();
		squaresFound.add(subject);
		safeties.add(false);
		squaresFound.add(mapSquares.get(0));
		safeties.add(true);
		squaresFound.add(mapSquares.get(1));
		safeties.add(true);
		Object[] params = Hero.handleWalls(this, squaresFound, safeties);
		float x = (float) params[0];
		float y = (float) params[1];
		subject.setX(subject.getX()-x);
		subject.setY(subject.getY()-y);

		params = Hero.handleWalls(getPartner(), squaresFound, safeties);
		x = (float) params[0];
		y = (float) params[1];
		if(x!=0||y!=0){
			subject.setX(subject.getX()-x);
			subject.setY(subject.getY()-y);
			return false;
		}
		squaresFound.clear();
		safeties.clear();
		for(int i=mapSquares.size()-1;i>=0;--i){
			if(subject==mapSquares.get(i))continue;
			squaresFound.add(mapSquares.get(i));
			if(mapSquares.get(i).getOnHitAction(this).isSafe()){
				safeties.add(true);
			}
			else {
				safeties.add(mapSquares.get(i).getOnHitAction(this).getIndex()==2);
			}
			if(subject.isCompletelyWithin(mapSquares.get(i))){
				break;
			}
		}

		params = Hero.handleWalls(subject, squaresFound, safeties);
		x = (float) params[0];
		y = (float) params[1];	
		subject.setX(subject.getX()+x);
		subject.setY(subject.getY()+y);
		return x==0&&y==0;
	}
}
