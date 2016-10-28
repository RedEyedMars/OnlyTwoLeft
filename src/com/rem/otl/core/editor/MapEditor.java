package com.rem.otl.core.editor;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.rem.otl.core.game.Game;
import com.rem.otl.core.game.environment.Map;
import com.rem.otl.core.game.environment.Square;
import com.rem.otl.core.game.environment.oncreate.OnCreateSquare;
import com.rem.otl.core.game.environment.update.UpdatableSquare;
import com.rem.otl.core.game.hero.Hero;
import com.rem.otl.core.game.menu.EditorMenu;
import com.rem.otl.core.game.menu.GetFileMenu;
import com.rem.otl.core.game.menu.MainMenu;
import com.rem.otl.core.gui.Gui;
import com.rem.otl.core.gui.graphics.GraphicEntity;
import com.rem.otl.core.gui.graphics.GraphicView;
import com.rem.otl.core.gui.inputs.HoverEvent;
import com.rem.otl.core.gui.inputs.KeyBoardEvent;
import com.rem.otl.core.gui.inputs.KeyBoardListener;
import com.rem.otl.core.gui.inputs.ClickEvent;
import com.rem.otl.core.gui.inputs.MouseListener;
import com.rem.otl.core.main.Hub;
import com.rem.otl.core.storage.Resource;
import com.rem.otl.core.storage.Storage;

public class MapEditor extends Editor implements KeyBoardListener{

	private Resource<InputStream> saveTo = null;

	private float screenX = 0;
	private float screenY = 0;

	private boolean reset = false;
	private List<GraphicEntity> heroButtons = new ArrayList<GraphicEntity>();
	private com.rem.otl.core.game.environment.Map myLoadedMap;
	private Button gravityButton;
	private Button lightDependencyButton;

	private boolean musicPaused;
	public MapEditor(GraphicView parentView){
		super(parentView);
		saveTo = GetFileMenu.getFile(this,"maps",true);
		if(saveTo!=null){
			if(Hub.music!=null){
				musicPaused = Hub.music.pause();
			}

			Storage.loadMap(saveTo);
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

			saveCurrent();

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
			public boolean onClick(ClickEvent event) {
				if(event.getAction()==ClickEvent.ACTION_UP){
					mode=0;
					Hub.handler.removeOnClick(this);
				}
				return false;
			}

			@Override
			public boolean onHover(HoverEvent event) {
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
			public void act(ClickEvent event) {
				mode = -2;
				Hub.handler.giveOnClick(mouseListener);
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
		final MapEditor self = this;
		Button playButton = new Button("editor_button_2",0,
				" - Preview the map by playing Solo on it",null,new ButtonAction(){

			@Override
			public void act(ClickEvent subject) {
				saveCurrent();
				Storage.loadMap(saveTo);
				Hub.gui.setView(new Game(
						visibleTo==Hero.BLACK_INT?Hero.BLACK_BOOL:
							visibleTo==Hero.WHITE_INT?Hero.WHITE_BOOL:Hero.BLACK_BOOL,
									Hub.seed,self){
					@Override
					public void returnToParent(){
						super.returnToParent();
						Storage.loadMap(saveTo);
					}
				});
			}

		});
		playButton.resize(0.05f, 0.05f);
		playButton.reposition(0.18f, 0.92f);
		addChild(playButton);
		buttons.add(playButton);

		gravityButton = new Button("gravity_icons",(myLoadedMap.getMapId()+20)/-20,
				"G - Cycle through and set this map's game mode:\n"
						+ "  Puzzle: no gravity, non-competetive, just trying to get both Heroes to the end point. Uses Visual Bubbles.\n"
						+ "  Platform: the Heroes are effected by gravity in opposite vertical directions, objective is the same as the Puzzle mode.\n"
						+ "  Race: the objective is to get to the end of the map as fast as possible and if duo'd faster than other hero. Uses graviy uniformly downward on both Heroes.\n"
						+ "  Android: similar to Race mode, except the only method of control is through clicking, so right and left are down clicks, release is jump.",null,new ButtonAction(){

			@Override
			public void act(ClickEvent subject) {
				myLoadedMap.setMapId(myLoadedMap.getMapId()==-80?-20:myLoadedMap.getMapId()-20);
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
			public void act(ClickEvent subject) {
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
			public boolean onClick(ClickEvent event) {
				if(event.getAction()==ClickEvent.ACTION_UP){
					mode=0;
					Hub.handler.removeOnClick(this);
				}
				return false;
			}

			@Override
			public boolean onHover(HoverEvent e) {
				copy.reposition(snapClickToGrid(e.getX(),Map.X_axis),
						snapClickToGrid(e.getY(),Map.Y_axis));
				return true;
			}

			@Override
			public void onMouseScroll(int distance) {				
			}
		};
		addSquare(copy);
		Hub.handler.giveOnClick(mouseListener);
	}

	public void update(double seconds){
		if(saveTo==null){
			Hub.gui.setView(new EditorMenu());
			if(musicPaused){
				Hub.music.unpause();
			}
		}
		//super.update(seconds);
	}
	@Override
	public void onType(KeyBoardEvent event) {
		if(event.keyUp()){
			if(event.is(KeyBoardEvent.SPACE)){//space				
				toggleVisibleSquares();
			}
			else if(event.is(44)&&!squares.isEmpty()){
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
			else if(event.is(45)){
				if(!reset){
					saveAndReturn();
				}
				else {
					reset = false;
				}
			}
			else if(event.is(46)){
				createCopyOfSquare(squares.get(squares.size()-1));
			}
			else if(event.is(17)||event.is(KeyBoardEvent.UP)){//up
				moveView(0,-0.5f);
			}
			else if(event.is(30)||event.is(KeyBoardEvent.LEFT)){//left
				moveView(0.5f,0);
			}
			else if(event.is(31)||event.is(KeyBoardEvent.DOWN)){//down
				moveView(0,0.5f);
			}
			else if(event.is(32)||event.is(KeyBoardEvent.RIGHT)){//right
				moveView(-0.5f,0);
			}
			else if(event.is(38)){//toggle light
				this.lightDependencyButton.performOnRelease(null);
			}
			else if(event.is(34)){//toggle gravity
				gravityButton.performOnRelease(null);
			}
			else if(event.is(33)){//toggle granity
				granityButton.performOnRelease(null);
			}
		}

	}
	protected void saveAndReturn() {
		saveCurrent();
		Hub.gui.setView(parentView);
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
		com.rem.otl.core.game.environment.Map map = com.rem.otl.core.game.environment.Map.createMap(myLoadedMap.getMapId());
		myLoadedMap.copyTo(map);
		for(Square square:squares){
			square.setRoot(this);
		}
		for(Square square:Hub.map.getTemplateSquares()){
			square.setRoot(this);
		}
		Storage.saveMap(Hub.manager.createOutputStream(saveTo.getPath()), map);
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
		MapEditor openMap = new MapEditor(parentView);
		Hub.gui.setView(openMap);
		openMap.musicPaused = this.musicPaused;
	}


	public GraphicView getParentView() {
		return parentView;
	}

}
