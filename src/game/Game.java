package game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import game.environment.FunctionalSquare;
import game.environment.Square;
import game.menu.GraphicNumber;
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

	private float pointerX = 0.05f;
	private float pointerY = 0.05f;

	protected int tick = 1;

	private boolean endGame = false;
	public Game(){

		black = new Hero(this,Hero.black);
		white = new Hero(this,Hero.white);
		black.move(0.25f,0f);
		addChild(Hub.map);
		addChild(black);
		addChild(white);

		controlled = black;
		Hub.map.setVisibleSquares(0);
		Gui.giveOnType(this);
	}	

	@Override
	public void update(double secondsSinceLastFrame){
		super.update(secondsSinceLastFrame);
		handleInterceptions();
	}

	private void handleInterceptions(){
		List<Action<Hero>> onHandle = new ArrayList<Action<Hero>>();
		List<Action<FunctionalSquare>> onHandleSquare = new ArrayList<Action<FunctionalSquare>>();
		List<FunctionalSquare> squares = new ArrayList<FunctionalSquare>();
		for(Hero hero:new Hero[]{black,white}){
			for(FunctionalSquare square:Hub.map.functionalSquares()){
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
			while(!onHandleSquare.isEmpty()){
				if(!endGame){
					//onHandleSquare.remove(0).act(squares.get(i));
				}
			}
			while(!onHandle.isEmpty()){
				if(!endGame){
					onHandle.remove(0).act(hero);
				}
			}
		}

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
				controlled.setXAcceleration(-standardAcceleration);
			}
			if('d'==c){
				controlled.setXAcceleration(standardAcceleration);
			}
			if('w'==c){
				controlled.setYAcceleration(standardAcceleration);
			}
			if('s'==c){
				controlled.setYAcceleration(-standardAcceleration);
			}
		}
		else if(b==KeyBoardListener.UP){
			if(32==keycode){
				if(controlled.getXAcceleration()==standardAcceleration)
					controlled.setXAcceleration(0f);
			}
			else if(30==keycode){
				if(controlled.getXAcceleration()==-standardAcceleration)
					controlled.setXAcceleration(0f);
			}
			else if(17==keycode){
				if(controlled.getYAcceleration()==standardAcceleration)
					controlled.setYAcceleration(0f);
			}
			else if(31==keycode){
				if(controlled.getYAcceleration()==-standardAcceleration)
					controlled.setYAcceleration(0f);
			}
			else if(57==keycode){//space
				if(controlled==black){
					controlled = white;
					Hub.map.setVisibleSquares(1);
				}
				else if(controlled==white){
					controlled = black;
					Hub.map.setVisibleSquares(0);
				}
			}
		}
	}

	@Override
	public boolean continuousKeyboard() {
		return false;
	}
}
