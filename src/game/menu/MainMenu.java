package game.menu;

import editor.Editor;
import editor.MapEditor;
import editor.OnCreateSquareEditor;
import game.Game;
import gui.Gui;
import gui.graphics.GraphicEntity;
import gui.graphics.GraphicText;
import gui.graphics.GraphicView;
import gui.inputs.MotionEvent;
import main.Hub;
import main.Main;

public class MainMenu extends GraphicView{

	public MainMenu() {
		super();
		addChild(new GraphicEntity("title"));
		GraphicEntity button = new Button("Editors"){
			@Override
			public void performOnClick(MotionEvent e){
				startStory();
			}
		};
		button.setY(0.51f);
		addChild(button);
		
		button = new GraphicEntity("blank"){
			@Override
			public void performOnClick(MotionEvent e){
				startEndless();
			}
		};
		button.adjust(1f,0.15f);
		button.setY(0.36f);
		button.setVisible(false);
		addChild(button);
		
		button = new GraphicEntity("blank"){
			@Override
			public void performOnClick(MotionEvent e){
				startPvp();
			}
		};
		button.adjust(1f,0.15f);
		button.setY(0.2f);
		button.setVisible(false);
		addChild(button);
		
		button = new GraphicEntity("blank"){
			@Override
			public void performOnClick(MotionEvent e){
				startHighscores();
			}
		};
		button.adjust(1f,0.15f);
		button.setY(0.04f);
		button.setVisible(false);
		addChild(button);
		
	
	}
	
	public void startStory(){
		Gui.setView(new OnCreateSquareEditor(null, 0f, 0f, 1f, 1f));
	}
	
	public void startEndless(){
		Main.loadMap();
		Gui.setView(new Game());
	}
	
	public void startPvp(){
		Gui.setView(new MapEditor());
	}
	
	public void startHighscores(){
	}
	
	private class Button extends GraphicEntity{		
		private Button self = this;
		public Button(String name) {
			super("blank",0);
			adjust(0.8f,0.15f);
			setX(0.2f);
			GraphicEntity left = new GraphicEntity("speech_bubble",0){
				@Override
				public void performOnClick(MotionEvent e){
					self.performOnClick(e);
				}
			};
			left.adjust(0.1f, 0.15f);
			left.setX(0.2f);
			left.setFrame(0);
			addChild(left);
			GraphicEntity mid = new GraphicEntity("speech_bubble",0){
				@Override
				public void performOnClick(MotionEvent e){
					self.performOnClick(e);
				}
			};
			mid.adjust(0.4f, 0.15f);
			mid.setX(0.3f);
			mid.setFrame(1);
			addChild(mid);
			GraphicEntity right = new GraphicEntity("speech_bubble",0){
				@Override
				public void performOnClick(MotionEvent e){
					self.performOnClick(e);
				}
			};
			right.adjust(0.1f, 0.15f);
			right.setX(0.7f);
			right.setFrame(2);
			addChild(right);
			
			GraphicText text = new GraphicText("impact",name);
			text.setWidthFactor(1.4f);
			text.setHeightFactor(3f);
			text.adjust(text.getWidth(), text.getHeight());
			text.setX(0.3f);
			addChild(text);
		}
		
	}
}
