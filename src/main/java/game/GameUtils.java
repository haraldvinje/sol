package game;

import engine.*;
import engine.character.*;
import engine.combat.DamageResolutionSys;
import engine.combat.DamageableComp;
import engine.combat.DamagerComp;
import engine.combat.abilities.*;
import engine.graphics.*;


import engine.graphics.text.TextMeshComp;
import engine.graphics.view_.ViewControlComp;
import engine.graphics.view_.ViewControlSys;
import engine.network.client.InterpolationComp;
import engine.network.client.InterpolationSys;

import engine.network.client.*;

import engine.network.client.clientStates.ClientCharacterselectState;
import engine.network.client.clientStates.ClientIngameState;
import engine.network.server.ServerClientHandler;
import engine.network.server.ServerNetworkSys;
import engine.physics.*;
import engine.visualEffect.VisualEffectComp;
import engine.visualEffect.VisualEffectSys;
import engine.window.Window;

import java.net.Socket;
import java.util.List;

/**
 * Created by eirik on 22.06.2017.
 */
public class GameUtils {


    public static final float MAP_WIDTH = 1600f,
                                MAP_HEIGHT = 900f;

    public static float VIEW_WIDTH = MAP_WIDTH, VIEW_HEIGHT = MAP_HEIGHT;

    public static final int SERVER = 0, CLIENT = 1, OFFLINE = 2;
    public static int PROGRAM = -1;
    //public static boolean SERVER_RENDER;
    public static Socket socket; //set by mainClient args

    public static List<ServerClientHandler> CLIENT_HANDELERS;

    public static int[][] startPositionsTeam1, startPositionsTeam2;





    public static void assignComponentTypes(WorldContainer wc) {

        wc.assignComponentType(TextMeshComp.class);

        wc.assignComponentType(PositionComp.class);
        wc.assignComponentType(ColoredMeshComp.class);
        wc.assignComponentType(TexturedMeshComp.class);
        wc.assignComponentType(CollisionComp.class);
        wc.assignComponentType(NaturalResolutionComp.class);
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
        wc.assignComponentType(UserCharacterInputComp.class);
        wc.assignComponentType(ProjectileComp.class);
        wc.assignComponentType(InterpolationComp.class);
        wc.assignComponentType(ViewControlComp.class);
        wc.assignComponentType(ControlledComp.class);
        wc.assignComponentType(VisualEffectComp.class);
        wc.assignComponentType(ViewRenderComp.class);


    }

    public static void assignSystems(WorldContainer wc, Window window, UserInput userInput) {
        if (PROGRAM == SERVER) {
            wc.addSystem(new CharacterSys());
            wc.addSystem(new AbilitySys());

            wc.addSystem(new ServerNetworkSys(CLIENT_HANDELERS)); //takes wc because it allocates icons

            wc.addSystem(new CollisionDetectionSys());
            wc.addSystem(new HoleResolutionSys());
            wc.addSystem(new HitboxResolutionSys());
            wc.addSystem(new DamageResolutionSys());
            wc.addSystem(new NaturalResolutionSys());

            wc.addSystem(new PhysicsSys());

            wc.addSystem(new ProjectileSys());

            wc.addSystem(new RenderSys(window));
        }

        else if (PROGRAM == CLIENT){
            wc.addSystem(new UserCharacterInputSys(userInput));

            wc.addSystem(new ClientNetworkInSys(socket));
            wc.addSystem(new AbilitySys());
            wc.addSystem(new PhysicsSys());

            wc.addSystem(new InterpolationSys());

            wc.addSystem(new ProjectileSys());

            wc.addSystem(new ViewControlSys());

            wc.addSystem(new ClientNetworkOutSys(socket, userInput));
            wc.addSystem(new VisualEffectSys());

            wc.addSystem(new OnScreenSys(wc, 2));

            wc.addSystem(new RenderSys(window));
        }

        else if (PROGRAM == OFFLINE) {
            wc.addSystem(new UserCharacterInputSys(userInput));
            wc.addSystem(new UserInputToCharacterSys());
            wc.addSystem(new CharacterSys());
            wc.addSystem(new AbilitySys());

            wc.addSystem(new CollisionDetectionSys());
            wc.addSystem(new HoleResolutionSys());
            wc.addSystem(new HitboxResolutionSys());
            wc.addSystem(new DamageResolutionSys());
            wc.addSystem(new NaturalResolutionSys());

            wc.addSystem(new PhysicsSys());

            wc.addSystem(new ProjectileSys());

            wc.addSystem(new ViewControlSys());
            wc.addSystem(new VisualEffectSys());

            wc.addSystem(new OnScreenSys(wc, 2));

            wc.addSystem(new RenderSys(window));

        }
    }


