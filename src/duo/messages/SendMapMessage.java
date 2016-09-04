package duo.messages;

import duo.Handler;
import duo.client.Client;
import game.environment.Map;
import main.Hub;
import storage.Storage;

/**
 * SendMapMessages are sent to prime the Server and the partnered {@link duo.client.Client} for a byte data transfer.
 * {@link game.environment.Map}'s are contained within map files, completely as byte data.
 * To send this data to the partnered {@link duo.client.Client} a SendMapMessage is sent first to change the mode of the handler from accepting {@link duo.messages.Message}'s to accepting bytes.
 * The bytes follow. 
 * @author Geoffrey
 */
public class SendMapMessage extends Message{
	//For Message sending.
	private static final long serialVersionUID = -7927768988704831847L;

	//The name of the map which will be sent, this name is used mostly for saving complete times.
	private String mapName;
	//The Message that is called after the map has been received.
	private Message onEnd;
	//The number of bytes in the map data.
	private int numberOfBytes;

	/**
	 * Called by the SendMapMessage.send method, this constructor initializes the {@link duo.messages.Message} with the necessary variables.
	 * @param mapName - The token name of the map, this name is not protected, and is used primarily for saving completed times.
	 * @param onEnd - {@link duo.messages.Message} that is acted upon after the map is received. Typically this is {@link duo.messages.StartGameMessage}, but is left for general use.
	 * @param numberOfBytes - The number of bytes that the map data has, i.e. the number of bytes to be sent.
	 */
	private SendMapMessage(String mapName, Message onEnd,int numberOfBytes){
		this.mapName = mapName;
		this.onEnd = onEnd;
		this.numberOfBytes = numberOfBytes;
	}

	/**
	 * When received, this {@link duo.messages.Message} calls the {@link duo.Handler}.acceptByte(int) method, which takes in incoming bytes.
	 * Those bytes are then converted into a {@link game.environment.Map} object using the {@link storage.Storage}.loadMap method.
	 * Then, the onEnd message is acted upon.
	 */
	@Override
	public void act(Handler handler) {
		//Accepts the incoming bytes that represent the map file data.
		byte[] bytes = handler.acceptBytes(numberOfBytes);
		//Those bytes are combined into a map object.
		Storage.loadMap(mapName,null,bytes);
		//onEnd Message is acted upon.
		onEnd.act(handler);
	}

	/**
	 * Opens the map file, then sends it as bytes to the partnered {@link duo.client.Client}.
	 * The map file is then used to load a map for this {@link duo.client.Client} as well.
	 * @param client - {@link duo.client.Client} that is used to send the map bytes.
	 * @param filename - File name of where the map resides.
	 * @param onEnd - {@link duo.messages.Message} that is acted upon after the SendMapMessage has been received.
	 */
	public static void send(Client client,String filename, Message onEnd){
		boolean loadForSelf = true;
		if(Hub.RESTART_STRING.equals(filename)&&Hub.map!=null&&Hub.map.getFileName()!=null){
			filename = Hub.map.getFileName();
			loadForSelf = false;
		}
		//Gets the map name from the file name, basically removing the extension ".map" and the folder names.
		String mapName = Storage.getMapNameFromFileName(filename);
		//Reads the bytes of the file into an array.
		byte[] file = Storage.readVerbatum(filename);
		//Sends the herald message in the SendMapMessage to prime the Server and consequently the partnered Client. 
		client.getHandler().sendNow(new SendMapMessage(mapName,onEnd,file.length));
		//Send the bytes from the map file.
		client.getHandler().sendBytes(file);
		if(loadForSelf){
			//Load the map from the bytes.
			Storage.loadMap(mapName,filename,file);
		}
	}
}
