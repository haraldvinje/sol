package utils.maths;



public class TrigUtils {
	
	public static float pointDirection(float x1, float y1, float x2, float y2) {
		float deltaY = y2 - y1;
		float deltaX = x2 - x1;
		float currRotation;
		if (deltaX == 0) currRotation = deltaY > 0? M.PI/2 : 1.5f*M.PI;
		else {
			currRotation = M.atan( deltaY/deltaX);
			if (deltaX < 0) currRotation += M.PI;
		}
		return currRotation;
	}
	/**
	 * returns a normalized vector in the direction specified by points
	 */
	public static Vec2 pointDirectionVec( Vec2 p1, Vec2 p2) {
		Vec2 v = p2.subtract(p1);
		return v.normalize();
	}
	
	public static float lengthdirX(float length, float dir) {
		return (float)M.cos(dir)*length;
	}
	public static float lengthdirY(float length, float dir) {
		return (float)M.sin(dir)*length;
	}
}
