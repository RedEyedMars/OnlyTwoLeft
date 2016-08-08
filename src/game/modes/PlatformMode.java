package game.modes;

import java.util.ArrayList;
import java.util.List;

import duo.client.Client;
import game.Hero;
import game.environment.OnStepAction;
import game.environment.OnStepSquare;
import gui.graphics.GraphicEntity;
import gui.inputs.KeyBoardListener;
import main.Hub;

public class PlatformMode implements GameMode{

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
	public void setup(boolean colourToControl, Hero black, Hero white, GraphicEntity wildWall){

		focused = black;
		wild = white;
		if(colourToControl==false/*white*/){
			flipView();
		}
		this.wildWall = wildWall;

		if(Client.isConnected()){
			Client.setHero(focused);
		}
	}

	private void handleJump(Hero hero, boolean northWall, int direction){
		if(northWall){
			hero.setYAcceleration(0.1f*direction);				
		}
		if(hero.getYAcceleration()*-direction>=0){
			hero.setYAcceleration(hero.getYAcceleration()+0.003f*direction);
		}
		else {				
			if(hero.getYAcceleration()*-direction>-0.1f){
				hero.setYAcceleration(hero.getYAcceleration()+0.005f*direction);
			}
			else {
				hero.setYAcceleration(0.1f*direction);				
			}				
		}
	}

	private void handleInterceptions(){
		List<OnStepSquare> mapSquares = Hub.map.getFunctionalSquares();
		for(Hero hero:new Hero[]{focused,wild}){
			List<GraphicEntity> safetiesFound = new ArrayList<GraphicEntity>();
			List<Boolean> safeties            = new ArrayList<Boolean>();
			for(int i=mapSquares.size()-1;i>=0;--i){
				OnStepAction action = mapSquares.get(i).getOnHitAction(hero);
				if(action!=null){
					if(hero.isWithin(mapSquares.get(i))){
						if(action.isSafe()){
							safetiesFound.add(mapSquares.get(i));
							safeties.add(action.isSafe());
							if(hero.isCompletelyWithin(mapSquares.get(i))){
								break;
							}
						}
						else {
							if(!action.resolve(hero)){
								safetiesFound.add(mapSquares.get(i));
								safeties.add(action.isSafe());
							}
						}
					}
				}
			}

			hero.handleWalls(safetiesFound,safeties);
		}
	}

	private void handleViewMovement(){
		if(focused.getX()>uppderViewBorder){
			Hub.map.setX(Hub.map.getX()-(focused.getX()-uppderViewBorder));
			wild.setX(wild.getX()-(focused.getX()-uppderViewBorder));
			focused.setX(uppderViewBorder);
		}
		else if(focused.getX()<lowerViewBorder){
			Hub.map.setX(Hub.map.getX()+(lowerViewBorder-focused.getX()));
			wild.setX(wild.getX()+(lowerViewBorder-focused.getX()));
			focused.setX(lowerViewBorder);
		}
		wildWall.setX(wild.getX()-0.1f);
		wildWall.setY(wild.getY()-0.1f);
	}
	private void flipView(){
		Hero temp = focused;
		focused = wild;
		wild = temp;
		Hub.map.setVisibleSquares(focused.textureIndex()+1);
		for(GraphicEntity child:Hub.map.getChildren()){
			child.setY(1f-child.getY()-child.getHeight());
		}
		boolean cj = focusedCanJump;
		boolean jg = focusedJumping;
		float acc = focused.getYAcceleration();
		focused.setY(1f-focused.getY()-focused.getHeight());
		focused.setYAcceleration(wild.getYAcceleration());
		focusedCanJump=wildCanJump;
		focusedJumping=true;
		wild.setY(1f-wild.getY()-wild.getHeight());
		wild.setYAcceleration(acc);
		wildCanJump=cj;
		wildJumping=true;
		
	}
	@Override 
	public void update(double secondsSinceLastFrame){
		if(focusedJumping||Math.abs(focused.getXAcceleration())>=0.001f){
			if(focused.foundSouthWall()&&focused.getYVelocity()<=0){
				focusedJumping=false;
				focusedCanJump=true;
				focused.setYVelocity(0f);
			}
			else {
				handleJump(focused, focused.foundNorthWall(),-1);
			}
		}
		if(wildJumping||Math.abs(wild.getXAcceleration())>=0.001f){
			if(wild.foundNorthWall()&&wild.getYVelocity()>=0){
				wildJumping=false;
				wildCanJump=true;
				wild.setYVelocity(0f);
			}
			else {
				handleJump(wild, wild.foundSouthWall(),1);
			}
		}
		handleViewMovement();
		handleInterceptions();
		if(focused.getY()<-0.05f||wild.getY()>1.0f){
			focused.endGame();
		}
	}

	@Override
	public void keyCommand(boolean b, char c, int keycode) {
		if(b==KeyBoardListener.DOWN){
			if('a'==c){
				focused.setXAcceleration(-standardAcceleration);
				focusedJumping=true;
			}
			if('d'==c){
				focused.setXAcceleration(standardAcceleration);
				focusedJumping=true;
			}
			if('w'==c){
				if(focusedCanJump&&focused.getYVelocity()>=-0.03f){
					focused.setYAcceleration(0.065f);
					if(focusedJumping){
						focusedCanJump=false;						
					}
					focusedJumping=true;
				}
			}
			if('s'==c){
				//controlled.setYAcceleration(-standardAcceleration);
			}
			else if(!Client.isConnected()){
				if(keycode==200){//up
					//controlled.getPartner().setYAcceleration(standardAcceleration);
				}
				else if(keycode==203){//left
					wild.setXAcceleration(-standardAcceleration);
					wildJumping=true;
				}
				else if(keycode==208){//down
					if(wildCanJump&&wild.getYVelocity()<=0.03f){
						wild.setYAcceleration(-0.065f);
						if(wildJumping){
							wildCanJump=false;
						}
						wildJumping=true;
					}
				}
				else if(keycode==205){//right
					wild.setXAcceleration(standardAcceleration);
					wildJumping=true;
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
	public boolean continuousKeyboard() {
		return false;
	}
}
