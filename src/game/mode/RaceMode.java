package game.mode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import duo.client.Client;
import duo.messages.HeroEndGameMessage;
import duo.messages.MoveHeroMessage;
import game.Action;
import game.Game;
import game.environment.onstep.OnStepSquare;
import game.hero.ConnectedHero;
import game.hero.ConnectedHumanoidHero;
import game.hero.Hero;
import game.hero.HumanoidHero;
import gui.Gui;
import gui.graphics.GraphicEntity;
import gui.graphics.GraphicText;
import gui.inputs.KeyBoardListener;
import gui.inputs.MotionEvent;
import main.Hub;

public class RaceMode extends GameMouseHandler implements GameMode{

	private static final float uppderViewBorder = 0.6f;
	private static final float lowerViewBorder = 0.4f;
	private static final float standardAcceleration = 0.02f;
	private static final float standingHeight = 0.04f;
	private static final float crouchingHeight = 0.03f;
	private boolean focusedCanJump = false;

	private Hero wild;
	private Hero focused;
	private GraphicEntity wildWall;
	private List<GraphicEntity> auxillaryChildren = new ArrayList<GraphicEntity>();
	private Iterator<Long> ghostItr;
	private Long ghostNext = 0L;
	private DataInputStream ghostPath = null;
	private DataOutputStream myPath;
	private long bestTime=Long.MAX_VALUE;

	private boolean ending = false;

	private GraphicText showTime;
	private GraphicText showTimeBack;
	private float previousX=0;
	private float previousY=0;
	private Game game;
	private boolean colourToControl;
	public List<GraphicEntity> getAuxillaryChildren(){
		return auxillaryChildren;
	}
	@Override 
	public void setup(Game game, boolean colourToControl, GraphicEntity wildWall){
		this.game = game;
		this.colourToControl = colourToControl;
		focused = Hub.getHero(colourToControl);
		wild = Hub.getHero(!colourToControl);
		
		focused.resize(0.02f, standingHeight);
		wild.resize(0.02f, 0.04f);
		this.wildWall = wildWall;
		wild.reposition(focused.getX(),focused.getY());
		if(Client.isConnected()){
			Client.setHero(focused);
		}
		else {
			setupPathStreams(Hub.map.getName());			
		}
		showTimeBack = new GraphicText("impact","0",1);
		showTimeBack.reposition(0.445f,0.895f);
		showTimeBack.setWidthFactor(1.45f);
		showTimeBack.setHeightFactor(3.2f);
		auxillaryChildren.add(showTimeBack);
		showTime = new GraphicText("impactWhite","0",1);
		showTime.reposition(0.45f,0.9f);
		showTime.setWidthFactor(1.4f);
		showTime.setHeightFactor(3f);
		auxillaryChildren.add(showTime);
	}

