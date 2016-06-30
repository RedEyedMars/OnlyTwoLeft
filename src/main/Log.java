package main;

public class Log {
	
	public static boolean verbose = true;
	public static boolean entity = false;
	public static boolean location = false;
	public static boolean tiles = false;
	public static void e(String str, String e)
	{
		//System.out.println(str+"\t"+e);
	}
	
	public static void log(String str)
	{
		//System.out.println(str);
	}

	public static void d(String mode, String method, String message) {
		if(entity&&mode.contains("entity")){
			display(method,message);
		}
		else if(location&&mode.contains("location")){
			display(method,message);
		}
		else if(tiles&&mode.contains("tiles")){
			display(method,message);
		}
	}
	private static void display(String method, String message){
		System.out.println(verbose?(method+"->"):""+message);
	}
}
