package gui;

import gui.gl.GLApp;
import gui.gl.GLFont;
import gui.graphics.GraphicEntity;
import gui.graphics.GraphicRenderer;
import gui.graphics.GraphicView;
import gui.inputs.KeyBoardListener;
import gui.inputs.MotionEvent;
import gui.inputs.MouseListener;

import java.awt.Font;
import java.io.File;
import java.util.Stack;

import javax.swing.JFileChooser;
import javax.swing.JPanel;

import main.Hub;
import main.Main;

import org.lwjgl.opengl.*;
import org.lwjgl.util.glu.*;



/**
 * Use setMaterial(), setLight() and makeTexture() to control light and material properties.
 * <P>
 * napier at potatoland dot org
 */
public class Gui extends GLApp {
	public static boolean running = true;
	private static Stack<MouseListener> mouseListener = new Stack<MouseListener>();
	private static Stack<KeyBoardListener> keyboardListener = new Stack<KeyBoardListener>();
	
    // Light position: if last value is 0, then this describes light direction.  If 1, then light position.

	private float[] cameraPos = new float[]{0f, 0f, 1f};
	private float[] cameraLook = new float[]{0f, 0f, 0f};
	

	private static boolean drag = true;
    
    public Gui(){
    	super();

        window_title = "Scrambooooooooooooooooooooooooooooo";
        displayWidth = 800;
        displayHeight = 600;
        Hub.width = displayWidth;
        Hub.height = displayHeight;
        this.run();  // will call init(), render(), mouse event functions
    }


    /**
     * Initialize the scene.  Called by GLApp.run()
     */
    public void setup()
    {
        giveOnClick(Hub.genericMouseListener);
        giveOnType(Hub.genericKeyBoardListener);
    	Hub.renderer = new GraphicRenderer();
    	Hub.renderer.loadImages();
        // Create texture for ground plane
        // setup perspective
        setPerspective();

        // no overall scene lighting
        setAmbientLight(new float[] { 10f, 10f, 10f, 0f });
        
        // enable lighting and texture rendering
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        // select model view for subsequent transforms
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();

        Main.setup();
    }


	@Override
    public void update(){
    	Hub.currentView.update(secondsSinceLastFrame); 
    }
    
    /**
     * Set the camera position, field of view, depth.
     */
    public static void setPerspective()
    {
        // select projection matrix (controls perspective)
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        // fovy, aspect ratio, zNear, zFar
        GLU.gluPerspective(30f, aspectRatio, 1f, 100f);
        // return to modelview matrix
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
    }
    
    /**
     * Render one frame.  Called by GLApp.run().
     */
    public void draw() {
        // clear depth buffer and color buffers
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        // select model view for subsequent transforms
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        
        // set the viewpoint
        GLU.gluLookAt(cameraPos[0], cameraPos[1], cameraPos[2],  // where is the eye
        		cameraLook[0], cameraLook[1], cameraLook[2],    // what point are we looking at
        			0f, 1f, 0f);    // which way is up
        if(Hub.renderer!=null){
        	Hub.renderer.display();
        }
        
    }
    
    @Override
    public void mouseWheel(int amount) {
    	mouseListener.peek().onMouseScroll(amount);
    }

    public void mouseMove(int x, int y) {
    	mouseListener.peek().onHover(new MotionEvent(x/Hub.width,y/Hub.height,MotionEvent.ACTION_DOWN,MotionEvent.MOUSE_LEFT));
    }
    
    public void dragLeftDown(int x, int y) {
    	if(drag){
    		mouseListener.peek().onClick(new MotionEvent(x/Hub.width,y/Hub.height,MotionEvent.ACTION_DOWN,MotionEvent.MOUSE_LEFT));
    	}
    }
    
    public void mouseLeftDown(int x, int y) {
    	mouseListener.peek().onClick(new MotionEvent(x/Hub.width,y/Hub.height,MotionEvent.ACTION_DOWN,MotionEvent.MOUSE_LEFT));

    }

    public void mouseLeftUp(int x, int y) {
    	mouseListener.peek().onClick(new MotionEvent(x/Hub.width,y/Hub.height,MotionEvent.ACTION_UP,MotionEvent.MOUSE_LEFT));

    }
    
    public void dragRightDown(int x, int y) {
    	if(drag){
    		mouseListener.peek().onClick(new MotionEvent(x/Hub.width,y/Hub.height,MotionEvent.ACTION_DOWN,MotionEvent.MOUSE_RIGHT));
    	}
    }
    public void mouseRightDown(int x, int y) {
    	mouseListener.peek().onClick(new MotionEvent(x/Hub.width,y/Hub.height,MotionEvent.ACTION_DOWN,MotionEvent.MOUSE_RIGHT));

    }

    public void mouseRightUp(int x, int y) {
    	mouseListener.peek().onClick(new MotionEvent(x/Hub.width,y/Hub.height,MotionEvent.ACTION_UP,MotionEvent.MOUSE_RIGHT));

    }

    @Override
    public void keyDown(char c, int keycode) {
    	if(!keyboardListener.isEmpty()){
    		keyboardListener.peek().keyCommand(KeyBoardListener.DOWN,c,keycode);
    	}
    }

    @Override
    public void keyUp(int keycode) {
    	if(!keyboardListener.isEmpty()){
    		keyboardListener.peek().keyCommand(KeyBoardListener.UP,' ',keycode);
    	}
    }


	public static void display(GraphicEntity e) {
		Hub.currentView.addChild(e);
	}


	public static void giveOnClick(MouseListener listener) {
		mouseListener.push(listener);
		//System.out.println("push"+mouseListener.size()+listener.getClass());
	}
	
	public static void removeOnClick(MouseListener listener) {
		while(!mouseListener.isEmpty()&&mouseListener.pop()!=listener){		
		}
		//System.out.println("pop"+mouseListener.size()+mouseListener.peek().getClass());
	}
	

    public static void giveOnType(KeyBoardListener listener) {
    	keyboardListener.push(listener);
		continuousKeyboard = listener.continuousKeyboard();
    	
	}
    public static void removeOnType(KeyBoardListener listener) {
    	if(!keyboardListener.empty()){
    		if(keyboardListener.peek()==listener){
    			keyboardListener.pop();
    		}
    	}
		if(!keyboardListener.empty()){
			continuousKeyboard = keyboardListener.peek().continuousKeyboard();
		}
		//System.out.println("pop"+mouseListener.size()+mouseListener.peek().getClass());
	}
    
	@Override
	public void cleanup(){
		Gui.running = false;
		Main.cleanup();
		super.cleanup();
	}


	public static void setView(GraphicView view) {
		if(Hub.currentView!=null){
			keyboardListener.clear();
			mouseListener.clear();
			Hub.currentView.onRemoveFromDrawable();			
		}
		Hub.currentView = view;
		giveOnClick(view);
		KeyBoardListener keyListener = view.getDefaultKeyBoardListener();
		if(keyListener!=null){
			Gui.giveOnType(keyListener);
		}
		view.onAddToDrawable();
	}


	public static void disableDrag() {
		Gui.drag = false;
	}

	public static void enableDrag(){
		Gui.drag = true;
	}

	public static File userSave(String sub){
		JFileChooser  fc = new JFileChooser("data"+File.separator+sub);
		int returnVal = fc.showOpenDialog(new JPanel());
        if (returnVal == JFileChooser.APPROVE_OPTION) {            
            return fc.getSelectedFile();
        } else {
            return null;
        }
	}
}