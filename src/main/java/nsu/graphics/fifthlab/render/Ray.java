package nsu.graphics.fifthlab.render;

import nsu.graphics.fifthlab.Point3D;
import nsu.graphics.fifthlab.Vector;
import nsu.graphics.fifthlab.scene.primitives.AbstractPrimitive;

public class Ray {
    private final Point3D pixel;
    private final Vector direction;
    private final int depth;
    private AbstractPrimitive intersectionWithPrimitive;
    private Point3D intersectionPoint;

    public Ray(Point3D pixel, Vector direction) {
        this(pixel, direction, 1);
    }

    public Ray(Point3D pixel, Vector direction, int depth) {
        this.pixel = pixel;
        this.direction = direction;
        this.intersectionWithPrimitive = null;
        this.depth = depth;
    }

    public int getDepth() {
        return depth;
    }

    public void setIntersectionWithPrimitive(AbstractPrimitive primitive) {
        this.intersectionWithPrimitive = primitive;
    }

    public void computeAndSetIntersectPoint(double t) {
        intersectionPoint = computeIntersectPoint(t);
    }

    public Point3D computeIntersectPoint(double t) {
        Point3D dirVertex = direction.getVertex();
        double x = pixel.x() + t * dirVertex.x();
        double y = pixel.y() + t * dirVertex.y();
        double z = pixel.z() + t * dirVertex.z();
        return new Point3D(x, y, z);
    }

    public Point3D getIntersectPoint() {
        return intersectionPoint;
    }

    public AbstractPrimitive getIntersectionWithPrimitive() {
        return intersectionWithPrimitive;
    }

    public Vector getIntersectionNormal() {
        return intersectionWithPrimitive.getNormal(intersectionPoint);
    }

    public Point3D getPixel() {
        return pixel;
    }

    public Vector getDirection() {
        return direction;
    }
}
