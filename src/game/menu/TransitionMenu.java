package game.menu;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import duo.client.Client;
import duo.messages.PassMessage;
import duo.messages.SaveGameMessage;
import duo.messages.StartGameMessage;
import editor.TextWriter;
import game.Game;
import gui.Gui;
import gui.graphics.GraphicEntity;
import gui.inputs.KeyBoardListener;
import gui.inputs.MotionEvent;
import main.Hub;
import main.Main;

public class TransitionMenu extends Menu{
	private TextWriter playerName;
	private boolean isBest;
	private boolean isWinner;
	private boolean isCompetitive;
	private int minutes;
	private int seconds;
	private int millis;
	private boolean canProceed;
	private MenuButton winnerButton;
	private GraphicEntity returnButton;
	public TransitionMenu(
			boolean isCompetitive,boolean isWinner,long millisecondsToComplete,
			final String previousMapName, final String nextMapName, final boolean myColour, boolean canProceed){
		super();
		this.isCompetitive = isCompetitive;
		this.isWinner = isWinner;
		this.minutes = (int) (millisecondsToComplete/1000/60);
		this.seconds = (int) ((millisecondsToComplete/1000)%60);
		this.millis = (int) ((millisecondsToComplete)%1000);
		this.canProceed = canProceed;
		//if(!competitive){
		final File saveFile = new File("data"+File.separatorChar+"save.data");
		String lastName = "";
		long bestWin = Long.MAX_VALUE;
		long bestFail = 0;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(saveFile));
			String line = reader.readLine();
			while(line!=null){
				int firstTab = line.indexOf('\t');
				if(firstTab==-1){
					line=reader.readLine();
					continue;
				}
				else if(firstTab==0){
					lastName="";
				}
				else {
					lastName=line.substring(0, firstTab);
				}
				if(previousMapName.equals(
						line.substring(firstTab+1,line.indexOf('\t',firstTab+1)))){
					String[] split = line.split("\\tIN\\t");
					if(split!=null&&split.length>1){
						Long millis = Long.parseLong(split[1].substring(0, split[1].indexOf("ms")));
						if(line.contains("\tW\t")){
							if(millis<bestWin){
								bestWin=millis;
							}
						}
						else if(line.contains("\tL\t")){
							if(millis>bestFail){
								bestFail=millis;
							}
						}
					}
				}
				line=reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		winnerButton = new MenuButton(isCompetitive?isWinner?"Victory!":"Defeat":isWinner?"Completed!":"Failed");
		winnerButton.setX(0.2f);
		winnerButton.setY(0.83f);
		addChild(winnerButton);
		boolean won = bestWin!=Long.MAX_VALUE;
		isBest = false;
		if((won&&millisecondsToComplete<=bestWin&&isWinner)||
				(!won&&(isWinner||millisecondsToComplete>=bestFail))){
			isBest = true;			
			playerName = new TextWriter("impact",lastName){
				{
					setWidthFactor(1.4f);
					setHeightFactor(3f);
					adjust(getWidth(), getHeight());
					charIndex=text.length();
					index=text.length();
				}
				@Override
				public void keyCommand(boolean b, char c, int keycode){
					if(keycode==14){
						super.keyCommand(b, c, keycode);
					}
					else if(c>=32){
						if(lines.get(0).size()==0||
								(lines.get(0).getChild(lines.get(0).size()-1).getX()<0.9f-0.1f)){
							super.keyCommand(b, c, keycode);
						}
					}
				}
			};
			GraphicEntity nameButton = new MenuButton("Name:"){
				@Override
				public void performOnRelease(MotionEvent e){
					Gui.giveOnType(playerName);
				}
				@Override
				public float offsetX(int index){
					if(index<3){
						return super.offsetX(index);
					}
					else {
						return 0.05f;
					}
				}
			};
			nameButton.adjust(0.8f, 0.15f);
			nameButton.setX(0.1f);
			nameButton.setY(0.67f);


			playerName.setX(0.325f);
			playerName.setY(0.69f);
			addChild(nameButton);
			addChild(playerName);

		}
		//}
		GraphicEntity time = new MenuButton((minutes>0?(minutes+"m "):" ")+seconds+"s"){
			@Override
			public void performOnRelease(MotionEvent e){
				Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
				StringSelection stringSelection = new StringSelection(this.getText());
				clip.setContents(stringSelection, null);
			}
		};
		time.setX(0.2f);
		time.setY(0.51f);
		addChild(time);

		returnButton = new MenuButton("Return to Main Menu"){
			@Override
			public void performOnRelease(MotionEvent e){
				if(Client.isConnected()){
					Client.pass(new PassMessage(
							new SaveGameMessage(previousMapName,minutes,seconds)));
				}
				saveTime(previousMapName);
				returnToMain();
			}
		};
		returnButton.setX(0.2f);
		returnButton.setY(0.19f);
		addChild(returnButton);
		if(isBest){
			GraphicEntity bestIndicator = new GraphicEntity("menu_best_indicator",1);
			bestIndicator.adjust(0.075f, 0.075f);
			bestIndicator.setX(0.6f);
			bestIndicator.setY(0.56f);
			addChild(bestIndicator);
		}
		else {
			winnerButton.setY(0.67f);
		}
	}

	public void saveTime(String previousMapName){
		if(isBest){
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(new File("data"+File.separatorChar+"save.data"),true));
				DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				Date date = new Date();
				writer.write("\n"+playerName.getText()+"\t"+previousMapName+(isWinner?"\tW\t":"\tL\t")+"on "+dateFormat.format(date)+"\tIN\t"+(((minutes*60)+seconds)*1000+millis)+"ms");
				writer.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	private void returnToMain() {
		Client.endConnectionToTheServer();
		Gui.setView(new MainMenu());
	}
	public void exit() {
		returnButton.performOnRelease(null);
	}
	public void verifyWhoWon(boolean successful, long theirTime) {
		if(!isCompetitive){
			if(isWinner&&!successful){
				isWinner=false;
			}
		}
		else {
			if(isWinner&&successful){
				long myTime = this.millis+1000*(this.seconds+60*this.minutes);
				if(myTime==theirTime){
					winnerButton.changeText("Tie");
					return;
				}
				else if(myTime>theirTime){
					isWinner = false;
				}
			}
		}
		winnerButton.changeText(isCompetitive?isWinner?"Victory!":"Defeat":isWinner?"Completed!":"Failed");
	}
	public void canProceed(final String previousMapName,String nextMapName,final boolean myColour){
		if(canProceed){
			GraphicEntity proceedButton = new MenuButton(nextMapName){
				@Override
				public void performOnRelease(MotionEvent e){
					long seed = Main.getNewRandomSeed();
					if(Client.isConnected()){
						Client.pass(new SaveGameMessage(previousMapName,minutes,seconds));						
						Client.pass(new StartGameMessage(!myColour));
					}
					saveTime(previousMapName);
					Game game = new Game(myColour,seed);
					Gui.setView(game);
				}
			};
			proceedButton.setX(0.2f);
			proceedButton.setY(0.35f);
			addChild(proceedButton);
			proceedButton.onAddToDrawable();
		}
	}

	public KeyBoardListener getDefaultKeyBoardListener(){
		return playerName;
	}

	public int getMinutes() {
		return minutes;
	}
	public int getSeconds() {
		return seconds;
	}
	public int getMillis() {
		return millis;
	}
	
}
