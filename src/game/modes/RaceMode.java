package game.modes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import duo.client.Client;
import game.Hero;
import game.environment.onstep.OnStepAction;
import game.environment.onstep.OnStepSquare;
import game.environment.update.UpdatableSquare;
import game.environment.update.UpdateAction;
import gui.graphics.GraphicEntity;
import gui.graphics.GraphicText;
import gui.inputs.KeyBoardListener;
import main.Hub;

public class RaceMode implements GameMode{

	private static final float uppderViewBorder = 0.6f;
	private static final float lowerViewBorder = 0.4f;
	private static final float standardAcceleration = 0.02f;
	private boolean focusedCanJump = false;
	private boolean focusedJumping = true;
	private boolean wildCanJump = false;
	private boolean wildJumping = true;

	private Hero wild;
	private Hero focused;
	private GraphicEntity wildWall;
	private List<GraphicEntity> auxillaryChildren = new ArrayList<GraphicEntity>();
	private Iterator<Long> ghostItr;
	private Long ghostNext;
	private LinkedHashMap<Long,Float[]> ghostPath = null;
	private LinkedHashMap<Long,Float[]> myPath = new LinkedHashMap<Long,Float[]>();
	private long bestTime=Long.MAX_VALUE;
	private long myTime=0;
	
	private GraphicText showTime = new GraphicText("impactWhite","0",1);
	private GraphicText showTimeBack = new GraphicText("impact","0",1);
	private float previousX=0;
	private float previousY=0;
	public List<GraphicEntity> getAuxillaryChildren(){
		return auxillaryChildren;
	}
	@Override 
	public void setup(boolean colourToControl, Hero black, Hero white, GraphicEntity wildWall){
		focused = black;
		wild = white;
		focused.adjust(0.04f, 0.04f);
		wild.adjust(0.04f, 0.04f);
		this.wildWall = wildWall;
		myTime = System.currentTimeMillis();
		wild.setX(focused.getX());
		wild.setY(focused.getY());
		if(Client.isConnected()){
			Client.setHero(focused);
		}
		else {
			ghostPath = getGhostPath(Hub.map.getName());
			ghostItr = ghostPath.keySet().iterator();
			if(ghostItr.hasNext()){
				ghostNext=ghostItr.next();
			}
			
		}
		showTimeBack.setX(0.445f);
		showTimeBack.setY(0.895f);
		showTimeBack.setWidthFactor(1.45f);
		showTimeBack.setHeightFactor(3.2f);
		auxillaryChildren.add(showTimeBack);
		showTime.setX(0.45f);
		showTime.setY(0.9f);
		showTime.setWidthFactor(1.4f);
		showTime.setHeightFactor(3f);
		auxillaryChildren.add(showTime);
	}

	public LinkedHashMap<Long,Float[]> getGhostPath(String mapName){
		File file = new File("data"+File.separatorChar+"saves"+File.separatorChar+mapName+".ghost");
		if(file.exists()){
			LinkedHashMap<Long,Float[]> map = new LinkedHashMap<Long,Float[]>();
			try {
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String line = reader.readLine();
				while(line!=null){
					int space = line.indexOf(' ');
					int comma = line.lastIndexOf(',');
					if(space==-1||comma==-1){
						bestTime = Long.parseLong(line);
						line = reader.readLine();
						break;
					}
					map.put(Long.parseLong(line.substring(0, space)),
							new Float[]{
									Float.parseFloat(line.substring(space+1,comma)),
									Float.parseFloat(line.substring(comma+1))
					});
					line = reader.readLine();
				}
				reader.close();
				return map;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		wild.setVisible(false);
		return new LinkedHashMap<Long,Float[]>();
	}
	private void saveGhostPath(String mapName){
		if(System.currentTimeMillis()-myTime<=bestTime){
			File file = new File("data"+File.separatorChar+"saves"+File.separatorChar+mapName+".ghost");
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(file));
				StringBuilder builder = new StringBuilder();
				for(Long key:myPath.keySet()){
					builder.append(key);
					builder.append(' ');
					builder.append(myPath.get(key)[0]);
					builder.append(',');
					builder.append(myPath.get(key)[1]);
					builder.append('\n');
				}
				builder.append(System.currentTimeMillis()-myTime);
				builder.append('\n');
				writer.write(builder.toString());
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void handleGhost(){
		long now = System.currentTimeMillis()-myTime;
		if(focused.getX()-Hub.map.getX()!=previousX||focused.getY()-Hub.map.getY()!=previousY){
			previousX = focused.getX()-Hub.map.getX();
			previousY = focused.getY()-Hub.map.getY();
			myPath.put(now, new Float[]{previousX,previousY});
		}

		while(ghostItr.hasNext()&&now>ghostNext){
			Float[] change = ghostPath.get(ghostNext);
			wild.setX(change[0]+Hub.map.getX());
			wild.setY(change[1]+Hub.map.getY());
			ghostNext=ghostItr.next();			
		}

		String minutes = now<60000?" ":now/60000+"m";
		String seconds = ((now/1000)%60<10&&!" ".equals(minutes)?"0":"")+(now/1000)%60+"s ";
		String time = minutes+seconds+now%1000;
		showTimeBack.change(time);
		showTime.change(time);
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
		if(focused.getY()>uppderViewBorder){
			Hub.map.setY(Hub.map.getY()-(focused.getY()-uppderViewBorder));
			wild.setY(wild.getY()-(focused.getY()-uppderViewBorder));
			focused.setY(uppderViewBorder);
		}
		else if(Hub.map.getY()<0&&focused.getY()<lowerViewBorder){
			Hub.map.setY(Hub.map.getY()+(lowerViewBorder-focused.getY()));
			wild.setY(wild.getY()+(lowerViewBorder-focused.getY()));
			focused.setY(lowerViewBorder);
		}
		wildWall.setX(wild.getX()-0.25f);
		wildWall.setY(wild.getY()-0.25f);
	}
	@Override 
	public void update(double secondsSinceLastFrame){
		handleGhost();
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
		if(Client.isConnected()){
			if(wild.foundSouthWall()){
				wildJumping=false;
				wildCanJump=true;
				if(wild.getYAcceleration()<0){
					wild.setYAcceleration(0);
				}
			}
			else {
				if(wild.getYAcceleration()>=-0.06){
					wild.setYAcceleration((float) (wild.getYAcceleration()-0.2f*secondsSinceLastFrame));
				}		
			}
		}
		if(focused.getY()<-0.05f||wild.getY()<-0.05f){
			loseGame();
		}
	}

	public void loseGame(){
		focused.getGame().transition("Restart", false);
	}
	public void winGame(String nextMap){
		saveGhostPath(Hub.map.getName());
		focused.getGame().transition(nextMap, true);
	}

	private void jump(){
		if(focusedCanJump){
			focused.setYAcceleration(0.06f);
			if(focusedJumping){
				focusedCanJump=false;
			}
			focusedJumping=true;
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
				jump();
			}
			if('s'==c){
				//controlled.setYAcceleration(-standardAcceleration);
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
				jump();
			}
		}
	}


	@Override
	public boolean continuousKeyboard() {
		return false;
	}
}
