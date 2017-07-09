package game;

import engine.PositionComp;
import engine.RotationComp;
import engine.UserCharacterInputComp;
import engine.WorldContainer;
import engine.character.CharacterComp;
import engine.character.CharacterInputComp;
import engine.combat.DamageableComp;
import engine.combat.DamagerComp;
import engine.combat.abilities.*;
import engine.graphics.*;
import engine.graphics.view_.ViewControlComp;
import engine.network.client.ClientControlledComp;
import engine.network.client.InterpolationComp;
import engine.physics.*;

import java.util.List;

/**
 * Created by eirik on 05.07.2017.
 */
public class CharacterUtils {

    public static final int SHRANK = 0, SCHMATHIS = 1;


    public static void createOfflineCharacters(WorldContainer wc, List<Integer> team1Characters, List<Integer> team2Characters, int team, int clientCharacterId) {
        createClientCharacters(wc, team1Characters, team2Characters, team, clientCharacterId);
    }

    public static void createClientCharacters(WorldContainer wc, List<Integer> team1Characters, List<Integer> team2Characters, int team, int clientCharacterId) {

        int i = 0;
        for (int charEnt : team1Characters) {
            boolean controlled = false;
            if (team == 0 && i == clientCharacterId) {
                controlled = true;
            }
            createCharacter(charEnt, wc, controlled, GameUtils.startPositionsTeam1[i][0], GameUtils.startPositionsTeam1[i][1]);

            i++;
        }
        i = 0;
        for (int charEnt : team2Characters) {
            boolean controlled = false;
            if (team == 1 && i == clientCharacterId) {
                controlled = true;
            }
            createCharacter(charEnt, wc, controlled, GameUtils.startPositionsTeam2[i][0], GameUtils.startPositionsTeam2[i][1]);

            i++;
        }
    }

    public static void createServerCharacters(WorldContainer wc, List<Integer> team1Characters, List<Integer> team2Character) {
        boolean controlled = true;

        int i = 0;
        for (int charEnt : team1Characters) {
            createCharacter(charEnt, wc, controlled, GameUtils.startPositionsTeam1[i][0], GameUtils.startPositionsTeam1[i][1]);
            i++;
        }
        i = 0;
        for (int charEnt : team2Character) {
            createCharacter(charEnt, wc, controlled, GameUtils.startPositionsTeam2[i][0], GameUtils.startPositionsTeam2[i][1]);
            i++;
        }
    }


    private static void createCharacter(int characterId, WorldContainer wc, boolean controlled, float x, float y) {
        switch(characterId) {
            case SHRANK: createShrank(wc, controlled, x, y);
                break;
            case SCHMATHIS: createSchmathias(wc, controlled, x, y);
                break;
        }
    }

    private static int createShrank(WorldContainer wc, boolean controlled, float x, float y) {
        float[] color1 = {1, 1, 0};
        float[] color2 = {1, 0, 1};
        int proj1Entity = ProjectileUtils.allocateSinglecolorProjectileAbility(wc, 8, color1);
        int proj2Entity = ProjectileUtils.allocateSinglecolorProjectileAbility(wc, 20, color2);

        //rapidshot
        ProjectileAbility abRapidshot = new ProjectileAbility(wc, proj1Entity, 2, 2, 30, 1200, 30 );
        abRapidshot.setDamagerValues(wc, 100, 180, 0.5f, -128, false);

        //hyperbeam3
        ProjectileAbility abHyperbeam = new ProjectileAbility(wc, proj2Entity, 15, 10, 120, 1500, 120);
        abHyperbeam.setDamagerValues( wc, 350,900, 1.1f, -256, false);

        //puffer
        MeleeAbility abPuffer = new MeleeAbility(wc, 8, 2, 8, 60*3, new Circle(128f), 0f);
        abPuffer.setDamagerValues(wc, 20, 900f, 0.1f, 0f, false);

        return createCharacter(wc, controlled, x, y, "sol_frank.png", 160f/2f, 512, 256, 180, 130, 32, 1800f,
                abRapidshot, abHyperbeam, abPuffer);
    }

