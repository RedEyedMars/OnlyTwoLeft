package game.menu;

import java.util.ArrayList;
import java.util.List;

import editor.MapEditor;
import editor.OnCreateSquareEditor;
import game.Game;
import game.environment.Square;
import gui.Gui;
import gui.graphics.GraphicEntity;
import gui.graphics.GraphicView;
import gui.inputs.MotionEvent;
import main.Main;

public class DuoMenu extends Menu {
	public DuoMenu() {
		super();
		GraphicEntity button = new MenuButton("Host"){
			@Override
			public void performOnRelease(MotionEvent e){
					host();
			}
		};
		button.reposition(0.2f,0.51f);
		addChild(button);
		
		button = new MenuButton("Join"){
			@Override
			public void performOnRelease(MotionEvent e){
				join();
			}
		};
		button.reposition(0.2f,0.35f);
		addChild(button);
		
		button = new MenuButton("Return"){
			@Override
			public void performOnRelease(MotionEvent e){
					returnToMain();
			}
		};
		button.reposition(0.2f,0.19f);
		addChild(button);
	}
	
	public void host(){
		Gui.setView(new HostMenu());
	}
	
	public void join(){
		Gui.setView(new JoinMenu());
	}

	public void returnToMain(){
		Gui.setView(new MainMenu());
	}
}
