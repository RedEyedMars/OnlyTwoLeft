package gui.music;
import java.io.File;
import java.io.IOException;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.SystemMenuBar;

import gui.graphics.R;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.FactoryRegistry;
import javazoom.jl.player.JavaSoundAudioDevice;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;
public class Track extends PlaybackListener implements Runnable {

	private String copyright;
	private String name;
	private String artist;
	private String artistAndName;
	private String mp3Name;
	private String httpLink = null;

	private String filePath;

	private AdvancedPlayer player;
	private Thread playerThread;
	private int pausedAt = 0;
	private boolean isFinished = false;
	private AudioDevice audio;
	private JavaSoundAudioDevice volumeAdjustableAudio;
	private float volume;
	private boolean pausing;
	private String feature;

	public Track(String mp3, String name, String artist, String copyright) {
		super();
		this.mp3Name = mp3;
		this.name = name;
		this.artist = artist;
		this.artistAndName = name+" - "+artist;
		this.copyright = copyright;

		this.filePath = "res/music/"+mp3Name;

		
	}

	public void play(float volume) throws JavaLayerException, IOException {
		File file = new File(this.filePath);
		if(!file.exists()){
			this.isFinished = true;
			return;
		}
		else {
			this.isFinished = false;
		}
		this.volume = volume;
		String urlAsString = 
            "file:///" 
            + file.getCanonicalPath(); 

		AudioDevice audio = FactoryRegistry.systemRegistry().createAudioDevice();

		if (audio instanceof JavaSoundAudioDevice)
		{
			volumeAdjustableAudio = (JavaSoundAudioDevice)audio;
		}
        this.player = new AdvancedPlayer(new java.net.URL(urlAsString).openStream(),audio);
        
		this.player.setPlayBackListener(this);

		this.playerThread = new Thread(this);

		this.playerThread.start();
	}

	public void pause(){
		pausing = true;
		player.stop();
	}
	public void skip(){
		pausing = true;
		player.stop();
		pausedAt = 0;
	}
	public void reset(){
		pausedAt = 0;
	}
	
	public void playbackStarted(PlaybackEvent playbackEvent) {
		adjustVolume(volume);
	}

	public void playbackFinished(PlaybackEvent playbackEvent) {
		pausedAt  = playbackEvent.getFrame();
		if(!pausing){
			isFinished  = true;
		}
		else {
			pausing = false;
		}
		
	}    

	public void run() {
		try
		{			
			this.player.play(pausedAt/10,Integer.MAX_VALUE);
		}
		catch (javazoom.jl.decoder.JavaLayerException ex)
		{
			ex.printStackTrace();
		}

	}

	public String getName() {
		return name;
	}

	public void setHttpLink(String link) {
		this.httpLink = link;
	}

	public String getHttpLink() {
		return this.httpLink;
	}
	public void setFeature(String ft) {
		this.feature = ft;
	}

	public String getFeature() {
		return this.feature;
	}

	public boolean isFinished() {
		return this.isFinished;
	}
	
	public void adjustVolume(float volume){
		if(volumeAdjustableAudio!=null){
			volumeAdjustableAudio.setVolume(volume);
			this.volume = volume;
		}
	}

	public String getLicense() {
		return copyright;
	}

	public String getArtist() {		
		return artist;
	}

	public String getFullName(){
		return artistAndName;
	}
}