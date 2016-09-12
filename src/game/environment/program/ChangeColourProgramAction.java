package game.environment.program;

public class ChangeColourProgramAction extends ProgramAction<Integer> {

	public ChangeColourProgramAction(){
		super();
		setData("subject", 0);
		setData("heroColourToChange", new Boolean(true));//black or white
	}
	@Override
	public void act(Integer newColour) {
		if((Boolean)getData("heroColourToChange")){
			getState().getTarget().changeColour(newColour, null);
		}
		else {
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
	public ChangeColourProgramAction create() {
		return new ChangeColourProgramAction();
	}

}
