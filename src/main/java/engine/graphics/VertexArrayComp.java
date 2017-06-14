package engine.graphics;

/**
 * Created by eirik on 13.06.2017.
 */
public class VertexArrayComp {

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
}
