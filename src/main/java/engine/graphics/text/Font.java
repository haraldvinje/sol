package engine.graphics.text;

import engine.graphics.Texture;
import engine.graphics.TextureUtils;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;

/**
 * Created by eirik on 03.07.2017.
 */
public class Font {


    private static EnumMap<FontType, Font> loadedFonts = new EnumMap<FontType, Font>(FontType.class);

    public static void loadFonts(FontType... fonts) {

    }

    private Texture fontAtlas;


    public Font getFont(FontType fontType) {
        return null;
    }

    public Font getDefaultFont() {
        return getFont(FontType.BROADWAY);
    }


    private void loadFont(FontType fontType) {
        String atlasPath = fontType.getTextAtlasPath();
        String ffilePath = fontType.getFontFilePath();

        fontAtlas = TextureUtils.loadTexture(atlasPath);

    }
}
