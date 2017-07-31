package game;

import engine.*;
import engine.audio.AudioComp;
import engine.audio.AudioSys;
import engine.audio.Sound;
import engine.audio.SoundListenerComp;
import engine.character.*;
import engine.combat.DamageResolutionSys;
import engine.combat.DamageableComp;
import engine.combat.DamagerComp;
import engine.combat.abilities.*;
import engine.graphics.*;


import engine.graphics.text.Font;
import engine.graphics.text.FontType;
import engine.graphics.text.TextMesh;
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
import utils.maths.Vec4;

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
        wc.assignComponentType(AudioComp.class);
        wc.assignComponentType(SoundListenerComp.class);
        wc.assignComponentType(GameDataComp.class);

    }


    public static int createGameData(WorldContainer wc, ClientGameTeams teams, List<Integer> charEntityIds) {
        //charEntityIds are assumed to be in same order as team character ids

        float[] teamStartX = {10, GameUtils.VIEW_WIDTH-200};
        float startY = GameUtils.VIEW_HEIGHT - 300;
        float spaceY = 50;

        Vec4 dmgTextColor = new Vec4(1, 0, 0, 1);
        float dmgTextSize = 64;

        GameDataComp dataComp = new GameDataComp();

        //Create damage text entities, and store id in dataComp
        for (int i = 0; i < teams.getTeamCount();  i++) {
            float currY = startY;
            int j = 0;
            for (int charId : teams.getCharacterIdsOnTeam(i)) {

                //create text entity
                int t = wc.createEntity("damage text");
                wc.addComponent(t, new PositionComp(teamStartX[i], currY));
                wc.addComponent(t, new ViewRenderComp(new TextMesh("0", Font.getFont(FontType.BROADWAY), dmgTextSize, dmgTextColor)));

                dataComp.charDamageTextEntities.put(charEntityIds.get(i+j), t);

                ++j;
                currY += spaceY;
            }
        }

        //create game end text entity and store in data comp
        int endGameTextEntity = wc.createEntity("game end text");
        wc.addComponent(endGameTextEntity, new PositionComp(300, 300 ));
        wc.addComponent(endGameTextEntity, new ViewRenderComp(new TextMesh("", Font.getFont(FontType.BROADWAY), 128, dmgTextColor)));
        dataComp.gameEndTextEntity = endGameTextEntity;

        //Create actual game data entity
        int gameDataEntity = wc.createEntity("game data");
        wc.addComponent(gameDataEntity, dataComp);

        return gameDataEntity;
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
        int hole = wc.createEntity("circle hole");
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
        int hole = wc.createEntity("rectangle hole");
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
        int bg = wc.createEntity("background");
        wc.addComponent(bg, new PositionComp(0, 0, -0.5f));
        wc.addComponent(bg, new TexturedMeshComp(TexturedMeshUtils.createRectangle("background_difuse.png", 1600, 900)));

        return bg;
    }

    private static int createWall(WorldContainer wc, float x, float y, float width, float height) {
        int w = wc.createEntity("wall");
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