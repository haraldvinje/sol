package engine.window;


import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import org.lwjgl.glfw.GLFWVidMode;

import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL11.*;


/**
 * Created by eirik on 13.06.2017.
 */
public class Window {


    private boolean GLFWinitialized = false;
    private boolean GLinitialized = false;


    private long windowId;


    public Window(float width, float height, String title) {

        initGLFW();
        createWindow(width, height, title);
        initGL();
    }


    public void initGLFW() {
        if (!glfwInit()) {
            throw new IllegalStateException("Could not initialize GLFW!");
        }

        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        //glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        GLFWinitialized = true;
    }

    public void createWindow(float width, float height, String title) {
        int iwidth = (int)width;
        int iheight = (int)height;

        if (!GLFWinitialized) throw new IllegalStateException("Cannot create window because GLFW is not initialized");

        windowId =  glfwCreateWindow(iwidth, iheight, title, NULL, NULL);

        if (windowId == NULL) {
            throw new IllegalStateException("Could not create GLFW window!");
        }

        // Get the resolution of the primary monitor
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        // Center our window
        glfwSetWindowPos(
                windowId,
                (vidmode.width() - iwidth) / 2,
                (vidmode.height() - iheight) / 2
        );

        glfwMakeContextCurrent(windowId);

        glfwSwapInterval(1);// Enable v-sync


        glfwShowWindow(windowId);
        glfwSwapBuffers(windowId);
    }

    public void initGL() {
        if (windowId == -1) throw new IllegalStateException("cannot init OpenGL before a window is created");

        GL.createCapabilities(); //get opengl context

        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        //glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        glEnable(GL_DEPTH_TEST);
        //glActiveTexture(GL_TEXTURE1);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        //print version
        System.out.println("OpenGL: " + glGetString(GL_VERSION));

        GLinitialized = true;
    }

    public void pollEvents() {
        glfwPollEvents();
    }

    public void swapBuffers() {
        glfwSwapBuffers(windowId);
    }

}
