package editor;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFileChooser;

import game.Action;
import game.Hero;
import game.environment.FunctionalSquare;
import game.environment.Square;
import game.environment.SquareAction;
import game.environment.UpdatableSquare;
import game.environment.UpdateAction;
import game.menu.MainMenu;
import gui.Gui;
import gui.graphics.GraphicEntity;
import gui.graphics.GraphicView;
import gui.inputs.KeyBoardListener;
import gui.inputs.MotionEvent;
import gui.inputs.MouseListener;
import main.Hub;
import storage.Storage;

public class Editor extends GraphicView implements KeyBoardListener {

	private static final float gridSize = 20f;
	
	private int visibleTo=0;
	private int mode=-1;
	private int colour = 0;
	private int colour2 = 0;
	private int action1 = 1;
	private int action2 = 1;
	private int action3 = 0;

	private List<Button> colourMenu = new ArrayList<Button>();
	private List<Button> colour2Menu = new ArrayList<Button>();
	private List<Button> actionMenu = new ArrayList<Button>();
	private List<Button> actionMenu2 = new ArrayList<Button>();
	private List<Button> updateActionMenu = new ArrayList<Button>();


	private GraphicEntity visibleToShower = new GraphicEntity("circles",1);

	private List<GraphicEntity> buttons = new ArrayList<GraphicEntity>();

	private List<Square> squares = new ArrayList<Square>();
	private Square builder1;
	private Square builder2;
	private File saveTo = null;
	private boolean readyToAddToDrawable = false;

