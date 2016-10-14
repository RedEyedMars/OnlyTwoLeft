package game.mode;

import java.util.ArrayList;
import java.util.List;

import duo.client.Client;
import duo.messages.HeroEndGameMessage;
import duo.messages.MoveHeroMessage;
import game.Game;
import game.VisionBubble;
import game.environment.onstep.OnStepAction;
import game.environment.onstep.OnStepSquare;
import game.hero.ConnectedHero;
import game.hero.Hero;
import gui.graphics.GraphicEntity;
import gui.inputs.KeyBoardListener;
import gui.inputs.MotionEvent;
import main.Hub;

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
			else if(keycode==1||keycode==25||keycode==197){
				game.pause();
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
				Hero temp = focused;
				focused = wild;
				wild = temp;
				Hub.map.setVisibleSquares(focused.isBlack()?Hero.BLACK_INT:focused.isWhite()?Hero.WHITE_INT:Hero.BOTH_INT);
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
	public boolean isCompetetive(){
		return false;
	}
	@Override
	public boolean continuousKeyboard() {
		return false;
	}
}
