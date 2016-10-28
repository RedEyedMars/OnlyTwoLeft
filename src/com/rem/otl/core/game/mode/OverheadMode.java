package com.rem.otl.core.game.mode;

import java.util.ArrayList;
import java.util.List;

import com.rem.otl.core.duo.client.Client;
import com.rem.otl.core.duo.messages.HeroEndGameMessage;
import com.rem.otl.core.duo.messages.MoveHeroMessage;
import com.rem.otl.core.game.Game;
import com.rem.otl.core.game.VisionBubble;
import com.rem.otl.core.game.environment.onstep.OnStepAction;
import com.rem.otl.core.game.environment.onstep.OnStepSquare;
import com.rem.otl.core.game.hero.ConnectedHero;
import com.rem.otl.core.game.hero.Hero;
import com.rem.otl.core.gui.graphics.GraphicEntity;
import com.rem.otl.core.gui.inputs.KeyBoardEvent;
import com.rem.otl.core.gui.inputs.KeyBoardListener;
import com.rem.otl.core.gui.inputs.ClickEvent;
import com.rem.otl.core.main.Hub;

public class OverheadMode extends GameMouseHandler implements GameMode{

	private static final float uppderViewBorder = 0.6f;
	private static final float lowerViewBorder = 0.4f;
	private static final float standardAcceleration = 0.03f;

	private Hero controlled;
	private Hero wild;
	private Hero focused;
	private VisionBubble visionBubble;
	private GraphicEntity wildWall;
	private List<GraphicEntity> auxillaryChildren = new ArrayList<GraphicEntity>();
	protected boolean colourToControl;
	protected Game game;
	@Override 
	public void setup(Game game, boolean colourToControl, GraphicEntity wildWall){
		this.game = game;
		this.colourToControl = colourToControl;
		controlled = Hub.getHero(colourToControl);
		wild = Hub.getHero(!colourToControl);
		focused = controlled;

		this.wildWall = wildWall;
		visionBubble = new VisionBubble(focused,wild);
		auxillaryChildren.add(visionBubble);

		if(Client.isConnected()){
			Client.setHero(controlled);
		}
	}


	public List<GraphicEntity> getAuxillaryChildren(){
		return auxillaryChildren;
	}
	private void handleInterceptions(){
		List<OnStepSquare> mapSquares = Hub.map.getFunctionalSquares();
		for(Hero hero:new Hero[]{focused,wild}){
			hero.handleWalls(mapSquares);
		}
	}

	private void handleViewMovement(){		
		float heroMoveX = focused.getX();
		float heroMoveY = focused.getY();
		if(focused.getX()>uppderViewBorder){
			heroMoveX = uppderViewBorder;
		}
		else if(focused.getX()<lowerViewBorder){
			heroMoveX = lowerViewBorder;
		}
		if(focused.getY()>uppderViewBorder){
			heroMoveY = uppderViewBorder;
		}
		else if(focused.getY()<lowerViewBorder){
			heroMoveY = lowerViewBorder;
		}

		Hub.map.reposition(Hub.map.getX()+(heroMoveX-focused.getX()),
				       Hub.map.getY()+(heroMoveY-focused.getY()));
		wild.reposition(wild.getX()+(heroMoveX-focused.getX()),
				    wild.getY()+(heroMoveY-focused.getY()));
		focused.reposition(heroMoveX,heroMoveY);
		wildWall.reposition(wild.getX()-0.1f,
				        wild.getY()-0.1f);
	}
	@Override
	public void loseGame(boolean colour){
		if(Client.isConnected()){
			if(colourToControl==colour){
				game.transition("Restart", false);
			}
		}
		else {
			long now = game.getTimeSpent();
			HeroEndGameMessage.setAndSend(this.colourToControl, false, now);
			HeroEndGameMessage.setAndSend(!this.colourToControl, false, now);			
			game.transition("Restart", false);
		}
	}
	@Override
	public void winGame(boolean colour,String nextMap){
		if(Client.isConnected()){
			if(colourToControl==colour){
				game.transition(nextMap, true);
			}
		}
		else {
			HeroEndGameMessage.setAndSend(colour, true, game.getTimeSpent());
			if(HeroEndGameMessage.isFinished()){
				game.transition(nextMap, true);
			}
		}
	}
	@Override
	public void update(double seconds) {
		handleViewMovement();
		handleInterceptions();	
		MoveHeroMessage.update(seconds, wild);
		visionBubble.update(seconds);
	}

	@Override
	public Hero createConnectedHero(boolean control, Game game, boolean bool) {
		return new ConnectedHero(control, game,bool);
	}


	@Override
	public Hero createHero(Game game, boolean bool) {
		return new Hero(game,bool);
	}
	@Override
	public void onType(KeyBoardEvent event) {
		if(event.keyDown()){
			if('a'==event.getChar()){
				controlled.setXAcceleration(-standardAcceleration);
			}
			else if('d'==event.getChar()){
				controlled.setXAcceleration(standardAcceleration);
			}
			else if('w'==event.getChar()){
				controlled.setYAcceleration(standardAcceleration);
			}
			else if('s'==event.getChar()){
				controlled.setYAcceleration(-standardAcceleration);
			}
			else if(event.is(KeyBoardEvent.ESCAPE)||event.is(25)||event.is(197)){
				game.pause();
			}
			else if(!Client.isConnected()){
				if(event.is(KeyBoardEvent.UP)){//up
					controlled.getPartner().setYAcceleration(standardAcceleration);
				}
				else if(event.is(KeyBoardEvent.LEFT)){//left
					controlled.getPartner().setXAcceleration(-standardAcceleration);
				}
				else if(event.is(KeyBoardEvent.DOWN)){//down
					controlled.getPartner().setYAcceleration(-standardAcceleration);
				}
				else if(event.is(KeyBoardEvent.RIGHT)){//right
					controlled.getPartner().setXAcceleration(standardAcceleration);
				}
			}			
		}
		else if(event.keyUp()){
			if(event.is(32)){
				controlled.setXAcceleration(0f);
			}
			else if(event.is(30)){
				controlled.setXAcceleration(0f);
			}
			else if(event.is(17)){
				controlled.setYAcceleration(0f);
			}
			else if(event.is(31)){
				controlled.setYAcceleration(0f);
			}
			else if(event.is(KeyBoardEvent.SPACE)){//space
				Hero temp = focused;
				focused = wild;
				wild = temp;
				Hub.map.setVisibleSquares(focused.isBlack()?Hero.BLACK_INT:focused.isWhite()?Hero.WHITE_INT:Hero.BOTH_INT);
				if(!Client.isConnected()){
					visionBubble.setHeroes(focused,wild);
				}
			}
			else if(!Client.isConnected()){
				if(event.is(KeyBoardEvent.UP)){//up
					controlled.getPartner().setYAcceleration(0f);
				}
				else if(event.is(KeyBoardEvent.LEFT)){//left
					controlled.getPartner().setXAcceleration(0f);
				}
				else if(event.is(KeyBoardEvent.DOWN)){//down
					controlled.getPartner().setYAcceleration(0f);
				}
				else if(event.is(KeyBoardEvent.RIGHT)){//right
					controlled.getPartner().setXAcceleration(0f);
				}
			}
		}
	}
	@Override
	public boolean isCompetetive(){
		return false;
	}
	@Override
	public boolean continuousKeyboard() {
		return false;
	}

}
