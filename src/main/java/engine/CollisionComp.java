package engine;

/**
 * Created by haraldvinje on 13-Jun-17.
 */
public class CollisionComp implements Component {

    private Shape shape;

    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }
}
