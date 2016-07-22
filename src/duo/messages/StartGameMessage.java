package duo.messages;

import java.io.File;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import game.Game;
import game.environment.Square;
import gui.Gui;
import main.Hub;
import main.Main;
import storage.Storage;

public class StartGameMessage extends Message {
	private static final long serialVersionUID = -4911635467561953110L;

	private List<Integer> ints;
	private List<Float> floats;
	private long seed;
	private boolean colour;

	public StartGameMessage(game.environment.Map map, boolean colour){
		ints = new ArrayList<Integer>();
		floats = new ArrayList<Float>();
		List<Object> probe = new ArrayList<Object>(){
			@Override
			public boolean add(Object obj){
				if(obj instanceof Integer){
					ints.add((Integer)obj);
				}
				else if(obj instanceof Float){
					floats.add((Float)obj);
				}
				return true;
			}
		};
		seed = Main.seed;
		this.colour = colour;
	}
	
	@Override
	public void act(Socket socket) {
		Main.randomizer = new Random(seed);
		Hub.map = new game.environment.Map();
		Iterator<Integer> intItr = ints.iterator();
		Iterator<Float> floatItr = floats.iterator();
		while(intItr.hasNext()){
			Hub.map.addSquare(Square.create(intItr, floatItr));
		}
		Gui.setView(new Game(colour));
	}

}
