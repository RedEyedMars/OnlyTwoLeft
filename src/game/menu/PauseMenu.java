package game.menu;

import duo.client.Client;
import game.Action;
import game.Game;
import game.menu.MainMenu;
import game.menu.MenuButton;
import gui.Gui;
import gui.graphics.GraphicEntity;
import gui.inputs.KeyBoardListener;
import main.Hub;

public class PauseMenu extends GraphicEntity implements KeyBoardListener {

	private boolean paused = false;
	public PauseMenu(final Game game) {
		super("blank",0);

		MenuButton resumeButton = new MenuButton("Resume"){
			@Override
			public void performOnRelease(gui.inputs.MotionEvent e){
				unpause();
			}
		};
		resumeButton.reposition(0.2f,0.51f);
		addChild(resumeButton);

		MenuButton restartButton = new MenuButton("Restart"){
			@Override
			public void performOnRelease(gui.inputs.MotionEvent e){
				unpause();
				game.restart();
			}
		};
		restartButton.reposition(0.2f,0.35f);
		addChild(restartButton);

		MenuButton returnToMain = new MenuButton("Return to Main Menu"){
			@Override
			public void performOnRelease(gui.inputs.MotionEvent e){
				unpause();
				Client.endConnectionToTheServer();
				Gui.setView(new MainMenu());
			}
		};
		returnToMain.reposition(0.2f,0.19f);
		addChild(returnToMain);

	}

	public boolean isPaused() {
		return paused;
	}
	public void pause(){
		this.paused = true;
		this.setVisible(true);
	}

	public void unpause(){
		this.paused = false;
		this.setVisible(false);
		removeInputs();
	}
	public void removeInputs(){
		Gui.removeOnClick(this);
		Gui.removeOnType(this);
	}

	@Override
	public void keyCommand(boolean b, char c, int keycode) {
		if(b==KeyBoardListener.DOWN){
			if(keycode==1||keycode==25||keycode==197){
				unpause();
			}
		}
	}

	@Override
	public boolean continuousKeyboard() {
		return false;
	}
}
