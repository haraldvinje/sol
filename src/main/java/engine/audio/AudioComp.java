package engine.audio;

import engine.Component;
import utils.maths.Vec2;
import utils.maths.Vec3;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.openal.AL10.*;

/**
 * Created by haraldvinje on 26-Jul-17.
 */
public class AudioComp implements Component {

    public List<Sound> soundList = new ArrayList<Sound>();

    private int sourcePointer;

    public int requestSound = -1;

    public AudioComp(Sound s){
        soundList.add(s);
    }


    public AudioComp(List<Sound> soundList){
        this.soundList = soundList;
        this.sourcePointer = alGenSources();

    }

    public void playSound(int i){
        soundList.get(i).playSource(sourcePointer);
    }



    public void addSound(Sound sound){
        soundList.add(sound);
    }

    public void addSound(String filename){
        addSound(new Sound(filename));
    }

    public void setPosition(Vec2 vec2){
        alSource3f(sourcePointer, AL_POSITION, vec2.x, vec2.y, 0);
    }

    public void setPosition(int x, int y){
        alSource3f(sourcePointer, AL_POSITION, x, y, 0);
    }

    public void setPosition(Vec3 vec3){
        alSource3f(sourcePointer, AL_POSITION, vec3.x, vec3.y, vec3.z);
    }

    public void setPosition(int x, int y, int z){
        alSource3f(sourcePointer, AL_POSITION, x, y, z);
    }



    public void setRollOffFactor(int value){
        alSourcef(sourcePointer, AL_ROLLOFF_FACTOR, value);
    }

    public void setReferenceDistance(int value){
        alSourcef(sourcePointer, AL_REFERENCE_DISTANCE, value);
    }

    public void setMaxDistance(int value){
        alSourcef(sourcePointer, AL_MAX_DISTANCE, value);
    }

}
