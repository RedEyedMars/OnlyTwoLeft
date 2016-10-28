package com.rem.otl.core.gui;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;

import com.rem.otl.core.editor.TextWriter;
import com.rem.otl.core.game.chat.Chat;
import com.rem.otl.core.gui.graphics.GraphicEntity;
import com.rem.otl.core.gui.graphics.GraphicView;
import com.rem.otl.core.gui.inputs.EventHandler;
import com.rem.otl.core.gui.inputs.InputEvent;
import com.rem.otl.core.gui.inputs.KeyBoardEvent;
import com.rem.otl.core.gui.inputs.KeyBoardListener;
import com.rem.otl.core.gui.inputs.MouseListener;
import com.rem.otl.core.main.Hub;
import com.rem.otl.core.storage.Resource;

/**
 * Use setMaterial(), setLight() and makeTexture() to control light and material properties.
 * <P>
 * napier at potatoland dot org
 */
public interface Gui {

	public void setup();
	
	public void update();
	
	public void cleanup();

	public void run() throws InterruptedException;
	
	public void setView(GraphicView view);

	public void setFinished(boolean b);

	public int createTexture(Resource resource);
	
	public void openWebpage(URL url);


}