package game.environment.update;

import java.util.List;

import game.Game;
import game.Hero;
import game.environment.Square;
import game.environment.onstep.OnStepSquare;
import gui.graphics.GraphicEntity;
import main.Hub;

public class LightSourceUpdateAction extends UpdateAction{
	
	private Square blackLight = null;
	private UpdatableSquare blackReflector = null;
	private Square whiteLight = null;
	private UpdatableSquare whiteReflector = null;
	private float minDis;
	public LightSourceUpdateAction(){
		defaultState = true;
	}
	@Override
	public void act(Double seconds) {
		if(!self.isVisible())return;
		boolean horizontal = x!=0;

		Object closest =getClosest(true,horizontal);//can be null
		OnStepSquare closestBlack = null;
		Hero closestBlackHero = null;
		if(closest!=null){
			if(closest instanceof OnStepSquare){
				closestBlack=(OnStepSquare)closest;
			}
			else if(closest instanceof Hero) {
				closestBlackHero = (Hero)closest;
			}
		}
		float blackDis = minDis;
		closest = getClosest(false,horizontal);//can be null
		OnStepSquare closestWhite = null;
		Hero closestWhiteHero = null;
		if(closest!=null){
			if(closest instanceof OnStepSquare){
				closestWhite=(OnStepSquare)closest;
			}
			else if(closest instanceof Hero) {
				closestWhiteHero = (Hero)closest;
			}
		}
		float whiteDis = minDis;
		if(horizontal){
			if(blackLight==null){
				blackLight = new OnStepSquare(0,self.getColour(0),-1,blackDis,self.getHeight(),-1,-1);
				whiteLight = new OnStepSquare(0,-1,self.getColour(1),whiteDis,self.getHeight(),-1,-1);
				Hub.map.displaySquare(blackLight);
				Hub.map.displaySquare(whiteLight);
				self.addChild(blackLight);
				self.addChild(whiteLight);
				blackLight.onAddToDrawable();
				whiteLight.onAddToDrawable();
			}
			else {
				blackLight.adjust(blackDis,self.getHeight());
				whiteLight.adjust(whiteDis,self.getHeight());
				blackLight.setVisible(true);
				whiteLight.setVisible(true);
			}				
			if(x>0){
				blackLight.setX(self.getX()+self.getWidth());
				whiteLight.setX(self.getX()+self.getWidth());
			}
			else {
				blackLight.setX(self.getX()-blackDis);
				whiteLight.setX(self.getX()-whiteDis);
			}
			blackLight.setY(self.getY());
			whiteLight.setY(self.getY());
		}
		else {
			if(blackLight==null){
				blackLight = new Square(self.getColour(0),-1,self.getWidth(),blackDis);
				whiteLight = new Square(-1,self.getColour(1),self.getWidth(),whiteDis);
				Hub.map.displaySquare(blackLight);
				Hub.map.displaySquare(whiteLight);
				self.addChild(blackLight);
				self.addChild(whiteLight);
				blackLight.onAddToDrawable();
				whiteLight.onAddToDrawable();
			}
			else {
				blackLight.adjust(self.getWidth(),blackDis);
				whiteLight.adjust(self.getWidth(),whiteDis);
				blackLight.setVisible(true);
				whiteLight.setVisible(true);
			}
			if(y>0){
				blackLight.setY(self.getY()+self.getHeight());
				whiteLight.setY(self.getY()+self.getHeight());
			}
			else {
				blackLight.setY(self.getY()-blackDis);
				whiteLight.setY(self.getY()-whiteDis);
			}
			blackLight.setX(self.getX());
			whiteLight.setX(self.getX());
		}
		blackReflector = makeReflector(closestBlack,blackReflector,0,seconds);
		whiteReflector = makeReflector(closestWhite,whiteReflector,1,seconds);
		handleHero(closestBlackHero,self.getColour(0));
		handleHero(closestWhiteHero,self.getColour(1));
	}
	@Override
	public void flip() {
		y=-y;
	}
	private void handleHero(Hero hero,int colour){
		if(hero!=null){
			hero.addToColour(self);
		}
	}
	private UpdatableSquare makeReflector(OnStepSquare closest,UpdatableSquare reflector,int colour,double seconds) {
		if(self.getColour(colour)!=-1&&closest!=null&&closest.getReflectTriangle(closest.getColour(colour))!=-1){
			int reflectorShape = closest.getReflectTriangle(closest.getColour(colour));
			float reflectorX = x;
			float reflectorY = y;
			boolean show=true;
			if(reflectorShape==2){
				if(y>0||x<0){
					show=false;
				}
				else {
					reflectorX=y;
					reflectorY=x;
				}
			}
			else if(reflectorShape==3){
				if(y>0||x>0){
					show=false;
				}
				else {
					reflectorX=-y;
					reflectorY=-x;
				}
			}
			else if(reflectorShape==4){
				if(y<0||x<0){
					show=false;
				}
				else {
					reflectorX=-y;
					reflectorY=-x;
				}
			}
			else if(reflectorShape==5){
				if(y<0||x>0){
					show=false;
				}
				else {
					reflectorX=y;
					reflectorY=x;
				}
			}

			if(show){
				if(reflector==null){
					if(colour==0){
						reflector=new UpdatableSquare(reflectorShape,
								self.getColour(colour),-1,
								closest.getWidth(),closest.getHeight(),getIndex(),reflectorX,reflectorY,-1,-1);
					}
					else if(colour==1){
						reflector=new UpdatableSquare(reflectorShape,
								-1,self.getColour(colour),
								closest.getWidth(),closest.getHeight(),getIndex(),reflectorX,reflectorY,-1,-1);

					}
					Hub.map.displaySquare(reflector);
					self.addChild(reflector);
					reflector.onAddToDrawable();
				}
				else {
					reflector.setFrame(self.getColour(colour));
					reflector.setShape(reflectorShape);
					reflector.adjust(closest.getWidth(),closest.getHeight());
					reflector.getAction().setFloats(reflectorX, reflectorY);
				}
				reflector.setX(closest.getX());
				reflector.setY(closest.getY());
				reflector.setVisible(true);
				reflector.getAction().act(seconds);
			}
			else if(reflector!=null){
				reflector.setVisible(false);
			}
		}
		else if(reflector!=null){
			reflector.setVisible(false);
		}
		return reflector;
	}
	private GraphicEntity getClosest(boolean black, boolean horizontal){
		List<OnStepSquare> squares = Hub.map.getFunctionalSquares();
		GraphicEntity stopSquare = null;
		minDis=1000f;
		/*Hero hero=null;
		if(black){
			hero=Game.black;
		}
		else {
			hero=Game.white;
		}*/
		for(Hero hero:new Hero[]{Game.black,Game.white}){
			if(hero!=null){
				if(horizontal&&
						(self.getY()+self.getHeight()/2f>=hero.getY()&&
						self.getY()+self.getHeight()/2f<=hero.getY()+hero.getHeight()||
						hero.getY()+hero.getHeight()/2f>=self.getY()&&
						hero.getY()+hero.getHeight()/2f<=self.getY()+self.getHeight())){
					if(x>0&&hero.getX()-(self.getX()+self.getWidth())<minDis&&
							self.getX()+self.getWidth()<=hero.getX()){
						minDis=hero.getX()-(self.getX()+self.getWidth());
						stopSquare=hero;
					}
					else if(x<0&&self.getX()-(hero.getX()+hero.getWidth())<minDis&&
							hero.getX()+hero.getWidth()<=self.getX()){
						minDis=self.getX()-(hero.getX()+hero.getWidth());
						stopSquare=hero;
					}
				}
				else if(!horizontal&&
						(self.getX()+self.getWidth()/2f>=hero.getX()&&
						self.getX()+self.getWidth()/2f<=hero.getX()+hero.getWidth()||
						hero.getX()+hero.getWidth()/2f>=self.getX()&&
						hero.getX()+hero.getWidth()/2f<=self.getX()+self.getWidth())){
					if(y>0&&hero.getY()-(self.getY()+self.getHeight())<minDis&&
							self.getY()+self.getHeight()-0.005f<=hero.getY()){
						minDis=hero.getY()-(self.getY()+self.getHeight());
						stopSquare=hero;
					}
					else if(y<0&&self.getY()-(hero.getY()+hero.getHeight())<minDis&&
							hero.getY()+hero.getHeight()-0.005f<=self.getY()){
						minDis=self.getY()-(hero.getY()+hero.getHeight());
						stopSquare=hero;
					}
				}
			}
		}
		for(int i=squares.size()-1;i>=0;--i){
			if(squares.get(i)==self)continue;
			OnStepSquare square = squares.get(i);
			if(black?(square.getBlackAction()!=null&&!square.getBlackAction().isPassible()):(square.getWhiteAction()!=null&&!square.getWhiteAction().isPassible())){
				if(horizontal&&
						(self.getY()+self.getHeight()/2f>=square.getY()&&
						self.getY()+self.getHeight()/2f<=square.getY()+square.getHeight()||
						square.getY()+square.getHeight()/2f>=self.getY()&&
						square.getY()+square.getHeight()/2f<=self.getY()+self.getHeight())){
					if(x>0&&square.getX()-(self.getX()+self.getWidth())<minDis&&
							self.getX()+self.getWidth()<=square.getX()){
						minDis=square.getX()-(self.getX()+self.getWidth());
						stopSquare=square;
					}
					else if(x<0&&self.getX()-(square.getX()+square.getWidth())<minDis&&
							square.getX()+square.getWidth()<=self.getX()){
						minDis=self.getX()-(square.getX()+square.getWidth());
						stopSquare=square;
					}
				}
				else if(!horizontal&&
						(self.getX()+self.getWidth()/2f>=square.getX()&&
						self.getX()+self.getWidth()/2f<=square.getX()+square.getWidth()||
						square.getX()+square.getWidth()/2f>=self.getX()&&
						square.getX()+square.getWidth()/2f<=self.getX()+self.getWidth())){
					if(y>0&&square.getY()-(self.getY()+self.getHeight())<minDis&&
							self.getY()+self.getHeight()<=square.getY()){
						minDis=square.getY()-(self.getY()+self.getHeight());
						stopSquare=square;
					}
					else if(y<0&&self.getY()-(square.getY()+square.getHeight())<minDis&&
							square.getY()+square.getHeight()<=self.getY()){
						minDis=self.getY()-(square.getY()+square.getHeight());
						stopSquare=square;
					}
				}
			}
		}
		return stopSquare;
	}
	@Override
	public void undo(){
		if(blackLight!=null){
			blackLight.setVisible(false);
		}
		if(whiteLight!=null){
			whiteLight.setVisible(false);
		}
		if(blackReflector!=null){
			blackReflector.setVisible(false);
		}
		if(whiteReflector!=null){
			whiteReflector.setVisible(false);
		}
	}
	@Override
	public void onDeactivate(){
		undo();
	}
	@Override
	public int getIndex() {
		return 2;
	}
	@Override
	public UpdateAction create(){
		return new LightSourceUpdateAction();
	}
}
