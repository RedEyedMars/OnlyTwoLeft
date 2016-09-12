package game.environment.update;

import game.Game;
import game.Hero;
import main.Hub;

public class GrowUpdateAction extends UpdateAction{
	private float growthW = 0f;
	private float growthH = 0f;
	public GrowUpdateAction(){
		defaultState = false;
	}
	@Override
	public void act(Double seconds) {
		float dx = (float) (x*seconds);
		float dy = (float) (y*seconds);
		growthW += dx;
		growthH += dy;
		limiter+=Math.sqrt(dx*dx+dy*dy);
		if(onLimitBrokenAction>-1&&limiter>=limit){
			if(dx==0){
				move(0,dy-Math.signum(y)*(limiter-limit));
			}
			else if(dy==0){
				move(dx-Math.signum(x)*(limiter-limit),0);
			}
			limiters.get(onLimitBrokenAction).act(this);
			growthW=0f;
			growthH=0f;
			limiter=0f;
		}
		else {
			move(dx,dy);
		}
	}
	@Override
	protected void move(float dx, float dy) {
		for(Hero hero:Hub.getBothHeroes()){
			if(y>0&&hero.getY()>=self.getY()+self.getHeight()||
					y<0&&hero.getY()+hero.getHeight()<=self.getY()||
					x>0&&hero.getX()>=self.getX()+self.getWidth()||
					x<0&&hero.getX()+hero.getWidth()<=self.getX()){
				hero.setX(hero.getX()+hero.getDeltaX());
				hero.setY(hero.getY()+hero.getDeltaY());
				if(hero.isWithin(self)){
					hero.move(dx,0);
				}
				hero.setX(hero.getX()-hero.getDeltaX());
				hero.setY(hero.getY()-hero.getDeltaY());
			}
		}
		self.adjust(self.getWidth()+dx,self.getHeight()+dy);
	}
	@Override
	public void flip(){
		growthH=-growthH;
		y=-y;
	}
	@Override
	public void undo(){
		self.adjust(self.getWidth()-growthW, self.getHeight()-growthH);
	}
	@Override
	public int getIndex() {
		return 0;
	}
	@Override
	public UpdateAction create(){
		return new GrowUpdateAction();
	}
}