    public static void createInitialEntities(WorldContainer wc, Integer [] characters) {

        //create players
        float centerSeparation = 300f;
        if (PROGRAM == OFFLINE) {
            //createShrank(wc, MAP_WIDTH / 2 - centerSeparation, MAP_HEIGHT / 2);
            createSchmathias(wc, MAP_WIDTH / 2 + centerSeparation, MAP_HEIGHT / 2);

            createSandbag(wc);
        }
        else {

            createCharacter(wc, MAP_WIDTH / 2 - centerSeparation, MAP_HEIGHT / 2, characters[0]);
            createCharacter(wc, MAP_WIDTH / 2 + centerSeparation, MAP_HEIGHT / 2, characters[1]);


//            createShrank(wc, MAP_WIDTH / 2 - centerSeparation, MAP_HEIGHT / 2);
//            createSchmathias(wc, MAP_WIDTH / 2 + centerSeparation, MAP_HEIGHT / 2);
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
        createCircleHole(wc, MAP_WIDTH/2, MAP_HEIGHT/2, 48f);


        //create background
        createBackground(wc);


    }




    public static void createMap(WorldContainer wc) {


//        //create players
//        float centerSeparation = 300f;
//        if (PROGRAM == OFFLINE) {
//            //createShrank(wc, MAP_WIDTH / 2 - centerSeparation, MAP_HEIGHT / 2);
//            createSchmathias(wc, true, MAP_WIDTH / 2 + centerSeparation, MAP_HEIGHT / 2);
//
//            createSandbag(wc);
//        }
//        else if (PROGRAM == SERVER){
//            createShrank(wc, true, MAP_WIDTH / 2 - centerSeparation, MAP_HEIGHT / 2);
//            createSchmathias(wc, true, MAP_WIDTH / 2 + centerSeparation, MAP_HEIGHT / 2);
//        }
//        else if (PROGRAM == CLIENT) {
//            createShrank(wc, true, MAP_WIDTH / 2 - centerSeparation, MAP_HEIGHT / 2);
//            createSchmathias(wc, false, MAP_WIDTH / 2 + centerSeparation, MAP_HEIGHT / 2);
//        }

        int[][] startPositionsTeam1 = { {100, 200}, {100, 400}};
        int[][] startPositionsTeam2 = { {1000, 200}, {1000, 400}};
        GameUtils.startPositionsTeam1 = startPositionsTeam1;
        GameUtils.startPositionsTeam2 = startPositionsTeam2;

        //create background
        createBackground(wc);

        //create walls
        float wallThickness = 64f;
        createWall(wc, wallThickness/2, MAP_HEIGHT/2, wallThickness, MAP_HEIGHT);
        createWall(wc, MAP_WIDTH-wallThickness/2, MAP_HEIGHT/2, wallThickness, MAP_HEIGHT);

//        createWall(wc, MAP_WIDTH/2, wallThickness/2, MAP_WIDTH-wallThickness*2, wallThickness);
//        createWall(wc, MAP_WIDTH/2, MAP_HEIGHT-wallThickness/2, MAP_WIDTH-wallThickness*2, wallThickness);

        //create holes
        createRectangleHoleInvisible(wc, MAP_WIDTH/2, wallThickness/2, MAP_WIDTH-wallThickness*2, wallThickness);
        createRectangleHoleInvisible(wc, MAP_WIDTH/2, MAP_HEIGHT-wallThickness/2, MAP_WIDTH-wallThickness*2, wallThickness);
        createCircleHole(wc, MAP_WIDTH/2, MAP_HEIGHT/2, 48f);



    }

    private static int createCharacter(WorldContainer wc, float x, float y, int characterId){
        if (characterId == ClientCharacterselectState.SCHMATHIAS_CHARACTER_ID){
            return createSchmathias(wc, x, y);
        }
        else if (characterId == ClientCharacterselectState.SHRANK_CHARACTER_ID){
            return createShrank(wc, x, y);
        }
        throw new IllegalArgumentException("No character with this id exists yet");
    }

    private static int createShrank(WorldContainer wc, float x, float y) {
        float[] color1 = {1, 1, 0};
        float[] color2 = {1, 0, 1};
        int proj1Entity = allocateSinglecolorProjectileAbility(wc, 8, color1);
        int proj2Entity = allocateSinglecolorProjectileAbility(wc, 20, color2);

        //rapidshot
        ProjectileAbility abRapidshot = new ProjectileAbility(wc, proj1Entity, 2, 2, 30, 1200, 30 );
        abRapidshot.setDamagerValues(wc, 100, 180, 0.5f, -128, false);

        //hyperbeam3
        ProjectileAbility abHyperbeam = new ProjectileAbility(wc, proj2Entity, 15, 10, 120, 1500, 120);
        abHyperbeam.setDamagerValues( wc, 350,900, 1.1f, -256, false);

        //puffer
        MeleeAbility abPuffer = new MeleeAbility(wc, 8, 2, 8, 60*3, new Circle(128f), 0f);
        abPuffer.setDamagerValues(wc, 20, 900f, 0.1f, 0f, false);

        return createCharacter(wc, x, y, "sol_frank.png", 160f/2f, 512f, 256f, 180, 130, 32, 1800f,
                abRapidshot, abHyperbeam, abPuffer);
    }

    private static int createSchmathias(WorldContainer wc, float x, float y) {

        //frogpunch
        MeleeAbility abFrogpunch = new MeleeAbility(wc, 3, 5, 3, 20, new Circle(64f),48.0f);
        abFrogpunch.setDamagerValues(wc, 150, 700, 0.8f, -48f, false);

        //hook
        int hookProjEntity = allocateImageProjectileEntity(wc, "hook.png", 256/2, 512, 256, 24); //both knockback angle and image angle depends on rotation comp. Cheat by setting rediusOnImage negative
        ProjectileAbility abHook = new ProjectileAbility(wc, hookProjEntity, 5, 14, 50, 900, 30);
        abHook.setDamagerValues(wc, 200f, 1500f, 0.2f, -128, true);

        //meteorpunch
        MeleeAbility abMeteorpunch = new MeleeAbility(wc, 15, 3, 4, 60, new Circle(32), 64);
        abMeteorpunch.setDamagerValues(wc, 500, 1000, 1.5f, -128f, false);

        return createCharacter(wc, x, y, "Schmathias.png", 228f/2f, 720, 400, 267, 195, 32, 2000f,
            abFrogpunch, abHook, abMeteorpunch);
    }

   
    private static int createSandbag(WorldContainer wc) {
        if (PROGRAM != OFFLINE) throw new UnsupportedOperationException("Sandbag not implemented for nonoffline use");

        float radius = 32f;
        int sandbag = wc.createEntity();
        wc.addComponent(sandbag, new PositionComp(500, 300) );
        wc.addComponent(sandbag, new TexturedMeshComp(TexturedMeshUtils.createRectangle("sandbag.png", radius*2, radius*2)));
        wc.addComponent(sandbag, new MeshCenterComp(radius, radius));

        wc.addComponent(sandbag, new PhysicsComp(80, 2.5f, 0.3f, PhysicsUtil.FRICTION_MODEL_VICIOUS));
        wc.addComponent(sandbag, new CollisionComp(new Circle(radius)));
        wc.addComponent(sandbag, new NaturalResolutionComp());


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
        wc.addComponent(bg, new PositionComp(0, 0, -0.5f));
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
            wc.addComponent(w, new NaturalResolutionComp());

        }

        return w;
    }

