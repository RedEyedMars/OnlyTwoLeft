package com.rem.otl.core.game.environment.program.action;

import com.rem.otl.core.game.environment.program.ProgrammableSquare;
import com.rem.otl.core.game.environment.program.Variable;
import com.rem.otl.core.game.environment.program.VariableListener;
import com.rem.otl.core.game.environment.update.NullUpdateAction;
import com.rem.otl.core.game.environment.update.UpdateAction;
import com.rem.otl.core.gui.graphics.GraphicEntity;
import com.rem.otl.core.main.Hub;

public class DisplayImageProgramAction extends ProgramAction<ProgrammableSquare>{

	public DisplayImageProgramAction(){
		super();
		setData("File", "none");
		setData("Frame", "0");
		setData("Dimensions", "1x1");
		setData("X",0f);
		setData("Y",0f);
		setData("Behaviour","free");
	}
	@Override
	public int targetType() {
		return 0;
	}

	@Override
	public int getIndex() {
		return 5;
	}

	@Override
	public void act(ProgrammableSquare subject) {
		if("none".equals((String)getData("File")))return;
		else
		if("free".equals((String)getData("Behaviour"))){
			Hub.renderer.prepareCustomLoader((String)getData("File"),(String)getData("Dimensions"));
			final GraphicEntity image = new GraphicEntity((String)getData("File"),Hub.MID_LAYER);
			setFrame(image,subject);
			image.reposition((Float)getData("X"), (Float)getData("Y"));
			image.resize(0.05f, 0.05f);
			subject.addIndependantImage(image);
		}
		else if("overlay".equals((String)getData("Behaviour"))){
			Hub.renderer.prepareCustomLoader((String)getData("File"),(String)getData("Dimensions"));
			final GraphicEntity image = new GraphicEntity((String)getData("File"),Hub.MID_LAYER);
			setFrame(image,subject);
			subject.addOverlayImage(image);
		}
	}

	private void setFrame(final GraphicEntity image, ProgrammableSquare subject) {
		try {
			image.setFrame(Integer.parseInt((String)getData("Frame")));
		}
		catch(NumberFormatException e){
			final Variable var = subject.getVariable((String)getData("Frame"));
			image.setFrame((Integer)var.getValue());
			var.addListener(new VariableListener(){
				@Override
				public void onVariableChange(String name, Object value) {
					int newFrame = (Integer)var.getValue();
					int limit = Hub.renderer.getFrameLimit(image.getTextureName());
					if(newFrame>=limit){
						var.setValue(0);
					}
					else if(newFrame<0){
						var.setValue(limit-1);
					}
					image.setFrame((Integer)var.getValue());
				}});
		}
	}
	@Override
	public ProgramAction create() {
		return new DisplayImageProgramAction();
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
