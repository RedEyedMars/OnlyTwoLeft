package com.rem.otl.core.game.mode;

import java.util.ArrayList;
import java.util.List;

import com.rem.otl.core.duo.client.Client;
import com.rem.otl.core.duo.messages.MoveHeroMessage;
import com.rem.otl.core.game.Game;
import com.rem.otl.core.game.environment.onstep.OnStepAction;
import com.rem.otl.core.game.environment.onstep.OnStepSquare;
import com.rem.otl.core.game.environment.update.UpdatableSquare;
import com.rem.otl.core.game.environment.update.UpdateAction;
import com.rem.otl.core.game.hero.Hero;
import com.rem.otl.core.gui.graphics.GraphicEntity;
import com.rem.otl.core.gui.inputs.KeyBoardEvent;
import com.rem.otl.core.gui.inputs.KeyBoardListener;
import com.rem.otl.core.main.Hub;

public class PlatformMode extends OverheadMode{

	private static final float uppderViewBorder = 0.6f;
	private static final float lowerViewBorder = 0.4f;
	private static final float standardAcceleration = 0.03f;
	private boolean focusedCanJump = false;
	private boolean focusedJumping = true;
	private boolean wildCanJump = false;
	private boolean wildJumping = true;

	private Hero wild;
	private Hero focused;
	private GraphicEntity wildWall;
	private List<GraphicEntity> auxillaryChildren = new ArrayList<GraphicEntity>();
	public List<GraphicEntity> getAuxillaryChildren(){
		return auxillaryChildren;
	}
	@Override 
	public void setup(Game game, boolean colourToControl, GraphicEntity wildWall){
		this.game = game;
		this.colourToControl = colourToControl;
		focused = Hub.getHero(Hero.BLACK_BOOL);
		wild = Hub.getHero(Hero.WHITE_BOOL);
		focused.resize(0.04f, 0.04f);
		wild.resize(0.04f, 0.04f);
		if(colourToControl==Hero.WHITE_BOOL/*white*/){
			flipView();
		}
		this.wildWall = wildWall;

		if(Client.isConnected()){
			Client.setHero(focused);
		}
	}

	private void handleInterceptions(){
		List<OnStepSquare> mapSquares = Hub.map.getFunctionalSquares();
		for(Hero hero:new Hero[]{focused,wild}){
			hero.handleWalls(mapSquares);
		}
	}

