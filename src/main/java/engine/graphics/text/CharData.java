package engine.graphics.text;

/**
 * Created by eirik on 03.07.2017.
 */
public class CharData {

    private CharMeshData meshData;
    private int xoffset, yoffset, xadvance;


    public CharData(CharMeshData meshData, int xoffset, int yoffset, int xadvance) {
        this.meshData = meshData;
        this.xoffset = xoffset;
        this.yoffset = yoffset;
        this.xadvance = xadvance;
    }
}
