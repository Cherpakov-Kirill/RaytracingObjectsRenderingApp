package nsu.graphics.fifthlab.scene.primitives;

import nsu.graphics.fifthlab.Point3D;
import nsu.graphics.fifthlab.Vector;
import nsu.graphics.fifthlab.panels.scene.Painter;
import nsu.graphics.fifthlab.render.Ray;
import nsu.graphics.fifthlab.RayColor;

import java.util.LinkedList;
import java.util.List;

public class Sphere extends AbstractPrimitive {
    private static final int numberOfSections = 6;
    private static final int numberOfLinesPerSection = 2;
    private final int numberOfTurns;

    private static final int numberOfIntervalsInSpline = 4;
    private static final int numberOfSplinePointsPerInterval = 3;
    private final int numberOfKeyPoints = numberOfIntervalsInSpline * numberOfSplinePointsPerInterval + 1;

    private final Point3D center;
    private final int radius;

    public Sphere(RayColor diffuseReflection, RayColor specularReflection, double power, Point3D center, int radius) {
        super(diffuseReflection, specularReflection, power);
        this.center = center;
        this.radius = radius;
        numberOfTurns = numberOfSections * numberOfLinesPerSection;
        initLocalObjectPoints();
    }

    private List<Point3D> initSpline() {
        List<Point3D> splinePoints = new LinkedList<>();
        initRotateXOperator(180 / (double) (numberOfIntervalsInSpline * numberOfSplinePointsPerInterval));
        Point3D startPoint = new Point3D(0, 0, radius);
        splinePoints.add(startPoint);
        for (int i = 1; i < numberOfKeyPoints; i++) {
            splinePoints.add(rotatePointAroundX(splinePoints.get(i - 1)));
        }
        return splinePoints;
    }

    @Override
    protected void initLocalObjectPoints() {
        localObjectPoints = new LinkedList<>();
        localObjectPoints.addAll(initSpline());

        initRotateZOperator(360 / (double) numberOfTurns);

        for (int turn = 1; turn < numberOfTurns; turn++) {
            for (int i = 0; i < numberOfKeyPoints; i++) {
                Point3D parentPoint = localObjectPoints.get((turn - 1) * numberOfKeyPoints + i);
                Point3D point = rotatePointAroundZ(parentPoint);
                parentPoint.addDelta(center);
                if (turn == numberOfTurns - 1) point.addDelta(center);
                localObjectPoints.add(point);
            }
        }
    }

    @Override
    public void drawPrimitive(Painter painter) {
        initGlobalObjectPoints(painter.getCamPosition(), painter.getUpCamVector(), painter.getViewVector());
        initCamProjectionPoints(painter.getDistanceToProjection());
        for (int section = 0; section < numberOfSections; section++) {
            for (int pointInLine = 1; pointInLine < numberOfKeyPoints; pointInLine++) {
                painter.drawLine(camProjectionPoints.get(section * numberOfLinesPerSection * numberOfKeyPoints + pointInLine - 1),
                        camProjectionPoints.get(section * numberOfLinesPerSection * numberOfKeyPoints + pointInLine));
            }
            for (int line = 0; line < numberOfLinesPerSection; line++) {
                int numberOfLine = section * numberOfLinesPerSection + line;
                for (int pointInLine = 0; pointInLine < numberOfIntervalsInSpline; pointInLine++) {
                    painter.drawLine(camProjectionPoints.get(numberOfLine * numberOfKeyPoints + pointInLine * numberOfSplinePointsPerInterval),
                            camProjectionPoints.get(((numberOfLine + 1) % numberOfTurns) * numberOfKeyPoints + pointInLine * numberOfSplinePointsPerInterval))
                    ;
                }
            }
        }
    }

    @Override
    public double intersect(Ray ray) {
        Vector centerFromPoint = new Vector(ray.getPixel(), center);
        double B = Vector.getScalarMul(centerFromPoint, ray.getDirection());
        double C = Vector.getScalarMul(centerFromPoint, centerFromPoint) - radius * radius;
        double D = B * B - C;
        //double D = radius*radius - Vector.getScalarMul(centerFromPoint,centerFromPoint)-B*B;
        if (D < 0) {
            return Double.MAX_VALUE;
        } else {
            double sqrt = Math.sqrt(D);
            double t0 = -B - sqrt;
            if (t0 >= 0) {
                return t0;
            } else {
                double t1 = -B + sqrt;
                if (t1 >= 0) return t1;
                else return Double.MAX_VALUE;
            }
        }

    }

    @Override
    public boolean isBelongsToTheFigure(Point3D point) {
        double rx = point.x() - center.x();
        double ry = point.y() - center.y();
        double rz = point.z() - center.z();
        double dist = rx * rx + ry * ry + rz * rz;
        return dist - radius < 1e-5;
    }

    @Override
    public Vector getNormal(Point3D point) {
        double x = (point.x() - center.x()) / radius;
        double y = (point.y() - center.y()) / radius;
        double z = (point.z() - center.z()) / radius;
        return new Vector(x, y, z);
    }

    @Override
    public Point3D getMinPoint() {
        return new Point3D(center.x() - radius, center.y() - radius, center.z() - radius);
    }

    @Override
    public Point3D getMaxPoint() {
        return new Point3D(center.x() + radius, center.y() + radius, center.z() + radius);
    }
}
