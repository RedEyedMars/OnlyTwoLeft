package game.modes;

import java.util.ArrayList;
import java.util.List;

import duo.client.Client;
import duo.messages.MoveHeroMessage;
import game.Game;
import game.environment.onstep.OnStepAction;
import game.environment.onstep.OnStepSquare;
import game.environment.update.UpdatableSquare;
import game.environment.update.UpdateAction;
import game.hero.Hero;
import gui.graphics.GraphicEntity;
import gui.inputs.KeyBoardListener;
import main.Hub;

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
		focused = Hub.getHero(true);
		wild = Hub.getHero(false);
		focused.resize(0.04f, 0.04f);
		wild.resize(0.04f, 0.04f);
		if(colourToControl==false/*white*/){
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
		Hub.map.setVisibleSquares(focused.isBlack()?1:2);
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
	public void keyCommand(boolean b, char c, int keycode) {
		if(b==KeyBoardListener.DOWN){
			if('a'==c){
				focused.setXAcceleration(-standardAcceleration);
			}
			if('d'==c){
				focused.setXAcceleration(standardAcceleration);
			}
			if('w'==c){
				if(focusedCanJump){
					focused.setYAcceleration(0.06f);
					if(focusedJumping){
						focusedCanJump=false;
					}
					focusedJumping=true;
				}
			}
			if('s'==c){
				//controlled.setYAcceleration(-standardAcceleration);
			}
			else if(keycode==1||keycode==25||keycode==197){
				game.pause();
			}
			else if(!Client.isConnected()){
				if(keycode==200){//up
					//controlled.getPartner().setYAcceleration(standardAcceleration);
				}
				else if(keycode==203){//left
					wild.setXAcceleration(-standardAcceleration);
				}
				else if(keycode==208){//down
					if(wildCanJump){
						wild.setYAcceleration(-0.06f);
						if(wildJumping){
							wildCanJump=false;
						}
						wildJumping=true;
					}
				}
				else if(keycode==205){//right
					wild.setXAcceleration(standardAcceleration);
				}
			}
		}
		else if(b==KeyBoardListener.UP){
			if(32==keycode){
				focused.setXAcceleration(0f);
			}
			else if(30==keycode){
				focused.setXAcceleration(0f);
			}
			else if(17==keycode){
			}
			else if(31==keycode){
			}
			else if(57==keycode){//space
				if(!Client.isConnected()){
					flipView();
				}
			}
			else if(!Client.isConnected()){
				if(keycode==200){//up
					//controlled.getPartner().setYAcceleration(0f);
				}
				else if(keycode==203){//left
					wild.setXAcceleration(0f);
				}
				else if(keycode==208){//down
					//controlled.getPartner().setYAcceleration(0f);
				}
				else if(keycode==205){//right
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
