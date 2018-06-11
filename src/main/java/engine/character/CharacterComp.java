package engine.character;

import engine.Component;
import engine.PositionComp;
import engine.RotationComp;
import engine.WorldContainer;
import engine.combat.DamagerComp;
import engine.graphics.ColoredMesh;
import engine.graphics.ColoredMeshComp;
import engine.graphics.ColoredMeshUtils;
import engine.physics.Circle;
import engine.physics.CollisionComp;
import engine.physics.PhysicsComp;
import game.GameUtils;
import utils.maths.M;
import utils.maths.Vec2;

/**
 * Created by eirik on 15.06.2017.
 */
public class CharacterComp implements Component {


    public int respawnTime = 60;
    public int respawnTimer = -1;

    private float moveAccel;
    private float maxSpeed;

    private int respawnCount = 0;


    public CharacterComp(float moveAccel, float maxSpeed) {
        setMoveAccel(moveAccel);
        setMaxSpeed(maxSpeed);
    }
    public CharacterComp(float moveAccel) {
        this(moveAccel, 500f);
    }


    public float getMoveAccel() {
        return moveAccel;
    }
    public float getMaxSpeed() {
        return maxSpeed;
    }

    public void setMoveAccel(float moveAccel) {
        this.moveAccel = moveAccel;
    }

    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public void incrementRespawnCount() {
        respawnCount++;
    }
    public int getRespawnCount() {
        return respawnCount;
    }
}
