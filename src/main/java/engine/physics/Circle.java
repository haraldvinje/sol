package engine.physics;

/**
 * Created by haraldvinje on 13-Jun-17.
 */
public class Circle extends Shape {

    private float radius;

    public Circle(){
        super(0, 0);
    }

    public Circle(float radius){
        super(0, 0);
        this.radius = radius;
    }

    public Circle(float cx, float cy, float radius){
        super(cx, cy);
        this.radius = radius;
    }

    public void setValues(float cx, float cy, float radius){
        super.setXY(cx, cy);
        this.radius = radius;
    }

//    public float getCx() {
//        return cx;
//    }
//
//    public void setCx(float cx) {
//        this.cx = cx;
//    }
//
//    public float getCy() {
//        return cy;
//    }
//
//    public void setCy(float cy) {
//        this.cy = cy;
//    }

    public void setRadius(float radius){
        this.radius = radius;
    }

    public float getRadius(){
        return this.radius;
    }
}
