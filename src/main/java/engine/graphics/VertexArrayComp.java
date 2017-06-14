package engine.graphics;

import engine.Component;
import engine.WorldContainer;

/**
 * Created by eirik on 13.06.2017.
 */
public class VertexArrayComp implements Component{

    private VertexArray vao;

    public VertexArrayComp(VertexArray vao) {
        setVao(vao);
    }

    public VertexArray getVao() {
        return vao;
    }

    public void setVao(VertexArray vao) {
        this.vao = vao;
    }


//    @Override
//    public int getMask() {
//        return WorldContainer.COMPMASK_VERTEX_ARRAY;
//    }
}
