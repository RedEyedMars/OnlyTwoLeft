package com.rem.otl.core.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Stack;

import com.rem.otl.core.duo.client.Client;
import com.rem.otl.core.duo.messages.ActionMessage;
import com.rem.otl.core.duo.messages.BlankMessage;
import com.rem.otl.core.duo.messages.LoadMapMessage;
import com.rem.otl.core.game.Action;
import com.rem.otl.core.game.hero.Hero;
import com.rem.otl.core.gui.Gui;
import com.rem.otl.core.gui.graphics.GraphicRenderer;
import com.rem.otl.core.gui.graphics.GraphicView;
import com.rem.otl.core.gui.inputs.EventHandler;
import com.rem.otl.core.gui.inputs.HoverEvent;
import com.rem.otl.core.gui.inputs.KeyBoardEvent;
import com.rem.otl.core.gui.inputs.KeyBoardListener;
import com.rem.otl.core.gui.inputs.ClickEvent;
import com.rem.otl.core.gui.inputs.MouseListener;
import com.rem.otl.core.gui.music.MusicPlayer;
import com.rem.otl.core.storage.Storage;
public class Hub {
	public static final String RESTART_STRING = "\n";
	public static final int TOP_LAYER = 2;
	public static final int MID_LAYER = 1;
	public static final int BOT_LAYER = 0;

	public static ICreator creator;
	public static EventHandler handler;
	public static IFileManager manager;
	public static GraphicView view;
	public static Gui gui;
	public static ILog log;
	public static GraphicRenderer renderer;
	public static MusicPlayer music;
	public static float width;
	public static float height;	

	public static long seed = new Random().nextLong();
	public static Random randomizer = new Random(seed);

	public static String defaultMapFile = "";
	public static String defaultPlayerName = "";
	public static MouseListener genericMouseListener = new MouseListener(){
		@Override
		public boolean onClick(ClickEvent event) {	return false; }
		@Override
		public boolean onHover(HoverEvent event) { return false; }
		@Override
		public void onMouseScroll(int distance) {}
	};
	public static KeyBoardListener genericKeyBoardListener = new KeyBoardListener(){
		@Override
		public boolean continuousKeyboard() {
			return false;
		}

		@Override
		public void onType(KeyBoardEvent event) {
			
		}
	};
	public static com.rem.otl.core.game.environment.Map map;
	public static void loadMapFromFileName(String mapFileName) {
		if(mapFileName!=null){
			if(Client.isConnected()){
				Client.sendMapMessage(mapFileName, new BlankMessage());
			}
			else {
				Storage.loadMap(Hub.manager.createInputStream(Hub.map.getFileName()));
			}
		}
	}
	public static void restartMap(Action<Object> onReturn) {
		if(!Client.isConnected()||Hub.map.getFileName()!=null) {
			Storage.loadMap(Hub.manager.createInputStream(Hub.map.getFileName()));
			onReturn.act(null);
		}
		else if(Client.isConnected()&&Hub.map.getFileName()==null){
			if(onReturn!=null&&Client.isConnected()){
				Client.pass(new LoadMapMessage(Hub.RESTART_STRING, new ActionMessage(onReturn)));
			}
			else {
				throw new RuntimeException("Tried to Restart the Map without hosting the Map.");
			}
		}
		else {
			throw new RuntimeException("Tried to Restart the Map without hosting the Map.");
		}
	}
	private static Hero black;
	private static Hero white;
	private static Hero[] both;
	public static void setHeroes(Hero black, Hero white){
		Hub.black = black;
		Hub.white = white;
		Hub.both = new Hero[]{black,white};
	}
	public static Hero getHero(boolean colourToControl) {
		return colourToControl?black:white;
	}
	public static Hero[] getHeroAsArray(boolean colourToControl) {
		return new Hero[]{colourToControl?black:white};
	}
	public static Hero[] getBothHeroes() {
		return both;
	}
	public static void load(Setupable main, boolean loadMusic, boolean loadDefaults, boolean run){

		log = creator.createLog();
		gui = creator.createGui(main);
		try {
			gui.setup();
			manager = creator.createFileManager(main);
			handler = new EventHandler();
			handler.giveOnClick(Hub.genericMouseListener);
			handler.giveOnType(Hub.genericKeyBoardListener);
			renderer = creator.createGraphicRenderer(main);
			if(loadMusic){
				music = creator.createMusic();
			}
			if(loadDefaults){
				loadDefaults();
			}
			if(run){
				gui.setView(main.getFirstView());
				gui.run();
			}

		}
		catch (Exception e) {
			log.err("GLApp.run(): " + e);
			e.printStackTrace(System.out);
		}
		if(run){
			// prepare to exit
			gui.cleanup();
			System.exit(0);
		}
	}
	public static void loadDefaults() {
		File file = new File("data"+File.separator+"meta.data");

		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = reader.readLine();
			int variable = -1;
			while(line!=null){
				if(line.startsWith("\t")){
					String var = line.substring(1);
					if(variable==0){
						if(Boolean.parseBoolean(var)){
							Hub.music.pause();
						}
					}
					else if(variable==1){
						Hub.music.adjustVolume(Float.parseFloat(var));
					}
					else if(variable==2){
						Hub.defaultPlayerName = var;
					}
					else if(variable==3){
						Hub.defaultMapFile = var;
					}
				}
				else {
					if("muted".equals(line)){
						variable = 0;
					}
					else if("volume".equals(line)){
						variable = 1;
					}
					else if("name".equals(line)){
						variable = 2;
					}
					else if("map".equals(line)){
						variable = 3;
					}
				}
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	public static void saveDefaults() {
		File file = new File("data"+File.separator+"meta.data");
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write("muted\n\t");
			if(music==null){
				writer.write("false\n");
			}
			else {
				writer.write(!Hub.music.isPlaying()+"\n");
			}
			writer.write("volume\n\t");
			if(music==null){
				writer.write("0.8\n");
			}
			else {
				writer.write(Hub.music.getVolume()+"\n");
			}
			writer.write("name\n\t");
			writer.write(defaultPlayerName);
			writer.write("\nmap\n\t");
			writer.write(defaultMapFile);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static long getNewRandomSeed() {
		Hub.seed = new Random().nextLong();
		return Hub.seed;
	}
}
