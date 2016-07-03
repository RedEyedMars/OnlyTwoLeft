package main;

import game.environment.Map;
import game.menu.MainMenu;
import game.menu.StoryScene;
import gui.Gui;
import gui.graphics.GraphicView;
import storage.Storage;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Gui gui = null;
		gui = new Gui();
	}

	public static void setup(){
		//load();
		MainMenu menu = new MainMenu();
		Gui.setView(menu);
	}
	
	public static void loadMap(){
		//StoryScene.setupScenes();
		//Storage.loadMap("./data/1.map");
		Hub.map = new Map();
		/*Hub.map.load(new Object[]{
				7,4,2,
				0,1,1,3,
				1f,0.45f,0.45f,0.1f,
				"grass","fire"
		});*/
		Storage.loadMap("data/mal.map");
	}

	public static void cleanup() {
		if(Hub.map.isMallible()){
			Storage.saveMap("data/mal.map", Hub.map);
		}
	}
}