	private HashMap<String,Square> screenBackgrounds = new HashMap<String,Square>();
	private float screenX = 0;
	private float screenY = 0;
	public Editor(){
		super();

		for(int i=0;i<8;++i){
			final int x = i;
			Button<Editor> button = new Button<Editor>("editor_icons","squares",i,this,new ButtonAction(){
				private int id;
				{
					id = x;
				}
				@Override
				public void act(Editor subject) {
					colourMenu.get(colour).setSelected(false);
					colourMenu.get(id).setSelected(true);
					colour2Menu.get(colour2).setSelected(false);
					colour2Menu.get(id).setSelected(true);
					colour=id;
					colour2=id;
				}

			}){
				@Override
				public float offsetX(int i){
					return i==0?0.011f:0f;
				}
				@Override
				public float offsetY(int i){
					return i==0?0.01125f:0f;
				}
			};
			button.setX(0.03f+i*0.06f);
			button.setY(0.03f);
			button.adjust(0.06f,0.06f,0.0375f, 0.0375f);
			addChild(button);
			colourMenu.add(button);
			buttons.add(button);
		}
		for(int i=0;i<8;++i){
			final int x = i;
			Button<Editor> button = new Button<Editor>("editor_icons","squares",i,this,new ButtonAction(){
				private int id;
				{
					id = x;
				}
				@Override
				public void act(Editor subject) {
					colour2Menu.get(colour2).setSelected(false);
					colour2Menu.get(id).setSelected(true);
					colour2=id;
				}

			}){
				@Override
				public float offsetX(int i){
					return i==0?0.011f:0f;
				}
				@Override
				public float offsetY(int i){
					return i==0?0.01125f:0f;
				}
			};
			button.setX(0.03f+i*0.06f);
			button.setY(0.09f);
			button.adjust(0.06f,0.06f,0.0375f, 0.0375f);
			addChild(button);
			colour2Menu.add(button);
			buttons.add(button);
		}
		for(int i=0;i<6;++i){
			final int x = i;
			Button<Editor> button = new Button<Editor>("editor_icons",i+1,this,new ButtonAction(){
				private int id;
				{
					id = x;
				}
				@Override
				public void act(Editor subject) {
					actionMenu2.get(action2).setSelected(false);
					actionMenu2.get(id).setSelected(true);
					actionMenu.get(action1).setSelected(false);
					actionMenu.get(id).setSelected(true);
					action1=id;
					action2=id;
				}
			});
			button.setX(0.03f+i*0.05f);
			button.setY(0.15f);
			button.adjust(0.05f,0.05f);
			actionMenu.add(button);
			buttons.add(button);
			addChild(button);
		}
		for(int i=0;i<4;++i){
			final int x = i;
			Button<Editor> button = new Button<Editor>("editor_icons",i+1,this,new ButtonAction(){
				private int id;
				{
					id = x;
				}
				@Override
				public void act(Editor subject) {
					actionMenu2.get(action2).setSelected(false);
					actionMenu2.get(id).setSelected(true);
					action2=id;
				}

			});
			button.setX(0.03f+i*0.05f);
			button.setY(0.2f);
			button.adjust(0.05f,0.05f);
			actionMenu2.add(button);
			addChild(button);
			buttons.add(button);
		}
		for(int i=0;i<2;++i){
			final int x = i;
			Button<Editor> button = new Button<Editor>("editor_update_icons",i+1,this,new ButtonAction(){
				private int id;
				{
					id = x;
				}
				@Override
				public void act(Editor subject) {
					updateActionMenu.get(action3).setSelected(false);
					updateActionMenu.get(id).setSelected(true);
					action3=id;
				}
			});
			button.setX(0.03f+i*0.05f);
			button.setY(0.25f);
			button.adjust(0.05f,0.05f);
			updateActionMenu.add(button);
			buttons.add(button);
			addChild(button);
		}


		colourMenu.get(colour).setSelected(true);
		colour2Menu.get(colour).setSelected(true);
		actionMenu.get(action1).setSelected(true);
		actionMenu2.get(action2).setSelected(true);
		updateActionMenu.get(action3).setSelected(true);

		visibleToShower.setX(0.2f);
		visibleToShower.setY(0.95f);
		visibleToShower.adjust(0.05f, 0.05f);
		visibleToShower.setFrame(3);
		addChild(visibleToShower);

		Gui.giveOnType(this);

		saveTo = Gui.userSave();
		if(saveTo!=null&&saveTo.exists()){
			Storage.loadMap(saveTo.getAbsolutePath());
			for(Square square:Hub.map.getSquares()){

				squares.add(square);
				addIconsToSquare(square,null);
				addChild(square);
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
	public boolean onClick(MotionEvent e){
		if(e.getButton()==MotionEvent.MOUSE_LEFT){
			if(e.getAction()==MotionEvent.ACTION_DOWN){
				if(mode==0){
					for(GraphicEntity child:buttons){
						if(child.isVisible()&&child.isWithin(e.getX(), e.getY())){
							child.performOnClick(e);
							return true;
						}
					}
					mode=2;
					int x = (int) (e.getX()*gridSize);
					int y = (int) (e.getY()*gridSize);

					List<Float> floats = new ArrayList<Float>();
					floats.add(((float)x)/gridSize);
					floats.add(((float)y)/gridSize);
					floats.add(0.05f);

					if(action3!=0){
						for(int i=0;i<UpdateAction.actions.get(action3-1).numberOfFloats();++i){
							floats.add(0f);
						}
					}

					if(colour2!=colour){
						Iterator<Integer> ints = Square.makeInts(action1-1,action2-1,action3-1,-1,colour,1,3);
						builder1 = Square.create(ints, floats.iterator());
						ints = Square.makeInts(action1-1,action2-1,action3-1,-1,colour2,2,3);
						builder2 = Square.create(ints, floats.iterator());
					}
					else {
						Iterator<Integer> ints = Square.makeInts(action1-1,action2-1,action3-1,-1,colour,0,3);
						builder1 = Square.create(ints, floats.iterator());
						builder2 = null;
					}
					if(!screenBackgrounds.containsKey(((int)screenX)+","+((int)screenY))){
						mode = -1;
						screenBackgrounds.put(((int)screenX)+","+((int)screenY), builder1);
						builder1.setX(0f);
						builder1.setY(0f);
						builder1.adjust(1f, 1f);
					}
					if(builder2!=null){
						addChild(builder2);
						builder2.onAddToDrawable();
						squares.add(builder2);
					}
					addChild(builder1);
					builder1.onAddToDrawable();
					squares.add(builder1);
				}
				else if(mode==2){
					int x = (int) (e.getX()*gridSize);
					int y = (int) (e.getY()*gridSize);					
					builder1.adjust(((float)x)/gridSize-builder1.getX(), ((float)y)/gridSize-builder1.getY());
					removeChild(builder1);
					if(builder2!=null){
						builder2.adjust(((float)x)/gridSize-builder2.getX(), ((float)y)/gridSize-builder2.getY());
						removeChild(builder2);
						addChild(builder2);
					}
					addChild(builder1);
					builder1.onAddToDrawable();
					if(builder2!=null){
						builder2.onAddToDrawable();
					}
				}
			}
			if(e.getAction()==MotionEvent.ACTION_UP){
				if(mode==-1){
					mode=0;
				}
				if(mode==2){
					mode=0;
					removeChild(builder1);
					if(builder2!=null){
						removeChild(builder2);
					}
					addIconsToSquare(builder1,builder2);
					if(builder2!=null){
						addChild(builder2);
						builder2.onAddToDrawable();
					}
					addChild(builder1);
					builder1.onAddToDrawable();
					
				}
			}
		}
		else if(e.getButton()==MotionEvent.MOUSE_RIGHT){
			if(e.getAction()==MotionEvent.ACTION_UP){
				for(int i=squares.size()-1;i>=0;--i){
					if(squares.get(i).isWithin(e.getX(), e.getY())){
						Square square = squares.remove(i);
						if(screenBackgrounds.containsValue(square)){
							screenBackgrounds.remove(((int)screenX)+","+((int)screenY));
						}
						removeChild(square);						
						return true;
					}
				}
			}
		}
		return false;
	}
	private void addIconsToSquare(Square square,Square square2) {
		addActionIconToSquare(square,square.getX()+square.getWidth()-0.05f,square.getY(),0.05f);
		addAdjustPositionButtonToSquare(square,square2);
		addAdjustSizeButtonToSquare(square,square2);
		addButtonToSquare(square);

	}
	private void addAdjustPositionButtonToSquare(final Square square, final Square square2) {
		final Button<Editor> button = new Button<Editor>("editor_update_icons","blank",0,this,null);

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
				float dx = ((float)x)/gridSize-square.getX();
				float dy = ((float)y)/gridSize-square.getY();
				square.setX(square.getX()+dx);
				square.setY(square.getY()+dy);
				if(square2!=null){
					square2.setX(square2.getX()+dx);
					square2.setY(square2.getY()+dy);
				}
				return false;
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

		button.setX(square.getX());
		button.setY(square.getY());
		button.adjust(0.015f, 0.015f);
		buttons.add(button);
		square.addChild(button);
	}
	private void addAdjustSizeButtonToSquare(final Square square, final Square square2) {
		final Button<Editor> button = new Button<Editor>("editor_update_icons","blank",0,this,null);
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
				square.adjust(((float)x)/gridSize-square.getX(), ((float)y)/gridSize-square.getY());
				if(square2!=null){
					square2.adjust(((float)x)/gridSize-square2.getX(), ((float)y)/gridSize-square2.getY());					
				}
				button.setX(square.getX()+square.getWidth()-0.015f);
				button.setY(square.getY()+square.getHeight()-0.015f);

				for(GraphicEntity e:square.getChildren()){
					if(e instanceof Button){
						buttons.remove(e);
					}
				}
				if(children.contains(square)){
					removeChild(square);
					if(square2!=null){
						removeChild(square2);
					}
					addIconsToSquare(square,square2);
					if(square2!=null){
						addChild(square2);
						square2.onAddToDrawable();
					}
					addChild(square);
					square.onAddToDrawable();
				}
				return false;
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

		button.setX(square.getX()+square.getWidth()-0.015f);
		button.setY(square.getY()+square.getHeight()-0.015f);
		button.adjust(0.015f, 0.015f);
		buttons.add(button);
		square.addChild(button);
	}
	private void addActionIconToSquare(Square fsq, float x, float y,float size){
		GraphicEntity e = null;
		List<Action> actions = fsq.getActions();
		for(Action action:actions){
			if(action == null){
				e.setFrame(1);
			}
			else if(action instanceof SquareAction){
				e = new GraphicEntity("editor_icons");
				e.setFrame(action.getIndex()+2);
			}
			else if(action instanceof UpdateAction){
				e = new GraphicEntity("editor_update_icons");
				e.setFrame(action.getIndex()+2);
			}
			e.setX(x);
			e.setY(y);
			e.adjust(size, size);
			x-=size;
			fsq.addChild(e);
		}
	}
	private void addButtonToSquare(final Square usq){
		List<Action> actions = usq.getActions();
		for(Action temp:actions){
			if(temp instanceof UpdateAction){
				final UpdateAction action= ((UpdateAction)temp);
				final Button<Editor> button = new Button<Editor>("editor_update_icons",action.getIndex()+2,this,null);
				final MouseListener listener = new MouseListener(){
					@Override
					public boolean onClick(MotionEvent event) {
						if(event.getAction()==MotionEvent.ACTION_UP){
							Gui.removeOnClick(this);
						}
						return false;
					}

					@Override
					public boolean onHover(MotionEvent event) {
						button.setX(event.getX());
						button.setY(event.getY());
						action.addFloats(event.getX()-usq.getX(),event.getY()-usq.getY());	
						return false;
					}

					@Override
					public void onMouseScroll(int distance) {

					}
				};
				button.setAction(new ButtonAction(){
					@Override
					public void act(Editor subject) {
						Gui.giveOnClick(listener);
					}
				});
				button.setX(usq.getX()+action.getFloat(0));
				button.setY(usq.getY()+action.getFloat(1));
				button.adjust(0.05f, 0.05f);
				usq.addChild(button);
				buttons.add(button);
			}
		}
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
				saveAndReturnToMainMenu();
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
	public void setVisibleSquares(int colour){
		for(Square square:squares){
			if(square.visibleToBlack()&&(colour==2)){
				square.turnOff();
			}
			else if(square.visibleToWhite()&&(colour==1)){
				square.turnOff();
			}
		}
		for(Square square:squares){
			if(colour==0){
				square.turnOn();
			}
			else if(square.visibleToBlack()&&(colour<2)){
				square.turnOn();
			}
			else if(square.visibleToWhite()&&(colour!=1)){
				square.turnOn();
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
		for(Square square:squares){
			square.setX(square.getX()+x);
			square.setY(square.getY()+y);
		}
	}

	@Override
	public boolean continuousKeyboard() {
		return true;
	}



}
