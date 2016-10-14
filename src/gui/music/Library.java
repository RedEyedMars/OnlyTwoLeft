package gui.music;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import game.environment.onstep.OnStepAction;
import javazoom.jl.decoder.JavaLayerException;

public class Library {
	/*
	public static Track living_nightmare = new Track("living_nightmare.mp3",
			"Living Nightmare - snowflake",
			"Living Nightmare by snowflake (c) copyright 2016 Licensed under a Creative Commons Attribution (3.0) license. http://dig.ccmixter.org/files/snowflake/54422 Ft: Blue Wave Theory");*/
	public static final List<Track> tracks = new ArrayList<Track>();

	static {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File("res"+File.separatorChar+"music"+File.separatorChar+"catalogue.data")));

			String currentLicense = "";
			String currentTrackName = "";
			Track currentTrack = null;
			String line = reader.readLine();
			while(line!=null){
				if(line.startsWith("\t\t")){
					if(currentTrack!=null){
						currentTrack.setHttpLink(line.substring(2));
					}
					else {
						currentTrack = new Track(
								line.substring(2),
								currentTrackName.replaceFirst(" by "," - "),
								currentTrackName+" "+currentLicense);
						tracks.add(currentTrack);
					}
				}
				else if(line.startsWith("\t")){
					currentTrack = null;
					currentTrackName = line.substring(1);
				}
				else {
					currentLicense = line;
				}
				line = reader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
