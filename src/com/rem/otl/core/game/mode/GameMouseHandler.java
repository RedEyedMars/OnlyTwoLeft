package com.rem.otl.core.game.mode;

import com.rem.otl.core.duo.client.Client;
import com.rem.otl.core.game.hero.Hero;
import com.rem.otl.core.gui.inputs.HoverEvent;
import com.rem.otl.core.gui.inputs.KeyBoardEvent;
import com.rem.otl.core.gui.inputs.KeyBoardListener;
import com.rem.otl.core.gui.inputs.ClickEvent;
import com.rem.otl.core.gui.inputs.MouseListener;
import com.rem.otl.core.main.Hub;

public abstract class GameMouseHandler implements MouseListener, KeyBoardListener{
	private int previousClickDirection = -1;
	protected static final int RIGHT = 0;
	protected static final int UP = 1;
	protected static final int LEFT = 2;
	protected static final int DOWN = 3;
	
	@Override
	public boolean onClick(ClickEvent event) {
		if(event.getAction()==ClickEvent.ACTION_MOVE)return false;
		if(Hub.music!=null&&Hub.music.isMaximized()){
			return Hub.music.onClick(event);			
		}
		int direction = getDirection(event.getX(),event.getY());
		boolean action = event.getAction()==ClickEvent.ACTION_DOWN?KeyBoardEvent.KEY_DOWN:
						 event.getAction()==ClickEvent.ACTION_UP?KeyBoardEvent.KEY_UP:false;
		if(action==KeyBoardEvent.KEY_UP){
			previousClickDirection = -1;
			performOnRelease(event);
		}
		else if(action==KeyBoardEvent.KEY_DOWN){
			if(previousClickDirection!=direction){
				releaseCurrentKey(previousClickDirection,event.getButton());			
			}
			performOnPress(event);
		}
		keypressBasedOnClickDirection(direction,action,event.getButton());
		previousClickDirection=direction;
		return true;
	}
	protected void performOnRelease(ClickEvent event) {		
	}
	protected void performOnPress(ClickEvent event){
	}
	
	protected int getDirection(float x, float y) {

		double angle = Math.atan2(y-0.5f,x-0.5f);
		if(angle<=Math.PI/4f&&angle>=-Math.PI/4f){
			return RIGHT;
		}
		else if(angle<=Math.PI*3f/4f&&angle>=Math.PI/4f){
			return UP;
		}
		else if(angle>=Math.PI*3f/4f||angle<=-Math.PI*3f/4f){
			return LEFT;
		}
		else if(angle>=-Math.PI*3f/4f||angle<=-Math.PI/4f){
			return DOWN;
		}
		return -1;
	}

	protected void keypressBasedOnClickDirection(int direction, boolean action, int button) {
		if(direction==RIGHT){
			if(button==ClickEvent.MOUSE_LEFT){
				onType(new KeyBoardEvent(action,'d',32));
			}
			else if(!Client.isConnected()&&button==ClickEvent.MOUSE_RIGHT){
				onType(new KeyBoardEvent(action,' ',KeyBoardEvent.RIGHT));
			}
		}
		else if(direction==LEFT){
			if(button==ClickEvent.MOUSE_LEFT){
				onType(new KeyBoardEvent(action,'a',32));
			}
			else if(!Client.isConnected()&&button==ClickEvent.MOUSE_RIGHT){
				onType(new KeyBoardEvent(action,' ',KeyBoardEvent.LEFT));
			}
		}
		else if(direction==UP){
			if(button==ClickEvent.MOUSE_LEFT){
				onType(new KeyBoardEvent(action,'w',17));
			}
			else if(!Client.isConnected()&&button==ClickEvent.MOUSE_RIGHT){
				onType(new KeyBoardEvent(action,' ',KeyBoardEvent.UP));
			}
		}
		else if(direction==DOWN){
			if(button==ClickEvent.MOUSE_LEFT){
				onType(new KeyBoardEvent(action,'s',31));
			}
			else if(!Client.isConnected()&&button==ClickEvent.MOUSE_RIGHT){
				onType(new KeyBoardEvent(action,' ',KeyBoardEvent.DOWN));
			}
		}
	}
	protected void releaseCurrentKey(int direction, int button) {
		keypressBasedOnClickDirection(direction, KeyBoardEvent.KEY_UP, button);
	}
	@Override
	public boolean onHover(HoverEvent event) {
		return false;
	}
	@Override
	public void onMouseScroll(int distance) {		
	}
}
