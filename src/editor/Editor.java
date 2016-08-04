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
import game.environment.OnStepSquare;
import game.environment.OnCreateAction;
import game.environment.OnCreateSquare;
import game.environment.Square;
import game.environment.SquareAction;
import game.environment.OnStepAction;
import game.environment.UpdatableSquare;
import game.environment.UpdateAction;
import game.menu.MainMenu;
import game.menu.MenuButton;
import gui.Gui;
import gui.graphics.GraphicEntity;
import gui.graphics.GraphicView;
import gui.inputs.KeyBoardListener;
import gui.inputs.MotionEvent;
import gui.inputs.MouseListener;
import main.Hub;
import storage.Storage;

public class Editor extends GraphicView {


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


	private UpdatableSquare updatableSquareFocused;
	private MenuButton updatableSquareDataField;
	private TextWriter updatableSquareXField;
	private TextWriter updatableSquareYField;
	private TextWriter updatableSquareLimitField;
	private GraphicEntity updatableSquareDefaultStateIndicator;
	private GraphicEntity updatableSquareLimiterIndicator;
	
	protected List<Square> squares;
	protected Square builder1;

	public Editor(){
		super();
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
					colourMenu.get(colour+1).setSelected(false);
					colourMenu.get(id+1).setSelected(true);
					colour=id;

					colour2Menu.get(colour2+1).setSelected(false);
					colour2Menu.get(id+1).setSelected(true);
					colour2=id;
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
					colour2Menu.get(colour2+1).setSelected(false);
					colour2Menu.get(id+1).setSelected(true);
					colour2=id;
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
		for(int i=-1;i<7;++i){
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
		for(int i=-1;i<7;++i){
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
		for(int i=-1;i<2;++i){
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
		
		updatableSquareDataField = new MenuButton("X:\nY:\nLimit:"){
			{
				text.setWidthFactor(1f);
				text.setHeightFactor(1f);
			}
			@Override
			public void adjust(float x, float y){
				super.adjust(x, y);
				if(6<size()){
					getChild(6).adjust(0.04f, 0.04f);
				}
				if(7<size()){
					getChild(7).adjust(0.04f, 0.04f);
				}
			}
			@Override
			public float offsetX(int index){
				if(index<3){
					return super.offsetX(index);
				}
				else if(index==3){
					return 0.065f;
				}
				else if(index<6){
					return 0.0925f;
				}
				else if(index==8){
					return 0.14f;
				}
				else {
					return 0.02f;
				}
			}
			@Override
			public float offsetY(int index){
				if(index<3){
					return super.offsetY(index);
				}
				else if(index<=4){
					return 0.055f;
				}
				else if(index==5){
					return 0.03f;
				}
				else if(index==6){
					return 0.055f;
				}
				else if(index==8){
					return 0.005f;
				}
				else return 0f;
			}
		};

		updatableSquareXField = new TextWriter("impact"," "){
			{
				charIndex=0;
				index=0;
			}
			@Override
			public void keyCommand(boolean b, char c, int keycode){
				if(c>=48&&c<=57||c==46||c==45||keycode==14){
					super.keyCommand(b, c, keycode);
				}
				else if(b==KeyBoardListener.UP&&(keycode==15||keycode==28)){
					Gui.removeOnType(this);
					Gui.giveOnType(updatableSquareYField);
				}
			}
		};
		updatableSquareYField = new TextWriter("impact"," "){
			{
				charIndex=0;
				index=0;
			}
			@Override
			public void keyCommand(boolean b, char c, int keycode){
				if(c>=48&&c<=57||c==46||c==45||keycode==14){
					super.keyCommand(b, c, keycode);
				}
				else if(b==KeyBoardListener.UP&&(keycode==15||keycode==28)){
					Gui.removeOnType(this);
					if(updatableSquareFocused.getAction().getLimiter()==-1){
						Gui.removeOnClick(updatableSquareDataField);
						updatableSquareDataField.setVisible(false);
					}
					else {
						Gui.giveOnType(updatableSquareLimitField);
					}
					try {
						updatableSquareFocused.getAction().addFloats(
							Float.parseFloat(updatableSquareXField.getText()),
							Float.parseFloat(updatableSquareYField.getText()));
					}
					catch(NumberFormatException e){
						e.printStackTrace();
					}
				}
			}
		};
		updatableSquareLimitField = new TextWriter("impact"," "){
			{
				charIndex=0;
				index=0;
			}
			@Override
			public void keyCommand(boolean b, char c, int keycode){
				if(c>=48&&c<=57||c==46||c==45||keycode==14){
					super.keyCommand(b, c, keycode);
				}
				else if(b==KeyBoardListener.UP&&(keycode==15||keycode==28)){
					try {
						updatableSquareFocused.getAction().setLimit(
								Float.parseFloat(updatableSquareLimitField.getText()));
					}
					catch(NumberFormatException e){
						e.printStackTrace();
					}
					Gui.removeOnType(this);
					Gui.removeOnClick(updatableSquareDataField);
					updatableSquareDataField.setVisible(false);
				}
			}
		};
		updatableSquareDefaultStateIndicator = new GraphicEntity("editor_icons",1){
			{
				this.listenToRelease=true;
			}
			@Override
			public void performOnRelease(MotionEvent e){
				if(!updatableSquareDataField.isVisible())return;
				updatableSquareFocused.getAction().
									setDefaultState(!updatableSquareFocused.getAction().getDefaultState());
				this.setFrame(updatableSquareFocused.getAction().getDefaultState()?3:4);
			}
		};
		updatableSquareDefaultStateIndicator.setFrame(3);
		updatableSquareLimiterIndicator = new GraphicEntity("editor_button",1){			
			{
				this.listenToRelease=true;
				this.addChild(new GraphicEntity("editor_update_limiter_icons",1));
				this.getChild(0).turnOff();
			}
			@Override
			public void performOnRelease(MotionEvent e){
				if(!updatableSquareDataField.isVisible())return;
				updatableSquareFocused.getAction().setLimiter(
						updatableSquareFocused.getAction().getLimiter()+1);
				if(updatableSquareFocused.getAction().getLimiter()>=3){
					updatableSquareFocused.getAction().setLimiter(-1);
				}
				setFrame(1);
			}
			@Override
			public void setFrame(int i){
				super.setFrame(1);
				if(updatableSquareFocused==null)return;
				if(updatableSquareFocused.getAction().getLimiter()==-1){
					turnOn();
					getChild(0).turnOff();
				}
				else {
					turnOff();
					getChild(0).turnOn();
					getChild(0).setFrame(updatableSquareFocused.getAction().getLimiter());
				}
			}
		};
		updatableSquareLimiterIndicator.setFrame(1);
		updatableSquareDataField.addChild(updatableSquareXField);
		updatableSquareDataField.addChild(updatableSquareYField);
		updatableSquareDataField.addChild(updatableSquareDefaultStateIndicator);
		updatableSquareDataField.addChild(updatableSquareLimiterIndicator);
		updatableSquareDataField.addChild(updatableSquareLimitField);
		addChild(updatableSquareDataField);
		updatableSquareDataField.adjust(0.3f, 0.1f);
		updatableSquareDataField.setVisible(false);
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
					int x = Hub.map.getIntXLow(e.getX());
					int y = Hub.map.getIntYLow(e.getY());
					builder1 = createSquare(x,y,colour,colour2,action1,action2,action3,onCreateAction.isSelected());				
					
					addChild(builder1);
					builder1.onAddToDrawable();
					squares.add(builder1);
				}
				else if(mode==2){
					int x = Hub.map.getIntXHigh(e.getX());
					int y = Hub.map.getIntYHigh(e.getY());				
					builder1.adjust(Hub.map.getRealX(x)-builder1.getX(), Hub.map.getRealY(y)-builder1.getY());
					//removeChild(builder1);
					builder1.onRemoveFromDrawable();
					//addChild(builder1);
					builder1.onAddToDrawable();
				}
			}
			else if(e.getAction()==MotionEvent.ACTION_UP){
				if(mode==-1){
					mode=0;
				}
				if(mode==2){
					mode=0;

					if(squares.get(0)==builder1&&this instanceof MapEditor){
						squares.get(0).setX(0f);
						squares.get(0).setY(0f);
						squares.get(0).adjust(1f,1f);
					}

					//removeChild(builder1);
					builder1.onRemoveFromDrawable();
					addIconsToSquare(builder1);
					//addChild(builder1);
					builder1.onAddToDrawable();
				}
			}
		}
		else if(e.getButton()==MotionEvent.MOUSE_RIGHT){
			if(e.getAction()==MotionEvent.ACTION_UP){
				for(int i=squares.size()-1;i>=0;--i){
					if(squares.get(i).isWithin(e.getX(), e.getY())){
						Square square = squares.remove(i);
						removeButtonsFromSquare(square);
						removeChild(square);
						return true;
					}
					else if(squares.get(i) instanceof UpdatableSquare){
						List<Square> depends = ((UpdatableSquare)squares.get(i)).getDependants();
						for(int j=depends.size()-1;j>=0;--j){
							if(depends.get(j).isWithin(e.getX(), e.getY())){
								Square square = depends.remove(j);
								removeButtonsFromSquare(square);
								squares.get(i).removeChild(square);
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
	protected Square createSquare(int x, int y, int colour, int colour2, int a1, int a2, int ua, boolean oc){
		mode=2;
		List<Float> floats = new ArrayList<Float>();
		if(action3>=0){			
			floats.add(0f);
			floats.add(0f);
		}
		Iterator<Integer> ints = Square.makeInts(a1,a2,ua,oc,colour,colour2,x,y,1,1);
		return Square.create(ints, floats.iterator());
	}
	
	protected void removeButtonsFromSquare(Square square){
		for(GraphicEntity e:square.getChildren()){
			if(e instanceof Button){
				buttons.remove((Button<Editor>)e);
			}
			else if(e instanceof Square){
				removeButtonsFromSquare((Square)e);
			}
		}
	}

	protected void addIconsToSquare(Square square) {
		addActionIconToSquare(square,square.getX()+square.getWidth()-0.025f,square.getY(),0.025f);
		addAdjustPositionButtonToSquare(square);
		addAdjustSizeButtonToSquare(square);
		if(square instanceof UpdatableSquare){
			addUpdateButtonToSquare((UpdatableSquare) square);
		}
		addOnCreateButtonToSquare(square);
	}
	private void addAdjustPositionButtonToSquare(final Square square) {
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
				int x = Hub.map.getIntXLow(event.getX());
				int y = Hub.map.getIntYLow(event.getY());
				float dx = Hub.map.getRealX(x)-square.getX();
				float dy = Hub.map.getRealY(y)-square.getY();
				square.setX(square.getX()+dx);
				square.setY(square.getY()+dy);
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
	private void addAdjustSizeButtonToSquare(final Square square) {
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

				int x = Hub.map.getIntXHigh(event.getX());
				int y = Hub.map.getIntYHigh(event.getY());
				square.adjust(Hub.map.getRealX(x)-square.getX(), Hub.map.getRealY(y)-square.getY());
				button.setX(square.getX()+square.getWidth()-0.015f);
				button.setY(square.getY()+square.getHeight()-0.015f);
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
		List<SquareAction> actions = fsq.getActions();
		for(SquareAction action:actions){
			if(action == null){
				e = new GraphicEntity("editor_button");
				e.setFrame(1);
			}
			else if(action instanceof OnStepAction){
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
	private void addUpdateButtonToSquare(final UpdatableSquare usq){
		List<SquareAction> actions = usq.getActions();
		for(SquareAction temp:actions){
			if(temp instanceof UpdateAction){
				final UpdateAction action= ((UpdateAction)temp);
				final Button<Editor> button = new Button<Editor>("editor_update_icons",action.getIndex(),this,null);
				final Button<Editor> activator = new Button<Editor>("editor_icons",3,this,null);
				/*final MouseListener listener = new MouseListener(){
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
						activator.setX(button.getX()+0.05f);
						activator.setY(button.getY());
						action.addFloats(event.getX()-usq.getX(),event.getY()-usq.getY());	
						return false;
					}

					@Override
					public void onMouseScroll(int distance) {
					}
				};*/
				button.setAction(new ButtonAction(){
					@Override
					public void act(Editor subject) {
						updatableSquareFocused=usq;
						updatableSquareDataField.setX(usq.getX()+0.015f);
						updatableSquareDataField.setY(usq.getY()+0.015f);
						updatableSquareXField.changeTextOnLine(""+usq.getAction().getFloat(0), 0);
						updatableSquareYField.changeTextOnLine(""+usq.getAction().getFloat(1), 0);
						updatableSquareLimitField.changeTextOnLine(""+usq.getAction().getFloat(2), 0);
						updatableSquareDefaultStateIndicator.setFrame(usq.getAction().getDefaultState()?3:4);
						updatableSquareLimiterIndicator.setFrame(1);
						updatableSquareDataField.setVisible(true);
						Gui.giveOnClick(updatableSquareDataField);						
						Gui.giveOnType(updatableSquareXField);
					}
				});
				button.setX(usq.getX()+0.015f);
				button.setY(usq.getY()+0.015f);
				button.adjust(0.025f, 0.025f);
				usq.addChild(button);
				buttons.add(button);

				activator.setAction(new ButtonAction(){
					@Override
					public void act(Editor subject) {
						builder1 = createSquare(Hub.map.getIntXLow(usq.getX()),Hub.map.getIntYLow(usq.getY()+usq.getHeight()),colour,colour2,action1==4?4:3,action2==4?4:3,action3,false);
						usq.addDependant(builder1);
						builder1.onAddToDrawable();
					}
				});
				activator.setX(button.getX()+0.025f);
				activator.setY(button.getY());
				activator.adjust(0.025f, 0.025f);
				usq.addChild(activator);
				buttons.add(activator);
			}
		}
	}
	private void addOnCreateButtonToSquare(final Square ocs){
		List<SquareAction> actions = ocs.getActions();
		for(SquareAction temp:actions){
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
								ocs.getX(),ocs.getY(),ocs.getWidth(),ocs.getHeight()));
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
			square.displayFor(colour);
		}
	}

}
