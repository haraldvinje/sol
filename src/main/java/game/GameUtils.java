package game;

import engine.PositionComp;
import engine.RotationComp;
import engine.UserInput;
import engine.WorldContainer;
import engine.character.*;
import engine.combat.DamageResolutionSys;
import engine.combat.DamageableComp;
import engine.combat.DamagerComp;
import engine.combat.abilities.*;
import engine.graphics.*;
import engine.network.client.ClientNetworkSys;
import engine.network.server.ServerNetworkSys;
import engine.physics.*;
import engine.window.Window;
import utils.maths.M;

/**
 * Created by eirik on 22.06.2017.
 */
public class GameUtils {


    public static final float MAP_WIDTH = 1600f,
                                MAP_HEIGHT = 900f;

    public static final int SERVER = 0, CLIENT = 1, OFFLINE = 2;
    public static int PROGRAM = -1;
    //public static boolean SERVER_RENDER;
    public static String HOST_NAME;


    public static void assignComponentTypes(WorldContainer wc) {

        if (PROGRAM == SERVER) {
            wc.assignComponentType(PositionComp.class);
            wc.assignComponentType(ColoredMeshComp.class);
            wc.assignComponentType(TexturedMeshComp.class);
            wc.assignComponentType(CollisionComp.class);
            wc.assignComponentType(PhysicsComp.class);
            wc.assignComponentType(CharacterComp.class);
            wc.assignComponentType(CharacterInputComp.class);
            wc.assignComponentType(RotationComp.class);
            wc.assignComponentType(MeshCenterComp.class);
            wc.assignComponentType(HoleComp.class);
            wc.assignComponentType(DamageableComp.class);
            wc.assignComponentType(DamagerComp.class);
            wc.assignComponentType(AffectedByHoleComp.class);
            wc.assignComponentType(AbilityComp.class);
            wc.assignComponentType(HitboxComp.class);
//        wc.assignComponentType(UserCharacterInputComp.class);

        }
        else if (PROGRAM == CLIENT){
            wc.assignComponentType(PositionComp.class);
            wc.assignComponentType(ColoredMeshComp.class);
            wc.assignComponentType(TexturedMeshComp.class);
            wc.assignComponentType(CharacterComp.class);
            wc.assignComponentType(RotationComp.class);
            wc.assignComponentType(MeshCenterComp.class);
//            wc.assignComponentType(CollisionComp.class);
            wc.assignComponentType(PhysicsComp.class);
//            wc.assignComponentType(CharacterInputComp.class);
//            wc.assignComponentType(UserCharacterInputComp.class);
//            wc.assignComponentType(HoleComp.class);
//            wc.assignComponentType(DamageableComp.class);
//            wc.assignComponentType(DamagerComp.class);
//            wc.assignComponentType(AffectedByHoleComp.class);
            wc.assignComponentType(AbilityComp.class);
            wc.assignComponentType(HitboxComp.class);
        }

        else if (PROGRAM == OFFLINE) {
            //assign component types
            wc.assignComponentType(PositionComp.class);
            wc.assignComponentType(ColoredMeshComp.class);
            wc.assignComponentType(TexturedMeshComp.class);
            wc.assignComponentType(CollisionComp.class);
            wc.assignComponentType(PhysicsComp.class);
            wc.assignComponentType(CharacterComp.class);
            wc.assignComponentType(CharacterInputComp.class);
            wc.assignComponentType(RotationComp.class);
            wc.assignComponentType(MeshCenterComp.class);
            wc.assignComponentType(HoleComp.class);
            wc.assignComponentType(DamageableComp.class);
            wc.assignComponentType(DamagerComp.class);
            wc.assignComponentType(AffectedByHoleComp.class);
            wc.assignComponentType(AbilityComp.class);
            wc.assignComponentType(UserCharacterInputComp.class);
            wc.assignComponentType(HitboxComp.class);
        }

        else {
            throw new IllegalStateException("Specify programtype in GameUtils");
        }

    }

    public static void assignSystems(WorldContainer wc, Window window, UserInput userInput) {
        if (PROGRAM == SERVER) {
            wc.addSystem(new CharacterSys());
            wc.addSystem(new AbilitySys());

            wc.addSystem(new ServerNetworkSys());

            wc.addSystem(new CollisionDetectionSys());
            wc.addSystem(new HoleResolutionSys());
            wc.addSystem(new HitboxResolutionSys());
            wc.addSystem(new DamageResolutionSys());
            wc.addSystem(new CollisionResolutionSys());

            wc.addSystem(new PhysicsSys());

            wc.addSystem(new RenderSys(window));
        }

        else if (PROGRAM == CLIENT){
            wc.addSystem(new ClientNetworkSys(HOST_NAME, userInput) );
            wc.addSystem(new AbilitySys());
            wc.addSystem(new PhysicsSys());

            wc.addSystem(new RenderSys(window));
        }

        else if (PROGRAM == OFFLINE) {
            wc.addSystem(new UserCharacterInputSys(userInput));
            wc.addSystem(new CharacterSys());
            wc.addSystem(new AbilitySys());

            wc.addSystem(new CollisionDetectionSys());
            wc.addSystem(new HoleResolutionSys());
            wc.addSystem(new HitboxResolutionSys());
            wc.addSystem(new DamageResolutionSys());
            wc.addSystem(new CollisionResolutionSys());

            wc.addSystem(new PhysicsSys());

            wc.addSystem(new RenderSys(window));
        }
    }


