package engine.graphics.text;

/**
 * Created by eirik on 03.07.2017.
 */
public class CharMeshData {


    private float[] vertices;
    private float[] uvs;
    private byte[] indices;


    public CharMeshData(float[] vertices, float[] uvs, byte[] indices) {
        this.vertices = vertices;
        this.uvs = uvs;
        this.indices = indices;
    }


}
