package game.environment;

import java.util.Iterator;
import java.util.List;

import game.Action;
import main.Hub;

public class SquareIdentity {
	private Action[] actions;
	private String name;
	private int colour;
	public SquareIdentity(String name, Action... actions){
		this.name = name;
		this.actions = actions;
	}

	public Square create(Iterator<Integer> ints, Iterator<Float> buffer){
		this.colour = ints.next();
		FunctionalSquare square = null;
		if(actions.length==0){
			return new Square(this,ints.next(),ints.next(),buffer);
		}
		else if(actions.length==1){
			square = new FunctionalSquare(this,ints.next(),ints.next(),buffer,(SquareAction)actions[0]);
		}
		else if(actions.length==2){
			if(actions[1] instanceof SquareAction){
				square = new FunctionalSquare(this,ints.next(),ints.next(),buffer,(SquareAction)actions[0],(SquareAction)actions[1]);
			}
			else if(actions[1] instanceof UpdateAction){
				square = new UpdatableSquare(this,ints.next(),ints.next(),buffer,(SquareAction)actions[0],(UpdateAction)actions[1]);
			}
		}
		else if(actions.length==3){
			square = new UpdatableSquare(this,ints.next(),ints.next(),buffer,(SquareAction)actions[0],(SquareAction)actions[1],(UpdateAction)actions[2]);
		}
		if(((SquareAction)actions[0]).numberOfTargets()>0){
			square.setTarget(Hub.map.getSquares().get(ints.next()));
		}
		else if(actions.length>1){
			for(int i=1;i<actions.length;++i){
				if(actions[1] instanceof SquareAction&&((SquareAction)actions[1]).numberOfTargets()>0){
					square.setTarget(Hub.map.getSquares().get(ints.next()));
					break;
				}
			}
		}

		return square;
	}

	public void saveTo(List<Object> toSave) {
		toSave.add(name);
	}

	public int colour() {
		return colour;
	}

	public String getName() {
		return name;
	}
}
