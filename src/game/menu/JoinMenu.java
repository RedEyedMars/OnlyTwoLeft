package game.menu;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import duo.client.Client;
import duo.messages.EndServerMessage;
import duo.messages.RemoveGameMessage;
import editor.ButtonAction;
import editor.Editor;
import editor.TextWriter;
import game.environment.Square;
import gui.Gui;
import gui.graphics.GraphicEntity;
import gui.graphics.GraphicText;
import gui.inputs.KeyBoardListener;
import gui.inputs.MotionEvent;

public class JoinMenu extends Menu {
	private Client client;
	private TextWriter name;
	private TextWriter ip;
	private MenuButton buttonList;
	private List<String[]> gamesList = new ArrayList<String[]>(){
		private static final long serialVersionUID = 6849911565108959389L;
		@Override
		public void clear(){
			super.clear();
		}
		@Override
		public boolean add(String[] game){
			boolean ret = super.add(game);
			if(ret){
				buttonList.changeText("");
			}
			return ret;
		}
	};
	public JoinMenu(List<Square> squares) {
		super();
		this.listenToRelease = true;
		addChild(new GraphicEntity("squares"));
		getChild(0).setFrame(7);

		GraphicEntity nameButton = new MenuButton("Name:"){
			@Override
			public void performOnRelease(MotionEvent e){
				Gui.removeOnType(ip);
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

		name = new TextWriter("impact","Player Two"){
			{
				setWidthFactor(1.4f);
				setHeightFactor(3f);
				adjust(getWidth(), getHeight());
				charIndex=10;
				index=10;
			}
			@Override
			public void keyCommand(boolean b, char c, int keycode){
				if(keycode==14){
					super.keyCommand(b, c, keycode);
				}
				else if(c>=32){
					if(lines.get(0).getChild(lines.get(0).size()-1).getX()<0.9f-0.1f){
						super.keyCommand(b, c, keycode);
					}
				}
			}
		};
		name.setX(0.325f);
		name.setY(0.69f);
		addChild(nameButton);
		addChild(name);
		//Gui.giveOnType(name);


		GraphicEntity ipButton = new MenuButton("IP:"){
			@Override
			public void performOnRelease(MotionEvent e){
				Gui.removeOnType(name);
				Gui.giveOnType(ip);
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
		ipButton.adjust(0.8f, 0.15f);
		ipButton.setX(0.1f);
		ipButton.setY(0.51f);

		ip = new TextWriter("impact"," "){
			{
				setWidthFactor(1.4f);
				setHeightFactor(3f);
				adjust(getWidth(), getHeight());
				charIndex=0;
				index=0;
				ctrlCommands = new HashMap<Integer, ButtonAction>();
				ctrlCommands.put(47, new ButtonAction(){
					@Override
					public void act(Editor subject) {
						Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
						try {
							String string = (String) clip.getContents(null).getTransferData(DataFlavor.stringFlavor);
							string = string.replace("\n", "");
							string = string.replace("\t", "");
							if(string.length()>40){
								string = string.substring(0,40);
							}
							change(string);
							charIndex = string.length();
							index = string.length();
						} catch (UnsupportedFlavorException | IOException e) {
							e.printStackTrace();
						}
					}});
				change("");
			}
			@Override
			public void keyCommand(boolean b, char c, int keycode){
				if(keycode==14||keycode==29||keycode==157){
					super.keyCommand(b, c, keycode);
				}
				else if(ctrling){
					super.keyCommand(b, c, keycode);
				}
				else if(c>=32){
					super.keyCommand(b, c, keycode);
					if(b==false){
						new JoinThread().start();
					}
				}

			}
		};
		ip.setX(0.23f);
		ip.setY(0.53f);
		addChild(ipButton);
		addChild(ip);
		Gui.giveOnType(name);

		buttonList = new MenuButton("stuff"){
			private int topIndex = 0;
			private int botIndex = 1;
			private int index;
			GraphicEntity selectorSquare = new GraphicEntity("squares",1);
			{
				selectorSquare.setFrame(14);
				selectorSquare.setVisible(false);
				children.remove(text);
				addChild(selectorSquare);
				addChild(text);
				text.setWidthFactor(1f);
				text.setHeightFactor(1.4f);

			}
			@Override
			public void performOnRelease(MotionEvent e){
				gamesList.add(new String[]{"newgame","forest1","black"});
				gamesList.add(new String[]{"newgame","forest2","black"});
				gamesList.add(new String[]{"newgame","forest3","black"});
				gamesList.add(new String[]{"newgame","forest4","black"});
			}
			@Override
			public void changeText(String newGame){
				reset();
			}
			public float offsetX(int index){
				return index==4?0.05f:index==3?0.05f:super.offsetX(index);
			}
			public float offsetY(int index){
				return index==4?getHeight()-(botIndex-1)*0.0225f:index==3?getHeight()+0.01f-index*0.0225f:super.offsetY(index);
			}
			@Override
			public void adjust(float x, float y){
				super.adjust(x, y);
				if(selectorSquare!=null){
					selectorSquare.adjust(0.7f,0.025f);
				}
			}
			@Override
			public void onMouseScroll(int dx){
				if(index==3||index==0){
					topIndex+=-dx/120;
					if(topIndex<0){
						topIndex=0;
					}
					else if(topIndex>gamesList.size()){
						topIndex=gamesList.size();
					}
				}
				else {
					index+=-dx/120;
				}
				reset();
			}
			private void reset(){
				StringBuilder builder = new StringBuilder();
				botIndex = gamesList.size();
				if(botIndex>4){
					botIndex=4;
				}
				for(int i=topIndex;i<topIndex+4&&i<gamesList.size();++i){
					builder.append(gamesList.get(i)[0]);
					for(int j=0;j<40-gamesList.get(i)[0].length();++j){
						builder.append(" ");
					}
					builder.append(gamesList.get(i)[1]);
					for(int j=0;j<40-gamesList.get(i)[1].length();++j){
						builder.append(" ");
					}
					builder.append(gamesList.get(i)[2]);
					builder.append("\n");
				}				
				setX(getX());
				setY(getY());
				System.out.println(builder.toString()+"------");
				selectorSquare.setVisible(botIndex!=0);
				super.changeText(builder.toString());
			}
		};
		buttonList.adjust(0.8f, 0.3f);
		buttonList.setX(0.1f);
		buttonList.setY(0.19f);
		addChild(buttonList);

		GraphicEntity button = new MenuButton("Return"){
			@Override
			public void performOnRelease(MotionEvent e){
				returnToMain();
			}
		};
		button.setX(0.2f);
		button.setY(0.03f);
		addChild(button);

		for(Square square:squares){
			addChild(square);
		}
		this.squares = squares;

	}

	public void returnToMain(){
		Client.endConnection();
		Gui.setView(new DuoMenu(squares));
	}

	private class JoinThread extends Thread {
		public JoinThread(){
			super();
		}
		@Override
		public void run(){
			try{
				client = new Client(ip.getText(),name.getText()){
					{
						games = gamesList;
					}
				};
				client.run();
			}
			catch(IOException e){
				client.close();
			}

		}
	}

}
