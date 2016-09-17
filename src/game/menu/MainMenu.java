package game.menu;

import java.io.File;
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
import gui.graphics.GraphicLine;
import gui.graphics.GraphicText;
import gui.graphics.GraphicView;
import gui.inputs.MotionEvent;
import main.Hub;
import main.Main;
import storage.Storage;

public class MainMenu extends Menu{


	public MainMenu() {
		super();
		if(Client.isConnected()){
			Client.endConnectionToTheServer();
		}
		GraphicEntity button = new MenuButton("Solo"){
			@Override
			public void performOnRelease(MotionEvent e){
				solo();
			}
		};
		button.reposition(0.2f,0.51f);
		addChild(button);

		button = new MenuButton("Duo"){
			@Override
			public void performOnRelease(MotionEvent e){
				duo();
			}
		};
		button.reposition(0.2f,0.35f);
		addChild(button);

		button = new MenuButton("Editors"){
			@Override
			public void performOnRelease(MotionEvent e){
				editor();
			}
		};
		button.reposition(0.2f,0.19f);
		addChild(button);
	}

	public void duo(){
		Gui.setView(new DuoMenu());
	}

	public void solo(){
		File file = GetFileMenu.getFile(this,"maps");
		if(file!=null){
			Storage.loadMap(file.getAbsolutePath());
			Gui.setView(new Game(true,Main.getNewRandomSeed()));
		}
	}

	public void editor(){
		Gui.setView(new EditorMenu());
	}

	public void startHighscores(){
	}


}
