package editor;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFileChooser;

import game.Action;
import game.Hero;
import game.environment.FunctionalSquare;
import game.environment.Square;
import game.environment.SquareAction;
import game.environment.SquareIdentity;
import game.environment.UpdateAction;
import game.menu.MainMenu;
import gui.Gui;
import gui.graphics.GraphicEntity;
import gui.graphics.GraphicView;
import gui.inputs.KeyBoardListener;
import gui.inputs.MotionEvent;
import main.Hub;
import storage.Storage;

public class Editor extends GraphicView implements KeyBoardListener {

	private int visibleTo=0;
	private int mode=-1;
	private int colour = 0;
	private int action1 = 1;
	private int action2 = 1;
	private int action3 = 0;

	private GraphicEntity actionMenu = new GraphicEntity("blank",1);
	private GraphicEntity actionMenu2 = new GraphicEntity("blank",1);
	private GraphicEntity updateActionMenu = new GraphicEntity("blank",1);
	private Square colourShower;
	private GraphicEntity action1Shower = new GraphicEntity("editor_icons",1);
	private GraphicEntity action2Shower = new GraphicEntity("editor_icons",1);
	private GraphicEntity action3Shower = new GraphicEntity("editor_update_icons",1);
	private GraphicEntity visibleToShower = new GraphicEntity("circles",1);

	private List<GraphicEntity> buttons = new ArrayList<GraphicEntity>();

