package com.rem.otl.core.game.hero;

import com.rem.otl.core.game.Action;
import com.rem.otl.core.game.Game;

public class HumanoidHero extends Hero {

	public HumanoidHero(Game game, boolean colour) {
		super("human_heroes",game, colour);
		setAnimation(new HumanoidHeroAnimation());
	}
	
	@Override
	public void jump(Action<Hero> action){
		((HumanoidHeroAnimation)animation).jump(this,action);
	}

}
