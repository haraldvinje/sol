package utils;

import java.io.*;

/**
 * Created by eirik on 13.06.2017.
 */
public class FileUtils {

    private FileUtils() {
    }

    public static String loadAsString(String file) {
        StringBuilder result = new StringBuilder();
        try {
            InputStream rs = FileUtils.class.getClassLoader().getResourceAsStream(file);

            BufferedReader reader = new BufferedReader(new InputStreamReader(rs));
            String buffer = "";
            while ((buffer = reader.readLine()) != null) {
                result.append(buffer + '\n');
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

}