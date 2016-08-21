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
import duo.messages.StartGameMessage;
import editor.TextWriter;
import game.Game;
import gui.Gui;
import gui.graphics.GraphicEntity;
import gui.inputs.KeyBoardListener;
import gui.inputs.MotionEvent;
import main.Main;

public class TransitionMenu extends Menu{
	TextWriter name;
	public TransitionMenu(
			boolean competitive,final boolean winner,final int minutes,final int seconds,
			final String previousMapName, final String nextMapName, final boolean myColour, boolean canProceed){
		super();
		//if(!competitive){
		final File saveFile = new File("data"+File.separatorChar+"save.data");
		String lastName = "";
		int bestWin = 1000000;
		int bestFail = 1000000;
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
						Integer mins = Integer.parseInt(split[1].substring(0, split[1].indexOf('m')));
						Integer secs = Integer.parseInt(split[1].substring(split[1].indexOf(' ')+1,split[1].indexOf('s')));
						secs=mins*60+secs;
						if(line.contains("\tCompleted\t")){
							if(secs<bestWin){
								bestWin=secs;
							}
						}
						else if(line.contains("\tFailed\t")){
							if(secs<bestFail){
								bestFail=secs;
							}
						}
					}
				}
				line=reader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		GraphicEntity winnerButton = new MenuButton(winner?"Completed!":"Failed");
		winnerButton.setX(0.2f);
		winnerButton.setY(0.83f);
		addChild(winnerButton);
		final int bestTime = bestWin==1000000?bestFail:bestWin;
		boolean best = false;
		//System.out.println(bestTime+"|"+minutes*60+seconds);
		if(minutes*60+seconds<=bestTime&&(bestTime==bestWin&&winner||bestTime==bestFail)){
			best = true;

			
			name = new TextWriter("impact",lastName){
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
					Gui.giveOnType(name);
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


			name.setX(0.325f);
			name.setY(0.69f);
			addChild(nameButton);
			addChild(name);


			GraphicEntity saveTime = new MenuButton("Save"){
				boolean saved = false;
				@Override
				public void performOnRelease(MotionEvent e){
					if(!saved){
						try {
							BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile,true));
							DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
							Date date = new Date();
							writer.write("\n"+name.getText()+"\t"+previousMapName+(winner?"\tCompleted\t":"\tFailed\t")+"on "+dateFormat.format(date)+"\tIN\t"+minutes+"m "+seconds+"s");
							writer.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
			};
			saveTime.setX(0.2f);
			saveTime.setY(0.51f);
			addChild(saveTime);		

		}
		//}
		GraphicEntity time = new MenuButton(minutes+"m "+seconds+"s"){
			@Override
			public void performOnRelease(MotionEvent e){
				Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
				StringSelection stringSelection = new StringSelection(this.getText());
				clip.setContents(stringSelection, null);
			}
		};
		time.setX(0.2f);
		time.setY(0.35f);
		addChild(time);
		GraphicEntity proceedButton = null;
		if(canProceed){
			proceedButton = new MenuButton(nextMapName){
				@Override
				public void performOnRelease(MotionEvent e){
					long seed = Main.getNewRandomSeed();
					if(Client.isConnected()){
						Client.pass(new StartGameMessage(!myColour));
					}
					Game game = new Game(myColour,seed);
					Gui.setView(game);
				}
			};
			proceedButton.setX(0.2f);
			proceedButton.setY(0.19f);
			addChild(proceedButton);
		}

		GraphicEntity returnButton = new MenuButton("Return to Main Menu"){
			@Override
			public void performOnRelease(MotionEvent e){
				returnToMain();
			}
		};
		returnButton.setX(0.2f);
		returnButton.setY(0.03f);
		addChild(returnButton);
		if(!best){
			winnerButton.setY(0.67f);
			time.setY(0.51f);
			if(proceedButton!=null){
				proceedButton.setY(0.35f);
			}
			returnButton.setY(0.19f);
		}
		else {
			GraphicEntity bestIndicator = new GraphicEntity("menu_best_indicator",1);
			bestIndicator.adjust(0.075f, 0.075f);
			bestIndicator.setX(0.6f);
			bestIndicator.setY(0.4f);
			addChild(bestIndicator);
		}
	}

	private void returnToMain() {
		Client.endConnectionToTheServer();
		Gui.setView(new MainMenu());
	}

	public KeyBoardListener getDefaultKeyBoardListener(){
		return name;
	}
}
