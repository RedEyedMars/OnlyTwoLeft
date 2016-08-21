package game.environment.update;

import java.util.Iterator;

public class MoveUpdateAction extends UpdateAction {
	public MoveUpdateAction(){
		defaultState = true;
	}
	private float movementX = 0f;
	private float movementY = 0f;
	private float origXvel = 0f;
	private float origYvel = 0f;
	@Override
	public void act(Double seconds) {
		movementX += x*seconds;
		movementY += y*seconds;
		if(onLimitBrokenAction>-1&&Math.sqrt(movementX*movementX+movementY*movementY)>=limit){
			limiters.get(onLimitBrokenAction).act(this);
			movementX=0f;
			movementY=0f;
		}
		else {
			self.move((float) (x*seconds),(float) (y*seconds));
		}
	}
	@Override
	public void flip(){
		movementY=-movementY;
		y=-y;
		origYvel=-origYvel;
	}
	@Override
	public void undo(){
		self.setX(self.getX()-movementX);
		self.setY(self.getY()-movementY);
		setFloats(origXvel,origYvel);
	}
	@Override
	public void setArgs(Iterator<Integer> ints,Iterator<Float> floats){
		super.setArgs(ints,floats);
		origXvel=getFloat(0);
		origYvel=getFloat(1);
	}
	@Override
	public int getIndex() {
		return 1;
	}
	@Override
	public UpdateAction create(){
		return new MoveUpdateAction();
	}
}
