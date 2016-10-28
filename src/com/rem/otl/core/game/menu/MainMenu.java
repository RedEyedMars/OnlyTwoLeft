package com.rem.otl.core.game.menu;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.rem.otl.core.duo.client.Client;
import com.rem.otl.core.editor.Editor;
import com.rem.otl.core.editor.MapEditor;
import com.rem.otl.core.editor.OnCreateSquareEditor;
import com.rem.otl.core.game.Game;
import com.rem.otl.core.game.environment.Square;
import com.rem.otl.core.gui.Gui;
import com.rem.otl.core.gui.graphics.GraphicEntity;
import com.rem.otl.core.gui.graphics.GraphicLine;
import com.rem.otl.core.gui.graphics.GraphicText;
import com.rem.otl.core.gui.graphics.GraphicView;
import com.rem.otl.core.gui.inputs.ClickEvent;
import com.rem.otl.core.main.Hub;
import com.rem.otl.core.storage.Resource;
import com.rem.otl.core.storage.Storage;

public class MainMenu extends Menu{

	public MainMenu(){
		this(true);
	}

	public MainMenu(boolean checkAndDestroyConnection) {
		super();
		if(checkAndDestroyConnection){
			if(Client.isConnected()){
				Client.endConnectionToTheServer();
			}
		}
		GraphicEntity button = new MenuButton("Solo"){
			@Override
			public void performOnRelease(ClickEvent e){
				solo();
			}
		};
		button.reposition(0.2f,0.51f);
		addChild(button);

		button = new MenuButton("Duo"){
			@Override
			public void performOnRelease(ClickEvent e){
				duo();
			}
		};
		button.reposition(0.2f,0.35f);
		addChild(button);

		/*
		button = new MenuButton("Editors"){
			@Override
			public void performOnRelease(MotionEvent e){
				editor();
			}
		};
		button.reposition(0.2f,0.19f);
		addChild(button);*/

		final MainMenu self = this;
		button = new IconMenuButton("music_player_icons",4){
			{
				icon.resize(0.08f, 0.08f);
			}
			@Override
			public void performOnRelease(ClickEvent e){
				Hub.gui.setView(new CreditMenu(self));
			}
			@Override
			public void resize(float w, float h){
				super.resize(w, h);
				if(icon!=null){
					left.resize(w*0.5f,h);
					mid.resize(w*0.0f,h);
					right.resize(w*0.5f,h);			
					icon.resize(0.08f,0.08f);
				}
			}
		};
		button.resize(0.09f, 0.08f);
		button.reposition(0.03f,0.77f);
		addChild(button);
	}

	public void solo(){
		Resource<InputStream> file = GetFileMenu.getFile(this,"maps/races",false);
		if(file!=null){
			Storage.loadMap(file);
			Hub.gui.setView(new Game(true,Hub.getNewRandomSeed(),this));
		}
	}
	public void duo(){
		Hub.gui.setView(new DuoMenu());
	}


	public void editor(){
		Hub.gui.setView(new EditorMenu());
	}

	public void startHighscores(){
	}


}
