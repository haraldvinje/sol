package game;

import engine.PositionComp;
import engine.RotationComp;
import engine.UserInput;
import engine.WorldContainer;
import engine.character.CharacterComp;
import engine.character.CharacterInputComp;
import engine.character.CharacterSys;
import engine.character.UserCharacterInputComp;
import engine.combat.DamageResolutionSys;
import engine.combat.DamageableComp;
import engine.combat.DamagerComp;
import engine.combat.abilities.Ability;
import engine.combat.abilities.AbilityComp;
import engine.combat.abilities.AbilitySys;
import engine.combat.abilities.MeleeAbility;
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

    public static boolean ON_SERVER;
    //public static boolean SERVER_RENDER;
    public static String HOST_NAME;


    public static void assignComponentTypes(WorldContainer wc) {

        if (ON_SERVER) {
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
//        wc.assignComponentType(UserCharacterInputComp.class);


        }
        else {
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

        }

    }

    public static void assignSystems(WorldContainer wc, Window window, UserInput userInput) {
        if (ON_SERVER) {
            wc.addSystem(new CharacterSys());
            wc.addSystem(new AbilitySys());

            wc.addSystem(new ServerNetworkSys());

            wc.addSystem(new CollisionDetectionSys());
            wc.addSystem(new HoleResolutionSys());
            wc.addSystem(new DamageResolutionSys());
            wc.addSystem(new CollisionResolutionSys());

            wc.addSystem(new PhysicsSys());

            wc.addSystem(new RenderSys(window));
        }

        else {
            wc.addSystem(new ClientNetworkSys(HOST_NAME, userInput) );

            wc.addSystem(new AbilitySys());

            wc.addSystem(new PhysicsSys());

            wc.addSystem(new RenderSys(window));
        }
    }


    public static void createInitialEntities(WorldContainer wc) {
        //create players
        float centerSeparation = 300f;
        createPlayer(wc, MAP_WIDTH/2 - centerSeparation, MAP_HEIGHT/2);
        createPlayer(wc, MAP_WIDTH/2 + centerSeparation, MAP_HEIGHT/2);

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


    public static int allocateHitboxEntity(WorldContainer wc, Circle shape, float damage, float knockbackRatio){
        int e = wc.createEntity();

        wc.addInactiveComponent(e, new PositionComp(0, 0));
        wc.addInactiveComponent(e, new RotationComp());

        wc.addInactiveComponent(e, new PhysicsComp());

        float[] redColor = {1.0f, 0f,0f};
        wc.addInactiveComponent(e, new ColoredMeshComp(ColoredMeshUtils.createCircleSinglecolor(shape.getRadius(), 8, redColor)) );

        if (ON_SERVER) {
            wc.addInactiveComponent(e, new CollisionComp(shape));
            wc.addInactiveComponent(e, new DamagerComp(damage, knockbackRatio));
        }

        return e;
    }

    private static int createPlayer(WorldContainer wc, float x, float y) {
        int player = wc.createEntity();
        float radius = 32f;
        float xoffset = 16f;

        wc.addComponent(player, new CharacterComp());
        wc.addComponent(player, new PositionComp(x, y));
        wc.addComponent(player, new RotationComp());

        wc.addComponent(player, new TexturedMeshComp(TexturedMeshUtils.createRectangle("sol_frank.png", 4*radius*2, 2*radius*2)));
        wc.addComponent(player, new MeshCenterComp(radius*2+xoffset, radius*2));


        wc.addComponent(player, new AbilityComp(
                new MeleeAbility(wc, 8, 0.3f, new Circle(16f), 82.0f, 0f, 4, 1, 5, 5),
                new MeleeAbility(wc, 15f, 0.7f, new Circle(32), 102f,  M.PI/1.0f, 10,1,5,5)
        ));

        if (ON_SERVER) {
            wc.addComponent(player, new CharacterInputComp());

            wc.addComponent(player, new PhysicsComp(80, 2.5f, 0.3f, PhysicsUtil.FRICTION_MODEL_VICIOUS));
            wc.addComponent(player, new CollisionComp(new Circle(radius)));

            wc.addComponent(player, new AffectedByHoleComp());

            wc.addComponent(player, new DamageableComp());
        }

        return player;
    }
//    private int createSandbag(WorldContainer wc) {
//        float radius = 32f;
//        int sandbag = wc.createEntity();
//        wc.addComponent(sandbag, new PositionComp(500, 300) );
//        wc.addComponent(sandbag, new TexturedMeshComp(TexturedMeshUtils.createRectangle("sandbag.png", radius*2, radius*2)));
//        wc.addComponent(sandbag, new MeshCenterComp(radius, radius));
//
//        wc.addComponent(sandbag, new PhysicsComp(80, 2.5f, 0.3f, PhysicsUtil.FRICTION_MODEL_VICIOUS));
//        wc.addComponent(sandbag, new CollisionComp(new Rectangle(radius*2, radius*2)));
//
//        wc.addComponent(sandbag, new DamageableComp());
//
//        return sandbag;
//    }

    private static int createCircleHole(WorldContainer wc, float x, float y, float radius) {
        int hole = wc.createEntity();
        float[] color = {0.0f, 0.0f, 0.0f};

        wc.addComponent(hole, new PositionComp(x, y));

        wc.addComponent(hole, new ColoredMeshComp(ColoredMeshUtils.createCircleSinglecolor(radius, 16, color)));
        //wc.addComponent(hole, new MeshCenterComp(radius, radius));

        if (ON_SERVER) {
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

        if (ON_SERVER) {
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

        if (ON_SERVER) {
            wc.addComponent(w, new PhysicsComp(0, 1, 1));
            wc.addComponent(w, new CollisionComp(new Rectangle(width, height)));

        }

        return w;
    }
}