    public static void createInitialEntities(WorldContainer wc) {

        //create players
        float centerSeparation = 300f;
        if (PROGRAM == OFFLINE) {
            createCharacter(wc, MAP_WIDTH / 2 - centerSeparation, MAP_HEIGHT / 2);
            //createPlayer(wc, MAP_WIDTH / 2 + centerSeparation, MAP_HEIGHT / 2);

            createSandbag(wc);
        }
        else {
            createCharacter(wc, MAP_WIDTH / 2 - centerSeparation, MAP_HEIGHT / 2);
            createCharacter(wc, MAP_WIDTH / 2 + centerSeparation, MAP_HEIGHT / 2);
        }


        //create walls
        float wallThickness = 64f;
        createWall(wc, wallThickness/2, MAP_HEIGHT/2, wallThickness, MAP_HEIGHT);
        createWall(wc, MAP_WIDTH-wallThickness/2, MAP_HEIGHT/2, wallThickness, MAP_HEIGHT);

//        createWall(wc, MAP_WIDTH/2, wallThickness/2, MAP_WIDTH-wallThickness*2, wallThickness);
//        createWall(wc, MAP_WIDTH/2, MAP_HEIGHT-wallThickness/2, MAP_WIDTH-wallThickness*2, wallThickness);

        //create holes
        createRectangleHoleInvisible(wc, MAP_WIDTH/2, wallThickness/2, MAP_WIDTH-wallThickness*2, wallThickness);
        createRectangleHoleInvisible(wc, MAP_WIDTH/2, MAP_HEIGHT-wallThickness/2, MAP_WIDTH-wallThickness*2, wallThickness);
        createCircleHole(wc, MAP_WIDTH/2, MAP_HEIGHT/2, 64f);


        //create background
        createBackground(wc);


    }


    public static int allocateHitboxEntity(WorldContainer wc, Circle shape, float damage, float baseKnockback, float knockbackRatio){
        int e = wc.createEntity();

        wc.addInactiveComponent(e, new PositionComp(0, 0));
        wc.addInactiveComponent(e, new RotationComp());

        wc.addInactiveComponent(e, new PhysicsComp());
        wc.addInactiveComponent(e, new HitboxComp());

        float[] redColor = {1.0f, 0f,0f};
        wc.addInactiveComponent(e, new ColoredMeshComp(ColoredMeshUtils.createCircleSinglecolor(shape.getRadius(), 16, redColor)) );

        if (PROGRAM == SERVER  || PROGRAM == OFFLINE) {
            wc.addInactiveComponent(e, new CollisionComp(shape));
            wc.addInactiveComponent(e, new DamagerComp(damage, knockbackRatio));

        }

        return e;
    }

    public static int allocateProjectileEntity(WorldContainer wc, Circle shape, float damage, float knockbackRatio) {
        int b = wc.createEntity();

        wc.addInactiveComponent(b, new PositionComp(0,0));
        wc.addInactiveComponent(b, new RotationComp());

        wc.addInactiveComponent(b, new PhysicsComp(20, 0.05f, 0.3f));
        wc.addInactiveComponent(b, new HitboxComp());

        wc.addInactiveComponent(b, new ColoredMeshComp( ColoredMeshUtils.createCircleTwocolor(shape.getRadius(), 12) ));

        if (PROGRAM == SERVER || PROGRAM == OFFLINE) {
            wc.addInactiveComponent(b, new CollisionComp(shape));
            wc.addInactiveComponent(b, new DamagerComp(damage, baseKnockback, knockbackRatio));

        }

        return b;
    }

