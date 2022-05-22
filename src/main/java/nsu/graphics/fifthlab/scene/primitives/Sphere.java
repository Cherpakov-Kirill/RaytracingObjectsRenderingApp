package nsu.graphics.fifthlab.scene.primitives;

import nsu.graphics.fifthlab.Point3D;
import nsu.graphics.fifthlab.panels.scene.Painter;

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

    public Sphere(OpticalCharacteristics opticalCharacteristics, Point3D center, int radius) {
        super(opticalCharacteristics);
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
    public Point3D getMinPoint() {
        return new Point3D(center.x() - radius, center.y() - radius, center.z() - radius);
    }

    @Override
    public Point3D getMaxPoint() {
        return new Point3D(center.x() + radius, center.y() + radius, center.z() + radius);
    }
}
