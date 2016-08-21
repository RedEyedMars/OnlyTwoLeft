package storage;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Encoder {
	public static void encode(Map<String,Integer> strings, List<Byte> build, Object... objs){
		for(int i=0;i<objs.length;++i){
			if(objs[i] instanceof Integer){
				encodeInteger((Integer) objs[i],build);
			}				
		}
		for(int i=0;i<objs.length;++i){
			if(objs[i] instanceof Float){
				encodeFloat((Float)objs[i],build);
			}
		}
		for(int i=0;i<objs.length;++i){
			if(objs[i] instanceof String){
				encodeString(objs[i].toString(),strings,build);				
			}
		}
	}

	public static void encodeInteger(Integer i, List<Byte> build){
		String ret = "";
		int c = 0;
		int f = 1;
		int mod = 64;
		int negmod = 64;
		if(i<0){
			negmod = 0;
			i=-i;
		}
		List<Byte> temp = new ArrayList<Byte>();
		do{
			c=(i/f)%mod;
			temp.add(0,(byte) ((char)(mod==64?(int)(c-128+negmod):c)));
			f*=mod;
			mod=128;
		} while(i>f);
		build.addAll(temp);
	}

	public static void encodeFloat(Float f, List<Byte> build){
		byte[] bytes = ByteBuffer.allocate(4).putFloat(f).array();
		for(int i=0;i<4;++i){
			build.add(bytes[i]);
		}

	}

	public static void encodeString(String string, Map<String,Integer> strings, List<Byte> build){
		if(!strings.containsKey(string)){
			strings.put(string, strings.size());
		}
		encodeInteger(strings.get(string),build);
	}

	public static void encodeBooleans(Boolean[] bools,List<Byte> build){
		Byte[] bytes = new Byte[bools.length/7+1];
		for(int i=0;i<bytes.length;++i){
			bytes[i]=0;
		}
		for(int arrayIndex = 0, boolIndex = 0, factor=1;boolIndex<bools.length;++boolIndex){
			bytes[arrayIndex] = (byte) (bytes[arrayIndex] + (bools[boolIndex]?(byte)factor:(byte)0));
			if(boolIndex>0&&boolIndex%6==0){
				++arrayIndex;
				factor = 1;
			}
			else {
				factor*=2;
			}
		}
		build.addAll(Arrays.asList(bytes));
	}
}
