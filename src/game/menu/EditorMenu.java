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

public class EditorMenu extends Menu {
	public EditorMenu(List<Square> squares) {
		super();
		GraphicEntity button = new MenuButton("Map"){
			@Override
			public void performOnRelease(MotionEvent e){
					Gui.setView(new MapEditor());
			}
		};
		button.setX(0.2f);
		button.setY(0.51f);
		addChild(button);
		
		button = new MenuButton("Square"){
			@Override
			public void performOnRelease(MotionEvent e){
					Gui.setView(new OnCreateSquareEditor(null,0f,0f,1f,1f));
			}
		};
		button.setX(0.2f);
		button.setY(0.35f);
		addChild(button);
		
		button = new MenuButton("Return"){
			@Override
			public void performOnRelease(MotionEvent e){
					returnToMain();
			}
		};
		button.setX(0.2f);
		button.setY(0.19f);
		addChild(button);
		
		for(Square square:squares){
			addChild(square);
		}
		this.squares = squares;
	}
	

	public void returnToMain(){
		Gui.setView(new MainMenu(squares));
	}
}
