package game;

import java.util.ArrayList;
import java.util.List;

import duo.client.Client;
import duo.messages.MoveHeroMessage;
import game.environment.Colourable;
import game.environment.Square;
import game.environment.onstep.OnStepAction;
import game.environment.onstep.OnStepSquare;
import gui.graphics.GraphicEntity;
import main.Hub;

public class Hero extends GraphicEntity implements Colourable{

	public static byte black = 0;
	public static byte white = 1;
	private float xVel=0f;
	private float yVel=0f;
	private float xAcc=0f;
	private float yAcc=0f;
	private float deltaX=0f;
	private float deltaY=0f;

	private Game game;
	private Hero partner;

	private boolean southWallFound=false;
	private boolean northWallFound=false;
	private boolean onCorner=false;
	private int index;
	private float radius=0.01f;
	public Hero(Game game, byte colour) {
		super("heroes",1);
		index=colour;
		this.setFrame(colour*7);
		this.adjust(radius*2f, radius*2f);
		this.game = game;
	}
	@Override
	public void adjust(float x, float y){
		this.radius = (x+y)/4f;
		super.adjust(x, y);
	}
	public void setPartner(Hero hero){
		this.partner = hero;
	}
	public Hero getPartner(){
		return partner;
	}
	public boolean isBlack() {
		return index==0;
	}
	public boolean isWhite(){
		return index==1;
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
		deltaX=(float) (xVel*secondsSinceLastFrame);
		deltaY=(float) (yVel*secondsSinceLastFrame);
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
		return this.index==0?"black":index==1?"white":"OTHER";
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
	public float getXVelocity() {
		return xVel;
	}
	public float getYVelocity() {
		return yVel;
	}
	public boolean foundSouthWall() {
		if(southWallFound){
			southWallFound=false;
			return true;
		} else return false;
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
							NE=safe;
							NEs=i;
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
		//System.out.println(target+" "+NE+" "+SE+" "+SW+" "+NW);
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
		return new Object[]{0f,0f,false,null,null,null,null};
	}
	public void handleWalls(List<OnStepSquare> mapSquares){

		setX(getX()+deltaX);
		setY(getY()+deltaY);
		List<GraphicEntity> entities = new ArrayList<GraphicEntity>();
		List<OnStepAction> actions = new ArrayList<OnStepAction>();
		List<Boolean> safeties            = new ArrayList<Boolean>();
		for(int i=mapSquares.size()-1;i>=0;--i){
			OnStepAction action = mapSquares.get(i).getOnHitAction(this);
			if(action!=null){
				if(this.isWithin(mapSquares.get(i))){
					if(this.isOppositeColour(mapSquares.get(i))){
						entities.add(mapSquares.get(i));
						actions.add(null);							
						safeties.add(true);
					}
					else if(this.isSameColour(mapSquares.get(i))){
						entities.add(mapSquares.get(i));
						actions.add(OnStepAction.wall);							
						safeties.add(false);
					}
					else if(action.isPassible()){
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
		setX(getX()-deltaX);
		setY(getY()-deltaY);
		float x = (float) params[0];
		float y = (float) params[1];
		onCorner = (boolean)params[2];
		Integer 
		NWs=(Integer) params[3],
		NEs=(Integer) params[4],
		SEs=(Integer) params[5],
		SWs=(Integer) params[6];
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
		//System.out.println(NWs+" "+NEs+" "+" "+SWs+" "+SEs);
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
		float x = (float) params[0];
		float y = (float) params[1];
		subject.move(-x,-y);

		if(!subject.getOnHitAction(getPartner()).isPassible()){
			params = Hero.handleWalls(getPartner(), squaresFound, safeties);
			x = (float) params[0];
			y = (float) params[1];
			if(x!=0||y!=0){
				subject.setX(subject.getX()-x);
				subject.setY(subject.getY()-y);
				return false;
			}
		}
		squaresFound.clear();
		safeties.clear();
		for(int i=mapSquares.size()-1;i>=0;--i){
			if(subject==mapSquares.get(i))continue;
			squaresFound.add(mapSquares.get(i));
			if(mapSquares.get(i).getOnHitAction(this).isPassible()){
				safeties.add(true);
			}
			else {
				safeties.add(mapSquares.get(i).getOnHitAction(this).getIndex()==2);
			}
			if(safeties.get(safeties.size()-1)&&subject.isCompletelyWithin(mapSquares.get(i))){
				break;
			}
		}
		subject.move(0.0001f,0.0001f);
		subject.adjust(subject.getWidth()-0.0002f,subject.getHeight()-0.0002f);
		params = Hero.handleWalls(subject, squaresFound, safeties);
		x = (float) params[0];
		y = (float) params[1];	
		subject.move(x-0.0001f,y-0.0001f);
		subject.adjust(subject.getWidth()+0.0002f,subject.getHeight()+0.0002f);
		return x==0&&y==0;
	}
	public Boolean[] getColours(boolean isBlack){

		boolean myGreen=textureIndex()==2||textureIndex()==3||textureIndex()==4||textureIndex()==7;
		boolean myRed=textureIndex()==1||textureIndex()==2||textureIndex()==6||textureIndex()==7;
		boolean myBlue=textureIndex()==4||textureIndex()==5||textureIndex()==6||textureIndex()==7;
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
	public Game getGame(){
		return game;
	}
}
