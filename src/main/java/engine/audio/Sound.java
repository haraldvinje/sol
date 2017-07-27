package engine.audio;

import static org.lwjgl.openal.AL10.*;

/**
 * Created by haraldvinje on 26-Jul-17.
 */
public class Sound {



    private String fileName;

    private int bufferPointer;




    public Sound(String fileName){
        this.bufferPointer = alGenBuffers();
        this.fileName = fileName;
        AudioUtils.initSound(fileName, bufferPointer);
    }

    public int getBufferPointer(){
        return bufferPointer;
    }



    public void playSource(int sourcePointer){
        alSourcei(sourcePointer, AL_BUFFER, bufferPointer);
        alSourcePlay(sourcePointer);
    }

}