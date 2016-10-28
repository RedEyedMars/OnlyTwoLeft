package com.rem.otl.core.game.hero;

import java.util.ArrayList;
import java.util.List;

import com.rem.otl.core.duo.client.Client;
import com.rem.otl.core.duo.messages.MoveHeroMessage;
import com.rem.otl.core.game.Action;
import com.rem.otl.core.game.Game;
import com.rem.otl.core.game.environment.Colourable;
import com.rem.otl.core.game.environment.Square;
import com.rem.otl.core.game.environment.onstep.HazardOnStepAction;
import com.rem.otl.core.game.environment.onstep.OnStepAction;
import com.rem.otl.core.game.environment.onstep.OnStepSquare;
import com.rem.otl.core.gui.graphics.GraphicEntity;
import com.rem.otl.core.main.Hub;

public class Hero extends GraphicEntity implements Colourable{

	public final static byte BLACK_COLOUR = 0;
	public final static byte WHITE_COLOUR = 7;	
	public final static byte BOTH_INT = 3;
	public final static byte BLACK_INT = 0;
	public final static byte WHITE_INT = 1;
	public final static boolean BLACK_BOOL = true;
	public final static boolean WHITE_BOOL = false;
	private float deltaX=0f;
	private float deltaY=0f;
	private float velocityX=0f;
	private float velocityY=0f;
	private float accelerationX=0f;
	private float accelerationY=0f;

	private Game game;
	private Hero partner;

	private boolean southWallFound=false;
	private boolean northWallFound=false;
	private boolean onCorner=false;
	private int index;
	private float radius=0.01f;
	private boolean isJumping = false;
	public Hero(Game game, boolean colour) {
		this("heroes",game,colour);
	}
	public Hero(String texture,Game game, boolean colour) {
		super(texture,Hub.MID_LAYER);
		if(colour==BLACK_BOOL){
			index=BLACK_INT;
			this.setFrame(BLACK_COLOUR);
		}
		else if(colour==WHITE_BOOL){
			index=WHITE_INT;
			this.setFrame(WHITE_COLOUR);
		}
		this.resize(radius*2f, radius*2f);
		this.game = game;
	}
	@Override
	public void resize(float x, float y){
		this.radius = (x+y)/4f;
		super.resize(x, y);
	}
	public void setPartner(Hero hero){
		this.partner = hero;
	}
	public Hero getPartner(){
		return partner;
	}
	public boolean isBlack() {
		return index==BLACK_INT;
	}
	public boolean isWhite(){
		return index==WHITE_INT;
	}
	@Override
	public void update(double secondsSinceLastFrame){
		velocityX=velocityX*0.9f+accelerationX;
		if(Math.abs(velocityX)>2f){
			velocityX=Math.signum(velocityX)*2f;
		}
		velocityY=velocityY*0.9f+accelerationY;
		if(Math.abs(velocityY)>2f){
			velocityY=Math.signum(velocityY)*2f;
		}
		deltaX=(float) (velocityX*secondsSinceLastFrame);
		deltaY=(float) (velocityY*secondsSinceLastFrame);
		if(deltaX>0.02f)deltaX=0.02f;
		if(deltaY>0.02f)deltaY=0.02f;
		if(deltaX<-0.02f)deltaX=-0.02f;
		if(deltaY<-0.02f)deltaY=-0.02f;
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
		return this.index==BLACK_INT?"black":index==WHITE_INT?"white":"OTHER";
	}

	public void move(float x, float y) {
		reposition(getX()+x,getY()+y);
	}
	public Game getGame(){
		return game;
	}

