package com.rem.otl.core.gui.music;

public interface ITrack {

	public void adjustVolume(float volume);

	public void setHttpLink(String substring);

	public String getHttpLink();
	public String getName();
	public String getArtist();
	public String getFullName();
	public String getFeature();

	public Object getLicense();

	public void setFeature(String substring);

	public void play(float volume);

	public void pause();

	public void skip();

	public void reset();

	public boolean isFinished();





}
