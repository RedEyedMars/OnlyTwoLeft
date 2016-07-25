package main;

import java.io.File;
import java.util.Random;

import duo.client.Client;
import game.environment.Map;
import game.menu.MainMenu;
import game.menu.StoryScene;
import gui.Gui;
import gui.graphics.GraphicView;
import storage.Storage;

public class Main {

	public static long seed = new Random().nextLong();
	public static Random randomizer = new Random(seed);

	static long time;
	public static void main(String[] args) {
		Gui gui = null;
		gui = new Gui();
	}

	public static void setup(){
		//load();
		MainMenu menu = new MainMenu();
		Gui.setView(menu);
	}
	
	public static boolean loadMap(){
		//StoryScene.setupScenes();
		//Storage.loadMap("./data/1.map");
		/*Hub.map.load(new Object[]{
				9,4,2,
				0,0,1,4,1,3,
				1f,0.45f,0.45f,0.1f,
				"void","hazard"
		});*/

		File file = Gui.userSave("maps");
		if(file!=null){
			Storage.loadMap(file.getAbsolutePath());
			return true;
		}
		else return false;
	}

	public static void cleanup() {
		if(Hub.map!=null&&Hub.map.isMallible()){
			//Storage.saveMap("data/mal.map", Hub.map);
		}
		Client.endConnection();
	}
}
