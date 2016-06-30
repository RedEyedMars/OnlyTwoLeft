package game.menu;

import java.util.ArrayList;
import java.util.List;

import game.Action;
import gui.Gui;
import gui.graphics.GraphicEntity;
import gui.inputs.KeyBoardListener;

public class GraphicWord extends GraphicEntity implements KeyBoardListener{

	private String word = "";
	private Action onEnterPressed;
	private boolean firstPress = true;
	private boolean newLinesAccepted = false;
	private int characterLimit = 15;
	private List<Integer> newLines = new ArrayList<Integer>();
	
	public GraphicWord() {
		super("letters");
		this.setVisible(false);
	}
	
	public GraphicWord(int characterLimit, boolean newlines) {
		this();
		this.newLinesAccepted = newlines;
		this.characterLimit = characterLimit;
	}

	@Override
	public float offsetX(int index){
		int factor = index;
		for(int i=0;i<newLines.size()-1;++i){
			if(newLines.get(i)<index&&newLines.get(i+1)>index){
				factor-=newLines.get(i)+1;
				break;
			}
		}
		if(newLines.size()>0&&newLines.get(newLines.size()-1)<index){
			factor-=newLines.get(newLines.size()-1)+1;
		}
		return (factor)*0.031f;
	}
	
	@Override
	public float offsetY(int index){
		int factor = 0;
		for(int i=0;i<newLines.size();++i){
			if(index>newLines.get(i)){
				++factor;
			}
			else break;
		}
		return -factor*0.031f;
	}
	
	private class Letter extends GraphicEntity {

		private int position;

		public Letter(int position) {
			super("letters");
			this.position = position;
		}
		
		public void update(){
			if(position>=word.length()||word.charAt(position)==' '||word.charAt(position)=='\n'){
				this.setVisible(false);
			}
			else {
				this.setVisible(true);
				this.setFrame(((int)word.charAt(position))-97);
			}
		}
	}

	public void setValue(String w) {
		this.word = w.toLowerCase();
		while(this.word.length()>this.children.size()&&this.children.size()<characterLimit){
			this.addChild(new Letter(this.children.size()));
			this.getChild(this.children.size()-1).adjust(0.05f, 0.05f);			
		}
		newLines .clear();
		for(int i=0;i<word.length();++i){
			if(this.word.charAt(i)=='\n'){
				newLines.add(i);
			}
		}
		this.setX(this.getX());
		this.setY(this.getY());
	}

	public String getValue() {
		return word;
	}

	public void dec() {
		if(word.length()>0){
			this.word = word.substring(0,this.word.length()-1);
		}
	}
	
	public void takeKeyboard(Action finish){
		this.onEnterPressed = finish;
		Gui.giveOnType(this);
	}
	
	private void onEnterPressed(){
		Gui.removeOnType(this);
		this.onEnterPressed.act(word);
	}
	
	public boolean continuousKeyboard(){
		return false;
	}

	@Override
	public void keyCommand(boolean b, char c, int keycode) {
		if(firstPress ){
			firstPress = false;
			setValue("");
		}
		if(keycode == 14){
			dec();
		}
		else if(keycode == 28){
			onEnterPressed();
		}//16-25, 30-38, 44-5
		else if((keycode>=16&&keycode<=25)||
				(keycode>=30&&keycode<=38)||
				(keycode>=44&&keycode<=50)){
			setValue(word + c);
		}
	}

}
