package editor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import game.environment.Map;
import game.environment.Square;
import game.environment.oncreate.OnCreateSquare;
import game.environment.update.UpdatableSquare;
import game.menu.EditorMenu;
import game.menu.GetFileMenu;
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
	private Button gravityButton;
	private Button lightDependencyButton;
	
	private boolean musicPaused;
	public MapEditor(){
		super();
		saveTo = GetFileMenu.getFile(this,"maps",true);
		if(saveTo!=null){
			musicPaused = Hub.music.pause();
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
					squares.get(0).reposition(0f,0f);
				}
			}
			else {
				Hub.map = game.environment.Map.createMap(0);
				myLoadedMap = Hub.map;
				squares = Hub.map.getSquares();
				saveCurrent();
			}
			setupHeroButton(0);
			setupHeroButton(1);
			setupButtons();
		}

	}
	

	public void setupHeroButton(final int colour){
		final Button button = new Button("circles",colour,"Drag to move the "
				+ (colour==0?"Black":colour==1?"White":"OTHER")+" Hero's starting position.",null,null);
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
				button.reposition(snapClickToGrid(event.getX(),Map.X_axis),
						          snapClickToGrid(event.getY(),Map.Y_axis));
				myLoadedMap.setStartPosition(colour,button.getX()-screenX,button.getY()-screenY);
				return true;
			}

			@Override
			public void onMouseScroll(int distance) {				
			}

		};
		button.setOnClick(new ButtonAction(){
			@Override
			public void act(MotionEvent event) {
				mode = -2;
				Gui.giveOnClick(mouseListener);
			}
		});

		button.reposition(myLoadedMap.getStartingXPosition(colour)+screenX,
						  myLoadedMap.getStartingYPosition(colour)+screenY);
		button.resize(0.05f, 0.05f);
		heroButtons .add(button);
		buttons.add(button);
		addChild(button);
	}

	@Override
	public void setupButtons(){
		super.setupButtons();
		gravityButton = new Button("gravity_icons",(myLoadedMap.getMapId()+20)/-20,
				"G - Cycle through and set this map's game mode:\n"
				+ "  Puzzle: no gravity, non-competetive, just trying to get both Heroes to the end point. Uses Visual Bubbles.\n"
				+ "  Platform: the Heroes are effected by gravity in opposite vertical directions, objective is the same as the Puzzle mode.\n"
				+ "  Race: the objective is to get to the end of the map as fast as possible and if duo'd faster than other hero. Uses graviy uniformly downward on both Heroes.",null,new ButtonAction(){

			@Override
			public void act(MotionEvent subject) {
				myLoadedMap.setMapId(myLoadedMap.getMapId()==-60?-20:myLoadedMap.getMapId()-20);
				gravityButton.getIcon().setFrame((myLoadedMap.getMapId()+20)/-20);
			}
			
		});
		gravityButton.resize(0.05f, 0.05f);
		gravityButton.reposition(0.82f,
				           0.92f);
		addChild(gravityButton);
		buttons.add(gravityButton);
		
		this.lightDependencyButton = new Button("editor_light_dependency",myLoadedMap.isLightDependent()?0:1,
				"L - Toggle whether this map uses the rule that lets Heroes always be able to pass through oppositely coloured squares, and always blocked by samely coloured squares.(Doesn't work on race atm)",null,new ButtonAction(){
			@Override
			public void act(MotionEvent subject) {
				myLoadedMap.setLightDependency(!myLoadedMap.isLightDependent());
				lightDependencyButton.getIcon().setFrame(myLoadedMap.isLightDependent()?0:1);
			}			
		});
		this.lightDependencyButton.resize(0.05f, 0.05f);	
		lightDependencyButton.reposition(0.82f,
				                     0.87f);
		addChild(lightDependencyButton);
		buttons.add(lightDependencyButton);
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
			public boolean onHover(MotionEvent e) {
				copy.reposition(snapClickToGrid(e.getX(),Map.X_axis),
						snapClickToGrid(e.getY(),Map.Y_axis));
				return true;
			}

			@Override
			public void onMouseScroll(int distance) {				
			}
		};
		addSquare(copy);
		Gui.giveOnClick(mouseListener);
	}

	public void update(double seconds){
		if(saveTo==null){
			Gui.setView(new EditorMenu());
			if(musicPaused){
				Hub.music.unpause();
			}
		}
		//super.update(seconds);
	}
	@Override
	public void keyCommand(boolean b, char c, int keycode) {
		if(KeyBoardListener.UP==b){
			if(57==keycode){//space				
				toggleVisibleSquares();
			}
			else if(44==keycode&&!squares.isEmpty()){
				if(mostRecentlyRemovedSquare!=null){
					addSquare(mostRecentlyRemovedSquare);
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
					saveAndReturn();
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
			else if(keycode==38){//toggle light
				this.lightDependencyButton.performOnRelease(null);
			}
			else if(keycode==34){//toggle gravity
				gravityButton.performOnRelease(null);
			}
			else if(keycode==33){//toggle granity
				granityButton.performOnRelease(null);
			}
		}

	}
	protected void saveAndReturn() {
		saveCurrent();
		Gui.setView(new MainMenu());
		if(musicPaused){
			Hub.music.unpause();
		}
	}
	@Override
	public void saveCurrent(){
		for(Square square:squares){
			square.reposition(square.getX()-screenX,
					          square.getY()-screenY);
		}
		game.environment.Map map = game.environment.Map.createMap(myLoadedMap.getMapId());
		myLoadedMap.copyTo(map);
		for(Square square:squares){
			square.setRoot(this);
		}
		for(Square square:Hub.map.getTemplateSquares()){
			square.setRoot(this);
		}
		Storage.saveMap(saveTo.getAbsolutePath(), map);
		for(Square square:squares){
			square.reposition(square.getX()+screenX,
					          square.getY()+screenY);
		}
	}
	private void moveView(float x, float y){
		this.screenX+=x;
		this.screenY+=y;
		for(int i=1;i<squares.size();++i){
			squares.get(i).reposition(squares.get(i).getX()+x,
					             squares.get(i).getY()+y);
		}
		for(GraphicEntity button:heroButtons){
			button.reposition(button.getX()+x,
					      button.getY()+y);
		}
	}

	@Override
	public boolean continuousKeyboard() {
		return true;
	}


	@Override
	protected void openNew() {
		MapEditor openMap = new MapEditor();
		Gui.setView(openMap);
		openMap.musicPaused = this.musicPaused;
	}

}
