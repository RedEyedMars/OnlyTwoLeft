package com.rem.otl.core.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.rem.otl.core.duo.client.Client;
import com.rem.otl.core.duo.messages.ChatMessage;
import com.rem.otl.core.duo.messages.HeroEndGameMessage;
import com.rem.otl.core.duo.messages.MoveHeroMessage;
import com.rem.otl.core.duo.messages.SaveGameMessage;
import com.rem.otl.core.game.chat.Chat;
import com.rem.otl.core.game.environment.onstep.OnStepSquare;
import com.rem.otl.core.game.environment.update.UpdatableSquare;
import com.rem.otl.core.game.hero.Hero;
import com.rem.otl.core.game.menu.MainMenu;
import com.rem.otl.core.game.menu.MenuButton;
import com.rem.otl.core.game.menu.PauseMenu;
import com.rem.otl.core.game.menu.TransitionMenu;
import com.rem.otl.core.game.mode.GameMode;
import com.rem.otl.core.gui.Gui;
import com.rem.otl.core.gui.graphics.GraphicEntity;
import com.rem.otl.core.gui.graphics.GraphicView;
import com.rem.otl.core.gui.inputs.HoverEvent;
import com.rem.otl.core.gui.inputs.KeyBoardListener;
import com.rem.otl.core.gui.inputs.ClickEvent;
import com.rem.otl.core.main.Hub;
import com.rem.otl.core.storage.Storage;

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
	private GraphicView parentView;
	public Game(boolean colourToControl, long seed, GraphicView parentView){
		this(colourToControl, seed, parentView,0);
	}
	public Game(boolean colourToControl, long seed, GraphicView parentView, long startTime){

		this.parentView = parentView;
		if(Hub.music!=null){
			addChild(Hub.music);
		}
		if(Hub.map.getFileName()!=null){
			Hub.defaultMapFile = Hub.map.getFileName();
		}
		MoveHeroMessage.reset();
		Hub.randomizer = new Random(seed);
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
			if(Client.isConnected()){
				chat = new Chat(Hub.TOP_LAYER,colourToControl);
				chat.reposition(0.03f, 0.03f);
				addChild(chat);
				ChatMessage.setChatBox(chat);
				chat.setVisible(false);			

			}
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
			if(colourToControl==Hero.BLACK_BOOL){
				addChild(white);
				addChild(black);
			}
			else if(colourToControl==Hero.WHITE_BOOL){
				addChild(black);
				addChild(white);
			}
			Hub.setHeroes(black, white);

			Hub.map.setVisibleSquares(colourToControl==Hero.BLACK_BOOL?Hero.BLACK_INT:
				colourToControl==Hero.WHITE_BOOL?Hero.WHITE_INT:Hero.BOTH_INT);

			update(startTime/1000.0);
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
			Hub.handler.removeOnType(gameMode);
			Hub.renderer.clearAdditions();
			Hub.gui.setView(parentView);
		}
		if((pauseMenu!=null&&pauseMenu.isPaused()) || waiting /*||secondsSinceLastFrame>0.1f*/)return;
		timeSpentInGame+=secondsSinceLastFrame*1000;
		for(Hero hero:Hub.getBothHeroes()){
			hero.update(secondsSinceLastFrame);
		}
		Hub.map.update(secondsSinceLastFrame);
		gameMode.update(secondsSinceLastFrame);
		if(chat!=null){
			chat.update(secondsSinceLastFrame);
		}
	}

	@Override
	public boolean onHover(HoverEvent event){
		pointerX = event.getX();
		pointerY = event.getY();
		return super.onHover(event);
	}
	@Override
	public boolean onClick(ClickEvent e){
		if(chat!=null&&
				chat.getOpenChatButton().isWithin(e.getX(),e.getY())){
			chat.getOpenChatButton().performOnRelease(e);
			e.setAction(ClickEvent.ACTION_UP);
		}
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
		Hub.handler.removeOnType(gameMode);
		Hub.renderer.clearAdditions();
		String nextMapName = Storage.getMapNameFromFileName(nextMap);
		TransitionMenu menu = new TransitionMenu(this,gameMode.isCompetetive(),successful,timeSpentInGame,previousMapName,nextMapName,colourToControl,Hub.map.getFileName()!=null);
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
		Hub.gui.setView(menu);		
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
		Hub.handler.giveOnClick(pauseMenu);
		Hub.handler.giveOnType(pauseMenu);
	}
	public void restart() {

		final float theirX = (Hub.getHero(!colourToControl).getX()-Hub.map.getX());
		final float theirY = (Hub.getHero(!colourToControl).getY()-Hub.map.getY());
		if(colourToControl==Hero.BLACK_BOOL){
			Hub.getHero(Hero.BLACK_BOOL).move(
					Hub.map.getStartingXPosition(Hero.BLACK_INT)-(Hub.getHero(Hero.BLACK_BOOL).getX()-Hub.map.getX()), 
					Hub.map.getStartingYPosition(Hero.BLACK_INT)-(Hub.getHero(Hero.BLACK_BOOL).getY()-Hub.map.getY()));
		}
		else if(colourToControl==Hero.WHITE_BOOL){
			Hub.getHero(Hero.WHITE_BOOL).move(
					Hub.map.getStartingXPosition(Hero.WHITE_INT)-(Hub.getHero(Hero.WHITE_BOOL).getX()-Hub.map.getX()), 
					Hub.map.getStartingYPosition(Hero.WHITE_INT)-(Hub.getHero(Hero.WHITE_BOOL).getY()-Hub.map.getY()));
		}

		waiting=true;
		Hub.restartMap(new Action<Object>(){
			@Override
			public void act(Object subject) {
				Game game = new Game(colourToControl,seed,parentView);
				Hub.getHero(!colourToControl).reposition(theirX+Hub.map.getX(),theirY+Hub.map.getY());
				Hub.gui.setView(game);
				game.update(timeSpentInGame/1000.0);
				waiting=false;
			}
		});
	}

	public Chat getChatBox(){
		return chat;
	}
	public GraphicView getParentView() {
		return this.parentView;
	}
	public void returnToParent() {
		Client.endConnectionToTheServer();
		Hub.gui.setView(getParentView());

	}
}
