package nsu.graphics.fifthlab.scene.primitives;

import nsu.graphics.fifthlab.MathUtils;
import nsu.graphics.fifthlab.Point3D;
import nsu.graphics.fifthlab.Vector;
import nsu.graphics.fifthlab.panels.scene.Painter;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractPrimitive {
    ///reflection coefficients
    protected double KDr;
    protected double KDg;
    protected double KDb;
    ///specular reflection coefficients
    protected double KSr;
    protected double KSg;
    protected double KSb;
    ///the indicator of specularity
    protected double Power;

    //Rotate
    protected double[][] rotateX;
    protected double[][] rotateY;
    protected double[][] rotateZ;

    //Points
    protected List<Point3D> localObjectPoints;
    protected List<Point3D> globalObjectPoints;
    protected List<Point> camProjectionPoints;

    public AbstractPrimitive(double KDr, double KDg, double KDb, double KSr, double KSg, double KSb, double Power) {
        this.KDr = KDr;
        this.KDg = KDg;
        this.KDb = KDb;
        this.KSr = KSr;
        this.KSg = KSg;
        this.KSb = KSb;
        this.Power = Power;
    }

    public AbstractPrimitive(OpticalCharacteristics opticalCharacteristics) {
        this.KDr = opticalCharacteristics.KDr;
        this.KDg = opticalCharacteristics.KDg;
        this.KDb = opticalCharacteristics.KDb;
        this.KSr = opticalCharacteristics.KSr;
        this.KSg = opticalCharacteristics.KSg;
        this.KSb = opticalCharacteristics.KSb;
        this.Power = opticalCharacteristics.Power;
    }

    //Rotations
    protected void initRotateXOperator(double deg) {
        rotateX = MathUtils.initRotateXOperator(deg);
    }

    protected void initRotateYOperator(double deg) {
        rotateY = MathUtils.initRotateYOperator(deg);
    }

    protected void initRotateZOperator(double deg) {
        rotateZ = MathUtils.initRotateZOperator(deg);
    }

    protected Point3D rotatePointAroundX(Point3D point) {
        return MathUtils.rotatePointAroundAxis(rotateX, point);
    }

    protected Point3D rotatePointAroundY(Point3D point) {
        return MathUtils.rotatePointAroundAxis(rotateY, point);
    }

    protected Point3D rotatePointAroundZ(Point3D point) {
        return MathUtils.rotatePointAroundAxis(rotateZ, point);
    }

    //Init points

    protected abstract void initLocalObjectPoints();

    private void initRotationBeforeCamCorrection(Vector upCamVector, Vector view) {
        Vector xAxis = new Vector(1, 0, 0);
        Vector yAxis = new Vector(0, 1, 0);
        Vector zAxis = new Vector(0, 0, 1);

        upCamVector.normalize();
        Point3D axisPoint = upCamVector.getVertex();
        double d = Math.sqrt(axisPoint.y() * axisPoint.y() + axisPoint.z() * axisPoint.z());
        double cos = axisPoint.z() / d;

        double alpha = Math.toDegrees(Math.acos(cos));
        initRotateXOperator(alpha);
        Vector rotatedXUp = new Vector(rotatePointAroundX(axisPoint));
        double angleX = rotatedXUp.getDegreeBetween(yAxis);
        if (Math.round(angleX) != 90 || Vector.getScalarMul(rotatedXUp, zAxis) < 0) {
            alpha *= -1;
            initRotateXOperator(alpha);
        }

        double betta = Math.toDegrees(Math.acos(d));
        initRotateYOperator(betta);
        Vector rotatedYUp = new Vector(rotatePointAroundY(rotatePointAroundX(axisPoint)));
        double angleY = rotatedYUp.getDegreeBetween(zAxis);
        if (Math.round(angleY) != 0) {
            betta *= -1;
            initRotateYOperator(betta);
        }

        Vector newView = new Vector(rotatePointAroundY(rotatePointAroundX(view.getVertex())));
        double gamma = newView.getDegreeBetween(xAxis);
        initRotateZOperator(gamma);
        Vector rotatedZ = new Vector(rotatePointAroundZ(newView.getVertex()));
        double angleZ = rotatedZ.getDegreeBetween(xAxis);
        if (Math.round(angleZ) != 0) {
            gamma *= -1;
            initRotateZOperator(gamma);
        }
    }

    protected void initGlobalObjectPoints(Point3D camPosition, Vector upCamVector, Vector view) {
        initRotationBeforeCamCorrection(upCamVector, view);
        globalObjectPoints = new LinkedList<>();
        for (Point3D point : localObjectPoints) {
            Point3D globalPoint = new Point3D(point, camPosition);
            Point3D rotatedX = rotatePointAroundX(globalPoint);
            Point3D rotatedY = rotatePointAroundY(rotatedX);
            Point3D rotatedZ = rotatePointAroundZ(rotatedY);
            globalObjectPoints.add(rotatedZ);
        }
    }

    protected void initCamProjectionPoints(double distanceToProjection) {
        camProjectionPoints = new LinkedList<>();
        for (Point3D point : globalObjectPoints) {
            double mul = distanceToProjection / point.x();
            camProjectionPoints.add(new Point((int) Math.round(point.y() * mul * Painter.screenIncreaseSize), (int) Math.round(point.z() * mul * Painter.screenIncreaseSize)));
        }
    }

    //Draw method

    public abstract void drawPrimitive(Painter painter);

    //Primitive getters

    public abstract Point3D getMinPoint();

    public abstract Point3D getMaxPoint();
}
