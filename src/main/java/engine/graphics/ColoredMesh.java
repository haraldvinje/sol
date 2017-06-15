package engine.graphics;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import utils.BufferUtils;

/**
 * Created by eirik on 13.06.2017.
 */
public class VertexArray {

    int vaoId;
    int verticesId;
    int colorsId;
    int indicesId;
    int normalsId;

    int indicesCount;

    public VertexArray(float[] vertices, float[] colors, byte[] indices) {
        indicesCount = indices.length;
        float[] normals = {0,0,0};
        vaoId = createVertexArray(vertices, normals, colors, indices);

        throw new IllegalStateException("Do not use this constructor, normals not defined");
    }
    public VertexArray(float[] vertices, float[] normals, float[] colors, byte[] indices) {
        indicesCount = indices.length;
        vaoId = createVertexArray(vertices, normals, colors, indices);
    }

    public int getIndicesCount() {
        return indicesCount;
    }

    public void bind() {
        GL30.glBindVertexArray(vaoId);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesId);
    }
    public void unbind() {
        GL30.glBindVertexArray(0);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    private int createVertexArray(float[] vertices, float[] normals, float[] colors, byte[] indices) {
        int id = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(id);

        verticesId = createVertexBuffer(Shader.VERTEX_LOCATION, 3, vertices);
        normalsId = createVertexBuffer(Shader.NORMALS_LOCATION, 3, normals);
        colorsId = createVertexBuffer(Shader.COLORS_LOCATION, 3, colors);
        indicesId = createIndicesBuffer(indices);


        GL30.glBindVertexArray(0);

        return id;
    }
    private int createVertexBuffer(int attribIndex, int size, float[] data) {
        int vboId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);

        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, BufferUtils.createFloatBuffer(data), GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attribIndex, size, GL11.GL_FLOAT, false, 0, 0);
        GL20.glEnableVertexAttribArray(attribIndex);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        return vboId;
    }

    private int createIndicesBuffer(byte[] indices) {
        int indicesId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesId);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, BufferUtils.createByteBuffer(indices), GL15.GL_STATIC_DRAW);

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

        return indicesId;
    }

}
