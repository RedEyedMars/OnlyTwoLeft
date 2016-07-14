package game.menu;

import editor.Editor;
import editor.MapEditor;
import editor.OnCreateSquareEditor;
import game.Game;
import gui.Gui;
import gui.graphics.GraphicEntity;
import gui.graphics.GraphicText;
import gui.graphics.GraphicView;
import gui.inputs.MotionEvent;
import main.Hub;
import main.Main;

public class MainMenu extends GraphicView{

	public MainMenu() {
		super();
		addChild(new GraphicEntity("title"));
		GraphicEntity button = new GraphicEntity("blank"){
			@Override
			public void performOnClick(MotionEvent e){
				startStory();
			}
		};
		button.adjust(1f,0.15f);
		button.setY(0.51f);
		button.setVisible(false);
		addChild(button);
		
		button = new GraphicEntity("blank"){
			@Override
			public void performOnClick(MotionEvent e){
				startEndless();
			}
		};
		button.adjust(1f,0.15f);
		button.setY(0.36f);
		button.setVisible(false);
		addChild(button);
		
		button = new GraphicEntity("blank"){
			@Override
			public void performOnClick(MotionEvent e){
				startPvp();
			}
		};
		button.adjust(1f,0.15f);
		button.setY(0.2f);
		button.setVisible(false);
		addChild(button);
		
		button = new GraphicEntity("blank"){
			@Override
			public void performOnClick(MotionEvent e){
				startHighscores();
			}
		};
		button.adjust(1f,0.15f);
		button.setY(0.04f);
		button.setVisible(false);
		addChild(button);
		
	
	}
	
	public void startStory(){
		Gui.setView(new OnCreateSquareEditor(null, 0f, 0f, 1f, 1f));
	}
	
	public void startEndless(){
		Main.loadMap();
		Gui.setView(new Game());
	}
	
	public void startPvp(){
		Gui.setView(new MapEditor());
	}
	
	public void startHighscores(){
	}
}
