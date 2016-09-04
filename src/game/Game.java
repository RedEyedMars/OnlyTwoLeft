package game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import duo.client.Client;
import duo.messages.HeroEndGameMessage;
import duo.messages.MoveHeroMessage;
import duo.messages.SaveGameMessage;
import game.environment.onstep.OnStepSquare;
import game.environment.update.UpdatableSquare;
import game.menu.MainMenu;
import game.menu.PauseMenu;
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

	public static boolean useLightEffects = false;
	public static Hero black;
	public static Hero white;
	private static List<Action<Double>> updateActions = new ArrayList<Action<Double>>() ;
	private float pointerX = 0.05f;
	private float pointerY = 0.05f;

	protected int tick = 1;

	private GameMode gameMode;
	private boolean colourToControl;
	private long seed;
	private boolean transition = false;
	private boolean successful = false;
	private String nextMap = "";
	private long timeSpentInGame=0;
	private PauseMenu pauseMenu;
	private boolean waiting=false;
	public Game(boolean colourToControl, long seed){
		MoveHeroMessage.reset();
		Main.randomizer = new Random(seed);
		this.seed = seed;
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
				gameMode.setup(this,colourToControl, black, white, wildWall);
				for(GraphicEntity e:gameMode.getAuxillaryChildren()){
					addChild(e);
				}
			}


		}
		useLightEffects = Hub.map.isLightDependent();
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
		if((pauseMenu!=null&&pauseMenu.isPaused()) || waiting ||secondsSinceLastFrame>0.1f)return;
		timeSpentInGame+=secondsSinceLastFrame*1000;
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
		String previousMapName = Hub.map.getName();
		Gui.removeOnType(gameMode);
		Hub.addLayer.clear();
		String nextMapName = Storage.getMapNameFromFileName(nextMap);
		TransitionMenu menu = new TransitionMenu(gameMode.isCompetetive(),successful,timeSpentInGame,previousMapName,nextMapName,colourToControl,Hub.map.getFileName()!=null);
		HeroEndGameMessage.setMenu(menu);
		SaveGameMessage.setMenu(menu);

		HeroEndGameMessage.setMapNames(previousMapName,nextMapName);
		if(Hub.map.getFileName()!=null){
			HeroEndGameMessage.setNextMapFileName(nextMap);
		}
		if(Client.isConnected()){		
			HeroEndGameMessage.setAndSend(colourToControl,successful,timeSpentInGame);			
		}
		if(HeroEndGameMessage.isFinished()){
			HeroEndGameMessage.finish();
		}
		Gui.setView(menu);		
	}
	public static void addUpdateAction(Action<Double> action) {
		Game.updateActions .add(action);
	}
	public void loseGame(boolean isBlack) {
		gameMode.loseGame(isBlack);
	}
	public void winGame(boolean isBlack,String nextMap) {
		gameMode.winGame(isBlack,nextMap);
	}
	public long getTimeSpent() {
		return timeSpentInGame;
	}
	public void pause() {
		if(pauseMenu==null){
			pauseMenu = new PauseMenu(this);
			addChild(pauseMenu);
			pauseMenu.onAddToDrawable();
		}
		pauseMenu.pause();
		Gui.giveOnClick(pauseMenu);
		Gui.giveOnType(pauseMenu);
	}
	public void restart() {
		float otherX = 0f, otherY = 0f;
		if(colourToControl){
			Game.black.move(Hub.map.getStartingXPosition(0)-(-Hub.map.getX()+Game.black.getX()), 
					Hub.map.getStartingYPosition(0)-(-Hub.map.getY()+Game.black.getY()));
			otherX=Game.white.getX()-Hub.map.getX();
			otherY=Game.white.getY()-Hub.map.getY();
		}
		else {
			Game.white.move(Hub.map.getStartingXPosition(1)-(-Hub.map.getX()+Game.white.getX()), 
					Hub.map.getStartingYPosition(1)-(-Hub.map.getY()+Game.white.getY()));
			otherX=Game.black.getX()-Hub.map.getX();
			otherY=Game.black.getY()-Hub.map.getY();
		}
		final float theirX = otherX;
		final float theirY = otherY;
		//unpause();
		waiting=true;
		Hub.restartMap(new Action<Object>(){
			@Override
			public void act(Object subject) {
				Gui.setView(new Game(colourToControl,seed));
				if(colourToControl){
					Game.white.setX(theirX);
					Game.white.setY(theirY);
				}
				else {
					Game.black.setX(theirX);
					Game.black.setY(theirY);
				}
				waiting=false;
			}
		});
	}

}
