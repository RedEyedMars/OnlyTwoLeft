package com.rem.otl.core.main;

import java.io.InputStream;
import java.io.OutputStream;

import com.rem.otl.core.gui.Gui;
import com.rem.otl.core.gui.graphics.GraphicElement;
import com.rem.otl.core.gui.graphics.GraphicRenderer;
import com.rem.otl.core.gui.graphics.GraphicView;
import com.rem.otl.core.gui.music.ITrack;
import com.rem.otl.core.gui.music.MusicPlayer;
import com.rem.otl.core.storage.Resource;

public interface ICreator {

	public Gui createGui(Setupable setupable);
	public GraphicRenderer createGraphicRenderer(Setupable main);
	public ILog createLog();
	public MusicPlayer createMusic();
	public GraphicElement createGraphicElement(String textureName, GraphicView view);
	public GraphicElement createGraphicLine(String string, GraphicView view);
	
	public ITrack createTrack(String substring, String string, String string2, String currentLicense);
	
	public int getPlainFontStyle();
	public void copyToClipboard(String copyTo);
	public String copyFromClipboard();
	
	public IFileManager createFileManager(Setupable main);
}
