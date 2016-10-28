package com.rem.otl.core.game.environment.update;

import com.rem.otl.core.game.Game;
import com.rem.otl.core.game.hero.Hero;
import com.rem.otl.core.main.Hub;

public class GrowUpdateAction extends UpdateAction{
	private float origW;
	private float origH;
	public GrowUpdateAction(){
		defaultState = false;
	}
	@Override
	public void act(Double seconds) {
		
		if(onLimitReachedAction>-1){
			float moveX = -(limiters.get(onLimitReachedAction).getDelta(timeSinceStart,(x*1),limit));
			float moveY = -(limiters.get(onLimitReachedAction).getDelta(timeSinceStart,(y*1),limit));
			timeSinceStart+=seconds;
			moveX += (limiters.get(onLimitReachedAction).getDelta(timeSinceStart,(x*1),limit));
			moveY += (limiters.get(onLimitReachedAction).getDelta(timeSinceStart,(y*1),limit));
			move(moveX,moveY);
			if(this.hasReachedLimit()){
				timeSinceStart=this.getTimeToLimit();
			}
		}
		else {
			float moveX = -(float)(timeSinceStart*x*1);
			float moveY = -(float)(timeSinceStart*y*1);
			timeSinceStart+=seconds;
			moveX += (float)(timeSinceStart*x*1);
			moveY += (float)(timeSinceStart*y*1);
			move(moveX,moveY);
		}
	}
	@Override
	protected void move(float dx, float dy) {

		self.resize(self.getWidth()+dx,self.getHeight()+dy);
		if(y<0){
			self.reposition(self.getX(), self.getY()-dy);
		}
		if(x<0){
			self.reposition(self.getX()-dx, self.getY());
		}
		for(Hero hero:Hub.getBothHeroes()){
			if(y>0&&hero.getY()>=self.getY()+self.getHeight()||
					y<0&&hero.getY()+hero.getHeight()<=self.getY()||
					x>0&&hero.getX()>=self.getX()+self.getWidth()||
					x<0&&hero.getX()+hero.getWidth()<=self.getX()){
				hero.reposition(hero.getX()+hero.getDeltaX(),
						    hero.getY()+hero.getDeltaY());
				if(hero.isWithin(self)){
					hero.move(dx,0);
				}
				hero.reposition(hero.getX()-hero.getDeltaX(),
						  hero.getY()-hero.getDeltaY());
			}
		}
	}
	@Override
	public int getIndex() {
		return 0;
	}
	@Override
	public UpdateAction create(){
		return new GrowUpdateAction();
	}
	@Override
	public void onActivate(){
		super.onActivate();
		if(limit!=0){
			timeSinceStart = startAtPercent/limit;
		}
		else {
			timeSinceStart=0;
		}
		origW = this.self.getWidth();
		origH = this.self.getHeight();
	}
}
