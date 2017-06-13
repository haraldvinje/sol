package engine;

import javax.swing.text.Position;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by eirik on 13.06.2017.
 *
 * Contains all entities and components
 */
public class WorldContainer {

    private static int ENTITY_COUNT = 32;

    public static final int COMPMASK_ENTITY_EXISTS = 1 << 0,
                            COMPMASK_POSITION = 1 << 1;


    private int[] entityMask; //the main container for entities

    private Map<Integer, PositionComp> positionComps = new HashMap<Integer, PositionComp>();


    public WorldContainer() {
        entityMask = new int[ENTITY_COUNT]; //all bits set to 0; no entity se

    }



    public int createEntity() {
        for (int i = 0; i < ENTITY_COUNT; i++) {
            if (!entityExists(i)) {
                resetEntity(i);
                return i;
            }
        }
        throw new IllegalStateException("There is not allocated enough space for more entities");
    }

    public void createPositionComp(int entity, float x, float y) {
        PositionComp pc = new PositionComp();
        pc.setX(x);
        pc.setY(y);
        positionComps.put(entity, pc);
    }
    public PositionComp getPositionComponent(int entity) {
        return positionComps.get(entity);
    }

    public boolean entityExists(int entity) {
        return hasComponent(entity, COMPMASK_ENTITY_EXISTS);
    }



    private boolean hasComponent(int entity, int compmask) {
        return (entityMask[entity] & compmask) == compmask;
    }

    private void resetEntity(int entity) { //cleans entity masks ++
        entityMask[entity] = 0;
    }
}
