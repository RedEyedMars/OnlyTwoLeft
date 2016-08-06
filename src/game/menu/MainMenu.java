package game.menu;

import java.util.ArrayList;
import java.util.List;

import duo.client.Client;
import editor.Editor;
import editor.MapEditor;
import editor.OnCreateSquareEditor;
import game.Game;
import game.environment.Square;
import gui.Gui;
import gui.graphics.GraphicEntity;
import gui.graphics.GraphicText;
import gui.graphics.GraphicView;
import gui.inputs.MotionEvent;
import main.Hub;
import main.Main;

public class MainMenu extends Menu{

	public MainMenu() {
		this(new ArrayList<Square>());
	}
	public MainMenu(List<Square> squares) {
		super();
		if(Client.isConnected()){
			Client.endConnection();
		}
		GraphicEntity button = new MenuButton("Solo"){
			@Override
			public void performOnRelease(MotionEvent e){
				solo();
			}
		};
		button.setX(0.2f);
		button.setY(0.51f);
		addChild(button);
		
		button = new MenuButton("Duo"){
			@Override
			public void performOnRelease(MotionEvent e){
				duo();
			}
		};
		button.setX(0.2f);
		button.setY(0.35f);
		addChild(button);
		
		button = new MenuButton("Editors"){
			@Override
			public void performOnRelease(MotionEvent e){
				editor();
			}
		};
		button.setX(0.2f);
		button.setY(0.19f);
		addChild(button);		
		this.squares = squares;
		for(Square square:squares){
			addChild(square);
		}
	}
	
	public void duo(){
		Gui.setView(new DuoMenu(squares));
	}
	
	public void solo(){
		if(Main.loadMap()){
			Gui.setView(new Game(true));
		}
	}
	
	public void editor(){
		Gui.setView(new EditorMenu(squares));
	}
	
	public void startHighscores(){
	}

	
}
