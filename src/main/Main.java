package main;

import java.util.Random;

import duo.client.Client;
import game.menu.MainMenu;
import gui.Gui;

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

	public static void cleanup() {
		if(Hub.map!=null&&Hub.map.isMallible()){
			//Storage.saveMap("data/mal.map", Hub.map);
		}
		Client.endConnectionToTheServer();
	}

	public static long getNewRandomSeed() {
		Main.seed = new Random().nextLong();
		return Main.seed;
	}
}
