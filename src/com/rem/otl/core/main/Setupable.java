package com.rem.otl.core.main;

import com.rem.otl.core.gui.graphics.GraphicView;

public interface Setupable {

	public void setup();

	public void cleanup();
	
	public GraphicView getFirstView();

}
