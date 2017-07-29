package engine.network.client;

import engine.*;
import engine.graphics.*;
import engine.graphics.text.TextMesh;
import engine.graphics.text.TextMeshComp;
import engine.graphics.view_.View;
import engine.window.Window;
import game.client.Client;

/**
 * Created by eirik on 04.07.2017.
 */
public class ClientUtils {

    /**
     * WorldContainer initialized with client default component types and systems
     * @return
     */
    public static WorldContainer createDefaultWorldContainer(Window window, UserInput userInput) {
        WorldContainer wc = new WorldContainer(
                new View(Client.CLIENT_WIDTH, Client.CLIENT_HEIGHT)
                );

        //assign components
        wc.assignComponentType(PositionComp.class);
        wc.assignComponentType(RotationComp.class);
        wc.assignComponentType(ColoredMeshComp.class);
        wc.assignComponentType(TexturedMeshComp.class);
        wc.assignComponentType(MeshCenterComp.class);
        wc.assignComponentType(TextMeshComp.class);
        wc.assignComponentType(ViewRenderComp.class);
        wc.assignComponentType(RectangleComp.class);
        wc.assignComponentType(ButtonComp.class);


        //add systems
        wc.addSystem(new ButtonSys(userInput));

        wc.addSystem(new RenderSys(window));


        return wc;
    }


    public static void setEntityString(WorldContainer wc, int textMeshEntity, String s) {
        ( (ViewRenderComp)wc.getComponent(textMeshEntity, ViewRenderComp.class) ).getTextMesh(0).setString(s);
    }

    public static TextMesh getEntityTextMesh(WorldContainer wc, int textMeshEntity) {
        return ( (ViewRenderComp)wc.getComponent(textMeshEntity, ViewRenderComp.class) ).getTextMesh(0);
    }

    public static int createButton(WorldContainer wc,
                                   float x, float y, float width, float height,
                                   TextMesh textMesh,
                                   OnButtonAction pressAction, OnButtonAction releaseAction, OnButtonAction enterAction, OnButtonAction exitAction
                                   )
    {
        int e = wc.createEntity();
        wc.addComponent(e, new PositionComp(x, y));
        wc.addComponent(e, new RectangleComp(width, height));
        wc.addComponent(e, new ButtonComp(pressAction, releaseAction, enterAction, exitAction));
        wc.addComponent(e, new ViewRenderComp(textMesh));

        return e;
    }
}