	private void handleViewMovement(){
		float heroMoveX = focused.getX();
		if(focused.getX()>uppderViewBorder){
			heroMoveX = uppderViewBorder;
		}
		else if(focused.getX()<lowerViewBorder){
			heroMoveX = lowerViewBorder;
		}
		Hub.map.reposition(Hub.map.getX()+(heroMoveX-focused.getX()),
				       Hub.map.getY());
		wild.reposition(wild.getX()+(heroMoveX-focused.getX()),
				    wild.getY());
		focused.reposition(heroMoveX,focused.getY());
		wildWall.reposition(wild.getX()-0.25f,
				        wild.getY()-0.25f);
	}
	private void flipView(){
		Hero temp = focused;
		focused = wild;
		wild = temp;
		Hub.map.setVisibleSquares(focused.isBlack()?Hero.BLACK_INT:Hero.WHITE_INT);
		for(GraphicEntity child:Hub.map.getChildren()){
			if(child instanceof UpdatableSquare){
				UpdatableSquare square = (UpdatableSquare)child;
				for(UpdateAction updateAction:square.getAction()){
					updateAction.flip();
				}
				float offset = square.getY()-(1f-(square.getY()+square.getHeight()));
				square.move(0f,-offset);
				for(GraphicEntity depend:square.getDependants()){
					depend.reposition(depend.getX(),1f-depend.getY()-depend.getHeight());
				}
			}
			else {
				child.reposition(child.getX(),1f-child.getY()-child.getHeight());
			}
		}
		boolean cj = focusedCanJump;
		boolean jg = focusedJumping;
		float acc = focused.getYAcceleration();
		focused.reposition(focused.getX(),1f-focused.getY()-focused.getHeight());
		focused.setYAcceleration(wild.getYAcceleration());
		focusedCanJump=wildCanJump;
		focusedJumping=true;
		wild.reposition(wild.getX(),1f-wild.getY()-wild.getHeight());
		wild.setYAcceleration(acc);
		wildCanJump=cj;
		wildJumping=true;

	}
	@Override 
	public void update(double secondsSinceLastFrame){
		handleViewMovement();
		handleInterceptions();
		MoveHeroMessage.update(secondsSinceLastFrame, wild);
		if(focused.foundSouthWall()){
			focusedCanJump=true;
			focusedJumping=false;
			if(focused.getYAcceleration()<0){
				focused.setYAcceleration(0);
			}
		}
		else {
			if(focused.getYAcceleration()>=-0.06){
				focused.setYAcceleration((float) (focused.getYAcceleration()-0.2f*secondsSinceLastFrame));
			}
		}
		if(wild.foundNorthWall()){
			wildJumping=false;
			wildCanJump=true;
			if(wild.getYAcceleration()>0){
				wild.setYAcceleration(0);
			}
		}
		else {
			if(wild.getYAcceleration()<=0.06){
				wild.setYAcceleration((float) (wild.getYAcceleration()+0.2f*secondsSinceLastFrame));
			}		
		}
		if(focused.getY()<-0.05f){
			loseGame(focused.isBlack());
		}
		if(wild.getY()>1.0f){
			loseGame(wild.isBlack());
		}
	}
	@Override
	public void onType(KeyBoardEvent event) {
		if(event.keyUp()){
			if('a'==event.getChar()){
				focused.setXAcceleration(-standardAcceleration);
			}
			if('d'==event.getChar()){
				focused.setXAcceleration(standardAcceleration);
			}
			if('w'==event.getChar()){
				if(focusedCanJump){
					focused.setYAcceleration(0.06f);
					if(focusedJumping){
						focusedCanJump=false;
					}
					focusedJumping=true;
				}
			}
			if('s'==event.getChar()){
				//controlled.setYAcceleration(-standardAcceleration);
			}
			else if(event.is(KeyBoardEvent.ESCAPE)||event.is(25)||event.is(197)){
				game.pause();
			}
			else if(!Client.isConnected()){
				if(event.is(KeyBoardEvent.UP)){//up
					//controlled.getPartner().setYAcceleration(standardAcceleration);
				}
				else if(event.is(KeyBoardEvent.LEFT)){//left
					wild.setXAcceleration(-standardAcceleration);
				}
				else if(event.is(KeyBoardEvent.DOWN)){//down
					if(wildCanJump){
						wild.setYAcceleration(-0.06f);
						if(wildJumping){
							wildCanJump=false;
						}
						wildJumping=true;
					}
				}
				else if(event.is(KeyBoardEvent.RIGHT)){//right
					wild.setXAcceleration(standardAcceleration);
				}
			}
		}
		else if(event.keyUp()){
			if(event.is(32)){
				focused.setXAcceleration(0f);
			}
			else if(event.is(30)){
				focused.setXAcceleration(0f);
			}
			else if(event.is(17)){
			}
			else if(event.is(31)){
			}
			else if(event.is(KeyBoardEvent.SPACE)){//space
				if(!Client.isConnected()){
					flipView();
				}
			}
			else if(!Client.isConnected()){
				if(event.is(KeyBoardEvent.UP)){//up
					//controlled.getPartner().setYAcceleration(0f);
				}
				else if(event.is(KeyBoardEvent.LEFT)){//left
					wild.setXAcceleration(0f);
				}
				else if(event.is(KeyBoardEvent.DOWN)){//down
					//controlled.getPartner().setYAcceleration(0f);
				}
				else if(event.is(KeyBoardEvent.RIGHT)){//right
					wild.setXAcceleration(0f);
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
