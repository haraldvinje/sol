import engine.PositionComp;
import engine.physics.Circle;
import engine.physics.CollisionData;
import engine.physics.CollisionDetectionSys;
import javafx.geometry.Pos;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by haraldvinje on 14-Jun-17.
 */
public class CollisionDetectionSystemTest {

    @Test
    public void testDetectCollision(){
/*      Circle c1 = new Circle(100,100,10);
        Circle c2 = new Circle(110, 100, 7);
        Circle c3 = new Circle(130, 100, 10);
        Circle c4 = new Circle(100,90,8);
        Circle c5 = new Circle(100, 90, 7);*/


        Circle c1 = new Circle(10);
        PositionComp p1 = new PositionComp(100,100);

        Circle c2 = new Circle(7);
        PositionComp p2 = new PositionComp(110,100);

        CollisionData data = new CollisionData(0,  )

        CollisionDetectionSys cds = new CollisionDetectionSys();

        Assert.assertTrue(cds.detectCollision(c1,c2));
        Assert.assertFalse(cds.detectCollision(c2,c3));
        Assert.assertTrue(cds.detectCollision(c2,c4));
        Assert.assertFalse(cds.detectCollision(c2,c5));

        //write more tests when we have rectangles as well
    }
}
