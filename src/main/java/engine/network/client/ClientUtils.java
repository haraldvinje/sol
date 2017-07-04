package engine.network.client;

import engine.PositionComp;
import engine.RotationComp;
import engine.WorldContainer;
import engine.graphics.ColoredMeshComp;
import engine.graphics.MeshCenterComp;
import engine.graphics.RenderSys;
import engine.graphics.TexturedMeshComp;
import engine.window.Window;

/**
 * Created by eirik on 04.07.2017.
 */
public class ClientUtils {

    /**
     * WorldContainer initialized with client default component types and systems
     * @return
     */
    public static WorldContainer createDefaultWorldContainer(Window window) {
        WorldContainer wc = new WorldContainer();

        //assign components
        wc.assignComponentType(PositionComp.class);
        wc.assignComponentType(RotationComp.class);
        wc.assignComponentType(ColoredMeshComp.class);
        wc.assignComponentType(TexturedMeshComp.class);
        wc.assignComponentType(MeshCenterComp.class);

        //add systems
        wc.addSystem(new RenderSys(window));

        return wc;
    }
}
