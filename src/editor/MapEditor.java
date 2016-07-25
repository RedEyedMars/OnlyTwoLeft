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
	private List<GraphicEntity> heroButtons = new ArrayList<GraphicEntity>();
	private game.environment.Map myLoadedMap;
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
				if(squares.size()>0){
					squares.get(0).setX(0f);
					squares.get(0).setY(0f);
				}
			}
			else {
				Hub.map = new game.environment.Map();
				squares = Hub.map.getSquares();
			}
			myLoadedMap = Hub.map;
			setupHeroButton(0);
			setupHeroButton(1);
			setupButtons();
			this.readyToAddToDrawable  = true;
		}

	}
	public KeyBoardListener getDefaultKeyBoardListener(){
		return this;
	}
	
	public void setupHeroButton(final int colour){
		final Button<Editor> button = new Button<Editor>("circles",colour,this,null);
		final MouseListener mouseListener = new MouseListener(){
			@Override
			public boolean onClick(MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_UP){
					mode=0;
					Gui.removeOnClick(this);
				}
				return false;
			}

			@Override
			public boolean onHover(MotionEvent event) {
				int x = (int) (event.getX()*gridSize);
				int y = (int) (event.getY()*gridSize);
				float dx = ((float)x)/gridSize-button.getX();
				float dy = ((float)y)/gridSize-button.getY();
				button.setX(button.getX()+dx);
				button.setY(button.getY()+dy);
				myLoadedMap.setStartPosition(colour,button.getX()-screenX,button.getY()-screenY);
				return true;
			}

			@Override
			public void onMouseScroll(int distance) {				
			}
		};
		button.setAction(new ButtonAction(){
			@Override
			public void act(Editor subject) {
				mode = -2;
				Gui.giveOnClick(mouseListener);
			}
		});

		button.setX(myLoadedMap.getStartingXPosition(colour)+screenX);
		button.setY(myLoadedMap.getStartingYPosition(colour)+screenY);
		button.adjust(0.05f, 0.05f);
		heroButtons .add(button);
		buttons.add(button);
		addChild(button);
	}
	
	private void createCopyOfSquare(Square square){
		final Square copy = Square.copy(square);
		final MouseListener mouseListener = new MouseListener(){
			@Override
			public boolean onClick(MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_UP){
					mode=0;
					Gui.removeOnClick(this);
				}
				return false;
			}

			@Override
			public boolean onHover(MotionEvent event) {
				int x = (int) (event.getX()*gridSize);
				int y = (int) (event.getY()*gridSize);
				float dx = ((float)x)/gridSize-copy.getX();
				float dy = ((float)y)/gridSize-copy.getY();
				copy.setX(copy.getX()+dx);
				copy.setY(copy.getY()+dy);
				return true;
			}

			@Override
			public void onMouseScroll(int distance) {				
			}
		};
		addIconsToSquare(copy,null);
		squares.add(copy);
		addChild(copy);
		copy.onAddToDrawable();
		Gui.giveOnClick(mouseListener);
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
			else if(46==keycode){
				createCopyOfSquare(squares.get(squares.size()-1));
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
		for(Square square:Hub.map.getTemplateSquares()){
			map.addTemplateSquare(square);
		}
		map.setStartPosition(0, myLoadedMap.getStartingXPosition(0), myLoadedMap.getStartingYPosition(0));
		map.setStartPosition(1, myLoadedMap.getStartingXPosition(1), myLoadedMap.getStartingYPosition(1));
		Storage.saveMap(saveTo.getAbsolutePath(), map);
	}
	private void moveView(float x, float y){
		this.screenX+=x;
		this.screenY+=y;
		for(int i=1;i<squares.size();++i){
			squares.get(i).setX(squares.get(i).getX()+x);
			squares.get(i).setY(squares.get(i).getY()+y);
		}
		for(GraphicEntity button:heroButtons){
			button.setX(button.getX()+x);
			button.setY(button.getY()+y);
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
		setupHeroButton(0);
		setupHeroButton(1);
		setupButtons();
		for(Square sqr:squares){
			addChild(sqr);
			addIconsToSquare(sqr,null);
		}

		reset = true;
	}

}
