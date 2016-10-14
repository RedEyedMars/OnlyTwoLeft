package game.mode;

import duo.client.Client;
import gui.inputs.KeyBoardListener;
import gui.inputs.MotionEvent;
import gui.inputs.MouseListener;

public abstract class GameMouseHandler implements MouseListener, KeyBoardListener{
	private int previousClickDirection = -1;
	private static final int RIGHT = 0;
	private static final int UP = 1;
	private static final int LEFT = 2;
	private static final int DOWN = 3;
	@Override
	public boolean onClick(MotionEvent event) {
		if(event.getAction()==MotionEvent.ACTION_MOVE)return false;		
		double angle = Math.atan2(event.getY()-0.5f,event.getX()-0.5f);
		int direction = getDirection(angle);
		boolean action = event.getAction()==MotionEvent.ACTION_DOWN?KeyBoardListener.DOWN:
						 event.getAction()==MotionEvent.ACTION_UP?KeyBoardListener.UP:false;
		if(action==KeyBoardListener.UP){
			previousClickDirection = -1;
		}
		else if(previousClickDirection!=direction&&action==KeyBoardListener.DOWN){
			keypressBasedOnClickDirection(previousClickDirection,KeyBoardListener.UP,event.getButton());
		}
		keypressBasedOnClickDirection(direction,action,event.getButton());
		previousClickDirection=direction;
		return true;
	}
	private int getDirection(double angle) {
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

	private void keypressBasedOnClickDirection(int direction, boolean action, int button) {
		if(direction==RIGHT){
			if(button==MotionEvent.MOUSE_LEFT){
				keyCommand(action,'d',32);
			}
			else if(!Client.isConnected()&&button==MotionEvent.MOUSE_RIGHT){
				keyCommand(action,' ',205);
			}
		}
		else if(direction==UP){
			if(button==MotionEvent.MOUSE_LEFT){
				keyCommand(action,'w',17);
			}
			else if(!Client.isConnected()&&button==MotionEvent.MOUSE_RIGHT){
				keyCommand(action,' ',200);
			}
		}
		else if(direction==LEFT){
			if(button==MotionEvent.MOUSE_LEFT){
				keyCommand(action,'a',30);
			}
			else if(!Client.isConnected()&&button==MotionEvent.MOUSE_RIGHT){
				keyCommand(action,' ',203);
			}
		}
		else if(direction==DOWN){
			if(button==MotionEvent.MOUSE_LEFT){
				keyCommand(action,'s',31);
			}
			else if(!Client.isConnected()&&button==MotionEvent.MOUSE_RIGHT){
				keyCommand(action,' ',208);
			}
		}
	}
	@Override
	public boolean onHover(MotionEvent event) {
		return false;
	}
	@Override
	public void onMouseScroll(int distance) {		
	}
}
