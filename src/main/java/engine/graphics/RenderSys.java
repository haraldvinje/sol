package engine.graphics;

import engine.PositionComp;
import engine.RotationComp;
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
    private ColorShader colorShader;
    private TextureShader textureShader;


    private WorldContainer wc;

    Mat4 projectionTransform = Mat4.orthographic(0, 1600, 900, 0, -10, 10);


    public RenderSys(Window window) {
        this.window = window;
        colorShader = new ColorShader();
        textureShader = new TextureShader();
    }

    @Override
    public void setWorldContainer(WorldContainer wc) {
        this.wc = wc;
    }


    public void update() {

        //clear screen
        glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        colorShader.bind();
        colorShader.setLightPoint(new Vec3(100f, 100f, -1000f));
        textureShader.bind();
        textureShader.setLightPoint(new Vec3(100f, 100f, -1000f));


        for (int entity : wc.getEntitiesWithComponentType(ColoredMeshComp.class)) {

            PositionComp positionComp = (PositionComp)wc.getComponent(entity, PositionComp.class);


            Mat4 modelScale = Mat4.identity();
            Mat4 modelRotate = Mat4.identity();
            Mat4 modelTranslate = Mat4.translate( new Vec3(positionComp.getX(), positionComp.getY(), 0f) );
            Mat4 modelCenterTranslate = Mat4.identity();

            //check if mesh should be centered
            if (wc.hasComponent(entity, CenterMeshComp.class)) {

            }

            if (wc.hasComponent(entity, RotationComp.class)) {
                RotationComp rotComp = (RotationComp) wc.getComponent(entity, RotationComp.class);

                modelRotate = Mat4.rotate(rotComp.getAngle());
            }
            Mat4 modelTransform = modelTranslate.multiply(modelScale.multiply(modelRotate));

            ColoredMeshComp coloredMeshComp = (ColoredMeshComp)wc.getComponent(entity, ColoredMeshComp.class);
            renderColoredMesh(coloredMeshComp.getMesh(), modelTransform, Mat4.identity(), projectionTransform);



        }

        for (int entity : wc.getEntitiesWithComponentType(TexturedMeshComp.class)) {

            PositionComp positionComp = (PositionComp)wc.getComponent(entity, PositionComp.class);

            Mat4 modelScale = Mat4.identity();
            Mat4 modelRotate = Mat4.identity();
            Mat4 modelTranslate = Mat4.translate( new Vec3(positionComp.getX(), positionComp.getY(), 0f) );

            if (wc.hasComponent(entity, RotationComp.class)) {
                RotationComp rotComp = (RotationComp) wc.getComponent(entity, RotationComp.class);

                modelRotate = Mat4.rotate(rotComp.getAngle());
            }
            Mat4 modelTransform = modelTranslate.multiply(modelScale.multiply(modelRotate));


            TexturedMeshComp texturedMeshComp = (TexturedMeshComp)wc.getComponent(entity, TexturedMeshComp.class);
            renderTexturedMesh(texturedMeshComp.getMesh(), modelTransform, Mat4.identity(), projectionTransform);


        }



        window.swapBuffers();
    }

    /**
     * Render a vertex array.
     * @param mesh
     */
    private void renderColoredMesh(ColoredMesh mesh, Mat4 modelTransform, Mat4 viewTransform, Mat4 projectionTransform) {
        colorShader.bind();
        mesh.bind();

        colorShader.setModelTransform(modelTransform);
        colorShader.setViewTransform(viewTransform);
        colorShader.setProjectionTransform(projectionTransform);


        glDrawElements(GL_TRIANGLES, mesh.getIndicesCount(), GL_UNSIGNED_BYTE, 0);

        mesh.unbind();
        colorShader.unbind();
    }

    private void renderTexturedMesh(TexturedMesh mesh, Mat4 modelTransform, Mat4 viewTransform, Mat4 projectionTransform) {
        textureShader.bind();
        mesh.bind();

        textureShader.setModelTransform(modelTransform);
        textureShader.setViewTransform(viewTransform);
        textureShader.setProjectionTransform(projectionTransform);


        glDrawElements(GL_TRIANGLES, mesh.getIndicesCount(), GL_UNSIGNED_BYTE, 0);

        mesh.unbind();
        textureShader.unbind();
    }
}
