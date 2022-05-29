package nsu.graphics.fifthlab.scene.primitives;

import nsu.graphics.fifthlab.Point3D;
import nsu.graphics.fifthlab.Vector;
import nsu.graphics.fifthlab.panels.scene.Painter;
import nsu.graphics.fifthlab.render.Ray;
import nsu.graphics.fifthlab.RayColor;

import java.util.LinkedList;

public class Rectangle extends AbstractPrimitive {
    private final Point3D point1;
    private final Point3D point2;
    private final Point3D point3;
    private final Point3D point4;
    private final Vector normal;
    private final Vector AB;
    private final Vector BC;
    private final Vector CD;
    private final Vector DA;
    private final double D;

    public Rectangle(RayColor diffuseReflection, RayColor specularReflection, double power, Point3D point1, Point3D point2, Point3D point3, Point3D point4) {
        super(diffuseReflection, specularReflection, power);
        this.point1 = point1;
        this.point2 = point2;
        this.point3 = point3;
        this.point4 = point4;
        Vector u = new Vector(point2, point1);
        Vector v = new Vector(point4, point1);
        Vector mul = Vector.vectorMultiply(u, v);
        mul.normalize();
        normal = mul;
        //A(x-x_0)+B(y-y_0)+C(z-z_0)=0
        //Ax-Ax_0+By-By_0+Cz-Cz_0=0
        //D=-Ax_0-By_0-Cz_0
        //M=(x_0;y_0;z_0)
        //normal=(A,B,C)
        //D=scalarMul(normal, vectorTo(point M))
        D = -Vector.getScalarMul(normal, new Vector(point1));
        AB = new Vector(point2, point1);
        BC = new Vector(point3, point2);
        CD = new Vector(point4, point3);
        DA = new Vector(point1, point4);
        initLocalObjectPoints();
    }

    @Override
    protected void initLocalObjectPoints() {
        localObjectPoints = new LinkedList<>();
        localObjectPoints.add(point1);
        localObjectPoints.add(point2);
        localObjectPoints.add(point3);
        localObjectPoints.add(point4);
    }

    @Override
    public void drawPrimitive(Painter painter) {
        initGlobalObjectPoints(painter.getCamPosition(), painter.getUpCamVector(), painter.getViewVector());
        initCamProjectionPoints(painter.getDistanceToProjection());
        painter.drawLine(camProjectionPoints.get(0), camProjectionPoints.get(1));
        painter.drawLine(camProjectionPoints.get(1), camProjectionPoints.get(2));
        painter.drawLine(camProjectionPoints.get(2), camProjectionPoints.get(3));
        painter.drawLine(camProjectionPoints.get(3), camProjectionPoints.get(0));
    }

    @Override
    public double intersect(Ray ray) {
        double Vd = Vector.getScalarMul(normal, ray.getDirection());
        if (Vd == 0) return Double.MAX_VALUE; //parallel
        double V0 = -Vector.getScalarMul(normal, new Vector(ray.getPixel())) - D;
        double t = V0 / Vd;
        if (t > 0) {
            Point3D intersectPoint = ray.computeIntersectPoint(t);
            if(isBelongsToTheFigure(intersectPoint)) return t;
        }
        return Double.MAX_VALUE;
    }

    @Override
    public boolean isBelongsToTheFigure(Point3D point) {
        Vector AP = new Vector(point, point1);
        Vector BP = new Vector(point, point2);
        Vector CP = new Vector(point, point3);
        Vector DP = new Vector(point, point4);
        Vector an = Vector.vectorMultiply(AB, AP);
        Vector bn = Vector.vectorMultiply(BC, BP);
        Vector cn = Vector.vectorMultiply(CD, CP);
        Vector dn = Vector.vectorMultiply(DA, DP);
        an.normalize();
        bn.normalize();
        cn.normalize();
        dn.normalize();
        double scalarAnBn = Vector.getScalarMul(an, bn);
        double scalarBnCn = Vector.getScalarMul(bn, cn);
        double scalarCnDn = Vector.getScalarMul(cn, dn);
        return scalarAnBn > 0.95 && scalarBnCn > 0.95 && scalarCnDn > 0.95;
    }

    @Override
    public Vector getNormal(Point3D point) {
        return normal;
    }

    @Override
    public Point3D getMinPoint() {
        Point3D min = null;
        min = Point3D.minOnAllCoordinates(point1, point2);
        min = Point3D.minOnAllCoordinates(min, point3);
        min = Point3D.minOnAllCoordinates(min, point4);
        return min;
    }

    @Override
    public Point3D getMaxPoint() {
        Point3D max = null;
        max = Point3D.maxOnAllCoordinates(point1, point2);
        max = Point3D.maxOnAllCoordinates(max, point3);
        max = Point3D.maxOnAllCoordinates(max, point4);
        return max;
    }
}
