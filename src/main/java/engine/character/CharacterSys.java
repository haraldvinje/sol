package engine.character;

import engine.*;
import engine.combat.DamagerComp;
import engine.graphics.ColoredMesh;
import engine.graphics.ColoredMeshComp;
import engine.graphics.ColoredMeshUtils;
import engine.physics.Circle;
import engine.physics.CollisionComp;
import engine.physics.PhysicsComp;
import engine.physics.Shape;
import utils.maths.TrigUtils;
import utils.maths.Vec2;

/**
 * Created by eirik on 15.06.2017.
 */
public class CharacterSys implements Sys {


    private WorldContainer wc;

    private float reloadTime = 0.1f;
    private float bulletSpeed = 10f;//600f;
    private float bulletRadius = 12;
    private float timeToShoot = reloadTime;
    private ColoredMesh bulletMesh = ColoredMeshUtils.createCircleTwocolor(bulletRadius, 8);


    @Override
    public void setWorldContainer(WorldContainer wc) {
        this.wc = wc;
    }

    @Override
    public void update() {
        //float aimAngle = TrigUtils.pointDirection(posComp.getX(), posComp.getY(),   userInput.getMouseX(), userInput.getMouseY());

        for (int entity : wc.getEntitiesWithComponentType(CharacterComp.class)) {
            PositionComp posComp = (PositionComp) wc.getComponent(entity, PositionComp.class);
            CharacterInputComp inputComp = (CharacterInputComp) wc.getComponent(entity, CharacterInputComp.class);
            RotationComp rotComp = (RotationComp) wc.getComponent(entity, RotationComp.class);
            PhysicsComp phComp = (PhysicsComp) wc.getComponent(entity, PhysicsComp.class);

            updateEntity(entity, posComp, inputComp, rotComp, phComp);
        }
    }

    private void updateEntity(int entity, PositionComp posComp, CharacterInputComp inputComp, RotationComp rotComp, PhysicsComp phComp) {
        updateMove(inputComp, phComp);
        updateRotation(inputComp, posComp, rotComp);
        updateAbilities(inputComp, posComp, rotComp);
    }


    private void updateMove(CharacterInputComp inputComp, PhysicsComp phComp) {
        float accel = 1200.0f;
        float stepX = ( (inputComp.isMoveRight()? 1:0) - (inputComp.isMoveLeft()? 1:0) );
        float stepY = ( (inputComp.isMoveDown()? 1:0) - (inputComp.isMoveUp()? 1:0) );

        phComp.addAcceleration(new Vec2(stepX, stepY).normalize().scale(accel));
    }

    private void updateRotation(CharacterInputComp inputComp, PositionComp posComp, RotationComp rotComp) {
        float angle = TrigUtils.pointDirection(posComp.getX(), posComp.getY(), inputComp.getAimX(), inputComp.getAimY());
        rotComp.setAngle(angle);
    }

    private void updateAbilities(CharacterInputComp inputComp, PositionComp posComp, RotationComp rotComp) {
        //System.out.println(inputComp.isAction1());

        if (inputComp.isAction1() && timeToShoot <= 0) {
            timeToShoot = reloadTime;

            createBullet(posComp.getPos(), Vec2.newLenDir(bulletSpeed, rotComp.getAngle()) );
        }
        else {
            timeToShoot -= 1.0f/60f;
        }
    }


    private void createBullet(Vec2 position, Vec2 velocity) {
        System.out.println("Creating bullet");
        int b = wc.createEntity();

        Vec2 offset = new Vec2(velocity);
        offset.setLength(64);
        position = position.add(offset);
        wc.addComponent(b, new PositionComp(position.x, position.y));
        PhysicsComp pc = new PhysicsComp(40, 0.5f, 1.2f);
        pc.addVelocity(velocity);
        //pc.setFrictionConstant(0.001f); not implemented in resolution
        wc.addComponent(b, pc);
        wc.addComponent(b, new CollisionComp(new Circle(bulletRadius)));
        wc.addComponent(b, new ColoredMeshComp(bulletMesh));

        wc.addComponent(b, new DamagerComp(600f, 1f));

    }
}
