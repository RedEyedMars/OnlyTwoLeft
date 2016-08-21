package game.menu;

public interface IDuoMenu {

	public void playerJoins(String playerName);
	public void kick();
	public void startGame(boolean colour, long seed);

}
