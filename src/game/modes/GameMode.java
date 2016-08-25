package game.modes;

import java.util.List;

import game.Hero;
import gui.graphics.GraphicEntity;
import gui.inputs.KeyBoardListener;

public interface GameMode extends KeyBoardListener{
	public void setup(boolean colourToControl,Hero black, Hero white, GraphicEntity wildWall);
	public void update(double seconds);
	public List<GraphicEntity> getAuxillaryChildren();
	public void loseGame();
	public void winGame(String nextMap);
}
