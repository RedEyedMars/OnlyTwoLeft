package com.rem.otl.core.game.environment.program.action;

import com.rem.otl.core.game.environment.program.ProgrammableSquare;
import com.rem.otl.core.game.environment.update.NullUpdateAction;
import com.rem.otl.core.game.environment.update.UpdateAction;

public class DefineVariableProgramAction extends ProgramAction<ProgrammableSquare>{

	public DefineVariableProgramAction(){
		super();
		setData("Name", "var");
		setData("Type", "int");
		setData("Value", "0");
	}
	@Override
	public int targetType() {
		return 0;
	}

	@Override
	public int getIndex() {
		return 3;
	}

	@Override
	public void act(ProgrammableSquare subject) {
		Object value = null;
		if("int".equals(getData("Type"))){
			value = Integer.parseInt((String)getData("Value"));
		}
		else if("float".equals(getData("Type"))){
			value = Float.parseFloat((String)getData("Value"));
		}
		else if("bool".equals(getData("Type"))){
			value = Boolean.parseBoolean(((String)getData("Value")).toLowerCase());
		}
		else if("string".equals(getData("Type"))){
			value = getData("Value");
		}
		subject.setVariable((String)getData("Name"),value);
	}

	@Override
	public ProgramAction create() {
		return new DefineVariableProgramAction();
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
