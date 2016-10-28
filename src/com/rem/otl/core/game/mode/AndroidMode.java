package com.rem.otl.core.game.mode;

import com.rem.otl.core.duo.client.Client;
import com.rem.otl.core.game.Game;
import com.rem.otl.core.game.menu.IconMenuButton;
import com.rem.otl.core.gui.graphics.GraphicEntity;
import com.rem.otl.core.gui.graphics.GraphicText;
import com.rem.otl.core.gui.inputs.KeyBoardEvent;
import com.rem.otl.core.gui.inputs.KeyBoardListener;
import com.rem.otl.core.gui.inputs.ClickEvent;
import com.rem.otl.core.main.Hub;

public class AndroidMode extends RaceMode{

	private boolean allowKey = false;
	private IconMenuButton pauseButton;
	@Override
	public void setup(Game game, boolean colourToControl, GraphicEntity wildWall){
		super.setup(game, colourToControl, wildWall);
		pauseButton = new IconMenuButton("editor_button",1){
			@Override
			public void performOnClick(ClickEvent e){
				onType(new KeyBoardEvent(KeyBoardEvent.KEY_DOWN,' ',KeyBoardEvent.ESCAPE));
			}
		};
		pauseButton.resize(0.09f, 0.08f);
		pauseButton.reposition(0.88f, 0.89f);
		auxillaryChildren.add(pauseButton);
	}
	
	@Override
	public boolean onClick(ClickEvent event) {
		allowKey = true;
		if(event.getAction()==ClickEvent.ACTION_DOWN&&
				pauseButton.isWithin(event.getX(), event.getY())){
			pauseButton.performOnClick(event);
			allowKey = false;
			return true;
		}
		else {
			boolean ret = super.onClick(event);
			allowKey = false;
			return ret;
		}
	}
	@Override
	public void onType(KeyBoardEvent event){
		if(allowKey){
			super.onType(event);
		}
	}
	@Override
	protected int getDirection(float x, float y) {
		double angle = Math.atan2(y-(focused.getY()+focused.getHeight()/2f),x-(focused.getX()+focused.getWidth()/2f));		
		if(angle<=Math.PI/2f&&angle>=-Math.PI/2f){
			return GameMouseHandler.RIGHT;
		}
		else if(angle>=Math.PI/2f||angle<=-Math.PI/2f){
			return GameMouseHandler.LEFT;
		}
		return -1;
	}
	
	@Override
	public void performOnRelease(ClickEvent event){
		jump();
	}
	protected void keypressBasedOnClickDirection(int direction, boolean action, int button) {
		super.keypressBasedOnClickDirection(direction, KeyBoardEvent.KEY_UP, button);
	}
	protected void releaseCurrentKey(int direction, int button) {
		super.keypressBasedOnClickDirection(direction, KeyBoardEvent.KEY_UP, button);
	}
}
