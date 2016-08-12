package editor;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.Map;
import game.environment.OnCreateAction;
import game.environment.OnCreateSquare;
import game.environment.Square;
import game.menu.MainMenu;
import gui.Gui;
import gui.graphics.GraphicEntity;
import gui.graphics.GraphicText;
import gui.graphics.GraphicView;
import gui.inputs.KeyBoardListener;
import gui.inputs.MotionEvent;
import main.Hub;
import storage.Storage;

public class OnCreateSquareEditor extends Editor{

	private File saveTo = null;
	private MapEditor editor;
	private float square_x;
	private float square_y;
	private float square_w;
	private float square_h;

	private TextWriter writer;

	public OnCreateSquareEditor(MapEditor parent,float x, float y, float w, float h){
		super();

		setupButtons();
		Square guide2 = new Square(15,0,w,h);
		guide2.setX(x);
		guide2.setY(y);
		addChild(guide2);
		String text = "";
		saveTo = Gui.userSave("ocs");
		if(saveTo!=null){
			if(saveTo.exists()){
				text = Storage.loadText(saveTo.getAbsolutePath());
			}
			squares = Hub.map.getTemplateSquares();
			for(Square square:squares){
				for(int i=0;i<square.size();++i){
					if(!(square.getChild(i) instanceof Square)){
						square.removeChild(i);
						--i;
					}
					else {
						addIconsToSquare((Square) square.getChild(i));
					}
				}
				addIconsToSquare(square);
				addChild(square);
			}
		}

		this.editor = parent;
		this.square_x = x;
		this.square_y = y;
		this.square_w = w;
		this.square_h = h;

		Map<Integer,ButtonAction> ctrlCommands = new HashMap<Integer,ButtonAction>();
		ctrlCommands.put(45, new ButtonAction(){
			@Override
			public void act(Editor subject) {
				saveAndReturnToEditor();
			}});
		ctrlCommands.put(57, new ButtonAction(){
			@Override
			public void act(Editor subject) {
				if(visibleTo==0){
					visibleTo = 1;
					visibleToShower.setFrame(0);
				}
				else if(visibleTo==1){
					visibleTo = 2;
					visibleToShower.setFrame(1);
				}
				else if(visibleTo==2){
					visibleTo = 0;
					visibleToShower.setFrame(3);
				}
				setVisibleSquares(visibleTo);
			}
		});
		ctrlCommands.put(17,  new ButtonAction(){
			@Override
			public void act(Editor subject){
				moveView(0,-0.25f);
			}
		});
		ctrlCommands.put(30, new ButtonAction(){
			@Override
			public void act(Editor subject){
				moveView(0.25f,0);
			
			}
		});
		ctrlCommands.put(31, new ButtonAction(){
			@Override
			public void act(Editor subject){
				moveView(0,0.25f);			
			}
		});
		ctrlCommands.put(32, new ButtonAction(){
			@Override
			public void act(Editor subject){
				moveView(-0.25f,0);
			}
		});
		writer = new TextWriter(this,text,ctrlCommands);
		addChild(writer);
		mode = -1;
	}
	@Override
	public void addIconsToSquare(Square square1){
		super.addIconsToSquare(square1);
		GraphicEntity e = new GraphicText("impact",""+squares.indexOf(square1),0);
		e.setX(square1.getX());
		e.setY(square1.getY()+square1.getHeight()-0.03f);
		square1.addChild(e);
	}
	@Override
	public KeyBoardListener getDefaultKeyBoardListener(){
		return writer;
	}
	@Override
	public void update(double seconds){
		if(saveTo==null){
			if(editor==null){
				Gui.setView(new MainMenu());
			}
			else {
				editor.restartWith(null);
				Gui.setView(editor);
			}
		}
		super.update(seconds);
	}
	private void saveAndReturnToEditor() {
		Storage.saveText(saveTo.getAbsolutePath(), writer.getText());
		List<OnCreateAction> actions = new ArrayList<OnCreateAction>();
		for(String line:writer.getLines()){
			OnCreateAction action = createFromString(line);
			if(action!=null){
				actions.add(action);
			}
		}

		final List<Integer> ints = new ArrayList<Integer>();
		final List<Float> floats = new ArrayList<Float>();
		List<Object> probe = new ArrayList<Object>(){
			@Override
			public boolean add(Object obj){
				if(obj instanceof Integer){
					return ints.add((Integer) obj);
				}
				else if(obj instanceof Float){
					return floats.add((Float) obj);
				}
				return false;
			}
		};
		ints.add(Hub.map.getIntX(square_x));
		ints.add(Hub.map.getIntY(square_y));
		ints.add(Hub.map.getIntX(square_w));
		ints.add(Hub.map.getIntX(square_h));
		int numberOfOffsets = 0;
		for(OnCreateAction action:actions){
			if(action.getIndex()==8){
				++numberOfOffsets;
			}
		}
		ints.add(actions.size()-numberOfOffsets);
		for(OnCreateAction action:actions){
			action.saveTo(probe);
		}
		OnCreateSquare square = new OnCreateSquare(0,-1,-1,ints.iterator(),floats.iterator());
		if(editor==null){
			for(GraphicEntity child:square.getChildren()){
				child.onAddToDrawable();
			}
		}
		else {
			while(!square.getChildren().isEmpty()){
				square.removeChild(0);
			}
			editor.restartWith(square);
			Gui.setView(editor);			
		}
	}

	public OnCreateAction createFromString(String toParse){
		String[] split = toParse.trim().split(":");
		String name = split[0];
		List<Integer> ints = new ArrayList<Integer>();
		List<Float> floats = new ArrayList<Float>();
		if(split.length>1){
			String[] args = split[1].split(" ");		
			for(String arg:args){
				try {
					ints.add(Integer.parseInt(arg));
				}
				catch(NumberFormatException ie){
					try {
						floats.add(Float.parseFloat(arg));
					}
					catch(NumberFormatException fe){
						if(arg.startsWith("#")){
							if(arg.contains("-")){
								String[] both = arg.substring(1).split("-");
								Integer first = Integer.parseInt(both[0]);
								Integer last = Integer.parseInt(both[1]);
								ints.add(new Integer(last-first+1));
								for(int i=first;i<last+1;++i){
									ints.add(i);
								}
							}
							else {
								ints.add(Integer.parseInt(arg.substring(1)));
							}
						}
					}
				}
			}
		}
		if(toParse.length()>0){
			OnCreateAction action = OnCreateAction.actions.get(name).create();
			action.setArgs(ints.iterator(), floats.iterator());
			return action;
		}
		else return null;
	}
	private void moveView(float x, float y){
		for(int i=0;i<squares.size();++i){
			squares.get(i).setX(squares.get(i).getX()+x);
			squares.get(i).setY(squares.get(i).getY()+y);
		}
	}

}
