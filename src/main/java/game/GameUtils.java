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

import game.server.ServerClientHandler;
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




    public static void createMap(WorldContainer wc) {

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


//    private static int createSandbag(WorldContainer wc) {
//        if (PROGRAM != OFFLINE) throw new UnsupportedOperationException("Sandbag not implemented for nonoffline use");
//
//        float radius = 32f;
//        int sandbag = wc.createEntity();
//        wc.addComponent(sandbag, new PositionComp(500, 300) );
//        wc.addComponent(sandbag, new TexturedMeshComp(TexturedMeshUtils.createRectangle("sandbag.png", radius*2, radius*2)));
//        wc.addComponent(sandbag, new MeshCenterComp(radius, radius));
//
//        wc.addComponent(sandbag, new PhysicsComp(80, 2.5f, 0.3f, PhysicsUtil.FRICTION_MODEL_VICIOUS));
//        wc.addComponent(sandbag, new CollisionComp(new Circle(radius)));
//        wc.addComponent(sandbag, new NaturalResolutionComp());
//
//
//        wc.addComponent(sandbag, new DamageableComp());
//        wc.addComponent(sandbag, new AffectedByHoleComp());
//
//        return sandbag;
//    }

    private static int createCircleHole(WorldContainer wc, float x, float y, float radius) {
        int hole = wc.createEntity();
        float[] color = {0.0f, 0.0f, 0.0f};

        wc.addComponent(hole, new PositionComp(x, y));

        wc.addComponent(hole, new ColoredMeshComp(ColoredMeshUtils.createCircleSinglecolor(radius, 16, color)));
        //wc.addComponent(hole, new MeshCenterComp(radius, radius));


        wc.addComponent(hole, new CollisionComp(new Circle(radius)));
        //wc.addComponent(hole, new PhysicsComp(500f, 10.0f));

        wc.addComponent(hole, new HoleComp());

        return hole;
    }

    private static int createRectangleHoleInvisible(WorldContainer wc, float x, float y, float width, float height) {
        int hole = wc.createEntity();
        float[] color = {0.0f, 0.0f, 0.0f};

        wc.addComponent(hole, new PositionComp(x, y));

//        wc.addComponent(hole, new ColoredMeshComp(ColoredMeshUtils.createRectangle(radius, 16, color)));
//        wc.addComponent(hole, new MeshCenterComp(width/2, height/2));

        wc.addComponent(hole, new CollisionComp(new Rectangle(width, height)));
        //wc.addComponent(hole, new PhysicsComp(500f, 10.0f));

        wc.addComponent(hole, new HoleComp());

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


        wc.addComponent(w, new PhysicsComp(0, 1, 1));
        wc.addComponent(w, new CollisionComp(new Rectangle(width, height)));
        wc.addComponent(w, new NaturalResolutionComp());

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