	public float getDeltaX() {
		return deltaX;
	}
	public float getDeltaY() {
		return deltaY;
	}
	public void setDeltaX(float dx) {
		this.deltaX=dx;
	}
	public void setDeltaY(float dy) {
		this.deltaY=dy;
	}
	public void setXVelocity(float dx) {
		velocityX = dx;
	}
	public void setYVelocity(float dy) {
		velocityY = dy;
	}
	public void setXAcceleration(float dx) {
		accelerationX=dx;
	}
	public void setYAcceleration(float dy) {
		accelerationY=dy;
	}
	public float getXAcceleration() {
		return accelerationX;
	}
	public float getYAcceleration() {
		return accelerationY;
	}
	public float getXVelocity() {
		return velocityX;
	}
	public float getYVelocity() {
		return velocityY;
	}
	public boolean foundSouthWall() {
		if(southWallFound){
			southWallFound=false;
			return true;
		} else return false;
	}
	public boolean isSouthWallTouching(){
		return southWallFound;
	}
	public boolean foundNorthWall() {
		if(northWallFound){
			northWallFound=false;
			return true;
		} else return false;
	}
	public boolean isOnCorner() {
		if(onCorner){
			onCorner = false;
			return true;
		} else return false;
	}
	public static Object[] handleWalls(GraphicEntity target,List<GraphicEntity> squaresFound, List<Boolean> isSafes) {
		if(squaresFound.size()<=1){
			if(squaresFound.size()==0)return new Object[]{0f,0f,false,null,null,null,null};
			return new Object[]{0f,0f,false,0,null,null,null};
		}
		boolean NW=true,NE=true,SW=true,SE=true;
		Integer NWs=null,NEs=null,SWs=null,SEs=null;
		float N=1000f,E=1000f,S=1000f,W=1000f;

		for(int i=squaresFound.size()-1;i>=0;--i){
			GraphicEntity bigHorz = squaresFound.get(i);
			GraphicEntity bigVert = squaresFound.get(i);
			GraphicEntity smallHorz = target;
			GraphicEntity smallVert = target;
			if(smallHorz.getWidth()>bigHorz.getWidth()){
				smallHorz=bigHorz;
				bigHorz=target;
			}
			if(smallVert.getHeight()>bigVert.getHeight()){
				smallVert=bigVert;
				bigVert=target;
			}

			float smallLeft = smallHorz.getX();
			float smallRight = smallLeft+smallHorz.getWidth();
			float smallDown = smallVert.getY();
			float smallUp = smallDown+smallVert.getHeight();
			float bigLeft = bigHorz.getX();
			float bigRight = bigLeft+bigHorz.getWidth();
			float bigDown = bigVert.getY();
			float bigUp = bigDown+bigVert.getHeight();
			boolean safe = isSafes.get(i);
			if(smallRight>=bigLeft&&smallRight<=bigRight){
				if(smallUp>=bigDown&&smallUp<=bigUp){
					if(smallVert==target){
						if(smallHorz==target){
							NEs=i;
							NE=safe;
						}
						else {
							NW=safe;
							NWs=i;
						}
					}
					else {
						if(smallHorz==target){
							SE=safe;
							SEs=i;
						}
						else {
							SW=safe;
							SWs=i;
						}
					}
				}
				if(smallDown<=bigUp&&smallDown>=bigDown){
					if(smallVert==target){
						if(smallHorz==target){
							SE=safe;
							SEs=i;
						}
						else {
							SW=safe;
							SWs=i;
						}
					}
					else {
						if(smallHorz==target){
							NE=safe;
							NEs=i;
						}
						else {
							NW=safe;
							NWs=i;
						}
					}
				}
			}
			if(smallLeft<=bigRight&&smallLeft>=bigLeft){
				if(smallDown<=bigUp&&smallDown>=bigDown){
					if(smallVert==target){
						if(smallHorz==target){
							SW=safe;
							SWs=i;
						}
						else {
							SE=safe;
							SEs=i;
						}
					}
					else {
						if(smallHorz==target){
							NW=safe;
							NWs=i;
						}
						else {
							NE=safe;
							NEs=i;
						}
					}
				}
				if(smallUp>=bigDown&&smallUp<=bigUp){
					if(smallVert==target){
						if(smallHorz==target){
							NW=safe;
							NWs=i;
						}
						else {
							NE=safe;
							NEs=i;
						}
					}
					else {
						if(smallHorz==target){
							SW=safe;
							SWs=i;
						}
						else {
							SE=safe;
							SEs=i;
						}
					}
				}				
			}

			float e=0f,w=0f,n=0f,s=0f;
			if(safe){
				e = smallRight-bigRight;
				w = bigLeft-smallLeft;
				n = smallUp-bigUp;
				s = bigDown-smallDown;
			}
			else {
				e = smallRight-bigLeft;
				w = bigRight-smallLeft;
				n = smallUp-bigDown;
				s = bigUp-smallDown;
			}

			if(smallVert==target){
				if(smallHorz==target){
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
				else {
					if(w>=0&&w<E){
						E=w;
					}
					if(e>=0&&e<W){
						W=e;
					}
					if(n>=0&&n<N){
						N=n;
					}
					if(s>=0&&s<S){
						S=s;
					}					
				}
			}
			else{
				if(smallHorz==target){
					if(e>=0&&e<E){
						E=e;
					}
					if(w>=0&&w<W){
						W=w;
					}
					if(s>=0&&s<N){
						N=s;
					}
					if(n>=0&&n<S){
						S=n;
					}
				}
				else{
					if(w>=0&&w<E){
						E=w;
					}
					if(e>=0&&e<W){
						W=e;
					}
					if(s>=0&&s<N){
						N=s;
					}
					if(n>=0&&n<S){
						S=n;
					}
				}
			}
		}
		//System.out.println(target+" "+NW+" "+NE+" "+SW+" "+SE);
		if(!(NE&&NW&&SE&&SW)&&(NE||NW||SE||SW)){
			float x = 0f;
			float y = 0f;
			boolean onCorner = false;
			if(NE&&SE&&SW){
				if(W<=N){
					x=W;						
				}
				if(N<=W){
					y=-N;
				}
				onCorner=true;
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
				onCorner=true;
			}
			else if(NE&&NW&&SE){
				if(W<=S){
					x=W;
				}
				if(S<=W){
					y=S;
				}
				onCorner=true;
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
			return new Object[]{x,y,onCorner,NWs,NEs,SEs,SWs};
		}
		return new Object[]{0f,0f,false,NWs,NEs,SEs,SWs};
		//return new Object[]{0f,0f,false,null,null,null,null};
	}
	public void handleWalls(List<OnStepSquare> mapSquares){

		reposition(getX()+deltaX,getY()+deltaY);
		List<GraphicEntity> entities = new ArrayList<GraphicEntity>();
		List<OnStepAction> actions = new ArrayList<OnStepAction>();
		List<Boolean> safeties            = new ArrayList<Boolean>();
		for(int i=mapSquares.size()-1;i>=0;--i){
			OnStepAction action = mapSquares.get(i).getOnHitAction(this);
			if(action!=null){
				if(this.isWithin(mapSquares.get(i))){
					if(Hub.map.isLightDependent()){
						if(this.isOppositeColour(mapSquares.get(i))){
							entities.add(mapSquares.get(i));
							actions.add(null);		
							safeties.add(true);
							continue;
						}
						else if(this.isSameColour(mapSquares.get(i))){
							entities.add(mapSquares.get(i));
							actions.add(OnStepAction.wall);							
							safeties.add(false);
							continue;
						}
					}
					if(action.isPassible()){
						entities.add(mapSquares.get(i));
						actions.add(action);
						safeties.add(action.isPassible());							
					}
					else {
						if(action.targetType()==0){
							action.setTarget(mapSquares.get(i));
						}
						if(!action.resolve(this)){
							entities.add(mapSquares.get(i));
							actions.add(action);
							safeties.add(action.isPassible());
						}
					}
					if(this.isCompletelyWithin(mapSquares.get(i))){
						break;
					}
				}
			}
		}
		Object[] params = Hero.handleWalls(this, entities, safeties);
		reposition(getX()-deltaX,
			 getY()-deltaY);
		float x = (Float) params[0];
		float y = (Float) params[1];
		onCorner = (Boolean)params[2];

		move(deltaX+x,deltaY+y);
		if(y!=0){
			//yVel=0;
			if(y>0){
				southWallFound=true;
			}
			else if(y<0){
				northWallFound=true;
			}
		}
		if(x!=0){
			//xVel=0;
			//northWallFound=true;
		}
		Integer 
		NWs=(Integer) params[3],
		NEs=(Integer) params[4],
		SEs=(Integer) params[5],
		SWs=(Integer) params[6];
		//System.out.println(NWs+" "+NEs+" "+SWs+" "+SEs+(NWs!=null?actions.get(NWs):"null")+" "+(NEs!=null?actions.get(NEs):null)+" "+(SWs!=null?actions.get(SWs):"null")+" "+(SEs!=null?actions.get(SEs):"null"));
		if(NEs!=null&&actions.get(NEs)!=null){
			if(actions.get(NEs).targetType()==0){
				actions.get(NEs).setTarget((Square) entities.get(NEs));
			}
			actions.get(NEs).act(this);
		}
		if(NWs!=null&&NWs!=NEs&&actions.get(NWs)!=null){
			if(actions.get(NWs).targetType()==0){
				actions.get(NWs).setTarget((Square) entities.get(NWs));
			}
			actions.get(NWs).act(this);
		}
		if(SEs!=null&&SEs!=NWs&&SEs!=NEs&&actions.get(SEs)!=null){
			if(actions.get(SEs).targetType()==0){
				actions.get(SEs).setTarget((Square) entities.get(SEs));
			}
			actions.get(SEs).act(this);
		}
		if(SWs!=null&&SWs!=NWs&&SWs!=NEs&&SWs!=SEs&&actions.get(SWs)!=null){
			if(actions.get(SWs).targetType()==0){
				actions.get(SWs).setTarget((Square) entities.get(SWs));
			}
			actions.get(SWs).act(this);
		}
	}
	public boolean push(OnStepSquare subject) {
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
		float x = (Float) params[0];
		float y = (Float) params[1];
		subject.move(-x,-y);

		if(subject.getOnHitAction(getPartner())!=null&&!subject.getOnHitAction(getPartner()).isPassible()){
			params = Hero.handleWalls(getPartner(), squaresFound, safeties);
			x = (Float) params[0];
			y = (Float) params[1];
			if(x!=0||y!=0){
				subject.reposition(subject.getX()-x,
						     subject.getY()-y);
				return false;
			}
		}
		squaresFound.clear();
		safeties.clear();
		for(int i=mapSquares.size()-1;i>=0;--i){
			if(subject==mapSquares.get(i))continue;
			squaresFound.add(mapSquares.get(i));
			if(mapSquares.get(i).getOnHitAction(this)!=null&&
				mapSquares.get(i).getOnHitAction(this).isPassible()){
				safeties.add(true);
			}
			else {
				safeties.add(mapSquares.get(i).getOnHitAction(this) instanceof HazardOnStepAction);
			}
			if(safeties.get(safeties.size()-1)&&subject.isCompletelyWithin(mapSquares.get(i))){
				break;
			}
		}
		subject.move(0.0001f,0.0001f);
		subject.resize(subject.getWidth()-0.0002f,subject.getHeight()-0.0002f);
		params = Hero.handleWalls(subject, squaresFound, safeties);
		x = (Float) params[0];
		y = (Float) params[1];	
		subject.move(x-0.0001f,y-0.0001f);
		subject.resize(subject.getWidth()+0.0002f,subject.getHeight()+0.0002f);
		return x==0&&y==0;
	}
	public Boolean[] getColours(boolean isBlack){

		boolean myGreen=getFrame()==2||getFrame()==3||getFrame()==4||getFrame()==7;
		boolean myRed=getFrame()==1||getFrame()==2||getFrame()==6||getFrame()==7;
		boolean myBlue=getFrame()==4||getFrame()==5||getFrame()==6||getFrame()==7;
		return new Boolean[]{myRed, myGreen, myBlue};
	}
	public void addToColour(Colourable other) {
		Boolean[] theirColour = other.getColours(isBlack());
		if(theirColour==null)return;
		boolean theirRed = theirColour[0];
		boolean theirGreen = theirColour[1];
		boolean theirBlue = theirColour[2];

		Boolean[] colour = getColours(isBlack());
		if(colour==null)return;
		boolean myRed = colour[0];		
		boolean myGreen = colour[1];
		boolean myBlue = colour[2];		

		if(isBlack()){
			if(!theirGreen&&!theirRed&&!theirBlue){
				myGreen=false;
				myRed=false;
				myBlue=false;
			}
			else {
				myGreen=theirGreen&&!myGreen||myGreen;
				myRed=theirRed&&!myRed||myRed;
				myBlue=theirBlue&&!myBlue||myBlue;
			}
		}
		else if(isWhite()){
			myGreen = !theirGreen;
			myRed = !theirRed;
			myBlue = !theirBlue;
		}
		//System.out.println(theirGreen+","+theirRed+","+theirBlue+" "+myGreen+","+myRed+","+myBlue);
		setColour(myRed,myGreen,myBlue);
	}
	@Override
	public void setColour(boolean red, boolean green, boolean blue) {
		int tex=0;
		if(red){
			if(green){
				if(blue){
					tex=7;
				}
				else {
					tex=2;
				}
			}
			else {
				if(blue){
					tex=6;
				}
				else {
					tex=1;
				}
			}
		}
		else {
			if(green){
				if(blue){
					tex=4;
				}
				else {
					tex=3;
				}
			}
			else {
				if(blue){
					tex=5;
				}
				else {
					tex=0;
				}
			}
		}
		setFrame(tex);
	}
	public boolean isSameColour(Colourable other) {
		Boolean[] myColour = getColours(isBlack());
		Boolean[] theirColour = other.getColours(isBlack());
		if(theirColour==null)return false;
		return myColour[0]==theirColour[0]&&myColour[1]==theirColour[1]&&myColour[2]==theirColour[2];
	}
	public boolean isOppositeColour(Colourable other) {
		Boolean[] myColour = getColours(isBlack());
		Boolean[] theirColour = other.getColours(isBlack());
		if(theirColour==null)return false;
		return myColour[0]!=theirColour[0]&&myColour[1]!=theirColour[1]&&myColour[2]!=theirColour[2];
	}
	public void jump(Action<Hero> action){
		this.isJumping = true;
	}
	public boolean isJumping() {
		return this.isJumping ;
	}
	public void setJumping(boolean jumping) {
		this.isJumping = jumping;		
	}
}
