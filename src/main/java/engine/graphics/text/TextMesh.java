package engine.graphics.text;

import engine.graphics.Texture;
import engine.graphics.VertexArrayUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by eirik on 03.07.2017.
 */
public class TextMesh {

    private Font font;
    private String string;

    private int vaoId;
    private int indicesId;
    private int verticesId;
    private int uvsId;

    private int indicesCount;


    public TextMesh(Font font, String string) {
        this.font = font;
        this.string = string;

        createVertexData(font, string);
    }

    private void setVertexData(float[] vertices, float[] uvs, byte[] indices) {
        vaoId = VertexArrayUtils.createVertexArray();
        verticesId = VertexArrayUtils.createVertexBuffer(0, 3, vertices);
        uvsId = VertexArrayUtils.createVertexBuffer(1, 2, uvs);
        indicesId = VertexArrayUtils.createIndicesBuffer(indices);

        indicesCount = indices.length;

        VertexArrayUtils.unbindVertexArray();

//        System.out.println("Created text vao with vertices="+vertices.length+" uvs="+uvs.length+" indices="+indices.length);
//        System.out.println(Arrays.toString(vertices) + "\n"+Arrays.toString(uvs)+"\n"+Arrays.toString(indices) );

    }

    public Font getFont() {
        return font;
    }

    public void setString(String string) {
        deleteVertexArraysAndBuffers();
        this.string = string;
        createVertexData(font, string);
    }
//    public void append(String string) {
//        //create an append data
//    }

    private void deleteVertexArraysAndBuffers() {
        VertexArrayUtils.deleteVertexBuffer(indicesId);
        VertexArrayUtils.deleteVertexBuffer(verticesId);
        VertexArrayUtils.deleteVertexBuffer(uvsId);

        VertexArrayUtils.deleteVertexArray(vaoId);
    }

    public int getIndicesCount() {
        return indicesCount;
    }


    public void bind() {
        VertexArrayUtils.bindVertexArray(vaoId, indicesId);
        font.getFontAtlas().bind();
    }

    public void unbind() {
        VertexArrayUtils.unbindVertexArray();
        font.getFontAtlas().unbind();
    }


    private void createVertexData(Font font, String string) {
        int charCount = string.length();
        int charVerteciesCount = 4* 3;
        int charUvsCount = 4*2;
        int charIndiciesCount = 2*3;

        int charIndexRange = 4;

        float[] vertecies = new float[ charVerteciesCount * charCount ];
        float[] uvs = new float[ charUvsCount * charCount ];
        byte[] indices = new byte[ charIndiciesCount * charCount ];

        int xcursor = 0, ycursor = 0;

        //create a vao combining all character data
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);

            if (c == '\n') {
                xcursor = 0;
                ycursor += font.getLineHeight();
            }
            else if (font.hasChar(c)) {
                CharData data = font.getCharData(c);
                CharMeshData meshData = data.getMeshData();

                //append vertex data
                int verteciesStartIndex = i * charVerteciesCount;
                copyVerticesToArray(vertecies, meshData.getVertices(), verteciesStartIndex, xcursor, ycursor);

                //append uv data
                int uvsStartIndex = i * charUvsCount;
                copyToArray(uvs, meshData.getUvs(), uvsStartIndex);

                //append index data
                int indicesStartIndex = i*charIndiciesCount;
                byte indicesStartValue = (byte)(i * charIndexRange); //the actual index to start at
                copyToArray(indices, meshData.getIndices(), indicesStartIndex, indicesStartValue);

                //move cursor
                xcursor += data.getXadvance();
            }
            else throw new IllegalStateException("Trying to render a char that is not in the font being used");

        }

        setVertexData(vertecies, uvs, indices);
    }

    private static void copyVerticesToArray(float[] targetArray, float[] fromArray, int startIndex, float xpos, float ypos) {
        if (fromArray.length % 3 != 0) throw new IllegalStateException("textMeshData vertices not a multiple of 3");

        for (int i = 0; i < fromArray.length; i+=3) {
            targetArray[i+0 + startIndex] = fromArray[i+0]+xpos;
            targetArray[i+1 + startIndex] = fromArray[i+1]+ypos;
            targetArray[i+2 + startIndex] = fromArray[i+2];
        }
    }

    private static void copyToArray(float[] targetArray, float[] fromArray, int startIndex) {
        for (int i = 0; i < fromArray.length; i++) {
            targetArray[i+startIndex] = fromArray[i];
        }
    }
    private static void copyToArray(byte[] targetArray, byte[] fromArray, int startIndex, byte addToValues) {
        for (int i = 0; i < fromArray.length; i++) {
            targetArray[i+startIndex] = (byte)( fromArray[i] + addToValues );
        }
    }

}
