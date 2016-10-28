package com.rem.otl.core.game.hero;

import com.rem.otl.core.game.Action;
import com.rem.otl.core.gui.graphics.Animation;

public class HumanoidHeroAnimation<SubjectType extends Hero> implements Animation<SubjectType>{
	private static final int BLACK = 0;
	private static final int WHITE = 32;
	private static final int RIGHT = 0;
	private static final int LEFT = 5;
	private static final int IDLE = 2;
	private static final int WALKING = -2;
	private static final int JUMPING = 10;
	private int facing=RIGHT;
	private int progression = 0;
	private int progressionDirection = 1;
	private Action<Hero> jumpAction;
	
	public void onAnimate(Hero hero) {
		int state = 0;
		int colour = hero.isBlack()?BLACK:hero.isWhite()?WHITE:-1;
		if(hero.getXAcceleration()>0f){
			facing=RIGHT;
		}
		else if(hero.getXAcceleration()<0f){
			facing=LEFT;
		}
		if(hero.isJumping()||jumpAction!=null){
			state=JUMPING;
		}
		else if(hero.getXAcceleration()!=0f){
			state=WALKING;
		}
		else {
			state=IDLE;
		}
		if(state==IDLE){
			hero.setFrame(facing+IDLE+colour);
			progression = 0;
			progressionDirection = 1;
		}
		else if(state==WALKING){
			progression+=progressionDirection;
			if(progression*progressionDirection==2){
				progressionDirection*=-1;
			}
			hero.setFrame(facing+IDLE+progression+colour);
		}
		else if(state==JUMPING){
			if(jumpAction!=null){
				jumpAction.act(hero);
				jumpAction=null;
			}
			hero.setFrame(JUMPING+facing*2/5+1+colour);
		}
	}
	
	public void jump(Hero hero, Action<Hero> action){
		int colour = hero.isBlack()?BLACK:hero.isWhite()?WHITE:-1;
		hero.setFrame(JUMPING+facing*2/5+colour);
		this.jumpAction = action;
	}
}
