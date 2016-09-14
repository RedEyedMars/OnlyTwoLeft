package game.modes;

import java.util.List;

import game.Game;
import game.hero.Hero;
import gui.graphics.GraphicEntity;
import gui.inputs.KeyBoardListener;

public interface GameMode extends KeyBoardListener{
	public void setup(Game game, boolean colourToControl, GraphicEntity wildWall);
	public void update(double seconds);
	public List<GraphicEntity> getAuxillaryChildren();
	public void loseGame(boolean isBlack);
	public void winGame(boolean isBlack,String nextMap);
	public boolean isCompetetive();
	public Hero createConnectedHero(boolean control, Game game, boolean whiteBool);
	public Hero createHero(Game game, boolean whiteBool);
}
