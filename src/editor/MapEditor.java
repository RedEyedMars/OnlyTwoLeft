package editor;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import game.Action;
import game.environment.Square;
import game.environment.SquareAction;
import game.environment.UpdateAction;
import game.menu.MainMenu;
import gui.Gui;
import gui.graphics.GraphicEntity;
import gui.inputs.KeyBoardListener;
import gui.inputs.MotionEvent;
import gui.inputs.MouseListener;
import main.Hub;
import storage.Storage;

public class MapEditor extends Editor implements KeyBoardListener{

	private File saveTo = null;
	private boolean readyToAddToDrawable = false;

	private HashMap<String,Square> screenBackgrounds = new HashMap<String,Square>();
	private float screenX = 0;
	private float screenY = 0;
	
	private boolean reset = false;
	public MapEditor(){
		super();
		saveTo = Gui.userSave("maps");
		if(saveTo!=null){
			if(saveTo.exists()){
				Storage.loadMap(saveTo.getAbsolutePath());
				squares = Hub.map.getSquares();
				for(Square square:Hub.map.getSquares()){
					addIconsToSquare(square,null);
					addChild(square);
				}
			}
			else {
				Hub.map = new game.environment.Map();
				squares = Hub.map.getSquares();
			}
		}
		this.readyToAddToDrawable  = true;
	}
	public void update(double seconds){
		if(saveTo==null){
			Gui.setView(new MainMenu());
		}
		//super.update(seconds);
	}
	@Override
	public void keyCommand(boolean b, char c, int keycode) {
		if(KeyBoardListener.UP==b){
			if(57==keycode){//space
				if(visibleTo==0){
					visibleTo = 1;
					visibleToShower.setFrame(0);
				}
				else if(visibleTo==1){
					visibleTo = 2;
					visibleToShower.setFrame(1);
				}
				else if(visibleTo==2){
					visibleTo = 0;
					visibleToShower.setFrame(3);
				}
				setVisibleSquares(visibleTo);
			}
			else if(44==keycode&&!squares.isEmpty()){
				removeChild(squares.remove(squares.size()-1));
			}
			else if(45==keycode){
				if(!reset){
					saveAndReturnToMainMenu();
				}
				else {
					reset = false;
				}
			}
			else if(keycode==17||keycode==200){//up
				moveView(0,-0.5f);
			}
			else if(keycode==30||keycode==203){//left
				moveView(0.5f,0);
			}
			else if(keycode==31||keycode==208){//down
				moveView(0,0.5f);
			}
			else if(keycode==32||keycode==205){//right
				moveView(-0.5f,0);
			}
		}

	}
	private void saveAndReturnToMainMenu() {
		saveMap();
		Gui.removeOnType(this);
		Gui.setView(new MainMenu());
	}
	private void saveMap(){

		for(Square square:squares){
			square.setX(square.getX()-screenX);
			square.setY(square.getY()-screenY);
		}
		game.environment.Map map = new game.environment.Map();
		for(Square square:squares){
			map.addSquare(square);
		}
		Storage.saveMap(saveTo.getAbsolutePath(), map);
	}
	private void moveView(float x, float y){
		this.screenX+=x;
		this.screenY+=y;
		for(int i=1;i<squares.size();++i){
			squares.get(i).setX(squares.get(i).getX()+x);
			squares.get(i).setY(squares.get(i).getY()+y);
		}
	}

	@Override
	public boolean continuousKeyboard() {
		return true;
	}
	public void restartWith(Square square) {
		if(square!=null){
			squares.add(square);
		}
		buttons.clear();
		setupButtons();
		for(Square sqr:squares){
			addChild(sqr);
			addIconsToSquare(sqr,null);
		}
		
		reset = true;
	}

}
