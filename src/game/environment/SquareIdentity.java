package game.environment;

import java.util.Iterator;
import java.util.List;

public class SquareIdentity {
	private SquareAction[] actions;
	private String name;
	private byte colour;
	public SquareIdentity(String name, byte colour, SquareAction... actions){
		this.name = name;
		this.colour = colour;
		this.actions = actions;
	}
	
	public Square create(int visibleTo, int bufferSize, Iterator<Float> buffer){
		if(actions.length==0){
			return new Square(this,visibleTo,bufferSize,buffer);
		}
		else if(actions.length==1){
			return new FunctionalSquare(this,visibleTo,bufferSize,buffer,actions[0]);
		}
		else if(actions.length==2){
			return new FunctionalSquare(this,visibleTo,bufferSize,buffer,actions[0],actions[1]);
		}
		else return null;
	}
	
	public byte colour(){
		return colour;
	}

	public void saveTo(List<Object> toSave) {
		toSave.add(name);
	}
}
