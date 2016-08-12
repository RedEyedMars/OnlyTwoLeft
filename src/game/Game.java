package game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import duo.client.Client;
import duo.messages.EndGameMessage;
import duo.messages.MoveHeroMessage;
import game.environment.OnStepSquare;
import game.environment.Square;
import game.environment.UpdatableSquare;
import game.environment.OnStepAction;
import game.menu.MainMenu;
import game.modes.GameMode;
import gui.Gui;
import gui.graphics.GraphicEntity;
import gui.graphics.GraphicView;
import gui.inputs.KeyBoardListener;
import gui.inputs.MotionEvent;
import main.Hub;

public class Game extends GraphicView{

	public static Hero black;
	public static Hero white;
	private float pointerX = 0.05f;
	private float pointerY = 0.05f;

	protected int tick = 1;

	private GameMode gameMode;
	public Game(boolean colourToControl){
		if(Client.isConnected()){
			if(colourToControl){
				black = new Hero(this,Hero.black){
					@Override
					public void move(float x, float y){
						super.move(x,y);
						MoveHeroMessage.send(x,y);
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
						MoveHeroMessage.send(x,y);
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
		OnStepSquare wildWall = new OnStepSquare(-1,0.5f,Hub.map.getFunctionalSquares().get(0).getBlackAction());
		Hub.map.getFunctionalSquares().add(0,wildWall);
		Hub.map.onCreate();
		for(UpdatableSquare square:Hub.map.getUpdateSquares()){
			square.run();
		}
		if(Hub.map.getSquares().size()>0){
			Hub.map.getSquares().get(0).setX(0f);
			Hub.map.getSquares().get(0).setY(0f);
		}
		Hub.map.moveToStart(black);
		Hub.map.moveToStart(white);
		addChild(black);
		addChild(white);
	
		Hub.map.setVisibleSquares(colourToControl?1:2);
		
		gameMode = Hub.map.getGameMode();
		if(gameMode==null)endGame();
		else {
			gameMode.setup(colourToControl, black, white, wildWall);
		}
		for(GraphicEntity e:gameMode.getAuxillaryChildren()){
			addChild(e);
		}
	}
	public KeyBoardListener getDefaultKeyBoardListener(){
		return gameMode;
	}
	@Override
	public void update(double secondsSinceLastFrame){
		super.update(secondsSinceLastFrame);
		gameMode.update(secondsSinceLastFrame);
	}

	@Override
	public boolean onHover(MotionEvent event){
		pointerX = event.getX();
		pointerY = event.getY();
		return true;
	}

	public void endGame(){
		Gui.removeOnType(gameMode);
		Hub.addLayer.clear();
		Gui.setView(new MainMenu());
	}

}
