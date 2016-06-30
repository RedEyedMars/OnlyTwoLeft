package game.menu;

import game.Action;
import gui.Gui;
import gui.graphics.GraphicEntity;
import gui.graphics.GraphicView;
import gui.inputs.KeyBoardListener;
import gui.inputs.MotionEvent;
import main.Hub;
import storage.Storage;
public class HighscoreMenu extends GraphicView{
	private String modeToInjectInto;
	private GraphicWord inputWord;
	private static final float[] sideYs = new float[]{0.41f,0.23f,0.055f};
	public HighscoreMenu(String mode, int newScore) {
		super();
		addChild(new GraphicEntity("enter_highscores"));
		modeToInjectInto = mode;
		if(newScore>Hub.getValue(mode, Hub.highscores)){
			if(Hub.highscoreNames.containsKey(mode+"2")){
				Hub.highscoreNames.put(mode+"3", Hub.highscoreNames.get(mode+"2"));
				Hub.highscores.put(mode+"3", Hub.highscores.get(mode+"2"));
			}
			if(Hub.highscoreNames.containsKey(mode)){
				Hub.highscoreNames.put(mode+"2", Hub.highscoreNames.get(mode));
				Hub.highscores.put(mode+"2", Hub.highscores.get(mode));
			}
		}
		else if(newScore>Hub.getValue(mode+"2", Hub.highscores)){
			modeToInjectInto = mode+"2";
			if(Hub.highscoreNames.containsKey(mode+"2")){
				Hub.highscoreNames.put(mode+"3", Hub.highscoreNames.get(mode+"2"));
				Hub.highscores.put(mode+"3", Hub.highscores.get(mode+"2"));
			}
		}
		else {
			modeToInjectInto = mode+"3";
		}
		Hub.highscores.put(modeToInjectInto, newScore);
		Hub.highscoreNames.put(modeToInjectInto, "Enter Name");
		for(int i=1;i<4;++i){
			String modeToDisplay = mode;
			if(i>1){
				modeToDisplay = mode + i;
			}
			if(Hub.highscores.containsKey(modeToDisplay)){
				GraphicNumber num = new GraphicNumber();
				num.setX(0.82f);
				num.setY(sideYs[i-1]);
				num.setValue(Hub.highscores.get(modeToDisplay));
				addChild(num);
				GraphicWord word = new GraphicWord();
				word.setX(0.1f);
				word.setY(sideYs[i-1]);
				if(modeToInjectInto.equals(modeToDisplay)){
					this.inputWord = word;
					word.takeKeyboard(new Action(){
						@Override
						public void act(Object subject) {
							String name = subject.toString();
							if(!"".equals(name)){
								Hub.highscoreNames.put(modeToInjectInto, name);
								Storage.saveCurrentView();
								Gui.setView(new MainMenu());
							}
							else {
								inputWord.takeKeyboard(this);
							}
						}
						
					});
					this.inputWord.setValue("Enter Name");
				}
				else {
					word.setValue(Hub.highscoreNames.get(modeToDisplay));
				}
				addChild(word);
			}
		}
	}
	
	public HighscoreMenu() {
		super();
		addChild(new GraphicEntity("enter_highscores"));
		for(int i=1;i<4;++i){
			String modeToDisplay = "endless";
			if(i>1){
				modeToDisplay = "endless" + i;
			}
			if(Hub.highscores.containsKey(modeToDisplay)&&Hub.highscoreNames.containsKey(modeToDisplay)){
				GraphicNumber num = new GraphicNumber();
				num.setX(0.82f);
				num.setY(sideYs[i-1]);
				num.setValue(Hub.highscores.get(modeToDisplay));
				addChild(num);
				GraphicWord word = new GraphicWord();
				word.setX(0.1f);
				word.setY(sideYs[i-1]);
				word.setValue(Hub.highscoreNames.get(modeToDisplay));
				addChild(word);
			}
		}
		Gui.giveOnType(new KeyBoardListener(){

			@Override
			public void keyCommand(boolean b,char c, int keycode) {
				if(keycode == 28){
					Gui.removeOnType(this);
					Gui.setView(new MainMenu());
				}
			}

			@Override
			public boolean continuousKeyboard() {
				return false;
			}
			
		});
	}
}
