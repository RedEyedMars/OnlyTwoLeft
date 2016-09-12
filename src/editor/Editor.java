package editor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import editor.field.BooleanOnClickFieldComponent;
import editor.field.FieldEditor;
import editor.field.FloatFieldComponent;
import editor.field.OnClickFieldComponent;
import editor.field.TextFieldComponent;
import editor.program.ProgramSquareEditor;
import game.Hero;
import game.environment.Square;
import game.environment.SquareAction;
import game.environment.oncreate.OnCreateAction;
import game.environment.oncreate.OnCreateSquare;
import game.environment.onstep.OnStepAction;
import game.environment.program.ProgrammableSquare;
import game.environment.update.UpdatableSquare;
import game.environment.update.UpdateAction;
import gui.Gui;
import gui.graphics.GraphicEntity;
import gui.graphics.GraphicView;
import gui.inputs.MotionEvent;
import gui.inputs.MouseListener;
import main.Hub;

public class Editor extends GraphicView {
	
	public static final int MODE_WAIT_FOR_RELEASE = -1;
	public static final int MODE_NEUTRAL = 0;
	public static final int MODE_MAKE_SQUARE = 2;
	protected int visibleTo=Hero.BOTH_INT;
	protected int mode=MODE_WAIT_FOR_RELEASE;
	protected int shape = 1;
	protected int blackColour = 0;
	protected int whiteColour = 0;
	protected int blackAction = 0;
	protected int whiteAction = 0;

	protected List<Button> shapeMenu = new ArrayList<Button>();
	protected List<Button> colourMenu = new ArrayList<Button>();
	protected List<Button> colour2Menu = new ArrayList<Button>();
	protected List<Button> actionMenu = new ArrayList<Button>();
	protected List<Button> actionMenu2 = new ArrayList<Button>();
	protected List<Button> updateActionMenu = new ArrayList<Button>();
	protected List<Button> specialActionMenu = new ArrayList<Button>();
	private List<Integer> previousActions = new ArrayList<Integer>();

	protected GraphicEntity visibleToShower = new GraphicEntity("circles",1);

	protected List<GraphicEntity> buttons = new ArrayList<GraphicEntity>();


	private FieldEditor<UpdateAction> updatableSquareDataField;

	protected List<Square> squares;
	protected Square hoveringOnSquare = null;
	protected Square builder1;
	protected Square mostRecentlyRemovedSquare;

	protected GraphicEntity granityShower = new GraphicEntity("editor_magnifier",1);
	public Editor(){
		super();
		mode = MODE_NEUTRAL;
	}

