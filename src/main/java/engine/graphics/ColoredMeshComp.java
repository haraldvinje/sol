package engine.graphics;

import engine.Component;

/**
 * Created by eirik on 13.06.2017.
 */
public class VertexArrayComp implements Component{

    private ColoredMesh mesh;

    public VertexArrayComp(ColoredMesh mesh) {
        setMesh(mesh);
    }

    public ColoredMesh getMesh() {
        return mesh;
    }

    public void setMesh(ColoredMesh mesh) {
        this.mesh = mesh;
    }

}
