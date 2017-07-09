package engine.graphics;

import engine.PositionComp;
import engine.RotationComp;
import engine.Sys;
import engine.WorldContainer;
import engine.graphics.text.*;
import engine.window.Window;
import game.GameUtils;
import org.lwjgl.opengl.GL11;
import utils.maths.M;
import utils.maths.Mat4;
import utils.maths.Vec2;
import utils.maths.Vec3;
import engine.graphics.view_.View;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by eirik on 14.06.2017.
 */
public class RenderSys implements Sys {

    static {
        Font.loadFonts(FontType.BROADWAY);
    }


    private Window window;
    private ColorShader colorShader;
    private TextureShader textureShader;
    private TextShader textShader;


    private WorldContainer wc;



    public RenderSys(Window window) {
        this.window = window;
        colorShader = new ColorShader();
        textureShader = new TextureShader();
        textShader = new TextShader();

    }

    @Override
    public void setWorldContainer(WorldContainer wc) {
        this.wc = wc;
    }


    public void update() {
        //clear screen
        glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);


        //get view propreties
        View view = wc.getView();

        Mat4 viewTransform = view.getViewTransform();
        Mat4 projectionTransform = view.getProjectionTransform();


        //render text
        textShader.bind();
        textShader.setProjectionTransform(projectionTransform);

        for (TextMeshComp textComp: texts){
            TextMesh textMesh = textComp.getTextMesh();

            //color
            textShader.setTextColor(textComp.getColor());

            //size
            float textScale = textComp.getSize() / textMesh.getFont().getFontSize();
            Mat4 screenScale = Mat4.scale(new Vec3(textScale, textScale, 1f));

            //position
            Mat4 screenTranslate = Mat4.translate(new Vec3(textComp.getViewPos(), 0f));

            //result
            textShader.setScreenTransform(screenTranslate.multiply( screenScale ));

            //draw
            textMesh.bind();
            glDrawElements(GL_TRIANGLES, textMesh.getIndicesCount(), GL_UNSIGNED_BYTE, 0);
            textMesh.unbind();

        });

        textShader.unbind();


        colorShader.bind();
        colorShader.setLightPoint(new Vec3(100f, 100f, -500f));
        textureShader.bind();
        textureShader.setLightPoint(new Vec3(100f, 100f, -500f));


        for (int entity : wc.getEntitiesWithComponentType(ColoredMeshComp.class)) {

            PositionComp positionComp = (PositionComp)wc.getComponent(entity, PositionComp.class);


            Mat4 modelScale = Mat4.identity();
            Mat4 modelRotate = Mat4.identity();
            Mat4 modelTranslate = Mat4.translate( positionComp.getPos3() );
            Mat4 modelCenterTranslate = Mat4.identity();

            //center mesh if centerComp is present
            if (wc.hasComponent(entity, MeshCenterComp.class)) {
                MeshCenterComp centerComp = (MeshCenterComp) wc.getComponent(entity, MeshCenterComp.class);
                modelCenterTranslate = Mat4.translate(new Vec3(-centerComp.getCx(), -centerComp.getCy(), 0));
            }

            if (wc.hasComponent(entity, RotationComp.class)) {
                RotationComp rotComp = (RotationComp) wc.getComponent(entity, RotationComp.class);

                modelRotate = Mat4.rotate(rotComp.getAngle());
            }


            Mat4 modelTransform = modelTranslate.multiply(modelScale.multiply(modelRotate.multiply( modelCenterTranslate )));

            ColoredMeshComp coloredMeshComp = (ColoredMeshComp)wc.getComponent(entity, ColoredMeshComp.class);
            renderColoredMesh(coloredMeshComp.getMesh(), modelTransform, viewTransform, projectionTransform);



        }

        for (int entity : wc.getEntitiesWithComponentType(TexturedMeshComp.class)) {

            PositionComp positionComp = (PositionComp)wc.getComponent(entity, PositionComp.class);

            Mat4 modelScale = Mat4.identity();
            Mat4 modelRotate = Mat4.identity();
            Mat4 modelTranslate = Mat4.translate( positionComp.getPos3() );
            Mat4 modelCenterTranslate = Mat4.identity();

            //center mesh if centerComp is present
            if (wc.hasComponent(entity, MeshCenterComp.class)) {
                MeshCenterComp centerComp = (MeshCenterComp) wc.getComponent(entity, MeshCenterComp.class);
                modelCenterTranslate = Mat4.translate(new Vec3(-centerComp.getCx(), -centerComp.getCy(), 0));
            }

            if (wc.hasComponent(entity, RotationComp.class)) {
                RotationComp rotComp = (RotationComp) wc.getComponent(entity, RotationComp.class);

                modelRotate = Mat4.rotate(rotComp.getAngle());
            }
            Mat4 modelTransform = modelTranslate.multiply(modelScale.multiply(modelRotate.multiply( modelCenterTranslate )));


            TexturedMeshComp texturedMeshComp = (TexturedMeshComp)wc.getComponent(entity, TexturedMeshComp.class);
            renderTexturedMesh(texturedMeshComp.getMesh(), modelTransform, viewTransform, projectionTransform);

        }


        window.swapBuffers();
    }

    @Override
    public void terminate() {

    }

    /**
     * Render a vertex array.
     * @param mesh
     */
    public void renderColoredMesh(ColoredMesh mesh, Mat4 modelTransform, Mat4 viewTransform, Mat4 projectionTransform) {
        colorShader.bind();
        mesh.bind();

        colorShader.setModelTransform(modelTransform);
        colorShader.setViewTransform(viewTransform);
        colorShader.setProjectionTransform(projectionTransform);


        glDrawElements(GL_TRIANGLES, mesh.getIndicesCount(), GL_UNSIGNED_BYTE, 0);

        mesh.unbind();
        colorShader.unbind();
    }

    public void renderTexturedMesh(TexturedMesh mesh, Mat4 modelTransform, Mat4 viewTransform, Mat4 projectionTransform) {
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