	private float viewX = 0f;
	private float viewY = 0f;
	private List<Square> squares = new ArrayList<Square>();
	private Square builder;
	private File saveTo = null;
	public Editor(){
		super();

		for(int i=0;i<8;++i){
			final int x = i;
			Button<Editor> button = new Button<Editor>("editor_icons","squares",i,this,new Action<Editor>(){
				private int id;
				{
					id = x;
				}
				@Override
				public void act(Editor subject) {
					colour=id;
					colourShower.setFrame(colour);
					subject.mode=1;
					actionMenu.setX(0.08f+id*0.12f);
					actionMenu.setVisible(true);
					updateActionMenu.setX(0.03f+id*0.12f);
					updateActionMenu.setVisible(true);
					actionMenu2.setVisible(false);
				}

			}){
				@Override
				public float offsetX(int i){
					return i==0?0.0225f:0f;
				}
				@Override
				public float offsetY(int i){
					return i==0?0.0225f:0f;
				}
			};
			button.setX(0.015f+i*0.12f);
			button.setY(0.015f);
			button.adjust(0.125f,0.125f,0.08f, 0.08f);
			addChild(button);
			buttons.add(button);
		}
		addChild(actionMenu);
		for(int i=0;i<6;++i){
			final int x = i;
			Button<Editor> button = new Button<Editor>("editor_icons",i+1,this,new Action<Editor>(){
				private int id;
				{
					id = x;
				}
				@Override
				public void act(Editor subject) {
					action1=id;
					action1Shower.setFrame(id+1);
					action2=id;
					action2Shower.setFrame(id+1);
					int i=0;
					for(GraphicEntity child:actionMenu2.getChildren()){
						child.setX(0.13f+i*0.05f+colour*0.12f);
						child.setY(0.14f+id*0.05f);
						++i;
					}
					actionMenu2.setVisible(true);
					i=0;
					for(GraphicEntity child:updateActionMenu.getChildren()){
						child.setX(0.03f+colour*0.12f);
						child.setY(0.14f+i*0.05f+id*0.05f);
						++i;
					}
				}
			});
			button.setX(0.05f);
			button.setY(0.14f+i*0.05f);
			button.adjust(0.05f,0.05f);
			actionMenu.addChild(button);
			buttons.add(button);
		}
		actionMenu.addChild(actionMenu2);
		for(int i=0;i<4;++i){
			final int x = i;
			Button<Editor> button = new Button<Editor>("editor_icons",i+1,this,new Action<Editor>(){
				private int id;
				{
					id = x;
				}
				@Override
				public void act(Editor subject) {
					action2=id;
					action2Shower.setFrame(id+1);
					int i=1;
					for(GraphicEntity child:updateActionMenu.getChildren()){
						child.setX(0.13f+id*0.05f+colour*0.12f);
						child.setY(0.14f+i*0.05f+action1*0.05f);
						++i;
					}
				}

			});
			button.adjust(0.05f,0.05f);
			actionMenu2.addChild(button);
			buttons.add(button);
		}
		actionMenu.setVisible(false);
		addChild(updateActionMenu);
		for(int i=0;i<2;++i){
			final int x = i;
			Button<Editor> button = new Button<Editor>("editor_update_icons",i+1,this,new Action<Editor>(){
				private int id;
				{
					id = x;
				}
				@Override
				public void act(Editor subject) {
					action3=id;
					action3Shower.setFrame(id+1);
				}
			});
			button.setX(0.03f);
			button.setY(0.14f+i*0.05f);
			button.adjust(0.05f,0.05f);
			updateActionMenu.addChild(button);
			buttons.add(button);
		}
		updateActionMenu.setVisible(false);

		colourShower = new SquareIdentity("safe").create(Arrays.asList(0, 0, 3).iterator(), Arrays.asList(0f,0.95f,0.05f).iterator());
		addChild(colourShower);
		action1Shower.setX(0.05f);
		action1Shower.setY(0.95f);
		action1Shower.adjust(0.05f, 0.05f);
		action1Shower.setFrame(2);
		addChild(action1Shower);
		action2Shower.setX(0.1f);
		action2Shower.setY(0.95f);
		action2Shower.adjust(0.05f, 0.05f);
		action2Shower.setFrame(2);
		addChild(action2Shower);
		action3Shower.setX(0.15f);
		action3Shower.setY(0.95f);
		action3Shower.adjust(0.05f, 0.05f);
		action3Shower.setFrame(1);
		addChild(action3Shower);
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
				addIconsToSquare(square);
				addChild(square);
				squares.add(square);
			}
		}
	}
	public void update(double seconds){
		if(saveTo==null){
			Gui.setView(new MainMenu());
		}
		super.update(seconds);
	}
	private static final float gridSize = 100f;
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
					String name = "";
					if(action1==0){
						name="void";
					}
					else name=SquareAction.actionNames.get(action1-1);
					if(action1!=action2){
						if(action2==0){
							name=name+"Xvoid";
						}
						else name=name+"X"+SquareAction.actionNames.get(action2-1);
					}
					if(action3>0){
						name=name+"X"+UpdateAction.actionNames.get(action3-1);
					}
					int x = (int) (e.getX()*gridSize);
					int y = (int) (e.getY()*gridSize);
					builder = new SquareIdentity(name).create(Arrays.asList(colour, visibleTo, 3).iterator(), Arrays.asList(((float)x)/gridSize,((float)y)/gridSize,0.05f).iterator());
					addIconsToSquare(builder);
					addChild(builder);
					builder.onAddToDrawable();
					squares.add(builder);
				}
				else if(mode==1){
					for(GraphicEntity child:buttons){
						if(child.isVisible()&&child.isWithin(e.getX(), e.getY())){
							child.performOnClick(e);
							return true;
						}
					}
				}
				else if(mode==2){
					int x = (int) (e.getX()*gridSize);
					int y = (int) (e.getY()*gridSize);
					builder.adjust(((float)x)/gridSize-builder.getX(), ((float)y)/gridSize-builder.getY());
				}
			}
			if(e.getAction()==MotionEvent.ACTION_UP){
				if(mode==-1){
					mode=0;
					actionMenu.setVisible(false);
					updateActionMenu.setVisible(false);
				}
				if(mode==1){
					mode=0;
					actionMenu.setVisible(false);
					updateActionMenu.setVisible(false);
				}
				if(mode==2){
					mode=0;
					actionMenu.setVisible(false);
					updateActionMenu.setVisible(false);
				}
			}
		}
		else if(e.getButton()==MotionEvent.MOUSE_RIGHT){
			if(e.getAction()==MotionEvent.ACTION_UP){
				for(int i=squares.size()-1;i>=0;--i){
					if(squares.get(i).isWithin(e.getX(), e.getY())){
						removeChild(squares.remove(i));						
						return true;
					}
				}
			}
		}
		return false;
	}
	private void addIconsToSquare(Square square) {
		String name = square.getIdentity().getName();
		if(name.contains("X")){
			String[] split = name.split("X");
			addActionIconToSquare(square,split[0],square.getX(),square.getY(),0.05f);
			addActionIconToSquare(square,split[1],square.getX()+0.05f,square.getY(),0.05f);
		}
		else {
			addActionIconToSquare(square,name,square.getX(),square.getY(),0.05f);
		}
		
	}
	private void addActionIconToSquare(Square fsq, String action, float x, float y,float size){
		GraphicEntity e = new GraphicEntity("editor_icons");
		if(!"void".equals(action)){
			for(int i=0;i<SquareAction.actions.size();++i){
				if(SquareAction.actionNames.get(i).equals(action)){					
					e.setFrame(i+2);
					break;
				}
			}
		}
		else {
			e.setFrame(1);
		}
		e.setX(x);
		e.setY(y);
		e.adjust(size, size);
		fsq.addChild(e);
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
		}
		else {
			if(keycode==17||keycode==200){//up
				moveView(0f,0.001f);
			}
			else if(keycode==30||keycode==203){//left
				moveView(-0.001f,0f);
			}
			else if(keycode==31||keycode==208){//down
				moveView(0f,-0.001f);
			}
			else if(keycode==32||keycode==205){//right
				moveView(0.001f,0f);
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
		game.environment.Map map = new game.environment.Map();
		for(Square square:squares){
			map.addSquare(square);
		}
		Storage.saveMap(saveTo.getAbsolutePath(), map);
	}
	private void moveView(float x, float y){
		this.viewX+=x;
		this.viewY+=y;
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
