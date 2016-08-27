package game.menu;

import editor.MapEditor;
import editor.OnCreateSquareEditor;
import gui.Gui;
import gui.graphics.GraphicEntity;
import gui.inputs.MotionEvent;

public class EditorMenu extends Menu {
	public EditorMenu() {
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
		
	}
	

	public void returnToMain(){
		Gui.setView(new MainMenu());
	}
}
