package engine;

/**
 * Created by haraldvinje on 13-Jun-17.
 */
public class Circle extends Shape {

    private float radius;
    private float cx;
    private float cy;

    public float getCx() {
        return cx;
    }

    public void setCx(float cx) {
        this.cx = cx;
    }

    public float getCy() {
        return cy;
    }

    public void setCy(float cy) {
        this.cy = cy;
    }

    public void setRadius(float radius){
        this.radius = radius;
    }

    public float getRadius(){
        return this.radius;
    }
}