    //    private static void addClientCharacter(WorldContainer wc, int characterEntity, boolean controlled) {
//        wc.addComponent(characterEntity, new InterpolationComp());
//
//        if (controlled) {
//            wc.addComponent(characterEntity, new UserCharacterInputComp());
//            wc.addComponent(characterEntity, new ViewControlComp(VIEW_WIDTH, VIEW_HEIGHT,  -VIEW_WIDTH/2f, -VIEW_HEIGHT/2f) );
//        }
//    }
//    private static void addServerCharacter(WorldContainer wc, int characterEntity, float radius) {
//        wc.addComponent(characterEntity, new ViewControlComp(VIEW_WIDTH, VIEW_HEIGHT,  -VIEW_WIDTH/2f, -VIEW_HEIGHT/2f) );
//
//        wc.addComponent(characterEntity, new CharacterInputComp());
//
//        wc.addComponent(characterEntity, new PhysicsComp(80, 5f, 0.3f, PhysicsUtil.FRICTION_MODEL_VICIOUS));
//        wc.addComponent(characterEntity, new CollisionComp(new Circle(radius)));
//        wc.addComponent(characterEntity, new NaturalResolutionComp());
//
//        wc.addComponent(characterEntity, new AffectedByHoleComp());
//
//        wc.addComponent(characterEntity, new DamageableComp());
//    }
//
//    private static void addOfflineCharacter(WorldContainer wc, int characterEntity, float radius) {
//        wc.addComponent(characterEntity, new CharacterInputComp());
//
//        wc.addComponent(characterEntity, new PhysicsComp(80, 5f, 0.3f, PhysicsUtil.FRICTION_MODEL_VICIOUS));
//        wc.addComponent(characterEntity, new CollisionComp(new Circle(radius)));
//        wc.addComponent(characterEntity, new NaturalResolutionComp());
//
//        wc.addComponent(characterEntity, new AffectedByHoleComp());
//
//        wc.addComponent(characterEntity, new DamageableComp());
//
//        wc.addComponent(characterEntity, new UserCharacterInputComp());
//
//    }
}