    private static int createSchmathias(WorldContainer wc, boolean controlled, float x, float y) {

        //frogpunch
        MeleeAbility abFrogpunch = new MeleeAbility(wc, 3, 5, 3, 20, new Circle(64f),48.0f);
        abFrogpunch.setDamagerValues(wc, 150, 700, 0.8f, -48f, false);

        //hook
        int hookProjEntity = ProjectileUtils.allocateImageProjectileEntity(wc, "hook.png", 256/2, 512, 256, 24); //both knockback angle and image angle depends on rotation comp. Cheat by setting rediusOnImage negative
        ProjectileAbility abHook = new ProjectileAbility(wc, hookProjEntity, 5, 14, 50, 900, 30);
        abHook.setDamagerValues(wc, 200f, 1500f, 0.2f, -128, true);

        //meteorpunch
        MeleeAbility abMeteorpunch = new MeleeAbility(wc, 15, 3, 4, 60, new Circle(32), 64);
        abMeteorpunch.setDamagerValues(wc, 500, 1000, 1.5f, -128f, false);

        return createCharacter(wc, controlled, x, y, "Schmathias.png", 228f/2f, 720, 400, 267, 195, 32, 2000f,
                abFrogpunch, abHook, abMeteorpunch);
    }


    public static int allocateHitboxEntity(WorldContainer wc, Circle shape){
        int e = wc.createEntity();

        wc.addInactiveComponent(e, new PositionComp(0, 0));
        wc.addInactiveComponent(e, new RotationComp());

        //wc.addInactiveComponent(e, new PhysicsComp());
        wc.addInactiveComponent(e, new HitboxComp());

        wc.addInactiveComponent(e, new DamagerComp());

        float[] redColor = {1.0f, 0f,0f};
        wc.addInactiveComponent(e, new ColoredMeshComp(ColoredMeshUtils.createCircleSinglecolor(shape.getRadius(), 16, redColor)) );

        wc.addInactiveComponent(e, new CollisionComp(shape));

        wc.addComponent(e, new VisualEffectComp(VisualEffectUtils.createOnHitEffect()));

        return e;
    }



    private static int createCharacter(WorldContainer wc, boolean controlled, float x, float y, String imagePath, float radiusOnImage, float imageWidth, float imageHeight, float offsetXOnImage, float offsetYOnImage, float radius, float moveAccel, Ability ab1, Ability ab2, Ability ab3) {
        int characterEntity = wc.createEntity();

        float scale = radius/radiusOnImage;
        float width = imageWidth*scale;
        float height = imageHeight*scale;
        float offsetX = offsetXOnImage*scale;
        float offsetY = offsetYOnImage*scale;

        wc.addComponent(characterEntity, new CharacterComp(moveAccel));//1500f));
        wc.addComponent(characterEntity, new PositionComp(x, y));
        wc.addComponent(characterEntity, new RotationComp());

        wc.addComponent(characterEntity, new TexturedMeshComp(TexturedMeshUtils.createRectangle(imagePath, width, height)));
        wc.addComponent(characterEntity, new MeshCenterComp(offsetX, offsetY));

        wc.addComponent(characterEntity, new AbilityComp(ab1, ab2, ab3));

        //server and offline
        wc.addComponent(characterEntity, new PhysicsComp(80, 5f, 0.3f, PhysicsUtil.FRICTION_MODEL_VICIOUS));
        wc.addComponent(characterEntity, new CollisionComp(new Circle(radius)));
        wc.addComponent(characterEntity, new NaturalResolutionComp());

        wc.addComponent(characterEntity, new AffectedByHoleComp());

        wc.addComponent(characterEntity, new DamageableComp());
        wc.addComponent(characterEntity, new CharacterInputComp());

        //client
        wc.addComponent(characterEntity, new InterpolationComp());

        if (controlled) {
            wc.addComponent(characterEntity, new UserCharacterInputComp());
            wc.addComponent(characterEntity, new ViewControlComp( -GameUtils.VIEW_WIDTH/2f, -GameUtils.VIEW_HEIGHT/2f) );
            wc.addComponent(characterEntity, new ClientControlledComp());
        }


        return characterEntity;
    }


}
