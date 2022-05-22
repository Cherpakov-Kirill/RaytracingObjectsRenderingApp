package nsu.graphics.fifthlab.scene.primitives;

import nsu.graphics.fifthlab.Point3D;
import nsu.graphics.fifthlab.panels.scene.Painter;

import java.util.LinkedList;

public class Triangle extends AbstractPrimitive {
    private final Point3D point1;
    private final Point3D point2;
    private final Point3D point3;

    public Triangle(OpticalCharacteristics opticalCharacteristics, Point3D point1, Point3D point2, Point3D point3) {
        super(opticalCharacteristics);
        this.point1 = point1;
        this.point2 = point2;
        this.point3 = point3;
        initLocalObjectPoints();
    }

    @Override
    protected void initLocalObjectPoints(){
        localObjectPoints = new LinkedList<>();
        localObjectPoints.add(point1);
        localObjectPoints.add(point2);
        localObjectPoints.add(point3);
    }

    @Override
    public void drawPrimitive(Painter painter) {
        initGlobalObjectPoints(painter.getCamPosition(), painter.getUpCamVector(), painter.getViewVector());
        initCamProjectionPoints(painter.getDistanceToProjection());
        painter.drawLine(camProjectionPoints.get(0), camProjectionPoints.get(1));
        painter.drawLine(camProjectionPoints.get(1), camProjectionPoints.get(2));
        painter.drawLine(camProjectionPoints.get(2), camProjectionPoints.get(0));
    }

    @Override
    public Point3D getMinPoint() {
        Point3D min = null;
        min = Point3D.minOnAllCoordinates(point1,point2);
        min = Point3D.minOnAllCoordinates(min,point3);
        return min;
    }

    @Override
    public Point3D getMaxPoint() {
        Point3D max = null;
        max = Point3D.maxOnAllCoordinates(point1,point2);
        max = Point3D.maxOnAllCoordinates(max,point3);
        return max;
    }


}
