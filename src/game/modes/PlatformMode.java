package game.modes;

import java.util.ArrayList;
import java.util.List;

import duo.client.Client;
import game.Hero;
import game.environment.OnStepAction;
import game.environment.OnStepSquare;
import game.environment.UpdatableSquare;
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
		focused.adjust(0.04f, 0.04f);
		wild.adjust(0.04f, 0.04f);
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
		wildWall.setX(wild.getX()-0.25f);
		wildWall.setY(wild.getY()-0.25f);
	}
	private void flipView(){
		Hero temp = focused;
		focused = wild;
		wild = temp;
		Hub.map.setVisibleSquares(focused.isBlack()?1:2);
		for(GraphicEntity child:Hub.map.getChildren()){
			if(child instanceof UpdatableSquare){
				UpdatableSquare square = (UpdatableSquare)child;
				if(square.getAction().getIndex()==2){
					square.getAction().addFloats(square.getAction().getFloat(0), -square.getAction().getFloat(1));
				}
				float offset = child.getY()-(1f-(child.getY()+child.getHeight()));
				square.move(0f,-offset);
				for(GraphicEntity depend:square.getDependants()){
					depend.setY(1f-depend.getY()-depend.getHeight());
				}
			}
			else {
				child.setY(1f-child.getY()-child.getHeight());
			}
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
		handleViewMovement();
		handleInterceptions();
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
		if(focused.getY()<-0.05f||wild.getY()>1.0f){
			focused.endGame();
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
	public boolean continuousKeyboard() {
		return false;
	}
}