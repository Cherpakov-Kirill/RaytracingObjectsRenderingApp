package nsu.graphics.fifthlab.objects;

import nsu.graphics.fifthlab.Point3D;

import java.awt.*;
import java.util.List;

import static nsu.graphics.fifthlab.MathUtils.*;

public class ObjectsCoordinates {
    /*private static final int screenIncreaseSize = 400;
    private final double camPos;
    private double distanceToProjection;
    private final Point3D[][] localObjectPoints;
    private final Point3D[][] globalObjectPoints;
    private final Point[][] camProjectionPoints;
    private double[][] rotateX;
    private double[][] rotateY;
    private double[][] rotateZ;
    private final int numberOfColumns;
    private final int numberOfPointsInLine;



    public void increaseDistanceToProjection(int delta) {
        this.distanceToProjection -= delta * (0.2);
        initCamProjectionPoints();
    }

    public ObjectsCoordinates(List<Point> splinePoints, int numberOfSections, int numberLinesPerSection) {
        camPos = -10;
        numberOfColumns = numberOfSections * numberLinesPerSection;
        numberOfPointsInLine = splinePoints.size();
        double deltaPhi = 360.0 / numberOfColumns;
        localObjectPoints = new Point3D[numberOfColumns][numberOfPointsInLine];
        globalObjectPoints = new Point3D[numberOfColumns][numberOfPointsInLine];
        camProjectionPoints = new Point[numberOfColumns][numberOfPointsInLine];

        initRotateXOperator(deltaPhi);
        initLocalObjectPoints(splinePoints);
        initGlobalObjectPoints();
        this.distanceToProjection = 5.0;
        initCamProjectionPoints();
    }


    public void rotate(int rotDegreeY, int rotDegreeZ) {
        initRotateYOperator(rotDegreeY);
        initRotateZOperator(rotDegreeZ);
        for (int turn = 0; turn < numberOfColumns; turn++) {
            for (int i = 0; i < numberOfPointsInLine; i++) {
                Point3D point = localObjectPoints[turn][i];
                double[][] result = matrixMultiplying(rotateY, new double[][]{{point.x()}, {point.y()}, {point.z()}, {1}});
                result = matrixMultiplying(rotateZ, result);
                globalObjectPoints[turn][i] = new Point3D(result[0][0], result[1][0], result[2][0] - camPos);
            }
        }
        initCamProjectionPoints();
    }*/
}
