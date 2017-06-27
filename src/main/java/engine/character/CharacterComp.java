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


    public float reloadTime = 100f; //frames
    public float bulletSpeed = 600f;
    public float bulletRadius = 12;
    public float bulletLifetime = 90f; //frames

    public float timeToShoot = reloadTime;
    public float timeToDestroy = -1;

    public int bulletEntity = -1;

    public boolean shootExecuted = false; //to be read by serverNetwork


    public CharacterComp() {
    }


    public void allocateBulletEntity(WorldContainer wc) {
        bulletEntity = GameUtils.allocateProjectileEntity(wc, new Circle(bulletRadius), 10, 0.5f);
    }

    public void activateBullet(WorldContainer wc, Vec2 pos, float direction) {
        System.out.println("Shooting bullet");

        shootExecuted = true;

        wc.activateEntity(bulletEntity);

        //reset physics
        ((PhysicsComp)wc.getComponent(bulletEntity, PhysicsComp.class)).reset();

        System.out.println("Bullet angle: "+direction);

        Vec2 velocity = Vec2.newLenDir(bulletSpeed, direction);

        Vec2 offset = new Vec2(velocity);
        offset.setLength(64);
        pos = pos.add(offset);
        ((PositionComp)wc.getComponent(bulletEntity, PositionComp.class)).setPos(pos);
        ((RotationComp)wc.getComponent(bulletEntity, RotationComp.class)).setAngle(direction+M.PI); //make the target of the bullet knocked back towards you
        ((PhysicsComp)wc.getComponent(bulletEntity, PhysicsComp.class)).addVelocity(velocity);
    }

    public void deactivateBullet( WorldContainer wc) {
        wc.deactivateEntity(bulletEntity);


    }

}
