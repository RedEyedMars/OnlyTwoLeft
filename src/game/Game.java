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
import game.hero.Hero;
import game.menu.MainMenu;
import game.menu.PauseMenu;
import game.menu.TransitionMenu;
import game.mode.GameMode;
import gui.Gui;
import gui.graphics.GraphicEntity;
import gui.graphics.GraphicView;
import gui.inputs.KeyBoardListener;
import gui.inputs.MotionEvent;
import main.Hub;
import main.Main;
import storage.Storage;

public class Game extends GraphicView{

	private float pointerX = 0.05f;
	private float pointerY = 0.05f;

	protected int tick = 1;

	private GameMode gameMode;
	private Chat chat;
	private boolean colourToControl;
	private long seed;
	private boolean transition = false;
	private boolean successful = false;
	private String nextMap = "";
	private long timeSpentInGame=0;
	private PauseMenu pauseMenu;
	private boolean waiting=false;
	public Game(boolean colourToControl, long seed){
		
		addChild(Hub.music);
		MoveHeroMessage.reset();
		Main.randomizer = new Random(seed);
		this.seed = seed;
		this.colourToControl = colourToControl;
		Hero black = null;
		Hero white = null;
		gameMode = Hub.map.getGameMode();
		if(gameMode==null) return;		

		if(Client.isConnected()){
			if(colourToControl){
				black = gameMode.createConnectedHero(true,this,Hero.BLACK_BOOL);
				white = gameMode.createConnectedHero(false,this,Hero.WHITE_BOOL);
			}
			else {
				white = gameMode.createConnectedHero(true,this,Hero.WHITE_BOOL);
				black = gameMode.createConnectedHero(false,this,Hero.BLACK_BOOL);				
			}
		}
		else {
			black = gameMode.createHero(this,Hero.BLACK_BOOL);
			white = gameMode.createHero(this,Hero.WHITE_BOOL);
		}
		black.setPartner(white);
		white.setPartner(black);
		Hub.setHeroes(black,white);
		addChild(Hub.map);

		if(Hub.map.getSquares().size()>0){
			OnStepSquare wildWall = new OnStepSquare(-1,0.5f,((OnStepSquare)Hub.map.getSquares().get(0)).getBlackAction());
			Hub.map.getFunctionalSquares().add(0,wildWall);
			chat = new Chat(Hub.TOP_LAYER);
			chat.reposition(0.03f, 0.03f);
			//addChild(chat);
			gameMode.setup(this,colourToControl, wildWall);
			for(GraphicEntity e:gameMode.getAuxillaryChildren()){
				addChild(e);
			}
			Hub.map.onCreate();
			for(UpdatableSquare square:Hub.map.getUpdateSquares()){
				square.run();
			}
			Hub.map.getSquares().get(0).reposition(0f,0f);
			Hub.map.moveToStart(black);
			Hub.map.moveToStart(white);
			addChild(black);
			addChild(white);
			Hub.setHeroes(black, white);

			Hub.map.setVisibleSquares(colourToControl==Hero.BLACK_BOOL?Hero.BLACK_INT:
				colourToControl==Hero.WHITE_BOOL?Hero.WHITE_INT:Hero.BOTH_INT);

		}
	}
	public KeyBoardListener getDefaultKeyBoardListener(){
		return gameMode;
	}
	private double delay = 0.0;
	private boolean hasDelay = false;
	@Override
	public void update(double secondsSinceLastFrame){
		if(hasDelay){
			delay+=secondsSinceLastFrame;
			if(delay<0.1)return;
			delay-=0.1;
		}
		if(transition){
			enactTransition();
		}
		if(gameMode==null){
			Gui.removeOnType(gameMode);
			Hub.renderer.clearAdditions();
			Gui.setView(new MainMenu());
		}
		if((pauseMenu!=null&&pauseMenu.isPaused()) || waiting /*||secondsSinceLastFrame>0.1f*/)return;
		timeSpentInGame+=secondsSinceLastFrame*1000;
		for(Hero hero:Hub.getBothHeroes()){
			hero.update(secondsSinceLastFrame);
		}
		Hub.map.update(secondsSinceLastFrame);
		gameMode.update(secondsSinceLastFrame);
		chat.update(secondsSinceLastFrame);
	}

	@Override
	public boolean onHover(MotionEvent event){
		pointerX = event.getX();
		pointerY = event.getY();
		return super.onHover(event);
	}
	@Override
	public boolean onClick(MotionEvent e){
		if(gameMode!=null){
			return gameMode.onClick(e);
		}
		else return super.onClick(e);
	}

	public void transition(String nextMap, boolean success) {		
		transition=true;
		this.nextMap = nextMap;
		this.successful = success;
	}

	private void enactTransition(){
		String previousMapName = Hub.map.getName();
		Gui.removeOnType(gameMode);
		Hub.renderer.clearAdditions();
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
	public void loseGame(boolean heroColour) {
		gameMode.loseGame(heroColour);
	}
	public void winGame(boolean heroColour,String nextMap) {
		gameMode.winGame(heroColour,nextMap);
	}
	public long getTimeSpent() {
		return timeSpentInGame;
	}
	public void pause() {
		if(pauseMenu==null){
			pauseMenu = new PauseMenu(this);
			addChild(pauseMenu);
		}
		pauseMenu.pause();
		Gui.giveOnClick(pauseMenu);
		Gui.giveOnType(pauseMenu);
	}
	public void restart() {
		Hub.getHero(colourToControl).move(
				Hub.map.getStartingXPosition(0)-(-Hub.map.getX()+Hub.getHero(colourToControl).getX()), 
				Hub.map.getStartingYPosition(0)-(-Hub.map.getY()+Hub.getHero(colourToControl).getY()));
		final float theirX=Hub.getHero(!colourToControl).getX()-Hub.map.getX();
		final float theirY =Hub.getHero(!colourToControl).getY()-Hub.map.getY();
		//unpause();
		waiting=true;
		Hub.restartMap(new Action<Object>(){
			@Override
			public void act(Object subject) {
				Game game = new Game(colourToControl,seed);
				Gui.setView(game);
				Hub.getHero(!colourToControl).reposition(theirX,theirY);
				game.update(timeSpentInGame/1000.0);
				waiting=false;
			}
		});
	}

	public Chat getChatBox(){
		return chat;
	}
}
