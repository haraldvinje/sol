package engine;

import engine.window.Window;
import static org.lwjgl.glfw.GLFW.*;


/**
 * Created by eirik on 14.06.2017.
 */
public class UserInput {

    public static final int KEY_A = GLFW_KEY_A;
    public static final int KEY_W = GLFW_KEY_W;
    public static final int KEY_D = GLFW_KEY_D;
    public static final int KEY_S = GLFW_KEY_S;
    public static final int KEY_Q = GLFW_KEY_Q;
    public static final int KEY_E = GLFW_KEY_E;
    public static final int KEY_F = GLFW_KEY_F;

    public static final int KEY_ENTER = GLFW_KEY_ENTER,
                            KEY_ESCAPE = GLFW_KEY_ESCAPE,
                            KEY_SPACE = GLFW_KEY_SPACE;


    public static final int MOUSE_BUTTON_1 = GLFW_MOUSE_BUTTON_1;
    public static final int MOUSE_BUTTON_2 = GLFW_MOUSE_BUTTON_2;
    public static final int MOUSE_BUTTON_3 = GLFW_MOUSE_BUTTON_3;


    private Window window;

    private float mouseX;
    private float mouseY;

    private boolean[] keyPressed = new boolean[562];
    private boolean[] mouseButtonPressed = new boolean[16];


    public UserInput(Window window) {
        this.window = window;

        //if (!initialized) throw new IllegalStateException("Have to init engine before enabling user input");

        window.setMouseButtonCallback( (w, button, action, mods) -> {
            if (action == GLFW_RELEASE) {
                mouseButtonPressed[button] = false;
            }
            else if (action == GLFW_PRESS) {
                mouseButtonPressed[button] = true;
            }
        });

        window.setCursorPosCallback( (w, xpos, ypos) -> {
            mouseX = (float)xpos;
            mouseY = (float)ypos;
        });

        window.setKeyCallback( (w, key, scancode, action, mods) -> {

            if (action == GLFW_RELEASE){
                keyPressed[key] = false;
            }
            else if (action == GLFW_PRESS){
                keyPressed[key] = true;
            }
        });
    }

    public float getMouseX() {
        return mouseX;
    }
    public float getMouseY() {
        return mouseY;
    }
    public boolean isMousePressed( int mouseButton ) {
        return mouseButtonPressed[mouseButton];
    }
    public boolean isKeyboardPressed( int keyCode) {
        return keyPressed[keyCode];
    }
}
