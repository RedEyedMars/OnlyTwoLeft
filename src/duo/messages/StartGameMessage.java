package duo.messages;

import java.io.File;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import duo.Handler;
import game.Game;
import game.environment.Square;
import game.menu.JoinMenu;
import gui.Gui;
import main.Hub;
import main.Main;
import storage.Storage;

public class StartGameMessage extends Message {
	private static final long serialVersionUID = -4911635467561953110L;

	private long seed;
	private boolean colour;

	public StartGameMessage(boolean colour){
		seed = Main.seed;
		this.colour = colour;
	}
	
	@Override
	public void act(Handler handler) {
		Main.randomizer = new Random(seed);
		handler.getMenu().startGame(colour);
	}

}
