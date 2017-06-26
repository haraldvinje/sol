package engine.character;

import engine.*;
import engine.combat.DamagerComp;
import engine.combat.abilities.Ability;
import engine.combat.abilities.AbilityComp;
import engine.graphics.ColoredMesh;
import engine.graphics.ColoredMeshComp;
import engine.graphics.ColoredMeshUtils;
import engine.physics.*;
import utils.maths.TrigUtils;
import utils.maths.Vec2;

/**
 * Created by eirik on 15.06.2017.
 */
public class CharacterSys implements Sys {


    private WorldContainer wc;

    private float reloadTime = 0.2f;
    private float bulletSpeed = 600f;
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
            AbilityComp abComp = (AbilityComp) wc.getComponent(entity, AbilityComp.class);

            updateEntity(entity, posComp, inputComp, rotComp, phComp, abComp);
        }
    }

    private void updateEntity(int entity, PositionComp posComp, CharacterInputComp inputComp, RotationComp rotComp, PhysicsComp phComp, AbilityComp abComp) {
        updateMove(inputComp, phComp);
        updateRotation(inputComp, posComp, rotComp);
        updateAbilities(inputComp, posComp, rotComp, abComp);
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

    private void updateAbilities(CharacterInputComp inputComp, PositionComp posComp, RotationComp rotComp,  AbilityComp abComp) {
        //System.out.println(inputComp.isAction1());


        if (inputComp.isAction2()) {
            abComp.requestExecution(0);
        }

        if (inputComp.isAction1()){
            abComp.requestExecution(1);
        }


/*        if (inputComp.isAction1() && timeToShoot <= 0) {
            timeToShoot = reloadTime;

            createBullet(posComp.getPos(), Vec2.newLenDir(bulletSpeed, rotComp.getAngle()) );
        }*/
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
        PhysicsComp pc = new PhysicsComp(20, 0.05f, 0.3f);
        pc.addVelocity(velocity);
        //pc.setFrictionConstant(0.001f); not implemented in resolution
        wc.addComponent(b, pc);
        wc.addComponent(b, new CollisionComp(new Circle(bulletRadius)));
        wc.addComponent(b, new ColoredMeshComp(bulletMesh));

        wc.addComponent(b, new DamagerComp(10, 1f));

        wc.addComponent(b, new AffectedByHoleComp());

    }
}
