package nsu.graphics.fifthlab.scene.primitives;

import nsu.graphics.fifthlab.Point3D;
import nsu.graphics.fifthlab.panels.scene.Painter;

import java.util.LinkedList;

public class Box extends AbstractPrimitive {
    private final Point3D MIN;
    private final Point3D MAX;

    public Box(OpticalCharacteristics opticalCharacteristics, Point3D MIN, Point3D MAX) {
        super(opticalCharacteristics);
        this.MIN = MIN;
        this.MAX = MAX;
        initLocalObjectPoints();
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
    public Point3D getMinPoint() {
        return new Point3D(MIN);
    }

    @Override
    public Point3D getMaxPoint() {
        return new Point3D(MAX);
    }
}
