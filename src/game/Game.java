package game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import duo.client.Client;
import duo.messages.EndGameMessage;
import duo.messages.LoadMapMessage;
import duo.messages.MoveHeroMessage;
import duo.messages.StartGameMessage;
import duo.messages.TransitionGameMessage;
import game.environment.Square;
import game.environment.onstep.OnStepAction;
import game.environment.onstep.OnStepSquare;
import game.environment.update.UpdatableSquare;
import game.menu.MainMenu;
import game.menu.TransitionMenu;
import game.modes.GameMode;
import gui.Gui;
import gui.graphics.GraphicEntity;
import gui.graphics.GraphicView;
import gui.inputs.KeyBoardListener;
import gui.inputs.MotionEvent;
import main.Hub;
import main.Main;
import storage.Storage;

public class Game extends GraphicView{

	public static Hero black;
	public static Hero white;
	private static List<Action<Double>> updateActions = new ArrayList<Action<Double>>() ;
	private float pointerX = 0.05f;
	private float pointerY = 0.05f;

	protected int tick = 1;

	private GameMode gameMode;
	private long startTime;
	private boolean colourToControl;
	private boolean transition = false;
	private String nextMap = "";
	private boolean successful = false;
	public Game(boolean colourToControl, long seed){
		Main.randomizer = new Random(seed);
		this.colourToControl = colourToControl;
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

		if(Hub.map.getSquares().size()>0){
			OnStepSquare wildWall = new OnStepSquare(-1,0.5f,((OnStepSquare)Hub.map.getSquares().get(0)).getBlackAction());

			Hub.map.getFunctionalSquares().add(0,wildWall);
			Hub.map.onCreate();
			for(UpdatableSquare square:Hub.map.getUpdateSquares()){
				square.run();
			}
			Hub.map.getSquares().get(0).setX(0f);
			Hub.map.getSquares().get(0).setY(0f);
			Hub.map.moveToStart(black);
			Hub.map.moveToStart(white);
			addChild(black);
			addChild(white);

			Hub.map.setVisibleSquares(colourToControl?1:2);

			gameMode = Hub.map.getGameMode();
			if(gameMode!=null){
				gameMode.setup(colourToControl, black, white, wildWall);
				for(GraphicEntity e:gameMode.getAuxillaryChildren()){
					addChild(e);
				}
				startTime = System.currentTimeMillis();
			}
			

		}
	}
	public KeyBoardListener getDefaultKeyBoardListener(){
		return gameMode;
	}
	@Override
	public void update(double secondsSinceLastFrame){
		if(transition){
			enactTransition();
		}
		if(gameMode==null){
			Gui.removeOnType(gameMode);
			Hub.addLayer.clear();
			Gui.setView(new MainMenu());
		}
		if(secondsSinceLastFrame>0.1f)return;
		super.update(secondsSinceLastFrame);
		gameMode.update(secondsSinceLastFrame);
	}

	@Override
	public boolean onHover(MotionEvent event){
		pointerX = event.getX();
		pointerY = event.getY();
		return true;
	}

	public void transition(String nextMap, boolean success) {		
		transition=true;
		this.nextMap = nextMap;
		this.successful = success;
	}

	private void enactTransition(){
		int seconds = (int) ((System.currentTimeMillis()-startTime)/1000);
		int minutes = seconds/60;
		seconds = seconds-(60*minutes);
		boolean canProceed = true;
		if(Client.isConnected()){
			String previousMap = Hub.map.getName();
			Gui.removeOnType(gameMode);
			Hub.addLayer.clear();
			String nextMapName = Storage.getMapNameFromFileName(nextMap);
			if(Hub.map.getFileName()!=null){
				Client.sendMapMessage(nextMap, new TransitionGameMessage(successful,minutes,seconds,previousMap,nextMapName,colourToControl,false));
				Gui.setView(new TransitionMenu(false,successful,minutes,seconds,previousMap,nextMapName,colourToControl,true));
			}
			else {
				Client.pass(
						new LoadMapMessage(
								nextMap,
								new TransitionGameMessage(successful,minutes,seconds,previousMap,nextMapName,!colourToControl,true)));
				Gui.setView(new TransitionMenu(false,successful,minutes,seconds,previousMap,nextMapName,colourToControl,false));
			}
		}
		else {
			String previousMap = Hub.map.getName();
			Storage.loadMap(nextMap);
			Gui.setView(new TransitionMenu(false,successful,minutes,seconds,previousMap,Storage.getMapNameFromFileName(nextMap),colourToControl,canProceed));
		}
	}
	public static void addUpdateAction(Action<Double> action) {
		Game.updateActions .add(action);
	}
	public void loseGame() {
		gameMode.loseGame();
	}
	public void winGame(String nextMap) {
		gameMode.winGame(nextMap);
	}

}
