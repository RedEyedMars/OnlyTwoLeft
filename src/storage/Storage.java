package storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import game.environment.Square;
import main.Hub;

public class Storage {
	
	public static String loadMap(String filename){
		byte[] file = readVerbatum(filename);
		String mapName = getMapNameFromFileName(filename);
		loadMap(mapName,filename,file);
		return mapName;
	}
	public static String getMapNameFromFileName(String filename){
		if(filename.trim().equals("Restart")){
			return filename;
		}
		int lastSeparator = filename.lastIndexOf(File.separatorChar);
		if(lastSeparator==-1){
			lastSeparator = filename.lastIndexOf('/');
			if(lastSeparator==-1){
				lastSeparator=0;
			}
			else {
				++lastSeparator;
			}
		}
		else {
			++lastSeparator;
		}
		int dot = filename.lastIndexOf('.');
		return filename.substring(lastSeparator, dot);
	}
	
	public static void loadMap(String name,String filename, byte[] file){
		int index = 0;
		for(;(char)file[index]!='\n';++index);
		Map<Integer,String> strings = loadStringMap(file);
		Object[] loaded = Decoder.decode(file, index+1, strings,3,0,0);
		loaded = Decoder.decode(file, index+1, strings,(Integer)loaded[0],(Integer)loaded[1],(Integer)loaded[2]);
		game.environment.Map.load(name,filename,loaded);
	}

	private static Map<Integer,String> loadStringMap(byte[] file) {
		Map<Integer,String> strings = new HashMap<Integer,String>();
		if(file[0]=='\n')return strings;
		int next = 0;		
		for(int i=1;i<file.length;i=next+1){
			next=i;
			while(file[next]!='\t'&&file[next]!='\n'){
				++next;
			}
			strings.put(strings.size(), readStringFromBytes(file,i,next));
			if(file[next]=='\n'){				
				return strings;
			}
		}
		return strings;
	}
	
	private static String readStringFromBytes(byte[] file, int start, int end){
		StringBuilder builder = new StringBuilder();
		for(;start<end;++start){
			builder.append(((char)(byte)file[start]));
		}
		return builder.toString();
	}

	public static byte[] readVerbatum(String filename){
		if(filename.trim().equals("Restart")){
			if(Hub.map.getFileName()==null){
				filename=null;
			}
			else {
				filename = Hub.map.getFileName();
			}
		}
		List<Byte> builder = new ArrayList<Byte>();
		try {
			FileInputStream reader = new FileInputStream(filename);
			int next = reader.read();
			while(((int)next)>=0){
				builder.add((byte)next);
				next = reader.read();
			}
			reader.close();
		}
		catch (IOException e){
			e.printStackTrace();
		}
		byte[] bytes = new byte[builder.size()];
		for(int i=0;i<builder.size();++i){
			bytes[i]=builder.get(i);
		}
		return bytes;
	}
	


	public static String loadText(String filename) {
		StringBuilder builder = new StringBuilder();
		for(Byte b:readVerbatum(filename)){
			builder.append((char)(int)b);
		}
		return builder.toString();
	}

	public static void saveText(String filename, String text) {
		createFolder("data");
		FileOutputStream writer = null;
		try {
			writer = new FileOutputStream(filename,false);
			byte[] bytes = new byte[text.length()];
			char[] chars = text.toCharArray();
			for(int i=0;i<chars.length;++i){
				bytes[i] = (byte) chars[i];
			}
			writer.write(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(writer!=null){
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void saveMap(String filename, game.environment.Map map) {
		List<Object> toSave = new ArrayList<Object>(){
			private static final long serialVersionUID = -7081569172132958341L;
			private Integer ints = 0;
			private Integer floats = 0;
			private Integer strings = 0;
			{
				add(new Integer(0));
				add(new Integer(0));
				add(new Integer(0));				
			}
			@Override
			public boolean add(Object o){
				if(o instanceof Integer){
					++ints;
				}
				else if(o instanceof Float){
					++floats;
				}
				else if(o instanceof String){
					++strings;
				}
				return super.add(o);
			}
			@Override
			public Object[] toArray(Object[] o){
				Object[] ret = super.toArray(o);
				ret[0] = ints;
				ret[1] = floats;
				ret[2] = strings;
				return ret;
			}
		};
		map.saveTo(toSave);		
		save(filename,toSave.toArray(new Object[0]));
	}
	public static void save(String filename, Object... toSave) {
		createFolder("data");
		FileOutputStream writer = null;
		try {
			writer = new FileOutputStream(filename,false);
			List<Byte> build = new ArrayList<Byte>();
			Map<String,Integer> strings = new HashMap<String,Integer>();
			Encoder.encode(strings,build,toSave);
			int index = 0;
			for(int i=0;i<strings.size();++i){
				build.add(index++,(byte) '\t');
				for(String key:strings.keySet()){
					if(strings.get(key)==i){
						for(char b:key.toCharArray()){
							build.add(index++,(byte)b);
						}
					}
				}
			}
			build.add(index,(byte)'\n');
			byte[] bytes = new byte[build.size()];
			for(int i=0;i<build.size();++i){
				bytes[i] = build.get(i);
			}
			writer.write(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(writer!=null){
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void createFolder(String folderName){
		StringBuilder path = new StringBuilder();
		path.append("./");
		path.append(folderName);
		File f = new File(path.toString());
		if(!f.exists()){
			f.mkdirs();
		}
	}
}
