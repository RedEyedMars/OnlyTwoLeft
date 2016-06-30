package game;

import java.util.ArrayList;
import java.util.List;

import game.menu.HighscoreMenu;
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
		for(Hero p:new Hero[]{black,white}){
			for(FunctionalSquare q:Hub.map.functionalSquares()){
				if(p.isWithin(q)){
					Action<FunctionalSquare> pq = p.getOnHitAction(q);
					if(pq!=null){
						onHandleSquare.add(pq);
					}
					Action<Hero> qp = q.getOnHitAction(p);
					if(qp!=null){
						onHandle.add(qp);
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
					onHandle.remove(0).act(p);
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
		Hub.removeLayer.addAll(Hub.drawLayer);
		Gui.setView(new MainMenu());
	}

	@Override
	public void keyCommand(boolean b, char c, int keycode) {
		if(b==KeyBoardListener.DOWN){
			if('a'==c){
				controlled.setXAcceleration(-0.1f);
			}
			if('d'==c){
				controlled.setXAcceleration(0.1f);
			}
			if('w'==c){
				controlled.setYAcceleration(0.1f);
			}
			if('s'==c){
				controlled.setYAcceleration(-0.1f);
			}
		}
		else if(b==KeyBoardListener.UP){
			if(32==keycode){
				if(controlled.getXAcceleration()==0.1f)
					controlled.setXAcceleration(0f);
			}
			else if(30==keycode){
				if(controlled.getXAcceleration()==-0.1f)
					controlled.setXAcceleration(0f);
			}
			else if(17==keycode){
				if(controlled.getYAcceleration()==0.1f)
					controlled.setYAcceleration(0f);
			}
			else if(31==keycode){
				if(controlled.getYAcceleration()==-0.1f)
					controlled.setYAcceleration(0f);
			}
		}
	}

	@Override
	public boolean continuousKeyboard() {
		return false;
	}
}
