package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import duo.client.Client;
import duo.messages.ActionMessage;
import duo.messages.BlankMessage;
import duo.messages.LoadMapMessage;
import game.Action;
import game.menu.StoryAction;
import game.menu.StoryScene;
import gui.Gui;
import gui.graphics.GraphicElement;
import gui.graphics.GraphicEntity;
import gui.graphics.GraphicRenderer;
import gui.graphics.GraphicView;
import gui.inputs.KeyBoardListener;
import gui.inputs.MotionEvent;
import gui.inputs.MouseListener;
import storage.Storage;
public class Hub {
	public static final String RESTART_STRING = "\n";
	public static GraphicView currentView;
	public static GraphicRenderer renderer;
	public static float width;
	public static float height;
	public static List<GraphicElement> addLayer = new ArrayList<GraphicElement>();
	public static List<GraphicElement> removeLayer = new ArrayList<GraphicElement>();
	public static List<GraphicElement> drawBotLayer = new ArrayList<GraphicElement>();
	public static List<GraphicElement> drawTopLayer = new ArrayList<GraphicElement>();
	public static MouseListener genericMouseListener = new MouseListener(){
		@Override
		public boolean onClick(MotionEvent event) {
			return false;
		}
		@Override
		public boolean onHover(MotionEvent event) {
			return false;
		}
		@Override
		public void onMouseScroll(int distance) {			
		}
	};
	public static KeyBoardListener genericKeyBoardListener = new KeyBoardListener(){
		@Override
		public void keyCommand(boolean b,char c, int keycode) {			
		}

		@Override
		public boolean continuousKeyboard() {
			return false;
		}
	};
	public static List<Action> scenes = new ArrayList<Action>();
	public static int sceneIndex = 7;
	public static game.environment.Map map;
	public static void updateIfHigher(String key, Integer integer, String name, Map<String, Integer> map, Map<String, String> namesMap) {
		if(map.containsKey(key)&&map.get(key)>integer){
			return;
		}
		else if("$null".equals(name)){
			return;
		}
		else {
			map.put(key,integer);
			namesMap.put(key,name);
		}
	}
	public static Integer getValue(String key, Map<String, Integer> map) {
		return map.containsKey(key)?map.get(key):0;
	}
	public static String getString(String key, Map<String, String> map) {
		return map.containsKey(key)?map.get(key):"$null";
	}
	public static void loadMapFromFileName(String mapFileName) {
		if(mapFileName!=null){
			if(Client.isConnected()){
				Client.sendMapMessage(mapFileName, new BlankMessage());
			}
			else {
				Storage.loadMap(mapFileName);
			}
		}
	}
	public static void restartMap(Action<Object> onReturn) {
		if(Hub.map.getFileName()==null){
			if(onReturn!=null&&Client.isConnected()){
				Client.pass(new LoadMapMessage(Hub.RESTART_STRING, new ActionMessage(onReturn)));
			}
			else {
				throw new RuntimeException("Tried to Restart the Map without hosting the Map.");
			}
		}
		else {
			Storage.loadMap(Hub.map.getFileName());
			onReturn.act(null);
		}
	}
}
