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

public class Editor extends GraphicView {

	protected static final float gridSize = 20f;

	protected int visibleTo=0;
	protected int mode=-1;
	protected int colour = 0;
	protected int colour2 = 0;
	protected int action1 = 1;
	protected int action2 = 1;
	protected int action3 = 0;

	protected List<Button> colourMenu = new ArrayList<Button>();
	protected List<Button> colour2Menu = new ArrayList<Button>();
	protected List<Button> actionMenu = new ArrayList<Button>();
	protected List<Button> actionMenu2 = new ArrayList<Button>();
	protected List<Button> updateActionMenu = new ArrayList<Button>();


	protected GraphicEntity visibleToShower = new GraphicEntity("circles",1);

	protected List<GraphicEntity> buttons = new ArrayList<GraphicEntity>();
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
		for(int i=0;i<6;++i){
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
	
	



}
