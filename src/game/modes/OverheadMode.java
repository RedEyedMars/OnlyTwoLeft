package game.modes;

import java.util.ArrayList;
import java.util.List;

import duo.client.Client;
import game.Hero;
import game.VisionBubble;
import game.environment.OnStepAction;
import game.environment.OnStepSquare;
import gui.graphics.GraphicEntity;
import gui.inputs.KeyBoardListener;
import main.Hub;

public class OverheadMode implements GameMode{



	private static final float uppderViewBorder = 0.6f;
	private static final float lowerViewBorder = 0.4f;
	private static final float standardAcceleration = 0.03f;
	private Hero black;
	private Hero white;

	private Hero controlled;
	private Hero wild;
	private Hero focused;
	private VisionBubble visionBubble;
	private GraphicEntity wildWall;
	private List<GraphicEntity> auxillaryChildren = new ArrayList<GraphicEntity>();
	@Override 
	public void setup(boolean colourToControl, Hero black, Hero white, GraphicEntity wildWall){
		this.black = black;
		this.white = white;
		if(colourToControl==true/*black*/){
			controlled = black;
			wild = white;
			focused = black;
		}
		else {
			controlled = white;
			wild = black;
			focused = white;			
		}
		this.wildWall = wildWall;
		visionBubble = new VisionBubble(colourToControl?black:white,colourToControl?white:black);
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
		for(Hero hero:new Hero[]{black,white}){
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
		if(focused.getY()>uppderViewBorder){
			Hub.map.setY(Hub.map.getY()-(focused.getY()-uppderViewBorder));
			wild.setY(wild.getY()-(focused.getY()-uppderViewBorder));
			focused.setY(uppderViewBorder);
		}
		else if(focused.getY()<lowerViewBorder){
			Hub.map.setY(Hub.map.getY()+(lowerViewBorder-focused.getY()));
			wild.setY(wild.getY()+(lowerViewBorder-focused.getY()));
			focused.setY(lowerViewBorder);
		}
		wildWall.setX(wild.getX()-0.1f);
		wildWall.setY(wild.getY()-0.1f);
	}

	@Override
	public void update(double seconds) {
		handleViewMovement();
		handleInterceptions();		
	}
	@Override
	public void keyCommand(boolean b, char c, int keycode) {
		if(b==KeyBoardListener.DOWN){
			if('a'==c){
				controlled.setXAcceleration(-standardAcceleration);
			}
			if('d'==c){
				controlled.setXAcceleration(standardAcceleration);
			}
			if('w'==c){
				controlled.setYAcceleration(standardAcceleration);
			}
			if('s'==c){
				controlled.setYAcceleration(-standardAcceleration);
			}
			else if(!Client.isConnected()){
				if(keycode==200){//up
					controlled.getPartner().setYAcceleration(standardAcceleration);
				}
				else if(keycode==203){//left
					controlled.getPartner().setXAcceleration(-standardAcceleration);
				}
				else if(keycode==208){//down
					controlled.getPartner().setYAcceleration(-standardAcceleration);
				}
				else if(keycode==205){//right
					controlled.getPartner().setXAcceleration(standardAcceleration);
				}
			}
		}
		else if(b==KeyBoardListener.UP){
			if(32==keycode){
				controlled.setXAcceleration(0f);
			}
			else if(30==keycode){
				controlled.setXAcceleration(0f);
			}
			else if(17==keycode){
				controlled.setYAcceleration(0f);
			}
			else if(31==keycode){
				controlled.setYAcceleration(0f);
			}
			else if(57==keycode){//space
				if(focused==black){
					focused = white;
					wild = black;
				}
				else if(focused==white){
					focused = black;
					wild = white;
				}

				if(!Client.isConnected()){
					visionBubble.setHeroes(focused,wild);
				}
			}
			else if(!Client.isConnected()){
				if(keycode==200){//up
					controlled.getPartner().setYAcceleration(0f);
				}
				else if(keycode==203){//left
					controlled.getPartner().setXAcceleration(0f);
				}
				else if(keycode==208){//down
					controlled.getPartner().setYAcceleration(0f);
				}
				else if(keycode==205){//right
					controlled.getPartner().setXAcceleration(0f);
				}
			}
		}
	}

	@Override
	public boolean continuousKeyboard() {
		return false;
	}
}
