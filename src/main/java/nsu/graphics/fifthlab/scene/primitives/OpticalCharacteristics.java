package nsu.graphics.fifthlab.scene.primitives;

import nsu.graphics.fifthlab.Point3D;
import nsu.graphics.fifthlab.panels.scene.Painter;

import java.awt.*;

public class OpticalCharacteristics extends AbstractPrimitive {
    public OpticalCharacteristics(double KDr, double KDg, double KDb, double KSr, double KSg, double KSb, double Power) {
        super(KDr, KDg, KDb, KSr, KSg, KSb, Power);
    }

    @Override
    public void drawPrimitive(Painter painter) {}

    @Override
    protected void initLocalObjectPoints() {

    }

    @Override
    public Point3D getMinPoint() {
        return null;
    }

    @Override
    public Point3D getMaxPoint() {
        return null;
    }
}
