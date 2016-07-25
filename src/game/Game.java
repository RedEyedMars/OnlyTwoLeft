package game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import duo.client.Client;
import duo.messages.MoveHeroMessage;
import game.environment.FunctionalSquare;
import game.environment.Square;
import game.environment.SquareAction;
import game.menu.MainMenu;
import gui.Gui;
import gui.graphics.GraphicEntity;
import gui.graphics.GraphicView;
import gui.inputs.KeyBoardListener;
import gui.inputs.MotionEvent;
import main.Hub;

public class Game extends GraphicView implements KeyBoardListener{
	protected Hero black;
	protected Hero white;

	protected Hero controlled;
	protected Hero wild;
	protected Hero focused;

	private float pointerX = 0.05f;
	private float pointerY = 0.05f;

	protected int tick = 1;

	private boolean endGame = false;
	private VisionBubble visionBubble;
	private float yAcceleration;
	private float xAcceleration;
	public Game(boolean colourToControl){

		if(colourToControl){
			black = new Hero(this,Hero.black){
				@Override
				public void move(float x, float y){
					super.move(x,y);
					Client.pass(new MoveHeroMessage(x,y));
				}
			};
			white = new Hero(this,Hero.white);
		}
		else {
			black = new Hero(this,Hero.black);
			white = new Hero(this,Hero.white){
				@Override
				public void move(float x, float y){
					super.move(x,y);
					Client.pass(new MoveHeroMessage(x,y));
				}
			};
		}
		black.setPartner(white);
		white.setPartner(black);

		addChild(Hub.map);
		Hub.map.onCreate();
		if(Hub.map.getSquares().size()>0){
			Hub.map.getSquares().get(0).setX(0f);
			Hub.map.getSquares().get(0).setY(0f);
		}
		Hub.map.moveToStart(black);
		Hub.map.moveToStart(white);
		addChild(black);
		addChild(white);

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

		visionBubble = new VisionBubble(focused,wild);
		addChild(visionBubble);
		Hub.map.setVisibleSquares(colourToControl?0:1);
	}
	public KeyBoardListener getDefaultKeyBoardListener(){
		return this;
	}

	private static final float uppderViewBorder = 0.6f;
	private static final float lowerViewBorder = 0.4f;
	@Override
	public void update(double secondsSinceLastFrame){
		super.update(secondsSinceLastFrame);
		controlled.setXAcceleration(xAcceleration);
		controlled.setYAcceleration(yAcceleration);
		handleInterceptions();
		handleViewMovement();
	}

	private void handleInterceptions(){
		List<Action<Hero>> onHandle = new ArrayList<Action<Hero>>();
		List<Action<FunctionalSquare>> onHandleSquare = new ArrayList<Action<FunctionalSquare>>();
		List<FunctionalSquare> squares = new ArrayList<FunctionalSquare>();
		List<FunctionalSquare> mapSquares = Hub.map.functionalSquares();
		for(Hero hero:new Hero[]{black,white}){
			boolean foundSafety = false;
			List<GraphicEntity> safetiesFound=new ArrayList<GraphicEntity>();
			int trueIndexFound = 0;
			for(int i=mapSquares.size()-1;i>=0;--i){
				SquareAction action = mapSquares.get(i).getOnHitAction(hero);
				if(action!=null&&
						action.requiresComplete()&&
						hero.isCompletelyWithin(mapSquares.get(i))){
					action.act(hero);
					foundSafety=true;
					trueIndexFound = i;
					safetiesFound.add(mapSquares.get(i));
					break;
				}
			}
			if(!foundSafety){
				for(int i=mapSquares.size()-1;i>=0;--i){
					SquareAction action = mapSquares.get(i).getOnHitAction(hero);
					if(action!=null&&
							action.requiresComplete()&&
							hero.isWithin(mapSquares.get(i))){
						action.act(hero);
						safetiesFound.add(mapSquares.get(i));
					}
				}
			}
			for(int i=mapSquares.size()-1;i>trueIndexFound;--i){
				SquareAction action = mapSquares.get(i).getOnHitAction(hero);
				if(action!=null&&!action.requiresComplete()&&
						hero.isWithin(mapSquares.get(i))){
					action.act(hero);
					foundSafety=false;
				}
			}
			if(!foundSafety){
				hero.setSafeties(true,safetiesFound.toArray(new GraphicEntity[0]));
			}

			/*use if more than one square can be triggered currently just the top square
				if(hero.isWithin(square)){
					Action<FunctionalSquare> herosquare = hero.getOnHitAction(square);
					if(herosquare!=null){
						onHandleSquare.add(herosquare);
					}
					Action<Hero> squarehero = square.getOnHitAction(hero);
					if(squarehero!=null){
						onHandle.add(squarehero);
					}
				}
			}
			/* 
			while(!onHandleSquare.isEmpty()){
				if(!endGame){
					//onHandleSquare.remove(0).act(squares.get(i));
				}
			}
			while(!onHandle.isEmpty()){
				if(!endGame){
					onHandle.remove(0).act(hero);
				}
			}*/
		}

	}

	private void handleViewMovement(){
		if(focused.getX()>uppderViewBorder){
			Hub.map.setX(Hub.map.getX()-(focused.getX()-uppderViewBorder));
			wild.setX(wild.getX()-(focused.getX()-uppderViewBorder));
			focused.setX(uppderViewBorder);

		}
		else if(focused.getX()<lowerViewBorder){
			Hub.map.setX(Hub.map.getX()+(lowerViewBorder-focused.getX()));;
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

	private static final float standardAcceleration = 0.075f;
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
					Hub.map.setVisibleSquares(1);
					if(!Client.isConnected()){
						controlled=white;
						visionBubble.swap();
					}
				}
				else if(focused==white){
					focused = black;
					wild = white;
					Hub.map.setVisibleSquares(0);
					if(!Client.isConnected()){
						controlled=black;
						visionBubble.swap();
					}
				}
			}
		}
	}

	@Override
	public boolean continuousKeyboard() {
		return false;
	}
}
