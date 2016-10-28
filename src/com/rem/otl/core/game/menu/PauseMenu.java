package com.rem.otl.core.game.menu;

import com.rem.otl.core.duo.client.Client;
import com.rem.otl.core.game.Action;
import com.rem.otl.core.game.Game;
import com.rem.otl.core.game.menu.MainMenu;
import com.rem.otl.core.game.menu.MenuButton;
import com.rem.otl.core.gui.Gui;
import com.rem.otl.core.gui.graphics.GraphicEntity;
import com.rem.otl.core.gui.inputs.KeyBoardEvent;
import com.rem.otl.core.gui.inputs.KeyBoardListener;
import com.rem.otl.core.main.Hub;

public class PauseMenu extends GraphicEntity implements KeyBoardListener {

	private boolean paused = false;
	public PauseMenu(final Game game) {
		super("blank",Hub.BOT_LAYER);

		MenuButton resumeButton = new MenuButton("Resume"){
			@Override
			public void performOnRelease(com.rem.otl.core.gui.inputs.ClickEvent e){
				unpause();
			}
		};
		resumeButton.reposition(0.2f,0.51f);
		addChild(resumeButton);

		MenuButton restartButton = new MenuButton("Restart"){
			@Override
			public void performOnRelease(com.rem.otl.core.gui.inputs.ClickEvent e){
				unpause();
				game.restart();
			}
		};
		restartButton.reposition(0.2f,0.35f);
		addChild(restartButton);

		MenuButton returnToMain = new MenuButton("Return to Main Menu"){
			@Override
			public void performOnRelease(com.rem.otl.core.gui.inputs.ClickEvent e){
				unpause();
				game.returnToParent();
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
		Hub.handler.removeOnClick(this);
		Hub.handler.removeOnType(this);
	}

	@Override
	public void onType(KeyBoardEvent event) {
		if(event.keyDown()){
			if(event.is(KeyBoardEvent.ESCAPE)||event.is(25)||event.is(197)){
				unpause();
			}
		}
	}

	@Override
	public boolean continuousKeyboard() {
		return false;
	}
}
