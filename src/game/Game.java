package game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import duo.client.Client;
import duo.messages.MoveHeroMessage;
import game.environment.OnStepSquare;
import game.environment.Square;
import game.environment.UpdatableSquare;
import game.environment.OnStepAction;
import game.menu.MainMenu;
import gui.Gui;
import gui.graphics.GraphicEntity;
import gui.graphics.GraphicView;
import gui.inputs.KeyBoardListener;
import gui.inputs.MotionEvent;
import main.Hub;

public class Game extends GraphicView implements KeyBoardListener{

	private static final float uppderViewBorder = 0.6f;
	private static final float lowerViewBorder = 0.4f;
	private static final float standardAcceleration = 0.075f;

	protected Hero black;
	protected Hero white;

	protected Hero controlled;
	protected Hero wild;
	protected Hero focused;

	private float pointerX = 0.05f;
	private float pointerY = 0.05f;

	protected int tick = 1;

	private boolean endGame = false;
	private float yAcceleration;
	private float xAcceleration;
	private OnStepSquare wildWall=new OnStepSquare(1,0.5f,OnStepAction.getAction(1));
	public Game(boolean colourToControl){

		if(Client.isConnected()){
			if(colourToControl){
				black = new Hero(this,Hero.black){
					@Override
					public void move(float x, float y){
						super.move(x,y);
						Client.pass(new MoveHeroMessage(x,y));
					}
				};
				white = new Hero(this,Hero.white){
					@Override
					public void move(float x, float y){
					}
				};
			}
			else {
				black = new Hero(this,Hero.black){
					@Override
					public void move(float x, float y){
					}
				};
				white = new Hero(this,Hero.white){
					@Override
					public void move(float x, float y){
						super.move(x,y);
						Client.pass(new MoveHeroMessage(x,y));
					}
				};
			}
		}
		else {
			black = new Hero(this,Hero.black);
			white = new Hero(this,Hero.white);
		}
		black.setPartner(white);
		white.setPartner(black);

		addChild(Hub.map);
		Hub.map.getFunctionalSquares().add(0,wildWall);
		Hub.map.setup(colourToControl,black,white);
		for(GraphicEntity e:Hub.map.getAuxillaryChildren()){
			addChild(e);
		}
		if(colourToControl==true/*black*/){
			controlled = black;
			wild = white;
			focused = black;
		}
		else {
			controlled = white;
			wild = black;
			focused = white;			
		}
	}
	public KeyBoardListener getDefaultKeyBoardListener(){
		return this;
	}
	@Override
	public void update(double secondsSinceLastFrame){
		super.update(secondsSinceLastFrame);
		controlled.setXAcceleration(xAcceleration);
		controlled.setYAcceleration(yAcceleration);
		handleInterceptions();
		handleViewMovement();
	}

	private void handleInterceptions(){
		List<OnStepSquare> mapSquares = Hub.map.getFunctionalSquares();
		for(Hero hero:new Hero[]{black,white}){
			List<GraphicEntity> safetiesFound = new ArrayList<GraphicEntity>();
			List<OnStepAction> onHandle = new ArrayList<OnStepAction>();
			List<Boolean> safeties            = new ArrayList<Boolean>();
			boolean isWithinSafety = true;
			for(int i=mapSquares.size()-1;i>=0;--i){
				OnStepAction action = mapSquares.get(i).getOnHitAction(hero);
				if(action!=null){
					if(hero.isWithin(mapSquares.get(i))){

						safetiesFound.add(mapSquares.get(i));
						safeties.add(action.isSafe());
						if(action.isSafe()){
							action.act(hero);
							if(hero.isCompletelyWithin(mapSquares.get(i))){
								break;
							}
						}
						else {
							onHandle.add(action);
						}
						isWithinSafety = false;
					}
				}
			}

			if(!isWithinSafety){
				if(hero.handleWalls(safetiesFound,safeties)){// is bumping
					onHandle.get(0).act(hero);
				}
				else {// is Safe

				}
			}
		}

	}

	private void handleViewMovement(){
		if(focused.getX()>uppderViewBorder){
			Hub.map.setX(Hub.map.getX()-(focused.getX()-uppderViewBorder));
			wild.setX(wild.getX()-(focused.getX()-uppderViewBorder));
			focused.setX(uppderViewBorder);
		}
		else if(focused.getX()<lowerViewBorder){
			Hub.map.setX(Hub.map.getX()+(lowerViewBorder-focused.getX()));
			wild.setX(wild.getX()+(lowerViewBorder-focused.getX()));
			focused.setX(lowerViewBorder);
		}
		if(focused.getY()>uppderViewBorder){
			Hub.map.setY(Hub.map.getY()-(focused.getY()-uppderViewBorder));
			wild.setY(wild.getY()-(focused.getY()-uppderViewBorder));
			focused.setY(uppderViewBorder);
		}
		else if(focused.getY()<lowerViewBorder){
			Hub.map.setY(Hub.map.getY()+(lowerViewBorder-focused.getY()));
			wild.setY(wild.getY()+(lowerViewBorder-focused.getY()));
			focused.setY(lowerViewBorder);
		}
		wildWall.setX(wild.getX()-0.1f);
		wildWall.setY(wild.getY()-0.1f);
	}

	public Hero getHero() {
		return controlled;
	}

	@Override
	public boolean onHover(MotionEvent event){
		pointerX = event.getX();
		pointerY = event.getY();
		return true;
	}

	public void endGame(){
		Gui.removeOnType(this);
		Hub.addLayer.clear();
		Gui.setView(new MainMenu());
	}

	@Override
	public void keyCommand(boolean b, char c, int keycode) {
		if(b==KeyBoardListener.DOWN){
			if('a'==c){
				xAcceleration = -standardAcceleration;
			}
			if('d'==c){
				xAcceleration = standardAcceleration;
			}
			if('w'==c){
				yAcceleration = standardAcceleration;
			}
			if('s'==c){
				yAcceleration = -standardAcceleration;
			}
			else if(!Client.isConnected()){
				if(keycode==200){//up
					controlled.getPartner().setYAcceleration(standardAcceleration);
				}
				else if(keycode==203){//left
					controlled.getPartner().setXAcceleration(-standardAcceleration);
				}
				else if(keycode==208){//down
					controlled.getPartner().setYAcceleration(-standardAcceleration);
				}
				else if(keycode==205){//right
					controlled.getPartner().setXAcceleration(standardAcceleration);
				}
			}
		}
		else if(b==KeyBoardListener.UP){
			if(32==keycode){
				xAcceleration = 0f;
			}
			else if(30==keycode){
				xAcceleration = 0f;
			}
			else if(17==keycode){
				yAcceleration = 0f;
			}
			else if(31==keycode){
				yAcceleration = 0f;
			}
			else if(57==keycode){//space
				if(focused==black){
					focused = white;
					wild = black;
					Hub.map.setVisibleSquares(2,white,black);
				}
				else if(focused==white){
					focused = black;
					wild = white;
					Hub.map.setVisibleSquares(1,black,white);
				}
			}
			else if(!Client.isConnected()){
				if(keycode==200){//up
					controlled.getPartner().setYAcceleration(0f);
				}
				else if(keycode==203){//left
					controlled.getPartner().setXAcceleration(0f);
				}
				else if(keycode==208){//down
					controlled.getPartner().setYAcceleration(0f);
				}
				else if(keycode==205){//right
					controlled.getPartner().setXAcceleration(0f);
				}
			}
		}
	}

	@Override
	public boolean continuousKeyboard() {
		return false;
	}
}
