package game.environment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import editor.Button;
import editor.Editor;
import game.Hero;
import gui.Gui;
import gui.inputs.MotionEvent;
import gui.inputs.MouseListener;
import game.Action;

public abstract class UpdateAction implements Action<Double>{
	public static List<UpdateAction> actions = new ArrayList<UpdateAction>();
	public static List<String> actionNames = new ArrayList<String>();

	public void setSelf(UpdatableSquare self){
		this.self = self;
	}
	protected UpdatableSquare self;

	public static final UpdateAction grow = new UpdateAction(){
		@Override
		public void act(Double seconds) {
			self.adjust((float) (self.getWidth()+data.get(0)*seconds), (float) (self.getHeight()+data.get(1)*seconds));
		}
		@Override
		public int numberOfFloats(){
			return 2;
		}
	};

	protected ArrayList<Float> data = new ArrayList<Float>(){
		@Override
		public boolean add(Float obj){
			if(this.size()>=numberOfFloats()){
				this.clear();
			}
			System.out.println(obj);
			return super.add(obj);
		}
	};
	public int numberOfFloats(){
		return 0;
	}
	public void setFloats(Iterator<Float> floats){
		for(int i=0;i<numberOfFloats();++i){
			data.add(floats.next());
		}
	}
	public float getFloat(int i){
		return data.get(i);
	}
	public void saveTo(List<Object> saveTo){
		for(Float flt:data){
			System.out.println("SAVE"+flt);
			saveTo.add(flt);			
		}
	}
	static {
		try {
			for(Field field:UpdateAction.class.getFields()){
				Object obj = field.get(UpdateAction.class);
				if(obj instanceof UpdateAction){
					//System.out.println(field.getName());
					actions.add((UpdateAction) obj);
					actionNames.add(field.getName());
				}
			} 
		}
		catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	public MouseListener getEditor(final int id, final float x,final float y,final float dx,final float dy, final Editor editor) {
		return new MouseListener(){
			Button<Editor> button;
			MouseListener self = this;
			private boolean pop = false;
			{				
				button = new Button<Editor>("editor_update_icons",id+1,editor,new Action<Editor>(){
					@Override
					public void act(Editor subject) {
						editor.setMode(-1);
						pop = true;
						Gui.giveOnClick(self);
					}
				});
				button.setX(x+dx);
				button.setY(y+dy);
				button.adjust(0.05f, 0.05f);
				editor.addButtonToLastSquare(button);				
			}
			@Override
			public boolean onClick(MotionEvent event) {
				if(!pop){
					data.add(event.getX()-x);				
					if(data.size()<numberOfFloats()){
						data.add(event.getY()-y);
					}
					Gui.removeOnClick(this);
				}
				else if(event.getAction()==MotionEvent.ACTION_UP){
					pop=false;
				}
				return false;
			}

			@Override
			public boolean onHover(MotionEvent event) {
				button.setX(event.getX());
				button.setY(event.getY());
				return false;
			}

			@Override
			public void onMouseScroll(int distance) {

			}};
	}
}
