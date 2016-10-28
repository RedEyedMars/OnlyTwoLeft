package com.rem.otl.core.game.mode;

import java.util.List;

import com.rem.otl.core.game.Game;
import com.rem.otl.core.game.hero.Hero;
import com.rem.otl.core.gui.graphics.GraphicEntity;
import com.rem.otl.core.gui.inputs.KeyBoardListener;
import com.rem.otl.core.gui.inputs.MouseListener;

public interface GameMode extends KeyBoardListener, MouseListener{
	public void setup(Game game, boolean colourToControl, GraphicEntity wildWall);
	public void update(double seconds);
	public List<GraphicEntity> getAuxillaryChildren();
	public void loseGame(boolean isBlack);
	public void winGame(boolean isBlack,String nextMap);
	public boolean isCompetetive();
	public Hero createConnectedHero(boolean control, Game game, boolean whiteBool);
	public Hero createHero(Game game, boolean whiteBool);
}
