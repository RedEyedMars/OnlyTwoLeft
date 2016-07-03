package game.menu;

import game.Game;
import gui.Gui;
import gui.graphics.GraphicEntity;
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
		//Hub.scenes.get(Hub.sceneIndex).act(null);
	}
	
	public void startEndless(){
		Main.loadMap();
		Gui.setView(new Game());
	}
	
	public void startPvp(){
		//Gui.setView(new PvPMode());
	}
	
	public void startHighscores(){
		//Gui.setView(new HighscoreMenu());
	}
}
