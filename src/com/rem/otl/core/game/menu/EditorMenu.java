package com.rem.otl.core.game.menu;

import java.io.File;
import java.io.InputStream;

import com.rem.otl.core.editor.MapEditor;
import com.rem.otl.core.editor.OnCreateSquareEditor;
import com.rem.otl.core.gui.Gui;
import com.rem.otl.core.gui.graphics.GraphicEntity;
import com.rem.otl.core.gui.inputs.ClickEvent;
import com.rem.otl.core.main.Hub;
import com.rem.otl.core.storage.Resource;

public class EditorMenu extends Menu {
	public EditorMenu() {
		super();
		final EditorMenu menu = this;
		GraphicEntity button = new MenuButton("Map"){
			@Override
			public void performOnRelease(ClickEvent e){
				Hub.gui.setView(new MapEditor(menu));
			}
		};
		button.reposition(0.2f,0.51f);
		addChild(button);

		final EditorMenu self = this;
		button = new MenuButton("Square"){
			@Override
			public void performOnRelease(ClickEvent e){
				Resource<InputStream> saveTo = GetFileMenu.getFile(this,"ocs",true);
				Hub.gui.setView(new OnCreateSquareEditor(self,saveTo,0f,0f,1f,1f));
			}
		};
		button.reposition(0.2f,0.35f);
		addChild(button);
		
		
/*
		button = new MenuButton("Return"){
			@Override
			public void performOnRelease(MotionEvent e){
				returnToMain();
			}
		};
		button.reposition(0.2f,0.19f);
		addChild(button);
*/
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


	public void returnToMain(){
		//Gui.setView(new MainMenu());
	}

	@Override
	public void update(double seconds){
		Hub.renderer.loadFont("timesnewroman");
		super.update(seconds);
	}
}
