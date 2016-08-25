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
		float dx = (float) (x*seconds);
		float dy = (float) (y*seconds);
		movementX += dx;
		movementY += dy;
		limiter  += Math.sqrt(dx*dx+dy*dy);
		if(onLimitBrokenAction>-1&&limiter>=limit){
			if(dx==0){
				self.move(0,dy-Math.signum(y)*(limiter-limit));
			}
			else if(dy==0){
				self.move(dx-Math.signum(x)*(limiter-limit),0);
			}
			limiters.get(onLimitBrokenAction).act(this);
			movementX=0f;
			movementY=0f;
			limiter =0f;
		}
		else {
			self.move(dx,dy);
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

	@Override
	public void onActivate(){
		super.onActivate();
		if(x==0&&y==0){
			return;
		}
		else if(y==0){
			self.setX(self.getX()+Math.signum(x)*limiter);
		}
		else if(x==0){
			self.setY(self.getY()+Math.signum(y)*limiter);
		}
		else {
			double angle = Math.atan2(y,x);
			self.setX((float) (self.getX()+Math.cos(angle)*limiter));
			self.setY((float) (self.getY()+Math.sin(angle)*limiter));
		}
	}
}
