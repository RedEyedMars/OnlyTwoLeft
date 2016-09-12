package duo.messages;

import duo.Handler;
import duo.client.Client;
import game.Game;
import game.Hero;
import game.menu.TransitionMenu;
import main.Hub;
import storage.Storage;

public class HeroEndGameMessage extends Message{

	private static final long serialVersionUID = -6677799385359052291L;
	private static TransitionMenu menu=null;
	private static String previousMapName=null;
	private static String nextMapFileName=null;
	private static String nextMapName=null;
	private static Boolean myColour=null;
	private static Boolean theirColour=null;
	private static Boolean blackWinner=null;
	private static Long blackTime=null;
	private static Boolean whiteWinner=null;
	private static Long whiteTime=null;

	private boolean colourToControl;
	private boolean successful;
	private long time;

	private HeroEndGameMessage(boolean colourToControl, boolean successful, long time) {
		this.colourToControl = colourToControl;
		this.successful=successful;
		this.time = time;
	}

	@Override
	public void act(Handler handler) {
		theirColour = colourToControl;
		if(colourToControl){
			blackWinner = successful;
			blackTime = time;
		}
		else {
			whiteWinner = successful;
			whiteTime = time;
		}
		if(isFinished()){
			finish();
		}
	}

	public static void setMenu(TransitionMenu menu) {
		HeroEndGameMessage.menu = menu;
	}

	public static void setAndSend(boolean colourToControl, boolean isWinner, long time) {
		if(myColour==null){
			myColour = colourToControl;
		}
		else if(myColour!=colourToControl){
			theirColour = colourToControl;
		}
		else {
			return;
		}
		if(colourToControl==Hero.BLACK_BOOL){
			blackWinner = isWinner;
			blackTime = time;
		}
		else if(colourToControl==Hero.WHITE_BOOL){
			whiteWinner = isWinner;
			whiteTime = time;
		}
		if(Client.isConnected()){
			Client.pass(new HeroEndGameMessage(colourToControl,isWinner,time));
		}

	}
	public static boolean isFinished(){
		if(myColour!=null&&theirColour!=null){
			return true;
		}
		else {
			return false;
		}
	}
	public static void finish(){
		Hub.loadMapFromFileName(nextMapFileName);
		if(theirColour!=null){
			if(theirColour){
				HeroEndGameMessage.menu.verifyWhoWon(blackWinner,blackTime);
			}
			else {
				HeroEndGameMessage.menu.verifyWhoWon(whiteWinner,whiteTime);				
			}			
		}
		if(nextMapName!=null){			
			HeroEndGameMessage.menu.canProceed(previousMapName, nextMapName, myColour);
		}

		menu=null;
		previousMapName=null;
		nextMapFileName=null;
		nextMapName=null;
		myColour=null;
		blackWinner=null;
		blackTime = null;
		theirColour=null;
		whiteWinner=null;
		whiteTime = null;
	}

	public static void setMapNames(String previousMapName,String nextMapName) {
		HeroEndGameMessage.previousMapName = previousMapName;
		HeroEndGameMessage.nextMapName = nextMapName;
	}
	public static void setNextMapFileName(String nextMapFileName) {
		HeroEndGameMessage.nextMapFileName = nextMapFileName;
	}

	public static boolean partnerHasWon() {
		if(theirColour==null)return false;
		if(theirColour==Hero.BLACK_BOOL){
			return blackWinner!=null&&blackWinner==true;
		}
		else if(theirColour==Hero.WHITE_BOOL){
			return whiteWinner!=null&&whiteWinner==true;
		}
		else return false;
	}


}