    private static int createCharacter(WorldContainer wc, float x, float y) {
        int player = wc.createEntity();
        float radius = 32f;
        float xoffset = 16f;

        wc.addComponent(player, new CharacterComp());
        wc.addComponent(player, new PositionComp(x, y));
        wc.addComponent(player, new RotationComp());

        wc.addComponent(player, new TexturedMeshComp(TexturedMeshUtils.createRectangle("sol_frank.png", 4*radius*2, 2*radius*2)));
        wc.addComponent(player, new MeshCenterComp(radius*2+xoffset, radius*2));


        wc.addComponent(player, new AbilityComp(
                new MeleeAbility(wc, 4, 16, 3, 20, 8f, 0.2f, new Circle(64f),32.0f, 0f),
                new ProjectileAbility(wc, 10, 15, 60, 10, 0.5f, 500f, M.PI, new Circle(8))
                //new MeleeAbility(wc,  15, 1,15,30,20f, 0.8f, new Circle(8f), 82f,0f)
        ));

        if (PROGRAM == SERVER || PROGRAM == OFFLINE) {
            wc.addComponent(player, new CharacterInputComp());

            wc.addComponent(player, new PhysicsComp(80, 5f, 0.3f, PhysicsUtil.FRICTION_MODEL_VICIOUS));
            wc.addComponent(player, new CollisionComp(new Circle(radius)));

            wc.addComponent(player, new AffectedByHoleComp());

            wc.addComponent(player, new DamageableComp());
        }
        if (PROGRAM == OFFLINE) {
            wc.addComponent(player, new UserCharacterInputComp());
        }

        return player;
    }
    private static int createSandbag(WorldContainer wc) {
        if (PROGRAM != OFFLINE) throw new UnsupportedOperationException("Sandbag not implemented for nonoffline use");

        float radius = 32f;
        int sandbag = wc.createEntity();
        wc.addComponent(sandbag, new PositionComp(500, 300) );
        wc.addComponent(sandbag, new TexturedMeshComp(TexturedMeshUtils.createRectangle("sandbag.png", radius*2, radius*2)));
        wc.addComponent(sandbag, new MeshCenterComp(radius, radius));

        wc.addComponent(sandbag, new PhysicsComp(80, 2.5f, 0.3f, PhysicsUtil.FRICTION_MODEL_VICIOUS));
        wc.addComponent(sandbag, new CollisionComp(new Rectangle(radius*2, radius*2)));

        wc.addComponent(sandbag, new DamageableComp());
        wc.addComponent(sandbag, new AffectedByHoleComp());

        return sandbag;
    }

    private static int createCircleHole(WorldContainer wc, float x, float y, float radius) {
        int hole = wc.createEntity();
        float[] color = {0.0f, 0.0f, 0.0f};

        wc.addComponent(hole, new PositionComp(x, y));

        wc.addComponent(hole, new ColoredMeshComp(ColoredMeshUtils.createCircleSinglecolor(radius, 16, color)));
        //wc.addComponent(hole, new MeshCenterComp(radius, radius));

        if (PROGRAM == SERVER || PROGRAM == OFFLINE) {
            wc.addComponent(hole, new CollisionComp(new Circle(radius)));
            //wc.addComponent(hole, new PhysicsComp(500f, 10.0f));

            wc.addComponent(hole, new HoleComp());
        }

        return hole;
    }

    private static int createRectangleHoleInvisible(WorldContainer wc, float x, float y, float width, float height) {
        int hole = wc.createEntity();
        float[] color = {0.0f, 0.0f, 0.0f};

        wc.addComponent(hole, new PositionComp(x, y));

//        wc.addComponent(hole, new ColoredMeshComp(ColoredMeshUtils.createRectangle(radius, 16, color)));
//        wc.addComponent(hole, new MeshCenterComp(width/2, height/2));

        if (PROGRAM == SERVER || PROGRAM == OFFLINE) {
            wc.addComponent(hole, new CollisionComp(new Rectangle(width, height)));
            //wc.addComponent(hole, new PhysicsComp(500f, 10.0f));

            wc.addComponent(hole, new HoleComp());
        }

        return hole;
    }

    private static int createBackground(WorldContainer wc) {
        int bg = wc.createEntity();
        wc.addComponent(bg, new PositionComp(0, 0));
        wc.addComponent(bg, new TexturedMeshComp(TexturedMeshUtils.createRectangle("background_difuse.png", 1600, 900)));

        return bg;
    }

    private static int createWall(WorldContainer wc, float x, float y, float width, float height) {
        int w = wc.createEntity();
        wc.addComponent(w, new PositionComp(x, y));

        wc.addComponent(w, new ColoredMeshComp(ColoredMeshUtils.createRectangle(width, height)));
        wc.addComponent(w, new MeshCenterComp(width/2, height/2)); //physical rectangle is defined with position being the center, while the graphical square is defined in the upper left corner

        if (PROGRAM == SERVER || PROGRAM == OFFLINE) {
            wc.addComponent(w, new PhysicsComp(0, 1, 1));
            wc.addComponent(w, new CollisionComp(new Rectangle(width, height)));

        }

        return w;
    }
}
