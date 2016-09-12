package editor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import game.environment.Square;
import game.environment.update.UpdatableSquare;
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

	private float screenX = 0;
	private float screenY = 0;

	private boolean reset = false;
	private List<GraphicEntity> heroButtons = new ArrayList<GraphicEntity>();
	private game.environment.Map myLoadedMap;
	private GraphicEntity gravityShower = new GraphicEntity("gravity_icons",1);
	private GraphicEntity lightDependencyShower = new GraphicEntity("editor_light_dependency",1);
	public MapEditor(){
		super();
		saveTo = Gui.userSave("maps");
		if(saveTo!=null){
			if(saveTo.exists()){
				Storage.loadMap(saveTo.getAbsolutePath());
				myLoadedMap = Hub.map;
				squares = Hub.map.getSquares();
				for(Square square:Hub.map.getSquares()){
					addIconsToSquare(square);
					if(square instanceof UpdatableSquare){
						for(Square dependant:((UpdatableSquare)square).getDependants()){
							addIconsToSquare(dependant);
						}
					}
					addChild(square);
				}
				if(squares.size()>0){
					squares.get(0).setX(0f);
					squares.get(0).setY(0f);
				}
			}
			else {
				Hub.map = game.environment.Map.createMap(0);
				myLoadedMap = Hub.map;
				squares = Hub.map.getSquares();
				saveMap();
			}
			setupHeroButton(0);
			setupHeroButton(1);
			setupButtons();
		}

	}
	

	public void setupHeroButton(final int colour){
		final Button button = new Button("circles",colour,null);
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

				int x = (int) (Hub.map.getIntX(event.getX()-0.025f)+2.5f);
				int y = (int) (Hub.map.getIntY(event.getY()-0.025f)+2.5f);
				x-=x%5;
				y-=y%5;
				float dx = Hub.map.getRealX(x)-button.getX();
				float dy = Hub.map.getRealY(y)-button.getY();
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
			public void act(Object subject) {
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

	@Override
	public void setupButtons(){
		super.setupButtons();
		gravityShower.adjust(0.04f, 0.04f);
		gravityShower.setX(0.9f);
		gravityShower.setY(0.96f);
		addChild(gravityShower);
		gravityShower.setFrame((myLoadedMap.getMapId()+20)/-20);
		
		this.lightDependencyShower.adjust(0.04f, 0.04f);
		lightDependencyShower.setX(0.905f);
		lightDependencyShower.setY(0.91f);
		addChild(lightDependencyShower);
		lightDependencyShower.setFrame(myLoadedMap.isLightDependent()?0:1);
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
				int x = Hub.map.getIntX(event.getX());
				int y = Hub.map.getIntY(event.getY());
				if(granityShower.textureIndex()==0){
					x = (int) (x+2.5f);
					y = (int) (y+2.5f);
					x-=x%5;
					y-=y%5;
				}
				float dx = Hub.map.getRealX(x)-copy.getX();
				float dy = Hub.map.getRealY(y)-copy.getY();
				copy.setX(copy.getX()+dx);
				copy.setY(copy.getY()+dy);
				return true;
			}

			@Override
			public void onMouseScroll(int distance) {				
			}
		};
		addIconsToSquare(copy);
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
				if(mostRecentlyRemovedSquare!=null){
					addIconsToSquare(mostRecentlyRemovedSquare);
					addChild(mostRecentlyRemovedSquare);
					mostRecentlyRemovedSquare.onAddToDrawable();
					squares.add(mostRecentlyRemovedSquare);
					mostRecentlyRemovedSquare = null;
				}
				else {
					mostRecentlyRemovedSquare = squares.remove(squares.size()-1);
					removeButtonsFromSquare(mostRecentlyRemovedSquare);
					removeChild(mostRecentlyRemovedSquare);
				}
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
				moveView(0,-0.2f);
			}
			else if(keycode==30||keycode==203){//left
				moveView(0.2f,0);
			}
			else if(keycode==31||keycode==208){//down
				moveView(0,0.2f);
			}
			else if(keycode==32||keycode==205){//right
				moveView(-0.2f,0);
			}
			else if(keycode==38){//toggle light
				myLoadedMap.setLightDependency(!myLoadedMap.isLightDependent());
				lightDependencyShower.setFrame(myLoadedMap.isLightDependent()?0:1);
			}
			else if(keycode==34){//toggle gravity
				myLoadedMap.setMapId(myLoadedMap.getMapId()==-60?-20:myLoadedMap.getMapId()-20);
				gravityShower.setFrame((myLoadedMap.getMapId()+20)/-20);
			}
			else if(keycode==33){//toggle granity
				granityShower.setFrame(granityShower.textureIndex()==0?1:0);
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

		game.environment.Map map = game.environment.Map.createMap(myLoadedMap.getMapId());
		myLoadedMap.copyTo(map);
		for(Square square:squares){
			square.setView(this);
		}
		for(Square square:Hub.map.getTemplateSquares()){
			square.setView(this);
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
		int restartWithSpecial = -1;
		for(int i=0;i<specialActionMenu.size();++i){
			if(specialActionMenu.get(i).isSelected()){
				restartWithSpecial = i;
				break;
			}
		}
		buttons.clear();
		setupHeroButton(0);
		setupHeroButton(1);
		setupButtons();
		if(restartWithSpecial>=0){
			for(int i=0;i<actionMenu.size();++i){
				actionMenu.get(i).setSelected(false);
				actionMenu2.get(i).setSelected(false);
			}
			specialActionMenu.get(restartWithSpecial).setSelected(true);
		}
		for(Square sqr:squares){
			addChild(sqr);
			addIconsToSquare(sqr);
			if(sqr instanceof UpdatableSquare){
				for(Square dependant:((UpdatableSquare)sqr).getDependants()){
					sqr.addChild(dependant);
					addIconsToSquare(dependant);
				}
			}
		}
		reset = true;
	}

}
