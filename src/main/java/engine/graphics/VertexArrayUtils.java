package engine.graphics;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import utils.BufferUtils;
import utils.maths.M;

/**
 * Created by eirik on 13.06.2017.
 */
public class VertexArrayUtils {


    public static int createVertexBuffer(int attribIndex, int size, float[] data) {
        int vboId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);

        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, BufferUtils.createFloatBuffer(data), GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attribIndex, size, GL11.GL_FLOAT, false, 0, 0);
        GL20.glEnableVertexAttribArray(attribIndex);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        return vboId;
    }

    public static int createIndicesBuffer(byte[] indices) {
        int indicesId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesId);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, BufferUtils.createByteBuffer(indices), GL15.GL_STATIC_DRAW);

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

        return indicesId;
    }

}
