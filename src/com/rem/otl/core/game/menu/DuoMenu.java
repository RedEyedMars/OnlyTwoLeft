package com.rem.otl.core.game.menu;

import com.rem.otl.core.gui.graphics.GraphicEntity;
import com.rem.otl.core.gui.inputs.ClickEvent;
import com.rem.otl.core.main.Hub;

public class DuoMenu extends Menu {
	public DuoMenu() {
		super();
		GraphicEntity button = new MenuButton("Host"){
			@Override
			public void performOnRelease(ClickEvent e){
					host();
			}
		};
		button.reposition(0.2f,0.51f);
		addChild(button);
		
		button = new MenuButton("Join"){
			@Override
			public void performOnRelease(ClickEvent e){
				join();
			}
		};
		button.reposition(0.2f,0.35f);
		addChild(button);
		
		button = new MenuButton("Return"){
			@Override
			public void performOnRelease(ClickEvent e){
					returnToMain();
			}
		};
		button.reposition(0.2f,0.19f);
		addChild(button);
	}
	
	public void host(){
		Hub.gui.setView(new HostMenu());
	}
	
	public void join(){
		Hub.gui.setView(new JoinMenu());
	}

	public void returnToMain(){
		Hub.gui.setView(new MainMenu());
	}
}
