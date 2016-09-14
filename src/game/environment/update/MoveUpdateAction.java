package game.environment.update;

import java.util.Iterator;

import game.Game;
import game.hero.Hero;

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
				move(0,dy-Math.signum(y)*(limiter-limit));				
			}
			else if(dy==0){
				move(dx-Math.signum(x)*(limiter-limit),0);
			}
			else if(y>x){
				move(dx-Math.signum(x)*(limiter-limit)*Math.abs(x/y),dy-Math.signum(y)*(limiter-limit)*(1-Math.abs(x/y)));
			}
			else {
				move(dx-Math.signum(x)*(limiter-limit)*(1-Math.abs(y/x)),dy-Math.signum(y)*(limiter-limit)*Math.abs(y/x));
			}
			limiters.get(onLimitBrokenAction).act(this);
			if(self.isActive()){
				movementX=0f;
				movementY=0f;
				limiter =0f;
			}
		}
		else {
			move(dx,dy);
		}
	}
	protected void move(float dx, float dy) {
		self.move(dx, dy);
		super.move(dx,dy);
	}
	@Override
	public void flip(){
		movementY=-movementY;
		y=-y;
		origYvel=-origYvel;
	}
	@Override
	public void undo(){
		self.reposition(self.getX()-movementX,
				  self.getY()-movementY);
		setFloats(origXvel,origYvel);
	}
	@Override
	public void loadFrom(Iterator<Integer> ints,Iterator<Float> floats){
		super.loadFrom(ints,floats);
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
			self.reposition(self.getX()+Math.signum(x)*limiter,self.getY());
		}
		else if(x==0){
			self.reposition(self.getX(),self.getY()+Math.signum(y)*limiter);
		}
		else {
			double angle = Math.atan2(y,x);
			self.reposition((float) (self.getX()+Math.cos(angle)*limiter),
					    (float) (self.getY()+Math.sin(angle)*limiter));
		}
	}
}
