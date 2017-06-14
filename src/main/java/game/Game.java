package game;


import static org.lwjgl.opengl.GL11.*;

import engine.PositionComp;
import engine.WorldContainer;
import engine.graphics.LightShader;
import engine.graphics.VertexArray;
import engine.graphics.VertexArrayComp;
import engine.graphics.VertexArrayUtils;
import engine.window.Window;
import utils.maths.Mat4;
import utils.maths.Vec3;

/**
 * Created by eirik on 13.06.2017.
 */
public class Game {

    private static final float FRAME_INTERVAL = 1.0f/60.0f;

    private Window window;
    private LightShader shader;
    private WorldContainer wc;


    private VertexArray vao;

    private long lastTime;

    private int player;




    public void init() {
        window = new Window(1600, 900, "SIIII");

        shader = new LightShader();
        wc = new WorldContainer();


        //assign component types
        wc.assignComponentType(PositionComp.class);
        wc.assignComponentType(VertexArrayComp.class);


        player = wc.createEntity();
        wc.addComponent(player, new PositionComp(100, 100));
        wc.addComponent(player, new VertexArrayComp( VertexArrayUtils.createRectangle(32, 32)));


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

        }

    }

    public void update() {
        System.out.println( ((PositionComp)wc.getComponent(player, WorldContainer.COMPMASK_POSITION)).getX() );

        window.pollEvents();

        //render
        Mat4 projectionTransform = Mat4.orthographic(0, 1600, 900, 0, 10, -10);

        VertexArray vao = ((VertexArrayComp) wc.getComponent(player, VertexArrayComp.class) ).getVao();


        shader.bind();
        vao.bind();

        shader.setLightPoint(new Vec3(100f, 100f, -100f));

        shader.bind();
        shader.setLightPoint(new Vec3(600f, 600f, 2f));
        shader.setModelTransform(Mat4.translate( new Vec3(100f, 100f, 0f) ));
        shader.setViewTransform(Mat4.identity());
        shader.setProjectionTransform(projectionTransform);

        vao.bind();
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
