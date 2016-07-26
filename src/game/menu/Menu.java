package game.menu;

import java.util.List;

import game.environment.Square;
import gui.graphics.GraphicEntity;
import gui.graphics.GraphicView;
import gui.inputs.MotionEvent;

public class Menu extends GraphicView{
	protected List<Square> squares;
	protected double since = 0;
	private float mouseX = 0;
	private float mouseY = 0;
	
	public Menu(){
		super();		
		this.listenToRelease = true;
		addChild(new GraphicEntity("squares"));
		getChild(0).setFrame(15);
	}
	@Override
	public boolean onHover(MotionEvent e){
		mouseX = e.getX();
		mouseY = e.getY();
		return true;
	}
	@Override
	public void update(double seconds){
		since+=seconds;
		if(since>0.1f){
			since-=0.1f;
			float w = (float) (0.4*Math.random());
			squares.add(new Square((int)(16*Math.random()),w,w));
			squares.get(squares.size()-1).setY(1f+1.5f*(float)Math.random());
			squares.get(squares.size()-1).setX((float) (-0.2f+1.4*Math.random()));
			addChild(squares.get(squares.size()-1));
			squares.get(squares.size()-1).onAddToDrawable();
			squares.get(squares.size()-1).getGraphicElement().rotate((float)(Math.random()-0.5f));
		}
		for(int i=0;i<squares.size();++i){
			Square square = squares.get(i);
			square.getGraphicElement().rotate((float) (square.getGraphicElement().getAngle()+Math.signum(square.getGraphicElement().getAngle())*seconds*10f/square.getWidth()));
			if(square.getY()>1f){
			//	square.setX((float) (square.getX()+(mouseX-square.getX())/2f*seconds));
			}
			square.setY((float) (square.getY()-0.1f*seconds));
			if(square.getY()<-1f){
				removeChild(square);
				squares.remove(square);
				--i;
			}
		}		
	}	
}
