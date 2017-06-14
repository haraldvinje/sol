package game;


import static org.lwjgl.opengl.GL11.*;

import engine.PositionComp;
import engine.UserInput;
import engine.WorldContainer;
import engine.graphics.LightShader;
import engine.graphics.VertexArray;
import engine.graphics.VertexArrayUtils;
import engine.window.Window;
import org.lwjgl.opengl.GL11;
import utils.maths.Mat4;
import utils.maths.Vec3;

/**
 * Created by eirik on 13.06.2017.
 */
public class Game {

    private static final float FRAME_INTERVAL = 1.0f/60.0f;


    private long lastTime;


    private Window window;
    private UserInput userInput;
    private LightShader shader;

    private WorldContainer wc;


    private VertexArray vao;
    private int player;


    public void init() {
        window = new Window(1600, 900, "SIIII");
        userInput = new UserInput(window);

        shader = new LightShader();
        wc = new WorldContainer();


        player = wc.createEntity();
        wc.addComponent(player, new PositionComp(100, 100));

        vao = VertexArrayUtils.createRectangle(200, 200);

    }

    /**
     * blocking while the game runs
     */
    public void start() {
        lastTime = System.nanoTime();

        float timeSinceUpdate = 0;

        while (true) {
            timeSinceUpdate += timePassed();

            if (timeSinceUpdate >= FRAME_INTERVAL) {
                timeSinceUpdate -= FRAME_INTERVAL;

                update();
            }


            if (window.shouldClosed() || userInput.isKeyboardPressed(UserInput.KEY_ESCAPE))
                break;
        }

        window.close();

    }

    public void update() {
        //System.out.println( ((PositionComp)wc.getComponent(player, WorldContainer.COMPMASK_POSITION)).getX() );

        window.pollEvents();

        System.out.println(vao.getIndicesCount());

        //render
        glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        Mat4 projectionTransform = Mat4.orthographic(0, 1600, 900, 0, 10, -10);

        shader.bind();
        vao.bind();

        shader.setLightPoint(new Vec3(100f, 100f, -100f));

        shader.setModelTransform(Mat4.translate( new Vec3(100f, 100f, 0f) ));
        shader.setViewTransform(Mat4.identity());
        shader.setProjectionTransform(projectionTransform);


        glDrawElements(GL_TRIANGLES, vao.getIndicesCount(), GL_UNSIGNED_BYTE, 0);

        vao.unbind();
        shader.unbind();

        window.swapBuffers();
    }


    /**
     * time passed since last call to this method
     * @return
     */
    private float timePassed() {
        long newTime = System.nanoTime();
        int deltaTime = (int)(newTime - lastTime);
        float deltaTimeF = (float) deltaTime;

        lastTime = newTime;

        return deltaTimeF/1000000000;
    }
}