	public void setupPathStreams(String mapName){

		File file = new File("data"+File.separatorChar+"saves"+File.separatorChar+mapName+".temp");
		try {
			myPath = new DataOutputStream(new FileOutputStream(file));
		} catch( IOException e) {
			e.printStackTrace();
		}
		file = new File("data"+File.separatorChar+"saves"+File.separatorChar+mapName+".ghost");
		if(file.exists()){
			try {
				ghostPath = new DataInputStream(new FileInputStream(file));
				bestTime = ghostPath.readLong();
				ghostNext = ghostPath.readLong();
				return;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		ghostPath = null;
		wild.setVisible(false);
	}
	private void saveGhostPath(String mapName,long now){
		if(now<=bestTime){
			try {
				DataOutputStream writer = new DataOutputStream(
						new FileOutputStream(new File("data"+File.separatorChar+"saves"+File.separatorChar+mapName+".ghost")));
				DataInputStream reader = new DataInputStream(
						new FileInputStream(new File("data"+File.separatorChar+"saves"+File.separatorChar+mapName+".temp")));

				writer.writeLong(now);
				try {
					while(true){
						writer.writeLong(reader.readLong());
						writer.writeFloat(reader.readFloat());
						writer.writeFloat(reader.readFloat());
						writer.writeChar(reader.readChar());					
					}
				} catch(EOFException e){					
				}
				writer.writeLong(now);
				writer.writeFloat(focused.getX()-Hub.map.getX());
				writer.writeFloat(focused.getY()-Hub.map.getY());
				writer.writeChar('!');

				reader.close();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void handleGhost(){
		if(ending)return;
		long now = game.getTimeSpent();
		String minutes = now<60000?" ":now/60000+"m";
		String seconds = ((now/1000)%60<10&&!" ".equals(minutes)?"0":"")+(now/1000)%60+"s ";
		String time = minutes+seconds+now%1000;
		showTimeBack.change(time);
		showTime.change(time);
		if(Client.isConnected()||myPath==null)return;
		if(focused.getX()-Hub.map.getX()!=previousX||focused.getY()-Hub.map.getY()!=previousY){
			previousX = focused.getX()-Hub.map.getX();
			previousY = focused.getY()-Hub.map.getY();

			try {
				myPath.writeLong(now);
				myPath.writeFloat(previousX);
				myPath.writeFloat(previousY);
				myPath.writeChar('\n');
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		while(ghostPath!=null&&now>ghostNext){
			try {
				float x = ghostPath.readFloat();
				float y = ghostPath.readFloat();
				wild.reposition(x+Hub.map.getX(),
						    y+Hub.map.getY());
				char endChar = ghostPath.readChar();
				if(endChar=='!'){
					ghostPath.close();
					ghostPath=null;
					break;
				}
				ghostNext = ghostPath.readLong();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	private void handleInterceptions(){
		List<OnStepSquare> mapSquares = Hub.map.getFunctionalSquares();
		focused.handleWalls(mapSquares);
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
		else if(Hub.map.getY()<0&&focused.getY()<lowerViewBorder){
			heroMoveY = lowerViewBorder;
		}

		Hub.map.reposition(Hub.map.getX()+(heroMoveX-focused.getX()),
				       Hub.map.getY()+(heroMoveY-focused.getY()));
		wild.reposition(wild.getX()+(heroMoveX-focused.getX()),
				    wild.getY()+(heroMoveY-focused.getY()));
		focused.reposition(heroMoveX,heroMoveY);
		wildWall.reposition(wild.getX()-0.25f,
				        wild.getY()-0.25f);
	}
	@Override 
	public void update(double secondsSinceLastFrame){
		handleGhost();
		handleViewMovement();
		handleInterceptions();	
		MoveHeroMessage.update(secondsSinceLastFrame, wild);
		if(focused.foundSouthWall()){
			focusedCanJump=true;
			focused.setJumping(false);
			if(focused.getYAcceleration()<0){
				focused.setYAcceleration(0);
			}
		}
		else if(focused.foundNorthWall()&&focused.getYAcceleration()>0){
			focused.setYAcceleration(0);
			focusedCanJump=false;
		}
		else {
			if(focused.getYAcceleration()>=-0.06){
				focused.setYAcceleration((float) (focused.getYAcceleration()-0.2f*secondsSinceLastFrame));
			}
		}
		if(focused.getY()<-0.05f){
			//System.out.println(focused.isBlack()&&focused.getY()<-0.05f?"black lose":"white lose");
			loseGame(focused.isBlack());
		}
	}
	@Override
	public Hero createConnectedHero(boolean control, Game game, boolean bool) {
		return new ConnectedHumanoidHero(control, game,bool);
	}


	@Override
	public Hero createHero(Game game, boolean bool) {
		return new HumanoidHero(game,bool);
	}
	@Override
	public void loseGame(boolean colour){
		if(colour!=colourToControl||ending){
			return;
		}
		try {
			if(ghostPath!=null){
				ghostPath.close();
				ghostPath = null;
			}
			if(myPath!=null){
				myPath.close();
				myPath=null;
				deleteMyPath(Hub.map.getName());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		if(HeroEndGameMessage.partnerHasWon()){
		game.transition("Restart", false);
		if(!Client.isConnected()){
			HeroEndGameMessage.setAndSend(focused.isBlack(), false, Long.MAX_VALUE);
			HeroEndGameMessage.setAndSend(!focused.isBlack(), false, bestTime);
		}
		ending = true;
		}
		else {
			game.restart();
		}
	}
	@Override
	public void winGame(boolean colour,String nextMap){
		if(colour!=colourToControl||ending){
			return;
		}
		long now = game.getTimeSpent();
		try {
			if(ghostPath!=null){
				ghostPath.close();
				ghostPath = null;
			}
			if(myPath!=null){
				myPath.close();
				myPath=null;
				saveGhostPath(Hub.map.getName(),now);
				deleteMyPath(Hub.map.getName());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		game.transition(nextMap, true);
		if(!Client.isConnected()){
			HeroEndGameMessage.setAndSend(focused.isBlack(), true, now);
			HeroEndGameMessage.setAndSend(!focused.isBlack(), true, bestTime);		
		}
		ending = true;
	}
	private void deleteMyPath(String mapName) {
		File file = new File("data"+File.separatorChar+"saves"+File.separatorChar+mapName+".temp");
		if(file.exists()){
			file.delete();
		}
	}
	private void jump(){
		if(focusedCanJump){
			if(focused.isJumping()){
				focused.setYAcceleration(focused.getYAcceleration()+0.06f);
				if(focused.getYAcceleration()>0.06f){
					focused.setYAcceleration(0.06f);					
				}
				focusedCanJump=false;
			}
			else {
				focusedCanJump=false;
				focused.jump(new Action<Hero>(){
					@Override
					public void act(Hero subject) {
						subject.setYAcceleration(0.06f);
						subject.setJumping(true);
						focusedCanJump=true;
					}
					
				});
			}
		}
	}
	@Override
	public void keyCommand(boolean b, char c, int keycode) {
		if(b==KeyBoardListener.DOWN){
			if('a'==c){
				focused.setXAcceleration(-standardAcceleration);
			}
			else if('d'==c){
				focused.setXAcceleration(standardAcceleration);
			}
			else if('w'==c){
				focused.resize(focused.getWidth(), standingHeight);
				jump();
			}
			else if('s'==c){
				if(!focused.isJumping()){
					focused.resize(focused.getWidth(), crouchingHeight);
				}
			}
			else if(keycode==1||keycode==25||keycode==197){
				game.pause();
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
				focused.resize(focused.getWidth(), standingHeight);
			}
			else if(31==keycode){
				focused.resize(focused.getWidth(), standingHeight);
			}
			else if(57==keycode){//space
				jump();
			}
			else if(28==keycode){//enter
				game.getChatBox().blinkerOn();
				Gui.giveOnType(game.getChatBox().getDefaultKeyBoardListener());
			}
		}
	}

	@Override
	public boolean isCompetetive(){
		return true;
	}
	
	@Override
	public boolean continuousKeyboard() {
		return false;
	}
}
