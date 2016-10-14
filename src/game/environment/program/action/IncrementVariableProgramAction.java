package game.environment.program.action;

import game.environment.program.ProgrammableSquare;
import game.environment.program.Variable;
import game.environment.update.NullUpdateAction;
import game.environment.update.UpdateAction;

public class IncrementVariableProgramAction extends ProgramAction<ProgrammableSquare>{

	public IncrementVariableProgramAction(){
		super();
		setData("Name", "var");
	}
	@Override
	public int targetType() {
		return 0;
	}

	@Override
	public int getIndex() {
		return 4;
	}

	@Override
	public void act(ProgrammableSquare subject) {
		Variable var = subject.getVariable((String)getData("Name"));
		if(var.is(Variable.INTEGER)){
			var.setValue(((Integer)var.getValue())+1);
		}
		else if(var.is(Variable.FLOAT)){
			var.setValue(((Float)var.getValue())+1f);
		}
	}

	@Override
	public ProgramAction create() {
		return new IncrementVariableProgramAction();
	}
	@Override
	public String[] copiableIntTextureNames() {
		return new String[]{};
	}
	@Override
	public int[] copiableIntTextureRanges(){
		return new int[]{};
	}

}
