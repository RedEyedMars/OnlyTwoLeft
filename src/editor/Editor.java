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
import game.environment.OnCreateAction;
import game.environment.OnCreateSquare;
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

public class Editor extends GraphicView {

	protected static final float gridSize = 20f;

	protected int visibleTo=0;
	protected int mode=-1;
	protected int colour = 0;
	protected int colour2 = 0;
	protected int action1 = 0;
	protected int action2 = 0;
	protected int action3 = -1;

	protected List<Button> colourMenu = new ArrayList<Button>();
	protected List<Button> colour2Menu = new ArrayList<Button>();
	protected List<Button> actionMenu = new ArrayList<Button>();
	protected List<Button> actionMenu2 = new ArrayList<Button>();
	protected List<Button> updateActionMenu = new ArrayList<Button>();
	private Button onCreateAction;


	protected GraphicEntity visibleToShower = new GraphicEntity("circles",1);

	protected List<GraphicEntity> buttons = new ArrayList<GraphicEntity>();


	protected List<Square> squares;
	protected Square builder1;
	protected Square builder2;

	public Editor(){
		super();
		setupButtons();

		mode = 0;
	}

	protected void setupButtons(){
		for(int i=-1;i<16;++i){
			final int x = i;
			Button<Editor> button = new Button<Editor>("squares",i,this,new ButtonAction(){
				private int id;
				{
					id = x;
				}
				@Override
				public void act(Editor subject) {
					if(colour2>=0){
						colourMenu.get(colour+1).setSelected(false);
						colourMenu.get(id+1).setSelected(true);
						colour=id;
					}
					if(colour>=0){
						colour2Menu.get(colour2+1).setSelected(false);
						colour2Menu.get(id+1).setSelected(true);
						colour2=id;
					}
				}

			}){
				@Override
				public float offsetX(int i){
					return frame>=0?0.01f:0f;
				}
				@Override
				public float offsetY(int i){
					return frame>=0?0.01125f:0f;
				}
			};
			button.setX(0.08f+i*0.05f);
			button.setY(0.03f);
			button.adjust(0.05f,0.06f,0.03125f, 0.0375f);
			addChild(button);
			colourMenu.add(button);
			buttons.add(button);
		}
		for(int i=-1;i<16;++i){
			final int x = i;
			Button<Editor> button = new Button<Editor>("squares",i,this,new ButtonAction(){
				private int id;
				{
					id = x;
				}
				@Override
				public void act(Editor subject) {
					if(colour>=0){
						colour2Menu.get(colour2+1).setSelected(false);
						colour2Menu.get(id+1).setSelected(true);
						colour2=id;
					}
				}

			}){
				@Override
				public float offsetX(int i){
					return frame>=0?0.01f:0f;
				}
				@Override
				public float offsetY(int i){
					return frame>=0?0.01125f:0f;
				}
			};
			button.setX(0.08f+i*0.05f);
			button.setY(0.09f);
			button.adjust(0.05f,0.06f,0.03125f, 0.0375f);
			addChild(button);
			colour2Menu.add(button);
			buttons.add(button);
		}
		for(int i=-1;i<6;++i){
			final int x = i;
			Button<Editor> button = new Button<Editor>("editor_icons",i,this,new ButtonAction(){
				private int id;
				{
					id = x;
				}
				@Override
				public void act(Editor subject) {
					actionMenu2.get(action2+1).setSelected(false);
					actionMenu2.get(id+1).setSelected(true);
					actionMenu.get(action1+1).setSelected(false);
					actionMenu.get(id+1).setSelected(true);
					action1=id;
					action2=id;
					onCreateAction.setSelected(false);
				}
			});
			button.setX(0.08f+i*0.05f);
			button.setY(0.15f);
			button.adjust(0.05f,0.05f);
			actionMenu.add(button);
			buttons.add(button);
			addChild(button);
		}
		for(int i=-1;i<6;++i){
			final int x = i;
			Button<Editor> button = new Button<Editor>("editor_icons",i,this,new ButtonAction(){
				private int id;
				{
					id = x;
				}
				@Override
				public void act(Editor subject) {
					actionMenu2.get(action2+1).setSelected(false);
					actionMenu2.get(id+1).setSelected(true);
					action2=id;
					onCreateAction.setSelected(false);
				}

			});
			button.setX(0.08f+i*0.05f);
			button.setY(0.2f);
			button.adjust(0.05f,0.05f);
			actionMenu2.add(button);
			addChild(button);
			buttons.add(button);
		}
		for(int i=-1;i<1;++i){
			final int x = i;
			Button<Editor> button = new Button<Editor>("editor_update_icons",i,this,new ButtonAction(){
				private int id;
				{
					id = x;
				}
				@Override
				public void act(Editor subject) {
					updateActionMenu.get(action3+1).setSelected(false);
					updateActionMenu.get(id+1).setSelected(true);
					action3=id;
					onCreateAction.setSelected(false);
				}
			});
			button.setX(0.08f+i*0.05f);
			button.setY(0.25f);
			button.adjust(0.05f,0.05f);
			updateActionMenu.add(button);
			buttons.add(button);
			addChild(button);
		}
		onCreateAction = new Button<Editor>("editor_oncreate_icon",0,this,new ButtonAction(){
			private int[] previousActions = new int[3];
			{
				Button<Editor> button = new Button<Editor>("editor_oncreate_icon",-1,null,new ButtonAction(){
					@Override
					public void act(Editor subject) {
						if(onCreateAction.isSelected()){
							onCreateAction.setSelected(false);
							action1=previousActions[0];
							action2=previousActions[1];
							action3=previousActions[2];
							actionMenu.get(action1+1).setSelected(true);
							actionMenu2.get(action2+1).setSelected(true);
							updateActionMenu.get(action3+1).setSelected(true);
						}
					}
				});
				button.setX(0.03f);
				button.setY(0.3f);
				button.adjust(0.05f,0.05f);
				buttons.add(button);
				addChild(button);
			}
			@Override
			public void act(Editor subject) {
				if(!onCreateAction.isSelected()){
					onCreateAction.setSelected(true);
					previousActions[0]=action1;
					previousActions[1]=action2;
					previousActions[2]=action3;
					actionMenu.get(action1+1).setSelected(false);
					actionMenu2.get(action2+1).setSelected(false);
					updateActionMenu.get(action3+1).setSelected(false);
					action1=-1;
					action2=-1;
					action3=-1;
				}
			}
		});
		onCreateAction.setX(0.08f);
		onCreateAction.setY(0.3f);
		onCreateAction.adjust(0.05f,0.05f);
		buttons.add(onCreateAction);
		addChild(onCreateAction);

		colourMenu.get(colour+1).setSelected(true);
		colour2Menu.get(colour+1).setSelected(true);
		actionMenu.get(action1+1).setSelected(true);
		actionMenu2.get(action2+1).setSelected(true);
		updateActionMenu.get(action3+1).setSelected(true);
		onCreateAction.setSelected(false);

		visibleToShower.setX(0.95f);
		visibleToShower.setY(0.95f);
		visibleToShower.adjust(0.05f, 0.05f);
		visibleToShower.setFrame(3);
		addChild(visibleToShower);
	}