	protected void setupButtons(){
		shapeMenu.clear();
		colourMenu.clear();
		colour2Menu.clear();
		actionMenu.clear();
		actionMenu2.clear();
		updateActionMenu.clear();
		specialActionMenu.clear();
		for(int i=0;i<7;++i){
			final int id = i;
			Button button = new Button("editor_shape_icons",i,new ButtonAction(){
				@Override
				public void act(Object subject) {
					shapeMenu.get(shape).setSelected(false);
					shapeMenu.get(id).setSelected(true);
					shape=id;
				}
			});
			button.setX(0.92f);
			button.setY(0.03f+i*0.05f);
			button.adjust(0.05f,0.05f);
			shapeMenu.add(button);
			buttons.add(button);
			addChild(button);
		}
		for(int i=-1;i<16;++i){
			final int x = i;
			Button button = new Button("squares",i,new ButtonAction(){
				private int id;
				{
					id = x;
				}
				@Override
				public void act(Object subject) {
					colourMenu.get(blackColour+1).setSelected(false);
					colourMenu.get(id+1).setSelected(true);
					blackColour=id;

					colour2Menu.get(whiteColour+1).setSelected(false);
					colour2Menu.get(id+1).setSelected(true);
					whiteColour=id;
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
			final int id = i;
			Button button = new Button("squares",i,new ButtonAction(){
				@Override
				public void act(Object subject) {
					colour2Menu.get(whiteColour+1).setSelected(false);
					colour2Menu.get(id+1).setSelected(true);
					whiteColour=id;
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
			final int id = i;
			Button button = new Button("editor_icons",i,new ButtonAction(){
				@Override
				public void act(Object subject) {
					for(int i=0;i<specialActionMenu.size();++i){
						if(specialActionMenu.get(i).isSelected()){
							specialActionMenu.get(i).performOnRelease(null);
						}
					}
					actionMenu2.get(whiteAction+1).setSelected(false);
					actionMenu2.get(id+1).setSelected(true);
					actionMenu.get(blackAction+1).setSelected(false);
					actionMenu.get(id+1).setSelected(true);
					blackAction=id;
					whiteAction=id;
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
			final int id = i;
			Button button = new Button("editor_icons",i,new ButtonAction(){
				@Override
				public void act(Object subject) {
					for(int i=0;i<specialActionMenu.size();++i){
						if(specialActionMenu.get(i).isSelected()){
							specialActionMenu.get(i).performOnRelease(null);
						}
					}
					actionMenu2.get(whiteAction+1).setSelected(false);
					actionMenu2.get(id+1).setSelected(true);
					whiteAction=id;
				}

			});
			button.setX(0.08f+i*0.05f);
			button.setY(0.2f);
			button.adjust(0.05f,0.05f);
			actionMenu2.add(button);
			addChild(button);
			buttons.add(button);
		}
		for(int i=0;i<3;++i){
			final int index = i;
			Button button = new Button("editor_update_icons",i,new ButtonAction(){public void act(Object subject) {}}){
				{
					this.listenToRelease = true;
				}
				@Override
				public void performOnRelease(MotionEvent event) {
					for(int i=0;i<specialActionMenu.size();++i){
						if(specialActionMenu.get(i).isSelected()){
							specialActionMenu.get(i).performOnRelease(null);
						}
					}
					updateActionMenu.get(index).setSelected(!updateActionMenu.get(index).isSelected());

				}
			};
			button.setX(0.08f+i*0.05f);
			button.setY(0.25f);
			button.adjust(0.05f,0.05f);
			updateActionMenu.add(button);
			buttons.add(button);
			addChild(button);
		}
		for(int i=0;i<2;++i){
			final int id = i;
			Button button = new Button("editor_special_icons",i,new ButtonAction(){public void act(Object subject) {}}){
				{
					this.listenToRelease = true;
				}			
				@Override
				public void performOnRelease(MotionEvent e) {
					if(!specialActionMenu.get(id).isSelected()){
						boolean specialIsSelected = false;
						for(int i=0;i<specialActionMenu.size();++i){
							if(specialActionMenu.get(i).isSelected()){
								specialIsSelected = true;
								break;
							}
						}
						if(!specialIsSelected){
							previousActions.clear();
							previousActions.add(blackAction);
							previousActions.add(whiteAction);
							actionMenu.get(blackAction+1).setSelected(false);
							actionMenu2.get(whiteAction+1).setSelected(false);
							for(int i=0;i<updateActionMenu.size();++i){
								if(updateActionMenu.get(i).isSelected()){
									updateActionMenu.get(i).setSelected(false);
									previousActions.add(i);
								}
							}

							blackAction=-1;
							whiteAction=-1;
						}
						for(int i=0;i<specialActionMenu.size();++i){
							specialActionMenu.get(i).setSelected(false);
						}
						specialActionMenu.get(id).setSelected(true);
					}
					else {
						specialActionMenu.get(id).setSelected(false);
						blackAction=previousActions.get(0);
						whiteAction=previousActions.get(1);
						for(int i=2;i<previousActions.size();++i){
							updateActionMenu.get(previousActions.get(i)).setSelected(true);
						}						
						actionMenu.get(blackAction+1).setSelected(true);
						actionMenu2.get(whiteAction+1).setSelected(true);
					}
				}
			};
			button.setX(0.08f+i*0.05f);
			button.setY(0.3f);
			button.adjust(0.05f,0.05f);
			buttons.add(button);
			addChild(button);
			button.setSelected(false);
			specialActionMenu.add(button);
		}

		shapeMenu.get(shape).setSelected(true);
		colourMenu.get(blackColour+1).setSelected(true);
		colour2Menu.get(blackColour+1).setSelected(true);
		actionMenu.get(blackAction+1).setSelected(true);
		actionMenu2.get(whiteAction+1).setSelected(true);

		visibleToShower.setX(0.95f);
		visibleToShower.setY(0.95f);
		visibleToShower.adjust(0.05f, 0.05f);
		visibleToShower.setFrame(3);
		addChild(visibleToShower);		

		granityShower.adjust(0.04f, 0.04f);
		granityShower.setX(0.86f);
		granityShower.setY(0.96f);
		addChild(granityShower);
		granityShower.setFrame(0);



		FloatFieldComponent<UpdateAction> updatableSquareXField = new FloatFieldComponent<UpdateAction>("impact"){
			@Override
			public void act(Float subject) {
				target.setX(subject);
			}

			@Override
			public void updateWith(UpdateAction subject) {
				changeTextOnLine(""+subject.getFloat(0), 0);
			}
		};
		;
		FloatFieldComponent<UpdateAction> updatableSquareYField = new FloatFieldComponent<UpdateAction>("impact"){
			@Override
			public void act(Float subject) {
				target.setY(subject);				
			}

			@Override
			public void updateWith(UpdateAction subject) {
				changeTextOnLine(""+subject.getFloat(1), 0);
			}
		};;
		
		FloatFieldComponent<UpdateAction> updatableSquareLimitField = new FloatFieldComponent<UpdateAction>("impact"){
			@Override
			public void act(Float subject) {
				target.setLimit(subject);					
			}

			@Override
			public void updateWith(UpdateAction subject) {				
				changeTextOnLine(""+subject.getFloat(2), 0);
			}
		};
		FloatFieldComponent<UpdateAction> updatableSquareLimiterPercentField = new FloatFieldComponent<UpdateAction>("impact"){
			@Override
			public void act(Float subject) {
				target.setLimiterStartPercent(subject);
			}
			@Override
			public void updateWith(UpdateAction subject) {
				changeTextOnLine(""+subject.getFloat(3), 0);
			}
		};
		;
		BooleanOnClickFieldComponent<UpdateAction> updatableSquareDefaultStateIndicator = new BooleanOnClickFieldComponent<UpdateAction>("editor_icons",true,3){
			@Override
			public void act(Boolean subject) {
				target.setDefaultState(subject);
			}

			@Override
			public void updateWith(UpdateAction subject) {
				setFrame(subject.getDefaultState()?3:4);
			}
		};

		OnClickFieldComponent<UpdateAction> updatableSquareLimiterIndicator = new OnClickFieldComponent<UpdateAction>("editor_update_limiter_icons",-1,3){			

			@Override
			public void act(Integer subject) {
				target.setLimiter(subject);
			}

			@Override
			public void updateWith(UpdateAction subject) {
				setFrame(subject.getLimiter());
			}
		};

		updatableSquareDataField = new FieldEditor<UpdateAction>("X:\nY:\nLimit:\nStart %:",
				new TextFieldComponent[]{
						updatableSquareXField,
						updatableSquareYField,
						updatableSquareLimitField,
						updatableSquareLimiterPercentField
				},
				new OnClickFieldComponent[]{
						updatableSquareDefaultStateIndicator,
						updatableSquareLimiterIndicator
				});
		addChild(updatableSquareDataField);
		updatableSquareDataField.adjust(0.3f, 0.13f);
		updatableSquareDataField.setVisible(false);


	}

	protected boolean handleButtons(MotionEvent e){
		for(GraphicEntity button:buttons){
			if(button.isVisible()&&button.isWithin(e.getX(), e.getY())){
				button.performOnClick(e);
				return true;
			}
		}
		return false;
	}


	@Override
	public boolean onClick(MotionEvent e){
		if(e.getButton()==MotionEvent.MOUSE_LEFT){
			if(e.getAction()==MotionEvent.ACTION_DOWN){
				if(mode==MODE_NEUTRAL){
					if(handleButtons(e))return true;
					if(shape>0){
						int x = Hub.map.getIntX(e.getX());
						int y = Hub.map.getIntY(e.getY());
						if(granityShower.textureIndex()==0){
							x = (int) (x+2.5f);
							y = (int) (y+2.5f);
							x-=x%5;
							y-=y%5;
						}
						List<Integer> updateAction = new ArrayList<Integer>();
						for(int i=0;i<updateActionMenu.size();++i){
							if(updateActionMenu.get(i).isSelected()){
								updateAction.add(i);
							}
						}
						builder1 = createSquare(x,y,shape-1,blackColour,whiteColour,blackAction,whiteAction,updateAction,specialActionMenu.get(0).isSelected(),specialActionMenu.get(1).isSelected());				
						builder1.adjust(0.05f, 0.05f);
						addChild(builder1);
						builder1.onAddToDrawable();
						squares.add(builder1);
					}
					else {//paint
						for(int i=squares.size()-1;i>=0;--i){
							if(squares.get(i).isWithin(e.getX(), e.getY())){
								squares.get(i).changeColour(blackColour,whiteColour);
								squares.get(i).displayFor(visibleTo);
								return true;
							}
							else if(squares.get(i) instanceof UpdatableSquare){
								List<Square> depends = ((UpdatableSquare)squares.get(i)).getDependants();
								for(int j=depends.size()-1;j>=0;--j){
									if(depends.get(j).isWithin(e.getX(), e.getY())){
										squares.get(i).changeColour(blackColour,whiteColour);
										squares.get(i).displayFor(visibleTo);
										return true;
									}
								}
							}
						}
					}
				}
				else if(mode==MODE_MAKE_SQUARE){

					int x = Hub.map.getIntX(e.getX());
					int y = Hub.map.getIntY(e.getY());
					if(granityShower.textureIndex()==0){
						x = (int) (x+2.5f);
						y = (int) (y+2.5f);
						x-=x%5;
						y-=y%5;
					}
					if(Hub.map.getRealX(x)-builder1.getX()!=0f&&Hub.map.getRealY(y)-builder1.getY()!=0f){

						builder1.adjust(Hub.map.getRealX(x)-builder1.getX(), Hub.map.getRealY(y)-builder1.getY());
						//removeChild(builder1);
						builder1.onRemoveFromDrawable();
						//addChild(builder1);
						builder1.onAddToDrawable();
					}
				}
			}
			else if(e.getAction()==MotionEvent.ACTION_UP){
				if(mode==MODE_WAIT_FOR_RELEASE){
					mode=MODE_NEUTRAL;
				}
				else if(mode==MODE_MAKE_SQUARE){
					mode=MODE_NEUTRAL;

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
				else {
					for(GraphicEntity child:buttons){
						if(child.isVisible()&&child.isWithin(e.getX(), e.getY())){
							child.performOnRelease(e);
						}
					}
				}
			}
		}
		else if(e.getButton()==MotionEvent.MOUSE_RIGHT){
			if(e.getAction()==MotionEvent.ACTION_UP){
				for(int i=squares.size()-1;i>=0;--i){
					if(squares.get(i).isWithin(e.getX(), e.getY())){
						mostRecentlyRemovedSquare = squares.remove(i);
						removeButtonsFromSquare(mostRecentlyRemovedSquare);
						removeChild(mostRecentlyRemovedSquare);
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

	@Override
	public boolean onHover(MotionEvent e){
		if(mode==2)return false;
		for(int i=squares.size()-1;i>=0;--i){
			if(squares.get(i).isWithin(e.getX(), e.getY())){
				if(hoveringOnSquare!=null){
					hoveringOnSquare.hideChildren();
				}
				squares.get(i).showChildren();
				hoveringOnSquare = squares.get(i);
				break;
			}
		}
		return true;
	}

	protected Square createSquare(int x, int y,int shape, int colour, int colour2, int a1, int a2, List<Integer> ua, boolean oc, boolean pc){
		mode=2;
		mostRecentlyRemovedSquare = null;
		List<Float> floats = new ArrayList<Float>();
		for(int i=0;i<ua.size();++i){			
			floats.add(0f);
			floats.add(0f);
		}
		if(pc){			
			floats.add(0f);
			floats.add(0f);			
		}
		Iterator<Integer> ints = Square.makeInts(a1,a2,ua,oc,pc,shape,colour,colour2,x,y,1,1);
		Square created = Square.create(ints, floats.iterator());
		if(hoveringOnSquare!=null){
			hoveringOnSquare.hideChildren();
			hoveringOnSquare=created;
		}
		return created;
	}

	protected void removeButtonsFromSquare(Square square){
		for(GraphicEntity e:square.getChildren()){
			if(e instanceof Button){
				buttons.remove((Button)e);
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
			if(square instanceof ProgrammableSquare){
				addProgramEditorButtonToSquare((ProgrammableSquare)square);
			}
			else {
				addUpdateButtonToSquare((UpdatableSquare) square);
			}

		}
		else if(square instanceof OnCreateSquare){
			addOnCreateButtonToSquare((OnCreateSquare)square);
		}
		if(square!=hoveringOnSquare){
			square.hideChildren();
		}
	}
	private void addAdjustPositionButtonToSquare(final Square square) {
		final Button button = new Button(null);

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
			public void act(Object subject) {
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
		final Button button = new Button(null);
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
				if(Hub.map.getRealX(x)-square.getX()!=0f&&Hub.map.getRealY(y)-square.getY()!=0f){
					System.out.println((Hub.map.getRealX(x)-square.getX())+","+(Hub.map.getRealY(y)-square.getY()));
					square.adjust(Hub.map.getRealX(x)-square.getX(), Hub.map.getRealY(y)-square.getY());
					button.setX(square.getX()+square.getWidth()-0.015f);
					button.setY(square.getY()+square.getHeight()-0.015f);
				}
				return false;
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

		button.setX(square.getX()+square.getWidth()-0.015f);
		button.setY(square.getY()+square.getHeight()-0.015f);
		button.adjust(0.015f, 0.015f);
		buttons.add(button);
		square.addChild(button);
	}
	@SuppressWarnings("rawtypes")
	private void addActionIconToSquare(Square fsq, float x, float y,float size){
		GraphicEntity e = null;
		List<SquareAction> actions = fsq.getActions();
		for(SquareAction<?, ?> action:actions){
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
				if(action.getIndex()!=-2){
					e.setFrame(action.getIndex());
				}
				else {
					//show one for each of the combine's inner actions
				}
			}
			else if(action instanceof OnCreateAction){
				e = new GraphicEntity("editor_special_icons");
				e.setFrame(0);
			}
			e.setX(x);
			e.setY(y);
			e.adjust(size, size);
			x-=size;
			fsq.addChild(e);
		}
	}
	@SuppressWarnings("rawtypes")
	private void addUpdateButtonToSquare(final UpdatableSquare usq){
		List<SquareAction> actions = usq.getActions();
		for(SquareAction<?, ?> temp:actions){
			if(temp instanceof UpdateAction){
				final UpdateAction action= ((UpdateAction)temp);
				int xOffset = 0;
				for(UpdateAction innerAction:action){
					final UpdateAction myAction = innerAction;
					Button button = new Button("editor_update_icons",innerAction.getIndex(),new ButtonAction(){
						@Override
						public void act(Object subject) {
							updatableSquareDataField.setX(usq.getX()+0.015f);
							updatableSquareDataField.setY(usq.getY()+0.015f);
							updatableSquareDataField.updateWith(myAction);
							updatableSquareDataField.setVisible(true);							
							Gui.giveOnClick(updatableSquareDataField);						
							Gui.giveOnType(updatableSquareDataField.getDefaultKeyBoardListener());
						}
					});
					button.setX(usq.getX()+0.015f+0.025f*(xOffset++));
					button.setY(usq.getY()+0.015f);
					button.adjust(0.025f, 0.025f);
					usq.addChild(button);
					buttons.add(button);
				}		

				final Button activator = new Button("editor_icons",3,null);
				activator.setAction(new ButtonAction(){
					@Override
					public void act(Object subject) {
						List<Integer> updateAction = new ArrayList<Integer>();
						for(int i=0;i<updateActionMenu.size();++i){
							if(updateActionMenu.get(i).isSelected()){
								updateAction.add(i);
							}
						}
						int a1=3,a2=3;
						if(blackAction==4||blackAction==-1){
							a1=blackAction;
						}
						if(whiteAction==4||whiteAction==-1){
							a2=whiteAction;
						}
						builder1 = createSquare(Hub.map.getIntX(usq.getX()),Hub.map.getIntY(usq.getY()+usq.getHeight()),shape==0?1:(shape-1),blackColour,whiteColour,a1,a2,updateAction,false,false);
						usq.addDependant(builder1);
						builder1.onAddToDrawable();
					}
				});

				activator.setX(usq.getX()+0.015f+0.025f*xOffset);
				activator.setY(usq.getY()+0.015f);
				activator.adjust(0.025f, 0.025f);
				usq.addChild(activator);
				buttons.add(activator);
			}
		}
	}
	@SuppressWarnings("rawtypes")
	private void addOnCreateButtonToSquare(final OnCreateSquare ocs){
		final MapEditor editor = (MapEditor)this;
		final Button button = new Button("editor_special_icons",0,new ButtonAction(){
			@Override
			public void act(Object subject) {
				squares.remove(ocs);
				if(getChildren().contains(ocs)){
					removeChild(ocs);						
				}
				Gui.setView(new OnCreateSquareEditor(
						editor,
						ocs.getX(),ocs.getY(),ocs.getWidth(),ocs.getHeight()));
			}
		});
		button.setX(ocs.getX()+0.015f);
		button.setY(ocs.getY()+0.015f);
		button.adjust(0.025f, 0.025f);
		ocs.addChild(button);
		buttons.add(button);

	}
	private void addProgramEditorButtonToSquare(final ProgrammableSquare ps){
		final MapEditor editor = (MapEditor)this;
		final Button button = new Button("editor_special_icons",1,new ButtonAction(){
			@Override
			public void act(Object subject) {
				Gui.setView(new ProgramSquareEditor(editor,ps));
			}
		});
		button.setX(ps.getX()+0.015f);
		button.setY(ps.getY()+0.015f);
		button.adjust(0.025f, 0.025f);
		ps.addChild(button);
		buttons.add(button);
	}


	public void setVisibleSquares(int colour){
		for(Square square:squares){
			square.displayFor(colour);
		}
	}

}
