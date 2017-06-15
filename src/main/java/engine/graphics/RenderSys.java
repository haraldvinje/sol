package engine.graphics;

import engine.PositionComp;
import engine.Sys;
import engine.WorldContainer;
import engine.window.Window;
import org.lwjgl.opengl.GL11;
import utils.maths.Mat4;
import utils.maths.Vec3;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by eirik on 14.06.2017.
 */
public class RenderSys implements Sys {


    private Window window;
    private LightShader shader;


    private WorldContainer wc;

    Mat4 projectionTransform = Mat4.orthographic(0, 1600, 900, 0, 10, -10);


    public RenderSys(Window window) {
        this.window = window;
        shader = new LightShader();
    }

    @Override
    public void setWorldContainer(WorldContainer wc) {
        this.wc = wc;
    }


    public void update() {

        //clear screen
        glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        shader.bind();
        shader.setLightPoint(new Vec3(100f, 100f, -1000f));


        for (int entity : wc.getEntitiesWithComponentType(VertexArrayComp.class)) {

            VertexArrayComp vertexArrayComp = (VertexArrayComp)wc.getComponent(entity, VertexArrayComp.class);
            PositionComp positionComp = (PositionComp)wc.getComponent(entity, PositionComp.class);


            VertexArray vao = vertexArrayComp.getVao();

            Mat4 modelTransform = Mat4.translate( new Vec3(positionComp.getX(), positionComp.getY(), 0f) );

            renderVertexArray(vao, modelTransform, Mat4.identity(), projectionTransform);

        }



        window.swapBuffers();
    }

    /**
     * Render a vertex array.
     * @param vao
     */
    private void renderVertexArray(VertexArray vao, Mat4 modelTransform, Mat4 viewTransform, Mat4 projectionTransform) {
        shader.bind();
        vao.bind();

        shader.setModelTransform(modelTransform);
        shader.setViewTransform(viewTransform);
        shader.setProjectionTransform(projectionTransform);


        glDrawElements(GL_TRIANGLES, vao.getIndicesCount(), GL_UNSIGNED_BYTE, 0);

        vao.unbind();
        shader.unbind();
    }
}