	protected boolean handleButtons(MotionEvent e){
		for(GraphicEntity child:buttons){
			if(child.isVisible()&&child.isWithin(e.getX(), e.getY())){
				child.performOnClick(e);
				return true;
			}
		}
		return false;
	}


	@Override
	public boolean onClick(MotionEvent e){
		if(e.getButton()==MotionEvent.MOUSE_LEFT){
			if(e.getAction()==MotionEvent.ACTION_DOWN){
				if(mode==0){
					if(handleButtons(e))return true;
					mode=2;
					int x = (int) (e.getX()*gridSize);
					int y = (int) (e.getY()*gridSize);

					List<Float> floats = new ArrayList<Float>();
					floats.add(((float)x)/gridSize);
					floats.add(((float)y)/gridSize);
					floats.add(0.05f);					

					if(action3>=0){
						for(int i=0;i<UpdateAction.actions.get(action3).numberOfFloats();++i){
							floats.add(0f);
						}
					}
					builder1=null;
					builder2=null;
					if(colour2!=colour){
						if(colour!=-1){
							Iterator<Integer> ints = Square.makeInts(action1,action2,action3,onCreateAction.isSelected(),colour,1,3);
							builder1 = Square.create(ints, floats.iterator());
						}
						if(colour2!=-1) {
							Iterator<Integer> ints = Square.makeInts(action1,action2,action3,onCreateAction.isSelected(),colour2,2,3);
							if(builder1==null){
								builder1 = Square.create(ints, floats.iterator());
							}
							else {
								builder2 = Square.create(ints, floats.iterator());
							}
						}
					}
					else {
						Iterator<Integer> ints = Square.makeInts(action1,action2,action3,onCreateAction.isSelected(),colour,0,3);
						builder1 = Square.create(ints, floats.iterator());
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
			else if(e.getAction()==MotionEvent.ACTION_UP){
				if(mode==-1){
					mode=0;
				}
				if(mode==2){
					mode=0;

					if(squares.get(0)==builder1){
						squares.get(0).setX(0f);
						squares.get(0).setY(0f);
						squares.get(0).adjust(1f,1f);
					}
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
						removeChild(square);						
						return true;
					}
				}
			}
		}
		return false;
	}
	protected void addIconsToSquare(Square square,Square square2) {
		addActionIconToSquare(square,square.getX()+square.getWidth()-0.05f,square.getY(),0.05f);
		addAdjustPositionButtonToSquare(square,square2);
		addAdjustSizeButtonToSquare(square,square2);
		addButtonToSquare(square);
		addOnCreateButtonToSquare(square);
	}
	private void addAdjustPositionButtonToSquare(final Square square, final Square square2) {
		final Button<Editor> button = new Button<Editor>(this,null);

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
		final Button<Editor> button = new Button<Editor>(this,null);
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
				e = new GraphicEntity("editor_button");
				e.setFrame(1);
			}
			else if(action instanceof SquareAction){
				e = new GraphicEntity("editor_icons");
				e.setFrame(action.getIndex());
			}
			else if(action instanceof UpdateAction){
				e = new GraphicEntity("editor_update_icons");
				e.setFrame(action.getIndex());
			}
			else if(action instanceof OnCreateAction){
				e = new GraphicEntity("editor_oncreate_icon");
				e.setFrame(0);
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
				final Button<Editor> button = new Button<Editor>("editor_update_icons",action.getIndex(),this,null);
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
	private void addOnCreateButtonToSquare(final Square ocs){
		List<Action> actions = ocs.getActions();
		for(Action temp:actions){
			if(temp instanceof OnCreateAction){
				final OnCreateAction action= ((OnCreateAction)temp);
				final Button<Editor> button = new Button<Editor>("editor_oncreate_icon",0,this,new ButtonAction(){
					@Override
					public void act(Editor subject) {
						squares.remove(ocs);
						if(getChildren().contains(ocs)){
							removeChild(ocs);						
						}
						Gui.setView(new OnCreateSquareEditor(
								(MapEditor) subject,
								ocs.getX(),ocs.getY(),ocs.getWidth(),ocs.getWidth()));
					}
				});
				button.setX(ocs.getX()+0.015f);
				button.setY(ocs.getY()+0.015f);
				button.adjust(0.05f, 0.05f);
				ocs.addChild(button);
				buttons.add(button);
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

}
