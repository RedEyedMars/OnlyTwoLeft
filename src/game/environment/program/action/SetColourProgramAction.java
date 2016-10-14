package game.environment.program.action;

import editor.program.Settable;
import game.hero.Hero;

public class SetColourProgramAction extends ProgramAction<Integer> {

	public SetColourProgramAction(){
		super();
		setData("subject", 0);
		setData("heroColourToChange", new Integer(Hero.BLACK_INT));//black or white
	}
	@Override
	public void act(Integer newColour) {
		if((Integer)getData("heroColourToChange")==Hero.BLACK_INT){
			getState().getTarget().changeColour(newColour, null);
		}
		else if((Integer)getData("heroColourToChange")==Hero.WHITE_INT){
			getState().getTarget().changeColour(null,newColour);
		}
	}
	@Override
	public int targetType() {
		return 4;
	}
	@Override
	public int getIndex() {
		return 1;
	}
	@Override
	public SetColourProgramAction create() {
		return new SetColourProgramAction();
	}
	@Override
	public String[] copiableIntTextureNames() {
		return new String[]{"squares","editor_circles"};
	}
	@Override
	public int[] copiableIntTextureRanges() {
		return new int[]{-1,16,0,2};
	}

}
