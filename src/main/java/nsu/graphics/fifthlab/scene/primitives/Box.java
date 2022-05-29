package nsu.graphics.fifthlab.scene.primitives;

import nsu.graphics.fifthlab.Point3D;
import nsu.graphics.fifthlab.Vector;
import nsu.graphics.fifthlab.panels.scene.Painter;
import nsu.graphics.fifthlab.render.Ray;
import nsu.graphics.fifthlab.RayColor;

import java.util.LinkedList;
import java.util.List;

public class Box extends AbstractPrimitive {
    private final Point3D MIN;
    private final Point3D MAX;

    private final Rectangle frontRec;
    private final Rectangle backRec;
    private final Rectangle upRec;
    private final Rectangle downRec;
    private final Rectangle leftRec;
    private final Rectangle rightRec;
    private final List<Rectangle> rectangles;

    public Box(RayColor diffuseReflection, RayColor specularReflection, double power, Point3D MIN, Point3D MAX) {
        super(diffuseReflection, specularReflection, power);
        this.MIN = MIN;
        this.MAX = MAX;
        initLocalObjectPoints();
        frontRec = new Rectangle(diffuseReflection, specularReflection, power, localObjectPoints.get(6), localObjectPoints.get(7), localObjectPoints.get(5), localObjectPoints.get(4));
        backRec = new Rectangle(diffuseReflection, specularReflection, power, localObjectPoints.get(0), localObjectPoints.get(1), localObjectPoints.get(3), localObjectPoints.get(2));
        leftRec = new Rectangle(diffuseReflection, specularReflection, power, localObjectPoints.get(4), localObjectPoints.get(5), localObjectPoints.get(1), localObjectPoints.get(0));
        rightRec = new Rectangle(diffuseReflection, specularReflection, power, localObjectPoints.get(2), localObjectPoints.get(3), localObjectPoints.get(7), localObjectPoints.get(6));
        downRec = new Rectangle(diffuseReflection, specularReflection, power, localObjectPoints.get(2), localObjectPoints.get(3), localObjectPoints.get(4), localObjectPoints.get(0));
        upRec = new Rectangle(diffuseReflection, specularReflection, power, localObjectPoints.get(3), localObjectPoints.get(7), localObjectPoints.get(5), localObjectPoints.get(1));
        rectangles = new LinkedList<>();
        rectangles.add(frontRec);
        rectangles.add(backRec);
        rectangles.add(leftRec);
        rectangles.add(rightRec);
        rectangles.add(downRec);
        rectangles.add(upRec);
    }

    @Override
    protected void initLocalObjectPoints() {
        localObjectPoints = new LinkedList<>();
        Point3D[] minimax = {MIN, MAX};
        for (Point3D pointX : minimax) {
            for (Point3D pointY : minimax) {
                for (Point3D pointZ : minimax) {
                    localObjectPoints.add(new Point3D(pointX.x(), pointY.y(), pointZ.z()));
                }
            }
        }
    }

    @Override
    public void drawPrimitive(Painter painter) {
        initGlobalObjectPoints(painter.getCamPosition(), painter.getUpCamVector(), painter.getViewVector());
        initCamProjectionPoints(painter.getDistanceToProjection());
        painter.drawLine(camProjectionPoints.get(0), camProjectionPoints.get(1));
        painter.drawLine(camProjectionPoints.get(0), camProjectionPoints.get(2));
        painter.drawLine(camProjectionPoints.get(0), camProjectionPoints.get(4));

        painter.drawLine(camProjectionPoints.get(3), camProjectionPoints.get(1));
        painter.drawLine(camProjectionPoints.get(3), camProjectionPoints.get(2));
        painter.drawLine(camProjectionPoints.get(3), camProjectionPoints.get(7));

        painter.drawLine(camProjectionPoints.get(5), camProjectionPoints.get(1));
        painter.drawLine(camProjectionPoints.get(5), camProjectionPoints.get(4));
        painter.drawLine(camProjectionPoints.get(5), camProjectionPoints.get(7));

        painter.drawLine(camProjectionPoints.get(6), camProjectionPoints.get(2));
        painter.drawLine(camProjectionPoints.get(6), camProjectionPoints.get(4));
        painter.drawLine(camProjectionPoints.get(6), camProjectionPoints.get(7));
    }

    @Override
    public double intersect(Ray ray) {
        double t = Double.MAX_VALUE;
        for (Rectangle rectangle : rectangles) {
            double res = rectangle.intersect(ray);
            if (res < t) t = res;
        }
        return t;
    }

    @Override
    public boolean isBelongsToTheFigure(Point3D point) {
        for (Rectangle rectangle : rectangles) {
            if (rectangle.isBelongsToTheFigure(point)) return true;
        }
        return false;
    }

    @Override
    public Vector getNormal(Point3D point) {
        for (Rectangle rectangle : rectangles) {
            if (rectangle.isBelongsToTheFigure(point)) {
                return rectangle.getNormal(point);
            }
        }
        return null; //this point is not belongs to the box
    }

    @Override
    public Point3D getMinPoint() {
        return new Point3D(MIN);
    }

    @Override
    public Point3D getMaxPoint() {
        return new Point3D(MAX);
    }
}
