package storage;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public abstract class Decoder {
	public static Object[] decode(byte[] file,int startAt,Map<Integer,String> strings, int integers, int floats, int numStrings){
		List<Object> objs = new ArrayList<Object>();
		for(int index = 0;index<integers;++index){
			Integer[] result = decodeInteger(file,startAt);
			objs.add(result[0]);
			startAt=result[1];
		}
		for(int index=0;index<floats;++index){
			Float x = decodeFloat(file,startAt);
			objs.add(x);
			startAt+=4;
		}
		for(int index=0;index<numStrings;++index){
			Integer[] result = decodeInteger(file,startAt);
			objs.add(strings.get(result[0]));
			startAt=result[1];
		}
		return objs.toArray();
	}

	public static Float decodeFloat(byte[] cs, int a){
		byte[] b = new byte[4];
		for(int i=0;i<4;++i){
			b[i]=cs[i+a];
		}
		return  ByteBuffer.wrap(b).getFloat();
	}


	public static Integer[] decodeInteger(byte[] cs, int a){
		int i=0;
		while(cs[a]>=0){
			i*=128;
			i+=cs[a];
			++a;
		}
		i*=64;
		if(cs[a]>=-128+64){
			i+=cs[a]-64+128;			
		}
		else {
			i+=cs[a]+128;
			i=-i;
		}
		return new Integer[]{i,a+1};
	}
}

