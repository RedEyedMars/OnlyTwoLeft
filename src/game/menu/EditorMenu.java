package game.menu;

import java.io.File;

import editor.MapEditor;
import editor.OnCreateSquareEditor;
import gui.Gui;
import gui.graphics.GraphicEntity;
import gui.inputs.MotionEvent;
import main.Hub;

public class EditorMenu extends Menu {
	public EditorMenu() {
		super();
		GraphicEntity button = new MenuButton("Map"){
			@Override
			public void performOnRelease(MotionEvent e){
					Gui.setView(new MapEditor());
			}
		};
		button.reposition(0.2f,0.51f);
		addChild(button);
		
		final EditorMenu self = this;
		button = new MenuButton("Square"){
			@Override
			public void performOnRelease(MotionEvent e){

				File saveTo = GetFileMenu.getFile(this,"ocs");
					Gui.setView(new OnCreateSquareEditor(self,saveTo,0f,0f,1f,1f));
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
	

	public void returnToMain(){
		Gui.setView(new MainMenu());
	}
	
	@Override
	public void update(double seconds){
		Hub.renderer.loadFont("timesnewroman");
		super.update(seconds);
	}
}
