package engine.graphics.text;

import utils.FileUtils;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by eirik on 03.07.2017.
 */
public class FontUtils {

    public static void main(String[] args) {
        loadFont("broadway.fnt");
    }

    public static HashMap<Character, CharData> loadFont(String fontfilePath) {
        InputStream is = FileUtils.loadAsStream(fontfilePath);
        Scanner s = new Scanner(is);

        HashMap<Character, CharData> charData;

        String line;
        while(s.hasNextLine()) {
            line = s.nextLine();
            String[] lineAttribs = line.split(" +");
            String lineIdentifier = lineAttribs[0]; //first word in a line

            if (lineIdentifier.equals("chars")) {
                //there is only one value, to we find the vakue after the '=' in the second atrribVal
                int charCount = Integer.parseInt( lineAttribs[1].split("=")[1] );
                charData = new HashMap<Character, CharData>( charCount );
            }
            else if (lineIdentifier.equals("char")) {
                //filter out values in an array
                int[] values = new int[10];
                for (int i = 1; i < lineAttribs.length; i++) {
                    values[i - 1] = Integer.parseInt(lineAttribs[i].split("=")[1]);
                }
                System.out.println(Arrays.toString(values));

                char c = (char)values[0]; //character id
                int x = values[1];
                int y = values[2];
                int width = values[3];
                int height = values[4];
                int xoffset = values[5];
                int yoffset = values[6];
                int xadvance = values[7];


            }
        }


        return null;
    }

    private static CharMeshData createMeshData(int x, int y, int width, int height) {
        float[] vertices = new float[] {
                0.0f, 0.0f, 0.0f,
                0.0f, height, 0.0f,
                width, height, 0.0f,
                width, 0.0f, 0.0f
        };

        float[] uvs = new float[] {
                x, y,
                x, y+height,
                x+width, y+height,
                x+width, y
        };

        byte[] indices = new byte[] {
                0, 1, 2,
                2, 3, 0
        };

        return new CharMeshData(vertices, uvs, indices);
    }
}
